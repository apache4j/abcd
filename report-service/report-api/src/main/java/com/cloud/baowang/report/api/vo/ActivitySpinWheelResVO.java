package com.cloud.baowang.report.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/10 9:52
 * @description: 用户排行榜对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "返回转盘活动符合条件的用户列表")
public class ActivitySpinWheelResVO {
    @Schema(description ="会员Id")
    private String userId;


    @Schema(description ="投注有效金额")
    private BigDecimal validAmount;


}
