package com.cloud.baowang.site.controller.withdraw;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
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
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordNotCollectInfoPagesVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordPagesVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResVO;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Tag(name = "资金-会员资金记录-会员提款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/user-withdraw-record/api")
public class UserWithdrawRecordController {

    private final UserWithdrawRecordApi userWithdrawRecordApi;

    private final SystemParamApi systemParamApi;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final SiteWithdrawWayApi withdrawWayApi;
    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        //币种，订单来源，订单状态，客户端状态，提款方式，是否为大额，是否为首次提款

        List<CodeValueVO> currencyDownBox = currencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode());
        List<String> param = new ArrayList<>();
        //订单来源
        param.add(CommonConstant.DEVICE_TYPE);
        //订单状态
        param.add(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
        //客户端状态
        param.add(CommonConstant.DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS);
        //申请时间和完成时间
        param.add(CommonConstant.APPLY_TIME_TYPE);

        //是否为大额
        param.add(CommonConstant.YES_NO);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            //提款方式
            ResponseVO<List<SiteWithdrawWayResVO>> wayResp = withdrawWayApi.queryWithdrawListBySite(CurrReqUtils.getSiteCode());

            Map<String, List<CodeValueVO>> result = resp.getData();
            result.put("currency_code", currencyDownBox);

            if (wayResp.isOk()) {
                List<SiteWithdrawWayResVO> data = wayResp.getData();
                List<CodeValueVO> codeValueList = data.stream()
                        .map(item -> new CodeValueVO(item.getWithdrawId(), item.getWithdrawWayI18())) // 创建 CodeValueVO 对象
                        .collect(Collectors.toList());
                result.put("recharge_way", codeValueList);
            }

            List<CodeValueVO> codeValueVOS = result.get(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
            if (CollectionUtil.isNotEmpty(codeValueVOS)) {
                //订单状态筛选
                List<String> statusList = new ArrayList<>();
                statusList.add(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.ORDER_AUDIT_REJECT.getCode());
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
            }
            List<CodeValueVO> yesNoList = result.get(CommonConstant.YES_NO);
            //是否都用这个isBigMoney，isFirstOut
            result.put("is_big_money", yesNoList);
            result.put("is_first_out", yesNoList);
            return ResponseVO.success(result);
        }
        return ResponseVO.success();
    }

    @Operation(summary = "提款记录列表")
    @PostMapping(value = "/withdrawalRecordPageList")
    public ResponseVO<UserWithdrawRecordPagesVO> withdrawalRecordPageList(@RequestBody UserWithdrawalRecordRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(userWithdrawRecordApi.withdrawalRecordPageList(vo));
    }

    @Operation(summary = "提款记录不包含提款信息列表")
    @PostMapping(value = "/withdrawalNotCollectInfoRecordPageList")
    public ResponseVO<UserWithdrawRecordNotCollectInfoPagesVO> withdrawalNotCollectInfoRecordPageList(@RequestBody UserWithdrawalRecordRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(Optional.ofNullable(userWithdrawRecordApi.withdrawalRecordPageList(vo)).map(data ->{
            Gson gson = new Gson();
            UserWithdrawRecordNotCollectInfoPagesVO info = gson.fromJson(gson.toJson(data), UserWithdrawRecordNotCollectInfoPagesVO.class);
            return info;
        }).orElse(null));
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserWithdrawalRecordRequestVO vo) {
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::userWithdrawRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long responseVO = userWithdrawRecordApi.withdrawalRecordPageCount(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserWithdrawRecordVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(withdrawalRecordPageList(param).getData().getPages().getRecords(), UserWithdrawRecordVO.class));

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

    @Operation(summary = "会员提款记录总数")
    @PostMapping(value = "/getWithDrawalRecord")
    public ResponseVO<UserDepositRecordRespVO> getWithDrawalRecord(@RequestBody UserWithdrawalRecordRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String currencyCode = vo.getCurrencyCode();
        if (StringUtils.isBlank(currencyCode)) {
            throw new BaowangDefaultException(ResultCode.CURRENCY_CODE_NOT_EXIT);
        }
        return ResponseVO.success(userWithdrawRecordApi.getWithDrawalRecord(vo));
    }
}
