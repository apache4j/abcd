package com.cloud.baowang.user.api.vo.medal;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章信息")
public class MedalUserRespVO {

    @Schema(description = "medalId")
    private String medalId;

    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    /**
     * 勋章名称
     */
    @Schema(description = "勋章名称")
    private String medalName;

    /**
     * 解锁条件
     */
    @Schema(description = "解锁条件")
    private String unlockCond;


    /**
     * 奖励金额
     */
    @Schema(description = "奖励金额")
    @JsonFormat(pattern = "0.00")
    private BigDecimal rewardAmount;

    /**
     * 打码倍数
     */
    @Schema(description = "打码倍数")
    @JsonFormat(pattern = "0.00")
    private BigDecimal typingMultiple;

    /**
     * 达成条件 N
     */
    @Schema(description = "达成条件 N")
    private Integer condNum;

    /**
     * 解锁条件说明
     */
    @Schema(description = "解锁条件说明")
    private String medalDesc;

    /**
     * 激活图片
     */
    @Schema(description = "激活图片")
    private String activatedPic;

    @Schema(description = "激活图片完整路径")
    private String activatedPicUrl;

    /**
     * 未激活图片
     */
    @Schema(description = "未激活图片")
    private String inactivatedPic;

    @Schema(description = "未激活图片完整路径")
    private String inactivatedPicUrl;


    /**
     * 达成条件时间
     */
    @Schema(description = "达成条件时间")
    private Long completeTime;

    /**
     * 解锁时间
     */
    @Schema(description = "解锁时间")
    private Long unlockTime;

    @Schema(description = "解锁状态")
    private Long lockStatus;


}
