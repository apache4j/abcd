package com.cloud.baowang.site.controller.play.order;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.service.CompensationOrderService;
import com.cloud.baowang.site.vo.CompensationOrderReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
