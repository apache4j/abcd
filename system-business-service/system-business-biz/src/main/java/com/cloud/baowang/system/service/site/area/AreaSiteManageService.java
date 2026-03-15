package com.cloud.baowang.system.service.site.area;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.area.AreaCodeManageReqVO;
import com.cloud.baowang.system.api.vo.area.AreaNameVO;
import com.cloud.baowang.system.api.vo.area.AreaStatusVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteManageVO;
import com.cloud.baowang.system.po.area.AreaAdminManagePO;
import com.cloud.baowang.system.po.area.AreaCountryNamePO;
import com.cloud.baowang.system.po.site.area.AreaSiteManagePO;
import com.cloud.baowang.system.repositories.area.AreaAdminManageRepository;
import com.cloud.baowang.system.repositories.area.AreaCountryNameRepository;
import com.cloud.baowang.system.repositories.site.AreaSiteManageRepository;
import com.cloud.baowang.system.api.file.MinioFileService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AreaSiteManageService extends ServiceImpl<AreaSiteManageRepository, AreaSiteManagePO> {
    private final MinioFileService minioFileService;
    private final AreaSiteManageRepository areaSiteManageRepository;
    private final AreaCountryNameRepository areaCountryNameRepository;
    private final AreaAdminManageRepository areaAdminManageRepository;

    public ResponseVO<Page<AreaSiteManageVO>> pageList(AreaCodeManageReqVO vo) {

        Page<AreaSiteManagePO> page = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(StrUtil.isNotBlank(vo.getCountryName()), AreaSiteManagePO::getCountryName, vo.getCountryName())
                .eq(StrUtil.isNotBlank(vo.getAreaCode()), AreaSiteManagePO::getAreaCode, vo.getAreaCode())
                .eq(ObjUtil.isNotEmpty(vo.getStatus()), AreaSiteManagePO::getStatus, vo.getStatus())
                .eq(AreaSiteManagePO::getSiteCode, vo.getSiteCode())
                .orderByDesc(AreaSiteManagePO::getCreatedTime)
                .page(new Page<>(vo.getPageNumber(), vo.getPageSize()));
        List<AreaSiteManagePO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            Page<AreaSiteManageVO> pageResult = new Page<>();
            BeanUtil.copyProperties(page, pageResult);
            return ResponseVO.success(pageResult);
        }
        List<AreaSiteManageVO> result = Lists.newArrayList();

        List<AreaCountryNamePO> nameList = areaCountryNameRepository.selectList(new LambdaQueryWrapper<>());
        Map<String, List<AreaCountryNamePO>> nameMap = nameList.stream()
                .collect(Collectors.groupingBy(AreaCountryNamePO::getAreaCode));
        String minioDomain = minioFileService.getMinioDomain();
        records.forEach(areaLimitManagerPO -> {
            AreaSiteManageVO areaSiteManageVO = new AreaSiteManageVO();
            BeanUtil.copyProperties(areaLimitManagerPO, areaSiteManageVO);
            String icon = areaSiteManageVO.getIcon();
            areaSiteManageVO.setIconImage(minioDomain+"/"+icon);
            List<AreaCountryNamePO> countryNameList = nameMap.get(areaLimitManagerPO.getAreaCode());
            if (ObjectUtil.isNotEmpty(countryNameList)) {
                List<AreaNameVO> areaNameVOS = ConvertUtil.entityListToModelList(countryNameList, AreaNameVO.class);
                areaSiteManageVO.setNameList(areaNameVOS);
            }

            result.add(areaSiteManageVO);
        });

        Page<AreaSiteManageVO> pageResult = new Page<>();
        BeanUtil.copyProperties(page, pageResult);
        pageResult.setRecords(result);
        return ResponseVO.success(pageResult);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean statusChange(AreaStatusVO vo) {
        AreaSiteManagePO po = getById(vo.getId());
        if (ObjUtil.isEmpty(po)) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        //如果总控禁用了，站点不能启用
        if (EnableStatusEnum.ENABLE.getCode().equals(vo.getStatus())) {
            LambdaQueryWrapper<AreaAdminManagePO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(AreaAdminManagePO::getAreaCode, po.getAreaCode());
            AreaAdminManagePO channelConfigPO = areaAdminManageRepository.selectOne(queryWrapper);
            if (EnableStatusEnum.DISABLE.getCode().equals(channelConfigPO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.AREA_CODE_NOT_USE);
            }
        }

        AreaSiteManagePO updatePO = new AreaSiteManagePO();
        updatePO.setId(po.getId());
        updatePO.setStatus(vo.getStatus());
        updatePO.setUpdater(vo.getUpdater());
        updatePO.setUpdatedTime(System.currentTimeMillis());
        return this.updateById(updatePO);
    }

    public List<AreaSiteLangVO> getValidList(String siteCode, String lang) {
        //先查出总控的配置，总控禁用的过滤
        List<AreaAdminManagePO> adminManagePOS = areaAdminManageRepository.selectList(new LambdaQueryWrapper<AreaAdminManagePO>());
        Map<String, AreaAdminManagePO> adminCodeMap = adminManagePOS.stream().collect(Collectors.toMap(AreaAdminManagePO::getAreaCode, p -> p, (k1, k2) -> k2));
        String minioDomain = minioFileService.getMinioDomain();
        List<AreaSiteLangVO> list =
                areaSiteManageRepository.selectByLanguage(siteCode, CommonConstant.business_one, lang);

        List<AreaSiteLangVO> result = list.stream()
                .filter(p -> adminCodeMap.get(p.getAreaCode()) != null && adminCodeMap.get(p.getAreaCode()).getStatus() == CommonConstant.business_one)
                .peek(v -> v.setIcon(minioDomain+"/"+v.getIcon()))
                .toList();


        String ip=CurrReqUtils.getReqIp();
//        IPResponse response = IpAddressUtils.queryIpRegion(ip);
        IPRespVO response = IpAPICoUtils.getIp(ip);
        result = result.stream().sorted(Comparator.comparing(AreaSiteLangVO::getCountryCode)).collect(Collectors.toList());
        if (ObjectUtils.isNotEmpty(response) && ObjectUtils.isNotEmpty(response.getCountryCode())){
            String finalCountry = response.getCountryCode();
            Optional<AreaSiteLangVO>  targetOpt = result.stream()
                    .filter(obj -> obj.getCountryCode().equals(finalCountry))
                    .findFirst();
            // 从列表中删除
            if (!targetOpt.isEmpty()){
                result.removeIf(obj -> obj.getCountryCode().equals(finalCountry));
                List<AreaSiteLangVO> data=new ArrayList<>();
                data.add(targetOpt.get());
                data.addAll(result);
                return data;
            }
        }
        return result;
    }

    public static List<AreaSiteLangVO> sortList(List<AreaSiteLangVO> list, String priorityCodename) {
        list.sort((a, b) -> {
            if (priorityCodename.equals(a.getCountryCode())) return -1;
            if (priorityCodename.equals(b.getCountryCode())) return 1;
            return a.getCountryCode().compareTo(b.getCountryCode());
        });
        return list;
    }

    public AreaSiteLangVO getAreaInfo(String areaCode, String siteCode) {
        LambdaQueryWrapper<AreaSiteManagePO> lqw = new LambdaQueryWrapper<AreaSiteManagePO>();
        lqw.eq(AreaSiteManagePO::getAreaCode, areaCode);
        lqw.eq(AreaSiteManagePO::getSiteCode, siteCode);
        AreaSiteManagePO po = areaSiteManageRepository.selectOne(lqw);
        return ConvertUtil.entityToModel(po, AreaSiteLangVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean initSiteArea(String siteCode) {
        if (this.count(Wrappers.lambdaQuery(AreaSiteManagePO.class).eq(AreaSiteManagePO::getSiteCode, siteCode)) > 0) {
            return true;
        }
        List<AreaAdminManagePO> areaAdminManagePOS = areaAdminManageRepository.selectList(new QueryWrapper<>());
        if (CollectionUtil.isNotEmpty(areaAdminManagePOS)) {
            List<AreaSiteManagePO> siteManagePOS = areaAdminManagePOS.stream()
                    .map(areaAdmin -> {
                        AreaSiteManagePO siteManage = new AreaSiteManagePO();
                        siteManage.setSiteCode(siteCode);
                        siteManage.setAreaId(areaAdmin.getAreaId());
                        siteManage.setAreaCode(areaAdmin.getAreaCode());
                        siteManage.setCountryName(areaAdmin.getCountryName());
                        siteManage.setCountryCode(areaAdmin.getCountryCode());
                        siteManage.setMaxLength(areaAdmin.getMaxLength());
                        siteManage.setMinLength(areaAdmin.getMinLength());
                        siteManage.setStatus(areaAdmin.getStatus());
                        siteManage.setIcon(areaAdmin.getIcon());
                        siteManage.setCreator(areaAdmin.getCreator());
                        siteManage.setCreatedTime(System.currentTimeMillis());
                        siteManage.setUpdater(areaAdmin.getCreator());
                        siteManage.setUpdatedTime(System.currentTimeMillis());
                        // 添加其他需要的字段
                        return siteManage; // 返回转换后的对象
                    })
                    .toList();
            this.saveBatch(siteManagePOS);
        }
        return true;
    }

    public void updateByAreaCode(AreaSiteManageVO vo) {
        LambdaUpdateWrapper<AreaSiteManagePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AreaSiteManagePO::getAreaCode, vo.getAreaCode())
                .set(ObjectUtil.isNotEmpty(vo.getCountryName()), AreaSiteManagePO::getCountryName, vo.getCountryName())
                .set(ObjectUtil.isNotEmpty(vo.getCountryCode()), AreaSiteManagePO::getCountryCode, vo.getCountryCode())
                .set(ObjectUtil.isNotEmpty(vo.getIcon()), AreaSiteManagePO::getIcon, vo.getIcon())
                .set(ObjectUtil.isNotEmpty(vo.getMaxLength()), AreaSiteManagePO::getMaxLength, vo.getMaxLength())
                .set(ObjectUtil.isNotEmpty(vo.getMinLength()), AreaSiteManagePO::getMinLength, vo.getMinLength());

        this.update(null, updateWrapper);
    }
}
