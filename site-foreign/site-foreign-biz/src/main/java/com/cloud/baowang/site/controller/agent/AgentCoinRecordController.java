package com.cloud.baowang.site.controller.agent;


import com.cloud.baowang.agent.api.api.AgentCoinRecordApi;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRespVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.vo.AgentCoinRecordDownBoxReqVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Tag(name = "资金-代理资金记录-代理账变记录")
@RestController
@RequestMapping("/agent-coin-record/api")
@AllArgsConstructor
public class AgentCoinRecordController  {


    private final AgentCoinRecordApi agentCoinRecordApi;


    private final SystemParamApi systemParamApi;

    private final MinioUploadApi minioUploadApi;


    private final RiskApi riskApi;

    @Operation(summary = "下拉框-根据walletType")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBoxByWalletType(@RequestBody AgentCoinRecordDownBoxReqVO vo) {

        ResponseVO<List<CodeValueVO>> businessResponseVo = systemParamApi.getSystemParamByType(CommonConstant.AGENT_BUSINESS_COIN_TYPE);
        List<CodeValueVO> businessCoinTypeList = businessResponseVo.getData();

        ResponseVO<List<CodeValueVO>> coinTypeResponseVo = systemParamApi.getSystemParamByType(CommonConstant.AGENT_COIN_TYPE);
        List<CodeValueVO> coinTypeList = coinTypeResponseVo.getData();
        ResponseVO<List<CodeValueVO>> customerCoinTypeResponseVo = systemParamApi.getSystemParamByType(CommonConstant.AGENT_CUSTOMER_COIN_TYPE);
        List<CodeValueVO> customerCoinTypeList = customerCoinTypeResponseVo.getData();

        if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(vo.getWalletType())) {

            List<String> commissionBusinessTypeCodeList = List.of(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode(),
                    AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_COMMISSION.getCode());
            businessCoinTypeList = businessCoinTypeList.stream()
                    .filter(obj -> !commissionBusinessTypeCodeList.contains(obj.getCode()))
                    .collect(Collectors.toList());
            List<String> commissionCoinTypeCodeList = List.of(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL_ADMIN.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL_FAIL.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.NEGATIVE_PROFIT_COMMISSION.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.EFFECTIVE_TURNOVER_REBATE.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.CAPITATION_FEE.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.COMMISSION_ADD.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.COMMISSION_SUBTRACT.getCode());
            coinTypeList = coinTypeList.stream()
                    .filter(obj -> !commissionCoinTypeCodeList.contains(obj.getCode()))
                    .collect(Collectors.toList());

        } else if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode().equals(vo.getWalletType())) {
            List<String> quotaBusinessTypeCodeList = List.of(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_PROMOTIONS.getCode(),
                    AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_QUOTA.getCode(),
                    AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.DEPOSIT_OF_SUBORDINATES.getCode());
            businessCoinTypeList = businessCoinTypeList.stream()
                    .filter(obj -> !quotaBusinessTypeCodeList.contains(obj.getCode()))
                    .collect(Collectors.toList());
            List<String> commissionCoinTypeCodeList = List.of(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.PROMOTIONS_ADD.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.PROMOTIONS_SUBTRACT.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_ADMIN_DEPOSIT.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_DEPOSIT.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.TRANSFER_SUBORDINATES_MEMBER.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.QUOTA_ADD.getCode(),
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.QUOTA_SUBTRACT.getCode());
            coinTypeList = coinTypeList.stream()
                    .filter(obj -> !commissionCoinTypeCodeList.contains(obj.getCode()))
                    .collect(Collectors.toList());

        }
        List<String> codeList = List.of(CommonConstant.AGENT_STATUS, CommonConstant.AGENT_WALLET_TYPE, CommonConstant.COIN_BALANCE_TYPE);
        Map<String, List<CodeValueVO>> responseVO = systemParamApi.getSystemParamsByList(codeList).getData();

        RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
        riskLevelDownReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        riskLevelDownReqVO.setRiskControlType(RiskTypeEnum.RISK_AGENT.getCode());
        ResponseVO<List<RiskLevelResVO>> riskControlLevelResponseVO = riskApi.getRiskLevelList(riskLevelDownReqVO);
        List<CodeValueVO> riskControlLevel = Lists.newArrayList();
        if (riskControlLevelResponseVO.isOk()) {
            List<RiskLevelResVO> list = riskControlLevelResponseVO.getData();
            riskControlLevel = list.stream().map(item ->
                            CodeValueVO.builder()
                                    .code(item.getId())
                                    .value(item.getRiskControlLevel())
                                    .build())
                    .toList();
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put(CommonConstant.RISK_CONTROL_LEVEL,riskControlLevel);
        result.put(CommonConstant.AGENT_STATUS, responseVO.get(CommonConstant.AGENT_STATUS));
        result.put(CommonConstant.AGENT_WALLET_TYPE, responseVO.get(CommonConstant.AGENT_WALLET_TYPE));
        result.put(CommonConstant.COIN_BALANCE_TYPE, responseVO.get(CommonConstant.COIN_BALANCE_TYPE));
        result.put(CommonConstant.AGENT_BUSINESS_COIN_TYPE, businessCoinTypeList);
        result.put(CommonConstant.AGENT_COIN_TYPE, coinTypeList);
        return ResponseVO.success(result);
    }


    @Operation(description = "代理账变记录列表")
    @PostMapping(value = "/listAgentCoinRecordPage")
    public ResponseVO<AgentCoinRecordRespVO> listAgentCoinRecordPage(@RequestBody AgentCoinRecordRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimeZone(CurrReqUtils.getTimezone());
        return agentCoinRecordApi.listAgentCoinRecordPage(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/agentCoinRecordReportExport")
    public ResponseVO<?> export(@RequestBody AgentCoinRecordRequestVO vo) {
        String currentUserAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String uniqueKey = "tableExport::centerControl::agentCoinRecord::" + siteCode + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setSiteCode(siteCode);
        vo.setPageSize(10000);
        vo.setTimeZone(CurrReqUtils.getTimezone());
        Long responseVO = agentCoinRecordApi.agentCoinRecordPageListCount(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentCoinRecordVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(listAgentCoinRecordPage(param).getData().getAgentCoinRecordVOPage().getRecords(), AgentCoinRecordVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_COIN_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());

    }

}
