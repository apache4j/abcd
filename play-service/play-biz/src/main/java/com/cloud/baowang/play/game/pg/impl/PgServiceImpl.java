package com.cloud.baowang.play.game.pg.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.play.api.api.exceptions.ACELTDefaultException;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.acelt.ACELTResultCodeEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.play.api.vo.base.PGUserToken;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ChangeStatusEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.pg.PGCurrencyEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.pg.enums.PGErrorEnums;
import com.cloud.baowang.play.api.vo.pg.req.PgAdjustmentReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBaseReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBetReq;
import com.cloud.baowang.play.api.vo.pg.req.VerifySessionReq;
import com.cloud.baowang.play.api.vo.pg.rsp.*;
import com.cloud.baowang.play.api.vo.third.FreeGameVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.pg.enums.PgServiceEnums;
import com.cloud.baowang.play.game.pg.response.PgBaseRes;
import com.cloud.baowang.play.game.pg.response.PgBetRecordRes;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.GameNameVO;
import com.cloud.baowang.play.vo.ThirdGameInfoVO;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.PG)
@RequiredArgsConstructor
public class PgServiceImpl extends GameBaseService implements GameService {

    @Autowired
    private OrderRecordProcessService orderRecordProcessService;


    @Value("${play.server.pgAesKey:abc1234567890def}")
    private String pgAesKey;
    @Value("${play.server.pgAesIv:abc1234567890def}")
    private String pgAesIv;


    /**
     * 根据我们平台币币种，匹配 ,返回的是对应场馆币种
     */
    public static PGCurrencyEnum byPlatCurrencyCode(String platCurrencyCode) {
        for (PGCurrencyEnum tmp : PGCurrencyEnum.values()) {
            if (StringUtils.equals(platCurrencyCode, tmp.getPlatCurrencyCode())) {
                return tmp;
            }
        }
        return null;
    }

