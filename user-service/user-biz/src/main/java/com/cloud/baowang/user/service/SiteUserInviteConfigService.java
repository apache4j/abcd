package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SpringBeanUtil;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.vo.language.LanguageValidListCacheVO;
import com.cloud.baowang.user.api.vo.user.invite.InviteIconVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteConfigReqVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteConfigResponseVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteIconVO;
import com.cloud.baowang.user.po.SiteUserInviteConfigPO;
import com.cloud.baowang.user.po.SiteUserInviteIconPO;
import com.cloud.baowang.user.repositories.SiteUserInviteConfigRepository;
import com.cloud.baowang.user.repositories.SiteUserInviteIconRepository;
import com.cloud.baowang.user.util.MinioFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 23:46
 * @description:
 */
@Service
@AllArgsConstructor
@Slf4j
public class SiteUserInviteConfigService extends ServiceImpl<SiteUserInviteConfigRepository, SiteUserInviteConfigPO> {
    private final SiteUserInviteConfigRepository siteUserInviteConfigRepository;
    private final MinioFileService minioFileService;
    private final SiteUserInviteIconRepository siteUserInviteIconRepository;
    private final SiteUserInviteIconService siteUserInviteIconService;
    private final LanguageManagerApi languageManagerApi;


    public SiteUserInviteConfigResponseVO getInviteConfig(String siteCode) {
        ResponseVO<List<LanguageValidListCacheVO>> data = languageManagerApi.validList();
        if (!data.isOk()){
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        List<LanguageValidListCacheVO> languages = data.getData();
        LambdaQueryWrapper<SiteUserInviteConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteUserInviteConfigPO::getSiteCode, siteCode);
        SiteUserInviteConfigPO po = siteUserInviteConfigRepository.selectOne(queryWrapper);
        SiteUserInviteConfigResponseVO responseVO = new SiteUserInviteConfigResponseVO();
        if (po != null) {
            BeanUtils.copyProperties(po, responseVO);
            LambdaQueryWrapper<SiteUserInviteIconPO> iconWrapper = new LambdaQueryWrapper<>();
            iconWrapper.eq(SiteUserInviteIconPO::getConfigId, po.getId());
            List<SiteUserInviteIconPO> iconList = siteUserInviteIconRepository.selectList(iconWrapper);
            String minioDomain = minioFileService.getMinioDomain();
            List<SiteUserInviteIconVO> pcIconVOS = new ArrayList<>();
            List<SiteUserInviteIconVO> h5IconVOS = new ArrayList<>();
            for (LanguageValidListCacheVO language : languages) {
                boolean exists = false;
                for (SiteUserInviteIconPO iconPO : iconList) {
                    SiteUserInviteIconVO iconVO = new SiteUserInviteIconVO();
                    if (language.getCode().equals(iconPO.getLanguage())) {
                        BeanUtils.copyProperties(iconPO, iconVO);
                        String languageName = LanguageEnum.parseNameByCode(iconPO.getLanguage());
                        String showCode = LanguageEnum.getCodeByLang(iconPO.getLanguage());
                        iconVO.setLanguageName(languageName);
                        iconVO.setShowCode(showCode);
                        iconVO.setIconUrl(iconPO.getIconUrl());
                        iconVO.setMessageFileUrl(minioDomain + "/" + iconPO.getIconUrl());
                        if ("1".equals(iconPO.getDeviceType())) {
                            pcIconVOS.add(iconVO);
                        } else {
                            h5IconVOS.add(iconVO);
                        }
                        exists = true;
                    }
                }
                if (!exists) {
                    SiteUserInviteIconVO iconVO = new SiteUserInviteIconVO();
                    iconVO.setLanguage(language.getCode());
                    String languageName = LanguageEnum.parseNameByCode(language.getCode());
                    String showCode = LanguageEnum.getCodeByLang(language.getCode());
                    iconVO.setLanguageName(languageName);
                    iconVO.setShowCode(showCode);
                    pcIconVOS.add(iconVO);
                    h5IconVOS.add(iconVO);
                }
            }

            responseVO.setPcIconList(pcIconVOS);
            responseVO.setH5IconList(h5IconVOS);
            String languageName = LanguageEnum.parseNameByCode(responseVO.getLanguage());
            String showCode = LanguageEnum.getCodeByLang(responseVO.getLanguage());
            responseVO.setLanguageName(languageName);
            responseVO.setShowCode(showCode);
            responseVO.setCurrencyUnit(CurrReqUtils.getPlatCurrencyName());
        }

        return responseVO;
    }

