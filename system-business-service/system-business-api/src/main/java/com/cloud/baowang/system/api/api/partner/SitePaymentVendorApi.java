package com.cloud.baowang.system.api.api.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.partner.AddSitePaymentVendorSortVO;
import com.cloud.baowang.system.api.vo.partner.SitePaymentVendorPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SitePaymentVendorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "sitePaymentVendorApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - SitePaymentVendorApi")
public interface SitePaymentVendorApi {

    String SITE_PARTNER_PREFIX = ApiConstants.PREFIX + "/site-payment-vendor/api/";

    @PostMapping(SITE_PARTNER_PREFIX + "pageQuery")
    ResponseVO<Page<SitePaymentVendorVO>> pageQuery(@RequestBody SitePaymentVendorPageQueryVO pageQueryVO);

    @PostMapping(SITE_PARTNER_PREFIX + "upd")
    ResponseVO<Boolean> upd(@RequestBody SitePaymentVendorVO sitePartnerVO);

    @PostMapping(SITE_PARTNER_PREFIX + "enableAndDisable")
    ResponseVO<Boolean> enableAndDisable(@RequestBody SitePaymentVendorVO sitePartnerVO);

    @GetMapping(SITE_PARTNER_PREFIX + "del")
    ResponseVO<Boolean> del(@RequestParam("id") String id);

    @GetMapping(SITE_PARTNER_PREFIX + "getListBySiteCode")
    ResponseVO<List<SitePaymentVendorVO>> getListBySiteCode(@RequestParam("siteCode") String siteCode);

    @PostMapping(SITE_PARTNER_PREFIX + "addSortRules")
    ResponseVO<Boolean> addSortRules(@RequestParam("operator") String operator, @RequestBody List<AddSitePaymentVendorSortVO> sortVOList);

    @GetMapping("getSortRules")
    ResponseVO<List<AddSitePaymentVendorSortVO>> getSortRules(@RequestParam("siteCode") String siteCode);

    @GetMapping(SITE_PARTNER_PREFIX+"detail")
    @Operation(summary = "详情")
    ResponseVO<SitePaymentVendorVO> detail(@RequestParam("id") String id);
}
