package com.cloud.baowang.site.controller.withdraw;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawReviewRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.withdraw.AgentWithdrawReviewExcelVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@AllArgsConstructor
@RequestMapping("agent-withdraw-review-record/api")
@Tag(name = "资金-资金审核记录-代理提款审核记录")
public class AgentWithdrawReviewRecordController {

    private final AgentWithdrawReviewRecordApi agentWithdrawReviewRecordApi;
    private final SystemParamApi systemParamApi;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        ArrayList<String> param = new ArrayList<>();
        param.add(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
        param.add(CommonConstant.AUDIT_TIME_TYPE);
        ResponseVO<Map<String, List<CodeValueVO>>> responseVO = systemParamApi
                .getSystemParamsByList(param);
        if (responseVO.isOk()) {
            List<CodeValueVO> currencyDownBox = currencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode());

            List<String> statusList = new ArrayList<>();
            statusList.add(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode());
            statusList.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT_REJECT.getCode());
            /*statusList.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_AUDIT_SUCCESS.getCode());*/
            statusList.add(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
            statusList.add(DepositWithdrawalOrderStatusEnum.WITHDRAW_FAIL.getCode());
            statusList.add(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
            Map<String, List<CodeValueVO>> data = responseVO.getData();
            if (data.containsKey(CommonConstant.DEPOSIT_WITHDRAW_STATUS)) {

                List<CodeValueVO> codeValueVOS = data.get(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
                //会员和代理使用同一份订单状态，代理的没有挂单审核相关，移除掉多余的下拉
                codeValueVOS.removeIf(codeValueVO -> !statusList.contains(codeValueVO.getCode()));
                data.put(CommonConstant.DEPOSIT_WITHDRAW_STATUS, codeValueVOS);
            }
            data.put("currency_code", currencyDownBox);
            return ResponseVO.success(data);
        }
        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
    }


    @Operation(summary = "代理提款审核记录列表")
    @PostMapping("withdrawalReviewRecordPageList")
    public ResponseVO<Page<AgentWithdrawReviewRecordVO>> withdrawalReviewRecordPageList(@RequestBody AgentWithdrawReviewRecordPageReqVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        return ResponseVO.success(agentWithdrawReviewRecordApi.withdrawalReviewRecordPageList(vo));
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody AgentWithdrawReviewRecordPageReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::agentWithdrawReviewRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long responseVO = agentWithdrawReviewRecordApi.getTotal(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentWithdrawReviewExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(withdrawalReviewRecordPageList(param).getData().getRecords(), AgentWithdrawReviewExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_WITHDRAW_REVIEW_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

    @Operation(summary = "代理提款审核记录详情")
    @PostMapping("withdrawReviewRecordDetail")
    public ResponseVO<AgentWithdrawReviewDetailsVO> withdrawReviewRecordDetail(@RequestBody AgentWithdrawReviewDetailReqVO vo) {
        return ResponseVO.success(agentWithdrawReviewRecordApi.withdrawReviewRecordDetail(vo));
    }


}
