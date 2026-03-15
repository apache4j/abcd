package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentRegisterRecordApi;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordParam;
import com.cloud.baowang.agent.api.vo.agentRegister.AgentRegisterRecordVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "代理-代理注册信息")
@AllArgsConstructor
@RestController
@RequestMapping("/agentRegisterRecord")
public class AgentRegisterController {

    private final AgentRegisterRecordApi agentRegisterRecordApi;
    private final CommonService commonService;

    @Operation(summary = "返回公共下拉框")
    @GetMapping(value = "/querySelectCommon")
    public ResponseVO<Map<String, List<CodeValueVO>>> querySelectCommon(){
        return ResponseVO.success(commonService.getSystemParamsByList(
                List.of(CommonConstant.AGENT_TYPE, CommonConstant.USER_REGISTRY)));
    }

    @Operation(summary = "代理注册日志记录分页查询")
    @PostMapping(value = "/queryAgentRegisterRecord")
    public ResponseVO<Page<AgentRegisterRecordVO>> queryAgentRegisterRecord(
            @RequestBody AgentRegisterRecordParam param){
        String siteCode = CurrReqUtils.getSiteCode();
        param.setSiteCode(siteCode);
        return agentRegisterRecordApi.queryAgentRegisterRecord(param);
    }
}
