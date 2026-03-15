package com.cloud.baowang.system.service.member;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.system.api.enums.BusinessSystemEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.member.BusinessAdminVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleDetailVO;
import com.cloud.baowang.system.api.vo.member.BusinessRolePageVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleUpdateVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.member.RoleMenuUrlVO;
import com.cloud.baowang.system.po.member.BusinessAdminPO;
import com.cloud.baowang.system.po.member.BusinessAdminRolePO;
import com.cloud.baowang.system.po.member.BusinessMenuPO;
import com.cloud.baowang.system.po.member.BusinessRoleMenuPO;
import com.cloud.baowang.system.po.member.BusinessRolePO;
import com.cloud.baowang.system.repositories.member.BusinessAdminRoleRepository;
import com.cloud.baowang.system.repositories.member.BusinessMenuRepository;
import com.cloud.baowang.system.repositories.member.BusinessRoleMenuRepository;
import com.cloud.baowang.system.repositories.member.BusinessRoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qiqi
 */
@Service
public class BusinessRoleService {

    private final BusinessRoleRepository businessRoleRepository;

    private final BusinessRoleMenuRepository businessRoleMenuRepository;

    private final BusinessAdminRoleRepository businessAdminRoleRepository;

    private final BusinessAdminService businessAdminService;

    private final BusinessMenuRepository businessMenuRepository;

    public BusinessRoleService(BusinessRoleRepository businessRoleRepository, BusinessRoleMenuRepository businessRoleMenuRepository, BusinessAdminRoleRepository businessAdminRoleRepository, BusinessAdminService businessAdminService, BusinessMenuRepository businessMenuRepository) {
        this.businessRoleRepository = businessRoleRepository;
        this.businessRoleMenuRepository = businessRoleMenuRepository;
        this.businessAdminRoleRepository = businessAdminRoleRepository;
        this.businessAdminService = businessAdminService;
        this.businessMenuRepository = businessMenuRepository;
    }

