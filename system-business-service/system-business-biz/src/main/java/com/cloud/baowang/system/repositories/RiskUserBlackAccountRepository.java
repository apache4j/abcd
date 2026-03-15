package com.cloud.baowang.system.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.risk.RiskUserBlackAccountPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RiskUserBlackAccountRepository extends BaseMapper<RiskUserBlackAccountPO> {
}
