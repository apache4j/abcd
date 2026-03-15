package com.cloud.baowang.system.api.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.IpAddressAreaCurrencyApi;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.service.operations.IpAddressAreaCurrencyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class IpAddressAreaCurrencyApiImpl implements IpAddressAreaCurrencyApi {

    private final IpAddressAreaCurrencyService ipAddressAreaCurrencyService;


    @Override
    public Page<IpAddressAreaCurrencyResVO> findPage(IpAddressAreaCurrencyQueryReqVO reqVO) {
        return ipAddressAreaCurrencyService.findPage(reqVO);
    }

    @Override
    public ResponseVO<List<IpAddressAreaCurrencyResVO>> findList(IpAddressAreaCurrencyReqVO reqVO) {
        return ResponseVO.success(ipAddressAreaCurrencyService.findList(reqVO));
    }

    @Override
    public ResponseVO<IpAddressAreaCurrencyResVO> findById(IpAddressAreaCurrencyIdReqVO reqVO) {
        return ResponseVO.success(ipAddressAreaCurrencyService.findById(reqVO));
    }

    @Override
    public ResponseVO<Boolean> insert(IpAddressAreaCurrencyAddReqVO reqVO) {
        return ipAddressAreaCurrencyService.insert(reqVO);
    }

    @Override
    public ResponseVO<Boolean> update(IpAddressAreaCurrencyUpdateReqVO reqVO) {
        return ipAddressAreaCurrencyService.update(reqVO);
    }

    @Override
    public ResponseVO<Boolean> delete(IpAddressAreaCurrencyIdReqVO reqVO) {
        return ipAddressAreaCurrencyService.delete(reqVO);
    }

    @Override
    public ResponseVO<IpAdsWebResVO> queryWebCurrey(IpAdsWebReqVO reqVO) {
        return ipAddressAreaCurrencyService.queryWebCurrey(reqVO);
    }

    @Override
    public ResponseVO<Boolean> enableOrDisable(IpAddressAreaCurrencyStatusReqVO reqVO) {
        return ipAddressAreaCurrencyService.enableOrDisable(reqVO);
    }
}
