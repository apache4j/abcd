package com.cloud.baowang.activity.api.vo.free;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 免费旋转记录
 */
@Data
@Schema(title = "活动免费旋转记录-返回活动列表")
@I18nClass
public class ActivityFreeGameRespVO {

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;


    @Schema(description = "获取来源订单号 唯一值 做防重处理; 活动订单号|注单订单号")
    private String orderNo;

    @Schema(description = "订单时间")
    private Long orderTime;

    /**
     * 旋转次数变化类型
     * { com.cloud.baowang.play.api.enums.FreeGameChangeTypeEnum}
     */
    @Schema(description = "旋转次数变化类型")
    private Integer type;

    @Schema(description = "活动id")
    private String activityId;

    @Schema(description = "活动编号")
    private String activityNo;

    /**
     * 活动模板-同system_param activity_template
     */
    @Schema(title = "活动模板-同system_param activity_template")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_TEMPLATE)
    private String activityTemplate;

    @Schema(title = "活动模板-同system_param activity_template")
    private String activityTemplateText;

    @Schema(description = "原次数")
    private Integer beforeNum;

    @Schema(description = "赠送次数")
    private Integer acquireNum;

    @Schema(description = "变更后次数")
    private Integer afterNum;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "平台code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;

    @Schema(description = "场馆名称")
    private String venueCodeText;

    @Schema(description = "ip")
    private String ip;


    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间 ")
    private Long createdTime;

    @Schema(title = "时效-小时")
    private Integer timeLimit;
    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "gameId")
    private String gameId;

    @Schema(description = "游戏名称")
    @I18nField
    private String gameName;
    @Schema(description = "消耗次数")
    private Integer consumeCount;


    /**
     * 投注盈亏
     */
    @Schema(description = "投注盈亏")
    private BigDecimal betWinLose;

    @Schema(description = "可领取开始时间")
    private Long receiveStartTime;

    @Schema(description = "可领取结束时间")
    private Long receiveEndTime;


    /**
     * {@link com.cloud.baowang.activity.api.enums.FreeGameSendStatusEnum}
     * free_game_send_status 0-发送中,1-成功,2-失败
     */
    @Schema(title = "free_game_send_status 0-发送中,1-成功,2-失败")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.FREE_GAME_SEND_STATUS)
    private Integer sendStatus;

    private String sendStatusText;

    /**
     * 洗码倍率
     */
    @Schema(title = "活动名称")
    @I18nField
    private String activityNameI18nCode;


    @Schema(title = "限注金额")
    private BigDecimal betLimitAmount;

    @Schema(description = "洗码倍率")
    private BigDecimal washRatio;

}