package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityFirstRechargePO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeBasePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 兑换码基础信息数据访问层
 */
@Mapper
public interface ActivityRedemptionCodeBaseRepository extends BaseMapper<SiteActivityRedemptionCodeBasePO> {
}
