package com.cloud.baowang.system.api.api.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.IdReqVO;
import com.cloud.baowang.system.api.vo.exchange.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSystemCurrencyApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-币种信息")
public interface SystemCurrencyInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/exchange/currency/";

    @GetMapping(value = PREFIX + "selectAllValid")
    @Operation(summary = "查询所有有效币种")
    ResponseVO<List<SystemCurrencyInfoRespVO>> selectAllValid();

    @GetMapping(value = PREFIX + "selectAll")
    @Operation(summary = "查询所有币种")
    ResponseVO<List<SystemCurrencyInfoRespVO>> selectAll();

    @PostMapping(value = PREFIX + "selectPage")
    @Operation(summary = "币种分页查询")
    ResponseVO<Page<SystemCurrencyInfoRespVO>> selectPage(@RequestBody SystemCurrencyInfoReqVO systemCurrencyInfoReqVO);

    @PostMapping(value = PREFIX + "insert")
    @Operation(summary = "币种新增")
    ResponseVO<Boolean> insert(@RequestBody SystemCurrencyInfoNewReqVO systemCurrencyInfoNewReqVO);

    @PostMapping(value = PREFIX + "update")
    @Operation(summary = "币种修改")
    ResponseVO<Boolean> update(@RequestBody SystemCurrencyInfoUpdateReqVO systemCurrencyInfoUpdateReqVO);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "币种启用禁用")
    ResponseVO<Boolean> enableOrDisable(@RequestBody SystemCurrencyInfoStatusReqVO systemCurrencyInfoStatusReqVO);

    @PostMapping(value = PREFIX + "info")
    @Operation(summary = "币种详情")
    ResponseVO<SystemCurrencyInfoDetailRespVO> info(@RequestBody IdReqVO idReqVO);

    @PostMapping(value = PREFIX + "selectByCurrencyCode")
    @Operation(summary = "按照币种查询详情")
    SystemCurrencyInfoDetailRespVO selectByCurrencyCode(@RequestParam("currencyCode") String currencyCode);

    @PostMapping(value = PREFIX + "batchSaveRate")
    @Operation(summary = "平台币汇率批量更新")
    ResponseVO<Boolean> batchSaveRate(@RequestBody List<SystemBatchRateReqVO> systemBatchRateReqVOS);
}
