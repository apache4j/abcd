package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityDailyCompetitionPO;
import com.cloud.baowang.activity.po.SiteActivityDailyRankingPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteActivityDailyRankingRepository extends BaseMapper<SiteActivityDailyRankingPO> {
}
