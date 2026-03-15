package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeExchangePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 兑换码奖金领取数据访问层
 */
@Mapper
public interface ActivityRedemptionCodeExchangeRepository extends BaseMapper<SiteActivityRedemptionCodeExchangePO> {
}
