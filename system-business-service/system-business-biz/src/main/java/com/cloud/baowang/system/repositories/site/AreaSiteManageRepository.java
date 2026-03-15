package com.cloud.baowang.system.repositories.site;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.system.po.site.area.AreaSiteManagePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AreaSiteManageRepository extends BaseMapper<AreaSiteManagePO> {
    List<AreaSiteLangVO> selectByLanguage(@Param("siteCode") String siteCode,
                                          @Param("status") Integer status,
                                          @Param("language") String language);
}
