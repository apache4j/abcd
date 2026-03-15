package com.cloud.baowang.activity.api.vo.free;


import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 活动基础信息的所有字段属性
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "免费旋转查询活动列表入参")
public class FreeGameReqVO extends PageVO implements Serializable  {

    @Schema(description = "创建开始时间 ")
    private Long startTime;

    @Schema(description = "创建结束时间 ")
    private Long endTime;
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;


    /**
     * 站点code
     */
    @Schema(title = "会员账号")
    private String userAccount;

    /**
     * 活动id
     */
    @Schema(title = "活动id")
    private String activitytId;



    /**
     * 站点code
     */
    @Schema(title = "操作人")
    private String operator;


    /**
     * 旋转次数变化类型
     * {@link com.cloud.baowang.play.api.enums.FreeGameChangeTypeEnum}
     */
    @Schema(title = "旋转次数变化类型 0-减少，1-增加")
    private Integer type;

    /**
     * 旋转次数变化类型
     * {@link com.cloud.baowang.play.api.enums.FreeGameChangeTypeEnum}
     */
    @Schema(title = "旋转次数变化类型 1-活动，2-配置")
    private Integer orderFrom = 2;

    /**
     * 站点code
     */
    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "游戏场馆")
    private String venueCode;

    /**
     * 站点code
     */
    @Schema(title = "游戏名称")
    private String gameName;


    @Schema(title = "币种")
    private List<String> currencyCode;

    @Schema(title = "free_game_send_status 0-发送中,1-成功,2-失败")
    private Integer sendStatus;


}