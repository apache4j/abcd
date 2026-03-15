package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentImageRecordApi;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageRecordBO;
import com.cloud.baowang.agent.api.vo.agentImage.AgentImageRecordVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 图片的变更记录
 */
@Slf4j
@Tag(name = "图片的变更记录")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-image-record/api")
public class AgentImageRecordController {
    private final SystemParamApi systemParamApi;
    private final AgentImageRecordApi agentImageRecordApi;

    @GetMapping("/getDownBox")
    @Operation(summary = "获取图片变更记录下拉框")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        return systemParamApi.getSystemParamByType(CommonConstant.AGENT_IMAGE_CHANGE_TYPE);
    }

    @Operation(summary = "获取图片的变更记录的列表")
    @PostMapping("/getAgentImageRecordList")
    public ResponseVO<Page<AgentImageRecordBO>> getAgentImageRecordList(@RequestBody AgentImageRecordVO agentImageRecordVO) {
        agentImageRecordVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentImageRecordApi.getAgentImageRecordList(agentImageRecordVO);
    }


}
