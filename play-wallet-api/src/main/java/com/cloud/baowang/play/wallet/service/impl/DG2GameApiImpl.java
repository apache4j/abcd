package com.cloud.baowang.play.wallet.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.wallet.enums.DGErrorEnum;
import com.cloud.baowang.play.wallet.service.base.BaseService;
import com.cloud.baowang.play.wallet.service.DGGameApi;
import com.cloud.baowang.play.wallet.vo.dg.DGCryptoConfig;
import com.cloud.baowang.play.wallet.vo.dg.vo.DG2Constant;
import com.cloud.baowang.play.wallet.vo.req.dg.DGActionVo;
import com.cloud.baowang.play.wallet.vo.res.dg.DGBaseRsp;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class DG2GameApiImpl extends BaseService implements DGGameApi {

    private final UserInfoApi userInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final UserCoinRecordApi userCoinRecordApi;

    private final VenueUserAccountApi venueUserAccountApi;

    private final GameInfoApi gameInfoApi;

    private final DGCryptoConfig dgCryptoConfig;


    @Override
    public DGBaseRsp getBalance(String agentName, DGActionVo actionVo) {
        log.info(" getBalance 获取余额 : agentName : "+agentName +" actionVo : "+actionVo);
        if (!dgCryptoConfig.getAgentName().equals(agentName)) {
            return DGBaseRsp.failed(DGErrorEnum.AGENT_ACCOUNT_NOT_FOUND,actionVo.getMember().getUsername());
        }
        String userName = actionVo.getMember().getUsername();
//        String thirdUserId = convertUserName(userName);
//        CasinoMemberReqVO casinoMember = new CasinoMemberReqVO();
//        casinoMember.setVenueUserAccount(thirdUserId);
//        casinoMember.setVenueCode(VenueEnum.DG2.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMember);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return DGBaseRsp.failed(DGErrorEnum.MEMBER_NOT_FOUND,thirdUserId);
//        }
        String userId = convertUserName(userName);
        if (userId == null) {
            return DGBaseRsp.failed(DGErrorEnum.MEMBER_NOT_FOUND,userName);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DG2.getVenueCode());
            return DGBaseRsp.failed(DGErrorEnum.ACCOUNT_LOCKED,userName);
        }
        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        if (userCenterCoin.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
            BigDecimal balance = BigDecimal.ZERO;
            return  DGBaseRsp.success(balance, userName);
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount();
        return DGBaseRsp.success(totalAmount.setScale(2,RoundingMode.DOWN), userName);
    }

    @Override
    public DGBaseRsp transfer(String agentName, DGActionVo actionVo) {
        log.info(" transfer 请求转账 : agentName : "+agentName +" actionVo : "+actionVo);
        if (!dgCryptoConfig.getAgentName().equals(agentName)) {
            return DGBaseRsp.failed(DGErrorEnum.AGENT_ACCOUNT_NOT_FOUND,actionVo.getMember().getUsername());
        }
        String userId = userCheck(actionVo);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        DGBaseRsp isValid = checkRequestValid(userInfoVO, actionVo, true);
        if (isValid != null) {
            return isValid;
        }
        Integer actionType = actionVo.getType();
        return switch (actionType) {
            case DG2Constant.REFUND -> refund(userInfoVO,actionVo);
            case DG2Constant.TIP -> tip(userInfoVO,actionVo);
            case DG2Constant.BET -> bet(userInfoVO,actionVo);
            case DG2Constant.SETTLE,DG2Constant.REWARD -> settle(userInfoVO,actionVo);
            default -> DGBaseRsp.failed(DGErrorEnum.UNKNOWN_ERROR);
        };
    }

    @Override
    public DGBaseRsp inform(String agentName, DGActionVo actionVo) {
        log.info(" inform 检查并确认 : agentName : "+agentName +" actionVo : "+actionVo);
        if (!dgCryptoConfig.getAgentName().equals(agentName)) {
            return DGBaseRsp.failed(DGErrorEnum.AGENT_ACCOUNT_NOT_FOUND,actionVo.getMember().getUsername());
        }
        String userId = userCheck(actionVo);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        DGBaseRsp isValid = checkRequestValid(userInfoVO, actionVo, true);
        if (isValid != null) {
            return isValid;
        }
        Integer actionType = actionVo.getType();
        return switch (actionType) {
            case DG2Constant.REFUND -> refund(userInfoVO,actionVo);
            case DG2Constant.TIP -> cancelTip(userInfoVO,actionVo);
            case DG2Constant.BET -> cancelBet(userInfoVO,actionVo);
            case DG2Constant.SETTLE,DG2Constant.REWARD -> settle(userInfoVO,actionVo);
            default -> DGBaseRsp.failed(DGErrorEnum.UNKNOWN_ERROR);
        };
    }

    public String convertUserName(String input) {
        return input.replaceAll("\\D+", "");
    }

    /**
     *
     * @param userInfoVO
     * @param reqData
     * @return
     */
    private DGBaseRsp refund(UserInfoVO userInfoVO,DGActionVo reqData) {
//        log.info("dg2 refund : " + reqData.toString());
        String thirdAccount = reqData.getMember().getUsername();
        String orderId = String.valueOf(reqData.getTicketId());
        String data = String.valueOf(reqData.getData());
        String userId =userInfoVO.getUserId();
        String detail = reqData.getDetail();
        JSONObject obj = JSON.parseObject(detail);
        //重算覆盖
        Long parentBetId = obj.getLong("parentBetId");
        String finalOrderId,finalRemark;
        if (parentBetId != null) {
            finalOrderId= String.valueOf(parentBetId);
            finalRemark = orderId;
        }else {
            finalOrderId = orderId;
            finalRemark = data;
        }
        //订单状态
//        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecord(userInfoVO, data, false);
//        if (!userCoinRecordVOS.isEmpty()) {
//            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//            return  DGBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue(), thirdAccount);
//        }
        BigDecimal amount = reqData.getMember().getAmount();
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            //加钱
            CoinRecordResultVO coinRecordResultVO = this.handleRefund(userInfoVO, finalOrderId, amount, finalRemark);
            if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
                BigDecimal rspBalance = balance.subtract(amount);
                return  DGBaseRsp.success(rspBalance,amount, thirdAccount);
            } else {
                log.info("dg2 refund 加钱: 失败 : reqData -> "+ reqData);
                return DGBaseRsp.failed(DGErrorEnum.OPERATION_FAILED,thirdAccount);
            }

        } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
            //扣钱
            CoinRecordResultVO coinRecordResultVO = this.handleRefundReduce(userInfoVO, finalOrderId, amount, finalRemark);
            if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
                BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
                BigDecimal rspBalance = balance.subtract(amount);
                return  DGBaseRsp.success(rspBalance,amount, thirdAccount);
            } else {
                log.info("dg2 refund 扣钱: 失败 : reqData -> "+ reqData);
                return DGBaseRsp.failed(DGErrorEnum.OPERATION_FAILED,thirdAccount);
            }
        }else {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }

    }



    public DGBaseRsp checkRequestValid(UserInfoVO userInfoVO, DGActionVo req, boolean isBetting) {
        if (ObjectUtil.isEmpty(userInfoVO)) {
            log.info("DG2GameApiExtendImpl.getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.DG2.getVenueName());
            return DGBaseRsp.failed(DGErrorEnum.MEMBER_NOT_FOUND);
        }

        if (isBetting) {
            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
                    .venueCode(VenueEnum.DG2.getVenueCode())
                    .currencyCode(userInfoVO.getMainCurrency()).build();
            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
            String merchantNo = venueInfoVO.getMerchantNo();
            String apiKey = venueInfoVO.getAesKey();
            String md5Encode = MD5Util.MD5Encode(merchantNo + apiKey);
            String md5 = md5Encode ==null?"": md5Encode;
            if (!md5.equals(req.getToken())){
                return DGBaseRsp.failed(DGErrorEnum.ACCOUNT_LOCKED);
            }
            if (!venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.DG2.getVenueCode());
                return DGBaseRsp.failed(DGErrorEnum.ACCOUNT_LOCKED);
            }

            if (userGameLock(userInfoVO)) {
                log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DG2.getVenueCode());
                return DGBaseRsp.failed(DGErrorEnum.ACCOUNT_LOCKED);
            }

        }
        return null;
    }


    public String userCheck(DGActionVo reqData) {
        String userName = reqData.getMember().getUsername();
        return convertUserName(userName);
//        CasinoMemberReqVO casinoMember = new CasinoMemberReqVO();
//        casinoMember.setVenueUserAccount(thirdUserId);
//        casinoMember.setVenueCode(VenueEnum.DG2.getVenueCode());
//        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMember);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            return null;
//        }
//        return respVO.getData();
    }


    /**
     * 下注
     */
    public DGBaseRsp bet(UserInfoVO userInfoVO,DGActionVo reqData) {
//        log.info(" dg2 bet : "+reqData);
        String thirdAccount = reqData.getMember().getUsername();
        String orderId = String.valueOf(reqData.getTicketId());
        String data = String.valueOf(reqData.getData());
        String userId = userInfoVO.getUserId();
        //订单状态
        BigDecimal amount = reqData.getMember().getAmount();
        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecord(userInfoVO, data, true);
        if (!userCoinRecordVOS.isEmpty()) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
       }
        //检查余额
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }
        if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            log.error("{} 下注失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 下注金额 : {} ", VenueEnum.DG2.getVenueCode(), reqData, userCoin, amount);
            return DGBaseRsp.failed(DGErrorEnum.INSUFFICIENT_BALANCE);
        }

        CoinRecordResultVO coinRecordResultVO = this.handleBet(userInfoVO, orderId, amount, data);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            BigDecimal rspBalance = balance.subtract(amount);
            return  DGBaseRsp.success(rspBalance,amount, thirdAccount);
        } else {
            log.info("dg2 bet : 失败 : reqData -> "+ reqData);
            return DGBaseRsp.failed(DGErrorEnum.OPERATION_FAILED,thirdAccount);
        }

    }



    /**
     * 取消下注
     */
    public DGBaseRsp cancelBet( UserInfoVO userInfoVO,DGActionVo reqData) {
        log.info("dg2 cancelBet : " + reqData.toString());
        String thirdAccount = reqData.getMember().getUsername();
        String orderId = String.valueOf(reqData.getTicketId());
        String data = String.valueOf(reqData.getData());
        String userId = userInfoVO.getUserId();
        //订单状态
        BigDecimal amount = reqData.getMember().getAmount();
        List<UserCoinRecordVO> existOrders = checkCoinRecord(userInfoVO, data);
        UserCoinRecordVO betOrder = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.GAME_BET.getCode())).findAny().orElse(null);
        if (betOrder != null) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }
        UserCoinRecordVO cancelBet = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
        if (cancelBet !=null) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }

        CoinRecordResultVO coinRecordResultVO = this.handleCancelBet(userInfoVO, orderId, amount, data);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            BigDecimal rspBalance = balance.subtract(amount);
            return  DGBaseRsp.success(rspBalance,amount, thirdAccount);
        } else {
            return DGBaseRsp.failed(DGErrorEnum.OPERATION_FAILED,thirdAccount);
        }
    }



    /**
     * 派彩
     */
    public DGBaseRsp settle(UserInfoVO userInfoVO, DGActionVo reqData) {
//        log.info("dg2 settle : " + reqData.toString());
        String thirdAccount = reqData.getMember().getUsername();
        String orderId = String.valueOf(reqData.getTicketId());
        String data = String.valueOf(reqData.getData());
        String userId = userInfoVO.getUserId();
        //订单状态
        BigDecimal amount = reqData.getMember().getAmount();
        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecord(userInfoVO, data, false);
        if (!userCoinRecordVOS.isEmpty()) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }
        CoinRecordResultVO coinRecordResultVO = this.handleSettle(userInfoVO, orderId, amount, data);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            BigDecimal rspBalance = balance.subtract(amount);
            return  DGBaseRsp.success(rspBalance,amount, thirdAccount);
        } else {
            log.info("dg2 settle : 失败 : reqData -> "+ reqData);
            return DGBaseRsp.failed(DGErrorEnum.OPERATION_FAILED,thirdAccount);
        }
    }




    /**
     * 打赏
     */
    private DGBaseRsp tip(UserInfoVO userInfoVO, DGActionVo reqData) {
//        log.info("dg2 tip : " + reqData.toString());
        String thirdAccount = reqData.getMember().getUsername();
        String orderId = String.valueOf(reqData.getTicketId());
        String data = String.valueOf(reqData.getData());
        String userId = userInfoVO.getUserId();
        //订单状态
        BigDecimal amount = reqData.getMember().getAmount();
        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecord(userInfoVO, data, true);
        if (!userCoinRecordVOS.isEmpty()) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }
        //检查余额
        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }

        if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            log.error("{} 打赏失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 打赏金额 : {} ", VenueEnum.DG2.getVenueCode(), reqData, userCoin, amount);
            return DGBaseRsp.failed(DGErrorEnum.INSUFFICIENT_BALANCE);
        }
        CoinRecordResultVO coinRecordResultVO = this.handleBet(userInfoVO, orderId, amount, data);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            BigDecimal rspBalance = balance.subtract(amount);
            return  DGBaseRsp.success(rspBalance,amount, thirdAccount);
        } else {
            log.info("dg2 tip : 失败 : reqData -> "+ reqData);
            return DGBaseRsp.failed(DGErrorEnum.OPERATION_FAILED,thirdAccount);
        }
    }


    /**
     * 取消打赏
     */
    private DGBaseRsp cancelTip(UserInfoVO userInfoVO,DGActionVo reqData) {
//        log.info("dg2 cancelTip : " + reqData.toString());
        String thirdAccount = reqData.getMember().getUsername();
        String orderId = String.valueOf(reqData.getTicketId());
        String data = String.valueOf(reqData.getData());
        String userId = userInfoVO.getUserId();
        //订单状态
        BigDecimal amount = reqData.getMember().getAmount();
        List<UserCoinRecordVO> userCoinRecordVOS = checkExistCoinRecord(userInfoVO, data, true);
        if (!userCoinRecordVOS.isEmpty()) {
            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }
        CoinRecordResultVO coinRecordResultVO = this.handleCancelBet(userInfoVO, orderId, amount, data);
        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
            BigDecimal balance = coinRecordResultVO.getCoinAfterBalance().setScale(2, RoundingMode.DOWN);
            BigDecimal rspBalance = balance.subtract(amount);
            return  DGBaseRsp.success(rspBalance,amount, thirdAccount);
        } else {
            return DGBaseRsp.failed(DGErrorEnum.OPERATION_FAILED,thirdAccount);
        }
    }

    /**
     * 订单校验 支出? 收入?
     */
    public List<UserCoinRecordVO> checkExistCoinRecord(UserInfoVO userInfoVO, String orderId, boolean expenses) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setRemark(orderId);
        coinRecordRequestVO.setBalanceType(expenses ? CoinBalanceTypeEnum.EXPENSES.getCode() : CoinBalanceTypeEnum.INCOME.getCode());
        ResponseVO<List<UserCoinRecordVO>> cancelBetRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord.getData() != null && !cancelBetRecord.getData().isEmpty()) {
            //订单已处理
            return cancelBetRecord.getData();

        }
        return Lists.newArrayList();
    }

    /**
     * 订单校验 支出? 收入?
     */
    public List<UserCoinRecordVO> checkCoinRecord(UserInfoVO userInfoVO, String orderId) {
        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
        coinRecordRequestVO.setRemark(orderId);
        ResponseVO<List<UserCoinRecordVO>> cancelBetRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord.getData() != null && !cancelBetRecord.getData().isEmpty()) {
            //订单已处理
            return cancelBetRecord.getData();

        }
        return Lists.newArrayList();
    }





    /**
     * 处理取消下注
     */
    protected CoinRecordResultVO handleCancelBet(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.DG2.getVenueCode());
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }

    /**
     * 派彩
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO handleSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.DG2.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }

    /**
     * 下注
     */
    protected CoinRecordResultVO handleBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setVenueCode(VenueEnum.DG2.getVenueCode());
        userCoinAddVO.setRemark(remark);
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }




    /**
     * 补单
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO handleRefund(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.DG2.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }

    /**
     * 补单 - 扣钱
     *
     * @param userInfoVO   userInfo
     * @param orderNo      orderNo
     * @param payoutAmount amount
     * @return result
     */
    protected CoinRecordResultVO handleRefundReduce(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(orderNo);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVOPayout.setRemark(remark);
        userCoinAddVOPayout.setVenueCode(VenueEnum.DG2.getVenueCode());
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }

}
