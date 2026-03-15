package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/30 15:54
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "充值通道返回分页参数")
@I18nClass
public class RechargeChannelResVO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;

    /**
     * 通道类型
     */
    @Schema(description = "通道类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CHANNEL_TYPE)
    private String channelType;

    @Schema(description = "通道类型中文名")
    private String channelTypeText;

    /**
     * 充值方式id
     */
    @Schema(description = "充值方式id")
    private String rechargeWayId;

    /**
     * 充值方式国际化
     */
    @Schema(description = "充值方式国际化")
    @I18nField
    private String rechargeWayI18;

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
     * 充值最小值
     */
    @Schema(description = "充值最小值")
    private BigDecimal rechargeMin;

    /**
     * 充值最大值
     */
    @Schema(description = "充值最大值")
    private BigDecimal rechargeMax;

    /**
     * 使用范围
     */
    @Schema(description = "使用范围")
    private String useScope;

    @Schema(description = "使用范围中文描述")
    private List<CodeValueNoI18VO> useScopeList;


    /**
     * 同类型权重
     */
    @Schema(description = "同类型权重")
    private Integer weight;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;

    @Schema(description = "选中状态(0:未选中,1:选中)")
    private Integer chooseFlag;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private String status;

    @Schema(description = "状态 0:禁用 1:启用")
    private String statusText;

    /**
     * 修改人
     */
    @Schema(description = "操作时间")
    private String updater;

    /**
     * 修改时间
     */
    @Schema(description = "操作人")
    private Long updatedTime;
}
