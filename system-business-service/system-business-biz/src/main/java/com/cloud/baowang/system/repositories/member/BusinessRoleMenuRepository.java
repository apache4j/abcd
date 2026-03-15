package com.cloud.baowang.system.repositories.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.api.vo.member.RoleMenuUrlVO;
import com.cloud.baowang.system.po.member.BusinessRoleMenuPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface BusinessRoleMenuRepository extends BaseMapper<BusinessRoleMenuPO> {

    /**
     * 批量添加角色菜单
     *
     * @param list
     */
    void batchRoleMenu(List<BusinessRoleMenuPO> list);

    /**
     * 查询角色对应菜单
     * @param
     * @return
     */
    List<RoleMenuUrlVO> selectRoleMenuUrls();
}
