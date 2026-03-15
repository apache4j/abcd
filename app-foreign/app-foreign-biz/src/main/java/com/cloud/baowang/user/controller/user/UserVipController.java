package com.cloud.baowang.user.controller.user;


import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.VipAwardV2Api;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.user.api.vo.vip.*;
import com.cloud.baowang.wallet.api.api.UserWithdrawConfigApi;
import com.cloud.baowang.wallet.api.api.vipV2.VIPAwardRecordV2Api;
import com.cloud.baowang.wallet.api.vo.withdraw.UserVipWithdrawConfigAPPVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Tag(name = "VIP俱乐部")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/user-vip/api")
@Slf4j
public class UserVipController {

    private final UserInfoApi userInfoApi;

    private final UserWithdrawConfigApi userWithdrawConfigApi;


    private final VIPAwardRecordV2Api vipAwardRecordV2Api;

    private final SiteApi siteApi;

    @Operation(summary = "国际盘获取会员VIP信息")
    @GetMapping("getUserVipInfo")
    private ResponseVO<UserVIPInfoVO> getUserVipInfo() {
        ResponseVO<UserVIPInfoVO> userVipInfo = userInfoApi.getUserVipInfo();
        return userVipInfo;
    }

    @Operation(summary = "国内盘获取会员VIP信息")
    @GetMapping("/getUserVipChinaInfo")
    public ResponseVO<UserVIPInfoCNVO> getUserVipChinaInfo() {
        UserVIPInfoResVO userVIPInfoResVO = new UserVIPInfoResVO();
        userVIPInfoResVO.setUserAccount(CurrReqUtils.getAccount());
        userVIPInfoResVO.setUserId(CurrReqUtils.getOneId());
        userVIPInfoResVO.setSiteCode(CurrReqUtils.getSiteCode());
        userVIPInfoResVO.setHandicapMode(SiteHandicapModeEnum.China.getCode());
        userVIPInfoResVO.setTimezone(CurrReqUtils.getTimezone());
        return userInfoApi.getUserVipChianInfo(userVIPInfoResVO);
    }

    @Operation(summary = "查看vip详情-大陆盘")
    @PostMapping("getUserVipDetailCNInfo")
    public ResponseVO<UserVIPDetailInfoVO> getUserVipDetailInfo() {

        ResponseVO<UserVIPDetailInfoVO> userVipInfo = userInfoApi.getUserVipDetailInfo();
        ResponseVO<List<UserVipWithdrawConfigAPPVO>> listResponseVO = listUserWithdrawConfig();
        if (listResponseVO.isOk()) {
            try {
                List<UserVipWithdrawConfigCopyAPPVO> results = ConvertUtil.convertListToList(listResponseVO.getData(), new UserVipWithdrawConfigCopyAPPVO());
                userVipInfo.getData().setUserVipWithdrawConfig(results);
                userVipInfo.getData().setSiteName(siteApi.getSiteInfo(CurrReqUtils.getSiteCode()).getData().getSiteName());
            } catch (Exception e) {
                log.error("getUserVipInfo error", e);
                return ResponseVO.fail(ResultCode.VIP_BENEFIT_QUERY_ERROR);
            }
        }

        return userVipInfo;
    }

    @Operation(summary = "获取VIP等级制度")
    @GetMapping("getUserVipBenefitDetail")
    private ResponseVO<SiteVIPSystemVO> getUserVipBenefitDetail() {
        return userInfoApi.getUserVipBenefitDetail();
    }

    @Operation(summary = "获取vip用户提现配置")
    @GetMapping("getUserWithdrawConfig")
    private ResponseVO<List<UserVipWithdrawConfigAPPVO>> listUserWithdrawConfig() {
        GetByUserAccountVO byUserInfoId = userInfoApi.getByUserInfoId(CurrReqUtils.getOneId());
        UserWithdrawConfigRequestVO vo = new UserWithdrawConfigRequestVO();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setCurrency(byUserInfoId.getMainCurrency());
        ResponseVO<List<UserWithdrawConfigVO>> listResponseVO = userWithdrawConfigApi.listUserWithdrawConfig(vo);
        if (listResponseVO.isOk()) {
            try {
                List<UserVipWithdrawConfigAPPVO> results = ConvertUtil.convertListToList(listResponseVO.getData(), new UserVipWithdrawConfigAPPVO());
                if (!results.isEmpty()) {
                    List<UserVipWithdrawConfigAPPVO> sortedResults = new ArrayList<>(results);
                    sortedResults.sort(Comparator.comparingInt(UserVipWithdrawConfigAPPVO::getVipGradeCode));
                    return ResponseVO.success(sortedResults);
                }
                return ResponseVO.success(results);
            } catch (Exception e) {
                log.error("listUserWithdrawConfig error", e);
                return ResponseVO.fail(ResultCode.VIP_BENEFIT_QUERY_ERROR);
            }

        }
        return ResponseVO.fail(ResultCode.VIP_BENEFIT_QUERY_ERROR);
    }

    @Operation(summary = "获取VIP奖励")
    @PostMapping("getUserVipRewards")
    private ResponseVO<?> getUserVipRewards(@RequestBody UserVipRewardReqVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setUserId(CurrReqUtils.getOneId());
        reqVO.setUserAccount(CurrReqUtils.getAccount());
        //vipAwardRecordV2Api.receiveActiveAward(reqVO.getId());
        return vipAwardRecordV2Api.receiveActiveAward(reqVO.getId());
    }


}
