package com.cloud.baowang.system.service.member;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.system.api.constant.AdminPermissionApiConstant;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.enums.BusinessSystemEnum;
import com.cloud.baowang.system.api.vo.business.BusinessStorageMenuRespVO;
import com.cloud.baowang.system.api.enums.AdminLockStatusEnum;
import com.cloud.baowang.system.api.vo.adminLogin.AdminUpdateVO;
import com.cloud.baowang.system.api.vo.member.*;
import com.cloud.baowang.system.api.vo.param.SystemSiteSelectQuickEntryParam;
import com.cloud.baowang.system.po.member.*;
import com.cloud.baowang.system.repositories.member.*;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiqi
 */
@Service
@RequiredArgsConstructor
public class BusinessAdminService extends ServiceImpl<BusinessAdminRepository, BusinessAdminPO> {

    private final BusinessAdminRepository businessAdminRepository;

    private final BusinessAdminRoleRepository businessAdminRoleRepository;

    private final BusinessRoleMenuRepository businessRoleMenuRepository;

    private final BusinessMenuRepository businessMenuRepository;

    private final BusinessRoleRepository businessRoleRepository;

    private final AdminLogLockStatusService lockStatusService;


    public Page<BusinessAdminPageVO> listAdmin(BusinessAdminQueryVO businessAdminQueryVO) {
        Page<BusinessAdminPO> page = new Page<>(businessAdminQueryVO.getPageNumber(), businessAdminQueryVO.getPageSize());
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(null != businessAdminQueryVO.getStatus(), BusinessAdminPO::getStatus, businessAdminQueryVO.getStatus());
        lqw.eq(BusinessAdminPO::getBusinessSystem, BusinessSystemEnum.ADMIN_CENTER.getCode());
        if (!businessAdminQueryVO.getIsSuperAdmin()) {
            lqw.eq(BusinessAdminPO::getIsSuperAdmin, YesOrNoEnum.NO.getCode());
            lqw.eq(BusinessAdminPO::getCreator, businessAdminQueryVO.getAdminUserName());

        }
        lqw.ne(BusinessAdminPO::getUserName, businessAdminQueryVO.getAdminUserName());
        lqw.like(StringUtils.isNotBlank(businessAdminQueryVO.getUserName()), BusinessAdminPO::getUserName, businessAdminQueryVO.getUserName());
        lqw.orderByDesc(BusinessAdminPO::getStatus);
        lqw.orderByDesc(BusinessAdminPO::getCreatedTime);
        Page<BusinessAdminPO> businessAdminPOPage = businessAdminRepository.selectPage(page, lqw);
        Page<BusinessAdminPageVO> businessAdminPageVOPage = new Page<>();
        BeanUtils.copyProperties(businessAdminPOPage, businessAdminPageVOPage);
        /*List<String> createIds = businessAdminPOPage.getRecords().stream().map(BusinessAdminPO::getCreator).toList();
        List<BusinessAdminVO> businessAdminList = getUserByIds(createIds);
        Map<String, String> businessAdminMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(businessAdminList)) {
            businessAdminMap = businessAdminList.stream().collect(Collectors.toMap(BusinessAdminVO::getId, BusinessAdminVO::getUserName, (k1, k2) -> k2));
        }
        Map<String, String> finalBusinessAdminMap = businessAdminMap;*/

        List<BusinessAdminPageVO> list = businessAdminPOPage.getRecords().stream().map(record -> {
            BusinessAdminPageVO businessAdminPageVO = new BusinessAdminPageVO();
            BeanUtils.copyProperties(record, businessAdminPageVO);
            if (lockStatusService.checkAdminIsLock(record.getSiteCode(), record.getUserName())) {
                businessAdminPageVO.setLockStatus(AdminLockStatusEnum.LOCKED.getCode());
            } else {
                businessAdminPageVO.setLockStatus(AdminLockStatusEnum.UN_LOCK.getCode());
            }
            /*String creatorId = businessAdminPageVO.getCreator();
            if (creatorId != null && !creatorId.equals(0L)) {
                businessAdminPageVO.setCreatorName(finalBusinessAdminMap.get(creatorId));
            }*/
            return businessAdminPageVO;
        }).toList();
        businessAdminPageVOPage.setRecords(list);
        return businessAdminPageVOPage;
    }


