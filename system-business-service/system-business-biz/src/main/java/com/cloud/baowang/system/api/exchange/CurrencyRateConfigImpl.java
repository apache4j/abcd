package com.cloud.baowang.system.api.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.exchange.CurrencyRateConfigApi;
import com.cloud.baowang.system.api.enums.exchange.RateTypeEnum;
import com.cloud.baowang.system.api.vo.exchange.CalculateRateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.system.api.vo.exchange.RateEditReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateInitRequestVO;
import com.cloud.baowang.system.api.vo.exchange.RateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateResVO;
import com.cloud.baowang.system.service.exchange.SystemRateConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @Desciption: 货币汇率
 * @Author: Ford
 * @Date: 2024/5/20 20:42
 * @Version: V1.0
 **/
@RestController
@Validated
@Slf4j
public class CurrencyRateConfigImpl implements CurrencyRateConfigApi {
    @Resource
    private SystemRateConfigService systemRateConfigService;

    /**
     * 分页查询
     * @param rateReqVO 查询条件
     * @return 查询结果
     */
    @Override
    public  ResponseVO<Page<RateResVO>> selectPage(@RequestBody RateReqVO rateReqVO) {
        return ResponseVO.success(systemRateConfigService.selectPage(rateReqVO));
    }

    /**
     * 修改
     * @param rateEditReqVO 修改参数
     * @return 修改结果
     */
    @Override
    public ResponseVO<String> edit(RateEditReqVO rateEditReqVO) {
        return  systemRateConfigService.edit(rateEditReqVO);
    }

    @Override
    public ResponseVO<String> refreshActRate(Boolean refreshNowFlag) {
        return systemRateConfigService.refreshEncryptActRate(refreshNowFlag);
    }

    @Override
    public ResponseVO<String> calculatingRate(CalculateRateReqVO calculateRateReqVO) {
        return systemRateConfigService.calculatingRateReq(calculateRateReqVO);
    }

    @Override
    public BigDecimal getLatestRate(RateCalculateRequestVO rateCalculateRequestVO) {
       // rateCalculateRequestVO.setRateType(RateTypeEnum.ENCRYPT.getCode());
        return systemRateConfigService.getLatestRate(rateCalculateRequestVO);
    }

    @Override
    public void init(RateInitRequestVO rateInitRequestVO) {
         systemRateConfigService.init(rateInitRequestVO);
    }


}
