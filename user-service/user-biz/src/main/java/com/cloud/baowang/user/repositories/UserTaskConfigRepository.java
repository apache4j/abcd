package com.cloud.baowang.user.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.UserTaskConfigPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户配置
 */
@Mapper
public interface UserTaskConfigRepository extends BaseMapper<UserTaskConfigPO> {

}