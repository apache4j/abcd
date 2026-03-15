package com.cloud.baowang.system.api.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.partner.SitePaymentVendorApi;
import com.cloud.baowang.system.api.vo.partner.AddSitePaymentVendorSortVO;
import com.cloud.baowang.system.api.vo.partner.SitePaymentVendorPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SitePaymentVendorVO;
import com.cloud.baowang.system.service.partner.SitePaymentVendorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 站点支付商 API 实现类
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SitePaymentVendorApiImpl implements SitePaymentVendorApi {

    private final SitePaymentVendorService vendorService;

    @Override
    public ResponseVO<Page<SitePaymentVendorVO>> pageQuery(SitePaymentVendorPageQueryVO pageQueryVO) {
        return ResponseVO.success(vendorService.pageQuery(pageQueryVO));
    }

    @Override
    public ResponseVO<Boolean> upd(SitePaymentVendorVO sitePartnerVO) {
        return ResponseVO.success(vendorService.upd(sitePartnerVO));
    }

    @Override
    public ResponseVO<Boolean> enableAndDisable(SitePaymentVendorVO sitePartnerVO) {
        return ResponseVO.success(vendorService.enableAndDisable(sitePartnerVO));
    }

    @Override
    public ResponseVO<Boolean> del(String id) {
        return ResponseVO.success(vendorService.del(id));
    }

    @Override
    public ResponseVO<List<SitePaymentVendorVO>> getListBySiteCode(String siteCode) {
        return ResponseVO.success(vendorService.getListBySiteCode(siteCode));
    }

    @Override
    public ResponseVO<Boolean> addSortRules(String operator, List<AddSitePaymentVendorSortVO> sortVOList) {
        return ResponseVO.success(vendorService.addSortRules(operator, sortVOList));
    }

    @Override
    public ResponseVO<List<AddSitePaymentVendorSortVO>> getSortRules(String siteCode) {
        return ResponseVO.success(vendorService.getSortRules(siteCode));
    }

    @Override
    public ResponseVO<SitePaymentVendorVO> detail(String id) {
        return ResponseVO.success(vendorService.detail(id));
    }
}
