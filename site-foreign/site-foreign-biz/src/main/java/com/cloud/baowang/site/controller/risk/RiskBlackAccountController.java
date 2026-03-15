package com.cloud.baowang.site.controller.risk;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AddressUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.service.risk.SiteRiskBlackAccountService;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.site.SiteRiskCtrlBlackApi;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountAddVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountVO;
import com.cloud.baowang.user.api.vo.RiskUserBlackAccountReqVO;
import com.cloud.baowang.user.api.vo.RiskUserBlackAccountVO;
import com.cloud.baowang.wallet.api.api.UserReceiveAccountApi;
import com.cloud.baowang.wallet.api.vo.userwallet.ReceiveAccountQueryVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "风控-黑名单管理")
@RequestMapping("/risk/black")
@AllArgsConstructor
public class RiskBlackAccountController {

    private final SiteRiskCtrlBlackApi riskCtrlBlackApi;

    private final SiteRiskBlackAccountService siteRiskBlackAccountService;

    private final SystemParamApi systemParamApi;

    private final UserReceiveAccountApi userReceiveAccountApi;


    @PostMapping("getRiskTypeList")
    @Operation(summary = "风控-风控黑名单管理-风控类型数据")
    public ResponseVO<List<CodeValueVO>> getRiskTypeList() {
        return systemParamApi.getSystemParamByType(CommonConstant.RISK_TYPE);
    }

    @PostMapping("addBlackAccount")
    @Operation(summary ="风控-风控黑名单管理-添加风控黑名单")
    public ResponseVO<Boolean> addBlackAccount(
            @RequestBody RiskBlackAccountAddVO addVO) {
        if (addVO.getRiskControlTypeCode() == null) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        String siteCode = CurrReqUtils.getSiteCode();
        if (siteCode == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        addVO.setSiteCode(siteCode);
        return siteRiskBlackAccountService.addBlackAccount(addVO);
    }

    @PostMapping("updateBlackAccount")
    @Operation(summary = "风控-风控黑名单管理-编辑")
    public ResponseVO<Boolean> updateBlackAccount(@RequestBody RiskBlackAccountVO riskBlackAccountVO) {
        if (riskBlackAccountVO.getId() == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        String siteCode = CurrReqUtils.getSiteCode();
        if (siteCode == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        riskBlackAccountVO.setSiteCode(siteCode);


        riskBlackAccountVO.setUpdater(CurrReqUtils.getAccount());
        riskBlackAccountVO.setUpdatedTime(System.currentTimeMillis());

        return siteRiskBlackAccountService.updateBlackAccount(riskBlackAccountVO);
    }

    @PostMapping("removeBlackAccount")
    @Operation(summary = "风控-风控黑名单管理-删除")
    public ResponseVO removeBlackAccount(
            @RequestBody IdVO idVO) {
        if (idVO.getId() == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return riskCtrlBlackApi.removeBlackAccount(idVO);
    }

    @PostMapping("getRiskBlackListPage")
    @Operation( summary = "风控-黑名单管理-查询")
    public ResponseVO<Page<RiskBlackAccountVO>> getRiskBlackListPage(@RequestBody RiskBlackAccountReqVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        if (siteCode == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        reqVO.setSiteCode(siteCode);
        return riskCtrlBlackApi.getRiskBlackListPage(reqVO);
    }

    @PostMapping("getRiskUserBlackListPage")
    @Operation( summary = "风控-黑名单管理-账号列表")
    public ResponseVO<Page<RiskUserBlackAccountVO>> getRiskUserBlackListPage(@RequestBody RiskUserBlackAccountReqVO reqVO) {
        return siteRiskBlackAccountService.getRiskUserBlackListPage(reqVO);
    }

    @PostMapping("getUserReceiveAccount")
    @Operation( summary = "风控-黑名单管理-根据加密货币地址获取查询绑定账号信息")
    public ResponseVO<UserReceiveAccountVO> getUserReceiveAccount(@RequestBody ReceiveAccountQueryVO receiveAccountQueryVO) {
        String netWork = AddressUtils.getAddressNetWork(receiveAccountQueryVO.getReceiveAccount());
        if (netWork == null) {
            return ResponseVO.fail(ResultCode.VIRTUAL_ADDRESS_ILLEGAL);
        }
        UserReceiveAccountVO userReceiveAccountVO = new UserReceiveAccountVO();
        userReceiveAccountVO.setNetworkType(netWork);
        userReceiveAccountVO.setAddressNo(receiveAccountQueryVO.getReceiveAccount());
        return ResponseVO.success(userReceiveAccountVO);
    }
}
