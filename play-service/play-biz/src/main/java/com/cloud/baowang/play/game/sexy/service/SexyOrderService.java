package com.cloud.baowang.play.game.sexy.service;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.play.api.vo.sexy.enums.SexyOrderType;
import com.cloud.baowang.play.api.vo.sexy.req.SexyBetRequest;
import com.cloud.baowang.play.api.vo.sexy.req.SexyGameInfo;
import com.cloud.baowang.play.game.sexy.po.SexyOrderRecordPO;
import com.cloud.baowang.play.game.sexy.po.SexyOrderRecordRepository;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class SexyOrderService extends ServiceImpl<SexyOrderRecordRepository, SexyOrderRecordPO> {
    private final SexyOrderRecordRepository repository;

    public void handleSexyBetOrder(UserInfoVO userInfoVO, List<SexyBetRequest> betRequest, SexyOrderType orderType) {
        if (SexyOrderType.BET.equals(orderType)) {
            List<SexyOrderRecordPO> sexyOrderRecordPOS = Lists.newArrayList();
            betRequest.forEach(order -> {
                SexyOrderRecordPO recordPO = buildBasicOrderInfo(userInfoVO, order);
                recordPO.setOrderType(orderType.getCode());
                sexyOrderRecordPOS.add(recordPO);

            });
            this.saveBatch(sexyOrderRecordPOS);
        } else {
            betRequest.forEach(order -> {
                SexyOrderRecordPO recordPO = buildBasicOrderInfo(userInfoVO, order);
                if (StringUtils.isNotBlank(order.getGameInfo())) {
                    try {
                        SexyGameInfo gameInfo = JSON.parseObject(order.getGameInfo(), SexyGameInfo.class);
                        recordPO.setDeskNo(gameInfo.getTableId());
                        recordPO.setResultList(JSON.toJSONString(gameInfo.getResult())); // 如果想存字符串
                        recordPO.setWinner(gameInfo.getWinner());
                        recordPO.setOdds(String.valueOf(gameInfo.getOdds()));
                        recordPO.setIp(gameInfo.getIp());
                        recordPO.setWinLoss(gameInfo.getWinLoss());
                        recordPO.setStatus(gameInfo.getStatus());
                    } catch (Exception e) {
                        log.error("解析sexy-gameInfo出错,内容: {}", recordPO.getGameInfo(), e);
                    }
                }
                Long txTime = order.getTxTime() == null ? 0L : convertTimeStr(order.getTxTime());
                recordPO.setTxTime(txTime);
                Long updateTime = order.getUpdateTime() == null ? 0L : convertTimeStr(order.getUpdateTime());
                recordPO.setUpdateTime(updateTime);
                recordPO.setOrderType(orderType.getCode());
                LambdaUpdateWrapper<SexyOrderRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SexyOrderRecordPO::getPlatformTxId, order.getPlatformTxId());
                log.info("sexy真人插入po : " + recordPO);
                repository.update(recordPO, updateWrapper);

            });
        }
    }

    public SexyOrderRecordPO buildBasicOrderInfo(UserInfoVO userInfoVO, SexyBetRequest order) {
        SexyOrderRecordPO recordPO = new SexyOrderRecordPO();
        try {
            BeanUtils.copyProperties(order, recordPO);
        } catch (Exception e) {
            log.error("copyProperties : ", e);
        }
        recordPO.setCurrency(userInfoVO.getMainCurrency());
        recordPO.setUserId(userInfoVO.getUserId());
        recordPO.setVenueUserId(order.getUserId());
        //下注时间
        if (StringUtils.isNotBlank(order.getBetTime())) {
            recordPO.setBetTime(convertTimeStr(order.getBetTime()));
        }
        //交易确认时间
        recordPO.setBetType(order.getBetType());
        if (StringUtils.isNotBlank(order.getGameInfo())) {
            JSONObject obj = JSON.parseObject(recordPO.getGameInfo());
            recordPO.setDeskNo(obj.getString("tableId"));
        }
        recordPO.setGameInfo(JSON.toJSONString(order));
        return recordPO;
    }


    public Long convertTimeStr(String timeStr) {
        OffsetDateTime odt = OffsetDateTime.parse(timeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return odt.toInstant().toEpochMilli();
    }

}
