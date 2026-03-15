package com.cloud.baowang.admin.controller.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.vo.exchange.CalculateRateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateEditReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/21 09:25
 * @Version: V1.0
 **/
@RestController
@Tag(name = "法币汇率")
@RequestMapping("/exchange/rate")
@AllArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    @PostMapping("selectPage")
    @Operation(summary = "法定货币分页查询")
    ResponseVO<Page<RateResVO>> selectPage(@RequestBody @Validated  RateReqVO rateReqVO){
        return exchangeRateConfigApi.selectPage(rateReqVO);
    }


    @PostMapping(value = "edit")
    @Operation(summary = "修改")
    ResponseVO<String> edit(@RequestBody  @Validated  RateEditReqVO tateEditReqVO){
        tateEditReqVO.setUpdater(CurrReqUtils.getAccount());
        return exchangeRateConfigApi.edit(tateEditReqVO);
    }

    @PostMapping(value = "calculatingRate")
    @Operation(summary = "法定货币汇率计算")
    ResponseVO<String> calculatingRate(@RequestBody @Validated CalculateRateReqVO calculateRateReqVO){
        return exchangeRateConfigApi.calculatingRate(calculateRateReqVO);
    }

}
