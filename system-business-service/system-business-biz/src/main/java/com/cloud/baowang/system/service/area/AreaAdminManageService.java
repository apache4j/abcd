package com.cloud.baowang.system.service.area;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.area.*;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.language.LanguageValidListCacheVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteManageVO;
import com.cloud.baowang.system.po.area.AreaAdminManagePO;
import com.cloud.baowang.system.po.area.AreaCountryNamePO;
import com.cloud.baowang.system.po.lang.LanguageManagerPO;
import com.cloud.baowang.system.po.site.area.AreaSiteManagePO;
import com.cloud.baowang.system.repositories.area.AreaAdminManageRepository;
import com.cloud.baowang.system.repositories.area.AreaCountryNameRepository;
import com.cloud.baowang.system.repositories.site.AreaSiteManageRepository;
import com.cloud.baowang.system.service.language.LanguageManagerService;
import com.cloud.baowang.system.service.site.area.AreaSiteManageService;
import com.cloud.baowang.system.api.file.MinioFileService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AreaAdminManageService extends ServiceImpl<AreaAdminManageRepository, AreaAdminManagePO> {
    private final MinioFileService fileService;
    private final AreaCountryNameService areaCountryNameService;
    private final AreaCountryNameRepository areaCountryNameRepository;
    private final LanguageManagerService languageManagerService;
    private final AreaAdminManageRepository areaAdminManageRepository;
    private final AreaSiteManageService areaSiteManageService;
    private final AreaSiteManageRepository areaSiteManageRepository;
    private final SiteApi siteApi;

    public ResponseVO<Page<AreaAdminManageVO>> pageList(AreaCodeManageReqVO vo) {

        Page<AreaAdminManagePO> page = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(StrUtil.isNotBlank(vo.getCountryName()), AreaAdminManagePO::getCountryName, vo.getCountryName())
                .eq(StrUtil.isNotBlank(vo.getAreaCode()), AreaAdminManagePO::getAreaCode, vo.getAreaCode())
                .eq(ObjUtil.isNotEmpty(vo.getStatus()), AreaAdminManagePO::getStatus, vo.getStatus())
                .orderByDesc(AreaAdminManagePO::getCreatedTime)
                .page(new Page<>(vo.getPageNumber(), vo.getPageSize()));
        List<AreaAdminManagePO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            Page<AreaAdminManageVO> pageResult = new Page<>();
            BeanUtil.copyProperties(page, pageResult);
            return ResponseVO.success(pageResult);
        }
        List<AreaAdminManageVO> result = Lists.newArrayList();
        List<AreaCountryNamePO> nameList = areaCountryNameRepository.selectList(new LambdaQueryWrapper<>());

        Map<String, List<AreaCountryNamePO>> nameMap = nameList.stream()
                .collect(Collectors.groupingBy(AreaCountryNamePO::getAreaCode));
        String minioDomain = fileService.getMinioDomain();

        List<LanguageManagerListVO> languageList = languageManagerService.languageByList(CurrReqUtils.getSiteCode()).getData();


        records.forEach(po -> {
            AreaAdminManageVO areaAdminManageVO = new AreaAdminManageVO();
            BeanUtil.copyProperties(po, areaAdminManageVO);
            //这里会导致编辑的时候，在没有修改图片的情况下，前端没办法传入这个icon的相对路径
            //areaAdminManageVO.setIcon(minioFileService.getFileUrlByKey(po.getIcon()));

            areaAdminManageVO.setIconImage(minioDomain+"/"+areaAdminManageVO.getIcon());
            List<AreaCountryNamePO> countryNameList = nameMap.get(po.getAreaCode());
            List<AreaNameVO> areaNameVOS = ConvertUtil.entityListToModelList(countryNameList, AreaNameVO.class);
            Set<String> areaLanguages = areaNameVOS.stream()
                    .map(AreaNameVO::getLanguage)
                    .collect(Collectors.toSet());

            List<AreaNameVO> extraAreas = languageList.stream()
                    .filter(language -> !areaLanguages.contains(language.getCode()))
                    .map(item -> {
                        AreaNameVO areaNameVO = new AreaNameVO();
                        areaNameVO.setLanguage(item.getCode());
                        return areaNameVO;
                    }).collect(Collectors.toList());

            areaNameVOS.addAll(extraAreas);
            areaAdminManageVO.setNameList(areaNameVOS);
            result.add(areaAdminManageVO);
        });

        Page<AreaAdminManageVO> pageResult = new Page<>();
        BeanUtil.copyProperties(page, pageResult);
        pageResult.setRecords(result);
        return ResponseVO.success(pageResult);
    }

    public AreaCodeManageInfoVO getInfo(IdVO idVO) {
        AreaCodeManageInfoVO infoVO = new AreaCodeManageInfoVO();
        AreaAdminManagePO po = getById(idVO.getId());
        if (ObjUtil.isEmpty(po)) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        BeanUtils.copyProperties(po, infoVO);
        infoVO.setIconImage(fileService.getMinioDomain()+"/"+po.getIcon() );
        LambdaQueryWrapper<AreaCountryNamePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AreaCountryNamePO::getAreaCode, po.getAreaCode());
        List<AreaCountryNamePO> nameList = areaCountryNameRepository.selectList(lqw);
        ResponseVO<List<LanguageManagerListVO>> responseVO = languageManagerService.languageByList(CommonConstant.ADMIN_CENTER_SITE_CODE);
        List<LanguageManagerListVO> languageList = responseVO.getData();
        if (ObjectUtil.isNotEmpty(nameList)) {
            Map<String, LanguageManagerListVO> cacheVOMap = languageList.stream().collect(Collectors.toMap(LanguageManagerListVO::getCode, p -> p, (k1, k2) -> k2));
            List<AreaNameVO> areaNameVOS = Lists.newArrayList();
            Map<String, AreaCountryNamePO> areaVOMap= nameList.stream().collect(Collectors.toMap(AreaCountryNamePO::getLanguage, p -> p, (k1, k2) -> k2));
            for (LanguageManagerListVO language : languageList) {
                AreaCountryNamePO countryNamePO = areaVOMap.get(language.getCode());
                AreaNameVO areaNameVO = new AreaNameVO();
                if (countryNamePO != null) {
                    areaNameVO.setLanguageName(countryNamePO.getCountryName());
                    areaNameVO.setCode(countryNamePO.getLanguage());
                    areaNameVO.setShowCode(countryNamePO.getCountryCode());
                    areaNameVO.setCountryName(countryNamePO.getCountryName());
                    areaNameVO.setLanguage(language.getCode());
                }else {
                    areaNameVO.setLanguage(language.getCode());
                    areaNameVO.setLanguageName(language.getName());
                    areaNameVO.setCode(language.getCode());
                    areaNameVO.setShowCode(language.getShowCode());
                }

                areaNameVOS.add(areaNameVO);
            }
            infoVO.setNameList(areaNameVOS);
        } else {
            List<AreaNameVO> areaNameVOS = new ArrayList<>();
            for (LanguageManagerListVO languageVO : languageList) {
                AreaNameVO areaNameVO = new AreaNameVO();
                areaNameVO.setLanguageName(languageVO.getName());
                areaNameVO.setShowCode(languageVO.getShowCode());
                areaNameVO.setCode(languageVO.getCode());
                areaNameVOS.add(areaNameVO);
            }
            infoVO.setNameList(areaNameVOS);
        }

        return infoVO;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> edit(AreaCodeManageEditReqVO vo) {

        AreaAdminManagePO po = getById(vo.getId());
        if (ObjUtil.isEmpty(po)) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        //取出中文国家名称
        AreaNameVO znName = vo.getNameList().stream().filter(p -> p.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).findFirst().get();

        areaCountryNameRepository.deleteByCode(po.getAreaCode());
        List<AreaCountryNamePO> poList = new ArrayList<>();
        for (AreaNameVO areaNameVO : vo.getNameList()) {
            AreaCountryNamePO countryNamePO = new AreaCountryNamePO();
            countryNamePO.setCountryName(areaNameVO.getCountryName());
            countryNamePO.setLanguage(areaNameVO.getLanguage());
            countryNamePO.setCountryCode(areaNameVO.getShowCode());
            countryNamePO.setAreaCode(po.getAreaCode());
            countryNamePO.setUpdatedTime(System.currentTimeMillis());
            countryNamePO.setUpdater(vo.getUpdater());
            poList.add(countryNamePO);
        }
        areaCountryNameService.saveBatch(poList);

        //更新图标
        AreaAdminManagePO managePO = new AreaAdminManagePO();
        managePO.setId(po.getId());
        managePO.setCountryName(znName.getCountryName());
        managePO.setCountryCode(vo.getCountryCode());
        managePO.setIcon(vo.getIcon());
        managePO.setMaxLength(vo.getMaxLength());
        managePO.setMinLength(vo.getMinLength());
        this.updateById(managePO);

        //更新站点数据
        AreaSiteManageVO siteManageVO = new AreaSiteManageVO();
        BeanUtils.copyProperties(managePO, siteManageVO);
        siteManageVO.setAreaCode(po.getAreaCode());
        areaSiteManageService.updateByAreaCode(siteManageVO);

        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean statusChange(AreaStatusVO vo) {
        AreaAdminManagePO po = getById(vo.getId());
        if (ObjUtil.isEmpty(po)) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        AreaAdminManagePO updatePO = new AreaAdminManagePO();
        updatePO.setId(po.getId());
        updatePO.setStatus(vo.getStatus());
        updatePO.setUpdater(vo.getUpdater());
        updatePO.setUpdatedTime(System.currentTimeMillis());
        boolean isSuc = this.updateById(updatePO);

        if (EnableStatusEnum.DISABLE.getCode().equals(vo.getStatus())) {
            //总控禁用，站点也同时禁用
            LambdaUpdateWrapper<AreaSiteManagePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AreaSiteManagePO::getAreaCode, po.getAreaCode())
                    .set(AreaSiteManagePO::getStatus, String.valueOf(EnableStatusEnum.DISABLE.getCode()))
                    .set(AreaSiteManagePO::getUpdatedTime, System.currentTimeMillis())
                    .set(AreaSiteManagePO::getUpdater, "system");

            areaSiteManageRepository.update(null, updateWrapper);
        } else {
            //启用的时候如果站点没有同步区号数据则同步过去
            ResponseVO<List<SiteVO>> siteList = siteApi.allSiteInfo();
            for (SiteVO siteVO : siteList.getData()) {
                LambdaQueryWrapper<AreaSiteManagePO> lqw = new LambdaQueryWrapper<>();
                lqw.eq(AreaSiteManagePO::getAreaCode, po.getAreaCode());
                lqw.eq(AreaSiteManagePO::getSiteCode, siteVO.getSiteCode());
                AreaSiteManagePO siteManagePO = areaSiteManageRepository.selectOne(lqw);
                if (siteManagePO == null) {
                    siteManagePO = new AreaSiteManagePO();
                    String id = siteManagePO.getId();
                    BeanUtils.copyProperties(po, siteManagePO);
                    siteManagePO.setId(id);
                    siteManagePO.setSiteCode(siteVO.getSiteCode());
                    siteManagePO.setStatus(EnableStatusEnum.ENABLE.getCode());
                    areaSiteManageRepository.insert(siteManagePO);
                }
            }
        }

        return isSuc;
    }

    public AreaSiteLangVO getAreaInfo(String areaCode) {
        LambdaQueryWrapper<AreaAdminManagePO> lqw = new LambdaQueryWrapper<AreaAdminManagePO>();
        lqw.eq(AreaAdminManagePO::getAreaCode, areaCode);
        AreaAdminManagePO po = areaAdminManageRepository.selectOne(lqw);
        return ConvertUtil.entityToModel(po, AreaSiteLangVO.class);
    }
}
