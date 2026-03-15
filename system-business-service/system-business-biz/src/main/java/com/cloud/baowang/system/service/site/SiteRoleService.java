package com.cloud.baowang.system.service.site;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.system.api.enums.BusinessSystemEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.member.RoleMenuUrlVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRoleAddVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRoleDetailVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRolePageVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRoleQueryVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRoleUpdateVO;
import com.cloud.baowang.system.po.member.BusinessAdminPO;
import com.cloud.baowang.system.po.member.BusinessAdminRolePO;
import com.cloud.baowang.system.po.member.BusinessMenuPO;
import com.cloud.baowang.system.po.member.BusinessRoleMenuPO;
import com.cloud.baowang.system.po.member.BusinessRolePO;
import com.cloud.baowang.system.repositories.site.SiteAdminRoleRepository;
import com.cloud.baowang.system.repositories.site.SiteMenuRepository;
import com.cloud.baowang.system.repositories.site.SiteRoleMenuRepository;
import com.cloud.baowang.system.repositories.site.SiteRoleRepository;
import com.cloud.baowang.system.service.member.BusinessAdminService;
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
public class SiteRoleService {

    private final SiteRoleRepository siteRoleRepository;

    private final SiteRoleMenuRepository siteRoleMenuRepository;

    private final SiteAdminRoleRepository siteAdminRoleRepository;

    private final SiteAdminService siteAdminService;

    private final SiteMenuRepository siteMenuRepository;

    public SiteRoleService(SiteRoleRepository siteRoleRepository, SiteRoleMenuRepository siteRoleMenuRepository, SiteAdminRoleRepository siteAdminRoleRepository, SiteAdminService siteAdminService, SiteMenuRepository siteMenuRepository, BusinessAdminService businessAdminService) {
        this.siteRoleRepository = siteRoleRepository;
        this.siteRoleMenuRepository = siteRoleMenuRepository;
        this.siteAdminRoleRepository = siteAdminRoleRepository;
        this.siteAdminService = siteAdminService;
        this.siteMenuRepository = siteMenuRepository;
    }

