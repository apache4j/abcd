package com.cloud.baowang.system.repositories.site;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.member.BusinessAdminRolePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 职员角色 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface SiteAdminRoleRepository extends BaseMapper<BusinessAdminRolePO> {

    /**
     * 批量添加职员角色
     *
     * @param list
     */
    void batchAdminRole(List<BusinessAdminRolePO> list);
}
