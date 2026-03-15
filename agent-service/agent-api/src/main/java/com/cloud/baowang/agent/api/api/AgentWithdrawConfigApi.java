package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.withdrawConfig.*;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteAgentWithdrawConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理提款设置 服务")
public interface AgentWithdrawConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/agentWithdrawConfig/api";

    @Operation(summary = "新增")
    @PostMapping(value = PREFIX + "/add")
    ResponseVO<Void> add(@RequestBody AgentWithdrawConfigAddVO vo);

    @Operation(summary = "删除")
    @PostMapping(value = PREFIX + "/del")
    ResponseVO<Void> del(@RequestBody IdVO vo);

    @Operation(summary = "修改")
    @PostMapping(value = PREFIX + "/edit")
    ResponseVO<Void> edit(@RequestBody AgentWithdrawConfigEditVO vo);

    @Operation(summary = "分页查询")
    @PostMapping(value = PREFIX + "/pageList")
    ResponseVO<Page<AgentWithdrawConfigPageVO>> pageList(@RequestBody AgentWithdrawConfigPageQueryVO vo);

    @Operation(summary = "详情")
    @PostMapping(value = PREFIX + "/detail")
    ResponseVO<AgentWithdrawConfigDetailResVO> detail(@RequestBody IdVO vo);

    @Operation(summary = "站点同步提款配置")
    @PostMapping(value = PREFIX + "/syncAgentWithdrawConfig")
    void syncAgentWithdrawConfig(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "查询币种下的提款方式")
    @GetMapping("/queryWithdrawWay")
    ResponseVO<List<AgentWithdrawWayRspVO>> queryWithdrawWay();
}
