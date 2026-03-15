package com.cloud.baowang.system.service.language;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.vo.language.*;
import com.cloud.baowang.system.po.lang.LanguageManagerPO;
import com.cloud.baowang.system.repositories.language.LanguageManagerRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LanguageManagerService extends ServiceImpl<LanguageManagerRepository, LanguageManagerPO> {

    public ResponseVO<Page<LanguageManagerVO>> pageList(LanguageManagerPageReqVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        Page<LanguageManagerPO> page = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(StrUtil.isNotBlank(vo.getName()), LanguageManagerPO::getName, vo.getName())
                .eq(StrUtil.isNotBlank(siteCode), LanguageManagerPO::getSiteCode, siteCode)
                .eq(StrUtil.isNotBlank(vo.getShowCode()), LanguageManagerPO::getShowCode, vo.getShowCode())
                .eq(ObjUtil.isNotEmpty(vo.getStatus()), LanguageManagerPO::getStatus, vo.getStatus())
                .orderByAsc(LanguageManagerPO::getSort)
                .page(new Page<>(vo.getPageNumber(), vo.getPageSize()));
        Map<String, LanguageManagerListVO> collect;
        if (!CommonConstant.ADMIN_CENTER_SITE_CODE.equals(siteCode)) {
            ResponseVO<List<LanguageManagerListVO>> responseVO = languageByList(CommonConstant.ADMIN_CENTER_SITE_CODE);
            List<LanguageManagerListVO> centerData = responseVO.getData();
            collect = centerData.stream().collect(Collectors.toMap(LanguageManagerListVO::getCode, p -> p, (k1, k2) -> k2));
        } else {
            collect = Maps.newHashMap();
        }
        IPage<LanguageManagerVO> convert = page.convert(record -> {
            LanguageManagerVO languageManagerVO = new LanguageManagerVO();
            BeanUtils.copyProperties(record, languageManagerVO);
            LanguageManagerListVO languageManagerListVO = collect.get(record.getCode());
            if (languageManagerListVO != null) {
                languageManagerVO.setCenterDisable(languageManagerListVO.getStatus());
            }
            return languageManagerVO;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> edit(LanguageManagerEditVO vo) {
        LanguageManagerPO po = getById(vo.getId());
        /*if (po.getStatus().equals(CommonConstant.business_one)) {
            throw new BaowangDefaultException(ResultCode.ENABLED_NOT_EDITABLE);
        }*/
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(LanguageManagerPO::getCode, po.getCode())
                .set(StrUtil.isNotBlank(vo.getName()), LanguageManagerPO::getName, vo.getName())
                .set(StrUtil.isNotBlank(vo.getIcon()), LanguageManagerPO::getIcon, vo.getIcon())
                .set(LanguageManagerPO::getOperator, CurrReqUtils.getAccount())
                .set(LanguageManagerPO::getOperateTime, System.currentTimeMillis())
                .update();
        // cacheService.clear();
        RedisUtil.localCacheMapClear(CacheConstants.LANGUAGE_INFO);
        return ResponseVO.success();
    }

    public ResponseVO<LanguageManagerInfoResVO> info(LanguageManagerInfoReqVO vo) {
        LanguageManagerPO one = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(LanguageManagerPO::getId, vo.getId()).one();
        LanguageManagerInfoResVO resVO = new LanguageManagerInfoResVO();
        BeanUtils.copyProperties(one, resVO);
        return ResponseVO.success(resVO);
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> changeStatus(LanguageManagerChangStatusReqVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        LanguageManagerPO po = getById(vo.getId());
        boolean isCenter = siteCode.equals(CommonConstant.ADMIN_CENTER_SITE_CODE);
        if (isCenter) {
            if (vo.getStatus().equals(CommonConstant.business_zero)) {
                new LambdaUpdateChainWrapper<>(baseMapper)
                        .eq(LanguageManagerPO::getCode, po.getCode())
                        .eq(LanguageManagerPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE)
                        .set(LanguageManagerPO::getStatus, vo.getStatus())
                        .set(LanguageManagerPO::getOperator, CurrReqUtils.getAccount())
                        .set(LanguageManagerPO::getOperateTime, System.currentTimeMillis())
                        .update();
                new LambdaUpdateChainWrapper<>(baseMapper)
                        .eq(LanguageManagerPO::getCode, po.getCode())
                        .ne(LanguageManagerPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE)
                        .set(LanguageManagerPO::getStatus, vo.getStatus())
                        .set(LanguageManagerPO::getOperator, "superadmin")
                        .set(LanguageManagerPO::getOperateTime, System.currentTimeMillis())
                        .update();
                RedisUtil.localCacheMapClear(CacheConstants.LANGUAGE_INFO);
            } else {
                new LambdaUpdateChainWrapper<>(baseMapper)
                        .eq(LanguageManagerPO::getCode, po.getCode())
                        .eq(LanguageManagerPO::getSiteCode, siteCode)
                        .set(LanguageManagerPO::getStatus, vo.getStatus())
                        .set(LanguageManagerPO::getOperator, CurrReqUtils.getAccount())
                        .set(LanguageManagerPO::getOperateTime, System.currentTimeMillis())
                        .update();
                RedisUtil.deleteLocalCachedMap(CacheConstants.LANGUAGE_INFO, siteCode);
            }
        }
        if (!isCenter) {
            // 总台禁用不可编辑
            String code = po.getCode();
            LanguageManagerPO centerPO = new LambdaQueryChainWrapper<>(baseMapper)
                    .eq(LanguageManagerPO::getCode, code)
                    .eq(LanguageManagerPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE)
                    .one();
            if (centerPO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
                throw new BaowangDefaultException(ResultCode.CENTER_LANGUAGE_DISABLE);
            }
            new LambdaUpdateChainWrapper<>(baseMapper)
                    .eq(LanguageManagerPO::getId, vo.getId())
                    .eq(LanguageManagerPO::getSiteCode, siteCode)
                    .set(LanguageManagerPO::getStatus, vo.getStatus())
                    .set(LanguageManagerPO::getOperator, CurrReqUtils.getAccount())
                    .set(LanguageManagerPO::getOperateTime, System.currentTimeMillis())
                    .update();
            RedisUtil.deleteLocalCachedMap(CacheConstants.LANGUAGE_INFO, siteCode);
        }
        return ResponseVO.success();
    }

    public ResponseVO<List<LanguageManagerListVO>> languageByList(String siteCode) {
        List<LanguageManagerListVO> value = (List<LanguageManagerListVO>) RedisUtil.getLocalCachedMap(CacheConstants.LANGUAGE_INFO, siteCode);
        if (CollUtil.isNotEmpty(value)) {
            return ResponseVO.success(value);
        }
        List<LanguageManagerPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(StrUtil.isNotBlank(siteCode), LanguageManagerPO::getSiteCode, siteCode)
                .orderByAsc(LanguageManagerPO::getSort)
                .list();
        if (CollUtil.isEmpty(list)) {
            return ResponseVO.success(List.of());
        } else {
            List<LanguageManagerListVO> listVOList = ConvertUtil.entityListToModelList(list, LanguageManagerListVO.class);
            // 放入redis缓存
            RedisUtil.setLocalCachedMap(CacheConstants.LANGUAGE_INFO, siteCode, listVOList);
            return ResponseVO.success(listVOList);
        }
    }

    public ResponseVO<List<LanguageValidListCacheVO>> validList() {
        List<LanguageManagerListVO> list = languageByList(CurrReqUtils.getSiteCode()).getData();
        List<LanguageManagerListVO> data = list.stream().filter(s -> s.getStatus().equals(CommonConstant.business_one)).toList();
        List<LanguageValidListCacheVO> cacheVOList = ConvertUtil.entityListToModelList(data, LanguageValidListCacheVO.class);

        // 当前请求ip
//        IPResponse ipResponse = IpAddressUtils.queryIpRegion(CurrReqUtils.getReqIp());
        IPRespVO ipResponse = IpAPICoUtils.getIp(CurrReqUtils.getReqIp());
        String countryCode = Optional.ofNullable(ipResponse).map(IPRespVO::getCountryCode).orElse(null);
        String langCode = LanguageEnum.getLangByCountryCode(countryCode);
        boolean current = false;
        if (CollUtil.isNotEmpty(cacheVOList)) {
            for (LanguageValidListCacheVO s : cacheVOList) {
                if (s.getCode().equals(langCode)) {
                    s.setCurrLang(CommonConstant.business_one);
                    current = true;
                }
            }
            if (!current) {
                cacheVOList.get(0).setCurrLang(CommonConstant.business_one);
            }
        }
        return ResponseVO.success(cacheVOList);
    }

    public ResponseVO<List<LanguageValidListCacheVO>> validList(String siteCode) {
        List<LanguageManagerListVO> list = languageByList(siteCode).getData();
        List<LanguageManagerListVO> data = list.stream().filter(s -> s.getStatus().equals(CommonConstant.business_one)).toList();
        List<LanguageValidListCacheVO> cacheVOList = ConvertUtil.entityListToModelList(data, LanguageValidListCacheVO.class);

        // 当前请求ip
//        IPResponse ipResponse = IpAddressUtils.queryIpRegion(CurrReqUtils.getReqIp());
        IPRespVO ipResponse = IpAPICoUtils.getIp(CurrReqUtils.getReqIp());
        String countryCode = Optional.ofNullable(ipResponse).map(IPRespVO::getCountryCode).orElse(null);
        String langCode = LanguageEnum.getLangByCountryCode(countryCode);
        boolean current = false;
        if (CollUtil.isNotEmpty(cacheVOList)) {
            for (LanguageValidListCacheVO s : cacheVOList) {
                if (s.getCode().equals(langCode)) {
                    s.setCurrLang(CommonConstant.business_one);
                    current = true;
                }
            }
            if (!current) {
                cacheVOList.get(0).setCurrLang(CommonConstant.business_one);
            }
        }
        return ResponseVO.success(cacheVOList);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> add(LanguageManagerAddVO vo) {
        String siteCode = vo.getSiteCode();
        LambdaQueryWrapper<LanguageManagerPO> del = Wrappers.lambdaQuery();
        del.eq(LanguageManagerPO::getSiteCode, siteCode);
        this.remove(del);
        List<String> codeList = vo.getCodeList();
        List<LanguageManagerListVO> languageManagerListVOS = languageByList(CurrReqUtils.getSiteCode()).getData();
        List<LanguageManagerListVO> list = languageManagerListVOS.stream().filter(s -> codeList.contains(s.getCode())).toList();
        List<LanguageManagerPO> poList = Lists.newArrayList();
        for (LanguageManagerListVO listVO : list) {
            LanguageManagerPO po = new LanguageManagerPO();
            BeanUtils.copyProperties(listVO, po);
            po.setSiteCode(siteCode);
            po.setId(null);
            poList.add(po);
        }
        saveBatch(poList);
        RedisUtil.deleteLocalCachedMap(CacheConstants.LANGUAGE_INFO, siteCode);
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> sort(List<LanguageManagerSortVO> vo) {
        long currentTimeMillis = System.currentTimeMillis();
        List<LanguageManagerPO> poList = vo.stream().map(s -> {
            LanguageManagerPO po = new LanguageManagerPO();
            po.setId(s.getId());
            po.setSort(s.getSort());
            po.setOperator(CurrReqUtils.getAccount());
            po.setOperateTime(currentTimeMillis);
            return po;
        }).toList();
        updateBatchById(poList);
        RedisUtil.deleteLocalCachedMap(CacheConstants.LANGUAGE_INFO, CurrReqUtils.getSiteCode());
        return ResponseVO.success();
    }

    public ResponseVO<List<SiteLanguageVO>> getSiteLanguageDownBox(String siteCode) {
        List<SiteLanguageVO> result = new ArrayList<>();
        int yesCode = Integer.parseInt(YesOrNoEnum.YES.getCode());
        int noCode = Integer.parseInt(YesOrNoEnum.NO.getCode());
        LambdaQueryWrapper<LanguageManagerPO> query = Wrappers.lambdaQuery();
        query.eq(LanguageManagerPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE);
        List<LanguageManagerPO> systemLanguageList = this.list(query);
        if (CollectionUtil.isNotEmpty(systemLanguageList)) {
            //获取总台全部语言,不区分启用禁用
            if (StringUtils.isBlank(siteCode)) {
                //新增站点,返回全部启用的语言
                //过滤掉所有禁用的币种
                systemLanguageList = systemLanguageList.stream().filter(item -> EnableStatusEnum.ENABLE.getCode().equals(item.getStatus())).toList();
                if (CollectionUtil.isNotEmpty(systemLanguageList)) {
                    systemLanguageList.forEach(obj -> result.add(SiteLanguageVO.builder()
                            .code(obj.getCode()).name(obj.getName()).isChecked(noCode).build()));
                }
            } else {
                //编辑站点,保留原始已保存过的语言(不区分启用禁用)
                LambdaQueryWrapper<LanguageManagerPO> siteQuery = Wrappers.lambdaQuery();
                siteQuery.eq(LanguageManagerPO::getSiteCode, siteCode);
                List<LanguageManagerPO> siteLanguageList = this.list(siteQuery);
                if (CollectionUtil.isNotEmpty(siteLanguageList)) {
                    Map<String, LanguageManagerPO> map = siteLanguageList.stream()
                            .collect(Collectors.toMap(LanguageManagerPO::getCode, item -> item));
                    for (LanguageManagerPO po : systemLanguageList) {
                        if (map.containsKey(po.getCode())) {
                            //站点历史保存语言,默认保留
                            SiteLanguageVO siteLanguageVO = SiteLanguageVO.builder().code(po.getCode()).name(po.getName()).isChecked(yesCode).build();
                            result.add(siteLanguageVO);
                        } else {
                            if (EnableStatusEnum.ENABLE.getCode().equals(po.getStatus())) {
                                //站点历史没有选择过的语言,判断是否总台禁用了,禁用了则不再出现在选择框
                                SiteLanguageVO siteLanguageVO = SiteLanguageVO.builder().code(po.getCode()).name(po.getName()).isChecked(noCode).build();
                                result.add(siteLanguageVO);
                            }
                        }
                    }
                }
            }
        }
        return ResponseVO.success(result);
    }
}
