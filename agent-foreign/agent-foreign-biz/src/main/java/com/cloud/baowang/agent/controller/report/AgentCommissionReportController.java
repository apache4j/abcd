package com.cloud.baowang.agent.controller.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCommissionApi;
import com.cloud.baowang.agent.api.vo.commission.AgentChildNodesCommissionVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPageQueryVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReportQueryVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReportVO;
import com.cloud.baowang.agent.api.vo.commission.front.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemConfigApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 10:46
 * @description:
 */
@Slf4j
@Tag(name = "佣金报表")
@AllArgsConstructor
@RestController
@RequestMapping("/frontCommissionReport/api")
public class AgentCommissionReportController {

    private final AgentCommissionApi agentCommissionApi;
    private final MinioUploadApi minioUploadApi;
    private final SystemConfigApi systemConfigApi;

    @Operation(summary = "直属会员佣金")
    @PostMapping(value = "/getSelfCommissionReport")
    public ResponseVO<List<AgentCommissionReportVO>> getSelfCommissionReport(@RequestBody AgentCommissionReportQueryVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setAgentId(CurrReqUtils.getOneId());
        return agentCommissionApi.AgentCommissionReportQueryVO(reqVO);
    }


    @Operation(summary = "下级代理佣金")
    @PostMapping(value = "/getTeamCommissionReport")
    public ResponseVO<List<AgentCommissionReportVO>> getTeamCommissionReport(@RequestBody AgentCommissionReportQueryVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setAgentId(CurrReqUtils.getOneId());
        return agentCommissionApi.getTeamCommissionReport(reqVO);
    }

    @Operation(summary = "下级代理佣金明细列表")
    @PostMapping(value = "/getSubAgentCommission")
    public ResponseVO<Page<AgentChildNodesCommissionVO>> getSubAgentCommission(@RequestBody AgentCommissionPageQueryVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setAgentId(CurrReqUtils.getOneId());
        return agentCommissionApi.getSubAgentCommission(reqVO);
    }

    @Operation(summary = "下级代理佣金明细-详情")
    @PostMapping(value = "/subAgentCommissionDetail")
    public ResponseVO<List<AgentCommissionReportVO>> subAgentCommissionDetail(@RequestBody AgentCommissionReportQueryVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setAgentId(CurrReqUtils.getOneId());
        return agentCommissionApi.subAgentCommissionDetail(reqVO);
    }

//
//    @Operation(summary = "佣金报表")
//    @PostMapping(value = "/getReport")
//    public ResponseVO<FrontCommissionReportResVO> getCommissionReport(@RequestBody FrontCommissionReportReqVO reqVO) {
//        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
//        reqVO.setAgentId(CurrReqUtils.getOneId());
//        return agentCommissionApi.getCommissionReport(reqVO);
//    }

//    @Operation(summary = "佣金报表-佣金明细")
//    @PostMapping(value = "/getReportDetail")
//    public ResponseVO<Page<SubCommissionGeneralVO>> getReportDetail(@RequestBody FrontCommissionDetailReqVO reqVO) {
//        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
//        reqVO.setAgentId(CurrReqUtils.getOneId());
//        return agentCommissionApi.getReportDetail(reqVO);
//    }

    /*@Operation(summary = "佣金报表-查询返点明细")
    @PostMapping(value = "/getAgentRebateDetail")
    public ResponseVO<AgentRebateDetailVO> getAgentRebateDetail(@RequestBody FrontCommissionDetailReqVO reqVO) {
        return ResponseVO.success();  //todo
    }*/

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody AgentCommissionPageQueryVO reqVO, HttpServletResponse response) {
        String agentId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();

        reqVO.setSiteCode(siteCode);
        reqVO.setAgentId(agentId);
        String uniqueKey = "tableExport::centerControl::CommissionReport::" + agentId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            Locale currLocale = LocaleContextHolder.getLocale();
            log.info("获取的语言：{}, {}", currLocale.getCountry(), currLocale.getLanguage());
            String message = String.format(I18nMessageUtil.getI18NMessage(CommonConstant.DOWNLOAD_LIMIT), remain);
            throw new BaowangDefaultException(message);
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        reqVO.setPageSize(10000);
        ResponseVO<Page<AgentChildNodesCommissionVO>> responseVO = agentCommissionApi.getSubAgentCommission(reqVO);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData().getRecords().size() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        Long count = responseVO.getData().getTotal();

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentChildNodesCommissionVO.class,
                reqVO,
                1,
                ExcelUtil.getPages(reqVO.getPageSize(), count),
                param -> responseVO.getData().getRecords());

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        String pageName =  I18nMessageUtil.getI18NMessage(CommonConstant.AGENT_COMMISSION_RECORD_DETAIL);
        ResponseVO<String> upload =  minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(pageName)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());

        String fileName = upload.getData();
        String fileKey = ExcelUtil.BAOWANG_BUCKET + "/" + fileName;
        String domain =  systemConfigApi.queryMinioDomain().getData();
        String downLoadUrl = domain + "/" + fileKey;

        return ResponseVO.success(downLoadUrl);
    }
}
