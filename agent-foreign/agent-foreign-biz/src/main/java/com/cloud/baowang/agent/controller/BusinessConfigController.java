package com.cloud.baowang.agent.controller;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.business.BusinessConfigVO;
import com.cloud.baowang.system.api.api.SystemConfigApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "代理-查询商务信息")
@AllArgsConstructor
@RestController
@RequestMapping("/agentBusiness/api")
public class BusinessConfigController {

    private SystemConfigApi systemConfigApi;

    @Operation(summary = "查询商务信息")
    @GetMapping("/businessConfig")
    public ResponseVO<List<BusinessConfigVO>> queryBusinessConfig() {
        return systemConfigApi.queryBusinessConfig();
    }
}
