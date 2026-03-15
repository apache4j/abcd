package com.cloud.baowang.common.redis.constant;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.redis.config.RedisUtil;

/**
 * redisLockKey
 */
public class RedisKeyTransUtil {
    public static final String USER_WITHDRAW_REVIEW_ORDER_NO = "lockOrUnLock.userWithdraw.lock.key.%s";
    public static final String AGENT_WITHDRAW_REVIEW_ORDER_NO = "lockOrUnLock.agentWithdraw.lock.key.%s";
    public static final String USER_WITHDRAW_REVIEW_ORDER_ID = "lockOrUnLock.userWithdraw.review.key.%s";
    public static final String AGENT_REBATE_REVIEW_ORDER_ID = "lockOrUnLock:agentRebate:review:key:%s";
    public static final String AGENT_WITHDRAW_REVIEW_ORDER_ID = "lockOrUnLock:agentWithdraw:review:key:%s";
    public static final String USER_WITHDRAW_APPLY = "wallet.userWithdraw.apply.key.%s";
    public static final String AGENT_WITHDRAW_APPLY = "agent.agentWithdraw.apply.key.%s";
    public static final String USER_DEPOSIT = "wallet.userDeposit.key.%s";

    public static final String USER_WITHDRAW_CALLBACK = "wallet.userWithdrawCallback.key.%s";
    public static final String AGENT_DEPOSIT = "agent.agentDeposit.key.%s";

    public static final String AGENT_WITHDRAW_CALLBACK = "agent.agentWithdrawCallback.key.%s";
    public static final String AGENT_DEPOSIT_SUBORDINATES = "agent.agentDepositSubordinates.key.%s";
    public static final String USER_DEPOSIT_CALLBACK = "agent.userDepositCallback.key.%s";
    /**
     * 后台手动拉单
     */
    public static final String CASINO_THIRD_MANUAL_PULL_BET_LOCK_KEY = "third::casino::manual::bet::%s";
    public static final String ADD_COIN_LOCK_KEY = "add.coin.lock.key.";

    public static final String TRANSFER_PLATFORM_COIN_LOCK_KEY = "transfer.platform.coin.lock.key.";

    public static final String ADD_PLATFORM_COIN_LOCK_KEY = "add.platform.coin.lock.key.";

    public static final String ADD_AGENT_QUOTE_COIN_LOCK_KEY = "add.agent.quote.coin.lock.key.";

    public static final String ADD_AGENT_COMMISSION_COIN_LOCK_KEY = "add.agent.commission.coin.lock.key.";
    public static String ADD_VENUE_COIN_LOCK_KEY = "add.venue.coin.lock.key.%s";
    public static String ADD_TYPING_AMOUNT_LOCK_KEY = "add.typingAmount.lock.key.%s";
    public static String BACKUP_CONTRACT_RATE_LOCK_KEY = "backup.contract.rate.lock.key";

    public static String FEED_BACK_LOCK_KEY = "feed.back.lock.key.%s";

    public static String ADD_LABEL_LOCK_KEY = "agent:add:label:lock:key:%s";

    public static final String ADD_SYSTEM_MESSAGE = "user.addSystemMessage.apply.key.%s.%s";
    public static final String AREA_LIMIT_CHANGE = "area.limit.change.lock";

    public static final String GAME_ZF_BET_LOCK_KEY = "game:zf:bet:lock:%s";
    public static final String GAME_SEAMLESS_WALLET_BET_LOCK_KEY = "game:%s:bet:lock:%s";

    public static final String SITE_CODE_LOCK_KEY = "site:add:lock:%s";
    /**
     * 代理人工加额批量锁
     */
    public static final String AGENT_MANUAL_LOCK_KEY = "agent:manual:lock:%s";
    /**
     * 会员人工加额批量锁
     */
    public static final String USER_MANUAL_LOCK_KEY = "user:manual:lock:%s";

    /**
     * i18n动态配置 自增值
     */
    public static final String I18N_DYNAMIC = "i18n:dynamic:inc";

    /**
     * websocket 在线用户列表 %s 站点code
     */
    public static final String WS_ONLINE_USER_LIST = "ws:online:user:%s";
    public static final String WS_ONLINE_GUEST_LIST = "ws:online:guest:%s";
    public static final String WS_ONLINE_AGENT_LIST = "ws:online:agent:%s";
    public static final String WS_ONLINE_BUSINESS_LIST = "ws:online:business:%s";
    public static final String WS_ONLINE_SITE_LIST = "ws:online:site:%s";
    public static final String WS_ONLINE_ADMIN_LIST = "ws:online:admin:%s";

    /**
     * 免费游戏账变
     */
    public static final String FREE_GAME_RECORD_CHANGE = "game:free:times:change:%s:%s";

    /**
     * 代理编号缓存
     */
    public static final String CACHE_NUM_AGENT_ID ="cache:num:agent:id";
    /**
     * 商务编号缓存
     */
    public static final String CACHE_NUM_MERCHANT_ID="cache:num:mer:id";

