package com.cloud.baowang.site.controller.user;


import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserInformationDownVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserLabelVO;
import com.cloud.baowang.common.excel.ImportExcelTemplateUtils;
import com.cloud.baowang.user.api.api.UserDetailsApi;
import com.cloud.baowang.user.api.vo.UserDetails.*;
import com.cloud.baowang.wallet.api.api.UserBankQueryApi;
import com.cloud.baowang.wallet.api.api.UserReceiveAccountApi;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.UserDepositWithdrawalResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountUnBindVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    private final UserInfoApi userInfoApi;

    @Operation(summary = "信息编辑-变更类型")
    @PostMapping("/informationEditings")
    public ResponseVO updateInformation(@RequestBody UserDetailsReqVO userDetailsReqVO) {
        userDetailsReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userDetailsReqVO.setHandicapMode(CurrReqUtils.getHandicapMode());
        return userDetailsApi.updateInformation(userDetailsReqVO, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "信息编辑-下拉框")
    @PostMapping("getInformationDowns")
    public ResponseVO<UserInformationDownVO> getInformationDown(@RequestBody UserInfoDwonReqVO userInfoDwonReqVO) {
        userInfoDwonReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return userDetailsApi.getInformationDown(userInfoDwonReqVO);
    }


    @Operation(summary ="会员详情银行卡列表")
    @PostMapping(value = "/queryBankCardInfo")
    public ResponseVO<List<UserDepositWithdrawalResponseVO>> queryBankCardInfo(@RequestBody WalletUserBasicRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return userBankQueryApi.queryBankCardInfo(requestVO);
    }

    @Operation(summary ="会员详情虚拟币列表")
    @PostMapping(value = "/queryVirtualInfo")
    public ResponseVO<List<UserDepositWithdrawalResponseVO>> queryVirtualInfo(@RequestBody WalletUserBasicRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return userBankQueryApi.queryVirtualInfo(requestVO);
    }

    @Operation(summary ="会员详情电子钱包列表")
    @PostMapping(value = "/queryElectronicInfo")
    public ResponseVO<List<UserDepositWithdrawalResponseVO>> queryElectronicInfo(@RequestBody WalletUserBasicRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return userBankQueryApi.queryWalletInfo(requestVO);
    }

    @Operation(summary = "批量 -变更用户标签")
    @PostMapping("/bathUpdateLabel")
    public ResponseVO updateInformation(@RequestBody BathUserReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        userDetailsApi.bathUpdateLabel(vo,CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
        return ResponseVO.success();
    }
    @Operation(summary = "批量 -变更用户备注")
    @PostMapping("/bathUpdateRemark")
    public ResponseVO<?> bathUpdateRemark(@RequestBody BathUserRemarkReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        userDetailsApi.bathUpdateRemark(vo);
        return ResponseVO.success();
    }


    @Operation(summary = "检查用户")
    @PostMapping("/checkUsers")
    public ResponseVO<String> checkUsers(@RequestBody CheckUserReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userDetailsApi.checkUsers(vo);
    }

    @Operation(summary = "获取批量用户标签")
    @PostMapping("/getBathUsersLabel")
    public ResponseVO<List<UserLabelVO>> getBathUsersLabel(@RequestBody CheckUserReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(userDetailsApi.getBathUsersLabel(vo).getData());
    }

    @Operation(summary = "批量 -删除用户标签")
    @PostMapping("/bathDeleteLabel")
    public ResponseVO bathDeleteLabel(@RequestBody BathUserReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        userDetailsApi.bathDeleteLabel(vo,CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
        return ResponseVO.success();
    }

    @Operation(summary = "免费旋转配置：会员添加旋转配置-excel模版导出")
    @GetMapping("/excelImport")
    public void excelImport(HttpServletResponse response, @RequestParam(value = "fileName", required = false, defaultValue = "会员账号") String fileName) {
        ImportExcelTemplateUtils.importExcelTemplateByCode(response, fileName);
    }

    @Operation(summary ="大陆盘-会员详情-收款信息 银行卡，电子钱包，加密货币列表")
    @PostMapping(value = "/userReceiveAccount")
    public ResponseVO<List<UserReceiveAccountResponseVO>> userReceiveAccount(@RequestBody WalletUserBasicRequestVO requestVO) {
        requestVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(ConvertUtil.entityToModel(requestVO,UserBasicRequestVO.class));
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        return userReceiveAccountApi.userReceiveAccount(requestVO);
    }


    @Operation(summary ="大陆盘-会员详情-收款信息 银行卡，电子钱包，加密货币 解绑申请")
    @PostMapping(value = "/userReceiveAccountUnBind")
    public ResponseVO<Boolean> userReceiveAccountUnBind(@RequestBody UserReceiveAccountUnBindVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(ConvertUtil.entityToModel(requestVO,UserBasicRequestVO.class));
        if (null == userInfoVO) {
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        return userReceiveAccountApi.userReceiveAccountUnBind(requestVO);
    }
}
