package com.cloud.baowang.system.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.IterUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.SystemActivityTemplateApi;
import com.cloud.baowang.activity.api.vo.SiteActivityTemplateSaveVO;
import com.cloud.baowang.activity.api.vo.SiteActivityTemplateVO;
import com.cloud.baowang.agent.api.api.AgentHomeAllButtonEntranceApi;
import com.cloud.baowang.agent.api.api.AgentWithdrawConfigApi;
import com.cloud.baowang.agent.api.vo.BaseReqVO;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.SiteMaintenanceVO;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.job.api.constant.SiteJobHandlerConstant;
import com.cloud.baowang.job.api.rest.JobInfoApi;
import com.cloud.baowang.job.api.vo.JobUpsertVO;
import com.cloud.baowang.play.api.api.venue.PlayGameClassInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.SiteVenueVO;
import com.cloud.baowang.play.api.vo.venue.siteDetail.SiteVenueQueryVO;
import com.cloud.baowang.system.api.api.exchange.CurrencyRateConfigApi;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.api.site.rebate.SiteRebateApi;
import com.cloud.baowang.system.api.api.verify.ChannelSiteLinkApi;
import com.cloud.baowang.system.api.enums.*;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.file.MinioFileService;
import com.cloud.baowang.system.api.vo.JsonDifferenceVO;
import com.cloud.baowang.system.api.vo.PwaVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.exchange.RateInitRequestVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerAddVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerVO;
import com.cloud.baowang.system.api.vo.language.SiteLanguageVO;
import com.cloud.baowang.system.api.vo.operations.CustomerChannelRequestVO;
import com.cloud.baowang.system.api.vo.operations.MeiQiaChannelVO;
import com.cloud.baowang.system.api.vo.operations.SkinResVO;
import com.cloud.baowang.system.api.vo.site.*;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminAddVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminResetPasswordVO;
import com.cloud.baowang.system.api.vo.site.agreement.i18nMessagesVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeBodyVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordListReqVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordReqVO;
import com.cloud.baowang.system.api.vo.verify.ChannelSiteLinkVO;
import com.cloud.baowang.system.po.exchange.SystemCurrencyInfo;
import com.cloud.baowang.system.po.lang.LanguageManagerPO;
import com.cloud.baowang.system.po.member.BusinessAdminPO;
import com.cloud.baowang.system.po.operations.DomainInfoPO;
import com.cloud.baowang.system.po.site.SitePO;
import com.cloud.baowang.system.repositories.SiteRepository;
import com.cloud.baowang.system.repositories.operations.CustomerChannelRepository;
import com.cloud.baowang.system.repositories.operations.DomainInfoRepository;
import com.cloud.baowang.system.service.dict.SystemDictConfigService;
import com.cloud.baowang.system.service.exchange.SystemCurrencyInfoService;
import com.cloud.baowang.system.service.language.LanguageManagerService;
import com.cloud.baowang.system.service.operations.SiteCustomerService;
import com.cloud.baowang.system.service.operations.SkinInfoService;
import com.cloud.baowang.system.service.partner.SitePartnerService;
import com.cloud.baowang.system.service.partner.SitePaymentVendorService;
import com.cloud.baowang.system.service.site.SiteAdminService;
import com.cloud.baowang.system.service.site.change.SiteInfoChangeRecordService;
import com.cloud.baowang.system.service.site.config.SiteDownloadConfigService;
import com.cloud.baowang.system.util.JsonComparator;
import com.cloud.baowang.user.api.api.medal.SiteMedalInfoApi;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.wallet.api.api.*;
import com.cloud.baowang.wallet.api.enums.wallet.WayFeeTypeEnum;
import com.cloud.baowang.wallet.api.vo.SiteCurrencyDownBoxVO;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceInitReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawBatchRequsetVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayResponseVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cloud.baowang.common.core.constants.TokenConstants.JWT_CACHE_KEY_HEAD;
import static com.cloud.baowang.common.core.constants.TokenConstants.LOGIN_TOKEN_KEY_HEAD;
import static java.util.stream.Collectors.toMap;

