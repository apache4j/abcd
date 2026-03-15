package com.cloud.baowang.activity.repositories.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.v2.SiteActivityEventRecordV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityNewHandPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteActivityNewHandRepository extends BaseMapper<SiteActivityNewHandPO> {
}
