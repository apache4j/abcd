package com.cloud.baowang.handler;

import com.cloud.baowang.wallet.api.enums.wallet.SBATransferEnums;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.play.api.api.third.SBASportApi;
import com.cloud.baowang.play.api.api.third.ThirdPullBetApi;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.vo.third.betpull.GamePullReqVO;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ThirdOrderPullHandler {
    private final ThirdPullBetApi thirdPullBetApi;

    private final SBASportApi sbaSportApi;

    public ThirdOrderPullHandler(final ThirdPullBetApi thirdPullBetApi, SBASportApi sbaSportApi) {
        this.thirdPullBetApi = thirdPullBetApi;
        this.sbaSportApi = sbaSportApi;
    }

    /**
     * 根据服务类型拉取注单
     *
     * @param taskType 服务类型
     */
    private void gamePullTask(String taskType) {
        GamePullReqVO gamePullReqVO = new GamePullReqVO();
        gamePullReqVO.setType(taskType);
        gamePullReqVO.setJsonParam(getGamePullXxlJobParam());
        log.info("执行拉单:{}", gamePullReqVO);
        thirdPullBetApi.gamePullTask(gamePullReqVO);
    }

    /**
     * 获取xxl-job参数
     *
     * @return 参数
     */
    private String getGamePullXxlJobParam() {
        String jobParam = XxlJobHelper.getJobParam();
        if (Strings.isBlank(jobParam)) {
            // 无参数直接返回
            return null;
        }
        // json 格式校验
        try {
            JsonElement jsonElement = JsonParser.parseString(jobParam);
        } catch (Exception e) {
            log.error("注单拉取参数格式错误{}", jobParam);
            XxlJobHelper.log("注单拉取参数格式错误");
            throw e;
        }
        return jobParam;
    }

    /**
     * PG电子
     */
    @XxlJob(value = "pgPullBetTask")
    public void pgPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PG_GAME_PULL_BET_TASK);
    }


    /**
     * EVO 视讯
     */
    @XxlJob(value = "evoPullBetTask")
    public void evoPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.EVO_GAME_PULL_BET_TASK);
    }

    /**
     * SH 视讯
     */
    @XxlJob(value = "shPullBetTask")
    public void shPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.SH_GAME_PULL_BET_TASK);
    }


    /**
     * 皮肤4-SH 视讯
     */
    @XxlJob(value = "shZhCnPullBetTask")
    public void shZhCnPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.SH_ZHCN_GAME_PULL_BET_TASK);
    }



    /**
     * SA-视讯
     */
    @XxlJob(value = "saPullBetTask")
    public void saPullBetTask() {
        log.info("SA-视讯");
        gamePullTask(ThirdGamePullBetTaskTypeConstant.SA_GAME_PULL_BET_TASK);
    }



    /**
     * 沙巴体育
     */
    @XxlJob(value = "sbaPullBetTask")
    public void sbaPullBetTask() {
        log.info("执行沙巴体育拉单");
        gamePullTask(ThirdGamePullBetTaskTypeConstant.SBA_GAME_PULL_BET_TASK);
    }


    /**
     * 沙巴体育-拉未来赛事
     */
    @XxlJob(value = "sbaPullEventsTask")
    public void sbaPullEventsTask() {
        log.info("执行沙巴体育拉未来赛事");
        sbaSportApi.sbaPullGameEventsTask();
    }


    /**
     * 沙巴体育-拉联赛基础信息
     */
    @XxlJob(value = "sbaPullEventInfo")
    public void sbaPullEventInfo() {
        log.info("拉取沙巴体育-联赛基础信息");
        sbaSportApi.sbaPullEventInfo();
    }


    /**
     * 沙巴体育-拉取未处理的订单-已下注未确认
     */
    @XxlJob(value = "sbaPullPlatBet")
    public void sbaPullPlatBet() {
        log.info("沙巴体育-拉取未处理的订单-已下注未确认");
        String jobParam = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(jobParam)) {
            jobParam = "";
        }
        sbaSportApi.sbaPullStatus(SBATransferEnums.PLACE_BET.getCode(), jobParam);
    }


    /**
     * 沙巴体育-拉取未处理的订单-已确认-未结算
     */
    @XxlJob(value = "sbaPullConfirmBet")
    public void sbaPullConfirmBet() {
        log.info("沙巴体育-拉取未处理的订单-已确认-未结算");

        String jobParam = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(jobParam)) {
            jobParam = "";
        }
        sbaSportApi.sbaPullStatus(SBATransferEnums.CONFIRM_BET.getCode(), jobParam);
    }


    /**
     * 沙巴体育-拉取有已达重试上限的注单
     */
    @XxlJob(value = "sbaPullReachLimitTrans")
    public void sbaPullReachLimitTrans() {
        log.info("沙巴体育-拉取有已达重试上限的注单");
        String jobParam = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(jobParam)) {
            jobParam = "";
        }
        sbaSportApi.sbaPullReachLimitTrans(jobParam);

        //昨天的
        long yestTime = TimeZoneUtils.adjustTimestamp(System.currentTimeMillis(), -1, TimeZoneUtils.sbaTimeZone);
        String yestDay = TimeZoneUtils.formatLocalDateTime(TimeZoneUtils.timeByTimeZone(yestTime, DateUtils.FULL_FORMAT_5), TimeZoneUtils.sbaTimeZone);
        sbaSportApi.sbaPullReachLimitTrans(yestDay);
    }


    /**
     * S128 拉单
     */
    @XxlJob(value = "s128PullBetTask")
    public void s128PullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.S128_GAME_PULL_BET_TASK);
    }

    /**
     * TF电竞
     */
    @XxlJob(value = "tfPullBetTask")
    public void tfPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.TF_GAME_PULL_BET_TASK);
    }


    /**
     * WINTO-彩票
     */
    @XxlJob(value = "aceltPullBetTask")
    public void aceltPullBetTask() {
        log.info("执行彩票拉单");
        gamePullTask(ThirdGamePullBetTaskTypeConstant.ACE_LT_GAME_PULL_BET_TASK);
    }

    /**
     * 王牌-彩票
     */
    @XxlJob(value = "wpAceltPullBetTask")
    public void wpAceltPullBetTask() {
        log.info("执行王牌彩票拉单");
        gamePullTask(ThirdGamePullBetTaskTypeConstant.WP_ACE_LT_GAME_PULL_BET_TASK);
    }

    /**
     * 王牌-彩票 02
     */
    @XxlJob(value = "wpAcelt02PullBetTask")
    public void wpAcelt02PullBetTask() {
        log.info("执行王牌彩票拉单");
        gamePullTask(ThirdGamePullBetTaskTypeConstant.WP_ACE_LT_02_GAME_PULL_BET_TASK);
    }


    /**
     * 王牌-彩票 03
     */
    @XxlJob(value = "wpAcelt03PullBetTask")
    public void wpAcelt03PullBetTask() {
        log.info("执行王牌彩票拉单");
        gamePullTask(ThirdGamePullBetTaskTypeConstant.WP_ACE_LT_03_GAME_PULL_BET_TASK);
    }


    /**
     * 王牌-彩票 03
     */
    @XxlJob(value = "wpAceltOkPullBetTask")
    public void wpAceltOkPullBetTask() {
        log.info("执行王牌彩票拉单");
        gamePullTask(ThirdGamePullBetTaskTypeConstant.WP_ACE_LT_OK_GAME_PULL_BET_TASK);
    }

    /**
     * 王牌-彩票 国内站皮肤4
     */
    @XxlJob(value = "wpAceltZhCnPullBetTask")
    public void wpAceltZhCnPullBetTask() {
        log.info("执行国内站-王牌彩票拉单");
        gamePullTask(ThirdGamePullBetTaskTypeConstant.WP_ACE_LT_ZH_CN_GAME_PULL_BET_TASK);
    }

    /**
     * 彩票
     */
    @XxlJob(value = "ftgPullBetTask")
    public void ftgPullBetTask() {
        log.info("执行FTG拉单");
        gamePullTask(ThirdGamePullBetTaskTypeConstant.FTG_GAME_PULL_BET_TASK);
    }

    /**
     * OMG-PP
     */
    @XxlJob(value = "ppPlusPullBetTask")
    public void ppPlusPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PP_PLUS_GAME_PULL_BET_TASK);
    }

    /**
     * OMG-PG
     */
    @XxlJob(value = "pgPlusPullBetTask")
    public void pgPlusPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PG_PLUS_GAME_PULL_BET_TASK);
    }

    /**
     * OMG-JILI
     */
    @XxlJob(value = "jiliPlusPullBetTask")
    public void jiliPlusPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.JILI_PLUS_GAME_PULL_BET_TASK);
    }


    /**
     * LGD
     */
    @XxlJob(value = "lgdPullBetTask")
    public void lgdPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.LGD_GAME_PULL_BET_TASK);
    }


    /**
     * Im-弹珠
     */
    @XxlJob(value = "marblesPullBetTask")
    public void marblesPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.MARBLES_GAME_PULL_BET_TASK);
    }


    /**
     * v8
     */
    @XxlJob(value = "v8PullBetTask")
    public void v8PullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.V8_GAME_PULL_BET_TASK);
    }

    /**
     * CQ9棋牌拉取订单
     */
    @XxlJob(value = "cq9GamePullTask")
    public void cq9GamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.CQ9_GAME_PULL_BET_TASK);
    }


    /**
     * OMG-PP
     */
    @XxlJob(value = "ppPlusPullBetTask02")
    public void ppPlusPullBetTask02() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PP_PLUS_GAME_PULL_BET_TASK_02);
    }

    /**
     * OMG-PG
     */
    @XxlJob(value = "pgPlusPullBetTask02")
    public void pgPlusPullBetTask02() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PG_PLUS_GAME_PULL_BET_TASK_02);
    }
    /**
     * OMG-JILI
     */
    @XxlJob(value = "jiliPlusPullBetTask02")
    public void jiliPlusPullBetTask02() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.JILI_PLUS_GAME_PULL_BET_TASK_02);
    }



    /**
     * OMG-PP-皮肤4
     */
    @XxlJob(value = "ppPlusPullBetTask04")
    public void ppPlusPullBetTask04() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PP_PLUS_GAME_PULL_BET_TASK_04);
    }

    /**
     * OMG-PG-皮肤4
     */
    @XxlJob(value = "pgPlusPullBetTask04")
    public void pgPlusPullBetTask04() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PG_PLUS_GAME_PULL_BET_TASK_04);
    }
    /**
     * OMG-JILI0皮肤4
     */
    @XxlJob(value = "jiliPlusPullBetTask04")
    public void jiliPlusPullBetTask04() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.JILI_PLUS_GAME_PULL_BET_TASK_04);
    }

    /**
     * 新JILI
     */
    @XxlJob(value = "jili03GamePullTask")
    public void jili03GamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.JILI_03_GAME_PULL_BET_TASK);
    }

    /**
     * OMG-PP
     */
    @XxlJob(value = "ppPlusPullBetTask03")
    public void ppPlusPullBetTask03() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PP_PLUS_GAME_PULL_BET_TASK_03);
    }
    /**
     * OMG-PG
     */
    @XxlJob(value = "pgPlusPullBetTask03")
    public void pgPlusPullBetTask03() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PG_PLUS_GAME_PULL_BET_TASK_03);
    }
    /**
     * OMG-JILI
     */
    @XxlJob(value = "jiliPlusPullBetTask03")
    public void jiliPlusPullBetTask03() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.JILI_PLUS_GAME_PULL_BET_TASK_03);
    }

    /**
     *  PP电子
     */
    @XxlJob(value = "ppGamePullTask")
    public void ppPullBetTask03() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.PP_GAME_PULL_BET_TASK);
    }

    /**
     *  fastSpin 电子
     */
    @XxlJob(value = "fastSpinGamePullTask")
    public void fastSpinGamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.FAST_SPIN_GAME_PULL_BET_TASK);
    }


    /**
     * nextSpin
     */
    @XxlJob(value = "nextSpinGamePullTask")
    public void nextSpinGamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.NEXTSPIN_GAME_PULL_BET_TASK);
    }

    @XxlJob(value = "jdbGamePullTask")
    public void jdbGamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.JDB_GAME_PULL_BET_TASK);
    }

    @XxlJob(value = "cmdGamePullTask")
    public void cmdGamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.CMD_GAME_PULL_BET_TASK);
    }

    @XxlJob(value = "sexyPullBetTask")
    public void sexyGamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.SEXY_GAME_PULL_BET_TASK);
    }


    @XxlJob(value = "dg2PullBetTask")
    public void dg2GamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.DG2_GAME_PULL_BET_TASK);
    }


    @XxlJob(value = ThirdGamePullBetTaskTypeConstant.FC_GAME_PULL_BET_TASK)
    public void fcPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.FC_GAME_PULL_BET_TASK);
    }

    @XxlJob(value = ThirdGamePullBetTaskTypeConstant.ACE_GAME_PULL_BET_TASK)
    public void acePullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.ACE_GAME_PULL_BET_TASK);
    }

    @XxlJob(value = "spadeGamePullTask")
    public void spadePullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.SPADE_GAME_PULL_BET_TASK);
    }


    @XxlJob(value = "dbEvgGamePullTask")
    public void dbEvgGamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.DB_EVG_GAME_PULL_BET_TASK);
    }

    @XxlJob(value = "dbFishingGamePullTask")
    public void dbFishingGamePullTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.DB_FISHING_GAME_PULL_BET_TASK);
    }

    /**
     * DB电竞
     */
    @XxlJob(value = "dbDjPullBetTask")
    public void dbDjPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.DB_DJ_GAME_PULL_BET_TASK);
    }


    /**
     * DB熊猫体育
     */
    @XxlJob(value = "dbPanDaSportPullBetTask")
    public void dbPanDaSportPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.DB_PANDA_SPORT_GAME_PULL_BET_TASK);
    }


    @XxlJob(value = "dbChessPullBetTask")
    public void dbChessPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.DB_CHESS_GAME_PULL_BET_TASK);
    }

    @XxlJob(value = "dbAceltPullBetTask")
    public void dbAceltPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.DB_ACELT_GAME_PULL_BET_TASK);
    }


    @XxlJob(value = "dbShGamePullBetTask")
    public void dbShGamePullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.DB_SH_GAME_PULL_BET_TASK);
    }

    @XxlJob(value = "dbScratchLotteryPullBetTask")
    public void dbScratchLotteryPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.DB_SCRATCH_LOTTERY_GAME_PULL_BET_TASK);
    }

    @XxlJob(value = "wintoEvgPullBetTask")
    public void wintoEvgPullBetTask() {
        gamePullTask(ThirdGamePullBetTaskTypeConstant.WINTO_EVG_GAME_PULL_BET_TASK);
    }



}
