package com.cloud.baowang.user.controller.user;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.util.StringUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.vo.user.PlayUserDataVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.user.api.api.UserGlobalSetApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.UserLoginApi;
import com.cloud.baowang.user.api.api.medal.MedalUserApi;
import com.cloud.baowang.user.api.vo.IndexVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserGuideVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.medal.*;
import com.cloud.baowang.user.api.vo.user.*;
import com.cloud.baowang.user.api.vo.user.request.UserDataReq;
import com.cloud.baowang.user.api.vo.user.request.UserEditNameVO;
import com.cloud.baowang.user.api.vo.user.request.UserEditVO;
import com.cloud.baowang.user.service.MemberService;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.vo.recharge.AliAuthRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.RECHARGE_REAL_NAME;
import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.WITHDRAW_REAL_NAME;

@Tag(name = "会员信息")
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/user-info/api")
public class UserInfoController {

    private final UserInfoApi userInfoApi;

    private final MedalUserApi medalUserApi;

    private final PlayServiceApi playServiceApi;

    private final UserGlobalSetApi userGlobalSetApi;
    private final MemberService memberService;

    private final UserLoginApi userLoginApi;

    private final SystemDictConfigApi systemDictConfigApi;

    private final UserCoinApi userCoinApi;


    @Operation(summary = "会员钱包余额信息")
    @PostMapping(value = "/queryWalletInfo")
    public ResponseVO<UserCoinWalletVO> queryWalletInfo() {
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setUserAccount(CurrReqUtils.getAccount());
        userCoinQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        //查询中心钱包
        UserCoinWalletVO userCoinWalletVO = userCoinApi.getUserCenterCoinAndPlatform(userCoinQueryVO);
        return ResponseVO.success(userCoinWalletVO);
    }
    @Operation(summary = "获取会员金额信息")
    @PostMapping("getUserBalance")
    public ResponseVO<IndexVO> getUserBalance() {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();
        return userInfoApi.getUserBalance(userId, userAccount, siteCode);
    }

    @Operation(summary = "获取会员首页信息")
    @PostMapping("getIndexInfo")
    public ResponseVO<UserIndexInfoVO> getIndexInfo() {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();
        Integer handicapMode = CurrReqUtils.getHandicapMode();

        ResponseVO<UserIndexInfoVO> responseVO = userInfoApi.getIndexInfo(userId, userAccount, siteCode, CurrReqUtils.getTimezone());
        if (ObjectUtil.isNotEmpty(responseVO.getData())) {
            responseVO.getData().setHandicapMode(handicapMode);
            responseVO.getData().setPlatCurrencyIcon(CurrReqUtils.getPlatCurrencyIcon());
        }

        log.info("会员首页信息：{}", responseVO);
        return responseVO;
    }

    @Operation(summary = "新手指引步骤记录")
    @PostMapping("/setNewUserGuide")
    ResponseVO<?> setNewUserGuide(@RequestBody UserGuideVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        vo.setUserId(CurrReqUtils.getOneId());
        vo.setUserAccount(CurrReqUtils.getAccount());
        return userLoginApi.setNewUserGuide(vo);
    }


    @Operation(summary = "个人中心-前N个勋章")
    @PostMapping("topNList")
    public ResponseVO<UserCenterMedalRespVO> topNList() {
        return medalUserApi.topNList(CurrReqUtils.getAccount(), CurrReqUtils.getSiteCode());
    }

    @Operation(summary = "获取会员勋章信息")
    @PostMapping("getUserMedalInfo")
    public ResponseVO<UserCenterMedalMyRespVo> getUserMedalInfo() {
        return medalUserApi.getUserMedalInfo(CurrReqUtils.getAccount(), CurrReqUtils.getSiteCode());
    }

    @Operation(summary = "点亮勋章")
    @PostMapping("lightUpMedal")
    public ResponseVO<MedalRemarkRespVO> lightUpMedal(@RequestBody @Validated MedalAcquireReqVO medalAcquireReqVO) {
        medalAcquireReqVO.setUserAccount(CurrReqUtils.getAccount());
        medalAcquireReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return medalUserApi.lightUpMedal(medalAcquireReqVO);
    }

    @Operation(summary = "打开宝箱")
    @PostMapping("openMedalReward")
    public ResponseVO<MedalRewardRemarkRespVO> openMedalReward(@RequestBody @Validated MedalRewardAcquireReqVO medalRewardAcquireReqVO) {
        medalRewardAcquireReqVO.setUserAccount(CurrReqUtils.getAccount());
        medalRewardAcquireReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        medalRewardAcquireReqVO.setUserId(CurrReqUtils.getOneId());
        return medalUserApi.openMedalReward(medalRewardAcquireReqVO);
    }


//    @Operation(summary = "获取用户信息")
//    @PostMapping("getUserInfo")
//    private ResponseVO<UserInfoVO> getUserInfo() {

    /// /        UserInfoVO userDetailVO = playServiceApi.getUserDetailVO(userAccount);
//        return ResponseVO.success(userDetailVO);
//    }
    @Operation(summary = "获取用户信息-统计详情")
    @PostMapping("getUserBetDetailVO")
    private ResponseVO<PlayUserDataVO> getUserBetDetailVO(@RequestBody UserDataReq req) {
        String userAccount = CurrReqUtils.getAccount();
        String gameId = "";
        if (ObjectUtil.isNotEmpty(req.getGameId())) {
            gameId = req.getGameId();
        }
        return ResponseVO.success(playServiceApi.getUserDataDetail(userAccount, gameId));
    }


