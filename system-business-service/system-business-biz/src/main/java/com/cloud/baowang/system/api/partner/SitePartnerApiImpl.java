package com.cloud.baowang.system.api.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.partner.SitePartnerApi;
import com.cloud.baowang.system.api.vo.partner.AddPartnerSortVO;
import com.cloud.baowang.system.api.vo.partner.SitePartnerPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SitePartnerVO;
import com.cloud.baowang.system.service.partner.SitePartnerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SitePartnerApiImpl implements SitePartnerApi {
    private final SitePartnerService sitePartnerService;

    @Override
    public ResponseVO<Page<SitePartnerVO>> pageQuery(SitePartnerPageQueryVO pageQueryVO) {
        return ResponseVO.success(sitePartnerService.pageQuery(pageQueryVO));
    }

    @Override
    public ResponseVO<Boolean> upd(SitePartnerVO sitePartnerVO) {
        return ResponseVO.success(sitePartnerService.upd(sitePartnerVO));
    }

    @Override
    public ResponseVO<Boolean> enableAndDisable(SitePartnerVO sitePartnerVO) {
        return ResponseVO.success(sitePartnerService.enableAndDisable(sitePartnerVO));
    }

    @Override
    public ResponseVO<Boolean> del(String id) {
        return ResponseVO.success(sitePartnerService.del(id));
    }

    @Override
    public ResponseVO<List<SitePartnerVO>> getListBySiteCode(String siteCode) {
        return ResponseVO.success(sitePartnerService.getListBySiteCode(siteCode));
    }

    @Override
    public ResponseVO<Boolean> addSortRules(String operator, List<AddPartnerSortVO> sortVOList) {
        return ResponseVO.success(sitePartnerService.addSortRules(operator, sortVOList));
    }

    @Override
    public ResponseVO<List<AddPartnerSortVO>> getSortRules(String siteCode) {
        return ResponseVO.success(sitePartnerService.getSortRules(siteCode));
    }

    @Override
    public ResponseVO<SitePartnerVO> detail(String id) {
        return ResponseVO.success(sitePartnerService.detail(id));
    }
}
