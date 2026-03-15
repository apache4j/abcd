package com.cloud.baowang.system.service.site.config;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.system.api.vo.PwaVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.site.agreement.i18nMessagesVO;
import com.cloud.baowang.system.po.site.SitePO;
import com.cloud.baowang.system.po.site.config.SiteDownloadConfigPO;
import com.cloud.baowang.system.repositories.SiteRepository;
import com.cloud.baowang.system.repositories.site.agreement.SiteDownloadConfigRepository;
import com.cloud.baowang.system.service.language.LanguageManagerService;
import com.cloud.baowang.system.api.file.MinioFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SiteDownloadConfigService extends ServiceImpl<SiteDownloadConfigRepository, SiteDownloadConfigPO> {

    private final SiteDownloadConfigRepository repository;
    private final I18nApi i18nApi;

    private final MinioFileService fileService;

    private final LanguageManagerService languageService;

    private final MinioUploadApi minioUploadApi;
    private final SiteRepository siteRepository;
    public HelpCenterManageService getBasicConfigService() {
        return SpringUtils.getBean(HelpCenterManageService.class);
    }

    public ResponseVO<Boolean> addDownloadInfo(i18nMessagesVO vo) {
        LambdaUpdateWrapper<SiteDownloadConfigPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteDownloadConfigPO::getSiteCode, CurrReqUtils.getSiteCode());
        repository.delete(updateWrapper);
        String jumpType = vo.getJumpType();
        String androidDownloadUrl = vo.getAndroidDownloadUrl();
        String iosDownloadUrl = vo.getIosDownloadUrl();
        String domainUrl = vo.getDomainUrl();
        List<List<I18nMsgFrontVO>> bannerInfo = vo.getI18nFileUrl();

        if (StringUtils.isBlank(jumpType) || bannerInfo == null || CollectionUtil.isEmpty(bannerInfo)) {
            throw new BaowangDefaultException(ResultCode.PARAM_MISSING);
        }
        if (CommonConstant.business_one_str.equals(jumpType)) {
            if (StringUtils.isBlank(androidDownloadUrl) || StringUtils.isBlank(iosDownloadUrl)) {
                throw new BaowangDefaultException(ResultCode.PARAM_MISSING);
            }
        } else if (CommonConstant.business_two_str.equals(jumpType)) {
            if (StringUtils.isBlank(domainUrl)) {
                throw new BaowangDefaultException(ResultCode.PARAM_MISSING);
            }
        }

        String siteCode = CurrReqUtils.getSiteCode();
        SiteDownloadConfigPO po = new SiteDownloadConfigPO();
        po.setSiteCode(siteCode);
        po.setJumpType(jumpType);
        po.setAndroidDownloadUrl(androidDownloadUrl);
        po.setIosDownloadUrl(iosDownloadUrl);
        po.setDomainUrl(domainUrl);
        po.setIcon(vo.getIcon());
        ArrayList<String> urlList = new ArrayList<>();
        for (List<I18nMsgFrontVO> fileUrl : bannerInfo) {
            String downloadUrl = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.IOS_ANDROID_DOWNLOAD_URL.getCode());
            i18nApi.insert(Map.of(downloadUrl, fileUrl));
            urlList.add(downloadUrl);
        }
        po.setBanner(String.join(CommonConstant.COMMA, urlList));
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdatedTime(System.currentTimeMillis());
        po.setCreator(CurrReqUtils.getAccount());
        po.setUpdater(CurrReqUtils.getAccount());
        boolean result = this.save(po);
        if (result) {
            initPwaFile(siteCode);
        }
        getBasicConfigService().updateBasicInfo(siteCode,CommonConstant.business_eleven);
        return ResponseVO.success(result);
    }

    public ResponseVO<i18nMessagesVO> getDownloadInfo() {
        i18nMessagesVO result = new i18nMessagesVO();
        String siteCode = CurrReqUtils.getSiteCode();
        LambdaQueryWrapper<SiteDownloadConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteDownloadConfigPO::getSiteCode, siteCode);
        SiteDownloadConfigPO downloadConfigPO = repository.selectOne(queryWrapper);
        if (downloadConfigPO == null) {
            return ResponseVO.fail(ResultCode.NO_HAVE_DATA);
        }
        result.setJumpType(downloadConfigPO.getJumpType());
        result.setIosDownloadUrl(downloadConfigPO.getIosDownloadUrl());
        result.setAndroidDownloadUrl(downloadConfigPO.getAndroidDownloadUrl());
        result.setIcon(downloadConfigPO.getIcon());
        result.setIconFileUrl(fileService.getMinioDomain() + "/" + downloadConfigPO.getIcon());
        result.setDomainUrl(downloadConfigPO.getDomainUrl());
        result.setDomainFullUrl(fileService.getMinioDomain() + "/" + downloadConfigPO.getDomainUrl());
        List<List<I18nMsgFrontVO>> urlResultList = new ArrayList<>();

        String i18nOption = downloadConfigPO.getBanner();
        if (StringUtils.isNotBlank(i18nOption)) {
            String[] urlList = i18nOption.split(CommonConstant.COMMA);
            String minioDomain = fileService.getMinioDomain();
            List<LanguageManagerListVO> languages = languageService.languageByList(siteCode).getData();
            List<String> languageCodes = languages.stream().map(LanguageManagerListVO::getCode).toList();
            for (String url : urlList) {
                List<I18NMessageDTO> data = i18nApi.getMessageByKey(url).getData();
                Set<String> i18LanguageCode = data.stream().map(I18NMessageDTO::getLanguage).collect(Collectors.toSet());
                if (CollectionUtil.isNotEmpty(data)) {
                    List<I18nMsgFrontVO> i18nUrlList = new ArrayList<>();
                    data.forEach(item -> {
                        if (languageCodes.contains(item.getLanguage())) {
                            I18nMsgFrontVO vo = new I18nMsgFrontVO();
                            vo.setLanguage(item.getLanguage());
                            vo.setMessage(item.getMessage());
                            String fullUrl = minioDomain + "/" + item.getMessage();
                            vo.setMessageFileUrl(fullUrl);
                            i18nUrlList.add(vo);
                        }
                    });
                    List<String> extraLanguage = languages.stream().map(LanguageManagerListVO::getCode)
                            .filter(language -> !i18LanguageCode.contains(language))
                            .toList();
                    if (!extraLanguage.isEmpty()) {
                        extraLanguage.forEach(language -> {
                            I18nMsgFrontVO vo = new I18nMsgFrontVO();
                            vo.setLanguage(language);
                            i18nUrlList.add(vo);
                        });
                    }
                    urlResultList.add(i18nUrlList);
                }
                result.setI18nFileUrl(urlResultList);
            }
        }
        return ResponseVO.success(result);
    }

    public ResponseVO<i18nMessagesVO> getDownloadInfoBySiteCode(String siteCode) {
        i18nMessagesVO result = new i18nMessagesVO();
        LambdaQueryWrapper<SiteDownloadConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteDownloadConfigPO::getSiteCode, siteCode);
        SiteDownloadConfigPO downloadConfigPO = repository.selectOne(queryWrapper);
        if (downloadConfigPO == null) {
            return ResponseVO.fail(ResultCode.NO_HAVE_DATA);
        }
        result.setJumpType(downloadConfigPO.getJumpType());
        result.setIosDownloadUrl(downloadConfigPO.getIosDownloadUrl());
        result.setAndroidDownloadUrl(downloadConfigPO.getAndroidDownloadUrl());
        result.setIcon(downloadConfigPO.getIcon());
        result.setIconFileUrl(fileService.getMinioDomain() + "/" + downloadConfigPO.getIcon());
        List<List<I18nMsgFrontVO>> urlResultList = new ArrayList<>();

        String i18nOption = downloadConfigPO.getBanner();
        if (StringUtils.isNotBlank(i18nOption)) {
            String[] urlList = i18nOption.split(CommonConstant.COMMA);
            String minioDomain = fileService.getMinioDomain();
            List<LanguageManagerListVO> languages = languageService.languageByList(siteCode).getData();
            List<String> languageCodes = languages.stream().map(LanguageManagerListVO::getCode).toList();
            for (String url : urlList) {
                List<I18NMessageDTO> data = i18nApi.getMessageByKey(url).getData();
                Set<String> i18LanguageCode = data.stream().map(I18NMessageDTO::getLanguage).collect(Collectors.toSet());
                if (CollectionUtil.isNotEmpty(data)) {
                    List<I18nMsgFrontVO> i18nUrlList = new ArrayList<>();
                    data.forEach(item -> {
                        if (languageCodes.contains(item.getLanguage())) {
                            I18nMsgFrontVO vo = new I18nMsgFrontVO();
                            vo.setLanguage(item.getLanguage());
                            vo.setMessage(item.getMessage());
                            String fullUrl = minioDomain + "/" + item.getMessage();
                            vo.setMessageFileUrl(fullUrl);
                            i18nUrlList.add(vo);
                        }
                    });
                    List<String> extraLanguage = languages.stream().map(LanguageManagerListVO::getCode)
                            .filter(language -> !i18LanguageCode.contains(language))
                            .toList();
                    if (!extraLanguage.isEmpty()) {
                        extraLanguage.forEach(language -> {
                            I18nMsgFrontVO vo = new I18nMsgFrontVO();
                            vo.setLanguage(language);
                            i18nUrlList.add(vo);
                        });
                    }
                    urlResultList.add(i18nUrlList);
                }
                result.setI18nFileUrl(urlResultList);
            }
        }
        return ResponseVO.success(result);
    }


    public List<I18nMsgFrontVO> getDownloadBackImgList(String siteCode) {
        LambdaQueryWrapper<SiteDownloadConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteDownloadConfigPO::getSiteCode, siteCode);
        SiteDownloadConfigPO po = this.getOne(queryWrapper);
        if (po == null) {
            return Collections.emptyList();
        }
        List<I18nMsgFrontVO> resultList = new ArrayList<>();
        Map<String, I18nMsgFrontVO> tempMap = new ConcurrentHashMap<>();
        StringBuilder picFullUrlBuilder = new StringBuilder();

        String[] urlList = po.getBanner().split(CommonConstant.COMMA);
        for (String url : urlList) {
            List<I18NMessageDTO> data = i18nApi.getMessageByKey(url).getData();
            if (CollectionUtil.isNotEmpty(data)) {
                String minioDomain = fileService.getMinioDomain();
                Iterator<I18NMessageDTO> iterator = data.iterator();
                if (!resultList.isEmpty()) {
                    while (iterator.hasNext()) {
                        I18NMessageDTO i18NMessageDTO = iterator.next();
                        //重新组装result
                        Iterator<I18nMsgFrontVO> resultIterator = resultList.iterator();
                        while (resultIterator.hasNext()) {
                            I18nMsgFrontVO next = resultIterator.next();
                            if (next.getLanguage().equals(i18NMessageDTO.getLanguage())) {
                                I18nMsgFrontVO newNext = new I18nMsgFrontVO();
                                picFullUrlBuilder.append(next.getMessageFileUrl()).append(CommonConstant.COMMA);
                                picFullUrlBuilder.append(minioDomain).append("/").append(i18NMessageDTO.getMessage());
                                newNext.setMessageFileUrl(picFullUrlBuilder.toString());
                                newNext.setLanguage(i18NMessageDTO.getLanguage());
                                picFullUrlBuilder.setLength(0);
                                tempMap.put(next.getLanguage(), newNext);
                                resultIterator.remove();
                                break;
                            }
                        }

                    }
                    resultList.addAll(tempMap.values());
                } else {
                    while (iterator.hasNext()) {
                        I18NMessageDTO i18nMessageDTO = iterator.next();
                        //第一次添加
                        I18nMsgFrontVO resultVO = new I18nMsgFrontVO();
                        String message = i18nMessageDTO.getMessage();
                        String language = i18nMessageDTO.getLanguage();
                        picFullUrlBuilder.append(minioDomain).append("/").append(message);
                        resultVO.setLanguage(language);
                        resultVO.setMessageFileUrl(picFullUrlBuilder.toString());
                        picFullUrlBuilder.setLength(0);
                        resultList.add(resultVO);
                    }

                }
            }
        }

        return resultList;
    }


    private void initPwaFile(String siteCode) {
        try {
            log.info("开始创建当前站点pwa配置文件至minio,siteCode:{}", siteCode);
            LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
            query.eq(SitePO::getSiteCode, siteCode);
            SitePO po = siteRepository.selectOne(query);
            if (po != null) {
                PwaVO pwaVO = new PwaVO();
                pwaVO.setName(po.getSiteName());
                pwaVO.setShort_name(po.getSiteName());
                pwaVO.setDescription(po.getSiteName());
                pwaVO.setTheme_color("#ffffff");
                pwaVO.setBackground_color("#ffffff");
                pwaVO.setDisplay("standalone");
                pwaVO.setStart_url("/");
                pwaVO.setScope("/");
                List<PwaVO.Icon> arr = new ArrayList<>();
                PwaVO.Icon shortIcon = new PwaVO.Icon();
                shortIcon.setSizes("192x192");
                shortIcon.setType("image/png");
                PwaVO.Icon longIcon = new PwaVO.Icon();
                longIcon.setSizes("512x512");
                longIcon.setType("image/png");
                ResponseVO<i18nMessagesVO> resp = getDownloadInfoBySiteCode(siteCode);
                if (resp.isOk()) {
                    i18nMessagesVO data = resp.getData();
                    shortIcon.setSrc(data.getIconFileUrl());
                    longIcon.setSrc(data.getIconFileUrl());
                }
                arr.add(shortIcon);
                arr.add(longIcon);
                pwaVO.setIcons(arr);
                minioUploadApi.uploadPwa(pwaVO, siteCode + "_manifest.json");
                log.info("当前{}站点pwa配置创建完成", siteCode);
            }
        } catch (Exception e) {
            log.error("当前站点:{},初始化pwa文件失败,原因:{}", siteCode, e.getMessage());
        }
    }


    public SiteDownloadConfigPO getSiteDownloadConfig(String siteCode) {
        LambdaQueryWrapper<SiteDownloadConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteDownloadConfigPO::getSiteCode, siteCode);
        return this.getOne(query);
    }


}
