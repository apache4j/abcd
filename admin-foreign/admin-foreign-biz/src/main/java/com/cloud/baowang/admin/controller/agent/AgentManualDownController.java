/*
package com.cloud.baowang.admin.controller.agent;

import com.cloud.baowang.agent.api.api.AgentManualDownApi;
import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.manualup.*;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.funding.UserManualOrderStatusEnum;
import com.cloud.baowang.common.core.enums.manualDowmUp.AgentManualDownAdjustTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.SystemParamVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

*/
/**
 * @author: kimi
 *//*

@Tag(name = "资金-资金调整-代理人工扣除额度")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-manual-down/api")
public class AgentManualDownController {


    private AgentManualDownApi agentManualDownApi;

    private AgentManualUpApi agentManualUpApi;

    private MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/queryManualDownSelect")
    public ResponseVO<Map<String, List<SystemParamVO>>> queryManualDownSelect() {
        // 钱包类型
        List<SystemParamVO> walletType = AgentCoinRecordTypeEnum.AgentWalletTypeEnum.getList().stream().map(item ->
                SystemParamVO.builder().code(item.getCode()).value(item.getName()).build()).toList();

        // 人工扣除调整类型
        List<SystemParamVO> adjustType = AgentManualDownAdjustTypeEnum.getList().stream().map(item ->
                SystemParamVO.builder().code(item.getCode()).value(item.getName()).build()).toList();
        Map<String, List<SystemParamVO>> result = Maps.newHashMap();

        // 订单状态
        SystemParamVO vo1 = SystemParamVO.builder()
                .code(UserManualOrderStatusEnum.REVIEW_SUCCESS.getCode().toString())
                .value("成功").build();
        SystemParamVO vo2 = SystemParamVO.builder()
                .code("-99")
                .value("失败").build();
        List<SystemParamVO> orderStatus = Lists.newArrayList();
        orderStatus.add(vo1);
        orderStatus.add(vo2);

        result.put("walletType", walletType);
        result.put("adjustType", adjustType);
        result.put("orderStatus", orderStatus);

        return ResponseVO.success(result);
    }

    @Operation(summary = "查询代理余额")
    @PostMapping(value = "/getAgentBalance")
    public ResponseVO<GetAgentBalanceVO> getAgentBalance(@Valid @RequestBody GetAgentBalanceQueryVO vo) {
        vo.setSiteCode(CurrentRequestUtils.getSiteCode());
        return agentManualUpApi.getAgentBalance(vo);
    }

    @Operation(summary = "代理人工扣除额度保存")
    @PostMapping(value = "/saveManualDown")
    public ResponseVO<?> saveManualDown(@Valid @RequestBody AgentManualDownAddVO vo) {
        BigDecimal adjustAmount = new BigDecimal(vo.getAdjustAmount());
        if (BigDecimal.ZERO.compareTo(adjustAmount) >= 0) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_NOT_LT_ZREO);
        }
        BigDecimal adjustAmountFmt = adjustAmount.stripTrailingZeros();
        if (adjustAmountFmt.scale() > CommonConstant.business_two) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_SCALE_GT_TWO);
        }
        if ((adjustAmountFmt.precision() - adjustAmountFmt.scale()) > CommonConstant.business_eleven) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_MAX_LENGTH);
        }
        GetAgentBalanceQueryVO getAgentBalanceQueryVO = new GetAgentBalanceQueryVO();
        getAgentBalanceQueryVO.setAgentAccount(vo.getAgentAccount());
        if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode().equals(String.valueOf(vo.getWalletTypeCode()))) {
            getAgentBalanceQueryVO.setWalletTypeCode(Integer.parseInt(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode()));

        } else if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(String.valueOf(vo.getWalletTypeCode()))) {
            getAgentBalanceQueryVO.setWalletTypeCode(Integer.parseInt(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode()));
        }
        GetAgentBalanceVO getAgentBalanceVO = agentManualUpApi.getAgentBalance(getAgentBalanceQueryVO).getData();

        if (null == getAgentBalanceVO) {
            throw new BaowangDefaultException(ResultCode.AGENT_MANUAL_DOWN_COIN_AMOUNT_NOT_ENOUGH);
        }
        BigDecimal balance = new BigDecimal(getAgentBalanceVO.getAgentBalance());
        if (balance.compareTo(BigDecimal.ZERO) <= 0 || balance.compareTo(adjustAmount) < 0) {
            throw new BaowangDefaultException(ResultCode.AGENT_MANUAL_DOWN_COIN_AMOUNT_NOT_ENOUGH);
        }
        return ResponseVO.success(agentManualDownApi.saveManualDown(vo, CurrentRequestUtils.getCurrentUserAccount()));
    }


    @Operation(summary = "代理人工扣除记录")
    @PostMapping(value = "/listAgentManualDownRecordPage")
    public ResponseVO<AgentManualDownRecordResponseVO> listAgentManualDownRecordPage(@RequestBody AgentManualDownRecordRequestVO vo) {
        return agentManualDownApi.listAgentManualDownRecordPage(vo);
    }

    @PostMapping("export")
    @Operation(summary = "代理人工扣除记录导出")
    public ResponseVO<?> export(@RequestBody AgentManualDownRecordRequestVO vo) {
        String adminId = CurrentRequestUtils.getCurrentOneId();
        String uniqueKey = "tableExport::centerControl::agentManualDownRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentManualDownApi.listAgentManualDownRecordPageExportCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentManualDownRecordVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> agentManualDownApi.listAgentManualDownRecordPage(param).getData().getRecords());

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_MANUAL_DOWN_RECORD)
                        .adminId(CurrentRequestUtils.getCurrentUserAccount())
                        .siteCode(CurrentRequestUtils.getSiteCode())
                        .build());
    }
}
*/
