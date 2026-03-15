package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

import static com.cloud.baowang.common.core.constants.I18nFieldTypeConstants.DICT;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@Data
@Schema(description = "站点提款通道响应")
@I18nClass
public class SiteWithdrawChannelResponseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;



    @Schema(description = "提款通道编号")
    private String withdrawChannelNo;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 通道类型
     */
    @Schema(description = "通道类型")
    @I18nField(type = DICT,value = CommonConstant.CHANNEL_TYPE)
    private String channelType;

    @Schema(description = "通道类型名称")
    private String channelTypeText;

    @Schema(description = "提款方式Id")
    private String withdrawWayId;

    @Schema(description = "提款方式多语言")
    @I18nField
    private String withdrawWayI18;

    private String channelId;

    /**
     * 通道代码
     */
    @Schema(description = "通道代码")
    private String channelCode;

    /**
     * 通道名称
     */
    @Schema(description = "通道名称")
    private String channelName;


    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;


    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    @I18nField(type = DICT,value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;

    private String statusText;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Long createdTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updater;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private Long updatedTime;
}
