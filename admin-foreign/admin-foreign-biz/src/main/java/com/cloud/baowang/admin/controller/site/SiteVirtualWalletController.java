package com.cloud.baowang.admin.controller.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.vo.member.BusinessAdminVO;
import com.cloud.baowang.wallet.api.api.SiteVirtualWalletApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteSingleVirtualWalletVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteVirtualWalletMerchantVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteVirtualWalletVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWalletAddrQueryVO;
import com.cloud.baowang.wallet.api.vo.userwallet.SiteVirtualWalletPageQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "总台-站点虚拟币设置")
@RestController
@AllArgsConstructor
@RequestMapping("/site-virtual-wallet/api")
@Validated
public class SiteVirtualWalletController {

    private final SiteVirtualWalletApi siteVirtualWalletApi;
    private final BusinessAdminApi businessAdminApi;

    @PostMapping("/pageQuery")
    @Operation(summary = "站点虚拟钱包配置列表")
    public ResponseVO<Page<SiteVirtualWalletVO>> pageQuery(@RequestBody SiteVirtualWalletPageQueryVO pageQueryVO) {
        return siteVirtualWalletApi.pageQuery(pageQueryVO);
    }

    @PostMapping("/siteWalletInfoQuery")
    @Operation(summary = "查询单个站点的虚拟钱包信息")
    public ResponseVO<List<SiteSingleVirtualWalletVO>> siteWalletInfoQuery( @RequestBody SiteWalletAddrQueryVO queryVO) {
        return  siteVirtualWalletApi.siteWalletInfoQuery(queryVO);
    }


    @PostMapping("/saveOrUpdateSiteWalletInfos")
    @Operation(summary = "添加/修改站点虚拟钱包信息")
    public ResponseVO<Boolean> saveOrUpdateSiteWalletInfos(@Valid @RequestBody List<SiteSingleVirtualWalletVO> vo) {
        if (vo.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        BusinessAdminVO adminUser = businessAdminApi.getAdminByUserName(CurrReqUtils.getAccount());
        if (adminUser == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        if(!StringUtils.isNumeric(vo.get(0).getGoogleAuthCode())){
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        boolean googleAuth = GoogleAuthUtil.checkCode(adminUser.getGoogleAuthKey(), Integer.parseInt(vo.get(0).getGoogleAuthCode()));
        if (!googleAuth) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        if (StringUtils.isBlank(vo.get(0).getSiteCode())) {
            throw new BaowangDefaultException(ResultCode.SITE_CODE_NOT_EXIST);
        }
        return siteVirtualWalletApi.saveOrUpdateSiteWalletInfos(vo);
    }


    @PostMapping("/saveOrUpdateSiteMerchantInfo")
    @Operation(summary = "添加/修改商户信息")
    public ResponseVO<Boolean> saveOrUpdateSiteMerchantInfo(@RequestBody @Validated SiteVirtualWalletMerchantVO vo) {
        BusinessAdminVO adminUser = businessAdminApi.getAdminByUserName(CurrReqUtils.getAccount());
        if (adminUser == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        if(!StringUtils.isNumeric(vo.getGoogleAuthCode())){
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        boolean googleAuth = GoogleAuthUtil.checkCode(adminUser.getGoogleAuthKey(), Integer.parseInt(vo.getGoogleAuthCode()));
        if (!googleAuth) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        return siteVirtualWalletApi.saveOrUpdateSiteMerchantInfo(vo);
    }


}
