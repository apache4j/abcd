package com.cloud.baowang.play.game.pp.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.BetGameTypeEnum;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.mq.PPFreeGameRecordReqVO;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.pp.PPBaseResVO;
import com.cloud.baowang.play.api.vo.pp.req.*;
import com.cloud.baowang.play.api.vo.pp.res.*;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.pp.enums.PPCurrencyEnum;
import com.cloud.baowang.play.game.pp.enums.PPErrorCodeEnum;
import com.cloud.baowang.play.game.pp.enums.PPGameStatusEnum;
import com.cloud.baowang.play.game.pp.enums.PPLangCodeEnum;
import com.cloud.baowang.play.game.pp.utils.CsvParserUtil;
import com.cloud.baowang.play.game.pp.utils.HashValidatorUtils;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.CasinoMemberService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.task.pulltask.pp.params.PPPullBetParams;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.PP)
@AllArgsConstructor
public class PPGameServiceImpl extends GameBaseService implements GameService {

    private final OrderRecordProcessService orderRecordProcessService;

    private final CasinoMemberService casinoMemberService;


    //NOTE 3.4 Authenticate
    public Object authenticate(JSONObject vo) {

        PPAuthenticateReqVO req = vo.to(PPAuthenticateReqVO.class);

        if (!req.isValid()) {
            log.error("{} pp authenticate但参数不全, authenticate:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_7.toResVO(null);
        }

        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().casinoPassword(req.getToken()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("{} pp authenticate 会员不存在, authenticate, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }
        //NOTE 查询场馆状态
        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP,null);
        if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
            log.error("{} pp authenticate venueInfoVO不存在或不开放, authenticate, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_8.toResVO(null);
        }

        if (!HashValidatorUtils.isValidHash(new HashMap<>(vo),venueInfoVO.getMerchantKey())){
            return PPErrorCodeEnum.CODE_5.toResVO(null);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} pp authenticate userInfoVO不存在, authenticate, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }
        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        return PPErrorCodeEnum.CODE_0.toResVO(PPAuthenticateResVO.builder()
                .userId(casinoMember.getVenueUserAccount())
                .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                .cash(userCenterCoin.getTotalAmount())
                .bonus(BigDecimal.ZERO)
                .token(req.getToken())
                .build());
    }

    //NOTE 3.5 Balance
    public Object balance(JSONObject vo) {

        PPBalanceReqVO req = vo.to(PPBalanceReqVO.class);

        if (!req.isValid()) {
            log.error("pp balance 但参数不全, balance:{} ",  req);
            return PPErrorCodeEnum.CODE_7.toResVO(null);
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).casinoPassword(req.getToken()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("pp balance casinoMember不存在, balance, 参数:{} ", req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} pp balance userInfoVO不存在, balance, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        //NOTE 查询场馆状态
        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP, null);
        if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
            log.error("{} pp balance venueInfoVO不存在或不开放, balance, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_8.toResVO(null);
        }

        if (!HashValidatorUtils.isValidHash(new HashMap<>(vo),venueInfoVO.getMerchantKey())){
            return PPErrorCodeEnum.CODE_5.toResVO(null);
        }

        if (userGameLock(userInfoVO)) {
            log.error("{} balance game lock, user参数:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
            return PPErrorCodeEnum.CODE_6.toResVO(null);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        return PPErrorCodeEnum.CODE_0.toResVO(PPBalanceResVO.builder()
                .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                .cash(userCenterCoin.getTotalAmount())
                .bonus(BigDecimal.ZERO)
                .build());
    }

    //NOTE 3.6 Bet
    public Object bet(JSONObject vo) {

        PPBetReqVO req = vo.to(PPBetReqVO.class);
        if (!req.isValid()) {
            log.error("pp bet但参数不全, balance:{} ", req);
            return PPErrorCodeEnum.CODE_7.toResVO(null);
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).casinoPassword(req.getToken()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null || casinoMember.getUserId() ==null) {
            log.error("{} pp bet casinoMember不存在, bet, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} pp bet userInfoVO不存在, bet, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }
        if (userGameLock(userInfoVO)) {
            log.error("{} bet game lock, user参数:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
            return PPErrorCodeEnum.CODE_6.toResVO(null);
        }

        if (StrUtil.isNotEmpty(userInfoVO.getAccountStatus()) && userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} pp bet 会员被锁定, userInfoVO:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
            return PPErrorCodeEnum.CODE_6.toResVO(null);
        }
        //NOTE 查询场馆状态
        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.PP.getVenueCode(), userInfoVO.getMainCurrency());

        if (venueInfoVO == null || !Objects.equals(StatusEnum.OPEN.getCode(), venueInfoVO.getStatus())) {
            return PPErrorCodeEnum.CODE_8.toResVO(null);
        }

        if (!HashValidatorUtils.isValidHash(new HashMap<>(vo),venueInfoVO.getMerchantKey())){
            return PPErrorCodeEnum.CODE_5.toResVO(null);
        }

        if (venueGameMaintainClosed(venueInfoVO.getVenueCode(),casinoMember.getSiteCode(),req.getGameId())){
            log.info("{}:游戏未开启", VenueEnum.PP.getVenueName());
            return PPErrorCodeEnum.CODE_8.toResVO(null);
        }

        BigDecimal totalAmount = getUserCenterCoin(userInfoVO.getUserId()).getTotalAmount();
        //4. 去重操作
        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());
        userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinRecordVo.setOrderNo(req.getRoundId());
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
        if (CollUtil.isNotEmpty(userCoinRecords)) {
            log.error("{} 下注失败, 当前reference已经被处理, 请求参数: {}", VenueEnum.PP.getVenueCode(), req);

            return PPErrorCodeEnum.CODE_0.toResVO(PPBetResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(totalAmount)
                    .bonus(BigDecimal.ZERO)
                    .usedPromo(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());

        }


        if (req.getAmount().compareTo(BigDecimal.ZERO)<=0){
            //T发消息
            if (StrUtil.isNotEmpty(req.getBonusCode())) {

                log.info("免费旋转下注: {} ", req);
                PPFreeGameRecordReqVO sendVO = new PPFreeGameRecordReqVO();
                sendVO.setOrderNo(req.getBonusCode());
                sendVO.setUserId(userInfoVO.getUserId());
                sendVO.setAcquireNum(1);
                sendVO.setBetId(req.getReference());
                sendVO.setPayOutAmount(BigDecimal.ZERO);

                KafkaUtil.send(TopicsConstants.FREE_GAME_RECORD_CONSUME, sendVO);
            }

            return PPErrorCodeEnum.CODE_0.toResVO(PPBetResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(totalAmount)
                    .bonus(BigDecimal.ZERO)
                    .usedPromo(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
        }

        UserCoinAddVO userCoinAddVO = getBetCoinAddVO(req, userInfoVO);
        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

        if (CoinBalanceTypeEnum.EXPENSES.getCode().equals(userCoinAddVO.getBalanceType()) && totalAmount.compareTo(req.getAmount())<0){
            return PPErrorCodeEnum.CODE_1.toResVO(null);
        }


        return switch (recordResultVO.getResultStatus()) {
            case INSUFFICIENT_BALANCE -> PPErrorCodeEnum.CODE_1.toResVO(null);
            case FAIL, WALLET_NOT_EXIST -> PPErrorCodeEnum.CODE_100.toResVO(null);
            //NOTE 4. 去重操作, 合并到账变状态里, 算成功.
            case REPEAT_TRANSACTIONS -> PPErrorCodeEnum.CODE_0.toResVO(PPBetResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(totalAmount)
                    .bonus(BigDecimal.ZERO)
                    .usedPromo(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
            default -> PPErrorCodeEnum.CODE_0.toResVO(PPBetResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(recordResultVO.getCoinAfterBalance())
                    .bonus(BigDecimal.ZERO)
                    .usedPromo(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
        };
    }

    //NOTE 3.7 Result
    public Object result(JSONObject vo) {


        PPResultReqVO req = vo.to(PPResultReqVO.class);
        if (!req.isValid()) {
            log.error("{} result但参数不全, result:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_7.toResVO(null);
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null || casinoMember.getUserId() == null) {
            log.error("{} result casinoMember不存在, result, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null || userInfoVO.getUserId() == null) {
            log.error("{} result userInfoVO不存在, result, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        if (StrUtil.isNotEmpty(userInfoVO.getAccountStatus()) && userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} result 但会员被锁定, userInfoVO:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
            return PPErrorCodeEnum.CODE_6.toResVO(null);
        }
        //NOTE result 不受场馆状态影响
//        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.PP.getVenueCode(), null);
        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP,null);
        if (!HashValidatorUtils.isValidHash(new HashMap<>(vo),venueInfoVO.getMerchantKey())){
            return PPErrorCodeEnum.CODE_5.toResVO(null);
        }


        UserCoinAddVO userCoinAddVO = getResultCoinAddVO(req, userInfoVO);

        BigDecimal totalAmount = getUserBalance(userInfoVO.getUserId());
        //4. 去重操作
        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());
        userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinRecordVo.setOrderNo(req.getRoundId());
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
        if (CollUtil.isNotEmpty(userCoinRecords)) {
            log.error("{} 派奖失败, 当前reference已经被处理, 请求参数: {}", VenueEnum.PP.getVenueCode(), req);

            return PPErrorCodeEnum.CODE_0.toResVO(PPResultResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(totalAmount)
                    .bonus(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());

        }

        if (StrUtil.isNotEmpty(req.getBonusCode())){
            log.info("免费旋转最后结算派奖, bonusCode: {}, 派奖金额: {}", req.getBonusCode(), req.getAmount());
            PPFreeGameRecordReqVO sendVO = new PPFreeGameRecordReqVO();
            sendVO.setOrderNo(req.getBonusCode());
            sendVO.setUserId(userInfoVO.getUserId());
            sendVO.setAcquireNum(0);
            sendVO.setBetId(req.getReference());
            //中奖金额
            sendVO.setPayOutAmount(req.getAmount());
            //NOTE 免费旋转增加描述字段.
            userCoinAddVO.setDescInfo("pp电子-平台赠送");

            KafkaUtil.send(TopicsConstants.FREE_GAME_RECORD_CONSUME, sendVO);
        }else {
            userCoinAddVO.setDescInfo("pp电子-游戏获得");
        }

        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);
        return switch (recordResultVO.getResultStatus()) {
            case FAIL, WALLET_NOT_EXIST -> PPErrorCodeEnum.CODE_100.toResVO(null);
            case INSUFFICIENT_BALANCE -> PPErrorCodeEnum.CODE_1.toResVO(null);
            case AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> PPErrorCodeEnum.CODE_0.toResVO(PPResultResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(totalAmount)
                    .bonus(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
            default -> PPErrorCodeEnum.CODE_0.toResVO(PPResultResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(recordResultVO.getCoinAfterBalance())
                    .bonus(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
        };
    }

    //NOTE 3.8 BonusWin
    public Object bonusWin(JSONObject vo) {


        PPBonusWinReqVO req = vo.to(PPBonusWinReqVO.class);
        if (!req.isValid()) {
            log.error("{} bonusWin但参数不全, result:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_7.toResVO(null);
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("{} bonusWin casinoMember不存在, result, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} bonusWin userInfoVO不存在, result, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());

        if (StrUtil.isNotEmpty(userInfoVO.getAccountStatus()) && userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} bonusWin 但会员被锁定, userInfoVO:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
            return PPErrorCodeEnum.CODE_6.toResVO(null);
        }
        //NOTE bonusWin 不受场馆状态影响
//        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.PP.getVenueCode(), null);
        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP, null);
        if (!HashValidatorUtils.isValidHash(new HashMap<>(vo),venueInfoVO.getMerchantKey())){
            return PPErrorCodeEnum.CODE_5.toResVO(null);
        }

        //NOTE 目前不作处理, 派奖已经在result中处理了.
        /*UserCoinAddVO userCoinAddVO = getBonusWinCoinAddVO(req, userInfoVO);
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());

        CoinRecordResultVO recordResultVO = userCoinApi.addCoin(userCoinAddVO);

        return switch (recordResultVO.getResultStatus()) {
            case FAIL, WALLET_NOT_EXIST -> PPErrorCodeEnum.CODE_100.toResVO(PPBonusWinResVO.builder()
                    .currency(userInfoVO.getMainCurrency())
                    .cash(userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build()).getTotalAmount())
                    .bonus(BigDecimal.ZERO)
                    .transactionId(null)
                    .build());
            case AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> PPErrorCodeEnum.CODE_0.toResVO(PPBetResVO.builder()
                    .currency(userInfoVO.getMainCurrency())
                    .cash(userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build()).getTotalAmount())
                    .bonus(BigDecimal.ZERO)
                    .transactionId(RandomUtil.randomNumbers(10))
                    .build());
            default -> PPErrorCodeEnum.CODE_0.toResVO(PPBonusWinResVO.builder()
                    .currency(userInfoVO.getMainCurrency())
                    .cash(recordResultVO.getCoinAfterBalance())
                    .bonus(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
        };*/

        /*if (StrUtil.isNotEmpty(req.getBonusCode())){
            log.info("免费旋转最后结算派奖, bonusCode: {}, 派奖金额: {}", req.getBonusCode(), req.getAmount());


        }*/

        return PPErrorCodeEnum.CODE_0.toResVO(PPBonusWinResVO.builder()
                .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                .cash(userCenterCoin.getTotalAmount())
                .bonus(BigDecimal.ZERO)
                .transactionId(req.getReference())
                .build());

    }

    //NOTE 3.9 JackpotWin
    public Object jackpotWin(JSONObject vo) {

        PPJackpotWinReqVO req = vo.to(PPJackpotWinReqVO.class);
        if (!req.isValid()) {
            log.error("{} jackpotWin但参数不全, jackpotWin:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_7.toResVO(null);
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("{} jackpotWin casinoMember不存在, jackpotWin, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} jackpotWin userInfoVO不存在, jackpotWin, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());

        if (StrUtil.isNotEmpty(userInfoVO.getAccountStatus()) && userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} jackpotWin但会员被锁定, userInfoVO:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
            return PPErrorCodeEnum.CODE_6.toResVO(null);
        }
//        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.PP.getVenueCode(), null);
        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP, null);
        if (!HashValidatorUtils.isValidHash(new HashMap<>(vo),venueInfoVO.getMerchantKey())){
            return PPErrorCodeEnum.CODE_5.toResVO(null);
        }

        UserCoinAddVO userCoinAddVO = getJackpotWinCoinAddVO(req, userInfoVO);

        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);


        return switch (recordResultVO.getResultStatus()) {
            case FAIL, WALLET_NOT_EXIST -> PPErrorCodeEnum.CODE_100.toResVO(null);
            case AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> PPErrorCodeEnum.CODE_0.toResVO(PPJackpotWinResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(userCenterCoin.getTotalAmount())
                    .bonus(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
            default -> PPErrorCodeEnum.CODE_0.toResVO(PPJackpotWinResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(recordResultVO.getCoinAfterBalance())
                    .bonus(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
        };
    }

    //NOTE 3.10 EndRound
    public Object endRound(JSONObject vo) {

        return PPErrorCodeEnum.CODE_0.toResVO(PPBaseResVO.builder().build());
    }

    //NOTE 3.11 refund
    public Object refund(JSONObject vo) {

        PPRefundReqVO req = vo.to(PPRefundReqVO.class);
        if (!req.isValid()) {
            log.error("{} refund但参数不全, refund:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_7.toResVO(null);
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("{} refund casinoMember不存在, refund, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} refund userInfoVO不存在, refund, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        if (StrUtil.isNotEmpty(userInfoVO.getAccountStatus()) && userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} refund 但会员被锁定, userInfoVO:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
            return PPErrorCodeEnum.CODE_6.toResVO(null);
        }

//        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.PP.getVenueCode(), null);
        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP, null);
        if (!HashValidatorUtils.isValidHash(new HashMap<>(vo),venueInfoVO.getMerchantKey())){
            return PPErrorCodeEnum.CODE_5.toResVO(null);
        }

        //4. 去重操作
        UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
        userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
        userCoinRecordVo.setUserId(userInfoVO.getUserId());
        userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinRecordVo.setOrderNo(req.getReference());
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
        if (CollUtil.isEmpty(userCoinRecords)) {
            log.error("{} refund失败, 当前Reference无法找到, 请求参数: {}", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_0.toResVO(PPRefundResVO.builder()
                    .transactionId(req.getReference())
                    .build());
        }

        UserCoinAddVO userCoinAddVO = getRefundCoinAddVO(req, userInfoVO);

        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

        return switch (recordResultVO.getResultStatus()) {
            case FAIL, WALLET_NOT_EXIST -> PPErrorCodeEnum.CODE_100.toResVO(null);
            /*case AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> PPErrorCodeEnum.CODE_0.toResVO(PPRefundResVO.builder()
                    .transactionId(req.getReference())
                    .build());*/
            default -> PPErrorCodeEnum.CODE_0.toResVO(PPRefundResVO.builder()
                    .transactionId(req.getReference())
                    .build());
        };
    }

    //NOTE 3.13 PromoWin 关FRB 作为奖品掉落或锦标赛的奖金
    public Object promoWin(JSONObject vo) {

        PPPromoWinReqVO req = vo.to(PPPromoWinReqVO.class);
        if (!req.isValid()) {
            log.error("{} promoWin但参数不全, refund:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_7.toResVO(null);
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("{} promoWin casinoMember不存在, refund, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} promoWin userInfoVO不存在, refund, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (StrUtil.isNotEmpty(userInfoVO.getAccountStatus()) && userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} promoWin 但会员被锁定, userInfoVO:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
            return PPErrorCodeEnum.CODE_6.toResVO(null);
        }

        if (venueMaintainClosed(VenuePlatformConstants.PP,userInfoVO.getSiteCode())) {
            return PPErrorCodeEnum.CODE_8.toResVO(null);

        }
        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP, null);
        if (!HashValidatorUtils.isValidHash(new HashMap<>(vo),venueInfoVO.getMerchantKey())){
            return PPErrorCodeEnum.CODE_5.toResVO(null);
        }

        UserCoinAddVO userCoinAddVO = getPromoWinCoinAddVO(req, userInfoVO);

        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);


        return switch (recordResultVO.getResultStatus()) {
            case FAIL, WALLET_NOT_EXIST -> PPErrorCodeEnum.CODE_100.toResVO(null);
            case AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> PPErrorCodeEnum.CODE_0.toResVO(PPPromoWinResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(userCenterCoin.getTotalAmount())
                    .bonus(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
            default -> PPErrorCodeEnum.CODE_0.toResVO(PPPromoWinResVO.builder()
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(recordResultVO.getCoinAfterBalance())
                    .bonus(BigDecimal.ZERO)
                    .transactionId(req.getReference())
                    .build());
        };
    }

    //NOTE 3.15 Adjustment
    public Object adjustment(JSONObject vo) {

        PPAdjustmentReqVO req = vo.to(PPAdjustmentReqVO.class);
        if (!req.isValid()) {
            log.error("{} adjustment但参数不全, adjustment:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_7.toResVO(null);
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).casinoPassword(req.getToken()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null || casinoMember.getUserId() == null ) {
            log.error("{} casinoMember不存在, adjustment, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("{} userInfoVO不存在, adjustment, 参数:{} ", VenueEnum.PP.getVenueCode(), req);
            return PPErrorCodeEnum.CODE_2.toResVO(null);
        }

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
        if (StrUtil.isNotEmpty(userInfoVO.getAccountStatus()) && userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
            log.error("{} 游戏adjustment但会员被锁定, userInfoVO:{} ", VenueEnum.PP.getVenueCode(), userInfoVO);
            return PPErrorCodeEnum.CODE_6.toResVO(null);
        }

        if (venueMaintainClosed(VenuePlatformConstants.PP,userInfoVO.getSiteCode())) {
            return PPErrorCodeEnum.CODE_8.toResVO(null);

        }

        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP, null);
        if (!HashValidatorUtils.isValidHash(new HashMap<>(vo),venueInfoVO.getMerchantKey())){
            return PPErrorCodeEnum.CODE_5.toResVO(null);
        }


        UserCoinAddVO userCoinAddVO = getAdjustmentCoinAddVO(req, userInfoVO);

        if (req.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            userCoinAddVO.setCoinValue(req.getAmount());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        } else {
            userCoinAddVO.setCoinValue(req.getAmount().abs());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        }
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        BigDecimal totalAmount = userCenterCoin.getTotalAmount();
        if (CoinBalanceTypeEnum.EXPENSES.getCode().equals(userCoinAddVO.getBalanceType()) && totalAmount.compareTo(req.getAmount())<0){
            return PPErrorCodeEnum.CODE_1.toResVO(null);
        }

        CoinRecordResultVO recordResultVO = toUserCoinHandle(userCoinAddVO);

        return switch (recordResultVO.getResultStatus()) {
            case FAIL, WALLET_NOT_EXIST -> PPErrorCodeEnum.CODE_100.toResVO(null);
            case INSUFFICIENT_BALANCE -> PPErrorCodeEnum.CODE_1.toResVO(null);
            case AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> PPErrorCodeEnum.CODE_0.toResVO(PPAdjustmentResVO.builder()
                    .transactionId(req.getReference())
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(totalAmount)
                    .bonus(BigDecimal.ZERO)
                    .build());
            default -> PPErrorCodeEnum.CODE_0.toResVO(PPAdjustmentResVO.builder()
                    .transactionId(req.getReference())
                    .currency(PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency()).name())
                    .cash(recordResultVO.getCoinAfterBalance())
                    .bonus(BigDecimal.ZERO)
                    .build());
        };
    }

    //NOTE 6.3 创建免费回合  POST /FreeRoundsBonusAPI/v2/bonus/create/
    public ResponseVO<Boolean> giveFRB(PPFreeRoundGiveReqVO req){

        if (!req.isValid()){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }

        if (!VenueEnum.PP.getVenueCode().equalsIgnoreCase(req.getVenueCode())){
            return ResponseVO.fail(ResultCode.QUERY_GAME_VENUE_NOT_EXIST);
        }

        UserInfoVO userInfoVO = getByUserId(req.getUserId());
        if (userInfoVO == null) {
            log.error("pp giveFRB userInfoVO不存在, 参数:{} ", req);
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }

        if (venueMaintainClosed(VenuePlatformConstants.PP,userInfoVO.getSiteCode())) {
            return ResponseVO.fail(ResultCode.QUERY_GAME_VENUE_NOT_EXIST);
        }


        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP, null);
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).userId(req.getUserId()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.info("pp giveFRB casinoMember不存在, 创建, 参数:{} ", req);
            casinoMember = new CasinoMemberVO();
            casinoMember.setVenueUserAccount(venueUserAccountConfig.addVenueUserAccountPrefix(userInfoVO.getUserId()));

        }

        String url = venueInfoVO.getApiUrl() + "/IntegrationService/v3/http/FreeRoundsBonusAPI/v2/bonus/player/create/";

        //NOTE  货币转换
        PPCurrencyEnum byCode = PPCurrencyEnum.getByCode(userInfoVO.getMainCurrency());
        if (byCode==PPCurrencyEnum.UNKNOWN){
            return ResponseVO.fail(ResultCode.CURRENCY_NOT_MATCH);
        }

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", venueInfoVO.getMerchantNo());
        paramMap.put("bonusCode", req.getBonusCode());
        paramMap.put("startDate", req.getStartDate());
        paramMap.put("expirationDate", req.getExpirationDate());
        paramMap.put("validityDate", req.getExpirationDate() );
        paramMap.put("rounds", req.getRounds());
        paramMap.put("playerId", casinoMember.getVenueUserAccount());
        paramMap.put("currency", byCode.name());

        JSONArray array = new JSONArray();
        JSONObject temp = new JSONObject();
        temp.put("gameId", req.getGameId());
        JSONObject betValuesObj = new JSONObject();
        betValuesObj.put("currency", byCode.name());
        betValuesObj.put("totalBet", req.getBetPerLine());
        JSONArray betValuesArray = new JSONArray();
        betValuesArray.add(betValuesObj);

        temp.put("betValues", betValuesArray);

        array.add(temp);
        String hashStr = "";

        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, venueInfoVO.getMerchantKey());
        } catch (Exception e) {
            log.error("{} PP giveFRB时Hash发生错误, 参数: {}", VenueEnum.PP.getVenueName(), paramMap);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        Map<String, Object> gameListMap = Maps.newHashMap();

        gameListMap.put("gameList", array);

        paramMap.put("hash", hashStr);
        url+="?secureLogin="+ venueInfoVO.getMerchantNo();
        url+="&bonusCode="+ req.getBonusCode();
        url+="&startDate="+req.getStartDate();
        url+="&expirationDate="+req.getExpirationDate();
        url+="&validityDate="+ req.getExpirationDate();
        url+="&rounds="+ req.getRounds();
        url+="&playerId=" + casinoMember.getVenueUserAccount();
        url+="&currency=" + byCode.name();
        url+="&hash="+hashStr;

        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap)
                .body(JSON.toJSONString(gameListMap))
                .execute())
        {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object error = jsonObject.getString("error");
                if ("0".equals(error)) {
                    return ResponseVO.success(true);
                }else {
                    log.error("PP giveFRB发生失败, 参数:{}, 返回:{}", url, response);
                }
            }else {
                log.error("PP giveFRB发生异常, 参数:{}, 返回:{}", url, response);
            }
        } catch (Exception e) {
            log.error("{} giveFRB发生错误!", VenueEnum.PP.getVenueName(), e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
    }

    //NOTE 6.4 取消免费回合  POST /FreeRoundsBonusAPI/v2/bonus/cancel/
    public ResponseVO<Boolean> cancelFRB(PPFreeRoundCancelReqVO req){

        if (!req.isValid()){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }

        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("pp cancelFRB casinoMember不存在, 参数:{} ", req);
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("pp cancelFRB userInfoVO不存在, 参数:{} ", req);
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }

        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.PP.getVenueCode(), null);
        if (venueInfoVO == null) {
            return ResponseVO.fail(ResultCode.QUERY_GAME_VENUE_NOT_EXIST);
        }


        String url = venueInfoVO.getApiUrl() + "/IntegrationService/v3/http/FreeRoundsBonusAPI/v2/bonus/cancel/";

        Map<String, Object> paramMap = Maps.newHashMap();

        paramMap.put("secureLogin", venueInfoVO.getMerchantNo());
        paramMap.put("bonusCode", req.getBonusCode());

        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, venueInfoVO.getMerchantKey());
        } catch (Exception e) {
            log.error("{} PP cancelFRB 时Hash发生错误, 参数: {}", VenueEnum.PP.getVenueName(), paramMap);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        paramMap.put("hash", hashStr);

        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap)
                .execute())
        {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                if (PPErrorCodeEnum.CODE_0.getCode().equals(jsonObject.getString("error"))) {
                    return ResponseVO.success(true);
                }
            }
        } catch (Exception e) {
            log.error("{} cancelFRB 发生错误!!", VenueEnum.PP.getVenueName(), e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);

    }

    //NOTE 6.5 获取玩家FRB  POST /FreeRoundsBonusAPI/getPlayersFRB/
    public ResponseVO<List<PPFreeRoundResVO>> getPlayersFRB(PPFreeRoundGetReqVO req){

        if (!req.isValid()){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }

        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.PP.getVenueCode()).venueUserAccount(req.getUserId()).build();
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null) {
            log.error("pp getPlayersFRB casinoMember不存在, 参数:{} ", req);
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }

        UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
        if (userInfoVO == null) {
            log.error("pp getPlayersFRB userInfoVO不存在, 参数:{} ", req);
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }

        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.PP.getVenueCode(), null);
        if (venueInfoVO == null) {
            return ResponseVO.fail(ResultCode.QUERY_GAME_VENUE_NOT_EXIST);
        }

        String url = venueInfoVO.getApiUrl() + "/IntegrationService/v3/http/FreeRoundsBonusAPI/getPlayersFRB/";

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", venueInfoVO.getMerchantNo());
        paramMap.put("playerId", casinoMember.getVenueUserAccount());

        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, venueInfoVO.getMerchantKey());
        } catch (Exception e) {
            log.error("{} PP getPlayersFRB 时Hash发生错误, 参数: {}", VenueEnum.PP.getVenueName(), paramMap);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        paramMap.put("hash", hashStr);

        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap)
                .execute())
        {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                if (PPErrorCodeEnum.CODE_0.getCode().equals(jsonObject.getString("error"))) {
                    JSONArray ds = jsonObject.getJSONArray("ds");
                    return ResponseVO.success(ds.toList(PPFreeRoundResVO.class));
                }else {
                    log.error("PP getPlayersFRB 发生失败, 参数:{}, 返回:{}", req, response);
                }
            }else {
                log.error("PP getPlayersFRB 发生异常, 参数:{}, 返回:{}", req, response);
            }
        } catch (Exception e) {
            log.error("{} getPlayersFRB 发生错误!!", VenueEnum.PP.getVenueName(), e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);

    }

    //NOTE 6.10 获得赌注范围
    public ResponseVO<List<PPGameLimitResVO>> getLimitGameLine(PPGameLimitReqVO req) {

        if (!req.isValid()){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }

        VenueInfoVO venueInfoVO = getVenueInfo(VenueEnum.PP.getVenueCode(), null);
        if (venueInfoVO == null || !VenueEnum.PP.getVenueCode().equals(req.getVenueCode())) {
            return ResponseVO.fail(ResultCode.QUERY_GAME_VENUE_NOT_EXIST);
        }

        String url = venueInfoVO.getApiUrl() + "/IntegrationService/v3/http/CasinoGameAPI/getBetScales";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", venueInfoVO.getMerchantNo());
        if (StrUtil.isNotEmpty(req.getGameIds())) {
            paramMap.put("gameIDs", req.getGameIds());
        }
        if (StrUtil.isNotEmpty(req.getCurrencies())) {
            String currencies = req.getCurrencies();

            String[] split = currencies.split(",");
            List<String> newCoin = new ArrayList<>();
            for (String coinStr : split) {
                PPCurrencyEnum byCode = PPCurrencyEnum.getByCode(coinStr);
                if (byCode==PPCurrencyEnum.UNKNOWN){
                    return ResponseVO.fail(ResultCode.VENUE_CURRENCY_NOT);
                }
                newCoin.add(byCode.name());
            }
            if (CollUtil.isNotEmpty(newCoin)){
                paramMap.put("currencies", String.join(",", newCoin));

            }
        }
        log.info("{} PP获得赌注范围, url: {}, 参数: {}", VenueEnum.PP.getVenueName(), url, JSON.toJSONString(paramMap));
        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, venueInfoVO.getMerchantKey());
        } catch (Exception e) {
            log.error("{} PP游戏获得赌注范围Hash发生错误, 参数: {}, 签名: {}", VenueEnum.PP.getVenueName(), paramMap, hashStr);
        }
        paramMap.put("hash", hashStr);
        JSONArray jsonArrayBack = new JSONArray();
        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap).execute()) {
            System.out.println(JSON.toJSONString(response.body()));
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object error = jsonObject.getString("error");
                if ("0".equals(error)) {
                    JSONArray gameList = jsonObject.getJSONArray("gameList");
                    List<PPGameLimitResVO> list = new ArrayList<>();
                    for (Object object : gameList) {
                        JSONObject oneObj = JSON.to(JSONObject.class, object);
                        PPGameLimitResVO gameLimitResVO = PPGameLimitResVO.builder().gameId(oneObj.getString("gameID")).build();
                        List<PPGameLimitCurrencyResVO> currencyGameLimits = new ArrayList<>();
                        JSONArray betScaleList = oneObj.getJSONArray("betScaleList");
                        for (Object obj : betScaleList) {
                            JSONObject betScale = JSON.to(JSONObject.class, obj);
                            PPGameLimitCurrencyResVO build = PPGameLimitCurrencyResVO.builder().currency(betScale.getString("currency")).build();

                            JSONArray betPerLineScales = betScale.getJSONArray("totalBetScales");
                            build.setBetPerLineMin(new BigDecimal(betPerLineScales.get(0).toString()));
                            build.setBetPerLineMax(new BigDecimal(betPerLineScales.get(betPerLineScales.size()-1).toString()));
                            build.setBetPerLineScales(betPerLineScales);

                            currencyGameLimits.add(build);
                        }

                        gameLimitResVO.setCurrencyGameLimits(currencyGameLimits);

                        list.add(gameLimitResVO);
                    }
                    return ResponseVO.success(list);
                }else {
                    log.error("PP 游戏获得赌注范围失败, 参数:{}, 返回:{}", req, response);
                }
            }else {
                log.error("PP 游戏获得赌注范围发生异常, 参数:{}, 返回:{}", req, response);
            }
        } catch (Exception e) {
            log.error("PP 游戏获得赌注范围发生错误!", e);
        }
        return ResponseVO.fail(ResultCode.SYSTEM_ERROR);

    }

    @NotNull
    private static UserCoinAddVO getRefundCoinAddVO(PPRefundReqVO req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getReference());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PP);
        userCoinAddVO.setThirdOrderNo(req.getRoundId());
        return userCoinAddVO;
    }

    @NotNull
    private static UserCoinAddVO getPromoWinCoinAddVO(PPPromoWinReqVO req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getReference());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        if (StrUtil.isNotEmpty(req.getRoundId())){
            userCoinAddVO.setRemark(req.getRoundId());
        }else if (StrUtil.isNotEmpty(req.getCampaignId())){
            userCoinAddVO.setRemark(req.getRoundId());
        }
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PP);
        userCoinAddVO.setThirdOrderNo(req.getRoundId());
        return userCoinAddVO;
    }

