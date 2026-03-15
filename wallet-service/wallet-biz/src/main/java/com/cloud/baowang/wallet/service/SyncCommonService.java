package com.cloud.baowang.wallet.service;

import com.cloud.baowang.wallet.po.UserTypingAmountMqMessagePO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author qiqi
 *
 * @Version 1.0
 */
@Service
@Slf4j
@AllArgsConstructor
public class SyncCommonService {

    private final UserTypingAmountMqMessageService userTypingAmountMqMessageService;

//    @Async
    public void saveUserTypingAmountMqMessage(String jsonStr){
        List<UserTypingAmountMqMessagePO>  list = new ArrayList<>();
        int strLength = 4000;
        Long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < jsonStr.length(); i += strLength) {
            String str = jsonStr.substring(i, Math.min(i + strLength, jsonStr.length()));
            //记录打码量MQ消费
            UserTypingAmountMqMessagePO userTypingAmountMqMessagePO = new UserTypingAmountMqMessagePO();
            userTypingAmountMqMessagePO.setJsonStr(str);
            userTypingAmountMqMessagePO.setCreatedTime(currentTimeMillis);
            userTypingAmountMqMessagePO.setUpdatedTime(currentTimeMillis);
            list.add(userTypingAmountMqMessagePO);
        }
        this.userTypingAmountMqMessageService.saveBatch(list);
    }

}
