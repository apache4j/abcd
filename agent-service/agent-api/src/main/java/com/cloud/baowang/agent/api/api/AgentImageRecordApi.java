package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageRecordBO;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageRecordVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

/**
 * @Desciption: 图片记录
 * @Author: sheldon
 * @Date: 2024/5/30 11:19
 * @Version: V1.0
 **/
@FeignClient(contextId = "remoteAgentImageRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - AgentImage")
public interface AgentImageRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agent-image-record/api";


    @PostMapping(PREFIX + "/getEnumList")
    @Operation(summary = "获取图片的变更记录的字典")
    ResponseVO<HashMap<String, Object>> getEnumList();


    @Operation(summary = "获取图片的变更记录的列表")
    @PostMapping(PREFIX + "/getAgentImageRecordList")
    ResponseVO<Page<AgentImageRecordBO>> getAgentImageRecordList(@RequestBody AgentImageRecordVO agentImageRecordVO);


}
