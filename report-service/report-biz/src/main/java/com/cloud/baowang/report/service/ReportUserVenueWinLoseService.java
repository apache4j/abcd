package com.cloud.baowang.report.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.BetGameTypeEnum;
import com.cloud.baowang.play.api.vo.order.report.VenueWinLoseRecalculateReqVO;
import com.cloud.baowang.play.api.vo.order.report.VenueWinLoseRecalculateVO;
import com.cloud.baowang.report.api.vo.PlatformVenueRequestVO;
import com.cloud.baowang.report.api.vo.ReportUserAgentVenueStaticsVO;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.ReportUserVenueStaticsVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueStaticsVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueWinLossVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.game.*;
import com.cloud.baowang.report.api.vo.site.SiteDataUserWinLossResVo;
import com.cloud.baowang.report.api.vo.site.SiteReportUserVenueStaticsVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueTopVO;
import com.cloud.baowang.report.api.vo.user.ReportUserWinLossRebateParamVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.report.api.vo.venuewinlose.*;
import com.cloud.baowang.report.po.ReportUserVenueWinLosePO;
import com.cloud.baowang.report.repositories.ReportUserVenueWinLoseRepository;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReportUserVenueWinLoseService extends ServiceImpl<ReportUserVenueWinLoseRepository, ReportUserVenueWinLosePO> {
    private final ReportUserVenueWinLoseRepository userVenueWinLoseRepository;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final PlayVenueInfoApi venueInfoApi;
    private final OrderRecordApi orderRecordApi;
    private final SiteApi siteApi;

    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(name = RedisConstants.VENUE_WIN_LOSS_LOCK_KEY, unique = "#vo.userId", fair = true, waitTime = 3, leaseTime = 180)
    public void userVenueWinLossHandler(UserVenueWinLossMqVO vo) {
        Long dayHour = vo.getDayHour();
        ReportUserVenueWinLosePO po = new LambdaQueryChainWrapper<>(userVenueWinLoseRepository)
                .eq(StrUtil.isNotBlank(vo.getAgentId()), ReportUserVenueWinLosePO::getAgentId, vo.getAgentId())
                .eq(StrUtil.isNotBlank(vo.getVenueGameType()), ReportUserVenueWinLosePO::getVenueGameType, vo.getVenueGameType())
                .eq(ReportUserVenueWinLosePO::getSiteCode, vo.getSiteCode())
                .eq(ReportUserVenueWinLosePO::getVenueCode, vo.getVenueCode())
                .eq(ReportUserVenueWinLosePO::getVenueType, vo.getVenueType())
                .eq(StrUtil.isNotBlank(vo.getRoomType()),ReportUserVenueWinLosePO::getRoomType,vo.getRoomType())
                .eq(ReportUserVenueWinLosePO::getDayHourMillis, dayHour)
                .eq(ReportUserVenueWinLosePO::getUserId, vo.getUserId())
                .one();

        if (Objects.equals(BetGameTypeEnum.FREE_SPIN.getCode(),vo.getBetType())){
            return;
        }
        if (ObjUtil.isNotNull(vo.getLastDayHour())) {
            // 同一小时
            if (vo.getLastDayHour().equals(dayHour)) {
                po.setBetAmount(po.getBetAmount().add(vo.getBetAmount()).subtract(vo.getLastBetAmount()));
                po.setWinLossAmount(po.getWinLossAmount().add(vo.getWinLossAmount()).subtract(vo.getLastBetWinLose()).add(vo.getTipsAmount()).subtract(vo.getLastTipsAmount()));
                po.setValidAmount(po.getValidAmount().add(vo.getValidAmount().subtract(vo.getLastValidBetAmount())));
                po.setUserWinLossAmount(po.getUserWinLossAmount().add(vo.getWinLossAmount()).subtract(vo.getLastBetWinLose()));
                po.setTipsAmount(po.getTipsAmount().add(vo.getTipsAmount().subtract(vo.getLastTipsAmount())));
                po.setTipsAmount(po.getTipsAmount().add(vo.getTipsAmount().subtract(vo.getLastTipsAmount())));
                userVenueWinLoseRepository.updateById(po);
                return;
            } else {
                // 上一次修改g
                ReportUserVenueWinLosePO lastPO = new LambdaQueryChainWrapper<>(userVenueWinLoseRepository)
                        .eq(StrUtil.isNotBlank(vo.getLastAgentId()), ReportUserVenueWinLosePO::getAgentId, vo.getLastAgentId())
                        .eq(StrUtil.isNotBlank(vo.getVenueGameType()), ReportUserVenueWinLosePO::getVenueGameType, vo.getVenueGameType())
                        .eq(ReportUserVenueWinLosePO::getSiteCode, vo.getSiteCode())
                        .eq(ReportUserVenueWinLosePO::getVenueCode, vo.getVenueCode())
                        .eq(ReportUserVenueWinLosePO::getVenueType, vo.getVenueType())
                        .eq(StrUtil.isNotBlank(vo.getRoomType()),ReportUserVenueWinLosePO::getRoomType,vo.getRoomType())
                        .eq(ReportUserVenueWinLosePO::getDayHourMillis, vo.getLastDayHour())
                        .eq(ReportUserVenueWinLosePO::getUserId, vo.getUserId())
                        .one();
                if (ObjUtil.isNotNull(lastPO)) {
                    lastPO.setBetAmount(lastPO.getBetAmount().subtract(vo.getLastBetAmount()));
                    lastPO.setValidAmount(lastPO.getValidAmount().subtract(vo.getLastValidBetAmount()));
                    lastPO.setUserWinLossAmount(lastPO.getUserWinLossAmount().subtract(vo.getLastBetWinLose()));
                    lastPO.setWinLossAmount(lastPO.getWinLossAmount().subtract(vo.getLastBetWinLose()).add(vo.getLastTipsAmount()).subtract(vo.getTipsAmount()));
                    lastPO.setTipsAmount(lastPO.getTipsAmount().subtract(vo.getLastTipsAmount()));
                    userVenueWinLoseRepository.updateById(lastPO);
                }
            }
        }
        if (po == null) {
            long siteDayMillis = TimeZoneUtils.getStartOfDayInTimeZone(dayHour, vo.getTimeZone());
            ReportUserVenueWinLosePO winLosePO = new ReportUserVenueWinLosePO();
            winLosePO.setSiteCode(vo.getSiteCode());
            winLosePO.setDayHourMillis(dayHour);
            winLosePO.setDayMillis(siteDayMillis);
            winLosePO.setVenueGameType(vo.getVenueGameType());
            winLosePO.setVenueCode(vo.getVenueCode());
            winLosePO.setVenueType(vo.getVenueType());
            winLosePO.setUserAccount(vo.getUserAccount());
            winLosePO.setAccountType(vo.getAccountType());
            winLosePO.setUserId(vo.getUserId());
            winLosePO.setAgentId(vo.getAgentId());
            winLosePO.setAgentAccount(vo.getAgentAccount());
            winLosePO.setBetCount(vo.getBetCount());
            winLosePO.setWinLossAmount(vo.getWinLossAmount().subtract(vo.getTipsAmount()));
            winLosePO.setUserWinLossAmount(vo.getWinLossAmount());
            winLosePO.setValidAmount(vo.getValidAmount());
            winLosePO.setBetAmount(vo.getBetAmount());
            winLosePO.setCurrency(vo.getCurrency());
            winLosePO.setRoomType(vo.getRoomType());
            winLosePO.setTipsAmount(vo.getTipsAmount());
            userVenueWinLoseRepository.insert(winLosePO);
        } else {
            po.setBetCount(po.getBetCount() + vo.getBetCount());
            po.setBetAmount(po.getBetAmount().add(vo.getBetAmount()));
            po.setValidAmount(po.getValidAmount().add(vo.getValidAmount()));
            po.setUserWinLossAmount(po.getUserWinLossAmount().add(vo.getWinLossAmount()));
            po.setWinLossAmount(po.getWinLossAmount().add(vo.getWinLossAmount()).subtract(vo.getTipsAmount()));
            po.setTipsAmount(po.getTipsAmount().add(vo.getTipsAmount()));
            userVenueWinLoseRepository.updateById(po);
        }

    }


    public List<ReportUserVenueTopVO> winLoseTopThree(String userAccount, String siteCode, Long startTime, Long endTime) {
        return userVenueWinLoseRepository.userVenueBets(userAccount, siteCode, startTime, endTime);
    }

    public Page<ReportUserVenueTopVO> topPlatformVenue(PlatformVenueRequestVO vo) {
        Page<ReportUserVenueTopVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        return userVenueWinLoseRepository.topPlatformVenue(page,vo);
    }

    public Page<ReportUserVenueBetsTopVO> queryUserBetsTop(ReportUserTopReqVO userTopReqVO) {
        Page<ReportUserVenueWinLosePO> page = new Page<>(userTopReqVO.getPageNumber(), userTopReqVO.getPageSize());
        if (ObjectUtil.isNotEmpty(userTopReqVO.getOrderField())) {
            //防注入
            if (userTopReqVO.getOrderField().contains(";")
                    || (userTopReqVO.getOrderType() != null && !userTopReqVO.getOrderType().trim().equalsIgnoreCase("desc")
                    && !userTopReqVO.getOrderType().trim().equalsIgnoreCase("asc"))) {
                return new Page<>();
            }
        }
        return userVenueWinLoseRepository.queryUserBetsTop(page, userTopReqVO);
    }

    public Page<VenueWinLossInfoResVO> info(ReportVenueWinLossPageReqVO vo) {
        Boolean convertPlat = vo.getConvertPlatCurrency(); // 转换平台币
        // 查询场馆下详细统计信息
        Page<VenueWinLossInfoResVO> resVOPage = userVenueWinLoseRepository.infoList(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
        List<VenueWinLossInfoResVO> records = resVOPage.getRecords();
        if (CollUtil.isNotEmpty(records) && convertPlat) {
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(vo.getSiteCode());
            records.forEach(record -> {
                String currency = record.getCurrency();
                BigDecimal rate = allFinalRate.get(currency);
                record.setWinlossAmount(record.getWinlossAmount().divide(rate, CommonConstant.business_two, RoundingMode.HALF_UP));
                record.setValidBetAmount(record.getValidBetAmount().divide(rate, CommonConstant.business_two, RoundingMode.HALF_UP));
                record.setBetAmount(record.getBetAmount().divide(rate, CommonConstant.business_two, RoundingMode.HALF_UP));
                record.setTipsAmount(record.getTipsAmount().divide(rate, CommonConstant.business_two, RoundingMode.HALF_UP));
                record.setUserWinlossAmount(record.getUserWinlossAmount().divide(rate, CommonConstant.business_two, RoundingMode.HALF_UP));
            });
        }
        return resVOPage;
    }

    public ReportVenueWinLossResVO pageList(ReportVenueWinLossPageReqVO vo) {
        Boolean convertPlat = vo.getConvertPlatCurrency(); // 转换平台币
        ReportVenueWinLossResVO venueWinLossResVO = new ReportVenueWinLossResVO();
        Page<VenueWinLossDetailResVO> pageResult = new Page<>(vo.getPageNumber(), vo.getPageSize());
        // 查询场馆下详细统计信息
        Page<VenueWinLossDetailResVO> venueWinLossDetailPage = userVenueWinLoseRepository.detailList(pageResult, vo);
        List<VenueWinLossDetailResVO> records = venueWinLossDetailPage.getRecords();
        Map<String, BigDecimal> allFinalRate = Maps.newHashMap();
        if (CollUtil.isNotEmpty(records)) {
            allFinalRate = siteCurrencyInfoApi.getAllFinalRate(vo.getSiteCode());
        }
        Map<String,String> venueMap =  venueInfoApi.getSiteVenueNameMap().getData();

        Map<String, BigDecimal> finalAllFinalRate = allFinalRate;
        records.forEach(detailResVO -> {
            String venueName = venueMap.get(detailResVO.getVenueCode());
            detailResVO.setVenueCodeText(venueName);
            // 转换为平台币
            if (convertPlat) {
                AtomicReference<Integer> betCount = new AtomicReference<>(CommonConstant.business_zero);
                AtomicReference<Integer> bettorCount = new AtomicReference<>(CommonConstant.business_zero);
                betCount.updateAndGet(v -> v + detailResVO.getBetCount());
                bettorCount.updateAndGet(v -> v + detailResVO.getBettorCount());
                BigDecimal rate = finalAllFinalRate.get(detailResVO.getCurrency());
                BigDecimal winlossAmount = detailResVO.getWinlossAmount();
                BigDecimal validBetAmount = detailResVO.getValidBetAmount();
                BigDecimal betAmount = detailResVO.getBetAmount();
                BigDecimal tipsAmount = detailResVO.getTipsAmount();
                BigDecimal userWinlossAmount = detailResVO.getUserWinlossAmount();
                BigDecimal platWinlossAmount = winlossAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN);
                BigDecimal platValidBetAmount = validBetAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN);
                BigDecimal platBetAmount = betAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN);
                BigDecimal platTipsAmount = tipsAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN);
                BigDecimal platUserWinlossAmount = userWinlossAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN);
                detailResVO.setWinlossAmount(platWinlossAmount);
                detailResVO.setValidBetAmount(platValidBetAmount);
                detailResVO.setBetAmount(platBetAmount);
                detailResVO.setTipsAmount(platTipsAmount);
                detailResVO.setUserWinlossAmount(platUserWinlossAmount);
                detailResVO.setPlatCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            }
        });
        // 总计
        if (!vo.getExportFlag()) {
            List<VenueWinLossDetailResVO> detailResVOS = userVenueWinLoseRepository.totalByParam(vo);
            VenueWinLossDetailResVO totalDetailRes = new VenueWinLossDetailResVO();
            AtomicReference<Integer> betCount = new AtomicReference<>(CommonConstant.business_zero);
            AtomicReference<Integer> bettorCount = new AtomicReference<>(CommonConstant.business_zero);
            AtomicReference<BigDecimal> platBetAmountAt = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> platWinlossAmountAt = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> platValidBetAmountAt = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> platTipsAmountAt = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> platUserWinlossAmount = new AtomicReference<>(BigDecimal.ZERO);
            if (CollUtil.isNotEmpty(detailResVOS)) {
                if (convertPlat) {
                    detailResVOS.forEach(totalDetail -> {
                        String currency = totalDetail.getCurrency();
                        BigDecimal rate = finalAllFinalRate.get(currency);
                        BigDecimal betAmount = totalDetail.getBetAmount();
                        BigDecimal winlossAmount = totalDetail.getWinlossAmount();
                        BigDecimal validBetAmount = totalDetail.getValidBetAmount();
                        BigDecimal tipsAmount =  totalDetail.getTipsAmount();
                        BigDecimal userWinlossAmount = totalDetail.getUserWinlossAmount();
                        platBetAmountAt.updateAndGet(v -> v.add(betAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                        platWinlossAmountAt.updateAndGet(v -> v.add(winlossAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                        platValidBetAmountAt.updateAndGet(v -> v.add(validBetAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                        platTipsAmountAt.updateAndGet(v -> v.add(tipsAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                        platUserWinlossAmount.updateAndGet(v -> v.add(userWinlossAmount.divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                        betCount.updateAndGet(v -> v + totalDetail.getBetCount());
                        bettorCount.updateAndGet(v -> v + totalDetail.getBettorCount());
                    });
                } else {
                    detailResVOS.forEach(totalDetail -> {
                        BigDecimal betAmount = totalDetail.getBetAmount();
                        BigDecimal winlossAmount = totalDetail.getWinlossAmount();
                        BigDecimal validBetAmount = totalDetail.getValidBetAmount();
                        BigDecimal tipsAmount =  totalDetail.getTipsAmount();
                        BigDecimal userWinlossAmount = totalDetail.getUserWinlossAmount();
                        platBetAmountAt.updateAndGet(v -> v.add(betAmount));
                        platWinlossAmountAt.updateAndGet(v -> v.add(winlossAmount));
                        platValidBetAmountAt.updateAndGet(v -> v.add(validBetAmount));
                        platTipsAmountAt.updateAndGet(v -> v.add(tipsAmount));
                        platUserWinlossAmount.updateAndGet(v -> v.add(userWinlossAmount));
                        betCount.updateAndGet(v -> v + totalDetail.getBetCount());
                        bettorCount.updateAndGet(v -> v + totalDetail.getBettorCount());
                    });
                }
            }
            totalDetailRes.setBetCount(betCount.get());
            totalDetailRes.setBettorCount(bettorCount.get());
            totalDetailRes.setBetAmount(platBetAmountAt.get());
            totalDetailRes.setWinlossAmount(platWinlossAmountAt.get());
            totalDetailRes.setValidBetAmount(platValidBetAmountAt.get());
            totalDetailRes.setTipsAmount(platTipsAmountAt.get());
            totalDetailRes.setUserWinlossAmount(platUserWinlossAmount.get());
            venueWinLossResVO.setTotalInfo(totalDetailRes);
        }
        venueWinLossResVO.setPageList(venueWinLossDetailPage);
        return venueWinLossResVO;
    }



    public List<ReportAgentVenueWinLossVO> queryAgentVenueWinLoss(@Param("vo") ReportAgentWinLossParamVO vo) {
        if(CollectionUtils.isEmpty(vo.getAgentIds())){
            return Lists.newArrayList();
        }
        return userVenueWinLoseRepository.queryAgentVenueWinLoss(vo);
    }

    public List<ReportVenueWinLossAgentVO> queryByTimeAndAgent(ReportVenueWinLossAgentReqVO vo) {
        List<ReportVenueWinLossAgentVO> vos = userVenueWinLoseRepository.queryByTimeAndAgent(vo);
        vos.forEach(item -> {
            item.setWinLoseAmount(item.getWinLoseAmount().subtract(item.getTipsAmount()));
        });
        return vos;
    }

    public Integer queryByTimeAndSiteCode(SiteDataUserWinLossResVo vo) {
        return userVenueWinLoseRepository.queryByTimeAndSiteCode(vo);
    }

    public Page<ReportGameQueryCenterVO> reportGameCenterPageList(ReportGameQueryCenterReqVO vo) {
        return userVenueWinLoseRepository.reportGameCenterPageList(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }


    public List<ReportGameQueryCenterVO> reportGameCenterPageListSum(ReportGameQueryCenterReqVO vo) {
        return userVenueWinLoseRepository.reportGameCenterPageListSum(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

    public List<ReportGameQueryCenterVO> reportGameCenterPageListTotalSum(ReportGameQueryCenterReqVO vo) {
        return userVenueWinLoseRepository.reportGameCenterPageListTotalSum(vo);
    }


    public Page<ReportGameQuerySiteVO> reportGameSitePageList(ReportGameQuerySiteReqVO vo) {
        return userVenueWinLoseRepository.reportGameSitePageList(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

    public List<ReportGameQuerySiteVO> reportGameSitePageListSum(ReportGameQuerySiteReqVO vo) {
        return userVenueWinLoseRepository.reportGameSitePageListSum(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }


    public List<ReportGameQuerySiteVO> reportGameSitePageListALL(ReportGameQuerySiteReqVO vo) {
        return userVenueWinLoseRepository.reportGameSitePageListALL(vo);
    }

    public Page<ReportGameQueryVenueTypeVO> reportGameVenueTypePageList(ReportGameQueryVenueTypeReqVO vo) {
        return userVenueWinLoseRepository.reportGameVenueTypePageList(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

    public List<ReportGameQueryVenueTypeVO> reportGameVenueTypePageListAll(ReportGameQueryVenueTypeReqVO vo) {
        return userVenueWinLoseRepository.reportGameVenueTypePageListAll(vo);
    }


    public List<ReportGameQueryVenueTypeVO> reportGameVenueTypePageListSum(ReportGameQueryVenueTypeReqVO vo) {
        return userVenueWinLoseRepository.reportGameVenueTypePageListSum(vo);
    }

    public Page<ReportGameQueryVenueVO> reportGameVenuePageList(ReportGameQueryVenueReqVO vo) {
        return userVenueWinLoseRepository.reportGameVenuePageList(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }
    public List<ReportGameQueryVenueVO> reportGameVenuePageListAll(ReportGameQueryVenueReqVO vo) {
        return userVenueWinLoseRepository.reportGameVenuePageListAll(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

    public List<ReportGameQueryVenueVO> reportGameVenuePageListPageSum(ReportGameQueryVenueReqVO vo) {
        return userVenueWinLoseRepository.reportGameVenuePageListPageSum(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

    public Long reportGameCenterCount(ReportGameQueryCenterReqVO vo) {
        return userVenueWinLoseRepository.reportGameCenterCount(vo);
    }

    public Long sitePageListCount(ReportGameQuerySiteReqVO vo) {
        return userVenueWinLoseRepository.sitePageListCount(vo);
    }

    public Long venueTypePageListCount(ReportGameQueryVenueTypeReqVO vo) {
        return userVenueWinLoseRepository.venueTypePageListCount(vo);
    }

    public Long venuePageListCount(ReportGameQueryVenueReqVO vo) {
        return userVenueWinLoseRepository.venuePageListCount(vo);
    }

    public Boolean recalculate(ReportRecalculateVO vo) {
        log.info("场馆盈亏重算开始,入参:{}",vo);
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();
        long nextStartTime = startTime;
        long nextEndTime = startTime + CommonConstant.ONE_HOUR_MILLISECONDS_999;
        String siteCode = vo.getSiteCode();
        Integer size = 500;
        ResponseVO<List<SiteVO>> listResponseVO = siteApi.siteInfoAllstauts();
        Map<String, String> siteMap = listResponseVO.getData().stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO::getTimezone, (k1, k2) -> k2));
        if (StrUtil.isBlank(siteCode)) {
            List<SiteVO> siteVOS = listResponseVO.getData();
            siteVOS.forEach(siteVO -> {
                recalculateProcess(siteVO.getSiteCode(), nextStartTime, nextEndTime, size, siteMap, endTime);
            });
        } else {
            recalculateProcess(siteCode, nextStartTime, nextEndTime, size, siteMap, endTime);
        }
        log.info("场馆盈亏重算结束,入参:{}", vo);
        return true;
    }

    private void recalculateProcess(String siteCode, long nextStartTime, long nextEndTime, Integer size, Map<String, String> siteMap, Long endTime) {
        do {
            VenueWinLoseRecalculateReqVO reqVO = new VenueWinLoseRecalculateReqVO()
                    .setSiteCode(siteCode)
                    .setStartTime(nextStartTime)
                    .setEndTime(nextEndTime);
            reqVO.setPageSize(size);

            List<VenueWinLoseRecalculateVO> records;
            List<ReportUserVenueWinLosePO> poList = Lists.newArrayList();
            Integer pages = 1;
            do {
                reqVO.setPageNumber(pages);
                Page<VenueWinLoseRecalculateVO> recalculateVOPage = orderRecordApi.venueWinLoseRecalculatePage(reqVO);
                records = recalculateVOPage.getRecords();
                long finalNextStartTime = nextStartTime;
                if (CollUtil.isNotEmpty(records)) {
                    records.forEach(record -> {
                        String poSiteCode = record.getSiteCode();
                        record.setDayHourMillis(finalNextStartTime);
                        record.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(finalNextStartTime, siteMap.get(poSiteCode)));
                        ReportUserVenueWinLosePO po = new ReportUserVenueWinLosePO();
                        BeanUtil.copyProperties(record, po);
                        poList.add(po);
                    });
                }
                pages++;
            } while (CollUtil.isNotEmpty(records));
            // 删除存量数据
            new LambdaUpdateChainWrapper<>(baseMapper)
                    .eq(StrUtil.isNotBlank(siteCode), ReportUserVenueWinLosePO::getSiteCode, siteCode)
                    .eq(ReportUserVenueWinLosePO::getDayHourMillis, nextStartTime)
                    .remove();
            // 新增
            saveBatch(poList);
            nextStartTime += CommonConstant.ONE_HOUR_MILLISECONDS;
            nextEndTime = nextStartTime + CommonConstant.ONE_HOUR_MILLISECONDS_999;
        } while (nextEndTime <= endTime);
    }

    public List<ReportAgentVenueStaticsVO> getUserVenueAmountByAgentIds(ReportAgentWinLossParamVO vo) {
        if(CollectionUtils.isEmpty(vo.getAgentIds())){
            return Lists.newArrayList();
        }
        return userVenueWinLoseRepository.getUserVenueAmountByAgentIds(vo);
    }

    public List<ReportAgentVenueStaticsVO> getUserVenueAmountGroupByAgent(ReportAgentWinLossParamVO vo) {
        return userVenueWinLoseRepository.getUserVenueAmountGroupByAgent(vo);
    }

    public List<ReportAgentVenueStaticsVO> queryVenueAmountByDay(ReportAgentWinLossParamVO vo) {
        return userVenueWinLoseRepository.queryVenueAmountByDay(vo);
    }

    public Long getTotalCount(ReportVenueWinLossPageReqVO vo) {
        return userVenueWinLoseRepository.getTotalCount(vo);
    }

    /**
     * 获取站点前一天用户下注信息(返水用)
     * @return
     */
    public List<ReportUserAgentVenueStaticsVO> getUserVenueBetInfo(ReportUserWinLossRebateParamVO vo){
        return userVenueWinLoseRepository.statAgentValidBetAmount(vo.getStartTime(),vo.getEndTime(),
                vo.getVenueType(),vo.getSiteCode(),vo.getCurrencyCode());

//        Page<ReportUserVenueWinLosePO> page = new Page<>(vo.getPageNumber(),vo.getPageSize());
//        LambdaQueryWrapper<ReportUserVenueWinLosePO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ReportUserVenueWinLosePO::getSiteCode, vo.getSiteCode());
//        queryWrapper.ge(ReportUserVenueWinLosePO::getDayHourMillis, vo.getStartTime());
//        queryWrapper.le(ReportUserVenueWinLosePO::getDayHourMillis, vo.getEndTime());
//        queryWrapper.eq(ReportUserVenueWinLosePO::getCurrency,vo.getCurrencyCode());
//        queryWrapper.orderByAsc(ReportUserVenueWinLosePO::getUserId);
//        page = userVenueWinLoseRepository.selectPage(page,queryWrapper);
//        return ConvertUtil.toConverPage(page.convert(item -> BeanUtil.copyProperties(item, ReportUserVenueStaticsVO.class)));
    }

    public List<SiteReportUserVenueStaticsVO> getUserBetNumByDay(ReportUserWinLossRebateParamVO vo){

        return userVenueWinLoseRepository.getDailyDistinctUserCount(vo.getSiteCode(), vo.getStartTime(), vo.getEndTime(),vo.getTimezone());
    }

    public List<SiteReportUserVenueStaticsVO> getDailyCurrencyAmount(ReportUserWinLossRebateParamVO vo){

        return userVenueWinLoseRepository.getDailyCurrencyAmount(vo.getSiteCode(), vo.getStartTime(), vo.getEndTime(),vo.getCurrencyCode(),vo.getTimezone());
    }
}
