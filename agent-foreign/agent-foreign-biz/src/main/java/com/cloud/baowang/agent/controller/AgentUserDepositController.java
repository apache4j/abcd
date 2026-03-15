package com.cloud.baowang.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.wallet.api.vo.agent.DepositRecordResponseVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserDepositRecordApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserDepositRecordParam;
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

/**
 * 代理客户端-存款记录
 */
@Tag(name = "代理客户端-下级信息-存款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-deposit/api")
public class AgentUserDepositController {

    private final UserDepositRecordApi userDepositRecordApi;

    private final AgentInfoApi agentInfoApi;

    @Operation(summary = "存款记录")
    @PostMapping("/depositRecord")
    public ResponseVO<Page<DepositRecordResponseVO>> depositRecord(@Valid @RequestBody UserDepositRecordParam vo){
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentIds(agentInfoApi.getSubAgentIdList(CurrReqUtils.getOneId()));
        return userDepositRecordApi.depositRecord(vo);
    }

    @Operation(summary = "存款记录-下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        return userDepositRecordApi.getDownBox();
    }
}
