package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SystemWithdrawTypeApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeTypeRespVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeStatusVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawTypeUpdateVO;
import com.cloud.baowang.wallet.service.SystemWithdrawTypeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: qiqi
 **/
@RestController
@Validated
@Slf4j
@AllArgsConstructor
public class SystemWithdrawTypeImpl implements SystemWithdrawTypeApi {

    private final SystemWithdrawTypeService withdrawTypeService;

    @Override
    public ResponseVO<Page<SystemWithdrawTypeResponseVO>> selectPage(SystemWithdrawTypeRequestVO withdrawTypeRequestVO) {
        return withdrawTypeService.selectPage(withdrawTypeRequestVO);
    }

    @Override
    public ResponseVO<Void> insert(SystemWithdrawTypeAddVO withdrawTypeAddVO) {
        return withdrawTypeService.insert(withdrawTypeAddVO);
    }

    @Override
    public ResponseVO<Boolean> init(String currencyCode) {
        return withdrawTypeService.init(currencyCode);
    }

    @Override
    public ResponseVO<Void> update(SystemWithdrawTypeUpdateVO withdrawTypeUpdateVO) {
        return withdrawTypeService.updateByInfo(withdrawTypeUpdateVO);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SystemWithdrawTypeStatusVO withdrawTypeStatusVO) {
        return withdrawTypeService.enableOrDisable(withdrawTypeStatusVO);
    }

    @Override
    public ResponseVO<List<SystemWithdrawTypeResponseVO>> selectAllValid() {
        return withdrawTypeService.selectAllValid();
    }

    @Override
    public ResponseVO<List<SystemWithdrawTypeResponseVO>> selectAll() {
        return withdrawTypeService.selectAll();
    }

    @Override
    public ResponseVO<SystemWithdrawTypeDetailResponseVO> info(IdReqVO idReqVO) {
        return withdrawTypeService.info(idReqVO);
    }
}
