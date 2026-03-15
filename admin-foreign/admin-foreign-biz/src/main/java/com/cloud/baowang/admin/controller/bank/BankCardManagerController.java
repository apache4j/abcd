package com.cloud.baowang.admin.controller.bank;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.bank.BankCardManagerApi;
import com.cloud.baowang.wallet.api.vo.bank.BankCardManagerAddReqVO;
import com.cloud.baowang.wallet.api.vo.bank.BankCardManagerChangStatusReqVO;
import com.cloud.baowang.wallet.api.vo.bank.BankCardManagerEditVO;
import com.cloud.baowang.wallet.api.vo.bank.BankCardManagerInfoReqVO;
import com.cloud.baowang.wallet.api.vo.bank.BankCardManagerInfoResVO;
import com.cloud.baowang.wallet.api.vo.bank.BankCardManagerListReqVO;
import com.cloud.baowang.wallet.api.vo.bank.BankCardManagerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "银行卡管理")
@RestController
@RequestMapping("/bank-manager/api")
@AllArgsConstructor
public class BankCardManagerController {

    private final BankCardManagerApi bankCardManagerApi;

    @Operation(summary = "银行卡管理查询")
    @PostMapping("/pageList")
    public ResponseVO<List<BankCardManagerVO>> pageList(@RequestBody BankCardManagerListReqVO vo) {
        return bankCardManagerApi.pageList(vo);
    }

    @Operation(summary = "银行卡管理新增")
    @PostMapping("/add")
    public ResponseVO<Void> add(@Validated @RequestBody BankCardManagerAddReqVO vo) {
        return bankCardManagerApi.add(vo);
    }

    @Operation(summary = "银行卡管理编辑")
    @PostMapping("/edit")
    public ResponseVO<Void> edit(@Validated @RequestBody BankCardManagerEditVO vo) {
        return bankCardManagerApi.edit(vo);
    }

    @Operation(summary = "银行卡管理详情")
    @PostMapping("/info")
    public ResponseVO<BankCardManagerInfoResVO> info(@Validated @RequestBody BankCardManagerInfoReqVO vo) {
        return bankCardManagerApi.info(vo);

    }

    @Operation(summary = "银行卡管理状态管理-status禁用/停用")
    @PostMapping("/changeStatus")
    public ResponseVO<Void> changeStatus(@Validated @RequestBody BankCardManagerChangStatusReqVO vo) {
        return bankCardManagerApi.changeStatus(vo);
    }

}
