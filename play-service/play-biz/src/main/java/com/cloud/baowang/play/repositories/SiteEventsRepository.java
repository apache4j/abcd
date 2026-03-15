package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.po.SiteEventsPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站点-体育联赛排序
 *
 * @author sheldon
 */
@Mapper
public interface SiteEventsRepository extends BaseMapper<SiteEventsPO> {


    List<String> getSiteEvents(@Param("siteCode") String siteCode,
                               @Param("venueCode") String venueCode,
                               @Param("sportType") Integer sportType);

}
