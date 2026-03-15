package com.cloud.baowang.play.game.dg2;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.dg2.enums.DGErrorEnum;
import com.cloud.baowang.play.api.vo.dg2.req.DGActionVo;
import com.cloud.baowang.play.api.vo.dg2.rsp.DGBaseRsp;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.game.dg2.enums.SBPlayTypeEnum;
import com.cloud.baowang.play.game.dg2.utils.DGCryptoConfig;
import com.cloud.baowang.play.game.dg2.vo.DG2Constant;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.card.Card;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.api.enums.dg2.DG2GameTypeEnum;
import com.cloud.baowang.play.game.dg2.enums.DG2PlayTypeEnum;
import com.cloud.baowang.play.game.dg2.enums.DGCurrencyEnum;
import com.cloud.baowang.play.game.dg2.enums.DGLangEnum;
import com.cloud.baowang.play.game.dg2.enums.SBPlayTypeEnum;
import com.cloud.baowang.play.game.dg2.vo.DG2GameHistory;
import com.cloud.baowang.play.game.dg2.vo.DGConstant;
import com.cloud.baowang.play.game.sh.enums.ShOrderStatusEnum;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.operations.DomainRequestVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.DG2)
public class DG2ServiceImpl extends GameBaseService implements GameService {


    private final OrderRecordProcessService orderRecordProcessService;

    private final GameInfoService gameInfoService;

    private final VenueInfoService venueInfoService;

    private final DomainInfoApi domainInfoApi;
    private final DGCryptoConfig dgCryptoConfig;
    private final UserInfoApi userInfoApi;
    private final UserCoinRecordApi userCoinRecordApi;





    private final static Integer SUCCESS_CODE = 0;

    private static final String URL = "https://api.dg99api.com/";

    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;

    private static final String apiKey = "ac96d432adb74d5ca896da6a172e076d";
    private static final String agentId = "DGTE01087S";
    private static final String userId = "Utest_28272974";



    public DGBaseRsp getBalance(String agentName, DGActionVo actionVo) {
        log.info(" getBalance 获取余额 : agentName : "+agentName +" actionVo : "+actionVo);
        if (!dgCryptoConfig.getAgentName().equals(agentName)) {
            return DGBaseRsp.failed(DGErrorEnum.AGENT_ACCOUNT_NOT_FOUND,actionVo.getMember().getUsername());
        }
        String userName = actionVo.getMember().getUsername();
        String userId = convertUserName(userName);
        if (userId == null) {
            return DGBaseRsp.failed(DGErrorEnum.MEMBER_NOT_FOUND,userName);
        }
        UserInfoVO userInfoVO = getByUserId(userId);
        if (userGameLock(userInfoVO)) {
            log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.DG2.getVenueCode());
            return DGBaseRsp.failed(DGErrorEnum.ACCOUNT_LOCKED,userName);
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (userCenterCoin.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
            BigDecimal balance = BigDecimal.ZERO;
            return  DGBaseRsp.success(balance, userName);
        }
        BigDecimal totalAmount = userCenterCoin.getTotalAmount();
        return DGBaseRsp.success(totalAmount.setScale(2, RoundingMode.DOWN), userName);
    }

