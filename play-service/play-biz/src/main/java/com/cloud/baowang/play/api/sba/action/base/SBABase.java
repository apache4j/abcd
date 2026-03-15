package com.cloud.baowang.play.api.sba.action.base;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.wallet.api.enums.usercoin.FreezeFlagEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.enums.SBDefaultException;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.sba.SBToCoinAddVO;
import com.cloud.baowang.play.api.vo.sba.SBUserCoinAddVO;
import com.cloud.baowang.play.api.vo.third.CheckActivityVO;
import com.cloud.baowang.play.api.vo.transferRecordVO.TransferRecordResultVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.service.TransferRecordService;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Slf4j
@AllArgsConstructor
@Service
public class SBABase extends GameBaseService {

    private final TransferRecordService transferRecordService;


    public void checkGameUser(UserInfoVO userInfoVO) {
        BigDecimal venueAmount = checkJoinActivity(userInfoVO.getUserId(),userInfoVO.getSiteCode(),VenuePlatformConstants.SBA);

        //不允许下注
        if (venueAmount == null || venueAmount.compareTo(BigDecimal.ZERO) > 0) {
            log.info("不允许下注.调用游戏校验失败:result:{}",  venueAmount);
            throw new SBDefaultException(SBResultCode.SYSTEM_ERROR);
        }
    }


    protected String getUserAccount(String account) {
        String userAccount = venueUserAccountConfig.getVenueUserAccount(account);
        if (StringUtils.isBlank(userAccount)) {
            throw new SBDefaultException(SBResultCode.ACCOUNT_DOES_NOT_EXIST);
        }
        return userAccount;
    }

    protected UserInfoVO getUserInfo(String account) {
        String userId = venueUserAccountConfig.getVenueUserAccount(account);
        if (StringUtils.isBlank(userId)) {
            throw new SBDefaultException(SBResultCode.ACCOUNT_DOES_NOT_EXIST);
        }

        UserInfoVO userInfoVOS = getByUserId(userId);
        if (ObjectUtils.isEmpty(userInfoVOS)) {
            log.info("{},getUserAccount,用户不存在,userInfoVOS  userAccount:[{}]", VenueEnum.SBA.getVenueName(), userId);
            throw new SBDefaultException(SBResultCode.ACCOUNT_DOES_NOT_EXIST);
        }

        return userInfoVOS;
    }


    /**
     * 重复请求验证
     *
     * @param api     接口名称
     * @param orderId 订单
     */
    private void validRepeat(String api, String orderId) {
        String key = String.format(RedisConstants.SABA_API_REPEAT_REQUEST, api, orderId);
        Long count = RedisUtil.getValue(key);

        //在某个时间段请求超过3次则直接拒绝
        if (ObjectUtils.isNotEmpty(count) && count >= 3) {
            throw new SBDefaultException(SBResultCode.THE_SYSTEM_IS_BUSY);
        }
    }


    /**
     * 验证订单是否存在
     */
    protected TransferRecordResultVO validateOrder(TransferRecordResultVO transferRecordVO) {
        return transferRecordService.getTransferRecord(transferRecordVO);
    }



    protected SBResultCode toCoin(SBActionEnum actionEnum, SBToCoinAddVO coinAddVO, TransferRecordResultVO transferRecordVO) {
        String refId = coinAddVO.getOrderId();
        String userId = coinAddVO.getUserId();
        BigDecimal amount = coinAddVO.getAmount();
        String actionName = VenuePlatformConstants.SBA;


        //结算逻辑累计次数
        if (actionEnum.getCode().equals(SBActionEnum.SETTLE.getCode()) || actionEnum.getCode().equals(SBActionEnum.UN_SETTLE.getCode()) ||
                actionEnum.getCode().equals(SBActionEnum.RE_SETTLE.getCode())) {
            //每次重结算并且是是给用户加钱就累计+1
            if (coinAddVO.getType() && amount.compareTo(BigDecimal.ZERO) > 0) {
                transferRecordVO.setSettleCount(transferRecordVO.getSettleCount() + 1);
            }
        }

        String repeatKey = String.format(RedisConstants.SABA_API_REPEAT_REQUEST, actionEnum.getCode(), refId);

        // 验证重复请求
        validRepeat(actionEnum.getCode(), refId);

        log.info("{} {} refId:[{}], userAccount:[{}], amount:{}, type:{}", actionName, "开始执行上下分", refId, userId, amount, coinAddVO.getType());
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("{} {} refId:[{}], userAccount:[{}], amount:{}, type:{}", actionName, "下注金额不能为负数", refId, userId, amount, coinAddVO.getType());
            return SBResultCode.INVALID_AMOUNT;
        }
        UserInfoVO userInfo = getUserInfo(userId);
        if (ObjectUtils.isEmpty(userInfo)) {
            log.info("{} {} refId:[{}], userAccount:[{}], amount:{}, type:{}", actionName, "用户不存在", refId, userId, amount, coinAddVO.getType());
            return SBResultCode.ACCOUNT_DOES_NOT_EXIST;
        }
        userId = userInfo.getUserId();
        UserCoinWalletVO userWallet = getUserCenterCoin(userId);
        if (ObjectUtils.isEmpty(userWallet)) {
            log.info("{} {} refId:[{}], userAccount:[{}], amount:{}, type:{}", actionName, "用户钱包不存在", refId, userId, amount, coinAddVO.getType());
            return SBResultCode.SYSTEM_ERROR;
        }

