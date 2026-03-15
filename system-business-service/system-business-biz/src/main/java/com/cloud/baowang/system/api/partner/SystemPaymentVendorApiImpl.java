package com.cloud.baowang.system.api.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.partner.SystemPaymentVendorApi;
import com.cloud.baowang.system.api.vo.partner.SystemPaymentVendorPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SystemPaymentVendorVO;
import com.cloud.baowang.system.service.partner.SystemPaymentVendorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统支付商 API 实现类
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SystemPaymentVendorApiImpl implements SystemPaymentVendorApi {

    private final SystemPaymentVendorService vendorService;

    @Override
    public ResponseVO<Page<SystemPaymentVendorVO>> pageQuery(SystemPaymentVendorPageQueryVO pageQueryVO) {
        return ResponseVO.success(vendorService.pageQuery(pageQueryVO));
    }

    @Override
    public ResponseVO<Boolean> add(SystemPaymentVendorVO vendorVO) {
        return ResponseVO.success(vendorService.add(vendorVO));
    }

    @Override
    public ResponseVO<Boolean> upd(SystemPaymentVendorVO vendorVO) {
        return ResponseVO.success(vendorService.upd(vendorVO));
    }

    @Override
    public ResponseVO<Boolean> del(String id) {
        return ResponseVO.success(vendorService.del(id));
    }

    @Override
    public ResponseVO<Boolean> enableAndDisAble(SystemPaymentVendorVO vendorVO) {
        return ResponseVO.success(vendorService.enableAndDisAble(vendorVO));
    }

    @Override
    public ResponseVO<List<SystemPaymentVendorVO>> listQuery() {
        return ResponseVO.success(vendorService.listQuery());
    }

    @Override
    public ResponseVO<SystemPaymentVendorVO> detail(String id) {
        return ResponseVO.success(vendorService.detail(id));
    }
}
