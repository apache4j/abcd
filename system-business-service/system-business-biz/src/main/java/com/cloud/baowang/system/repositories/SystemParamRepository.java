package com.cloud.baowang.system.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.SystemParamPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemParamRepository extends BaseMapper<SystemParamPO> {
}
