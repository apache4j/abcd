package com.cloud.baowang.play.game.sa;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.*;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.play.api.enums.SADefaultException;
import com.cloud.baowang.play.api.enums.sa.SAResultCodeEnums;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.sa.*;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.sa.constant.SAConstantApi;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.OrderRecordService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.SA)
@AllArgsConstructor
public class SAGameServiceImpl extends GameBaseService implements GameService {

    private final SiteApi siteApi;

    private final OrderRecordProcessService orderRecordProcessService;
    private final OrderRecordService orderRecordService;


    private String getData(String data) {
        data = URLDecoder.decode(data, StandardCharsets.UTF_8);
//        List<VenueInfoVO> venueInfoVOList = venueInfoService.getAdminVenueInfoByVenueCodeList(VenueEnum.SA.getVenueCode());
//        if (CollectionUtil.isEmpty(venueInfoVOList)) {
//            log.info("SA查询余额未查到场馆信息");
//            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
//        }
//        VenueInfoVO venueInfoVO = venueInfoVOList.get(0);

        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.SA,null);

        String encryptKey = venueInfoVO.getMerchantNo();

        String reqData = null;
        try {
            reqData = DESCBCUtil.decrypt(data, encryptKey);
        } catch (Exception e) {
            log.error("SA下注异常;", e);
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }
        log.info("解密后:{}", reqData);
        String json = toJson(reqData);
        log.info("转JSON:{}", json);
        return json;
    }

    public void checkGameUser(UserInfoVO userInfoVO) {
//        CheckActivityVO checkActivityVO = new CheckActivityVO();
//        checkActivityVO.setUserId(userInfoVO.getUserId());
//        checkActivityVO.setSiteCode(userInfoVO.getSiteCode());
//        checkActivityVO.setVenueCode(VenueEnum.SA.getVenueCode());
        BigDecimal venueAmount = checkJoinActivity(userInfoVO.getUserId(),userInfoVO.getSiteCode(),VenuePlatformConstants.SA);
        //不允许下注
        if (venueAmount == null || venueAmount.compareTo(BigDecimal.ZERO) > 0) {
            log.info("不允许下注.调用游戏校验失败,result:{}", venueAmount);
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }
    }

    public SAGetUserBalanceRes balanceAdjustment(String data) {
        String reqData = getData(data);
        SABalanceAdjustmentReq req = JSON.parseObject(reqData, SABalanceAdjustmentReq.class);
        if (Stream.of(
                req.getUsername(),
                req.getCurrency(),
                req.getAmount(),
                req.getTxnid(),
                req.getTimestamp(),
                req.getAdjustmenttype(),
                req.getAdjustmenttime()
        ).anyMatch(Objects::isNull)) {
            log.info("SA奖励参数异常:派彩金额缺失:{}", req);
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }

        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername(), VenuePlatformConstants.SA);
        UserInfoVO userInfoVO = getByUserId(userId);
        if (userInfoVO == null) {
            log.info("SA奖励参数异常,用户未找到:{}", userId);
            throw new SADefaultException(SAResultCodeEnums.ACCOUNT_NOT_FOUND.getCode());
        }


        if(req.getAmount().equals(BigDecimal.ZERO)){
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.SUCCESS.getCode())
                    .currency(req.getCurrency())
                    .amount(getBalance(userId))
                    .build();
        }

        SAAdjustmentTypeEnum saAdjustmentTypeEnum = SAAdjustmentTypeEnum.byCode(req.getAdjustmenttype());
        if(saAdjustmentTypeEnum == null){
            log.info("SA奖励,参数异常");
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }

        SACurrencyEnum saCurrencyEnum = SACurrencyEnum.byCodeCurrency(req.getCurrency());
        if (saCurrencyEnum == null) {
            log.info("SA奖励,场馆币种异常1:{}", VenueEnum.SA.getVenueCode());
            throw new SADefaultException(SAResultCodeEnums.INVALID_CURRENCY.getCode());
        }

        UserCoinRecordRequestVO recordRequestVO = new UserCoinRecordRequestVO();
        recordRequestVO.setOrderNo(req.getTxnid());
        recordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        recordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        recordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        Long giftRes = userCoinRecordPageCount(recordRequestVO);

        //赠送礼物给荷官,扣费
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getTxnid());
        userCoinAddVO.setUserName(userInfoVO.getUserName());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setVenueCode(VenuePlatformConstants.SA);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_TIPS.getCode());
        userCoinAddVO.setThirdOrderNo(req.getTxnid());
        //赠送奖赏-扣费
        if(saAdjustmentTypeEnum.getCode().equals(SAAdjustmentTypeEnum.GIFT.getCode())){
            if(giftRes > 0){
                log.info("{}:SA奖励, 重复扣款:{}", VenueEnum.SA.getVenueName(), req.getTxnid());
                return SAGetUserBalanceRes
                        .builder()
                        .username(req.getUsername())
                        .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                        .currency(req.getCurrency())
                        .amount(getBalance(userId))
                        .build();
            }
        }else{

            if(ObjectUtil.isEmpty(req.getAdjustmentdetails())){
                log.info("{}:SA奖励, 加款参数不足.没有返回扣费的ID详情:{}", VenueEnum.SA.getVenueName(), req.getTxnid());
                return SAGetUserBalanceRes
                        .builder()
                        .username(req.getUsername())
                        .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                        .currency(req.getCurrency())
                        .amount(getBalance(userId))
                        .build();
            }


            JSONObject detail = JSON.parseObject(req.getAdjustmentdetails(), JSONObject.class);
            String cancelTxnId = detail.getStr("canceltxnid");
            if(ObjectUtil.isEmpty(cancelTxnId)){
                log.info("{}:SA奖励, 加款参数不足.cancelTxnId为空:{}", VenueEnum.SA.getVenueName(), req.getTxnid());
                return SAGetUserBalanceRes
                        .builder()
                        .username(req.getUsername())
                        .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                        .currency(req.getCurrency())
                        .amount(getBalance(userId))
                        .build();
            }



            UserCoinRecordRequestVO canCelReq = new UserCoinRecordRequestVO();
            canCelReq.setOrderNo(cancelTxnId);
            canCelReq.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            canCelReq.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            canCelReq.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());

            Long cancelRes = userCoinRecordPageCount(canCelReq);
            //取消扣费扣费,优先判断是否有扣费,如果有扣费,则重新加款
            if(cancelRes <= 0){
                log.info("{}:SA取消奖励, 该单没有加款,不进行扣:{}", VenueEnum.SA.getVenueName(), req.getTxnid());
                return SAGetUserBalanceRes
                        .builder()
                        .username(req.getUsername())
                        .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                        .currency(req.getCurrency())
                        .amount(getBalance(userId))
                        .build();

            }


            //判断这笔单是否已经加过款,防止重复加款
            UserCoinRecordRequestVO addRecord = new UserCoinRecordRequestVO();
            addRecord.setOrderNo(cancelTxnId);
            addRecord.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            addRecord.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            addRecord.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            Long addRecordRes = userCoinRecordPageCount(addRecord);

            if(addRecordRes > 0){
                log.info("{}:SA奖励,重复加款,{}", VenueEnum.SA.getVenueName(), addRecordRes);
                return SAGetUserBalanceRes
                        .builder()
                        .username(req.getUsername())
                        .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                        .currency(req.getCurrency())
                        .amount(getBalance(userId))
                        .build();
            }

            userCoinAddVO.setOrderNo(cancelTxnId);
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVO.setVenueCode(VenuePlatformConstants.SA);
            userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
            userCoinAddVO.setThirdOrderNo(cancelTxnId);
        }



        CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(coinRecordResultVO)) {
            log.error("SA奖励,调用取消派彩失败1:orderNo:{},amount:{}", req.getTxnid(), req.getAmount());
            return SAGetUserBalanceRes.builder()
                    .error(SAResultCodeEnums.SYSTEM_ERROR.getCode())
                    .amount(getBalance(userId))
                    .username(req.getUsername())
                    .currency(req.getCurrency())
                    .build();
        }


        if (!coinRecordResultVO.getResult()) {
            log.error("SA奖励,调用派彩失败:orderNo:{},amount:{}", req.getTxnid(), req.getAmount());
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.SYSTEM_ERROR.getCode())
                    .currency(req.getCurrency())
                    .amount(getBalance(userId))
                    .build();
        }
        return SAGetUserBalanceRes
                .builder()
                .username(req.getUsername())
                .error(SAResultCodeEnums.SUCCESS.getCode())
                .currency(req.getCurrency())
                .amount(getBalance(userId))
                .build();
    }


    public SAGetUserBalanceRes playerWin(String data) {
        String reqData = getData(data);
        SAPlaceWinBetReq req = JSON.parseObject(reqData, SAPlaceWinBetReq.class);
        if (req.getUsername() == null || req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.info("SA派彩参数异常:派彩金额缺失:{}", req);
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }

        if (StringUtils.isBlank(req.getPayoutdetails())) {
            log.info("SA派彩参数异常:详情缺失:{}", req);
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }

        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername(), VenueEnum.SA.getVenueCode());

        UserInfoVO userInfoVO = getByUserId(userId);
        if (userInfoVO == null) {
            log.info("SA用户未找到:{}", userId);
            throw new SADefaultException(SAResultCodeEnums.ACCOUNT_NOT_FOUND.getCode());
        }

        SACurrencyEnum saCurrencyEnum = SACurrencyEnum.byCodeCurrency(req.getCurrency());

        if (saCurrencyEnum == null) {
            log.info("SA场馆币种异常1:{}", VenueEnum.SA.getVenueCode());
            throw new SADefaultException(SAResultCodeEnums.INVALID_CURRENCY.getCode());
        }

        JSONObject jsonDetail = JSON.parseObject(req.getPayoutdetails(), JSONObject.class);
        if(jsonDetail == null){
            log.info("SA派彩解析异常1:{}", userId);
            throw new SADefaultException(SAResultCodeEnums.GENERAL_ERROR.getCode());
        }

        if(!jsonDetail.containsKey("betlist")){
            log.info("SA派彩解析异常2:{}", userId);
            throw new SADefaultException(SAResultCodeEnums.GENERAL_ERROR.getCode());
        }

        List<SAPlaceWinBetDetailReq> detailList = JSONUtil.toList(jsonDetail.getStr("betlist"), SAPlaceWinBetDetailReq.class);

        for (SAPlaceWinBetDetailReq item : detailList) {
            UserCoinRecordRequestVO recordRequestVO = new UserCoinRecordRequestVO();
            recordRequestVO.setOrderNo(item.getTxnid());
            recordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
            recordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            recordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
            Long betCount = userCoinRecordPageCount(recordRequestVO);
            if(betCount <= 0){
                log.info("{}:用户钱包, 加款不进行加款,该单未下注:{}", VenueEnum.SA.getVenueName(), betCount);
                return SAGetUserBalanceRes
                        .builder()
                        .username(req.getUsername())
                        .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                        .currency(saCurrencyEnum.getCode())
                        .amount(getBalance(userId))
                        .build();
            }
        }


        UserCoinRecordRequestVO recordRequestVO = new UserCoinRecordRequestVO();
        recordRequestVO.setOrderNo(req.getTxnid());
        recordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        recordRequestVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        recordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        Long payOutCount = userCoinRecordPageCount(recordRequestVO);

        if(payOutCount > 0){
            log.info("{}:用户钱包, 加款不进行加款,该单已经加款:{}", VenueEnum.SA.getVenueName(), payOutCount);
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(getBalance(userId))
                    .build();
        }


        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getTxnid());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setUserName(userInfoVO.getUserName());
        userCoinAddVO.setCurrency(saCurrencyEnum.getPlatCurrencyCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));

