package com.cloud.baowang.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.vo.agent.AgentUserOrderRecordPageVO;
import com.cloud.baowang.play.api.vo.agent.AgentUserOrderRecordReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "代理-下级信息-游戏记录")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/order-record/api")
public class AgentUserOrderController {
    private final OrderRecordApi orderRecordApi;
    private final AgentInfoApi agentInfoApi;

    @Operation(summary = ("游戏记录"))
    @PostMapping("/pageList")
    public ResponseVO<Page<AgentUserOrderRecordPageVO>> pageList(@RequestBody AgentUserOrderRecordReqVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        String agentAccount = CurrReqUtils.getAccount();
        List<String> allAgentAccountList = agentInfoApi.getALLAgentAccountList(siteCode, agentAccount);
        vo.setAgentAccounts(allAgentAccountList);
        vo.setSiteCode(siteCode);
        return orderRecordApi.getAgentUserPageList(vo);
    }
}
