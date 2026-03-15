package com.cloud.baowang.system.service.versions;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.enums.DomainBindStatusEnum;
import com.cloud.baowang.system.api.enums.versions.VersionMobilePlatform;
import com.cloud.baowang.system.api.enums.versions.VersionUpdateStatus;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.system.api.vo.site.SiteMessageQueryVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.agreement.i18nMessagesVO;
import com.cloud.baowang.system.api.vo.version.SiteSystemInfo;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerPageQueryVO;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerReqVO;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerRespVO;
import com.cloud.baowang.system.po.operations.DomainInfoPO;
import com.cloud.baowang.system.po.site.SitePO;
import com.cloud.baowang.system.po.versions.SystemVersionChangeRecordPO;
import com.cloud.baowang.system.po.versions.SystemVersionManagerPO;
import com.cloud.baowang.system.repositories.SiteRepository;
import com.cloud.baowang.system.repositories.versions.SystemVersionChangeRecordMapper;
import com.cloud.baowang.system.repositories.versions.SystemVersionManagerMapper;
import com.cloud.baowang.system.service.SiteService;
import com.cloud.baowang.system.service.language.LanguageManagerService;
import com.cloud.baowang.system.service.operations.DomainInfoService;
import com.cloud.baowang.system.service.site.config.SiteDownloadConfigService;
import com.cloud.baowang.system.api.file.MinioFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class SystemVersionManagerService extends ServiceImpl<SystemVersionManagerMapper, SystemVersionManagerPO> {
    private final SystemVersionChangeRecordMapper recordMapper;
    private final MinioFileService fileService;
    private final I18nApi i18nApi;
    private final LanguageManagerService languageManagerService;
    private final SiteService siteService;
    private final DomainInfoService domainInfoService;
    private final SiteDownloadConfigService siteDownloadConfigService;
    private final SiteRepository siteRepository;
    private final SystemVersionChangeRecordService systemVersionChangeRecordService;
    private final String iosPListFilePrefix = "itms-services://?action=download-manifest&url=";


    public ResponseVO<Page<SystemVersionManagerRespVO>> pageQuery(SystemVersionManagerPageQueryVO queryVO) {
        Page<SystemVersionManagerPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        LambdaQueryWrapper<SystemVersionManagerPO> query = Wrappers.lambdaQuery();
        String siteCode = queryVO.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(SystemVersionManagerPO::getSiteCode, siteCode);
        }
        Integer versionUpdateStatus = queryVO.getVersionUpdateStatus();
        if (versionUpdateStatus != null) {
            query.eq(SystemVersionManagerPO::getVersionUpdateStatus, versionUpdateStatus);
        }
        Integer deviceTerminal = queryVO.getDeviceTerminal();
        if (deviceTerminal != null) {
            query.eq(SystemVersionManagerPO::getDeviceTerminal, deviceTerminal);
        }
        String creator = queryVO.getCreator();
        if (StringUtils.isNotBlank(creator)) {
            query.eq(SystemVersionManagerPO::getCreator, creator);
        }
        String minioDomain = fileService.getMinioDomain();
        String orderField = queryVO.getOrderField();
        if (StringUtils.isNotBlank(orderField) && "updatedTime".equals(orderField)) {
            if ("asc".equals(queryVO.getOrderType())) {
                query.orderByAsc(SystemVersionManagerPO::getUpdatedTime);
            } else {
                query.orderByDesc(SystemVersionManagerPO::getUpdatedTime);
            }
        }else{
            query.orderByDesc(SystemVersionManagerPO::getCreatedTime);
        }
        page = this.page(page, query);
        List<SystemVersionManagerPO> records = page.getRecords();
        Map<String, List<I18nMsgFrontVO>> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(records)) {
            List<String> i18KeyList = records.stream().map(SystemVersionManagerPO::getUpdateDescription).toList();
            ResponseVO<List<I18NMessageDTO>> resp = i18nApi.getMessageByKeyList(i18KeyList);
            if (resp.isOk()) {
                List<I18NMessageDTO> data = resp.getData();
                List<I18nMsgFrontVO> i18nMsgFrontVOS = BeanUtil.copyToList(data, I18nMsgFrontVO.class);
                map = i18nMsgFrontVOS.stream()
                        .collect(Collectors.groupingBy(I18nMsgFrontVO::getMessageKey));
            }
        }

        Map<String, List<I18nMsgFrontVO>> finalMap = map;
        return ResponseVO.success(ConvertUtil.toConverPage(page.convert(item -> {
            SystemVersionManagerRespVO resp = BeanUtil.copyProperties(item, SystemVersionManagerRespVO.class);
            String fileUrl = resp.getFileUrl();
            if (finalMap.containsKey(resp.getUpdateDescription())) {
                List<I18nMsgFrontVO> i18nMsgFrontVOS = finalMap.get(resp.getUpdateDescription());
                resp.setUpdateDescriptionArr(i18nMsgFrontVOS);
            }
            if (StringUtils.isNotBlank(fileUrl)) {
                resp.setFileShowUrl(minioDomain + "/" + fileUrl);
            }
            return resp;
        })));
    }

    @Transactional
    public ResponseVO<Boolean> createVersion(SystemVersionManagerReqVO reqVO) {

        if (!isValidVersion(reqVO)) {
            throw new BaowangDefaultException(ResultCode.VERSION_NUMBER_ERROR);
        }
        //创建一条变更记录
        SystemVersionChangeRecordPO recordPO = new SystemVersionChangeRecordPO();
        recordPO.setSiteCode(reqVO.getSiteCode());
        recordPO.setSiteName(reqVO.getSiteName());
        BeanUtil.copyProperties(reqVO, recordPO);
        SystemVersionManagerPO newPO = getNewPO(recordPO.getSiteCode(), reqVO.getDeviceTerminal());
        if (newPO != null) {
            // recordPO.setBeforeFile(newPO.getFileUrl());
            recordPO.setChangeBefore(newPO.getVersionUpdateStatus());
        }
        // recordPO.setAfterFile(reqVO.getFileUrl());
        recordPO.setChangeAfter(reqVO.getVersionUpdateStatus());
        //创建i18code
        String i18Key = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.VERSION_MANAGER_DESC.getCode());
        // 插入国际化信息
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(
                i18Key, reqVO.getUpdateDescription());
        i18nApi.update(i18nData);
        SystemVersionManagerPO po = BeanUtil.copyProperties(reqVO, SystemVersionManagerPO.class);
        po.setUpdateDescription(i18Key);
        this.save(po);
        recordMapper.insert(recordPO);
        if (reqVO.getVersionUpdateStatus()== VersionUpdateStatus.MANDATORY_UPGRADE.getCode()){
            LambdaUpdateWrapper<SystemVersionManagerPO> updateWrapper= new LambdaUpdateWrapper<>();
            updateWrapper.set(SystemVersionManagerPO::getVersionUpdateStatus, VersionUpdateStatus.MANDATORY_UPGRADE.getCode());
            updateWrapper.eq(SystemVersionManagerPO::getSiteCode,reqVO.getSiteCode());
            updateWrapper.eq(SystemVersionManagerPO::getDeviceTerminal,reqVO.getDeviceTerminal());
            updateWrapper.le(SystemVersionManagerPO::getCreatedTime,po.getCreatedTime());
            this.update(null, updateWrapper);
        }
        return ResponseVO.success();
    }

    /**
     * 校验版本号规范
     *
     * @param vo
     * @return true
     */
    private boolean isValidVersion(SystemVersionManagerReqVO vo) {
        int deviceTerminal = vo.getDeviceTerminal();
        String version = vo.getVersionNumber();
        String siteCode = vo.getSiteCode();
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        if (version.length() > 20) {
            return false;
        }
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);

        if (major > 999 || minor > 999 || patch > 999) {
            return false;
        }
        SystemVersionManagerPO newPO = getNewPO(siteCode, deviceTerminal);
        if (newPO != null) {
            //存在历史的版本,判断当前版本号是否是升序
            return isVersionAscending(version.split("\\."), newPO.getVersionNumber().split("\\."));
        }
        //不存在,不需要校验版本号是否升序
        return true;
    }


    /**
     * 判断当前版本号是否比历史版本号升序
     *
     * @param currentParts    当前版本号的各个部分
     * @param historicalParts 历史版本号的各个部分
     * @return 如果当前版本号是升序，则返回true，否则返回false
     */
    private boolean isVersionAscending(String[] currentParts, String[] historicalParts) {
        // 填充数组长度一致
        int maxLength = Math.max(currentParts.length, historicalParts.length);
        String[] currentPadded = new String[maxLength];
        String[] historicalPadded = new String[maxLength];

        for (int i = 0; i < maxLength; i++) {
            currentPadded[i] = i < currentParts.length ? currentParts[i] : "0";
            historicalPadded[i] = i < historicalParts.length ? historicalParts[i] : "0";
        }

        // 比较版本号的每一部分
        for (int i = 0; i < maxLength; i++) {
            int currentPart = Integer.parseInt(currentPadded[i]);
            int historicalPart = Integer.parseInt(historicalPadded[i]);

            if (currentPart > historicalPart) {
                return true; // 当前版本号大于历史版本号，返回true
            } else if (currentPart < historicalPart) {
                return false; // 当前版本号小于历史版本号，返回false
            }
        }
        return false;
    }


    private SystemVersionManagerPO getNewPO(String siteCode, Integer deviceTerminal) {
        LambdaQueryWrapper<SystemVersionManagerPO> query = Wrappers.lambdaQuery();
        query.eq(SystemVersionManagerPO::getSiteCode, siteCode);
        if (deviceTerminal != null) {
            query.eq(SystemVersionManagerPO::getDeviceTerminal, deviceTerminal);
        }
        query.orderByDesc(SystemVersionManagerPO::getCreatedTime).last(" limit 1");
        return this.getOne(query);
    }


    private SystemVersionManagerPO getWebNewPO(String siteCode, Integer deviceTerminal,String versionCode) {
        LambdaQueryWrapper<SystemVersionManagerPO> query = Wrappers.lambdaQuery();
        query.eq(SystemVersionManagerPO::getSiteCode, siteCode);
        if (deviceTerminal != null) {
            query.eq(SystemVersionManagerPO::getDeviceTerminal, deviceTerminal);
        }
        query.eq(SystemVersionManagerPO::getVersionNumber,versionCode);
        return this.getOne(query);
    }

    public ResponseVO<SystemVersionManagerRespVO> getNewVersionBySiteCode(String siteCode, Integer platform,String versionCode) {
        log.info("获取最新版本,当前站点:{},所在的端:{}", siteCode, platform);
        SystemVersionManagerRespVO vo = BeanUtil.copyProperties(getNewPO(siteCode, platform), SystemVersionManagerRespVO.class);
        if (vo == null) {
            vo = new SystemVersionManagerRespVO();
        } else {
            if (StringUtils.isEmpty(versionCode)){
                vo.setVersionUpdateStatus(VersionUpdateStatus.MANDATORY_UPGRADE.getCode());
            }else{
                SystemVersionManagerRespVO nowVersionData = BeanUtil.copyProperties(getWebNewPO(siteCode, platform,versionCode), SystemVersionManagerRespVO.class);
                if (ObjectUtils.isEmpty(nowVersionData)){
                    return ResponseVO.fail(ResultCode.VERSION_NUMBER_ERROR);
                }
                if (nowVersionData.getVersionNumber().equals(vo.getVersionNumber())){
                    vo.setVersionUpdateStatus(VersionUpdateStatus.LATEST_VERSION.getCode());
                }else{
                    vo.setVersionUpdateStatus(nowVersionData.getVersionUpdateStatus());
                }
            }
            String fileUrl = vo.getFileUrl();
            if (StringUtils.isNotBlank(fileUrl)) {
                String minioDomain = fileService.getMinioDomain();
                vo.setFileUrl(minioDomain + "/" + fileUrl);
                if (vo.getDeviceTerminal().equals(VersionMobilePlatform.IOS.getCode())) {
                    //IOS,拼接
                    vo.setFileUrl(iosPListFilePrefix + vo.getFileUrl());
                }
            }
            if (platform != null && platform == VersionMobilePlatform.IOS.getCode()) {
                LambdaQueryWrapper<SitePO> siteQuery = Wrappers.lambdaQuery();
                siteQuery.eq(SitePO::getSiteCode, siteCode);
                SitePO sitePO = siteRepository.selectOne(siteQuery);
                if (sitePO != null) {
                    vo.setSiteName(sitePO.getSiteName());
                }
                vo.setPlistUrl(iosPListFilePrefix + fileService.getMinioDomain() + "/file-baowang/" + siteCode + ".plist");
                LambdaQueryWrapper<DomainInfoPO> query = Wrappers.lambdaQuery();
                query.eq(DomainInfoPO::getSiteCode, siteCode);
                query.eq(DomainInfoPO::getDomainType, DomainInfoTypeEnum.DOWNLOAD_PAGE.getType());
                query.orderByDesc(DomainInfoPO::getUpdatedTime).last(" limit 1");
                DomainInfoPO domainInfoPO = domainInfoService.getOne(query);
                vo.setFileShowUrl(Objects.nonNull(domainInfoPO) ? domainInfoPO.getDomainAddr() : null);
            }
        }
        return ResponseVO.success(vo);
    }

    @Transactional
    public ResponseVO<Boolean> updVersion(SystemVersionManagerReqVO reqVO) {
        String id = reqVO.getId();
        SystemVersionManagerPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SystemVersionManagerPO systemVersionManagerPO = BeanUtil.copyProperties(reqVO, SystemVersionManagerPO.class);
        //创建一条变更记录
        SystemVersionChangeRecordPO recordPO = new SystemVersionChangeRecordPO();
        recordPO.setSiteCode(po.getSiteCode());
        recordPO.setSiteName(po.getSiteName());
        recordPO.setChangeBefore(po.getVersionUpdateStatus());
        // recordPO.setBeforeFile(po.getFileUrl());

        // recordPO.setAfterFile(reqVO.getFileUrl());
        recordPO.setChangeAfter(reqVO.getVersionUpdateStatus());
        String i18Key = po.getUpdateDescription();
        // 插入国际化信息
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(
                i18Key, reqVO.getUpdateDescription());
        i18nApi.update(i18nData);
        systemVersionManagerPO.setUpdateDescription(i18Key);

        this.updateById(systemVersionManagerPO);
        recordMapper.insert(recordPO);
        if (reqVO.getVersionUpdateStatus()== VersionUpdateStatus.MANDATORY_UPGRADE.getCode()){
            LambdaUpdateWrapper<SystemVersionManagerPO> updateWrapper= new LambdaUpdateWrapper<>();
            updateWrapper.set(SystemVersionManagerPO::getVersionUpdateStatus, VersionUpdateStatus.MANDATORY_UPGRADE.getCode());
            updateWrapper.eq(SystemVersionManagerPO::getSiteCode,reqVO.getSiteCode());
            updateWrapper.eq(SystemVersionManagerPO::getDeviceTerminal,reqVO.getDeviceTerminal());
            updateWrapper.le(SystemVersionManagerPO::getCreatedTime,systemVersionManagerPO.getCreatedTime());
            this.update(null, updateWrapper);
        }
        return ResponseVO.success();
    }

    public ResponseVO<SiteSystemInfo> getSiteSystemInfo(SiteMessageQueryVO queryVO) {
        List<Integer> domainTypeList = new ArrayList<>();
        domainTypeList.add(DomainInfoTypeEnum.DOWNLOAD_PAGE.getType());
        domainTypeList.add(DomainInfoTypeEnum.PC_DOWNLOAD_ADDRESS.getType());
        DomainVO domainVO = domainInfoService.getDomainByAddress(queryVO.getDomainAddr(), domainTypeList, DomainBindStatusEnum.BIND.getCode());
        if (domainVO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String siteCode = domainVO.getSiteCode();
        SiteSystemInfo result = new SiteSystemInfo();
        //站点首页地址
        LambdaQueryWrapper<DomainInfoPO> homeQuery = Wrappers.lambdaQuery();
        homeQuery.eq(DomainInfoPO::getSiteCode, siteCode)
                .eq(DomainInfoPO::getBind, DomainBindStatusEnum.BIND.getCode())
                .eq(DomainInfoPO::getDomainType, DomainInfoTypeEnum.WEB_PORTAL.getType());
        homeQuery.orderByDesc(DomainInfoPO::getUpdatedTime).last("limit 0,1");
        DomainInfoPO homeDomain = domainInfoService.getOne(homeQuery);
        if (homeDomain == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        result.setSiteCode(siteCode);
        result.setHomePageUrl(homeDomain.getDomainAddr());

        //语言列表
        ResponseVO<List<LanguageManagerListVO>> listResponseVO = languageManagerService.languageByList(siteCode);
        if (listResponseVO.isOk()) {
            List<LanguageManagerListVO> languageManagerListVOS = listResponseVO.getData();
            result.setLanguageList(languageManagerListVOS);
        }
        //站点图片
        ResponseVO<SiteVO> siteVOResponseVO = siteService.getSiteInfo(siteCode);
        if (siteVOResponseVO.isOk()) {
            SiteVO siteVO = siteVOResponseVO.getData();
            result.setSiteName(siteVO.getSiteName());
            result.setLongLogoImage(siteVO.getLongLogoImage());
            result.setShortLogoImage(siteVO.getShortLogoImage());
        }
        //变更为从信息配置里面的下载地址
        ResponseVO<i18nMessagesVO> resp = siteDownloadConfigService.getDownloadInfoBySiteCode(siteCode);
        if (resp.isOk()) {
            i18nMessagesVO data = resp.getData();
            result.setAndroidDownloadUrl(data.getAndroidDownloadUrl());
            result.setIosDownloadUrl(data.getIosDownloadUrl());
            result.setDomainUrl(data.getDomainUrl());
            result.setIconFullUrl(data.getIconFileUrl());
            result.setJumpType(data.getJumpType());

        }

        /*//下载链接
        ResponseVO<SystemVersionManagerRespVO> androidVersionResp = getNewVersionBySiteCode(siteCode, VersionMobilePlatform.ANDROID.getCode());
        if (androidVersionResp.isOk() && androidVersionResp.getData() != null) {
            result.setAndroidDownloadUrl(androidVersionResp.getData().getFileUrl());
        }
        //IOS
        ResponseVO<SystemVersionManagerRespVO> iosResp = getNewVersionBySiteCode(siteCode, VersionMobilePlatform.IOS.getCode());
        if (iosResp.isOk() && iosResp.getData() != null) {
            result.setIosDownloadUrl(iosResp.getData().getFileUrl());
        }*/
        //轮播图
        List<I18nMsgFrontVO> imgList = siteDownloadConfigService.getDownloadBackImgList(siteCode);
        if (!imgList.isEmpty()) {
            result.setDownloadImgI18nList(imgList);
        }
        return ResponseVO.success(result);
    }

}
