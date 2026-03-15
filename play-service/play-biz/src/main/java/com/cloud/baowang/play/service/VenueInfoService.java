package com.cloud.baowang.play.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.venue.VenueCurrencyTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenueJoinTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.api.vo.venue.siteDetail.SiteGameQueryVO;
import com.cloud.baowang.play.api.vo.venue.siteDetail.SiteVenueQueryVO;
import com.cloud.baowang.play.po.*;
import com.cloud.baowang.play.repositories.*;
import com.cloud.baowang.play.util.LobbyCateUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.change.SiteInfoChangeRecordApi;
import com.cloud.baowang.system.api.enums.*;
import com.cloud.baowang.system.api.vo.JsonDifferenceVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoRespVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeBodyVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordListReqVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class VenueInfoService extends ServiceImpl<VenueInfoRepository, VenueInfoPO> {

    private final SiteVenueRepository siteVenueRepository;

    private final SiteVenueService siteVenueService;

    private final GameInfoRepository gameInfoRepository;

    private final AdminGameInfoService adminGameInfoService;

    private final SiteGameRepository siteGameRepository;

    private final SiteGameService siteGameService;

    private final SystemParamApi systemParamApi;

    private final GameTwoCurrencySortService gameTwoCurrencySortService;

    private final I18nApi i18nApi;

    private final LanguageManagerApi languageManagerApi;

    private final SystemCurrencyInfoApi systemCurrencyInfoApi;

    private final SiteInfoChangeRecordApi siteInfoChangeRecordApi;

    private final SiteGameHotSortRepository siteGameHotSortRepository;

    private final GameTwoCurrencySortRepository gameTwoCurrencySortRepository;

    private final SiteApi siteApi;

    private final GameOneVenueRepository gameOneVenueRepository;

    private final SiteVenueConfigService siteVenueConfigService;

    public List<VenueInfoVO> getAdminVenueInfoByVenueCodeList(List<String> venueCodeList) {
        if (CollectionUtil.isEmpty(venueCodeList)) {
            return Lists.newArrayList();
        }
        VenueInfoRequestVO vo = VenueInfoRequestVO.builder().venueCodeList(venueCodeList).build();
        vo.setPageSize(-1);
        List<VenueInfoVO> voList = getAdminVenueInfoPage(vo).getRecords();
        if (CollectionUtil.isNotEmpty(voList)) {
            return voList;
        }
        return Lists.newArrayList();
    }

    /**
     * 获取 总控中 场馆师傅配置了这个币种,不区分站点
     *
     * @param venueCode 场馆
     * @param currency  币种
     * @return 场馆信息
     */
    public VenueInfoVO getAdminVenueInfoByVenueCode(String venueCode, String currency) {
        List<VenueInfoVO> list = getAdminVenueInfoByVenueCodeList(venueCode);
        if (StringUtils.isNotBlank(currency)) {
            list = list.stream().filter(x -> x.getPullCurrencyCodeList().contains(currency)).toList();
        }
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    /**
     * 查询站点的场馆配置信息
     *
     * @param siteCode  站点
     * @param venueCode 场馆
     * @param currency  比重
     * @return 场馆配置
     */
    public VenueInfoVO getSiteVenueInfoByVenueCode(String siteCode, String venueCode, String currency) {

        SiteVenueConfigVO siteVenuePOList = siteVenueService.getSiteVenueBySiteCodeAndVenueCode(siteCode, venueCode);

        if (ObjectUtil.isEmpty(siteVenuePOList)) {
            return null;
        }

        VenueInfoVO adminVenueInfoList = getAdminVenueInfoByVenueCode(venueCode, currency);

        if (ObjectUtil.isEmpty(adminVenueInfoList)) {
            return null;
        }
        adminVenueInfoList.setSiteLabelChangeType(siteVenuePOList.getSiteLabelChangeType());
        //这些配置信息获取站点的.
        adminVenueInfoList.setStatus(siteVenuePOList.getStatus());
        adminVenueInfoList.setMaintenanceStartTime(siteVenuePOList.getMaintenanceStartTime());
        adminVenueInfoList.setMaintenanceEndTime(siteVenuePOList.getMaintenanceEndTime());
        adminVenueInfoList.setRemark(siteVenuePOList.getRemark());
        adminVenueInfoList.setSmallIcon1I18nCode(siteVenuePOList.getSmallIcon1I18nCode());
        adminVenueInfoList.setSmallIcon2I18nCode(siteVenuePOList.getSmallIcon2I18nCode());
        adminVenueInfoList.setSmallIcon3I18nCode(siteVenuePOList.getSmallIcon3I18nCode());
        adminVenueInfoList.setSmallIcon4I18nCode(siteVenuePOList.getSmallIcon4I18nCode());
        adminVenueInfoList.setSmallIcon5I18nCode(siteVenuePOList.getSmallIcon5I18nCode());
        adminVenueInfoList.setSmallIcon6I18nCode(siteVenuePOList.getSmallIcon6I18nCode());
        adminVenueInfoList.setHtIconI18nCode(siteVenuePOList.getHtIconI18nCode());
        adminVenueInfoList.setH5IconI18nCode(siteVenuePOList.getH5IconI18nCode());
        adminVenueInfoList.setPcLogoCode(siteVenuePOList.getPcLogoCode());
        adminVenueInfoList.setPcIconI18nCode(siteVenuePOList.getPcIconI18nCode());
        adminVenueInfoList.setPcBackgroundCode(siteVenuePOList.getPcBackgroundCode());
        adminVenueInfoList.setVenueDescI18nCode(siteVenuePOList.getVenueDesc());
        adminVenueInfoList.setVenueNameI18nCode(siteVenuePOList.getVenueName());
        adminVenueInfoList.setMiddleIconI18nCode(siteVenuePOList.getMiddleIconI18nCode());
        return adminVenueInfoList;
    }

    /**
     * 根据 场馆平台+商户 获取到场馆信息
     *
     * @param platform 平台
     * @param merchant 商户
     * @return 场馆CODE
     */
    public VenueInfoVO venueInfoByPlatMerchant(String platform, String merchant) {
        String key = String.format(RedisConstants.VENUE_INFO_PLAT_MERCHANT, platform, merchant);
        VenueInfoVO venueInfoVO = RedisUtil.getValue(key);
        if (ObjectUtil.isNotEmpty(venueInfoVO)) {
            return venueInfoVO;
        }
        List<VenueInfoPO> venueInfoList = baseMapper.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenuePlatform, platform)
                .eq(VenueInfoPO::getMerchantNo, merchant)
        );

        if (CollectionUtil.isEmpty(venueInfoList)) {
            return null;
        }
        VenueInfoPO venueInfoPO = venueInfoList.get(0);
        VenueInfoVO resultVenueInfo = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO,resultVenueInfo);
        RedisUtil.setValue(key, resultVenueInfo, 5L, TimeUnit.MINUTES);
        return resultVenueInfo;
    }


    public VenueInfoVO venueInfoByPlatMerchantKey(String platform, String merchantKey) {
        String key = String.format(RedisConstants.VENUE_INFO_PLAT_MERCHANT_KEY, platform, merchantKey);
        VenueInfoVO venueInfoVO = RedisUtil.getValue(key);
        if (ObjectUtil.isNotEmpty(venueInfoVO)) {
            return venueInfoVO;
        }
        List<VenueInfoPO> venueInfoList = baseMapper.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenuePlatform, platform)
                .eq(VenueInfoPO::getMerchantKey, merchantKey)
        );

        if (CollectionUtil.isEmpty(venueInfoList)) {
            return null;
        }
        VenueInfoPO venueInfoPO = venueInfoList.get(0);
        VenueInfoVO resultVenueInfo = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO,resultVenueInfo);
        RedisUtil.setValue(key, resultVenueInfo, 5L, TimeUnit.MINUTES);
        return resultVenueInfo;
    }

    /**
     * 获取总控场馆信息
     */
    public List<VenueInfoVO> getAdminVenueInfoByVenueCodeList(String venueCode) {
        String key = String.format(RedisConstants.NEW_VENUE_INFO_LIST, venueCode);

        String jsonList = RedisUtil.getValue(key);
        if (!StringUtils.isBlank(jsonList)) {
            return JSON.parseArray(jsonList, VenueInfoVO.class);
        }

        List<VenueInfoPO> venueInfoList = baseMapper.selectList(Wrappers.lambdaQuery(VenueInfoPO.class).eq(
                VenueInfoPO::getVenueCode, venueCode)
        );


        List<VenueInfoPO> allVenue = baseMapper.selectList(null);

        Map<String, List<VenueInfoPO>> venueInfoMap = allVenue.stream().filter(x -> x.getVenueCurrencyType()
                        .equals(VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode()))
                .collect(Collectors.groupingBy(VenueInfoPO::getVenueCode));


        if (CollectionUtil.isNotEmpty(venueInfoList)) {
            List<VenueInfoVO> venueInfo = venueInfoList.stream().map(x -> {
                VenueInfoVO vo = new VenueInfoVO();
                BeanUtils.copyProperties(x, vo);
                vo.setVenueDescI18nCode(x.getVenueDesc());
                vo.setVenueName(x.getVenueName());
                if (x.getVenueCurrencyType().equals(VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode()) && ObjectUtil.isNotEmpty(x.getCurrencyCode())) {
                    List<VenueInfoPO> venueInfoPOList = venueInfoMap.get(x.getVenueCode());
                    if (CollectionUtil.isNotEmpty(venueInfoPOList)) {
                        List<String> currencyList = venueInfoPOList.stream().map(VenueInfoPO::getCurrencyCode).toList();
                        vo.setCurrencyCodeList(currencyList);
                    }
                } else {
                    vo.setCurrencyCodeList(Arrays.asList((x.getCurrencyCode().split(","))));
                }


                if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(x.getVenueCurrencyType())) {
                    //该字段针对单币种场馆单个币种集合
                    vo.setPullCurrencyCodeList(List.of(x.getCurrencyCode()));
                } else {
                    if (ObjectUtil.isNotEmpty(x.getCurrencyCode())) {
                        vo.setPullCurrencyCodeList(Arrays.asList((x.getCurrencyCode().split(","))));
                    }
                }
                return vo;
            }).toList();
            if (CollectionUtil.isNotEmpty(venueInfo)) {
                RedisUtil.setValue(key, JSON.toJSONString(venueInfo), 10L, TimeUnit.MINUTES);
            }
            return venueInfo;
        }
        return null;
    }


    /**
     * 查询平台的类型
     */
    public Integer getVenueTypeByCode(String venueCode) {
        String venueTypeKey = String.format(RedisConstants.VENUE_TYPE);
        Map<String, Integer> venueTypeMap = RedisUtil.getValue(venueTypeKey);
        if (ObjectUtil.isEmpty(venueTypeMap)) {
            List<VenueInfoPO> venueInfoList = baseMapper.selectList(null);
            if (CollectionUtil.isEmpty(venueInfoList)) {
                return null;
            }
            venueTypeMap = venueInfoList.stream()
                    .filter(vo -> vo.getVenueCode() != null && vo.getVenueType() != null)
                    .collect(Collectors.toMap(
                            VenueInfoPO::getVenueCode,
                            VenueInfoPO::getVenueType,
                            (first, second) -> first // 如果 venueCode 重复，取第一个
                    ));
            RedisUtil.setValue(venueTypeKey, venueTypeMap, 60L, TimeUnit.MINUTES);
        }
        return venueTypeMap.get(venueCode);
    }

    public Map<String, String> getAdminVenueNameMap(){
        List<VenueInfoPO> venueInfoPOList = baseMapper.selectList(null);
        for (VenueInfoPO item : venueInfoPOList){
            if(ObjectUtil.isNotEmpty(item.getVenueName())){
                item.setVenueName(I18nMessageUtil.getI18NMessageInAdvice(item.getVenueName()));
            }
        }
        return venueInfoPOList.stream()
                .collect(Collectors.toMap(
                        VenueInfoPO::getVenueCode,
                        v -> Optional.ofNullable(v.getVenueName()).orElse(""),
                        (first, second) -> first
                ));
    }

    public Map<String, String> getSiteVenueNameMap() {
        List<SiteVenueConfigPO> siteVenueConfigPOS = siteVenueConfigService.getBaseMapper()
                .selectList(Wrappers.lambdaQuery(SiteVenueConfigPO.class)
                        .eq(SiteVenueConfigPO::getSiteCode, CurrReqUtils.getSiteCode()));
        Map<String, String> map = Maps.newHashMap();
        for (SiteVenueConfigPO item : siteVenueConfigPOS) {
            String venueName = item.getVenueName();
            if(ObjectUtil.isEmpty(venueName)){
                venueName = "";
            }else{
                venueName = I18nMessageUtil.getI18NMessageInAdvice(venueName);
            }
            map.put(item.getVenueCode(), venueName);
        }
        return map;
    }

    public List<VenueInfoVO> getSiteVenueInfoList() {
        String key = RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_VENUE);
        if (ObjectUtil.isNotEmpty(CurrReqUtils.getSiteCode()) &&
                !CommonConstant.ADMIN_CENTER_SITE_CODE.equals(CurrReqUtils.getSiteCode())) {
            List<VenueInfoVO> set = RedisUtil.getValue(key);
            if (CollectionUtil.isNotEmpty(set)) {
                return new ArrayList<>(set);
            }
        }

        VenueInfoRequestVO requestVO = VenueInfoRequestVO.builder()
                .build();
        requestVO.setPageNumber(1);
        requestVO.setPageSize(-1);
        List<VenueInfoVO> list = getSiteVenueInfoPage(requestVO).getRecords();

        if (CollectionUtil.isNotEmpty(list)) {
            RedisUtil.setValue(key, list, 5L, TimeUnit.MINUTES);
        }
        return list;
    }

    @Deprecated
    public List<VenueInfoVO> venueInfoListByParam(VenueInfoRequestVO venueInfoQueryVO) {
        venueInfoQueryVO.setPageSize(-1);
        List<VenueInfoVO> list = getVenueInfoPage(venueInfoQueryVO).getRecords();

        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        list.forEach(x -> {
            x.setBetUrl(null);
            x.setApiUrl(null);
            x.setGameUrl(null);
            x.setAesKey(null);
            x.setMerchantNo(null);
            x.setBetKey(null);
            x.setMerchantKey(null);
            if (CollectionUtil.isNotEmpty(x.getVenueInfoCurrencyList())) {
                x.getVenueInfoCurrencyList().forEach(d -> {
                    d.setAesKey(null);
                    d.setMerchantNo(null);
                    d.setMerchantKey(null);
                });
            }
        });


        return list.stream()
                .collect(Collectors.toMap(
                        VenueInfoVO::getVenueCode,
                        v -> v,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(VenueInfoVO::getId))
                .toList();
    }


    @Deprecated
    public Page<VenueInfoVO> getVenueInfoPage(VenueInfoRequestVO venueInfoQueryVO, String siteCode) {
        Page<VenueInfoVO> pageResult = new Page<>();
        if (venueInfoQueryVO.getPageSize() == -1) {
            venueInfoQueryVO.setPageSize(5000);
        }

        venueInfoQueryVO.setSiteCode(siteCode);
        IPage<VenueInfoPO> venueInfoPage = null;

        //总控
        if (CommonConstant.ADMIN_CENTER_SITE_CODE.equals(siteCode)) {
            venueInfoPage = baseMapper.queryVenueInfoPage(PageConvertUtil.getMybatisPage(venueInfoQueryVO), venueInfoQueryVO);
        } else {
            //站点
            venueInfoPage = baseMapper.querySiteVenueInfoPage(PageConvertUtil.getMybatisPage(venueInfoQueryVO), venueInfoQueryVO);
        }

        List<VenueInfoPO> venueInfoList = venueInfoPage.getRecords();

        if (!StringUtils.isBlank(venueInfoQueryVO.getVenueName()) && CollectionUtil.isEmpty(venueInfoList)) {
            throw new BaowangDefaultException(ResultCode.NOT_FUND_PLATFORM);
        }

        if (!StringUtils.isBlank(venueInfoQueryVO.getVenueName()) && CollectionUtil.isEmpty(venueInfoList)) {
            throw new BaowangDefaultException(ResultCode.USER_NOT_EXIST);
        }

        if (!StringUtils.isBlank(venueInfoQueryVO.getUpdater()) && CollectionUtil.isEmpty(venueInfoList)) {
            throw new BaowangDefaultException(ResultCode.USER_NOT_EXIST);
        }

        if (CollectionUtil.isEmpty(venueInfoList)) {
            return pageResult;
        }

        List<VenueInfoPO> allVenueList = baseMapper.selectList(null);
        Map<String, VenueInfoPO> venueInfoMap = allVenueList.stream().filter(x -> x.getVenueCurrencyType()
                        .equals(VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode()))
                .collect(Collectors.toMap(
                        x -> x.getVenueCode() + "_" + x.getCurrencyCode(),
                        x -> x
                ));

        // 获取总台全部语种
        List<I18nMsgFrontVO> frontVOS = Lists.newArrayList();
        ResponseVO<List<LanguageManagerListVO>> responseVO = languageManagerApi.list();
        if (responseVO.isOk() && null != responseVO.getData()) {
            List<LanguageManagerListVO> languageManagerListVOS = responseVO.getData();
            languageManagerListVOS.forEach(obj -> frontVOS.add(I18nMsgFrontVO.builder()
                    .language(obj.getCode()).languageName(obj.getName()).build()));
        }

        List<SystemCurrencyInfoRespVO> currencyList = systemCurrencyInfoApi.selectAll().getData();


        IPage<VenueInfoVO> infoPage = venueInfoPage.convert(record -> {
            VenueInfoVO venueInfoVO = new VenueInfoVO();
            BeanUtils.copyProperties(record, venueInfoVO);

            if (ObjectUtil.isNotEmpty(record.getCurrencyCode())) {
                List<String> currencyCodeList = List.of(record.getCurrencyCode().split(","));
                venueInfoVO.setCurrencyCodeList(currencyCodeList);
            }

            venueInfoVO.setPcIconI18nCode(venueInfoVO.getPcIconI18nCode());
            venueInfoVO.setPcIconI18nCodeList(frontVOS);
            venueInfoVO.setH5IconI18nCode(venueInfoVO.getH5IconI18nCode());
            venueInfoVO.setH5IconI18nCodeList(frontVOS);
            String venueCurrencyCode = record.getCurrencyCode();
            venueInfoVO.setVenueCurrencyCode(venueCurrencyCode);

            if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(record.getVenueCurrencyType())) {
                List<VenueInfoCurrencyVO> venueInfoCurrencyList = currencyList.stream().map(c -> {
                            VenueInfoCurrencyVO venueInfoCurrencyVO = VenueInfoCurrencyVO.builder().currencyCode(c.getCurrencyCode()).currencyName(c.getCurrencyNameI18()).build();
                            VenueInfoPO venueInfoPO = venueInfoMap.get(record.getVenueCode() + "_" + c.getCurrencyCode());
                            if (ObjectUtil.isNotEmpty(venueInfoPO)) {
                                venueInfoCurrencyVO.setId(venueInfoPO.getId());
                                venueInfoCurrencyVO.setAesKey(venueInfoPO.getAesKey());
                                venueInfoCurrencyVO.setMerchantKey(venueInfoPO.getMerchantKey());
                                venueInfoCurrencyVO.setMerchantNo(venueInfoPO.getMerchantNo());
                                return venueInfoCurrencyVO;
                            }
                            return null;
                        }).filter(Objects::nonNull)  // 过滤掉所有为null的元素
                        .collect(Collectors.toList()); // 收集成最终的List
                venueInfoVO.setVenueInfoCurrencyList(venueInfoCurrencyList);
                venueInfoVO.setCurrencyCodeList(venueInfoCurrencyList.stream().map(VenueInfoCurrencyVO::getCurrencyCode).toList());
                venueInfoVO.setVenueCurrencyCode(String.join(",", venueInfoCurrencyList.stream().map(VenueInfoCurrencyVO::getCurrencyCode).toList()));

                //用来给前端高亮显示
                if (CollectionUtil.isNotEmpty(venueInfoCurrencyList)) {
                    String lastCurrencyCode = venueInfoCurrencyList.get(venueInfoCurrencyList.size() - 1).getCurrencyCode();
                    venueInfoVO.setLastCurrencyCode(lastCurrencyCode);
                }
            }

            return venueInfoVO;
        });
        BeanUtils.copyProperties(infoPage, pageResult);

        return pageResult;
    }


    public Page<VenueInfoVO> getAdminVenueInfoPage(VenueInfoRequestVO venueInfoQueryVO) {
        Page<VenueInfoVO> pageResult = new Page<>();
        if (venueInfoQueryVO.getPageSize() == -1) {
            venueInfoQueryVO.setPageSize(5000);
        }
        IPage<VenueInfoPO> venueInfoPage = baseMapper.queryVenueInfoPage(PageConvertUtil.getMybatisPage(venueInfoQueryVO), venueInfoQueryVO);

        List<VenueInfoPO> venueInfoList = venueInfoPage.getRecords();

        if (CollectionUtil.isEmpty(venueInfoList)) {
            return pageResult;
        }

        List<VenueInfoPO> allVenueList = baseMapper.selectList(null);
        Map<String, VenueInfoPO> venueInfoMap = allVenueList.stream().filter(x -> x.getVenueCurrencyType()
                        .equals(VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode()))
                .collect(Collectors.toMap(
                        x -> x.getVenueCode() + "_" + x.getCurrencyCode(),
                        x -> x
                ));

        // 获取总台全部语种
        List<I18nMsgFrontVO> frontVOS = Lists.newArrayList();
        ResponseVO<List<LanguageManagerListVO>> responseVO = languageManagerApi.list();
        if (responseVO.isOk() && null != responseVO.getData()) {
            List<LanguageManagerListVO> languageManagerListVOS = responseVO.getData();
            languageManagerListVOS.forEach(obj -> frontVOS.add(I18nMsgFrontVO.builder()
                    .language(obj.getCode()).languageName(obj.getName()).build()));
        }

        List<SystemCurrencyInfoRespVO> currencyList = systemCurrencyInfoApi.selectAll().getData();


        IPage<VenueInfoVO> infoPage = venueInfoPage.convert(record -> {


            VenueInfoVO venueInfoVO = new VenueInfoVO();
            BeanUtils.copyProperties(record, venueInfoVO);

            if (ObjectUtil.isNotEmpty(record.getCurrencyCode())) {
                List<String> currencyCodeList = List.of(record.getCurrencyCode().split(","));
                venueInfoVO.setCurrencyCodeList(currencyCodeList);
            }

            venueInfoVO.setPcIconI18nCodeList(frontVOS);
            venueInfoVO.setH5IconI18nCodeList(frontVOS);
            venueInfoVO.setVenueDescI18nCodeList(frontVOS);
            venueInfoVO.setPcLogoCodeList(frontVOS);
            venueInfoVO.setPcBackgroundCodeList(frontVOS);
            venueInfoVO.setSmallIcon1I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon2I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon3I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon4I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon5I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon6I18nCodeList(frontVOS);
            venueInfoVO.setHtIconI18nCodeList(frontVOS);
            venueInfoVO.setMiddleIconI18nCodeList(frontVOS);
            venueInfoVO.setVenueNameI18nCode(record.getVenueName());
            venueInfoVO.setPcIconI18nCode(venueInfoVO.getPcIconI18nCode());
            venueInfoVO.setH5IconI18nCode(venueInfoVO.getH5IconI18nCode());
            venueInfoVO.setVenueDescI18nCode(record.getVenueDesc());
            String venueCurrencyCode = record.getCurrencyCode();
            venueInfoVO.setVenueCurrencyCode(venueCurrencyCode);

            if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(record.getVenueCurrencyType())) {
                List<VenueInfoCurrencyVO> venueInfoCurrencyList = currencyList.stream().map(c -> {
                            VenueInfoCurrencyVO venueInfoCurrencyVO = VenueInfoCurrencyVO
                                    .builder()
                                    .currencyCode(c.getCurrencyCode())
                                    .currencyName(c.getCurrencyNameI18())
                                    .build();
                            VenueInfoPO venueInfoPO = venueInfoMap.get(record.getVenueCode() + "_" + c.getCurrencyCode());
                            if (ObjectUtil.isNotEmpty(venueInfoPO)) {
                                venueInfoCurrencyVO.setId(venueInfoPO.getId());
                                venueInfoCurrencyVO.setAesKey(venueInfoPO.getAesKey());
                                venueInfoCurrencyVO.setMerchantKey(venueInfoPO.getMerchantKey());
                                venueInfoCurrencyVO.setMerchantNo(venueInfoPO.getMerchantNo());
                                venueInfoCurrencyVO.setBetKey(venueInfoPO.getBetKey());
                                return venueInfoCurrencyVO;
                            }
                            return venueInfoCurrencyVO;
                        }).filter(Objects::nonNull)  // 过滤掉所有为null的元素
                        .collect(Collectors.toList()); // 收集成最终的List
                venueInfoVO.setVenueInfoCurrencyList(venueInfoCurrencyList);
                venueInfoCurrencyList = venueInfoCurrencyList.stream().filter(x -> ObjectUtil.isNotEmpty(x.getId())).toList();
                venueInfoVO.setCurrencyCodeList(venueInfoCurrencyList.stream().map(VenueInfoCurrencyVO::getCurrencyCode).toList());
                venueInfoVO.setVenueCurrencyCode(String.join(",", venueInfoCurrencyList.stream().map(VenueInfoCurrencyVO::getCurrencyCode).toList()));

                //用来给前端高亮显示
                if (CollectionUtil.isNotEmpty(venueInfoCurrencyList)) {
                    String lastCurrencyCode = venueInfoCurrencyList.get(venueInfoCurrencyList.size() - 1).getCurrencyCode();
                    venueInfoVO.setLastCurrencyCode(lastCurrencyCode);
                }
            }
            return venueInfoVO;
        });
        BeanUtils.copyProperties(infoPage, pageResult);
