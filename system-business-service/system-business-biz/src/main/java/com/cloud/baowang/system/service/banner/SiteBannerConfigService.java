package com.cloud.baowang.system.service.banner;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.enums.SkinTemplateCodeEnums;
import com.cloud.baowang.system.api.enums.banner.BannerDuration;
import com.cloud.baowang.system.api.vo.banner.*;
import com.cloud.baowang.system.po.banner.SiteBannerConfigPO;
import com.cloud.baowang.system.po.site.SitePO;
import com.cloud.baowang.system.repositories.SiteRepository;
import com.cloud.baowang.system.repositories.banner.SiteBannerConfigMapper;
import com.cloud.baowang.system.service.I18nMessageService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteBannerConfigService extends ServiceImpl<SiteBannerConfigMapper, SiteBannerConfigPO> {
    private final SiteBannerConfigMapper siteBannerConfigMapper;
//    private final I18nApi i18nApi;
    private final SiteRepository siteRepository;

    private final I18nMessageService i18nMessageService;


    public ResponseVO<Page<SiteBannerConfigPageRespVO>> getPage(SiteBannerConfigPageQueryReqVO reqVO) {
        Page<SiteBannerConfigPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        LambdaQueryWrapper<SiteBannerConfigPO> query = Wrappers.lambdaQuery();
        // 判断站点编码
        if (StringUtils.isNotBlank(reqVO.getSiteCode())) {
            query.eq(SiteBannerConfigPO::getSiteCode, reqVO.getSiteCode());
        }

        if (StringUtils.isNotBlank(reqVO.getBannerName())) {
            query.eq(SiteBannerConfigPO::getBannerName, reqVO.getBannerName());
        }

        // 判断展示位置
        if (StringUtils.isNotBlank(reqVO.getGameOneClassId())) {
            query.eq(SiteBannerConfigPO::getGameOneClassId, reqVO.getGameOneClassId());
        }

        // 判断轮播图区域
        if (reqVO.getBannerArea() != null) {
            query.eq(SiteBannerConfigPO::getBannerArea, reqVO.getBannerArea());
        }

        // 判断时效
        if (reqVO.getBannerDuration() != null) {
            query.eq(SiteBannerConfigPO::getBannerDuration, reqVO.getBannerDuration());
        }

        // 判断展示开始时间
        if (reqVO.getDisplayStartTime() != null) {
            query.ge(SiteBannerConfigPO::getDisplayStartTime, reqVO.getDisplayStartTime());
        }

        // 判断展示结束时间
        if (reqVO.getDisplayEndTime() != null) {
            query.le(SiteBannerConfigPO::getDisplayEndTime, reqVO.getDisplayEndTime());
        }

        // 判断是否跳转
        if (reqVO.getIsRedirect() != null) {
            query.eq(SiteBannerConfigPO::getIsRedirect, reqVO.getIsRedirect());
        }

        // 判断跳转目标
        if (reqVO.getRedirectTarget() != null) {
            query.eq(SiteBannerConfigPO::getRedirectTarget, reqVO.getRedirectTarget());
        }

        // 判断跳转目标地址配置
        if (StringUtils.isNotBlank(reqVO.getRedirectTargetConfig())) {
            query.eq(SiteBannerConfigPO::getRedirectTargetConfig, reqVO.getRedirectTargetConfig());
        }

        // 判断启用状态
        if (reqVO.getStatus() != null) {
            query.eq(SiteBannerConfigPO::getStatus, reqVO.getStatus());
        }
        if (StringUtils.isNotBlank(reqVO.getCreator())) {
            query.eq(SiteBannerConfigPO::getCreator, reqVO.getCreator());
        }
        if (StringUtils.isNotBlank(reqVO.getUpdater())) {
            query.eq(SiteBannerConfigPO::getUpdater, reqVO.getUpdater());
        }
        query.orderByDesc(SiteBannerConfigPO::getCreatedTime);
        query.orderByAsc(SiteBannerConfigPO::getSort);
        page = this.page(page, query);
        int yesCode = Integer.parseInt(YesOrNoEnum.YES.getCode());
        int noCode = Integer.parseInt(YesOrNoEnum.NO.getCode());
        long nowTime = System.currentTimeMillis();
        LambdaQueryWrapper<SitePO> siteQuery = Wrappers.lambdaQuery();
        siteQuery.eq(SitePO::getSiteCode, reqVO.getSiteCode());
        SitePO sitePO = siteRepository.selectOne(siteQuery);
        if (sitePO == null) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        return ResponseVO.success(ConvertUtil.toConverPage(page.convert(item -> {
            SiteBannerConfigPageRespVO vo = BeanUtil.copyProperties(item, SiteBannerConfigPageRespVO.class);
//            if(sitePO.getSkin().equals(SkinTemplateCodeEnums.SKIN_ONE.getSkinCode())){
//                //常规皮肤站点,查询置空h5地址
//                item.setH5BannerName("");
//            }
            //长期banner,默认可以启用
            Integer bannerDuration = vo.getBannerDuration();
            if (BannerDuration.LONG_TERM.getCode().equals(bannerDuration)) {
                //长期活动,默认都可以启用
                vo.setIsEnable(yesCode);
            } else {
                //看看有没有过有效期,如果有效期比当前时间小,说明已过期,则不允许启用了,同时设置启用状态为禁用
                Long displayEndTime = vo.getDisplayEndTime();
                if (nowTime > displayEndTime) {
                    vo.setStatus(EnableStatusEnum.DISABLE.getCode());
                    vo.setIsEnable(noCode);
                } else {
                    vo.setIsEnable(yesCode);
                }
            }
            return vo;
        })));
    }


    public List<SiteBannerConfigPO> getListBySiteCode(String siteCode) {
        if (ObjectUtil.isEmpty(siteCode)) {
            return Lists.newArrayList();
        }
        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, RedisConstants.KEY_LOBBY_BANNER);
        List<SiteBannerConfigPO> list = RedisUtil.getValue(key);
        if (CollectionUtil.isNotEmpty(list)) {
            return list;
        }
        List<SiteBannerConfigPO> bannerList = this.list(Wrappers
                .lambdaQuery(SiteBannerConfigPO.class)
                .eq(SiteBannerConfigPO::getSiteCode, siteCode)
                .orderByAsc(SiteBannerConfigPO::getSort));
        if (CollectionUtil.isNotEmpty(bannerList)) {
            RedisUtil.setValue(key, bannerList, 10L, TimeUnit.MINUTES);
        }
        return bannerList;
    }

    public ResponseVO<List<SiteBannerConfigPageRespVO>> getListBySiteCode(SiteBannerConfigAppQueryVO queryVO) {
        String siteCode = queryVO.getSiteCode();

        List<SiteBannerConfigPO> list = getListBySiteCode(siteCode);
        if (CollectionUtil.isEmpty(list)) {
            return ResponseVO.success(Lists.newArrayList());
        }

        Integer status = queryVO.getStatus();
        if (status != null) {
            list = list.stream().filter(x -> status.equals(x.getStatus())).toList();
        }

        String gameOneClassId = queryVO.getGameOneClassId();
        if (StringUtils.isNotBlank(gameOneClassId)) {
            list = list.stream().filter(x -> gameOneClassId.equals(x.getGameOneClassId())).toList();
        }


        Long siteTime = queryVO.getSiteTime();
        long switchTime = 2L;
        if (CollectionUtil.isNotEmpty(list) && siteTime != null) {
            /*ResponseVO<SystemDictConfigRespVO> resp = configService.getByCode(DictCodeConfigEnums.BANNER_AUTO_SWITCH_TIME.getCode(), siteCode);

            if (resp.isOk()) {
                SystemDictConfigRespVO data = resp.getData();
                String configParam = data.getConfigParam();
                if (StringUtils.isNotBlank(configParam)) {
                    BigDecimal bigDecimal = new BigDecimal(configParam);
                    switchTime = bigDecimal.longValue();
                }
            }*/
//            list = list.stream()
//                    .filter(banner -> !banner.getBannerDuration().equals(BannerDuration.LIMITED_TIME.getCode()) ||
//                            (banner.getDisplayStartTime() > siteTime || banner.getDisplayEndTime() < siteTime))
//                    .collect(Collectors.toList());

            list = list.stream()
                    .filter(banner -> {
                        if (BannerDuration.LONG_TERM.getCode().equals(banner.getBannerDuration())) {
                            return siteTime >= banner.getDisplayStartTime();
                        }
                        return siteTime >= banner.getDisplayStartTime() && siteTime <= banner.getDisplayEndTime();
                    })
                    .collect(Collectors.toList());
        }
        List<SiteBannerConfigPageRespVO> respVOS = BeanUtil.copyToList(list, SiteBannerConfigPageRespVO.class);
        if (CollectionUtil.isNotEmpty(respVOS)) {
            long finalSwitchTime = switchTime;
            respVOS.forEach(item -> item.setSwitchTime(finalSwitchTime));
        }
        return ResponseVO.success(respVOS);
    }

    @Transactional
    public ResponseVO<Boolean> createConfig(SiteBannerConfigReqVO bannerConfigVO) {
        LambdaQueryWrapper<SitePO> siteQuery = Wrappers.lambdaQuery();
        siteQuery.eq(SitePO::getSiteCode, bannerConfigVO.getSiteCode());
        SitePO sitePO = siteRepository.selectOne(siteQuery);
        if (sitePO == null) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        //皮肤2需要配置h5banner
        if (sitePO.getSkin().equals(SkinTemplateCodeEnums.SKIN_TWO.getSkinCode())
                && (CollectionUtil.isEmpty(bannerConfigVO.getH5BannerNameList())
                || CollectionUtil.isEmpty(bannerConfigVO.getBannerUrlList()))) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        // 创建新的轮播图配置
        SiteBannerConfigPO po = BeanUtil.copyProperties(bannerConfigVO, SiteBannerConfigPO.class);
        po.setStatus(EnableStatusEnum.DISABLE.getCode());
        po.setCreator(bannerConfigVO.getOperator());
        po.setUpdater(bannerConfigVO.getOperator());
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdatedTime(System.currentTimeMillis());
        if (bannerConfigVO.getBannerDuration().equals(BannerDuration.LIMITED_TIME.getCode())) {
            if (bannerConfigVO.getDisplayStartTime() == null || bannerConfigVO.getDisplayEndTime() == null) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        } else {
            if (bannerConfigVO.getDisplayStartTime() == null) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }

        Map<String, List<I18nMsgFrontVO>> i18nData = Maps.newHashMap();
        List<I18nMsgFrontVO> bannerUrlList =  bannerConfigVO.getBannerUrlList();

        //PC
        if(CollectionUtil.isNotEmpty(bannerUrlList)){
            for (I18nMsgFrontVO item : bannerUrlList){
                if(ObjectUtil.isEmpty(item.getMessage())){
                    item.setMessage("");
                }
            }
            String bannerI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_BANNER_IMAGE.getCode());
            i18nData.put(bannerI18,bannerUrlList);
            po.setBannerUrl(bannerI18);
        }

        List<I18nMsgFrontVO> darkBannerUrlList = bannerConfigVO.getDarkBannerUrlList();

        //PC -黑底
        if(CollectionUtil.isNotEmpty(darkBannerUrlList)){
            for (I18nMsgFrontVO item : darkBannerUrlList){
                if(ObjectUtil.isEmpty(item.getMessage())){
                    item.setMessage("");
                }
            }
            String darkBannerI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_BANNER_IMAGE.getCode());
            i18nData.put(darkBannerI18,darkBannerUrlList);
            po.setDarkBannerUrl(darkBannerI18);
        }


        //H5
        List<I18nMsgFrontVO> h5BannerUrlList = bannerConfigVO.getH5BannerNameList();
        if(CollectionUtil.isNotEmpty(h5BannerUrlList)){
            for (I18nMsgFrontVO item : h5BannerUrlList){
                if(ObjectUtil.isEmpty(item.getMessage())){
                    item.setMessage("");
                }
            }
            String h5bannerI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_BANNER_IMAGE.getCode());
            i18nData.put(h5bannerI18,h5BannerUrlList);
            po.setH5BannerName(h5bannerI18);
        }



        //H5 - 黑底
        List<I18nMsgFrontVO> darkH5BannerUrlList = bannerConfigVO.getDarkH5BannerUrlList();
        if(CollectionUtil.isNotEmpty(darkH5BannerUrlList)){
            for (I18nMsgFrontVO item : darkH5BannerUrlList){
                if(ObjectUtil.isEmpty(item.getMessage())){
                    item.setMessage("");
                }
            }
            String h5darkBannerI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_BANNER_IMAGE.getCode());
            i18nData.put(h5darkBannerI18,darkH5BannerUrlList);
            po.setDarkH5BannerUrl(h5darkBannerI18);
        }

        i18nMessageService.update(i18nData);



