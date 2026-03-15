package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.play.api.vo.venue.SportEventsInfoRequestVO;
import com.cloud.baowang.play.api.vo.venue.SportEventsInfoVO;
import com.cloud.baowang.play.po.SportEventsInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 体育联赛
 *
 * @author sheldon
 */
@Mapper
public interface SportEventsInfoRepository extends BaseMapper<SportEventsInfoPO> {

    Page<SportEventsInfoVO> getSportEventsInfoPage(IPage<SportEventsInfoVO> page, @Param("siteCode") String siteCode,
                                                   @Param("vo") SportEventsInfoRequestVO requestVO);

    List<SportEventsInfoVO> getSportEventsInfoSortList(@Param("siteCode") String siteCode,@Param("vo") SportEventsInfoRequestVO requestVO);



}