/**
 * @Author : 小智
 * @Date : 2024/7/26 16:09
 * @Version : 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SiteService extends ServiceImpl<SiteRepository, SitePO> {
    private final SiteRepository siteRepository;

    private final SiteCustomerService siteCustomerService;
    private final LanguageManagerService languageManagerService;
    private final SiteAdminService siteAdminService;
    private final MinioFileService minioFileService;
    private final SiteVipOptionApi siteVipOptionApi;
    private final SystemCurrencyInfoService systemCurrencyInfoService;
    private final SitePartnerService sitePartnerService;
    private final SitePaymentVendorService sitePaymentVendorService;
    private final AgentHomeAllButtonEntranceApi buttonEntranceApi;
    private final AreaSiteManageApi siteAreaManageApi;
    private final PlayVenueInfoApi playVenueInfoApi;
    private final SiteRechargeApi siteRechargeApi;
    private final SiteWithdrawApi siteWithdrawApi;
    private final ChannelSiteLinkApi channelSiteLinkApi;
    private final VipRankApi vipRankApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SiteMedalInfoApi siteMedalInfoApi;
    private final SiteVirtualWalletApi walletApi;
    private final JobInfoApi jobInfoApi;
    private final PlayGameClassInfoApi gameClassInfoApi;
    private final AgentWithdrawConfigApi agentWithdrawConfigApi;
    private final SystemDictConfigService dictConfigService;
    private final DomainInfoRepository domainInfoRepository;
    private final SkinInfoService skinInfoService;
    private final SystemRechargeWayApi systemRechargeWayApi;
    private final SystemWithdrawWayApi systemWithdrawWayApi;
    private final SiteDownloadConfigService siteDownloadConfigService;
    private final CustomerChannelRepository channelRepository;
    private final MinioUploadApi minioUploadApi;
    private final SiteInfoChangeRecordService siteInfoChangeRecordService;
    private final SiteRebateApi siteRebateApi;
    private final CurrencyRateConfigApi currencyRateConfigApi;
    private final SiteSecurityBalanceApi siteSecurityBalanceApi;
    private final SystemActivityTemplateApi systemActivityTemplateApi;

    @Value("${admin.pwd:aa123456}")
    private String pwd;


    public ResponseVO<List<SiteVO>> getSiteList() {
        return ResponseVO.success(BeanUtil.copyToList(this.list(), SiteVO.class));
    }

    public ResponseVO<Page<SiteVO>> querySiteInfo(final SiteRequestVO siteRequestVO) {
        Page<SiteVO> result;
        try {
            Page<SitePO> page = new Page<>(siteRequestVO.getPageNumber(), siteRequestVO.getPageSize());
            result = siteRepository.querySiteInfo(page, siteRequestVO);

            Map<String, String> languageMap = languageManagerService.lambdaQuery()
                    .eq(LanguageManagerPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE).list()
                    .stream().collect(Collectors.toMap(LanguageManagerPO::getCode, LanguageManagerPO::getName));

            List<SiteVO> records = result.getRecords();
            if (CollectionUtil.isNotEmpty(records)) {
                String minioDomain = minioFileService.getMinioDomain();
                //获取站点code ids
                List<String> siteCodeList = records.stream()
                        .map(SiteVO::getSiteCode)
                        .toList();
                //批量获取站点管理员白名单列表
                LambdaQueryWrapper<BusinessAdminPO> query = Wrappers.lambdaQuery();
                query.in(BusinessAdminPO::getSiteCode, siteCodeList).eq(BusinessAdminPO::getIsSuperAdmin, YesOrNoEnum.YES.getCode());
                List<BusinessAdminPO> businessAdminPOList = siteAdminService.list(query);
                //批量获取所有站点对应币种信息
                List<SiteCurrencyInfoRespVO> currencyRespList = siteCurrencyInfoApi.getListBySiteCodes(siteCodeList);
                Map<String, List<SiteCurrencyInfoRespVO>> currencyInfoMap = new HashMap<>();
                if (CollectionUtil.isNotEmpty(currencyRespList)) {
                    //转map.key为siteCode,value为币种列表
                    currencyInfoMap = currencyRespList.stream()
                            .collect(Collectors.groupingBy(SiteCurrencyInfoRespVO::getSiteCode));
                }
                Map<String, List<SiteCurrencyInfoRespVO>> finalCurrencyInfoMap = currencyInfoMap;
                records.forEach(obj -> {
                    for (BusinessAdminPO businessAdminPO : businessAdminPOList) {
                        if (obj.getSiteCode().equals(businessAdminPO.getSiteCode())
                                && obj.getSiteAdminAccount().equals(businessAdminPO.getUserName())) {
                            //设置管理员登录白名单
                            obj.setAllowIps(businessAdminPO.getAllowIps());
                            break;
                        }
                    }
                    obj.setLongLogoImage(minioDomain + "/" + obj.getLongLogo());
                    obj.setShortLogoImage(minioDomain + "/" + obj.getShortLogo());
                    if (Objects.nonNull(obj.getBlackLongLogo())) {
                        obj.setBlackLongLogoImage(minioDomain + "/" + obj.getBlackLongLogo());
                    }
                    if (Objects.nonNull(obj.getBlackShortLogo())) {
                        obj.setBlackShortLogoImage(minioDomain + "/" + obj.getBlackShortLogo());
                    }
                    List<CodeValueNoI18VO> languageVO = Lists.newArrayList();
                    if (ObjectUtils.isNotEmpty(obj.getLanguage())) {
                        Arrays.asList(obj.getLanguage().split(",")).forEach(item ->
                                languageVO.add(CodeValueNoI18VO.builder().code(item)
                                        .value(languageMap.get(item)).build())
                        );
                    }
                    obj.setLanguageList(languageVO);
                    //设置币种
                    if (finalCurrencyInfoMap.containsKey(obj.getSiteCode())) {
                        //多语言key
                        String i18MessageCodes = finalCurrencyInfoMap.get(obj.getSiteCode()).stream()
                                .map(SiteCurrencyInfoRespVO::getCurrencyNameI18)
                                .collect(Collectors.joining(","));
                        obj.setCurrencyCodes(i18MessageCodes);
                        //获取到当前站点全部币种信息,获取i18message到vo中
                        String currencyCodes = finalCurrencyInfoMap.get(obj.getSiteCode()).stream()
                                .map(SiteCurrencyInfoRespVO::getCurrencyCode)
                                .collect(Collectors.joining(","));
                        obj.setCurrency(currencyCodes);
                    }
                });

            }
        } catch (Exception e) {
            log.error("query site info have problem", e);
            return ResponseVO.fail(ResultCode.QUERY_SITE_INFO_ERROR);
        }
        return ResponseVO.success(result);
    }

    public ResponseVO<List<SiteVO>> allSiteInfo() {
        List<SiteVO> result;
        try {
            LambdaQueryWrapper<SitePO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SitePO::getStatus, YesOrNoEnum.YES.getCode());
            List<SitePO> siteList = baseMapper.selectList(queryWrapper);
            return ResponseVO.success(siteList.stream().map(x -> {
                SiteVO vo = SiteVO.builder().build();
                BeanUtils.copyProperties(x, vo);
                return vo;
            }).toList());
        } catch (Exception e) {
            log.error("query site info have problem", e);
            return ResponseVO.fail(ResultCode.QUERY_SITE_INFO_ERROR);
        }
    }

    public ResponseVO<List<SiteVO>> siteInfoAllstauts() {
        List<SiteVO> result;
        try {
            LambdaQueryWrapper<SitePO> queryWrapper = new LambdaQueryWrapper<>();
            List<SitePO> siteList = baseMapper.selectList(queryWrapper);
            return ResponseVO.success(siteList.stream().map(x -> {
                SiteVO vo = SiteVO.builder().build();
                BeanUtils.copyProperties(x, vo);
                return vo;
            }).toList());
        } catch (Exception e) {
            log.error("query site info have problem", e);
            return ResponseVO.fail(ResultCode.QUERY_SITE_INFO_ERROR);
        }
    }


    public ResponseVO<?> isEnable(final SiteEnableVO siteEnableVO) {
        try {
            log.info("站点:{}启用/禁用/维护,当前操作人:{},操作时间:{}", siteEnableVO.getSiteCode(), siteEnableVO.getOperator(), siteEnableVO.getOperatorTime());
            Integer status = siteEnableVO.getStatus();
            String siteCode = siteEnableVO.getSiteCode();
            SitePO sitePO = this.getBaseMapper().selectOne(new LambdaUpdateWrapper<SitePO>().eq(SitePO::getSiteCode, siteEnableVO.getSiteCode()));
            String statusBeforeStr = SiteStatusChangeEnums.getSiteStatusEnums(sitePO.getStatus()).getDesc();
            String statusAfterStr = SiteStatusChangeEnums.getSiteStatusEnums(siteEnableVO.getStatus()).getDesc();
            if (SiteStatusEnums.MAINTENANCE.getStatus().equals(sitePO.getStatus())) {
                String start = TimeZoneUtils.formatTimestampToTimeZone(sitePO.getMaintenanceTimeStart(), CurrReqUtils.getTimezone());
                String end = TimeZoneUtils.formatTimestampToTimeZone(sitePO.getMaintenanceTimeEnd(), CurrReqUtils.getTimezone());
                statusBeforeStr = "{" + statusBeforeStr + "时间:[" + start + "," + end + "]}";
            }
            LambdaUpdateWrapper<SitePO> updateWrapper = new LambdaUpdateWrapper<>();

            if (SiteStatusEnums.MAINTENANCE.getStatus().equals(status)) {
                Long maintenanceTimeEnd = siteEnableVO.getMaintenanceTimeEnd();
                Long maintenanceTimeStart = siteEnableVO.getMaintenanceTimeStart();
                //维护中状态,更新维护信息
                setMaintainSiteCache(siteCode, maintenanceTimeStart, maintenanceTimeEnd, SiteStatusEnums.MAINTENANCE.getStatus());
                //设置维护时间
                updateWrapper.set(SitePO::getMaintenanceTimeStart, maintenanceTimeStart).set(SitePO::getMaintenanceTimeEnd, maintenanceTimeEnd);
                String start = TimeZoneUtils.formatTimestampToTimeZone(maintenanceTimeStart, CurrReqUtils.getTimezone());
                String end = TimeZoneUtils.formatTimestampToTimeZone(maintenanceTimeEnd, CurrReqUtils.getTimezone());
                statusAfterStr = "{" + statusAfterStr + "时间:[" + start + "," + end + "]}";
            } else if (SiteStatusEnums.DISABLE.getStatus().equals(status)) {
                updateWrapper.set(SitePO::getMaintenanceTimeStart, null).set(SitePO::getMaintenanceTimeEnd, null);
                //维护中状态,更新维护信息
                setMaintainSiteCache(siteCode, null, null, SiteStatusEnums.DISABLE.getStatus());
                // 站点禁用 踢出所有已登录用户
                //查询所有登录token Keys
                String jwtCachePatterKey = String.format(JWT_CACHE_KEY_HEAD.concat("*"), siteCode);
                Iterable<String> jwtKeys = RedisUtil.getKeysByPattern(jwtCachePatterKey);
                if (!IterUtil.isEmpty(jwtKeys)) {
                    for (String jwtKey : jwtKeys) {
                        log.info("删除 Jwt Key:{}", jwtKey);
                        RedisUtil.deleteKey(jwtKey);
                    }
                }
                //查询所有 权限 keys
                String loginTokenPatterKey = String.format(LOGIN_TOKEN_KEY_HEAD.concat("*"), siteCode);
                Iterable<String> tokenKeys = RedisUtil.getKeysByPattern(loginTokenPatterKey);
                if (!IterUtil.isEmpty(tokenKeys)) {
                    for (String tokenKey : tokenKeys) {
                        log.info("删除 Token Key:{}", tokenKey);
                        RedisUtil.deleteKey(tokenKey);
                    }
                }
                log.info("siteCode:{} is disabled,kick out all login user", siteCode);
            } else if (SiteStatusEnums.ENABLE.getStatus().equals(status)) {
                updateWrapper.set(SitePO::getMaintenanceTimeStart, null).set(SitePO::getMaintenanceTimeEnd, null);
                RedisUtil.deleteKey(RedisConstants.KEY_SERVER_MAINTAIN_SITE_KEY + siteCode);
            }
            updateWrapper.eq(SitePO::getSiteCode, siteEnableVO.getSiteCode());
            updateWrapper.set(SitePO::getStatus, siteEnableVO.getStatus());
            updateWrapper.set(SitePO::getUpdater, siteEnableVO.getOperator());
            updateWrapper.set(SitePO::getUpdatedTime, siteEnableVO.getOperatorTime());
            this.update(null, updateWrapper);
            if (!statusAfterStr.equals(statusBeforeStr)) {
                //站点操作日志增加
                SiteInfoChangeRecordListReqVO isEnableBody = new SiteInfoChangeRecordListReqVO();
                isEnableBody.setLoginIp(CurrReqUtils.getReqIp());
                isEnableBody.setCreator(CurrReqUtils.getAccount());
                isEnableBody.setOptionType(SiteOptionTypeEnum.DataUpdate.getCode());
                isEnableBody.setOptionStatus(SiteOptionStatusEnum.success.getCode());
                isEnableBody.setOptionModelName(SiteOptionModelNameEnum.site.getname());
                isEnableBody.setOptionCode(siteEnableVO.getSiteCode());
                isEnableBody.setOptionName(getSiteInfo(siteEnableVO.getSiteCode()).getData().getSiteName());
                List<JsonDifferenceVO> sitadataList = new ArrayList<>();
                JsonDifferenceVO vo = new JsonDifferenceVO();
                vo.setNewValue(statusAfterStr);
                vo.setOldValue(statusBeforeStr);
                vo.setChangeType(SiteChangeTypeEnum.option.getname());
                vo.setPathName(SitClounmDefaultEnum.state.getname());
                sitadataList.add(vo);
                isEnableBody.setData(sitadataList);
                siteInfoChangeRecordService.addJsonDifferenceList(isEnableBody);
            }
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("siteCode:{} isEnable error", siteEnableVO.getSiteCode(), e);
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
    }

    /**
     * 更新站点维护状态缓存
     *
     * @param siteCode
     * @param maintenanceTimeStart
     * @param maintenanceTimeEnd
     */
    private void setMaintainSiteCache(String siteCode, Long maintenanceTimeStart, Long maintenanceTimeEnd, Integer siteStatus) {
        log.info("开始更新站点维护中缓存信息,当前站点:{},维护时间:{} - {}", siteCode, maintenanceTimeStart, maintenanceTimeEnd);
        LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
        query.eq(SitePO::getSiteCode, siteCode);
        SitePO sitePO = siteRepository.selectOne(query);
        if (sitePO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        //设置站点基础信息
        SiteMaintenanceVO vo = new SiteMaintenanceVO();
        vo.setSiteName(sitePO.getSiteName());
        String minioDomain = minioFileService.getMinioDomain();
        if (StringUtils.isNotBlank(sitePO.getLongLogo())) {
            vo.setLongLogo(minioDomain + "/" + sitePO.getLongLogo());
        }
        if (StringUtils.isNotBlank(sitePO.getShortLogo())) {
            vo.setShortLogo(minioDomain + "/" + sitePO.getShortLogo());
        }
        vo.setSiteStatus(siteStatus);
        vo.setSiteCode(siteCode);

        //如果是维护站点,设置通道,维护页域名信息
        if (SiteStatusEnums.MAINTENANCE.getStatus().equals(siteStatus)) {
            if (maintenanceTimeStart == null
                    || maintenanceTimeEnd == null
                    || maintenanceTimeStart > maintenanceTimeEnd
                    || System.currentTimeMillis() > maintenanceTimeStart) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            LambdaQueryWrapper<DomainInfoPO> domainQuery = Wrappers.lambdaQuery();
            domainQuery.eq(DomainInfoPO::getSiteCode, siteCode).eq(DomainInfoPO::getDomainType, DomainInfoTypeEnum.MAINTENANCE_PAGE.getType());
            domainQuery.orderByDesc(DomainInfoPO::getUpdatedTime).last("limit 0,1");
            DomainInfoPO domainInfoPO = domainInfoRepository.selectOne(domainQuery);
            if (domainInfoPO != null) {
                vo.setMaintenancePageAddress(domainInfoPO.getDomainAddr());
            }
            vo.setMaintenanceTimeStart(maintenanceTimeStart);
            vo.setMaintenanceTimeEnd(maintenanceTimeEnd);
            //获取通道信息
            CustomerChannelRequestVO reqVO = new CustomerChannelRequestVO();
            reqVO.setSiteCode(siteCode);
            reqVO.setStatus(String.valueOf(EnableStatusEnum.ENABLE.getCode()));
            //获取当前站点中已启用的一条客服通道
            List<MeiQiaChannelVO> customerChannelByCode = channelRepository.getCustomerChannelByCode(reqVO);
            MeiQiaChannelVO channelVO = new MeiQiaChannelVO();
            if (CollectionUtil.isNotEmpty(customerChannelByCode)) {
                boolean customerEnable = false;
                for (MeiQiaChannelVO meiQiaChannelVO : customerChannelByCode) {
                    if (meiQiaChannelVO.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
                        channelVO = meiQiaChannelVO;
                        customerEnable = true;
                        break;
                    }
                }
                if (!customerEnable) {
                    channelVO = customerChannelByCode.get(0);
                }
                //赋值通道相关
                BeanUtil.copyProperties(channelVO, vo);
            }
        }
        RedisUtil.setValue(RedisConstants.KEY_SERVER_MAINTAIN_SITE_KEY + siteCode, JSON.toJSONString(vo));
        log.info("更新站点维护中缓存信息完成,当前站点:{}", siteCode);
    }

    public ResponseVO<?> resetPassword(SiteEnableVO siteEnableVO) {
        try {
            LambdaQueryWrapper<BusinessAdminPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BusinessAdminPO::getSiteCode, siteEnableVO.getSiteCode());
            queryWrapper.eq(BusinessAdminPO::getIsSuperAdmin, BigDecimal.ONE.intValue());
            BusinessAdminPO po = siteAdminService.getOne(queryWrapper);
            SiteAdminResetPasswordVO siteAdminResetPasswordVO = new SiteAdminResetPasswordVO();
            siteAdminResetPasswordVO.setPassword(encryptPassword(pwd));
            siteAdminResetPasswordVO.setId(po.getId());
            siteAdminService.resetPassword(siteAdminResetPasswordVO);
            //站点操作日志增加
            SiteInfoChangeRecordListReqVO resetPasswordBody = new SiteInfoChangeRecordListReqVO();
            resetPasswordBody.setLoginIp(CurrReqUtils.getReqIp());
            resetPasswordBody.setCreator(CurrReqUtils.getAccount());
            resetPasswordBody.setOptionType(SiteOptionTypeEnum.DataUpdate.getCode());
            resetPasswordBody.setOptionStatus(SiteOptionStatusEnum.success.getCode());
            resetPasswordBody.setOptionModelName(SiteOptionModelNameEnum.site.getname());
            resetPasswordBody.setOptionCode(siteEnableVO.getSiteCode());
            resetPasswordBody.setOptionName(getSiteInfo(siteEnableVO.getSiteCode()).getData().getSiteName());
            List<JsonDifferenceVO> sitadataList = new ArrayList<>();
            JsonDifferenceVO vo = new JsonDifferenceVO();
            vo.setNewValue("******");
            vo.setOldValue("******");
            vo.setChangeType(SiteChangeTypeEnum.option.getname());
            vo.setPathName(SitClounmDefaultEnum.resetPassword.getname());
            sitadataList.add(vo);
            resetPasswordBody.setData(sitadataList);
            siteInfoChangeRecordService.addJsonDifferenceList(resetPasswordBody);

            return ResponseVO.success();
        } catch (Exception e) {
            log.error("siteCode:{} resetPwd error ", siteEnableVO.getSiteCode(), e);
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
    }

    public ResponseVO<SiteVO> getSiteInfo(String siteCode) {
        SiteVO vo = RedisUtil.getValue(RedisConstants.SITE_INFO + siteCode);
        log.info("第一次获取:{}", vo);
        if (ObjectUtils.isEmpty(vo)) {
            setSiteRedis(siteCode);
            vo = RedisUtil.getValue(RedisConstants.SITE_INFO + siteCode);
            log.info("第二次获取:{}", vo);
//            SitePO po = this.getOne(new LambdaQueryWrapper<SitePO>().eq(SitePO::getSiteCode, siteCode));
//            if (po != null) {
//                BeanUtils.copyProperties(po, vo);
//                String minioDomain = businessConfigService.queryMinioDomain();
//                String shortLogo = vo.getShortLogo();
//                String longLogo = vo.getLongLogo();
//                if (StringUtils.isNotBlank(shortLogo)) {
//                    vo.setShortLogoImage(minioDomain + "/" + shortLogo);
//                }
//                if (StringUtils.isNotBlank(longLogo)) {
//                    vo.setLongLogoImage(minioDomain + "/" + longLogo);
//                }
//
//                //封装语言信息
//                LambdaQueryWrapper<LanguageManagerPO> langQuery = Wrappers.lambdaQuery();
//                langQuery.eq(LanguageManagerPO::getSiteCode, siteCode);
//                List<LanguageManagerPO> languageManagerPOS = languageManagerService.list(langQuery);
//                if (CollectionUtil.isNotEmpty(languageManagerPOS)) {
//                    List<CodeValueNoI18VO> languageList = languageManagerPOS.stream()
//                            .map(item -> new CodeValueNoI18VO(item.getCode(), item.getName()))
//                            .toList();
//                    vo.setLanguageList(languageList);
//                    vo.setLanguageManagerVOS(BeanUtil.copyToList(languageManagerPOS, LanguageManagerVO.class));
//                }
//                //封装币种信息
//                List<SiteCurrencyInfoRespVO> currencyCodeList = siteCurrencyInfoApi.getBySiteCode(po.getSiteCode());
//                if (CollectionUtil.isNotEmpty(currencyCodeList)) {
//                    //获取到当前站点全部币种信息,获取i18message到vo中
//                    String i18MessageCodes = currencyCodeList.stream()
//                            .map(SiteCurrencyInfoRespVO::getCurrencyNameI18)
//                            .collect(Collectors.joining(","));
//                    vo.setCurrency(i18MessageCodes);
//
//                    //获取到当前站点全部币种信息,获取i18message到vo中
//                    String currencyCodes = currencyCodeList.stream()
//                            .map(SiteCurrencyInfoRespVO::getCurrencyCode)
//                            .collect(Collectors.joining(","));
//                    vo.setCurrencyCodes(currencyCodes);
//                }
//                //封装游戏场馆信息
//                List<SiteVenueQueryVO> siteVenueInfoVOS = playVenueInfoApi.querySiteVenueBySiteCode(siteCode);
//                vo.setSiteVenueInfoVOS(BeanUtil.copyToList(siteVenueInfoVOS, com.cloud.baowang.system.api.vo.site.siteDetail.SiteVenueQueryVO.class));
//                String siteAdminAccount = vo.getSiteAdminAccount();
//                //白名单
//                LambdaQueryWrapper<BusinessAdminPO> query = Wrappers.lambdaQuery();
//                query.eq(BusinessAdminPO::getSiteCode, siteCode).eq(BusinessAdminPO::getUserName, siteAdminAccount)
//                        .last("limit 0,1");
//                BusinessAdminPO superAdminPO = siteAdminService.getOne(query);
//                if (superAdminPO != null) {
//                    vo.setAllowIps(superAdminPO.getAllowIps());
//                }
//            }
        }
        LambdaQueryWrapper<DomainInfoPO> domainQuery = Wrappers.lambdaQuery();
        domainQuery.eq(DomainInfoPO::getBind, DomainBindStatusEnum.BIND.getCode()).eq(DomainInfoPO::getSiteCode, siteCode)
                .eq(DomainInfoPO::getDomainType, DomainInfoTypeEnum.DOWNLOAD_PAGE.getType())
                .orderByDesc(DomainInfoPO::getUpdatedTime)
                .last("limit 0,1");
        DomainInfoPO domainInfoPO = domainInfoRepository.selectOne(domainQuery);
        if (domainInfoPO != null) {
            vo.setDownLoadDomainAddr(domainInfoPO.getDomainAddr());
        }

        LambdaQueryWrapper<DomainInfoPO> pcDownLoadQuery = Wrappers.lambdaQuery();
        pcDownLoadQuery.eq(DomainInfoPO::getBind, DomainBindStatusEnum.BIND.getCode()).eq(DomainInfoPO::getSiteCode, siteCode)
                .eq(DomainInfoPO::getDomainType, DomainInfoTypeEnum.PC_DOWNLOAD_ADDRESS.getType())
                .orderByDesc(DomainInfoPO::getUpdatedTime)
                .last("limit 0,1");
        DomainInfoPO pcDownLoadDomain = domainInfoRepository.selectOne(pcDownLoadQuery);
        if (pcDownLoadDomain != null) {
            vo.setPcDownLoadUrl(pcDownLoadDomain.getDomainAddr());
        }

        ResponseVO<SystemDictConfigRespVO> timeConfigRes = dictConfigService.getByCode(DictCodeConfigEnums.BIND_EMAIL_PHONE_CODE_EXPIRY_TIME.getCode(), siteCode);
        if (timeConfigRes != null && timeConfigRes.getData() != null && timeConfigRes.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = timeConfigRes.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            vo.setCodeExpireTime(Integer.parseInt(param));
        } else {
            vo.setCodeExpireTime(5);
        }
        ResponseVO<i18nMessagesVO> resp = siteDownloadConfigService.getDownloadInfoBySiteCode(siteCode);
        if (resp.isOk()) {
            i18nMessagesVO data = resp.getData();
            vo.setIconFullUrl(data.getIconFileUrl());
        }
        return ResponseVO.success(vo);
    }

    public ResponseVO<List<CodeValueVO>> getCurrency(String siteCode) {

        List<CodeValueVO> result = new ArrayList<>();
        //封装币种信息
        List<SiteCurrencyInfoRespVO> currencyCodeList = siteCurrencyInfoApi.getBySiteCode(siteCode);
        for (SiteCurrencyInfoRespVO vo : currencyCodeList) {
            result.add(CodeValueVO.builder().code(vo.getCurrencyCode()).value(vo.getCurrencyNameI18()).build());
        }
        return ResponseVO.success(result);
    }

    public Map<String, List<CodeValueVO>> getLanAndCurrencyDownBox() {
        Map<String, List<CodeValueVO>> resultMap = Maps.newHashMap();
        List<CodeValueVO> list = Lists.newArrayList();
        List<SystemCurrencyInfo> systemCurrencyInfo = systemCurrencyInfoService.list();
        systemCurrencyInfo.forEach(obj -> list.add(CodeValueVO.builder()
                .code(obj.getCurrencyCode()).value(obj.getCurrencyNameI18()).build()));
        resultMap.put(CommonConstant.CURRENCY_TYPE, list);
        return resultMap;
    }

    public ResponseVO<List<CodeValueVO>> chooseCurrency(String siteCode) {
        return getCurrency(siteCode);
    }

    /**
     * 2024-08-24 09:25:00 by:aomiao
     * 判断当前站点包不包风控，包返回true,不包为false(风控层级，风控使用)
     *
     * @param siteCode 当前站点code
     * @return boolean 为true包含，false不包含
     */
    public Boolean checkSiteIncludesRiskControl(String siteCode) {
        ResponseVO<SiteVO> siteInfo = this.getSiteInfo(siteCode);
        boolean isExcluded = false;
        if (!siteInfo.isOk()) {
            throw new BaowangDefaultException("获取站点信息失败");
        }
        SiteVO data = siteInfo.getData();
        if (data == null) {
            throw new BaowangDefaultException("没有获取到当前站点信息");
        }
       /* Integer siteModel = data.getSiteModel();
        //类型是全包，或者是包风控时，走总台配置的风控层级信息（siteCode is null）
        if (SiteModelEnum.FULL_PACKAGE.getType().equals(siteModel) ||
                SiteModelEnum.RISK_MANAGEMENT_PACKAGE.getType().equals(siteModel)) {
            isExcluded = true;
        }*/
        return isExcluded;
    }

    public ResponseVO<List<CodeValueVO>> getSiteDownBox() {
        List<CodeValueVO> result = Lists.newArrayList();
        this.lambdaQuery().eq(SitePO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .list().forEach(obj -> result.add(CodeValueVO.builder().code(obj.getSiteCode())
                        .value(obj.getSiteName()).build()));
        return ResponseVO.success(result);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<SitePO> saveAndUpdateSite(String siteCode, SiteAddVO siteAddVO, String pwd) {
        // 默认第一个站点code
        // 站点规则6位字母加数字 首位字母
        if (StringUtils.isBlank(siteCode)) {
            siteCode = RandomStringUtil.getRandomFirstEN(6);
        }
        siteAddVO.setSiteCode(siteCode);
        String operatorUserNo = siteAddVO.getCreator();
        log.info("新增站点,当前站点编号:{},操作人:{}", siteAddVO, operatorUserNo);
        RLock lock = RedisUtil.getLock(RedisKeyTransUtil.getAddSiteLockKey(operatorUserNo));
        log.info("新建站点,当前站点编号:{},操作人:{}", siteCode, operatorUserNo);
        try {
            if (lock.tryLock(20000, 30000L, TimeUnit.MILLISECONDS)) {
                //保存站点相关全部数据
                return ResponseVO.success(processSiteData(siteAddVO, pwd, siteCode));
            }
        } catch (InterruptedException e) {
            log.error("add site have error e :{}", e.getMessage());
            throw new BaowangDefaultException(e.getMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("锁释放{}", operatorUserNo);
            }
        }
        throw new BaowangDefaultException(ResultCode.ADD_SITE_BASIC_ERROR);
    }


    public ResponseVO<Boolean> updateSiteInfo(SiteAddVO siteAddVO) {

        Integer step = siteAddVO.getLastStep();
        String siteCode = siteAddVO.getSiteCode();
        String operator = siteAddVO.getCreator();
        LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
        query.eq(SitePO::getSiteCode, siteCode);
        SitePO po = this.getOne(query);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        po.setUpdater(operator);
        po.setUpdatedTime(System.currentTimeMillis());
        this.updateById(po);
        log.info("编辑站点,当前站点编号:{},操作人:{},步骤:{}", siteCode, operator, step);
        RedisUtil.deleteKey(RedisConstants.SITE_INFO + siteCode);
        //刷新平台币缓存
        RedisUtil.setLocalCachedMap(CacheConstants.KEY_SITE_PLAT_CURRENCY, siteCode, CommonConstant.PLAT_CURRENCY_NAME);
        RedisUtil.setLocalCachedMap(CacheConstants.KEY_SITE_PLAT_CURRENCY_SYMBOL, siteCode, po.getPlatCurrencySymbol());
        log.info("修改站点,当前站点:{},操作人:{},操作步骤:{}", siteCode, operator, step);
        ResponseVO<Boolean> result = ResponseVO.success();
        if (step == 1) {
            result = updateSiteBasic(po, siteAddVO.getSiteBasic(), operator);
        } else if (step == 2) {
            result = updateSiteConfig(po, siteAddVO.getSiteConfig(), operator);
        } else if (step == 3) {
            result = updateSiteVenue(siteCode, siteAddVO.getSiteVenue(), operator, po.getSiteName());
        } else if (step == 4) {
            result = updateSiteDeposit(siteCode, siteAddVO.getSiteDeposit(), operator, po.getSiteName(), po.getHandicapMode());
        } else if (step == 5) {
            result = updateSiteWithdraw(siteCode, siteAddVO.getSiteWithdraw(), operator, po.getSiteName());
        } else if (step == 6) {
            result = updateSiteSms(siteCode, siteAddVO.getSiteSms(), operator, po.getSiteName());
        } else if (step == 7) {
            result = updateSiteEmail(siteCode, siteAddVO.getSiteEmail(), operator, po.getSiteName());
        } else if (step == 8) {
            updateSiteCustomer(siteCode, siteAddVO.getSiteCustomer(), operator, po.getSiteName());
        } else {
            result = null;
        }
        //当前站点如果是维护中状态,需要去更新维护信息
        if (SiteStatusEnums.MAINTENANCE.getStatus().equals(po.getStatus())) {
            log.info("当前站点:{},处于维护中,更新站点缓存", po.getSiteCode());
            String value = RedisUtil.getValue(RedisConstants.KEY_SERVER_MAINTAIN_SITE_KEY + po.getSiteCode());
            if (StringUtils.isNotBlank(value)) {
                SiteMaintenanceVO vo = JSON.parseObject(value, SiteMaintenanceVO.class);
                setMaintainSiteCache(po.getSiteCode(), vo.getMaintenanceTimeStart(), vo.getMaintenanceTimeEnd(), SiteStatusEnums.MAINTENANCE.getStatus());
            }
        } else if (SiteStatusEnums.DISABLE.getStatus().equals(po.getStatus())) {
            //禁用后再编辑的,更新一下缓存信息
            log.info("当前站点:{},禁用状态下编辑,更新站点缓存", po.getSiteCode());
            setMaintainSiteCache(po.getSiteCode(), null, null, SiteStatusEnums.DISABLE.getStatus());
        }
        //更新一下配置文件
        initPwaFile(siteCode);
        return result;
    }

    private ResponseVO<Boolean> updateSiteCustomer(String siteCode, SiteCustomerVO siteCustomer, String operator, String siteName) {
        // 客服授权
        boolean customerAuthorize = siteCustomerService.addSiteCustomer(siteCode, siteCustomer.getChannelCode(), siteName);
        return ResponseVO.success();
    }

    private ResponseVO<Boolean> updateSiteEmail(String siteCode, SiteEmailVO siteEmail, String operator, String siteName) {
        ChannelSiteLinkVO mailSiteLinkVO = new ChannelSiteLinkVO();
        mailSiteLinkVO.setSiteCode(siteCode);
        mailSiteLinkVO.setChannelCodeList(siteEmail.getEmailId());
        mailSiteLinkVO.setCreator(operator);
        // 邮箱授权
        ResponseVO<Boolean> emailAuthorize = channelSiteLinkApi.mailSiteAddBatch(siteCode, mailSiteLinkVO, siteName);

        if (emailAuthorize.isOk() && !emailAuthorize.getData() || !emailAuthorize.isOk()) {
            throw new BaowangDefaultException(ResultCode.UPDATE_SITE_EMAIL_ERROR);
        }
        return ResponseVO.success();
    }

    private ResponseVO<Boolean> updateSiteSms(String siteCode, SiteSmsVO siteSms, String operator, String siteName) {

        ChannelSiteLinkVO smsSiteLinkVO = new ChannelSiteLinkVO();

        smsSiteLinkVO.setSiteCode(siteCode);
        smsSiteLinkVO.setChannelCodeList(siteSms.getSmsId());
        //新增创建人oneId
        smsSiteLinkVO.setCreator(operator);
        // 短信授权
        ResponseVO<Boolean> smsAuthorize = channelSiteLinkApi.smsSiteAddBatch(siteCode, smsSiteLinkVO, siteName);

        if (smsAuthorize.isOk() && !smsAuthorize.getData() || !smsAuthorize.isOk()) {
            throw new BaowangDefaultException(ResultCode.UPDATE_SITE_MESSAGE_ERROR);
        }
        return ResponseVO.success();
    }

    /**
     * 修改提款方式
     *
     * @param siteCode
     * @param operator
     * @return
     */
    private ResponseVO<Boolean> updateSiteWithdraw(String siteCode, List<SiteWithdrawVO> siteWithdrawList, String operator, String siteName) {
        Map<String, SiteWithdrawVO> siteWithdrawMap = siteWithdrawList.stream()
                .collect(toMap(SiteWithdrawVO::getWithdrawWayId, item -> item));
        // 手续费费率不能低于场馆费
        List<SystemWithdrawWayResponseVO> withdrawResult = systemWithdrawWayApi.queryWithdrawWayList();

        for (SystemWithdrawWayResponseVO vo : withdrawResult) {
            if (siteWithdrawMap.containsKey(vo.getId())) {
                SiteWithdrawVO siteWithdraw = siteWithdrawMap.get(vo.getId());
                //总台金额配置
                BigDecimal systemWayFee = vo.getWayFee();
                BigDecimal systemWayFeeFixedAmount = vo.getWayFeeFixedAmount();

                //当前传入配置
                BigDecimal siteWayFee = siteWithdraw.getWithdrawFee();
                BigDecimal siteWayFeeFixedAmount = siteWithdraw.getWayFeeFixedAmount();

                if (WayFeeTypeEnum.PERCENTAGE.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFee.compareTo(systemWayFee) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFeeFixedAmount.compareTo(systemWayFeeFixedAmount) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                } else if (WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFee.compareTo(systemWayFee) < 0 || siteWayFeeFixedAmount.compareTo(systemWayFeeFixedAmount) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                }
            }
        }
        List<SiteWithdrawBatchRequsetVO> siteWithdrawReq = BeanUtil.copyToList(siteWithdrawList, SiteWithdrawBatchRequsetVO.class);
        // 提款授权
        ResponseVO<Boolean> withdrawAuthorize = siteWithdrawApi.batchSave(operator, siteCode, siteWithdrawReq, siteName);

        if (withdrawAuthorize.isOk() && !withdrawAuthorize.getData() || !withdrawAuthorize.isOk()) {
            throw new BaowangDefaultException(ResultCode.UPDATE_SITE_WITHDRAW_ERROR);
        }
        return ResponseVO.success();
    }

    /**
     * 修改存款方式
     *
     * @param siteCode    站点
     * @param siteDeposit 当前数据
     * @param operator    操作人
     * @return void
     */
    private ResponseVO<Boolean> updateSiteDeposit(String siteCode, List<SiteDepositVO> siteDeposit, String operator, String siteName, Integer handicapMode) {
//        if (CollectionUtil.isEmpty(siteDeposit)) {
//            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//        }

        // 手续费费率不能低于场馆费
        List<SystemRechargeWayRespVO> rechargeResult = systemRechargeWayApi.queryRechargeWayList();
        //校验前端传入存款方式是否存在
        List<String> systemRechargeIds = rechargeResult.stream()
                .map(SystemRechargeWayRespVO::getId)
                .toList();
        for (SiteDepositVO siteDepositVO : siteDeposit) {
            if (!systemRechargeIds.contains(siteDepositVO.getRechargeWayId())) {
                throw new BaowangDefaultException(ResultCode.DEPOSIT_CHOOSE_ERROR);
            }
        }

        Map<String, SiteDepositVO> siteDepositMap = siteDeposit.stream()
                .collect(toMap(SiteDepositVO::getRechargeWayId, siteDepositVO -> siteDepositVO));
        //校验值是否小于总台配置
        for (SystemRechargeWayRespVO vo : rechargeResult) {
            if (siteDepositMap.containsKey(vo.getId())) {
                SiteDepositVO siteDepositVO = siteDepositMap.get(vo.getId());
                //总台金额配置
                BigDecimal systemWayFee = vo.getWayFee();
                BigDecimal systemWayFeeFixedAmount = vo.getWayFeeFixedAmount();

                //当前传入配置
                BigDecimal siteWayFee = siteDepositVO.getDepositFee();
                BigDecimal siteWayFeeFixedAmount = siteDepositVO.getWayFeeFixedAmount();

                if (WayFeeTypeEnum.PERCENTAGE.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFee.compareTo(systemWayFee) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFeeFixedAmount.compareTo(systemWayFeeFixedAmount) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                } else if (WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFee.compareTo(systemWayFee) < 0 || siteWayFeeFixedAmount.compareTo(systemWayFeeFixedAmount) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                }
            }
        }
        // 充值授权
        ResponseVO<Boolean> rechargeAuthorize = siteRechargeApi.batchSave(operator, siteCode, BeanUtil.copyToList(siteDeposit, SiteRechargeBatchReqVO.class), siteName, handicapMode);
        if (rechargeAuthorize.isOk() && !rechargeAuthorize.getData() || !rechargeAuthorize.isOk()) {
            throw new BaowangDefaultException(ResultCode.UPDATE_SITE_DEPOSIT_ERROR);
        }
        return ResponseVO.success();
    }

    private ResponseVO<Boolean> updateSiteVenue(String siteCode, List<SiteVenueAuthorizeVO> siteVenue, String operator, String siteName) {
        List<SiteVenueVO> siteVenueVOS = BeanUtil.copyToList(siteVenue, SiteVenueVO.class);
        if (CollectionUtil.isNotEmpty(siteVenueVOS)) {
            siteVenueVOS.forEach(item -> {
                item.setOperator(operator);
            });
        }
        // 设置场馆手续费，游戏授权
        ResponseVO<Boolean> venueAuthorize = playVenueInfoApi.addSiteVenue(siteCode, siteVenueVOS, siteName);
        if (venueAuthorize.isOk() && !venueAuthorize.getData() || !venueAuthorize.isOk()) {
            ResultCode resultCode = ResultCode.of(venueAuthorize.getCode());
            throw new BaowangDefaultException(resultCode);
        }
        return ResponseVO.success();
    }

    /**
     * 修改站点配置信息
     *
     * @param po           po
     * @param siteConfigVO 皮肤配置等
     * @param operator     操作人
     * @return void
     */
    private ResponseVO<Boolean> updateSiteConfig(SitePO po, SiteConfigVO siteConfigVO, String operator) {
        if (ObjectUtils.isNotEmpty(siteConfigVO)) {
            // 查询皮肤释放被禁用
            if (StringUtils.isNotEmpty(siteConfigVO.getSkin())) {
                SkinResVO skinResVO = skinInfoService.querySkinOne(siteConfigVO.getSkin());
                if (Objects.nonNull(skinResVO) && skinResVO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
                    return ResponseVO.fail(ResultCode.SKIN_DISABLED);
                }
            }
            //站点配置操作对象 by mufan
            List<SiteInfoChangeBodyVO> data = new ArrayList<>();
            SiteInfoChangeBodyVO basicChangeSiteInfo = new SiteInfoChangeBodyVO();
            SiteConfigVO siteConfigchangeVO = new SiteConfigVO();

            BeanUtils.copyProperties(po, siteConfigchangeVO);
            List<String> iteActivityTemplates = systemActivityTemplateApi.querySiteActivityTemplate(po.getSiteCode(), po.getHandicapMode()).getData().stream()    // 1. 获取流 (Stream<User>)
                    .filter(e -> e.getBindStatus() == 1).map(SiteActivityTemplateVO::getActivityTemplate)                  // 2. 映射转换 (Stream<User> -> Stream<String>)
                    .collect(Collectors.toList());
            siteConfigchangeVO.setCheckActivityTemplate(iteActivityTemplates);
            basicChangeSiteInfo.setChangeBeforeObj(siteConfigchangeVO);
            basicChangeSiteInfo.setChangeAfterObj(siteConfigVO);
            basicChangeSiteInfo.setChangeType(SiteChangeTypeEnum.BaseConfig.getname());
            basicChangeSiteInfo.setColumnNameMap(JsonComparator.extractSchemas(SiteConfigVO.class));
            data.add(basicChangeSiteInfo);
            SiteInfoChangeRecordReqVO sd = siteInfoChangeRecordService.initSiteInfoChangeRecordReqVO(po.getSiteCode(), po.getSiteName(), SiteOptionModelNameEnum.site.getname(),
                    CurrReqUtils.getReqIp(), data, SiteOptionTypeEnum.DataUpdate.getCode(),
                    SiteOptionStatusEnum.success.getCode(), CurrReqUtils.getAccount());
            siteInfoChangeRecordService.addSiteInfoChangeRequestVO(sd);
            //end by mufan

            po.setBkName(siteConfigVO.getBkName());
            po.setSkin(siteConfigVO.getSkin());
            po.setLongLogo(siteConfigVO.getLongLogo());
            po.setShortLogo(siteConfigVO.getShortLogo());
//            po.setBlackLongLogo(siteConfigVO.getBlackLongLogo());
//            po.setBlackShortLogo(siteConfigVO.getBlackShortLogo());
            po.setUpdater(operator);
            po.setUpdatedTime(System.currentTimeMillis());
            this.updateById(po);
            //批量添加站点和活动模版
            SiteActivityTemplateSaveVO siteActivityTemplateSaveVO = new SiteActivityTemplateSaveVO();
            siteActivityTemplateSaveVO.setCheckActivityTemplate(siteConfigVO.getCheckActivityTemplate());
            siteActivityTemplateSaveVO.setSiteCode(po.getSiteCode());
            siteActivityTemplateSaveVO.setOperator(operator);
            systemActivityTemplateApi.batchBindAndUnBindActivityTemplate(siteActivityTemplateSaveVO);
            //批量添加站点和活动模版end by mufan
        }
        return ResponseVO.success();
    }

    /**
     * 编辑站点基础信息
     *
     * @param po        当前数据库po
     * @param siteBasic 基础信息
     * @param operator  操作人
     * @return void
     */
    private ResponseVO<Boolean> updateSiteBasic(SitePO po, SiteBasicVO siteBasic, String operator) {
        if (ObjectUtils.isNotEmpty(siteBasic)) {
            //基础站点字段原始内容补充与Schema中文描述原始信息 by mufan
            List<SiteInfoChangeBodyVO> data = new ArrayList<>();
            SiteInfoChangeBodyVO basicChangeSiteInfo = new SiteInfoChangeBodyVO();
            SiteBasicVO siteBefore = new SiteBasicVO();
            BeanUtils.copyProperties(po, siteBefore);
            siteBefore.setSiteType(po.getSiteType() + "");
            siteBefore.setAllowIps(siteAdminService.getAdminByUserName(po.getSiteAdminAccount(), po.getSiteCode()).getAllowIps());
            //end
            po.setSiteName(siteBasic.getSiteName());
            po.setSiteType(Integer.parseInt(siteBasic.getSiteType()));
            po.setCompany(siteBasic.getCompany());
            po.setSitePrefix(siteBasic.getSitePrefix());
            po.setSiteAdminAccount(siteBasic.getSiteAdminAccount());
            po.setCommissionPlan(siteBasic.getCommissionPlan());
            po.setRemark(siteBasic.getRemark());
            po.setStatus(EnableStatusEnum.ENABLE.getCode());
            po.setTimezone(siteBasic.getTimezone());
            po.setUpdater(operator);

            //添加保证金状态 编辑
            po.setGuaranTeeFlag(siteBasic.getGuaranTeeFlag());
//            po.setHandicapMode(siteBasic.getHandicapMode());
            SiteSecurityBalanceInitReqVO sitInitReqVO = new SiteSecurityBalanceInitReqVO();
            sitInitReqVO.setSiteCode(po.getSiteCode());
            sitInitReqVO.setSiteName(po.getSiteName());
            sitInitReqVO.setCompany(po.getCompany());
            sitInitReqVO.setSiteType(po.getSiteType());
            sitInitReqVO.setSecurityStatus(siteBasic.getGuaranTeeFlag());
            sitInitReqVO.setOperatorUser(operator);
            ResponseVO<Void> responseVO = siteSecurityBalanceApi.init(sitInitReqVO);
            if (!responseVO.isOk()) {
                throw new BaowangDefaultException(responseVO.getMessage());
            }
            //添加保证金状态 编辑 end

            po.setUpdatedTime(System.currentTimeMillis());
            //获取修改前内容
            List<String> langlist = languageManagerService.getSiteLanguageDownBox(po.getSiteCode()).getData().stream().
                    filter(x -> x.getIsChecked() == 1).map(SiteLanguageVO::getCode).collect(Collectors.toList());
            siteBefore.setLanguage(langlist);
            List<String> crulist = siteCurrencyInfoApi.getSiteCurrencyDownBox(po.getSiteCode()).getData().stream().
                    filter(x -> x.getIsChecked() == 1).map(SiteCurrencyDownBoxVO::getCode).collect(Collectors.toList());
            siteBefore.setCurrency(crulist);
            basicChangeSiteInfo.setChangeBeforeObj(siteInfoChangeRecordService.getSiteBase(siteBefore));
            basicChangeSiteInfo.setChangeAfterObj(siteInfoChangeRecordService.getSiteBase(siteBasic));
            basicChangeSiteInfo.setChangeType(SiteChangeTypeEnum.Baseinfo.getname());
            basicChangeSiteInfo.setColumnNameMap(JsonComparator.extractSchemas(SiteBasicVO.class));
            data.add(basicChangeSiteInfo);
            SiteInfoChangeRecordReqVO sd = siteInfoChangeRecordService.initSiteInfoChangeRecordReqVO(po.getSiteCode(), siteBasic.getSiteName(), SiteOptionModelNameEnum.site.getname(),
                    CurrReqUtils.getReqIp(), data, SiteOptionTypeEnum.DataUpdate.getCode(),
                    SiteOptionStatusEnum.success.getCode(), CurrReqUtils.getAccount());
            siteInfoChangeRecordService.addSiteInfoChangeRequestVO(sd);
            //end by mufan
            this.updateById(po);
            // 编辑语言
            languageManagerService.add(LanguageManagerAddVO.builder().siteCode(po.getSiteCode())
                    .codeList(siteBasic.getLanguage()).build());
            //编辑币种
            siteCurrencyInfoApi.init(SiteCurrencyInitReqVO.builder().siteCode(po.getSiteCode())
                    .operatorUserNo(operator).currencyCodeLists(siteBasic.getCurrency()).build()).isOk();
            //编辑白名单
            String allowIps = siteBasic.getAllowIps();
            //校验ip格式
            IPUtil.validateIPs(allowIps);
            LambdaUpdateWrapper<BusinessAdminPO> upd = Wrappers.lambdaUpdate();
            upd.eq(BusinessAdminPO::getSiteCode, po.getSiteCode()).eq(BusinessAdminPO::getUserName, siteBasic.getSiteAdminAccount())
                    .set(BusinessAdminPO::getAllowIps, allowIps);
            siteAdminService.update(upd);
            //初始化站点汇率 by mufan
            RateInitRequestVO vo = new RateInitRequestVO();
            vo.setSiteCode(po.getSiteCode());
            vo.setCurrencyCodeList(siteBasic.getCurrency());
            currencyRateConfigApi.init(vo);
            //初始化冲提款
            vipRankApi.initUserWithdrawConfig(po.getSiteCode(), siteBasic.getCurrency(), siteBasic.getHandicapMode());

            //vip大陆初始化
            if (SiteHandicapModeEnum.China.getCode().equals(siteBasic.getHandicapMode())) {
                siteVipOptionApi.initVIP(po.getSiteCode(), siteBasic.getCurrency());
            }
            RedisUtil.setValue(CacheConstants.SITE_HANDICAPMODE.concat(po.getSiteCode()), siteBasic.getHandicapMode());
        }
        return ResponseVO.success();
    }


    private SitePO processSiteData(SiteAddVO siteAddVO, String pwd, String siteCode) {
        try {
            // 先保存基础信息
            // 基础信息
            SiteBasicVO basicVO = siteAddVO.getSiteBasic();
            // 站点信息
            SiteConfigVO siteConfigVO = siteAddVO.getSiteConfig();
            LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
            query.eq(SitePO::getSiteCode, siteCode);
            SitePO sitePO = siteRepository.selectOne(query);
            //是否创建定时任务(编辑站点不需要再次创建定时任务)
            boolean isCreateJob = false;
            if (sitePO == null) {
                sitePO = new SitePO();
                isCreateJob = true;
            }
            if (ObjectUtils.isNotEmpty(basicVO)) {
                sitePO.setSiteName(basicVO.getSiteName());
                sitePO.setSiteCode(siteCode);
                // sitePO.setSiteModel(Integer.parseInt(basicVO.getSiteModel()));
                sitePO.setSiteType(Integer.parseInt(basicVO.getSiteType()));
                sitePO.setCompany(basicVO.getCompany());
                sitePO.setSitePrefix(basicVO.getSitePrefix());
                sitePO.setSiteAdminAccount(basicVO.getSiteAdminAccount());
                sitePO.setCommissionPlan(basicVO.getCommissionPlan());
                sitePO.setRemark(basicVO.getRemark());
                sitePO.setStatus(EnableStatusEnum.ENABLE.getCode());
                sitePO.setTimezone(basicVO.getTimezone());
                sitePO.setGuaranTeeFlag(basicVO.getGuaranTeeFlag());
                sitePO.setHandicapMode(basicVO.getHandicapMode());
            }
            //基础站点字段原始内容补充与Schema中文描述原始信息
            List<SiteInfoChangeBodyVO> data = new ArrayList<>();
            SiteInfoChangeBodyVO basicChangeSiteInfo = new SiteInfoChangeBodyVO();
            basicChangeSiteInfo.setChangeAfterObj(siteInfoChangeRecordService.getSiteBase(basicVO));
            basicChangeSiteInfo.setChangeType(SiteChangeTypeEnum.Baseinfo.getname());
            basicChangeSiteInfo.setColumnNameMap(JsonComparator.extractSchemas(SiteBasicVO.class));
            data.add(basicChangeSiteInfo);
            //站点配置操作对象
            SiteInfoChangeBodyVO basicChangeSiteInfoConfig = new SiteInfoChangeBodyVO();
            basicChangeSiteInfoConfig.setChangeAfterObj(siteConfigVO);
            basicChangeSiteInfoConfig.setChangeType(SiteChangeTypeEnum.BaseConfig.getname());
            basicChangeSiteInfoConfig.setColumnNameMap(JsonComparator.extractSchemas(SiteConfigVO.class));
            data.add(basicChangeSiteInfoConfig);

            SiteInfoChangeRecordReqVO sd = siteInfoChangeRecordService.initSiteInfoChangeRecordReqVO(sitePO.getSiteCode(), basicVO.getSiteName(), SiteOptionModelNameEnum.site.getname(),
                    CurrReqUtils.getReqIp(), data, SiteOptionTypeEnum.DataInsert.getCode(),
                    SiteOptionStatusEnum.success.getCode(), CurrReqUtils.getAccount());
            siteInfoChangeRecordService.addSiteInfoChangeRequestVO(sd);
            if (ObjectUtils.isNotEmpty(siteConfigVO)) {
                sitePO.setBkName(siteConfigVO.getBkName());
                sitePO.setSkin(siteConfigVO.getSkin());
                sitePO.setLongLogo(siteConfigVO.getLongLogo());
                sitePO.setShortLogo(siteConfigVO.getShortLogo());
//                sitePO.setBlackShortLogo(siteConfigVO.getBlackShortLogo());
//                sitePO.setBlackLongLogo(siteConfigVO.getBlackLongLogo());
            }
            if (StringUtils.isBlank(sitePO.getId())) {
                sitePO.setCreator(siteAddVO.getCreator());
                sitePO.setCreatedTime(System.currentTimeMillis());
            }
            if (StringUtils.isBlank(sitePO.getPlatCurrencyCode())) {
                sitePO.setPlatCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                sitePO.setPlatCurrencyName(CommonConstant.PLAT_CURRENCY_NAME);
                sitePO.setPlatCurrencySymbol(Character.toString(CommonConstant.PLAT_FORM_SYMBOL));
                //添加站点平台币缓存
                RedisUtil.setLocalCachedMap(CacheConstants.KEY_SITE_PLAT_CURRENCY, siteCode, CommonConstant.PLAT_CURRENCY_NAME);
                RedisUtil.setLocalCachedMap(CacheConstants.KEY_SITE_PLAT_CURRENCY_SYMBOL, siteCode, sitePO.getPlatCurrencySymbol());
            }
            sitePO.setUpdater(siteAddVO.getCreator());
            sitePO.setUpdatedTime(System.currentTimeMillis());
            SiteSecurityBalanceInitReqVO sitInitReqVO = new SiteSecurityBalanceInitReqVO();
            sitInitReqVO.setSiteCode(sitePO.getSiteCode());
            sitInitReqVO.setSiteName(sitePO.getSiteName());
            sitInitReqVO.setCompany(sitePO.getCompany());
            sitInitReqVO.setSiteType(sitePO.getSiteType());
            sitInitReqVO.setSecurityStatus(0);
            sitInitReqVO.setOperatorUser(siteAddVO.getCreator());
            siteSecurityBalanceApi.init(sitInitReqVO);
            this.saveOrUpdate(sitePO);

            //批量添加站点和活动模版
            if (CollUtil.isNotEmpty(siteConfigVO.getCheckActivityTemplate())) {
                SiteActivityTemplateSaveVO siteActivityTemplateSaveVO = new SiteActivityTemplateSaveVO();
                siteActivityTemplateSaveVO.setCheckActivityTemplate(siteConfigVO.getCheckActivityTemplate());
                siteActivityTemplateSaveVO.setSiteCode(sitePO.getSiteCode());
                siteActivityTemplateSaveVO.setOperator(siteAddVO.getCreator());
                systemActivityTemplateApi.batchBindAndUnBindActivityTemplate(siteActivityTemplateSaveVO);
            }
            //批量添加站点和活动模版end by mufan

            //初始化站点汇率 by mufan
            RateInitRequestVO vo = new RateInitRequestVO();
            vo.setSiteCode(sitePO.getSiteCode());
            vo.setCurrencyCodeList(basicVO.getCurrency());
            currencyRateConfigApi.init(vo);
            //初始化站点汇率 end by mufan

            List<SiteVenueVO> siteVenue = ConvertUtil.convertListToList(siteAddVO.getSiteVenue(),
                    new SiteVenueVO());
            if (CollectionUtil.isNotEmpty(siteVenue)) {
                siteVenue.forEach(item -> {
                    item.setOperator(siteAddVO.getCreator());
                });
            }
            if (isCreateJob) {
                //新增站点才初始化定时任务
                //创建默认定时任务三个 2024-10-18
                createDefaultJob(sitePO.getTimezone(), sitePO.getSiteCode());
                //新增的时候,初始化一次代理提款设置
                agentWithdrawConfigApi.syncAgentWithdrawConfig(siteCode);
            }
            // 设置场馆手续费，游戏授权
            ResponseVO<Boolean> venueAuthorize = playVenueInfoApi.addSiteVenue(sitePO.getSiteCode(),
                    siteVenue, basicVO.getSiteName());
            if (venueAuthorize.isOk() && !venueAuthorize.getData() || !venueAuthorize.isOk()) {
                throw new BaowangDefaultException(ResultCode.UPDATE_SITE_VENUE_ERROR);
            }
            List<SiteRechargeBatchReqVO> siteRecharge = ConvertUtil.convertListToList(siteAddVO.getSiteDeposit(),
                    new SiteRechargeBatchReqVO());
            // 充值授权
            ResponseVO<Boolean> rechargeAuthorize = siteRechargeApi.batchSave(siteAddVO.getCreator(), sitePO.getSiteCode(),
                    siteRecharge, basicVO.getSiteName(), sitePO.getHandicapMode());
            if (rechargeAuthorize.isOk() && !rechargeAuthorize.getData() || !rechargeAuthorize.isOk()) {
                throw new BaowangDefaultException(ResultCode.UPDATE_SITE_DEPOSIT_ERROR);
            }
            List<SiteWithdrawBatchRequsetVO> siteWithdrawReq = ConvertUtil.convertListToList(siteAddVO.getSiteWithdraw(),
                    new SiteWithdrawBatchRequsetVO());
            // 提款授权
            ResponseVO<Boolean> withdrawAuthorize = siteWithdrawApi.batchSave(siteAddVO.getCreator(), sitePO.getSiteCode(),
                    siteWithdrawReq, basicVO.getSiteName());

            if (withdrawAuthorize.isOk() && !withdrawAuthorize.getData() || !withdrawAuthorize.isOk()) {
                throw new BaowangDefaultException(ResultCode.UPDATE_SITE_WITHDRAW_ERROR);
            }
            ChannelSiteLinkVO smsSiteLinkVO = new ChannelSiteLinkVO();

            smsSiteLinkVO.setSiteCode(sitePO.getSiteCode());
            smsSiteLinkVO.setChannelCodeList(siteAddVO.getSiteSms().getSmsId());
            //新增创建人oneId
            smsSiteLinkVO.setCreator(siteAddVO.getCreator());
            // 短信授权
            ResponseVO<Boolean> smsAuthorize = channelSiteLinkApi.smsSiteAddBatch(siteAddVO.getSiteCode(), smsSiteLinkVO, basicVO.getSiteName());

            if (smsAuthorize.isOk() && !smsAuthorize.getData() || !smsAuthorize.isOk()) {
                throw new BaowangDefaultException(ResultCode.UPDATE_SITE_MESSAGE_ERROR);
            }
            ChannelSiteLinkVO mailSiteLinkVO = new ChannelSiteLinkVO();
            mailSiteLinkVO.setSiteCode(sitePO.getSiteCode());
            mailSiteLinkVO.setChannelCodeList(siteAddVO.getSiteEmail().getEmailId());
            mailSiteLinkVO.setCreator(siteAddVO.getCreator());
            // 邮箱授权
            ResponseVO<Boolean> emailAuthorize = channelSiteLinkApi.mailSiteAddBatch(sitePO.getSiteCode(), mailSiteLinkVO, basicVO.getSiteName());

            if (emailAuthorize.isOk() && !emailAuthorize.getData() || !emailAuthorize.isOk()) {
                throw new BaowangDefaultException(ResultCode.UPDATE_SITE_EMAIL_ERROR);
            }
            // 客服授权
            boolean customerAuthorize = siteCustomerService.addSiteCustomer(sitePO.getSiteCode(),
                    siteAddVO.getSiteCustomer().getChannelCode(), basicVO.getSiteName());
            // 初始化站点币种
            boolean currencyAuthorize = false;
            if (ObjectUtils.isNotEmpty(basicVO.getCurrency())) {
                currencyAuthorize = siteCurrencyInfoApi.init(SiteCurrencyInitReqVO.builder().siteCode(siteCode)
                        .operatorUserNo(siteAddVO.getCreator()).currencyCodeLists(basicVO.getCurrency()).build()).isOk();
            }

            if (!currencyAuthorize) {
                throw new BaowangDefaultException(ResultCode.SITE_CURRENCY_INIT_ERROR);
            }
            // 初始化语言
            languageManagerService.add(LanguageManagerAddVO.builder().siteCode(siteCode)
                    .codeList(basicVO.getLanguage()).build());

            //初始化vip等级段位
            List<String> currency = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(basicVO.getCurrency())) {
                currency = basicVO.getCurrency();
            }
            //初始化vip等级,段位,勋章,会员提款
            if (SiteHandicapModeEnum.China.getCode().equals(sitePO.getHandicapMode())) {
                siteVipOptionApi.initVIP(sitePO.getSiteCode(), currency);
                siteVipOptionApi.initVIPGrade(sitePO.getSiteCode());
            } else {
                vipRankApi.batchVIPRank(sitePO.getSiteCode(), currency, basicVO.getHandicapMode());
            }
            RedisUtil.setValue(CacheConstants.SITE_HANDICAPMODE.concat(sitePO.getSiteCode()), sitePO.getHandicapMode());

            //代理端菜单初始化
            BaseReqVO baseReqVO = new BaseReqVO();
            baseReqVO.setAdminId(siteAddVO.getCreator());
            baseReqVO.setAdminName(siteAddVO.getCreator());
            baseReqVO.setSiteCode(siteCode);
            buttonEntranceApi.init(baseReqVO);
            //站点初始化勋章
            setMedal(siteCode);
            //初始化站点支付商，赞助商
            setPartner(siteCode);
            setVendor(siteCode);
            //初始化站点区号数据
            setAreaCode(siteCode);
            // 添加管理员账号
            setAdmin(sitePO, pwd, basicVO.getAllowIps());
            SiteVirtualWalletVO walletVO = new SiteVirtualWalletVO();
            walletVO.setSiteCode(siteCode);
            walletVO.setSiteName(siteAddVO.getSiteBasic().getSiteName());
            walletVO.setCompany(siteAddVO.getSiteBasic().getCompany());
            walletVO.setOperator(siteAddVO.getCreator());
            walletVO.setUpdateTime(System.currentTimeMillis());
            walletApi.addSiteBaseInfo(walletVO);

            //目前只有国际盘会需要 原声体育跟原声彩票国内盘不初始化原声体育跟彩票的一级分类
            if (SiteHandicapModeEnum.Internacional.getCode().equals(sitePO.getHandicapMode())) {
                gameClassInfoApi.initGameOneClassInfo(siteCode);
            }
            //初始化字典配置
            dictConfigService.initSiteDictConfig(siteCode, siteAddVO.getCreator());
            //初始化缓存数据
            setSiteRedis(siteCode);
            //初始化pwa文件到minio
            initPwaFile(siteCode);
            //初始化反水配置 - 废掉
//            SiteRebateInitVO reqVO = SiteRebateInitVO.builder().siteCode(siteCode).capMode(sitePO.getHandicapMode()).build();
//            siteRebateApi.initRebateList(reqVO);
            return sitePO;
        } catch (Exception e) {
            log.error("save or update site have error, siteCode: {}", siteCode, e);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
    }

    private void initPwaFile(String siteCode) {
        try {
            log.info("开始创建当前站点pwa配置文件至minio,siteCode:{}", siteCode);
            LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
            query.eq(SitePO::getSiteCode, siteCode);
            SitePO po = this.getOne(query);
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
                String minioDomain = minioFileService.getMinioDomain();

                List<PwaVO.Icon> arr = new ArrayList<>();
                PwaVO.Icon shortIcon = new PwaVO.Icon();
                //shortIcon.setSrc("https://oss.playesoversea.store/baowang/1ef69030788a4c9f844109515ff82b9f.png");
                shortIcon.setSizes("192x192");
                shortIcon.setType("image/png");

                PwaVO.Icon longIcon = new PwaVO.Icon();
                //longIcon.setSrc("https://oss.playesoversea.store/baowang/650a604fcecf401783cba8bb672200fe.png");
                longIcon.setSizes("512x512");
                longIcon.setType("image/png");
                ResponseVO<i18nMessagesVO> resp = siteDownloadConfigService.getDownloadInfoBySiteCode(siteCode);
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

    /**
     * 初始化站点默认三个定时任务
     *
     * @param timezone 站点时区
     * @param siteCode 站点code
     */
    private void createDefaultJob(String timezone, String siteCode) {
        //初始化job
        String cronDay = CronUtil.generateCronByCurrentAndTargetTimeZone(timezone);
        log.info("当前站点code:{},生成每天定时任务表达式:{}", siteCode, cronDay);
        JobUpsertVO dayJob = new JobUpsertVO()
                .setCron(cronDay)
                .setName(siteCode + CommonConstant.CENTER_LINE + CommonConstant.MEDAL_DAY_JOB_DESC + CommonConstant.CENTER_LINE + timezone)
                .setExecutorParam(siteCode)
                .setHandlerName(SiteJobHandlerConstant.SITE_MEDAL_DAY_JOB);

        String cronWeak = CronUtil.generateWeeklyCronInUTC5(timezone);
        log.info("当前站点code:{},生成每周定时任务表达式:{}", siteCode, cronWeak);
        JobUpsertVO weekJob = new JobUpsertVO()
                .setCron(cronWeak)
                .setName(siteCode + CommonConstant.CENTER_LINE + CommonConstant.MEDAL_WEAK_JOB_DESC + CommonConstant.CENTER_LINE + timezone)
                .setExecutorParam(siteCode)
                .setHandlerName(SiteJobHandlerConstant.SITE_MEDAL_WEEK_JOB);

        String cronMonth = CronUtil.generateMonthlyCron(timezone);
        log.info("当前站点code:{},生成每月定时任务表达式:{}", siteCode, cronMonth);
        JobUpsertVO monthJob = new JobUpsertVO()
                .setCron(cronMonth)
                .setName(siteCode + CommonConstant.CENTER_LINE + CommonConstant.MEDAL_MONTH_JOB_DESC + CommonConstant.CENTER_LINE + timezone)
                .setExecutorParam(siteCode)
                .setHandlerName(SiteJobHandlerConstant.SITE_MEDAL_MONTH_JOB);

        ArrayList<JobUpsertVO> param = new ArrayList<>();
        param.add(dayJob);
        param.add(weekJob);
        param.add(monthJob);
        log.info("创建job,当前站点:{},定时任务:{}", siteCode, JSON.toJSONString(param));
        ResponseVO<Map<String, String>> responseVO = jobInfoApi.create(param);
        log.info("当前站点:{},创建站点定时任务结果:{}", siteCode, responseVO.getCode());
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.XXL_JOB_API_ERROR);
        }
    }


    /**
     * 初始化勋章
     *
     * @param siteCode 站点编号
     */
    private void setMedal(String siteCode) {
        log.info("站点:{}开始初始化勋章", siteCode);
        siteMedalInfoApi.init(siteCode);
    }

    /**
     * 初始化区号
     *
     * @param siteCode 站点
     */
    private void setAreaCode(String siteCode) {
        siteAreaManageApi.initSiteArea(siteCode);
    }

    /**
     * 初始化站点支付商信息
     *
     * @param siteCode 站点code
     */
    private void setVendor(String siteCode) {
        sitePaymentVendorService.setVendor(siteCode);
    }

    /**
     * 初始化站点赞助商信息
     *
     * @param siteCode
     */
    private void setPartner(String siteCode) {
        sitePartnerService.setPartner(siteCode);
    }

    /**
     * 更新缓存数据
     *
     * @param siteCode
     */
    private void setSiteRedis(final String siteCode) {
//        RedisUtil.deleteKey(RedisConstants.SITE_INFO + siteCode);
        SitePO po = this.getOne(new LambdaQueryWrapper<SitePO>().eq(SitePO::getSiteCode, siteCode));
        SiteVO vo = new SiteVO();
        if (po != null) {
            BeanUtils.copyProperties(po, vo);
            String shortLogo = vo.getShortLogo();
            String longLogo = vo.getLongLogo();
            String blackShortLogo = vo.getBlackShortLogo();
            String blackLongLogo = vo.getBlackLongLogo();
            String minioDomain = minioFileService.getMinioDomain();
            if (StringUtils.isNotBlank(shortLogo)) {
                vo.setShortLogoImage(minioDomain + "/" + shortLogo);
            }
            if (StringUtils.isNotBlank(longLogo)) {
                vo.setLongLogoImage(minioDomain + "/" + longLogo);
            }
            if (StringUtils.isNotBlank(blackShortLogo)) {
                vo.setBlackShortLogoImage(minioDomain + "/" + blackShortLogo);
            }
            if (StringUtils.isNotBlank(blackLongLogo)) {
                vo.setBlackLongLogoImage(minioDomain + "/" + blackLongLogo);
            }
            //封装语言信息
            LambdaQueryWrapper<LanguageManagerPO> langQuery = Wrappers.lambdaQuery();
            langQuery.eq(LanguageManagerPO::getSiteCode, siteCode);
            List<LanguageManagerPO> languageManagerPOS = languageManagerService.list(langQuery);
            if (CollectionUtil.isNotEmpty(languageManagerPOS)) {
                List<CodeValueNoI18VO> languageList = languageManagerPOS.stream()
                        .map(item -> new CodeValueNoI18VO(item.getCode(), item.getName()))
                        .toList();
                vo.setLanguageList(languageList);
                vo.setLanguageManagerVOS(BeanUtil.copyToList(languageManagerPOS, LanguageManagerVO.class));
            }
            //封装币种信息
            List<SiteCurrencyInfoRespVO> currencyCodeList = siteCurrencyInfoApi.getBySiteCode(po.getSiteCode());
            if (CollectionUtil.isNotEmpty(currencyCodeList)) {
                //获取到当前站点全部币种信息,获取i18message到vo中
                String i18MessageCodes = currencyCodeList.stream()
                        .map(SiteCurrencyInfoRespVO::getCurrencyNameI18)
                        .collect(Collectors.joining(","));
                vo.setCurrency(i18MessageCodes);

                //获取到当前站点全部币种信息,获取i18message到vo中
                String currencyCodes = currencyCodeList.stream()
                        .map(SiteCurrencyInfoRespVO::getCurrencyCode)
                        .collect(Collectors.joining(","));
                vo.setCurrencyCodes(currencyCodes);
            }
            //封装游戏场馆信息
            List<SiteVenueQueryVO> siteVenueInfoVOS = playVenueInfoApi.querySiteVenueBySiteCode(siteCode);
            vo.setSiteVenueInfoVOS(BeanUtil.copyToList(siteVenueInfoVOS, com.cloud.baowang.system.api.vo.site.siteDetail.SiteVenueQueryVO.class));
        }
        RedisUtil.setValue(RedisConstants.SITE_INFO + siteCode, vo);
    }

    private void setAdmin(SitePO sitePO, String pwd, String allowIps) {
        LambdaQueryWrapper<BusinessAdminPO> query = Wrappers.lambdaQuery();
        query.eq(BusinessAdminPO::getSiteCode, sitePO.getSiteCode()).eq(BusinessAdminPO::getUserName, sitePO.getSiteAdminAccount());
        //先查询一下当前站点是否存在职员，存在说明是编辑入口，不做处理
        long count = siteAdminService.count(query);
        if (count > 0) {
            //修改一下白名单配置
            LambdaUpdateWrapper<BusinessAdminPO> upd = Wrappers.lambdaUpdate();
            upd.eq(BusinessAdminPO::getSiteCode, sitePO.getSiteCode()).eq(BusinessAdminPO::getUserName, sitePO.getSiteAdminAccount())
                    .set(BusinessAdminPO::getAllowIps, allowIps);
            siteAdminService.update(upd);
            return;
        }
        SiteAdminAddVO siteAdminAddVO = new SiteAdminAddVO();
        siteAdminAddVO.setUserName(sitePO.getSiteAdminAccount());
        siteAdminAddVO.setPassword(pwd);
        siteAdminAddVO.setConfirmPassword(pwd);
        siteAdminAddVO.setSiteCode(sitePO.getSiteCode());
        siteAdminAddVO.setCreator(CurrReqUtils.getAccount());
        siteAdminAddVO.setIsSuperAdmin(BigDecimal.ONE.toString());
        siteAdminAddVO.setAllowIps(allowIps);
        siteAdminService.addAdmin(siteAdminAddVO);
    }

    public Boolean updPlatCurrency(String siteCode, String platCurrencyName, String platCurrencySymbol, String platCurrencyIcon) {
        LambdaUpdateWrapper<SitePO> upd = Wrappers.lambdaUpdate();
        upd.set(SitePO::getPlatCurrencyName, platCurrencyName).set(SitePO::getPlatCurrencySymbol, platCurrencySymbol).set(SitePO::getPlatCurrencyIcon, platCurrencyIcon);
        if (this.update(upd)) {
            //同步清空所有站点的缓存数据
            List<SitePO> list = this.list();
            for (SitePO sitePO : list) {
                String poSiteCode = sitePO.getSiteCode();
                //汇率变更 不需要刷新缓存
                //setSiteRedis(poSiteCode);
                //添加站点平台币缓存
                RedisUtil.setLocalCachedMap(CacheConstants.KEY_SITE_PLAT_CURRENCY, poSiteCode, platCurrencyName);
                RedisUtil.setLocalCachedMap(CacheConstants.KEY_SITE_PLAT_CURRENCY_SYMBOL, poSiteCode, platCurrencySymbol);
                RedisUtil.setLocalCachedMap(CacheConstants.KEY_SITE_PLAT_CURRENCY_ICON, poSiteCode, platCurrencyIcon);
            }
        }
        return true;
    }

    public List<SiteVO> getSiteInfoByName(String siteName) {
        List<SitePO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SitePO::getSiteName, siteName)
                .list();
        return ConvertUtil.entityListToModelList(list, SiteVO.class);
    }

    public SiteVO getSiteInfoByCode(String siteCode) {
        SitePO sitePO = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SitePO::getSiteCode, siteCode)
                .one();
        return ConvertUtil.entityToModel(sitePO, SiteVO.class);
    }

    public List<SiteVO> getSiteInfoByTimezone(String timeZone) {
        List<SitePO> sitePOList = this.lambdaQuery().eq(SitePO::getTimezone, timeZone)
                .eq(SitePO::getStatus, EnableStatusEnum.ENABLE.getCode()).list();
        return ConvertUtil.entityListToModelList(sitePOList, SiteVO.class);
    }

    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public SiteVO getSiteDetail(String siteCode) {
        LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
        query.eq(SitePO::getSiteCode, siteCode);
        SitePO one = this.getOne(query);
        return BeanUtil.copyProperties(one, SiteVO.class);
    }


    public List<SiteVO> getSiteInfoSByCodes(List<String> totalSiteCodeList) {
        LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
        query.in(SitePO::getSiteCode, totalSiteCodeList);

        return BeanUtil.copyToList(this.list(query), SiteVO.class);
    }

    public ResponseVO updateSiteRebateStatus(Integer status) {
        String siteCode = CurrReqUtils.getSiteCode();
        LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
        query.eq(SitePO::getSiteCode, siteCode);
        SitePO po = this.getOne(query);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String operator = CurrReqUtils.getAccount();
        po.setUpdater(operator);
        po.setUpdatedTime(System.currentTimeMillis());
        po.setRebateStatus(status);
        this.updateById(po);
        log.info("编辑站点返水状态,当前站点编号:{},操作人:{},返回状态修改前:{},返回状态修改后:{}", siteCode, operator, po.getRebateStatus(), status);
        RedisUtil.deleteKey(RedisConstants.SITE_INFO + siteCode);
        return ResponseVO.success();
    }
}
