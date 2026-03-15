package com.cloud.baowang.admin.controller.play.order;

import com.cloud.baowang.admin.service.CompensationOrderService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.baowang.admin.vo.CompensationOrderReqVO;

@Tag(name = "手动拉单")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/game/api")
public class CompensationOrderController {

    private final CompensationOrderService compensationOrderService;
    @Operation(summary = "手动补单")
    @PostMapping("/manualGamePullTask")
    public ResponseVO<Boolean> manualGamePullTask(@RequestBody @Valid CompensationOrderReqVO requestVO) {
        return ResponseVO.success(compensationOrderService.manualGamePullTask(requestVO));
    }
}
