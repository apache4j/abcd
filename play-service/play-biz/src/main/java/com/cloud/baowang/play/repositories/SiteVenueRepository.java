package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.play.api.vo.venue.SiteVenueConfigVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueRequestVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueResponsePageVO;
import com.cloud.baowang.play.api.vo.venue.siteDetail.SiteVenueQueryVO;
import com.cloud.baowang.play.po.SiteVenuePO;
import com.cloud.baowang.play.po.VenueInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站点关联场馆表 Mapper 接口
 *
 * @author sheldon
 */
@Mapper
public interface SiteVenueRepository extends BaseMapper<SiteVenuePO> {

    Page<SiteVenueResponsePageVO> queryVenueAuthorize(@Param("page") Page<VenueInfoPO> page,
                                                      @Param("vo") SiteVenueRequestVO siteVenueRequestVO);

    List<SiteVenueQueryVO> querySiteVenueBySiteCode(@Param("siteCode") String siteCode);


    List<SiteVenueConfigVO> querySiteVenueJoinConfigBySiteCode(@Param("siteCode") String siteCode);
}
