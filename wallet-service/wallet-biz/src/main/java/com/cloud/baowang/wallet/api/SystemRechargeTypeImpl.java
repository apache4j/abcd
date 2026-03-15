package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SystemRechargeTypeApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeDetailRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeUpdateReqVO;
import com.cloud.baowang.wallet.service.SystemRechargeTypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:45
 * @Version: V1.0
 **/
@RestController
@Validated
@Slf4j
public class SystemRechargeTypeImpl implements SystemRechargeTypeApi {
    @Resource
    private SystemRechargeTypeService systemRechargeTypeService;

    @Override
    public ResponseVO<List<SystemRechargeTypeRespVO>> selectAllValid() {
        return systemRechargeTypeService.selectAllValid();
    }

    @Override
    public ResponseVO<List<SystemRechargeTypeRespVO>> selectAll() {
        return systemRechargeTypeService.selectAll();
    }


    @Override
    public ResponseVO<Page<SystemRechargeTypeRespVO>> selectPage(SystemRechargeTypeReqVO systemRechargeTypeReqVO) {
        return systemRechargeTypeService.selectPage(systemRechargeTypeReqVO);
    }

    @Override
    public ResponseVO<Boolean> init(String currencyCode) {
        return systemRechargeTypeService.init(currencyCode);
    }

    @Override
    public ResponseVO<Void> insert(SystemRechargeTypeNewReqVO systemRechargeTypeReqNewVO) {
        return systemRechargeTypeService.insert(systemRechargeTypeReqNewVO);
    }

    @Override
    public ResponseVO<Void> update(SystemRechargeTypeUpdateReqVO systemRechargeTypeUpdateReqVO) {
        return systemRechargeTypeService.updateByInfo(systemRechargeTypeUpdateReqVO);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SystemRechargeTypeStatusReqVO systemRechargeTypeStatusReqVO) {
        return systemRechargeTypeService.enableOrDisable(systemRechargeTypeStatusReqVO);
    }

    @Override
    public ResponseVO<SystemRechargeTypeDetailRespVO> info(IdReqVO idReqVO) {
        return systemRechargeTypeService.info(idReqVO);
    }
}