//        CoinRecordResultVO coinRecordResultVO = userCoinApi.addCoin(userCoinAddVO);
        CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(coinRecordResultVO)) {
            log.error("SA调用派彩失败1:orderNo:{},amount:{}", req.getTxnid(), req.getAmount());
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.SYSTEM_ERROR.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(getBalance(userId))
                    .build();
        }

        if (!coinRecordResultVO.getResult()) {
            log.error("SA调用派彩失败2:orderNo:{},amount:{}", req.getTxnid(), req.getAmount());
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.SYSTEM_ERROR.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(getBalance(userId))
                    .build();
        }


        return SAGetUserBalanceRes
                .builder()
                .username(req.getUsername())
                .error(SAResultCodeEnums.SUCCESS.getCode())
                .currency(saCurrencyEnum.getCode())
                .amount(getBalance(userId))
                .build();
    }


    public SAGetUserBalanceRes placeBetCancel(String data) {
        String reqData = getData(data);
        SAPlayerCancelReq req = JSON.parseObject(reqData, SAPlayerCancelReq.class);

        String orderId = req.getTxn_reverse_id();

        if (StringUtils.isBlank(orderId) || req.getUsername() == null || req.getAmount() == null ||
                req.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.info("SA取消下注参数异常:参数缺失:{}", req);
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }

        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername(), VenueEnum.SA.getVenueCode());

        UserInfoVO userInfoVO = getByUserId(userId);
        if (userInfoVO == null) {
            log.info("SA取消下注-用户未找到:{}", userId);
            throw new SADefaultException(SAResultCodeEnums.ACCOUNT_NOT_FOUND.getCode());
        }

        SACurrencyEnum saCurrencyEnum = SACurrencyEnum.byCodeCurrency(req.getCurrency());

        if (saCurrencyEnum == null) {
            log.info("SA取消下注-场馆币种异常1:{}", VenueEnum.SA.getVenueCode());
            throw new SADefaultException(SAResultCodeEnums.INVALID_CURRENCY.getCode());
        }


        //先判断是否有扣费记录
        UserCoinRecordRequestVO betRecordRequestVO = new UserCoinRecordRequestVO();
        betRecordRequestVO.setUserId(userId);
        betRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        betRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        betRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        betRecordRequestVO.setOrderNo(orderId);
        List<UserCoinRecordVO> recordVOS = getUserCoinRecords(betRecordRequestVO);
        if (CollectionUtil.isEmpty(recordVOS)) {//该单没有下注记录
            log.info("下注前校验,交易回滚,该单没有下注记录,不进行回滚:{}", betRecordRequestVO);
            return SAGetUserBalanceRes.builder()
                    .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                    .amount(getBalance(userId))
                    .username(req.getUsername())
                    .currency(saCurrencyEnum.getCode())
                    .build();
        }


        UserCoinRecordRequestVO addRecordRequestVO = new UserCoinRecordRequestVO();
        //判断是否有加款记录
        addRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        addRecordRequestVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        addRecordRequestVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        addRecordRequestVO.setOrderNo(orderId);
        addRecordRequestVO.setUserId(userId);
        Long addCount = userCoinRecordPageCount(addRecordRequestVO);
        if (addCount > 0) {//该单已经加扣过款,不进行重复加款
            log.info("下注前校验,交易回滚,该单已经加扣过款,不进行重复加款:{}", addRecordRequestVO);
            return SAGetUserBalanceRes.builder()
                    .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                    .amount(getBalance(userId))
                    .username(req.getUsername())
                    .currency(saCurrencyEnum.getCode())
                    .build();
        }


        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderId);
        userCoinAddVO.setUserName(userInfoVO.getUserName());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setVenueCode(VenuePlatformConstants.SA);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setThirdOrderNo(orderId);
