package com.cloud.baowang.system.repositories.site.agreement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.site.config.SiteBusinessBasicInfoPO;
import com.cloud.baowang.system.po.site.config.media.SiteMediaInfoPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteMediaInfoRepository extends BaseMapper<SiteMediaInfoPO> {
}