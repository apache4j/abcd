package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentreview.info.*;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
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

@FeignClient(contextId = "remoteAgentInfoModifyReviewApi", value = ApiConstants.NAME)
@Tag(name = "RPC 新增会员配置 服务")
public interface AgentInfoModifyReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/agentInfoModify/api/";

    @Operation(summary = "代理信息变更记录")
    @PostMapping(value = PREFIX + "record/pageList")
    ResponseVO<Page<AgentInfoChangeRecordPageVO>> recordPageList(@RequestBody AgentInfoChangeRecordQueryVO vo);

    @Operation(summary = "代理信息编辑审核列表分页查询接口")
    @PostMapping(PREFIX + "pageList")
    ResponseVO<Page<AgentInfoModifyReviewPageVO>> pageList(@RequestBody AgentInfoModifyReviewPageQueryVO vo);

    @Operation(summary = "代理信息编辑审核前锁单")
    @PostMapping(PREFIX + "lock")
    ResponseVO<Void> lock(@Valid @RequestBody AgentInfoModifyReviewLockVO vo);

    @Operation(summary = "代理信息编辑审核接口")
    @PostMapping(PREFIX + "review")
    ResponseVO<Void> review(@Valid @RequestBody AgentInfoModifyReviewVO vo);

    @Operation(summary = "代理信息编辑审核详情接口")
    @PostMapping(PREFIX + "detail")
    ResponseVO<AgentInfoModifyReviewDetailVO> detail(@Valid @RequestBody AgentInfoModifyReviewDetailQueryVO vo);

    @Operation(summary = "代理信息编辑审核详情接口")
    @PostMapping(PREFIX + "getDownBox")
    ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestParam("siteCode") String siteCode);
    @Operation(summary = "获取当前站点所有代理信息变更待审核记录数")
    @GetMapping(PREFIX + "getAgentInfoReviewRecord")
    long getAgentInfoReviewRecord(@RequestParam("siteCode") String siteCode);
}
