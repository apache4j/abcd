package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.SiteSingleVirtualWalletVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteVirtualWalletMerchantVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteVirtualWalletVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWalletAddrQueryVO;
import com.cloud.baowang.wallet.api.vo.userwallet.SiteVirtualWalletPageQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteSiteVirtualWalletApi", value = ApiConstants.NAME)
@Tag(name = "总台虚拟钱包服务")
public interface SiteVirtualWalletApi {

    String PREFIX = ApiConstants.PREFIX + "/siteVirtualWallet/api/";

    @Operation(summary = "站点基础信息列表")
    @PostMapping(value = PREFIX+"pageQuery")
    ResponseVO<Page<SiteVirtualWalletVO>> pageQuery(@RequestBody SiteVirtualWalletPageQueryVO pageVO);

    @PostMapping(value = PREFIX+"siteWalletInfoQuery")
    @Operation(summary = "查询单个站点钱包信息")
    ResponseVO<List<SiteSingleVirtualWalletVO>> siteWalletInfoQuery(@RequestBody SiteWalletAddrQueryVO queryVO);

    @PostMapping(value = PREFIX+"saveOrUpdateSiteWalletInfos")
    @Operation(summary = "新增/修改站点钱包信息")
    ResponseVO<Boolean> saveOrUpdateSiteWalletInfos(@RequestBody List<SiteSingleVirtualWalletVO> siteWalletInfos);

    @PostMapping(value = PREFIX+"addSiteBaseInfo")
    @Operation(summary = "新增/修改站点基础信息")
    ResponseVO<Boolean> addSiteBaseInfo(@RequestBody SiteVirtualWalletVO siteWalletInfo);


    @PostMapping("/saveOrUpdateSiteMerchantInfo")
    @Operation(summary = "添加/修改商户信息")
    ResponseVO<Boolean> saveOrUpdateSiteMerchantInfo(@RequestBody SiteVirtualWalletMerchantVO vo);
}
