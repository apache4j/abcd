package com.cloud.baowang.system.service.site;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.system.api.constant.AdminPermissionApiConstant;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.api.AdminLogLockStatusApi;
import com.cloud.baowang.system.api.enums.AdminLockStatusEnum;
import com.cloud.baowang.system.api.enums.BusinessSystemEnum;
import com.cloud.baowang.system.api.vo.adminLogin.AdminUpdateVO;
import com.cloud.baowang.system.api.vo.member.AccountSetParamVO;
import com.cloud.baowang.system.api.vo.member.AdminPasswordEditVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.member.NameUniqueVO;
import com.cloud.baowang.system.api.vo.site.admin.*;
import com.cloud.baowang.system.po.member.*;
import com.cloud.baowang.system.repositories.member.BusinessRoleRepository;
import com.cloud.baowang.system.repositories.site.*;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiqi
 */
@RequiredArgsConstructor
@Service
public class SiteAdminService extends ServiceImpl<SiteAdminRepository, BusinessAdminPO> {

    private final SiteAdminRepository siteAdminRepository;

    private final SiteAdminRoleRepository siteAdminRoleRepository;

    private final SiteRoleMenuRepository siteRoleMenuRepository;

    private final SiteMenuRepository siteMenuRepository;

    private final SiteRoleRepository siteRoleRepository;

    private final AdminLogLockStatusApi lockStatusApi;

    private final BusinessRoleRepository businessRoleRepository;


    public Page<SiteAdminPageVO> listAdmin(SiteAdminQueryVO siteAdminQueryVO) {
        Page<BusinessAdminPO> page = new Page<>(siteAdminQueryVO.getPageNumber(), siteAdminQueryVO.getPageSize());
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getBusinessSystem, BusinessSystemEnum.SITE.getCode());
        lqw.eq(StringUtils.isNotBlank(siteAdminQueryVO.getAllowIps()),BusinessAdminPO::getAllowIps,siteAdminQueryVO.getAllowIps());
        lqw.eq(null != siteAdminQueryVO.getStatus(), BusinessAdminPO::getStatus, siteAdminQueryVO.getStatus());
        if (!siteAdminQueryVO.getIsSuperAdmin()) {
            lqw.eq(BusinessAdminPO::getIsSuperAdmin, YesOrNoEnum.NO.getCode());
            lqw.eq(BusinessAdminPO::getCreator, siteAdminQueryVO.getAdminName());
        }
        lqw.ne(BusinessAdminPO::getUserName, siteAdminQueryVO.getAdminName());
        lqw.eq(StringUtils.isNotBlank(siteAdminQueryVO.getSiteCode()), BusinessAdminPO::getSiteCode, siteAdminQueryVO.getSiteCode());
        lqw.like(StringUtils.isNotBlank(siteAdminQueryVO.getUserName()), BusinessAdminPO::getUserName, siteAdminQueryVO.getUserName());
        lqw.orderByDesc(BusinessAdminPO::getStatus);
        lqw.orderByDesc(BusinessAdminPO::getCreatedTime);
        Page<BusinessAdminPO> siteAdminPOPage = siteAdminRepository.selectPage(page, lqw);
        Page<SiteAdminPageVO> siteAdminPageVOPage = new Page<>();
        BeanUtils.copyProperties(siteAdminPOPage, siteAdminPageVOPage);
//        List<String> createIds = siteAdminPOPage.getRecords().stream().map(BusinessAdminPO::getCreator).toList();
//        List<SiteAdminVO> siteAdminList = getUserByIds(createIds);
//        Map<String, String> siteAdminMap = new HashMap<>();
//        if (CollectionUtil.isNotEmpty(siteAdminList)) {
//            siteAdminMap = siteAdminList.stream().collect(Collectors.toMap(SiteAdminVO::getId, SiteAdminVO::getUserName, (k1, k2) -> k2));
//        }
//        Map<String, String> finalSiteAdminMap = siteAdminMap;

