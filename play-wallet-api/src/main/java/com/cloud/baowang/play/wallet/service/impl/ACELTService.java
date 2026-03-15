//package com.cloud.baowang.play.wallet.service.impl;
//
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.cloud.baowang.common.core.constants.RedisConstants;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.play.api.enums.SBDefaultException;
//import com.cloud.baowang.play.api.enums.SBResultCode;
//import com.cloud.baowang.play.api.enums.acelt.ACELTCurrencyEnum;
//import com.cloud.baowang.user.api.enums.UserStatusEnum;
//import com.cloud.baowang.play.api.enums.acelt.ACELTResultCodeEnums;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.utils.MD5Util;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
//import com.cloud.baowang.common.redis.annotation.DistributedLock;
//import com.cloud.baowang.play.api.api.order.PlayServiceApi;
//import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.vo.third.CheckActivityVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.enums.ACELTDefaultException;
//import com.cloud.baowang.play.wallet.enums.ACELTInOutTypeEnum;
//import com.cloud.baowang.play.wallet.enums.ACELTTransferTypeEnum;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.acelt.*;
//import com.cloud.baowang.play.wallet.vo.res.acelt.ACELTAccountCheangeRes;
//import com.cloud.baowang.play.wallet.vo.res.acelt.ACELTBaseRes;
//import com.cloud.baowang.play.wallet.vo.res.acelt.ACELTGetBalanceRes;
//import com.cloud.baowang.user.api.api.UserInfoApi;
//import com.cloud.baowang.wallet.api.api.UserCoinApi;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.*;
//
//@Slf4j
//@AllArgsConstructor
//@Service
//public class ACELTService extends BaseService {
//
//    private final PlayVenueInfoApi playVenueInfoApi;
//
//    private final UserCoinApi userCoinApi;
//
//    private final VenueUserAccountApi venueUserAccountApi;
//
//    private final UserInfoApi userInfoApi;
//
//    private final UserCoinRecordApi userCoinRecordApi;
//
//    private final PlayServiceApi playServiceApi;
//
//    public void checkGameUser(UserInfoVO userInfoVO, VenueEnum venueEnum) {
//        CheckActivityVO checkActivityVO = new CheckActivityVO();
//        checkActivityVO.setUserId(userInfoVO.getUserId());
//        checkActivityVO.setSiteCode(userInfoVO.getSiteCode());
//        checkActivityVO.setVenueCode(venueEnum.getVenueCode());
//        BigDecimal venueAmount = playServiceApi.checkJoinActivity(checkActivityVO);
//
//        //不允许下注
//        if (venueAmount == null || venueAmount.compareTo(BigDecimal.ZERO) > 0) {
//            log.info("不允许下注.调用游戏校验失败:{},result:{}", checkActivityVO, venueAmount);
//            throw new SBDefaultException(SBResultCode.SYSTEM_ERROR);
//        }
//    }
//
//    /**
//     * 去除用户前缀,得到用户真实账号
//     */
//    public String getVenueUserAccount(String userAccount) {
//        return venueUserAccountApi.getVenueUserAccount(userAccount);
//    }
//
//    public ACELTBaseRes<ACELTGetBalanceRes> queryBalance(ACELTGetBalanceReq aceltGetBalanceReq, String venuePlatform) {
//        //参数失败
//        if (!aceltGetBalanceReq.valid()) {
//            log.info("{}:参数校验失败", venuePlatform);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
//        }
//
//        VenueEnum venueEnum = getVenueByUser(aceltGetBalanceReq.getOperatorId(), venuePlatform);
//
//        if (validateMD5Sign(aceltGetBalanceReq.getSign(), aceltGetBalanceReq.getOperatorAccount(), venueEnum)) {
//            log.info("{}:MD5参数校验失败", venueEnum.getVenueName());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.MD5_VERIFICATION_FAILED);
//        }
//
//        String userId = getVenueUserAccount(aceltGetBalanceReq.getOperatorAccount());
//        if (StringUtils.isBlank(userId)) {
//            log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
//        }
//
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if (ObjectUtil.isEmpty(userInfoVO)) {
//            log.info("{}:用户账号查询失败,不存在:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
//        }
//
//        if (venueMaintainClosed(venueEnum.getVenueCode(),userInfoVO.getSiteCode())) {
//            log.info("{}:场馆未开启", venueEnum.getVenueName());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
//        }
//
//        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
//            log.info("{}:用户被打标签,登录锁定不允许下注:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
//        }
//
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                .siteCode(userInfoVO.getSiteCode())
//                .venueCode(venueEnum.getVenueCode())
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//        if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//            log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), venueEnum.getVenueCode());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
//        }
//
//        checkGameUser(userInfoVO, venueEnum);
//
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//        if (Objects.isNull(userCenterCoin)) {
//            log.info("{}:用户钱包,不存在:{}", venueEnum.getVenueName(), userId);
//            return ACELTBaseRes.success(ACELTGetBalanceRes.builder().balance(BigDecimal.ZERO).username(aceltGetBalanceReq.getOperatorAccount()).build());
//        }
//
//        if (userCenterCoin.getTotalAmount() == null) {
//            return ACELTBaseRes.success(ACELTGetBalanceRes.builder().balance(BigDecimal.ZERO).username(aceltGetBalanceReq.getOperatorAccount()).build());
//        }
//
//        ACELTGetBalanceRes res = ACELTGetBalanceRes.builder()
//                .balance(userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN))
//                .username(aceltGetBalanceReq.getOperatorAccount())
//                .build();
//        setBase(res, aceltGetBalanceReq.getOperatorAccount(), venueEnum);
//        return ACELTBaseRes.success(res);
//    }
//
//    @DistributedLock(name = RedisConstants.ACELT_COIN_LOCK, unique = "#req.transferReference", waitTime = 3, leaseTime = 180)
//    public ACELTBaseRes<ACELTAccountCheangeRes> accountChange(ACELTAccountCheangeReq req, String venuePlatform) {
//        if (!req.valid()) {
//            log.info("{}:参数校验失败,req:{}", venuePlatform, req);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
//        }
//
//        VenueEnum venueEnum = getVenueByUser(req.getOperatorId(), venuePlatform);
//
//        if (validateMD5Sign(req.getSign(), req.getOperatorAccount(), venueEnum)) {
//            log.info("{}:MD5参数校验失败", venueEnum.getVenueName());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.MD5_VERIFICATION_FAILED);
//        }
//
//        ACELTInOutTypeEnum inOutTypeEnum = ACELTInOutTypeEnum.fromCode(req.getInOut());
//        if (inOutTypeEnum == null) {
//            log.info("{}:参数校验失败,收支类型异常,:{}", venueEnum.getVenueName(), req.getInOut());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
//        }
//
//        ACELTTransferTypeEnum transferTypeEnum = ACELTTransferTypeEnum.fromCode(req.getTransferType());
//        if (transferTypeEnum == null) {
//            log.info("{}:参数校验失败,交易类型代码异常,:{}", venueEnum.getVenueName(), req.getInOut());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
//        }
//
//        ACELTCurrencyEnum aceltCurrencyEnum = ACELTCurrencyEnum.getCurrencyEnum(req.getCurrencyType());
//        if (aceltCurrencyEnum == null) {
//            log.info("{}:币种映射有误", venueEnum.getVenueName());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_CURRENCY);
//        }
//
//        String userId = getVenueUserAccount(req.getOperatorAccount());
//        if (StringUtils.isBlank(userId)) {
//            log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
//        }
//
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if (ObjectUtil.isEmpty(userInfoVO)) {
//            log.info("{}:用户账号查询失败,不存在:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
//        }
//        if (venueMaintainClosed(venueEnum.getVenueCode(),userInfoVO.getSiteCode())) {
//            log.info("{}:场馆未开启", venueEnum.getVenueName());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
//        }
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                .siteCode(userInfoVO.getSiteCode())
//                .venueCode(venueEnum.getVenueCode())
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//        if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//            log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getSiteCode(), venueEnum.getVenueCode());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
//        }
//
//
//        if (!userInfoVO.getMainCurrency().equals(aceltCurrencyEnum.getPlatformCurrencyCode())) {
//            log.info("{}:币种与用户的法币,不一致:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_CURRENCY);
//        }
//
//        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
//            log.info("{}:用户被打标签,登录锁定不允许下注:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
//        }
//
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//        if (Objects.isNull(userCenterCoin)) {
//            log.info("{}:用户钱包,不存在:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INSUFFICIENT_BALANCE);
//        }
//
//        BigDecimal tradeAmount = req.getTotalAmount();//账变金额
//        BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();//用户钱包金额
//        if (inOutTypeEnum.getCode().equals(ACELTInOutTypeEnum.OUT.getCode())) {
//            if (userTotalAmount.subtract(tradeAmount).compareTo(BigDecimal.ZERO) < 0) {
//                log.info("{}:用户钱包,余额不足:{},用户金额:{},扣款金额:{}", venueEnum.getVenueName(), userId, userTotalAmount, tradeAmount);
//                throw new ACELTDefaultException(ACELTResultCodeEnums.INSUFFICIENT_BALANCE);
//            }
//        }
//
//        String balanceType = inOutTypeEnum.getCode().equals(ACELTInOutTypeEnum.IN.getCode()) ? CoinBalanceTypeEnum.INCOME.getCode() :
//                CoinBalanceTypeEnum.EXPENSES.getCode();
//
//
//        for (ACELTDetailReq item : req.getDetail()) {
//            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//            userCoinAddVO.setOrderNo(item.getBetNum());
//            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//            userCoinAddVO.setBalanceType(balanceType);
//            userCoinAddVO.setUserId(userInfoVO.getUserId());
//            userCoinAddVO.setCoinValue(item.getTradeAmount());
//            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//            toSetUserCoinAddVO(userCoinAddVO, req.getTransferType());
//            CoinRecordResultVO recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//
//            if (ObjectUtil.isEmpty(recordResultVO)) {
//                log.info("{}:调用扣费失败,userCoinAddVO:{}", venueEnum.getVenueName(), userCoinAddVO);
//                throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
//            }
//
//            if(!recordResultVO.getResult()){
//                UpdateBalanceStatusEnums resultStatus = recordResultVO.getResultStatus();
//                //重复订单不做拒绝
//                if(resultStatus.getCode().equals(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS.getCode())){
//                    continue;
//                }
//                throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
//            }
//        }
//        UserCoinWalletVO centerCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//
//        ACELTAccountCheangeRes res = ACELTAccountCheangeRes
//                .builder()
//                .afterAmount(centerCoin.getTotalAmount())
//                .beforeAmount(userTotalAmount)
//                .tradeAmount(tradeAmount)
//                .currencyCode(userInfoVO.getMainCurrency())
//                .inOut(req.getInOut())
//                .transferReference(req.getTransferReference())
//                .username(req.getOperatorAccount())
//                .build();
//        setBase(res, req.getOperatorAccount(), venueEnum);
//
//
//        return ACELTBaseRes.success(res);
//    }
//
//
//    /**
//     * 账变类型转换
//     *
//     * @param userCoinAddVO 对象
//     * @param transferType  类型
//     */
//    private void toSetUserCoinAddVO(UserCoinAddVO userCoinAddVO, Integer transferType) {
//        if (ACELTTransferTypeEnum.PT.getCode().equals(transferType) || ACELTTransferTypeEnum.ZH.getCode().equals(transferType)) {
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//        } else if (ACELTTransferTypeEnum.JJ.getCode().equals(transferType)) {
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//        } else if (ACELTTransferTypeEnum.HJ.getCode().equals(transferType)) {
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RETURN_BET.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//        } else if (ACELTTransferTypeEnum.GR.getCode().equals(transferType) || ACELTTransferTypeEnum.XT.getCode().equals(transferType)) {
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//        }
//    }
//
//
//    /**
//     * 根据商户查询场馆
//     * @param merchantNo 商户
//     * @param venuePlatform 平台
//     * @return 场馆
//     */
//    private VenueEnum getVenueByUser(String merchantNo, String venuePlatform) {
//        ResponseVO<String> respVO = playVenueInfoApi.venueInfoByPlatMerchant(venuePlatform, merchantNo);
//        if (!respVO.isOk() || respVO.getData() == null) {
//            log.info("未查到用户登陆数据:{},{}", merchantNo, venuePlatform);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.DATA_NOT_EXIST);
//        }
//        String venueCode = respVO.getData();
//        return VenueEnum.nameOfCode(venueCode);
//    }
//
//    public ACELTBaseRes<ACELTAccountCheangeRes> accountChangeCallBack(ACELTAccountChangeCallBackReq req, String venuePlatform) {
//        if (!req.valid()) {
//            log.info("{}:参数校验失败,req:{}", venuePlatform, req);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_INPUT);
//        }
//        VenueEnum venueEnum = getVenueByUser(req.getOperatorId(), venuePlatform);
//
//
//        if (validateMD5Sign(req.getSign(), req.getOperatorId(), venueEnum)) {
//            log.info("{}:MD5参数校验失败", venueEnum.getVenueName());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.MD5_VERIFICATION_FAILED);
//        }
//
//        String userId = getVenueUserAccount(req.getOperatorId());
//        if (StringUtils.isBlank(userId)) {
//            log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
//        }
//
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if (ObjectUtil.isEmpty(userInfoVO)) {
//            log.info("{}:用户账号查询失败,不存在:{}", venueEnum.getVenueName(), userId);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
//        }
//        if (venueMaintainClosed(venueEnum.getVenueCode(),userInfoVO.getSiteCode())) {
//            log.info("{}:场馆未开启", venueEnum.getVenueName());
//            throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
//        }
//        UserCoinRecordRequestVO userCoinRecordRequestVO = new UserCoinRecordRequestVO();
//        userCoinRecordRequestVO.setUserId(userInfoVO.getUserId());
//        userCoinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//        userCoinRecordRequestVO.setOrderNo(req.getTransferReference());
//        ResponseVO<List<UserCoinRecordVO>> userCoinResp = userCoinRecordApi.getUserCoinRecords(userCoinRecordRequestVO);
//        if (!userCoinResp.isOk()) {
//            log.info("{}:查询账变记录异常,:{}", venueEnum.getVenueName(), userCoinRecordRequestVO);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
//        }
//        List<UserCoinRecordVO> userCoinRecord = userCoinResp.getData();
//        if (CollectionUtil.isEmpty(userCoinRecord)) {
//            log.info("{}:查询账变记录不存在,:{}", venueEnum.getVenueName(), userCoinRecordRequestVO);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.DATA_NOT_EXIST);
//        }
//        UserCoinRecordVO userCoinRecordVO = userCoinRecord.get(0);
//        String balanceType = userCoinRecordVO.getBalanceType();
//        Integer inOut = null;
//        if (balanceType.equals(CoinBalanceTypeEnum.INCOME.getCode())) {
//            inOut = ACELTInOutTypeEnum.IN.getCode();
//        } else if (balanceType.equals(CoinBalanceTypeEnum.EXPENSES.getCode())) {
//            inOut = ACELTInOutTypeEnum.OUT.getCode();
//        } else {
//            //彩票的订单 只会出现 两种,收入与支出,如果是第三种状态则需要排查
//            log.info("{}:查询账变记录异常,收支状态异常:{}", venueEnum.getVenueName(), userCoinRecordVO);
//            throw new ACELTDefaultException(ACELTResultCodeEnums.SYSTEM_ERROR);
//        }
//        return ACELTBaseRes.success(ACELTAccountCheangeRes.builder()
//                .username(req.getOperatorId())
//                .inOut(inOut)
//                .currencyCode(ACELTCurrencyEnum.getPlatCurrencyEnum(userInfoVO.getMainCurrency()).getCurrencyCode())
//                .tradeAmount(userCoinRecordVO.getCoinValue())
//                .afterAmount(userCoinRecordVO.getCoinTo())
//                .beforeAmount(userCoinRecordVO.getCoinFrom())
//                .username(req.getOperatorId())
//                .transferReference(req.getTransferReference())
//                .build());
//    }
//
//
//    private void setBase(ACELTBaseReq baseReq, String userName, VenueEnum venueEnum) {
//        VenueInfoVO venueConfigInfo = playVenueInfoApi.venueInfoByVenueCode(venueEnum.getVenueCode()
//                , "").getData();
//        Map<String, Object> signMap = new HashMap<>();
//        signMap.put("operatorAccount", userName);
//        signMap.put("secretKey", venueConfigInfo.getMerchantKey());
//        signMap.put("operatorId", venueConfigInfo.getMerchantNo());
//        baseReq.setSign(sign(signMap));
//        baseReq.setTimestamp(System.currentTimeMillis());
//        baseReq.setOperatorId(venueConfigInfo.getMerchantNo());
//    }
//
//    private boolean validateMD5Sign(String reqMD5Sign, String userName, VenueEnum venueEnum) {
//        VenueInfoVO venueConfigInfo = playVenueInfoApi.venueInfoByVenueCode(venueEnum.getVenueCode()
//                , "").getData();
//        Map<String, Object> signMap = new HashMap<>();
//        signMap.put("operatorAccount", userName);
//        signMap.put("secretKey", venueConfigInfo.getMerchantKey());
//        signMap.put("operatorId", venueConfigInfo.getMerchantNo());
//        return !reqMD5Sign.equals(sign(signMap));
//    }
//
//
//    private static String sign(Map<String, Object> paramMap) {
//        SortedMap<String, String> sortedMap = new TreeMap<>();
//        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
//            sortedMap.put(entry.getKey(), entry.getValue() + "");
//        }
//        StringBuilder sb = new StringBuilder();
//        //生成
//        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
//            String k = entry.getKey();
//            if (!"sign".equals(k)) {
//                sb.append(k).append("=").append(entry.getValue()).append("&");
//            }
//        }
//        log.info("sign:{}", sb);
//        return MD5Util.md5(sb.toString());
//    }
//
//
//}
