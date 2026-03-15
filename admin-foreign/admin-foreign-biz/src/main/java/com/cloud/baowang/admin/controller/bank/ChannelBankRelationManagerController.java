package com.cloud.baowang.admin.controller.bank;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.vo.ChannelBankDelReqVO;
import com.cloud.baowang.admin.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.bank.BankChannelManagerApi;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.enums.BankCodeStatusEnums;
import com.cloud.baowang.system.api.vo.bank.*;
import com.cloud.baowang.wallet.api.api.SystemWithdrawChannelApi;
import com.cloud.baowang.wallet.api.vo.bank.BankInfoRspVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.api.SystemWithdrawWayApi;
import com.cloud.baowang.wallet.api.api.bank.BankCardManagerApi;
import com.cloud.baowang.wallet.api.vo.bank.*;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "银行编码管理")
@RestController
@RequestMapping("/channel-bank-manager/api")
@AllArgsConstructor
public class ChannelBankRelationManagerController {

    private final BankChannelManagerApi bankChannelManagerApi;
    private final SystemWithdrawWayApi systemWithdrawWayApi;
    private final SystemCurrencyInfoApi systemCurrencyInfoApi;
    private final BankCardManagerApi bankCardManagerApi;

    private final SystemWithdrawChannelApi systemWithdrawChannelApi;




    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String,List<CodeValueNoI18VO>>> getDownBox() {
        Map<String,List<CodeValueNoI18VO>> result =  new HashMap<>();
        BankCodeStatusEnums[] enums = BankCodeStatusEnums.values();
        List<CodeValueNoI18VO> bankCodeStatus = Lists.newArrayList();
        for (BankCodeStatusEnums item : enums) {
            CodeValueNoI18VO codeValue =  CodeValueNoI18VO.builder().code(item.getCode()).value(item.getName()).build();
            bankCodeStatus.add(codeValue);
        }
        result.put("bankChannelStatusList",bankCodeStatus);

        List<SystemCurrencyInfoRespVO> currencyList = systemCurrencyInfoApi.selectAll().getData();
        List<CodeValueNoI18VO> currencyCodeResult = Lists.newArrayList();
        for (SystemCurrencyInfoRespVO currency : currencyList) {
            CodeValueNoI18VO codeValue =  CodeValueNoI18VO.builder().code(currency.getId()).value(currency.getCurrencyCode()).build();
            currencyCodeResult.add(codeValue);
        }
        result.put("currencyCodeList",currencyCodeResult);
        return ResponseVO.success(result);
    }

    @Operation(summary = "根据币种获得通道")
    @PostMapping(value = "/queryChannelInfoByCurrency")
    public ResponseVO<List<ChannelInfoRspVO>> queryChannelInfoByCurrency(@Validated @RequestBody CurrencyCodeReqVO currencyCodeReqVO) {
        List<ChannelInfoRspVO> channelInfoRspVOS = Lists.newArrayList();
        ResponseVO<List<SystemWithdrawChannelResponseVO>> listResponseVO = systemWithdrawChannelApi.selectBankAll(currencyCodeReqVO.getCurrencyCode());
        if (listResponseVO.isOk()) {
            List<SystemWithdrawChannelResponseVO> systemWithdrawChannelResponseVOS = listResponseVO.getData();
            for (SystemWithdrawChannelResponseVO channelInfo : systemWithdrawChannelResponseVOS) {
                ChannelInfoRspVO rspVO = new ChannelInfoRspVO();
                rspVO.setId(channelInfo.getChannelName()+"/"+channelInfo.getChannelCode());
                rspVO.setChannelName(channelInfo.getChannelName());
                rspVO.setChannelCode(channelInfo.getChannelCode());
                channelInfoRspVOS.add(rspVO);
            }
        }
        return ResponseVO.success(channelInfoRspVOS);
    }

    @Operation(summary = "根据币种返回银行-银行编码")
    @PostMapping("/queryBankInfoByCurrency")
    public ResponseVO<List<BankInfoRspVO>> queryBankInfoByCurrency(@Validated @RequestBody CurrencyCodeReqVO currencyCodeReqVO) {
        String currencyCode = currencyCodeReqVO.getCurrencyCode();
        return bankCardManagerApi.queryBankInfoByCurrency(currencyCode);
    }

    @Operation(summary = "页面-通道分页数据")
    @PostMapping("/pageList")
    public ResponseVO<Page<BankChannelInfoRspVO>> pageList(@RequestBody BankCodeListReqVO vo) {
        return bankChannelManagerApi.pageList(vo);
    }

    @Operation(summary = "页面-删除通道数据")
    @PostMapping("/deleteChannelInfo")
    public ResponseVO<Void> deleteChannelInfo(@RequestBody @Validated ChannelBankDelReqVO req) {
        return bankChannelManagerApi.deleteChannelInfo(req.getId());
    }


    @Operation(summary = "页面-编辑-获取通道银行编码集合")
    @PostMapping("/queryChannelBankRelation")
    public ResponseVO<List<BankChannelManageRspVO>> queryChannelBankRelation(@RequestBody @Validated ChannelBankDelReqVO req) {
        return bankChannelManagerApi.queryChannelBankRelation(req.getId());
    }


    @Operation(summary = "通道银行编码配置-删除")
    @PostMapping("/deleteBankChannelConfig")
    public ResponseVO<Void> deleteBankChannelConfig(@RequestBody @Validated ChannelBankDelReqVO req) {
        return bankChannelManagerApi.deleteBankChannelConfig(req.getId());
    }



    @Operation(summary = "通道银行编码配置-新增")
    @PostMapping("/add")
    public ResponseVO<Boolean> add(@Validated @RequestBody BankChannelManageAddVO vo) {
        return bankChannelManagerApi.add(vo);
    }


    @Operation(summary = "通道银行编码配置-修改")
    @PostMapping("/edit")
    public ResponseVO<Void> edit(@Validated @RequestBody BankChannelManageAddVO editVO) {
        return bankChannelManagerApi.edit(editVO);
    }



    @Operation(summary = "获取总控全部银行编码")
    @PostMapping("/queryAllChannelBankRelation")
    public ResponseVO<Set<BankInfoAdminRspVO>> queryAllChannelBankRelation(@RequestBody @Validated BankChannelRelationQueryVO req) {
        return bankChannelManagerApi.queryAllChannelBankRelation(req);
    }


}
