package com.cloud.baowang.site.controller.deposit;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.api.UserManualDepositApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: mufan
 */
@Tag(name = "资金-资金审核-会员人工存款")
@AllArgsConstructor
@RestController
@RequestMapping("/userDepositReview/api")
public class UserDepositReviewController {

    private final SiteCurrencyInfoApi currencyInfoApi;
    private final SystemParamApi systemParamApi;
    private final SystemRechargeWayApi wayApi;
    private final UserManualDepositApi userManualDepositApi;



    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.USER_REVIEW_LOCK_STATUS);

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

    @Operation(summary = "人工充值审核列表")
    @PostMapping("userManualDepositPage")
    public ResponseVO<Page<UserManualDepositPageResVO>> userManualDepositPage(@Valid @RequestBody UserManualDepositPageReqVO userManualDepositPageReqVO) {
        userManualDepositPageReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userManualDepositPageReqVO.setOperator(CurrReqUtils.getAccount());
        return ResponseVO.success(userManualDepositApi.pageList(userManualDepositPageReqVO));
    }


    @Operation(summary = "锁定/解锁")
    @PostMapping(value = "lockOrUnLock")
    public ResponseVO<Boolean> lockOrUnLock(@RequestBody UserManualDepositLockOrUnLockVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userManualDepositApi.lockOrUnLock(vo);
    }

    @Operation(summary = "充值审核成功")
    @PostMapping(value = "paymentReviewSuccess")
    public ResponseVO<Boolean> paymentReviewSuccess(@RequestBody @Valid UserDepositReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userManualDepositApi.paymentReviewSuccess(vo);
    }

    @Operation(summary = "充值审核拒绝")
    @PostMapping(value = "paymentReviewFail")
    public ResponseVO<Boolean> paymentReviewFail(@RequestBody @Valid UserDepositReviewReqVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        return userManualDepositApi.paymentReviewFail(vo);
    }

}
