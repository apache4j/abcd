package com.cloud.baowang.agent.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferPageRecordVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordTotalVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.info.AgentPayPasswordParam;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentTransferApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理转账 服务")
public interface AgentTransferApi {

    String PREFIX = ApiConstants.PREFIX + "/agentTransfer/api";

    @Operation(summary = "代理转账保存")
    @PostMapping(value = "/saveAgentTransfer")
    ResponseVO<?> saveAgentTransfer(@RequestBody AgentTransferParam param);


    @Operation(summary = "获取代理转账对象")
    @PostMapping(value = "/queryAgentTransfer")
    ResponseVO<AgentTransferVO> queryAgentTransfer(@RequestBody AgentDetailParam param);

    @Operation(summary = "支付密码验证")
    @PostMapping(value = "/verifyPayPassword")
    ResponseVO<?> verifyPayPassword(@RequestBody AgentPayPasswordParam param);

    @Operation(summary = "代理转账记录")
    @PostMapping("queryAgentTransferRecord")
    ResponseVO<Page<AgentTransferPageRecordVO>> queryAgentTransferRecord(
            @RequestBody AgentTransferRecordParam param);

    @Operation(summary = "代理转账记录(站点)")
    @PostMapping(PREFIX + "siteQueryAgentTransferRecord")
    ResponseVO<AgentTransferRecordTotalVO> siteQueryAgentTransferRecord(
            @RequestBody AgentTransferRecordPageParam param);

    @Operation(summary = "代理转账记录(站点)总数")
    @PostMapping(PREFIX + "siteQueryAgentTransferRecordCount")
    Long siteQueryAgentTransferRecordCount(@RequestBody AgentTransferRecordPageParam vo);

    @Operation(summary = "代理转账记录分页查询")
    @PostMapping(PREFIX + "listPage")
    Page<AgentTransferPageRecordVO> listPage(@RequestBody AgentTransferRecordPageReqVO vo);
}
