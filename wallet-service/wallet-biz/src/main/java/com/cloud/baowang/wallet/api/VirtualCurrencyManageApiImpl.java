package com.cloud.baowang.wallet.api;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.VirtualCurrencyManageApi;
import com.cloud.baowang.wallet.po.VirtualCurrencyManagePO;
import com.cloud.baowang.wallet.service.VirtualCurrencyManageService;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.EditVirtualCurrencyAddressVO;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.RiskEditVirtualCurrencyAddressVO;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
public class VirtualCurrencyManageApiImpl implements VirtualCurrencyManageApi {

    private final VirtualCurrencyManageService virtualCurrencyManageService;

    @Override
    public ResponseVO<RiskEditVirtualCurrencyAddressVO> getRiskEditVirtualCurrencyInfoGetByAddr(String addr) {
        VirtualCurrencyManagePO one = virtualCurrencyManageService.getOne(Wrappers.<VirtualCurrencyManagePO>lambdaQuery()
                .eq(VirtualCurrencyManagePO::getVirtualCurrencyAddress, addr).last("limit 1"));
        if(one == null){
            return ResponseVO.success(null);
        }

        return ResponseVO.success(ConvertUtil.entityToModel(one, RiskEditVirtualCurrencyAddressVO.class));
    }

    @Override
    public ResponseVO<RiskEditVirtualCurrencyAddressVO> getRiskEditVirtualCurrencyInfoGetByAddrAndSiteCode(String addr, String siteCode) {
        VirtualCurrencyManagePO one = virtualCurrencyManageService.getOne(Wrappers.<VirtualCurrencyManagePO>lambdaQuery()
                .eq(VirtualCurrencyManagePO::getVirtualCurrencyAddress, addr).eq(VirtualCurrencyManagePO::getSiteCode,siteCode).last("limit 1"));
        if(one == null){
            return ResponseVO.success(null);
        }

        return ResponseVO.success(ConvertUtil.entityToModel(one, RiskEditVirtualCurrencyAddressVO.class));
    }

    @Override
    public ResponseVO<Boolean> updateVirtualCurrencyById(EditVirtualCurrencyAddressVO editVirtualCurrencyAddressVO) {
        if (editVirtualCurrencyAddressVO.getId() == null){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return virtualCurrencyManageService.updateVirtualCurrencyById(editVirtualCurrencyAddressVO);
    }
}
