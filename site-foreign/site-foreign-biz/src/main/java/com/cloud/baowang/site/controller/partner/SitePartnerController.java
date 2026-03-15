package com.cloud.baowang.site.controller.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.partner.SitePartnerApi;
import com.cloud.baowang.system.api.vo.partner.AddPartnerSortVO;
import com.cloud.baowang.system.api.vo.partner.SitePartnerPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SitePartnerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "站点合作赞助商")
@RequestMapping("/sitePartner/api")
@AllArgsConstructor
public class SitePartnerController {
    private final SitePartnerApi partnerApi;

    @PostMapping("pageQuery")
    @Operation(summary = "站点赞助商列表")
    public ResponseVO<Page<SitePartnerVO>> pageQuery(@RequestBody SitePartnerPageQueryVO pageQueryVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        pageQueryVO.setSiteCode(siteCode);
        return partnerApi.pageQuery(pageQueryVO);
    }

    @GetMapping("detail")
    @Operation(summary = "详情")
    public ResponseVO<SitePartnerVO> detail(@RequestParam("id") String id) {
        return partnerApi.detail(id);
    }

    @PostMapping("upd")
    @Operation(summary = "编辑赞助商")
    public ResponseVO<Boolean> upd(@RequestBody SitePartnerVO sitePartnerVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        sitePartnerVO.setUpdater(currentUserAccount);
        sitePartnerVO.setUpdatedTime(System.currentTimeMillis());
        return partnerApi.upd(sitePartnerVO);
    }

    @PostMapping("enableAndDisable")
    @Operation(summary = "启用禁用赞助商")
    public ResponseVO<Boolean> enableAndDisable(@RequestBody SitePartnerVO sitePartnerVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        sitePartnerVO.setUpdater(currentUserAccount);
        sitePartnerVO.setUpdatedTime(System.currentTimeMillis());
        return partnerApi.enableAndDisable(sitePartnerVO);
    }

    @GetMapping("del")
    @Operation(summary = "删除赞助商")
    public ResponseVO<Boolean> del(@RequestParam("id") String id) {
        return partnerApi.del(id);
    }

    @PostMapping("addSortRules")
    @Operation(summary = "添加排序规则")
    public ResponseVO<Boolean> addSortRules(@RequestBody List<AddPartnerSortVO> sortVOList) {
        String operator = CurrReqUtils.getAccount();
        return partnerApi.addSortRules(operator, sortVOList);
    }

    @GetMapping("getSortRules")
    @Operation(summary = "获取当前赞助商排序规则")
    public ResponseVO<List<AddPartnerSortVO>> getSortRules() {
        String siteCode = CurrReqUtils.getSiteCode();
        return partnerApi.getSortRules(siteCode);
    }
}
