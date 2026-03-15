/*package com.cloud.baowang.admin.controller.agent;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCoinRecordApi;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordVO;
import com.cloud.baowang.admin.controller.base.ExportBaseController;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.admin.vo.AgentCoinRecordDownBoxReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
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
public class AgentCoinRecordController extends ExportBaseController {


    private final AgentCoinRecordApi agentCoinRecordApi;


    private final SystemParamApi systemParamApi;

    private final MinioUploadApi minioUploadApi;

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
                    AgentCoinRecordTypeEnum.AgentCoinTypeEnum.PLATFORM_ADD.getCode(),
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
        riskLevelDownReqVO.setRiskControlType(CommonConstant.business_two.toString());
//        ResponseVO<List<RiskLevelResVO>> riskControlLevelResponseVO = riskApi.getRiskLevelList(riskLevelDownReqVO);

       *//* List<CodeValueVO> riskControlLevel = Lists.newArrayList();
        if (riskControlLevelResponseVO.isOk()) {
            List<RiskLevelResVO> list = riskControlLevelResponseVO.getData();
            riskControlLevel = list.stream().map(item ->
                            CodeValueVO.builder()
                                    .code(item.getId())
                                    .value(item.getRiskControlLevel())
                                    .build())
                    .toList();
        }*//*

        Map<String, Object> result = Maps.newHashMap();
//        result.put(CommonConstant.RISK_CONTROL_LEVEL,riskControlLevel);
        result.put(CommonConstant.AGENT_STATUS, responseVO.get(CommonConstant.AGENT_STATUS));
        result.put(CommonConstant.AGENT_WALLET_TYPE, responseVO.get(CommonConstant.AGENT_WALLET_TYPE));
        result.put(CommonConstant.COIN_BALANCE_TYPE, responseVO.get(CommonConstant.COIN_BALANCE_TYPE));
        result.put(CommonConstant.AGENT_BUSINESS_COIN_TYPE, businessCoinTypeList);
        result.put(CommonConstant.AGENT_COIN_TYPE, coinTypeList);
        return ResponseVO.success(result);
    }


    @Operation(description = "代理账变记录列表")
    @PostMapping(value = "/listAgentCoinRecordPage")
    public ResponseVO<Page<AgentCoinRecordVO>> listAgentCoinRecordPage(@RequestBody AgentCoinRecordRequestVO vo) {
        return agentCoinRecordApi.listAgentCoinRecordPage(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/agentCoinRecordReportExport")
    public ResponseVO<?> export(@RequestBody AgentCoinRecordRequestVO vo) {
        String currentUserAccount = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::agentCoinRecord::" + CommonConstant.ADMIN_CENTER_SITE_CODE + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
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
                param -> ConvertUtil.entityListToModelList(listAgentCoinRecordPage(param).getData().getRecords(), AgentCoinRecordVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_WITHDRAW_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());

    }

}
*/