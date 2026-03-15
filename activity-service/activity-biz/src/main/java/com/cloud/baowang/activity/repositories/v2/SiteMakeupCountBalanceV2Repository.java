package com.cloud.baowang.activity.repositories.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteMakeupCountBalancePO;
import com.cloud.baowang.activity.po.v2.SiteMakeupCountBalanceV2PO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author brence
 * @date 2026-10-17
 * @desc 补签次数余额数据访问层
 */
@Mapper
public interface SiteMakeupCountBalanceV2Repository extends BaseMapper<SiteMakeupCountBalanceV2PO> {
}