    @NotNull
    private static UserCoinAddVO getAdjustmentCoinAddVO(PPAdjustmentReqVO req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(req.getReference());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PP);
        userCoinAddVO.setThirdOrderNo(req.getRoundId());
        return userCoinAddVO;
    }

    @NotNull
    private static UserCoinAddVO getJackpotWinCoinAddVO(PPJackpotWinReqVO req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getReference());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PP);
        userCoinAddVO.setThirdOrderNo(req.getRoundId());
        return userCoinAddVO;
    }

    @NotNull
    private static UserCoinAddVO getBonusWinCoinAddVO(PPBonusWinReqVO req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getReference());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getRoundId());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PP);
        userCoinAddVO.setThirdOrderNo(req.getRoundId());
        return userCoinAddVO;
    }


    @NotNull
    private static UserCoinAddVO getResultCoinAddVO(PPResultReqVO req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getRoundId());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setRemark(req.getReference());

        userCoinAddVO.setCoinValue(req.getAmount());
        if (req.getPromoWinAmount()!=null){
            userCoinAddVO.setCoinValue(userCoinAddVO.getCoinValue().add(req.getPromoWinAmount()));
        }

        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PP);
        userCoinAddVO.setThirdOrderNo(req.getReference());
        return userCoinAddVO;
    }

    @NotNull
    private static UserCoinAddVO getBetCoinAddVO(PPBetReqVO req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setOrderNo(req.getRoundId());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getAmount());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getReference());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.ACE);
        userCoinAddVO.setThirdOrderNo(req.getReference());
        return userCoinAddVO;
    }

    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        JSONObject player = createPlayer(venueInfoVO, casinoMemberVO);

        if (player == null || StrUtil.isEmpty(player.getString("playerId"))) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        casinoMemberVO.setVenueUserId(player.getString("playerId"));

        return ResponseVO.success(Boolean.TRUE);
    }

    /**
     * 3.1.2 GameURL API 方法
     * 请求路径：POST /game/url
     * 通过这种方法，运营商可以接收到所请求游戏的有效启动URL
     */
    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {

        PPLangCodeEnum ppLangCodeEnum = PPLangCodeEnum.fromPlatCode(loginVO.getLanguageCode());
        String url = GetGameURL(loginVO, venueInfoVO, casinoMemberVO, ppLangCodeEnum.getCode());

        //get free rounds
        //ExecutorService executorService = Executors.newSingleThreadExecutor();

        //executorService.submit(() -> giveFreeSqu( loginVO,  venueInfoVO,  casinoMemberVO, 10));

        //executorService.shutdown();

        GameLoginVo gameLoginVo = GameLoginVo.builder()
                .source(url).userAccount(loginVO.getUserAccount())
                .venueCode(VenueEnum.PP.getVenueCode())
                .type(GameLoginTypeEnums.URL.getType()).build();

        return ResponseVO.success(gameLoginVo);
    }

    @Override
    public ResponseVO<PPPullBetParams> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {

        Map<String, UserInfoVO> userInfoMap = new HashMap<>();
        Map<String, String> siteNameMap = getSiteNameMap();
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueInfoVO.getVenueCode());

        JSONArray transactions = getTransactionsByCSV(venueInfoVO, venuePullParamVO, venueInfoVO.getBetUrl());
        JSONArray transactionsTwo = getTransactionsByCSV(venueInfoVO, venuePullParamVO, venueInfoVO.getGameUrl());
        transactions.addAll(transactionsTwo);

        List<OrderRecordVO> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(transactions)) {

            transactions.forEach(obj -> {
                JSONObject jsonObj = (JSONObject) obj;

                String userVenueAccount = jsonObj.getString("extPlayerID");
                UserInfoVO userInfoVO = userInfoMap.get(userVenueAccount);
                if (userInfoVO == null) {
                    CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(CasinoMemberReq.builder()
                            .venueUserAccount(userVenueAccount).venueCode(venueInfoVO.getVenueCode()).build());
                    if (casinoMember == null) {
                        log.info("{} 三方关联账号不存在, 账号: {} ", venueInfoVO.getVenueCode(), userVenueAccount);
                        return;
                    }
                    userInfoVO = getByUserId(casinoMember.getUserId());
                    userInfoMap.put(userVenueAccount, userInfoVO);
                }
                OrderRecordVO recordVO = new OrderRecordVO();
                recordVO.setUserAccount(userInfoVO.getUserAccount());
                recordVO.setUserId(userInfoVO.getUserId());
                recordVO.setUserName(userInfoVO.getUserName());
                recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
                recordVO.setAgentId(userInfoVO.getSuperAgentId());
                recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
                recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
                String transactionId = jsonObj.getString("playSessionID");

                recordVO.setBetAmount(jsonObj.getBigDecimal("bet"));
                //NOTE 投注时间按开始时间算. "2017-08-28 02:14:13"
                if (StrUtil.isNotEmpty(jsonObj.getString("startDate"))) {
                    LocalDateTime utcTime = LocalDateTime.parse(jsonObj.getString("startDate").replace(" ", "T"));
                    recordVO.setBetTime(utcTime.toInstant(ZoneOffset.UTC).toEpochMilli());

                }

                recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
                recordVO.setVenueCode(venueInfoVO.getVenueCode());
                recordVO.setVenueType(VenueEnum.PP.getType().getCode());

                recordVO.setCasinoUserName(userVenueAccount);
                recordVO.setBetIp(userInfoVO.getLastLoginIp());
                recordVO.setCurrency(userInfoVO.getMainCurrency());
                recordVO.setOrderId(OrderUtil.getGameNo());
                recordVO.setThirdOrderId(jsonObj.getString("playSessionID"));
                recordVO.setPayoutAmount(jsonObj.getBigDecimal("win"));
                //NOTE 中奖减投注
                recordVO.setWinLossAmount(recordVO.getPayoutAmount().subtract(recordVO.getBetAmount()));
                recordVO.setCreatedTime(System.currentTimeMillis());
                recordVO.setUpdatedTime(System.currentTimeMillis());

                //NOTE 结算时间 yyyy-mm-dd HH:mm:ss
                if (StrUtil.isNotEmpty(jsonObj.getString("endDate"))) {
                    LocalDateTime utcTime = LocalDateTime.parse(jsonObj.getString("endDate").replace(" ", "T"));
                    recordVO.setSettleTime(utcTime.toInstant(ZoneOffset.UTC).toEpochMilli());
                }
                //NOTE 状态
                recordVO.setOrderStatus(getOrderStatus(jsonObj.getString("status")).getCode());
                recordVO.setOrderClassify(getOrderClassifyStatus(jsonObj.getString("status")).getCode());
                recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
                recordVO.setChangeStatus(0);
                recordVO.setReSettleTime(0L);
                recordVO.setParlayInfo(jsonObj.toString());
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
                recordVO.setVipRank(userInfoVO.getVipRank());
                recordVO.setValidAmount(jsonObj.getBigDecimal("bet"));
                recordVO.setThirdGameCode(jsonObj.getString("gameID"));

                recordVO.setOrderInfo(recordVO.getThirdOrderId());

                if (StrUtil.isNotEmpty(jsonObj.getString("bonusCode")) && !jsonObj.getString("bonusCode").equals("null")){
                    recordVO.setEventInfo(jsonObj.getString("bonusCode"));
                    recordVO.setExId1(BetGameTypeEnum.FREE_SPIN.getCode());
                }

                recordVO.setGameCode(jsonObj.getString("gameID"));
                //NOTE  局号
                if (Objects.isNull(jsonObj.get("parentSessionID"))||jsonObj.get("parentSessionID").equals("null")){
                    recordVO.setGameNo(jsonObj.getString("playSessionID"));
                }else {
                    recordVO.setGameNo(jsonObj.getString("parentSessionID"));
                    transactionId = jsonObj.getString("parentSessionID");
                }
                recordVO.setTransactionId(transactionId);
                GameInfoPO gameInfoPO = paramToGameInfo.get(recordVO.getGameCode());
                if (gameInfoPO != null) {
                    recordVO.setGameId(gameInfoPO.getGameId());
                    recordVO.setGameName(gameInfoPO.getGameI18nCode());
                }
                list.add(recordVO);
            });
            List<String> userIdList = list.stream().map(OrderRecordVO::getUserId).toList();
            Map<String, UserLoginInfoVO> loginInfoByUserIds = getLoginInfoByUserIds(userIdList);
            list.forEach(orderRecordVO -> {
                UserLoginInfoVO userLoginInfoVO = loginInfoByUserIds.get(orderRecordVO.getUserId());
                if (userLoginInfoVO != null) {
                    orderRecordVO.setBetIp(userLoginInfoVO.getIp());
                    if (userLoginInfoVO.getLoginTerminal() != null) {
                        orderRecordVO.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
                    }
                }
            });
            if (CollUtil.isNotEmpty(list)){
                orderRecordProcessService.orderProcess(list);
            }
            log.info("PP电子, getBetRecordList拉单结束, 共拉单: {}条", list.size());
        }
        return ResponseVO.success();

    }


    private OrderStatusEnum getOrderStatus(String name) {

        return switch (PPGameStatusEnum.valueOf(name)) {
            case C -> OrderStatusEnum.SETTLED;
            case I -> OrderStatusEnum.NOT_SETTLE;
            case F -> OrderStatusEnum.CANCEL;

            default -> OrderStatusEnum.ABERRANT;

        };
    }

    private ClassifyEnum getOrderClassifyStatus(String name) {

        return switch (PPGameStatusEnum.valueOf(name)) {
            case C -> ClassifyEnum.SETTLED;
            //case I -> ClassifyEnum.NOT_SETTLE;
            case F -> ClassifyEnum.CANCEL;

            default -> ClassifyEnum.NOT_SETTLE;

        };
    }

    //NOTE 8.3 游戏内交易 GET/DataFeeds/transactions/
    private JSONArray getTransactionsByCSV(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO, String url) {

        if (StrUtil.isEmpty(url)) {
            return new JSONArray();
        }

        url = url + "/IntegrationService/v3/DataFeeds/gamerounds/finished/?";

        log.info("{} getTransactionsByCSV游戏注单, url: {}, timepoint参数: {}", VenueEnum.PP.getVenueName(), url, venuePullParamVO.getStartTime());
        url += "login=" + venueInfoVO.getMerchantNo();
        url += "&password=" + venueInfoVO.getMerchantKey();
        url += "&timepoint=" + venuePullParamVO.getStartTime();
        url += "&options=addRoundDetails";

        try (HttpResponse response = HttpRequest.get(url)
                .header(Header.CONTENT_TYPE, "text/csv")
                .execute()) {
            if (response.isOk()) {
                return CsvParserUtil.parseCsv(response.body());
            }else {
                log.error("{} getTransactionsByCSV返回错误, 请求返回 {}", VenueEnum.PP.getVenueName(), response);
            }
        } catch (Exception e) {
            log.error("{} getTransactionsByCSV发生错误!!", VenueEnum.PP.getVenueName(), e);
        }
        return new JSONArray();

    }

    //NOTE 4.4 CreatePlayer
    public JSONObject createPlayer(VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        String url = venueInfoVO.getApiUrl() + "/IntegrationService/v3/http/CasinoGameAPI/player/account/create";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", venueInfoVO.getMerchantNo());
        paramMap.put("externalPlayerId", casinoMemberVO.getVenueUserAccount());
        paramMap.put("currency", PPCurrencyEnum.getByCode(casinoMemberVO.getCurrencyCode()).name());
        log.info("PP createPlayer游戏玩家, url: {}, 参数: {}", url, JSON.toJSONString(paramMap));
        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, venueInfoVO.getMerchantKey());
        } catch (Exception e) {
            log.error("{} createPlayer获取游戏列表Hash发生错误, 参数: {}, 签名: {}", VenueEnum.PP.getVenueName(), paramMap, hashStr);
        }
        paramMap.put("hash", hashStr);

        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap).execute()) {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object error = jsonObject.get("error");
                if ("0".equals(error)) {
                    return jsonObject;
                } else {
                    log.error("{} CreatePlayer返回错误, 请求返回body: {}", VenueEnum.PP.getVenueName(), response.body());
                }
            } else {
                log.error("{} CreatePlayer返回错误, 请求返回 {}", VenueEnum.PP.getVenueName(), response);
            }
        } catch (Exception e) {
            log.error("PP CreatePlayer发生错误!", e);
        }
        return new JSONObject();
    }


    public List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();

        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PP,null);
        //平台语言.
        String langCode = CurrReqUtils.getLanguage();

        PPLangCodeEnum ppLangCodeEnum = PPLangCodeEnum.fromPlatCode(langCode);

        JSONArray jsonArray = GetCasinoGames(venueInfoVO, ppLangCodeEnum.getCode());

        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                JSONObject gameJson = new JSONObject();
