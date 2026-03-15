package com.cloud.baowang.play.api.vo.AbnormalOrder;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "异常注单信息", description = "异常注单信息")
public class OrderAbnormalDetailVO implements Serializable {
    @Schema(description = "站点编码")
    private String siteCode;
    @Schema(description = "序号，0：原始注单信息 1：第一次变更，以此类推")
    private Integer sequenceNo;
    @Schema(description = "id")
    private String id;
    @Schema(description = "会员账号")
    private String userAccount;
    @Schema(description = "三方会员账号")
    private String casinoUserName;
    @Schema(description = "异常类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ABNORMAL_TYPE)
    private Integer abnormalType;
    @Schema(description = "异常类型名称")
    private String abnormalTypeText;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description = "游戏平台")
    private String venueCodeText;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;
    @Schema(description = "游戏类别-文本")
    private String venueTypeText;
    @Schema(description = "游戏名称/比赛类型")
    private String gameName;
    @Schema(description = "玩法类型")
    private String playType;
    @Schema(description = "房间类型名称")
    private String roomTypeName;
    @Schema(description = "投注时间")
    private Long betTime;
    @Schema(description = "首次结算时间")
    private Long firstSettleTime;
    @Schema(description = "结算时间")
    private Long settleTime;
    @Schema(description = "投注额")
    private BigDecimal betAmount;
    @Schema(description = "有效投注")
    private BigDecimal validAmount;
    @Schema(description = "派彩金额")
    private BigDecimal payoutAmount;
    @Schema(description = "输赢金额")
    private BigDecimal winLossAmount;
    @Schema(description = "注单ID")
    private String orderId;
    @Schema(description = "三方注单ID")
    private String thirdOrderId;
    @Schema(description = "注单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderStatus;
    @Schema(description = "注单状态名称")
    private String orderStatusText;
    @Schema(description = "注单归类")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(description = "注单归类名称")
    private String orderClassifyText;
    @Schema(description = "处理状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PROCESS_STATUS)
    private Integer processStatus;
    @Schema(description = "处理状态名称")
    private String processStatusText;
    @Schema(description = "局号/期号/赛事ID")
    private String gameNo;
    @Schema(description = "桌号")
    private String deskNo;
    @Schema(description = "局结果")
    private String resultList;
    @Schema(description = "下注内容")
    private String betContent;
    @Schema(description = "返水比例")
    private BigDecimal rebateRate;
    @Schema(description = "返水金额")
    private BigDecimal rebateAmount;
    @Schema(description = "变更状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CHANGE_STATUS)
    private Integer changeStatus;
    @Schema(description = "变更状态名称")
    private String changeStatusText;
    @Schema(description = "变更次数")
    private Integer changeCount;
    @Schema(description = "变更时间")
    private Long changeTime;
    @Schema(description = "联赛名称")
    private String leagueName;
    @Schema(description = "客队名称")
    private String awayName;
    @Schema(description = "队名称")
    private String homeName;
    @Schema(description = "赛事时间")
    private Long matchTime;
    @Schema(description = "赔率")
    private String odds;
    @Schema(description = "投注类型(1:滚球,2:单一,3:混合过关)")
    private Integer betOrderType;
    @Schema(description = "投注IP")
    private String betIp;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "设备类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TERMINAL)
    private Integer deviceType;
    @Schema(description = "设备类型名称")
    private String deviceTypeText;
    @Schema(description = "注单详情--投注玩法")
    private String orderInfo;
    @Schema(description = "串关信息")
    private String parlayInfo;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "同步时间")
    private Long updatedTime;

    public Integer getOrderStatus() {
        return orderClassify;
    }
}
