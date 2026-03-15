package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.UserInformationChangePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInformationChangeRepository extends BaseMapper<UserInformationChangePO> {
}
