package com.cloud.baowang.system.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.site.SiteRequestVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.po.site.SitePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteRepository extends BaseMapper<SitePO> {
    Page<SiteVO> querySiteInfo(Page<SitePO> page, @Param("siteVO") SiteRequestVO siteRequestVO);

    List<SiteVO> allSiteInfo();
}
