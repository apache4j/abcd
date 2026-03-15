package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCoinRecordApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRespVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordVO;
import com.cloud.baowang.agent.service.AgentCoinRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentCoinRecordApiImpl implements AgentCoinRecordApi {

    private final AgentCoinRecordService agentCoinRecordService;

    @Override
    public ResponseVO<AgentCoinRecordRespVO> listAgentCoinRecordPage(AgentCoinRecordRequestVO vo) {
        AgentCoinRecordRespVO agentCoinRecordRespVO=new AgentCoinRecordRespVO();
        Page<AgentCoinRecordVO> agentCoinRecordVOPage=agentCoinRecordService.listAgentCoinRecordPage(vo);
        agentCoinRecordRespVO.setAgentCoinRecordVOPage(agentCoinRecordVOPage);
        //计算当前页汇总
        AgentCoinRecordVO agentCoinRecordVO=new AgentCoinRecordVO();
        agentCoinRecordVO.setOrderNo("小计");
        for(AgentCoinRecordVO agentCoinRecordVOSingle:agentCoinRecordVOPage.getRecords()){
            agentCoinRecordVO.addCoinFrom(agentCoinRecordVOSingle.getCoinFrom());
            agentCoinRecordVO.addCoinTo(agentCoinRecordVOSingle.getCoinTo());
            agentCoinRecordVO.addCoinAmount(agentCoinRecordVOSingle.getCoinAmount());
        }
        agentCoinRecordRespVO.setCurrentData(agentCoinRecordVO);
        AgentCoinRecordVO sumAllData=new AgentCoinRecordVO();
        AgentCoinRecordVO sumAllDataDb=agentCoinRecordService.sumAllAgentCoinRecord(vo);
        if(sumAllDataDb!=null){
            BeanUtils.copyProperties(sumAllDataDb,sumAllData);
        }
        sumAllData.setOrderNo("总计");
        agentCoinRecordRespVO.setSumAllData(sumAllData);
        return ResponseVO.success(agentCoinRecordRespVO);
    }

    @Override
    public Long agentCoinRecordPageListCount(AgentCoinRecordRequestVO vo) {
        return agentCoinRecordService.agentCoinRecordPageListCount(vo);
    }
}