//        pageResult.getRecords().forEach(x -> {
//            String venueName = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.VENUE_CODE, x.getVenueCode());
//            x.setVenueName(venueName);
//        });

        return pageResult;
    }

    public Page<VenueInfoVO> getSiteVenueInfoPage(VenueInfoRequestVO venueInfoQueryVO) {
        venueInfoQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        if (venueInfoQueryVO.getPageSize() == -1) {
            venueInfoQueryVO.setPageSize(5000);
        }

        IPage<VenueInfoPO> venueInfoPage = baseMapper.querySiteVenueInfoPage(PageConvertUtil.getMybatisPage(venueInfoQueryVO), venueInfoQueryVO);
        Page<VenueInfoVO> pageResult = new Page<>();

        List<SiteVenuePO> siteVenuePOList = siteVenueService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteVenuePO.class).eq(SiteVenuePO::getSiteCode,CurrReqUtils.getSiteCode()));

        Map<String,SiteVenuePO>  siteVenuePOMap = siteVenuePOList.stream().collect(Collectors.toMap(SiteVenuePO::getVenueCode,Function.identity()));
        // 获取总台全部语种
        List<I18nMsgFrontVO> frontVOS = Lists.newArrayList();
        ResponseVO<List<LanguageManagerListVO>> responseVO = languageManagerApi.list();
        List<LanguageManagerListVO> languageManagerListVOS = responseVO.getData();
        if (CollectionUtil.isNotEmpty(languageManagerListVOS)) {
            languageManagerListVOS.forEach(obj -> frontVOS.add(I18nMsgFrontVO.builder()
                    .language(obj.getCode()).languageName(obj.getName()).build()));
        }


        List<VenueInfoPO> allVenueList = baseMapper.selectList(null);
        Map<String, VenueInfoPO> venueInfoMap = allVenueList.stream().filter(x -> x.getVenueCurrencyType()
                        .equals(VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode()))
                .collect(Collectors.toMap(
                        x -> x.getVenueCode() + "_" + x.getCurrencyCode(),
                        x -> x
                ));

        List<SystemCurrencyInfoRespVO> currencyList = systemCurrencyInfoApi.selectAll().getData();


        IPage<VenueInfoVO> result = venueInfoPage.convert(record -> {
            VenueInfoVO venueInfoVO = new VenueInfoVO();
            BeanUtils.copyProperties(record, venueInfoVO);
            SiteVenuePO siteVenuePO = siteVenuePOMap.get(record.getVenueCode());
            if(ObjectUtil.isNotEmpty(siteVenuePO)){
                venueInfoVO.setSiteLabelChangeType(siteVenuePO.getSiteLabelChangeType());
            }
            venueInfoVO.setVenueDescI18nCode(record.getVenueDesc());
            venueInfoVO.setVenueNameI18nCode(record.getVenueName());
            venueInfoVO.setPcIconI18nCodeList(frontVOS);
            venueInfoVO.setH5IconI18nCodeList(frontVOS);
            venueInfoVO.setVenueDescI18nCodeList(frontVOS);
            venueInfoVO.setPcLogoCodeList(frontVOS);
            venueInfoVO.setPcBackgroundCodeList(frontVOS);
            venueInfoVO.setSmallIcon1I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon2I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon3I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon4I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon5I18nCodeList(frontVOS);
            venueInfoVO.setSmallIcon6I18nCodeList(frontVOS);
            venueInfoVO.setHtIconI18nCodeList(frontVOS);
            venueInfoVO.setMiddleIconI18nCodeList(frontVOS);

            //单币种是多条数据,所以要把多条币种的数据整数组
            if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(record.getVenueCurrencyType())) {
                List<VenueInfoCurrencyVO> venueInfoCurrencyList = currencyList.stream().map(c -> {
                            VenueInfoCurrencyVO venueInfoCurrencyVO = VenueInfoCurrencyVO.builder().currencyCode(c.getCurrencyCode()).currencyName(c.getCurrencyNameI18()).build();
                            VenueInfoPO venueInfoPO = venueInfoMap.get(record.getVenueCode() + "_" + c.getCurrencyCode());
                            if (ObjectUtil.isNotEmpty(venueInfoPO)) {
                                venueInfoCurrencyVO.setId(venueInfoPO.getId());
                                venueInfoCurrencyVO.setAesKey(venueInfoPO.getAesKey());
                                venueInfoCurrencyVO.setMerchantKey(venueInfoPO.getMerchantKey());
                                venueInfoCurrencyVO.setMerchantNo(venueInfoPO.getMerchantNo());
                                return venueInfoCurrencyVO;
                            }
                            return null;
                        }).filter(Objects::nonNull)  // 过滤掉所有为null的元素
                        .collect(Collectors.toList()); // 收集成最终的List
                venueInfoVO.setVenueInfoCurrencyList(venueInfoCurrencyList);
                venueInfoVO.setCurrencyCodeList(venueInfoCurrencyList.stream().map(VenueInfoCurrencyVO::getCurrencyCode).toList());
                venueInfoVO.setVenueCurrencyCode(String.join(",", venueInfoCurrencyList.stream().map(VenueInfoCurrencyVO::getCurrencyCode).toList()));

                //用来给前端高亮显示
                if (CollectionUtil.isNotEmpty(venueInfoCurrencyList)) {
                    String lastCurrencyCode = venueInfoCurrencyList.get(venueInfoCurrencyList.size() - 1).getCurrencyCode();
                    venueInfoVO.setLastCurrencyCode(lastCurrencyCode);
                }
            }else{
                //多币种
                if (ObjectUtil.isNotEmpty(record.getCurrencyCode())) {
                    venueInfoVO.setVenueCurrencyCode(record.getCurrencyCode());
                    venueInfoVO.setCurrencyCodeList(Arrays.asList(record.getCurrencyCode().split(",")));
                }
            }


            return venueInfoVO;
        });

        BeanUtils.copyProperties(result, pageResult);
        return pageResult;
    }

    @Deprecated
    public Page<VenueInfoVO> getVenueInfoPage(VenueInfoRequestVO venueInfoQueryVO) {
        return getVenueInfoPage(venueInfoQueryVO, CurrReqUtils.getSiteCode());
    }

