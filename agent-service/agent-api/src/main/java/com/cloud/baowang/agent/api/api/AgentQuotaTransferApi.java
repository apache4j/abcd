/**
 * @(#)AgentLabeService.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferRecordReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferRecordRespVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentQuotaTransferVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelAddVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelDeleteVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelEditVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelListPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelListVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelRecordListVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelReordListPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelReordListUserPageVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelUserVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.List;

@FeignClient(contextId = "remoteAgentQuotaTransferApi", value = ApiConstants.NAME)
@Tag(name = "代理-额度转账")
public interface AgentQuotaTransferApi {

    String PREFIX = ApiConstants.PREFIX + "/agent-quota-transfer/api/";

    @Operation(summary = "转账记录")
    @PostMapping("record")
    ResponseVO<Page<AgentQuotaTransferRecordRespVO>> record(@RequestBody AgentQuotaTransferRecordReqVO reqVO);

    @Operation(summary = "额度转账-转账")
    @PostMapping("transfer")
    ResponseVO<Boolean> transfer(@RequestBody AgentQuotaTransferVO agentQuotaTransferVO);

    @Operation(summary = "额度转账-可用余额")
    @PostMapping("balance")
    ResponseVO<AgentQuotaTransferBalanceVO> balance(@RequestParam("agentAccount")String agentAccount,
                                                    @RequestParam("siteCode")String siteCode);
}