    public PGBaseRes<VerifySessionRes> sendVerification(VerifySessionReq request) {

        try {

            PGUserToken pgUserToken = parseUserTokenAndValidateVenue(request.getOperator_player_session(), VenuePlatformConstants.PG, request.getSecret_key(), request.getOperator_token());

            if (pgUserToken == null) {
                log.warn("PgService sendVerification operator and player token verification failed!");
                return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST, "operator and player token verification failed!");
            }
            VerifySessionRes verifySession = new VerifySessionRes();
            verifySession.setPlayer_name(pgUserToken.getN());

            String userId = getVenueUserAccount(pgUserToken.getN());
            if (StringUtils.isBlank(userId)) {
                log.info("{}:用户账号解析失败,不存在:{}", VenuePlatformConstants.PG, userId);
                throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
            }

            UserInfoVO userInfoVO = getByUserId(userId);
            // 转换为对应平台的货币
            String currencyCode = byPlatCurrencyCode(userInfoVO.getMainCurrency()).getCode();
            if (!currencyCode.equals(pgUserToken.getC())) {
                log.info("{}:币种与用户的法币,不一致:{}", currencyCode, pgUserToken.getC());
                throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_CURRENCY);
            }
            verifySession.setCurrency(currencyCode);
            if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
                log.info("{}:用户被打标签,登录锁定不允许下注:{}", VenuePlatformConstants.PG, userId);
                throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
            }
            return PGBaseRes.success(verifySession);
        } catch (Exception e) {
            log.error("游戏登入后令牌验证error.userToken", e);
        }
        return PGBaseRes.failed(PGErrorEnums.SERVER_INTERNAL_ERROR, "operator and player token verification failed!");
    }

    /**
     * 解析前端传入的 operator_player_session，提取 siteCode 和加密的用户 token，
     * 然后根据 siteCode 和场馆 code 查询场馆信息，并校验场馆是否启用。
     * 最后解析并返回解密后的用户 token 对象。
     *
     * @param operatorPlayerSession 前端传入的 session 参数（已 URL 编码，包含 token 和 siteCode）
     * @param venuePlatform             场馆枚举，包含当前操作的场馆 code
     * @return 解密后的 PGUserToken 对象
     */
    public PGUserToken parseUserTokenAndValidateVenue(String operatorPlayerSession, String venuePlatform, String merchantKey, String operator_token) {
        try {
            operatorPlayerSession = operatorPlayerSession.replace("\\\"", "\""); // 把 \" 替换成 "
            // Step 1: URL 解码前端传入的 JSON 字符串
            String decodedPayload = URLDecoder.decode(operatorPlayerSession, StandardCharsets.UTF_8);

            // Step 2: 解析 JSON，提取 siteCode 和加密后的用户 token
            JSONObject jsonObject = JSON.parseObject(decodedPayload);
            String siteCode = jsonObject.getString("siteCode");
            String userToken = jsonObject.getString("token");

            // Step 3: 构建查询场馆信息所需的参数
            SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
                    .siteCode(siteCode)
                    .venueCode(venuePlatform)
                    .build();

            // Step 4: 通过 API 获取场馆信息
//            VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(siteCode,
//                    VenueEnum.DBCHESS.getVenueCode(),
//                    null);


            // Step 5: 校验场馆是否存在且处于启用状态
            if (venueMaintainClosed(VenuePlatformConstants.PG,siteCode)) {
                log.info("该站:{} 没有分配:{} 场馆的权限", siteCode, platformEnum());
                throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
            }

            VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PG,null);

            // Step 6: 使用场馆密钥解密用户 token 并返回 PGUserToken 对象
            PGUserToken pgUserToken = getUserToken(userToken, venueInfoVO);
            if (!this.checkUserToken(pgUserToken, venueInfoVO.getMerchantKey())
                    || !StringUtils.equalsIgnoreCase(merchantKey, venueInfoVO.getAesKey())
                    || !StringUtils.equals(operator_token, venueInfoVO.getMerchantNo())) {
                log.warn("PgService sendVerification operator and player token verification failed!");
                return null;
            }
            return pgUserToken;
        } catch (Exception e) {
            log.warn("PgService sendVerification operator and player token verification failed!", e);
            return null;
        }

    }



    /**
     * 获取令牌信息
     *
     * @param userToken 用户token信息
     * @return PGUserToken token信息
     */
    private PGUserToken getUserToken(String userToken, VenueInfoVO venueInfoVO) {
        PGUserToken pgUserToken = null;
        try {
            /*String checkString = Encrypt.aesDecrypt(pgAesKey, pgAesIv, URLDecoder.decode(userToken, StandardCharsets.UTF_8));
            // 解析失败之后 token包含%20，代表有空格问题，需要替换成+
            if (StringUtils.isBlank(checkString) && userToken.contains("%20")) {
                checkString = Encrypt.aesDecrypt(pgAesKey, pgAesIv, URLDecoder.decode(userToken, StandardCharsets.UTF_8).replace(" ", "+"));
            }
            if (StringUtils.isBlank(checkString)) {
                checkString = Encrypt.aesDecrypt(pgAesKey, pgAesIv, userToken);
            }*/
            pgUserToken = JSON.parseObject(userToken, PGUserToken.class);
        } catch (Exception e) {
            log.error("PgServiceImpl getUserToken aesDecrypt error", e);
        }
        return pgUserToken;
    }

    /**
     * 用户token验证
     *
     * @param pgUserToken 用户token信息
     * @param secretKey   密钥
     * @return 用户是否验证成功
     */
    private boolean checkUserToken(PGUserToken pgUserToken, String secretKey) {
        if (StringUtils.isBlank(secretKey)) {
            log.warn("checkUserToken secretKey is blank");
            return false;
        }
        LocalDate tokenDate = Instant.ofEpochMilli(pgUserToken.getE()).atZone(ZoneId.systemDefault()).toLocalDate();
        // 用户token有效期为7天
        if (ChronoUnit.DAYS.between(tokenDate, LocalDate.now()) > 7) {
            log.warn("tokenDate expired. tokenDate={},now={}", tokenDate, LocalDate.now());
            return false;
        }
        return StringUtils.equalsIgnoreCase(secretKey, pgUserToken.getK());
    }

    public PGBaseRes<PgBalanceRes> queryBalance(PgBaseReq request) {
        if (!request.validate()) {
            return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST);
        }

        PGUserToken pgUserToken = parseUserTokenAndValidateVenue(request.getOperator_player_session(), VenuePlatformConstants.PG, request.getSecret_key(), request.getOperator_token());

        if (pgUserToken == null) {
            log.warn("PgService sendVerification operator and player token verification failed!");
            return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST, "operator and player token verification failed!");
        }
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(request.getPlayer_name());
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.PG);
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null) {
            return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST);
        }
        String userAccount = casinoMember.getUserAccount();
        String siteCode = casinoMember.getSiteCode();
        // 币种校验与切换

        UserCoinWalletVO userCenterCoin = getUserCenterCoin(casinoMember.getUserId());
        PGCurrencyEnum currency = byPlatCurrencyCode(userCenterCoin.getCurrency());
        if (currency == null || currency.getCode() == null) {
            return PGBaseRes.failed(PGErrorEnums.WALLET_NOT_EXIST);
        }
        String currencyCode = currency.getCode();
        if (Objects.isNull(currencyCode)) {
            return PGBaseRes.failed(PGErrorEnums.WALLET_NOT_EXIST);
        }
        PgBalanceRes pgBalanceRes = new PgBalanceRes();
        BigDecimal totalAmount = userCenterCoin.getTotalAmount();
        pgBalanceRes.setBalance_amount(
                PGCurrencyEnum.isRate1000(currencyCode)
                        ? totalAmount.divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN)
                        : totalAmount
        );


        pgBalanceRes.setCurrency_code(currencyCode);
        pgBalanceRes.setUpdated_time(Instant.now().toEpochMilli());
        return PGBaseRes.success(pgBalanceRes);
    }

    public PGBaseRes<PgAmountBetRes> processBet(PgBetReq request) {
        if (PGCurrencyEnum.isRate1000(request.getCurrency_code())) {
            // 实际金额扩大1000倍,参与计算
            request.setTransfer_amount(request.getTransfer_amount().multiply(BigDecimal.valueOf(1000)));
            request.setBet_amount(request.getBet_amount().multiply(BigDecimal.valueOf(1000)));
            request.setWin_amount(request.getWin_amount().multiply(BigDecimal.valueOf(1000)));
            //request.setReal_transfer_amount(request.getReal_transfer_amount().multiply(BigDecimal.valueOf(1000)));
        }
        boolean isCheck = Boolean.TRUE.equals(request.getIs_adjustment())
                || Boolean.TRUE.equals(request.getIs_validate_bet());
        UserInfoVO userInfoVO = this.betRequestValid(request, isCheck);
        if (userInfoVO==null) {
            return PGBaseRes.failed(PGErrorEnums.GAME_MAINTENANCE);
        }
        // 场馆关闭
        if (venueMaintainClosed(VenuePlatformConstants.PG, userInfoVO.getSiteCode())) {
            log.info("{}:场馆未开启", VenueEnum.PG.getVenueName());
            return PGBaseRes.failed(PGErrorEnums.GAME_MAINTENANCE);
        }
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                .siteCode(userInfoVO.getUserInfoVO().getSiteCode())
//                .venueCode(VenueCodeConstants.PG)
//                .currencyCode(userInfoVO.getUserInfoVO().getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();

        //只有下注的时候才判断是否游戏是否开启
        if(request.getBet_amount().abs().compareTo(BigDecimal.ZERO) > 0){
            if(venueGameMaintainClosed(VenueEnum.PG.getVenueCode(),userInfoVO.getSiteCode(),request.getGame_id())){
                log.info("{}:游戏未开启,user:{}", VenueEnum.PG.getVenueName(),request.getGame_id());
                return PGBaseRes.failed(PGErrorEnums.GAME_MAINTENANCE);
            }
        }



        // 修改金额
        String currencyCode = userInfoVO.getMainCurrency();

        // 改变的金额
        UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
        BigDecimal balance =userCoin.getTotalAmount().subtract(request.getTransfer_amount().negate());
        BigDecimal betAmountReq = userCoin.getTotalAmount().subtract(request.getBet_amount());
        // 余额如果小于0，说明余额不足
        if (balance.compareTo(BigDecimal.ZERO) < 0 || betAmountReq.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("PG Service updateBalance error, transactionId = {}, orderNo = {}, amount = {}, beforeBalance = {}",
                    request.getTransaction_id(), request.getBet_id(), request.getTransfer_amount(), userCoin.getTotalAmount());
            return PGBaseRes.failed(PGErrorEnums.INSUFFICIENT_BALANCE_CANT_BET);
        }

        //
        if (request.getWin_amount().subtract(request.getBet_amount()).compareTo(request.getTransfer_amount()) != 0) {
            log.error("PG 电子体育 玩家的输赢金额不等赢得金额减去投注金额");
            return PGBaseRes.failed(PGErrorEnums.BET_FAILED_EXCEPTION);
        }

        try {
            //修改余额 记录账变
            CoinRecordResultVO coinRecordResultVO = updateBalanceBetPayOutPG(userInfoVO, request.getBet_id(),
                    request.getBet_amount(), request.getWin_amount(), request.getTransaction_id());
            PgAmountBetRes pgAmountBetRes = new PgAmountBetRes();
            pgAmountBetRes.setUpdated_time(request.getUpdated_time());
            pgAmountBetRes.setCurrency_code(request.getCurrency_code());
            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS, AMOUNT_LESS_ZERO -> {
                    pgAmountBetRes.setBalance_amount(userCoin.getTotalAmount().add(request.getTransfer_amount()).setScale(2, RoundingMode.DOWN));
                    pgAmountBetRes.setReal_transfer_amount(request.getReal_transfer_amount().setScale(2, RoundingMode.DOWN));
                    if (PGCurrencyEnum.isRate1000(currencyCode)) {
                        pgAmountBetRes.setBalance_amount(pgAmountBetRes.getBalance_amount().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                        //pgAmountBetRes.setReal_transfer_amount(pgAmountBetRes.getReal_transfer_amount().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN));
                    }
                    yield PGBaseRes.success(pgAmountBetRes);
                }
                case REPEAT_TRANSACTIONS -> {
                    pgAmountBetRes.setBalance_amount(userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN));
                    pgAmountBetRes.setReal_transfer_amount(request.getReal_transfer_amount());
                    if (PGCurrencyEnum.isRate1000(currencyCode)) {
                        pgAmountBetRes.setBalance_amount(pgAmountBetRes.getBalance_amount().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                        //pgAmountBetRes.setReal_transfer_amount(pgAmountBetRes.getReal_transfer_amount().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN));
                    }
                    yield PGBaseRes.success(pgAmountBetRes);
                }
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST ->
                        PGBaseRes.failed(PGErrorEnums.INSUFFICIENT_BALANCE_CANT_BET);
                default -> PGBaseRes.failed(PGErrorEnums.SERVER_INTERNAL_ERROR);
            };
        } catch (Exception e) {
            log.error("processBet error", e);
            if (e instanceof BaowangDefaultException baoWangException) {
                if (baoWangException.getResultCode().getCode() == ResultCode.REPEAT_TRANSACTIONS.getCode()) {
                    return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST,
                            String.format("Balance calculation error: expected %s = %s + %s",
                                    balance,
                                    balance,
                                    request.getTransfer_amount())
                    );
                }
            }
        }
        return PGBaseRes.failed(PGErrorEnums.SERVER_INTERNAL_ERROR);
    }

    private CoinRecordResultVO updateBalance(UserInfoVO userInfoVO, String transactionId, String currency, BigDecimal transferAmount) throws PGDefaultException {
        // 查找是否是唯一
        UserCoinRecordRequestVO userCoinRecordRequestVO = new UserCoinRecordRequestVO();
        userCoinRecordRequestVO.setUserId(userInfoVO.getUserId());
        userCoinRecordRequestVO.setOrderNo(transactionId);
        List<UserCoinRecordVO> userCoinRecord = getUserCoinRecords(userCoinRecordRequestVO);
        if (!CollectionUtil.isEmpty(userCoinRecord)) {
            log.info("PG::查询账变记录存在,重复,:{}", userCoinRecordRequestVO);
            CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
            coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS);
            return coinRecordResultVO;
            //throw new PGDefaultException(PGErrorEnums.INVALID_REQUEST, transactionId);

        }
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(currency);
        userCoinAddVO.setBalanceType(transferAmount.compareTo(BigDecimal.ZERO) > 0 ? CoinBalanceTypeEnum.INCOME.getCode() : CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(transferAmount.compareTo(BigDecimal.ZERO) > 0 ? WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode() : WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(transferAmount.compareTo(BigDecimal.ZERO) > 0 ? WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode() : WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));

        userCoinAddVO.setAccountCoinType(transferAmount.compareTo(BigDecimal.ZERO) > 0 ? AccountCoinTypeEnums.GAME_PAYOUT.getCode() : AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PG);
        userCoinAddVO.setThirdOrderNo(transactionId);
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }


    private UserInfoVO betRequestValid(PgBaseReq request, boolean isCheck) {
        if (!request.validate()) {
            log.error("betRequestValid validate fail");
            return null;
        }
        PGUserToken pgUserToken = parseUserTokenAndValidateVenue(request.getOperator_player_session(), VenuePlatformConstants.PG, request.getSecret_key(), request.getOperator_token());
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(request.getPlayer_name());
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.PG);
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);

        if (casinoMember == null) {
            log.error("PG queryUserInfoByAccount userName[{}] not find.", request.getPlayer_name());
            return null;
        }
        String userId = casinoMember.getUserId();
        // 校验密钥
        UserInfoVO userInfoVO = getByUserId(userId);

        //投注验证币种
        if (!request.getCurrency_code().equals(pgUserToken.getC())) {
            log.warn("币种校验失败");
            return null;
        }
        //投注与实际交易金额不一致
        if (request.getTransfer_amount().compareTo(request.getReal_transfer_amount()) != 0) {
            log.error("投注与实际交易金额不一致");
            return null;
        }

        // 游戏锁定
        if (userGameLock(userInfoVO)) {
            log.error("PG queryUserInfoByAccount userName[{}] game lock.", request.getPlayer_name());
            return null;
        }

        return userInfoVO;
    }

    private UserInfoVO betRequestValidForAdjuest(PgBaseReq request, boolean isCheck) {
        if (!request.validate()) {
            log.error("betRequestValid validate fail");
            return null;
        }
        PGUserToken pgUserToken = new PGUserToken();
        CasinoMemberReq casinoMemberReqVO = new CasinoMemberReq();
        casinoMemberReqVO.setVenueUserAccount(request.getPlayer_name());
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.PG);
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReqVO);

        if (casinoMember == null) {
            log.error("PG queryUserInfoByAccount userName[{}] not find.", request.getPlayer_name());
            return null;
        }
        String userId = casinoMember.getUserId();
        UserInfoVO userInfoVO = getByUserId(userId);

        // 校验密钥
        String mainCurrency = userInfoVO.getMainCurrency();
