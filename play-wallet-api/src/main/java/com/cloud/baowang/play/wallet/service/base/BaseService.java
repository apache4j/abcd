package com.cloud.baowang.play.wallet.service.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.SiteStatusEnums;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.vo.SiteMaintenanceVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.member.CasinoMemberApi;
import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoValidRequestVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserActivityTypingAmountApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.activity.UserActivityTypingAmountResp;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.cloud.baowang.common.core.constants.RedisConstants.KEY_SERVER_MAINTAIN_SITE_KEY;

@Component
@Slf4j
public abstract class BaseService {
    @Resource
    protected UserCoinApi userCoinApi;

    @Resource
    private VenueUserAccountApi venueUserAccountApi;
    @Resource
    protected UserInfoApi userInfoApi;
    @Resource
    protected CasinoMemberApi casinoMemberApi;
    @Resource
    private PlayVenueInfoApi playVenueInfoApi;
    @Resource
    private UserActivityTypingAmountApi userActivityTypingAmountApi;
    @Resource
    private UserCoinRecordApi userCoinRecordApi;
    @Resource
    private GameInfoApi gameInfoApi;

    /**
     * 去除用户前缀,得到用户真实账号
     *
     * @return
     */
    public String getVenueUserAccount(String userAccount) {
        return venueUserAccountApi.getVenueUserAccount(userAccount);
    }

