package com.cloud.baowang.system.repositories.site.rebate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.site.rebate.SiteNonRebateQueryVO;
import com.cloud.baowang.system.po.site.rebate.SiteNonRebateConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface SiteNonRebateConfigRepository extends BaseMapper<SiteNonRebateConfigPO> {


    Page<SiteNonRebateConfigPO> listPage(@Param("page") Page<SiteNonRebateConfigPO> page, @Param("vo") SiteNonRebateQueryVO vo);

}
