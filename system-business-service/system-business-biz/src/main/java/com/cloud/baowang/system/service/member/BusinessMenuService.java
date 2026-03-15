package com.cloud.baowang.system.service.member;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.system.api.enums.BusinessSystemEnum;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.enums.BusinessMenuTypeEnum;
import com.cloud.baowang.system.api.vo.member.BusinessMenuAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuUpdateVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuVO;
import com.cloud.baowang.system.api.vo.business.BusinessUserMenuRespVO;
import com.cloud.baowang.system.po.member.BusinessAdminRolePO;
import com.cloud.baowang.system.po.member.BusinessMenuPO;
import com.cloud.baowang.system.po.member.BusinessRoleMenuPO;
import com.cloud.baowang.system.po.member.BusinessRolePO;
import com.cloud.baowang.system.repositories.member.BusinessAdminRoleRepository;
import com.cloud.baowang.system.repositories.member.BusinessMenuRepository;
import com.cloud.baowang.system.repositories.member.BusinessRoleMenuRepository;
import com.cloud.baowang.system.repositories.member.BusinessRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiqi
 */
@Service
@Slf4j
public class BusinessMenuService {

    private final BusinessMenuRepository businessMenuRepository;

    private final BusinessRoleMenuRepository businessRoleMenuRepository;

    private final BusinessAdminRoleRepository businessAdminRoleRepository;

    private final BusinessRoleRepository businessRoleRepository;

    public BusinessMenuService(BusinessMenuRepository businessMenuRepository, BusinessRoleMenuRepository businessRoleMenuRepository, BusinessAdminRoleRepository businessAdminRoleRepository, BusinessRoleRepository businessRoleRepository) {
        this.businessMenuRepository = businessMenuRepository;
        this.businessRoleMenuRepository = businessRoleMenuRepository;
        this.businessAdminRoleRepository = businessAdminRoleRepository;
        this.businessRoleRepository = businessRoleRepository;
    }


    public String addMenu(BusinessMenuAddVO businessMenuAddVO) {
        BusinessMenuPO businessMenuPO = new BusinessMenuPO();
        BeanUtils.copyProperties(businessMenuAddVO, businessMenuPO);
        businessMenuPO.setBusinessSystem(BusinessSystemEnum.ADMIN_CENTER.getCode());
        if (CommonConstant.business_zero.toString().equals(businessMenuPO.getParentId())) {
            businessMenuPO.setLevel(1);
            businessMenuPO.setParentId(CommonConstant.business_zero.toString());
        } else {
            BusinessMenuPO parentMenu = businessMenuRepository.selectById(businessMenuPO.getParentId());
            if (null == parentMenu) {
                log.error("未查询到对应的父节点");
                return "-1";
            }
            businessMenuPO.setLevel(parentMenu.getLevel().intValue() + 1);
            if (StringUtils.isNotEmpty(parentMenu.getPath())) {
                businessMenuPO.setPath(parentMenu.getPath() + "," + parentMenu.getId());
            } else {
                businessMenuPO.setPath(parentMenu.getId().toString());
            }
        }
        businessMenuPO.setCreatedTime(System.currentTimeMillis());
        businessMenuPO.setUpdatedTime(System.currentTimeMillis());
        businessMenuRepository.insert(businessMenuPO);
        return businessMenuPO.getId();
    }


    public String updateMenu(BusinessMenuUpdateVO businessMenuUpdateVO) {
        BusinessMenuPO businessMenuPO = new BusinessMenuPO();
        BeanUtils.copyProperties(businessMenuUpdateVO, businessMenuPO);
        businessMenuPO.setUpdatedTime(System.currentTimeMillis());
        businessMenuRepository.updateById(businessMenuPO);
        return businessMenuPO.getId();
    }


    public Integer deleteMenu(IdVO idVO) {
         return businessMenuRepository.deleteById(idVO.getId());
    }

    public List<BusinessMenuVO> listTreeMenu(BusinessMenuQueryVO businessMenuQueryVO) {
        LambdaQueryWrapper<BusinessMenuPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessMenuPO::getBusinessSystem, BusinessSystemEnum.ADMIN_CENTER.getCode());
        lqw.orderByAsc(BusinessMenuPO::getOrderNum);
        List<BusinessMenuPO> businessMenuPOList = businessMenuRepository.selectList(lqw);

