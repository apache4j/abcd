package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 我的页面-勋章信息VO
 **/
@Data
@Schema(description = "我的页面-勋章信息VO")
@I18nClass
public class UserCenterMedalMyRespVo {


    @Schema(description = "宝箱列表")
    private List<MedalRewardRespVO> medalRewardRespVOS;

    @Schema(description = "勋章奖励备注列表 ")
    private List<MedalRewardRemarkRespVO> rewardRemarkList;


    @Schema(description = "已解锁勋章列表")
    private List<UserCenterMedalRespDetailVO> hasUnlockList;


    @Schema(description = "未解锁勋章列表")
    private List<UserCenterMedalRespDetailVO> notUnlockList;

}
