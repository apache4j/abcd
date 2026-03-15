package com.cloud.baowang.admin.controller.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.partner.SystemPaymentVendorApi;
import com.cloud.baowang.system.api.vo.partner.SystemPaymentVendorPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SystemPaymentVendorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "支付商")
@RequestMapping("/systemPaymentVendor/api")
@AllArgsConstructor
public class SystemPaymentVendorController {

    private final SystemPaymentVendorApi vendorApi;

    @PostMapping("pageQuery")
    @Operation(summary = "支付商分页列表")
    public ResponseVO<Page<SystemPaymentVendorVO>> pageQuery(@RequestBody SystemPaymentVendorPageQueryVO pageQueryVO) {
        return vendorApi.pageQuery(pageQueryVO);
    }

    @GetMapping("detail")
    @Operation(summary = "详情")
    public ResponseVO<SystemPaymentVendorVO> detail(@RequestParam("id") String id) {
        return vendorApi.detail(id);
    }

    @PostMapping("add")
    @Operation(summary = "新增支付商")
    public ResponseVO<Boolean> add(@RequestBody @Validated SystemPaymentVendorVO vendorVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        long time = System.currentTimeMillis();
        vendorVO.setCreator(currentUserAccount);
        vendorVO.setCreatedTime(time);
        vendorVO.setUpdater(currentUserAccount);
        vendorVO.setUpdatedTime(time);
        return vendorApi.add(vendorVO);
    }

    @PostMapping("upd")
    @Operation(summary = "修改支付商")
    public ResponseVO<Boolean> upd(@RequestBody @Validated SystemPaymentVendorVO vendorVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        long time = System.currentTimeMillis();
        vendorVO.setUpdater(currentUserAccount);
        vendorVO.setUpdatedTime(time);
        return vendorApi.upd(vendorVO);
    }

    @GetMapping("del")
    @Operation(summary = "删除支付商")
    public ResponseVO<Boolean> del(@RequestParam("id") String id) {
        return vendorApi.del(id);
    }

    @GetMapping("enableAndDisAble")
    @Operation(summary = "停用/启用")
    public ResponseVO<Boolean> enableAndDisAble(@RequestParam("id") String id, @RequestParam("status") Integer status) {
        String currentUserAccount = CurrReqUtils.getAccount();
        long time = System.currentTimeMillis();
        SystemPaymentVendorVO vendorVO = new SystemPaymentVendorVO();
        vendorVO.setId(id);
        vendorVO.setStatus(status);
        vendorVO.setUpdater(currentUserAccount);
        vendorVO.setUpdatedTime(time);
        return vendorApi.enableAndDisAble(vendorVO);
    }
}
