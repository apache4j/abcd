package com.cloud.baowang.activity.api.vo.redbag;

import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedBagRainRespVO extends ActivityBaseRespVO implements Serializable {
    /**
     * 站点code
     */
    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    /**
     * 存款金额限制 1是 0否
     */
    @Schema(description = "存款金额限制 1是 0否")
    private Integer depositAmountLimit = CommonConstant.business_zero;

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
     * 投注流水限制
     */
    @Schema(description = "投注流水限制 1 是 0 否")
    private Integer betAmountLimit = CommonConstant.business_zero;

    /**
     * 段位要求 vip_rank_code 数组
     */
    @Schema(description = "段位要求 vip_rank_code 数组")
    private List<String> rankLimit;

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


    public Integer getDepositAmountLimit() {
        if (ObjUtil.isNotEmpty(depositAmount)) {
            return CommonConstant.business_one;
        }
        return CommonConstant.business_zero;
    }

    public Integer getBetAmountLimit() {
        if (ObjUtil.isNotEmpty(betAmount)) {
            return CommonConstant.business_one;
        }
        return CommonConstant.business_zero;
    }
}
