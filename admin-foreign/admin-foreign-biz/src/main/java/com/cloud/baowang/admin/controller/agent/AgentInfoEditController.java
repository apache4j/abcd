/*
package com.cloud.baowang.admin.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoEditApi;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentDetailIBasicVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoEditVO;
import com.cloud.baowang.agent.api.vo.remark.AgentRemarkRecordVO;
import com.cloud.baowang.admin.utils.CommonAdminUtils;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.agent.api.vo.agent.AgentTeamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

*/
/**
 * 代理详情-基本信息
 *//*

@Tag(name = "代理详情-基本信息")
@AllArgsConstructor
@RestController
@RequestMapping("/agentDetail")
public class AgentInfoEditController {
    
    
    private AgentInfoEditApi agentInfoEditApi;

    @Operation(summary = "代理信息编辑")
    @PostMapping("/edit/{type}")
    public ResponseVO<Void> edit(@PathVariable String type, @Valid @RequestBody AgentInfoEditVO vo) {
        vo.setOperator(CurrentRequestUtils.getCurrentUserAccount());
        return agentInfoEditApi.agentInfoEdit(vo);
    }

    @Operation(summary = "基本信息查询")
    @PostMapping(value = "/getBasicAgentInfo")
    public ResponseVO<AgentDetailIBasicVO> getBasicAgentInfo(@RequestBody AgentDetailParam param){
        param.setDataDesensitization(CurrentRequestUtils.getDataDesensity());
        return agentInfoEditApi.getBasicAgentInfo(param);
    }

    @Operation(summary = "备注信息查询")
    @PostMapping(value = "/getAgentRemark")
    public ResponseVO<Page<AgentRemarkRecordVO>> getAgentRemark(@RequestBody AgentDetailParam param){
        return agentInfoEditApi.getAgentRemark(param);
    }

    @Operation(summary = "团队信息查询")
    @PostMapping(value = "/getAgentTeam")
    public ResponseVO<AgentTeamVO> getAgentTeam(@RequestBody AgentDetailParam param){
        return agentInfoEditApi.getAgentTeam(param);
    }
}
*/
