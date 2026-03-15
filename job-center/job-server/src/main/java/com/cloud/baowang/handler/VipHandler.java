package com.cloud.baowang.handler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.play.api.api.rebate.UserAwardApi;
import com.cloud.baowang.play.api.vo.vip.VIPSendReqVO;
import com.cloud.baowang.report.api.api.ReportVIPDataApi;
import com.cloud.baowang.report.api.vo.vip.VIPDataVO;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipAwardV2Api;
import com.cloud.baowang.user.api.vo.vip.VIPSendRewardReqVO;
import com.cloud.baowang.wallet.api.api.VIPAwardRecordApi;
import com.cloud.baowang.wallet.api.api.vipV2.VIPAwardRecordV2Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

/**
 * @Author : 小智
 * @Date : 17/5/24 3:02 PM
 * @Version : 1.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class VipHandler {

    private final UserAwardApi userAwardApi;

    private final ReportVIPDataApi vipDataApi;

    private final VIPAwardRecordApi vipAwardRecordApi;

    private final VIPAwardRecordV2Api vipAwardRecordV2Api;
    private final VipAwardV2Api vipAwardV2Api;

    private final SiteVipOptionApi siteVipOptionApi;


    /**
     * 刷新VIP等级配置 每天凌晨0点10秒执行
     */
//    @XxlJob(value = "refreshVIPRankConfig")
//    public void refreshVIPRankConfig(){
//        try {
//            log.info("***************** 刷新VIP等级配置 每天0点10秒执行-XxlJob-start *****************");
//            vipRankApi.refreshVIPRankTask();
//            log.info("***************** 刷新VIP等级配置 每天0点10秒执行-XxlJob-end *****************");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * 刷新VIP权益配置 每天凌晨0点20秒执行
     */
//    @XxlJob(value = "refreshVIPBenefitConfig")
//    public void refreshVIPBenefitConfig(){
//        log.info("***************** 刷新VIP权益配置 每天0点20秒执行-XxlJob-start *****************");
//        vipBenefitApi.refreshVIPBenefitTask();
//        log.info("***************** 刷新VIP权益配置 每天0点20秒执行-XxlJob-end *****************");
//    }

    /**
     * VIP升级 每天下午4点05分执行
     */
