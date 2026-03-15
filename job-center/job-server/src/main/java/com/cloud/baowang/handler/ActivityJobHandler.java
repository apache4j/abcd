package com.cloud.baowang.handler;

import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.api.ActivityFreeGameApi;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.api.ActivityRedBagApi;
import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.activity.api.api.v2.ActivityBaseV2Api;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ActivityJobHandler {
    @Resource
    ActivityBaseApi activityBaseApi;

    @Resource
    ActivityBaseV2Api activityBaseV2Api;

    @Resource
    ActivityRedBagApi redBagApi;

    @Resource
    private ActivityParticipateApi activityParticipateApi;

    @Resource
    private TaskConfigApi taskConfigApi;
    @Resource
    private ActivityFreeGameApi activityFreeGameApi;


    /**
     * 活动/任务 优惠过期
     */
    @XxlJob(value = "activityAwardExpire")
    public void activityAwardExpire() {
        log.info("活动优惠过期job开始");
        activityBaseApi.awardExpire();
        activityBaseV2Api.awardExpire();
        taskConfigApi.taskAwardExpire();
        activityFreeGameApi.freeGameExpire();
        log.info("活动优惠过期job结束");
    }

    /**
     * 下一期任务配置生效
     */
    @XxlJob(value = "updateEffective")
    public void updateEffective() {
        log.info("下一期任务配置生效job开始");
        XxlJobHelper.log("----------- 下一期任务配置生效开始-----------");
        taskConfigApi.updateEffective();
        log.info("下一期任务配置生效job结束");
        XxlJobHelper.log("----------- 下一期任务配置生效job结束-----------");
    }

    /**
     * 活动优惠满足条件会员状态变更
     */
    @XxlJob(value = "activityAwardActive")
    public void activityAwardActive() {
        log.info("活动优惠满足条件会员状态变更job开始");
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isNotBlank(jobParam)) {
            String[] split = jobParam.split(CommonConstant.CENTER_LINE);
            String siteCode = split[0];
            String template = split[1];

            //当等于3 的时候代表手动执行定时任务.并且传入参数
            String param = "";
            if (split.length == 3) {
                param = split[2];
            }


            log.info("站点:{},模版:{}", siteCode, template);
            activityBaseApi.awardActive(siteCode, template, param);
        }
        log.info("活动优惠满足条件会员状态变更job结束");
    }

    /**
     * NOTE 活动v2优惠满足条件会员状态变更
     */
    @XxlJob(value = "activityAwardActiveV2")
    public void activityAwardActiveV2() {
        log.info("活动V2优惠满足条件会员状态变更job开始");
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isNotBlank(jobParam)) {
            String[] split = jobParam.split(CommonConstant.CENTER_LINE);
            String siteCode = split[0];
            String template = split[1];

            //当等于3 的时候代表手动执行定时任务.并且传入参数
            String param = "";
            if (split.length == 3) {
                param = split[2];
            }


            log.info("站点:{},V2模版:{}", siteCode, template);
            activityBaseV2Api.awardActive(siteCode, template, param);
        }
        log.info("活动V2优惠满足条件会员状态变更job结束");
    }

    /**
     * 红包雨开始时间推送
     */
    @XxlJob(value = "activityRedBagStartPush")
    public void activityRedBagStartPush() {
        log.info("红包雨开始时间推送job开始");
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isNotBlank(jobParam)) {
            String[] split = jobParam.split(CommonConstant.CENTER_LINE);
            String siteCode = split[0];
            String timeStr = split[1];
            redBagApi.activityRedBagStartPush(siteCode, timeStr);
        }
        log.info("红包雨开始时间推送结束");
    }

    /**
     * 红包雨结束时间推送
     */
    @XxlJob(value = "activityRedBagEndPush")
    public void activityRedBagEndPush() {
        log.info("红包雨结束时间推送job开始");
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isNotBlank(jobParam)) {
            String[] split = jobParam.split(CommonConstant.CENTER_LINE);
            String siteCode = split[0];
            String timeStr = split[1];
            redBagApi.activityRedBagEndPush(siteCode, timeStr);
        }
        log.info("红包雨结束时间推送job结束");
    }


    /**
     * 每日竞赛-勋章发放
     */
    @XxlJob(value = "activityDailMedalAwardActive")
    public void activityDailMedalAwardActive() {
        log.info("每日竞赛-勋章发放-job开始");

        //发放真人场馆勋章
        activityParticipateApi.activityDailMedalAwardActive(VenueTypeEnum.SH.getCode());


        //发放电子场馆勋章
        activityParticipateApi.activityDailMedalAwardActive(VenueTypeEnum.ELECTRONICS.getCode());

        log.info("每日竞赛-勋章发放-job结束");
    }

    /**
     * 场馆-连续30个自然日均有投注，单日流水$1000以上-勋章发放-运动健将
     */
    @XxlJob(value = "activityVenueCodeMedalAwardActive")
    public void activityVenueCodeMedalAwardActive() {
        log.info("运动健将-连续30个自然日均有投注，单日流水$1000以上-勋章发放-job开始");

        //发放体育-场馆勋章
        activityParticipateApi.activityVenueCodeMedalAwardActive(VenueTypeEnum.SPORTS.getCode());


        //发放彩票-场馆勋章
        activityParticipateApi.activityVenueCodeMedalAwardActive(VenueTypeEnum.ACELT.getCode());

        log.info("运动健将-连续30个自然日均有投注，单日流水$1000以上-勋章发放-job结束");
    }


    /**
     * 每日竞赛-计算出当天的top100名用户
     */
    @XxlJob(value = "toSetActivityDailyTop100")
    public void toSetActivityDailyTop100() {
        activityParticipateApi.toSetActivityDailyTop100();
    }


    /**
     * 初始化所有游戏的币种
     */
    @XxlJob(value = "initGameCurrencyCode")
    public void initGameCurrencyCode() {
//        playVenueInfoApi.initGameToVenueCurrency();
    }



    /**
     * 每日竞赛-10分钟计算一次机器人流水
     */
    @XxlJob(value = "calculateActivityDailyRobot")
    public void calculateActivityDailyRobot() {
        log.info("每日竞赛-10分钟计算一次机器人流水:开始");
        activityParticipateApi.calculateActivityDailyRobot();
        log.info("每日竞赛-10分钟计算一次机器人流水:结束");
    }

    /**
     * 每日竞赛-10分钟计算一次机器人流水
     *
     */
    @XxlJob(value = "calculateActivityNewhand")
    public void calculateActivityNewhand() {
        log.info("新手活动任务开始:开始");
        activityParticipateApi.calculateActivityDailyRobot();
        log.info("新手活动任务开始:结束");
    }

    /**
     * NOTE 活动v2优惠满足条件会员状态变更
     */
    @XxlJob(value = "activityAwardActiveNewhand")
    public void activityAwardActiveNewhand() {
        log.info("新手活动任务开始：开始");
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isNotBlank(jobParam)) {
            String[] split = jobParam.split(CommonConstant.CENTER_LINE);
            String siteCode = split[0];
            String template = split[1];

            //当等于3 的时候代表手动执行定时任务.并且传入参数
            String param = "";
            if (split.length == 3) {
                param = split[2];
            }
            activityBaseV2Api.awardActive(siteCode, ActivityTemplateV2Enum.NEW_HAND.getType(), param);
        }
        log.info("新手活动任务开始：结束");
    }


}