    @Transactional(rollbackFor = Exception.class)
    public String addAdmin(BusinessAdminAddVO businessAdminAddVO) {
        BusinessAdminPO businessAdminPO = new BusinessAdminPO();
        BeanUtils.copyProperties(businessAdminAddVO, businessAdminPO);
        businessAdminPO.setBusinessSystem(BusinessSystemEnum.ADMIN_CENTER.getCode());
        Long currentTimeMillis = System.currentTimeMillis();
        businessAdminPO.setUserId(SnowFlakeUtils.getCommonRandomId());
        businessAdminPO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        businessAdminPO.setCreatedTime(currentTimeMillis);
        businessAdminPO.setUpdatedTime(currentTimeMillis);
        businessAdminPO.setStatus(CommonConstant.business_one);
        businessAdminRepository.insert(businessAdminPO);
        if (null != businessAdminAddVO.getRoleIds() && businessAdminAddVO.getRoleIds().length > 0) {
            addAdminRole(businessAdminAddVO.getRoleIds(), businessAdminPO.getId());
        }

        return businessAdminPO.getId();
    }

    private void addAdminRole(String[] roleIds, String AdminId) {
        List<BusinessAdminRolePO> list = new ArrayList<BusinessAdminRolePO>();
        for (String menuId : roleIds) {
            BusinessAdminRolePO ar = new BusinessAdminRolePO();
            ar.setAdminId(AdminId);
            ar.setRoleId(menuId);
            list.add(ar);
        }
        if (list.size() > 0) {
            businessAdminRoleRepository.batchAdminRole(list);
        }
        setRoleUserNums();
    }

    /**
     * 更新角色使用数
     */
    private void setRoleUserNums() {
        //统计角色使用数
        List<BusinessAdminRolePO> adminRolePOS = this.businessAdminRoleRepository.selectList(new LambdaQueryWrapper<BusinessAdminRolePO>());
        Map<String, List<BusinessAdminRolePO>> groupMap = adminRolePOS.stream()
                .collect(Collectors.groupingBy(BusinessAdminRolePO::getRoleId));
        for (Map.Entry<String, List<BusinessAdminRolePO>> map : groupMap.entrySet()) {
            String roleId = map.getKey();
            BusinessRolePO businessRolePO = new BusinessRolePO();
            businessRolePO.setId(roleId);
            businessRolePO.setUseNums(map.getValue().size());
            this.businessRoleRepository.updateById(businessRolePO);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateAdmin(BusinessAdminUpdateVO businessAdminUpdateVO) {
        BusinessAdminPO businessAdminPO = new BusinessAdminPO();
        BeanUtils.copyProperties(businessAdminUpdateVO, businessAdminPO);
        businessAdminPO.setUpdatedTime(System.currentTimeMillis());
        businessAdminRepository.updateById(businessAdminPO);
        if (null != businessAdminUpdateVO.getRoleIds() && businessAdminUpdateVO.getRoleIds().length > 0) {
            LambdaQueryWrapper<BusinessAdminRolePO> lqw = new LambdaQueryWrapper();
            lqw.eq(BusinessAdminRolePO::getAdminId, businessAdminUpdateVO.getId());
            businessAdminRoleRepository.delete(lqw);
            addAdminRole(businessAdminUpdateVO.getRoleIds(), businessAdminUpdateVO.getId());
        }
        return businessAdminUpdateVO.getId();
    }

    public int deleteAdmin(IdVO idVO) {

        LambdaQueryWrapper<BusinessAdminRolePO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessAdminRolePO::getAdminId, idVO.getId());
        businessAdminRoleRepository.delete(lqw);
        setRoleUserNums();
        return businessAdminRepository.deleteById(idVO.getId());
    }

    public BusinessAdminDetailVO getAdminById(IdVO idVO) {

        BusinessAdminDetailVO businessAdminDetailVO = new BusinessAdminDetailVO();
        BusinessAdminPO businessAdminPO = businessAdminRepository.selectById(idVO.getId());
        BeanUtils.copyProperties(businessAdminPO, businessAdminDetailVO);
        LambdaQueryWrapper<BusinessAdminRolePO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessAdminRolePO::getAdminId, idVO.getId());
        List<BusinessAdminRolePO> businessAdminRolePOS = businessAdminRoleRepository.selectList(lqw);
        String[] roleIds = businessAdminRolePOS.stream().map(BusinessAdminRolePO::getRoleId).toArray(String[]::new);
        businessAdminDetailVO.setRoleIds(roleIds);
        return businessAdminDetailVO;
    }

