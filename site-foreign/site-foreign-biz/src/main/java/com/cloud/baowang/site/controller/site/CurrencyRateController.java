package com.cloud.baowang.site.controller.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.exchange.CurrencyRateConfigApi;
import com.cloud.baowang.system.api.vo.exchange.CalculateRateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateEditReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateInitRequestVO;
import com.cloud.baowang.system.api.vo.exchange.RateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateResVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/21 09:25
 * @Version: V1.0
 **/
@RestController
@Tag(name = "站点汇率设置")
@RequestMapping("/currency-rate/api")
@AllArgsConstructor
public class CurrencyRateController {

   private final CurrencyRateConfigApi currencyRateConfigApi;

   private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    @PostMapping("selectPage")
    @Operation(summary = "分页查询")
    ResponseVO<Page<RateResVO>> selectPage(@RequestBody RateReqVO rateReqVO){
        rateReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return currencyRateConfigApi.selectPage(rateReqVO);
    }


    @PostMapping(value = "edit")
    @Operation(summary = "修改")
    ResponseVO<String> edit(@RequestBody RateEditReqVO rateEditReqVO){
        rateEditReqVO.setUpdater(CurrReqUtils.getAccount());
        rateEditReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return currencyRateConfigApi.edit(rateEditReqVO);
    }


    @PostMapping(value = "refreshActRate")
    @Operation(summary = "刷新实时汇率")
    ResponseVO<String> refreshActRate(){
        return currencyRateConfigApi.refreshActRate(Boolean.TRUE);
    }


    @PostMapping(value = "calculatingRate")
    @Operation(summary = "汇率计算")
    ResponseVO<String> calculatingRate(@RequestBody @Validated CalculateRateReqVO calculateRateReqVO){
        return currencyRateConfigApi.calculatingRate(calculateRateReqVO);
    }

    @PostMapping(value = "syncAdminRate")
    @Operation(summary = "同步总控最新汇率")
    ResponseVO<String> syncAdminRate(){
        List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS= siteCurrencyInfoApi.getBySiteCode(CurrReqUtils.getSiteCode());
        if(CollectionUtils.isEmpty(siteCurrencyInfoRespVOS)){
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        RateInitRequestVO rateInitRequestVO=new RateInitRequestVO();
        rateInitRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        rateInitRequestVO.setSyncAdmin(true);
        rateInitRequestVO.setSiteFlag(true);
        List<String> currencyCodeList=siteCurrencyInfoRespVOS.stream().map(SiteCurrencyInfoRespVO::getCurrencyCode).toList();
        rateInitRequestVO.setCurrencyCodeList(currencyCodeList);
        rateInitRequestVO.setOperatorUserNo(CurrReqUtils.getAccount());
        currencyRateConfigApi.init(rateInitRequestVO);
        return ResponseVO.success();
    }


}