//        VenueInfoVO venueInfoVO = venueInfoService.getSiteVenueInfoByVenueCode(userInfoVO.getSiteCode(),
//                VenueEnum.DBACELT.getVenueCode(),
//                mainCurrency);
        VenueInfoVO venueInfoVO = getVenueInfo(VenuePlatformConstants.PG,mainCurrency);
        if (!StringUtils.equalsIgnoreCase(request.getSecret_key(), venueInfoVO.getAesKey())
                || !StringUtils.equals(request.getOperator_token(), venueInfoVO.getMerchantNo())) {
            log.error("PG operator_token：{} 或者 secret_key:{} 不一致.", request.getSecret_key(), request.getOperator_token());
            return null;
        }
        //

        // 转换为对应平台的货币
        String currencyCode = byPlatCurrencyCode(userInfoVO.getMainCurrency()).getCode();
        //投注验证币种
        if (!request.getCurrency_code().equals(currencyCode)) {
            log.warn("币种校验失败");
            return null;
        }
        //投注与实际交易金额不一致
        if (request.getTransfer_amount().compareTo(request.getReal_transfer_amount()) != 0) {
            log.error("投注与实际交易金额不一致");
            return null;
        }


        if (Objects.isNull(userInfoVO)) {
            log.error("PG queryUserInfoByAccount userName[{}] not find.", request.getPlayer_name());
            return null;
        }
        // 游戏锁定
        /*if (userGameLock(userInfoVO)) {
            log.error("PG queryUserInfoByAccount userName[{}] game lock.", request.getPlayer_name());
            validVO.setPgErrorEnums(PGErrorEnums.SERVER_INTERNAL_ERROR);
            return validVO;
        }*/

        return userInfoVO;
    }


    public PGBaseRes<PgAdjustAmountRes> processAdjustBet(PgAdjustmentReq request) {

        if (ObjectUtil.isEmpty(request.getAdjustment_transaction_id())) {
            log.info("PG::交易的唯一标识符,缺少,:{}", JSONObject.toJSONString(request));
            return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST);
        }
        // 对一些货币扩大1000倍
        if (PGCurrencyEnum.isRate1000(request.getCurrency_code())) {
            // 实际金额扩大1000倍,参与计算
            request.setTransfer_amount(request.getTransfer_amount().multiply(BigDecimal.valueOf(1000)));
            //request.setReal_transfer_amount(request.getReal_transfer_amount().multiply(BigDecimal.valueOf(1000)));
        }

        UserInfoVO userInfoVO = this.betRequestValid(request, true);
        if (userInfoVO==null) {
            return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST);
        }
        if (venueMaintainClosed(VenuePlatformConstants.PG, userInfoVO.getSiteCode())) {
            log.info("{}:场馆未开启", VenueEnum.PG.getVenueName());
            return PGBaseRes.failed(PGErrorEnums.SERVER_INTERNAL_ERROR);
        }

        UserCoinWalletVO userCoin = getUserCenterCoin(userInfoVO.getUserId());
        // 余额如果小于0，说明余额不足
        BigDecimal balance = userCoin.getTotalAmount().add(request.getTransfer_amount());
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("PG Service processAdjustBet error, request = {}, beforeBalance = {}", request, userCoin.getTotalAmount());
            return PGBaseRes.failed(PGErrorEnums.INSUFFICIENT_BALANCE_CANT_BET);
        }
        String currencyCode = request.getCurrency_code();

        try {
            //修改余额 记录账变
            CoinRecordResultVO coinRecordResultVO = this.updateBalance(userInfoVO, request.getAdjustment_transaction_id(),
                    request.getCurrency_code(), request.getTransfer_amount());
            PgAdjustAmountRes adjustAmountRes = new PgAdjustAmountRes();
            adjustAmountRes.setUpdated_time(request.getAdjustment_time());
            adjustAmountRes.setAdjust_amount(request.getTransfer_amount());
            adjustAmountRes.setReal_transfer_amount(request.getReal_transfer_amount());
            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS, AMOUNT_LESS_ZERO -> {
                    adjustAmountRes.setBalance_before(userCoin.getTotalAmount());
                    adjustAmountRes.setBalance_after(userCoin.getTotalAmount().add(request.getTransfer_amount()));
                    if (PGCurrencyEnum.isRate1000(currencyCode)) {
                        adjustAmountRes.setBalance_before(adjustAmountRes.getBalance_before().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                        adjustAmountRes.setBalance_after(adjustAmountRes.getBalance_after().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                        adjustAmountRes.setAdjust_amount(request.getTransfer_amount().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                    }
                    yield PGBaseRes.success(adjustAmountRes);
                }
                case REPEAT_TRANSACTIONS -> {
                    adjustAmountRes.setBalance_before(userCoin.getTotalAmount().subtract(request.getTransfer_amount()).setScale(2, RoundingMode.DOWN));
                    adjustAmountRes.setBalance_after(userCoin.getTotalAmount().setScale(2, RoundingMode.DOWN));
                    if (PGCurrencyEnum.isRate1000(currencyCode)) {
                        adjustAmountRes.setBalance_before(adjustAmountRes.getBalance_before().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                        adjustAmountRes.setBalance_after(adjustAmountRes.getBalance_after().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                        adjustAmountRes.setAdjust_amount(request.getTransfer_amount().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                    }
                    yield PGBaseRes.success(adjustAmountRes);
                }
                case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST ->
                        PGBaseRes.failed(PGErrorEnums.INSUFFICIENT_BALANCE_CANT_BET);
                default -> PGBaseRes.failed(PGErrorEnums.SERVER_INTERNAL_ERROR);
            };
        } catch (Exception e) {
            log.error("processAdjustBet error", e);
            if (e instanceof PGDefaultException pgDefaultException) {
                if ((pgDefaultException.getResultCode().getCode().equals(PGErrorEnums.INVALID_REQUEST.getCode()))) {
                    return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST);
                }
            }
        }
        return PGBaseRes.failed(PGErrorEnums.SERVER_INTERNAL_ERROR);
    }



    public CoinRecordResultVO updateBalanceBetPayOutPG(UserInfoVO userInfoVO, String betId, BigDecimal betAmount, BigDecimal payoutAmount, String traceId) {
        CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
        // 构建投注记录对象（支出）
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(betId); // 设置订单号
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency()); // 设置币种
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode()); // 设置为支出类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode()); // 设置币种类型为游戏投注
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode()); // 设置业务币种类型为游戏投注
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode()); // 设置客户币种类型为游戏投注
        userCoinAddVO.setUserId(userInfoVO.getUserId()); // 设置用户ID
        userCoinAddVO.setCoinValue(betAmount.abs()); // 投注金额（绝对值）
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class)); // 设置用户信息
        userCoinAddVO.setRemark(traceId);
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PG);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setThirdOrderNo(traceId);

        // 如果投注金额大于0，才进行投注记录
        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) > 0) {
            UserCoinRecordRequestVO requestVO = new UserCoinRecordRequestVO();
            requestVO.setOrderNo(betId);
            requestVO.setUserId(userInfoVO.getUserId());
            requestVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
            List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(requestVO);
//            List<UserCoinRecordVO> userCoinRecords = userCoinRecordApi.getUserCoinRecordPG(betId, userInfoVO.getUserId(), CoinBalanceTypeEnum.EXPENSES.getCode());
            if (CollUtil.isNotEmpty(userCoinRecords)) {
                Optional<UserCoinRecordVO> first = userCoinRecords.stream().filter(e -> StrUtil.equals(e.getRemark(), traceId)).findFirst();
                // 不存在，不是重复，属于第二次
                if (!first.isPresent()) {
                    userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
                    coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
                } else {
                    // 发起扣款（投注）操作
                    coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
                }

            } else {
                // 发起扣款（投注）操作
                coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
            }
            // 如果投注失败，直接返回失败结果，不进行派彩操作
            if (!coinRecordResultVO.getResultStatus().equals(UpdateBalanceStatusEnums.SUCCESS)) {
                return coinRecordResultVO;
            }
        }

        // 构建派彩记录对象（收入）
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(betId); // 同一订单号
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency()); // 设置币种
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode()); // 设置为收入类型
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode()); // 设置币种类型为游戏派彩
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode()); // 设置业务币种类型为游戏派彩
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode()); // 设置客户币种类型为游戏派彩
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId()); // 设置用户ID
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs()); // 派彩金额（绝对值）
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class)); // 设置用户信息
        userCoinAddVOPayout.setRemark(traceId);
        userCoinAddVO.setVenueCode(VenuePlatformConstants.PG);
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        userCoinAddVO.setThirdOrderNo(traceId);

        UserCoinRecordRequestVO requestVO = new UserCoinRecordRequestVO();
        requestVO.setOrderNo(betId);
        requestVO.setUserId(userInfoVO.getUserId());
        requestVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        List<UserCoinRecordVO> userCoinRecords = getUserCoinRecords(requestVO);

