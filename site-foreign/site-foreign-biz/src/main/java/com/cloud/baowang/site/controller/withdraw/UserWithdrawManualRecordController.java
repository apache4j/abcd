package com.cloud.baowang.site.controller.withdraw;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SiteWithdrawWayApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawManualRecordApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualRecordPageResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualDetailReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualPayReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualRecordExportVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualRecordlDetailVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@RequestMapping("user-withdraw-manual-record/api")
@Tag(name = "资金-资金审核-会员人工提款")

public class UserWithdrawManualRecordController {

    private final SiteCurrencyInfoApi currencyInfoApi;
    private final SystemParamApi systemParamApi;

    private final UserWithdrawManualRecordApi userWithdrawManualRecordApi;

    private final SiteWithdrawWayApi withdrawWayApi;

    private final MinioUploadApi minioUploadApi ;


    @Operation(summary = "会员人工出款记录下拉框数据")
    @GetMapping("getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        ArrayList<String> param = new ArrayList<>();
        param.add(CommonConstant.DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {

            String siteCode = CurrReqUtils.getSiteCode();
            List<CodeValueVO> currencyDownBox = currencyInfoApi.getCurrencyListNo(siteCode);
            currencyDownBox = currencyDownBox.stream().filter(item -> !CommonConstant.PLAT_CURRENCY_CODE.equals(item.getCode())).toList();
            Map<String, List<CodeValueVO>> data = resp.getData();
            data.put("currencyCode", currencyDownBox);
            List<CodeValueVO> statusData = data.get(CommonConstant.DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS);
            statusData = statusData.stream().filter(item ->
                            String.valueOf(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode()).equals(item.getCode()) ||
                                    String.valueOf(DepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode()).equals(item.getCode()))
                    .toList();
            data.put(CommonConstant.DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS,statusData);


            ResponseVO<List<SiteWithdrawWayResVO>> wayResp = withdrawWayApi.queryWithdrawListBySite(CurrReqUtils.getSiteCode());
            if (wayResp.isOk()) {
                List<SiteWithdrawWayResVO> wayRespData = wayResp.getData();
                List<CodeValueVO> codeValueList = wayRespData.stream()
                        .filter(item -> WithdrawTypeEnum.MANUAL_WITHDRAW.getCode().equals(item.getWithdrawTypeCode()))
                        .map(item -> new CodeValueVO(item.getWithdrawId(), item.getWithdrawWayI18())) // 创建 CodeValueVO 对象
                        .collect(Collectors.toList());
                data.put("withdraw_way", codeValueList);
            }
            resp.setData(data);
        }
        return resp;

    }


    @Operation(summary = "会员人工出款")
    @PostMapping("withdrawManualPage")
    public ResponseVO<Page<UserWithdrawManualRecordPageResVO>> withdrawManualPage(@Valid @RequestBody UserWithdrawManualPageReqVO
                                                                                    userWithdrawManualPageReqVO) {
        userWithdrawManualPageReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userWithdrawManualPageReqVO.setOperator(CurrReqUtils.getAccount());
        userWithdrawManualPageReqVO.setCustomerStatusList(List.of(DepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode()));
        return ResponseVO.success(userWithdrawManualRecordApi.withdrawManualPage(userWithdrawManualPageReqVO));
    }

    @Operation(summary = "会员人工出款详情")
    @PostMapping("withdrawManualDetail")
    public ResponseVO<UserWithdrawManualRecordlDetailVO> withdrawManualDetail(@RequestBody UserWithdrawManualDetailReqVO vo) {
        Boolean dataDesensity = CurrReqUtils.getDataDesensity();
        vo.setDataDesensitization(dataDesensity);
        return ResponseVO.success(userWithdrawManualRecordApi.withdrawManualDetail(vo));
    }

    @Operation(summary = "出款成功")
    @PostMapping("withdrawManualPaySuccess")
    public ResponseVO<Boolean> withdrawManualPaySuccess(@RequestBody UserWithdrawManualPayReqVO vo) {
        vo.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode());
        return userWithdrawManualRecordApi.withdrawManualPay(vo);
    }

    @Operation(summary = "出款失败")
    @PostMapping("withdrawManualPayFail")
    public ResponseVO<Boolean> withdrawManualPayFail(@RequestBody UserWithdrawManualPayReqVO vo) {
        vo.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
        return userWithdrawManualRecordApi.withdrawManualPay(vo);
    }

    @Operation(summary = "会员人工出款记录")
    @PostMapping("withdrawManualRecordPage")
    public ResponseVO<Page<UserWithdrawManualRecordPageResVO>> withdrawManualRecordPage(@Valid @RequestBody UserWithdrawManualPageReqVO
                                                                                          userWithdrawManualPageReqVO) {
        userWithdrawManualPageReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userWithdrawManualPageReqVO.setOperator(CurrReqUtils.getAccount());
        if(StringUtils.isNotBlank(userWithdrawManualPageReqVO.getCustomerStatus())){
            userWithdrawManualPageReqVO.setCustomerStatusList(List.of(userWithdrawManualPageReqVO.getCustomerStatus()));
        }else{
            userWithdrawManualPageReqVO.setCustomerStatusList(List.of(DepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode(),
                    DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode()));
        }
        return ResponseVO.success(userWithdrawManualRecordApi.withdrawManualPage(userWithdrawManualPageReqVO));
    }
    @Operation(summary = "会员人工出款记录导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserWithdrawManualPageReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::userWithdrawManualRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        if(StringUtils.isNotBlank(vo.getCustomerStatus())){
            vo.setCustomerStatusList(List.of(vo.getCustomerStatus()));
        }else{
            vo.setCustomerStatusList(List.of(DepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode(),
                    DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode()));
        }
        ResponseVO<Long> responseVO = userWithdrawManualRecordApi.withdrawalManualRecordPageCount(vo);

        if(!responseVO.isOk()){
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserWithdrawManualRecordExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(userWithdrawManualRecordApi.withdrawManualPage(param).getRecords(), UserWithdrawManualRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_WITHDRAW_MANUAL_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
