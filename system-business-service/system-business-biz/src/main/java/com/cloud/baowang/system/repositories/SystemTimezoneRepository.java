package com.cloud.baowang.system.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.timezone.SystemTimezonePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemTimezoneRepository extends BaseMapper<SystemTimezonePO> {
    // 可以定义自定义查询方法
}
