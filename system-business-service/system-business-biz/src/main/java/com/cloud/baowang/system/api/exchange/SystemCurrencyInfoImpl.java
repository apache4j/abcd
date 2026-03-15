package com.cloud.baowang.system.api.exchange;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.vo.IdReqVO;
import com.cloud.baowang.system.api.vo.exchange.*;
import com.cloud.baowang.system.service.exchange.SystemCurrencyInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption: 币种信息操作
 * @Author: Ford
 * @Date: 2024/7/26 14:10
 * @Version: V1.0
 **/
@RestController
@Validated
@Slf4j
public class SystemCurrencyInfoImpl implements SystemCurrencyInfoApi {
    @Resource
    private SystemCurrencyInfoService systemCurrencyInfoService;

    @Override
    public ResponseVO<List<SystemCurrencyInfoRespVO>> selectAllValid() {
        return systemCurrencyInfoService.selectAllValid();
    }

    @Override
    public ResponseVO<List<SystemCurrencyInfoRespVO>> selectAll() {
        return systemCurrencyInfoService.selectAll();
    }

    @Override
    public ResponseVO<Page<SystemCurrencyInfoRespVO>> selectPage(SystemCurrencyInfoReqVO systemCurrencyInfoReqVO) {
        return systemCurrencyInfoService.selectPage(systemCurrencyInfoReqVO);
    }

    @Override
    public ResponseVO<Boolean> insert(SystemCurrencyInfoNewReqVO systemCurrencyInfoNewReqVO) {
        return systemCurrencyInfoService.insert(systemCurrencyInfoNewReqVO);
    }

    @Override
    public ResponseVO<Boolean> update(SystemCurrencyInfoUpdateReqVO systemCurrencyInfoUpdateReqVO) {
        return systemCurrencyInfoService.updateInfo(systemCurrencyInfoUpdateReqVO);
    }

    @Override
    public ResponseVO<Boolean> enableOrDisable(SystemCurrencyInfoStatusReqVO systemCurrencyInfoStatusReqVO) {
        return systemCurrencyInfoService.enableOrDisable(systemCurrencyInfoStatusReqVO);
    }

    @Override
    public ResponseVO<SystemCurrencyInfoDetailRespVO> info(IdReqVO idReqVO) {
        return systemCurrencyInfoService.info(idReqVO);
    }

    @Override
    public SystemCurrencyInfoDetailRespVO selectByCurrencyCode(String currencyCode) {
        return systemCurrencyInfoService.selectByCurrencyCode(currencyCode);
    }

    @Override
    public ResponseVO<Boolean> batchSaveRate(List<SystemBatchRateReqVO> systemBatchRateReqVOS) {
        return systemCurrencyInfoService.batchSaveRate(systemBatchRateReqVOS);
    }


}
