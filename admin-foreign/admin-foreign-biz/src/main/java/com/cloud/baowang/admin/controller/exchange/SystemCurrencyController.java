package com.cloud.baowang.admin.controller.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.vo.IdReqVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoDetailRespVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoNewReqVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoReqVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoRespVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoStatusReqVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 17:25
 * @Version: V1.0
 **/
@RestController
@Tag(name = "基础设置-币种管理")
@RequestMapping("/exchange/systemCurrency")
@AllArgsConstructor
public class SystemCurrencyController {

    private final SystemCurrencyInfoApi systemCurrencyInfoApi;

    @GetMapping("selectAllValid")
    @Operation(summary = "有效币种查询")
    ResponseVO<List<SystemCurrencyInfoRespVO>> selectAllValid(){
        return systemCurrencyInfoApi.selectAllValid();
    }

    @GetMapping("selectAll")
    @Operation(summary = "所有币种查询")
    ResponseVO<List<SystemCurrencyInfoRespVO>> selectAll(){
        return systemCurrencyInfoApi.selectAll();
    }

    @PostMapping("selectPage")
    @Operation(summary = "币种分页查询")
    ResponseVO<Page<SystemCurrencyInfoRespVO>> selectPage(@RequestBody @Validated SystemCurrencyInfoReqVO systemCurrencyInfoReqVO){
        systemCurrencyInfoReqVO.setLanguageCode(CurrReqUtils.getLanguage());
        return systemCurrencyInfoApi.selectPage(systemCurrencyInfoReqVO);
    }

    @PostMapping("info")
    @Operation(summary = "币种详情查询")
    ResponseVO<SystemCurrencyInfoDetailRespVO> info(@RequestBody @Validated IdReqVO idReqVO){
        return systemCurrencyInfoApi.info(idReqVO);
    }


    @PostMapping("insert")
    @Operation(summary = "币种新增")
    ResponseVO<Boolean> insert(@RequestBody @Validated SystemCurrencyInfoNewReqVO systemCurrencyInfoNewReqVO){
        systemCurrencyInfoNewReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        systemCurrencyInfoNewReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return systemCurrencyInfoApi.insert(systemCurrencyInfoNewReqVO);
    }

    @PostMapping("update")
    @Operation(summary = "币种修改")
    ResponseVO<Boolean> update(@RequestBody @Validated SystemCurrencyInfoUpdateReqVO systemCurrencyInfoUpdateReqVO){
        systemCurrencyInfoUpdateReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        systemCurrencyInfoUpdateReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return systemCurrencyInfoApi.update(systemCurrencyInfoUpdateReqVO);
    }


    @PostMapping("enableOrDisable")
    @Operation(summary = "币种启用禁用")
    ResponseVO<Boolean> enableOrDisable(@RequestBody @Validated SystemCurrencyInfoStatusReqVO systemCurrencyInfoStatusReqVO){
        systemCurrencyInfoStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemCurrencyInfoApi.enableOrDisable(systemCurrencyInfoStatusReqVO);
    }

    @Operation(summary = "下拉框 CURRENCY_DECIMAL_TYPE 精度类型 ENABLE_DISABLE_TYPE 启用禁用状态" )
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        //CURRENCY_DECIMAL_TYPE 精度类型
        // ENABLE_DISABLE_TYPE 启用禁用状态
       /* List<CodeValueVO> currencyStatus = Lists.newArrayList();
        for(EnableStatusEnum enableStatusEnum:EnableStatusEnum.getList()){
            CodeValueVO codeValueVO=new CodeValueVO();
            codeValueVO.setCode(enableStatusEnum.getCode()+"");
            codeValueVO.setValue(enableStatusEnum.getName());
            currencyStatus.add(codeValueVO);
        }
        List<CodeValueVO> decimalEnums = Lists.newArrayList();
        for(DecimalEnum decimalEnum: DecimalEnum.getList()){
            CodeValueVO codeValueVO=new CodeValueVO();
            codeValueVO.setCode(decimalEnum.getCode());
            codeValueVO.setValue(decimalEnum.getName());
            decimalEnums.add(codeValueVO);
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", currencyStatus);
        result.put("decimalEnum", decimalEnums);*/
        return ResponseVO.success();
    }



}