//        CoinRecordResultVO coinRecordResultVO = userCoinApi.addCoin(userCoinAddVO);
        CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(coinRecordResultVO)) {
            log.error("SA调用取消派彩失败1:orderNo:{},amount:{}", req.getTxnid(), req.getAmount());
            return SAGetUserBalanceRes.builder()
                    .error(SAResultCodeEnums.SYSTEM_ERROR.getCode())
                    .amount(getBalance(userId))
                    .username(req.getUsername())
                    .currency(saCurrencyEnum.getCode())
                    .build();
        }

        if (!coinRecordResultVO.getResult()) {
            log.error("SA调用取消派彩失败2:orderNo:{},amount:{},coinRecordResultVO:{}", req.getTxnid(), req.getAmount(),coinRecordResultVO);
            return SAGetUserBalanceRes.builder()
                    .error(SAResultCodeEnums.SYSTEM_ERROR.getCode())
                    .amount(getBalance(userId))
                    .username(req.getUsername())
                    .currency(saCurrencyEnum.getCode())
                    .build();
        }


        return SAGetUserBalanceRes
                .builder()
                .username(req.getUsername())
                .error(SAResultCodeEnums.SUCCESS.getCode())
                .currency(saCurrencyEnum.getCode())
                .amount(coinRecordResultVO.getCoinAfterBalance())
                .build();

    }

    public SAGetUserBalanceRes playerLost(String data) {
        String reqData = getData(data);
        SAPlayerLostReq req = JSON.parseObject(reqData, SAPlayerLostReq.class);
        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername(), VenueEnum.SA.getVenueCode());
        return SAGetUserBalanceRes.builder()
                .error(SAResultCodeEnums.SUCCESS.getCode())
                .amount(getBalance(userId))
                .username(req.getUsername())
                .currency(req.getCurrency())
                .build();
    }

    private BigDecimal getBalance(String userId){
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (Objects.isNull(userCenterCoin)) {
            log.info("{}:用户钱包,不存在:{}", VenueEnum.SA.getVenueName(), userId);
            throw new SADefaultException(SAResultCodeEnums.INSUFFICIENT_FUNDS.getCode());
        }
        return userCenterCoin.getTotalAmount();
    }

    public SAGetUserBalanceRes placeBet(String data) {
        String reqData = getData(data);
        SAPlaceBetReq req = JSON.parseObject(reqData, SAPlaceBetReq.class);
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.info("SA下注参数异常:下注金额缺失:{}", req);
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }

        if (StringUtils.isBlank(req.getTxnid())) {
            log.info("SA下注参数异常:下注订单缺失:{}", req);
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }

        String userId = venueUserAccountConfig.getVenueUserAccount(req.getUsername(), VenueEnum.SA.getVenueCode());

        UserInfoVO userInfoVO = getByUserId(userId);

        if (userInfoVO == null) {
            log.info("SA用户未找到:{}", userId);
            throw new SADefaultException(SAResultCodeEnums.ACCOUNT_NOT_FOUND.getCode());
        }

        SACurrencyEnum saCurrencyEnum = SACurrencyEnum.byCodeCurrency(req.getCurrency());
        if (saCurrencyEnum == null) {
            log.info("SA场馆币种异常1:{}", VenueEnum.SA.getVenueCode());
            throw new SADefaultException(SAResultCodeEnums.INVALID_CURRENCY.getCode());
        }

        if (venueMaintainClosed(VenuePlatformConstants.SA,userInfoVO.getSiteCode())) {
            log.info("场馆未开启:{}", VenueEnum.SA.getVenueCode());
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.SYSTEM_ERROR.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(getBalance(userId))
                    .build();
        }

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenueEnum.SA.getVenueName(), userId);
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.ACCOUNT_LOCKED.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(getBalance(userId))
                    .build();
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userId);
        if (Objects.isNull(userCenterCoin)) {
            log.info("{}:用户钱包,不存在:{}", VenueEnum.SA.getVenueName(), userId);
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.INSUFFICIENT_FUNDS.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(BigDecimal.ZERO)
                    .build();

        }

        List<SAPlaceBetDetailsReq> betDetails = req.getBetdetails();
        if(CollectionUtil.isEmpty(betDetails)){
            log.info("{}:参数异常,用户钱包:{}", VenueEnum.SA.getVenueName(), userId);
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.GENERAL_ERROR.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(getBalance(userId))
                    .build();
        }


        BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();
        if (userTotalAmount.subtract(req.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            log.info("{}:用户钱包,余额不足:{}", VenueEnum.SA.getVenueName(), userId);
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.INSUFFICIENT_FUNDS.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(getBalance(userId))
                    .build();
        }
        checkGameUser(userInfoVO);

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getTxnid());
        userCoinAddVO.setThirdOrderNo(req.getTxnid());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.SA);
        userCoinAddVO.setUserName(userInfoVO.getUserName());
        userCoinAddVO.setCurrency(userCenterCoin.getCurrency());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        CoinRecordResultVO coinRecordResultVO = userCoinApi.addCoin(userCoinAddVO);
        CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);

        if (ObjectUtil.isEmpty(coinRecordResultVO)) {
            log.error("SA调用扣费失败1:orderNo:{},amount:{}", req.getTxnid(), req.getAmount());
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.SYSTEM_ERROR.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(coinRecordResultVO.getCoinAfterBalance())
                    .build();
        }

        if (!coinRecordResultVO.getResult()) {
            log.error("SA调用扣费失败2:orderNo:{},amount:{}", req.getTxnid(), req.getAmount());
            return SAGetUserBalanceRes
                    .builder()
                    .username(req.getUsername())
                    .error(SAResultCodeEnums.SYSTEM_ERROR.getCode())
                    .currency(saCurrencyEnum.getCode())
                    .amount(coinRecordResultVO.getCoinAfterBalance())
                    .build();

        }

        return SAGetUserBalanceRes
                .builder()
                .username(req.getUsername())
                .error(SAResultCodeEnums.SUCCESS.getCode())
                .currency(saCurrencyEnum.getCode())
                .amount(coinRecordResultVO.getCoinAfterBalance())
                .build();

    }

    public SAGetUserBalanceRes queryBalance(String data) {
        String reqData = getData(data);
        SAGetUserBalanceReq req = JSON.parseObject(reqData, SAGetUserBalanceReq.class);

        String userAccount = venueUserAccountConfig.getVenueUserAccount(req.getUsername(), VenueEnum.SA.getVenueCode());

        UserInfoVO userInfoVO = getByUserId(userAccount);

        if (userInfoVO == null) {
            log.info("SA用户未找到:{}", userAccount);
            throw new SADefaultException(SAResultCodeEnums.ACCOUNT_NOT_FOUND.getCode());
        }

        SACurrencyEnum saCurrencyEnum = SACurrencyEnum.byCodeCurrency(req.getCurrency());

        if (saCurrencyEnum == null) {
            log.info("SA场馆币种异常1:{}", VenueEnum.SA.getVenueCode());
            throw new SADefaultException(SAResultCodeEnums.INVALID_CURRENCY.getCode());
        }

        if (!saCurrencyEnum.getCode().equals(req.getCurrency())) {
            log.info("SA场馆币种异常2:{}", VenueEnum.SA.getVenueCode());
            throw new SADefaultException(SAResultCodeEnums.INVALID_CURRENCY.getCode());
        }

//        VenueInfoVO siteVenueInfoByVenueCode = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.SA.getVenueCode(), null);
        if (venueMaintainClosed(VenuePlatformConstants.SA,userInfoVO.getSiteCode())) {
            log.info("场馆未开启:{}", VenueEnum.SA.getVenueCode());
            throw new SADefaultException(SAResultCodeEnums.SYSTEM_ERROR.getCode());
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        return SAGetUserBalanceRes.builder()
                .error(SAResultCodeEnums.SUCCESS.getCode())
                .amount(userCenterCoin.getTotalAmount())
                .username(req.getUsername())
                .currency(saCurrencyEnum.getCode())
                .build();
    }

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        String method = SAConstantApi.LOGIN;
        String secretKey = venueDetailVO.getAesKey();
        String md5key = venueDetailVO.getMerchantKey();
        String encryptKey = venueDetailVO.getMerchantNo();
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String currencyCode = loginVO.getCurrencyCode();
        String username = casinoMemberVO.getVenueUserAccount();
        SACurrencyEnum saCurrencyEnum = SACurrencyEnum.byPlatCurrencyCode(currencyCode);
        if (saCurrencyEnum == null) {
            log.info("该场馆不支持这个币种:{}", loginVO.getCurrencyCode());
            return ResponseVO.fail(ResultCode.VENUE_CURRENCY_NOT);
        }


        //后期有需要的时候在关了这个过滤
//        if (saCurrencyEnum.getCurrencyMultiple() == 1000) {
//            log.info("该场馆不支持1000倍的币:{}", loginVO.getCurrencyCode());
//            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
//        }

        String url = venueDetailVO.getApiUrl();
        String qs = String.format("method=%s&Key=%s&Time=%s&Username=%s&CurrencyType=%s", method, secretKey, time, username, saCurrencyEnum.getCode());

        try {
            String encryptedQS = DESCBCUtil.encrypt(qs, encryptKey);
            String input = qs + md5key + time + secretKey;
            String md5 = MD5Util.MD5Encode(input);
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("q", encryptedQS);
            paramMap.put("s", md5);
            String response = HttpClientHandler.post(url, paramMap);
            log.info("SA-登陆,response={}", response);
            if (StringUtils.isBlank(response)) {
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }

            JSONObject json = XML.toJSONObject(response);
            if (json == null) {
                log.info("SA-登陆失败,XML转失败 response={}", response);
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }

            JSONObject infoRes = json.getJSONObject("LoginRequestResponse");
            if (infoRes == null) {
                log.info("SA-登陆失败,infoRes response={}", infoRes);
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }

            Integer code = infoRes.getInt("ErrorMsgId");
            if (code == null) {
                log.info("SA-登陆失败,code response={}", code);
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }

            if (!code.equals(SAResultCodeEnums.SUCCESS.getCode())) {
                log.info("SA-登陆失败,code response={}", code);
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }

            String account = infoRes.getStr("DisplayName");//三方账号

            String token = infoRes.getStr("Token");//token

            String gameUrl = venueDetailVO.getGameUrl();//登陆地址

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(gameUrl)
                    .append("?").append("username=").append(account)
                    .append("&").append("token=").append(token)
                    .append("&").append("lobby=").append("SA")
                    .append("&").append("lang=").append(SALangEnum.byPlatCurrencyCode(CurrReqUtils.getLanguage()).getCode())
                    .append("&").append("options=").append("defaulttable=").append(loginVO.getGameCode());

            //手机格式
            Integer deviceType = CurrReqUtils.getReqDeviceType();
            if (deviceType != null && deviceType > 1) {
                stringBuilder.append("&").append("mobile=").append(true);
            }


            GameLoginVo gameLoginVo = GameLoginVo.builder()
                    .source(stringBuilder.toString())
                    .type(GameLoginTypeEnums.URL.getType())
                    .userAccount(loginVO.getUserAccount())
                    .venueCode(VenueEnum.SA.getVenueCode())
                    .build();
            return ResponseVO.success(gameLoginVo);
        } catch (Exception e) {
            log.error("登陆失败:", e);
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }
    }

    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        Integer gameTypeId = VenueTypeEnum.SH.getCode();
        List<SiteVO> siteVOList = siteApi.siteInfoAllstauts().getData();
        Map<String, SiteVO> siteVOMap = siteVOList.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO -> SiteVO));

        String method = SAConstantApi.GET_ORDER_RECORD;
        String secretKey = venueDetailVO.getAesKey();
        String md5key = venueDetailVO.getMerchantKey();
        String encryptKey = venueDetailVO.getMerchantNo();
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String url = venueDetailVO.getBetUrl();


        String startTime = TimeZoneUtils.convertTimestampToString(venuePullParamVO.getStartTime(), "GMT+8", TimeZoneUtils.patten_yyyyMMddHHmmss);
        String endTime = TimeZoneUtils.convertTimestampToString(venuePullParamVO.getEndTime(), "GMT+8", TimeZoneUtils.patten_yyyyMMddHHmmss);

        String qs = String.format("method=%s&Key=%s&Time=%s&FromTime=%s&ToTime=%s", method, secretKey, time, startTime, endTime);

        log.info("SA拉单API:{}", qs);
        try {
            String encryptedQS = DESCBCUtil.encrypt(qs, encryptKey);

            String input = qs + md5key + time + secretKey;

            String md5 = MD5Util.MD5Encode(input);

            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("q", encryptedQS);
            paramMap.put("s", md5);
            String result = HttpClientHandler.post(url, paramMap);
            JSONObject jsonObj = XML.toJSONObject(result);
            JSONObject getAllJson = jsonObj.getJSONObject("GetAllBetDetailsForTimeIntervalResponse");
            if (getAllJson == null) {
                log.info("SA 拉取注单 返回:空,1");
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            Integer errorMsgId = getAllJson.getInt("ErrorMsgId");

            if (errorMsgId == null || !errorMsgId.equals(0)) {
                log.info("SA 拉取注单 返回:空,2");
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            if (!getAllJson.containsKey("BetDetailList") || getAllJson.get("BetDetailList").equals("")) {
                log.info("SA 拉取注单 返回:空,3");
                return ResponseVO.success();
            }
            JSONObject betDetailList = getAllJson.getJSONObject("BetDetailList");
            if (betDetailList == null) {
                log.info("SA 拉取注单 返回:空,4");
                return ResponseVO.success();
            }

            if (!betDetailList.containsKey("BetDetail")) {
                log.info("SA 拉取注单 返回:空,5");
                return ResponseVO.success();
            }

            Object betDetailObj = betDetailList.get("BetDetail");

            List<SABetRecordRes> betData = Lists.newArrayList();
            if (betDetailObj instanceof JSONArray) {
                JSONArray betDetailJSON = betDetailList.getJSONArray("BetDetail");
                if (betDetailJSON == null) {
                    log.info("SA 拉取注单 返回:空,5");
                    return ResponseVO.success();
                }
                betData = JSON.parseArray(betDetailJSON.toString(), SABetRecordRes.class);
            } else if (betDetailObj instanceof JSONObject) {
                JSONObject betDetailJSON = betDetailList.getJSONObject("BetDetail");
                SABetRecordRes saBetRecordRes = JSON.parseObject(betDetailJSON.toString(), SABetRecordRes.class);
                betData.add(saBetRecordRes);
            }


            List<String> thirdUserName = betData.stream().map(SABetRecordRes::getUsername).distinct().toList();

            List<CasinoMemberPO> casinoMemberPOS = casinoMemberService.list(Wrappers.<CasinoMemberPO>lambdaQuery()
                    .eq(CasinoMemberPO::getVenueCode, venueDetailVO.getVenueCode())
                    .in(CasinoMemberPO::getVenueUserAccount, thirdUserName));


            if (CollectionUtils.isEmpty(casinoMemberPOS)) {
                log.info("SA 拉取注单 {} 拉单异常,casinoMember 没有记录:{}", venueDetailVO.getVenueCode(), thirdUserName);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }

            Map<String, CasinoMemberPO> casinoMemberMap = casinoMemberPOS.stream()
                    .collect(Collectors.toMap(CasinoMemberPO::getVenueUserAccount, e -> e));


            // 用户信息
            List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).distinct().toList();

            if (CollectionUtils.isEmpty(userIds)) {
                log.info("SA 拉取注单 {} 拉单异常,accountList", venueDetailVO.getVenueCode());
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }


            userIds = userIds.stream().distinct().toList();

            Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
            // 用户登录信息
            Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);


            Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenueEnum.SA.getVenueCode());
            List<OrderRecordVO> list = new ArrayList<>();
            for (SABetRecordRes item : betData) {
                CasinoMemberPO casinoMemberVO = casinoMemberMap.get(item.getUsername());
                if (casinoMemberVO == null) {
                    log.info("{} 三方关联账号 {} 不存在", venueDetailVO.getVenueCode(), item.getUsername());
                    continue;
                }
                UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
                if (userInfoVO == null) {
                    log.info("{} 用户账号{}不存在", venueDetailVO.getVenueCode(), casinoMemberVO.getUserAccount());
                    continue;
                }

                //读取站点名称
                String siteName = null;
                SiteVO siteVO = null;
                if (ObjectUtil.isNotEmpty(userInfoVO.getSiteCode())) {
                    siteVO = siteVOMap.get(userInfoVO.getSiteCode());
                    siteName = siteVO.getSiteName();
                }


                UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());

                // 映射原始注单
                OrderRecordVO recordVO = parseRecords(venueDetailVO, item, userInfoVO, userLoginInfoVO, siteVO, paramToGameInfo);
                recordVO.setVenueType(gameTypeId);
                recordVO.setSiteName(siteName);
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                recordVO.setUserName(userInfoVO.getUserName());
                list.add(recordVO);
            }
            if (CollectionUtil.isNotEmpty(list)) {
                orderRecordProcessService.orderProcess(list);
            }

        } catch (Exception e) {
            log.error("SA 拉取注单失败,异常", e);
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

        return ResponseVO.success();
    }


    private OrderRecordVO parseRecords(VenueInfoVO venueDetailVO, SABetRecordRes orderResponseVO, UserInfoVO userInfoVO,
                                       UserLoginInfoVO userLoginInfoVO, SiteVO siteVO, Map<String, GameInfoPO> paramToGameInfo) {
        OrderRecordVO recordVO = new OrderRecordVO();
        recordVO.setUserAccount(userInfoVO.getUserAccount());

        recordVO.setUserId(userInfoVO.getUserId());
        recordVO.setUserName(userInfoVO.getUserName());
        recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        recordVO.setAgentId(userInfoVO.getSuperAgentId());
        recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
        recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
        recordVO.setBetAmount(orderResponseVO.getBetAmount());
        recordVO.setBetIp(userLoginInfoVO.getIp());
        recordVO.setBetContent(orderResponseVO.getBetType().toString());
        recordVO.setPlayType(orderResponseVO.getGameType());
        if (siteVO != null) {
            recordVO.setBetTime(TimeZoneUtils.convertTimeZone(orderResponseVO.getBetTime(), "GMT+8", siteVO.getTimezone()));
            Long settleTime = TimeZoneUtils.convertTimeZone(orderResponseVO.getPayoutTime(), "GMT+8", siteVO.getTimezone());
            recordVO.setSettleTime(settleTime);
        }
        recordVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
        recordVO.setVenueCode(venueDetailVO.getVenueCode());
        recordVO.setCasinoUserName(orderResponseVO.getUsername());
        if (userLoginInfoVO.getLoginTerminal() != null) {
            recordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
        }

        recordVO.setCurrency(userInfoVO.getMainCurrency());
        recordVO.setRoomType(String.valueOf(orderResponseVO.getHostID()));
        recordVO.setGameNo(String.valueOf(orderResponseVO.getRound()));
        recordVO.setThirdGameCode(orderResponseVO.getGameType());
        recordVO.setOrderId(OrderUtil.getGameNo());
        recordVO.setThirdOrderId(String.valueOf(orderResponseVO.getBetID()));
        recordVO.setValidAmount(orderResponseVO.getRolling());
        recordVO.setWinLossAmount(orderResponseVO.getResultAmount());
        recordVO.setPayoutAmount(orderResponseVO.getResultAmount());
        recordVO.setCreatedTime(System.currentTimeMillis());
        recordVO.setUpdatedTime(System.currentTimeMillis());

        recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());//vip当前等级code
        recordVO.setVipRank(userInfoVO.getVipRank());//vip段位
