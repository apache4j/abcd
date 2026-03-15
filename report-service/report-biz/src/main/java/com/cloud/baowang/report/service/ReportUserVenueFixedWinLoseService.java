package com.cloud.baowang.report.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.ReportUserWinLossReqVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.report.po.ReportUserFixedVenueWinlosePO;
import com.cloud.baowang.report.po.ReportUserVenueWinLosePO;
import com.cloud.baowang.report.repositories.ReportUserFixedVenueWinloseRepository;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReportUserVenueFixedWinLoseService extends ServiceImpl<ReportUserFixedVenueWinloseRepository, ReportUserFixedVenueWinlosePO> {
    private final ReportUserFixedVenueWinloseRepository userFixedVenueWinloseRepository;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(name = RedisConstants.VENUE_FIXED_WIN_LOSS_LOCK_KEY, unique = "#vo.userId", fair = true, waitTime = 3, leaseTime = 180)
    public void userVenueWinLossHandler(UserVenueWinLossMqVO vo) {
        Long dayHour = vo.getFirstSettleDayHour();
        ReportUserFixedVenueWinlosePO po = new LambdaQueryChainWrapper<>(userFixedVenueWinloseRepository)
                .eq(StrUtil.isNotBlank(vo.getAgentId()), ReportUserFixedVenueWinlosePO::getAgentId, vo.getAgentId())
                .eq(StrUtil.isNotBlank(vo.getVenueGameType()), ReportUserFixedVenueWinlosePO::getVenueGameType, vo.getVenueGameType())
                .eq(ReportUserFixedVenueWinlosePO::getSiteCode, vo.getSiteCode())
                .eq(ReportUserFixedVenueWinlosePO::getVenueCode, vo.getVenueCode())
                .eq(ReportUserFixedVenueWinlosePO::getVenueType, vo.getVenueType())
                .eq(ReportUserFixedVenueWinlosePO::getDayHourMillis, dayHour)
                .eq(ReportUserFixedVenueWinlosePO::getUserId, vo.getUserId())
                .one();

        if (po == null) {
            long siteDayMillis = TimeZoneUtils.getStartOfDayInTimeZone(dayHour, vo.getTimeZone());
            ReportUserFixedVenueWinlosePO winLosePO = new ReportUserFixedVenueWinlosePO();
            winLosePO.setSiteCode(vo.getSiteCode());
            winLosePO.setDayHourMillis(dayHour);
            winLosePO.setDayMillis(siteDayMillis);
            winLosePO.setVenueGameType(vo.getVenueGameType());
            winLosePO.setVenueCode(vo.getVenueCode());
            winLosePO.setVenueType(vo.getVenueType());
            winLosePO.setUserAccount(vo.getUserAccount());
            winLosePO.setUserId(vo.getUserId());
            winLosePO.setAgentId(vo.getAgentId());
            winLosePO.setAgentAccount(vo.getAgentAccount());
            winLosePO.setBetCount(vo.getBetCount());
            winLosePO.setWinLossAmount(vo.getWinLossAmount());
            winLosePO.setValidAmount(vo.getValidAmount());
            winLosePO.setBetAmount(vo.getBetAmount());
            winLosePO.setCurrency(vo.getCurrency());
            userFixedVenueWinloseRepository.insert(winLosePO);
        } else {
            BigDecimal lastBetWinLose = ObjUtil.isNull(vo.getLastDayHour()) ? BigDecimal.ZERO : vo.getLastBetWinLose();
            BigDecimal betAmount = ObjUtil.isNull(vo.getLastDayHour()) ? BigDecimal.ZERO : vo.getLastBetAmount();
            BigDecimal validBetAmount = ObjUtil.isNull(vo.getLastDayHour()) ? BigDecimal.ZERO : vo.getLastValidBetAmount();
            po.setBetCount(po.getBetCount() + vo.getBetCount());
            po.setBetAmount(po.getBetAmount().add(vo.getBetAmount()).subtract(betAmount));
            po.setValidAmount(po.getValidAmount().add(vo.getValidAmount()).subtract(validBetAmount));
            po.setWinLossAmount(po.getWinLossAmount().add(vo.getWinLossAmount()).subtract(lastBetWinLose));
            userFixedVenueWinloseRepository.updateById(po);
        }
    }

    public ReportUserVenueBetsTopVO queryUserWinLossInfo(ReportUserWinLossReqVO userWinLossReqVO) {
        List<ReportUserVenueBetsTopVO> list = userFixedVenueWinloseRepository.queryUserWinLossInfo(userWinLossReqVO);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public List<String> queryVenueDayCurrency(ReportUserTopReqVO vo) {
        return userFixedVenueWinloseRepository.queryVenueDayCurrency(vo);
    }

    public Page<ReportUserVenueBetsTopVO> queryUserIdsByVenueDayAmount(ReportUserTopReqVO vo) {
        return userFixedVenueWinloseRepository.queryUserIdsByVenueDayAmount(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }


    public List<ReportUserVenueBetsTopVO> queryUserBetsTopPlatBetAmount(ReportUserTopReqVO userTopReqVO) {
        Map<String, BigDecimal> siteCurrencyMap = siteCurrencyInfoApi.getAllFinalRate(userTopReqVO.getSiteCode());

        if (CollectionUtil.isEmpty(siteCurrencyMap)) {
            log.info("查询出前100的用户投注失败,币种汇率查询失败:{}", userTopReqVO);
            return Lists.newArrayList();
        }

        //查询出所有币种的前100条数据
        List<ReportUserVenueBetsTopVO> dateList = userFixedVenueWinloseRepository.queryAllCurrencyTop(userTopReqVO);

        Map<String, List<ReportUserVenueBetsTopVO>> listMap = dateList.stream().collect(Collectors.groupingBy(ReportUserVenueBetsTopVO::getCurrency));

        Map<String, List<ReportUserVenueBetsTopVO>> currencyMap = new HashMap<>();


        //将每个币种的前100条数据取出来
        for (Map.Entry<String, List<ReportUserVenueBetsTopVO>> dateItem : listMap.entrySet()) {
            List<ReportUserVenueBetsTopVO> data = dateItem.getValue();
            if (CollectionUtil.isNotEmpty(data)) {
                List<ReportUserVenueBetsTopVO> topList = data.stream()
                        .sorted((a, b) -> b.getValidAmount().compareTo(a.getValidAmount())) // 倒序排序
                        .limit(100) // 取前100条
                        .toList();

                currencyMap.put(dateItem.getKey(), topList);

            }
        }


        List<ReportUserVenueBetsTopVO> resultList = Lists.newArrayList();

        //将所有币种都转成平台币
        for (Map.Entry<String, List<ReportUserVenueBetsTopVO>> itemMap : currencyMap.entrySet()) {
            String currencyCode = itemMap.getKey();
            List<ReportUserVenueBetsTopVO> currencyDateList = itemMap.getValue();
            BigDecimal rate = siteCurrencyMap.get(currencyCode);
            if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            for (ReportUserVenueBetsTopVO betsTopVO : currencyDateList) {
                ReportUserVenueBetsTopVO top = new ReportUserVenueBetsTopVO();


                BigDecimal validAmount = betsTopVO.getValidAmount();
                if (validAmount.compareTo(BigDecimal.ZERO) > 0) {
                    top.setPlatValidAmount(AmountUtils.divide(validAmount, rate));
                }


                BigDecimal betAmount = betsTopVO.getBetAmount();
                if (betAmount.compareTo(BigDecimal.ZERO) > 0) {
                    top.setPlatBetAmount(AmountUtils.divide(betAmount, rate));
                }


                BigDecimal winLossAmount = betsTopVO.getWinLossAmount();
                if (winLossAmount.compareTo(BigDecimal.ZERO) != 0) {
                    top.setPlatWinLossAmount(AmountUtils.divide(winLossAmount, rate));
                }
                top.setUserId(betsTopVO.getUserId());
                top.setUserAccount(betsTopVO.getUserAccount());
                top.setBetAmount(betAmount);
                top.setValidAmount(validAmount);
                top.setWinLossAmount(winLossAmount);
                top.setCurrency(betsTopVO.getCurrency());
                resultList.add(top);
            }
        }

        //如果出现相同的用户则汇总成一条
        resultList = new ArrayList<>(resultList.stream()
                .collect(Collectors.toMap(
                        ReportUserVenueBetsTopVO::getUserId,
                        user -> new ReportUserVenueBetsTopVO(user.getUserId(), user.getUserAccount(), user.getCurrency(),
                                user.getWinLossAmount(), user.getBetAmount(), user.getValidAmount(),
                                user.getPlatWinLossAmount(), user.getPlatBetAmount(), user.getPlatValidAmount()),
                        (existing, replacement) -> {
                            existing.setWinLossAmount(existing.getWinLossAmount().add(replacement.getWinLossAmount()));
                            existing.setBetAmount(existing.getBetAmount().add(replacement.getBetAmount()));
                            existing.setValidAmount(existing.getValidAmount().add(replacement.getValidAmount()));
                            existing.setPlatWinLossAmount(existing.getPlatWinLossAmount().add(replacement.getPlatWinLossAmount()));
                            existing.setPlatBetAmount(existing.getPlatBetAmount().add(replacement.getPlatBetAmount()));
                            existing.setPlatValidAmount(existing.getPlatValidAmount().add(replacement.getPlatValidAmount()));
                            return existing;
                        }
                ))
                .values());

        // 按 validAmount 倒序排序并获取前 100 条数据
        return resultList.stream()
                .filter(x -> x.getPlatValidAmount() != null && x.getPlatValidAmount().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(ReportUserVenueBetsTopVO::getPlatValidAmount).reversed())
                .limit(100)
                .toList();

    }


    public ReportUserVenueBetsTopVO queryVenueBetsPlatBetAmountTotal(ReportUserTopReqVO userTopReqVO) {
        List<ReportUserVenueBetsTopVO> list = userFixedVenueWinloseRepository.queryVenueBetsPlatBetAmountTotal(userTopReqVO);
        ReportUserVenueBetsTopVO topVO = toSetConvPlatAmount(list, userTopReqVO);
        log.info("每日竞赛奖池总额:场馆:{},list:{},转平台币:{}", userTopReqVO.getVenueCodeList(), list, topVO);
        return topVO;
    }


    private ReportUserVenueBetsTopVO toSetConvPlatAmount(List<ReportUserVenueBetsTopVO> list, ReportUserTopReqVO userTopReqVO) {
        Map<String, BigDecimal> siteCurrencyMap = siteCurrencyInfoApi.getAllFinalRate(userTopReqVO.getSiteCode());
        if (CollectionUtil.isEmpty(siteCurrencyMap)) {
            log.info("查询用户投注失败,币种汇率查询失败:{}", userTopReqVO);
            return null;
        }
        for (ReportUserVenueBetsTopVO item : list) {
            BigDecimal rate = siteCurrencyMap.get(item.getCurrency());
            if (ObjectUtil.isEmpty(rate) || rate.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("查询用户投注失败,币种汇率查询失败:{},currency:{}", userTopReqVO, item.getCurrency());
                continue;
            }

            if (item.getBetAmount() != null && item.getBetAmount().compareTo(BigDecimal.ZERO) > 0) {
                item.setPlatBetAmount(AmountUtils.divide(item.getBetAmount(), rate));
            }

            if (item.getValidAmount() != null && item.getValidAmount().compareTo(BigDecimal.ZERO) > 0) {
                item.setPlatValidAmount(AmountUtils.divide(item.getValidAmount(), rate));
            }

            if (item.getWinLossAmount() != null && item.getWinLossAmount().compareTo(BigDecimal.ZERO) > 0) {
                item.setPlatWinLossAmount(AmountUtils.divide(item.getWinLossAmount(), rate));
            }
        }
        ReportUserVenueBetsTopVO result = null;
        if (CollectionUtil.isNotEmpty(list)) {
            result = new ReportUserVenueBetsTopVO();
            ReportUserVenueBetsTopVO topVO = list.get(0);

            if (topVO.getUserId() != null) {
                result.setUserId(topVO.getUserId());
            }

            if (topVO.getUserAccount() != null) {
                result.setUserAccount(topVO.getUserAccount());
            }

            result.setCurrency(topVO.getCurrency());


            // 汇总所有非空 price 字段的总和
            BigDecimal betAmount = list.stream()
                    .map(ReportUserVenueBetsTopVO::getBetAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.setBetAmount(betAmount);

            BigDecimal validAmount = list.stream()
                    .map(ReportUserVenueBetsTopVO::getValidAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setValidAmount(validAmount);

            BigDecimal winLossAmount = list.stream()
                    .map(ReportUserVenueBetsTopVO::getWinLossAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setWinLossAmount(winLossAmount);

            BigDecimal platBetAmount = list.stream()
                    .map(ReportUserVenueBetsTopVO::getPlatBetAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setPlatBetAmount(platBetAmount);

            BigDecimal platWinLossAmount = list.stream()
                    .map(ReportUserVenueBetsTopVO::getPlatWinLossAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setPlatWinLossAmount(platWinLossAmount);

            BigDecimal platValidAmount = list.stream()
                    .map(ReportUserVenueBetsTopVO::getPlatValidAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.setPlatValidAmount(platValidAmount);
        }

        return result;
    }

    public ReportUserVenueBetsTopVO queryUserBetsPlatBetAmountTotal(ReportUserTopReqVO userTopReqVO) {
        List<ReportUserVenueBetsTopVO> list = userFixedVenueWinloseRepository.queryUserBetsPlatBetAmountTotal(userTopReqVO);
        return toSetConvPlatAmount(list, userTopReqVO);
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
        return userFixedVenueWinloseRepository.queryUserBetsTop(page, userTopReqVO);
    }
}
