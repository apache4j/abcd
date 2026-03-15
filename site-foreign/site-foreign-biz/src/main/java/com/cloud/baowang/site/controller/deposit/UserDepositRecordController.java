package com.cloud.baowang.site.controller.deposit;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
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
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.api.UserDepositRecordApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageRespVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Tag(name = "会员存款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/user-deposit-record/api")
public class UserDepositRecordController {

    private final UserDepositRecordApi userDepositRecordApi;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final MinioUploadApi minioUploadApi;
    private final SystemParamApi systemParamApi;
    private final SystemRechargeWayApi wayApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        //来源
        param.add(CommonConstant.DEVICE_TYPE);
        //审核状态
        param.add(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
        //客户端状态
        param.add(CommonConstant.DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS);
        //申请时间和完成时间
        param.add(CommonConstant.APPLY_TIME_TYPE);

        ResponseVO<Map<String, List<CodeValueVO>>> responseVO = systemParamApi.getSystemParamsByList(param);
        if (responseVO.isOk()) {
            Map<String, List<CodeValueVO>> data = responseVO.getData();
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
            if (CollectionUtil.isNotEmpty(codeValueVOS)) {
                //订单状态筛选 处理中 21 取消订单 98 失败100 成功101
                List<String> statusList = new ArrayList<>();
                statusList.add(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.FAIL.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
                Set<String> statusSet = Set.copyOf(statusList);
                codeValueVOS = codeValueVOS.stream()
                        .filter(codeValue -> statusSet.contains(codeValue.getCode()))
                        .toList();
                data.put(CommonConstant.DEPOSIT_WITHDRAW_STATUS, codeValueVOS);


            }
            List<CodeValueVO> currencyDownBox = currencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode());
            currencyDownBox = currencyDownBox.stream()
                    //过滤平台币
                    .filter(codeValueVO -> !CommonConstant.PLAT_CURRENCY_CODE.equals(codeValueVO.getCode()))
                    .collect(Collectors.toList());
            data.put("currency_code", currencyDownBox);
            //充值方式
            List<CodeValueVO> rechargeWayListBySiteCode = wayApi.getRechargeWayListBySiteCode(CurrReqUtils.getSiteCode());
            if (CollectionUtil.isNotEmpty(rechargeWayListBySiteCode)) {
                data.put("deposit_withdraw_way", rechargeWayListBySiteCode);
            }
        }

        return responseVO;
    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getUserDepositRecordPage")
    public ResponseVO<UserDepositRecordPageRespVO> getUserDepositRecordPage(@RequestBody UserDepositRecordPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(userDepositRecordApi.getUserDepositRecordPage(vo));
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserDepositRecordPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userDepositRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = userDepositRecordApi.getUserDepositRecordPageCount(vo);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserDepositRecordResponseVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> userDepositRecordApi.getUserDepositRecordPage(vo).getPages().getRecords());

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_DEPOSIT_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

    @Operation(summary = "会员存款记录总数")
    @PostMapping(value = "/getUserDepositRecord")
    public ResponseVO<UserDepositRecordRespVO> getUserDepositRecord(@RequestBody UserDepositRecordPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String currencyCode = vo.getCurrencyCode();
        if (StringUtils.isBlank(currencyCode)) {
            throw new BaowangDefaultException(ResultCode.CURRENCY_CODE_NOT_EXIT);
        }
        return ResponseVO.success(userDepositRecordApi.getUserDepositRecord(vo));
    }

}