    public DGBaseRsp transfer(String agentName, DGActionVo actionVo) {
        log.info(" transfer 请求转账 : agentName : "+agentName +" actionVo : "+actionVo);
        if (!dgCryptoConfig.getAgentName().equals(agentName)) {
            return DGBaseRsp.failed(DGErrorEnum.AGENT_ACCOUNT_NOT_FOUND,actionVo.getMember().getUsername());
        }
        String userId = userCheck(actionVo);
        UserInfoVO userInfoVO = getByUserId(userId);
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

    public DGBaseRsp inform(String agentName, DGActionVo actionVo) {
        log.info(" inform 检查并确认 : agentName : "+agentName +" actionVo : "+actionVo);
        if (!dgCryptoConfig.getAgentName().equals(agentName)) {
            return DGBaseRsp.failed(DGErrorEnum.AGENT_ACCOUNT_NOT_FOUND,actionVo.getMember().getUsername());
        }
        String userId = userCheck(actionVo);
        UserInfoVO userInfoVO = getByUserId(userId);
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
    private DGBaseRsp refund(UserInfoVO userInfoVO, DGActionVo reqData) {
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
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
            String mainCurrency = userInfoVO.getMainCurrency();
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(),
                    VenueEnum.DG2.getVenueCode(),
                    mainCurrency);
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
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }
        //检查余额
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }
        UserCoinRecordVO cancelBet = existOrders.stream().filter(order -> order.getCoinType().equals(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())).findAny().orElse(null);
        if (cancelBet !=null) {
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
            BigDecimal balance = userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
            return  DGBaseRsp.success(balance,amount, thirdAccount);
        }
        //检查余额
        UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
            UserCoinWalletVO userCoin = getUserCenterCoin(userId);
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
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord != null && !cancelBetRecord.isEmpty()) {
            //订单已处理
            return cancelBetRecord;

        }
        return org.apache.commons.compress.utils.Lists.newArrayList();
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
        List<UserCoinRecordVO> cancelBetRecord = getUserCoinRecords(coinRecordRequestVO);
        if (cancelBetRecord != null && !cancelBetRecord.isEmpty()) {
            //订单已处理
            return cancelBetRecord;

        }
        return org.apache.commons.compress.utils.Lists.newArrayList();
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
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
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
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.DG2.getVenueCode());
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        return toUserCoinHandle(userCoinAddVO);
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
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());

        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
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
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.DG2.getVenueCode());
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RECALCULATE_PAYOUT.getCode());
        return toUserCoinHandle(userCoinAddVO);
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
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(payoutAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(VenueEnum.DG2.getVenueCode());
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RECALCULATE_PAYOUT.getCode());

        return toUserCoinHandle(userCoinAddVO);
    }


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        log.info("dg2 createMember : "+casinoMemberVO);

        String currencyCode = casinoMemberVO.getCurrencyCode();
        String DGCurrencyCode = DGCurrencyEnum.getSexyCodeByCode(currencyCode);
        if (DGCurrencyCode == null) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }
        String agentId = venueDetailVO.getMerchantNo();
        String apiKey = venueDetailVO.getAesKey();

        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(apiKey)) {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

        String apiUrl = venueDetailVO.getApiUrl();
        String url = apiUrl + DGConstant.CREATE_MEMBER;

        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json");
        buildSign(agentId, apiKey, head);


        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("username", casinoMemberVO.getVenueUserAccount());
        dataMap.put("password", casinoMemberVO.getCasinoPassword());
        dataMap.put("currencyName", DGCurrencyCode);
        dataMap.put("winLimit", 0);
        String json = JSONObject.toJSONString(dataMap);
        String rsp = HttpClientHandler.post(url, head, json);

        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject.getInteger("codeId").equals(SUCCESS_CODE)) {
                return ResponseVO.success(true);
            } else {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

    }
    @Override
    public String genVenueUserPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        log.info("dg2 login : "+loginVO+"casinoMemberVO:"+casinoMemberVO);
        String currencyCode = loginVO.getCurrencyCode();
        String DGCurrencyCode = DGCurrencyEnum.getSexyCodeByCode(currencyCode);
        if (DGCurrencyCode == null) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }
        String agentId = venueDetailVO.getMerchantNo();
        String apiKey = venueDetailVO.getAesKey();

        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(apiKey)) {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

        String apiUrl = venueDetailVO.getApiUrl();
        String gameCode = loginVO.getGameCode();
        String dgLanguage = DGLangEnum.conversionLang(loginVO.getLanguageCode());
        String url = apiUrl + DGConstant.LOGIN ;
        Map<String, Object> dataMap = new HashMap<>();
        String casinoPassword = casinoMemberVO.getCasinoPassword();
        dataMap.put("username", casinoMemberVO.getVenueUserAccount());
        dataMap.put("password", casinoPassword);
        dataMap.put("currencyName", DGCurrencyCode);
        dataMap.put("winLimit", 0);
        dataMap.put("language", dgLanguage);
        String json = JSONObject.toJSONString(dataMap);
        log.info("dg2 登录参数 " + json);

        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json");
        buildSign(agentId, apiKey, head);
        String rsp = HttpClientHandler.post(url, head, json);
        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject.getInteger("codeId").equals(SUCCESS_CODE)) {
                JSONArray list = jsonObject.getJSONArray("list");
                if (list.isEmpty()) {
                    throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                }
                List<String> urlList = list.toJavaList(String.class);
                String source = urlList.get(0);
                //https://new-dd-cn.izogcnj.com/ddnewpc/direct1.html?token=40913ee575ec4bc5a64058e8be1e468d&language=cn
                String venueUrl = source+"&tableId="+gameCode+"&showapp=off";
                String userAccount = loginVO.getUserAccount();
                String venueCode = venueDetailVO.getVenueCode();
//                ResponseVO<Page<DomainVO>> domainPage = domainInfoApi.queryDomainPage(DomainRequestVO.builder().siteCode(casinoMemberVO.getSiteCode()).build());
//                if (domainPage.isOk()){
//                    List<DomainVO> domainVOS = domainPage.getData().getRecords();
//                    DomainVO domainVO = domainVOS.stream().filter(domain -> domain.getDomainType() == 2).findAny().orElse(null);
//                    if (domainVO != null) {
//                        String URL_PREFIX = "https://";
//                        String domainAddr = URL_PREFIX + domainVO.getDomainAddr();
//                        venueUrl= venueUrl+"&backUrl="+ domainAddr +"&backType=enter";
//                    }
//                }
                /**
                 * https://new-dd-cn.izogcnj.com/ddnewpc/direct1.html
                 * ?token=40913ee575ec4bc5a64058e8be1e468d&
                 * language=cn&tableId=20501
                 * &backUrl=https://www.google.com&backType=enter
                 */
                GameLoginVo gameLoginVo = GameLoginVo.builder()
                        .source(venueUrl)
                        .type(GameLoginTypeEnums.URL.getType())
                        .userAccount(userAccount)
                        .venueCode(venueCode).build();
                return ResponseVO.success(gameLoginVo);
            } else {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            throw new BaowangDefaultException(ResultCode.CREATE_MEMBER_FAIL);
        }

    }

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        List<JSONObject> resultList = Lists.newArrayList();
        List<GameInfoPO> gameInfoList = gameInfoService.queryGameByVenueCode(venueInfoVO.getVenueCode());
        gameInfoList.forEach(gameInfo -> {
            JSONObject gameJson = new JSONObject();
            gameJson.put("deskName", gameInfo.getGameName());
            gameJson.put("deskNumber", gameInfo.getAccessParameters());
            resultList.add(gameJson);
        });
        return ResponseVO.success(resultList);
    }


    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        log.info("dg2 getBetRecordList : "+venuePullParamVO);
        String apiUrl = venueInfoVO.getApiUrl();
        String agentId = venueInfoVO.getMerchantNo();
        String apiKey = venueInfoVO.getAesKey();
        String url = apiUrl + DGConstant.ORDER_RECORD;
        Map<String, String> head = Maps.newHashMap();
        buildSign(agentId, apiKey, head);
        try {
            String rsp = HttpClientHandler.doPostPar(url, head, null);
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            if (jsonObject == null || !Objects.equals(jsonObject.getInteger("codeId"), SUCCESS_CODE)) {
                log.error("{} 拉取注返回异常，返回：{} : 当前时间 : {} ", venueInfoVO.getVenueCode(), rsp, System.currentTimeMillis());
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            log.info("DG2三方拉单返回结果 : {}  代理ID : {} ", rsp, agentId);
            JSONArray dataArray = jsonObject.getJSONArray("list");
            if (dataArray.isEmpty()) {
                return ResponseVO.success();
            }
            List<DG2GameHistory> gameHistoryList = dataArray.toJavaList(DG2GameHistory.class);
            //标记下
            List<Long> idList = gameHistoryList.stream().map(DG2GameHistory::getId).collect(Collectors.toList());
            if (!markReport(apiUrl, agentId, apiKey, idList)) {
                return ResponseVO.success();
            }
            handleRemoteOrder(gameHistoryList, venueInfoVO);
        } catch (Exception e) {
            log.info("DG2 拉取注单失败 " ,e);
        }

        return ResponseVO.success();

    }

    private static void buildSign(String agentId, String apiKey, Map<String, String> head) {
        String time = String.valueOf(System.currentTimeMillis());
        String md5Key = agentId + apiKey + time;
        String sign = MD5Util.MD5Encode(md5Key);
        head.put("agent", agentId);
        head.put("sign", sign);
        head.put("time", time);
    }

    private void handleRemoteOrder(List<DG2GameHistory> gameHistoryList, VenueInfoVO venueInfoVO) {
        //三方账号
        List<String> userIds = gameHistoryList.stream().map(order -> adaptUsername(order.getUserName())).distinct().toList();
        Map<String, UserInfoVO> userMap = getUserInfoByUserIds(userIds);

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = getLoginInfoByUserIds(userIds);

        //vip返水配置
        Map<String, String> rebateMap = getVIPRebateSingleVOMap(VenueEnum.DG2.getVenueCode());

        //站点信息
        Map<String, String> siteNameMap = getSiteNameMap();

        //游戏信息
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.DG2.getVenueCode());

        List<OrderRecordVO> list = new ArrayList<>();

        for (DG2GameHistory order : gameHistoryList) {
            String userId = adaptUsername(order.getUserName());
            UserInfoVO userInfoVO = userMap.get(userId);
            if (userInfoVO == null) {
                log.info("{} 用户账号{}不存在", venueInfoVO.getVenueCode(), order.getUserName());
                continue;
            }
            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
            // 映射原始注单
            OrderRecordVO recordVO = parseRecords(venueInfoVO, order, userInfoVO, userLoginInfoVO, rebateMap, siteNameMap, paramToGameInfo);
            Integer gameTypeId = venueInfoService.getVenueTypeByCode(VenueEnum.SH.getVenueCode());
            recordVO.setVenueType(gameTypeId);
            recordVO.setSiteName(siteNameMap.get(userInfoVO.getSiteCode()));
            recordVO.setSiteCode(userInfoVO.getSiteCode());
            recordVO.setUserName(userInfoVO.getUserName());
            recordVO.setVenueType(VenueEnum.DG2.getType().getCode());
            list.add(recordVO);
            if (list.size() == 300) {
                orderRecordProcessService.orderProcess(list);
                list.clear();
            }

        }
        // 订单处理
        if (CollectionUtil.isNotEmpty(list)) {
            orderRecordProcessService.orderProcess(list);
        }

    }

    public String adaptUsername(String source) {
        return source.replaceAll("\\D+", "");
    }

    private boolean markReport(String apiUrl, String agentId, String apiKey, List<Long> idList) {
        String url = apiUrl + DGConstant.MARK_REPORT;
        Map<String, String> head = Maps.newHashMap();
        buildSign(agentId, apiKey, head);
        Map<String, List<Long>> body = Maps.newHashMap();
        body.put("list",idList);
        String json = JSONObject.toJSONString(body);
        String rsp = HttpClientHandler.post(url, head, json);
        log.info(" 标记注单已抓取 : rsp " + rsp+" idlist : "+idList);
        if (rsp != null) {
            JSONObject jsonObject = JSONObject.parseObject(rsp);
            Integer codeId = jsonObject.getInteger("codeId");
            return codeId != null && codeId == 0;
        }
        return false;
    }


    private OrderRecordVO parseRecords(VenueInfoVO venueDetailVO, DG2GameHistory orderResponseVO, UserInfoVO userInfoVO, UserLoginInfoVO userLoginInfoVO, Map<String, String> rebateMap, Map<String, String> siteNameMap, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());

        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getBetPoints());
        recordVO.setBetIp(orderResponseVO.getIp() == null ? null : orderResponseVO.getIp());
        Long betTime = TimeZoneUtils.formatDateStrToTimestamp(orderResponseVO.getBetTime());
        recordVO.setBetTime(betTime);
        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
        recordVO.setVenueCode(venueDetailVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getUserName());
        if (userLoginInfoVO != null) {
            if (userLoginInfoVO.getLoginTerminal() != null) {
                recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
        }

        String gameId = orderResponseVO.getGameId().toString();
        String tableId = String.valueOf(orderResponseVO.getTableId());

        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setRoomType(gameId);
        recordVO.setGameNo(orderResponseVO.getExt());
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(String.valueOf(orderResponseVO.getId()));
        recordVO.setTransactionId(String.valueOf(orderResponseVO.getId()));
        recordVO.setValidAmount(orderResponseVO.getAvailableBet());
        recordVO.setPayoutAmount(orderResponseVO.getWinOrLoss());
        BigDecimal winLose = orderResponseVO.getWinOrLoss().subtract(orderResponseVO.getBetPoints());
        recordVO.setWinLossAmount(winLose);
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());

        Integer orderStatus = getLocalOrderStatus(orderResponseVO,recordVO);
        recordVO.setOrderStatus(orderStatus);
        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));

        //结算时间
        String calTimeStr = orderResponseVO.getCalTime();
        Long calTime = TimeZoneUtils.formatDateStrToTimestamp(calTimeStr);
        recordVO.setSettleTime(calTime);
        if (Objects.equals(orderStatus, OrderStatusEnum.CANCEL.getCode())) {
            recordVO.setFirstSettleTime(calTime);
        }
        recordVO.setReSettleTime(0L);
        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
        recordVO.setDeskNo(String.valueOf(orderResponseVO.getTableId()));
        recordVO.setBootNo(String.valueOf(orderResponseVO.getShoeId()));
        recordVO.setPlayType(getPlayType(orderResponseVO));
        recordVO.setBetContent(recordVO.getPlayType());
        recordVO.setResultList(recordVO.getPlayType());
        GameInfoPO gameInfoPO = paramToGameInfo.get(tableId);
        if (gameInfoPO != null) {
            recordVO.setRoomTypeName(gameInfoPO.getGameName());
        }else {
            recordVO.setRoomTypeName(DG2GameTypeEnum.enumOfCode(String.valueOf(orderResponseVO.getGameId())).getDescription());
        }
        recordVO.setThirdGameCode(DG2GameTypeEnum.enumOfCode(String.valueOf(orderResponseVO.getGameId())).getGameType());
        recordVO.setGameName(DG2GameTypeEnum.enumOfCode(String.valueOf(orderResponseVO.getGameId())).getGameType());
        recordVO.setPlayInfo(orderResponseVO.getBetDetail());
        buildBetDetailAndResult(recordVO,orderResponseVO);
        orderResponseVO.setBetResult(DG2OrderInfoUtil.getDGResultForBetResult(recordVO.getOrderInfo(),String.valueOf(orderResponseVO.getGameId()), LanguageEnum.ZH_CN.getLang()));
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));
        log.info("parentBetId : "+orderResponseVO.getParentBetId()+" - recordVO.Id - "+recordVO.getId());
        return recordVO;
    }


    public void buildBetDetailAndResult(OrderRecordVO recordVO, DG2GameHistory orderResponseVO){
//        log.info("orderResponseVO2 : "+orderResponseVO);
        String gameId = String.valueOf(orderResponseVO.getGameId());
        switch (gameId) {
            case "1":
            case "2":
            case "8":
            case "41":  // 区块链百家乐
                recordVO.setOrderInfo(buildBJLResultList(orderResponseVO));
                break;
            case "3":
            case "42":  // 龙虎类
                recordVO.setOrderInfo(buildLHResultList(orderResponseVO));
                break;
            case "4":
            case "47":
            case "5":
            case "48":  // 骰宝类 ,轮盘 ,番摊,色碟
            case "6":
            case "14":
                recordVO.setOrderInfo(buildSBResultList(orderResponseVO));

                break;

            case "11":  // 炸金花
            case "43":  // 区块链炸金花
                recordVO.setOrderInfo(buildZJHResultList(orderResponseVO));
                break;

            case "7":   // 斗牛
            case "44":  // 区块链牛牛
                recordVO.setOrderInfo(buildCowResultList(orderResponseVO));
                break;

            case "16":  // 三公
            case "45":  // 区块链三公
                recordVO.setOrderInfo(buildSGResultList(orderResponseVO));
                break;

            case "20":  // 安达巴哈
            case "46":  // 区块链安达巴哈
                recordVO.setOrderInfo(buildABResultList(orderResponseVO));
                break;

            case "21"://21点
            case "53":
                recordVO.setOrderInfo(buildBlackJackResultList(orderResponseVO));
                break;
            default:
                break;
        }
    }

    private String getPlayType(DG2GameHistory orderResponseVO) {
        //BetField
        StringBuilder playType = new StringBuilder();
        String result = orderResponseVO.getBetDetail();
        Map<String, Object> map = JSON.parseObject(result, Map.class);
        Integer gameId = orderResponseVO.getGameId();
        if (gameId == 5 || gameId == 48) {
            map.keySet().forEach(key -> {
                SBPlayTypeEnum.fromKey(key).ifPresent(SBPlayTypeEnum -> playType.append(SBPlayTypeEnum.getShCode()).append(","));
            });
        }else {
            map.keySet().forEach(key -> {
                DG2PlayTypeEnum.fromKey(key).ifPresent(DG2PlayTypeEnum -> playType.append(DG2PlayTypeEnum.getShCode()).append(","));
            });
        }
        orderResponseVO.setPlayType(playType.toString());
        return playType.toString();
    }


    public Integer getLocalOrderStatus(DG2GameHistory order,OrderRecordVO recordVO) {


        /**
         * GameType=1，parentBetId没有值，isRevocation=1的注单是普通投注注单。
         * GameType=1，parentBetId没有值，isRevocation=2 的注单是游戏结果撤销了的投注注单。
         * GameType=1，parentBetId有值，isRevocation=1的注单是游戏结果有修改的对冲注单(parentBetId为原注单ticketId)。
         * GameType=1，parentBetId有值，isRevocation=2的注单是游戏结果有撤销的对冲注单(parentBetId为原注单ticketId)。
         * GameType=1，parentBetId有值，isRevocation=3的注单是游戏结果有冻结的对冲注单(parentBetId为原注单ticketId)。
         * */

        /**
         *   NOT_SETTLE(0, "未结算", ClassifyEnum.NOT_SETTLE.getCode()),
         *     SETTLED(1, "已结算", ClassifyEnum.SETTLED.getCode()),
         *     CANCEL(2, "已取消", ClassifyEnum.CANCEL.getCode()),
         *     RESETTLED(4, "重新结算", ClassifyEnum.RESETTLED.getCode()),
         */
        Long parentBetId = order.getParentBetId();
        Integer isRevocation = order.getIsRevocation();
        log.info("parentBetId : "+parentBetId+" isRevocation : "+isRevocation);
        if (parentBetId == null){
            if (isRevocation == 0){
                return OrderStatusEnum.NOT_SETTLE.getCode();
            }
            if (isRevocation == 1){
                return OrderStatusEnum.SETTLED.getCode();
            }
            if (isRevocation == 2){
                return OrderStatusEnum.CANCEL.getCode();
            }
            if (isRevocation == 3){
                return OrderStatusEnum.ABERRANT.getCode();
            }
        }else {
            recordVO.setThirdOrderId(String.valueOf(parentBetId));
            log.info("parentBetId : "+parentBetId+" setThirdOrderId : "+recordVO.getId());
            if (isRevocation == 1){
                return OrderStatusEnum.RESETTLED.getCode();
            }
            if (isRevocation == 2){
                return OrderStatusEnum.CANCEL.getCode();
            }
            if (isRevocation == 3){
                return OrderStatusEnum.ABERRANT.getCode();
            }
        }

        return 0;

    }






    public static void loginTest() {

        String url = URL + "v2/wallet/login" + "?showapp=off";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("username", userId);
        dataMap.put("password", "322fc6fae6033e81c7475bcac09cc314");
        dataMap.put("currencyName", "MYR");
        dataMap.put("winLimit", 0);
        dataMap.put("language", "cn");
        String json = JSONObject.toJSONString(dataMap);

        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json");
        buildSign(agentId, apiKey, head);


        String rsp = HttpClientHandler.post(url, head, json);
        JSONObject jsonObject = JSONObject.parseObject(rsp);
        JSONArray list = jsonObject.getJSONArray("list");
        List<String> urlList = list.toJavaList(String.class);

        System.out.println("DG2ServiceImpl.loginTest ---- " + urlList);
    }

    public static void createMemberTest() {

        String url = URL + "v2/wallet/signup";
        Map<String, String> head = Maps.newHashMap();
        head.put("Content-Type", "application/json");
        buildSign(agentId, apiKey, head);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("username", userId);
        dataMap.put("password", "322fc6fae6033e81c7475bcac09cc314");
        dataMap.put("currencyName", "MYR");
        dataMap.put("winLimit", 0);
        String json = JSONObject.toJSONString(dataMap);
        String rsp = HttpClientHandler.post(url, head, json);
        System.out.println("dg 2 .createmember test ---- " + rsp);
    }


    public  int cardPoint(String ids) {
        int sum = 0;
        for (String s : ids.split("-")) {
            int id = Integer.parseInt(s.trim());
            int rank = ((id - 1) % 13) + 1;
            int val = (rank >= 10) ? 0 : rank;
            sum += val;
        }
        return sum % 10;
    }

    public void convertCard(List<String> source,List<Card> target) {
        source.stream().filter(power -> StringUtils.isNotEmpty(power)&& !CommonConstant.business_zero_str.equals(power)).forEach(cardStr -> target.add(new Card().formatCard(Integer.parseInt(cardStr))));

    }
    public String buildBJLResultList(DG2GameHistory orderResponseVO){
        log.info(" buildBJLResultList - "+orderResponseVO.getGameId());

        String result = orderResponseVO.getResult();
        Map<String, Object> map = JSON.parseObject(result, Map.class);

        String pokerJson = String.valueOf(map.get("poker"));

        Map<String, Object> pokerMap = JSON.parseObject(pokerJson, Map.class);
        if (pokerMap == null){
            return "";
        }
        List<Card> bankerCardList = Lists.newArrayList();
        List<Card> playerCardList = Lists.newArrayList();

        String banker = String.valueOf(pokerMap.get("banker"));
        String player = String.valueOf(pokerMap.get("player"));
        List<String> bankerPart = List.of(banker.split("-"));
        List<String> playerPart  = List.of(player.split("-"));
        convertCard(bankerPart,bankerCardList);
        convertCard(playerPart,playerCardList);
//        bankerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> bankerCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
//        playerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> playerCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("banker", bankerCardList);
        cardMap.put("player", playerCardList);
        String bjlResult = JSONObject.toJSONString(cardMap);
        // 大小
//        String result = String.valueOf(map.get("result"));
//        String[] arr = result.split(",");
//        String power = arr[1]; // "2"
//        if (!power.equals("-1")){
//            bjlResult.append(CommonConstant.COMMA2)
//                    .append(power.equals(CommonConstant.business_one_str) ? SHBetResultEnum.BJL_SMALL.getCode() : SHBetResultEnum.BJL_BIG.getCode());
//        }
        String resultJson = String.valueOf(map.get("result"));
        List<String> resultPart = List.of(resultJson.split(CommonConstant.COMMA));
        String winner = resultPart.get(0);
        switch (winner) {
            case CommonConstant.business_one_str -> orderResponseVO.setWinner("庄赢");
            case CommonConstant.business_two_str -> orderResponseVO.setWinner("庄赢庄对");
            case CommonConstant.business_three_str -> orderResponseVO.setWinner("庄赢闲对");
            case CommonConstant.business_four_str -> orderResponseVO.setWinner("庄赢庄对闲对");
            case CommonConstant.business_five_str -> orderResponseVO.setWinner("闲赢");
            case CommonConstant.business_six_str -> orderResponseVO.setWinner("闲赢庄对");
            case CommonConstant.business_seven_str -> orderResponseVO.setWinner("闲赢闲对");
            case CommonConstant.business_eight_str -> orderResponseVO.setWinner("闲赢庄对闲对");
            case CommonConstant.business_nine_str -> orderResponseVO.setWinner("和");
            case CommonConstant.business_ten_str -> orderResponseVO.setWinner("和庄对");
            case "11" -> orderResponseVO.setWinner("和闲对");
            case "12" -> orderResponseVO.setWinner("和庄对闲对");
        }


        return bjlResult.toString();
    }

    public String buildABResultList(DG2GameHistory orderResponseVO){
        log.info(" buildABResultList - "+orderResponseVO.getGameId());

        String result = orderResponseVO.getResult();
        Map<String, Object> map = JSON.parseObject(result, Map.class);

        String pokerJson = String.valueOf(map.get("poker"));

        Map<String, Object> pokerMap = JSON.parseObject(pokerJson, Map.class);
        if (pokerMap == null){
            return "";
        }
        List<Card> jokerCardList = Lists.newArrayList();
        List<Card> andarCardList = Lists.newArrayList();
        List<Card> baharCardList = Lists.newArrayList();

        String joker = String.valueOf(pokerMap.get("joker"));
        String andar = String.valueOf(pokerMap.get("andar"));
        String bahar = String.valueOf(pokerMap.get("bahar"));


        List<String> andarPart = List.of(andar.split("-"));
        List<String> baharPart  = List.of(bahar.split("-"));

        jokerCardList.add(new Card().formatCard(Integer.parseInt(joker)));
        andarPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> andarCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        baharPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> baharCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("joker", jokerCardList);
        cardMap.put("andar", andarCardList);
        cardMap.put("bahar", baharCardList);
        String bjlResult = JSONObject.toJSONString(cardMap);

        String resultJson = String.valueOf(map.get("result"));
        List<String> resultPart = List.of(resultJson.split(CommonConstant.COMMA));
        String winner = resultPart.get(0);
        switch (winner) {
            case CommonConstant.business_one_str -> orderResponseVO.setWinner("安达");
            case CommonConstant.business_two_str -> orderResponseVO.setWinner("巴哈");
        }
        return result;
    }



    public String buildLHResultList(DG2GameHistory orderResponseVO){
        log.info(" buildLHResultList - "+orderResponseVO.getGameId());

        String result = orderResponseVO.getResult();
        Map<String, Object> map = JSON.parseObject(result, Map.class);
        Map<String, Object> pokerMap = (Map<String, Object>) map.get("poker");
        if (pokerMap == null){
            return "";
        }
        String dragon = String.valueOf(pokerMap.get("dragon")) ;
        String tiger = String.valueOf(pokerMap.get("tiger")) ;

        int dragonNumber = Integer.parseInt(dragon);
        int tigerNumber = Integer.parseInt(tiger);
        List<Card> bankerCardList = Lists.newArrayList();
        List<Card> playerCardList = Lists.newArrayList();
        bankerCardList.add(new Card().formatCard(dragonNumber));
        playerCardList.add(new Card().formatCard(tigerNumber));
        Map<String,List<Card>> cardMap = new HashMap<>();
        cardMap.put("dragon",bankerCardList);
        cardMap.put("tiger",playerCardList);
        String bjlResult = JSONObject.toJSONString(cardMap);

        String resultJson = String.valueOf(map.get("result"));
        List<String> resultPart = List.of(resultJson.split(CommonConstant.COMMA));
        String winner = resultPart.get(0);
        switch (winner) {
            case CommonConstant.business_one_str -> orderResponseVO.setWinner("龙");
            case CommonConstant.business_two_str -> orderResponseVO.setWinner("虎");
            case CommonConstant.business_three_str -> orderResponseVO.setWinner("和");
        }

        return bjlResult;
    }

    public String buildZJHResultList(DG2GameHistory orderResponseVO){
        log.info(" buildZJHResultList - "+orderResponseVO.getGameId());

        String result = orderResponseVO.getResult();
        Map<String, Object> map = JSON.parseObject(result, Map.class);

        String pokerJson = String.valueOf(map.get("poker"));

        Map<String, Object> pokerMap = JSON.parseObject(pokerJson, Map.class);
        if (pokerMap == null){
            return "";
        }
        List<Card> blackCardList = Lists.newArrayList();
        List<Card> redCardList = Lists.newArrayList();

        String black = String.valueOf(pokerMap.get("black"));
        String red = String.valueOf(pokerMap.get("red"));
        List<String> bankerPart = List.of(black.split("-"));
        List<String> playerPart  = List.of(red.split("-"));

        convertCard(bankerPart,blackCardList);
        convertCard(playerPart,redCardList);
//        bankerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> blackCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
//        playerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> redCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("black", blackCardList);
        cardMap.put("red", redCardList);
        String bjlResult = JSONObject.toJSONString(cardMap);

        String resultJson = String.valueOf(map.get("result"));
        List<String> resultPart = List.of(resultJson.split(CommonConstant.COMMA));
        String winner = resultPart.get(0);
        switch (winner) {
            case CommonConstant.business_one_str -> orderResponseVO.setWinner("黑");
            case CommonConstant.business_two_str -> orderResponseVO.setWinner("红");
            case CommonConstant.business_three_str -> orderResponseVO.setWinner("和");
        }
        return bjlResult;
    }

    public String buildCowResultList(DG2GameHistory orderResponseVO){
        log.info(" buildCowResultList - "+orderResponseVO.getGameId());

        String result = orderResponseVO.getResult();
        Map<String, Object> map = JSON.parseObject(result, Map.class);

        String pokerJson = String.valueOf(map.get("poker"));

        Map<String, Object> pokerMap = JSON.parseObject(pokerJson, Map.class);
        if (pokerMap == null){
            return "";
        }
        List<Card> firstcardList = Lists.newArrayList();
        List<Card> bankerCardList = Lists.newArrayList();
        List<Card> player1CardList = Lists.newArrayList();
        List<Card> player2CardList = Lists.newArrayList();
        List<Card> player3CardList = Lists.newArrayList();


        String firstcard = String.valueOf(pokerMap.get("firstcard"));
        String banker = String.valueOf(pokerMap.get("banker"));
        String player1 = String.valueOf(pokerMap.get("player1"));
        String player2 = String.valueOf(pokerMap.get("player2"));
        String player3 = String.valueOf(pokerMap.get("player3"));



        List<String> bankerPart = List.of(banker.split("-"));
        List<String> player1Part  = List.of(player1.split("-"));
        List<String> player2Part  = List.of(player2.split("-"));
        List<String> player3Part  = List.of(player3.split("-"));

        firstcardList.add(new Card().formatCard(Integer.parseInt(firstcard)));
        bankerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> bankerCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));

        player1Part.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> player1CardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        player2Part.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> player2CardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        player3Part.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> player3CardList.add(new Card().formatCard(Integer.parseInt(cardStr))));

        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("firstcard", firstcardList);
        cardMap.put("banker", bankerCardList);
        cardMap.put("player1", player1CardList);
        cardMap.put("player2", player2CardList);
        cardMap.put("player3", player3CardList);
        String bjlResult = JSONObject.toJSONString(cardMap);

        String resultJson = String.valueOf(map.get("result"));
        String[] parts = resultJson.split("\\|");
        String resultStr = parts.length > 1 ? parts[1] : "";
        StringBuilder winnerBuilder = new StringBuilder();
        if (!resultStr.isEmpty()) {
            String[] split = resultStr.split(CommonConstant.COMMA);
            for (int i = 0; i < split.length-1; i++) {
                if (i==0 ) {
                    //赢
                    if (split[i].equals(CommonConstant.business_one_str)){
                        winnerBuilder.append("闲1-赢").append(CommonConstant.COMMA);
                    }else {
                        winnerBuilder.append("闲1-输").append(CommonConstant.COMMA);
                    }
                }
                if (i==1 ) {
                    if (split[i].equals(CommonConstant.business_one_str)){
                        winnerBuilder.append("闲2-赢").append(CommonConstant.COMMA);
                    }else {
                        winnerBuilder.append("闲2-输").append(CommonConstant.COMMA);
                    }
                }
                if (i==3 ) {
                    if (split[i].equals(CommonConstant.business_one_str)){
                        winnerBuilder.append("闲2-赢").append(CommonConstant.COMMA);
                    }else {
                        winnerBuilder.append("闲2-输").append(CommonConstant.COMMA);
                    }
                }
            }
        }
        orderResponseVO.setWinner(winnerBuilder.toString());
        return bjlResult;
    }


    public String buildSGResultList(DG2GameHistory orderResponseVO){
        log.info(" buildSGResultList - "+orderResponseVO.getGameId());

        String result = orderResponseVO.getResult();
        Map<String, Object> map = JSON.parseObject(result, Map.class);

        String pokerJson = String.valueOf(map.get("poker"));

        Map<String, Object> pokerMap = JSON.parseObject(pokerJson, Map.class);
        if (pokerMap == null){
            return "";
        }
        List<Card> firstcardList = Lists.newArrayList();
        List<Card> bankerCardList = Lists.newArrayList();
        List<Card> player1CardList = Lists.newArrayList();
        List<Card> player2CardList = Lists.newArrayList();
        List<Card> player3CardList = Lists.newArrayList();


        String firstcard = String.valueOf(pokerMap.get("firstcard"));
        String banker = String.valueOf(pokerMap.get("banker"));
        String player1 = String.valueOf(pokerMap.get("player1"));
        String player2 = String.valueOf(pokerMap.get("player2"));
        String player3 = String.valueOf(pokerMap.get("player3"));



        List<String> bankerPart = List.of(banker.split("-"));
        List<String> player1Part  = List.of(player1.split("-"));
        List<String> player2Part  = List.of(player2.split("-"));
        List<String> player3Part  = List.of(player3.split("-"));

        firstcardList.add(new Card().formatCard(Integer.parseInt(firstcard)));
        bankerPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> bankerCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));

        player1Part.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> player1CardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        player2Part.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> player2CardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        player3Part.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> player3CardList.add(new Card().formatCard(Integer.parseInt(cardStr))));

        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("firstcard", firstcardList);
        cardMap.put("banker", bankerCardList);
        cardMap.put("player1", player1CardList);
        cardMap.put("player2", player2CardList);
        cardMap.put("player3", player3CardList);
        String bjlResult = JSONObject.toJSONString(cardMap);

        String resultJson = String.valueOf(map.get("result"));
        String[] parts = resultJson.split("\\|");
        String resultStr = parts.length > 1 ? parts[1] : "";
        StringBuilder winnerBuilder = new StringBuilder();
        if (!resultStr.isEmpty()) {
            String[] split = resultStr.split(CommonConstant.COMMA);
            for (int i = 0; i < split.length-1; i++) {
                if (i==0  ) {
                    switch (split[i]) {
                        case CommonConstant.business_one_str ->
                                winnerBuilder.append("闲1-庄赢").append(CommonConstant.COMMA);//庄赢
                        case CommonConstant.business_two_str ->
                                winnerBuilder.append("闲1-闲1赢").append(CommonConstant.COMMA);//闲赢
                        case CommonConstant.business_three_str ->
                                winnerBuilder.append("闲1-和").append(CommonConstant.COMMA);//和
                    }

                }
                if (i==1 ) {
                    switch (split[i]) {
                        case CommonConstant.business_one_str ->
                                winnerBuilder.append("闲2-庄赢").append(CommonConstant.COMMA);
                        case CommonConstant.business_two_str ->
                                winnerBuilder.append("闲2-闲2赢").append(CommonConstant.COMMA);
                        case CommonConstant.business_three_str ->
                                winnerBuilder.append("闲2-和").append(CommonConstant.COMMA);
                    }
                }
                if (i==3 ) {
                    switch (split[i]) {
                        case CommonConstant.business_one_str ->
                                winnerBuilder.append("闲3-庄赢").append(CommonConstant.COMMA);
                        case CommonConstant.business_two_str ->
                                winnerBuilder.append("闲3-闲3赢").append(CommonConstant.COMMA);
                        case CommonConstant.business_three_str ->
                                winnerBuilder.append("闲3-和").append(CommonConstant.COMMA);
                    }
                }
            }
        }
        orderResponseVO.setWinner(winnerBuilder.toString());
        return bjlResult;
    }


    public String buildBlackJackResultList(DG2GameHistory orderResponseVO){
        log.info(" buildBlackJackResultList - "+orderResponseVO.getGameId());

        String result = orderResponseVO.getResult();
        Map<String, Object> resultMap = JSON.parseObject(result, Map.class);

        String pokerJson = String.valueOf(resultMap.get("poker"));

        Map<String, Object> pokerMap = JSON.parseObject(pokerJson, Map.class);
        if (pokerMap == null){
            return "";
        }
        List<Card> bankerCardList = Lists.newArrayList();

        Map<String, List<Card>> cardMap = new HashMap<>();

        String betDetail = orderResponseVO.getBetDetail();
        JSONObject obj = JSON.parseObject(betDetail);
        int seat = obj.getIntValue("seat")+1;
        //闲家牌
        String player = String.valueOf(pokerMap.get("map"));
        Map<String, List<List<Integer>>> map = JSON.parseObject(player, new TypeReference<Map<String, List<List<Integer>>>>() {});
        for (Map.Entry<String, List<List<Integer>>> entry : map.entrySet()) {
            String key = entry.getKey();
            if (seat != Integer.parseInt(key)) {
                continue;
            }
            List<List<Integer>> values = entry.getValue();
            if (values.size() > 1) {
                // 多组牌
                int i = 1;
                for (List<Integer> value : values) {
                    List<Card> cardList = new ArrayList<>();
                    for (Integer cardValue : value) {
                        cardList.add(new Card().formatCard(cardValue));
                    }
                    String extraKey = key + "_" + i;
                    cardMap.put(extraKey, cardList);
                    i++;
                }
            } else if (!values.isEmpty()) {
                List<Card> cardList = new ArrayList<>();
                for (Integer cardValue : values.get(0)) {
                    cardList.add(new Card().formatCard(cardValue));
                }
                cardMap.put(key, cardList);
            }
        }

        //庄家牌
        JSONArray bankerArray = (JSONArray) pokerMap.get("b");

        List<Integer> bankerPart = bankerArray.toJavaList(Integer.class);
        bankerPart.forEach(cardStr -> bankerCardList.add(new Card().formatCard(cardStr)));
        cardMap.put("banker", bankerCardList);

        String bjlResult = JSONObject.toJSONString(cardMap);
        return bjlResult.toString();
    }




    public String buildSBResultList(DG2GameHistory orderResponseVO){
        StringBuilder bjlResult = new StringBuilder();
        String source = orderResponseVO.getResult();
        Map<String, String> map = JSON.parseObject(source, Map.class);
        String result = map.get("result");
        bjlResult.append(result);
        return bjlResult.toString();
    }



    public static void main(String[] args) {
//        loginTest();
    }


}
