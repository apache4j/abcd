package com.cloud.baowang.agent.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.AgentCoinBalanceTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentCustomerCoinTypGroupEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeDetailReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordDetailVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordVO;
import com.cloud.baowang.agent.repositories.AgentCoinRecordRepository;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class AgentCoinChangeService {

    private final AgentCoinRecordRepository agentCoinRecordRepository;

    public ResponseVO<Page<AgentCustomerCoinRecordVO>> listAgentCustomerCoinRecord(AgentCoinChangeReqVO vo){

        if(Objects.isNull(vo.getCoinChangeType())){
            vo.setCoinTypeList(AgentCustomerCoinTypGroupEnum.AgentCoinTypeEnum.getAllCodes());
        }else{
            AgentCustomerCoinTypGroupEnum.AgentCustomCoinTypeEnum agentCustomCoinTypeEnum = AgentCustomerCoinTypGroupEnum.AgentCustomCoinTypeEnum.nameOfCode(vo.getCoinChangeType().toString());
            vo.setCoinTypeList(AgentCustomerCoinTypGroupEnum.AgentCoinTypeEnum.getCodesByAgentCustomCoinTypeEnum(agentCustomCoinTypeEnum));
        }
        Page<AgentCustomerCoinRecordVO> page =  agentCoinRecordRepository.listAgentCustomerCoinRecord(new Page<>(vo.getPageNumber(), vo.getPageSize()),vo);
        page.getRecords().forEach(obj->{
            obj.setAgentCustomerShowType(AgentCustomerCoinTypGroupEnum.AgentCoinTypeEnum.nameOfCode(obj.getCoinType()).getAgentCustomCoinTypeEnum().getCode());

            /*if(AgentCoinBalanceTypeEnum.EXPENSES.getCode().equals(obj.getBalanceType())){
                obj.setCoinAmount(obj.getCoinAmount().negate());
            }*/
        });

        return ResponseVO.success(page);
    }

    public ResponseVO<AgentCustomerCoinRecordDetailVO> getCoinRecordDetail(AgentCoinChangeDetailReqVO vo) {
        AgentCustomerCoinRecordDetailVO obj = agentCoinRecordRepository.getCoinRecordDetail(vo);
        obj.setAgentCustomerShowType(AgentCustomerCoinTypGroupEnum.AgentCoinTypeEnum.nameOfCode(obj.getCoinType()).getAgentCustomCoinTypeEnum().getCode());

        if(AgentCoinBalanceTypeEnum.EXPENSES.getCode().equals(obj.getBalanceType())){
            obj.setCoinAmount(obj.getCoinAmount().negate());
        }
        return ResponseVO.success(obj);
    }

}
