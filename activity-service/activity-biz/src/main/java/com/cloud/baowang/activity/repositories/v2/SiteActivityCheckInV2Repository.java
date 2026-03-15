package com.cloud.baowang.activity.repositories.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityCheckInPO;
import com.cloud.baowang.activity.po.v2.SiteActivityCheckInV2PO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动签到数据访问层
 * @version V2
 * @author brence
 * @date 2025-10-17
 *
 */
@Mapper
public interface SiteActivityCheckInV2Repository extends BaseMapper<SiteActivityCheckInV2PO> {
}
