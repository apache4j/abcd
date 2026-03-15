package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.api.vo.SiteActivityRedemptionCodeDetailVO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeDetailPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 兑换码明细信息数据访问层
 */
@Mapper
public interface ActivityRedemptionCodeDetailRepository extends BaseMapper<SiteActivityRedemptionCodeDetailPO> {
}