    public SiteUserInviteConfigPO getInviteConfigBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteUserInviteConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteUserInviteConfigPO::getSiteCode, siteCode);
        SiteUserInviteConfigPO po = siteUserInviteConfigRepository.selectOne(queryWrapper);
        return po;
    }

    public ResponseVO userInviteConfig(SiteUserInviteConfigReqVO reqVO) {
        SiteUserInviteConfigPO po = new SiteUserInviteConfigPO();
        BeanUtils.copyProperties(reqVO, po);
        LambdaQueryWrapper<SiteUserInviteConfigPO> configWrapper = new LambdaQueryWrapper<>();
        configWrapper.eq(SiteUserInviteConfigPO::getSiteCode, reqVO.getSiteCode());
        SiteUserInviteConfigPO configPO = siteUserInviteConfigRepository.selectOne(configWrapper);
//        if (po.getId() == null && configPO != null) {
//            po.setId(configPO.getId());
//        }
        if (configPO == null) {
            po.setCreatedTime(System.currentTimeMillis());
            po.setCreator(CurrReqUtils.getAccount());
            siteUserInviteConfigRepository.insert(po);
        } else {
            po.setId(configPO.getId());
            po.setUpdatedTime(System.currentTimeMillis());
            po.setUpdater(CurrReqUtils.getAccount());
            siteUserInviteConfigRepository.updateById(po);
            onInviteConfigChanged(po.getSiteCode());
            LambdaQueryWrapper<SiteUserInviteIconPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SiteUserInviteIconPO::getConfigId, po.getId());
            siteUserInviteIconRepository.delete(queryWrapper);
        }

        List<SiteUserInviteIconPO> pcIconPOS = new ArrayList<>();
        for (InviteIconVO iconVO : reqVO.getPcIconList()) {
            SiteUserInviteIconPO iconPO = new SiteUserInviteIconPO();
            iconPO.setIconUrl(iconVO.getIconUrl());
            iconPO.setDeviceType("1");
            iconPO.setLanguage(iconVO.getLanguage());
            iconPO.setConfigId(po.getId());
            pcIconPOS.add(iconPO);
        }
        siteUserInviteIconService.saveBatch(pcIconPOS);

        List<SiteUserInviteIconPO> h5IconPOS = new ArrayList<>();
        for (InviteIconVO iconVO : reqVO.getH5IconList()) {
            SiteUserInviteIconPO iconPO = new SiteUserInviteIconPO();
            iconPO.setIconUrl(iconVO.getIconUrl());
            iconPO.setDeviceType("2");
            iconPO.setLanguage(iconVO.getLanguage());
            iconPO.setConfigId(po.getId());
            h5IconPOS.add(iconPO);
        }
        siteUserInviteIconService.saveBatch(h5IconPOS);

        return ResponseVO.success();
    }

    /**
     * 同步记录表 - 有效邀请数据
     * @param siteCode
     */
    @Async
    public void onInviteConfigChanged(String siteCode) {
        SiteUserInviteRecordService recordService = SpringUtils.getBean(SiteUserInviteRecordService.class);
        recordService.validInviteRecoup(siteCode,false);
    }

    public String getIconUrlByLanguage(String siteCode, String language, String deviceType) {
        LambdaQueryWrapper<SiteUserInviteConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteUserInviteConfigPO::getSiteCode, siteCode);
        SiteUserInviteConfigPO po = siteUserInviteConfigRepository.selectOne(queryWrapper);
        if(po==null){
            return null;
        }else {
            return siteUserInviteIconService.getIconUrl(po.getId(), language, deviceType);
        }
    }
}
