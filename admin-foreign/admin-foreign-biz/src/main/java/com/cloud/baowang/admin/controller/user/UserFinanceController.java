package com.cloud.baowang.admin.controller.user;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.agent.api.vo.agent.AgentUserDepositWithdrawVO;
import com.cloud.baowang.agent.api.vo.agent.UserBetsVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.UserChangeTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.agent.api.vo.agent.UserPromotionsVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.PlatformVenueRequestVO;
import com.cloud.baowang.report.api.vo.user.ReportUserBetsVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueTopVO;
import com.cloud.baowang.user.api.api.UserDetailsApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserDetails.UserDetailsReqVO;
import com.cloud.baowang.user.api.vo.UserFinanceQueryVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserManualUpDownApi;
import com.cloud.baowang.wallet.api.api.UserPlatformTransferApi;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserWithdrawRunningWaterVO;
import com.cloud.baowang.agent.api.vo.agent.UserFinanceVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserDepositWithdrawVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.WithdrawRunningWaterAddVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "会员-会员管理-会员详情-财务信息")
@RestController
@RequestMapping("/user-finance/api")
@AllArgsConstructor
public class UserFinanceController {

    private final UserCoinApi userCoinApi;

    private final UserTypingAmountApi userTypingAmountApi;

    private final UserWithdrawRecordApi userWithdrawRecordApi;

    private final UserInfoApi userInfoApi;

    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;

    private final ReportUserWinLoseApi reportUserWinLoseApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final UserPlatformTransferApi userPlatformTransferApi;

    private final UserDetailsApi userDetailsApi;

    private final UserManualUpDownApi userManualUpDownApi;

    private final ActivityParticipateApi activityParticipateApi;


    @Operation(summary = "会员钱包余额信息")
    @PostMapping(value = "/queryWalletInfo")
    public ResponseVO<UserCoinWalletVO> queryWalletInfo(
            @Valid @RequestBody UserBasicRequestVO requestVO) {
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setUserAccount(requestVO.getUserAccount());
        userCoinQueryVO.setSiteCode(requestVO.getSiteCode());
        //查询中心钱包
        UserCoinWalletVO userCoinWalletVO = userCoinApi.getUserCenterCoinAndPlatform(userCoinQueryVO);
        return ResponseVO.success(userCoinWalletVO);
    }

    @Operation(summary = "会员提现流水信息")
    @PostMapping(value = "/queryWithdrawRunningWater")
    public ResponseVO<UserWithdrawRunningWaterVO> queryWithdrawRunningWater(
            @Valid @RequestBody UserBasicRequestVO requestVO) {
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        return ResponseVO.success(userTypingAmountApi.getWithdrawRunningWater(ConvertUtil.entityToModel(requestVO, WalletUserBasicRequestVO.class)));
    }


