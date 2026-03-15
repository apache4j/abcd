package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityEventRecordPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteActivityEventRecordRepository extends BaseMapper<SiteActivityEventRecordPO> {
}
