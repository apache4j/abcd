package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloud.baowang.play.api.vo.venue.SportRecommendRequestVO;
import com.cloud.baowang.play.api.vo.venue.SportRecommendVO;
import com.cloud.baowang.play.po.SportEventsRecommendPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 体育赛事推荐
 *
 * @author sheldon
 */
@Mapper
public interface SportEventsRecommendRepository extends BaseMapper<SportEventsRecommendPO> {

    IPage<SportRecommendVO> querySportRecommendPage(@Param("page") IPage<SportEventsRecommendPO> page, @Param("vo") SportRecommendRequestVO vo, @Param("siteCode") String siteCode);
}
