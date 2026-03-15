package com.cloud.baowang.user.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.UserTaskRecordPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息配置多语言游戏名称配置Mapper
 */
@Mapper
public interface UserTaskRecordRepository extends BaseMapper<UserTaskRecordPO> {

}