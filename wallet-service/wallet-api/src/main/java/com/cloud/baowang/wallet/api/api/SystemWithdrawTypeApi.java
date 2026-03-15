package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeRespVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeStatusVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeUpdateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSystemWithdrawTypeApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-提现类型信息")
public interface SystemWithdrawTypeApi {

    String PREFIX = ApiConstants.PREFIX + "/withdrawType/api/";


    @PostMapping(value = PREFIX + "selectPage")
    @Operation(summary = "提现类型分页查询")
    ResponseVO<Page<SystemWithdrawTypeResponseVO>> selectPage(@RequestBody SystemWithdrawTypeRequestVO withdrawTypeRequestVO);


    @PostMapping(value = PREFIX + "init")
    @Operation(summary = "充值类型初始化")
    ResponseVO<Boolean> init(@RequestParam("currencyCode")String currencyCode);

    @PostMapping(value = PREFIX + "insert")
    @Operation(summary = "提现类型新增")
    ResponseVO<Void> insert(@RequestBody SystemWithdrawTypeAddVO withdrawTypeAddVO);

    @PostMapping(value = PREFIX + "update")
    @Operation(summary = "提现类型修改")
    ResponseVO<Void> update(@RequestBody SystemWithdrawTypeUpdateVO withdrawTypeUpdateVO);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "提现类型启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SystemWithdrawTypeStatusVO withdrawTypeStatusVO);


    @PostMapping(value = PREFIX + "selectAllValid")
    @Operation(summary = "获取有效提现类型")
    ResponseVO<List<SystemWithdrawTypeResponseVO>> selectAllValid();

    @PostMapping(value = PREFIX + "selectAll")
    @Operation(summary = "获取有效提现类型")
    ResponseVO<List<SystemWithdrawTypeResponseVO>> selectAll();


    @PostMapping(value = PREFIX + "info")
    @Operation(summary = "提现类型详情")
    ResponseVO<SystemWithdrawTypeDetailResponseVO> info(@RequestBody IdReqVO idReqVO);
}