        List<SiteAdminPageVO> list = siteAdminPOPage.getRecords().stream().map(record -> {
            SiteAdminPageVO siteAdminPageVO = new SiteAdminPageVO();
            BeanUtils.copyProperties(record, siteAdminPageVO);
            if (lockStatusApi.checkAdminIsLock(record.getSiteCode(), record.getUserName())) {
                siteAdminPageVO.setLockStatus(AdminLockStatusEnum.LOCKED.getCode());
            } else {
                siteAdminPageVO.setLockStatus(AdminLockStatusEnum.UN_LOCK.getCode());
            }
            return siteAdminPageVO;
        }).toList();
        siteAdminPageVOPage.setRecords(list);
        return siteAdminPageVOPage;
    }


    @Transactional(rollbackFor = Exception.class)
    public String addAdmin(SiteAdminAddVO siteAdminAddVO) {
        BusinessAdminPO siteAdminPO = new BusinessAdminPO();
        BeanUtils.copyProperties(siteAdminAddVO, siteAdminPO);
        Long currentTimeMillis = System.currentTimeMillis();
        siteAdminPO.setBusinessSystem(BusinessSystemEnum.SITE.getCode());
        siteAdminPO.setUserId(SnowFlakeUtils.getCommonRandomId());
        siteAdminPO.setCreatedTime(currentTimeMillis);
        siteAdminPO.setUpdatedTime(currentTimeMillis);
        siteAdminPO.setStatus(CommonConstant.business_one);
        if (StringUtils.isNotBlank(siteAdminAddVO.getIsSuperAdmin())) {
            siteAdminPO.setPassword(encryptPassword(siteAdminPO.getPassword()));
        }
        siteAdminRepository.insert(siteAdminPO);
        if (StringUtils.isBlank(siteAdminAddVO.getIsSuperAdmin())
                || CommonConstant.business_zero.toString().equals(siteAdminAddVO.getIsSuperAdmin())) {
            addAdminRole(siteAdminAddVO.getRoleIds(), siteAdminPO.getId());
        }

        return siteAdminPO.getId();
    }

    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
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
            siteAdminRoleRepository.batchAdminRole(list);
        }
        setRoleUserNums();
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateAdmin(SiteAdminUpdateVO siteAdminUpdateVO) {
        BusinessAdminPO siteAdminPO = new BusinessAdminPO();
        BeanUtils.copyProperties(siteAdminUpdateVO, siteAdminPO);
        siteAdminPO.setUpdatedTime(System.currentTimeMillis());
        siteAdminPO.setHomeQuickButton("");
        siteAdminRepository.updateById(siteAdminPO);
        LambdaQueryWrapper<BusinessAdminRolePO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessAdminRolePO::getAdminId, siteAdminUpdateVO.getId());
        siteAdminRoleRepository.delete(lqw);
        addAdminRole(siteAdminUpdateVO.getRoleIds(), siteAdminUpdateVO.getId());
        return siteAdminUpdateVO.getId();
    }

    public int deleteAdmin(IdVO idVO) {

        LambdaQueryWrapper<BusinessAdminRolePO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessAdminRolePO::getAdminId, idVO.getId());
        siteAdminRoleRepository.delete(lqw);
        setRoleUserNums();
        return siteAdminRepository.deleteById(idVO.getId());
    }

    private void setRoleUserNums() {
        //统计角色使用数
        List<BusinessAdminRolePO> adminRolePOS = this.siteAdminRoleRepository.selectList(new LambdaQueryWrapper<BusinessAdminRolePO>());
        Map<String, List<BusinessAdminRolePO>> groupMap = adminRolePOS.stream()
                .collect(Collectors.groupingBy(BusinessAdminRolePO::getRoleId));
        for (Map.Entry<String, List<BusinessAdminRolePO>> map : groupMap.entrySet()) {
            String roleId = map.getKey();
            BusinessRolePO businessRolePO = new BusinessRolePO();
            businessRolePO.setId(roleId);
            businessRolePO.setUseNums(map.getValue().size());
            this.siteRoleRepository.updateById(businessRolePO);
        }
    }

    public SiteAdminDetailVO getAdminById(IdVO idVO) {

        SiteAdminDetailVO siteAdminDetailVO = new SiteAdminDetailVO();
        BusinessAdminPO siteAdminPO = siteAdminRepository.selectById(idVO.getId());
        BeanUtils.copyProperties(siteAdminPO, siteAdminDetailVO);
        LambdaQueryWrapper<BusinessAdminRolePO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessAdminRolePO::getAdminId, idVO.getId());
        List<BusinessAdminRolePO> siteAdminRolePOS = siteAdminRoleRepository.selectList(lqw);
        String[] roleIds = siteAdminRolePOS.stream().map(BusinessAdminRolePO::getRoleId).toArray(String[]::new);
        siteAdminDetailVO.setRoleIds(roleIds);
        return siteAdminDetailVO;
    }

    public boolean checkAdminNameUnique(NameUniqueVO nameUniqueVO) {
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getUserName, nameUniqueVO.getUserName());
        lqw.eq(BusinessAdminPO::getBusinessSystem, BusinessSystemEnum.SITE.getCode());
        lqw.eq(BusinessAdminPO::getSiteCode, nameUniqueVO.getSiteCode());
        lqw.ne(null != nameUniqueVO.getId(), BusinessAdminPO::getId, nameUniqueVO.getId());
        List<BusinessAdminPO> siteAdminPOList = siteAdminRepository.selectList(lqw);
        return siteAdminPOList.isEmpty();
    }


    public int updateAdminStatus(ChangeStatusVO changeStatusVO) {
        BusinessAdminPO siteAdminPO = new BusinessAdminPO();
        siteAdminPO.setStatus(Integer.parseInt(changeStatusVO.getAbleStatus()));
        siteAdminPO.setId(changeStatusVO.getId());
        siteAdminPO.setUpdatedTime(System.currentTimeMillis());
        return siteAdminRepository.updateById(siteAdminPO);
    }

    public int adminUnLock(IdVO idVO) {
        BusinessAdminPO siteAdminPO = siteAdminRepository.selectById(idVO.getId());
        //siteAdminPO.setLockStatus(AdminLockStatusEnum.UN_LOCK.getCode());
        //RedisUtil.deleteKey(TokenConstants.PWD_ERR_CNT_KEY + siteAdminPO.getUserName());
        //return siteAdminRepository.updateById(siteAdminPO);
        if (siteAdminPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        lockStatusApi.removeAdminLockStatus(siteAdminPO.getSiteCode(), siteAdminPO.getUserName());
        return 1;
    }

    public int resetPassword(SiteAdminResetPasswordVO siteAdminResetPasswordVO) {
        BusinessAdminPO siteAdminPO = new BusinessAdminPO();
        siteAdminPO.setId(siteAdminResetPasswordVO.getId());
        siteAdminPO.setPassword(siteAdminResetPasswordVO.getPassword());
        siteAdminPO.setGoogleAuthKey("");
        return siteAdminRepository.updateById(siteAdminPO);
    }

    public SiteAdminVO getAdminByUserName(String userName, String siteCode) {
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getUserName, userName);
        lqw.eq(StringUtils.isNotEmpty(siteCode), BusinessAdminPO::getSiteCode, siteCode);
        BusinessAdminPO siteAdminPO = siteAdminRepository.selectOne(lqw);
        List<String> apiUrls = new ArrayList<>();
        List<String> urls = null;
        if (null == siteAdminPO) {
            return new SiteAdminVO();
        }
        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.eq(BusinessAdminRolePO::getAdminId, siteAdminPO.getId());
        List<BusinessAdminRolePO> adminRolePOS = siteAdminRoleRepository.selectList(adminRoleLqw);
        List<String> roleIds = adminRolePOS.stream().map(BusinessAdminRolePO::getRoleId).distinct().toList();

        //获取用户菜单权限列表
        if (YesOrNoEnum.YES.getCode().equals(siteAdminPO.getIsSuperAdmin())) {
            apiUrls = getAllMenuApis();
            urls = getAllMenuUrls();
        } else {
            apiUrls = getUserMenuApis(siteAdminPO.getId());
            urls = getUserMenuUrls(siteAdminPO.getId());
        }
        SiteAdminVO siteAdminVO = new SiteAdminVO();
        siteAdminVO.setRoleIds(roleIds);
        if (urls.contains(AdminPermissionApiConstant.USER_DATA_DESENSITIZATION)) {
            siteAdminVO.setDataDesensitization(false);
        } else {
            siteAdminVO.setDataDesensitization(true);
        }
        if(CollectionUtil.isNotEmpty(roleIds)) {
            LambdaQueryWrapper<BusinessRolePO> warpper = new LambdaQueryWrapper<>();
            warpper.in(BusinessRolePO::getId, roleIds);
            List<BusinessRolePO> businessRolePOS = businessRoleRepository.selectList(warpper);
            List<String> roleNameList = Optional.of(businessRolePOS).orElse(Lists.newArrayList())
                    .stream().map(BusinessRolePO::getName).distinct().collect(Collectors.toList());
            siteAdminVO.setRoleNames(roleNameList);
        }

        siteAdminVO.setRoleIds(roleIds);
        siteAdminVO.setApiPermissions(apiUrls);
        siteAdminVO.setUrlList(urls);
        BeanUtils.copyProperties(siteAdminPO, siteAdminVO);
        siteAdminVO.setUserId(siteAdminPO.getUserId());
        return siteAdminVO;
    }

    private List<String> getAllMenuApis() {
        LambdaQueryWrapper<BusinessMenuPO> menuLqw = new LambdaQueryWrapper<>();
        menuLqw.select(BusinessMenuPO::getApiUrl);
        List<Object> apiUrls = siteMenuRepository.selectObjs(menuLqw);
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
        List<Object> urls = siteMenuRepository.selectObjs(menuLqw);
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

        List<Object> roleIds = siteAdminRoleRepository.selectObjs(adminRoleLqw);
        if (roleIds.isEmpty()) {
            return apiList;
        }
        LambdaQueryWrapper<BusinessRoleMenuPO> roleMenuLqw = new LambdaQueryWrapper<>();
        roleMenuLqw.select(BusinessRoleMenuPO::getMenuId);
        roleMenuLqw.in(BusinessRoleMenuPO::getRoleId, roleIds);
        List<Object> menuIds = siteRoleMenuRepository.selectObjs(roleMenuLqw);
        if (menuIds.isEmpty()) {
            return apiList;
        }
        LambdaQueryWrapper<BusinessMenuPO> menuLqw = new LambdaQueryWrapper<>();
        menuLqw.select(BusinessMenuPO::getApiUrl);
        menuLqw.in(BusinessMenuPO::getId, menuIds);

        List<Object> apiUrls = siteMenuRepository.selectObjs(menuLqw);

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

        List<Object> roleIds = siteAdminRoleRepository.selectObjs(adminRoleLqw);
        if (roleIds.isEmpty()) {
            return urlList;
        }
        LambdaQueryWrapper<BusinessRoleMenuPO> roleMenuLqw = new LambdaQueryWrapper<>();
        roleMenuLqw.select(BusinessRoleMenuPO::getMenuId);
        roleMenuLqw.in(BusinessRoleMenuPO::getRoleId, roleIds);
        List<Object> menuIds = siteRoleMenuRepository.selectObjs(roleMenuLqw);
        if (menuIds.isEmpty()) {
            return urlList;
        }
        LambdaQueryWrapper<BusinessMenuPO> menuLqw = new LambdaQueryWrapper<>();
        menuLqw.select(BusinessMenuPO::getUrl);
        menuLqw.in(BusinessMenuPO::getId, menuIds);

        List<Object> urls = siteMenuRepository.selectObjs(menuLqw);

        if (urls.isEmpty()) {
            return urlList;
        }
        Set<String> urlSet = new HashSet<>();
        for (Object o : urls) {
            urlSet.add(null == o ? "" : o.toString().trim());
        }
        return new ArrayList<>(urlSet);

    }

    public int lockAdmin(String userName, String siteCode) {
        LambdaUpdateWrapper<BusinessAdminPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BusinessAdminPO::getUserName, userName);
        updateWrapper.eq(BusinessAdminPO::getSiteCode, siteCode);
        updateWrapper.set(BusinessAdminPO::getLockStatus, AdminLockStatusEnum.LOCKED.getCode());
        return siteAdminRepository.update(null, updateWrapper);

    }

    public SiteAdminVO getSiteAdminById(String id) {
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getId, id);
        BusinessAdminPO siteAdminPO = siteAdminRepository.selectOne(lqw);
        SiteAdminVO siteAdminVO = new SiteAdminVO();
        if (siteAdminPO != null) {
            BeanUtils.copyProperties(siteAdminPO, siteAdminVO);
        }
        return siteAdminVO;
    }

    public List<String> getUserIdsByUseName(String userName) {
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.like(BusinessAdminPO::getUserName, userName);
        List<BusinessAdminPO> siteAdminPOList = siteAdminRepository.selectList(lqw);
        List<String> adminIds = Lists.newArrayList();
        if (!siteAdminPOList.isEmpty()) {
            adminIds = siteAdminPOList.stream().map(BusinessAdminPO::getId).collect(Collectors.toList());
        }
        return adminIds;
    }

    public String getUserNameById(String id) {
        BusinessAdminPO siteAdminPO = siteAdminRepository.selectById(id);
        if (null != siteAdminPO) {
            return siteAdminPO.getUserName();
        }
        return "";
    }

    public List<SiteAdminVO> getUserByIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<BusinessAdminPO> list = new LambdaQueryChainWrapper<>(baseMapper).in(BusinessAdminPO::getId, ids).list();
        if (CollectionUtil.isNotEmpty(list)) {
            return ConvertUtil.entityListToModelList(list, SiteAdminVO.class);
        }
        return Collections.emptyList();
    }

    public List<String> getRoleIdsByUseName(String adminId) {

        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.eq(BusinessAdminRolePO::getAdminId, adminId);
        List<BusinessAdminRolePO> siteAdminPOList = siteAdminRoleRepository.selectList(adminRoleLqw);
        List<String> roleIds = Lists.newArrayList();
        if (!siteAdminPOList.isEmpty()) {
            roleIds = siteAdminPOList.stream().map(BusinessAdminRolePO::getRoleId).collect(Collectors.toList());
        }
        return roleIds;
    }

    public Integer accountSet(AccountSetParamVO accountSetParamVO) {

        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getUserName, accountSetParamVO.getUserName());
        BusinessAdminPO siteAdminPO = siteAdminRepository.selectOne(lqw);

        siteAdminPO.setPassword(accountSetParamVO.getNewPassword());
        return siteAdminRepository.updateById(siteAdminPO);
    }


    public Map<String, String> id2NameByIds(Set<String> ids) {
        List<BusinessAdminPO> siteAdminPOS = siteAdminRepository.selectBatchIds(ids);
        if (CollectionUtil.isEmpty(siteAdminPOS)) {
            return Maps.newHashMap();
        }
        return siteAdminPOS.stream().collect(Collectors.toMap(BusinessAdminPO::getId, BusinessAdminPO::getUserName, (o, n) -> n));
    }

    public boolean update(AdminUpdateVO adminUpdateVO) {
        LambdaUpdateWrapper<BusinessAdminPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(BusinessAdminPO::getSiteCode, adminUpdateVO.getSiteCode())
                .eq(BusinessAdminPO::getUserName, adminUpdateVO.getUserName())
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

    public int editPassword(AdminPasswordEditVO editVO) {
        BusinessAdminPO po = new BusinessAdminPO();
        po.setId(editVO.getId());
        po.setPassword(editVO.getNewPassword());
        po.setUpdater(editVO.getUserName());
        po.setUpdatedTime(System.currentTimeMillis());
        return siteAdminRepository.updateById(po);
    }

}
