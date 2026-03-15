package com.cloud.baowang.system.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.SystemParamPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author rudger
 * @Date 2023.05.03
 * @Version 1.0
 */
@Mapper
public interface RiskControlTypeRepository extends BaseMapper<SystemParamPO> {
}
