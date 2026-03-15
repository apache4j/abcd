    package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.po.SiteSportEventsRecommendPO;
import com.cloud.baowang.play.po.SportEventsRecommendPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站点-体育赛事热门推荐
 *
 * @author sheldon
 */
@Mapper
public interface SiteSportEventsRecommendRepository extends BaseMapper<SiteSportEventsRecommendPO> {

}
