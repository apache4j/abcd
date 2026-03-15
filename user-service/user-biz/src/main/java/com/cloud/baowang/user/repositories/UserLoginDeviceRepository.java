package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.UserLoginDevicePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员登录设备记录表 Mapper 接口
 *
 */
@Mapper
public interface UserLoginDeviceRepository extends BaseMapper<UserLoginDevicePO> {
    Integer deleteById(@Param("id")String id);
}
