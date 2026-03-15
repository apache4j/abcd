package com.cloud.baowang.wallet.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.wallet.po.UserTypingAmountMqMessagePO;
import com.cloud.baowang.wallet.repositories.UserTypingAmountMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 会员打码量-MQ消息体 服务类
 */
@Slf4j
@Service
public class UserTypingAmountMqMessageService extends ServiceImpl<UserTypingAmountMessageRepository, UserTypingAmountMqMessagePO> {

}
