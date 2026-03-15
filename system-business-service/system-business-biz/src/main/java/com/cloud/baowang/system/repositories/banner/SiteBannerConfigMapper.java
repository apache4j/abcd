package com.cloud.baowang.system.repositories.banner;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.banner.SiteBannerConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * SiteBannerConfig Mapper interface for database operations.
 */
@Mapper
public interface SiteBannerConfigMapper extends BaseMapper<SiteBannerConfigPO> {


    @Select("SELECT sort, game_one_class_id, banner_area " +
            "FROM site_banner_config " +
            "WHERE site_code = #{siteCode} " +
            "AND status = 1 " +
            "AND game_one_class_id = #{gameOneClassId} " +
            "GROUP BY sort, banner_area, game_one_class_id " +  // 确保这里有空格
            "ORDER BY sort ASC")
    List<SiteBannerConfigPO> selectGroup(@Param("siteCode") String siteCode, @Param("gameOneClassId") String gameOneClassId);
}
