/*
package com.cloud.baowang.admin.controller.agent;

import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.enums.AgentAdjustTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpSubmitVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentBalanceQueryVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentBalanceVO;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.common.core.vo.SystemParamVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

*/
/**
 * @author: kimi
 *//*

@Tag(name = "代理人工添加额度")
@RestController
@AllArgsConstructor
@RequestMapping("/agent-manual-up/api")
public class AgentManualUpController {



    private final AgentManualUpApi agentManualUpApi;

    @Operation(summary ="提交")
    @PostMapping(value = "/agentSubmit")
    public ResponseVO<?> agentSubmit(@Valid @RequestBody AgentManualUpSubmitVO vo) {
        return agentManualUpApi.agentSubmit(vo, CurrentRequestUtils.getCurrentOneId(), CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary ="下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 钱包类型
        List<SystemParamVO> walletType = AgentCoinRecordTypeEnum.AgentWalletTypeEnum.getList().stream().map(item ->
                SystemParamVO.builder().code(item.getCode()).value(item.getName()).build()).toList();
        // 调整类型
        List<SystemParamVO> adjustType = AgentAdjustTypeEnum.getList().stream().map(item ->
                SystemParamVO.builder().code(item.getCode()).value(item.getName()).build()).toList();

        Map<String, Object> result = Maps.newHashMap();
        result.put("walletType", walletType);
        result.put("adjustType", adjustType);
        return ResponseVO.success(result);
    }

    @Operation(summary ="查询")
    @PostMapping(value = "/getAgentBalance")
    public ResponseVO<GetAgentBalanceVO> getAgentBalance(@Valid @RequestBody GetAgentBalanceQueryVO vo) {
        return agentManualUpApi.getAgentBalance(vo);
    }
}
*/
