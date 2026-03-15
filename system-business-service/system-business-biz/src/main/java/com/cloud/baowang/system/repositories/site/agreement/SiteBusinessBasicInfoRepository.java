package com.cloud.baowang.system.repositories.site.agreement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicVO;
import com.cloud.baowang.system.po.site.config.SiteBusinessBasicInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteBusinessBasicInfoRepository extends BaseMapper<SiteBusinessBasicInfoPO> {
}