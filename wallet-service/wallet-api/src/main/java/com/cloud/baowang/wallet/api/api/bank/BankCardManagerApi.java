package com.cloud.baowang.wallet.api.api.bank;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.bank.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "remoteBankCardManagerApi", value = ApiConstants.NAME)
@Tag(name = "RPC 系统-银行卡管理服务 - BankCardManagerApi")
public interface BankCardManagerApi {
    String PREFIX = "/bankCardManager/api";

    @Operation(summary = "银行卡管理分页查询")
    @PostMapping(PREFIX + "/pageList")
    ResponseVO<List<BankCardManagerVO>> pageList(@RequestBody BankCardManagerListReqVO vo);

    @Operation(summary = "银行卡管理新增")
    @PostMapping(PREFIX + "/add")
    ResponseVO<Void> add(@RequestBody BankCardManagerAddReqVO vo);

    @Operation(summary = "银行卡管理编辑")
    @PostMapping(PREFIX + "/edit")
    ResponseVO<Void> edit(@RequestBody BankCardManagerEditVO vo);

    @Operation(summary = "银行卡管理详情")
    @PostMapping(PREFIX + "/info")
    ResponseVO<BankCardManagerInfoResVO> info(@RequestBody BankCardManagerInfoReqVO vo);

    @Operation(summary = "银行卡管理状态管理-status禁用/停用")
    @PostMapping(PREFIX + "/changeStatus")
    ResponseVO<Void> changeStatus(@RequestBody BankCardManagerChangStatusReqVO vo);


    @Operation(summary = "银行列表-根据币种")
    @PostMapping(PREFIX + "/bankList")
    List<BankManageVO> bankList(@RequestParam("currency") String currency);



    @Operation(summary = "银行卡管理分页查询")
    @GetMapping(PREFIX + "/queryBankInfoByCurrency")
    ResponseVO<List<BankInfoRspVO>> queryBankInfoByCurrency(@RequestParam("currencyCode") String currencyCode);
}