//                gameJson.put("deskName", jsonObject.get("gameName"));
//                gameJson.put("deskNumber", jsonObject.get("gameID"));
                ShDeskInfoVO info = ShDeskInfoVO.builder().deskName(jsonObject.getString("gameName")).deskNumber(jsonObject.getString("gameID")).build();
                resultList.add(info);
            }
            return resultList;
        }
        return resultList;
    }

    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {

        List<JSONObject> resultList = Lists.newArrayList();
        //平台语言.
        String langCode = CurrReqUtils.getLanguage();

        PPLangCodeEnum ppLangCodeEnum = PPLangCodeEnum.fromPlatCode(langCode);

        JSONArray jsonArray = GetCasinoGames(venueInfoVO, ppLangCodeEnum.getCode());

        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject gameJson = new JSONObject();
                gameJson.put("deskName", jsonObject.get("gameName"));
                gameJson.put("deskNumber", jsonObject.get("gameID"));
                resultList.add(gameJson);
            }
            log.info("{} 获取游戏列表列表成功, 游戏数量: {}", VenueEnum.PP.getVenueName(), 0);
            return ResponseVO.success(resultList);
        }
        return ResponseVO.success(resultList);
    }

    //NOTE 2.1 GetCasinoGames
    public JSONArray GetCasinoGames(@NotNull VenueInfoVO venueInfoVO, String lang) {
        String url = venueInfoVO.getApiUrl() + "/IntegrationService/v3/http/CasinoGameAPI/getCasinoGames";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", venueInfoVO.getMerchantNo());
        log.info("{} GetCasinoGames游戏列表, url: {}, 参数: {}", VenueEnum.PP.getVenueName(), url, JSON.toJSONString(paramMap));
        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, venueInfoVO.getMerchantKey());
        } catch (Exception e) {
            System.out.println(e);
            log.error("{} 获取游戏列表Hash发生错误, 参数: {}", VenueEnum.PP.getVenueName(), paramMap);
        }
        paramMap.put("hash", hashStr);
        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap).execute()) {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object error = jsonObject.get("error");
                if ("0".equals(error)) {
                    return jsonObject.getJSONArray("gameList");
                } else {
                    log.error("{} GetCasinoGames返回错误, 请求返回 {}", VenueEnum.PP.getVenueName(), response.body());
                }
            } else {
                log.error("{} GetCasinoGames请求错误, 返回 {}", VenueEnum.PP.getVenueName(), response);
            }
        } catch (Exception e) {
            log.error("{} GetCasinoGames获取游戏列表发生错误!", VenueEnum.PP.getVenueName(), e);
        }
        return new JSONArray();
    }

    //NOTE 3.1.2 GameURL API 方法
    public String GetGameURL(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO, String lang) {
        String url = venueInfoVO.getApiUrl() + "/IntegrationService/v3/http/CasinoGameAPI/game/url";

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("secureLogin", venueInfoVO.getMerchantNo());
        paramMap.put("symbol", loginVO.getGameCode());
        paramMap.put("language", lang);
        paramMap.put("token", casinoMemberVO.getCasinoPassword());
        paramMap.put("externalPlayerId", casinoMemberVO.getVenueUserAccount());
        //paramMap.put("lobbyUrl", "https://www.google.com");

        log.info("{} GetGameURL游戏列表, url: {}, 参数: {}", VenueEnum.PP.getVenueName(), url, JSON.toJSONString(paramMap));
        String hashStr = "";
        try {
            hashStr = HashValidatorUtils.calculateHash(paramMap, venueInfoVO.getMerchantKey());
        } catch (Exception e) {
            log.error("{} GetGameURL获取游戏链接Hash发生错误, 参数: {}", VenueEnum.PP.getVenueName(), paramMap);
        }
        paramMap.put("hash", hashStr);
        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .form(paramMap).execute()) {
            if (response.isOk()) {
                JSONObject jsonObject = JSON.parseObject(response.body());
                Object error = jsonObject.get("error");
                if ("0".equals(error)) {
                    return jsonObject.getString("gameURL");
                } else {
                    log.error("{} GetGameURL返回错误, 请求返回body: {}", VenueEnum.PP.getVenueName(), response.body());
                }
            } else {
                log.error("{} GetGameURL返回错误, 请求返回 {}", VenueEnum.PP.getVenueName(), response);
            }
        } catch (Exception e) {
            log.error("{} 获取游戏URL发生异常, 参数: {}", VenueEnum.PP.getVenueName(), paramMap, e);
        }
        return "";
    }

}