        //冻结支出不校验
        if(coinAddVO.getFreezeFlagEnum() != null && !coinAddVO.getFreezeFlagEnum().equals(FreezeFlagEnum.UNFREEZE.getCode())){
            //扣费的过程中,所有的冻结跟支出类型都要 校验 余额
            if (coinAddVO.getCoinBalanceTypeEnum() != null && (CoinBalanceTypeEnum.FREEZE.getCode().equals(coinAddVO.getCoinBalanceTypeEnum()) ||
                    CoinBalanceTypeEnum.EXPENSES.getCode().equals(coinAddVO.getCoinBalanceTypeEnum()))) {
                if (!hasSufficientBalance(userWallet, amount)) {
                    log.info("{} {} refId:[{}], userAccount:[{}], amount:{}, type:{}", actionName, "余额不足", refId, userId, amount, coinAddVO.getType());
                    return SBResultCode.INSUFFICIENT_PLAYER_BALANCE;
                }
            }

        }


        UserCoinAddVO userCoinAddVO = buildAddTransferRecordVO(actionEnum, coinAddVO, userWallet, userInfo);
        userCoinAddVO.setOrderNo(transferRecordVO.getTransId());

        CoinRecordResultVO coinRecordResultVO = null;
        try {
            coinRecordResultVO = transferRecordService.addTransferRecordCoin(transferRecordVO, userCoinAddVO);
        } catch (Exception e) {
            log.info("{} {} refId:[{}], userAccount:[{}], amount:{}, type:{}", actionName, "调用上下分接口失败", refId, userId, amount, coinAddVO.getType());
            return SBResultCode.SYSTEM_ERROR;
        }

        if (!coinRecordResultVO.getResult()) {
            log.info("{} {} refId:[{}], userAccount:[{}], amount:{}, type:{}", actionName, "调用上下分接口失败", refId, userId, amount, coinAddVO.getType());
            RedisUtil.incrAndExpirationFirst(repeatKey, 1, 60 * 10);
            return SBResultCode.SYSTEM_ERROR;
        }