//        List<UserCoinRecordVO> userCoinRecords = userCoinRecordApi.getUserCoinRecordPG(betId, userInfoVO.getUserId(), CoinBalanceTypeEnum.INCOME.getCode());
        if (CollUtil.isNotEmpty(userCoinRecords)) {
            Optional<UserCoinRecordVO> first = userCoinRecords.stream().filter(e -> StrUtil.equals(e.getRemark(), traceId)).findFirst();
            // 不存在，不是重复，属于第二次
            if (!first.isPresent()) {
                userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
                // 发起派彩（收入）操作
                coinRecordResultVO = toUserCoinHandle(userCoinAddVOPayout);
            } else {
                // 存在直接报错
                //coinRecordResultVO = userCoinApi.addCoin(userCoinAddVOPayout);
                //throw new BaowangDefaultException(ResultCode.REPEAT_TRANSACTIONS);
                log.info("PG::查询账变记录存在,重复,:{}", traceId);
                coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS);
                return coinRecordResultVO;
            }
        } else {
            // 发起派彩（收入）操作
            coinRecordResultVO = toUserCoinHandle(userCoinAddVOPayout);
        }
        return coinRecordResultVO;

    }




    @Override
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(true);
    }

    public static void main(String[] args) {
        String userAccountUrlEncode = URLEncoder.encode("pro_list_123456", StandardCharsets.UTF_8);

        // 解析（解码）回原始账号字符串
        String userAccountDecoded = URLDecoder.decode(userAccountUrlEncode, StandardCharsets.UTF_8);
        System.out.println(userAccountUrlEncode);
        System.out.println(userAccountDecoded);
    }
    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        // 校验游戏
        if (StringUtils.isNotBlank(loginVO.getGameCode())) {
            if (venueGameMaintainClosed(loginVO.getSiteCode(), loginVO.getGameCode(), loginVO.getVenueCode())) {
                throw new BaowangDefaultException(ResultCode.VENUE_CHOOSE_ERROR);
            }
        }
        String userAccountUrlEncode = null;
        try {
            String userAccountAes = this.createUserToken(casinoMemberVO, venueDetailVO, loginVO.getCurrencyCode());
            String payload = JSON.toJSONString(Map.of(
                    "token", userAccountAes,
                    "siteCode", loginVO.getSiteCode()
            ));
            userAccountUrlEncode = URLEncoder.encode(payload, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("PG-login encrypt error");
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        String url = venueDetailVO.getApiUrl() + PgServiceEnums.LOGIN_GAME.getPath().concat("?trace_id=").concat(UUID.randomUUID().toString());
        Map<String, String> prams = new HashMap<>();
        // 运营商令牌
        prams.put("operator_token", venueDetailVO.getMerchantNo());
        prams.put("path", "/" + loginVO.getGameCode() + "/index.html");//指定子游戏进入
        prams.put("url_type", "game-entry");//指定子游戏进入
        prams.put("extra_args", "btt=1&ops=" + userAccountUrlEncode + "&l=" + LANGUAGE_MAP.getOrDefault(loginVO.getLanguageCode(),"en"));
        prams.put("client_ip", loginVO.getIp());
        log.info("PG-进入游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.post(url, prams);
            log.info("PG-进入游戏response={}", response);
            GameLoginVo gameLoginVo = GameLoginVo.builder().source(response)
                    .type(GameLoginTypeEnums.HTML.getType())
                    .userAccount(loginVO.getUserAccount())
                    .venueCode(VenueEnum.PG.getVenueCode())
                    .build();
            return ResponseVO.success(gameLoginVo);
        } catch (Exception e) {
            log.error("PG-进入游戏异常【{}】", casinoMemberVO.getVenueUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @Override
    public String genVenueUserPassword() {
        return null;
    }

    @Override
    public ResponseVO<String> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
        String version = venuePullParamVO.getVersionKey();
        Map<String, String> params = Maps.newHashMap();
        params.put("operator_token", venueInfoVO.getMerchantNo());
        params.put("secret_key", venueInfoVO.getAesKey());
        params.put("count", "1500");
        params.put("bet_type", "1");
        String url = venueInfoVO.getBetUrl().concat(PgServiceEnums.QUERY_GAME_HISTORY.getPath());
        List<PgBetRecordRes> data = Lists.newArrayList();
        try {
            do {
                params.put("row_version", version);
                log.info("PG-拉单request apiPath={}, request={}", url, com.alibaba.fastjson.JSON.toJSONString(params));
                String response = HttpClientHandler.post(url.concat("?trace_id=").concat(UUID.randomUUID().toString()), params);
                log.info("PG-拉单response apiPath={}, response={}", url, response);
                PgBaseRes<List<PgBetRecordRes>> baseRes = com.alibaba.fastjson.JSON.parseObject(response, new TypeReference<>() {
                });
                if (ObjectUtils.isEmpty(baseRes) || Objects.nonNull(baseRes.getError())) {
                    return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
                }
                data = baseRes.getData();
                if (CollectionUtils.isEmpty(data)) {
                    break;//当前页无内容
                }
                List<PgBetRecordRes> collect = data.stream().distinct().collect(Collectors.toList());
                ;
                //解析注单数据入库
                List<OrderRecordVO> orderRecordVOList = this.parseOrder(venueInfoVO, collect);
                if (CollectionUtils.isNotEmpty(orderRecordVOList)) {
                    log.info("PG-1111 orderProcess: {}", JSON.toJSONString(orderRecordVOList));
                    orderRecordProcessService.orderProcess(orderRecordVOList);
                }
                //获取最大的版本号
                version = data.stream().max(Comparator.comparing(PgBetRecordRes::getRowVersion)).get().getRowVersion().toString();
            } while (data.size() == 1500);
            log.info("PG-拉单完成");
            return ResponseVO.success(version);
        } catch (Exception e) {
            log.error("PG-拉单异常,version:{}", version, e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }


    public List<OrderRecordVO> parseOrder(VenueInfoVO venueInfoVO, List<PgBetRecordRes> dataList) throws Exception {

        Integer gameTypeId = VenueTypeEnum.ELECTRONICS.getCode();
        // 场馆用户关联信息
        List<String> thirdUserName = dataList.stream().map(PgBetRecordRes::getPlayerName).distinct().toList();
        Map<String, CasinoMemberPO> casinoMemberMap = super.getCasinoMemberByUsers(thirdUserName, venueInfoVO.getVenuePlatform());
        if (MapUtil.isEmpty(casinoMemberMap)) {
            log.info("{} 未找到用户信息", venueInfoVO.getVenueCode());
            return null;
        }
        // 用户信息
        List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
        Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
        if (CollUtil.isEmpty(userMap)) {
            log.info("PG游戏用户账号不存在{}", userIds);
            return null;
        }
        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);
        // 场馆游戏配置
        Map<String, GameInfoPO> gameInfoMap = super.getGameInfoByVenueCode(VenuePlatformConstants.PG);
        if (CollUtil.isEmpty(gameInfoMap)) {
            log.error("PG游戏列表未配置");
            return null;
        }

        Map<String, String> siteNameMap = getSiteNameMap();

        List<OrderRecordVO> orderRecordList = Lists.newArrayList();
        for (PgBetRecordRes entity : dataList) {
            String account = entity.getPlayerName();
            CasinoMemberPO casinoMemberPO = casinoMemberMap.get(account);
            if (casinoMemberPO == null) {
                log.error("PG场馆账号玩家信息不存在，注单信息{}", entity);
                continue;
            }
            UserInfoVO userInfoVO = userMap.get(casinoMemberPO.getUserId());
            if (userInfoVO == null) {
                log.error("PG玩家信息不存在，注单信息{}", entity);
                continue;
            }
            UserLoginInfoVO userLoginInfoVO = loginVOMap.get(casinoMemberPO.getUserId());
            String betIp = userLoginInfoVO != null ? userLoginInfoVO.getIp() : "";
            Integer deviceType = userLoginInfoVO != null ? Integer.valueOf(userLoginInfoVO.getLoginTerminal()) : null;

            OrderRecordVO recordVO = new OrderRecordVO();
            recordVO.setSiteCode(userInfoVO.getSiteCode());
            recordVO.setSiteName(siteNameMap.get(recordVO.getSiteCode()));
            // 会员账号
            recordVO.setUserAccount(userInfoVO.getUserAccount());
            recordVO.setUserId(userInfoVO.getUserId());
            // 会员姓名
            recordVO.setUserName(userInfoVO.getUserName());
            // 账号类型 1测试 2正式 3商务 4置换
            recordVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
            recordVO.setAgentId(userInfoVO.getSuperAgentId());
            recordVO.setAgentAcct(userInfoVO.getSuperAgentAccount());
            // 三方会员账号
            recordVO.setCasinoUserName(account);
            // 上级代理账号 agentAcct
            recordVO.setSuperAgentName(userInfoVO.getSuperAgentName());
            // VIP等级
            recordVO.setVipGradeCode(userInfoVO.getVipGradeCode());
            recordVO.setVipRank(userInfoVO.getVipRank());
            // 三方平台
            recordVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
            // 游戏平台CODE
            recordVO.setVenueCode(venueInfoVO.getVenueCode());
            // 游戏大类
            recordVO.setVenueType(gameTypeId);
            GameInfoPO gameInfoPO = gameInfoMap.get(entity.getGameId().toString());
            if (!Objects.isNull(gameInfoPO)) {
                recordVO.setGameId(gameInfoPO.getId());
                recordVO.setThirdGameCode(gameInfoPO.getGameId());
                recordVO.setGameName(gameInfoPO.getGameI18nCode());
            }
            // 游戏编码
            recordVO.setThirdGameCode(entity.getGameId().toString());
            recordVO.setGameNo(entity.getParentBetId().toString());
            recordVO.setPlayType(recordVO.getGameName());
            recordVO.setOdds(BigDecimal.ZERO.toString());
            // 投注时间
            recordVO.setBetTime(entity.getBetTime());
            // 结算时间
            recordVO.setSettleTime(entity.getBetEndTime());
            // 变更状态
            recordVO.setChangeStatus(ChangeStatusEnum.NOT_CHANGE.getCode());
            // 投注额
            recordVO.setBetAmount(entity.getBetAmount());
            // 输赢金额
            recordVO.setWinLossAmount(entity.getWinAmount().subtract(entity.getBetAmount()));
            // 有效投注
            recordVO.setValidAmount(entity.getBetAmount());
            // 派彩金额
            recordVO.setPayoutAmount(entity.getWinAmount());
            // 注单状态
            recordVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
            // 重新结算
            if (entity.getHandsStatus() == 3) {
                recordVO.setOrderStatus(OrderStatusEnum.RESETTLED.getCode());
            }
            // 注单归类
            recordVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(recordVO.getOrderStatus()));
            // 注单ID
            recordVO.setOrderId(OrderUtil.getGameNo());
            // 三方注单ID
            recordVO.setThirdOrderId(entity.getBetId());

            recordVO.setTransactionId(entity.getBetId());
            // 订单详情
            recordVO.setOrderInfo(entity.getBetId());
            // 投注IP
            recordVO.setBetIp(betIp);
            // 币种
            recordVO.setCurrency(entity.getCurrency());
            // 设备类型
            recordVO.setDeviceType(deviceType);
            //记录原始注单
            recordVO.setParlayInfo(com.alibaba.fastjson.JSON.toJSONString(entity));
            // 创建时间
            recordVO.setCreatedTime(System.currentTimeMillis());
            recordVO.setFreeGame(entity.getTransactionType() == 3);
            String currencyCode = byPlatCurrencyCode(entity.getCurrency()).getCode();
            if (PGCurrencyEnum.isRate1000(currencyCode)) {
                // 实际金额扩大1000倍,参与计算
                recordVO.setBetAmount(recordVO.getBetAmount().multiply(BigDecimal.valueOf(1000)));
                recordVO.setWinLossAmount(recordVO.getWinLossAmount().multiply(BigDecimal.valueOf(1000)));
                recordVO.setValidAmount(recordVO.getValidAmount().multiply(BigDecimal.valueOf(1000)));
                recordVO.setPayoutAmount(recordVO.getPayoutAmount().multiply(BigDecimal.valueOf(1000)));

            }
            orderRecordList.add(recordVO);
        }
        return orderRecordList;
    }

    /**
     * 创建用户token
     *
     * @param casinoMemberVO 用户信息
     * @param venueInfoVO（包含 merchant_no、aes_key、merchant_key）
     * @param currency       币种 ISO
     * @return
     */
    private String createUserToken(CasinoMemberVO casinoMemberVO, VenueInfoVO venueInfoVO, String currency) {
        String currencyCode = byPlatCurrencyCode(currency).getCode();

        PGUserToken pgUserToken = PGUserToken.builder()
                .n(casinoMemberVO.getVenueUserAccount())
                .c(currencyCode)
                .e(System.currentTimeMillis())
                .k(venueInfoVO.getMerchantKey()) //盐（签名校验）
                .build();
        return JSON.toJSONString(pgUserToken);
        //return Encrypt.aesEncrypt(pgAesKey, pgAesIv, JSON.toJSONString(pgUserToken));
    }




    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    static {
        LANGUAGE_MAP.put("zh-CN", "zh");
        LANGUAGE_MAP.put("en-US", "en");
        LANGUAGE_MAP.put("hi-IN", "en");//PG不支持 北印度语-印度
        LANGUAGE_MAP.put("id-ID", "id");
        LANGUAGE_MAP.put("pt-BR", "pt");
        LANGUAGE_MAP.put(LanguageEnum.KO_KR.getLang(), "ko");
        LANGUAGE_MAP.put(LanguageEnum.DA_DK.getLang(), "da");
        LANGUAGE_MAP.put(LanguageEnum.DE_DE.getLang(), "de");
        LANGUAGE_MAP.put(LanguageEnum.ES_ES.getLang(), "es");
        LANGUAGE_MAP.put(LanguageEnum.FI_FI.getLang(), "fi");
        LANGUAGE_MAP.put(LanguageEnum.FR_FR.getLang(), "fr");
        LANGUAGE_MAP.put(LanguageEnum.ID_ID.getLang(), "id");
        LANGUAGE_MAP.put(LanguageEnum.IT_IT.getLang(), "it");
        LANGUAGE_MAP.put(LanguageEnum.JA_JP.getLang(), "ja");
        LANGUAGE_MAP.put(LanguageEnum.NL_NL.getLang(), "nl");
        LANGUAGE_MAP.put(LanguageEnum.NO_NO.getLang(), "no");
        LANGUAGE_MAP.put(LanguageEnum.PL_PL.getLang(), "pl");
        LANGUAGE_MAP.put(LanguageEnum.RO_RO.getLang(), "ro");
        LANGUAGE_MAP.put(LanguageEnum.RU_RU.getLang(), "ru");
        LANGUAGE_MAP.put(LanguageEnum.SV_SE.getLang(), "sv");
        LANGUAGE_MAP.put(LanguageEnum.TH_TH.getLang(), "th");
        LANGUAGE_MAP.put(LanguageEnum.TR_TR.getLang(), "tr");
        LANGUAGE_MAP.put(LanguageEnum.VI_VN.getLang(), "vi");
        // 缅甸
        LANGUAGE_MAP.put("my-MM", "my");

    }

    /**
     * 踢线
     */
    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount) {
        String url = venueDetailVO.getApiUrl() + PgServiceEnums.KICK_OUT.getPath().concat("?trace_id=").concat(UUID.randomUUID().toString());
        Map<String, String> prams = new HashMap<>();
        prams.put("operator_token", venueDetailVO.getMerchantNo());
        prams.put("secret_key", venueDetailVO.getMerchantKey());
        prams.put("player_name", venueUserAccount);
        log.info("PG-用户踢线request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.post(url, prams);
            log.info("PG-用户踢线response {}", response);
        } catch (Exception e) {
            log.error("PG-进入游戏异常【{}】", venueUserAccount, e);
        }
        return ResponseVO.success(true);
    }

    @Override
    public ResponseVO<Boolean> freeGame(FreeGameVO freeGameVO, VenueInfoVO venueInfoVO, List<CasinoMemberVO> casinoMembers) {
        String url = venueInfoVO.getApiUrl() + PgServiceEnums.FREE_GAME.getPath().concat("?trace_id=").concat(UUID.randomUUID().toString());
        Map<String, String> prams = new HashMap<>();
        prams.put("operator_token", venueInfoVO.getMerchantNo());
        prams.put("secret_key", venueInfoVO.getMerchantKey());
        prams.put("free_game_id", "252791");
        prams.put("transfer_reference", UUID.randomUUID().toString());
        prams.put("allow_multiple", "false");

        List<NameValuePair> params = convertMapToNameValuePairs(prams);
        for (CasinoMemberVO casinoMember : casinoMembers) {
            Map<String, Object> playerData = new HashMap<>();
            playerData.put("player_name", casinoMember.getVenueUserAccount());
            playerData.put("free_game_count", freeGameVO.getCount());
            playerData.put("is_unlimited_bonus_maximum_conversion_amount", false);
            playerData.put("is_unlimited_free_game_maximum_conversion_amount", false);
            playerData.put("bonus_maximum_conversion_amount", 100.50);
            playerData.put("free_game_maximum_conversion_amount", 100.50);
            playerData.put("description", "activity bonus");
            String jsonString = JSONObject.toJSONString(playerData);
            params.add(new BasicNameValuePair("player_free_games", jsonString));
        }

        log.info("PG-进入游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try {
            String response = HttpClientHandler.doPostPar(url, null, params);
            log.info("PG-免费游戏response={}", response);
        } catch (Exception e) {
            log.error("PG-免费游戏异常【{}】", freeGameVO, e);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        return ResponseVO.success(true);
    }

    private static List<NameValuePair> convertMapToNameValuePairs(Map<String, String> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return nameValuePairs;
    }

    public List<ShDeskInfoVO> queryGameList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoVO venueInfoVO =  getVenueInfo(VenuePlatformConstants.PG,null);

        JSONArray jsonArray = listAllPGGame(venueInfoVO);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String gameCode = json.getString("gameId"); //游戏代碼
                // 设置语言
                //String lang = LANGUAGE_MAP.getOrDefault(langCode, "en");
                //String gameName = getLangName(json, "nameset", lang);
                int status = json.getInteger("status");
                int releaseStatus = json.getInteger("releaseStatus");
                if (status == 1 && releaseStatus == 1) {
                    String gameName = json.getString("gameName");
                    resultList.add(ShDeskInfoVO.builder().deskNumber(gameCode).deskName(gameName).build());
                }
            }
        }
        log.info("{} 获取游戏列表列表成功, 游戏数量: {}", platformName(), resultList.size());
        return resultList;
    }


    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        List<JSONObject> resultList = Lists.newArrayList();
        String langCode = CurrReqUtils.getLanguage();
        JSONArray jsonArray = listAllPGGame(venueInfoVO);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                String gameCode = json.getString("gameId"); //游戏代碼
                // 设置语言
                //String lang = LANGUAGE_MAP.getOrDefault(langCode, "en");
                //String gameName = getLangName(json, "nameset", lang);
                int status = json.getInteger("status");
                int releaseStatus = json.getInteger("releaseStatus");
                if (status == 1 && releaseStatus == 1) {
                    String gameName = json.getString("gameName");
                    JSONObject gameJson = new JSONObject();
                    gameJson.put("deskName", gameName);
                    gameJson.put("deskNumber", gameCode);
                    resultList.add(gameJson);
                }
            }
        }
        log.info("{} 获取游戏列表列表成功, 游戏数量: {}", platformName(), resultList.size());
        return ResponseVO.success(resultList);

    }

    public VenueEnum platformEnum() {
        return VenueEnum.PG;
    }

    public String platformName() {
        return platformEnum().getVenueName();
    }


    private JSONArray listAllPGGame(VenueInfoVO venueInfoVO) {
        String url = venueInfoVO.getApiUrl() + PgServiceEnums.GAME_GET.getPath().concat("?trace_id=").concat(UUID.randomUUID().toString());
        Map<String, Object> prams = new HashMap<>();
        prams.put("operator_token", venueInfoVO.getMerchantNo());
        prams.put("secret_key", venueInfoVO.getAesKey());
        prams.put("currency", PGCurrencyEnum.CNY.getCode());
        prams.put("language", "zh-cn");
        log.info("PG-获取最新游戏request apiPath={}, request={}", url, JSON.toJSONString(prams));
        try (HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .header(Header.AUTHORIZATION, venueInfoVO.getMerchantKey())
                .timeout(30000).form(prams).execute()) {
            /*String response = HttpClientHandler.post(url, prams);
            log.info("PG-获取最新游戏response={}", response);
            JSONObject jsonObject = JSONObject.parseObject(response);*/
            log.info("{} PG获取游戏列表返回码: {}, 返回消息: {}", platformName(), response.getStatus(), response.body());

            JSONObject jsonObject = JSONObject.parseObject(response.body());
            JSONArray data = jsonObject.getJSONArray("data");
            return data;
        } catch (Exception e) {
            log.error(String.format("%s 获取游戏列表发生错误!!", platformName()), e);
        }
        return new JSONArray();
    }

    @Override
    public List<ThirdGameInfoVO> gameInfo(VenueInfoVO venueDetailVO) {
        List<ThirdGameInfoVO> ret = Lists.newArrayList();
        try {
            JSONArray data = listAllPGGame(venueDetailVO);
            for (int i = 0; i < data.size(); i++) {
                ThirdGameInfoVO thirdGameInfoVO = new ThirdGameInfoVO();
                JSONObject game = data.getJSONObject(i);
                String gameName = game.getString("gameName");
                String gameId = game.getString("gameId");
                thirdGameInfoVO.setGameCode(gameId);
                // 名称多语言
                List<GameNameVO> gameNameVOS = Lists.newArrayList();
                GameNameVO gameNameVO = new GameNameVO();
                gameNameVO.setGameName(gameName);
                gameNameVO.setLang(LanguageEnum.ZH_CN.getLang());
                gameNameVOS.add(gameNameVO);
                thirdGameInfoVO.setGameName(gameNameVOS);

                ret.add(thirdGameInfoVO);
            }
        } catch (Exception e) {
            log.error("PG-获取最新游戏error", e);
        }
        return ret;
    }


}