//        recordVO.setChangeStatus(getChangeStatus(orderResponseVO.getOrderStatus(), recordVO.getOrderId()));
        recordVO.setDeskNo(String.valueOf(orderResponseVO.getHostID()));
        recordVO.setBootNo(String.valueOf(orderResponseVO.getSet()));
        recordVO.setTransactionId(String.valueOf(orderResponseVO.getTransactionID()));

//        recordVO.setResultList(getResultList(orderResponseVO));
        recordVO.setReSettleTime(0L);
        recordVO.setParlayInfo(JSON.toJSONString(orderResponseVO));

        String info = null;

        JSONObject gameResult = orderResponseVO.getGameResult();
        //针对 至尊轮盘 的玩法解析特殊处理
        if (SAGameTypeEnum.ULTRAROULETTE.getCode().equals(orderResponseVO.getGameType())) {
            if (gameResult != null) {
                JSONObject subObj = gameResult.getJSONObject("UltraRouletteResult");
                info = subObj != null ? subObj.toString() : null;
            }
        }else if (SAGameTypeEnum.BAC.getCode().equals(orderResponseVO.getGameType())) {
            info = gameResult != null ? gameResult.toString() : null;
        }else if (SAGameTypeEnum.BLACKJACK.getCode().equals(orderResponseVO.getGameType())) {
            info = gameResult != null ? gameResult.toString() : null;
        }else if(SAGameTypeEnum.POKDENG.getCode().equals(orderResponseVO.getGameType())){
            info = gameResult != null ? gameResult.toString() : null;
        }else if(SAGameTypeEnum.TEENPATTI2020.getCode().equals(orderResponseVO.getGameType())){
            info = gameResult != null ? gameResult.toString() : null;
        }else if (SAGameTypeEnum.DTX.getCode().equals(orderResponseVO.getGameType())) {
            if (gameResult != null) {
                JSONObject dragonJson = gameResult.getJSONObject("DragonTigerResult");
                info = dragonJson != null ? dragonJson.toString() : null;
            }
        } else {
            for (String key : gameResult.keySet()) {
                JSONObject subObj = gameResult.getJSONObject(key);
                if (subObj.containsKey("ResultDetail")) {
                    JSONObject resultDetail = subObj.getJSONObject("ResultDetail");
                    info = resultDetail != null ? resultDetail.toString() : null;
                }
            }
        }

        recordVO.setOrderInfo(info);

        setOrderStatus(recordVO);

        recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));

        recordVO.setPlayType(orderResponseVO.getGameType());
