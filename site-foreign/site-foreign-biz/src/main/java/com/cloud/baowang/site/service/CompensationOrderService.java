package com.cloud.baowang.site.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.api.third.ThirdPullBetApi;
import com.cloud.baowang.play.api.enums.VenueManulPullEnum;
import com.cloud.baowang.play.api.vo.third.betpull.ManualGamePullReqVO;
import com.cloud.baowang.site.vo.CompensationOrderReqVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class CompensationOrderService {

    private final ThirdPullBetApi thirdPullBetApi;

//    public Boolean manualGamePullTask(CompensationOrderReqVO requestVO) {
//        timeCheck(requestVO);
//        List<String> typeList = VenueManulPullEnum.getPullTypeByVenueCode(requestVO.getVenueCode());
//        if (CollectionUtil.isEmpty(typeList)) {
//            throw new BaowangDefaultException(ResultCode.NOT_SUPPORTED_YET);
//        }
//        for (String type : typeList) {
//            ManualGamePullReqVO manualGamePullReqVO = new ManualGamePullReqVO();
//            // 只有部分补单是有2个，所以此处循环调用
//            BeanUtil.copyProperties(requestVO, manualGamePullReqVO);
//            manualGamePullReqVO.setType(type);
//            thirdPullBetApi.manualGamePullTask(manualGamePullReqVO);
//        }
//        return true;
//    }
    public Boolean manualGamePullTask(CompensationOrderReqVO requestVO){
        timeCheck(requestVO);
        List<String> typeList = VenueManulPullEnum.getPullTypeByVenueCode(requestVO.getVenueCode());
        log.info("venueCode:"+requestVO.getVenueCode()+"枚举:"+typeList);
        if (CollectionUtil.isEmpty(typeList)){
            throw new BaowangDefaultException(ResultCode.NOT_SUPPORTED_YET);
        }

        for (String type : typeList) {
            ManualGamePullReqVO manualGamePullReqVO = new ManualGamePullReqVO();
            // 拉取状态检查
            String lockValue = RedisUtil.getValue(String.format(RedisKeyTransUtil.CASINO_THIRD_MANUAL_PULL_BET_LOCK_KEY, type));
            if (lockValue != null){
                // 任务正在执行中
                throw new BaowangDefaultException(ResultCode.EXECUTING);

            }
            Long end = Long.valueOf(requestVO.getEndTime());
            Long start = Long.valueOf(requestVO.getStartTime());
            // 第三方限制时间只能为10分钟一次， 如果大于10分钟就做切割
            if(requestVO.getVenueCode().equals(VenueManulPullEnum.MARBLES.getVenueCode()) && end - start > 600000){
               List<ManualGamePullReqVO> manualGamePullReqVOS = new ArrayList<>(2);
                // 第一次执行
                BeanUtil.copyProperties(requestVO,manualGamePullReqVO);
                manualGamePullReqVO.setType(type);
                manualGamePullReqVO.setStartTime(start+"");
                end = start + 600000;
                manualGamePullReqVO.setEndTime(end+"");
                manualGamePullReqVOS.add(manualGamePullReqVO);
                manualGamePullReqVO = new ManualGamePullReqVO();
                // 第二次执行
                BeanUtil.copyProperties(requestVO,manualGamePullReqVO);
                manualGamePullReqVO.setType(type);
                manualGamePullReqVO.setStartTime(end+"");
                end = end + 600000;
                manualGamePullReqVO.setEndTime(end+"");
                manualGamePullReqVOS.add(manualGamePullReqVO);

                for (ManualGamePullReqVO gamePullReqVO : manualGamePullReqVOS) {
                    log.info("IM弹珠手动执行={}", JSONObject.toJSONString(gamePullReqVO));
                    thirdPullBetApi.manualGamePullTask(gamePullReqVO);
                    try{
                        // 停留5秒
                        Thread.sleep(5000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }else{
                // 只有部分补单是有2个，所以此处循环调用
                BeanUtil.copyProperties(requestVO,manualGamePullReqVO);
                manualGamePullReqVO.setType(type);
                thirdPullBetApi.manualGamePullTask(manualGamePullReqVO);
            }

        }
        return true;
    }


    private void timeCheck(CompensationOrderReqVO requestVO) {
        long nowTime = System.currentTimeMillis();
        Long startTime = Long.valueOf(requestVO.getStartTime());
        Long endTime = Long.valueOf(requestVO.getEndTime());

        long fiveMinuteAgo = nowTime - 5 * 60 * 1000L;

        if (endTime.compareTo(fiveMinuteAgo) > 0) {
            requestVO.setEndTime(String.valueOf(fiveMinuteAgo));
        }

        long threeMonthAgo = nowTime - 3 * 30 * 24 * 60 * 60 * 1000L;

        if (startTime.compareTo(threeMonthAgo) < 0) {
            // 只能拉取三个月内的数据
            throw new BaowangDefaultException(ResultCode.PULL_TIME_ERR);
        }
    }

}
