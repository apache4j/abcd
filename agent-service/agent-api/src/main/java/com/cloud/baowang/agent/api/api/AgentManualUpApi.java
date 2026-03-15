package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewListVO;
import com.cloud.baowang.agent.api.vo.manualup.*;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteAgentManualUpApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理人工加额 服务")
public interface AgentManualUpApi {

    @Operation(summary = "代理人工添加额度 提交")
    @PostMapping(value = "/agent-manual-up/api/agentSubmit")
    ResponseVO<?> agentSubmit(@RequestBody AgentManualUpSubmitVO vo, @RequestParam("operator") String operator);

    @Operation(summary = "代理人工添加额度 查询余额")
    @PostMapping(value = "/agent-manual-up/api/getAgentBalance")
    ResponseVO<List<GetAgentBalanceVO>> getAgentBalance(@RequestBody GetAgentBalanceQueryVO vo);

    @Operation(summary = "代理人工加额记录 分页列表")
    @PostMapping(value = "/agent-manual-up-record/api/getUpRecordPage")
    AgentManualUpRecordResult getUpRecordPage(@RequestBody AgentManualUpRecordPageVO vo);


    @Operation(summary = "代理人工加额记录 分页总数")
    @PostMapping(value = "/agent-manual-up-record/api/getUpRecordPageCount")
    ResponseVO<Long> getUpRecordPageCount(@RequestBody AgentManualUpRecordPageVO vo);


    /* 代理人工加额审核--start */
    @Operation(summary = "代理人工加额审核 锁单或解锁")
    @PostMapping(value = "/agent-manual-up-review/api/lockManualUp")
    ResponseVO<?> lockManualUp(@RequestBody StatusListVO vo, @RequestParam("operator") String adminName);

    @Operation(summary = "代理人工加额审核 一审通过-提交")
    @PostMapping(value = "/agent-manual-up-review/api/oneReviewSuccessManualUp")
    ResponseVO<?> oneReviewSuccessManualUp(@RequestBody ReviewListVO vo,
                                            @RequestParam("operator") String operator);

    @Operation(summary = "代理人工加额审核 一审拒绝-提交")
    @PostMapping(value = "/agent-manual-up-review/api/oneReviewFailManualUp")
    ResponseVO<?> oneReviewFailManualUp(@RequestBody ReviewListVO vo,@RequestParam("operator") String operator);


    @Operation(summary = "代理人工加额审核 审核列表")
    @PostMapping(value = "/agent-manual-up-review/api/getUpReviewPageManualUp")
    Page<AgentManualUpReviewResponseVO> getUpReviewPageManualUp(@RequestBody AgentManualUpReviewPageVO vo, @RequestParam("operator") String operator);

    @Operation(summary = "代理人工加额审核 审核详情")
    @PostMapping(value = "/agent-manual-up-review/api/getUpReviewDetailsManualUp")
    ResponseVO<AgentUpReviewDetailsVO> getUpReviewDetailsManualUp(@RequestBody IdVO vo);

    @Operation(summary = "代理加额审核记录 分页列表")
    @PostMapping(value = "/agent-up-review-record/api/getRecordPage")
    Page<AgentGetRecordResponseResultVO> getRecordPage(@RequestBody AgentGetRecordPageVO vo);


    @Operation(summary = "代理加额审核记录 分页总数")
    @PostMapping(value = "/agent-up-review-record/api/getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody AgentGetRecordPageVO vo);

    @Operation(summary = "代理加额审核记录 分页总数")
    @PostMapping(value = "/agent-up-review-record/api/getNotReviewNum")
    AgentReviewOrderNumVO getNotReviewNum(@RequestParam("siteCode")String siteCode);

    @Operation(summary = "代理人工加额校验代理信息")
    @PostMapping(value = "/agent-up-review-record/api/checkAgentInfo")
    ResponseVO<List<AgentManualUpDownAccountResultVO>> checkAgentInfo(@RequestBody List<AgentManualUpDownAccountResultVO> agentList);
}
