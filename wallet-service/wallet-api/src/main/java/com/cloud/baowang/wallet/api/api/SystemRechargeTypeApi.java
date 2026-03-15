package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeDetailRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSystemRechargeTypeApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-充值类型信息")
public interface SystemRechargeTypeApi {

    String PREFIX = ApiConstants.PREFIX + "/exchange/rechargeType/";

    @PostMapping(value = PREFIX + "selectAllValid")
    @Operation(summary = "所有有效充值类型")
    ResponseVO<List<SystemRechargeTypeRespVO>> selectAllValid();

    @PostMapping(value = PREFIX + "selectAll")
    @Operation(summary = "所有充值类型")
    ResponseVO<List<SystemRechargeTypeRespVO>> selectAll();

    @PostMapping(value = PREFIX + "selectPage")
    @Operation(summary = "充值类型分页查询")
    ResponseVO<Page<SystemRechargeTypeRespVO>> selectPage(@RequestBody SystemRechargeTypeReqVO systemRechargeTypeReqVO);

    @PostMapping(value = PREFIX + "init")
    @Operation(summary = "充值类型初始化")
    ResponseVO<Boolean> init(@RequestParam("currencyCode")String currencyCode);

    @PostMapping(value = PREFIX + "insert")
    @Operation(summary = "充值类型新增")
    ResponseVO<Void> insert(@RequestBody SystemRechargeTypeNewReqVO systemRechargeTypeReqNewVO);

    @PostMapping(value = PREFIX + "update")
    @Operation(summary = "充值类型修改")
    ResponseVO<Void> update(@RequestBody SystemRechargeTypeUpdateReqVO systemRechargeTypeUpdateReqVO);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "充值类型启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SystemRechargeTypeStatusReqVO systemRechargeTypeStatusReqVO);

    @PostMapping(value = PREFIX + "info")
    @Operation(summary = "充值类型详情查询")
    ResponseVO<SystemRechargeTypeDetailRespVO> info(@RequestBody  IdReqVO idReqVO);


}