//        if (sitePO.getSkin().equals(SkinTemplateCodeEnums.SKIN_TWO.getSkinCode())) {
//            List<I18nMsgFrontVO> h5BannerUrlList = bannerConfigVO.getH5BannerUrlList();
//            String h5bannerI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_BANNER_IMAGE.getCode());
//            // 插入国际化信息
//            Map<String, List<I18nMsgFrontVO>> h5I18nData = Map.of(
//                    h5bannerI18, h5BannerUrlList);
//            i18nApi.update(h5I18nData);
//            po.setH5BannerName(h5bannerI18);
//        }
        int result = siteBannerConfigMapper.insert(po);
        return ResponseVO.success(result > 0);
    }

    @Transactional
    public ResponseVO<Boolean> updateConfig(SiteBannerConfigReqVO bannerConfigVO) {
        String id = bannerConfigVO.getId();
        SiteBannerConfigPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        LambdaUpdateWrapper<SiteBannerConfigPO> upd = Wrappers.lambdaUpdate();
        upd.eq(SiteBannerConfigPO::getId, id);
        if (bannerConfigVO.getBannerDuration().equals(BannerDuration.LIMITED_TIME.getCode())) {
            if (bannerConfigVO.getDisplayStartTime() == null || bannerConfigVO.getDisplayEndTime() == null) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            upd.set(SiteBannerConfigPO::getDisplayEndTime, bannerConfigVO.getDisplayEndTime());
        } else {
            upd.set(SiteBannerConfigPO::getDisplayEndTime, null);
        }
        upd.set(SiteBannerConfigPO::getBannerArea, bannerConfigVO.getBannerArea());
        upd.set(SiteBannerConfigPO::getBannerDuration, bannerConfigVO.getBannerDuration());
        upd.set(SiteBannerConfigPO::getDisplayStartTime, bannerConfigVO.getDisplayStartTime());
        upd.set(SiteBannerConfigPO::getIsRedirect, bannerConfigVO.getIsRedirect());
        upd.set(SiteBannerConfigPO::getRedirectTarget, bannerConfigVO.getRedirectTarget());
        upd.set(SiteBannerConfigPO::getRedirectTargetConfig, bannerConfigVO.getRedirectTargetConfig());
        upd.set(SiteBannerConfigPO::getBannerName, bannerConfigVO.getBannerName());
        upd.set(SiteBannerConfigPO::getStatus, EnableStatusEnum.DISABLE.getCode());
        upd.set(SiteBannerConfigPO::getUpdater, bannerConfigVO.getOperator());
        upd.set(SiteBannerConfigPO::getUpdatedTime, System.currentTimeMillis());

        // 插入国际化信息
//        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(
//                po.getBannerUrl(), bannerConfigVO.getBannerUrlList());
//        i18nApi.update(i18nData);

        LambdaQueryWrapper<SitePO> siteQuery = Wrappers.lambdaQuery();
        siteQuery.eq(SitePO::getSiteCode, bannerConfigVO.getSiteCode());
        SitePO sitePO = siteRepository.selectOne(siteQuery);
        if (sitePO == null) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        //皮肤2需要配置h5banner
//        if (sitePO.getSkin().equals(SkinTemplateCodeEnums.SKIN_TWO.getSkinCode()) &&
//                (CollectionUtil.isEmpty(bannerConfigVO.getH5BannerUrlList())||CollectionUtil.isEmpty(bannerConfigVO.getBannerUrlList()))) {
//            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//        }
//
//        if (sitePO.getSkin().equals(SkinTemplateCodeEnums.SKIN_TWO.getSkinCode())) {
//            List<I18nMsgFrontVO> h5BannerUrlList = bannerConfigVO.getH5BannerUrlList();
//            // 插入国际化信息
//            Map<String, List<I18nMsgFrontVO>> h5I18nData = Map.of(
//                    po.getH5BannerName(), h5BannerUrlList);
//            i18nApi.update(h5I18nData);
//        }

        Map<String, List<I18nMsgFrontVO>> i18nData = Maps.newHashMap();
        List<I18nMsgFrontVO> bannerUrlList =  bannerConfigVO.getBannerUrlList();
        List<I18nMsgFrontVO> bannerUrlListDark = bannerConfigVO.getDarkBannerUrlList();

        //PC
        String bannerUrl = bannerConfigVO.getBannerUrl();
        if(CollectionUtil.isNotEmpty(bannerUrlList)){
            if(ObjectUtil.isEmpty(bannerUrl)){
                bannerUrl = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_BANNER_IMAGE.getCode());
            }
            i18nData.put(bannerUrl,bannerUrlList);
            upd.set(SiteBannerConfigPO::getBannerUrl, bannerUrl);
        }

        //黑底-pc
        String darkBannerUrl = bannerConfigVO.getDarkBannerUrl();
        if(CollectionUtil.isNotEmpty(bannerUrlListDark)){
            if(ObjectUtil.isEmpty(darkBannerUrl)){
                darkBannerUrl = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_BANNER_IMAGE.getCode());
            }
            i18nData.put(darkBannerUrl,bannerUrlListDark);
            upd.set(SiteBannerConfigPO::getDarkBannerUrl, darkBannerUrl);
        }

        //H5
        String h5BannerUrl = bannerConfigVO.getH5BannerName();
        List<I18nMsgFrontVO> h5BannerUrlList = bannerConfigVO.getH5BannerNameList();
        if(CollectionUtil.isNotEmpty(h5BannerUrlList)){
            if(ObjectUtil.isEmpty(h5BannerUrl)){
                h5BannerUrl = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_BANNER_IMAGE.getCode());
            }
            i18nData.put(h5BannerUrl,h5BannerUrlList);
            upd.set(SiteBannerConfigPO::getH5BannerName, h5BannerUrl);
        }


        //H5 -黑底
        String darkH5BannerUrl = bannerConfigVO.getDarkH5BannerUrl();
        List<I18nMsgFrontVO> h5DarkBannerUrlList = bannerConfigVO.getDarkH5BannerUrlList();
        if(CollectionUtil.isNotEmpty(h5DarkBannerUrlList)){
            if(ObjectUtil.isEmpty(darkH5BannerUrl)){
                darkH5BannerUrl = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_BANNER_IMAGE.getCode());
            }
            i18nData.put(darkH5BannerUrl,h5DarkBannerUrlList);
            upd.set(SiteBannerConfigPO::getDarkH5BannerUrl, darkH5BannerUrl);
        }

        i18nMessageService.update(i18nData);

        // 更新轮播图配置
        this.update(upd);

        String key = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(), RedisConstants.KEY_LOBBY_BANNER);
        RedisUtil.deleteKey(key);
        return ResponseVO.success();
    }

    @Transactional
    public ResponseVO<Boolean> deleteConfigById(String id) {
        SiteBannerConfigPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        Integer bannerDuration = po.getBannerDuration();
        if (BannerDuration.LIMITED_TIME.getCode().equals(bannerDuration)) {
            long nowTime = System.currentTimeMillis();
            Long displayEndTime = po.getDisplayEndTime();
            //如果当前banner有效期已经过期,并且是启用状态的原始数据,需要允许删除
            if (nowTime > displayEndTime && po.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
                po.setStatus(EnableStatusEnum.DISABLE.getCode());
            }
        }
        if (po.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.NOT_DELETABLE);
        }
        //移除当前使用的i18
        i18nMessageService.deleteByMsgKey(po.getBannerUrl());
        // 根据ID删除轮播图配置
        int result = siteBannerConfigMapper.deleteById(id);
        String key = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(), RedisConstants.KEY_LOBBY_BANNER);
        RedisUtil.deleteKey(key);
        return ResponseVO.success(result > 0);
    }

    public ResponseVO<SiteBannerConfigRespVO> getConfigById(String id) {
        // 根据ID获取轮播图配置详情
        SiteBannerConfigPO byId = this.getById(id);
        log.info("SiteBannerConfigPO : "+byId);
        return ResponseVO.success(BeanUtil.copyProperties(byId, SiteBannerConfigRespVO.class));
    }

    public ResponseVO<Boolean> enableAndDisableStatus(SiteBannerConfigReqVO reqVO) {
        String id = reqVO.getId();
        SiteBannerConfigPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (EnableStatusEnum.ENABLE.getCode().equals(reqVO.getStatus())) {
            LambdaQueryWrapper<SiteBannerConfigPO> query = Wrappers.lambdaQuery();
            query.eq(SiteBannerConfigPO::getSiteCode, reqVO.getSiteCode())
                    .ne(SiteBannerConfigPO::getId, id)
                    .eq(SiteBannerConfigPO::getGameOneClassId, po.getGameOneClassId())
                    .eq(SiteBannerConfigPO::getBannerArea, po.getBannerArea())
                    .eq(SiteBannerConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode());
            List<SiteBannerConfigPO> list = this.list(query);
            if (CollectionUtil.isNotEmpty(list)) {
                boolean checkedData = false;
                long nowTime = System.currentTimeMillis();
                for (SiteBannerConfigPO siteBannerConfigPO : list) {
                    if (BannerDuration.LIMITED_TIME.getCode().equals(siteBannerConfigPO.getBannerDuration())) {
                        Long displayEndTime = siteBannerConfigPO.getDisplayEndTime();
                        if (nowTime < displayEndTime) {
                            //当前限时的banner还没有过期,不允许再启用其他banner
                            checkedData = true;
                        }
                    } else {
                        //存在长期启用的banner,不允许启用
                        checkedData = true;
                    }
                }
                if (checkedData) {
                    throw new BaowangDefaultException(ResultCode.ONLY_ONE_ENABLED);
                }
            }

            if (BannerDuration.LIMITED_TIME.getCode().equals(po.getBannerDuration())) {
                long nowTime = System.currentTimeMillis();
                Long displayEndTime = po.getDisplayEndTime();
                if (nowTime > displayEndTime) {
                    throw new BaowangDefaultException(ResultCode.CANT_ENABlE_BANNER);
                }
            }
        }
        po.setUpdater(reqVO.getOperator());
        po.setUpdatedTime(System.currentTimeMillis());


        po.setStatus(reqVO.getStatus());
        // 启用或禁用轮播图配置
        this.updateById(po);
        String key = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(), RedisConstants.KEY_LOBBY_BANNER);
        RedisUtil.deleteKey(key);
        return ResponseVO.success();
    }

    public ResponseVO<List<SiteBannerConfigAddSortVO>> querySortList(String siteCode, String gameOneClassId) {

        List<SiteBannerConfigPO> list = siteBannerConfigMapper.selectGroup(siteCode, gameOneClassId);

        return ResponseVO.success(BeanUtil.copyToList(list, SiteBannerConfigAddSortVO.class));
    }

    @Transactional
    public ResponseVO<Boolean> updSortList(String gameOneClassId, List<SiteBannerConfigAddSortVO> sortVOS, String operator, String siteCode) {
        Map<Integer, Integer> areaSortMap = sortVOS.stream()
                .collect(Collectors.toMap(
                        SiteBannerConfigAddSortVO::getBannerArea,
                        SiteBannerConfigAddSortVO::getSort,
                        (existing, replacement) -> existing // 如果键重复，保留现有的值
                ));
        LambdaQueryWrapper<SiteBannerConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteBannerConfigPO::getSiteCode, siteCode).eq(SiteBannerConfigPO::getGameOneClassId, gameOneClassId);
        List<SiteBannerConfigPO> list = this.list(query);
        if (CollectionUtil.isNotEmpty(list)) {
            for (SiteBannerConfigPO siteBannerConfigPO : list) {
                if (areaSortMap.containsKey(siteBannerConfigPO.getBannerArea())) {
                    siteBannerConfigPO.setSort(areaSortMap.get(siteBannerConfigPO.getBannerArea()));
                    siteBannerConfigPO.setUpdater(operator);
                    siteBannerConfigPO.setUpdatedTime(System.currentTimeMillis());
                }
            }
        }
        this.updateBatchById(list);
        String key = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getSiteCode(), RedisConstants.KEY_LOBBY_BANNER);
        RedisUtil.deleteKey(key);
        return ResponseVO.success();
    }
}
