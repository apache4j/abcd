package com.cloud.baowang.site.controller.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.partner.SitePaymentVendorApi;
import com.cloud.baowang.system.api.vo.partner.AddSitePaymentVendorSortVO;
import com.cloud.baowang.system.api.vo.partner.SitePaymentVendorPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SitePaymentVendorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "站点支付商")
@RequestMapping("/sitePaymentVendor/api")
@AllArgsConstructor
public class SitePaymentVendorController {
    private final SitePaymentVendorApi paymentVendorApi;

    @PostMapping("pageQuery")
    @Operation(summary = "站点赞助商列表")
    public ResponseVO<Page<SitePaymentVendorVO>> pageQuery(@RequestBody SitePaymentVendorPageQueryVO pageQueryVO) {
        pageQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return paymentVendorApi.pageQuery(pageQueryVO);
    }

    @GetMapping("detail")
    @Operation(summary = "详情")
    public ResponseVO<SitePaymentVendorVO> detail(@RequestParam("id") String id) {
        return paymentVendorApi.detail(id);
    }

    @PostMapping("upd")
    @Operation(summary = "编辑赞助商")
    public ResponseVO<Boolean> upd(@RequestBody SitePaymentVendorVO sitePaymentVendorVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        sitePaymentVendorVO.setUpdater(currentUserAccount);
        sitePaymentVendorVO.setUpdatedTime(System.currentTimeMillis());
        return paymentVendorApi.upd(sitePaymentVendorVO);
    }

    @PostMapping("enableAndDisable")
    @Operation(summary = "启动/禁用赞助商")
    public ResponseVO<Boolean> enableAndDisable(@RequestBody SitePaymentVendorVO sitePaymentVendorVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        sitePaymentVendorVO.setUpdater(currentUserAccount);
        sitePaymentVendorVO.setUpdatedTime(System.currentTimeMillis());
        return paymentVendorApi.enableAndDisable(sitePaymentVendorVO);
    }

    @GetMapping("del")
    @Operation(summary = "删除赞助商")
    public ResponseVO<Boolean> del(@RequestParam("id") String id) {
        return paymentVendorApi.del(id);
    }

    @PostMapping("addSortRules")
    @Operation(summary = "添加排序规则")
    public ResponseVO<Boolean> addSortRules(@RequestBody List<AddSitePaymentVendorSortVO> sortVOList) {
        String operator = CurrReqUtils.getAccount();
        return paymentVendorApi.addSortRules(operator, sortVOList);
    }

    @GetMapping("getSortRules")
    @Operation(summary = "获取当前排序规则")
    public ResponseVO<List<AddSitePaymentVendorSortVO>> getSortRules() {
        String siteCode = CurrReqUtils.getSiteCode();
        return paymentVendorApi.getSortRules(siteCode);
    }

}
