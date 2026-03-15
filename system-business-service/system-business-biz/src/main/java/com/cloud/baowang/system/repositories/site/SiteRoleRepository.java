package com.cloud.baowang.system.repositories.site;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.member.BusinessRolePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface SiteRoleRepository extends BaseMapper<BusinessRolePO> {

}
