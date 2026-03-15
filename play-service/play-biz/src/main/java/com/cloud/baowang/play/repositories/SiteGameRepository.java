package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.api.vo.venue.siteDetail.SiteGameQueryVO;
import com.cloud.baowang.play.po.SiteGamePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站点关联游戏表 Mapper 接口
 *
 * @author sheldon
 */
@Mapper
public interface SiteGameRepository extends BaseMapper<SiteGamePO> {

    List<SiteGameQueryVO> queryGameBySiteCodeVenueCode(@Param("siteCode") String siteCode,
                                                       @Param("venueCodes") List<String> venueCodes);
}
