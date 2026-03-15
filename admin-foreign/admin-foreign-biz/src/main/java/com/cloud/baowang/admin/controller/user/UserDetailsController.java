package com.cloud.baowang.admin.controller.user;


import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.verify.SiteInfoVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserInformationDownVO;
import com.cloud.baowang.user.api.api.UserDetailsApi;
import com.cloud.baowang.user.api.vo.UserDetails.UserInfoDwonReqVO;
import com.cloud.baowang.wallet.api.api.UserBankQueryApi;
import com.cloud.baowang.wallet.api.api.UserReceiveAccountApi;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.UserDepositWithdrawalResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wade
 * 时间：2023-05-06
 * 会员详情接口
 */
@Tag(name = "会员-会员管理-会员详情")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/user-details/api")
public class UserDetailsController {
    private final UserDetailsApi userDetailsApi;


    private final UserBankQueryApi userBankQueryApi;


    private final UserReceiveAccountApi userReceiveAccountApi;

    private final SiteApi siteApi;
    /*@Operation(summary = "信息编辑-变更类型")
    @PostMapping("/informationEditings")
    public ResponseVO updateInformation(@RequestBody UserDetailsReqVO userDetailsReqVO) {

        return userDetailsApi.updateInformation(userDetailsReqVO, CurrentRequestUtils.getCurrentUserNo(), CurrentRequestUtils.getCurrentUserAccount());
    }*/

    @Operation(summary = "信息编辑-下拉框")
    @PostMapping("getInformationDowns")
    public ResponseVO<UserInformationDownVO> getInformationDown(@RequestBody UserInfoDwonReqVO userInfoDwonReqVO) {

        return userDetailsApi.getInformationDown(userInfoDwonReqVO);
    }


    @Operation(summary ="会员详情银行卡分页列表")
    @PostMapping(value = "/queryBankCardInfo")
    public ResponseVO<List<UserDepositWithdrawalResponseVO>> queryBankCardInfo(@RequestBody WalletUserBasicRequestVO requestVO) {
        requestVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return userBankQueryApi.queryBankCardInfo(requestVO);
    }

    @Operation(summary ="会员详情虚拟币分页列表")
    @PostMapping(value = "/queryVirtualInfo")
    public ResponseVO<List<UserDepositWithdrawalResponseVO>> queryVirtualInfo(@RequestBody WalletUserBasicRequestVO requestVO) {
        return userBankQueryApi.queryVirtualInfo(requestVO);
    }

    @Operation(summary ="会员详情电子钱包分页列表")
    @PostMapping(value = "/queryWalletInfo")
    public ResponseVO<List<UserDepositWithdrawalResponseVO>> queryWalletInfo(@RequestBody WalletUserBasicRequestVO requestVO) {
        return userBankQueryApi.queryWalletInfo(requestVO);
    }

    @Operation(summary ="大陆盘-会员详情-收款信息 银行卡，电子钱包，加密货币列表")
    @PostMapping(value = "/userReceiveAccount")
    public ResponseVO<List<UserReceiveAccountResponseVO>> userReceiveAccount(@RequestBody WalletUserBasicRequestVO requestVO) {
        requestVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return userReceiveAccountApi.userReceiveAccount(requestVO);
    }

    @Operation(summary ="会员详情-获取站点信息 ")
    @PostMapping(value = "/getSiteInfo")
    public ResponseVO<SiteVO> getSiteInfo(@RequestBody UserBasicRequestVO requestVO) {
        String siteCode  = requestVO.getSiteCode();
        return siteApi.getSiteInfo(siteCode);
    }
}
