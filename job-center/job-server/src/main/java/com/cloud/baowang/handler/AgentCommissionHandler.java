package com.cloud.baowang.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.agent.api.api.AgentCommissionCalcApi;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionCalcVO;
import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: fangfei
 * @createTime: 2024/11/20 22:29
 * @description: 佣金结算
 */
@Slf4j
@Component
@AllArgsConstructor
public class AgentCommissionHandler {

    private final AgentCommissionCalcApi agentCommissionCalcApi;

    @XxlJob(value = "agentFinalCommission")
    public void agentFinalCommissionGenerate() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("代理佣金结算job开始, 参数：{}", jobParam);
        if (StrUtil.isNotBlank(jobParam)) {
            AgentCommissionCalcVO commissionCalcVO = JSON.parseObject(jobParam, AgentCommissionCalcVO.class);
            agentCommissionCalcApi.agentFinalCommissionGenerate(commissionCalcVO);
        }
        log.info("代理佣金结算job结束, 参数：{}", jobParam);
    }

    @XxlJob(value = "agentExpectCommission")
    public void agentExpectCommissionGenerate() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("代理预期佣金结算job开始, 参数：{}", jobParam);
        AgentCommissionCalcVO commissionCalcVO = new AgentCommissionCalcVO();
        if (StrUtil.isNotBlank(jobParam)) {
            commissionCalcVO = JSON.parseObject(jobParam, AgentCommissionCalcVO.class);
        }

        agentCommissionCalcApi.agentExpectCommissionGenerate(commissionCalcVO);
        log.info("代理预期佣金结算job结束, 参数：{}", jobParam);
    }

    @XxlJob(value = "agentFinalRebate")
    public void agentRebateGenerate() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("代理返点结算job开始, 参数：{}", jobParam);
        if (StrUtil.isNotBlank(jobParam)) {
            AgentCommissionCalcVO commissionCalcVO = JSON.parseObject(jobParam, AgentCommissionCalcVO.class);
            agentCommissionCalcApi.agentRebateGenerate(commissionCalcVO);
        }
        log.info("代理返点结算job结束, 参数：{}", jobParam);
    }

    @XxlJob(value = "agentExpectRebate")
    public void agentRebateExpectGenerate() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("代理预期返点结算job开始, 参数：{}", jobParam);
        AgentCommissionCalcVO commissionCalcVO = new AgentCommissionCalcVO();
        if (StrUtil.isNotBlank(jobParam)) {
            commissionCalcVO = JSON.parseObject(jobParam, AgentCommissionCalcVO.class);
        }
        agentCommissionCalcApi.agentRebateExpectGenerate(commissionCalcVO);
        log.info("代理预期返点结算job结束, 参数：{}", jobParam);
    }
}
