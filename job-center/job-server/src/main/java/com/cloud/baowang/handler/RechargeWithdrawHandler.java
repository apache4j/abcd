package com.cloud.baowang.handler;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.report.api.api.ReportUserDepositWithdrawApi;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.vo.SiteReportSyncDataVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportRealTimeUserDepositWithdrawReqParam;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawDayReqParam;
import com.cloud.baowang.wallet.api.api.UserRechargeWithdrawOrderStatusHandleApi;
import com.cloud.baowang.wallet.api.vo.recharge.VirtualCurrencyRechargeOmissionsReqVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@AllArgsConstructor
public class RechargeWithdrawHandler {


    private final UserRechargeWithdrawOrderStatusHandleApi rechargeWithdrawOrderStatusHandleApi;

    private final ReportUserDepositWithdrawApi reportUserDepositWithdrawApi;

    private final ReportUserRechargeApi reportUserRechargeApi;


    /**
     * 会员充值订单状态处理 每2分钟一次
     */
    @XxlJob(value = "rechargeOrderHandle")
    public void rechargeOrderHandle() {
        log.info("***************** 处理会员充值订单状态 redisson-XxlJob-start *****************");
        rechargeWithdrawOrderStatusHandleApi.rechargeOrderHandle();
        log.info("***************** 处理会员充值订单状态 redisson-XxlJob-end *****************");
    }

    /**
     * 会员提款订单状态处理 每2分钟一次
     */
    @XxlJob(value = "withdrawOrderHandle")
    public void withdrawOrderHandle() {
        log.info("***************** 处理会员提款订单状态 redisson-XxlJob-start *****************");
        rechargeWithdrawOrderStatusHandleApi.withdrawOrderHandle();
        log.info("***************** 处理会员提款订单状态 redisson-XxlJob-end *****************");
    }


    /**
     * 会员虚拟币订单拉取前半小时订单，查看是否有遗漏 每20分钟一次
     */
    @XxlJob(value = "virtualCurrencyRechargeOmissionsHandle")
    public void virtualCurrencyRechargeOmissionsHandle() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("***************** 处理会员虚拟币订单拉取前半小时订单，查看是否有遗漏 redisson-XxlJob-start 参数{}*****************",jobParam);
        VirtualCurrencyRechargeOmissionsReqVO vo = new VirtualCurrencyRechargeOmissionsReqVO();
        if (StringUtils.isNotBlank(jobParam)) {
            vo = JSON.parseObject(jobParam, VirtualCurrencyRechargeOmissionsReqVO.class);
        }
        rechargeWithdrawOrderStatusHandleApi.virtualCurrencyRechargeOmissionsHandle(vo);
        log.info("***************** 处理会员虚拟币订单拉取前半小时订单，查看是否有遗漏 redisson-XxlJob-end *****************");
    }


    /**
     * 会员存取款日报表
     */
    @XxlJob(value = "reportUserDepositWithdrawDay")
    public void reportUserDepositWithdrawDay() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("***************** 处理会员存取报表 redisson-XxlJob-start 参数{}*****************",jobParam);
        ReportUserDepositWithdrawDayReqParam param = new ReportUserDepositWithdrawDayReqParam();
        if (StringUtils.isNotBlank(jobParam)) {
            param = JSON.parseObject(jobParam, ReportUserDepositWithdrawDayReqParam.class);
        }
        reportUserDepositWithdrawApi.reportUserDepositWithdrawDay(param);
        log.info("***************** 处理会员存取报表 redisson-XxlJob-end *****************");
    }

    /**
     * 会员存取实时报表重算
     */
    @XxlJob(value = "reportRealTimeUserRechargeWithdraw")
    public void reportRealTimeUserRechargeWithdraw() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("***************** 处理会员实时存取报表 重跑 redisson-XxlJob-start 参数{}*****************",jobParam);
        ReportRealTimeUserDepositWithdrawReqParam param = new ReportRealTimeUserDepositWithdrawReqParam();
        if (StringUtils.isBlank(jobParam)) {
            return;
        }else{
            param = JSON.parseObject(jobParam, ReportRealTimeUserDepositWithdrawReqParam.class);
        }
        if(null == param.getStartTime() || null == param.getEndTime()){
            return;
        }
        reportUserRechargeApi.reportRealTimeUserRechargeWithdraw(param);
        log.info("***************** 处理会员实时存取报表 重跑 redisson-XxlJob-end *****************");
    }

}
