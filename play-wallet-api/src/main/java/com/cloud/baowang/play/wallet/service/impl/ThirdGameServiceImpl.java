package com.cloud.baowang.play.wallet.service.impl;

import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.wallet.po.UserWalletGameRecordPO;
import com.cloud.baowang.play.wallet.repositories.UserCoinRepository;
import com.cloud.baowang.play.wallet.repositories.UserWalletGameRecordRepository;
import com.cloud.baowang.play.wallet.service.ThirdGameService;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ThirdGameServiceImpl implements ThirdGameService {

    @Resource
    private UserCoinRepository userCoinRepository;
    @Resource
    private UserWalletGameRecordRepository walletGameRecordRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateBalanceStatusEnums updateBalance(UserWalletGameRecordPO walletGame) {
        String addCoinLockKey = RedisKeyTransUtil.getAddCoinLockKey(walletGame.getUserAccount());
        RLock rLock = RedisUtil.getLock(addCoinLockKey);
        try {
            if (rLock.tryLock(2, 3, TimeUnit.SECONDS)) {
                int status = userCoinRepository.updateBalance(walletGame.getUserAccount(), walletGame.getAmount());
                if (status < 1) {
                    log.warn("Insufficient balance for walletGame: {}", walletGame);
                    return UpdateBalanceStatusEnums.INSUFFICIENT_BALANCE;
                }
                walletGameRecordRepository.insert(walletGame);
            } else {
                log.error("Lock timeout for key: {}", addCoinLockKey);
                return UpdateBalanceStatusEnums.FAIL;
            }
        } catch (Exception e) {
            log.error("Failed to update balance for walletGame: {}", walletGame, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (e instanceof DuplicateKeyException) {
                return UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS;
            }
            return UpdateBalanceStatusEnums.FAIL;
        } finally {
            if (rLock.isLocked()) {
                rLock.unlock();
                log.info("Lock released for key: {}", addCoinLockKey);
            }
        }
        return UpdateBalanceStatusEnums.SUCCESS;
    }

}
