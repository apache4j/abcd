package com.cloud.baowang.activity.api.vo.category;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 活动配置-红包雨
 *
 * @author aomiao
 * @TableName activity_red_packet
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点-活动配置-新增红包雨实体")
@I18nClass
public class ActivityRedPacketAddRequestVo implements Serializable {
    @Schema(description = "id")
    private Long id;
    /**
     * 所属活动
     */
    @Schema(description = "所属活动")
    private Long activityId;
    /**
     * 有效时间（0.长期，1.指定时间段）
     * {@link com.cloud.baowang.user.api.enums.ActivityEffectiveTimeTypeEnum}
     */
    @Schema(description = "有效时间（0.长期，1.指定时间段）")
    private Integer effectiveTimeType;

    /**
     * 如果type为1（指定时间），则此字段有值，生效时间段
     */
    @Schema(description = "如果type为1（指定时间），则此字段有值，生效时间段")
    private String effectiveTime;
    /**
     * 入口显示时间
     */
    @Schema(description = "入口显示时间")
    private Long displayTime;
    /**
     * 发放开始时间
     */
    @Schema(description = "发放开始时间")
    private Long grantStartTime;
    /**
     * 发放结束时间
     */
    @Schema(description = "发放结束时间")
    private Long grantEndTime;
    /**
     * 金币总金额
     */
    @Schema(description = "金币总金额")
    private BigDecimal coinsTotal;
    /**
     * 金币最低额度，百分比
     */
    @Schema(description = "金币最低额度，百分比")
    private BigDecimal coinsMinQuota;
    /**
     * 金币最高额度，百分比
     */
    @Schema(description = "金币最高额度，百分比")
    private BigDecimal coinsMaxQuota;
    /**
     * 最低存款要求
     */
    @Schema(description = "最低存款要求")
    private BigDecimal depositMin;
    /**
     * 最低流水要求
     */
    @Schema(description = "最低流水要求")
    private BigDecimal bankStatement;
    /**
     * 可领取数量
     */
    @Schema(description = "可领取数量")
    private BigDecimal amountReceived;
    /**
     * 入口图标h5
     */
    @Schema(description = "入口图标h5")
    private String entranceIconH5;
    /**
     * 入口图标pc
     */
    @Schema(description = "入口图标pc")
    private String entranceIconPc;


}
