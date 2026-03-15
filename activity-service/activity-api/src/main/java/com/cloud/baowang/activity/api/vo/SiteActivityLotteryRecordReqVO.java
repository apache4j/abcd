package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: SiteActivityLotteryRecordRespVO
 * @author: wade
 * @description: 转盘活动抽奖记录查询入参
 * @date: 10/9/24 21:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "转盘活动抽奖记录查询入参")
public class SiteActivityLotteryRecordReqVO extends PageVO implements Serializable {
    /**
     * 站点code
     */
    @Schema(description = "站点编码", hidden = true)
    private String siteCode;

    /**
     * 会员账号
     */
    @Schema(description = "会员账号")
    private String userAccount;

    /**
     * 获取来源 system-param(activity_prize_source)
     */
    @Schema(description = "获取来源 1-存款赠送 2-流水赠送 system-param(activity_prize_source) ")
    private String prizeSource;

    /**
     * VIP等级code
     */
    @Schema(description = "VIP等级编码")
    private Integer vipGradeCode;


    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Long receiveTimeStart;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private Long receiveTimeEnd;




}
