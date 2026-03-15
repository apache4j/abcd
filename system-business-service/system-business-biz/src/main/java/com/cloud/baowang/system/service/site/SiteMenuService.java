package com.cloud.baowang.system.service.site;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.system.api.enums.BusinessSystemEnum;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuAddVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuQueryVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuUpdateVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuVO;
import com.cloud.baowang.system.po.member.BusinessAdminRolePO;
import com.cloud.baowang.system.po.member.BusinessMenuPO;
import com.cloud.baowang.system.po.member.BusinessRoleMenuPO;
import com.cloud.baowang.system.po.member.BusinessRolePO;
import com.cloud.baowang.system.repositories.site.SiteAdminRoleRepository;
import com.cloud.baowang.system.repositories.site.SiteMenuRepository;
import com.cloud.baowang.system.repositories.site.SiteRoleMenuRepository;
import com.cloud.baowang.system.repositories.site.SiteRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiqi
 */
@Service
@Slf4j
public class SiteMenuService {

    private final SiteMenuRepository siteMenuRepository;

    private final SiteRoleMenuRepository siteRoleMenuRepository;

    private final SiteAdminRoleRepository siteAdminRoleRepository;

    private final SiteRoleRepository siteRoleRepository;


    public SiteMenuService(SiteMenuRepository siteMenuRepository, SiteRoleMenuRepository siteRoleMenuRepository, SiteAdminRoleRepository siteAdminRoleRepository, SiteRoleRepository siteRoleRepository) {
        this.siteMenuRepository = siteMenuRepository;
        this.siteRoleMenuRepository = siteRoleMenuRepository;
        this.siteAdminRoleRepository = siteAdminRoleRepository;
        this.siteRoleRepository = siteRoleRepository;
    }


    public String addMenu(SiteMenuAddVO siteMenuAddVO) {
        BusinessMenuPO siteMenuPO = new BusinessMenuPO();
        BeanUtils.copyProperties(siteMenuAddVO, siteMenuPO);
        siteMenuPO.setBusinessSystem(BusinessSystemEnum.SITE.getCode());
        if (CommonConstant.business_zero.toString().equals(siteMenuPO.getParentId())) {
            siteMenuPO.setLevel(1);
            siteMenuPO.setParentId(CommonConstant.business_zero.toString());
        } else {
            BusinessMenuPO parentMenu = siteMenuRepository.selectById(siteMenuPO.getParentId());
            if (null == parentMenu) {
                log.error("未查询到对应的父节点");
                return "-1";
            }
            siteMenuPO.setLevel(parentMenu.getLevel().intValue() + 1);
            if (StringUtils.isNotEmpty(parentMenu.getPath())) {
                siteMenuPO.setPath(parentMenu.getPath() + "," + parentMenu.getId());
            } else {
                siteMenuPO.setPath(parentMenu.getId().toString());
            }
        }
        siteMenuPO.setCreatedTime(System.currentTimeMillis());
        siteMenuPO.setUpdatedTime(System.currentTimeMillis());
        siteMenuRepository.insert(siteMenuPO);
        return siteMenuPO.getId();
    }


    public String updateMenu(SiteMenuUpdateVO siteMenuUpdateVO) {
        BusinessMenuPO siteMenuPO = new BusinessMenuPO();
        BeanUtils.copyProperties(siteMenuUpdateVO, siteMenuPO);
        siteMenuPO.setUpdatedTime(System.currentTimeMillis());
        siteMenuRepository.updateById(siteMenuPO);
        return siteMenuPO.getId();
    }


    public Integer deleteMenu(IdVO idVO) {
         return siteMenuRepository.deleteById(idVO.getId());
    }

    public List<SiteMenuVO> listTreeMenu(SiteMenuQueryVO siteMenuQueryVO) {
        LambdaQueryWrapper<BusinessMenuPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessMenuPO::getBusinessSystem, BusinessSystemEnum.SITE.getCode());
        lqw.orderByAsc(BusinessMenuPO::getOrderNum);
        List<BusinessMenuPO> siteMenuPOList = siteMenuRepository.selectList(lqw);

