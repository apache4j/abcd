package com.cloud.baowang.site.controller.vip;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.rebate.SiteRebateApi;
import com.cloud.baowang.system.api.vo.site.SiteBasicVO;
import com.cloud.baowang.system.api.vo.site.rebate.SiteRebateInitVO;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author : mufan
 * @Date : 2024/8/7 11:24
 * @Version : 1.0
 */
@Tag(name = "站点后台大陆盘vip等级相关配置")
@RestController
@RequestMapping("/vipOption/api")
@AllArgsConstructor
public class SiteVipOptionController {

    private final SiteVipOptionApi siteVipOptionApi;
    private final SiteRebateApi siteRebateApi;

    @Operation(summary = "VIP列表查询")
    @GetMapping(value = "/queryList")
    public ResponseVO<List<SiteVipOptionVO>> queryVIPGrade(@RequestParam("currency") String currency) {
        return siteVipOptionApi.getList(CurrReqUtils.getSiteCode(),currency);
    }

    @Operation(summary = "VIP等级编辑")
    @PostMapping(value = "/updateVIP")
    public ResponseVO<Void> updateVIPGrade(@Valid @RequestBody SiteVipOptionVO vo) {
        siteVipOptionApi.updateSiteVipOptionVO(vo);
        SiteRebateInitVO reqVo = SiteRebateInitVO.builder()
                .siteCode(vo.getSiteCode())
                .vipGradeCode(vo.getVipGradeCode())
                .capMode(CurrReqUtils.getHandicapMode())
                .currencyCode(vo.getCurrencyCode())
                .status(vo.getRebateConfig()).build();
        siteRebateApi.updateVipGradeRebateConfig(reqVo);
        return ResponseVO.success();
    }
}
