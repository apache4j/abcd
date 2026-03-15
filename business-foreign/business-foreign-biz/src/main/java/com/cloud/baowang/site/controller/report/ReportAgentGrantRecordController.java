package com.cloud.baowang.site.controller.report;

import com.cloud.baowang.agent.api.api.AgentCommissionGrantApi;
import com.cloud.baowang.agent.api.api.AgentCommissionPlanApi;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanInfoVO;
import com.cloud.baowang.agent.api.vo.commission.AgentGranRecordPageAllVO;
import com.cloud.baowang.agent.api.vo.commission.AgentGrantRecordPageVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionGranRecordReqVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionGrantRecordDetailVO;
import com.cloud.baowang.agent.api.vo.commission.IdPageVO;
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
import com.cloud.baowang.system.api.api.SystemConfigApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: ford
 * @createTime: 2025-02-11
 * @description:
 */
@Tag(name = "商务后台-报表-佣金发放记录")
@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/business-agent-grant/api")
public class ReportAgentGrantRecordController {
    private final AgentCommissionGrantApi agentCommissionGrantApi;
    public final AgentCommissionPlanApi agentCommissionPlanApi;
    private final CommonService commonService;
    private final MinioUploadApi minioUploadApi;
    private final SystemConfigApi systemConfigApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> types = new ArrayList<>();
        types.add(CommonConstant.AGENT_CATEGORY);
        types.add(CommonConstant.COMMISSION_TYPE);
        types.add(CommonConstant.SETTLE_CYCLE);
        return ResponseVO.success(commonService.getSystemParamsByList(types));
    }

    @Operation(summary = "佣金发放记录分页查询")
    @PostMapping(value = "/getGrantRecordPageList")
    public ResponseVO<AgentGranRecordPageAllVO> getGrantRecordPageList(@RequestBody CommissionGranRecordReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setMerchantAccount(CurrReqUtils.getAccount());
        return agentCommissionGrantApi.getGrantRecordPageList(requestVO);
    }

    @Operation(summary = "查看佣金方案 --传入planId")
    @PostMapping("getPlanInfo")
    public ResponseVO<AgentCommissionPlanInfoVO> getPlanInfo(@RequestBody IdVO idVO) {
        return agentCommissionPlanApi.getPlanInfo(idVO);
    }

    @Operation(summary = "查看佣金详情")
    @PostMapping("getCommissionDetail")
    public ResponseVO<CommissionGrantRecordDetailVO> getCommissionDetail(@RequestBody IdPageVO idVO) {
        return agentCommissionGrantApi.getCommissionDetail(idVO);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody CommissionGranRecordReqVO vo, HttpServletResponse response) {
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::commissionMerchantGranRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long count = agentCommissionGrantApi.getGrantRecordPageCount(vo);
        if (count == null || count <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentGrantRecordPageVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), count),
                param -> ConvertUtil.entityListToModelList(getGrantRecordPageList(param).getData().getPages().getRecords(), AgentGrantRecordPageVO.class));


        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        ResponseVO<String> upload =  minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_MERCHANT_COMMISSION_GRANT_RECORD)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());

        String fileName = upload.getData();
        String fileKey = ExcelUtil.BAOWANG_BUCKET + "/" + fileName;
        String domain =  systemConfigApi.queryMinioDomain().getData();
        String downLoadUrl = domain + "/" + fileKey;

        return ResponseVO.success(downLoadUrl);
    }
}
