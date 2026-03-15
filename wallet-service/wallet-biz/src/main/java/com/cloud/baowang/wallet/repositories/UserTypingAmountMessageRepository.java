package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.UserTypingAmountMqMessagePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员打码量-MQ消息体 Mapper 接口
 */
@Mapper
public interface UserTypingAmountMessageRepository extends BaseMapper<UserTypingAmountMqMessagePO> {

}
