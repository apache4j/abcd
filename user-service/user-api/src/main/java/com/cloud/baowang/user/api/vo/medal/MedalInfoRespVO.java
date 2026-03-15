package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
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
@I18nClass
public class MedalInfoRespVO {
    @Schema(description = "Id")
    private String id;


    /**
     * 语言代码
     */
    @Schema(description = "语言代码")
    private String languageCode;


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
     * 解锁条件名称
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
     * 达成条件1 标签名
     */
    @Schema(description = "达成条件1 标签名")
    private String condLabel1;


    /**
     * 达成条件2 标签名
     */
    @Schema(description = "达成条件2 标签名")
    private String condLabel2;

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
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    @Schema(description = "状态 0:禁用 1:启用")
    private String statusText;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "修改人")
    private String updater;

    @Schema(description = "修改时间")
    private Long updatedTime;
}
