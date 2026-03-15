package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteVirtualWalletApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteSingleVirtualWalletVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteVirtualWalletMerchantVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteVirtualWalletVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWalletAddrQueryVO;
import com.cloud.baowang.wallet.api.vo.userwallet.SiteVirtualWalletPageQueryVO;
import com.cloud.baowang.wallet.service.SiteVirtualWalletInfoService;
import com.cloud.baowang.wallet.service.SiteVirtualWalletService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class SiteVirtualWalletApiImpl implements SiteVirtualWalletApi {

    private final SiteVirtualWalletService siteWalletService;
    private final SiteVirtualWalletInfoService siteSingleWalletService;

    @Override
    public ResponseVO<Page<SiteVirtualWalletVO>> pageQuery(SiteVirtualWalletPageQueryVO pageVO) {
        return ResponseVO.success(siteWalletService.selectPage(pageVO));
    }

    @Override
    public ResponseVO<List<SiteSingleVirtualWalletVO>> siteWalletInfoQuery(SiteWalletAddrQueryVO queryVO) {
        return ResponseVO.success(siteSingleWalletService.singleSiteWalletInfo(queryVO));
    }

    @Override
    public ResponseVO<Boolean> saveOrUpdateSiteWalletInfos(List<SiteSingleVirtualWalletVO> siteWalletInfos) {
        return ResponseVO.success(siteSingleWalletService.saveOrUpdateWalletInfo(siteWalletInfos));
    }


    @Override
    public ResponseVO<Boolean> addSiteBaseInfo(SiteVirtualWalletVO siteWalletInfo) {
        return ResponseVO.success(siteWalletService.insertSiteBaseInfo(siteWalletInfo));
    }

    @Override
    public ResponseVO<Boolean> saveOrUpdateSiteMerchantInfo(SiteVirtualWalletMerchantVO vo) {
        return ResponseVO.success(siteWalletService.saveOrUpdateSiteMerchantInfo(vo));
    }
}
