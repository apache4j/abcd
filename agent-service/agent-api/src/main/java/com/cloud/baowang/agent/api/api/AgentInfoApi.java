package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.AgentUserLanguageVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AddAgentNewVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoCondVo;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoPartVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.site.AgentDataOverviewResVo;
import com.cloud.baowang.agent.api.vo.AgentSystemMessageConfigVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteAgentInfoApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 代理信息查询 AgentAddApi")
public interface AgentInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/agentInfo/api";

    /*@Operation(description = "根据代理账号 查询代理信息")
    @PostMapping(value = PREFIX + "/getByAgentAccount")
    AgentInfoVO getByAgentAccount(@RequestParam("agentAccount") String agentAccount);*/

    @Operation(description = "根据代理账号 查询代理信息")
    @PostMapping(value = PREFIX + "/getByAgentAccountSite")
    AgentInfoVO getByAgentAccountSite(@RequestParam("siteCode") String siteCode, @RequestParam("agentAccount") String agentAccount);


    @Operation(description = "根据代理账号,siteCode 查询代理信息")
    @GetMapping(value = PREFIX + "/getByAgentAccount")
    AgentInfoVO getByAgentAccountAndSiteCode(@RequestParam("agentAccount") String agentAccount,
                                             @RequestParam("siteCode") String siteCode);


    @Operation(description = "根据代理账号 查询代理信息")
    @PostMapping(value = PREFIX + "/getByCurrAgentAccount")
    AgentInfoVO getByCurrAgentAccount(@RequestParam("agentAccount") String agentAccount);

    @Operation(description = "根据代理账号 查询代理信息")
    @PostMapping(value = PREFIX + "/getListByCurrAgentAccount")
    List<String> getListByCurrAgentAccount(@RequestParam("siteCode") String siteCode, @RequestParam("agentAccount") String agentAccount);

    @Operation(description = "根据代理账号集合 查询代理信息")
    @PostMapping(value = PREFIX + "/getByAgentAccounts")
    List<AgentInfoVO> getByAgentAccounts(@RequestBody List<String> agentAccounts);

    @Operation(description = "根据代理账号集合 批量查询代理信息")
    @PostMapping(value = PREFIX + "/getByAgentAccountsAndSiteCode")
    List<AgentInfoVO> getByAgentAccountsAndSiteCode(@RequestParam("siteCode") String siteCode, @RequestBody List<String> agentAccounts);

    @Operation(description = "根据代理id 查询代理信息")
    @PostMapping(value = PREFIX + "/getByAgentId")
    AgentInfoVO getByAgentId(@RequestParam("agentId") String agentId);

    @Operation(description = "根据代理账号查询所有下级代理信息")
    @PostMapping(value = PREFIX + "/getALLAgentAccountList")
    List<String> getALLAgentAccountList(@RequestParam("siteCode") String siteCode, @RequestParam("agentAccount") String agentAccount);

    @Operation(description = "更新代理")
    @PostMapping(value = PREFIX + "/updateAgentByAccount")
    void updateAgentByAccount(@RequestBody AgentInfoVO agentInfoVO);

    @Operation(description = "更新代理")
    @PostMapping(value = PREFIX + "/updateAgentInfoById")
    ResponseVO<Boolean> updateAgentInfoById(@RequestBody AgentInfoModifyVO editVO);

    @Operation(description = "新增代理")
    @PostMapping(value = PREFIX + "/addAgent")
    ResponseVO addAgent(
            @RequestBody AddAgentNewVO vo,
            @RequestParam("registerIp") String registerIp,
            @RequestParam("registerDeviceType") Integer registerDeviceType,
            @RequestParam("id") String id);

    @PostMapping(value = PREFIX + "/getAgentByInviteCode")
    AgentInfoVO getAgentByInviteCode(@RequestParam("inviteCode") String inviteCode , @RequestParam("siteCode")  String siteCode);

    @Operation(description = "根据短id,批量获取代理列表")
    @PostMapping(value = PREFIX + "/getByAgentIds")
    List<AgentInfoVO> getByAgentIds(@RequestBody List<String> superAgentIds);

    @Operation(description = "获取所有的agentId")
    @PostMapping(value = PREFIX + "/getALLAgentIds")
    List<String> getALLAgentIds(@RequestParam("siteCode") String siteCode);

    @Operation(description = "获取所有的agentId")
    @PostMapping(value = PREFIX + "/getAllPartAgentInfoBySiteCode")
    List<AgentInfoPartVO> getAllPartAgentInfoBySiteCode(@RequestParam("siteCode") String siteCode);


    @Operation(description = "根据时间获取站点新增代理数量")
    @PostMapping(value = PREFIX + "/getNewAgents")
    Long getNewAgents(@RequestBody AgentDataOverviewResVo vo);

    @Operation(description = "获取代理下级id集合")
    @PostMapping(value = PREFIX + "/getSubAgentIdList")
    List<String> getSubAgentIdList(@RequestParam("agentId") String agentId);

    @Operation(description = "获取代理直属下级id集合")
    @PostMapping(value = PREFIX + "/getSubOwnerAgentIdList")
    List<String> getSubAgentIdDirectReportList(@RequestParam("agentId") String agentId);

    @Operation(description = "按照条件查询代理列表")
    @PostMapping(value = PREFIX + "/getAgentListByCond")
    List<AgentInfoVO> getAgentListByCond(@RequestBody AgentInfoCondVo agentInfoCondVo);

    @Operation(description = "查询代理会员福利")
    @PostMapping(value = PREFIX + "/getAgentBenefit")
    AgentInfoVO getAgentBenefit(@RequestParam("userId") String userId);

    @Operation(description = "代理系统消息多语言")
    @PostMapping(value = PREFIX + "/getAgentLanguage")
    AgentSystemMessageConfigVO getAgentLanguage(@RequestBody AgentUserLanguageVO vo);

    @Operation(description = "查询代理会员福利")
    @PostMapping(value = PREFIX + "/getAgentBenefitList")
    Map<String,AgentInfoVO> getAgentBenefitList(@RequestParam("userId") List<String> userId);

    @Operation(description = "代理关系刷新")
    @PostMapping(value = PREFIX + "/agentRelationRefresh")
    ResponseVO<Boolean> agentRelationRefresh();
}
