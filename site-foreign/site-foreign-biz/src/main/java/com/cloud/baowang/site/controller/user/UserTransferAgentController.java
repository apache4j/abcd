package com.cloud.baowang.site.controller.user;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.UserTransferAgentApi;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.agent.api.vo.member.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.userTransfer.MemberTransferReviewExcelVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "站点后台-会员转代")
@RestController
@RequestMapping("/userTransferAgent")
@AllArgsConstructor
public class UserTransferAgentController {
    private final SystemParamApi systemParamApi;
    private final UserTransferAgentApi userTransferAgentApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "会员转代申请")
    @PostMapping("/apply")
    public ResponseVO<Boolean> apply(@Valid @RequestBody MemberTransferAgentApplyVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        String userName = CurrReqUtils.getAccount();
        return userTransferAgentApi.apply(vo, userName);
    }

    @Operation(summary = "会员转代审核分页查询")
    @PostMapping("/listPage")
    public ResponseVO<Page<MemberTransferReviewPageResVO>> listPage(@Valid @RequestBody MemberTransferReviewPageReqVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        String adminName = CurrReqUtils.getAccount();
        //查询全部待审核
        vo.setAuditStep(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
        return userTransferAgentApi.listPage(vo, adminName);
    }

    @Operation(summary = "会员转代审核记录")
    @PostMapping("/getRecord")
    public ResponseVO<Page<MemberTransferReviewPageResVO>> getRecord(@Valid @RequestBody MemberTransferReviewPageReqVO vo) {

        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        String adminName = CurrReqUtils.getAccount();
        //查询全部待审核
        vo.setAuditStep(ReviewOperationEnum.CHECK.getCode());
        vo.setOrderName("auditDatetime");
        return userTransferAgentApi.listPage(vo, adminName);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody MemberTransferReviewPageReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String account = CurrReqUtils.getAccount();
        vo.setAuditStep(ReviewOperationEnum.CHECK.getCode());
        String uniqueKey = "tableExport::centerControl::userTransferRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long responseVO = userTransferAgentApi.getTotal(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                MemberTransferReviewExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(userTransferAgentApi.listPage(param, account).getData().getRecords(), MemberTransferReviewExcelVO.class));

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_TRANSFER_AGENT_LIST)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

    @Operation(summary = "锁单/解单")
    @PostMapping("/lockOrder")
    public ResponseVO<?> lockOrder(@Valid @RequestBody MemberTransferLockReqVO vo) {
        String userName = CurrReqUtils.getAccount();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userTransferAgentApi.lockOrder(vo, userName);
    }

    @Operation(summary = "会员转代详情")
    @PostMapping("/detail")
    public ResponseVO<MemberTransferDetailResVO> detail(@Valid @RequestBody MemberTransferLockReqVO vo) {
        return userTransferAgentApi.detail(vo);
    }

    @Operation(summary = "统计待审核转代记录条数")
    @GetMapping("/getPendingCountBySiteCode")
    public ResponseVO<UserAccountUpdateVO> getPendingCountBySiteCode() {
        String siteCode = CurrReqUtils.getSiteCode();
        return ResponseVO.success(userTransferAgentApi.getPendingCountBySiteCode(siteCode));
    }

    @Operation(summary = "会员转代审核")
    @PostMapping("/audit")
    public ResponseVO<?> audit(@Valid @RequestBody MemberTransferAuthReqVO vo) {
        String userName = CurrReqUtils.getAccount();
        return userTransferAgentApi.audit(vo, userName);
    }

    @Operation(summary = "会员转代会员查询")
    @PostMapping("/queryUser")
    public ResponseVO<MemberTransferUserRespVO> queryUser(@RequestBody MemberTransferUserReqVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        if (StringUtils.isEmpty(vo.getUserAccount()) && StringUtils.isEmpty(vo.getUserRegister())) {
            return ResponseVO.fail(ResultCode.MISSING_PARAMETERS);
        }
        return userTransferAgentApi.queryUser(vo);
    }

    @Operation(summary = ("获取审核列表,审核记录列表下拉"))
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.USER_REVIEW_REVIEW_STATUS);
        param.add(CommonConstant.USER_REVIEW_LOCK_STATUS);
        param.add(CommonConstant.AUDIT_TIME_TYPE);
        ResponseVO<Map<String, List<CodeValueVO>>> responseVO = systemParamApi.getSystemParamsByList(param);
        if (responseVO.isOk()) {
            Map<String, List<CodeValueVO>> data = responseVO.getData();
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.USER_REVIEW_REVIEW_STATUS);
            codeValueVOS = codeValueVOS.stream()
                    .filter(item -> item.getCode().equals(String.valueOf(ReviewStatusEnum.REVIEW_PASS.getCode())) ||
                            item.getCode().equals(String.valueOf(ReviewStatusEnum.REVIEW_REJECTED.getCode())))
                    .toList();
            data.put(CommonConstant.USER_REVIEW_REVIEW_STATUS, codeValueVOS);
            responseVO.setData(data);
        }
        return responseVO;
    }
}
