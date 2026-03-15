package com.cloud.baowang.user.api.api.medal;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRemarkRespVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRemarkRespVO;
import com.cloud.baowang.user.api.vo.medal.UserCenterMedalMyRespVo;
import com.cloud.baowang.user.api.vo.medal.UserCenterMedalRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteMedalUserApi", value = ApiConstants.NAME)
@Tag(name = "RPC-用户勋章api")
public interface MedalUserApi {

    String PREFIX = ApiConstants.PREFIX + "/medalUser/api";

    /**
     * APP或H5客户端 个人中心 前N个勋章
     * @return
     */
    @Operation(summary = "个人中心-前N个勋章")
    @PostMapping(value = PREFIX+"/topNList/{currentUserNo}/{siteCode}")
    ResponseVO<UserCenterMedalRespVO> topNList(@PathVariable("currentUserNo") String currentUserNo, @PathVariable("siteCode")String siteCode);

    /**
     * APP或H5客户端 个人中心 勋章详情
     * @param currentUserNo
     * @param siteCode
     * @return
     */
    @Operation(summary = "我的页面-勋章详情")
    @PostMapping(value = PREFIX+"/getUserMedalInfo/{currentUserNo}/{siteCode}")
    ResponseVO<UserCenterMedalMyRespVo> getUserMedalInfo(@PathVariable("currentUserNo")String currentUserNo, @PathVariable("siteCode")String siteCode);

    /**
     * 点亮勋章
     * @param medalAcquireReqVO 点亮参数
     * @return
     */
    @Operation(summary = "我的页面-勋章详情-点亮勋章")
    @PostMapping(value = PREFIX+"/lightUpMedal")
    ResponseVO<MedalRemarkRespVO> lightUpMedal(@RequestBody MedalAcquireReqVO medalAcquireReqVO);


    /**
     * 打开宝箱
     * @param medalRewardAcquireReqVO 打开宝箱
     * @return
     */
    @Operation(summary = "我的页面-勋章详情-打开宝箱")
    @PostMapping(value = PREFIX+"/openMedalReward")
    ResponseVO<MedalRewardRemarkRespVO> openMedalReward(@RequestBody MedalRewardAcquireReqVO medalRewardAcquireReqVO);
}
