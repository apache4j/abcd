package com.cloud.baowang.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentTransferRecordApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordResponseVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 代理客户端-下级信息转账记录
 */
@Tag(name = "代理客户端-下级信息-转账记录")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-transfer-record/api")
public class AgentTransferRecordController {

    private final AgentTransferRecordApi agentTransferRecordApi;

    private final SystemParamApi systemParamApi;

    @Operation(summary = "下级转账记录列表")
    @PostMapping("/transferRecord")
    public ResponseVO<Page<AgentTransferRecordResponseVO>> transferRecord(@Valid @RequestBody AgentTransferRecordRequestVO vo){
        return agentTransferRecordApi.transferRecord(vo);
    }
    @Operation(summary = "转账记录-下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.OWNER_USER_TYPE);
        param.add(CommonConstant.DIRECTION);
        param.add(CommonConstant.TRANSFER_WALLET_TYPE);
        return systemParamApi.getSystemParamsByList(param);
    }
}
