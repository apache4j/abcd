package com.cloud.baowang.system.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.risk.RiskChangeRecordPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RiskRecordRepository extends BaseMapper<RiskChangeRecordPO> {

}
