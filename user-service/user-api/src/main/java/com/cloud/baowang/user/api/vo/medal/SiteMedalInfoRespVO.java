package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "站点勋章信息")
@I18nClass
public class SiteMedalInfoRespVO {
    @Schema(description = "Id")
    private String id;

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
    @I18nField(type = DICT,value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;

    @Schema(description = "状态多语言")
    private String statusText;
    /**
     * 勋章名称多语言
     */
    @I18nField
    @Schema(description = "勋章名称-多语言CODE")
    private String medalNameI18;

   // @Schema(description = "勋章名称-多语言集合")
    //private String medalNameI18Text;
    /**
     * 勋章描述多语言
     */
    @I18nField
    @Schema(description = "勋章描述-多语言CODE")
    private String medalDescI18;

   // @Schema(description = "勋章描述-多语言集合")
   // private String medalDescI18Text;

    @I18nField
    @Schema(description = "解锁条件-多语言CODE")
    private String unlockCondNameI18;

    /**
     * 达成条件1 标签名 多语言
     */
    @I18nField
    @Schema(description = "达成条件1 标签名 多语言CODE")
    private String condLabel1I18;


    /**
     * 达成条件2 标签名 多语言
     */
    @I18nField
    @Schema(description = "达成条件2 标签名 多语言CODE")
    private String condLabel2I18;


    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "修改人")
    private String updater;

    @Schema(description = "修改时间")
    private Long updatedTime;
    @Schema(description = "平台币代码")
    private String currencyName;
    @Schema(description = "平台币符号")
    private String currencySymbol;


    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;


}
