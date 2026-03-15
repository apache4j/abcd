package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityDailyRecordPO;
import com.cloud.baowang.activity.po.SiteActivityOrderRecordPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteActivityDailyRecordRepository extends BaseMapper<SiteActivityDailyRecordPO> {
}