    /**
     * 会员编号缓存
     */
    public static final String CACHE_NUM_USER_ID="cache:num:user:id";

    /**
     * 总站管理员编号缓存
     */
    public static final String CACHE_NUM_ADMIN_ID="cache:num:admin:id";

    /**
     * 站点管理员编号缓存
     */
    public static final String CACHE_NUM_SITE_ADMIN_ID="cache:num:siteAdmin:id";

    public static String getFreeGameRecordChange(String uid, String siteCode) {
        return String.format(FREE_GAME_RECORD_CHANGE, siteCode, uid);
    }

    public static String getGameSeamlessWalletBetLockKey(String venueCode, String orderNo) {
        return String.format(GAME_SEAMLESS_WALLET_BET_LOCK_KEY, venueCode, orderNo);
    }

    public static String getGameZfBetLockKey(String orderNo) {
        return String.format(GAME_ZF_BET_LOCK_KEY, orderNo);
    }

    public static String getAddCoinLockKey(String userAccount) {
        return String.format(ADD_COIN_LOCK_KEY, userAccount);
    }

    public static String getAddVenueCoinLockKey(String userAccount) {
        return String.format(ADD_VENUE_COIN_LOCK_KEY, userAccount);
    }

    public static String getAddTypingAmountLockKey(String userAccount) {
        return String.format(ADD_TYPING_AMOUNT_LOCK_KEY, userAccount);
    }

    public static String getUserWithdrawLockOrUnLockLockKey(String userAccount) {
        return String.format(USER_WITHDRAW_REVIEW_ORDER_NO, userAccount);
    }

    public static String getAgentWithdrawLockOrUnLockLockKey(String agentAccount) {
        return String.format(AGENT_WITHDRAW_REVIEW_ORDER_NO, agentAccount);
    }

    public static String getUserWithdrawReviewLockKey(String id) {
        return String.format(USER_WITHDRAW_REVIEW_ORDER_ID, id);
    }

    public static String getFeedBackLockKey(String id) {
        return String.format(FEED_BACK_LOCK_KEY, id);
    }

    public static String getAgentRebateReviewLockKey(Long id) {
        return String.format(AGENT_REBATE_REVIEW_ORDER_ID, id);
    }

    public static String getAgentWithdrawReviewOrderId(String id) {
        return String.format(AGENT_WITHDRAW_REVIEW_ORDER_ID, id);
    }

    public static String getUserWithdrawApply(String userAccount) {
        return String.format(USER_WITHDRAW_APPLY, userAccount);
    }

    public static String getAgentWithdrawApply(String agentAccount) {
        return String.format(AGENT_WITHDRAW_APPLY, agentAccount);
    }

    public static String getUserDeposit(String userAccount) {
        return String.format(USER_DEPOSIT, userAccount);
    }

    public static String getAgentDeposit(String agentAccount) {
        return String.format(AGENT_DEPOSIT, agentAccount);
    }

    public static String getAgentDepositSubordinates(String agentAccount) {
        return String.format(AGENT_DEPOSIT_SUBORDINATES, agentAccount);
    }

    public static String getUserDepositCallback(String userAccount) {
        return String.format(USER_DEPOSIT_CALLBACK, userAccount);
    }

    public static String getAddLabelLockKey(String agentAccount) {
        return String.format(ADD_LABEL_LOCK_KEY, agentAccount);
    }

    public static String getAddUserSystemMessage(String userAccount, String messageType) {
        return String.format(ADD_SYSTEM_MESSAGE, userAccount, messageType);
    }

    public static String getAddSiteLockKey(String siteCode) {
        return String.format(SITE_CODE_LOCK_KEY, siteCode);
    }

    public static String getAgentManualLockKey(String id) {
        return String.format(AGENT_MANUAL_LOCK_KEY, id);
    }

    public static String getUserManualLockKey(String id) {
        return String.format(USER_MANUAL_LOCK_KEY, id);
    }

    public static String wsOnlineUserList(String siteCode) {
        return String.format(WS_ONLINE_USER_LIST, siteCode);
    }

    public static String wsOnlineGuestList(String siteCode) {
        return String.format(WS_ONLINE_GUEST_LIST, siteCode);
    }

    public static String wsOnlineAgentList(String siteCode) {
        return String.format(WS_ONLINE_AGENT_LIST, siteCode);
    }
    public static String wsOnlineBusinessList(String siteCode) {
        return String.format(WS_ONLINE_BUSINESS_LIST, siteCode);
    }
    public static String wsOnlineSiteList(String siteCode) {
        return String.format(WS_ONLINE_SITE_LIST, siteCode);
    }

    public static String wsOnlineAdminList(String siteCode) {
        return String.format(WS_ONLINE_ADMIN_LIST, siteCode);
    }

    /**
     * i18n 动态自增key 对于自定义业务类型使用
     *
     * @param businessCode
     * @return
     */
    public static String getI18nDynamicKey(String businessCode) {
        long incr = RedisUtil.incr(I18N_DYNAMIC, 1);
        return businessCode + CommonConstant.UNDERLINE + incr;
    }
}


