package com.cloud.baowang.admin.controller.serverMaintn;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.maintain.ServerMaintainApi;
import com.cloud.baowang.system.api.vo.maintain.ServerMaintainChangeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "维护信息管理")
@RestController
@RequestMapping("/server-maintain-manager/api")
@AllArgsConstructor
public class ServerMaintainManagerController {
    private final ServerMaintainApi serverMaintainApi;

    @PostMapping("change")
    @Operation(summary = "维护信息变更")
    public ResponseVO<Void> change(@Valid @RequestBody ServerMaintainChangeVO vo){
        String adminId = CurrReqUtils.getAccount();
        vo.setOperator(adminId);
        return serverMaintainApi.change(vo);
    }

    @PostMapping("info")
    @Operation(summary = "维护信息详情")
    public ResponseVO<ServerMaintainChangeVO> info(){
        return serverMaintainApi.info();
    }

}
