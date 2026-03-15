package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoEditApi;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentDetailIBasicVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoEditVO;
import com.cloud.baowang.agent.api.vo.remark.AgentRemarkRecordVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.agent.api.vo.agent.AgentTeamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代理详情-基本信息
 */
@Tag(name = "代理详情-基本信息")
@AllArgsConstructor
@RestController
@RequestMapping("/agentDetail")
public class AgentInfoDetailController {


    private AgentInfoEditApi agentInfoEditApi;

    @Operation(summary = "发起代理信息变更")
    @PostMapping("/initiateAgentInfoChange")
    public ResponseVO<Void> initiateAgentInfoChange(@Valid @RequestBody AgentInfoEditVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return agentInfoEditApi.initiateAgentInfoChange(vo);
    }

    @Operation(summary = "基本信息查询")
    @PostMapping(value = "/getBasicAgentInfo")
    public ResponseVO<AgentDetailIBasicVO> getBasicAgentInfo(@RequestBody AgentDetailParam param) {
        param.setSiteCode(CurrReqUtils.getSiteCode());
        param.setDataDesensitization(CurrReqUtils.getDataDesensity());
        param.setTimeZone(CurrReqUtils.getTimezone());
        return agentInfoEditApi.getBasicAgentInfo(param);
    }

    @Operation(summary = "备注信息查询")
    @PostMapping(value = "/getAgentRemark")
    public ResponseVO<Page<AgentRemarkRecordVO>> getAgentRemark(@RequestBody AgentDetailParam param) {
        param.setSiteCode(CurrReqUtils.getSiteCode());
        return agentInfoEditApi.getAgentRemark(param);
    }

    @Operation(summary = "团队信息查询")
    @PostMapping(value = "/getAgentTeam")
    public ResponseVO<AgentTeamVO> getAgentTeam(@RequestBody AgentDetailParam param) {
        param.setSiteCode(CurrReqUtils.getSiteCode());
        param.setTimeZone(CurrReqUtils.getTimezone());
        return agentInfoEditApi.getAgentTeam(param);
    }
}
