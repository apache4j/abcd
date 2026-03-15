package com.cloud.baowang.user.controller.wallet;


import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.UserTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.enums.UserLabelEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserQueryVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.api.UserRechargeApi;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "客户端-我的-充值")
@RestController
@AllArgsConstructor
@RequestMapping("userRecharge/api")
public class UserRechargeController {
    private final UserInfoApi userInfoApi;

    private final SystemRechargeWayApi rechargeWayApi;


    private final UserRechargeApi userRechargeApi;

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final SiteUserLabelConfigApi siteUserLabelConfigApi;



    @Operation(summary = "获取充值类型及充值方式列表")
    @PostMapping("rechargeWayList")
    public ResponseVO<List<RechargeWayListVO>> rechargeWayList() {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        UserQueryVO queryVO = UserQueryVO.builder().userAccount(userAccount).siteCode(siteCode).build();
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByQueryVO(queryVO);
        RechargeWayRequestVO rechargeWayRequestVO = new RechargeWayRequestVO();
        rechargeWayRequestVO.setMainCurrency(userInfoVO.getMainCurrency());
        rechargeWayRequestVO.setVipRank(userInfoVO.getVipRank());
        rechargeWayRequestVO.setSiteCode(userInfoVO.getSiteCode());
        rechargeWayRequestVO.setVipGradeCode(userInfoVO.getVipGradeCode());
        rechargeWayRequestVO.setHandicapMode(CurrReqUtils.getHandicapMode());
        List<RechargeWayListVO> rechargeTypeListVOS = rechargeWayApi.rechargeWayList(rechargeWayRequestVO);

        return ResponseVO.success(rechargeTypeListVOS);
    }


    @Operation(summary = "会员充值")
    @PostMapping("userRecharge")
    public ResponseVO<OrderNoVO> userRecharge(@RequestBody UserRechargeReqVO userRechargeReqVo) {

        String userAccount = CurrReqUtils.getAccount();
        userRechargeReqVo.setUserAccount(userAccount);
        String ip = CurrReqUtils.getReqIp();
        userRechargeReqVo.setApplyIp(ip);
        Integer deviceType = CurrReqUtils.getReqDeviceType();
        userRechargeReqVo.setDeviceType(String.valueOf(deviceType));
        userRechargeReqVo.setApplyDomain(CurrReqUtils.getReferer());
        userRechargeReqVo.setUserId(CurrReqUtils.getOneId());
        userRechargeReqVo.setSiteCode(CurrReqUtils.getSiteCode());
        userRechargeReqVo.setDeviceNo(CurrReqUtils.getReqDeviceId());

        //创建存款记录
        ResponseVO<OrderNoVO> responseVO = userRechargeApi.userRecharge(userRechargeReqVo);

        return responseVO;
    }

    @Operation(summary = "获取充值订单详情")
    @PostMapping("depositOrderDetail")
    public ResponseVO<UserDepositOrderDetailVO> depositOrderDetail(@RequestBody OrderNoVO orderNoVO) {
        return userRechargeApi.depositOrderDetail(orderNoVO);
    }

    @Operation(summary = "上传凭证")
    @PostMapping("uploadVoucher")
    public ResponseVO<Integer> uploadVoucher(@Validated @RequestBody DepositOrderFileVO depositOrderFileVO) {
        if (StringUtils.isBlank(depositOrderFileVO.getCashFlowFile())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return userRechargeApi.uploadVoucher(depositOrderFileVO);
    }

    @Operation(summary = "撤销充值订单")
    @PostMapping("cancelDepositOrder")
    public ResponseVO<Integer> cancelDepositOrder(@RequestBody OrderNoVO orderNoVO) {
        if (StringUtils.isBlank(orderNoVO.getOrderNo())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return userRechargeApi.cancelDepositOrder(orderNoVO);
    }

    @Operation(summary = "获取充值配置信息")
    @PostMapping("getRechargeConfig")
    public ResponseVO<RechargeConfigVO> getRechargeConfig(@RequestBody RechargeConfigRequestVO rechargeConfigRequestVO) {
        String userId = CurrReqUtils.getOneId();
        rechargeConfigRequestVO.setUserId(userId);
        rechargeConfigRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<RechargeConfigVO> rechargeConfigVO = userRechargeApi.getRechargeConfig(rechargeConfigRequestVO);
        return rechargeConfigVO;
    }


    @Operation(summary = "催单")
    @PostMapping("urgeOrder")
    public ResponseVO urgeOrder(@RequestBody OrderNoVO vo) {
        userRechargeApi.urgeOrder(vo);
        return ResponseVO.success();
    }

    @Operation(summary = "获取充值汇率")
    @PostMapping("getRechargeExchange")
    public ResponseVO<BigDecimal> getRechargeExchange() {
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        UserInfoVO userInfoVO = userInfoApi.getByUserId(CurrReqUtils.getOneId());
        exchangeRateRequestVO.setSiteCode(userInfoVO.getSiteCode());
        exchangeRateRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        return ResponseVO.success(exchangeRate);
    }

    @Operation(summary = "不再提醒")
    @PostMapping("notRemind")
    public ResponseVO notRemind(@RequestBody NotRemindRequestVO notRemindRequestVO) {
        String remindKey = "recharge::noRemind::" + CurrReqUtils.getOneId() + "::" + notRemindRequestVO.getNetWorkType();
        RedisUtil.setValue(remindKey, remindKey, 3600 * 24L, TimeUnit.SECONDS);
        return ResponseVO.success();
    }

    @Operation(summary = "校验会员充值提款状态")
    @PostMapping("checkUserRechargeStatus")
    public ResponseVO checkUserRechargeStatus(@RequestBody CheckUserStatusVO checkUserStatusVO) {
        UserInfoVO userInfoVO = userInfoApi.getByUserId(CurrReqUtils.getOneId());
        if (!userInfoVO.getAccountType().equals(String.valueOf(UserTypeEnum.FORMAL.getCode()))) {
            throw new BaowangDefaultException(ResultCode.CURRENT_ACCOUNT_NOT_DEPOSIT);
        }
        //校验会员账号状态
        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.PAY_LOCK.getCode())) {
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }
        if (CommonConstant.business_two_str.equals(checkUserStatusVO.getType())) {
            //获取会员标签集合
            List<String> userLabelList = getUserLabels(userInfoVO.getUserLabelId());
            //校验标签出款限制
            if (userLabelList.contains(UserLabelEnum.WITHDRAWAL_LIMIT.getLabelId())) {
                throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
            }
        }
        return ResponseVO.success();

    }

    private List<String> getUserLabels(String userLabelId) {
        List<String> userLabelList = new ArrayList<>();
        if (StringUtils.isNotBlank(userLabelId)) {
            List<String> labelIds = Arrays.asList(userLabelId.split(","));
            //会员标签
            List<GetUserLabelByIdsVO> labelList = siteUserLabelConfigApi.getUserLabelByIds(labelIds);
            userLabelList = labelList.stream()
                    .map(GetUserLabelByIdsVO::getLabelId)
                    .collect(Collectors.toList());
        }
        return userLabelList;
    }




}
