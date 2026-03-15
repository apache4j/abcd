package com.cloud.baowang.system.service.site.rebate;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.site.rebate.*;
import com.cloud.baowang.system.po.site.rebate.SiteRebateConfigPO;
import com.cloud.baowang.system.repositories.site.rebate.SiteRebateConfigRepository;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankRabateVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SiteRebateConfigService extends ServiceImpl<SiteRebateConfigRepository, SiteRebateConfigPO> {

    private final SiteRebateConfigRepository repository;
    private final VipRankApi vipRankApi;
    private final SiteVipOptionApi siteVipOptionApi;

    private final SiteCurrencyInfoApi currencyInfoApi;

    /**
     * 返水配置列表
     *
     * @return
     */
    public List<SiteRebateConfigVO> listPage(SiteRebateConfigQueryVO reqVO) {
        LambdaQueryWrapper<SiteRebateConfigPO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SiteRebateConfigPO::getSiteCode, reqVO.getSiteCode());
        wrapper.eq(SiteRebateConfigPO::getCurrencyCode, reqVO.getCurrencyCode());
        List<SiteRebateConfigPO> list = repository.selectList(wrapper);
        List<SiteRebateConfigVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            //初始化数据
            if (CurrReqUtils.getHandicapMode() == null || CurrReqUtils.getHandicapMode() == 0) {
                //国际盘
                List<SiteVIPRankRabateVO> vipRankList = vipRankApi.getVipRankBySiteCode(reqVO.getSiteCode());
                for (SiteVIPRankRabateVO vipGradeVO : vipRankList) {
                    SiteRebateConfigVO configVO = new SiteRebateConfigVO();
                    configVO.setVipGradeCode(String.valueOf(vipGradeVO.getVipRankCode()));
                    configVO.setVipGradeName(vipGradeVO.getVipRankName());
                    configVO.setCurrencyCode(reqVO.getCurrencyCode());
                    result.add(configVO);
                }

            } else {
                //大陆盘
                List<VIPGradeVO> vipGradeList = siteVipOptionApi.getCnVipGradeList().getData();
                for (VIPGradeVO vipGradeVO : vipGradeList) {
                    SiteRebateConfigVO configVO = new SiteRebateConfigVO();
                    configVO.setVipGradeCode(String.valueOf(vipGradeVO.getVipGradeCode()));
                    configVO.setVipGradeName(vipGradeVO.getVipGradeName());
                    configVO.setCurrencyCode(reqVO.getCurrencyCode());
                    result.add(configVO);
                }
            }
            return result;
        }
        result = list.stream()
                .map(po -> {
                    try {
                        multiplyBigDecimalFields(po);
                    } catch (IllegalAccessException e) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    return BeanUtil.copyProperties(po, SiteRebateConfigVO.class);
                })
                .collect(Collectors.toList());

        return result;
    }


    public List<SiteRebateConfigPO> rebateConfigList(String siteCode, String currencyCode) {
        LambdaQueryWrapper<SiteRebateConfigPO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SiteRebateConfigPO::getSiteCode, siteCode);
        wrapper.eq(SiteRebateConfigPO::getCurrencyCode, currencyCode);
        return repository.selectList(wrapper);
    }


    /**
     * 编辑返水列表
     *
     * @param vo
     * @return
     */

    public ResponseVO saveRebateConfig(List<SiteRebateConfigAddVO> vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        if (CollectionUtils.isEmpty(vo)) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        LambdaUpdateWrapper<SiteRebateConfigPO> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(SiteRebateConfigPO::getSiteCode, siteCode);
        wrapper.eq(SiteRebateConfigPO::getCurrencyCode, vo.get(0).getCurrencyCode());
        this.remove(wrapper);

        List<SiteRebateConfigPO> configPOS = new ArrayList<>();
        vo.forEach(item -> {
            SiteRebateConfigPO po = BeanUtil.copyProperties(item, SiteRebateConfigPO.class);
            po.setSiteCode(siteCode);
            po.setUpdatedTime(System.currentTimeMillis());
            po.setUpdater(CurrReqUtils.getAccount());
            try {
                divideBigDecimalFields(po);
            } catch (IllegalAccessException e) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            configPOS.add(po);
        });


        this.saveBatch(configPOS);
        return ResponseVO.success();
    }

    public static void divideBigDecimalFields(SiteRebateConfigPO obj) throws IllegalAccessException {
        if (obj.getStatus() == null || obj.getStatus() == 0) {
            return;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ("dailyLimit".equals(field.getName())) {
                continue;
            }
            if (field.getType().equals(BigDecimal.class)) {
                field.setAccessible(true);
                BigDecimal value = (BigDecimal) field.get(obj);
                if (value != null) {
                    field.set(obj, value.divide(new BigDecimal("100")));
                }
            }
        }
    }

    public static void multiplyBigDecimalFields(SiteRebateConfigPO obj) throws IllegalAccessException {
        if (obj.getStatus() == 0) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == BigDecimal.class) {
                    field.setAccessible(true);
                    try {
                        field.set(obj, BigDecimal.ZERO);
                    } catch (Exception ignored) {

                    }
                }
            }
            return;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ("dailyLimit".equals(field.getName())) {
                continue;
            }
            if (field.getType().equals(BigDecimal.class)) {
                field.setAccessible(true);
                BigDecimal value = (BigDecimal) field.get(obj);
                if (value != null) {
                    field.set(obj, value.multiply(new BigDecimal("100")));
                }
            }
        }
    }

    public List<SiteVIPRankRabateVO> buildVipInitInfo(SiteRebateInitVO reqVO) {
        List<SiteVIPRankRabateVO> result = Lists.newArrayList();
        //新等级
        List<VIPGradeVO> vipGradeList = siteVipOptionApi.getCnVipGradeList().getData();
        result = vipGradeList.stream().map(item -> {
            SiteVIPRankRabateVO vo = new SiteVIPRankRabateVO();
            vo.setVipRankCode(item.getVipGradeCode());
            vo.setVipRankName(item.getVipGradeName());
            return vo;
        }).collect(Collectors.toList());

        return result;
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void initRebateList(SiteRebateInitVO reqVO) {
        String siteCode = reqVO.getSiteCode();
        BigDecimal bigDecimal = BigDecimal.ZERO;
        List<SiteVIPRankRabateVO> siteVIPRankList = buildVipInitInfo(reqVO);
        List<SiteRebateConfigPO> list = this.getBaseMapper().selectList(Wrappers.<SiteRebateConfigPO>lambdaQuery().eq(SiteRebateConfigPO::getSiteCode, siteCode));
        List<String> currencyListNo = currencyInfoApi.getCurrencyListNo(siteCode).stream().map(CodeValueVO::getCode).toList();
        if (CollectionUtils.isEmpty(list)) {
            for (String currencyCode : currencyListNo) {
                List<SiteRebateConfigPO> finalList = list;
                siteVIPRankList.forEach(e -> {
                    SiteRebateConfigPO vo = new SiteRebateConfigPO();
                    vo.setSiteCode(e.getSiteCode());
                    vo.setVipGradeCode(e.getVipRankCode().toString());
                    vo.setVipGradeName(e.getVipRankName());
                    vo.setSportsRebate(bigDecimal);
                    vo.setEsportsRebate(bigDecimal);
                    vo.setVideoRebate(bigDecimal);
                    vo.setPokerRebate(bigDecimal);
                    vo.setSlotsRebate(bigDecimal);
                    vo.setLotteryRebate(bigDecimal);
                    vo.setCockfightingRebate(bigDecimal);
                    vo.setFishingRebate(bigDecimal);
                    vo.setMarblesRebate(bigDecimal);
                    vo.setDailyLimit(bigDecimal);
                    vo.setMarblesRebate(bigDecimal);
                    vo.setStatus(e.getRebateConfig());
                    vo.setCurrencyCode(currencyCode);
                    finalList.add(vo);
                });
                this.saveBatch(finalList);
            }
        } else {
            Map<Integer, String> userMap = siteVIPRankList.stream().filter(s -> StringUtils.isNotEmpty(s.getVipRankNameI18nCode())).collect(Collectors.toMap(
                    SiteVIPRankRabateVO::getVipRankCode, SiteVIPRankRabateVO::getVipRankNameI18nCode, (existing, replacement) -> existing));
            Map<Integer, Integer> rabateMap = siteVIPRankList.stream().filter(s -> ObjectUtils.isNotEmpty(s.getRebateConfig())).collect(Collectors.toMap(
                    SiteVIPRankRabateVO::getVipRankCode, SiteVIPRankRabateVO::getRebateConfig, (existing, replacement) -> existing));
            List<SiteRebateConfigPO> update = list.stream().filter(e -> {
                Integer vipRankCode = Integer.parseInt(e.getVipGradeCode());
                if (userMap.containsKey(vipRankCode)) {
                    if (StringUtils.isEmpty(e.getVipGradeName()) || !userMap.get(vipRankCode).equals(e.getVipGradeName())
                            || !rabateMap.get(vipRankCode).equals(e.getStatus())) {
                        return true;
                    }
                }
                return false;
            }).map(e -> {
                e.setVipGradeName(userMap.get(Integer.parseInt(e.getVipGradeCode())));
                e.setStatus(rabateMap.get(Integer.parseInt(e.getVipGradeCode())));
                return e;
            }).collect(Collectors.toList());
            this.saveOrUpdateBatch(update);
        }
    }

    /**
     * 返水配置列表
     *
     * @param
     * @return
     */
    public List<SiteRebateConfigWebVO> webListPage(SiteRebateClientShowVO req) {
        List<SiteRebateConfigPO> list = this.getBaseMapper()
                .selectList(Wrappers.<SiteRebateConfigPO>lambdaQuery()
                        .eq(StringUtils.isNotEmpty(req.getSiteCode()),SiteRebateConfigPO::getSiteCode, req.getSiteCode())
                        .eq(StringUtils.isNotEmpty(req.getCurrencyCode()),SiteRebateConfigPO::getCurrencyCode, req.getCurrencyCode())
                        .eq(Objects.nonNull(req.getVipCode()),SiteRebateConfigPO::getVipGradeCode, req.getVipCode())
                        .orderByAsc(SiteRebateConfigPO::getSiteCode));
        list.forEach(e -> {
            try {
                multiplyBigDecimalFields(e);
            } catch (IllegalAccessException ex) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        });
        return BeanUtil.copyToList(list, SiteRebateConfigWebVO.class);
    }

    /**
     * 返水配置列表
     *
     * @param
     * @return
     */
    public List<SiteRebateConfigWebVO> webQueryListByStatusPage(SiteRebateClientShowVO req) {
        List<SiteRebateConfigPO> list = this.getBaseMapper()
                .selectList(Wrappers.<SiteRebateConfigPO>lambdaQuery()
                        .eq(StringUtils.isNotEmpty(req.getSiteCode()),SiteRebateConfigPO::getSiteCode, req.getSiteCode())
                        .eq(StringUtils.isNotEmpty(req.getCurrencyCode()),SiteRebateConfigPO::getCurrencyCode, req.getCurrencyCode())
                        .eq(Objects.nonNull(req.getVipCode()),SiteRebateConfigPO::getVipGradeCode, req.getVipCode())
                        .eq(SiteRebateConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                        .orderByAsc(SiteRebateConfigPO::getSiteCode));
        list.forEach(e -> {
            try {
                multiplyBigDecimalFields(e);
            } catch (IllegalAccessException ex) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        });
        return BeanUtil.copyToList(list, SiteRebateConfigWebVO.class);
    }


    /**
     * 返水标签
     *
     * @return public Map<String, Map<VenueTypeEnum, BigDecimal>> rebateLabel(SiteRebateClientShowVO req) {
     * List<SiteRebateConfigWebVO> tempList = webListPage(req);
     * Map<String, List<SiteRebateConfigWebVO>> tempMap = tempList.stream()
     * .filter(po -> po.getCurrencyCode() != null)
     * .collect(Collectors.groupingBy(SiteRebateConfigWebVO::getCurrencyCode));
     * <p>
     * <p>
     * <p>
     * Map<VenueTypeEnum, Function<SiteRebateConfigWebVO, BigDecimal>> mapping = new HashMap<VenueTypeEnum, Function<SiteRebateConfigWebVO, BigDecimal>>();
     * mapping.put(VenueTypeEnum.SH, SiteRebateConfigWebVO::getVideoRebate);
     * mapping.put(VenueTypeEnum.SPORTS, SiteRebateConfigWebVO::getSportsRebate);
     * mapping.put(VenueTypeEnum.CHESS, SiteRebateConfigWebVO::getPokerRebate);
     * mapping.put(VenueTypeEnum.ELECTRONICS, SiteRebateConfigWebVO::getSlotsRebate);
     * mapping.put(VenueTypeEnum.ACELT, SiteRebateConfigWebVO::getLotteryRebate);
     * mapping.put(VenueTypeEnum.COCKFIGHTING, SiteRebateConfigWebVO::getCockfightingRebate);
     * mapping.put(VenueTypeEnum.ELECTRONIC_SPORTS, SiteRebateConfigWebVO::getEsportsRebate);
     * mapping.put(VenueTypeEnum.FISHING, SiteRebateConfigWebVO::getFishingRebate);
     * mapping.put(VenueTypeEnum.MARBLES, SiteRebateConfigWebVO::getMarblesRebate);
     * //        Map<VenueTypeEnum, BigDecimal> result = new EnumMap<>(VenueTypeEnum.class);
     * <p>
     * //        mapping.forEach((venueType, getter) -> {
     * //            BigDecimal max = tempList.stream()
     * //                    .map(getter)
     * //                    .filter(Objects::nonNull)
     * //                    .max(BigDecimal::compareTo)
     * //                    .orElse(BigDecimal.ZERO);
     * //            result.put(venueType, max);
     * //        });
     * <p>
     * Map<String, Map<VenueTypeEnum, BigDecimal>> finalResult = new HashMap<>();
     * <p>
     * tempMap.forEach((currency, voList) -> {
     * Map<VenueTypeEnum, BigDecimal> result = new EnumMap<>(VenueTypeEnum.class);
     * mapping.forEach((venueType, getter) -> {
     * BigDecimal max = voList.stream()
     * .map(getter)
     * .filter(Objects::nonNull)
     * .max(BigDecimal::compareTo)
     * .orElse(BigDecimal.ZERO);
     * result.put(venueType, max);
     * });
     * finalResult.put(currency, result);
     * });
     * <p>
     * return finalResult;
     * }
     */


    public Map<Integer, Map<String, BigDecimal>> rebateLabel(SiteRebateClientShowVO req) {
        List<SiteRebateConfigWebVO> tempList = webListPage(req);

        Map<Integer, Function<SiteRebateConfigWebVO, BigDecimal>> mapping = new HashMap<>();
        mapping.put(VenueTypeEnum.SH.getCode(), SiteRebateConfigWebVO::getVideoRebate);
        mapping.put(VenueTypeEnum.SPORTS.getCode(), SiteRebateConfigWebVO::getSportsRebate);
        mapping.put(VenueTypeEnum.CHESS.getCode(), SiteRebateConfigWebVO::getPokerRebate);
        mapping.put(VenueTypeEnum.ELECTRONICS.getCode(), SiteRebateConfigWebVO::getSlotsRebate);
        mapping.put(VenueTypeEnum.ACELT.getCode(), SiteRebateConfigWebVO::getLotteryRebate);
        mapping.put(VenueTypeEnum.COCKFIGHTING.getCode(), SiteRebateConfigWebVO::getCockfightingRebate);
        mapping.put(VenueTypeEnum.ELECTRONIC_SPORTS.getCode(), SiteRebateConfigWebVO::getEsportsRebate);
        mapping.put(VenueTypeEnum.FISHING.getCode(), SiteRebateConfigWebVO::getFishingRebate);
        mapping.put(VenueTypeEnum.MARBLES.getCode(), SiteRebateConfigWebVO::getMarblesRebate);

        Map<Integer, Map<String, BigDecimal>> finalResult = new HashMap<>();

        //  按 currencyCode 分组取最大值
        mapping.forEach((venueType, getter) -> {
            Map<String, BigDecimal> currencyMaxMap = tempList.stream()
                    .filter(po -> po.getCurrencyCode() != null)
                    .collect(Collectors.groupingBy(
                            SiteRebateConfigWebVO::getCurrencyCode,
                            Collectors.collectingAndThen(Collectors.toList(), voList ->
                                    voList.stream()
                                            .map(getter)
                                            .filter(Objects::nonNull)
                                            .max(BigDecimal::compareTo)
                                            .orElse(BigDecimal.ZERO))
                    ));
            finalResult.put(venueType, currencyMaxMap);
        });

        return finalResult;
    }

    /**
     * @param reqVO
     * @return
     */
    public Boolean updateVipGradeRebateConfig(SiteRebateInitVO reqVO) {
        LambdaUpdateWrapper<SiteRebateConfigPO> updateWrapper = Wrappers.<SiteRebateConfigPO>lambdaUpdate();
        updateWrapper.eq(SiteRebateConfigPO::getSiteCode, reqVO.getSiteCode());
        updateWrapper.eq(SiteRebateConfigPO::getVipGradeCode, reqVO.getVipGradeCode());
        updateWrapper.eq(SiteRebateConfigPO::getCurrencyCode, reqVO.getCurrencyCode());
        updateWrapper.set(SiteRebateConfigPO::getStatus, reqVO.getStatus());
        return this.update(updateWrapper);
    }
}
