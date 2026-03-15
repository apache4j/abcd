package com.cloud.baowang.play.game.fc.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.fc.req.*;
import com.cloud.baowang.play.api.vo.fc.res.FCBaseRes;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.fc.enums.FCCurrencyEnum;
import com.cloud.baowang.play.game.fc.enums.FCErrorCodeEnum;
import com.cloud.baowang.play.game.fc.enums.FCGameTypeEnum;
import com.cloud.baowang.play.game.fc.enums.FCLangCodeEnum;
import com.cloud.baowang.play.game.fc.utils.FcAESUtill;
import com.cloud.baowang.play.game.fc.vo.FCRecordVO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.CasinoMemberService;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.play.task.pulltask.fc.params.FCPullBetParams;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.FC)
@AllArgsConstructor
public class FCGamServiceImpl extends GameBaseService implements GameService {

    private final OrderRecordProcessService orderRecordProcessService;
    private final CasinoMemberService casinoMemberService;
    private final UserInfoApi userInfoApi;
    private final GameInfoService gameInfoService;

    private final UserCoinRecordApi userCoinRecordApi;

    private final VenueUserAccountConfig venueUserAccountConfig;

    private final UserCoinApi userCoinApi;
    private final VenueInfoService venueInfoService;

    static String AGENT_CODE = "AgentCode";
    static String CURRENCY = "Currency";
    static String PARAMS = "Params";
    static String SIGN = "Sign";
    static String CONTENT_TYPE_ENCODED = "application/x-www-form-urlencoded";
    static String CONTENT_TYPE = "application/json";

    static String GET_GAME_ICON_LIST = "GetGameIconList";


    /**
     * 4-1、取得余额 (GetBalance)
     */
    public FCBaseRes GetBalance(FCBaseReq req) {

        try {
            log.info("FC GetBalance: {}", JSON.toJSONString(req));

            if (StrUtil.isEmpty(req.getCurrency())) {
                return FCErrorCodeEnum.CODE_604.toResVO();
            }
            FCCurrencyEnum fsCurrencyEnum = FCCurrencyEnum.valueOf(req.getCurrency());
            VenueInfoVO venueAdminInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.FC.getVenueCode(), fsCurrencyEnum.getCode());
            //NOTE 验证MD5
            String aesDecrypt = FcAESUtill.aesDecrypt(req.getParams(), venueAdminInfoVO.getMerchantKey());
            if (StrUtil.isEmpty(aesDecrypt)) {
                log.error("FC getBalance 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            GetBalanceReq vo = JSON.parseObject(aesDecrypt, GetBalanceReq.class);
            if (vo == null) {
                log.error("FC getBalance 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            String acctId = vo.getMemberAccount();
            Integer gameID = vo.getGameID();
            if (StrUtil.isEmpty(acctId) || ObjectUtil.isEmpty(gameID)) {
                log.error("FC getBalance 但参数不全, vo:{} ", vo);
                return FCErrorCodeEnum.CODE_1098.toResVO();
            }
            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FC.getVenueCode()).venueUserAccount(acctId).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("FC getBalance casinoMember不存在, 参数:{} ", vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{}  getBalance userInfoVO不存在, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }

            if (userGameLock(userInfoVO)) {
                log.error("{} getBalance game lock, user参数:{} ", VenueEnum.FC.getVenueCode(), userInfoVO);
                return FCErrorCodeEnum.CODE_407.toResVO();
            }
            //NOTE 查询场馆状态
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FC.getVenueCode(), userInfoVO.getMainCurrency());
            if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                log.error("{}  getBalance venueInfoVO不存在或不开放, balance, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_408.toResVO();
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());

            return FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(userCenterCoin.getTotalAmount()).build());
        } catch (Exception e) {
            log.error("{} getBalance 参数:{} Exception: {}", VenueEnum.FC.getVenueCode(), req, e.getMessage());
        }

        return FCErrorCodeEnum.CODE_999.toResVO();

    }

    /**
     * 4-2、下注信息及游戏结果 (BetNInfo)
     */
    public FCBaseRes BetNInfo(FCBaseReq req) {

        try {
            log.info("FC BetNInfo: {}", JSON.toJSONString(req));

            if (StrUtil.isEmpty(req.getCurrency())) {
                return FCErrorCodeEnum.CODE_604.toResVO();
            }
            FCCurrencyEnum fsCurrencyEnum = FCCurrencyEnum.valueOf(req.getCurrency());
            VenueInfoVO venueAdminInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.FC.getVenueCode(), fsCurrencyEnum.getCode());
            //NOTE 验证MD5
            String aesDecrypt = FcAESUtill.aesDecrypt(req.getParams(), venueAdminInfoVO.getMerchantKey());
            if (StrUtil.isEmpty(aesDecrypt)) {
                log.error("FC BetNInfo 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            log.info("FC BetNInfo 资料解密 aesDecrypt : {}", aesDecrypt);

            BetNInfoReq vo = JSON.parseObject(aesDecrypt, BetNInfoReq.class);
            if (vo == null) {
                log.error("FC BetNInfo 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }

            String acctId = vo.getMemberAccount();
            Integer gameID = vo.getGameID();
            String currency = vo.getCurrency();
            if (StrUtil.isEmpty(acctId) || ObjectUtil.isEmpty(gameID) || StrUtil.isEmpty(currency)) {
                log.error("FC BetNInfo 但参数不全, vo:{} ", vo);
                return FCErrorCodeEnum.CODE_1098.toResVO();
            }
            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FC.getVenueCode()).venueUserAccount(acctId).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("FC BetNInfo casinoMember不存在, 参数:{} ", vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{}  BetNInfo userInfoVO不存在, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }

            if (userGameLock(userInfoVO)) {
                log.error("{} BetNInfo game lock, user参数:{} ", VenueEnum.FC.getVenueCode(), userInfoVO);
                return FCErrorCodeEnum.CODE_407.toResVO();
            }
            //NOTE 查询场馆状态
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FC.getVenueCode(), userInfoVO.getMainCurrency());
            if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                log.error("{}  BetNInfo venueInfoVO 不存在或不开放, balance, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_408.toResVO();
            }

            if (venueGameMaintainClosed(venueInfoVO.getVenueCode(), casinoMember.getSiteCode(), gameID.toString())) {
                log.info("{} BetNInfo 游戏未开启", VenueEnum.FC.getVenueName());
                return FCErrorCodeEnum.CODE_406.toResVO();
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            //NOTE 投注
            if (vo.getBet().abs().compareTo(userCenterCoin.getTotalAmount())>0){
                log.error("{} BetNInfo 余额不足, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_203.toResVO();
            }

            UserCoinAddVO userBetCoinAddVO = getBetNInfoBetCoinAddVO(vo, userInfoVO);

            CoinRecordResultVO betResultVO = toUserCoinHandle(userBetCoinAddVO);

            return switch (betResultVO.getResultStatus()) {
                case FAIL, WALLET_NOT_EXIST -> FCErrorCodeEnum.CODE_999.toResVO();
                //NOTE REPEAT_TRANSACTIONS 重复算成功, 往下走.
                default -> {
                    if (vo.getWin().compareTo(BigDecimal.ZERO)>0){
                        UserCoinAddVO userPayoutCoinAddVO = getBetNInfoPayoutCoinAddVO(vo, userInfoVO);
                        CoinRecordResultVO payoutResultVO = toUserCoinHandle(userPayoutCoinAddVO);
                        yield switch (payoutResultVO.getResultStatus()) {
                            case FAIL, WALLET_NOT_EXIST -> FCErrorCodeEnum.CODE_999.toResVO();
                            case REPEAT_TRANSACTIONS -> FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(betResultVO.getCoinAfterBalance()).build());
                            default -> FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(payoutResultVO.getCoinAfterBalance()).build());
                        };
                    }else {
                        yield FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(betResultVO.getCoinAfterBalance()).build());
                    }
                }
            };
        } catch (Exception e) {
            log.error("{} BetNInfo 参数:{} Exception: {}", VenueEnum.FC.getVenueCode(), req, e.getMessage());
        }
        return FCErrorCodeEnum.CODE_999.toResVO();
    }

