package com.cloud.baowang.system.api.api.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.partner.AddPartnerSortVO;
import com.cloud.baowang.system.api.vo.partner.SitePartnerPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SitePartnerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "sitePartnerApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - SitePartnerApi")
public interface SitePartnerApi {
    String SITE_PARTNER_PREFIX = ApiConstants.PREFIX + "/site-partner/api/";

    @PostMapping(SITE_PARTNER_PREFIX + "pageQuery")
    ResponseVO<Page<SitePartnerVO>> pageQuery(@RequestBody SitePartnerPageQueryVO pageQueryVO);

    @PostMapping(SITE_PARTNER_PREFIX + "upd")
    ResponseVO<Boolean> upd(@RequestBody SitePartnerVO sitePartnerVO);

    @PostMapping(SITE_PARTNER_PREFIX + "enableAndDisable")
    ResponseVO<Boolean> enableAndDisable(@RequestBody SitePartnerVO sitePartnerVO);

    @GetMapping(SITE_PARTNER_PREFIX + "del")
    ResponseVO<Boolean> del(@RequestParam("id") String id);

    @GetMapping(SITE_PARTNER_PREFIX + "getListBySiteCode")
    ResponseVO<List<SitePartnerVO>> getListBySiteCode(@RequestParam("siteCode") String siteCode);

    @PostMapping(SITE_PARTNER_PREFIX + "addSortRules")
    ResponseVO<Boolean> addSortRules(@RequestParam("operator") String operator, @RequestBody List<AddPartnerSortVO> sortVOList);

    @GetMapping(SITE_PARTNER_PREFIX + "getSortRules")
    ResponseVO<List<AddPartnerSortVO>> getSortRules(@RequestParam("siteCode") String siteCode);

    @GetMapping(SITE_PARTNER_PREFIX+"detail")
    @Operation(summary = "详情")
    ResponseVO<SitePartnerVO> detail(@RequestParam("id") String id);
}
