package com.cloud.baowang.activity.api.vo.redbag;

import com.cloud.baowang.activity.api.vo.ActivityBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 红包雨活动实现
 */
@Data
@Schema(description = "红包雨活动配置")
@AllArgsConstructor
@NoArgsConstructor
public class RedBagRainVO extends ActivityBaseVO implements Serializable {

    /**
     * 站点code
     */
    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    /**
     * 存款金额
     */
    @Schema(description = "存款金额")
    private BigDecimal depositAmount;

    /**
     * 投注流水
     */
    @Schema(description = "投注流水")
    private BigDecimal betAmount;

    /**
     * 段位要求 vip_rank_code 数组
     */
    @Schema(description = "段位要求 vip_rank_code 数组")
    private List<Integer> rankLimit;

    /**
     * 红包雨场次开始时间 数组
     */
    @Schema(description = "红包雨场次开始&ji结束时间 key:value")
    private LinkedHashMap<String, String> sessionTime;

    /**
     * 提前时间 秒
     */
    @Schema(description = "提前时间")
    private Integer advanceTime;

    /**
     * 红包总金额
     */
    @Schema(description = "红包总金额")
    private BigDecimal totalAmount;

    /**
     * 红包掉落时间 秒
     */
    @Schema(description = "红包掉落时间")
    private Integer dropTime;

    /**
     * 活动主键id
     */
    @Schema(description = "活动主键id")
    private String baseId;

    @Schema(description = "活动中奖设置附表")
    private List<RedBagRainConfigVO> configList;

}
