package com.cloud.baowang.play.wallet.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.acelt.ACELTResultCodeEnums;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.wallet.enums.ACELTDefaultException;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.Encrypt;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.play.api.api.member.CasinoMemberApi;
import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.pg.PGCurrencyEnum;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueInfoCheckVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.wallet.enums.GameEventTypeEnums;
import com.cloud.baowang.play.wallet.enums.PGErrorEnums;
import com.cloud.baowang.play.wallet.po.UserWalletGameRecordPO;
import com.cloud.baowang.play.wallet.service.PgService;
import com.cloud.baowang.play.wallet.service.base.BaseService;
import com.cloud.baowang.play.wallet.vo.exception.PGDefaultException;
import com.cloud.baowang.play.wallet.vo.req.pg.PgAdjustmentReq;
import com.cloud.baowang.play.wallet.vo.req.pg.PgBaseReq;
import com.cloud.baowang.play.wallet.vo.req.pg.PgBetReq;
import com.cloud.baowang.play.wallet.vo.req.pg.VerifySessionReq;
import com.cloud.baowang.play.wallet.vo.res.pg.*;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class PgServiceImpl extends BaseService implements PgService {


    private static final String  pgAesKey = "abc1234567890def";

    private static final String pgAesIv = "abc1234567890def";


    private final UserCoinApi userCoinApi;

    private final UserInfoApi userInfoApi;

    private final CasinoMemberApi casinoMemberApi;


    private final PlayVenueInfoApi playVenueInfoApi;

    private final VenueUserAccountApi venueUserAccountApi;


    private final UserCoinRecordApi userCoinRecordApi;

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

    @Override
    public PGBaseRes<VerifySessionRes> sendVerification(VerifySessionReq request) {

        try {
            VenueEnum venueEnum = VenueEnum.PG;

            PGUserToken pgUserToken = parseUserTokenAndValidateVenue(request.getOperator_player_session(), venueEnum, request.getSecret_key(), request.getOperator_token());

            if (pgUserToken == null) {
                log.warn("PgService sendVerification operator and player token verification failed!");
                return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST, "operator and player token verification failed!");
            }
            VerifySessionRes verifySession = new VerifySessionRes();
            verifySession.setPlayer_name(pgUserToken.getN());

            String userId = getVenueUserAccount(pgUserToken.getN());
            if (StringUtils.isBlank(userId)) {
                log.info("{}:用户账号解析失败,不存在:{}", venueEnum.getVenueName(), userId);
                throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_USER_DATA);
            }

            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            // 转换为对应平台的货币
            String currencyCode = byPlatCurrencyCode(userInfoVO.getMainCurrency()).getCode();
            if (!currencyCode.equals(pgUserToken.getC())) {
                log.info("{}:币种与用户的法币,不一致:{}", currencyCode, pgUserToken.getC());
                throw new ACELTDefaultException(ACELTResultCodeEnums.INVALID_CURRENCY);
            }
            verifySession.setCurrency(currencyCode);
            if (userInfoVO.getAccountStatus().contains(UserStatusEnum.GAME_LOCK.getCode())) {
                log.info("{}:用户被打标签,登录锁定不允许下注:{}", venueEnum.getVenueName(), userId);
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
     * @param venueEnum             场馆枚举，包含当前操作的场馆 code
     * @return 解密后的 PGUserToken 对象
     */
    public PGUserToken parseUserTokenAndValidateVenue(String operatorPlayerSession, VenueEnum venueEnum, String merchantKey, String operator_token) {
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
                    .venueCode(venueEnum.getVenueCode())
                    .build();

            // Step 4: 通过 API 获取场馆信息
            ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
            VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();

            // Step 5: 校验场馆是否存在且处于启用状态
            if (venueInfoVO == null || !StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                log.info("该站:{} 没有分配:{} 场馆的权限", siteCode, venueEnum.getVenueCode());
                throw new ACELTDefaultException(ACELTResultCodeEnums.GAME_LOCK);
            }

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
     * 去除用户前缀,得到用户真实账号
     */
    public String getVenueUserAccount(String userAccount) {
        return venueUserAccountApi.getVenueUserAccount(userAccount);
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

    @Override
    public PGBaseRes<PgBalanceRes> queryBalance(PgBaseReq request) {
        if (!request.validate()) {
            return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST);
        }
        VenueEnum venueEnum = VenueEnum.PG;

        PGUserToken pgUserToken = parseUserTokenAndValidateVenue(request.getOperator_player_session(), venueEnum, request.getSecret_key(), request.getOperator_token());

        if (pgUserToken == null) {
            log.warn("PgService sendVerification operator and player token verification failed!");
            return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST, "operator and player token verification failed!");
        }
        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
        casinoMemberReqVO.setVenueUserAccount(request.getPlayer_name());
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.PG);
        ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
        if (casinoMember == null || casinoMember.getData() == null) {
            return PGBaseRes.failed(PGErrorEnums.INVALID_REQUEST);
        }
        CasinoMemberRespVO casinoMemberData = casinoMember.getData();
        String userAccount = casinoMemberData.getUserAccount();
        String siteCode = casinoMemberData.getSiteCode();
        // 币种校验与切换

        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userAccount(userAccount).siteCode(siteCode).build());
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

    @Override
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
        PgValidVO pgValidVO = this.betRequestValid(request, isCheck);
        if (pgValidVO.getPgErrorEnums() != PGErrorEnums.SUCCESS) {
            return PGBaseRes.failed(pgValidVO.getPgErrorEnums());
        }
        // 场馆关闭
        if (venueMaintainClosed(VenuePlatformConstants.PG, pgValidVO.getUserInfoVO().getSiteCode())) {
            log.info("{}:场馆未开启", VenueEnum.PG.getVenueName());
            return PGBaseRes.failed(pgValidVO.getPgErrorEnums());
        }
//        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
//                .siteCode(pgValidVO.getUserInfoVO().getSiteCode())
//                .venueCode(VenueCodeConstants.PG)
//                .currencyCode(pgValidVO.getUserInfoVO().getMainCurrency()).build();
//        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
//        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();

        //只有下注的时候才判断是否游戏是否开启
        if(request.getBet_amount().abs().compareTo(BigDecimal.ZERO) > 0){
            if(venueGameMaintainClosed(VenueEnum.PG.getVenueCode(),pgValidVO.getUserInfoVO().getSiteCode(),request.getGame_id())){
                log.info("{}:游戏未开启,user:{}", VenueEnum.PG.getVenueName(),request.getGame_id());
                return PGBaseRes.failed(PGErrorEnums.GAME_MAINTENANCE);
            }
        }



        // 修改金额
        String currencyCode = pgValidVO.getUserInfoVO().getMainCurrency();

        // 改变的金额
        BigDecimal balance = pgValidVO.getUserCenterCoin().getTotalAmount().subtract(request.getTransfer_amount().negate());
        BigDecimal betAmountReq = pgValidVO.getUserCenterCoin().getTotalAmount().subtract(request.getBet_amount());
        // 余额如果小于0，说明余额不足
        if (balance.compareTo(BigDecimal.ZERO) < 0 || betAmountReq.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("PG Service updateBalance error, transactionId = {}, orderNo = {}, amount = {}, beforeBalance = {}",
                    request.getTransaction_id(), request.getBet_id(), request.getTransfer_amount(), pgValidVO.getUserCenterCoin().getTotalAmount());
            return PGBaseRes.failed(PGErrorEnums.INSUFFICIENT_BALANCE_CANT_BET);
        }

        //
        if (request.getWin_amount().subtract(request.getBet_amount()).compareTo(request.getTransfer_amount()) != 0) {
            log.error("PG 电子体育 玩家的输赢金额不等赢得金额减去投注金额");
            return PGBaseRes.failed(PGErrorEnums.BET_FAILED_EXCEPTION);
        }

        try {
            //修改余额 记录账变
            CoinRecordResultVO coinRecordResultVO = updateBalanceBetPayOutPG(pgValidVO.getUserInfoVO(), request.getBet_id(),
                    request.getBet_amount(), request.getWin_amount(), request.getTransaction_id());
            PgAmountBetRes pgAmountBetRes = new PgAmountBetRes();
            pgAmountBetRes.setUpdated_time(request.getUpdated_time());
            pgAmountBetRes.setCurrency_code(request.getCurrency_code());
            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS, AMOUNT_LESS_ZERO -> {
                    pgAmountBetRes.setBalance_amount(pgValidVO.getUserCenterCoin().getTotalAmount().add(request.getTransfer_amount()).setScale(2, RoundingMode.DOWN));
                    pgAmountBetRes.setReal_transfer_amount(request.getReal_transfer_amount().setScale(2, RoundingMode.DOWN));
                    if (PGCurrencyEnum.isRate1000(currencyCode)) {
                        pgAmountBetRes.setBalance_amount(pgAmountBetRes.getBalance_amount().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                        //pgAmountBetRes.setReal_transfer_amount(pgAmountBetRes.getReal_transfer_amount().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN));
                    }
                    yield PGBaseRes.success(pgAmountBetRes);
                }
                case REPEAT_TRANSACTIONS -> {
                    pgAmountBetRes.setBalance_amount(pgValidVO.getUserCenterCoin().getTotalAmount().setScale(2, RoundingMode.DOWN));
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
        ResponseVO<List<UserCoinRecordVO>> userCoinResp = userCoinRecordApi.getUserCoinRecords(userCoinRecordRequestVO);
        if (!userCoinResp.isOk()) {
            log.info("PG:查询账变记录异常,:{}", userCoinRecordRequestVO);
            throw new PGDefaultException(PGErrorEnums.SERVER_INTERNAL_ERROR, transactionId);
        }
        List<UserCoinRecordVO> userCoinRecord = userCoinResp.getData();
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
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }

    private UserWalletGameRecordPO buildUserWalletGamePO(PgBetReq request, BigDecimal balance) {
        UserWalletGameRecordPO userWalletGameRecordPO = new UserWalletGameRecordPO();
        userWalletGameRecordPO.setTransactionId(request.getTransaction_id());
        userWalletGameRecordPO.setBetNum(request.getBet_id());
        userWalletGameRecordPO.setUserAccount(request.getPlayer_name());
        userWalletGameRecordPO.setVenueCode(VenuePlatformConstants.PG);
        userWalletGameRecordPO.setCurrency(request.getCurrency_code());
        GameEventTypeEnums gameEventType = request.getTransfer_amount().compareTo(BigDecimal.ZERO) > 0 ?
                GameEventTypeEnums.PAYOUT : GameEventTypeEnums.BET;
        userWalletGameRecordPO.setEventType(gameEventType.getCode());
        userWalletGameRecordPO.setAmount(request.getTransfer_amount());
        userWalletGameRecordPO.setBeforeBalance(balance);
        userWalletGameRecordPO.setAfterBalance(balance.add(request.getTransfer_amount()));
        userWalletGameRecordPO.setMonthYear(Date.from(Instant.ofEpochMilli(request.getUpdated_time())));
        return userWalletGameRecordPO;
    }

    private PgValidVO betRequestValid(PgBaseReq request, boolean isCheck) {
        PgValidVO validVO = new PgValidVO();
        if (!request.validate()) {
            log.error("betRequestValid validate fail");
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        PGUserToken pgUserToken = parseUserTokenAndValidateVenue(request.getOperator_player_session(), VenueEnum.PG, request.getSecret_key(), request.getOperator_token());
        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
        casinoMemberReqVO.setVenueUserAccount(request.getPlayer_name());
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.PG);
        ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);

        if (!casinoMember.isOk()) {
            log.error("PG queryUserInfoByAccount userName[{}] not find.", request.getPlayer_name());
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        String userId = casinoMember.getData().getUserId();
        if (Objects.isNull(userId)) {
            log.error("PG queryUserInfoByAccount userName[{}] not find.", request.getPlayer_name());
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        // 校验密钥
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getData().getSiteCode()).build());
        if (pgUserToken == null) {
            if (isCheck) {
                validVO.setUserInfoVO(userInfoVO);
                validVO.setUserCenterCoin(userCenterCoin);
                validVO.setPgErrorEnums(PGErrorEnums.SUCCESS);
                return validVO;
            }
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        //投注验证币种
        if (!request.getCurrency_code().equals(pgUserToken.getC())) {
            log.warn("币种校验失败");
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        //投注与实际交易金额不一致
        if (request.getTransfer_amount().compareTo(request.getReal_transfer_amount()) != 0) {
            log.error("投注与实际交易金额不一致");
            validVO.setPgErrorEnums(PGErrorEnums.BET_AMOUNT);
            return validVO;
        }


        if (Objects.isNull(userInfoVO)) {
            log.error("PG queryUserInfoByAccount userName[{}] not find.", request.getPlayer_name());
            validVO.setPgErrorEnums(PGErrorEnums.USER_NOT_EXIST);
            return validVO;
        }
        // 游戏锁定
        if (userGameLock(userInfoVO)) {
            log.error("PG queryUserInfoByAccount userName[{}] game lock.", request.getPlayer_name());
            validVO.setPgErrorEnums(PGErrorEnums.SERVER_INTERNAL_ERROR);
            return validVO;
        }
        validVO.setUserInfoVO(userInfoVO);
        if (Objects.isNull(userCenterCoin)) {
            validVO.setPgErrorEnums(PGErrorEnums.WALLET_NOT_EXIST);
            return validVO;
        }
        validVO.setPgErrorEnums(PGErrorEnums.SUCCESS);
        validVO.setUserCenterCoin(userCenterCoin);
        return validVO;
    }

    private PgValidVO betRequestValidForAdjuest(PgBaseReq request, boolean isCheck) {
        PgValidVO validVO = new PgValidVO();
        if (!request.validate()) {
            log.error("betRequestValid validate fail");
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        PGUserToken pgUserToken = new PGUserToken();
        CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
        casinoMemberReqVO.setVenueUserAccount(request.getPlayer_name());
        casinoMemberReqVO.setVenueCode(VenuePlatformConstants.PG);
        ResponseVO<CasinoMemberRespVO> casinoMember = casinoMemberApi.getCasinoMember(casinoMemberReqVO);

        if (!casinoMember.isOk()) {
            log.error("PG queryUserInfoByAccount userName[{}] not find.", request.getPlayer_name());
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        String userId = casinoMember.getData().getUserId();
        if (Objects.isNull(userId)) {
            log.error("PG queryUserInfoByAccount userName[{}] not find.", request.getPlayer_name());
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        // 校验密钥
        String siteCode = casinoMember.getData().getSiteCode();
        SiteVenueInfoCheckVO checkVO = SiteVenueInfoCheckVO.builder()
                .siteCode(siteCode)
                .venueCode(VenuePlatformConstants.PG)
                .build();
        ResponseVO<VenueInfoVO> venueInfoVOResponseVO = playVenueInfoApi.getSiteVenueInfoByVenueCode(checkVO);
        VenueInfoVO venueInfoVO = venueInfoVOResponseVO.getData();
        if (!StringUtils.equalsIgnoreCase(request.getSecret_key(), venueInfoVO.getAesKey())
                || !StringUtils.equals(request.getOperator_token(), venueInfoVO.getMerchantNo())) {
            log.error("PG operator_token：{} 或者 secret_key:{} 不一致.", request.getSecret_key(), request.getOperator_token());
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(casinoMember.getData().getSiteCode()).build());
        if (pgUserToken == null) {
            if (isCheck) {
                validVO.setUserInfoVO(userInfoVO);
                validVO.setUserCenterCoin(userCenterCoin);
                validVO.setPgErrorEnums(PGErrorEnums.SUCCESS);
                return validVO;
            }
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        //

        // 转换为对应平台的货币
        String currencyCode = byPlatCurrencyCode(userInfoVO.getMainCurrency()).getCode();
        //投注验证币种
        if (!request.getCurrency_code().equals(currencyCode)) {
            log.warn("币种校验失败");
            validVO.setPgErrorEnums(PGErrorEnums.INVALID_REQUEST);
            return validVO;
        }
        //投注与实际交易金额不一致
        if (request.getTransfer_amount().compareTo(request.getReal_transfer_amount()) != 0) {
            log.error("投注与实际交易金额不一致");
            validVO.setPgErrorEnums(PGErrorEnums.BET_AMOUNT);
            return validVO;
        }


        if (Objects.isNull(userInfoVO)) {
            log.error("PG queryUserInfoByAccount userName[{}] not find.", request.getPlayer_name());
            validVO.setPgErrorEnums(PGErrorEnums.USER_NOT_EXIST);
            return validVO;
        }
        // 游戏锁定
        /*if (userGameLock(userInfoVO)) {
            log.error("PG queryUserInfoByAccount userName[{}] game lock.", request.getPlayer_name());
            validVO.setPgErrorEnums(PGErrorEnums.SERVER_INTERNAL_ERROR);
            return validVO;
        }*/
        validVO.setUserInfoVO(userInfoVO);
        if (Objects.isNull(userCenterCoin)) {
            validVO.setPgErrorEnums(PGErrorEnums.WALLET_NOT_EXIST);
            return validVO;
        }
        validVO.setPgErrorEnums(PGErrorEnums.SUCCESS);
        validVO.setUserCenterCoin(userCenterCoin);
        return validVO;
    }


    @Override
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

        PgValidVO pgValidVO = this.betRequestValidForAdjuest(request, true);
        if (pgValidVO.getPgErrorEnums() != PGErrorEnums.SUCCESS) {
            return PGBaseRes.failed(pgValidVO.getPgErrorEnums());
        }
        if (venueMaintainClosed(VenuePlatformConstants.PG, pgValidVO.getUserInfoVO().getSiteCode())) {
            log.info("{}:场馆未开启", VenueEnum.PG.getVenueName());
            return PGBaseRes.failed(PGErrorEnums.SERVER_INTERNAL_ERROR);
        }
        // 余额如果小于0，说明余额不足
        BigDecimal balance = pgValidVO.getUserCenterCoin().getTotalAmount().add(request.getTransfer_amount());
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("PG Service processAdjustBet error, request = {}, beforeBalance = {}", request, pgValidVO.getUserCenterCoin().getTotalAmount());
            return PGBaseRes.failed(PGErrorEnums.INSUFFICIENT_BALANCE_CANT_BET);
        }
        String currencyCode = request.getCurrency_code();

        try {
            //修改余额 记录账变
            CoinRecordResultVO coinRecordResultVO = this.updateBalance(pgValidVO.getUserInfoVO(), request.getAdjustment_transaction_id(),
                    request.getCurrency_code(), request.getTransfer_amount());
            PgAdjustAmountRes adjustAmountRes = new PgAdjustAmountRes();
            adjustAmountRes.setUpdated_time(request.getAdjustment_time());
            adjustAmountRes.setAdjust_amount(request.getTransfer_amount());
            adjustAmountRes.setReal_transfer_amount(request.getReal_transfer_amount());
            return switch (coinRecordResultVO.getResultStatus()) {
                case SUCCESS, AMOUNT_LESS_ZERO -> {
                    adjustAmountRes.setBalance_before(pgValidVO.getUserCenterCoin().getTotalAmount());
                    adjustAmountRes.setBalance_after(pgValidVO.getUserCenterCoin().getTotalAmount().add(request.getTransfer_amount()));
                    if (PGCurrencyEnum.isRate1000(currencyCode)) {
                        adjustAmountRes.setBalance_before(adjustAmountRes.getBalance_before().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                        adjustAmountRes.setBalance_after(adjustAmountRes.getBalance_after().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                        adjustAmountRes.setAdjust_amount(request.getTransfer_amount().divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN));
                    }
                    yield PGBaseRes.success(adjustAmountRes);
                }
                case REPEAT_TRANSACTIONS -> {
                    adjustAmountRes.setBalance_before(pgValidVO.getUserCenterCoin().getTotalAmount().subtract(request.getTransfer_amount()).setScale(2, RoundingMode.DOWN));
                    adjustAmountRes.setBalance_after(pgValidVO.getUserCenterCoin().getTotalAmount().setScale(2, RoundingMode.DOWN));
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
}
