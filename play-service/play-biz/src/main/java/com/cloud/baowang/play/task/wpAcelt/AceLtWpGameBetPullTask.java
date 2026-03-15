
package com.cloud.baowang.play.task.wpAcelt;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.acelt.impl.AceLtGameServiceImpl;
import com.cloud.baowang.play.game.sh.constant.SHConstantApi;
import com.cloud.baowang.play.task.acelt.params.AceLtVenuePullBetParams;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.sh.params.ShVenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Log4j2
@Component(ThirdGamePullBetTaskTypeConstant.WP_ACE_LT_GAME_PULL_BET_TASK)
public class AceLtWpGameBetPullTask extends BasePullBetTask {

    @Autowired
    private AceLtGameServiceImpl aceLtGameService;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoRspVO, String pullParamJson) {
        ShVenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, ShVenuePullBetParams.class);
        int i = 0;
        boolean pullType = true;
        while (pullType) {

            //如果这个字段有值并且是 false = 手动拉单,这么说只需要执行一次
            if(venuePullBetParams.getPullType() != null && !venuePullBetParams.getPullType()){
                pullType = false;
            }

            i++;
            log.info("王牌彩票拉单次数:{},开始时间start：{}，end：{}", i,
                    DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime()), DateUtils.FULL_FORMAT_1)
                    , DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime()), DateUtils.FULL_FORMAT_1));
            VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
            venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
            venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
            ResponseVO<?> responseVO = aceLtGameService.getBetRecordList(venueInfoRspVO, venuePullParamVO);
            log.info("王牌彩票拉单次数:{},结束", i);
            if (!responseVO.isOk()) {
                return venuePullBetParams;
            }
            venuePullBetParams = (ShVenuePullBetParams) genNextPullParams(venuePullBetParams);
            if (venuePullBetParams.getEndTime() >= System.currentTimeMillis()) {
                return venuePullBetParams;
            }
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                log.error(e);
            }
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.WP_ACELT;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        AceLtVenuePullBetParams venuePullBetParams = new AceLtVenuePullBetParams();
        venuePullBetParams.setStartTime(System.currentTimeMillis());
        venuePullBetParams.setStep(10 * 60 * 1000L); //拉取10分钟
        venuePullBetParams.setLastOrderId(0L);
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime() + venuePullBetParams.getStep());
        return venuePullBetParams;
    }

    /**
     * 生成下次拉单参数
     *
     * @param currPullBetParams 当前拉单参数
     * @return
     */

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        ShVenuePullBetParams params = (ShVenuePullBetParams) currPullBetParams;
        ShVenuePullBetParams newPullBetParams = new ShVenuePullBetParams();
        newPullBetParams.setStartTime(params.getStartTime());
        newPullBetParams.setEndTime(params.getEndTime());

        long timeInterval = params.getStep() == null ? SHConstantApi.DEFAULT_STEP : params.getStep();
        newPullBetParams.setStep(timeInterval);

        newPullBetParams.setStartTime(newPullBetParams.getEndTime() - newPullBetParams.getStep());

        newPullBetParams.setEndTime(newPullBetParams.getEndTime() + newPullBetParams.getStep());
        if (newPullBetParams.getEndTime() > System.currentTimeMillis()) {
            newPullBetParams.setStartTime(System.currentTimeMillis() - newPullBetParams.getStep());
            newPullBetParams.setEndTime(System.currentTimeMillis());
        }
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        ShVenuePullBetParams venuePullBetParams = new ShVenuePullBetParams();
        long startTimeLong = Long.parseLong(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + SHConstantApi.DEFAULT_STEP.longValue());
        // 获取下次拉单参数
//        ShVenuePullBetParams shVenuePullBetParams = (ShVenuePullBetParams) genNextPullParams(venuePullBetParams);
//        // 本次结束时间
//        shVenuePullBetParams.setManualCurrentPullEndTime(shVenuePullBetParams.getEndTime().toString());
//        return shVenuePullBetParams;
        return venuePullBetParams;
    }

}

