package com.cloud.baowang.activity.repositories.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.api.vo.v2.ActivityContestPayoutV2VO;

import com.cloud.baowang.activity.po.v2.SiteActivityContestPayoutV2PO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动赛事包赔数据访问
 */
@Mapper
public interface ActivityContestPayoutV2Repository extends BaseMapper<SiteActivityContestPayoutV2PO> {
}
