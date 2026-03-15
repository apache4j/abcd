package com.cloud.baowang.admin.controller.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.exchange.EncryptRateConfigApi;
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
@Tag(name = "虚拟币汇率")
@RequestMapping("/encrypt/rate")
@AllArgsConstructor
public class EncyptRateController {

   private final EncryptRateConfigApi encryptRateConfigApi;

    @PostMapping("selectPage")
    @Operation(summary = "虚拟币分页查询")
    ResponseVO<Page<RateResVO>> selectPage(@RequestBody RateReqVO rateReqVO){
        return encryptRateConfigApi.selectPage(rateReqVO);
    }


    @PostMapping(value = "edit")
    @Operation(summary = "虚拟币修改")
    ResponseVO<String> edit(@RequestBody RateEditReqVO rateEditReqVO){
        rateEditReqVO.setUpdater(CurrReqUtils.getAccount());
        return encryptRateConfigApi.edit(rateEditReqVO);
    }



    @PostMapping(value = "calculatingRate")
    @Operation(summary = "虚拟币汇率计算")
    ResponseVO<String> calculatingRate(@RequestBody @Validated CalculateRateReqVO calculateRateReqVO){
        return encryptRateConfigApi.calculatingRate(calculateRateReqVO);
    }


}
