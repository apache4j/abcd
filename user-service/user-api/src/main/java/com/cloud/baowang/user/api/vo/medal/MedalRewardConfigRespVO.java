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
@Schema(description = "勋章奖励配置")
@I18nClass
public class MedalRewardConfigRespVO {
    @Schema(description = "Id")
    private String id;

    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

    /**
     * 激励编号  固定值:1,2,3,4,5
     */
    @Schema(description = "激励编号 固定值:1,2,3,4,5")
    private Integer rewardNo;

    /**
     * 解锁勋章数
     */
    @Schema(description = "解锁勋章数")
    private Integer unlockMedalNum;

    /**
     * 奖励金额
     */
    @Schema(description = "奖励金额")
    private BigDecimal rewardAmount;

    @Schema(description = "平台币简称")
    private String platformCurrency;


    /**
     * 打码倍数
     */
    @Schema(description = "打码倍数")
    private BigDecimal typingMultiple;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    @Schema(description = "状态描述 0:禁用 1:启用")
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
