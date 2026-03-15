package com.cloud.baowang.system.api.api.exchange;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.exchange.CalculateRateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.system.api.vo.exchange.RateEditReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 *
 */
@FeignClient(contextId = "remoteEncryptRateApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 虚拟货币汇率配置")
public interface EncryptRateConfigApi {


    String PREFIX = ApiConstants.PREFIX + "/encrypt/rate/";

    @PostMapping(value = PREFIX + "selectPage")
    @Operation(summary = "虚拟货币汇率分页查询")
    ResponseVO<Page<RateResVO>> selectPage(@RequestBody RateReqVO rateReqVO);


    @PostMapping(value = PREFIX +"edit")
    @Operation(summary = "虚拟货币汇率修改")
    ResponseVO<String> edit(@RequestBody RateEditReqVO rateEditReqVO);

    @PostMapping(value = PREFIX +"refreshActRate")
    @Operation(summary = "刷新实时虚拟货币汇率")
    ResponseVO<String> refreshActRate(@RequestParam("refreshNowFlag")Boolean refreshNowFlag);

    @PostMapping(value = PREFIX +"calculatingRate")
    @Operation(summary = "虚拟货币汇率计算")
    ResponseVO<String> calculatingRate(@RequestBody  CalculateRateReqVO calculateRateReqVO);

    @PostMapping(value = PREFIX +"getLatestRate")
    @Operation(summary = "获取汇率")
    BigDecimal getLatestRate(@RequestBody RateCalculateRequestVO rateCalculateRequestVO);
}
