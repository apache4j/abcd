package com.cloud.baowang.user.controller.wallet;


import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserQueryVO;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawWayApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawApi;
import com.cloud.baowang.wallet.api.vo.withdraw.CheckRemainingFlowVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithDrawApplyVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayListVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Tag(name = "客户端-我的-提现")
@RestController
@AllArgsConstructor
@RequestMapping("/userWithdraw/api")
public class UserWithdrawController {

    private final SystemWithdrawWayApi withdrawWayApi;


    private final UserWithdrawApi userWithdrawApi;

    private final UserInfoApi userInfoApi;

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final SiteUserLabelConfigApi siteUserLabelConfigApi;



    @Operation(summary = "获取提款方式列表")
    @PostMapping("withdrawWayList")
    public ResponseVO<List<WithdrawWayListVO>> withdrawWayList(){
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        UserQueryVO queryVO = UserQueryVO.builder().userAccount(userAccount).siteCode(siteCode).build();
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByQueryVO(queryVO);
        WithdrawWayRequestVO withdrawWayRequestVO  = new WithdrawWayRequestVO();
        withdrawWayRequestVO.setMainCurrency(userInfoVO.getMainCurrency());
        withdrawWayRequestVO.setVipRank(userInfoVO.getVipRank());
        withdrawWayRequestVO.setSiteCode(userInfoVO.getSiteCode());
        List<WithdrawWayListVO> withdrawWayListVOS = withdrawWayApi.withdrawWayList(withdrawWayRequestVO);
        return ResponseVO.success(withdrawWayListVOS);

    }



    @Operation(summary = "获取提款详情配置")
    @PostMapping("getWithdrawConfig")
    public ResponseVO<WithdrawConfigVO> getWithdrawConfig(@RequestBody WithdrawConfigRequestVO withdrawConfigRequestVO){
        String userId = CurrReqUtils.getOneId();

        withdrawConfigRequestVO.setUserId(userId);


        WithdrawConfigVO withdrawConfigVO = userWithdrawApi.getWithdrawConfig(withdrawConfigRequestVO);
        return ResponseVO.success(withdrawConfigVO);
    }

    @Operation(summary = "校验流水是否满足提款")
    @PostMapping("checkRemainingFlow")
    public ResponseVO<CheckRemainingFlowVO> checkRemainingFlow(){
        String userId = CurrReqUtils.getOneId();

        CheckRemainingFlowVO checkRemainingFlowVO = userWithdrawApi.checkRemainingFlow(userId);
        return ResponseVO.success(checkRemainingFlowVO);
    }
    @Operation(summary = "会员提款申请")
    @PostMapping("withdrawApply")
    public ResponseVO<Integer> withdrawApply(@RequestBody UserWithDrawApplyVO userWithDrawApplyVO){
        String userAccount = CurrReqUtils.getAccount();
        String ip = CurrReqUtils.getReqIp();
        userWithDrawApplyVO.setApplyIp(ip);
        Integer deviceType = CurrReqUtils.getReqDeviceType();
        userWithDrawApplyVO.setDeviceType(String.valueOf(deviceType));
        userWithDrawApplyVO.setApplyDomain(CurrReqUtils.getReferer());
        userWithDrawApplyVO.setUserId(CurrReqUtils.getOneId());
        userWithDrawApplyVO.setDeviceNo(CurrReqUtils.getReqDeviceId());
        return userWithdrawApi.userWithdrawApply(userWithDrawApplyVO);

    }

    @Operation(summary = "获取提款汇率")
    @PostMapping("getWithdrawExchange")
    public ResponseVO<BigDecimal> getRechargeExchange(){
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        UserInfoVO userInfoVO = userInfoApi.getByUserId(CurrReqUtils.getOneId());
        exchangeRateRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        exchangeRateRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.WITHDRAW.getCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        return ResponseVO.success(exchangeRate);
    }

}