//    @XxlJob(value = "userVIPUpgrade")
//    public void userVIPUpgrade(){
//        log.info("***************** VIP升级 每天16点05秒执行-XxlJob-start *****************");
//        vipRankApi.userVIPUpgrade();
//        log.info("***************** VIP升级 每天16点05秒执行-XxlJob-end *****************");
//    }

    /**
     * VIP周返水 动态执行
     * 0 0 5-17 ? * FRI-SAT
     */
    @XxlJob(value = "weekRebate")
    public void weekRebate() {
        log.info("***************** VIP周流水 动态时间执行-XxlJob-start *****************");
        String jobParam = XxlJobHelper.getJobParam();
        VIPSendReqVO vo = new VIPSendReqVO();
        if (Strings.isNotBlank(jobParam)) {
            // json 格式校验
            try {
                vo = JSONObject.parseObject(jobParam, VIPSendReqVO.class);
            } catch (Exception e) {
                log.error("VIP周流水注单拉取参数格式错误{}", jobParam);
                XxlJobHelper.log("VIP周流水注单拉取参数格式错误");
                throw e;
            }
        }
        userAwardApi.weekRebate(vo);
        log.info("***************** VIP周流水 动态时间执行-XxlJob-end *****************");
    }

    /**
     * VIP月返水 动态执行
     */
    @XxlJob(value = "monthRebate")
    public void monthRebate() {
        log.info("***************** VIP月流水 动态时间执行-XxlJob-start *****************");
        String jobParam = XxlJobHelper.getJobParam();
        VIPSendReqVO vo = new VIPSendReqVO();
        if (Strings.isNotBlank(jobParam)) {
            // json 格式校验
            try {
                vo = JSONObject.parseObject(jobParam, VIPSendReqVO.class);
            } catch (Exception e) {
                log.error("VIP月流水注单拉取参数格式错误{}", jobParam);
                XxlJobHelper.log("VIP月流水注单拉取参数格式错误");
                throw e;
            }
        }
        userAwardApi.monthRebate(vo);
        log.info("***************** VIP月流水 动态时间执行-XxlJob-end *****************");
    }

    /**
     * VIP周体育流水礼金 每个时区周五12点发放
     */
    @XxlJob(value = "weekSportRebate")
    public void weekSportRebate() {
        log.info("***************** VIP周体育礼金 执行-XxlJob-start *****************");
        String jobParam = XxlJobHelper.getJobParam();
        VIPSendReqVO vo = new VIPSendReqVO();
        if (Strings.isNotBlank(jobParam)) {
            // json 格式校验
            try {
                vo = JSONObject.parseObject(jobParam, VIPSendReqVO.class);
            } catch (Exception e) {
                log.error("VIP周体育礼金注单拉取参数格式错误{}", jobParam);
                XxlJobHelper.log("VIP周体育礼金注单拉取参数格式错误");
                throw e;
            }
        }
        userAwardApi.weekSportRebate(vo);
        log.info("***************** VIP周体育礼金 执行-XxlJob-end *****************");
    }

    /**
     * VIP数据报表 每天每隔一小时测各个时区前一天数据
     */
    @XxlJob(value = "vipDataReport")
    public void vipDataReport() {
        log.info("***************** VIP数据报表收集 执行-XxlJob-start *****************");
        String jobParam = XxlJobHelper.getJobParam();
        VIPDataVO vo = new VIPDataVO();
        if (Strings.isNotBlank(jobParam)) {
            // json 格式校验
            try {
                vo = JSONObject.parseObject(jobParam, VIPDataVO.class);
            } catch (Exception e) {
                log.error("VIP数据报表拉取参数格式错误{}", jobParam);
                XxlJobHelper.log("VIP数据报表参数格式错误");
                throw e;
            }
        }
        vipDataApi.collectVIPDataReport(vo);
        log.info("***************** VIP数据报表收集 执行-XxlJob-end *****************");
    }

    /**
     * VIP相关礼金过期 处理
     */
    @XxlJob(value = "vipExpired")
    public void vipExpired() {
        log.info("***************** VIP活动过期处理 执行-XxlJob-start *****************");
        vipAwardRecordApi.vipExpired();
        log.info("***************** VIP活动过期处理 执行-XxlJob-end *****************");
    }

    /**
     * VIP相关礼金过期 处理
     */
    @XxlJob(value = "vipExpiredV2")
    public void vipExpiredV2() {
        log.info("***************** VIP活动过期处理 执行-XxlJob-start *****************");
        vipAwardRecordV2Api.vipExpired();
        log.info("***************** VIP活动过期处理 执行-XxlJob-end *****************");
    }

    /**
     * VIP 周礼金处理
     */
    @XxlJob(value = "vipWeekAward")
    public void weekAward() {

        log.info("***************** VIP 月礼金处理 执行-XxlJob-start *****************");

        String jobParam = XxlJobHelper.getJobParam();
        VIPSendRewardReqVO vo = new VIPSendRewardReqVO();
        if (Strings.isNotBlank(jobParam)) {
            // json 格式校验
            try {
                vo = JSONObject.parseObject(jobParam, VIPSendRewardReqVO.class);
            } catch (Exception e) {
                log.error("VIP 月礼金处理参数格式错误{}", jobParam);
                XxlJobHelper.log("VIP周体育礼金注单拉取参数格式错误");
                throw e;
            }
        }
        vipAwardV2Api.weekAward(vo);
        log.info("***************** VIP 月礼金处理 执行-XxlJob-end *****************");

    }

    /**
     * VIP 生日礼金处理
     */
    @XxlJob(value = "vipBirthDayAward")
    public void birthDayAward() {

        log.info("***************** VIP生日礼金处理 执行-XxlJob-start *****************");
        String jobParam = XxlJobHelper.getJobParam();
        VIPSendRewardReqVO vo = new VIPSendRewardReqVO();
        if (Strings.isNotBlank(jobParam)) {
            // json 格式校验
            try {
                vo = JSONObject.parseObject(jobParam, VIPSendRewardReqVO.class);
            } catch (Exception e) {
                log.error("VIP生日礼金处理参数格式错误{}", jobParam);
                XxlJobHelper.log("VIP周体育礼金注单拉取参数格式错误");
                throw e;
            }
        }
        vipAwardV2Api.birthDayAward(vo);
        log.info("***************** VIP生日礼金处理 执行-XxlJob-end *****************");

    }

    @XxlJob(value = "cnVipUpDownAllSiteCode")
    public void cnVipUpDownAllSiteCode() {
        log.info("***************** 大陆盘口所有处理vip自动升降 job 执行-XxlJob-start *****************");
        String jobParam = XxlJobHelper.getJobParam();
        VIPDataVO vo = new VIPDataVO();
        if (Strings.isNotBlank(jobParam)) {
            log.info("大陆盘口所有处理vip自动{}", jobParam);
            // json 格式校验
        }
        siteVipOptionApi.cnVipUpDownAllSiteCode(jobParam);
        log.info("***************** 大陆盘口所有处理vip自动升降 执行-XxlJob-end *****************");

    }

}
