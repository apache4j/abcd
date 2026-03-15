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
@TableName("report_user_venue_win_lose")
@Schema(title = "会员场馆每日盈亏")
public class ReportUserVenueWinLosePO extends BasePO {
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
     * 账号类型
     */
    private Integer accountType;
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

    /**
     * 视讯游戏id
     */
    private String roomType;

    /**
     * 打赏金额
     */
    private BigDecimal tipsAmount;

    /**
     * 用户输赢
     */
    private BigDecimal userWinLossAmount;

}
