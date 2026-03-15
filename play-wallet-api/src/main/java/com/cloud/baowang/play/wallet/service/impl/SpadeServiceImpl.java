package com.cloud.baowang.play.wallet.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.spade.SpadeAcctInfo;
import com.cloud.baowang.play.api.vo.spade.enums.SpadeCurrencyEnum;
import com.cloud.baowang.play.api.vo.spade.enums.SpadeResCodeEnum;
import com.cloud.baowang.play.api.vo.spade.enums.SpadeTransferTypeEnum;
import com.cloud.baowang.play.api.vo.spade.req.SpadeBalanceReq;
import com.cloud.baowang.play.api.vo.spade.req.SpadeTransferReq;
import com.cloud.baowang.play.api.vo.spade.res.SpadeBalanceRes;
import com.cloud.baowang.play.api.vo.spade.res.SpadeBaseRes;
import com.cloud.baowang.play.api.vo.spade.res.SpadeTransferRes;
import com.cloud.baowang.play.api.vo.spade.utils.SpadeDigestUtils;
import com.cloud.baowang.play.api.vo.spade.utils.SpadeUtil;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.wallet.service.SpadeService;
import com.cloud.baowang.play.wallet.service.base.BaseService;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class SpadeServiceImpl  extends BaseService implements SpadeService {
    private final static String NULL_STR = "null";

    private final UserCoinRecordApi userCoinRecordApi;
    private final UserCoinApi userCoinApi;
    private final VenueUserAccountApi venueUserAccountApi;
    /**
     * 4.3.1 查询用户余额 接口名：getBalance
     */
    @Override
    public Object getBalance(SpadeBalanceReq vo, String digest) {
        try {
            String acctId = vo.getAcctId();
            if (StrUtil.isEmpty(acctId) || acctId.equals(NULL_STR) || acctId.split("_").length != 2) {
                log.error("spade balance 但参数不全, acctId:{} ", acctId);
                return SpadeResCodeEnum.CODE_105.toResVO(new SpadeBaseRes());
            }
            String[] split = acctId.split("_");
            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
            casinoMemberReqVO.setUserId(split[1]);
            casinoMemberReqVO.setVenueCode(VenueEnum.SPADE.getVenueCode());

            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
            if (!respVO.isOk() || respVO.getData() == null) {
                log.error("spade balance casinoMember不存在, balance, 参数:{} ", vo);
                return SpadeResCodeEnum.C_50100.toResVO(new SpadeBaseRes());
            }
            CasinoMemberRespVO casinoMember = respVO.getData();
            UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} spade balance userInfoVO不存在, balance, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.C_50100.toResVO();
            }
            if (userGameLock(userInfoVO)) {
                log.error("{} getBalance game lock, user参数:{} ", VenueEnum.SPADE.getVenueCode(), userInfoVO);
                return SpadeResCodeEnum.C_50102.toResVO();
            }
            String venueCode = casinoMember.getVenueCode();
            List<String> venueCodes = Lists.newArrayList(VenueEnum.SPADE.getVenueCode());
            //NOTE 查询场馆状态
            if (!venueCodes.contains(venueCode)){
                log.error("{} spade balance venueInfoVO不存在或不开放, balance, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.C_5003.toResVO();
            }
            VenueInfoVO venueInfoVO= venueUserAccountApi.getVenueInfoByVenueCode(VenueEnum.SPADE.getVenueCode());
//            //NOTE 验证MD5
            if (StrUtil.isEmpty(digest) || StrUtil.isEmpty(vo.getBody()) || (!SpadeDigestUtils.checkDigest(vo.getBody(), venueInfoVO.getMerchantKey(), digest))) {
                return SpadeResCodeEnum.C_50104.toResVO();
            }

            SpadeCurrencyEnum spadeCurrencyEnum = SpadeCurrencyEnum.fromCode(userInfoVO.getMainCurrency());

            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
            SpadeAcctInfo acctInfo = SpadeAcctInfo.builder().acctId(casinoMember.getVenueUserAccount()).userName(casinoMember.getUserAccount())
                    .balance(userCenterCoin.getTotalAmount()).currency(spadeCurrencyEnum.name()).build();

            return SpadeResCodeEnum.CODE_0.toResVO(SpadeBalanceRes.builder().acctInfo(acctInfo)
                    .merchantCode(venueInfoVO.getMerchantNo()).serialNo(SpadeUtil.generalSerialNo()).build());
        } catch (Exception e) {
            log.error("{} spade balance Exception, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
        }
        return SpadeResCodeEnum.CODE_1.toResVO();
    }
    @Override
    public Object transfer(SpadeTransferReq vo, String digest) {
        try {
            String acctId = vo.getAcctId();
            String merchantCode= vo.getMerchantCode();
            if (StrUtil.isEmpty(acctId) || acctId.equals(NULL_STR) || acctId.split("_").length != 2) {
                log.error("spade balance 但参数不全, acctId:{} ", acctId);
                return SpadeResCodeEnum.CODE_105.toResVO(new SpadeBaseRes());
            }
            String[] split = acctId.split("_");
            CasinoMemberReqVO casinoMemberReqVO = new CasinoMemberReqVO();
            casinoMemberReqVO.setUserId(split[1]);
            casinoMemberReqVO.setVenueCode(VenueEnum.SPADE.getVenueCode());
            ResponseVO<CasinoMemberRespVO> respVO = casinoMemberApi.getCasinoMember(casinoMemberReqVO);
            if (!respVO.isOk() || respVO.getData() == null) {
                log.error("spade balance casinoMember不存在, balance, 参数:{} ", vo);
                return SpadeResCodeEnum.C_50100.toResVO(new SpadeBaseRes());
            }
            CasinoMemberRespVO casinoMember = respVO.getData();
            UserInfoVO userInfoVO = userInfoApi.getByUserId(casinoMember.getUserId());
            if (userInfoVO == null) {
                log.error("{} spade balance userInfoVO不存在, balance, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.C_50100.toResVO();
            }
            String venueCode = casinoMember.getVenueCode();
            List<String> venueCodes = Lists.newArrayList(VenueEnum.SPADE.getVenueCode());
            //NOTE 查询场馆状态
            if (!venueCodes.contains(venueCode)){
                log.error("{} spade balance venueInfoVO不存在或不开放, balance, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.C_5003.toResVO();
            }
            VenueInfoVO venueInfoVO= venueUserAccountApi.getVenueInfoByVenueCode(VenueEnum.SPADE.getVenueCode());
            //NOTE 验证MD5
            if (StrUtil.isEmpty(digest) || StrUtil.isEmpty(vo.getBody()) || (!SpadeDigestUtils.checkDigest(vo.getBody(), venueInfoVO.getMerchantKey(), digest))) {
                return SpadeResCodeEnum.C_50104.toResVO();
            }

            SpadeTransferTypeEnum typeEnum = SpadeTransferTypeEnum.fromCode(vo.getType());
            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userInfoVO.getUserId()).build());
            SpadeTransferRes transferRes = SpadeTransferRes.builder()
                    .transferId(vo.getTransferId())
                    .acctId(acctId)
                    .balance(userCenterCoin.getTotalAmount())
                    .serialNo(SpadeUtil.generalSerialNo())
                    .build();
            //账变金额为 < 0 不处理
            if (vo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("{} transfer, 无账变金额, 请求参数: {}", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.CODE_0.toResVO(transferRes);
            }
            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVO.setUserId(userInfoVO.getUserId());
            userCoinAddVO.setCoinValue(vo.getAmount());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            userCoinAddVO.setOrderNo(vo.getTicketId());
            userCoinAddVO.setRemark(vo.getReferenceId());
            switch (Objects.requireNonNull(typeEnum)) {
                case PLACE_BET:
                    userCoinAddVO.setOrderNo(vo.getTransferId());
                    userCoinAddVO.setRemark(vo.getSerialNo());
                    getBetCoinAddVO(userCoinAddVO);
                    if (!StatusEnum.OPEN.getCode().equals(venueInfoVO.getStatus())) {
                        log.error("{} SPADE transfer venueInfoVO不存在或不开放, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
                        return SpadeResCodeEnum.C_5003.toResVO();
                    }
                    if (venueGameMaintainClosed(venueInfoVO.getVenueCode(),casinoMember.getSiteCode(),vo.getGameCode())){
                        log.info("{}:游戏未开启", VenueEnum.SPADE.getVenueName());
                        return SpadeResCodeEnum.C_5003.toResVO(transferRes);
                    }
                    if (userGameLock(userInfoVO)) {
                        log.error("{} transfer bet game lock, user参数:{} ", VenueEnum.SPADE.getVenueCode(), userInfoVO);
                        return SpadeResCodeEnum.C_50102.toResVO();
                    }
                    //账变金额为 < 0 不处理
                    if (vo.getAmount().compareTo(userCenterCoin.getTotalAmount()) > 0) {
                        log.error("{} transfer PLACE_BET, 余额不足, 请求参数: {}", VenueEnum.SPADE.getVenueCode(), vo);
                        return SpadeResCodeEnum.C_50110.toResVO(transferRes);
                    }
                    break;
                case PAYOUT:
                    getPayoutCoinAddVO(userCoinAddVO);
                    break;
                case BONUS:
                    log.info("SPADE transfer bonus operation: {}", vo);
                    getPayoutCoinAddVO(userCoinAddVO);
                    break;
                case CANCEL_BET:
                    log.info("SPADE transfer cancel bet operation: {}", vo);
                    userCoinAddVO.setOrderNo(vo.getReferenceId());
                    userCoinAddVO.setRemark(vo.getSerialNo());
                    getCancelBetCoinAddVO(userCoinAddVO);
                    //NOTE 确认成功下注
                    UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
                    userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
                    userCoinRecordVo.setUserId(userInfoVO.getUserId());
                    userCoinRecordVo.setOrderNo(userCoinAddVO.getOrderNo());
                    userCoinRecordVo.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                    ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(userCoinRecordVo);
                    if (CollUtil.isEmpty(userCoinRecords.getData())) {
                        log.error("{} transfer 失败, 当前下注单不存在，不做取消账变操作, 请求参数: {}", VenueEnum.SPADE.getVenueCode(), vo);
                        return SpadeResCodeEnum.CODE_0.toResVO(transferRes);
                    }
                    break;
            }
            if (StrUtil.isEmpty(userCoinAddVO.getOrderNo())) {
                return SpadeResCodeEnum.CODE_105.toResVO(transferRes);
            }
            //NOTE 去重操作
            UserCoinRecordRequestVO userCoinRecordVo = new UserCoinRecordRequestVO();
            userCoinRecordVo.setUserAccount(userInfoVO.getUserAccount());
            userCoinRecordVo.setUserId(userInfoVO.getUserId());
            userCoinRecordVo.setOrderNo(userCoinAddVO.getOrderNo());
            userCoinRecordVo.setRemark(userCoinAddVO.getRemark());
            ResponseVO<List<UserCoinRecordVO>> userCoinRecords = userCoinRecordApi.getUserCoinRecords(userCoinRecordVo);
            if (CollUtil.isNotEmpty(userCoinRecords.getData())) {
                log.error("{} transfer失败, 当前reference已经被处理, 请求参数: {}", VenueEnum.SPADE.getVenueCode(), vo);
                return SpadeResCodeEnum.CODE_0.toResVO(transferRes);
            }
            CoinRecordResultVO recordResultVO = userCoinApi.addCoin(userCoinAddVO);
            switch (recordResultVO.getResultStatus()) {
                case INSUFFICIENT_BALANCE -> SpadeResCodeEnum.C_50110.toResVO(transferRes);
                //NOTE 走默认： case FAIL, WALLET_NOT_EXIST -> spadeResCodeEnum.CODE_1.toResVO(transferRes);
                //NOTE 重复操作, 合并到账变状态里, 算成功.
                case REPEAT_TRANSACTIONS -> SpadeResCodeEnum.CODE_0.toResVO(transferRes);
                case SUCCESS -> {
                    transferRes.setBalance(recordResultVO.getCoinAfterBalance());
                    transferRes.setMerchantTxId(recordResultVO.getId());
                    SpadeResCodeEnum.CODE_0.toResVO(transferRes);
                }
                default -> SpadeResCodeEnum.CODE_1.toResVO(transferRes);
            }
            return transferRes;
        } catch (Exception e) {
            log.error("{} transfer Exception, 参数:{} ", VenueEnum.SPADE.getVenueCode(), vo);
        }
        return SpadeResCodeEnum.CODE_1.toResVO();
    }




    /**
     * 投注账变
     */
    private static void getBetCoinAddVO(UserCoinAddVO userCoinAddVO) {
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
    }

    /**
     * 派彩账变
     */
    private static void getPayoutCoinAddVO(UserCoinAddVO userCoinAddVO) {
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
    }

    /**
     * 取消下注
     */
    private static void getCancelBetCoinAddVO(UserCoinAddVO userCoinAddVO) {
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
    }
}
