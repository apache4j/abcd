package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityConfigDetailReq {

    @Schema(description = "活动请求模板:" +
            "红包雨:RED_ENVELOPE_RAIN, " +
            "首存活动:FIRST_DEPOSIT, " +
            "次存:SECOND_DEPOSIT," +
            " 免费旋转:FREE_WHEEL, " +
            "指定日期存款:ASSIGN_DAY, " +
            "体育负盈利:LOSS_IN_SPORTS, " +
            "流水排行榜:TURNOVER_RANKING, " +
            "每日竞赛:DAILY_COMPETITION:" +
            "转盘:SPIN_WHEEL")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String activityTemplate;

    @Schema(description = "id")
    private String id;

    @Schema(description = "id")
    private List<String> ids;


    /**
     * 活动开始时间
     */
    @Schema(title = "活动开始时间")
    private Long activityStartTime;

    /**
     * 活动结束时间
     */
    @Schema(title = "活动结束时间")
    private Long activityEndTime;

    /**
     * 活动展示开始时间
     */
    @Schema(title = "活动展示开始时间")
    private Long showStartTime;

    /**
     * 活动展示结束时间
     */
    @Schema(title = "活动展示结束时间")
    private Long showEndTime;


    @Schema(title = "活动展示终端")
    private String showTerminal;

    @Schema(title = "活动展示用户类型")
    private String accountType;

    /**
     * 活动时效-ActivityDeadLineEnum
     */
    private Integer activityDeadline;


    /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "状态 0已禁用 1开启中")
    private Integer status;

    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "用户号", hidden = true)
    private String userId;

    @Schema(title = "用户账号", hidden = true)
    private String userAccount;

    @Schema(title = "设备类型", hidden = true)
    private Integer reqDeviceType;

    @Schema(title = "站点时区", hidden = true)
    private String timezone;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    private String venueType;

    // 申请操作:true 派发操作:false
    private boolean applyFlag = true;


}
