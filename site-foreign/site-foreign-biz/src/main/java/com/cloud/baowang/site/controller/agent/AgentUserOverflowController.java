package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentUserOverflowApi;
import com.cloud.baowang.agent.api.enums.AgentClientDeviceEnum;
import com.cloud.baowang.agent.api.enums.UserOverFlowSourceEnums;
import com.cloud.baowang.agent.api.vo.member.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.userTransfer.MemberOverflowReviewExcelVO;
import com.cloud.baowang.site.vo.export.userTransfer.MemberTransferReviewExcelVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "代理-会员溢出")
@RestController
@RequestMapping("/agentUserOverflow")
@AllArgsConstructor
public class AgentUserOverflowController {

    private final AgentUserOverflowApi agentUserOverflowApi;
    private final SystemParamApi systemParamApi;
    private final MinioUploadApi minioUploadApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.USER_REVIEW_REVIEW_STATUS);
        param.add(CommonConstant.USER_REVIEW_LOCK_STATUS);
        param.add(CommonConstant.AUDIT_TIME_TYPE);
        param.add(CommonConstant.DEVICE_TYPE);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            Map<String, List<CodeValueVO>> data = resp.getData();
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.USER_REVIEW_REVIEW_STATUS);
            codeValueVOS = codeValueVOS.stream().filter(item -> item.getCode().equals(String.valueOf(ReviewStatusEnum.REVIEW_PASS.getCode()))
                    || item.getCode().equals(String.valueOf(ReviewStatusEnum.REVIEW_REJECTED.getCode()))).toList();
            data.put(CommonConstant.USER_REVIEW_REVIEW_STATUS, codeValueVOS);
            resp.setData(data);
        }
        return resp;
    }


    @Operation(summary = "会员溢出申请")
    @PostMapping("/apply")
    public ResponseVO<Boolean> apply(@RequestBody @Valid AgentUserOverflowApplyVO agentUserOverflowApplyVO) {
        agentUserOverflowApplyVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentUserOverflowApplyVO.setApplySource(UserOverFlowSourceEnums.SITE_BACKEND.getType());
        return agentUserOverflowApi.agentUserOverflowApply(agentUserOverflowApplyVO, CurrReqUtils.getAccount());
    }

    @Operation(summary = "会员溢出审核分页查询")
    @PostMapping("/listPage")
    public ResponseVO<Page<MemberOverflowReviewPageResVO>> listPage(@Valid @RequestBody MemberOverflowReviewPageReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAuditStep(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        return agentUserOverflowApi.agentUserOverflowListPage(vo, CurrReqUtils.getAccount());
    }

    @Operation(summary = "会员溢出审核记录")
    @PostMapping("/getRecord")
    public ResponseVO<Page<MemberOverflowReviewPageResVO>> getRecord(@Valid @RequestBody MemberOverflowReviewPageReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAuditStep(ReviewOperationEnum.CHECK.getCode());
        return agentUserOverflowApi.agentUserOverflowListPage(vo, CurrReqUtils.getAccount());
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody MemberOverflowReviewPageReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String account = CurrReqUtils.getAccount();
        vo.setAuditStep(ReviewOperationEnum.CHECK.getCode());
        String uniqueKey = "tableExport::centerControl::agentUserOverflowRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long responseVO = agentUserOverflowApi.getTotal(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                MemberOverflowReviewExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(agentUserOverflowApi.agentUserOverflowListPage(param, account).getData().getRecords(), MemberOverflowReviewExcelVO.class));

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_USER_OVER_FLOW_LIST)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }


    @Operation(summary = "会员溢出锁单/解单")
    @PostMapping("/lockOrder")
    public ResponseVO<?> lockOrder(@Valid @RequestBody MemberOverflowLockReqVO vo) {
        return agentUserOverflowApi.agentUserOverflowLockOrder(vo, CurrReqUtils.getAccount());
    }

    @Operation(summary = "会员溢出详情")
    @PostMapping("/detail")
    public ResponseVO<MemberOverflowDetailResVO> detail(@Valid @RequestBody MemberOverflowLockReqVO vo) {
        return agentUserOverflowApi.detail(vo);
    }

    @Operation(summary = "会员溢出审核")
    @PostMapping("/audit")
    public ResponseVO<?> audit(@Valid @RequestBody MemberOverflowAuthReqVO vo) {
        return agentUserOverflowApi.audit(vo, CurrReqUtils.getAccount());
    }

    @Operation(summary = "会员溢出-根据会员账号获取会员类型，当前上级代理")
    @PostMapping("/queryUser")
    public ResponseVO<MemberTransferUserRespVO> queryUser(@RequestBody MemberTransferUserReqVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        if (StringUtils.isEmpty(vo.getUserAccount()) && StringUtils.isEmpty(vo.getUserRegister())) {
            return ResponseVO.fail(ResultCode.MISSING_PARAMETERS);
        }
        return agentUserOverflowApi.queryUser(vo);
    }

}
