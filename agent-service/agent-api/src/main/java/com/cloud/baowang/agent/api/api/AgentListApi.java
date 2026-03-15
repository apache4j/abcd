package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoPageResultVo;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoPageVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoResponseVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoResultVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentListTreeVO;
import com.cloud.baowang.agent.api.vo.agentinfo.CheckAesSecretKeyVO;
import com.cloud.baowang.agent.api.vo.agentinfo.UpdateWhitelistVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteAgentListApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理列表 服务")
public interface AgentListApi {

    String PREFIX = ApiConstants.PREFIX + "/agentList/api";

    @Operation(summary = "查看AES秘钥")
    @PostMapping(value = PREFIX + "/checkAesSecretKey")
    ResponseVO<CheckAesSecretKeyVO> checkAesSecretKey(@RequestBody IdVO vo);

    @Operation(summary = "更新流量代理的白名单")
    @PostMapping(value = PREFIX + "/updateWhitelist")
    ResponseVO<?> updateWhitelist(@RequestBody UpdateWhitelistVO vo);

    @Operation(summary = "代理列表-查询代理树")
    @PostMapping(value = PREFIX + "/getAgentTree")
    ResponseVO<List<AgentListTreeVO>> getAgentTree(@RequestParam("siteCode")String siteCode);

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "/getAgentPage")
    ResponseVO<AgentInfoResultVO> getAgentPage(@RequestBody AgentInfoPageVO vo);

    @Operation(summary = "代理列表-总记录数")
    @PostMapping(value = PREFIX + "/getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody AgentInfoPageVO vo);


    @Operation(summary = "分页查询")
    @PostMapping(value = PREFIX + "/listPage")
    Page<AgentInfoPageResultVo> listPage(@RequestBody AgentInfoPageVO vo);
}