    public Page<SiteRolePageVO> listRole(SiteRoleQueryVO siteRoleQueryVO) {
        Page<BusinessRolePO> page = new Page<>(siteRoleQueryVO.getPageNumber(), siteRoleQueryVO.getPageSize());
        LambdaQueryWrapper<BusinessRolePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessRolePO::getBusinessSystem,BusinessSystemEnum.SITE.getCode());
        lqw.eq(BusinessRolePO::getSiteCode,siteRoleQueryVO.getSiteCode());
        lqw.eq(null != siteRoleQueryVO.getStatus(),BusinessRolePO::getStatus,siteRoleQueryVO.getStatus());
        if (!siteRoleQueryVO.getIsSuperAdmin()) {
            lqw.eq(BusinessRolePO::getCreator, siteRoleQueryVO.getCurrentAdminId());
        }
        lqw.orderByDesc(BusinessRolePO::getStatus);
        lqw.orderByDesc(BusinessRolePO::getCreatedTime);
        lqw.like(StringUtils.isNotBlank(siteRoleQueryVO.getName()), BusinessRolePO::getName, siteRoleQueryVO.getName());
        Page<BusinessRolePO> siteRolePOPage = siteRoleRepository.selectPage(page, lqw);
        Page<SiteRolePageVO> siteRolePageVOPage = new Page<>();
        BeanUtils.copyProperties(siteRolePOPage, siteRolePageVOPage);
        List<SiteRolePageVO> list = siteRolePOPage.getRecords().stream().map(record -> {
            SiteRolePageVO siteRolePageVO = new SiteRolePageVO();
            BeanUtils.copyProperties(record, siteRolePageVO);
            return siteRolePageVO;
        }).toList();
        siteRolePageVOPage.setRecords(list);
        return siteRolePageVOPage;
    }


    @Transactional(rollbackFor = Exception.class)
    public String addRole(SiteRoleAddVO siteRoleAddVO) {
        BusinessRolePO siteRolePO = new BusinessRolePO();
        BeanUtils.copyProperties(siteRoleAddVO, siteRolePO);
        Long currentTimeMillis = System.currentTimeMillis();
        siteRolePO.setCreatedTime(currentTimeMillis);
        siteRolePO.setUpdatedTime(currentTimeMillis);
        siteRolePO.setBusinessSystem(BusinessSystemEnum.SITE.getCode());
        siteRolePO.setStatus(CommonConstant.business_one);
        siteRoleRepository.insert(siteRolePO);
        addRoleMenu(siteRoleAddVO.getMenuIds(), siteRolePO.getId());
        setLocalCacheMap(siteRoleAddVO.getMenuIds(),siteRolePO.getId());
        return siteRolePO.getId();
    }
    private void setLocalCacheMap(String[] menuIds,String roleId){
        LambdaQueryWrapper<BusinessMenuPO> lqw = new LambdaQueryWrapper<>();
        lqw.select(BusinessMenuPO::getApiUrl);
        lqw.in(BusinessMenuPO::getId,menuIds);
        lqw.isNotNull(BusinessMenuPO::getApiUrl);
        lqw.ne(BusinessMenuPO::getApiUrl,"");
        List<Object> menuUrlList = siteMenuRepository.selectObjs(lqw);
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
            siteRoleMenuRepository.batchRoleMenu(list);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<String> updateRole(SiteRoleUpdateVO siteRoleUpdateVO) {

        BusinessRolePO businessRolePOold = siteRoleRepository.selectById(siteRoleUpdateVO.getId());
        if(!CommonConstant.business_zero.equals(businessRolePOold.getStatus())){
            throw new BaowangDefaultException(ResultCode.BUSINESS_ROLE_EDIT_ERROR);
        }
        handleDeleteIds(siteRoleUpdateVO.getId(),siteRoleUpdateVO.getMenuIds(),siteRoleUpdateVO.getUpdater(),businessRolePOold.getSiteCode());
        LambdaQueryWrapper<BusinessRoleMenuPO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessRoleMenuPO::getRoleId, siteRoleUpdateVO.getId());
        siteRoleMenuRepository.delete(lqw);
        addRoleMenu(siteRoleUpdateVO.getMenuIds(), siteRoleUpdateVO.getId());
        BusinessRolePO siteRolePO = new BusinessRolePO();
        BeanUtils.copyProperties(siteRoleUpdateVO, siteRolePO);
        siteRolePO.setUpdatedTime(System.currentTimeMillis());
        this.siteRoleRepository.updateById(siteRolePO);
        setLocalCacheMap(siteRoleUpdateVO.getMenuIds(), siteRoleUpdateVO.getId());
        return ResponseVO.success(siteRoleUpdateVO.getId());
    }

    /**
     * 对比是否有删除授权菜单
     * @param
     * @return
     */
    private void handleDeleteIds (String roleId,String[] updateMenuIds,String currentAccount,String siteCode){
        LambdaQueryWrapper<BusinessRoleMenuPO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessRoleMenuPO::getRoleId, roleId);
        lqw.select(BusinessRoleMenuPO::getMenuId);
        List<Object> oldList = siteRoleMenuRepository.selectObjs(lqw);
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
                getChildRoleIds(roleIdList,currentAccount,siteCode);
                System.out.println("角色列表："+roleIdList);
                for (Object roleIdObj:roleIdList){
                    LambdaQueryWrapper<BusinessRoleMenuPO> deletelqw = new LambdaQueryWrapper();
                    deletelqw.eq(BusinessRoleMenuPO::getRoleId, roleIdObj.toString());
                    deletelqw.in(BusinessRoleMenuPO::getMenuId,result);
                    siteRoleMenuRepository.delete(deletelqw);
                    LambdaQueryWrapper<BusinessRoleMenuPO> queryLqw = new LambdaQueryWrapper();
                    queryLqw.eq(BusinessRoleMenuPO::getRoleId, roleIdObj.toString());
                    List<BusinessRoleMenuPO> businessRoleMenuPOS = siteRoleMenuRepository.selectList(queryLqw);
                    String[] roleMenuStrIds = businessRoleMenuPOS.stream().map(BusinessRoleMenuPO::getMenuId).toArray(String[]::new);
                    setLocalCacheMap(roleMenuStrIds, roleIdObj.toString());
                }
            }


        }
    }
    private void getChildRoleIds(List<Object> roleIdList,String userName,String siteCode){
        LambdaQueryWrapper<BusinessAdminPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessAdminPO::getBusinessSystem,BusinessSystemEnum.SITE.getCode());
        lqw.eq(BusinessAdminPO::getSiteCode,siteCode);
        lqw.eq(BusinessAdminPO::getCreator,userName);
        lqw.ne(BusinessAdminPO::getUserName,userName);
        lqw.select(BusinessAdminPO::getUserName);
        List<Object> accounts = siteAdminService.listObjs(lqw);
        if(!accounts.isEmpty()){
            for (Object o:accounts){
                LambdaQueryWrapper<BusinessRolePO> roleLqw = new LambdaQueryWrapper<>();
                roleLqw.eq(BusinessRolePO::getBusinessSystem,BusinessSystemEnum.SITE.getCode());
                roleLqw.in(BusinessRolePO::getCreator,o);
                roleLqw.eq(BusinessRolePO::getSiteCode,siteCode);
                roleLqw.select(BusinessRolePO::getId);
                List<Object> roleIds = siteRoleRepository.selectObjs(roleLqw);
                roleIdList.addAll(roleIds);
                getChildRoleIds( roleIdList,o.toString(),siteCode);
            }
        }

    }


    public int deleteRole(IdVO idVO) {
        BusinessRolePO businessRolePOold = siteRoleRepository.selectById(idVO.getId());
        if(!CommonConstant.business_zero.equals(businessRolePOold.getStatus())){
            throw new BaowangDefaultException(ResultCode.BUSINESS_ROLE_DELETE_ERROR);
        }
        RedisUtil.deleteLocalCachedMap(CacheConstants.KEY_ADMIN_AUTH_INFO_KEY, idVO.getId());
        LambdaQueryWrapper<BusinessAdminRolePO> adminRoleLqw = new LambdaQueryWrapper<>();
        adminRoleLqw.eq(BusinessAdminRolePO::getRoleId, idVO.getId());
        List<BusinessAdminRolePO> siteAdminRolePOS = siteAdminRoleRepository.selectList(adminRoleLqw);
        if (!siteAdminRolePOS.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.ROLE_EXIST_USER);
        }
        LambdaQueryWrapper<BusinessRoleMenuPO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessRoleMenuPO::getRoleId, idVO.getId());
        siteRoleMenuRepository.delete(lqw);
        return siteRoleRepository.deleteById(idVO.getId());
    }

    public SiteRoleDetailVO getRoleById(IdVO idVO) {

        SiteRoleDetailVO siteRoleDetailVO = new SiteRoleDetailVO();
        BusinessRolePO siteRolePO = siteRoleRepository.selectById(idVO.getId());
        BeanUtils.copyProperties(siteRolePO, siteRoleDetailVO);
        LambdaQueryWrapper<BusinessRoleMenuPO> lqw = new LambdaQueryWrapper();
        lqw.eq(BusinessRoleMenuPO::getRoleId, idVO.getId());
        List<BusinessRoleMenuPO> siteRoleMenuPOS = siteRoleMenuRepository.selectList(lqw);
        String[] menuIds = siteRoleMenuPOS.stream().map(BusinessRoleMenuPO::getMenuId).toArray(String[]::new);
        siteRoleDetailVO.setMenuIds(menuIds);
        return siteRoleDetailVO;
    }

    public boolean checkRoleNameUnique(String name) {
        LambdaQueryWrapper<BusinessRolePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessRolePO::getBusinessSystem,BusinessSystemEnum.SITE.getCode());
        lqw.eq(BusinessRolePO::getSiteCode, CurrReqUtils.getSiteCode());
        lqw.eq(BusinessRolePO::getName, name);
        List<BusinessRolePO> siteRolePOList = siteRoleRepository.selectList(lqw);
        return siteRolePOList.isEmpty();
    }


    public List<SiteRoleDetailVO> listAllRole(SiteRoleQueryVO siteRoleQueryVO) {
        LambdaQueryWrapper<BusinessRolePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessRolePO::getBusinessSystem,BusinessSystemEnum.SITE.getCode());
        lqw.eq(BusinessRolePO::getStatus,CommonConstant.business_one);
        if (!siteRoleQueryVO.getIsSuperAdmin()) {
            lqw.eq(BusinessRolePO::getCreator, siteRoleQueryVO.getCurrentAdminId());

        }
        lqw.eq(BusinessRolePO::getSiteCode,siteRoleQueryVO.getSiteCode());
        List<BusinessRolePO> siteRolePOList = siteRoleRepository.selectList(lqw);
        List<SiteRoleDetailVO> list = siteRolePOList.stream().map(po -> {
            SiteRoleDetailVO vo = new SiteRoleDetailVO();
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
            List<BusinessRoleMenuPO> businessRoleMenuPOS = siteRoleMenuRepository.selectList(lqw);
            String[] menuIds = businessRoleMenuPOS.stream().map(BusinessRoleMenuPO::getMenuId).toArray(String[]::new);
            setLocalCacheMap(menuIds,changeStatusVO.getId());
        }
        BusinessRolePO siteRolePO = new BusinessRolePO();
        siteRolePO.setStatus(Integer.parseInt(changeStatusVO.getAbleStatus()));
        siteRolePO.setId(changeStatusVO.getId());
        siteRolePO.setUpdatedTime(System.currentTimeMillis());
        siteRolePO.setUpdater(changeStatusVO.getUpdater());
        return siteRoleRepository.updateById(siteRolePO);
    }

    public Map<String,Map<String,String>> getAllRoleMenuUrls() {
        LambdaQueryWrapper<BusinessRolePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(BusinessRolePO::getBusinessSystem, BusinessSystemEnum.SITE.getCode());
        lqw.select(BusinessRolePO::getId);
        List<Object> roleIds = siteRoleRepository.selectObjs(lqw);
        Map<String,Map<String,String>> map = new HashMap<>();
        if(!roleIds.isEmpty()){
            List<RoleMenuUrlVO> roleMenuUrlVOS = siteRoleMenuRepository.selectRoleMenuUrls(roleIds);
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
        }
        return map;
    }
}
