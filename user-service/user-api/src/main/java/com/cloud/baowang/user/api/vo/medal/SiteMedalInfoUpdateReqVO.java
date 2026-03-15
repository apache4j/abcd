package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章信息")
public class SiteMedalInfoUpdateReqVO {
    @Schema(description = "Id")
    private String id;
    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

    /**
     * 勋章代码
     */
    @Schema(description = "勋章代码")
    private String medalCode;

    /**
     * 勋章名称
     */
    @Schema(description = "勋章名称")
    private String medalName;

    /**
     * 解锁条件
     */
    @Schema(description = "解锁条件名称")
    private String unlockCondName;


    /**
     * 奖励金额
     */
    @Schema(description = "奖励金额")
    private BigDecimal rewardAmount;

    /**
     * 打码倍数
     */
    @Schema(description = "打码倍数")
    private BigDecimal typingMultiple;

    /**
     * 达成条件1 N
     */
    @Schema(description = "达成条件1 N")
    private String condNum1;


    /**
     * 达成条件2 N
     */
    @Schema(description = "达成条件2 N")
    private String condNum2;
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

    /**
     * 未激活图片
     */
    @Schema(description = "未激活图片")
    private String inactivatedPic;


    /**
     * 勋章名称多语言
     */
    @Schema(description = "勋章名称-多语言CODE")
    private String medalNameI18;

    @Schema(description = "勋章名称-多语言集合")
    private List<I18nMsgFrontVO> medalNameI18List;
    /**
     * 勋章描述多语言
     */
    @Schema(description = "勋章描述-多语言CODE")
    private String medalDescI18;

    @Schema(description = "勋章描述-多语言集合")
    private List<I18nMsgFrontVO> medalDescI18List;

}
