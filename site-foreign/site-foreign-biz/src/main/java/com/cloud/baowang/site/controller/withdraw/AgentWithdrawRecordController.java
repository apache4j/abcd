package com.cloud.baowang.site.controller.withdraw;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.agent.api.api.AgentWithdrawRecordApi;
import com.cloud.baowang.agent.api.api.AgentWithdrawReviewRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalStatisticsVO;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.vo.export.AgentWithdrawalRecordExcelVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SiteWithdrawWayApi;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: kimi
 */

@Tag(name = "资金-代理资金记录-代理提款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-withdrawal-record/api")
public class AgentWithdrawRecordController {

    private final AgentWithdrawReviewRecordApi agentWithdrawReviewRecordApi;

    private AgentWithdrawRecordApi agentWithdrawRecordApi;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final SiteWithdrawWayApi withdrawWayApi;
    private SystemParamApi systemParamApi;

    private MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @GetMapping("getWithdrawalRecordSelect")
    public ResponseVO<Map<String, List<CodeValueVO>>> getWithdrawalRecordSelect() {
        //订单来源，订单状态，客户端状态，币种，提款方式，是否大额，是否首提
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
        param.add(CommonConstant.DEVICE_TYPE);
        param.add(CommonConstant.DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS);
        param.add(CommonConstant.YES_NO);
        param.add(CommonConstant.AUDIT_TIME_TYPE);
        List<CodeValueVO> currencyDownBox = currencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode());
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            //提款方式


            Map<String, List<CodeValueVO>> result = resp.getData();
            result.put("currency_code", currencyDownBox);

            ResponseVO<List<SiteWithdrawWayResVO>> wayResp = withdrawWayApi.queryWithdrawListBySite(CurrReqUtils.getSiteCode());
            if (wayResp.isOk()) {
                List<SiteWithdrawWayResVO> data = wayResp.getData();
                List<CodeValueVO> codeValueList = data.stream()
                        .map(item -> new CodeValueVO(item.getWithdrawId(), item.getWithdrawWayI18())) // 创建 CodeValueVO 对象
                        .collect(Collectors.toList());
                result.put("recharge_way", codeValueList);
            }

            List<CodeValueVO> codeValueVOS = result.get(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
            if (CollectionUtil.isNotEmpty(codeValueVOS)) {
                List<String> statusList = new ArrayList<>();
                statusList.add(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_WAIT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT_REJECT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT_SUCCESS.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_FAIL.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.BACKSTAGE_CANCEL.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());

                Set<String> statusSet = Set.copyOf(statusList);
                codeValueVOS = codeValueVOS.stream()
                        .filter(codeValue -> statusSet.contains(codeValue.getCode()))
                        .toList();
                result.put(CommonConstant.DEPOSIT_WITHDRAW_STATUS, codeValueVOS);
                List<CodeValueVO> yesNoList = result.get(CommonConstant.YES_NO);
                //是否都用这个isBigMoney，isFirstOut
                result.put("is_big_money", yesNoList);
                result.put("is_first_out", yesNoList);
            }
            return ResponseVO.success(result);
        }
        return ResponseVO.success();

    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getAgentWithdrawalRecordPageList")
    public ResponseVO<AgentWithdrawalRecordPageResVO> getAgentWithdrawalRecordPageList(@RequestBody @Validated AgentWithdrawalRecordReqVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentWithdrawRecordApi.getAgentWithdrawalRecordPageList(requestVO);
    }


    @PostMapping("export")
    @Operation(summary = "导出")
    public ResponseVO<?> export(@RequestBody @Validated AgentWithdrawalRecordReqVO vo) {

        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::agentWithdrawRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentWithdrawRecordApi.agentWithdrawRecordRecordPageCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentWithdrawalRecordExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(agentWithdrawRecordApi.getAgentWithdrawalRecordPageList(param).getData().getPages().getRecords(), AgentWithdrawalRecordExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_WITHDRAWAL_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

    @PostMapping("getWithdrawTotal")
    @Operation(summary = "统计代理提款")
    public ResponseVO<AgentWithdrawalStatisticsVO> getWithdrawTotal(@RequestBody @Validated AgentWithdrawalRecordReqVO requestVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        requestVO.setSiteCode(siteCode);
        if (StringUtils.isBlank(requestVO.getCurrencyCode())) {
            throw new BaowangDefaultException(ResultCode.CURRENCY_CODE_NOT_EXIT);
        }
        return ResponseVO.success(agentWithdrawReviewRecordApi.getWithdrawTotal(requestVO));
    }
}

