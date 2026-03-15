package com.cloud.baowang.common.sentinel.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component
public class ServiceBlockExceptionHandler implements BlockExceptionHandler {

    //重写默认异常返回内容
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        response.setContentType(CommonConstant.CONTENT_TYPE_HEAD_VALUE);
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
        //输出
        PrintWriter out = response.getWriter();
        out.print(JSON.toJSONString(r));
        out.flush();
        out.close();
    }
}
