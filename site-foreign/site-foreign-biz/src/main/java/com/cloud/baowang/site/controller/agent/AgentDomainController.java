package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDomainApi;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainResponseVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 域名管理
 */
@Slf4j
@Tag(name = "代理-推广链接")
@RestController
@AllArgsConstructor
@RequestMapping("/agent-domain/api")
public class AgentDomainController {

    private final SystemParamApi systemParamApi;
    private final AgentDomainApi agentDomainApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.SITE_DOMAIN_TYPE);
        param.add(CommonConstant.ENABLE_DISABLE_TYPE);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            Map<String, List<CodeValueVO>> data = resp.getData();
            List<CodeValueVO> domainType = data.get(CommonConstant.SITE_DOMAIN_TYPE);
            String agentDomainCode = String.valueOf(DomainInfoTypeEnum.AGENT_BACKEND.getType());
            String webDomainCode = String.valueOf(DomainInfoTypeEnum.WEB_PORTAL.getType());
            String merchantDomainCode = String.valueOf(DomainInfoTypeEnum.AGENT_MERCHANT.getType());
            List<CodeValueVO> filteredDomainType = domainType.stream()
                    .filter(codeValue ->
                            codeValue.getCode().equals(agentDomainCode) ||
                                    codeValue.getCode().equals(webDomainCode) ||
                                    codeValue.getCode().equals(merchantDomainCode))
                    .toList();
            data.put(CommonConstant.SITE_DOMAIN_TYPE, filteredDomainType);
            resp.setData(data);
        }
        return resp;
    }

    @Operation(summary = "获取域名管理的列表")
    @PostMapping("/getAgentDomainList")
    public ResponseVO<Page<AgentDomainResponseVO>> getAgentDomainList(@RequestBody AgentDomainPageQueryVO pageQueryVO) {
        pageQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentDomainApi.getAgentDomainList(pageQueryVO);
    }

    @PostMapping("/updateAgentDomain")
    @Operation(summary = "修改域名管理")
    public ResponseVO<Boolean> updateAgentDomain(@RequestBody AgentDomainVO agentDomainVO) {
        agentDomainVO.setUpdater(CurrReqUtils.getAccount());
        agentDomainVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentDomainApi.updateAgentDomain(agentDomainVO);
    }

    @GetMapping("/getAgentDomainById")
    @Operation(summary = "获取域名管理")
    public ResponseVO<AgentDomainResponseVO> getAgentDomainById(@RequestParam("id") String id) {
        return agentDomainApi.getAgentDomainById(id);
    }

}
