package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.vo.SiteActivityTemplateVO;
import com.cloud.baowang.activity.api.vo.SystemActivityTemplateInfoVO;
import com.cloud.baowang.activity.api.vo.SystemActivityTemplateReqVO;
import com.cloud.baowang.activity.api.vo.SystemActivityTemplateVO;
import com.cloud.baowang.activity.po.SystemActivityTemplatePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemActivityTemplateRepository extends BaseMapper<SystemActivityTemplatePO> {

   List<SiteActivityTemplateVO> querySiteActivityTemplate(@Param("siteCode") String siteCode);

   Page<SystemActivityTemplateVO> getPage(Page<SystemActivityTemplateVO> page, @Param("vo") SystemActivityTemplateReqVO vo);

   List<SystemActivityTemplateInfoVO> getInfo(@Param("vo")SystemActivityTemplateReqVO vo);
}
