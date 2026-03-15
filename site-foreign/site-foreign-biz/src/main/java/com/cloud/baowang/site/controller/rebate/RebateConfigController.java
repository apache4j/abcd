package com.cloud.baowang.site.controller.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.rebate.SiteRebateApi;
import com.cloud.baowang.system.api.vo.site.rebate.SiteCurrencyRebateConfigAddVO;
import com.cloud.baowang.system.api.vo.site.rebate.SiteRebateConfigAddVO;
import com.cloud.baowang.system.api.vo.site.rebate.SiteRebateConfigQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.SiteRebateConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@Tag(name = "站点-游戏返水配置")
@RequestMapping("/rebate/api/")
@AllArgsConstructor
@Slf4j
public class RebateConfigController {

    private final SiteRebateApi siteRebateApi;
    private final SiteApi siteApi;

    @PostMapping("listPage")
    @Operation(summary = "游戏返水配置分页查询 币种-配置集合")
    ResponseVO<List<SiteRebateConfigVO>> listPage(@RequestBody @Validated SiteRebateConfigQueryVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        reqVO.setSiteCode(siteCode);
        return siteRebateApi.listPage(reqVO);
    }


    @PostMapping("saveRebateConfig")
    @Operation(summary = "保存返水配置-单币种")
    ResponseVO saveRebateConfig(@RequestBody List<SiteRebateConfigAddVO> vo){
        return siteRebateApi.saveRebateConfig(vo);
    }


    @PostMapping("enableRebate")
    @Operation(summary = "禁用/开启 0-禁用 1-开启")
    ResponseVO enableRebate(@RequestBody StatusListVO vo){
        Integer status = vo.getStatus() ==null?1:vo.getStatus();
        return siteApi.updateSiteRebateStatus(status);
    }


}