    @Operation(summary = "会员财务信息 （备注信息，充提信息，投注信息，top平台统计）")
    @PostMapping(value = "/queryUserFinance")
    public ResponseVO<UserFinanceVO> queryUserFinance(
            @Valid @RequestBody UserFinanceQueryVO requestVO) {
        UserBasicRequestVO userBasicRequestVO = new UserBasicRequestVO();
        userBasicRequestVO.setUserAccount(requestVO.getUserAccount());
        userBasicRequestVO.setSiteCode(requestVO.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(userBasicRequestVO);
        UserFinanceVO userFinanceVO = new UserFinanceVO();
        if (null == userInfoVO || StringUtils.isBlank(userInfoVO.getUserId())) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        String userAccount = userInfoVO.getUserAccount();
        ReportUserBetsVO reportUserBetsVO = reportUserWinLoseApi.getUserBetsInfo(userAccount,userInfoVO.getSiteCode());


        reportUserBetsVO.setActivityAmount(reportUserBetsVO.getActivityAmount().add(reportUserBetsVO.getVipAmount()));
        reportUserBetsVO.setCompanyWinLose(reportUserBetsVO.getPlayerWinLose().add(reportUserBetsVO.getAlreadyUseAmount()).add(reportUserBetsVO.getAdjustAmount()));

        reportUserBetsVO.setCurrency(userInfoVO.getMainCurrency());
        reportUserBetsVO.setPlatformCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        UserBetsVO userBetsVO=new UserBetsVO();
        BeanUtils.copyProperties(reportUserBetsVO,userBetsVO);
        userFinanceVO.setUserBets(userBetsVO);
        WalletUserDepositWithdrawVO userDepositWithdrawVO = userWithdrawRecordApi.getUserDepositWithdraw(userInfoVO.getUserId());
        userDepositWithdrawVO.setCurrency(userInfoVO.getMainCurrency());
        AgentUserDepositWithdrawVO agentUserDepositWithdrawVO=new AgentUserDepositWithdrawVO();
        BeanUtils.copyProperties(userDepositWithdrawVO,agentUserDepositWithdrawVO);
        userFinanceVO.setUserDepositWithdraw(agentUserDepositWithdrawVO);
        //优惠活动信息
        //获取平台币兑换总额统计
        BigDecimal wtcToMainCurrencyAmount = userPlatformTransferApi.getTransferAmountByUserAccount(userAccount,userInfoVO.getSiteCode());
        UserPromotionsVO userPromotionsVO = new UserPromotionsVO();
        userPromotionsVO.setWtcToMainCurrencyAmount(wtcToMainCurrencyAmount);
        userPromotionsVO.setCurrency(userInfoVO.getMainCurrency());
        userPromotionsVO.setReceivedWtcAmount(userBetsVO.getActivityAmount().add(userBetsVO.getRebateAmount()));
        userPromotionsVO.setReceivedMainCurrencyAmount(userBetsVO.getAlreadyUseAmount().subtract(wtcToMainCurrencyAmount));
        userPromotionsVO.setPlatformCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userPromotionsVO.setUsedDiscountAmount(userBetsVO.getAlreadyUseAmount());
        userFinanceVO.setUserPromotionsVO(userPromotionsVO);
        return ResponseVO.success(userFinanceVO);
    }
    @Operation(summary = "平台统计")
    @PostMapping(value = "/platformVenue")
    public ResponseVO<Page<ReportUserVenueTopVO>> platformVenue(@Valid @RequestBody PlatformVenueRequestVO requestVO) {
        Map<String, String> map = playVenueInfoApi.getAdminVenueNameMap().getData();
        ResponseVO<Page<ReportUserVenueTopVO>> pageResponseVO =  reportUserVenueWinLoseApi.topPlatformVenue(requestVO);
        for (ReportUserVenueTopVO vo:pageResponseVO.getData().getRecords()) {
            vo.setVenueCodeText(map.get(vo.getVenueCode()));
        }
        return pageResponseVO;
    }


    @Operation(summary = "清除会员流水")
    @PostMapping(value = "/cleanWithdrawRunningWater")
    public ResponseVO<Object> cleanWithdrawRunningWater(@Valid @RequestBody UserBasicRequestVO requestVO) {
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(requestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        return userTypingAmountApi.cleanWithdrawRunningWater(CurrReqUtils.getSiteCode(),userInfoVO.getUserAccount());
    }

    @Operation(summary = "添加会员流水")
    @PostMapping(value = "/addWithdrawRunningWater")
    public ResponseVO<Object> addWithdrawRunningWater(@Valid @RequestBody WithdrawRunningWaterAddVO requestVO) {
        UserBasicRequestVO userBasicRequestVO = new UserBasicRequestVO();
        userBasicRequestVO.setUserAccount(requestVO.getUserAccount());
        userBasicRequestVO.setSiteCode(requestVO.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(userBasicRequestVO);
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        requestVO.setUserAccount(userInfoVO.getUserAccount());
        UserDetailsReqVO userDetailsReqVO = new UserDetailsReqVO();
        userDetailsReqVO.setUserAccount(requestVO.getUserAccount());
        userDetailsReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userDetailsReqVO.setChangeType(String.valueOf(UserChangeTypeEnum.ADD_TYPING.getCode()));
        userDetailsReqVO.setRemark(requestVO.getRemark());
        userDetailsReqVO.setTypingAmount(requestVO.getAddTypingAmount());
        return userDetailsApi.updateInformation(userDetailsReqVO, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

}