    public Page<BusinessRolePageVO> listRole(BusinessRoleQueryVO businessRoleQueryVO) {
        Page<BusinessRolePO> page = new Page<>(businessRoleQueryVO.getPageNumber(), businessRoleQueryVO.getPageSize());
        LambdaQueryWrapper<BusinessRolePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessRolePO::getBusinessSystem, BusinessSystemEnum.ADMIN_CENTER.getCode());
        lqw.eq(null != businessRoleQueryVO.getStatus(),BusinessRolePO::getStatus,businessRoleQueryVO.getStatus());
        if (!businessRoleQueryVO.getIsSuperAdmin()) {
            lqw.eq(BusinessRolePO::getCreator, businessRoleQueryVO.getCurrentAdminId());
        }
        lqw.orderByDesc(BusinessRolePO::getStatus);
        lqw.orderByDesc(BusinessRolePO::getCreatedTime);
        lqw.like(StringUtils.isNotBlank(businessRoleQueryVO.getName()), BusinessRolePO::getName, businessRoleQueryVO.getName());
        Page<BusinessRolePO> businessRolePOPage = businessRoleRepository.selectPage(page, lqw);
        Page<BusinessRolePageVO> businessRolePageVOPage = new Page<>();
        BeanUtils.copyProperties(businessRolePOPage, businessRolePageVOPage);
        List<String> createIds = businessRolePOPage.getRecords().stream().map(BusinessRolePO::getCreator).toList();
        List<BusinessAdminVO> businessAdminList = businessAdminService.getUserByIds(createIds);
        Map<String, String> businessAdminMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(businessAdminList)) {
            businessAdminMap = businessAdminList.stream().collect(Collectors.toMap(BusinessAdminVO::getId, BusinessAdminVO::getUserName, (k1, k2) -> k2));
        }
        Map<String, String> finalBusinessAdminMap = businessAdminMap;
        List<BusinessRolePageVO> list = businessRolePOPage.getRecords().stream().map(record -> {
            BusinessRolePageVO businessRolePageVO = new BusinessRolePageVO();
            BeanUtils.copyProperties(record, businessRolePageVO);
            if (businessRolePageVO.getCreator() != null && !businessRolePageVO.getCreator().equals(CommonConstant.business_zero.longValue())) {

                businessRolePageVO.setCreatorName(finalBusinessAdminMap.get(record.getCreator()));
            }
            return businessRolePageVO;
        }).toList();
        businessRolePageVOPage.setRecords(list);
        return businessRolePageVOPage;
    }


    @Transactional(rollbackFor = Exception.class)
    public String addRole(BusinessRoleAddVO businessRoleAddVO) {
        BusinessRolePO businessRolePO = new BusinessRolePO();
        BeanUtils.copyProperties(businessRoleAddVO, businessRolePO);
        Long currentTimeMillis = System.currentTimeMillis();
        businessRolePO.setBusinessSystem(BusinessSystemEnum.ADMIN_CENTER.getCode());
        businessRolePO.setCreatedTime(currentTimeMillis);
        businessRolePO.setUpdatedTime(currentTimeMillis);
        businessRolePO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        businessRolePO.setStatus(CommonConstant.business_one);
        businessRoleRepository.insert(businessRolePO);
        addRoleMenu(businessRoleAddVO.getMenuIds(), businessRolePO.getId());
        setLocalCacheMap(businessRoleAddVO.getMenuIds(),businessRolePO.getId());
        return businessRolePO.getId();
    }
    private void setLocalCacheMap(String[] menuIds,String roleId){
        LambdaQueryWrapper<BusinessMenuPO> lqw = new LambdaQueryWrapper<>();
        lqw.select(BusinessMenuPO::getApiUrl);
        lqw.in(BusinessMenuPO::getId,menuIds);
        lqw.isNotNull(BusinessMenuPO::getApiUrl);
        lqw.ne(BusinessMenuPO::getApiUrl,"");
        List<Object> menuUrlList = businessMenuRepository.selectObjs(lqw);
        Map<String,String> map = new HashMap<>();
        for (Object o:menuUrlList) {
            map.put(o.toString(),CommonConstant.business_one.toString());
        }
        RedisUtil.setLocalCachedMap(CacheConstants.KEY_ADMIN_AUTH_INFO_KEY, roleId, map);
    }

    private void addRoleMenu(String[] menuIds, String roleId) {
        List<BusinessRoleMenuPO> list = new ArrayList<BusinessRoleMenuPO>();
        for (String menuId : menuIds) {
            BusinessRoleMenuPO rm = new BusinessRoleMenuPO();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            businessRoleMenuRepository.batchRoleMenu(list);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<String> updateRole(BusinessRoleUpdateVO businessRoleUpdateVO) {

        BusinessRolePO businessRolePOold = businessRoleRepository.selectById(businessRoleUpdateVO.getId());
        if(!CommonConstant.business_zero.equals(businessRolePOold.getStatus())){
            throw new BaowangDefaultException(ResultCode.BUSINESS_ROLE_EDIT_ERROR);
        }
        handleDeleteIds(businessRoleUpdateVO.getId(),businessRoleUpdateVO.getMenuIds(),businessRoleUpdateVO.getUpdater());
        LambdaQueryWrapper<BusinessRoleMenuPO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessRoleMenuPO::getRoleId, businessRoleUpdateVO.getId());
        businessRoleMenuRepository.delete(lqw);
        addRoleMenu(businessRoleUpdateVO.getMenuIds(), businessRoleUpdateVO.getId());
        BusinessRolePO businessRolePO = new BusinessRolePO();
        BeanUtils.copyProperties(businessRoleUpdateVO, businessRolePO);
        this.businessRoleRepository.updateById(businessRolePO);
        setLocalCacheMap(businessRoleUpdateVO.getMenuIds(), businessRoleUpdateVO.getId());
        return ResponseVO.success(businessRoleUpdateVO.getId());
    }

    /**
     * 对比是否有删除授权菜单
     * @param
     * @return
     */
     private void handleDeleteIds (String roleId,String[] updateMenuIds,String currentAccount){
         LambdaQueryWrapper<BusinessRoleMenuPO> lqw = new LambdaQueryWrapper();
         lqw.eq(BusinessRoleMenuPO::getRoleId, roleId);
         lqw.select(BusinessRoleMenuPO::getMenuId);
         List<Object> oldList = businessRoleMenuRepository.selectObjs(lqw);
         List<String> menuIds = new ArrayList<>();
         if (!oldList.isEmpty()) {
             List<String> oldStrList = oldList.stream()
                     .map(Object::toString)
                     .collect(Collectors.toList());
             List<String> updateList = Arrays.asList(updateMenuIds);
             List<String> result = new ArrayList<>();

             for (String num : oldStrList) {
                 if (!updateList.contains(num)) {
                     result.add(num);
                 }
             }
             if(!result.isEmpty()){
                 //获取下级创建的所有角色
                 List<Object> roleIdList = new ArrayList<>();
                 getChildRoleIds(roleIdList,currentAccount);
                 System.out.println("角色列表："+roleIdList);
                 for (Object roleIdObj:roleIdList){
                     LambdaQueryWrapper<BusinessRoleMenuPO> deletelqw = new LambdaQueryWrapper();
                     deletelqw.eq(BusinessRoleMenuPO::getRoleId, roleIdObj.toString());
                     deletelqw.in(BusinessRoleMenuPO::getMenuId,result);
                     businessRoleMenuRepository.delete(deletelqw);
                     LambdaQueryWrapper<BusinessRoleMenuPO> queryLqw = new LambdaQueryWrapper();
                     queryLqw.eq(BusinessRoleMenuPO::getRoleId, roleIdObj.toString());
                     List<BusinessRoleMenuPO> businessRoleMenuPOS = businessRoleMenuRepository.selectList(queryLqw);
                     String[] roleMenuStrIds = businessRoleMenuPOS.stream().map(BusinessRoleMenuPO::getMenuId).toArray(String[]::new);
                     setLocalCacheMap(roleMenuStrIds, roleIdObj.toString());
                 }
             }


         }
     }
     private void getChildRoleIds(List<Object> roleIdList,String userName){
         LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
         lqw.eq(BusinessAdminPO::getBusinessSystem,BusinessSystemEnum.ADMIN_CENTER.getCode());
         lqw.eq(BusinessAdminPO::getCreator,userName);
         lqw.select(BusinessAdminPO::getUserName);
         List<Object> accounts = businessAdminService.listObjs(lqw);
         if(!accounts.isEmpty()){
             for (Object o:accounts){
                 LambdaQueryWrapper<BusinessRolePO> roleLqw = new LambdaQueryWrapper<>();
                 roleLqw.eq(BusinessRolePO::getBusinessSystem,BusinessSystemEnum.ADMIN_CENTER.getCode());
                 roleLqw.in(BusinessRolePO::getCreator,o);
                 roleLqw.select(BusinessRolePO::getId);
                 List<Object> roleIds = businessRoleRepository.selectObjs(roleLqw);
                 roleIdList.addAll(roleIds);
                 getChildRoleIds( roleIdList,o.toString());
             }
         }

     }


    public ResponseVO<Integer> deleteRole(IdVO idVO) {
        BusinessRolePO businessRolePOold = businessRoleRepository.selectById(idVO.getId());
        if(!CommonConstant.business_zero.equals(businessRolePOold.getStatus())){
            throw new BaowangDefaultException(ResultCode.BUSINESS_ROLE_DELETE_ERROR);
        }
        RedisUtil.deleteLocalCachedMap(CacheConstants.KEY_ADMIN_AUTH_INFO_KEY, idVO.getId());
        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.eq(BusinessAdminRolePO::getRoleId, idVO.getId());
        List<BusinessAdminRolePO> businessAdminRolePOS = businessAdminRoleRepository.selectList(adminRoleLqw);
        if (!businessAdminRolePOS.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.ROLE_EXIST_USER);
        }
        LambdaQueryWrapper<BusinessRoleMenuPO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessRoleMenuPO::getRoleId, idVO.getId());
        businessRoleMenuRepository.delete(lqw);
        return ResponseVO.success(businessRoleRepository.deleteById(idVO.getId()));
    }

    public BusinessRoleDetailVO getRoleById(IdVO idVO) {

        BusinessRoleDetailVO businessRoleDetailVO = new BusinessRoleDetailVO();
        BusinessRolePO businessRolePO = businessRoleRepository.selectById(idVO.getId());
        BeanUtils.copyProperties(businessRolePO, businessRoleDetailVO);
        LambdaQueryWrapper<BusinessRoleMenuPO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessRoleMenuPO::getRoleId, idVO.getId());
        List<BusinessRoleMenuPO> businessRoleMenuPOS = businessRoleMenuRepository.selectList(lqw);
        String[] menuIds = businessRoleMenuPOS.stream().map(BusinessRoleMenuPO::getMenuId).toArray(String[]::new);
        businessRoleDetailVO.setMenuIds(menuIds);
        return businessRoleDetailVO;
    }

    public boolean checkRoleNameUnique(String name) {
        LambdaQueryWrapper<BusinessRolePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessRolePO::getBusinessSystem, BusinessSystemEnum.ADMIN_CENTER.getCode());
        lqw.eq(BusinessRolePO::getName, name);
        List<BusinessRolePO> businessRolePOList = businessRoleRepository.selectList(lqw);
        return businessRolePOList.isEmpty();
    }


    public List<BusinessRoleDetailVO> listAllRole(BusinessRoleQueryVO businessRoleQueryVO) {
        LambdaQueryWrapper<BusinessRolePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessRolePO::getBusinessSystem, BusinessSystemEnum.ADMIN_CENTER.getCode());
        lqw.eq(BusinessRolePO::getStatus,CommonConstant.business_one);
        if (!businessRoleQueryVO.getIsSuperAdmin()) {
            lqw.eq(BusinessRolePO::getCreator, businessRoleQueryVO.getCurrentAdminId());
        }
        List<BusinessRolePO> businessRolePOList = businessRoleRepository.selectList(lqw);
        List<BusinessRoleDetailVO> list = businessRolePOList.stream().map(po -> {
            BusinessRoleDetailVO vo = new BusinessRoleDetailVO();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }).toList();
        return list;
    }
    public int updateAdminStatus(ChangeStatusVO changeStatusVO) {
        if(CommonConstant.business_zero.toString().equals(changeStatusVO.getAbleStatus())){
            RedisUtil.deleteLocalCachedMap(CacheConstants.KEY_ADMIN_AUTH_INFO_KEY, changeStatusVO.getId());
        }else{
            LambdaQueryWrapper<BusinessRoleMenuPO> lqw = new LambdaQueryWrapper();
            lqw.eq(BusinessRoleMenuPO::getRoleId, changeStatusVO.getId());
            List<BusinessRoleMenuPO> businessRoleMenuPOS = businessRoleMenuRepository.selectList(lqw);
            String[] menuIds = businessRoleMenuPOS.stream().map(BusinessRoleMenuPO::getMenuId).toArray(String[]::new);
            setLocalCacheMap(menuIds,changeStatusVO.getId());
        }
        BusinessRolePO businessRolePO = new BusinessRolePO();
        businessRolePO.setStatus(Integer.parseInt(changeStatusVO.getAbleStatus()));
        businessRolePO.setId(changeStatusVO.getId());
        businessRolePO.setUpdater(changeStatusVO.getUpdater());
        businessRolePO.setUpdatedTime(System.currentTimeMillis());
        return businessRoleRepository.updateById(businessRolePO);
    }

    public Map<String,Map<String,String>> getAllRoleMenuUrls() {
        Map<String,Map<String,String>> map = new HashMap<>();
        List<RoleMenuUrlVO> roleMenuUrlVOS = businessRoleMenuRepository.selectRoleMenuUrls();
        Map<String, List<RoleMenuUrlVO>> group = roleMenuUrlVOS.stream()
                .collect(Collectors.groupingBy(RoleMenuUrlVO::getRoleId));
        for(Map.Entry<String, List<RoleMenuUrlVO>> roleMenuMap : group.entrySet()){
            List<RoleMenuUrlVO> list = roleMenuMap.getValue();
            Map<String,String> menuMap = new HashMap<>();
            for (RoleMenuUrlVO roleMenu:list) {
                menuMap.put(roleMenu.getMenuUrl(),CommonConstant.business_one.toString());
            }
            map.put(roleMenuMap.getKey(),menuMap);
        }
        return map;
    }
}