        return handleCoinRecordResult(coinRecordResultVO);
    }


    /**
     * @param orderId             唯一ID
     * @param userId              玩家账号
     * @param amount              账变金额
     * @param currency            币种
     * @param type                true = 加款，false = 扣款
     * @param userInfoVO          用户信息
     * @param coinBalanceTypeEnum
     * @param freezeFlagEnum      扣款类型
     */
    public static UserCoinAddVO buildUserCoinAddVO(String orderId, String userId, BigDecimal amount
            , String currency, boolean type, UserInfoVO userInfoVO, String coinBalanceTypeEnum,
                                                   Integer freezeFlagEnum, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderId);
        userCoinAddVO.setCurrency(currency);
        userCoinAddVO.setRemark(remark);

        WalletEnum.CoinTypeEnum coinTypeEnum = null;
        WalletEnum.BusinessCoinTypeEnum businessCoinTypeEnum = null;
        WalletEnum.CustomerCoinTypeEnum customerCoinTypeEnum = null;


        if (!type) {
            coinTypeEnum = WalletEnum.CoinTypeEnum.GAME_BET;
            businessCoinTypeEnum = WalletEnum.BusinessCoinTypeEnum.GAME_BET;
            customerCoinTypeEnum = WalletEnum.CustomerCoinTypeEnum.GAME_BET;
        } else {
            coinTypeEnum = WalletEnum.CoinTypeEnum.GAME_PAYOUT;
            businessCoinTypeEnum = WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT;
            customerCoinTypeEnum = WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT;
        }
        userCoinAddVO.setCoinType(coinTypeEnum.getCode());
        userCoinAddVO.setBalanceType(coinBalanceTypeEnum);
        userCoinAddVO.setBusinessCoinType(businessCoinTypeEnum.getCode());
        userCoinAddVO.setCustomerCoinType(customerCoinTypeEnum.getCode());
        userCoinAddVO.setUserId(userId);
        userCoinAddVO.setCoinValue(amount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        if (ObjectUtil.isNotEmpty(freezeFlagEnum)) {
            userCoinAddVO.setFreezeFlag(freezeFlagEnum);
        }
        return userCoinAddVO;
    }

    private UserCoinAddVO buildAddTransferRecordVO(SBActionEnum sbActionEnum, SBToCoinAddVO coinAddVO, UserCoinWalletVO userWallet, UserInfoVO userInfo) {
        UserCoinAddVO userCoinAddVO = buildUserCoinAddVO(
                coinAddVO.getOrderId(),
                userInfo.getUserId(),
                coinAddVO.getAmount(),
                userWallet.getCurrency(),
                coinAddVO.getType(),
                userInfo,
                coinAddVO.getCoinBalanceTypeEnum(),
                coinAddVO.getFreezeFlagEnum(),
                coinAddVO.getRemark()
        );

        if (SBActionEnum.RE_SETTLE.getCode().equals(sbActionEnum.getCode()) || SBActionEnum.UN_SETTLE.getCode().equals(sbActionEnum.getCode())) {
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
            if (coinAddVO.getType()) {
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
            } else {
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
                userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
            }
        }

        return userCoinAddVO;

    }


    private boolean hasSufficientBalance(UserCoinWalletVO userWallet, BigDecimal amount) {
        return userWallet.getTotalAmount().subtract(amount).compareTo(BigDecimal.ZERO) >= 0;
    }

    private SBResultCode handleCoinRecordResult(CoinRecordResultVO coinRecordResult) {
        if (ObjectUtils.isEmpty(coinRecordResult) || !coinRecordResult.getResult()) {
            return SBResultCode.SYSTEM_ERROR;
        }

        return switch (coinRecordResult.getResultStatus()) {
            case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST -> SBResultCode.INSUFFICIENT_PLAYER_BALANCE;
            //重复的交易理解为成功
            case REPEAT_TRANSACTIONS, SUCCESS -> SBResultCode.SUCCESS;
            case AMOUNT_LESS_ZERO -> SBResultCode.INVALID_AMOUNT;
            case FAIL -> SBResultCode.SYSTEM_ERROR;
        };
    }


    /**
     * @param orderId             账变唯一ID
     * @param userId         用户账号
     * @param addAmount           加款金额
     * @param subAmount           扣款金额
     * @param coinBalanceTypeEnum 支收类型
     * @param freezeFlagEnum      冻结标记
     */
    public static SBToCoinAddVO getBuilderCoinAdd(String orderId, String userId, BigDecimal addAmount,
                                                  BigDecimal subAmount, CoinBalanceTypeEnum coinBalanceTypeEnum, FreezeFlagEnum freezeFlagEnum) {

        //true = 增加余额,false = 扣除余额
        boolean type = addAmount.compareTo(BigDecimal.ZERO) > 0;

        BigDecimal amount = type ? addAmount : subAmount;

        //不等于冻结 跟解冻 的情况下,根据 type来判断是什么类型
        if (!CoinBalanceTypeEnum.FREEZE.equals(coinBalanceTypeEnum) && !CoinBalanceTypeEnum.UN_FREEZE.equals(coinBalanceTypeEnum)) {
            coinBalanceTypeEnum = type ? CoinBalanceTypeEnum.INCOME : CoinBalanceTypeEnum.EXPENSES;
        }

        SBToCoinAddVO sbToCoinAddVO = SBToCoinAddVO.builder()
                .orderId(orderId)
                .type(type)
                .userId(userId)
                .amount(amount)
                .addAmount(addAmount)
                .subAmount(subAmount)
                .coinBalanceTypeEnum(coinBalanceTypeEnum.getCode())
                .build();

        if(freezeFlagEnum != null){
            sbToCoinAddVO.setFreezeFlagEnum(freezeFlagEnum.getCode());
        }

        return sbToCoinAddVO;
    }


}
