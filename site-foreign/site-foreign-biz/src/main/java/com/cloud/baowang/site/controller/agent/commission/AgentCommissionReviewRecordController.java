package com.cloud.baowang.site.controller.agent.commission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCommissionPlanApi;
import com.cloud.baowang.agent.api.api.AgentCommissionReviewRecordApi;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanInfoVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewDetailVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewRecordVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionReviewReq;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: fangfei
 * @createTime: 2024/10/27 0:54
 * @description: 代理佣金审核记录
 */
@Tag(name = "资金-资金审核记录-代理佣金审核记录")
@AllArgsConstructor
@RestController
@RequestMapping("/commission-review-record/api")
public class AgentCommissionReviewRecordController {

    private final AgentCommissionReviewRecordApi agentCommissionReviewRecordApi;
    private final CommonService commonService;
    private final MinioUploadApi minioUploadApi;
    public final AgentCommissionPlanApi agentCommissionPlanApi;

    /**
     * 下拉框
     */
    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> types = new ArrayList<>();
        types.add(CommonConstant.COMMISSION_OPERATION);
        types.add(CommonConstant.COMMISSION_TYPE);
        types.add(CommonConstant.AUDIT_TIME_TYPE);
        Map<String, List<CodeValueVO>>  map = commonService.getSystemParamsByList(types);
        List<CodeValueVO> statusList = map.get(CommonConstant.COMMISSION_OPERATION);
        List<CodeValueVO> newStatusList = statusList.stream()
                .filter(s -> s.getCode().equals(CommonConstant.business_three_str)
                        || s.getCode().equals(CommonConstant.business_four_str)
                        || s.getCode().equals(CommonConstant.business_nine_str)
                        || s.getCode().equals(CommonConstant.business_eight_str)).toList();
        map.put(CommonConstant.COMMISSION_OPERATION, newStatusList);

        return ResponseVO.success(map);
    }

    @Operation(summary = "分页查询佣金审核记录")
    @PostMapping("getReviewRecordPage")
    public ResponseVO<Page<AgentCommissionReviewRecordVO>> getReviewRecordPage(@RequestBody CommissionReviewReq vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAdminName(CurrReqUtils.getAccount());
        return agentCommissionReviewRecordApi.getReviewRecordPage(vo);
    }

    @Operation(summary = "查看佣金审核详情")
    @PostMapping(value = "/getAgentCommissionDetail")
    public ResponseVO<AgentCommissionReviewDetailVO> getAgentCommissionDetail(@RequestBody IdVO idVO) {
        return agentCommissionReviewRecordApi.getAgentCommissionRecordDetail(idVO);
    }

    @Operation(summary = "查看佣金方案详情")
    @PostMapping(value = "/getPlanInfoById")
    public ResponseVO<AgentCommissionPlanInfoVO> getPlanInfoById(@RequestBody IdVO idVO) {
        return agentCommissionPlanApi.getPlanInfo(idVO);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody CommissionReviewReq vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::commissionReview::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long count  = agentCommissionReviewRecordApi.getReviewRecordPageCount(vo);
        if (count ==  null || count <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        CommissionReviewReq reviewReq = agentCommissionReviewRecordApi.buildExportFields(vo);
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentCommissionReviewRecordVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), count),
                param -> ConvertUtil.entityListToModelList(agentCommissionReviewRecordApi.getReviewRecordPage(reviewReq).getData().getRecords(), AgentCommissionReviewRecordVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_REVIEW_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

}
