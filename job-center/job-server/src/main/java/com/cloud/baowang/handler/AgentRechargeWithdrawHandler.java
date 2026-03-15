package com.cloud.baowang.handler;


import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.agent.api.api.AgentRechargeWithdrawOrderStatusHandleApi;
import com.cloud.baowang.agent.api.vo.recharge.AgentVirtualCurrencyRechargeOmissionsReqVO;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AgentRechargeWithdrawHandler {


    private final AgentRechargeWithdrawOrderStatusHandleApi rechargeWithdrawOrderStatusHandleApi;



    /**
     * 代理充值订单状态处理 每2分钟一次
     */
    @XxlJob(value = "agentRechargeOrderHandle")
    public void agentRechargeOrderHandle() {
        log.info("***************** 处理代理充值订单状态 redisson-XxlJob-start *****************");
        rechargeWithdrawOrderStatusHandleApi.rechargeOrderHandle();
        log.info("***************** 处理代理充值订单状态 redisson-XxlJob-end *****************");
    }

    /**
     * 代理提款订单状态处理 每2分钟一次
     */
    @XxlJob(value = "agentWithdrawOrderHandle")
    public void agentWithdrawOrderHandle() {
        log.info("***************** 处理代理提款订单状态 redisson-XxlJob-start *****************");
        rechargeWithdrawOrderStatusHandleApi.withdrawOrderHandle();
        log.info("***************** 处理代理提款订单状态 redisson-XxlJob-end *****************");
    }


    /**
     * 代理虚拟币订单拉取前半小时订单，查看是否有遗漏 每20分钟一次
     */
    @XxlJob(value = "agentVirtualCurrencyRechargeOmissionsHandle")
    public void agentVirtualCurrencyRechargeOmissionsHandle() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("***************** 处理代理虚拟币订单拉取前半小时订单，查看是否有遗漏 redisson-XxlJob-start 参数{}*****************",jobParam);
        AgentVirtualCurrencyRechargeOmissionsReqVO vo = new AgentVirtualCurrencyRechargeOmissionsReqVO();
        if (StringUtils.isNotBlank(jobParam)) {
            vo = JSON.parseObject(jobParam, AgentVirtualCurrencyRechargeOmissionsReqVO.class);
        }
        rechargeWithdrawOrderStatusHandleApi.virtualCurrencyRechargeOmissionsHandle(vo);
        log.info("***************** 处理代理虚拟币订单拉取前半小时订单，查看是否有遗漏 redisson-XxlJob-end *****************");
    }


    /**
     * 代理存取款日报表
     */
    /*@XxlJob(value = "reportAgentDepositWithdrawDay")
    public void reportAgentDepositWithdrawDay() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("***************** 处理代理存取报表 redisson-XxlJob-start 参数{}*****************",jobParam);
        ReportAgentDepositWithdrawDayReqParam param = new ReportAgentDepositWithdrawDayReqParam();
        if (StringUtils.isNotBlank(jobParam)) {
            param = JSON.parseObject(jobParam, ReportAgentDepositWithdrawDayReqParam.class);
        }
        reportAgentDepositWithdrawApi.reportAgentDepositWithdrawDay(param);
        log.info("***************** 处理代理存取报表 redisson-XxlJob-end *****************");
    }*/
}
