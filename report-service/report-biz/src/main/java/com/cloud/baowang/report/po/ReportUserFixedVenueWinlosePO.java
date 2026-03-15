package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_user_fixed_venue_win_lose")
@Schema(title = "会员场馆盈亏-首次结算时间维度")
public class ReportUserFixedVenueWinlosePO extends BasePO {
    /**
     * 日期小时维度
     */
    private Long dayHourMillis;
    /**
     * 站点日期 当天起始时间戳
     */
    private Long dayMillis;
    /**
     * 会员账号
     */
    private String userAccount;
    /**
     * 会员Id
     */
    private String userId;
    /**
     * 上级代理Id
     */
    private String agentId;
    /**
     * 上级代理
     */
    private String agentAccount;
    /**
     * 游戏平台CODE
     */
    private String venueCode;

    /**
     * 平台分类
     */
    private Integer venueType;

    /**
     * 平台游戏分类
     */
    private String venueGameType;

    /**
     * 币种
     */
    private String currency;
    /**
     * 注单数
     */
    private Integer betCount;
    /**
     * 投注金额
     */
    private BigDecimal betAmount;
    /**
     * 有效投注
     */
    private BigDecimal validAmount;
    /**
     * 投注盈亏
     */
    private BigDecimal winLossAmount;
    /**
     * 站点code
     */
    private String siteCode;
}
