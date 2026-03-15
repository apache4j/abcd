package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityFreeGameRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteActivityFreeGameRecordRepository extends BaseMapper<SiteActivityFreeGameRecordPO> {
    List<SiteActivityFreeGameRecordPO> getLatestUserRecord(@Param("userIds") List<String> userIds, @Param("siteCode") String siteCode, @Param("venueCode")String venueCode);
}
