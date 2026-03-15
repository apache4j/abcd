package com.cloud.baowang.common.gateway.config;


import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * sentinel限流返回
 */
@Configuration
public class SentinelConfig {
    public SentinelConfig() {
        BlockRequestHandler blockRequestHandler = (exchange, e) -> {
            ResponseVO<?> r = null;
            switch (e.getClass().getName()) {
                case "com.alibaba.csp.sentinel.slots.block.BlockException.ParamFlowException" -> // 参数限流
                        r = ResponseVO.fail(ResultCode.CURRENT_REQUEST_LIMIT);
                case "com.alibaba.csp.sentinel.slots.block.BlockException.FlowException" -> // 限流
                        r = ResponseVO.fail(ResultCode.CURRENT_LIMIT);
                case "com.alibaba.csp.sentinel.slots.block.BlockException.DegradeException" -> // 熔断
                        r = ResponseVO.fail(ResultCode.HAS_BLOWN);
                case "com.alibaba.csp.sentinel.slots.block.BlockException.SystemBlockException" -> // 系统保护
                        r = ResponseVO.fail(ResultCode.SYSTEM_PROTECTION);
                default -> r = ResponseVO.fail(ResultCode.UNCAUGHT_EXCEPTION);
            }
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(r));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);

    }
}
