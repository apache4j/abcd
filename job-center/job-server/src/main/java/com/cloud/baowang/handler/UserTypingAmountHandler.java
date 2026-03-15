package com.cloud.baowang.handler;


import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 会员打码量处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserTypingAmountHandler {


    private final UserTypingAmountApi userTypingAmountApi;


    /**
     * 会员打码量清零 每5分钟一次
     */
    @XxlJob(value = "userTypingAmountCleanZero")
    public void userTypingAmountCleanZero() {
        log.info("***************** 处理会员打码量清零 redisson-XxlJob-start *****************");
//        userTypingAmountApi.userTypingAmountCleanZero();
        log.info("***************** 处理会员打码量清零 redisson-XxlJob-end *****************");
    }


}
