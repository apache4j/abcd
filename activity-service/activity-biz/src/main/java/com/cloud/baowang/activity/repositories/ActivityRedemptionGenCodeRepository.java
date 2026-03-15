package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityRedemptionGenCodePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 兑换码生成数据访问层
 */
@Mapper
public interface ActivityRedemptionGenCodeRepository extends BaseMapper<SiteActivityRedemptionGenCodePO> {
}
