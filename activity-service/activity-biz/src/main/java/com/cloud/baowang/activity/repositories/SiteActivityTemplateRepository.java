package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityTemplatePO;
import com.cloud.baowang.activity.po.SystemActivityTemplatePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteActivityTemplateRepository extends BaseMapper<SiteActivityTemplatePO> {
    

}
