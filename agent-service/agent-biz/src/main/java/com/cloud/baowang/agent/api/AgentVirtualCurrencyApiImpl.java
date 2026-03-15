package com.cloud.baowang.agent.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentVirtualCurrencyApi;
import com.cloud.baowang.agent.api.vo.EnableOrDisableVO;
import com.cloud.baowang.agent.api.vo.UnbindVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyAddVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyPageRequestVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyPageVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyResVO;
import com.cloud.baowang.agent.api.vo.virtualCurrency.AgentVirtualCurrencyResponseVO;
import com.cloud.baowang.agent.service.AgentVirtualCurrencyService;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentVirtualCurrencyApiImpl implements AgentVirtualCurrencyApi {

    private final AgentVirtualCurrencyService agentVirtualCurrencyService;


    @Override
    public ResponseVO<Page<AgentVirtualCurrencyResVO>> listAgentVirtualCurrency(@RequestBody AgentVirtualCurrencyPageVO agentVirtualCurrencyPageVO) {
        return ResponseVO.success(agentVirtualCurrencyService.listAgentVirtualCurrency(agentVirtualCurrencyPageVO));
    }


    @Override
    public ResponseVO<Integer> virtualCurrencyAdd(@RequestBody AgentVirtualCurrencyAddVO agentVirtualCurrencyAddVO) {

        if(!agentVirtualCurrencyAddVO.getVirtualCurrencyAddress().equals(agentVirtualCurrencyAddVO.getConfirmVirtualCurrencyAddress())){
            throw new BaowangDefaultException(ResultCode.CONFIRM_VIRTUAL_CURRENCY_ADDRESS_ERROR);
        }
        /*if (!agentVirtualCurrencyService.checkVirtualCurrencyUnique(agentVirtualCurrencyAddVO.getVirtualCurrencyAddress(), null)) {
            throw new BaowangDefaultException(ResultCode.AGENT_VIRTUAL_CURRENCY_IS_EXIST);
        }*/
        return ResponseVO.success(agentVirtualCurrencyService.virtualCurrencyAdd(agentVirtualCurrencyAddVO));
    }

    @Override
    public ResponseVO<Integer> virtualCurrencyDelete(@RequestBody IdVO idVO) {
        return ResponseVO.success(agentVirtualCurrencyService.virtualCurrencyDelete(idVO));
    }

    @Override
    public ResponseVO<List<AgentVirtualCurrencyResVO>> virtualCurrencyList(@RequestParam String agentAccount){
        return ResponseVO.success(agentVirtualCurrencyService.virtualCurrencyList(agentAccount));
    }


    @Override
    public ResponseVO<Page<AgentVirtualCurrencyResponseVO>> getAgentVirtualCurrencyPage(@RequestBody AgentVirtualCurrencyPageRequestVO vo) {
        return ResponseVO.success(agentVirtualCurrencyService.getAgentVirtualCurrencyPage(vo));
    }
    @Override
    public ResponseVO<?> enableOrDisable(@RequestBody EnableOrDisableVO vo, @RequestParam String adminId, @RequestParam String adminName) {
        return agentVirtualCurrencyService.enableOrDisable(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<?> unbind(@RequestBody UnbindVO vo, @RequestParam String adminId, @RequestParam String adminName) {
        return agentVirtualCurrencyService.unbind(vo, adminId, adminName, true);
    }
}