//    @Transactional(rollbackFor = Exception.class)
//    public Boolean addVenueInfo(VenueInfoAddVO venueInfoAddVO) {
//        if (baseMapper.selectCount(Wrappers.lambdaQuery(VenueInfoPO.class)
//                .eq(VenueInfoPO::getVenueCode, venueInfoAddVO.getVenueCode())) > 0) {
//            throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
//        }
//
//        VenueEnum venueEnum = VenueEnum.nameOfCode(venueInfoAddVO.getVenueCode());
//        if (ObjectUtil.isEmpty(venueEnum)) {
//            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//        }
//        VenueInfoPO venueInfoPO = new VenueInfoPO();
//        BeanUtils.copyProperties(venueInfoAddVO, venueInfoPO);
//        venueInfoPO.setVenueCode(venueEnum.getVenueCode());
//        venueInfoPO.setVenuePlatform(venueEnum.getVenuePlatform());
//        venueInfoPO.setVenueName(venueEnum.getVenueName());
//        venueInfoPO.setStatus(StatusEnum.CLOSE.getCode());
//        if (CollectionUtil.isNotEmpty(venueInfoAddVO.getCurrencyCodeList())) {
//            String currencyCode = String.join(",", venueInfoAddVO.getCurrencyCodeList());
//            venueInfoPO.setCurrencyCode(currencyCode);
//        }
//
//        String pcIconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_PC_VENUE_ICON.getCode());
//        Map<String, List<I18nMsgFrontVO>> pcReq = Maps.newHashMap();
//        pcReq.put(pcIconI18nCode, venueInfoAddVO.getPcIconI18nCodeList());
//        ResponseVO<Boolean> responseVO = i18nApi.insert(pcReq);
//        if (!responseVO.isOk() || !responseVO.getData()) {
//            log.info("PC调用i18是失败:param:{},result:{}", pcReq, responseVO);
//            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
//        }
//        venueInfoPO.setPcIconI18nCode(pcIconI18nCode);
//        List<I18nMsgFrontVO> pcIconI18nZh = venueInfoAddVO.getPcIconI18nCodeList().stream()
//                .filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();
//        if (CollectionUtil.isNotEmpty(pcIconI18nZh)) {
//            venueInfoPO.setPcVenueIcon(pcIconI18nZh.get(0).getMessage());
//        }
//
//
//        String h5IconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.H5_VENUE_ICON.getCode());
//        Map<String, List<I18nMsgFrontVO>> h5Req = Maps.newHashMap();
//        h5Req.put(h5IconI18nCode, venueInfoAddVO.getH5IconI18nCodeList());
//        ResponseVO<Boolean> h5ResponseVO = i18nApi.insert(h5Req);
//        if (!h5ResponseVO.isOk() || !h5ResponseVO.getData()) {
//            log.info("H5调用i18是失败:param:{},result:{}", h5Req, h5ResponseVO);
//            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
//        }
//        venueInfoPO.setH5IconI18nCode(h5IconI18nCode);
//        List<I18nMsgFrontVO> h5IconI18nZh = venueInfoAddVO.getH5IconI18nCodeList().stream()
//                .filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();
//        if (CollectionUtil.isNotEmpty(h5IconI18nZh)) {
//            venueInfoPO.setH5VenueIcon(h5IconI18nZh.get(0).getMessage());
//        }
//
//        int count = baseMapper.insert(venueInfoPO);
//
//        if (count <= 0) {
//            log.info("新增平台id失败");
//            return Boolean.FALSE;
//        }
//
//        return Boolean.TRUE;
//    }

    @Transactional(rollbackFor = {Exception.class})
    public Boolean upAdminVenueInfoStatus(GameClassStatusRequestUpVO requestVO) {
        Boolean venueLock = RedisUtil.getValue(RedisConstants.UP_VENUE_LOCK);
        if (ObjectUtil.isNotEmpty(venueLock)) {
            throw new BaowangDefaultException(ResultCode.PLEASE_TRY_AGAIN_LATER);
        }

        RedisUtil.setValue(RedisConstants.UP_VENUE_LOCK, Boolean.TRUE, 10L, TimeUnit.SECONDS);

        VenueInfoPO venueInfo = baseMapper.selectOne(Wrappers
                .lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getId, requestVO.getId()));
        if (ObjectUtil.isEmpty(venueInfo)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        UpdateWrapper<VenueInfoPO> updateWrapper = new UpdateWrapper<>();

        if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(venueInfo.getVenueCurrencyType())) {
            updateWrapper = updateWrapper.eq("venue_code", venueInfo.getVenueCode());
        } else {
            updateWrapper = updateWrapper.eq("id", requestVO.getId());
        }
        if (ObjectUtil.isEmpty(StatusEnum.nameByCode(requestVO.getStatus()))) {
            log.info("请求参数异常:{}", requestVO);
            return Boolean.FALSE;
        }

        if (ObjectUtil.isNotEmpty(requestVO.getMaintenanceStartTime()) && ObjectUtil.isNotEmpty(requestVO.getMaintenanceEndTime())) {
            if (requestVO.getMaintenanceStartTime() >= requestVO.getMaintenanceEndTime()) {
                throw new BaowangDefaultException(ResultCode.TIME_NOT_GOOD);
            }
        }

        Long maintenanceEndTime = null;
        Long maintenanceStartTime = null;
        String remark = null;
        if (Objects.equals(requestVO.getStatus(), StatusEnum.MAINTAIN.getCode())) {

            //场馆未开启,场馆不允许维护
            if (!StatusEnum.OPEN.getCode().equals(venueInfo.getStatus())) {
                throw new BaowangDefaultException(ResultCode.ADMIN_VENUE_CLOSE);
            }

            maintenanceEndTime = requestVO.getMaintenanceEndTime();
            maintenanceStartTime = requestVO.getMaintenanceStartTime();
            remark = requestVO.getRemark();
        }

        updateWrapper.set("status", requestVO.getStatus())
                .set("maintenance_start_time", maintenanceStartTime)
                .set("maintenance_end_time", maintenanceEndTime)
                .set("remark", remark)
                .set("updater", CurrReqUtils.getAccount())
                .set("updated_time", System.currentTimeMillis());

        GameClassStatusRequestUpVO requestUpVO = GameClassStatusRequestUpVO.builder()
                .status(requestVO.getStatus())
                .maintenanceStartTime(maintenanceStartTime)
                .maintenanceEndTime(maintenanceEndTime)
                .remark(remark)
                .venueCode(venueInfo.getVenueCode())
                .build();

