package com.cloud.baowang.activity.api.vo.free;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 免费旋转次数消费记录
 */
@Data
@NoArgsConstructor
@Schema(title = "免费旋转次数消费记录 Resp")
public class SiteActivityFreeGameConsumeResp {

    /**
     * 主键ID
     */
    @Schema(title = "主键ID")
    private Long id;

    /**
     * 会员ID
     */
    @Schema(title = "会员ID")
    private String userId;

    /**
     * 会员账号
     */
    @Schema(title = "会员账号")
    private String userAccount;

    /**
     * 当前次数余额
     */
    @Schema(title = "当前次数余额")
    private Integer balance;

    /**
     * 消耗次数
     */
    @Schema(title = "消耗次数")
    private Integer consumeCount;

    /**
     * 平台编号
     */
    @Schema(title = "平台编号")
    private String venueCode;

    /**
     * 游戏ID
     */
    @Schema(title = "游戏ID")
    private String gameId;

    /**
     * 投注盈亏
     */
    @Schema(title = "投注盈亏")
    private BigDecimal betWinLose;

    /**
     * 获取来源订单号
     */
    @Schema(title = "获取来源订单号")
    private String orderNo;

    /**
     * 投注单号
     */
    @Schema(title = "投注单号")
    private String betId;

    /**
     * 创建时间
     */
    @Schema(title = "创建时间")
    private Long createdTime;
}

