package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.vo.betCoinJoin.BetCoinJoinVO;
import com.cloud.baowang.play.po.BetCoinJoinPO;
import com.cloud.baowang.play.repositories.BetCoinJoinRepository;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BetCoinJoinService extends ServiceImpl<BetCoinJoinRepository, BetCoinJoinPO> {


    /**
     * 查询场馆记录
     *
     * @return 查询数据
     */
    public List<BetCoinJoinPO> getBetCoinJoinList(BetCoinJoinVO betCoinJoinVO) {
        return baseMapper.selectList(Wrappers.lambdaQuery(BetCoinJoinPO.class)
                .eq(StringUtils.isNotBlank(betCoinJoinVO.getVenueCode()), BetCoinJoinPO::getVenueCode, betCoinJoinVO.getVenueCode())
                .eq(StringUtils.isNotBlank(betCoinJoinVO.getBetId()), BetCoinJoinPO::getBetId, betCoinJoinVO.getBetId())
                .eq(StringUtils.isNotBlank(betCoinJoinVO.getOrderId()), BetCoinJoinPO::getOrderId, betCoinJoinVO.getOrderId())
                .eq(StringUtils.isNotBlank(betCoinJoinVO.getTransId()), BetCoinJoinPO::getTransId, betCoinJoinVO.getTransId())
                .orderByAsc(BetCoinJoinPO::getCreatedTime));


    }


    public Map<String, List<BetCoinJoinPO>> getBetCoinJoinMap(String venueCode, List<String> betId) {
        if (CollectionUtil.isEmpty(betId)) {
            return Maps.newHashMap();
        }

        List<BetCoinJoinPO> list = baseMapper.selectList(Wrappers.lambdaQuery(BetCoinJoinPO.class)
                .eq(BetCoinJoinPO::getVenueCode, venueCode)
                .in(BetCoinJoinPO::getBetId, betId)
                .orderByAsc(BetCoinJoinPO::getCreatedTime));

        return list.stream().collect(Collectors.groupingBy(BetCoinJoinPO::getBetId));
    }



    //删除2小时之前的无用数据
    public void delete() {
        // 当前时间往前推2小时
        long twoHoursAgoMillis = System.currentTimeMillis() - 2 * 60 * 60 * 1000L;
        baseMapper.delete(Wrappers.lambdaQuery(BetCoinJoinPO.class)
                .lt(BetCoinJoinPO::getCreatedTime, twoHoursAgoMillis)
        );
    }




}