//        String gameCode = String.valueOf(orderResponseVO.getHostID());
//        GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
//        if (gameInfoPO != null) {
//            recordVO.setGameName(orderResponseVO.getGameType());
//        }
        recordVO.setGameName(orderResponseVO.getGameType());
        return recordVO;
    }


    //SA场馆拉单没有状态字段,所以每次都是根据ID去查询是否已经存在这笔单.如果存在库中并且结果牌不一样则代表是重新结算
    public void setOrderStatus(OrderRecordVO recordVO) {
        Integer status = OrderStatusEnum.SETTLED.getCode();

        OrderRecordVO dbOrderRecordVO = orderRecordService.getByThirdOrderId(recordVO.getThirdOrderId());
        if(dbOrderRecordVO != null){
            String orderInfo = dbOrderRecordVO.getOrderInfo();

            String orderInfoNew = recordVO.getOrderInfo();

            //SA场馆比较特殊, 结果牌、有效投注 输赢金额、结算时间 跟库里的不一样 .则代表这个单是重结算单,要进异常订单
            if(!orderInfo.equals(orderInfoNew) || !dbOrderRecordVO.getValidAmount().equals(recordVO.getValidAmount())
                    || !dbOrderRecordVO.getWinLossAmount().equals(recordVO.getWinLossAmount())
                    || !dbOrderRecordVO.getSettleTime().equals(recordVO.getSettleTime())){
                status = OrderStatusEnum.RESETTLED.getCode();
                recordVO.setFirstSettleTime(dbOrderRecordVO.getSettleTime());

                //结算时间是一样的,则将当前时间改为重结算的时间
                if(dbOrderRecordVO.getSettleTime().equals(recordVO.getSettleTime())){
                    recordVO.setSettleTime(System.currentTimeMillis());
                }
            }
        }
        recordVO.setOrderStatus(status);
    }


    public List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoVO venueInfoPO = getVenueInfo(VenuePlatformConstants.SA,null);
        if (ObjectUtil.isEmpty(venueInfoPO)) {
            log.info("场馆不存在:{}", VenueEnum.SA.getVenueCode());
            return resultList;
        }
        try {


            String method = SAConstantApi.GAME_INFO_LIST;
            String secretKey = venueInfoPO.getAesKey();
            String md5key = venueInfoPO.getMerchantKey();
            String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String encryptKey = venueInfoPO.getMerchantNo();
            String url = venueInfoPO.getApiUrl();
            String qs = String.format("method=%s&Key=%s&Time=%s", method, secretKey, time);
            String encryptedQS = DESCBCUtil.encrypt(qs, encryptKey);

            String input = qs + md5key + time + secretKey;

            String md5 = MD5Util.MD5Encode(input);

            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("q", encryptedQS);
            paramMap.put("s", md5);

            String response = HttpClientHandler.post(url, paramMap);

            if (StringUtils.isBlank(response)) {
                log.info("调用三方失败:{}", VenueEnum.SA.getVenueCode());
                return resultList;
            }

            JSONObject json = XML.toJSONObject(response);
            if (json == null) {
                log.info("调用三方失败:{}", VenueEnum.SA.getVenueCode());
                return resultList;
            }

            JSONObject infoRes = json.getJSONObject("GetActiveHostListResponse");
            if (infoRes == null) {
                log.info("调用三方失败:{}", VenueEnum.SA.getVenueCode());
                return resultList;
            }

            Integer code = infoRes.getInt("ErrorMsgId");
            if (code == null) {
                log.info("调用三方失败:{}", VenueEnum.SA.getVenueCode());
                return resultList;
            }

            if (!code.equals(SAResultCodeEnums.SUCCESS.getCode())) {
                log.info("调用三方失败:{}", VenueEnum.SA.getVenueCode());
                return resultList;
            }

            JSONObject hostListJson = infoRes.getJSONObject("HostList");

            if (hostListJson == null) {
                return resultList;
            }

            JSONArray jsonArray = hostListJson.getJSONArray("Host");

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

//                Boolean enabled = jsonObject.getBool("Enabled");
//                if (!enabled) {
//                    continue;
//                }
//                Integer gameStatus = jsonObject.getInt("GameStatus");
//                if (gameStatus != 0) {
//                    continue;
//                }

                Integer key = jsonObject.getInt("HostID");
                String name = jsonObject.getStr("HostName");
                resultList.add(ShDeskInfoVO.builder().deskName(name).deskNumber(key.toString()).build());
            }

        } catch (Exception e) {
            log.info("调用三方异常:{}", VenueEnum.SA.getVenueCode(), e);
        }
        return resultList;
    }



    public static String toJson(String input) {
        JSONObject jsonObject = new JSONObject();
        String[] pairs = input.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx < 0) continue;

            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);

            // 判断value是否是JSON对象或数组，尝试转成对应类型
            if ((value.startsWith("{") && value.endsWith("}")) || (value.startsWith("[") && value.endsWith("]"))) {
                try {
                    if (value.startsWith("{")) {
                        JSONObject nested = new JSONObject(value);
                        jsonObject.put(key, nested);
                    } else {
                        JSONArray nestedArr = new JSONArray(value);
                        jsonObject.put(key, nestedArr);
                    }
                    continue;
                } catch (Exception e) {
                    log.error("解析失败按字符串处理", e);
                }
            }

            jsonObject.put(key, value);
        }
        return jsonObject.toString();
    }


}
