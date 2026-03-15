package com.cloud.baowang.system.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.risk.RiskCtrlLevelPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RiskCtrlLevelRepository extends BaseMapper<RiskCtrlLevelPO> {
    // 自定义查询，通过风险类型查询风险层级
    List<RiskCtrlLevelPO> selectList(String type);

}