//        List<GameInfoPO> gameInfoList = adminGameInfoService.getBaseMapper().selectList(
//                Wrappers.lambdaQuery(GameInfoPO.class)
//                        .select(GameInfoPO::getId)
//                        .eq(GameInfoPO::getVenueCode, venueInfo.getVenueCode()));
//
//        //总控向下改总控游戏状态
//        if (CollectionUtil.isNotEmpty(gameInfoList)) {
//            requestUpVO.setIds(gameInfoList.stream().map(GameInfoPO::getId).toList());
//            adminGameInfoService.upAdminVenueGameInfoStatus(requestUpVO);
//        }


        Long count = adminGameInfoService.getBaseMapper().selectCount(
                Wrappers.lambdaQuery(GameInfoPO.class)
                        .eq(GameInfoPO::getVenueCode, venueInfo.getVenueCode()));
        if (count > 0) {//总控向下改总控游戏状态
            adminGameInfoService.newUpAdminVenueGameInfoStatus(requestUpVO, venueInfo.getVenueCode());
        }

        //总控向下改场馆
//        siteVenueService.adminUpVenueStatus(requestUpVO);
        siteVenueService.newAdminUpVenueStatus(requestUpVO, venueInfo.getVenueCode());
        boolean bool = baseMapper.update(null, updateWrapper) > 0;
        if (bool) {
            CompletableFuture.runAsync(() -> {
                RedisUtil.deleteKey(String.format(RedisConstants.VENUE_INFO_PLAT_MERCHANT, venueInfo.getVenuePlatform(), venueInfo.getMerchantNo()));
                RedisUtil.deleteKey(RedisConstants.UP_VENUE_LOCK);
                LobbyCateUtil.deleteLobbyAllSiteGameInfo();
            });
        }
        return bool;
    }


    /**
     * 获取中文消息
     */
    private String getI18nZhMsg(List<I18nMsgFrontVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<I18nMsgFrontVO> pcIconI18nZh = list.stream()
                    .filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();
            if (CollectionUtil.isNotEmpty(pcIconI18nZh)) {
                return pcIconI18nZh.get(0).getMessage();
            }
        }
        return null;
    }

    private Boolean upSiteVenueInfo(SiteVenueInfoUpVO requestVO, String venueCode) {
        String siteCode = CurrReqUtils.getSiteCode();
        SiteVenueConfigPO siteVenueConfigPO = siteVenueConfigService.getBaseMapper()
                .selectOne(Wrappers.lambdaQuery(SiteVenueConfigPO.class)
                        .eq(SiteVenueConfigPO::getVenueCode, venueCode)
                        .eq(SiteVenueConfigPO::getSiteCode,siteCode));
        if(siteVenueConfigPO == null){
            return Boolean.FALSE;
        }

        //传上来的参数有缺失要补充
        toSetI18Url(requestVO.getPcIconI18nCodeList());
        toSetI18Url(requestVO.getMiddleIconI18nCodeList());
        toSetI18Url(requestVO.getH5IconI18nCodeList());
        toSetI18Url(requestVO.getPcLogoCodeList());
        toSetI18Url(requestVO.getPcBackgroundCodeList());
        toSetI18Url(requestVO.getSmallIcon1I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon2I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon3I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon4I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon5I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon6I18nCodeList());
        toSetI18Url(requestVO.getHtIconI18nCodeList());

        String middleIconI18nCode = toSetI18Code(siteVenueConfigPO.getMiddleIconI18nCode(),I18MsgKeyEnum.SITE_MIDDLE_HT_ICON, requestVO.getMiddleIconI18nCodeList());
        String pcIconI18nCode = toSetI18Code(siteVenueConfigPO.getPcIconI18nCode(),I18MsgKeyEnum.SITE_PC_VENUE_ICON, requestVO.getPcIconI18nCodeList());
        String h5IconI18nCode = toSetI18Code(siteVenueConfigPO.getH5IconI18nCode(),I18MsgKeyEnum.SITE_H5_VENUE_ICON, requestVO.getH5IconI18nCodeList());
        String pcLogoCode = toSetI18Code(siteVenueConfigPO.getPcLogoCode(),I18MsgKeyEnum.SITE_PC_LOGO_CODE, requestVO.getPcLogoCodeList());
        String pcBackgroundCode = toSetI18Code(siteVenueConfigPO.getPcBackgroundCode(),I18MsgKeyEnum.SITE_PC_BACKGROUND_ICON, requestVO.getPcBackgroundCodeList());
        String smallIcon1I18nCode = toSetI18Code(siteVenueConfigPO.getSmallIcon1I18nCode(),I18MsgKeyEnum.SITE_SMALL_ICON1, requestVO.getSmallIcon1I18nCodeList());
        String smallIcon2I18nCode = toSetI18Code(siteVenueConfigPO.getSmallIcon2I18nCode(),I18MsgKeyEnum.SITE_SMALL_ICON2, requestVO.getSmallIcon2I18nCodeList());
        String smallIcon3I18nCode = toSetI18Code(siteVenueConfigPO.getSmallIcon3I18nCode(),I18MsgKeyEnum.SITE_SMALL_ICON3, requestVO.getSmallIcon3I18nCodeList());
        String smallIcon4I18nCode = toSetI18Code(siteVenueConfigPO.getSmallIcon4I18nCode(),I18MsgKeyEnum.SITE_SMALL_ICON4, requestVO.getSmallIcon4I18nCodeList());
        String smallIcon5I18nCode = toSetI18Code(siteVenueConfigPO.getSmallIcon5I18nCode(),I18MsgKeyEnum.SITE_SMALL_ICON5, requestVO.getSmallIcon5I18nCodeList());
        String smallIcon6I18nCode = toSetI18Code(siteVenueConfigPO.getSmallIcon6I18nCode(),I18MsgKeyEnum.SITE_SMALL_ICON6, requestVO.getSmallIcon6I18nCodeList());
        String htIconI18nCode = toSetI18Code(siteVenueConfigPO.getHtIconI18nCode(),I18MsgKeyEnum.SITE_HT_ICON, requestVO.getHtIconI18nCodeList());
        String venueDescI18Code = toSetI18Code(siteVenueConfigPO.getVenueDesc(),I18MsgKeyEnum.SITE_VENUE_DESC, requestVO.getVenueDescI18nCodeList());
        String venueNameI18Code = toSetI18Code(siteVenueConfigPO.getVenueName(),I18MsgKeyEnum.SITE_INIT_VENUE_NAME, requestVO.getVenueNameI18nCodeList());

        siteVenueConfigService.getBaseMapper().delete(Wrappers.lambdaQuery(SiteVenueConfigPO.class).eq(SiteVenueConfigPO::getVenueCode, venueCode)
                .eq(SiteVenueConfigPO::getSiteCode,siteCode));

        SiteVenueConfigPO addSiteVenue = SiteVenueConfigPO
                .builder()
                .venueCode(venueCode)
                .siteCode(siteCode)
                .venueName(venueNameI18Code)
                .pcIconI18nCode(pcIconI18nCode)
                .h5IconI18nCode(h5IconI18nCode)
                .middleIconI18nCode(middleIconI18nCode)
                .pcBackgroundCode(pcBackgroundCode)
                .pcLogoCode(pcLogoCode)
                .venueDesc(venueDescI18Code)
                .smallIcon1I18nCode(smallIcon1I18nCode)
                .smallIcon2I18nCode(smallIcon2I18nCode)
                .smallIcon3I18nCode(smallIcon3I18nCode)
                .smallIcon4I18nCode(smallIcon4I18nCode)
                .smallIcon5I18nCode(smallIcon5I18nCode)
                .smallIcon6I18nCode(smallIcon6I18nCode)
                .htIconI18nCode(htIconI18nCode)
                .build();
        addSiteVenue.setCreatedTime(System.currentTimeMillis());
        addSiteVenue.setUpdatedTime(System.currentTimeMillis());
        boolean addBool = siteVenueConfigService.getBaseMapper().insert(addSiteVenue) > 0;
        if (!addBool) {
            log.info("站点场馆配置添加失败");
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        SiteVenuePO siteVenuePO = new SiteVenuePO();
        siteVenuePO.setSiteLabelChangeType(requestVO.getSiteLabelChangeType());
        siteVenueService.update(siteVenuePO,Wrappers.lambdaQuery(SiteVenuePO.class)
                .eq(SiteVenuePO::getSiteCode,siteCode)
                .eq(SiteVenuePO::getVenueCode,requestVO.getVenueCode()));
        CompletableFuture.runAsync(() -> LobbyCateUtil.deleteLobbySiteGameInfo(siteCode));
        return addBool;
    }

    public Boolean upVenueCurrencyType(VenueInfoUpVO requestVO, VenueInfoPO venueInfoPO, List<String> reqVenueCurrency,List<VenueInfoPO> venueInfoPOList) {
        baseMapper.delete(Wrappers.lambdaQuery(VenueInfoPO.class).eq(VenueInfoPO::getVenueCode, venueInfoPO.getVenueCode()));

        List<VenueInfoPO> addVenuePo = Lists.newArrayList();

        toSetI18Url(requestVO.getPcIconI18nCodeList());
        toSetI18Url(requestVO.getH5IconI18nCodeList());
        String pcI18nCode = toSetI18Code(venueInfoPO.getPcIconI18nCode(), I18MsgKeyEnum.PC_VENUE_ICON, requestVO.getPcIconI18nCodeList());
        String h5I18nCode = toSetI18Code(venueInfoPO.getH5IconI18nCode(), I18MsgKeyEnum.H5_VENUE_ICON, requestVO.getH5IconI18nCodeList());

        toSetI18Url(requestVO.getPcLogoCodeList());
        toSetI18Url(requestVO.getPcBackgroundCodeList());
        String pcLogoCode = toSetI18Code(venueInfoPO.getPcLogoCode(), I18MsgKeyEnum.PC_LOGO_CODE, requestVO.getPcLogoCodeList());
        String pcBackgroundCode = toSetI18Code(venueInfoPO.getPcBackgroundCode(), I18MsgKeyEnum.PC_BACKGROUND_ICON, requestVO.getPcBackgroundCodeList());

        toSetI18Url(requestVO.getSmallIcon1I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon2I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon3I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon4I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon5I18nCodeList());
        toSetI18Url(requestVO.getSmallIcon6I18nCodeList());
        String smallIcon1I18nCode = toSetI18Code(venueInfoPO.getSmallIcon1I18nCode(), I18MsgKeyEnum.SMALL_ICON1, requestVO.getSmallIcon1I18nCodeList());
        String smallIcon2I18nCode = toSetI18Code(venueInfoPO.getSmallIcon2I18nCode(), I18MsgKeyEnum.SMALL_ICON2, requestVO.getSmallIcon2I18nCodeList());
        String smallIcon3I18nCode = toSetI18Code(venueInfoPO.getSmallIcon3I18nCode(), I18MsgKeyEnum.SMALL_ICON3, requestVO.getSmallIcon3I18nCodeList());
        String smallIcon4I18nCode = toSetI18Code(venueInfoPO.getSmallIcon4I18nCode(), I18MsgKeyEnum.SMALL_ICON4, requestVO.getSmallIcon4I18nCodeList());
        String smallIcon5I18nCode = toSetI18Code(venueInfoPO.getSmallIcon5I18nCode(), I18MsgKeyEnum.SMALL_ICON5, requestVO.getSmallIcon5I18nCodeList());
        String smallIcon6I18nCode = toSetI18Code(venueInfoPO.getSmallIcon6I18nCode(), I18MsgKeyEnum.SMALL_ICON6, requestVO.getSmallIcon6I18nCodeList());
        String middleIconI18nCode = toSetI18Code(venueInfoPO.getMiddleIconI18nCode(), I18MsgKeyEnum.MIDDLE_HT_ICON, requestVO.getMiddleIconI18nCodeList());



        //横图
        String htIconI18nCode = toSetI18Code(venueInfoPO.getHtIconI18nCode(), I18MsgKeyEnum.HT_ICON, requestVO.getHtIconI18nCodeList());
        toSetI18Url(requestVO.getHtIconI18nCodeList());

        String venueDescI18Code = toSetI18Code(venueInfoPO.getVenueDesc(), I18MsgKeyEnum.VENUE_DESC, requestVO.getVenueDescI18nCodeList());

        VenueEnum venueEnum = VenueEnum.nameOfCode(venueInfoPO.getVenueCode());

        String venueNameI18nCode = I18MsgKeyEnum.VENUE_INIT_NAME.getCode() + "_" + venueInfoPO.getVenueCode();

        //指定KEY同步多语言
        toSetI18NameList(venueNameI18nCode, requestVO.getVenueNameI18nCodeList());

        Integer venueJoinType = venueEnum.getVenueJoinTypeEnum().getCode();
        //单币种
        if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(requestVO.getVenueCurrencyType())) {
            List<VenueInfoCurrencyVO> venueInfoCurrencyList = requestVO.getVenueInfoCurrencyList();

            if (CollectionUtil.isEmpty(venueInfoCurrencyList)) {
                log.info("单币种场馆修改类型失败,缺少参数:{}", requestVO);
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }

            Map<String, VenueInfoCurrencyVO> venueInfoCurrencyVOMap = venueInfoCurrencyList.stream()
                    .collect(Collectors.toMap(VenueInfoCurrencyVO::getCurrencyCode, Function.identity()));
            Map<String,VenueInfoPO> venueInfoPoMap =  venueInfoPOList.stream().collect(Collectors.toMap(VenueInfoPO::getCurrencyCode,Function.identity()));

            for (String item : reqVenueCurrency) {
                VenueInfoCurrencyVO venueInfoCurrencyVO = venueInfoCurrencyVOMap.get(item);
                if (ObjectUtil.isEmpty(venueInfoCurrencyVO)) {
                    continue;
                }
                VenueInfoPO venueInfo = VenueInfoPO
                        .builder()
                        .venueName(venueNameI18nCode)
                        .venueCode(venueInfoPO.getVenueCode())
                        .venueJoinType(venueJoinType)
                        .venuePlatformName(requestVO.getVenuePlatformName())
                        .pcLogoCode(pcLogoCode)
                        .pcBackgroundCode(pcBackgroundCode)
                        .venueType(venueInfoPO.getVenueType())
                        .status(venueInfoPO.getStatus())
                        .venueDesc(venueDescI18Code)
                        .gameUrl(venueInfoPO.getGameUrl())
                        .venueCurrencyType(requestVO.getVenueCurrencyType())
                        .h5VenueIcon(getI18nZhMsg(requestVO.getH5IconI18nCodeList()))
                        .pcVenueIcon(getI18nZhMsg(requestVO.getPcIconI18nCodeList()))
                        .venueProportion(requestVO.getVenueProportion())
                        .remark(requestVO.getRemark())
                        .maintenanceStartTime(requestVO.getMaintenanceStartTime())
                        .maintenanceEndTime(requestVO.getMaintenanceEndTime())
                        .aesKey(venueInfoCurrencyVO.getAesKey())
                        .apiUrl(requestVO.getApiUrl())
                        .betUrl(venueInfoPO.getBetUrl())
                        .currencyCode(venueInfoCurrencyVO.getCurrencyCode())
                        .merchantKey(venueInfoCurrencyVO.getMerchantKey())
                        .merchantNo(venueInfoCurrencyVO.getMerchantNo())
                        .pcIconI18nCode(pcI18nCode)
                        .h5IconI18nCode(h5I18nCode)
                        .venuePlatform(venueInfoPO.getVenuePlatform())
                        .validProportion(requestVO.getValidProportion())
                        .proportionType(requestVO.getProportionType())
                        .smallIcon1I18nCode(smallIcon1I18nCode)
                        .smallIcon2I18nCode(smallIcon2I18nCode)
                        .smallIcon3I18nCode(smallIcon3I18nCode)
                        .smallIcon4I18nCode(smallIcon4I18nCode)
                        .smallIcon5I18nCode(smallIcon5I18nCode)
                        .smallIcon6I18nCode(smallIcon6I18nCode)
                        .htIconI18nCode(htIconI18nCode)
                        .middleIconI18nCode(middleIconI18nCode)
                        .build();
                venueInfo.setCreator(CurrReqUtils.getAccount());
                venueInfo.setUpdater(CurrReqUtils.getAccount());
                venueInfo.setCreatedTime(System.currentTimeMillis());
                venueInfo.setUpdatedTime(System.currentTimeMillis());
                VenueInfoPO venuePo = venueInfoPoMap.get(item);
                if(ObjectUtil.isNotEmpty(venuePo)){
                    venueInfo.setBetKey(venuePo.getBetKey());
                }

                addVenuePo.add(venueInfo);
            }
        } else {
            String currencyCode = String.join(",", requestVO.getCurrencyCodeList());
            VenueInfoPO venueInfo = VenueInfoPO.builder()
                    .venueCode(venueInfoPO.getVenueCode())
                    .venueName(venueNameI18nCode)
                    .pcLogoCode(pcLogoCode)
                    .pcBackgroundCode(pcBackgroundCode)
                    .venueJoinType(venueJoinType)
                    .venuePlatformName(requestVO.getVenuePlatformName())
                    .venueType(venueInfoPO.getVenueType())
                    .status(venueInfoPO.getStatus())
                    .gameUrl(venueInfoPO.getGameUrl())
                    .venueCurrencyType(requestVO.getVenueCurrencyType())
                    .h5VenueIcon(getI18nZhMsg(requestVO.getH5IconI18nCodeList()))
                    .pcVenueIcon(getI18nZhMsg(requestVO.getPcIconI18nCodeList()))
                    .venueProportion(requestVO.getVenueProportion())
                    .remark(requestVO.getRemark())
                    .maintenanceStartTime(requestVO.getMaintenanceStartTime())
                    .maintenanceEndTime(requestVO.getMaintenanceEndTime())
                    .aesKey(requestVO.getAesKey())
                    .venueDesc(venueDescI18Code)
                    .apiUrl(requestVO.getApiUrl())
                    .betUrl(venueInfoPO.getBetUrl())
                    .currencyCode(currencyCode)
                    .merchantKey(requestVO.getMerchantKey())
                    .merchantNo(requestVO.getMerchantNo())
                    .pcIconI18nCode(pcI18nCode)
                    .h5IconI18nCode(h5I18nCode)
                    .betKey(venueInfoPO.getBetKey())
                    .venuePlatform(venueInfoPO.getVenuePlatform())
                    .validProportion(requestVO.getValidProportion())
                    .proportionType(requestVO.getProportionType())
                    .smallIcon1I18nCode(smallIcon1I18nCode)
                    .smallIcon2I18nCode(smallIcon2I18nCode)
                    .smallIcon3I18nCode(smallIcon3I18nCode)
                    .smallIcon4I18nCode(smallIcon4I18nCode)
                    .smallIcon5I18nCode(smallIcon5I18nCode)
                    .smallIcon6I18nCode(smallIcon6I18nCode)
                    .htIconI18nCode(htIconI18nCode)
                    .middleIconI18nCode(middleIconI18nCode)
                    .build();
            venueInfo.setCreator(CurrReqUtils.getAccount());
            venueInfo.setUpdater(CurrReqUtils.getAccount());
            venueInfo.setCreatedTime(System.currentTimeMillis());
            venueInfo.setUpdatedTime(System.currentTimeMillis());
            addVenuePo.add(venueInfo);
        }

        GameInfoPO po = GameInfoPO.builder().currencyCode(String.join(",", reqVenueCurrency)).build();
        gameInfoRepository.update(po, Wrappers
                .lambdaQuery(GameInfoPO.class)
                .eq(GameInfoPO::getVenueCode, venueInfoPO.getVenueCode()));

        SiteGamePO siteGamePO = SiteGamePO.builder().currencyCode(String.join(",", reqVenueCurrency)).build();
        siteGameService.getBaseMapper().update(siteGamePO,
                Wrappers.lambdaQuery(SiteGamePO.class)
                        .eq(SiteGamePO::getVenueCode, venueInfoPO.getVenueCode())
        );


        if (CollectionUtil.isNotEmpty(addVenuePo)) {
            if (!super.saveBatch(addVenuePo)) {
                log.info("新增场馆失败:{}", addVenuePo);
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            CompletableFuture.runAsync(LobbyCateUtil::deleteLobbyAllSiteGameInfo);
            return true;
        }
        log.info("执行失败没有执行插入");
        throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
    }



    @Transactional(rollbackFor = {Exception.class})
    public Boolean siteUpVenueInfo(SiteVenueInfoUpVO requestVO) {

        VenueInfoPO venueInfoPO = baseMapper.selectById(requestVO.getId());
        if (ObjectUtil.isEmpty(venueInfoPO)) {
            log.info("修改站点场馆,参数异常,场馆不存在:{}", requestVO.getId());
            return false;
        }

        return upSiteVenueInfo(requestVO, venueInfoPO.getVenueCode());
    }

    @Transactional(rollbackFor = {Exception.class})
    public Boolean adminUpVenueInfo(VenueInfoUpVO requestVO) {
        VenueInfoPO venueInfoPO = baseMapper.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class).eq(VenueInfoPO::getVenueCode,requestVO.getVenueCode())
                .last(" limit 1 "));
        if (ObjectUtil.isEmpty(venueInfoPO)) {
            log.info("参数异常,场馆不存在:{}", requestVO.getId());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        List<VenueInfoPO> venueInfoPOList = baseMapper.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, requestVO.getVenueCode())
        );

        //场馆原本的币种
        List<String> dbVenueCurrency = Lists.newArrayList();

        //请求进来的新币种
        List<String> reqVenueCurrency;

        //多币种的币种字段
        if (requestVO.getVenueCurrencyType().equals(VenueCurrencyTypeEnum.MULTI_CURRENCY.getCode())) {
            if (CollectionUtil.isEmpty(requestVO.getCurrencyCodeList())) {
                log.info("多币种修改场馆没有传币种");
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            reqVenueCurrency = requestVO.getCurrencyCodeList();
        } else {//单币种的币种字段
            reqVenueCurrency = Lists.newArrayList();
            List<VenueInfoCurrencyVO> venueInfoCurrencyList = requestVO.getVenueInfoCurrencyList();
            for (VenueInfoCurrencyVO item : venueInfoCurrencyList) {
                if (StringUtils.isNotBlank(item.getCurrencyCode()) && StringUtils.isNotBlank(item.getMerchantKey())
                        && StringUtils.isNotBlank(item.getMerchantNo()) && StringUtils.isNotBlank(item.getAesKey())) {
                    reqVenueCurrency.add(item.getCurrencyCode());
                }
            }
        }

        if (CollectionUtil.isEmpty(reqVenueCurrency)) {
            log.info("修改场馆币种参数缺失");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(venueInfoPO.getVenueCurrencyType())) {
            dbVenueCurrency = venueInfoPOList.stream().map(VenueInfoPO::getCurrencyCode).toList();
        } else {
            dbVenueCurrency = Arrays.asList(venueInfoPO.getCurrencyCode().split(","));
        }

        // 被删除的币种 = 原有集合 - 新集合
        List<String> removedCurrency = dbVenueCurrency.stream()
                .filter(currency -> !reqVenueCurrency.contains(currency))
                .toList();


        //场馆删除币种需要同步到游戏表
        if (CollectionUtil.isNotEmpty(removedCurrency)) {
            List<GameInfoPO> gameInfoVOS = gameInfoRepository.selectList(Wrappers.lambdaQuery(GameInfoPO.class)
                    .eq(GameInfoPO::getVenueCode, venueInfoPO.getVenueCode())
                    .select(GameInfoPO::getId, GameInfoPO::getVenueCode, GameInfoPO::getCurrencyCode));
            if (CollectionUtil.isNotEmpty(gameInfoVOS)) {
                //游戏的币种被删了.把关系也要删除
                if (CollectionUtil.isNotEmpty(removedCurrency)) {
                    List<String> gameList = gameInfoVOS.stream().map(GameInfoPO::getId).toList();
                    siteGameHotSortRepository.delete(Wrappers.lambdaQuery(SiteGameHotSortPO.class)
                            .in(SiteGameHotSortPO::getGameId, gameList)
                            .in(SiteGameHotSortPO::getCurrencyCode, removedCurrency));
                    gameTwoCurrencySortRepository.delete(Wrappers.lambdaQuery(GameTwoCurrencySortPO.class)
                            .in(GameTwoCurrencySortPO::getGameId, gameList)
                            .in(GameTwoCurrencySortPO::getCurrencyCode, removedCurrency));
                }
            }
        }
        return upVenueCurrencyType(requestVO, venueInfoPO, reqVenueCurrency,venueInfoPOList);
    }


    /**
     * I18配置接口调用
     * @param i18Code key
     * @param iconI18nCodeList 多语言
     * @return 如果code存在直接调用修改接口,不存在直接新增
     */
    public void toSetI18NameList(String i18Code, List<I18nMsgFrontVO> iconI18nCodeList) {
        ResponseVO<Boolean> iconI18responseVO = null;
        if (ObjectUtil.isNotEmpty(i18Code) && CollectionUtil.isNotEmpty(iconI18nCodeList)) {
            Map<String, List<I18nMsgFrontVO>> req = Maps.newHashMap();
            req.put(i18Code, iconI18nCodeList);
            iconI18responseVO = i18nApi.update(req);
        } else {
            Map<String, List<I18nMsgFrontVO>> pcReq = Maps.newHashMap();
            pcReq.put(i18Code, iconI18nCodeList);
            iconI18responseVO = i18nApi.insert(pcReq);
        }

        if (!iconI18responseVO.isOk() || !iconI18responseVO.getData()) {
            log.info("toSetVenueNameI18Code,调用i18是失败:result:{}", iconI18responseVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
    }


    /**
     * 指定KYE 枚举 插入或者修改到多语言表中
     */
    public String toSetI18Code(String i18Code, I18MsgKeyEnum keyEnum, List<I18nMsgFrontVO> iconI18nCodeList) {
        ResponseVO<Boolean> iconI18responseVO = null;
        if (ObjectUtil.isNotEmpty(i18Code) && CollectionUtil.isNotEmpty(iconI18nCodeList)) {
            Map<String, List<I18nMsgFrontVO>> req = Maps.newHashMap();
            req.put(i18Code, iconI18nCodeList);
            iconI18responseVO = i18nApi.update(req);
        } else {
            if(CollectionUtil.isEmpty(iconI18nCodeList)){
                return null;
            }
            for (I18nMsgFrontVO item : iconI18nCodeList){
                if(ObjectUtil.isEmpty(item.getMessage())){
                    item.setMessage("");
                }
            }
            i18Code = RedisKeyTransUtil.getI18nDynamicKey(keyEnum.getCode());
            Map<String, List<I18nMsgFrontVO>> pcReq = Maps.newHashMap();
            pcReq.put(i18Code, iconI18nCodeList);
            iconI18responseVO = i18nApi.insert(pcReq);
        }

        if (!iconI18responseVO.isOk() || !iconI18responseVO.getData()) {
            log.info("调用i18是失败:result:{}", iconI18responseVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return i18Code;
    }

    private void toSetI18Url(List<I18nMsgFrontVO> h5IconI18nCodeList) {
        if (CollectionUtil.isNotEmpty(h5IconI18nCodeList)) {
            //防止传上来的图片地址是全路径的
            h5IconI18nCodeList.forEach(x -> {
                String message = x.getMessage();
                if (!StringUtils.isBlank(message)) {
                    // 判断是否包含 "http"
                    if (message.contains("http")) {
                        // 找到最后两个 "/" 的位置
                        int lastSlashIndex = message.lastIndexOf('/');
                        int secondLastSlashIndex = message.lastIndexOf('/', lastSlashIndex - 1);
                        // 截取从第二个 "/" 后面的部分
                        x.setMessage(message.substring(secondLastSlashIndex + 1));
                    }
                }
            });
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean addSiteVenue(String siteCode, List<SiteVenueVO> siteVenueVOS, String siteName) {
        log.info("站点初始化游戏开始...");
        long start = System.currentTimeMillis();
        if (ObjectUtil.isEmpty(siteCode) || CollectionUtil.isEmpty(siteVenueVOS)) {
            throw new BaowangDefaultException(ResultCode.UPDATE_SITE_VENUE_ERROR);
        }

        List<GameInfoPO> gameInfoList = gameInfoRepository.selectList(Wrappers.lambdaQuery(GameInfoPO.class));

        //默认全选时,根据venueCode获取同一个code的所有游戏(存在相同场馆code的多个场馆)
        List<String> venueCodes = siteVenueVOS.stream()
                .filter(siteVenue -> CollectionUtil.isEmpty(siteVenue.getGameId()))
                .map(SiteVenueVO::getVenueCode)
                .toList();


        if (CollectionUtil.isNotEmpty(venueCodes)) {
            //说明存在全选的场馆，找到该场馆对应的游戏列表
            List<GameInfoPO> gameInfoPOS = gameInfoList.stream().filter(x -> venueCodes.contains(x.getVenueCode())).toList();
            if (CollectionUtil.isNotEmpty(gameInfoPOS)) {
                Map<String, List<String>> venueIdToIdsMap = gameInfoPOS.stream()
                        .collect(Collectors.groupingBy(GameInfoPO::getVenueCode, Collectors.mapping(GameInfoPO::getId, Collectors.toList())));
                for (SiteVenueVO siteVenueVO : siteVenueVOS) {
                    String venueCode = siteVenueVO.getVenueCode();
                    if (venueIdToIdsMap.containsKey(venueCode)) {
                        siteVenueVO.setGameId(venueIdToIdsMap.get(venueCode));
                    }
                }
            }
        }
        List<String> venueCodeIds = siteVenueVOS.stream().map(SiteVenueVO::getVenueCode).toList();

        List<VenueInfoPO> list = baseMapper.selectList(Wrappers.lambdaQuery(VenueInfoPO.class).in(VenueInfoPO::getVenueCode, venueCodeIds));
        if (CollectionUtil.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.PARAM_NOT_VALID);
        }

        Map<Integer, List<VenueInfoPO>> checkSprot = list.stream()
                .filter(venue -> venue.getVenueType() == VenueTypeEnum.SPORTS.getCode()) // 过滤条件
                .collect(Collectors.groupingBy(VenueInfoPO::getVenueJoinType));

        if (ObjectUtils.isNotEmpty(checkSprot) && checkSprot.size() >= 2) {
            throw new BaowangDefaultException(ResultCode.VENUE_JOIN_TYPE_INVITE_CODE_ERROR);
        }

        //场馆修改前对象 by mufan
        List<VenueInfoPO> venueInfolist = baseMapper.selectList(Wrappers.<VenueInfoPO>query().select("venue_code,venue_name").groupBy("venue_code,venue_name"));
        Map<String, String> venueInfoMap = venueInfolist.stream()
                .collect(Collectors.toMap(
                        VenueInfoPO::getVenueCode,
                        v -> Objects.toString(v.getVenueName(), ""), // null 转 ""
                        (v1, v2) -> v1 // key 冲突时保留第一个
                ));
        List<SiteGamePO> siteGamebeforeList = siteGameRepository.selectList(Wrappers.lambdaQuery(SiteGamePO.class).eq(SiteGamePO::getSiteCode, siteCode));
        Map<String, String> gameInfoPOMap = gameInfoList.stream().collect(Collectors.toMap(GameInfoPO::getId, GameInfoPO::getGameName));
        Map<String, List<String>> venueBeforeGameList = new HashMap<>();
        siteGamebeforeList.forEach(e -> {
            List<String> games = venueBeforeGameList.get(venueInfoMap.get(e.getVenueCode()));
            if (CollectionUtil.isEmpty(games)) {
                games = new ArrayList<>();
            }
            games.add(gameInfoPOMap.get(e.getGameInfoId()));
            venueBeforeGameList.put(venueInfoMap.get(e.getVenueCode()), games);
        });

        //获取更改前场馆手续费率
        List<SiteVenuePO> siteVenuePOList = siteVenueRepository.selectList(Wrappers.lambdaQuery(SiteVenuePO.class).eq(SiteVenuePO::getSiteCode, siteCode));
        Map<String, List<SiteVenuePO>> siteVenueMap = siteVenuePOList.stream().collect(Collectors.groupingBy(SiteVenuePO::getVenueCode));
        Map<String, String> siteHandlingFeeBeforeList = new HashMap<>();
        siteVenuePOList.forEach(e -> {
            SiteVenueVO vo = new SiteVenueVO();
            String venuueName = venueInfoMap.get(e.getVenueCode());
            BeanUtils.copyProperties(e, vo);
            String fee = Objects.isNull(vo.getHandlingFee()) ? "0" : vo.getHandlingFee().toString();
            siteHandlingFeeBeforeList.put(venuueName, fee);
        });
        Map<String, String> siteValidProportionBeforeList = new HashMap<>();
        siteVenuePOList.forEach(e -> {
            SiteVenueVO vo = new SiteVenueVO();
            String venuueName = venueInfoMap.get(e.getVenueCode());
            BeanUtils.copyProperties(e, vo);
            String fee = Objects.isNull(vo.getValidProportion()) ? "0" : vo.getValidProportion().toString();
            siteValidProportionBeforeList.put(venuueName, fee);
        });
        //end by mufan

        //差值要删除
        if (CollectionUtil.isNotEmpty(siteVenuePOList)) {
            List<String> venueList = new ArrayList<>(siteVenuePOList.stream().map(SiteVenuePO::getVenueCode).toList());
            venueList.removeAll(venueCodeIds);
            if (CollectionUtil.isNotEmpty(venueList)) {
                //已经取消的场馆 游戏也要删除
                List<SiteGamePO> siteGamePOList = siteGameRepository.selectList(Wrappers.lambdaQuery(SiteGamePO.class).eq(SiteGamePO::getSiteCode, siteCode)
                        .in(SiteGamePO::getVenueCode, venueList));
                if (CollectionUtil.isNotEmpty(siteGamePOList)) {
                    List<String> gameList = siteGamePOList.stream().map(SiteGamePO::getGameInfoId).toList();
//                    gameJoinClassService.getBaseMapper().delete(Wrappers.lambdaQuery(GameJoinClassPO.class)
//                            .eq(GameJoinClassPO::getSiteCode, siteCode)
//                            .in(GameJoinClassPO::getGameId, gameList));
                    gameTwoCurrencySortService.getBaseMapper().delete(Wrappers.lambdaQuery(GameTwoCurrencySortPO.class)
                            .eq(GameTwoCurrencySortPO::getSiteCode, siteCode)
                            .in(GameTwoCurrencySortPO::getGameId, gameList));
                    siteGameRepository.delete(Wrappers.lambdaQuery(SiteGamePO.class)
                            .in(SiteGamePO::getId, siteGamePOList.stream().map(SiteGamePO::getId).toList()));
                }
                siteVenueRepository.delete(Wrappers.lambdaQuery(SiteVenuePO.class).eq(SiteVenuePO::getSiteCode, siteCode).in(SiteVenuePO::getVenueCode, venueList));
            }
        }

        //修改站点费率
        for (SiteVenueVO tmp : siteVenueVOS) {
            SiteVenuePO po = SiteVenuePO.builder().validProportion(tmp.getValidProportion()).handlingFee(tmp.getHandlingFee()).build();
            siteVenueRepository.update(po, Wrappers.lambdaQuery(SiteVenuePO.class)
                    .eq(SiteVenuePO::getSiteCode, siteCode)
                    .eq(SiteVenuePO::getVenueCode, tmp.getVenueCode()));
        }


        List<String> venueCodeList = list.stream().map(VenueInfoPO::getVenueCode).toList();
        long updTime = System.currentTimeMillis();


        List<SiteVenuePO> addSiteVenueList = Lists.newArrayList();
        for (SiteVenueVO siteVenueVO : siteVenueVOS) {
            if (!venueCodeList.contains(siteVenueVO.getVenueCode())) {
                continue;
            }

            if (CollectionUtil.isNotEmpty(siteVenueMap.get(siteVenueVO.getVenueCode()))) {
                continue;
            }

            SiteVenuePO venue = SiteVenuePO.builder()
                    .venueId(siteVenueVO.getVenueId())
                    .venueCode(siteVenueVO.getVenueCode())
                    .handlingFee(siteVenueVO.getHandlingFee())
                    .validProportion(siteVenueVO.getValidProportion())
                    .siteCode(siteCode).build();
            String operator = siteVenueVO.getOperator();
            venue.setCreator(operator);
            venue.setStatus(StatusEnum.CLOSE.getCode());//新分配的场馆默认禁用
            venue.setUpdater(operator);
            venue.setUpdatedTime(updTime);
            venue.setCreatedTime(updTime);
            addSiteVenueList.add(venue);
//            siteVenueRepository.insert(venue);
        }

        if(CollectionUtil.isNotEmpty(addSiteVenueList)){
            siteVenueService.saveBatch(addSiteVenueList);
            List<String> addVenueCodeList = addSiteVenueList.stream().map(SiteVenuePO::getVenueCode).toList();
            //初始化总控场馆配置到站点
            siteVenueConfigService.addSiteVenueConfig(addVenueCodeList,siteCode);
        }


        List<String> gameInfoIds = gameInfoList.stream().map(GameInfoPO::getId).toList();


        //取出所有的配置游戏
        List<String> allGameIds = Lists.newArrayList();
        for (SiteVenueVO siteVenueVO : siteVenueVOS) {
            List<String> venueVoGameIds = siteVenueVO.getGameId();
            if (CollectionUtil.isEmpty(venueVoGameIds)) {
                continue;
            }

            venueVoGameIds.retainAll(gameInfoIds);

            if (CollectionUtil.isEmpty(venueVoGameIds)) {
                continue;
            }
            allGameIds.addAll(venueVoGameIds);
        }
        if (CollectionUtil.isNotEmpty(allGameIds)) {
            siteGameService.addSiteGame(siteCode, new ArrayList<>(allGameIds));
        }

        //如果没有传游戏.直接删除所有游戏
        if (CollectionUtil.isEmpty(allGameIds)) {
            siteGameRepository.delete(Wrappers.lambdaQuery(SiteGamePO.class).eq(SiteGamePO::getSiteCode, siteCode));
//            gameJoinClassService.getBaseMapper().delete(Wrappers.lambdaQuery(GameJoinClassPO.class).eq(GameJoinClassPO::getSiteCode, siteCode));
            gameTwoCurrencySortService.getBaseMapper().delete(Wrappers.lambdaQuery(GameTwoCurrencySortPO.class)
                    .eq(GameTwoCurrencySortPO::getSiteCode, siteCode));
//            RedisUtil.deleteKey(RedisConstants.getWildcardsKey(RedisConstants.KEY_GAME_JOIN_CLASS));
        }

//        RedisUtil.deleteKeysByPatternList(Lists.newArrayList(RedisConstants.getWildcardsKey(String.format(RedisConstants.KEY_QUERY_LOBBY_TOP_GAME, "*")),
//                RedisConstants.getWildcardsKey(RedisConstants.KEY_SITE_GAME_LIST)
//                , RedisConstants.getWildcardsKey(RedisConstants.KEY_SITE_VENUE_LIST),
//                RedisConstants.getWildcardsKey(String.format(RedisConstants.SITE_LOBBY_LABEL, "*"))
//                , RedisConstants.getWildcardsKey(RedisConstants.KEY_SITE_VENUE_LIST)));

        LobbyCateUtil.deleteLobbySiteGameInfo(siteCode);

        //添加获取选中后数据 by mufan
        List<SiteGamePO> siteGameAfterList = siteGameRepository.selectList(Wrappers.lambdaQuery(SiteGamePO.class).eq(SiteGamePO::getSiteCode, siteCode));
        Map<String, List<String>> venueAfterGameList = new HashMap<>();
        siteGameAfterList.forEach(e -> {
            List<String> games = venueAfterGameList.get(venueInfoMap.get(e.getVenueCode()));
            if (CollectionUtil.isEmpty(games)) {
                games = new ArrayList<>();
            }
            games.add(gameInfoPOMap.get(e.getGameInfoId()));
            venueAfterGameList.put(venueInfoMap.get(e.getVenueCode()), games);
        });
        SiteInfoChangeBodyVO data = new SiteInfoChangeBodyVO();
        data.setChangeBeforeObj(venueBeforeGameList);
        data.setChangeAfterObj(venueAfterGameList);
        Map<String, String> dataClounmName = new HashMap();
        dataClounmName.put(SitClounmDefaultEnum.baseClounm.getCode(), SiteChangeTypeEnum.VenueAuthor.getname());
        data.setColumnNameMap(dataClounmName);
        data.setChangeType(SiteChangeTypeEnum.VenueAuthor.getname());
        List<JsonDifferenceVO> changevo = siteInfoChangeRecordApi.getJsonDifferenceList(data);

        List<SiteVenuePO> siteVenueAfterPOList = siteVenueRepository.selectList(Wrappers.lambdaQuery(SiteVenuePO.class).eq(SiteVenuePO::getSiteCode, siteCode));
        Map<String, String> siteHandlingFeeAfterList = new HashMap<>();
        siteVenueAfterPOList.forEach(e -> {
            SiteVenueVO vo = new SiteVenueVO();
            String venuueName = venueInfoMap.get(e.getVenueCode());
            BeanUtils.copyProperties(e, vo);
            String fee = Objects.isNull(vo.getHandlingFee()) ? "0" : vo.getHandlingFee().toString();
            siteHandlingFeeAfterList.put(venuueName, fee);
        });
        Map<String, String> siteValidProportionAfterList = new HashMap<>();
        siteVenueAfterPOList.forEach(e -> {
            SiteVenueVO vo = new SiteVenueVO();
            String venuueName = venueInfoMap.get(e.getVenueCode());
            BeanUtils.copyProperties(e, vo);
            String fee = Objects.isNull(vo.getValidProportion()) ? "0" : vo.getValidProportion().toString();
            siteValidProportionAfterList.put(venuueName, fee);
        });
        //场馆负盈利手续费
        SiteInfoChangeBodyVO siteVenue = new SiteInfoChangeBodyVO();
        siteVenue.setChangeBeforeObj(siteHandlingFeeBeforeList);
        siteVenue.setChangeAfterObj(siteHandlingFeeAfterList);
        siteVenue.setChangeType(SiteChangeTypeEnum.VenueAuthor.getname());
        Map<String, String> siteVenueName = new HashMap();
        siteVenueName.put(SitClounmDefaultEnum.baseClounm.getCode(), SitClounmDefaultEnum.handlingFee.getname());
        siteVenue.setColumnNameMap(siteVenueName);
        List<JsonDifferenceVO> sitadataList = new ArrayList<>();
        List<JsonDifferenceVO> siteVenuevo = siteInfoChangeRecordApi.getJsonDifferenceList(siteVenue);
        //场馆有效流水费率
        SiteInfoChangeBodyVO siteVenueValidProportion = new SiteInfoChangeBodyVO();
        siteVenueValidProportion.setChangeBeforeObj(siteValidProportionBeforeList);
        siteVenueValidProportion.setChangeAfterObj(siteValidProportionAfterList);
        siteVenueValidProportion.setChangeType(SiteChangeTypeEnum.VenueAuthor.getname());
        Map<String, String> siteVenueValidProportionName = new HashMap();
        siteVenueValidProportionName.put(SitClounmDefaultEnum.baseClounm.getCode(), SitClounmDefaultEnum.validProportion.getname());
        siteVenueValidProportion.setColumnNameMap(siteVenueValidProportionName);
        List<JsonDifferenceVO> siteVenuevoValidProportionName = siteInfoChangeRecordApi.getJsonDifferenceList(siteVenueValidProportion);
        SiteInfoChangeRecordListReqVO vo = new SiteInfoChangeRecordListReqVO();
        vo.setLoginIp(CurrReqUtils.getReqIp());
        vo.setCreator(CurrReqUtils.getAccount());
        vo.setOptionType(SiteOptionTypeEnum.DataUpdate.getCode());
        vo.setOptionStatus(SiteOptionStatusEnum.success.getCode());
        vo.setOptionModelName(SiteOptionModelNameEnum.site.getname());
        vo.setOptionCode(siteCode);
        vo.setOptionName(siteName);
        sitadataList.addAll(changevo);
        sitadataList.addAll(siteVenuevo);
        sitadataList.addAll(siteVenuevoValidProportionName);
        vo.setData(sitadataList);
        siteInfoChangeRecordApi.addJsonDifferenceList(vo);
        //end by mufan
        log.info("站点初始化游戏结束---{}",(System.currentTimeMillis()-start)/1000);
        return Boolean.TRUE;
    }

    public SiteVenueResponseVO queryVenueAuthorize(SiteVenueRequestVO siteVenueRequestVO) {
        SiteVenueResponseVO resultVO = new SiteVenueResponseVO();
        Page<VenueInfoPO> page = new Page<>(siteVenueRequestVO.getPageNumber(), siteVenueRequestVO.getPageSize());
        Page<SiteVenueResponsePageVO> result = siteVenueRepository.queryVenueAuthorize(page, siteVenueRequestVO);
        Map<String, String> venueMap = systemParamApi.getSystemParamMapInner(CommonConstant.VENUE_TYPE);
        result.getRecords().forEach(obj -> obj.setVenueTypeName(venueMap.get(String.valueOf(obj.getVenueType()))));
        //当前站点已经选择的场馆(编辑进入)
        List<FeeVO> chooseFee = Lists.newArrayList();
        //所有的场馆数据
        List<FeeVO> allFee = Lists.newArrayList();
        List<FeeVO> finalChooseFee = chooseFee;
        siteVenueRepository.selectList(new LambdaQueryWrapper<SiteVenuePO>()
                        .eq(SiteVenuePO::getSiteCode, siteVenueRequestVO.getSiteCode()))
                .forEach(obj -> finalChooseFee.add(FeeVO.builder().venueCode(obj.getVenueCode()).validProportion(obj.getValidProportion()).fee(obj.getHandlingFee()).venueId(obj.getVenueId()).build()));

        baseMapper.selectList(new LambdaQueryWrapper<>())
                .forEach(obj -> allFee.add(FeeVO.builder().venueCode(obj.getVenueCode()).venueId(obj.getId()).validProportion(obj.getValidProportion()).fee(obj.getVenueProportion()).build()));
        String siteCode = siteVenueRequestVO.getSiteCode();

        if (CollectionUtil.isNotEmpty(finalChooseFee) && StringUtils.isNotBlank(siteCode)) {
            log.info("当前站点:{},存在老场馆数据:{}", siteVenueRequestVO.getSiteCode(), JSON.toJSONString(finalChooseFee));
            //可能存在站点老数据现在总数据中不存在了,移除掉勾选的数据
            Set<String> allFeeIds = allFee.stream()
                    .map(FeeVO::getVenueCode)
                    .collect(Collectors.toSet());
            //回显勾选,只展示当前场馆总台数据中,存在的
            chooseFee = chooseFee.stream()
                    .filter(fee -> allFeeIds.contains(fee.getVenueCode()))
                    .collect(Collectors.toList());

            //场馆code相同,币种不同,存在多条数据,分页做了分组,所以查询勾选时,只保留当前页面展示对应的那一条场馆code对应的数据
            List<SiteVenueResponsePageVO> records = result.getRecords();
            List<String> resultNeedShowVenueIds = records.stream().map(SiteVenueResponsePageVO::getVenueCode).toList();
            chooseFee = chooseFee.stream().filter(item -> resultNeedShowVenueIds.contains(item.getVenueCode())).toList();

            if (CollectionUtil.isNotEmpty(chooseFee)) {
                //获取到站点的场馆id
                List<String> siteVenueIds = chooseFee.stream()
                        .map(FeeVO::getVenueId)
                        .toList();
                LambdaQueryWrapper<SiteVenuePO> siteVenueQuery = Wrappers.lambdaQuery();
                siteVenueQuery.in(SiteVenuePO::getVenueId, siteVenueIds).eq(SiteVenuePO::getSiteCode, siteCode);
                List<SiteVenuePO> siteVenuePOS = siteVenueRepository.selectList(siteVenueQuery);
                if (CollectionUtil.isNotEmpty(siteVenuePOS)) {
                    List<String> venueCodes = siteVenuePOS.stream().map(SiteVenuePO::getVenueCode).toList();
                    //站点关联场馆不为空，反查一下站点场馆对应的游戏，找到gameInfo id
                    LambdaQueryWrapper<SiteGamePO> siteGameQuery = Wrappers.lambdaQuery();
                    siteGameQuery.eq(SiteGamePO::getSiteCode, siteCode).in(SiteGamePO::getVenueCode, venueCodes);


                    List<SiteGamePO> siteGamePOS = siteGameRepository.selectList(siteGameQuery);
                    if (CollectionUtil.isNotEmpty(siteGamePOS)) {
                        List<GameInfoPO> allGame = gameInfoRepository.selectList(new QueryWrapper<>());
                        // 创建一个包含 allGame 中所有 ID 的集合
                        Set<String> allGameIds = allGame.stream()
                                .map(GameInfoPO::getId)
                                .collect(Collectors.toSet());
                        // 过滤 siteGamePOS，只保留在 allGame 中存在的项
                        siteGamePOS = siteGamePOS.stream()
                                .filter(siteGame -> allGameIds.contains(siteGame.getGameInfoId()))
                                .collect(Collectors.toList());

                        Map<String, List<String>> gameInfoIdsMap = siteGamePOS.stream()
                                .collect(Collectors.groupingBy(
                                        SiteGamePO::getVenueCode,
                                        Collectors.mapping(
                                                SiteGamePO::getGameInfoId,
                                                Collectors.toList()
                                        )
                                ));
                        List<SiteVenueAuthorizeQueryVO> venueRes = new ArrayList<>();
                        for (FeeVO feeVO : chooseFee) {
                            SiteVenueAuthorizeQueryVO queryVO = new SiteVenueAuthorizeQueryVO();
                            queryVO.setVenueId(feeVO.getVenueId());
                            queryVO.setVenueCode(feeVO.getVenueCode());
                            queryVO.setHandlingFee(feeVO.getFee());
                            queryVO.setValidProportion(feeVO.getValidProportion());
                            queryVO.setGameId(gameInfoIdsMap.get(feeVO.getVenueCode()));
                            venueRes.add(queryVO);
                        }
                        resultVO.setSiteVenue(venueRes);
                    }
                }
            }
        }

        resultVO.setAllVenueFee(allFee);
        resultVO.setPageVO(result);
        return resultVO;
    }


    public List<SiteVenueQueryVO> querySiteVenueBySiteCode(String siteCode) {
        //查询场馆基础信息
        List<SiteVenueQueryVO> queryVOS = siteVenueRepository.querySiteVenueBySiteCode(siteCode);
        if (CollectionUtil.isNotEmpty(queryVOS)) {
            List<String> venueCodes = queryVOS.stream()
                    .map(SiteVenueQueryVO::getVenueCode)
                    .toList();
            //游戏信息
            List<SiteGameQueryVO> siteGameQueryVOS = siteGameRepository.queryGameBySiteCodeVenueCode(siteCode, venueCodes);
            Map<String, List<SiteGameQueryVO>> venueCodeMap = siteGameQueryVOS.stream()
                    .collect(Collectors.groupingBy(SiteGameQueryVO::getVenueCode));
            queryVOS.forEach(item -> {
                if (venueCodeMap.containsKey(item.getVenueCode())) {
                    item.setSiteGameQueryVOS(venueCodeMap.get(item.getVenueCode()));
                }
            });
            return queryVOS;
        }
        return new ArrayList<>();
    }

    public List<VenueInfoVO> getSystemVenuesByIds(List<String> ids) {
        LambdaQueryWrapper<VenueInfoPO> query = Wrappers.lambdaQuery();
        query.in(VenueInfoPO::getVenueCode, ids);
        List<VenueInfoPO> venueInfoPOS = baseMapper.selectList(query);
        return BeanUtil.copyToList(venueInfoPOS, VenueInfoVO.class);
    }

    public boolean isClose(String venueCode) {
        VenueInfoPO venueInfoPO = baseMapper.selectOne(Wrappers.<VenueInfoPO>lambdaQuery().eq(VenueInfoPO::getVenueCode, venueCode).last("limit 1"));
        if (venueInfoPO == null) {
            return true;
        }
        if (venueInfoPO.getStatus() == null || venueInfoPO.getStatus().equals(CommonConstant.business_three)) {
            return true;
        }
        return false;

    }

    //getVenueInfoByVenueCode
    public VenueInfoVO getVenueInfoByVenueCode(String venueCode) {
        VenueInfoPO venueInfoPO = baseMapper.selectOne(Wrappers.<VenueInfoPO>lambdaQuery().eq(VenueInfoPO::getVenueCode, venueCode).last("limit 1"));
        return BeanUtil.copyProperties(venueInfoPO, VenueInfoVO.class);
    }

    public VenueInfoVO getVenueInfoByMerchantNo(String venueCode,String merchantNo) {
        VenueInfoPO venueInfoPO = baseMapper.selectOne(Wrappers.<VenueInfoPO>lambdaQuery()
                .eq(VenueInfoPO::getVenueCode, venueCode)
                .eq(VenueInfoPO::getMerchantNo, merchantNo)
                .last("limit 1"));
        return BeanUtil.copyProperties(venueInfoPO, VenueInfoVO.class);
    }

    public List<VenueInfoVO> getVenueInfoList(String venueCode) {
        List<VenueInfoPO> venueInfoPO = baseMapper.selectList(Wrappers.<VenueInfoPO>lambdaQuery().eq(VenueInfoPO::getVenueCode, venueCode));
        return BeanUtil.copyToList(venueInfoPO, VenueInfoVO.class);
    }


    public GameOneClassVenueCurrencyVO getGameOneVenueJoin(GameClassInfoDeleteVO req) {

        String siteCode = CurrReqUtils.getSiteCode();
        SiteVO siteVO = siteApi.getSiteInfo(siteCode).getData();


        VenueInfoRequestVO venueInfoQueryVO = VenueInfoRequestVO.builder().venueJoinType(VenueJoinTypeEnum.VENUE.getCode()).build();


//        List<VenueInfoVO> venueList = venueInfoListByParam(venueInfoQueryVO);
        venueInfoQueryVO.setPageSize(-1);
        List<VenueInfoVO> venueList = getSiteVenueInfoPage(venueInfoQueryVO).getRecords();


        Map<String, VenueInfoVO> venueInfoMap = venueList.stream().collect(Collectors.toMap(VenueInfoVO::getVenueCode, Function.identity()));

        String[] currencyList = siteVO.getCurrencyCodes().split(",");


        List<GameOneClassCurrencyListVO> allList = Lists.newArrayList();
        for (String currencyCode : currencyList) {
            List<GameOneClassVenueInfoVO> currencyVenue = Lists.newArrayList();
            for (VenueInfoVO item : venueList) {
                if (CollectionUtil.isNotEmpty(item.getCurrencyCodeList()) && item.getCurrencyCodeList().contains(currencyCode)) {
                    GameOneClassVenueInfoVO currencyListVO = GameOneClassVenueInfoVO
                            .builder()
                            .venueCode(item.getVenueCode())
                            .venueName(I18nMessageUtil.getI18NMessageInAdvice(item.getVenueName()))
                            .venueType(item.getVenueType())
                            .status(item.getStatus())
                            .build();
                    currencyVenue.add(currencyListVO);
                }
            }

            GameOneClassCurrencyListVO oneClassCurrencyListVO = GameOneClassCurrencyListVO.builder()
                    .currencyCode(currencyCode)
                    .venueCodeList(currencyVenue)
                    .build();
            allList.add(oneClassCurrencyListVO);
        }

        List<GameOneClassCurrencyListVO> inList = Lists.newArrayList();
        if (ObjectUtil.isNotEmpty(req.getId())) {
            List<GameOneVenuePO> gameOneVenueList = gameOneVenueRepository.selectList(Wrappers.lambdaQuery(GameOneVenuePO.class)
                    .eq(GameOneVenuePO::getGameOneId, req.getId()));
            Map<String, List<GameOneVenuePO>> gameOneVenueMap = gameOneVenueList.stream().collect(Collectors.groupingBy(GameOneVenuePO::getCurrencyCode));

            for (Map.Entry<String, List<GameOneVenuePO>> item : gameOneVenueMap.entrySet()){

                String currencyCode = item.getKey();

                List<GameOneVenuePO> currencyVenueList = item.getValue();


                List<GameOneClassVenueInfoVO> venueCodeList = Lists.newArrayList();
                for (GameOneVenuePO venue : currencyVenueList){
                    String venueCode = venue.getVenueCode();
                    VenueInfoVO venueInfoVO = venueInfoMap.get(venueCode);
                    if(ObjectUtil.isEmpty(venueInfoVO)){
                        continue;
                    }

                    GameOneClassVenueInfoVO infoVO = GameOneClassVenueInfoVO.builder()
                            .venueCode(venueCode)
                            .venueType(venueInfoVO.getVenueType())
                            .venueName(I18nMessageUtil.getI18NMessageInAdvice(venueInfoVO.getVenueName()))
                            .status(venueInfoVO.getStatus())
                            .build();
                    venueCodeList.add(infoVO);
                }
                inList.add(GameOneClassCurrencyListVO.builder().currencyCode(currencyCode).venueCodeList(venueCodeList).build());
            }
        }
        return GameOneClassVenueCurrencyVO.builder().allList(allList).inList(inList).build();
    }


    @Transactional(rollbackFor = Exception.class)
    public void initVenueSiteConfig(){
        List<VenueInfoPO> venueInfoList = baseMapper.selectList(null);

        List<String> venueList = venueInfoList.stream().map(VenueInfoPO::getVenueCode).toList();

        venueList = venueList.stream().distinct().toList();

        List<SiteVO> siteVOList = siteApi.allSiteInfo().getData();

        //初始化总控场馆配置到站点
        for (SiteVO siteVO : siteVOList) {
            siteVenueConfigService.addSiteVenueConfig(venueList, siteVO.getSiteCode());
        }
    }

    public Map<String, SiteVenueConfigPO> getMapVenueCodeBySiteCode(String siteCode) {
        List<SiteVenueConfigPO> list = siteVenueConfigService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteVenueConfigPO.class)
                .eq(SiteVenueConfigPO::getSiteCode, siteCode));
        return list.stream().collect(Collectors.toMap(SiteVenueConfigPO::getVenueCode, Function.identity()));
    }

    public String getVenueNameCodeBySiteCodeVenueCode(String siteCode,String venueCode){
        Map<String, SiteVenueConfigPO> venueMap = getMapVenueCodeBySiteCode(siteCode);
        SiteVenueConfigPO siteVenueConfigPO = venueMap.get(venueCode);
        if(ObjectUtil.isEmpty(siteVenueConfigPO)){
            return null;
        }
        return siteVenueConfigPO.getVenueName();
    }

}
