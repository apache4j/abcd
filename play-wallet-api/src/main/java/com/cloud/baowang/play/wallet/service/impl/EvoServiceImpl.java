//package com.cloud.baowang.play.wallet.service.impl;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson2.JSON;
//import com.cloud.baowang.common.core.constants.CommonConstant;
//import com.cloud.baowang.common.core.constants.RedisConstants;
//import com.cloud.baowang.play.api.constants.VenueCodeConstants;
//import com.cloud.baowang.common.core.enums.ResultCode;
//import com.cloud.baowang.common.core.enums.StatusEnum;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
//import com.cloud.baowang.user.api.enums.UserStatusEnum;
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
//import com.cloud.baowang.common.redis.config.RedisUtil;
//import com.cloud.baowang.play.api.api.member.CasinoMemberApi;
//import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
//import com.cloud.baowang.play.api.api.venue.GameInfoApi;
//import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
//import com.cloud.baowang.play.api.enums.evo.EvoCurrencyEnum;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
//import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
//import com.cloud.baowang.play.api.vo.evo.EvoApiTokens;
//import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
//import com.cloud.baowang.play.api.vo.venue.GameInfoValidRequestVO;
//import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.wallet.enums.EvoGameErrorCode;
//import com.cloud.baowang.play.wallet.service.EvoService;
//import com.cloud.baowang.play.wallet.vo.res.evo.*;
//import com.cloud.baowang.user.api.api.UserInfoApi;
//import com.cloud.baowang.wallet.api.api.UserCoinApi;
//import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
//import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
//import jakarta.annotation.Resource;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.compress.utils.Lists;
//import org.apache.commons.lang3.StringUtils;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
///**
// * @className: EvoServiceImpl
// * @author: wade
// * @description: evo真人
// * @date: 13/8/25 09:05
// */
//@Slf4j
//@Service
//@AllArgsConstructor
//public class EvoServiceImpl implements EvoService {
//
//    @Resource
//    private VenueUserAccountApi venueUserAccountApi;
//
//    @Resource
//    private UserInfoApi userInfoApi;
//    @Resource
//    private PlayVenueInfoApi playVenueInfoApi;
//    @Resource
//    private CasinoMemberApi casinoMemberApi;
//    @Resource
//    private UserCoinApi userCoinApi;
//    @Resource
//    private GameInfoApi gameInfoApi;
//    @Resource
//    private UserCoinRecordApi userCoinRecordApi;
//
//    /**
//     * 根据我们平台币币种，匹配 ,返回的是对应场馆币种
//     */
//    public static EvoCurrencyEnum byPlatCurrencyCode(String platCurrencyCode) {
//        for (EvoCurrencyEnum tmp : EvoCurrencyEnum.values()) {
//            if (StringUtils.equals(platCurrencyCode, tmp.getPlatCurrencyCode())) {
//                return tmp;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 去除用户前缀,得到用户真实账号
//     */
//    public String getVenueUserAccount(String userAccount) {
//        return venueUserAccountApi.getVenueUserAccount(userAccount);
//    }
//
//    @Override
//    public BalanceResponse balance(String authToken, BalanceRequest request) {
//        try {
//            VenueEnum venueEnum = VenueEnum.EVO;
//            String userId = getVenueUserAccount(request.getUserId());
//            if (StringUtils.isBlank(userId)) {
//                log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (userInfoVO == null /*|| userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())*/) {
//                log.info("{}:用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
//            if (!tokenFlag) {
//                log.info("{}:用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.EVO);
//            ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            if (casinoMember == null || casinoMember.getData() == null) {
//                log.info("{}:游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            CasinoMemberRespVO casinoMemberData = casinoMember.getData();
//            String userAccount = casinoMemberData.getUserAccount();
//            String siteCode = casinoMemberData.getSiteCode();
//            // 币种校验与切换
//
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(userAccount).siteCode(siteCode).build());
//            EvoCurrencyEnum currency = byPlatCurrencyCode(userCenterCoin.getCurrency());
//            if (currency == null || currency.getCode() == null || !StringUtils.equals(currency.getCode(), request.getCurrency())) {
//                log.info("{}:用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            BigDecimal totalAmount = userCenterCoin.getTotalAmount();
//
//            return BalanceResponse.success(request.getUuid(), totalAmount);
//        } catch (Exception e) {
//            log.error("游戏登入后令牌验证异常, userId: {}", request.getUserId(), e);
//            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//        }
//
//    }
//
//    @Override
//    public CheckUserResponse check(String authToken, CheckUserRequest request) {
//        try {
//            VenueEnum venueEnum = VenueEnum.EVO;
//            String userId = getVenueUserAccount(request.getUserId());
//            if (StringUtils.isBlank(userId)) {
//                log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
//                return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
//            }
//
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (userInfoVO == null || userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
//                log.info("{}:用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
//            if (!tokenFlag) {
//                return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
//            }
//
//            return CheckUserResponse.success(request.getUuid(), request.getSid());
//        } catch (Exception e) {
//            log.error("游戏登入后令牌验证异常, userId: {}", request.getUserId(), e);
//            return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
//        }
//    }
//
//    /**
//     * 生成用户的 sid
//     *
//     * @param request 请求参数
//     */
//    @Override
//    public CheckUserResponse sid(String authToken, CheckUserRequest request) {
//
//        VenueEnum venueEnum = VenueEnum.EVO;
//        String userId = getVenueUserAccount(request.getUserId());
//        if (StringUtils.isBlank(userId)) {
//            log.info("{}:用户sid账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
//            //return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
//        }
//
//        /*UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//        if (userInfoVO == null || userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
//            log.info("{}:用户sid被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//            //return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
//        }*/
//        /*boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
//        if (!tokenFlag) {
//            //return buildFailResponse(request, EvoGameErrorCode.INVALID_PARAMETER);
//        }*/
//
//        // 生成新的 SID
//        String newSid = UUID.randomUUID().toString().replace("-", "");
//        return CheckUserResponse.success(request.getUuid(), newSid);
//    }
//
//    @Override
//    public BalanceResponse debit(String authToken, DebitRequest request) {
//        try {
//            VenueEnum venueEnum = VenueEnum.EVO;
//            String userId = getVenueUserAccount(request.getUserId());
//            if (StringUtils.isBlank(userId)) {
//                log.info("{}:投注用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (userInfoVO == null
//                    || userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())
//                    || userInfoVO.getAccountStatus().contains(UserStatusEnum.LOGIN_LOCK.getCode())) {
//                log.info("{}:投注用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.ACCOUNT_LOCKED);
//            }
//            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), true);
//            if (!tokenFlag) {
//                log.info("{}:投注用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.TEMPORARY_ERROR);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.EVO);
//            ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            if (casinoMember == null || casinoMember.getData() == null) {
//                log.info("{}:投注游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            CasinoMemberRespVO casinoMemberData = casinoMember.getData();
//
//            // 币种校验与切换
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.info("{}:投注用户钱包,不存在:{}", VenueEnum.EVO.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            EvoCurrencyEnum currency = byPlatCurrencyCode(userCenterCoin.getCurrency());
//            if (currency == null || currency.getCode() == null || !StringUtils.equals(currency.getCode(), request.getCurrency())) {
//                log.info("{}:投注用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            // 游戏配置是否存在
//            String tableId = request.getGame().getDetails().getTable().getId();
//            boolean gameAvailable = isGameAvailable(userInfoVO.getSiteCode(), tableId, VenueCodeConstants.EVO, userInfoVO.getMainCurrency());
//            if (!gameAvailable) {
//                log.info("该站:{} 没有分配:{} 游戏的权限", userInfoVO.getSiteCode(), tableId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.ACCOUNT_LOCKED);
//            }
//            BigDecimal userTotalAmount = userCenterCoin.getTotalAmount();
//            // 投注金额
//            BigDecimal betAmount = request.getTransaction().getAmount();//账变金额
//            // 余额校验
//
//            if (userTotalAmount.compareTo(betAmount) < 0) {
//                log.info("{}:投注用户钱包,余额不足:{},用户金额:{},扣款金额:{}", VenueEnum.EVO.getVenueName(), userId, userTotalAmount, betAmount);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INSUFFICIENT_FUNDS);
//            }
//            // 每一笔注单都要发生帐变
//            //List<DebitRequest.Transaction.Bets> bets = request.getTransaction().getBets();
//
//            CoinRecordResultVO recordResultVO = new CoinRecordResultVO();
//            // 取总的投注进行帐变
//            // 校验
//            UserCoinAddVO userCoinAddVO = getUserCoinAddVO(request, null, userInfoVO);
//            // 或者金额是否为0
//            if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
//                // 帐变金额为o
//                recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//            }
//            // 判断是否重复，
//            List<UserCoinRecordVO> userCoinRecord = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), userCoinAddVO.getBalanceType());
//            if (ObjectUtil.isNotEmpty(userCoinRecord)) {
//                // 有重复
//                recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_EXIST);
//            }
//            // 取消订单校验
//            boolean betCancel = isBetCancel(userInfoVO, userCoinAddVO.getOrderNo());
//            if (betCancel) {
//                recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.FINAL_ERROR_ACTION_FAILED);
//            }
//            recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//            if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
//                log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
//            }
//            /*if (bets != null && bets.size() > 0) {
//                for (DebitRequest.Transaction.Bets bet : bets) {
//                    BigDecimal amount = bet.getAmount();
//                    String code = bet.getCode();
//                    // 校验投注金额
//                    if (Objects.isNull(code) || Objects.isNull(amount)) {
//                        log.info("{}:投注详值不合法:{},用户注单:{},投注金额:{}", VenueEnum.EVO.getVenueName(), userId, code, amount);
//                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//                    }
//                    UserCoinAddVO userCoinAddVO = getUserCoinAddVO(request, bet, userInfoVO);
//                    // 是否重复
//                    List<UserCoinRecordVO> userCoinRecord = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), userCoinAddVO.getBalanceType());
//                    if (ObjectUtil.isNotEmpty(userCoinRecord)) {
//                        recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_EXIST);
//                    }
//                    // 取消订单校验
//                    boolean betCancel = isBetCancel(userInfoVO, userCoinAddVO.getOrderNo());
//                    if (betCancel) {
//                        recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.FINAL_ERROR_ACTION_FAILED);
//                    }
//
//                }
//
//                for (DebitRequest.Transaction.Bets bet : bets) {
//                    UserCoinAddVO userCoinAddVO = getUserCoinAddVO(request, bet, userInfoVO);
//                    // 金额是否为0
//                    if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
//                        // 帐变金额为o
//                        recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                        continue;
//                    }
//                    // 是否重复
//                    List<UserCoinRecordVO> userCoinRecord = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), userCoinAddVO.getBalanceType());
//                    if (ObjectUtil.isNotEmpty(userCoinRecord)) {
//                        recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                        continue;
//                    }
//
//                    recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//                    if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
//                        log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
//                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//                    }
//                }
//            } else {
//                // 校验
//                UserCoinAddVO userCoinAddVO = getUserCoinAddVO(request, null, userInfoVO);
//                // 或者金额是否为0
//                if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
//                    // 帐变金额为o
//                    recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                    return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//                }
//                // 判断是否重复，
//                List<UserCoinRecordVO> userCoinRecord = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), userCoinAddVO.getBalanceType());
//                if (ObjectUtil.isNotEmpty(userCoinRecord)) {
//                    // 有重复
//                    recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_EXIST);
//                }
//                // 取消订单校验
//                boolean betCancel = isBetCancel(userInfoVO, userCoinAddVO.getOrderNo());
//                if (betCancel) {
//                    recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.FINAL_ERROR_ACTION_FAILED);
//                }
//                recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//                if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
//                    log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
//                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
//                }
//            }*/
//            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//        } catch (Exception e) {
//            log.error("EVO游戏投注异常, userId: {}", request.getUserId(), e);
//            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
//        }
//
//    }
//
//    /**
//     * 投注校验是否该注单取消了
//     *
//     * @return 结果
//     */
//    private Boolean isBetCancel(UserInfoVO userInfoVO, String orderNo) {
//        //订单状态
//        String key = String.format(RedisConstants.EVO_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userInfoVO.getUserId());
//        List<String> cancelBetList = RedisUtil.getValue(key);
//        if (cancelBetList != null && !cancelBetList.isEmpty()) {
//            return cancelBetList.contains(orderNo);
//        }
//        return false;
//
//    }
//
//    @Override
//    public BalanceResponse credit(String authToken, CreditRequest request) {
//        try {
//            VenueEnum venueEnum = VenueEnum.EVO;
//            String userId = getVenueUserAccount(request.getUserId());
//            if (StringUtils.isBlank(userId)) {
//                log.info("{}:派彩失败,用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (userInfoVO == null /*|| userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())*/) {
//                log.info("{}:派彩失败,用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
//            if (!tokenFlag) {
//                log.info("{}:派彩失败,用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.EVO);
//            ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            if (casinoMember == null || casinoMember.getData() == null) {
//                log.info("{}:派彩失败,游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            // 币种校验与切换
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.info("{}:派彩失败,用户钱包,不存在:{}", VenueEnum.EVO.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            EvoCurrencyEnum currency = byPlatCurrencyCode(userCenterCoin.getCurrency());
//            if (currency == null || currency.getCode() == null || !currency.getCode().equals(request.getCurrency())) {
//                log.info("{}:派彩失败,用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//
//            // 每一笔注单都要发生帐变
//            //List<CreditRequest.Transaction.Bets> bets = request.getTransaction().getBets();
//            CoinRecordResultVO recordResultVO = new CoinRecordResultVO();
//            UserCoinAddVO userCoinAddVO = getUserCoinAddVOForPayOff(request, null, userInfoVO);
//            BalanceResponse response = validateUserCredit(userCoinAddVO, request, userCenterCoin, recordResultVO);
//            if (ObjectUtil.isNotEmpty(response)) {
//                return response;
//            }
//            recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//            if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
//                if (!recordResultVO.getResult() && recordResultVO.getResultStatus().equals(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS)) {
//                    return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//                }
//                log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//           /* if (bets != null && bets.size() > 0) {
//                for (CreditRequest.Transaction.Bets bet : bets) {
//                    BigDecimal amount = bet.getPayoff();
//                    String code = bet.getCode();
//                    // 校验投注金额
//                    if (Objects.isNull(code) || Objects.isNull(amount)) {
//                        log.info("{}:派彩失败值不合法:{},用户注单:{},投注金额:{}", VenueEnum.EVO.getVenueName(), userId, code, amount);
//                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//                    }
//                    UserCoinAddVO userCoinAddVO = getUserCoinAddVOForPayOff(request, bet, userInfoVO);
//                    BalanceResponse response = validateUserCredit(userCoinAddVO, request, userCenterCoin, recordResultVO);
//                    if (ObjectUtil.isNotEmpty(response)) {
//                        return response;
//                    }
//
//                }
//
//                for (CreditRequest.Transaction.Bets bet : bets) {
//                    UserCoinAddVO userCoinAddVO = getUserCoinAddVOForPayOff(request, bet, userInfoVO);
//                    recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//                    if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
//                        log.error("派彩失败帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
//                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
//                    }
//                }
//            } else {
//                UserCoinAddVO userCoinAddVO = getUserCoinAddVOForPayOff(request, null, userInfoVO);
//                BalanceResponse response = validateUserCredit(userCoinAddVO, request, userCenterCoin, recordResultVO);
//                if (ObjectUtil.isNotEmpty(response)) {
//                    return response;
//                }
//                recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//                if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
//                    if (!recordResultVO.getResult() && recordResultVO.getResultStatus().equals(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS)) {
//                        return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//                    }
//                    log.error("投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
//                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//                }
//            }*/
//
//            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//        } catch (Exception e) {
//            log.error("派彩失败, 参数: {}", JSON.toJSONString(request), e);
//            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
//        }
//    }
//
//    /**
//     * 派彩校验
//     * 1.投注。 2.取消投注  3.派彩，那么这个时候，派彩会成功还是失败， 应该是失败，提示BET_ALREADY_SETTLED
//     *
//     * @param userCoinAddVO  用户投注请求对象
//     * @param userCenterCoin 用户中心余额对象
//     * @param recordResultVO 记录结果对象，用于设置余额
//     * @return 如果存在问题返回对应的BalanceResponse，否则返回null
//     */
//    private BalanceResponse validateUserCredit(
//            UserCoinAddVO userCoinAddVO,
//            CreditRequest request,
//            UserCoinWalletVO userCenterCoin,
//            CoinRecordResultVO recordResultVO) {
//
//        // 判断是否重复，或者金额是否为0
//        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
//            // 帐变金额为o
//            recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//        }
//        // 取消投注
//        List<UserCoinRecordVO> userCoinRecord = userCoinRecordApi.getUserCoinRecordsForEVO(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId());
//        boolean isExist = false;
//        if (ObjectUtil.isNotEmpty(userCoinRecord)) {
//            // 判断是否重复或者已经取消
//            for (UserCoinRecordVO userCoinRecordVO : userCoinRecord) {
//                String coinType = userCoinRecordVO.getCoinType();
//                //是否有这笔投注
//                // 是否有这笔投注
//                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.GAME_BET.getCode()) && ObjectUtil.equals(userCoinRecordVO.getBalanceType(), CoinBalanceTypeEnum.EXPENSES.getCode())) {
//                    isExist = true;
//                }
//                // 是否重复
//                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())) {
//                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
//                }
//                // 这笔投注已经取消
//                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())) {
//                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
//                }
//
//            }
//
//        }
//        // 是否有这笔投注
//        //List<UserCoinRecordVO> userCoinRecordRePeat = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), CoinBalanceTypeEnum.EXPENSES.getCode());
//        if (!isExist) {
//            // 没有这笔投注
//            recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_DOES_NOT_EXIST);
//        }
//        // 4. 校验通过
//        return null;
//    }
//
//
//    @Override
//    public BalanceResponse cancel(String authToken, CancelRequest request) {
//        try {
//            VenueEnum venueEnum = VenueEnum.EVO;
//            String userId = getVenueUserAccount(request.getUserId());
//            if (StringUtils.isBlank(userId)) {
//                log.info("{}:取消投注,用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (userInfoVO == null /*|| userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())*/) {
//                log.info("{}:取消投注,用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
//            if (!tokenFlag) {
//                log.info("{}:取消投注,用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.EVO);
//            ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            if (casinoMember == null || casinoMember.getData() == null) {
//                log.info("{}:取消投注,游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            // 币种校验与切换
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.info("{}:取消投注,用户钱包,不存在:{}", VenueEnum.EVO.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            EvoCurrencyEnum currency = byPlatCurrencyCode(userCenterCoin.getCurrency());
//            if (currency == null || currency.getCode() == null || !currency.getCode().equals(request.getCurrency())) {
//                log.info("{}:取消投注,用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            // 每一笔注单都要发生帐变
//
//            CoinRecordResultVO recordResultVO = new CoinRecordResultVO();
//            UserCoinAddVO userCoinAddVO = getUserCoinAddVOForCancel(request, null, userInfoVO);
//            setCancelBetList(userInfoVO, userCoinAddVO.getOrderNo());
//            BalanceResponse response = ValidateCancel(userCoinAddVO, request, userCenterCoin, recordResultVO);
//            if (response != null) {
//                return response;
//            }
//
//            recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//            if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
//                log.error("取消投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            /*List<CancelRequest.Transaction.Bets> bets = request.getTransaction().getBets();
//            if (bets != null && bets.size() > 0) {
//                for (CancelRequest.Transaction.Bets bet : bets) {
//                    BigDecimal amount = bet.getAmount();
//                    String code = bet.getCode();
//                    // 校验投注金额
//                    if (Objects.isNull(code) || Objects.isNull(amount)) {
//                        log.info("{}:取消投注不合法:{},用户注单:{},投注金额:{}", VenueEnum.EVO.getVenueName(), userId, code, amount);
//                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//                    }
//
//                    UserCoinAddVO userCoinAddVO = getUserCoinAddVOForCancel(request, bet, userInfoVO);
//                    setCancelBetList(userInfoVO, userCoinAddVO.getOrderNo());
//                    BalanceResponse response = ValidateCancel(userCoinAddVO, request, userCenterCoin, recordResultVO);
//                    if (response != null) {
//                        return response;
//                    }
//
//
//                }
//                for (CancelRequest.Transaction.Bets bet : bets) {
//
//                    UserCoinAddVO userCoinAddVO = getUserCoinAddVOForCancel(request, bet, userInfoVO);
//
//                    recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//
//                    if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
//                        log.error("取消投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
//                        return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//                    }
//
//                }
//            } else {
//                UserCoinAddVO userCoinAddVO = getUserCoinAddVOForCancel(request, null, userInfoVO);
//                setCancelBetList(userInfoVO, userCoinAddVO.getOrderNo());
//                BalanceResponse response = ValidateCancel(userCoinAddVO, request, userCenterCoin, recordResultVO);
//                if (response != null) {
//                    return response;
//                }
//
//                recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//                if (recordResultVO == null || recordResultVO.getResult() == null || !recordResultVO.getResult()) {
//                    log.error("取消投注帐变失败,订单详情:{}.请求入参:{}", JSON.toJSONString(recordResultVO), JSON.toJSONString(request));
//                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//                }
//
//
//            }*/
//
//            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//        } catch (Exception e) {
//            log.error("派彩失败, 参数: {}", JSON.toJSONString(request), e);
//            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
//        }
//    }
//
//    private void setCancelBetList(UserInfoVO userInfoVO, String orderId) {
//        log.info("evo cancelBet error 账单不存在: " + orderId);
//        String key = String.format(RedisConstants.EVO_CANCEL_BET_KEY, userInfoVO.getSiteCode(), userInfoVO.getUserId());
//        List<String> cancelBetList = RedisUtil.getValue(key);
//        if (cancelBetList == null || cancelBetList.isEmpty()) {
//            cancelBetList = Lists.newArrayList();
//        }
//        cancelBetList.add(orderId);
//        log.info(" evo cancel bet list : " + cancelBetList + " tid : " + orderId + " key : " + key);
//        RedisUtil.setValue(key, cancelBetList, 5L, TimeUnit.MINUTES);
//    }
//
//    private BalanceResponse ValidateCancel(UserCoinAddVO userCoinAddVO,
//                                           CancelRequest request,
//                                           UserCoinWalletVO userCenterCoin,
//                                           CoinRecordResultVO recordResultVO) {
//
//        // 判断是否重复，或者金额是否为0
//        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
//            // 帐变金额为o
//            recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//            return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//        }
//        // 取消投注是否重复
//        List<UserCoinRecordVO> userCoinRecord = userCoinRecordApi.getUserCoinRecordsForEVO(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId());
//        boolean isExist = false;
//        if (CollectionUtil.isNotEmpty(userCoinRecord)) {
//            // 判断是否重复或者已经取消
//            for (UserCoinRecordVO userCoinRecordVO : userCoinRecord) {
//                String coinType = userCoinRecordVO.getCoinType();
//                //是否有这笔投注
//                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.GAME_BET.getCode()) && ObjectUtil.equals(userCoinRecordVO.getBalanceType(), CoinBalanceTypeEnum.EXPENSES.getCode())) {
//                    isExist = true;
//                }
//                // 是否已经派彩
//                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode())) {
//                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
//                }
//                // 这笔投注已经取消
//                if (ObjectUtil.equals(coinType, WalletEnum.CoinTypeEnum.CANCEL_BET.getCode())) {
//                    log.info("{}:取消投注,下注已取消,订单号:{}", VenueEnum.EVO.getVenueName(), userCoinAddVO.getOrderNo());
//                    return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
//                }
//            }
//        }
//        // 是否有这笔投注
//        //List<UserCoinRecordVO> userCoinRecordRePeat = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), CoinBalanceTypeEnum.EXPENSES.getCode());
//        if (!isExist) {
//            log.info("{}:取消投注,下注不存在,订单号:{}", VenueEnum.EVO.getVenueName(), userCoinAddVO.getOrderNo());
//            // 没有这笔投注
//            recordResultVO.setCoinAfterBalance(userCenterCoin.getCenterAmount());
//            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_DOES_NOT_EXIST);
//        }
//        return null;
//    }
//
//
//    @Override
//    public BalanceResponse promo_payout(String authToken, PromoPayoutRequest request) {
//        try {
//            VenueEnum venueEnum = VenueEnum.EVO;
//            String userId = getVenueUserAccount(request.getUserId());
//            if (StringUtils.isBlank(userId)) {
//                log.info("{}:奖励,用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId == null ? "unknown" : userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//
//            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
//            if (userInfoVO == null /*|| userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())*/) {
//                log.info("{}:奖励,用户被锁定或不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            boolean tokenFlag = validatorToken(authToken, userInfoVO.getSiteCode(), false);
//            if (!tokenFlag) {
//                log.info("{}:奖励,用户token校验失败,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
//            casinoMemberReqVO.setVenueUserAccount(request.getUserId());
//            casinoMemberReqVO.setVenueCode(VenueCodeConstants.EVO);
//            ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
//            if (casinoMember == null || casinoMember.getData() == null) {
//                log.info("{}:奖励,游戏账户不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            // 币种校验与切换
//            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
//            if (Objects.isNull(userCenterCoin)) {
//                log.info("{}:奖励,用户钱包,不存在:{}", VenueEnum.EVO.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            EvoCurrencyEnum currency = byPlatCurrencyCode(userCenterCoin.getCurrency());
//            if (currency == null || currency.getCode() == null || !currency.getCode().equals(request.getCurrency())) {
//                log.info("{}:奖励,用户货币不存在,用户ID:{}", venueEnum.getVenueName(), userId);
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.INVALID_PARAMETER);
//            }
//            // 每一笔注单都要发生帐变
//            BigDecimal payoutAmount = request.getPromoTransaction().getAmount();
//            String orderNo = request.getPromoTransaction().getId();
//            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//            userCoinAddVO.setOrderNo(orderNo);
//            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//            userCoinAddVO.setUserId(userInfoVO.getUserId());
//            userCoinAddVO.setCoinValue(payoutAmount);
//            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//            // 使用 一组投注或者一组派彩
//            userCoinAddVO.setRemark("promo_payout_" + orderNo);
//            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//            userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//            // 判断是否重复，或者金额是否为0
//            if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) == 0) {
//                // 帐变金额为o
//                return BalanceResponse.success(request.getUuid(), userCenterCoin.getCenterAmount());
//            }
//            // 重复奖励
//            List<UserCoinRecordVO> userCoinRecordRepeats = userCoinRecordApi.getUserCoinRecordPG(userCoinAddVO.getOrderNo(), userCoinAddVO.getUserId(), userCoinAddVO.getBalanceType());
//            if (CollectionUtil.isNotEmpty(userCoinRecordRepeats)) {
//                return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.BET_ALREADY_SETTLED);
//            }
//            CoinRecordResultVO recordResultVO = userCoinApi.addCoin(userCoinAddVO);
//            if (recordResultVO != null && recordResultVO.getResult()) {
//                return BalanceResponse.success(request.getUuid(), recordResultVO.getCoinAfterBalance());
//            }
//            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
//        } catch (Exception e) {
//            log.error("奖励失败, 参数: {}", JSON.toJSONString(request), e);
//            return BalanceResponse.fail(request.getUuid(), EvoGameErrorCode.UNKNOWN_ERROR);
//        }
//
//    }
//
//    @NotNull
//    private UserCoinAddVO getUserCoinAddVO(DebitRequest request, DebitRequest.Transaction.Bets bet, UserInfoVO userInfoVO) {
//        String refId = request.getTransaction().getRefId();
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        // 使用 一组投注或者一组派彩
//        //userCoinAddVO.setRemark(request.getTransaction().getRefId());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
//        if (Objects.isNull(bet)) {
//            userCoinAddVO.setOrderNo(refId);
//            userCoinAddVO.setCoinValue(request.getTransaction().getAmount());
//        } else {
//            userCoinAddVO.setOrderNo(refId + "_" + bet.getCode());
//            userCoinAddVO.setCoinValue(bet.getAmount());
//        }
//        return userCoinAddVO;
//    }
//
//
//    @NotNull
//    private UserCoinAddVO getUserCoinAddVOForPayOff(CreditRequest request, CreditRequest.Transaction.Bets bet, UserInfoVO userInfoVO) {
//
//        String refId = request.getTransaction().getRefId();
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        // 使用 一组投注或者一组派彩
//        //userCoinAddVO.setRemark(request.getTransaction().getRefId());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//        if (Objects.isNull(bet)) {
//            userCoinAddVO.setOrderNo(refId);
//            userCoinAddVO.setCoinValue(request.getTransaction().getAmount());
//        } else {
//            String orderNo = refId + "_" + bet.getCode();
//            userCoinAddVO.setOrderNo(orderNo);
//            userCoinAddVO.setCoinValue(bet.getPayoff());
//        }
//
//        return userCoinAddVO;
//    }
//
//    @NotNull
//    private Boolean getUserCoinAddVOForCancelCheck(CancelRequest request, CancelRequest.Transaction.Bets bet, UserInfoVO userInfoVO) {
//
//        String orderNo;
//        if (Objects.isNull(bet)) {
//            orderNo = request.getTransaction().getRefId();
//        } else {
//            orderNo = request.getTransaction().getRefId() + "_" + bet.getCode();
//
//        }
//        List<UserCoinRecordVO> userCoinRecordPG = userCoinRecordApi.getUserCoinRecordPG(orderNo, userInfoVO.getUserId(), CoinBalanceTypeEnum.EXPENSES.getCode());
//        if (ObjectUtil.isNotEmpty(userCoinRecordPG)) {
//            return true;
//        }
//        return false;
//    }
//
//
//    @NotNull
//    private UserCoinAddVO getUserCoinAddVOForCancel(CancelRequest request, CancelRequest.Transaction.Bets bet, UserInfoVO userInfoVO) {
//
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//
//        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
//        userCoinAddVO.setUserId(userInfoVO.getUserId());
//
//        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
//        // 使用 一组投注或者一组派彩
//        //userCoinAddVO.setRemark(request.getTransaction().getRefId());
//        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
//        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
//        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
//        if (Objects.isNull(bet)) {
//            String orderNo = request.getTransaction().getRefId();
//            userCoinAddVO.setOrderNo(orderNo);
//            userCoinAddVO.setCoinValue(request.getTransaction().getAmount());
//        } else {
//            String orderNo = request.getTransaction().getRefId() + "_" + bet.getCode();
//            userCoinAddVO.setOrderNo(orderNo);
//            userCoinAddVO.setCoinValue(bet.getAmount());
//        }
//
//        return userCoinAddVO;
//    }
//
//
//    /**
//     * 检查指定场馆的游戏是否可用。
//     *
//     * @param siteCode  站点代码
//     * @param gameCode  游戏代码
//     * @param venueCode 场馆代码
//     * @return 如果游戏存在且状态为开启，返回 {@code true}，否则返回 {@code false}
//     */
//    private boolean isGameAvailable(String siteCode, String gameCode, String venueCode, String currencyCode) {
//        GameInfoValidRequestVO requestVO = GameInfoValidRequestVO.builder().siteCode(siteCode).gameId(gameCode).venueCode(venueCode).build();
//        ResponseVO<GameInfoVO> responseVO = gameInfoApi.getGameInfoByCode(requestVO);
//        // todo test
//       /* boolean gameAvailable = true;
//        if (gameAvailable) {
//            return true;
//        }*/
//        if (!responseVO.isOk()) {
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
//    private boolean validatorToken(String authToken, String siteCode, Boolean isCheck) {
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder().siteCode(siteCode).venueCode(VenueEnum.EVO.getVenueCode()).build();
//        boolean venueMaintainClosed = venueMaintainClosed(VenueEnum.EVO.getVenueCode(), siteCode);
//        if (venueMaintainClosed && isCheck) {
//            log.error("场馆维护中, siteCode: {}", siteCode);
//            return false;
//        }
//        // Step 4: 通过 API 获取场馆信息
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
//        if (venueInfoVO == null) {
//            log.error("获取场馆信息失败, siteCode: {}", siteCode);
//            return false;
//        }
//
//        EvoApiTokens evoApiTokens = JSON.parseObject(venueInfoVO.getMerchantKey(), EvoApiTokens.class);
//        if (evoApiTokens == null) {
//            log.error("获取场馆信息失败, siteCode: {}", siteCode);
//            return false;
//        }
//        String token = evoApiTokens.getWalletToken();
//        return StringUtils.equals(token, authToken);
//    }
//
//    protected boolean venueMaintainClosed(String venueCode, String siteCode) {
//        ResponseVO<Boolean> venueInfoVOResponseVO = playVenueInfoApi.venueMaintainClosed(venueCode, siteCode);
//        if (venueInfoVOResponseVO.getCode() != ResultCode.SUCCESS.getCode()) {
//            return true;
//        }
//        Boolean status = venueInfoVOResponseVO.getData();
//        return status == null || status;
//    }
//
//
//    private CheckUserResponse buildFailResponse(CheckUserRequest request, EvoGameErrorCode evoGameErrorCode) {
//        return CheckUserResponse.fail(request.getUuid(), request.getSid(), evoGameErrorCode);
//    }
//
//
//}
