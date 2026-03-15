//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.cloud.baowang.common.core.constants.CommonConstant;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
//import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
//import com.cloud.baowang.play.api.api.venue.GameInfoApi;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.enums.jdb.JDBErrorEnum;
//import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
//import com.cloud.baowang.play.api.vo.venue.GameInfoValidRequestVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.wallet.vo.jdb.constant.JDBConstant;
//import com.cloud.baowang.play.wallet.vo.jdb.enums.JDBCurrencyEnum;
//import com.cloud.baowang.play.wallet.vo.jdb.utils.AESDecrypt;
//import com.cloud.baowang.play.wallet.vo.jdb.utils.JDBCryptoConfig;
//import com.cloud.baowang.play.wallet.vo.res.jdb.JDBBaseRsp;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.service.base.BaseService;
//import com.cloud.baowang.play.wallet.vo.req.jdb.*;
//import com.cloud.baowang.user.api.api.UserInfoApi;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.JDBUserCoinRecordVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StopWatch;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@AllArgsConstructor
//@Service
//public class JDBGameApiImplExtend extends BaseService {
//
//    private final UserInfoApi userInfoApi;
//
//    private final PlayVenueInfoApi playVenueInfoApi;
//
//
//    private final UserCoinRecordApi userCoinRecordApi;
//
//    private final VenueUserAccountApi venueUserAccountApi;
//
//    private JDBCryptoConfig jdbCryptoConfig;
//
//    private final GameInfoApi gameInfoApi;
//
//    //    @Value("${play.server.jdb:domain:}")
////    private String dc;
////
////    @Value("${play.server.jdb:key:}")
////    private String key;
////
////
////    @Value("${play.server.jdb:iv:}")
////    private String iv;
//    // dail
//    private static final String dc = "WT";
//
//    private static final String KEY = "9397edcba20ef10b";
//    private static final String IV = "9ca2a2050e68ff85";
//
//    private static final String parent = "wtrmbag";
//
//
//    public JDBBaseRsp getBalance(JSONObject reqData) {
////        log.info("JDB getBalance : "+reqData);
//        String userId = userCheck(reqData);
//        if (userId == null) {
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if (userInfoVO == null) {
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,false);
//        if (isValid != null) {
//            return isValid;
//        }
//        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//        if (ObjectUtil.isNull(userCenterCoin)) {
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        if (userCenterCoin.getTotalAmount() == null || BigDecimal.ZERO.compareTo(userCenterCoin.getTotalAmount()) >= 0) {
//            return JDBBaseRsp.success(BigDecimal.ZERO.doubleValue());
//        }
//        BigDecimal totalAmount = userCenterCoin.getTotalAmount().setScale(2, RoundingMode.DOWN);
//        return JDBBaseRsp.success(totalAmount.setScale(2,RoundingMode.DOWN).doubleValue());
//    }
//
//    public JDBBaseRsp checkRequestValid(UserInfoVO userInfoVO, JSONObject jsonData,boolean isBetting) {
//        if (ObjectUtil.isEmpty(userInfoVO)) {
//            log.info("JDBGameApiExtendImpl.getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.JDB.getVenueName());
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//
//
////        if (venueMaintainClosed(VenueEnum.JDB.getVenueCode(),userInfoVO.getSiteCode())) {
////            log.info("场馆未开启:{} ", VenueEnum.JDB.getVenueCode());
////            return JDBBaseRsp.failed(JDBErrorEnum.GAME_MAINTAINED);
////        }
//
//
//
//        String userCurrencyCode = userInfoVO.getMainCurrency();
//        String jdbCode = JDBCurrencyEnum.enumOfCode(userCurrencyCode).getJdbCode();
//        String currency = jsonData.getString("currency");
//        String gameCode = jsonData.getString("gType")+"_"+jsonData.getString("mType");
//
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(userInfoVO.getSiteCode())
//                .venueCode(VenueEnum.JDB.getVenueCode())
//                .currencyCode(userInfoVO.getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//        if (venueInfoVO == null || !venueInfoVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
//            log.info("该站:{} 没有分配:{} 场馆的权限", userInfoVO.getSiteCode(), VenueEnum.JDB.getVenueCode());
//            return JDBBaseRsp.failed(JDBErrorEnum.GAME_MAINTAINED);
//        }
//
//        if (!isGameAvailable(userInfoVO.getSiteCode(),gameCode,VenueEnum.JDB.getVenueCode(),userCurrencyCode)){
//            log.info("该站:{} 没有分配:{} 游戏的权限", userInfoVO.getSiteCode(), gameCode);
//            return JDBBaseRsp.failed(JDBErrorEnum.GAME_MAINTAINED);
//        }
//
//        if (!jdbCode.equals(currency)) {
//            log.info("币种不支持 : {} : 用户 : {}", userCurrencyCode, userInfoVO.getUserId());
//            return JDBBaseRsp.failed(JDBErrorEnum.CURRENCY_UNSUPPORTED);
//        }
//
//        if (isBetting){
//            if (userGameLock(userInfoVO)) {
//                log.error("玩家锁定{} - 场馆 : {}", userInfoVO.getUserName(), VenueEnum.JDB.getVenueCode());
//                return JDBBaseRsp.failed(JDBErrorEnum.USER_LOCKED);
//            }
//
//        }
//        return null;
//    }
//
//    /**
//     * 游戏开启/关闭校验
//     * @param siteCode
//     * @param gameCode
//     * @param venueCode
//     * @return
//     */
//
//    private boolean isGameAvailable(String siteCode, String gameCode, String venueCode,String currencyCode) {
//        // 判断游戏是否配置
//        GameInfoValidRequestVO requestVO = GameInfoValidRequestVO.builder().siteCode(siteCode).gameId(gameCode).venueCode(venueCode).build();
//        ResponseVO<GameInfoVO> responseVO = gameInfoApi.getGameInfoByCode(requestVO);
//        if (!responseVO.isOk()){
//            return false;
//        }
//        GameInfoVO gameInfo = responseVO.getData();
//        if (gameInfo == null) {
//            log.error("场馆:{} 没有配置游戏，游戏：{}", venueCode, gameCode);
//            return false;
//        }
//        // 判断游戏是否开启
//        if (!Objects.equals(gameInfo.getStatus(), StatusEnum.OPEN.getCode())) {
//            log.error("场馆:{} 游戏关闭，游戏：{}", venueCode, gameCode);
//            return false;
//        }
//        //币种
//        List<String> currencyList = Arrays.asList(gameInfo.getCurrencyCode().split(CommonConstant.COMMA));
//        if (!currencyList.contains(currencyCode)) {
//            log.error("场馆:{} 游戏：{} 币种不支持 : {}", venueCode, gameCode, currencyCode);
//            return false;
//        }
//        return true;
//    }
//
//    public String userCheck(JSONObject reqData) {
//        String userAccount = reqData.getString("uid");
//        //        String thirdAccount = userAccount.substring(0, 1).toUpperCase() + userAccount.substring(1);
////        CasinoMemberReqVO casinoMember = new CasinoMemberReqVO();
////        casinoMember.setVenueUserAccount(thirdAccount);
////        casinoMember.setVenueCode(VenueEnum.JDB.getVenueCode());
////        ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMember);
////        if (!respVO.isOk() || respVO.getData() == null) {
////            return null;
////        }
//        return userAccount.replaceAll("^[a-zA-Z]+_", "");
//    }
//
//    public static void main(String[] args) {
//        String userAccount = "prod_72969005";
//        String result = userAccount.replaceAll("^[a-zA-Z]+_", "");
//        System.out.println("JDBGameApiExtendImpl.main - "+result);
//    }
//
//    /**
//     * 下注并结算
//     *
//     * @param reqData
//     * @return
//     */
//    public JDBBaseRsp betNSettle(JSONObject reqData) {
//        JDBBetNSettleReq req = reqData.toJavaObject(JDBBetNSettleReq.class);
//        String userId = userCheck(reqData);
//        if (userId == null) {
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,true);
//        if (isValid != null) {
//            return isValid;
//        }
//
//        //最小下注额
//        BigDecimal minBalance = BigDecimal.valueOf(req.getMb()).abs();
//        //下注额 需取正
//        BigDecimal betBalance = BigDecimal.valueOf(req.getBet()).abs();
//        //游戏赢分 <派彩> 需取正
//        BigDecimal win = BigDecimal.valueOf(req.getWin()).abs();
//
//        BigDecimal netWin = BigDecimal.valueOf(req.getNetWin()).abs();
////        log.info(" betNSettle : " + reqData);
//
//        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//        if (userCoin == null) {
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        BigDecimal totalAmount = userCoin.getTotalAmount();
//        String orderId = req.getHistoryId();
//        //bet = 0,奖励
//        if (betBalance.compareTo(BigDecimal.ZERO)==0){
//            //"bet":0,"win":0,"netWin":0
//            if (win.compareTo(BigDecimal.ZERO)==0 || netWin.compareTo(BigDecimal.ZERO)==0){
//                return JDBBaseRsp.success(totalAmount.setScale(2,RoundingMode.DOWN).doubleValue());
//            }else {
//                CoinRecordResultVO settleResult = this.handleSettle(userInfoVO, orderId, win, req.getTransferId());
//                if (settleResult != null ) {
//                    if (UpdateBalanceStatusEnums.SUCCESS.equals(settleResult.getResultStatus())
//                            || UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO.equals(settleResult.getResultStatus())){
//                        BigDecimal coinAfterBalance = settleResult.getCoinAfterBalance();
//                        return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
//                    }else {
//                        log.error(" JDB道具奖励异常 : 请求数据 : " + reqData + " 结算数据 : " + settleResult);
//                        return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//                    }
//                }else {
//                    log.error(" JDB道具奖励异常 : 请求数据 : " + reqData);
//                    return JDBBaseRsp.failed(JDBErrorEnum.TRY_AGAIN_LATER);
//                }
//            }
//
//        }
//        // 玩家余额 < bet 請返回錯誤代碼 6006.
//        if (totalAmount.subtract(betBalance).compareTo(BigDecimal.ZERO) < 0 || totalAmount.subtract(minBalance).compareTo(BigDecimal.ZERO) < 0) {
//            return JDBBaseRsp.failed(JDBErrorEnum.BALANCE_NOT_ENOUGH);
//        }
//
//        //生成 下注/结算账变
//        //查询账变
//        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
//        coinRecordRequestVO.setOrderNo(orderId);
//        ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//        if (!userCoinRecords.isOk()) {
//            log.info(" betNSettle : 注单查询异常 : " );
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//        if (userCoinRecords.getData() != null && !userCoinRecords.getData().isEmpty()) {
//            //重复投注
//            return JDBBaseRsp.failed(JDBErrorEnum.DUPLICATE_TRANSACTION);
//        }
//
//        CoinRecordResultVO betResult = this.handleBet(userInfoVO, orderId, betBalance, req.getTransferId());
//        if (betResult != null) {
//            UpdateBalanceStatusEnums betStatus = betResult.getResultStatus();
//            if (UpdateBalanceStatusEnums.SUCCESS.equals(betStatus)) {
//                //下注成功,处理结算
//                CoinRecordResultVO settleResult = this.handleSettle(userInfoVO, orderId, win, req.getTransferId());
//                if (settleResult != null ) {
//                    if (UpdateBalanceStatusEnums.SUCCESS.equals(settleResult.getResultStatus())){
//                        //都处理成功
//                        BigDecimal coinAfterBalance = settleResult.getCoinAfterBalance();
//                        return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
//                        //玩家余额 < mb 则此笔请求应回覆 6006
////                        boolean isEnough = coinAfterBalance.subtract(minBalance).compareTo(BigDecimal.ZERO) > 0;
////                        if (isEnough) {
////                            return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
////                        } else {
////                            return JDBBaseRsp.failed(JDBErrorEnum.BALANCE_NOT_ENOUGH, coinAfterBalance.doubleValue());
////                        }
//                    }else if (UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO.equals(settleResult.getResultStatus())) {
//                        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//                        BigDecimal coinAfterBalance = userCenterCoin.getTotalAmount();
//                        return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
////                        boolean isEnough = coinAfterBalance.subtract(minBalance).compareTo(BigDecimal.ZERO) > 0;
////                        if (isEnough) {
////                            return JDBBaseRsp.success(coinAfterBalance.setScale(2,RoundingMode.DOWN).doubleValue());
////                        } else {
////                            return JDBBaseRsp.failed(JDBErrorEnum.BALANCE_NOT_ENOUGH, coinAfterBalance.doubleValue());
////                        }
//                    }else {
//                        return JDBBaseRsp.failed(JDBErrorEnum.TRY_AGAIN_LATER);
//                    }
//                } else {
//                    //取消下注订单
//                    CoinRecordResultVO cancelBetResult = this.handleCancelBet(userInfoVO, orderId, betBalance, req.getTransferId());
//                    if (cancelBetResult == null || !cancelBetResult.getResultStatus().equals(UpdateBalanceStatusEnums.SUCCESS)) {
//                        log.error(" JDB下注并结算 : 异常 订单号 : " + orderId);
//                    }
//                    return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//                }
//            } else {
//                log.error(" JDB下注并结算 : 失败 订单号 : " + orderId);
//                return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//            }
//        } else {
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//    }
//
//    /**
//     * 取消下注并结算 action 4 超时<></>
//     *
//     * @param reqData
//     * @return
//     */
//    public JDBBaseRsp cancelBetNSettle(JSONObject reqData) {
////        log.info("JDB cancelBetNSettle : "+reqData.toString());
//        JDBBetNSettleReq cancelBetNSettleReq = reqData.toJavaObject(JDBBetNSettleReq.class);
//        String userId = userCheck(reqData);
//        if (userId == null) {
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,false);
//        if (isValid != null) {
//            return isValid;
//        }
//        //查询账变
//        String orderId = cancelBetNSettleReq.getHistoryId();
//        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
//        coinRecordRequestVO.setOrderNo(orderId);
//        ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//        if (userCoinRecords.isOk()) {
//            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//            BigDecimal totalAmount = userCoin.getTotalAmount();
//            BigDecimal rspAmount = userCoin.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0 ? totalAmount : BigDecimal.ZERO;
//            List<UserCoinRecordVO> coinRecordVOS = userCoinRecords.getData();
//
//            if (coinRecordVOS.isEmpty()) {
//                //如果不存在账变 通知三方取消订单
//                return JDBBaseRsp.success(rspAmount.setScale(2,RoundingMode.DOWN).doubleValue());
//            } else {
//                //已经处理过 remark = 6106
//                UserCoinRecordVO cancelFlag = coinRecordVOS.stream()
//                        .filter(item -> JDBErrorEnum.BET_IS_VALID.getCode().equals(item.getRemark()))
//                        .findAny()
//                        .orElse(null);
//                if (cancelFlag != null) {
//                    return JDBBaseRsp.success(rspAmount.setScale(2,RoundingMode.DOWN).doubleValue());
//                }
//                //如果已存在账变 -> 取消
//                BigDecimal amount = handleBetAndCancel(userInfoVO, coinRecordVOS);
//                return JDBBaseRsp.success(amount.doubleValue());
//            }
//        } else {
//            return JDBBaseRsp.failed(JDBErrorEnum.BET_TRY_AGAIN_LATER);
//        }
//    }
//
//    private BigDecimal handleBetAndCancel(UserInfoVO userInfoVO, List<UserCoinRecordVO> coinRecordVOS) {
//        BigDecimal amount = BigDecimal.ZERO;
//        for (UserCoinRecordVO coinRecordVO : coinRecordVOS) {
//            if (coinRecordVO.getBalanceType().equals(CoinBalanceTypeEnum.EXPENSES.getCode())){
//                //支出 ->取消下注
//                String orderNo = coinRecordVO.getOrderNo();
//                BigDecimal coinValue = coinRecordVO.getCoinValue();
//                CoinRecordResultVO coinRecordResultVO = handleCancelBet(userInfoVO, orderNo, coinValue, JDBErrorEnum.BET_IS_VALID.getCode());
//                amount = coinRecordResultVO.getCoinAfterBalance();
//
//            } else if (coinRecordVO.getBalanceType().equals(CoinBalanceTypeEnum.INCOME.getCode())) {
//                //收入 -> 取消派彩
//                String orderNo = coinRecordVO.getOrderNo();
//                BigDecimal coinValue = coinRecordVO.getCoinValue();
//                CoinRecordResultVO coinRecordResultVO = handleCancelSettle(userInfoVO, orderNo, coinValue, JDBErrorEnum.BET_IS_VALID.getCode());
//                amount = coinRecordResultVO.getCoinAfterBalance();
//
//            }
//        }
//        return amount;
//    }
//
//    /**
//     * 下注 (游戏类型支援范围：街机 (9)、棋牌 (18))
//     *
//     * @param reqData
//     * @return
//     */
//    public JDBBaseRsp bet(JSONObject reqData) {
////        log.info("JDB bet : "+reqData.toString());
//        JDBBetReq req = reqData.toJavaObject(JDBBetReq.class);
//        if (!req.valid()) {
//            return JDBBaseRsp.failed(JDBErrorEnum.PARAM_ERROR);
//        }
//
//        String userId = userCheck(reqData);
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,true);
//        if (isValid != null) {
//            return isValid;
//        }
//        String orderId = req.getGameRoundSeqNo();
//
//        //订单状态
//        JDBBaseRsp jdbBaseRsp = checkExistCoinRecord(userInfoVO, orderId,true);
//        if (jdbBaseRsp != null) {
//            return jdbBaseRsp;
//        }
//        //检查余额
//        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
//        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//        if (amount.compareTo(BigDecimal.ZERO) == 0) {
//            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//        }
//
//        if (ObjectUtil.isNull(userCoin) || userCoin.getTotalAmount().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
//            log.error("{} 下注失败, 余额不足, 请求参数: {}, 钱包余额userCoin: {} - 下注金额 : {} ", VenueEnum.JDB.getVenueCode(), req, userCoin, amount);
//            return JDBBaseRsp.failed(JDBErrorEnum.BALANCE_NOT_ENOUGH);
//        }
//
//        //账变
//        CoinRecordResultVO coinRecordResultVO = this.handleBet(userInfoVO, orderId, amount, req.getTransferId());
//        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
//            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().setScale(2,RoundingMode.DOWN).doubleValue());
//        } else {
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//    }
//
//
//    /**
//     * 结算
//     *
//     * @param reqData
//     * @return
//     */
//    public JDBBaseRsp settle(JSONObject reqData) {
////        log.info("JDB settle : "+reqData.toString());
//        JDBSettleReq req = reqData.toJavaObject(JDBSettleReq.class);
//        if (!req.valid()) {
//            return JDBBaseRsp.failed(JDBErrorEnum.PARAM_ERROR);
//        }
////        Boolean roundClosed = req.getRoundClosed();
//        //牌局未结束 -》 不处理结算
////        if (!roundClosed) {
////            log.error("JDB未结算订单 : 三方订单 : {}", reqData);
////            return JDBBaseRsp.failed(JDBErrorEnum.TRY_AGAIN_LATER);
////        }
//        //用户校验
//        String userId = userCheck(reqData);
//        if (userId == null) {
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        //合法性校验
//        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,false);
//        if (isValid != null) {
//            return isValid;
//        }
//        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
//        if (amount.compareTo(BigDecimal.ZERO) == 0) {
//            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//        }
//        //结算: 以局号座位订单的结算标记
//        String transferId = req.getTransferId();
//
//        String orderId = req.getHistoryId();
//        JDBBaseRsp jdbBaseRsp = checkExistCoinRecord(userInfoVO, orderId,amount);
//        if (jdbBaseRsp != null) {
//            return jdbBaseRsp;
//        }
//        //修改余额 记录账变 <取三方金额>
//        CoinRecordResultVO coinRecordResultVO = this.handleSettle(userInfoVO, orderId, amount, transferId);
//        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
//            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().setScale(2,RoundingMode.DOWN).doubleValue());
//        } else {
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//    }
//
//    /**
//     * 取消派彩
//     * @param reqData
//     * @return
//     */
//
//    /**
//     * JDB 取消派彩,需要记录订单状态
//     *
//     * @param userInfoVO   userInfo
//     * @param orderNo      orderNo
//     * @param payoutAmount amount
//     * @return result
//     */
//    protected CoinRecordResultVO handleCancelSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
//        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
//        userCoinAddVOPayout.setOrderNo(orderNo);
//        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
//        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
//        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVOPayout.setRemark(remark);
//        userCoinAddVOPayout.setVenueCode(VenueEnum.JDB.getVenueCode());
//        return userCoinApi.addCoin(userCoinAddVOPayout);
//    }
//
//    /**
//     * 取消下注
//     *
//     * @param reqData
//     * @return
//     */
//    public JDBBaseRsp cancelBet(JSONObject reqData) {
////        log.info("JDB cancelBet : "+reqData.toString());
//        JDBSettleReq req = reqData.toJavaObject(JDBSettleReq.class);
//        if (!req.valid()) {
//            return JDBBaseRsp.failed(JDBErrorEnum.PARAM_ERROR);
//        }
//
//        String userId = userCheck(reqData);
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        JDBBaseRsp isValid = checkRequestValid(userInfoVO, reqData,false);
//        if (isValid != null) {
//            return isValid;
//        }
//
//        String orderId = req.getGameRoundSeqNo();
//        JDBBaseRsp jdbBaseRsp = checkExistCoinRecord(userInfoVO, orderId,false);
//        if (jdbBaseRsp != null) {
//            return jdbBaseRsp;
//        }
//        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
//        if (amount.compareTo(BigDecimal.ZERO) == 0) {
//            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//        }
//
//        //取消下注,校验订单号,金额是否对上
//        List<String> refTransferIds = req.getRefTransferIds().stream().map(String::valueOf).collect(Collectors.toList());
//
//        JDBUserCoinRecordVO betOrderCheck = new JDBUserCoinRecordVO();
//        betOrderCheck.setUserAccount(userInfoVO.getUserAccount());
//        betOrderCheck.setUserId(userInfoVO.getUserId());
//        betOrderCheck.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//        betOrderCheck.setReferIds(refTransferIds);
//        ResponseVO<List<UserCoinRecordVO>> betOrders = userCoinRecordApi.getJDBBetRecords(betOrderCheck);
//        if (betOrders.isOk()) {
//            List<UserCoinRecordVO> betOrdersData = betOrders.getData();
//            log.info("betOrdersData 已存在注单 : "+betOrdersData);
//            if (betOrdersData.isEmpty()) {
//                //无注单
//                UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//                return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//            }
//        }
//        JDBUserCoinRecordVO userCoinRecordVo = new JDBUserCoinRecordVO();
//        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
//        userCoinRecordVo.setUserId(userInfoVO.getUserId());
//        userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        String oldReferIds = String.join(",", refTransferIds);
//        userCoinRecordVo.setOrdersNo(oldReferIds);
//        log.info(" 检查已取消参数 : "+userCoinRecordVo);
//        ResponseVO<UserCoinRecordVO> userCoinRecords = userCoinRecordApi.getJDBUserCoinRecords(userCoinRecordVo);
////
//        UserCoinRecordVO coinRecordsData = userCoinRecords.getData();
//        if (coinRecordsData != null) {
//            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//        }
//
//        //修改余额 记录账变 <取三方金额>
//        CoinRecordResultVO coinRecordResultVO = this.handleCancelBet(userInfoVO, orderId, amount, oldReferIds);
//        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
//            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().setScale(2,RoundingMode.DOWN).doubleValue());
//        } else {
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//    }
//
//
//
//    /**
//     * 免费场次派彩
//     *
//     * @param reqData
//     * @return
//     */
//    private JDBBaseRsp freeSpinReward(JSONObject reqData) {
////        log.info("JDB freeSpinReward : "+reqData.toString());
//        JDBFreeSpinRewardReq req = reqData.toJavaObject(JDBFreeSpinRewardReq.class);
//        String userId = userCheck(reqData);
//        if (userId == null) {
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if (ObjectUtil.isEmpty(userInfoVO)) {
//            log.info("JDBGameApiExtendImpl.getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.JDB.getVenueName());
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        String orderId = req.getEventId();
//        String transferId = req.getTransferId();
//
//        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
//        coinRecordRequestVO.setOrderNo(orderId);
//        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        coinRecordRequestVO.setRemark(transferId);
//        ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//        if (userCoinRecords.isOk() && !userCoinRecords.getData().isEmpty()) {
//            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//        }
//
//        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
//        //账变
//        CoinRecordResultVO coinRecordResultVO = handleSettle(userInfoVO, orderId, amount, transferId);
//        log.info("JDB免费场次派彩 : 用户 : {} transferId : {} eventId : {} amount :{} accumulatedTurnover : {}  accumulatedWin : {}",
//                userId, transferId,orderId, req.getAmount(), req.getAccumulatedTurnover(), req.getAccumulatedWin());
//        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
//            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().doubleValue());
//        } else {
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//
//    }
//
//    /**
//     * 活动派彩
//     *
//     * @param reqData
//     * @return
//     */
//    private JDBBaseRsp spinReward(JSONObject reqData) {
////        log.info("JDB spinReward : "+reqData.toString());
//        JDBSpinRewardReq req = reqData.toJavaObject(JDBSpinRewardReq.class);
//        String userId = userCheck(reqData);
//        if (userId == null) {
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if (ObjectUtil.isEmpty(userInfoVO)) {
//            log.info("JDBGameApiExtendImpl.getBalance : userId : {} 用户不存在  场馆 - {}", userInfoVO.getUserId(), VenueEnum.JDB.getVenueName());
//            return JDBBaseRsp.failed(JDBErrorEnum.USER_NOT_FOUND);
//        }
//
//        String orderId = req.getActivityNo();
//        String transferId = req.getTransferId();
//        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
//        coinRecordRequestVO.setOrderNo(orderId);
//        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        coinRecordRequestVO.setRemark(transferId);
//        ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//        if (userCoinRecords.isOk() && !userCoinRecords.getData().isEmpty()) {
//            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
//            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//        }
//        //派彩直接加 <只有重复订单号>
//        BigDecimal amount = BigDecimal.valueOf(req.getAmount()).abs();
//        CoinRecordResultVO coinRecordResultVO = handleSettle(userInfoVO, orderId, amount, transferId);
//        log.info("JDB活动派彩 : 用户 : {} transferId : {} activityNo : {} activityDate : {} activityName : {} amount : {} ",
//                userId, transferId, orderId, req.getActivityDate(), req.getActivityName(), req.getAmount());
//        if (coinRecordResultVO != null && UpdateBalanceStatusEnums.SUCCESS.equals(coinRecordResultVO.getResultStatus())) {
//            return JDBBaseRsp.success(coinRecordResultVO.getCoinAfterBalance().setScale(2,RoundingMode.DOWN).doubleValue());
//        } else {
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//
//    }
//
//    /**
//     * 订单校验
//     * @param userInfoVO
//     * @param orderId
//     * @return
//     */
//    public JDBBaseRsp checkExistCoinRecord(UserInfoVO userInfoVO, String orderId,boolean betting) {
//        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
//        coinRecordRequestVO.setOrderNo(orderId);
//        coinRecordRequestVO.setCoinType(betting ? WalletEnum.CoinTypeEnum.GAME_BET.getCode() : WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//        ResponseVO<List<UserCoinRecordVO>> cancelBetRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//        if (!cancelBetRecord.isOk()) {
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//        if (cancelBetRecord.getData() != null && !cancelBetRecord.getData().isEmpty()) {
//            //订单已处理
//            UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
//            return JDBBaseRsp.success(userCoin.getTotalAmount().doubleValue());
//        }
//        return null;
//    }
//
//    /**
//     * 结算订单校验
//     * @param userInfoVO
//     * @param orderId
//     * @return
//     */
//    public JDBBaseRsp checkExistCoinRecord(UserInfoVO userInfoVO, String orderId,BigDecimal amount) {
//        UserCoinRecordRequestVO coinRecordRequestVO = new UserCoinRecordRequestVO();
//        coinRecordRequestVO.setSiteCode(userInfoVO.getSiteCode());
//        coinRecordRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
//        coinRecordRequestVO.setUserId(userInfoVO.getUserId());
//        coinRecordRequestVO.setOrderNo(orderId);
//        coinRecordRequestVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        ResponseVO<List<UserCoinRecordVO>> settleRecord = userCoinRecordApi.getUserCoinRecords(coinRecordRequestVO);
//        if (!settleRecord.isOk()) {
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//        UserCoinWalletVO userCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
//        if (settleRecord.getData() != null && !settleRecord.getData().isEmpty()) {
//            //订单已处理
//            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//        }
//        if (amount.compareTo(BigDecimal.ZERO) == 0) {
//            return JDBBaseRsp.success(userCoin.getTotalAmount().setScale(2,RoundingMode.DOWN).doubleValue());
//        }
//        return null;
//    }
//
//
//
//    /**
//     * 解密
//     *
//     * @param x
//     * @return
//     */
//    private JSONObject decrypt(String x,String key,String iv) {
//        JSONObject reqData;
//        try {
//            String decrypt = AESDecrypt.decrypt(x, key, iv);
//            reqData = JSON.parseObject(decrypt);
//            return reqData;
//        } catch (Exception e) {
//            log.error(" jdb数据解密失败 - " + e.getMessage());
//            return null;
//        }
//    }
//
//    /**
//     * 三方行动
//     *
//     * @param x
//     * @return
//     */
//    public JDBBaseRsp doAction(String x) {
//        String key = jdbCryptoConfig.getKey();
//        String iv = jdbCryptoConfig.getIV();
//        JSONObject reqData = decrypt(x,key,iv);
//        if (reqData == null) {
//            return JDBBaseRsp.failed(JDBErrorEnum.FAILED);
//        }
//        log.info("JDB doAction : "+reqData);
//        Integer actionType = reqData.getInteger("action");
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        JDBBaseRsp response = switch (actionType) {
//            case JDBConstant.BALANCE -> getBalance(reqData);
//            case JDBConstant.BET_N_SETTLE -> betNSettle(reqData);
//            case JDBConstant.CANCEL_BET_N_SETTLE -> cancelBetNSettle(reqData);
//            case JDBConstant.BET -> bet(reqData);
//            case JDBConstant.SETTLE -> settle(reqData);
//            case JDBConstant.CANCEL_BET -> cancelBet(reqData);
//            case JDBConstant.SPIN_REWARD -> spinReward(reqData);
//            case JDBConstant.FREE_SPIN_REWARD -> freeSpinReward(reqData);
//            default -> JDBBaseRsp.builder().status(JDBErrorEnum.FAILED.getCode()).build();
//        };
//        stopWatch.stop();
//        long totalTimeMillis = stopWatch.getTotalTimeMillis();
//        if (totalTimeMillis>=3000){
//            log.info("JDB-Action 请求: {} 返回: {} 耗时: {}ms", reqData,response, totalTimeMillis);
//        }
//        return response;
//
//    }
//
//    /**
//     * 处理取消下注
//     *
//     * @param userInfoVO
//     * @param orderNo
//     * @param amount
//     * @param remark
//     * @return
//     */
//
//    protected CoinRecordResultVO handleCancelBet(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderNo);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        // 账变类型
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(amount.abs());
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVO.setRemark(remark);
//        userCoinAddVO.setVenueCode(VenueEnum.JDB.getVenueCode());
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//
//    /**
//     * JDB 结算,需要记录局号
//     *
//     * @param userInfoVO   userInfo
//     * @param orderNo      orderNo
//     * @param payoutAmount amount
//     * @return result
//     */
//    protected CoinRecordResultVO handleSettle(UserInfoVO userInfoVO, String orderNo, BigDecimal payoutAmount, String remark) {
//        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
//        userCoinAddVOPayout.setOrderNo(orderNo);
//        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
//        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
//        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVOPayout.setRemark(remark);
//        userCoinAddVOPayout.setVenueCode(VenueEnum.JDB.getVenueCode());
//        return userCoinApi.addCoin(userCoinAddVOPayout);
//    }
//
//    /**
//     * 下注
//     *
//     * @param userInfoVO
//     * @param transactionId
//     * @param transferAmount
//     * @return
//     */
//    protected CoinRecordResultVO handleBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount, String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(transactionId);
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//        userCoinAddVO.setCoinValue(transferAmount);
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        userCoinAddVO.setRemark(remark);
//        //修改余额 记录账变
//        return userCoinApi.addCoin(userCoinAddVO);
//    }
//}
