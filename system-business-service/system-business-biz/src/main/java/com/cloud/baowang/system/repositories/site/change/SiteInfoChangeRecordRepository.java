package com.cloud.baowang.system.repositories.site.change;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.site.SiteInfoChangeRecordPO;
import com.cloud.baowang.system.po.site.black.SiteRiskCtrlBlackAccountPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteInfoChangeRecordRepository extends BaseMapper<SiteInfoChangeRecordPO> {
}