    /**
     * 4-3、取消下注與游戏结果 (CancelBetNInfo)
     */
    public FCBaseRes CancelBetNInfo(FCBaseReq req) {
        try {
            log.info("FC CancelBetNInfo: {}", JSON.toJSONString(req));

            if (StrUtil.isEmpty(req.getCurrency())) {
                return FCErrorCodeEnum.CODE_604.toResVO();
            }
            FCCurrencyEnum fsCurrencyEnum = FCCurrencyEnum.valueOf(req.getCurrency());
            VenueInfoVO venueAdminInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.FC.getVenueCode(), fsCurrencyEnum.getCode());
            //NOTE 验证MD5
            String aesDecrypt = FcAESUtill.aesDecrypt(req.getParams(), venueAdminInfoVO.getMerchantKey());
            if (StrUtil.isEmpty(aesDecrypt)) {
                log.error("FC CancelBetNInfo 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            log.info("FC CancelBetNInfo 资料解密 aesDecrypt : {}", aesDecrypt);

            CancelBetNInfoReq vo = JSON.parseObject(aesDecrypt, CancelBetNInfoReq.class);
            if (vo == null) {
                log.error("FC CancelBetNInfo 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            String acctId = vo.getMemberAccount();
            Integer gameID = vo.getGameID();
            if (StrUtil.isEmpty(acctId) || ObjectUtil.isEmpty(gameID)) {
                log.error("FC CancelBetNInfo 但参数不全, vo:{} ", vo);
                return FCErrorCodeEnum.CODE_1098.toResVO();
            }
            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FC.getVenueCode()).venueUserAccount(acctId).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("FC CancelBetNInfo casinoMember不存在, 参数:{} ", vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{}  CancelBetNInfo userInfoVO不存在, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }
            if (userGameLock(userInfoVO)) {
                log.error("{} CancelBetNInfo game lock, user参数:{} ", VenueEnum.FC.getVenueCode(), userInfoVO);
                return FCErrorCodeEnum.CODE_407.toResVO();
            }
            //NOTE 查询场馆状态
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FC.getVenueCode(), userInfoVO.getMainCurrency());
            if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                log.error("{}  CancelBetNInfo venueInfoVO 不存在或不开放, balance, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_408.toResVO();
            }

            if (venueGameMaintainClosed(venueInfoVO.getVenueCode(), casinoMember.getSiteCode(), gameID.toString())) {
                log.info("{} CancelBetNInfo 游戏未开启", VenueEnum.FC.getVenueName());
                return FCErrorCodeEnum.CODE_406.toResVO();
            }
            //NOTE 确认成功下注
            UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
            userCoinRecordVo.setUserId(userInfoVO.getUserId());
            userCoinRecordVo.setRemark(vo.getBankID());
            userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
            //NOTE  4-3 cancel是用於如果4-2 BetNInfo 遇到timeout狀況下，我方會發送此請求與您確認是否該注單於貴司方是否有成單，若該注單成立，請回覆我方799，若不成立，請回覆我方0以取消此注單
            if (CollUtil.isEmpty(userCoinRecords) ) {
                log.error("{} CancelBetNInfo 失败, 当前下注单不存在，不做取消账变操作, 请求参数: {}", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(userCenterCoin.getTotalAmount()).build());
            }else {
                return FCErrorCodeEnum.CODE_799.toResVO(FCBaseRes.builder().MainPoints(userCenterCoin.getTotalAmount()).build());

            }
        } catch (Exception e) {
            log.error("{} CancelBetNInfo 参数:{} Exception: {}", VenueEnum.FC.getVenueCode(), req, e.getMessage());
        }
        return FCErrorCodeEnum.CODE_999.toResVO();
    }

    public FCBaseRes Bet(FCBaseReq req) {

        try {
            log.info("FC Bet: {}", JSON.toJSONString(req));

            if (StrUtil.isEmpty(req.getCurrency())) {
                return FCErrorCodeEnum.CODE_604.toResVO();
            }
            FCCurrencyEnum fsCurrencyEnum = FCCurrencyEnum.valueOf(req.getCurrency());
            VenueInfoVO venueAdminInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.FC.getVenueCode(), fsCurrencyEnum.getCode());
            //NOTE 验证MD5
            String aesDecrypt = FcAESUtill.aesDecrypt(req.getParams(), venueAdminInfoVO.getMerchantKey());
            if (StrUtil.isEmpty(aesDecrypt)) {
                log.error("FC Bet 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            log.info("FC Bet 资料解密 aesDecrypt : {}", aesDecrypt);

            BetReq vo = JSON.parseObject(aesDecrypt, BetReq.class);
            if (vo == null) {
                log.error("FC Bet 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            String acctId = vo.getMemberAccount();
            Integer gameID = vo.getGameID();
            if (StrUtil.isEmpty(acctId) || ObjectUtil.isEmpty(gameID)) {
                log.error("FC Bet 但参数不全, vo:{} ", vo);
                return FCErrorCodeEnum.CODE_1098.toResVO();
            }
            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FC.getVenueCode()).venueUserAccount(acctId).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("FC Bet casinoMember不存在, 参数:{} ", vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{}  Bet userInfoVO不存在, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }

            if (userGameLock(userInfoVO)) {
                log.error("{} Bet game lock, user参数:{} ", VenueEnum.FC.getVenueCode(), userInfoVO);
                return FCErrorCodeEnum.CODE_407.toResVO();
            }
            //NOTE 查询场馆状态
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FC.getVenueCode(), userInfoVO.getMainCurrency());
            if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                log.error("{}  Bet venueInfoVO 不存在或不开放, balance, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_408.toResVO();
            }

            if (venueGameMaintainClosed(venueInfoVO.getVenueCode(), casinoMember.getSiteCode(), gameID.toString())) {
                log.info("{} Bet 游戏未开启", VenueEnum.FC.getVenueName());
                return FCErrorCodeEnum.CODE_406.toResVO();
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            if (vo.getBet().abs().compareTo(userCenterCoin.getTotalAmount())>0){
                log.error("{} Bet 余额不足, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_203.toResVO();
            }

            UserCoinAddVO userCoinAddVO = getBetCoinAddVO(vo, userInfoVO);
            CoinRecordResultVO betResultVO = toUserCoinHandle(userCoinAddVO);

            return switch (betResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> FCErrorCodeEnum.CODE_203.toResVO();
                case FAIL, WALLET_NOT_EXIST -> FCErrorCodeEnum.CODE_999.toResVO();
                //NOTE 4. 去重操作, 合并到账变状态里, 算成功.
                case REPEAT_TRANSACTIONS, AMOUNT_LESS_ZERO -> FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder()
                        .MainPoints(userCenterCoin.getTotalAmount()).build());
                default ->
                        FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(betResultVO.getCoinAfterBalance()).build());
            };

        } catch (Exception e) {
            log.error("{} Bet 参数:{} Exception: {}", VenueEnum.FC.getVenueCode(), req, e.getMessage());
        }
        return FCErrorCodeEnum.CODE_999.toResVO();
    }

    public FCBaseRes Settle(FCBaseReq req) {

        try {
            log.info("FC Settle: {}", JSON.toJSONString(req));

            if (StrUtil.isEmpty(req.getCurrency())) {
                return FCErrorCodeEnum.CODE_604.toResVO();
            }
            FCCurrencyEnum fsCurrencyEnum = FCCurrencyEnum.valueOf(req.getCurrency());
            VenueInfoVO venueAdminInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.FC.getVenueCode(), fsCurrencyEnum.getCode());
            //NOTE 验证MD5
            String aesDecrypt = FcAESUtill.aesDecrypt(req.getParams(), venueAdminInfoVO.getMerchantKey());
            if (StrUtil.isEmpty(aesDecrypt)) {
                log.error("FC Settle 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            log.info("FC Settle 资料解密 aesDecrypt : {}", aesDecrypt);

            SettleReq vo = JSON.parseObject(aesDecrypt, SettleReq.class);
            if (vo == null) {
                log.error("FC Settle 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }

            String acctId = vo.getMemberAccount();
            Integer gameID = vo.getGameID();
            if (StrUtil.isEmpty(acctId) || ObjectUtil.isEmpty(gameID)) {
                log.error("FC Settle 但参数不全, vo:{} ", vo);
                return FCErrorCodeEnum.CODE_1098.toResVO();
            }
            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FC.getVenueCode()).venueUserAccount(acctId).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("FC Settle casinoMember不存在, 参数:{} ", vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{}  Settle userInfoVO不存在, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }

            if (userGameLock(userInfoVO)) {
                log.error("{} Settle game lock, user参数:{} ", VenueEnum.FC.getVenueCode(), userInfoVO);
                return FCErrorCodeEnum.CODE_407.toResVO();
            }
            //NOTE 查询场馆状态
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FC.getVenueCode(), userInfoVO.getMainCurrency());
            if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                log.error("{}  Settle venueInfoVO 不存在或不开放, balance, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_408.toResVO();
            }

            if (venueGameMaintainClosed(venueInfoVO.getVenueCode(), casinoMember.getSiteCode(), gameID.toString())) {
                log.info("{} Settle 游戏未开启", VenueEnum.FC.getVenueName());
                return FCErrorCodeEnum.CODE_406.toResVO();
            }
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            //NOTE 确认成功下注
            List<String> betIdList = new ArrayList<>();
            for (Object settleBetID : vo.getSettleBetIDs()) {
                betIdList.add(((JSONObject) JSON.toJSON(settleBetID)).getString("betID"));
            }

            UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
            userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
            userCoinRecordVo.setUserId(userInfoVO.getUserId());
            userCoinRecordVo.setRemarkList(betIdList);
            userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
            if (CollUtil.isEmpty(userCoinRecords) || userCoinRecords.size() != betIdList.size()) {
                log.error("{} Settle 失败, 当前下注单异常，取消账变操作, 请求参数: {}", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_221.toResVO(FCBaseRes.builder().MainPoints(userCenterCoin.getTotalAmount()).build());
            }
            UserCoinAddVO userCoinAddVO = getSettleCoinAddVO(vo, userInfoVO, betIdList);
            CoinRecordResultVO betResultVO = toUserCoinHandle(userCoinAddVO);
            return switch (betResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> FCErrorCodeEnum.CODE_203.toResVO();
                case FAIL, WALLET_NOT_EXIST -> FCErrorCodeEnum.CODE_999.toResVO();
                case REPEAT_TRANSACTIONS, AMOUNT_LESS_ZERO ->
                        FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(userCenterCoin.getTotalAmount()).build());
                case SUCCESS ->
                        FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(betResultVO.getCoinAfterBalance()).build());
            };

        } catch (Exception e) {
            log.error("{} Settle 参数:{} Exception: {}", VenueEnum.FC.getVenueCode(), req, e.getMessage());
        }
        return FCErrorCodeEnum.CODE_999.toResVO();
    }

    public FCBaseRes CancelBet(FCBaseReq req) {
        try {
            log.info("FC CancelBet: {}", JSON.toJSONString(req));

            if (StrUtil.isEmpty(req.getCurrency())) {
                return FCErrorCodeEnum.CODE_604.toResVO();
            }
            FCCurrencyEnum fsCurrencyEnum = FCCurrencyEnum.valueOf(req.getCurrency());
            VenueInfoVO venueAdminInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.FC.getVenueCode(), fsCurrencyEnum.getCode());
            //NOTE 验证MD5
            String aesDecrypt = FcAESUtill.aesDecrypt(req.getParams(), venueAdminInfoVO.getMerchantKey());
            if (StrUtil.isEmpty(aesDecrypt)) {
                log.error("FC CancelBet 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            log.info("FC CancelBet 资料解密 aesDecrypt : {}", aesDecrypt);

            CancelBetReq vo = JSON.parseObject(aesDecrypt, CancelBetReq.class);
            if (vo == null) {
                log.error("FC CancelBet 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            String acctId = vo.getMemberAccount();
            Integer gameID = vo.getGameID();
            if (StrUtil.isEmpty(acctId) || ObjectUtil.isEmpty(gameID)) {
                log.error("FC CancelBet 但参数不全, vo:{} ", vo);
                return FCErrorCodeEnum.CODE_1098.toResVO();
            }
            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FC.getVenueCode()).venueUserAccount(acctId).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("FC CancelBet casinoMember不存在, 参数:{} ", vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{}  CancelBet userInfoVO不存在, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }
            if (userGameLock(userInfoVO)) {
                log.error("{} CancelBet game lock, user参数:{} ", VenueEnum.FC.getVenueCode(), userInfoVO);
                return FCErrorCodeEnum.CODE_407.toResVO();
            }
            //NOTE 查询场馆状态
            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FC.getVenueCode(), userInfoVO.getMainCurrency());
            if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                log.error("{}  CancelBet venueInfoVO 不存在或不开放, balance, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_408.toResVO();
            }

            if (venueGameMaintainClosed(venueInfoVO.getVenueCode(), casinoMember.getSiteCode(), gameID.toString())) {
                log.info("{} CancelBet 游戏未开启", VenueEnum.FC.getVenueName());
                return FCErrorCodeEnum.CODE_406.toResVO();
            }
            //NOTE 确认成功下注
            UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
            userCoinRecordVo.setUserId(userInfoVO.getUserId());
            userCoinRecordVo.setOrderNo(vo.getRecordID());
            userCoinRecordVo.setRemark(vo.getBetID());
            userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(userCoinRecordVo);
            if (CollUtil.isEmpty(userCoinRecords)) {
                log.error("{} CancelBet 失败, 当前下注单不存在，不做取消账变操作, 请求参数: {}", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(userCenterCoin.getTotalAmount()).build());
            }
            UserCoinAddVO userCoinAddVO = getCancelBetCoinAddVO(vo, userInfoVO);
            CoinRecordResultVO betResultVO = toUserCoinHandle(userCoinAddVO);
            return switch (betResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> FCErrorCodeEnum.CODE_203.toResVO();
                case FAIL, WALLET_NOT_EXIST -> FCErrorCodeEnum.CODE_999.toResVO();
                case AMOUNT_LESS_ZERO ->
                        FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(userCenterCoin.getTotalAmount()).build());
                default -> {

                    //NOTE 确认成功派彩
                    UserCoinRecordRequestVO userPayoutCoinRecordVo = new UserCoinRecordRequestVO();

                    userPayoutCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
                    userPayoutCoinRecordVo.setUserId(userInfoVO.getUserId());
                    userPayoutCoinRecordVo.setOrderNo(vo.getRecordID());
                    userPayoutCoinRecordVo.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
                    userPayoutCoinRecordVo.setRemark(vo.getBetID());
                    userPayoutCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());

                    List<UserCoinRecordVO> first = getUserCoinRecords(userPayoutCoinRecordVo);
                    if (first.isEmpty()) {
                        log.info("{} CancelBet 派彩记录不存在，不做取消账变操作, 请求参数: {}", VenueEnum.FC.getVenueCode(), vo);
                        yield FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(betResultVO.getCoinAfterBalance()).build());
                    }
                    UserCoinRecordVO userPayoutCoinRecordVO = first.get(0);

                    UserCoinAddVO cancelPayoutCoinAddVO = getCancelPayoutCoinAddVO(vo, userInfoVO, userPayoutCoinRecordVO.getCoinValue());
                    CoinRecordResultVO cancelPayoutResultVO = toUserCoinHandle(cancelPayoutCoinAddVO);

                    BigDecimal coinAfterBalance = betResultVO.getCoinAfterBalance();
                    if (UpdateBalanceStatusEnums.SUCCESS == cancelPayoutResultVO.getResultStatus()){
                        coinAfterBalance = cancelPayoutResultVO.getCoinAfterBalance();
                    }
                    yield FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(coinAfterBalance).build());
                }
            };

        } catch (Exception e) {
            log.error("{} CancelBet 参数:{} Exception: {}", VenueEnum.FC.getVenueCode(), req, e.getMessage());
        }
        return FCErrorCodeEnum.CODE_999.toResVO();
    }


    public FCBaseRes EventSettle(FCBaseReq req) {
        try {

            if (req.isValid()) {
                log.error("FC EventSettle 但参数不全, vo:{} ", req);
                return FCErrorCodeEnum.CODE_1098.toResVO();
            }

            log.info("FC EventSettle: {}", JSON.toJSONString(req));
            if (StrUtil.isEmpty(req.getCurrency())) {
                return FCErrorCodeEnum.CODE_604.toResVO();
            }
            FCCurrencyEnum fsCurrencyEnum = FCCurrencyEnum.valueOf(req.getCurrency());
            VenueInfoVO venueAdminInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.FC.getVenueCode(), fsCurrencyEnum.getCode());
            //NOTE 验证MD5
            String aesDecrypt = FcAESUtill.aesDecrypt(req.getParams(), venueAdminInfoVO.getMerchantKey());
            if (StrUtil.isEmpty(aesDecrypt)) {
                log.error("FC EventSettle 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            log.info("FC EventSettle 资料解密 aesDecrypt : {}", aesDecrypt);
            List<EventSettleReq> eventSettleReqs = JSON.parseArray(aesDecrypt, EventSettleReq.class);
            if (CollUtil.isEmpty(eventSettleReqs)) {
                log.error("FC EventSettle 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }


            for (EventSettleReq vo : eventSettleReqs) {
                String acctId = vo.getMemberAccount();
                Integer gameID = vo.getGameID();
                if (StrUtil.isEmpty(acctId) || ObjectUtil.isEmpty(gameID)) {
                    log.error("FC EventSettle 但参数不全, vo:{} ", vo);
                    return FCErrorCodeEnum.CODE_1098.toResVO();
                }
                CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FC.getVenueCode()).venueUserAccount(acctId).build();
                CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
                if (casinoMember == null) {
                    log.error("FC EventSettle casinoMember不存在, 参数:{} ", vo);
                    return FCErrorCodeEnum.CODE_500.toResVO();
                }
                UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
                if (userInfoVO == null) {
                    log.error("{}  EventSettle userInfoVO不存在, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                    return FCErrorCodeEnum.CODE_500.toResVO();
                }
                if (userGameLock(userInfoVO)) {
                    log.error("{} EventSettle game lock, user参数:{} ", VenueEnum.FC.getVenueCode(), userInfoVO);
                    return FCErrorCodeEnum.CODE_407.toResVO();
                }
                //NOTE 查询场馆状态
                VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(), VenueEnum.FC.getVenueCode(), userInfoVO.getMainCurrency());
                if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                    log.error("{}  EventSettle venueInfoVO 不存在或不开放, balance, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                    return FCErrorCodeEnum.CODE_408.toResVO();
                }
                if (venueGameMaintainClosed(venueInfoVO.getVenueCode(), casinoMember.getSiteCode(), gameID.toString())) {
                    log.info("{} EventSettle 游戏未开启", VenueEnum.FC.getVenueName());
                    return FCErrorCodeEnum.CODE_406.toResVO();
                }
                UserCoinAddVO userCoinAddVO = getEventSettleCoinAddVO(vo, userInfoVO);
                CoinRecordResultVO betResultVO = toUserCoinHandle(userCoinAddVO);

                switch (betResultVO.getResultStatus()) {
                    case INSUFFICIENT_BALANCE ->
                            log.error("{} EventSettle 派彩失败, 余额不足, 信息:{}", VenueEnum.FC.getVenueName(), vo);
                    case FAIL, WALLET_NOT_EXIST ->
                            log.error("{} EventSettle 派彩失败, 无法预期的错误, 信息:{}", VenueEnum.FC.getVenueName(), vo);
                    case REPEAT_TRANSACTIONS, AMOUNT_LESS_ZERO ->
                            log.error("{} EventSettle 派彩异常, 交易重复或账变为0, 信息:{}", VenueEnum.FC.getVenueName(), vo);
                    default -> log.info("{} EventSettle 派彩成功, 信息:{}", VenueEnum.FC.getVenueName(), vo);
                }
            }
            return FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().build());

        } catch (Exception e) {
            log.error("{} EventSettle 参数:{} Exception: {}", VenueEnum.FC.getVenueCode(), req, e.getMessage());
        }
        return FCErrorCodeEnum.CODE_999.toResVO();
    }

    //4-8、Free Spin 下注信息及游戏结果 (FreeSpinBetNInfo)
    //收到该注单时, 不需要异动任何钱包余额, 我方于旋转完毕后才会使用 4-7 活动派彩 派出真实玩家获得的奖金。
    public FCBaseRes FreeSpinBetNInfo(FCBaseReq req) {


        try {
            log.info("FC FreeSpinBetNInfo: {}", JSON.toJSONString(req));

            if (StrUtil.isEmpty(req.getCurrency())) {
                return FCErrorCodeEnum.CODE_604.toResVO();
            }
            FCCurrencyEnum fsCurrencyEnum = FCCurrencyEnum.valueOf(req.getCurrency());
            VenueInfoVO venueAdminInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.FC.getVenueCode(), fsCurrencyEnum.getCode());
            //NOTE 验证MD5
            String aesDecrypt = FcAESUtill.aesDecrypt(req.getParams(), venueAdminInfoVO.getMerchantKey());
            if (StrUtil.isEmpty(aesDecrypt)) {
                log.error("FC FreeSpinBetNInfo 资料解密失败, vo:{} ", req);
                return FCErrorCodeEnum.CODE_303.toResVO();
            }
            log.info("FC FreeSpinBetNInfo 资料解密GetBalanceReq vo : {}", aesDecrypt);

            FreeSpinBetNInfoReq vo = JSON.parseObject(aesDecrypt, FreeSpinBetNInfoReq.class);

            CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder().venueCode(VenueEnum.FC.getVenueCode()).venueUserAccount(vo.getMemberAccount()).build();
            CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
            if (casinoMember == null) {
                log.error("FC FreeSpinBetNInfo casinoMember不存在, 参数:{} ", vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }
            UserInfoVO userInfoVO = getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{}  FreeSpinBetNInfo userInfoVO不存在, 参数:{} ", VenueEnum.FC.getVenueCode(), vo);
                return FCErrorCodeEnum.CODE_500.toResVO();
            }

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            //NOTE 直接返回成功
            return FCErrorCodeEnum.CODE_0.toResVO(FCBaseRes.builder().MainPoints(userCenterCoin.getTotalAmount()).build());


        } catch (Exception e) {
            log.error("{} FreeSpinBetNInfo 参数:{} Exception: {}", VenueEnum.FC.getVenueCode(), req, e.getMessage());
        }
        return FCErrorCodeEnum.CODE_999.toResVO();
    }


    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(GameLoginVo.builder()
                .source(FCLogin(venueInfoVO, loginVO, casinoMemberVO)).userAccount(loginVO.getUserAccount())
                .venueCode(VenueEnum.FC.getVenueCode())
                .type(GameLoginTypeEnums.URL.getType()).build());
    }

    @Override
    public ResponseVO<FCPullBetParams> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {

        JSONArray array = new JSONArray();
        for (String code:venueInfoVO.getCurrencyCode().split(",")){
            array.addAll(GetRecord(venueInfoVO, venuePullParamVO,code));
        }

        List<OrderRecordVO> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(array)) {

            Map<String, UserInfoVO> userInfoMap = new HashMap<>();
            Map<String, String> siteNameMap = getSiteNameMap();
            Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(venueInfoVO.getVenueCode());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TimeZoneUtils.patten_yyyyMMddHHmmss);
            ZoneId zone = ZoneId.of("America/New_York");
            array.forEach(obj -> {
                FCRecordVO fcRecordVO = ((JSONObject) obj).to(FCRecordVO.class);

                String venueAccount = fcRecordVO.getAccount();
                UserInfoVO userInfoVO = userInfoMap.get(venueAccount);
                if (userInfoVO == null) {
                    CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(CasinoMemberReq.builder().venueUserAccount(venueAccount).venueCode(venueInfoVO.getVenueCode()).build());
                    if (casinoMember == null) {
                        log.info("{} 三方关联账号不存在, 账号: {} ", venueInfoVO.getVenueCode(), venueAccount);
                        return;
                    }
                    userInfoVO = getByUserId(casinoMember.getUserId());
                    userInfoMap.put(venueAccount, userInfoVO);
                }
                OrderRecordVO recordVO = new OrderRecordVO();
                recordVO.setUserAccount(userInfoVO.getUserAccount());
                recordVO.setUserId(userInfoVO.getUserId());
                recordVO.setUserName(userInfoVO.getUserName());
                recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
                recordVO.setAgentId(userInfoVO.getSuperAgentId());
                recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
                recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
                recordVO.setBetAmount(fcRecordVO.getBet());
                //NOTE 投注时间为GMT-4时区.
                recordVO.setBetTime(LocalDateTime.parse(fcRecordVO.getBdate(), formatter).atZone(zone).toEpochSecond()*1000);
                recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
                recordVO.setVenueCode(venueInfoVO.getVenueCode());
                recordVO.setVenueType(VenueEnum.FC.getType().getCode());
                recordVO.setCasinoUserName(venueAccount);
                recordVO.setBetIp(userInfoVO.getLastLoginIp());
                recordVO.setCurrency(userInfoVO.getMainCurrency());
                recordVO.setOrderId(OrderUtil.getGameNo());
                recordVO.setThirdOrderId(fcRecordVO.getRecordID());
                recordVO.setTransactionId(fcRecordVO.getRecordID());
                recordVO.setPayoutAmount(fcRecordVO.getRefund());
                //NOTE 中奖减投注
                recordVO.setWinLossAmount(recordVO.getPayoutAmount().subtract(recordVO.getBetAmount()));
                recordVO.setCreatedTime(System.currentTimeMillis());
                recordVO.setUpdatedTime(System.currentTimeMillis());
                //NOTE 结算时间按下注时间算
                recordVO.setSettleTime(recordVO.getBetTime());
                //NOTE 状态
                recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
                recordVO.setOrderClassify(ClassifyEnum.SETTLED.getCode());
                recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
                recordVO.setChangeStatus(0);
                recordVO.setReSettleTime(0L);
                recordVO.setParlayInfo(obj.toString());
                recordVO.setSiteCode(userInfoVO.getSiteCode());
                recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
                recordVO.setVipRank(userInfoVO.getVipRank());
                recordVO.setValidAmount(fcRecordVO.getValidBet());
                recordVO.setThirdGameCode(fcRecordVO.getGameID().toString());
                recordVO.setOrderInfo(recordVO.getThirdOrderId());
                recordVO.setGameCode(fcRecordVO.getGameID().toString());

                FCGameTypeEnum typeEnum = FCGameTypeEnum.fromCode(fcRecordVO.getGametype().toString());
                recordVO.setRoomType(typeEnum.getCode());
                recordVO.setRoomTypeName(typeEnum.getDesc());

                //NOTE  局号
                recordVO.setGameNo(fcRecordVO.getGametype().toString());
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
            if (CollUtil.isNotEmpty(list)) {
                orderRecordProcessService.orderProcess(list);
            }
            log.info("FC电子, getBetRecordList 拉单结束, 共拉单: {}条", list.size());
        }
        return ResponseVO.success();
    }

    public List<ShDeskInfoVO> queryGameList() {
        VenueInfoPO venueInfoPO = venueInfoService.getBaseMapper().selectOne(new LambdaQueryWrapper<>(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.FC.getVenueCode()).orderByAsc(VenueInfoPO::getCurrencyCode)
                .last("limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        return GetGameIconList(venueInfoVO);

    }

    //2-3、游戏登入 (Login)
    public static String FCLogin(VenueInfoVO venueInfoVO, LoginVO loginVO, CasinoMemberVO casinoMemberVO) {
        String thisUrl = venueInfoVO.getApiUrl() + "/Login";
        val param = new HashMap<>();
        param.put("MemberAccount", casinoMemberVO.getVenueUserAccount());
        param.put("GameID", loginVO.getGameCode());
        //语言转换
        FCLangCodeEnum fcLangCodeEnum = FCLangCodeEnum.fromCode(loginVO.getLanguageCode());
        param.put("LanguageID", fcLangCodeEnum.getNum());
        param.put("HomeUrl", "https://www.google.com");
        String jsonData = JSON.toJSONString(param);
        String aesSign = "";
        String md5JsonData = "";
        try {
            aesSign = FcAESUtill.aesEncrypt(jsonData, venueInfoVO.getMerchantKey());
            md5JsonData = MD5Util.MD5Encode(jsonData);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{} FCLogin 加密错误, 加密参数 jsonData {}, 商户号 {}", VenueEnum.FC.getVenueName(), jsonData, venueInfoVO.getMerchantNo());
        }
        //币种转换
        FCCurrencyEnum fcCurrencyEnum = FCCurrencyEnum.fromCode(loginVO.getCurrencyCode());
        if (FCCurrencyEnum.UNKNOWN == fcCurrencyEnum) {
            log.error("{} FCLogin 未知币种, 请求对象, 币种参数:{}", VenueEnum.FC.getVenueName(), loginVO.getCurrencyCode());
            return "";
        }
        HttpRequest request = HttpRequest.post(thisUrl)
                .header(Header.CONTENT_TYPE, CONTENT_TYPE_ENCODED)
                .form(AGENT_CODE, venueInfoVO.getMerchantNo())
                .form(CURRENCY, fcCurrencyEnum.name())
                .form(PARAMS, aesSign)
                .form(SIGN, md5JsonData);
        try (HttpResponse response = request.execute()) {
            if (response.isOk()) {
                JSONObject jsonBody = JSON.parseObject(response.body());
                FCErrorCodeEnum result = FCErrorCodeEnum.fromCode(jsonBody.getIntValue("Result"));
                if (result == null || FCErrorCodeEnum.CODE_0.getCode() != result.getCode()) {
                    log.error("{} FCLogin 登录请求异常, 返回 {}", VenueEnum.FC.getVenueName(), response);
                    return "";
                }
                return jsonBody.getString("Url");
            } else {
                log.error("{} FCLogin 请求错误, 返回 {}", VenueEnum.FC.getVenueName(), response);
            }
        } catch (Exception e) {
            log.error("{} FCLogin 游戏登录发生错误!", VenueEnum.FC.getVenueName(), e);
        }

        return "";
    }

    //NOTE 2-11、取得游戏列表与游戏图示 (GetGameIconList)
    public static List<ShDeskInfoVO> GetGameIconList(VenueInfoVO venueInfoVO) {
        String thisUrl = venueInfoVO.getApiUrl() + "/GetGameIconList";
        String jsonData = JSON.toJSONString(new HashMap<>());
        String aesSign = "";
        String md5JsonData = "";
        try {
            aesSign = FcAESUtill.aesEncrypt(jsonData, venueInfoVO.getMerchantKey());
            md5JsonData = MD5Util.MD5Encode(jsonData);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{} GetGameIconList 加密错误, 加密参数 jsonData {}, 商户号 {}", VenueEnum.FC.getVenueName(), jsonData, venueInfoVO.getMerchantNo());
        }
        String[] split = venueInfoVO.getCurrencyCode().split(",");

        List<String> currencyList = List.of(split);

        String currency = FCCurrencyEnum.CNY.name();
        if (!currencyList.contains(FCCurrencyEnum.CNY.getCode())) {
            currency = currencyList.get(0);
        }

        FCCurrencyEnum fcCurrencyEnum = FCCurrencyEnum.fromCode(currency);
        if (fcCurrencyEnum == FCCurrencyEnum.UNKNOWN){
            log.error("{} GetGameIconList 币咱配置错误, currency {}", VenueEnum.FC.getVenueName(), currency);
            return Lists.newArrayList();

        }

        HttpRequest request = HttpRequest.post(thisUrl)
                .header(Header.CONTENT_TYPE, CONTENT_TYPE_ENCODED)
                .form(AGENT_CODE, venueInfoVO.getMerchantNo())
                .form(CURRENCY, fcCurrencyEnum.name())
                .form(PARAMS, aesSign)
                .form(SIGN, md5JsonData);
        try (HttpResponse response = request.execute()) {
            if (response.isOk()) {
                JSONObject jsonBody = JSON.parseObject(response.body());
                FCErrorCodeEnum result = FCErrorCodeEnum.fromCode(jsonBody.getIntValue("Result"));
                if (result == null || FCErrorCodeEnum.CODE_0.getCode() != result.getCode()) {
                    log.error("{} GetGameIconList请求异常, 返回 {}", VenueEnum.FC.getVenueName(), response);
                    return Lists.newArrayList();
                }
                Map<Object, Object> allMap = new HashMap<>();
                val getGameIconList = jsonBody.getObject(GET_GAME_ICON_LIST, Map.class);
                for (Object value : getGameIconList.values()) {
                    allMap.putAll(((JSONObject) value).to(Map.class));
                }
                List<ShDeskInfoVO> resultList = Lists.newArrayList();
                allMap.forEach((gameCode, game) -> {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(game));
                    resultList.add(ShDeskInfoVO.builder().deskName(jsonObject.getString("gameNameOfChinese")).deskNumber(gameCode.toString()).build());

                });
                return resultList;
            } else {
                log.error("{} GetGameIconList请求错误, 返回 {}", VenueEnum.FC.getVenueName(), response);
            }
        } catch (Exception e) {
            log.error("{} GetGameIconList 获取游戏列表发生错误!", VenueEnum.FC.getVenueName(), e);
        }

        return Lists.newArrayList();
    }


    //NOTE 2-9、取得游戏纪录列表 (GetRecordList) 两小时以内
    //NOTE 2-14、取得历史游戏纪录列表 (GetHistoryRecordList) 两小时以前
    public static JSONArray GetRecord(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO, String code) {
        String thisUrl = venueInfoVO.getBetUrl();
        Long startTime = venuePullParamVO.getStartTime();
        if (System.currentTimeMillis() - startTime >= 1000 * 2 * 3600) {
            thisUrl += "/GetHistoryRecordList";
        } else {
            thisUrl += "/GetRecordList";
        }
        Map<String, Object> paramMap = new LinkedHashMap<>();
        ZonedDateTime startTimeZoned = Instant.ofEpochMilli(venuePullParamVO.getStartTime()).atZone(ZoneId.of("America/New_York"));
        ZonedDateTime endTimeZoned = Instant.ofEpochMilli(venuePullParamVO.getEndTime()).atZone(ZoneId.of("America/New_York"));
        paramMap.put("StartDate", startTimeZoned.format(DateTimeFormatter.ofPattern(TimeZoneUtils.patten_yyyyMMddHHmmss)));
        paramMap.put("EndDate", endTimeZoned.format(DateTimeFormatter.ofPattern(TimeZoneUtils.patten_yyyyMMddHHmmss)));
        String jsonData = JSON.toJSONString(paramMap);
        String aesSign = "";
        String md5JsonData = "";
        try {
            aesSign = FcAESUtill.aesEncrypt(jsonData, venueInfoVO.getMerchantKey());
            md5JsonData = MD5Util.MD5Encode(jsonData);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("{} GetRecord 加密错误, 加密参数 jsonData {}, 商户号 {}", VenueEnum.FC.getVenueName(), jsonData, venueInfoVO.getMerchantNo());
        }

        FCCurrencyEnum currencyEnum = FCCurrencyEnum.fromCode(code);
        if (FCCurrencyEnum.UNKNOWN == currencyEnum) {
            log.error("{} GetRecord 未知币种, 请求对象, 币种参数:{}", VenueEnum.FC.getVenueName(), code);
            return new JSONArray();
        }

        HttpRequest request = HttpRequest.post(thisUrl)
                .header(Header.CONTENT_TYPE, CONTENT_TYPE_ENCODED)
                .form(AGENT_CODE, venueInfoVO.getMerchantNo())
                .form(CURRENCY, currencyEnum.name())
                .form(PARAMS, aesSign)
                .form(SIGN, md5JsonData);
        try (HttpResponse response = request.execute()) {
            if (response.isOk()) {
                JSONObject jsonBody = JSON.parseObject(response.body());
                FCErrorCodeEnum result = FCErrorCodeEnum.fromCode(jsonBody.getIntValue("Result"));
                if (result == null || FCErrorCodeEnum.CODE_0.getCode() != result.getCode()) {
                    log.error("{} GetRecord 请求结果异常, 币种:{}, 返回 {}", VenueEnum.FC.getVenueName(), code, response);
                    return new JSONArray();
                }
                JSONArray records = jsonBody.getJSONArray("Records");
                if (CollUtil.isNotEmpty(records)){
                    return records;
                }
            } else {
                log.error("{} GetRecord 请求错误, 返回 {}", VenueEnum.FC.getVenueName(), response);
            }
        } catch (Exception e) {
            log.error("{} GetRecord 获取游戏列表发生错误!", VenueEnum.FC.getVenueName(), e);
        }
        return new JSONArray();
    }


    /**
     * 投注账变
     */
    @NotNull
    private static UserCoinAddVO getBetNInfoBetCoinAddVO(BetNInfoReq req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setOrderNo(req.getRecordID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getBet().abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getBankID());


        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.FC);
        userCoinAddVO.setThirdOrderNo(req.getBankID());
        return userCoinAddVO;
    }

    /**
     * 派彩账变
     */
    @NotNull
    private static UserCoinAddVO getBetNInfoPayoutCoinAddVO(BetNInfoReq req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getRecordID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getWin().abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getBankID());

        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.FC);
        userCoinAddVO.setThirdOrderNo(req.getBankID());
        return userCoinAddVO;
    }

    /**
     *  BNI取消下注(成功下注和派彩中的下注)
     */
    private static UserCoinAddVO getCancelBNIBetCoinAddVO(UserCoinRecordVO vo, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setOrderNo(vo.getOrderNo());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(vo.getCoinValue());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(vo.getRemark());
        return userCoinAddVO;
    }

    /**
     *  BNI取消派彩(成功下注和派彩中的派彩)
     */
    private static UserCoinAddVO getCancelBNIPayoutAddVO(UserCoinRecordVO vo, UserInfoVO userInfoVO) {

        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(vo.getOrderNo());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(vo.getCoinValue());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(vo.getRemark());
        return userCoinAddVO;
    }

    /**
     * 投注账变
     */
    @NotNull
    private static UserCoinAddVO getBetCoinAddVO(BetReq req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getRecordID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getBet());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getBetID());
        return userCoinAddVO;
    }

    /**
     * 派彩账变
     */
    @NotNull
    private static UserCoinAddVO getSettleCoinAddVO(SettleReq req, UserInfoVO userInfoVO, List<String> betIdList) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getRecordID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getRefund());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(String.join(",", betIdList));
        return userCoinAddVO;
    }

    /**
     * 派彩账变
     */
    @NotNull
    private static UserCoinAddVO getEventSettleCoinAddVO(EventSettleReq req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getBankID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getPoints());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getEventID() + "_" + req.getTrsID());
        return userCoinAddVO;
    }


    /**
     * 取消下注
     */
    private static UserCoinAddVO getCancelBetCoinAddVO(CancelBetReq req, UserInfoVO userInfoVO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setOrderNo(req.getRecordID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(req.getBet());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getBetID());
        return userCoinAddVO;
    }

    /**
     * 取消派奖
     */
    private static UserCoinAddVO getCancelPayoutCoinAddVO(CancelBetReq req, UserInfoVO userInfoVO, BigDecimal amount) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setOrderNo(req.getRecordID());
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setUserId(userInfoVO.getUserId());

        userCoinAddVO.setCoinValue(amount);

        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark(req.getBetID());
        return userCoinAddVO;
    }


}
