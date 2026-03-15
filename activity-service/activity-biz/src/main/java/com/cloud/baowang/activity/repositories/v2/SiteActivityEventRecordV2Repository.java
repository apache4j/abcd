package com.cloud.baowang.activity.repositories.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityEventRecordPO;
import com.cloud.baowang.activity.po.v2.SiteActivityEventRecordV2PO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteActivityEventRecordV2Repository extends BaseMapper<SiteActivityEventRecordV2PO> {
}