    /**
     * 单投注账变
     *
     * @param userInfoVO     userInfo
     * @param transactionId  orderNo
     * @param transferAmount amount
     * @return result
     */
    protected CoinRecordResultVO updateBalanceBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount) {
        return updateBalanceBet(userInfoVO, transactionId, transferAmount, null);
    }

    protected CoinRecordResultVO updateBalanceBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount.abs());
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }

    /**
     * 单派彩账变
     *
     * @param userInfoVO    userInfo
     * @param transactionId orderNo
     * @param payoutAmount  amount
     * @return result
     */
    protected CoinRecordResultVO updateBalancePayout(UserInfoVO userInfoVO, String transactionId, BigDecimal payoutAmount) {
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(transactionId);
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs());
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }

    /**
     * 处理用户的投注和派彩余额变更操作。
     * <p>
     * 此方法执行两步操作：
     * 1. 记录投注（支出）金额；
     * 2. 记录派彩（收入）金额；
     * 若投注记录失败，则不会进行派彩记录。
     * </p>
     *
     * @param userInfoVO    用户信息对象，包含用户ID、主币种等
     * @param transactionId 交易订单号，投注和派彩共用同一订单号
     * @param betAmount     投注金额（支出），会取绝对值处理
     * @param payoutAmount  派彩金额（收入），会取绝对值处理
     * @return CoinRecordResultVO 返回余额变更结果对象，包含状态和结果信息
     */
    protected CoinRecordResultVO updateBalanceBetPayOut(UserInfoVO userInfoVO, String transactionId, BigDecimal betAmount, BigDecimal payoutAmount) {
        // 构建投注记录对象（支出）
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId); // 设置订单号
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency()); // 设置币种
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode()); // 设置为支出类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode()); // 设置币种类型为游戏投注
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode()); // 设置业务币种类型为游戏投注
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode()); // 设置客户币种类型为游戏投注
        userCoinAddVO.setUserId(userInfoVO.getUserId()); // 设置用户ID
        userCoinAddVO.setCoinValue(betAmount.abs()); // 投注金额（绝对值）
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class)); // 设置用户信息

        // 如果投注金额大于0，才进行投注记录
        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) > 0) {
            // 发起扣款（投注）操作
            CoinRecordResultVO coinRecordResultVO = userCoinApi.addCoin(userCoinAddVO);
            // 如果投注失败，直接返回失败结果，不进行派彩操作
            if (!coinRecordResultVO.getResultStatus().equals(UpdateBalanceStatusEnums.SUCCESS)) {
                return coinRecordResultVO;
            }
        }

        // 构建派彩记录对象（收入）
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(transactionId); // 同一订单号
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency()); // 设置币种
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode()); // 设置为收入类型
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode()); // 设置币种类型为游戏派彩
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode()); // 设置业务币种类型为游戏派彩
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode()); // 设置客户币种类型为游戏派彩
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId()); // 设置用户ID
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs()); // 派彩金额（绝对值）
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class)); // 设置用户信息

        // 发起派彩（收入）操作
        return userCoinApi.addCoin(userCoinAddVOPayout);
    }

    /**
     * 处理用户的投注和派彩余额变更操作。
     * <p>
     * 此方法执行两步操作：
     * 1. 记录投注（支出）金额；
     * 2. 记录派彩（收入）金额；
     * 若投注记录失败，则不会进行派彩记录。
     * </p>
     *
     * @param userInfoVO   用户信息对象，包含用户ID、主币种等
     * @param betId        交易订单号，投注和派彩共用同一订单号 ，投注单号
     * @param betAmount    投注金额（支出），会取绝对值处理
     * @param payoutAmount 派彩金额（收入），会取绝对值处理
     * @return CoinRecordResultVO 返回余额变更结果对象，包含状态和结果信息
     */
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

        // 如果投注金额大于0，才进行投注记录
        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) > 0) {
            List<UserCoinRecordVO> userCoinRecords = userCoinRecordApi.getUserCoinRecordPG(betId, userInfoVO.getUserId(), CoinBalanceTypeEnum.EXPENSES.getCode());
            if (CollUtil.isNotEmpty(userCoinRecords)) {
                Optional<UserCoinRecordVO> first = userCoinRecords.stream().filter(e -> StrUtil.equals(e.getRemark(), traceId)).findFirst();
                // 不存在，不是重复，属于第二次
                if (!first.isPresent()) {
                    userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
                    coinRecordResultVO = userCoinApi.addCoin(userCoinAddVO);
                } else {
                    // 发起扣款（投注）操作
                    coinRecordResultVO = userCoinApi.addCoin(userCoinAddVO);
                }

            } else {
                // 发起扣款（投注）操作
                coinRecordResultVO = userCoinApi.addCoin(userCoinAddVO);
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
        List<UserCoinRecordVO> userCoinRecords = userCoinRecordApi.getUserCoinRecordPG(betId, userInfoVO.getUserId(), CoinBalanceTypeEnum.INCOME.getCode());
        if (CollUtil.isNotEmpty(userCoinRecords)) {
            Optional<UserCoinRecordVO> first = userCoinRecords.stream().filter(e -> StrUtil.equals(e.getRemark(), traceId)).findFirst();
            // 不存在，不是重复，属于第二次
            if (!first.isPresent()) {
                userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
                // 发起派彩（收入）操作
                coinRecordResultVO = userCoinApi.addCoin(userCoinAddVOPayout);
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
            coinRecordResultVO = userCoinApi.addCoin(userCoinAddVOPayout);
        }
        return coinRecordResultVO;

    }


    protected UserCoinWalletVO getUserCoin(String userId, String siteCode) {
        UserCoinWalletVO userCoinWalletVO = new UserCoinWalletVO();
        userCoinWalletVO.setTotalAmount(BigDecimal.ZERO);
        try {
            UserCoinWalletVO userCenterCoin = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).siteCode(siteCode).build());
            if (Objects.isNull(userCenterCoin)) {
                return userCoinWalletVO;
            }
            return userCenterCoin;
        } catch (Exception e) {
            log.error("单一钱包获取用户钱包信息失败", e);
            return userCoinWalletVO;
        }
    }

    /**
     * 重结算
     *
     * @param userInfoVO
     * @param orderNo
     * @param amount
     * @return
     */
    protected CoinRecordResultVO updateBalanceResettle(UserInfoVO userInfoVO, String orderNo, BigDecimal amount) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);

    }

    /**
     * 投注取消
     *
     * @param userInfoVO
     * @param orderNo
     * @param amount
     * @return
     */
    protected CoinRecordResultVO updateBalanceBetCancel(UserInfoVO userInfoVO, String orderNo, BigDecimal amount) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        //修改余额 记录账变
        return userCoinApi.addCoin(userCoinAddVO);
    }

    protected boolean venueMaintainClosed(String venueCode, String siteCode) {
        //增加站点维护判断
        String serverStatusKey=KEY_SERVER_MAINTAIN_SITE_KEY.concat(siteCode);
        String serverStatusInfo = RedisUtil.getValue(serverStatusKey);
        if(org.springframework.util.StringUtils.hasText(serverStatusInfo)){
            SiteMaintenanceVO siteMaintenanceVO= JSON.parseObject(serverStatusInfo, SiteMaintenanceVO.class);
            if(Objects.equals(SiteStatusEnums.MAINTENANCE.getStatus(), siteMaintenanceVO.getSiteStatus())||
                    Objects.equals(SiteStatusEnums.DISABLE.getStatus(), siteMaintenanceVO.getSiteStatus())){
                return true;
            }
        }
        ResponseVO<Boolean> venueInfoVOResponseVO = playVenueInfoApi.venueMaintainClosed(venueCode, siteCode);
        if (venueInfoVOResponseVO.getCode() != ResultCode.SUCCESS.getCode()) {
            return true;
        }
        Boolean status = venueInfoVOResponseVO.getData();
        return status == null || status;
    }



    protected boolean venueGameMaintainClosed(String venueCode, String siteCode,String gameCode) {
        GameInfoValidRequestVO requestVO = GameInfoValidRequestVO.builder().siteCode(siteCode).gameId(gameCode).venueCode(venueCode).build();
        ResponseVO<GameInfoVO> responseVO = gameInfoApi.getGameInfoByCode(requestVO);
        return !responseVO.isOk() || responseVO.getData() == null || responseVO.getData().getStatus() != 1;
    }



    protected boolean userGameLock(UserInfoVO userInfoVO) {
        String accountStatus = userInfoVO.getAccountStatus();
        if (StringUtils.isEmpty(accountStatus)) {
            return false;
        }
        return Arrays.asList(accountStatus.split(CommonConstant.COMMA)).contains(UserStatusEnum.GAME_LOCK.getCode()) ||
                Arrays.asList(accountStatus.split(CommonConstant.COMMA)).contains(UserStatusEnum.LOGIN_LOCK.getCode());
    }


    /**
     * 余额小于投注金额
     *
     * @param amount 投注金额
     */
    protected boolean compareAmount(String userId, String siteCode, BigDecimal amount) {
        UserCoinWalletVO userCoin = getUserCoin(userId, siteCode);
        if (userCoin == null) {
            return false;
        }
        log.info("用户可用余额{}, 扣减的余额{}", userCoin.getCenterAmount(), amount);
        log.info("可用和扣减比对{}", userCoin.getCenterAmount().compareTo(BigDecimal.ZERO) > 0 && userCoin.getCenterAmount().compareTo(amount.abs()) >= 0);
        return userCoin.getCenterAmount().compareTo(BigDecimal.ZERO) > 0 && userCoin.getCenterAmount().compareTo(amount.abs()) >= 0;
    }


    /**
     * 获取流水限制
     *
     * @return isLimit 是否限制 true:限制 false:不限制
     * amount 限制金额
     * @Param userId 用户id
     * @Param venueCode 场馆code
     */
    protected Map<String, Object> checkJoinActivity(final String userId, final String venueCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("isLimit", false);
        if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(venueCode)) {
            UserInfoVO infoVO = UserInfoVO.builder().build();
            infoVO.setUserId(userId);
            UserActivityTypingAmountResp userActivityTypingLimit = userActivityTypingAmountApi.getUserActivityTypingLimit(ConvertUtil.entityToModel(infoVO, WalletUserInfoVO.class));
            if (Objects.nonNull(userActivityTypingLimit) && Objects.nonNull(userActivityTypingLimit.getTypingAmount())
                    && userActivityTypingLimit.getTypingAmount().compareTo(BigDecimal.ZERO) == 1
                    && !userActivityTypingLimit.getLimitGameType().equals(VenueEnum.nameOfCode(venueCode).getType())
            ) {
                map.put("amount", userActivityTypingLimit.getTypingAmount());
                map.put("isLimit", true);
                return map;
            }
        }
        return map;
    }



}
