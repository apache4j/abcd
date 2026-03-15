package com.cloud.baowang.system.repositories.site;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.member.BusinessAdminPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 职员信息 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface SiteAdminRepository extends BaseMapper<BusinessAdminPO> {

}