        List<BusinessMenuVO> list = businessMenuPOList.stream().map(po -> {
            BusinessMenuVO vo = new BusinessMenuVO();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }).toList();
        List<BusinessMenuVO> treeMenuList = buildMenuTree(list);
        return treeMenuList;

    }

    public List<BusinessMenuVO> listTreeMenuByAdminId(String adminId) {
        List<BusinessMenuVO> list = getUserMenuApis(adminId);
        List<BusinessMenuVO> treeMenuList = buildMenuTree(list);
        return treeMenuList;

    }

    public List<BusinessUserMenuRespVO> listAllMenuByAdminId(String adminId) {
        List<BusinessMenuVO> businessMenuVOS = getUserMenuApis(adminId);
        List<BusinessUserMenuRespVO> businessUserMenuRespVOS= Lists.newArrayList();
        for (BusinessMenuVO businessMenuVO:businessMenuVOS){
            //只查询菜单
            if(Objects.equals(BusinessMenuTypeEnum.MENU.getCode(), businessMenuVO.getType())){
                BusinessUserMenuRespVO businessUserMenuRespVO=new BusinessUserMenuRespVO();
                BeanUtils.copyProperties(businessMenuVO,businessUserMenuRespVO);
                businessUserMenuRespVO.setAdminId(adminId);
                businessUserMenuRespVOS.add(businessUserMenuRespVO);
            }
        }
        return businessUserMenuRespVOS;

    }

    private List<BusinessMenuVO> getUserMenuApis(String adminId) {
        List<BusinessMenuVO> menuVOList = new ArrayList<>();
        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.select(BusinessAdminRolePO::getRoleId);

        adminRoleLqw.eq(BusinessAdminRolePO::getAdminId, adminId);

        List<Object> roleIds = businessAdminRoleRepository.selectObjs(adminRoleLqw);
        if (roleIds.isEmpty()) {
            return menuVOList;
        }
        LambdaQueryWrapper<BusinessRolePO> roleLqw = new LambdaQueryWrapper<>();
        roleLqw.in(BusinessRolePO::getId,roleIds);
        roleLqw.eq(BusinessRolePO::getStatus,CommonConstant.business_one);
        roleLqw.select(BusinessRolePO::getId);
        List<Object> roleIdsArray = businessRoleRepository.selectObjs(roleLqw);
        if (roleIdsArray.isEmpty()) {
            return menuVOList;
        }
        LambdaQueryWrapper<BusinessRoleMenuPO> roleMenuLqw = new LambdaQueryWrapper<>();
        roleMenuLqw.select(BusinessRoleMenuPO::getMenuId);
        roleMenuLqw.in(BusinessRoleMenuPO::getRoleId, roleIds);
        List<Object> menuIds = businessRoleMenuRepository.selectObjs(roleMenuLqw);
        if (menuIds.isEmpty()) {
            return menuVOList;
        }
        LambdaQueryWrapper<BusinessMenuPO> menuLqw = new LambdaQueryWrapper<>();
        menuLqw.in(BusinessMenuPO::getId, menuIds);
        menuLqw.ne(BusinessMenuPO::getSuperAdminOnlyVisible, 1);
        menuLqw.orderByAsc(BusinessMenuPO::getOrderNum);

        List<BusinessMenuPO> menuPOS = businessMenuRepository.selectList(menuLqw);

        if (menuPOS.isEmpty()) {
            return menuVOList;
        }
        for (BusinessMenuPO menuPO : menuPOS) {
            BusinessMenuVO businessMenuVO = new BusinessMenuVO();
            BeanUtils.copyProperties(menuPO, businessMenuVO);
            menuVOList.add(businessMenuVO);
        }

        return menuVOList;

    }

    public List<BusinessMenuVO> buildMenuTree(List<BusinessMenuVO> menus) {
        List<BusinessMenuVO> returnList = new ArrayList<BusinessMenuVO>();
        List<String> tempList = menus.stream().map(BusinessMenuVO::getId).collect(Collectors.toList());
        for (Iterator<BusinessMenuVO> iterator = menus.iterator(); iterator.hasNext(); ) {
            BusinessMenuVO menu = iterator.next();
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
    private void recursionFn(List<BusinessMenuVO> list, BusinessMenuVO t) {
        // 得到子节点列表
        List<BusinessMenuVO> childList = getChildList(list, t);
        t.setChildMenuList(childList);
        for (BusinessMenuVO tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<BusinessMenuVO> getChildList(List<BusinessMenuVO> list, BusinessMenuVO t) {
        List<BusinessMenuVO> tlist = new ArrayList<BusinessMenuVO>();
        Iterator<BusinessMenuVO> it = list.iterator();
        while (it.hasNext()) {
            BusinessMenuVO n = it.next();
            if (n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<BusinessMenuVO> list, BusinessMenuVO t) {
        return getChildList(list, t).size() > 0;
    }


    public boolean checkMenuKeyUnique(String menuKey) {
        LambdaQueryWrapper<BusinessMenuPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessMenuPO::getBusinessSystem,BusinessSystemEnum.ADMIN_CENTER.getCode());
        lqw.eq(BusinessMenuPO::getMenuKey, menuKey);
        List<BusinessMenuPO> businessMenuPOList = businessMenuRepository.selectList(lqw);
        return businessMenuPOList.isEmpty();

    }

}