        List<SiteMenuVO> list = siteMenuPOList.stream().map(po -> {
            SiteMenuVO vo = new SiteMenuVO();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }).toList();
        List<SiteMenuVO> treeMenuList = buildMenuTree(list);
        return treeMenuList;

    }

    public List<SiteMenuVO> listTreeMenuByAdminId(String adminId) {
        List<SiteMenuVO> list = getUserMenuApis(adminId);
        List<SiteMenuVO> treeMenuList = buildMenuTree(list);
        return treeMenuList;

    }

    private List<SiteMenuVO> getUserMenuApis(String adminId) {
        List<SiteMenuVO> menuVOList = new ArrayList<>();
        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.select(BusinessAdminRolePO::getRoleId);

        adminRoleLqw.eq(BusinessAdminRolePO::getAdminId, adminId);

        List<Object> roleIds = siteAdminRoleRepository.selectObjs(adminRoleLqw);
        if (roleIds.isEmpty()) {
            return menuVOList;
        }
        LambdaQueryWrapper<BusinessRolePO> roleLqw = new LambdaQueryWrapper<>();
        roleLqw.in(BusinessRolePO::getId,roleIds);
        roleLqw.eq(BusinessRolePO::getStatus,CommonConstant.business_one);
        roleLqw.select(BusinessRolePO::getId);
        List<Object> roleIdsArray = siteRoleRepository.selectObjs(roleLqw);
        if (roleIdsArray.isEmpty()) {
            return menuVOList;
        }

        LambdaQueryWrapper<BusinessRoleMenuPO> roleMenuLqw = new LambdaQueryWrapper<>();
        roleMenuLqw.select(BusinessRoleMenuPO::getMenuId);
        roleMenuLqw.in(BusinessRoleMenuPO::getRoleId, roleIdsArray);
        List<Object> menuIds = siteRoleMenuRepository.selectObjs(roleMenuLqw);
        if (menuIds.isEmpty()) {
            return menuVOList;
        }
        LambdaQueryWrapper<BusinessMenuPO> menuLqw = new LambdaQueryWrapper<>();
        menuLqw.in(BusinessMenuPO::getId, menuIds);
        menuLqw.ne(BusinessMenuPO::getSuperAdminOnlyVisible, 1);
        menuLqw.orderByAsc(BusinessMenuPO::getOrderNum);

        List<BusinessMenuPO> menuPOS = siteMenuRepository.selectList(menuLqw);

        if (menuPOS.isEmpty()) {
            return menuVOList;
        }
        for (BusinessMenuPO menuPO : menuPOS) {
            SiteMenuVO siteMenuVO = new SiteMenuVO();
            BeanUtils.copyProperties(menuPO, siteMenuVO);
            menuVOList.add(siteMenuVO);
        }

        return menuVOList;

    }

    public List<SiteMenuVO> buildMenuTree(List<SiteMenuVO> menus) {
        List<SiteMenuVO> returnList = new ArrayList<SiteMenuVO>();
        List<String> tempList = menus.stream().map(SiteMenuVO::getId).collect(Collectors.toList());
        for (Iterator<SiteMenuVO> iterator = menus.iterator(); iterator.hasNext(); ) {
            SiteMenuVO menu = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SiteMenuVO> list, SiteMenuVO t) {
        // 得到子节点列表
        List<SiteMenuVO> childList = getChildList(list, t);
        t.setChildMenuList(childList);
        for (SiteMenuVO tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SiteMenuVO> getChildList(List<SiteMenuVO> list, SiteMenuVO t) {
        List<SiteMenuVO> tlist = new ArrayList<SiteMenuVO>();
        Iterator<SiteMenuVO> it = list.iterator();
        while (it.hasNext()) {
            SiteMenuVO n = it.next();
            if (n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SiteMenuVO> list, SiteMenuVO t) {
        return getChildList(list, t).size() > 0;
    }


    public boolean checkMenuKeyUnique(String menuKey) {
        LambdaQueryWrapper<BusinessMenuPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessMenuPO::getBusinessSystem,BusinessSystemEnum.SITE.getCode());
        lqw.eq(BusinessMenuPO::getMenuKey, menuKey);
        List<BusinessMenuPO> siteMenuPOList = siteMenuRepository.selectList(lqw);
        return siteMenuPOList.isEmpty();

    }
}