    public boolean checkAdminNameUnique(NameUniqueVO nameUniqueVO) {
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getUserName, nameUniqueVO.getUserName());
        lqw.eq(BusinessAdminPO::getBusinessSystem, BusinessSystemEnum.ADMIN_CENTER.getCode());
        lqw.ne(null != nameUniqueVO.getId(), BusinessAdminPO::getId, nameUniqueVO.getId());
        List<BusinessAdminPO> businessAdminPOList = businessAdminRepository.selectList(lqw);
        return businessAdminPOList.isEmpty();
    }


    public int updateAdminStatus(ChangeStatusVO changeStatusVO) {
        BusinessAdminPO businessAdminPO = new BusinessAdminPO();
        businessAdminPO.setStatus(Integer.parseInt(changeStatusVO.getAbleStatus()));
        businessAdminPO.setId(changeStatusVO.getId());
        businessAdminPO.setUpdatedTime(System.currentTimeMillis());
        return businessAdminRepository.updateById(businessAdminPO);
    }

    public int adminUnLock(IdVO idVO) {
        BusinessAdminPO businessAdminPO = businessAdminRepository.selectById(idVO.getId());
        //businessAdminPO.setLockStatus(AdminLockStatusEnum.UN_LOCK.getCode());
        //RedisUtil.deleteKey(TokenConstants.PWD_ERR_CNT_KEY + businessAdminPO.getUserName());
        //return businessAdminRepository.updateById(businessAdminPO);
        lockStatusService.removeAdminLockStatus(businessAdminPO.getSiteCode(), businessAdminPO.getUserName());
        return 1;
    }

    public int resetPassword(BusinessAdminResetPasswordVO businessAdminResetPasswordVO) {
        BusinessAdminPO businessAdminPO = new BusinessAdminPO();
        businessAdminPO.setId(businessAdminResetPasswordVO.getId());
        businessAdminPO.setPassword(businessAdminResetPasswordVO.getPassword());
        return businessAdminRepository.updateById(businessAdminPO);
    }

    public BusinessAdminVO getAdminByUserName(String userName) {
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getUserName, userName);
        lqw.eq(BusinessAdminPO::getBusinessSystem, BusinessSystemEnum.ADMIN_CENTER.getCode());
        BusinessAdminPO businessAdminPO = businessAdminRepository.selectOne(lqw);
        List<String> apiUrls = new ArrayList<>();
        List<String> urls = null;
        if (null == businessAdminPO) {
            return new BusinessAdminVO();
        }
        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.eq(BusinessAdminRolePO::getAdminId, businessAdminPO.getId());
        List<BusinessAdminRolePO> adminRolePOS = businessAdminRoleRepository.selectList(adminRoleLqw);
        List<String> roleIds = adminRolePOS.stream().map(BusinessAdminRolePO::getRoleId).distinct().toList();


        //获取用户菜单权限列表
        if (YesOrNoEnum.YES.getCode().equals(businessAdminPO.getIsSuperAdmin())) {
            apiUrls = getAllMenuApis();
            urls = getAllMenuUrls();
        } else {
            apiUrls = getUserMenuApis(businessAdminPO.getId());
            urls = getUserMenuUrls(businessAdminPO.getId());
        }
        BusinessAdminVO businessAdminVO = new BusinessAdminVO();
        if (urls.contains(AdminPermissionApiConstant.USER_DATA_DESENSITIZATION)) {
            businessAdminVO.setDataDesensitization(false);
        } else {
            businessAdminVO.setDataDesensitization(true);
        }
        if (CollectionUtil.isNotEmpty(roleIds)) {
            LambdaQueryWrapper<BusinessRolePO> warpper = new LambdaQueryWrapper<>();
            warpper.in(BusinessRolePO::getId, roleIds);
            List<BusinessRolePO> businessRolePOS = businessRoleRepository.selectList(warpper);
            List<String> roleNameList = Optional.of(businessRolePOS).orElse(Lists.newArrayList())
                    .stream().map(BusinessRolePO::getName).distinct().collect(Collectors.toList());
            businessAdminVO.setRoleNames(roleNameList);
        }

        businessAdminVO.setRoleIds(roleIds);
        businessAdminVO.setApiPermissions(apiUrls);
        businessAdminVO.setUrlList(urls);
        BeanUtils.copyProperties(businessAdminPO, businessAdminVO);
        businessAdminVO.setUserId(businessAdminPO.getUserId());
        return businessAdminVO;
    }

    private List<String> getAllMenuApis() {
        LambdaQueryWrapper<BusinessMenuPO> menuLqw = new LambdaQueryWrapper<>();
        menuLqw.select(BusinessMenuPO::getApiUrl);
        List<Object> apiUrls = businessMenuRepository.selectObjs(menuLqw);
        List<String> apiList = new ArrayList<>();
        if (apiUrls.isEmpty()) {
            return apiList;
        }
        Set<String> apiUrlSet = new HashSet<>();
        for (Object o : apiUrls) {
            apiUrlSet.add(null == o ? "" : o.toString().trim());
        }
        return new ArrayList<>(apiUrlSet);
    }

    private List<String> getAllMenuUrls() {
        LambdaQueryWrapper<BusinessMenuPO> menuLqw = new LambdaQueryWrapper<>();
        menuLqw.select(BusinessMenuPO::getApiUrl);
        List<Object> urls = businessMenuRepository.selectObjs(menuLqw);
        List<String> urlList = new ArrayList<>();
        if (urls.isEmpty()) {
            return urlList;
        }
        Set<String> urlSet = new HashSet<>();
        for (Object o : urls) {
            urlSet.add(null == o ? "" : o.toString().trim());
        }
        return new ArrayList<>(urlSet);
    }

    private List<String> getUserMenuApis(String adminId) {
        List<String> apiList = new ArrayList<>();
        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.select(BusinessAdminRolePO::getRoleId);
        adminRoleLqw.eq(BusinessAdminRolePO::getAdminId, adminId);

        List<Object> roleIds = businessAdminRoleRepository.selectObjs(adminRoleLqw);
        if (roleIds.isEmpty()) {
            return apiList;
        }
        LambdaQueryWrapper<BusinessRoleMenuPO> roleMenuLqw = new LambdaQueryWrapper<>();
        roleMenuLqw.select(BusinessRoleMenuPO::getMenuId);
        roleMenuLqw.in(BusinessRoleMenuPO::getRoleId, roleIds);
        List<Object> menuIds = businessRoleMenuRepository.selectObjs(roleMenuLqw);
        if (menuIds.isEmpty()) {
            return apiList;
        }
        LambdaQueryWrapper<BusinessMenuPO> menuLqw = new LambdaQueryWrapper<>();
        menuLqw.select(BusinessMenuPO::getApiUrl);
        menuLqw.in(BusinessMenuPO::getId, menuIds);

        List<Object> apiUrls = businessMenuRepository.selectObjs(menuLqw);

        if (apiUrls.isEmpty()) {
            return apiList;
        }
        Set<String> apiUrlSet = new HashSet<>();
        for (Object o : apiUrls) {
            apiUrlSet.add(null == o ? "" : o.toString().trim());
        }
        return new ArrayList<>(apiUrlSet);

    }

    private List<String> getUserMenuUrls(String adminId) {
        List<String> urlList = new ArrayList<>();
        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.select(BusinessAdminRolePO::getRoleId);
        adminRoleLqw.eq(BusinessAdminRolePO::getAdminId, adminId);

        List<Object> roleIds = businessAdminRoleRepository.selectObjs(adminRoleLqw);
        if (roleIds.isEmpty()) {
            return urlList;
        }
        LambdaQueryWrapper<BusinessRoleMenuPO> roleMenuLqw = new LambdaQueryWrapper<>();
        roleMenuLqw.select(BusinessRoleMenuPO::getMenuId);
        roleMenuLqw.in(BusinessRoleMenuPO::getRoleId, roleIds);
        List<Object> menuIds = businessRoleMenuRepository.selectObjs(roleMenuLqw);
        if (menuIds.isEmpty()) {
            return urlList;
        }
        LambdaQueryWrapper<BusinessMenuPO> menuLqw = new LambdaQueryWrapper<>();
        menuLqw.select(BusinessMenuPO::getUrl);
        menuLqw.in(BusinessMenuPO::getId, menuIds);

        List<Object> urls = businessMenuRepository.selectObjs(menuLqw);

        if (urls.isEmpty()) {
            return urlList;
        }
        Set<String> urlSet = new HashSet<>();
        for (Object o : urls) {
            urlSet.add(null == o ? "" : o.toString().trim());
        }
        return new ArrayList<>(urlSet);

    }

    public int lockAdmin(String userName) {
        LambdaUpdateWrapper<BusinessAdminPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BusinessAdminPO::getUserName, userName);
        updateWrapper.set(BusinessAdminPO::getLockStatus, AdminLockStatusEnum.LOCKED.getCode());
        return businessAdminRepository.update(null, updateWrapper);

    }

    public BusinessAdminVO getBusinessAdminById(String id) {
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getId, id);
        BusinessAdminPO businessAdminPO = businessAdminRepository.selectOne(lqw);
        BusinessAdminVO businessAdminVO = new BusinessAdminVO();
        if (businessAdminPO != null) {
            BeanUtils.copyProperties(businessAdminPO, businessAdminVO);
        }
        LambdaQueryWrapper<BusinessAdminRolePO> roleLqw = new LambdaQueryWrapper();
        roleLqw.eq(BusinessAdminRolePO::getAdminId, id);
        List<BusinessAdminRolePO> businessAdminRolePOS = businessAdminRoleRepository.selectList(roleLqw);
        List<String> roleIds = businessAdminRolePOS.stream().map(BusinessAdminRolePO::getRoleId).collect(Collectors.toList());
        businessAdminVO.setRoleIds(roleIds);
        return businessAdminVO;
    }

    public List<String> getUserIdsByUseName(String userName) {
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.like(BusinessAdminPO::getUserName, userName);
        List<BusinessAdminPO> businessAdminPOList = businessAdminRepository.selectList(lqw);
        List<String> adminIds = Lists.newArrayList();
        if (!businessAdminPOList.isEmpty()) {
            adminIds = businessAdminPOList.stream().map(BusinessAdminPO::getId).collect(Collectors.toList());
        }
        return adminIds;
    }

    public String getUserNameById(String id) {
        BusinessAdminPO businessAdminPO = businessAdminRepository.selectById(id);
        if (null != businessAdminPO) {
            return businessAdminPO.getUserName();
        }
        return "";
    }

    public List<BusinessAdminVO> getUserByIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<BusinessAdminPO> list = new LambdaQueryChainWrapper<>(baseMapper).in(BusinessAdminPO::getId, ids).list();
        if (CollectionUtil.isNotEmpty(list)) {
            return ConvertUtil.entityListToModelList(list, BusinessAdminVO.class);
        }
        return Collections.emptyList();
    }

    public List<String> getRoleIdsByUseName(String adminId) {

        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.eq(BusinessAdminRolePO::getAdminId, adminId);
        List<BusinessAdminRolePO> businessAdminPOList = businessAdminRoleRepository.selectList(adminRoleLqw);
        List<String> roleIds = Lists.newArrayList();
        if (!businessAdminPOList.isEmpty()) {
            roleIds = businessAdminPOList.stream().map(BusinessAdminRolePO::getRoleId).collect(Collectors.toList());
        }
        return roleIds;
    }

    public Integer accountSet(AccountSetParamVO accountSetParamVO) {

        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getUserName, accountSetParamVO.getUserName());
        BusinessAdminPO businessAdminPO = businessAdminRepository.selectOne(lqw);

        businessAdminPO.setPassword(accountSetParamVO.getNewPassword());
        return businessAdminRepository.updateById(businessAdminPO);
    }


    public Map<String, String> id2NameByIds(Set<String> ids) {
        List<BusinessAdminPO> businessAdminPOS = businessAdminRepository.selectBatchIds(ids);
        if (CollectionUtil.isEmpty(businessAdminPOS)) {
            return Maps.newHashMap();
        }
        return businessAdminPOS.stream().collect(Collectors.toMap(BusinessAdminPO::getId, BusinessAdminPO::getUserName, (o, n) -> n));
    }

    public int editPassword(AdminPasswordEditVO editVO) {
        BusinessAdminPO po = new BusinessAdminPO();
        po.setId(editVO.getId());
        po.setPassword(editVO.getNewPassword());
        return businessAdminRepository.updateById(po);

    }

    public boolean update(AdminUpdateVO adminUpdateVO) {
        LambdaUpdateWrapper<BusinessAdminPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(BusinessAdminPO::getId, adminUpdateVO.getId())
                .set(StringUtils.isNotEmpty(adminUpdateVO.getPassword()), BusinessAdminPO::getPassword, adminUpdateVO.getPassword())
                .set(StringUtils.isNotEmpty(adminUpdateVO.getGoogleAuthKey()), BusinessAdminPO::getGoogleAuthKey, adminUpdateVO.getGoogleAuthKey())
                .set(ObjectUtils.isNotEmpty(adminUpdateVO.getIsSetGoogle()), BusinessAdminPO::getIsSetGoogle, adminUpdateVO.getIsSetGoogle())
                .set(adminUpdateVO.getLastLoginTime() != null, BusinessAdminPO::getLastLoginTime, adminUpdateVO.getLastLoginTime())
                .set(BusinessAdminPO::getUpdatedTime, System.currentTimeMillis());
        return this.update(null, lambdaUpdate);
    }

    public Integer resetGoogleAuthKey(IdVO idVO) {
        BusinessAdminPO businessAdminPO = this.baseMapper.selectById(idVO.getId());
        businessAdminPO.setIsSetGoogle(CommonConstant.business_one);
        businessAdminPO.setGoogleAuthKey(GoogleAuthUtil.generateSecretKey());
        return this.baseMapper.updateById(businessAdminPO);
    }

    public Boolean updateQuickButton(String adminId, List<BusinessStorageMenuRespVO> quickEntry) {
        LambdaUpdateWrapper<BusinessAdminPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<BusinessAdminPO>();
        lambdaUpdateWrapper.set(BusinessAdminPO::getHomeQuickButton, JSONObject.toJSONString(quickEntry));
        lambdaUpdateWrapper.set(BusinessAdminPO::getUpdatedTime, System.currentTimeMillis());
        lambdaUpdateWrapper.eq(BusinessAdminPO::getId, adminId);
        return this.update(lambdaUpdateWrapper);
    }

    public List<BusinessStorageMenuRespVO> getQuickButton(SystemSiteSelectQuickEntryParam vo) {
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getId, vo.getAdminId());
        lqw.eq(BusinessAdminPO::getSiteCode, vo.getSiteCode());
        BusinessAdminPO adminPO = businessAdminRepository.selectOne(lqw);
        List<BusinessStorageMenuRespVO> result = new ArrayList<>();
        if (adminPO != null && StringUtils.isNotBlank(adminPO.getHomeQuickButton())) {
            try {
                result = JSON.parseArray(adminPO.getHomeQuickButton(), BusinessStorageMenuRespVO.class);
            } catch (Exception e) {
                log.error("操作人:" + vo.getAdminId() + "getQuickButton:" + e.getMessage());
            }
        }
        return result;
    }

    public List<String> selectUserBySiteCodeAndApiUrl(String siteCode, List<String> menuKey) {
        return businessAdminRepository.selectUserBySiteCodeAndApiUrl(siteCode, menuKey);
    }

    public List<String> getUserIdsBySiteCode(String siteCode) {
        LambdaQueryWrapper<BusinessAdminPO> query = Wrappers.lambdaQuery();
        query.eq(BusinessAdminPO::getSiteCode, siteCode);
        List<BusinessAdminPO> list = this.list(query);
        List<String> result = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            result = list.stream().map(BusinessAdminPO::getId).filter(StringUtils::isNotBlank).toList();
        }
        return result;
    }
}
