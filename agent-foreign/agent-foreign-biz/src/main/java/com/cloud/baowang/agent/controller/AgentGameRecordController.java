package com.cloud.baowang.agent.controller;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/3 11:07
 * @Version: V1.0
 **/
@Tag(name = "下级会员-游戏记录")
@RestController
@RequestMapping("/agent-game/api")
@Slf4j
@AllArgsConstructor
public class AgentGameRecordController {

    private final PlayServiceApi playServiceApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        return ResponseVO.success(playServiceApi.agentGameSelect());
    }

}
