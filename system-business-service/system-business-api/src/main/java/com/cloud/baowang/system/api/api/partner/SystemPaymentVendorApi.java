package com.cloud.baowang.system.api.api.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.partner.SystemPaymentVendorPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SystemPaymentVendorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "systemPaymentVendorApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - SystemPaymentVendor")
public interface SystemPaymentVendorApi {

    String SYSTEM_PAYMENT_VENDOR_PREFIX = ApiConstants.PREFIX + "/system-payment-vendor/api/";

    @PostMapping(SYSTEM_PAYMENT_VENDOR_PREFIX + "pageQuery")
    ResponseVO<Page<SystemPaymentVendorVO>> pageQuery(@RequestBody SystemPaymentVendorPageQueryVO pageQueryVO);

    @PostMapping(SYSTEM_PAYMENT_VENDOR_PREFIX + "add")
    ResponseVO<Boolean> add(@RequestBody SystemPaymentVendorVO vendorVO);

    @PostMapping(SYSTEM_PAYMENT_VENDOR_PREFIX + "upd")
    ResponseVO<Boolean> upd(@RequestBody SystemPaymentVendorVO vendorVO);

    @GetMapping(SYSTEM_PAYMENT_VENDOR_PREFIX + "del")
    ResponseVO<Boolean> del(@RequestParam("id") String id);

    @PostMapping(SYSTEM_PAYMENT_VENDOR_PREFIX + "enableAndDisAble")
    ResponseVO<Boolean> enableAndDisAble(@RequestBody SystemPaymentVendorVO vendorVO);

    @GetMapping(SYSTEM_PAYMENT_VENDOR_PREFIX + "listQuery")
    ResponseVO<List<SystemPaymentVendorVO>> listQuery();

    @GetMapping(SYSTEM_PAYMENT_VENDOR_PREFIX + "detail")
    @Operation(summary = "详情")
    ResponseVO<SystemPaymentVendorVO> detail(@RequestParam("id") String id);
}