    @Operation(summary = "更新用户信息")
    @PostMapping("setUserInfo")
    private ResponseVO<Boolean> setUserInfo(@RequestBody UserEditNameVO vo) {
        String userAccount = CurrReqUtils.getAccount();

        if (StringUtils.isBlank(vo.getAvatar()) && StringUtils.isBlank(vo.getNickName())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        String uid = CurrReqUtils.getOneId();
        memberService.checkAvatarChangeTimeLimit(uid);
        UserEditVO userEditVO = UserEditVO.builder().siteCode(CurrReqUtils.getSiteCode())
                .userAccount(userAccount).avatarCode(vo.getAvatarCode()).nickName(vo.getNickName())
                .avatar(vo.getAvatar()).build();
        ResponseVO<Boolean> responseVO = userGlobalSetApi.setUserInfo(userEditVO);
        if (responseVO.isOk()) {
            memberService.incrAvatarChangeTimeLimit(uid);
        }
        return responseVO;
    }

    @Operation(summary = "更新用户信息-个人信息")
    @PostMapping("setUserPersonalInfo")
    private ResponseVO<Boolean> setUserPersonalInfo(@RequestBody UserInfoPersonReqVO vo) {
        String userId = CurrReqUtils.getOneId();
        vo.setUserId(userId);
        return userInfoApi.updateUserPersonInfoById(vo);

    }

    @Operation(summary = "查看用户信息-个人信息")
    @PostMapping("userPersonalInfo")
    private ResponseVO<UserInfoPersonRespVO> userPersonalInfo() {
        GetByUserAccountVO byUserInfoId = userInfoApi.getByUserInfoId(CurrReqUtils.getOneId());
        UserInfoPersonRespVO respVO = ConvertUtil.entityToModel(byUserInfoId, UserInfoPersonRespVO.class);
        ResponseVO<Boolean> realNameAuth = isRealNameAuth();
        if (realNameAuth.isOk()) {
            respVO.setRealNameAuth(realNameAuth.getData());
        } else {
            respVO.setRealNameAuth(Boolean.FALSE);
        }
        return ResponseVO.success(respVO);
    }

    @Operation(summary = "存款是否实名认证")
    @PostMapping("isRealNameAuth")
    public ResponseVO<Boolean> isRealNameAuth() {
        SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(RECHARGE_REAL_NAME.getCode(), CurrReqUtils.getSiteCode()).getData();
        String configName = configValue.getConfigParam();
        if (ObjectUtil.isNotEmpty(configName)) {
            // 配置是否需要实名认证
            if (ObjectUtil.equals(YesOrNoEnum.YES.getCode(), configName)) {
                // 查询是否进行了验证
                GetByUserAccountVO byUserInfoId = userInfoApi.getByUserInfoId(CurrReqUtils.getOneId());
                // 配置需要实名认证，查询是否进行了验证
                if (ObjectUtil.equals(byUserInfoId.getAuthStatus(), CommonConstant.business_one)) {
                    // 已经验证，则不需要验证
                    return ResponseVO.success(Boolean.FALSE);
                } else {
                    return ResponseVO.success(Boolean.TRUE);
                }
            }
        }
        // 如果么有配置，则不进行认证
        return ResponseVO.success(Boolean.FALSE);
    }

    @Operation(summary = "取款/绑卡是否实名认证")
    @PostMapping("withdrawIsRealNameAuth")
    public ResponseVO<Boolean> withdrawIsRealNameAuth() {
        SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(WITHDRAW_REAL_NAME.getCode(), CurrReqUtils.getSiteCode()).getData();
        String configName = configValue.getConfigParam();
        if (ObjectUtil.isNotEmpty(configName)) {
            // 配置是否需要实名认证
            if (ObjectUtil.equals(YesOrNoEnum.YES.getCode(), configName)) {
                // 查询是否进行了验证
                GetByUserAccountVO byUserInfoId = userInfoApi.getByUserInfoId(CurrReqUtils.getOneId());
//                boolean isCny = ObjectUtil.equals(CurrencyEnum.CNY.getCode(), byUserInfoId.getMainCurrency());
                // 配置需要实名认证，查询是否进行了验证
                if (ObjectUtil.equals(byUserInfoId.getAuthStatus(), CommonConstant.business_one)) {
                    // 已经验证，则不需要验证
                    return ResponseVO.success(Boolean.FALSE);
                } else {
                    return ResponseVO.success(Boolean.TRUE);
                }

            }
        }
        // 如果么有配置，则不进行认证
        return ResponseVO.success(Boolean.FALSE);
    }

    @Operation(summary = "实名认证")
    @PostMapping("realNameAuth")
    public ResponseVO<ResultCode> realNameAuth(@RequestBody @Validated AliAuthRequestVO requestVO) {
        requestVO.setUserId(CurrReqUtils.getOneId());
        // 校验验证码功能
        SafeVerifyCodeVO safeVerifyCodeVO = new SafeVerifyCodeVO();
        safeVerifyCodeVO.setVerifyCode(requestVO.getVerifyCode());
        safeVerifyCodeVO.setSiteCode(CurrReqUtils.getSiteCode());
        safeVerifyCodeVO.setType(1);
        safeVerifyCodeVO.setUserAccount(CurrReqUtils.getAccount());
        ResponseVO<ResultCode> responseVO = userGlobalSetApi.checkVerifyCode(safeVerifyCodeVO);
        if (!responseVO.isOk()) {
            return ResponseVO.fail(ResultCode.CODE_ERROR);
        }
        ResponseVO<ResultCode> result = userInfoApi.authVerify(requestVO.getUserId(), requestVO.getUserName(), requestVO.getAreaCode(), requestVO.getPhone(), requestVO.getBirthday());
        if (result.isOk()) {
            return ResponseVO.success(ResultCode.SUCCESS);
        } else {
            return result;
        }

    }

}
