package com.cloud.baowang.play.api.vo.order;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@I18nClass
@Schema(description = "后台管理注单列表返回分页对象")
public class OrderRecordPageRespVO implements Serializable {
    @Schema(description = "注单ID")
    private String orderId;
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(description = "站点code 取这个")
    private String siteCode_$_siteName;
    @Schema(description = "站点名称")
    private String siteName;
    @Schema(description = "三方注单ID")
    private String thirdOrderId;
    @Schema(description = "三方场馆code")
    private String venueCode;
    @Schema(description = "三方场馆-文本")
    private String venueCodeText;

    @Schema(title = "游戏平台名称")
    private String venuePlatformName;

    @Schema(description = "游戏名称")
    private String gameName;
    @Schema(description = "游戏类别")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;
    @Schema(description = "游戏类别-文本")
    private String venueTypeText;
    @Schema(description = "局号/期号")
    private String gameNo;
    @Schema(description = "会员账号")
    private String userAccount;
    @Schema(description = "账号类型 1测试 2正式 3商务 4置换")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;
    @Schema(description = "账号类型名称")
    private String accountTypeText;
    @Schema(description = "VIP段位")
    private Integer vipRank;
    @Schema(description = "VIP段位-文本")
    @I18nField
    private String vipRankText;
    @Schema(description = "VIP等级")
    private Integer vipGradeCode;
    @Schema(description = "VIP等级-文本")
    private String vipGradeText;

    @Schema(description = "国内盘-VIP等级-文本")
    private String zhVipGradeText;


    @Schema(description = "上级代理id")
    private String agentId;
    @Schema(description = "上级代理账号")
    private String agentAcct;
    @Schema(description = "三方会员账号/游戏账号")
    private String casinoUserName;
    @Schema(description = "注单状态(1:已结算,2:未结算)")
    private Integer orderStatus;
    @Schema(description = "投注额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betAmount;
    @Schema(description = "有效投注")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validAmount;
    @Schema(description = "输赢金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winLossAmount;
    @Schema(description = "变更时间")
    private Long changeTime;
    @Schema(description = "结算时间")
    private Long settleTime;
    @Schema(description = "投注时间")
    private Long betTime;
    @Schema(description = "首次结算时间")
    private Long firstSettleTime;
    @Schema(description = "投注IP")
    private String betIp;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "投注终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TERMINAL)
    private Integer deviceType;
    @Schema(description = "投注终端名称")
    private String deviceTypeText;
    @Schema(description = "玩法项")
    private String playType;
    @Schema(description = "投注内容")
    private String betContent;
    @Schema(description = "开奖内容")
    private String resultList;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "注单归类")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(description = "注单归类-文本")
    private String orderClassifyText;
    @Schema(description = "桌台号")
    private String deskNo;
    @Schema(description = "玩法赔率")
    private String odds;
    @Schema(description = "房间类型")
    private String roomType;
    @Schema(description = "房间类型名称")
    private String roomTypeName;
    @Schema(description = "同步时间")
    private Long updatedTime;
    @Schema(description = "最新变更时间")
    private Long latestTime;
    @Schema(description = "注单详情")
    private String orderInfo;
    @Schema(description = "注单详情 数组展示")
    private List<String> orderInfoList;
    @Schema(description = "玩法")
    private String playInfo;
    @Schema(description = "玩法-数组展示")
    private List<String> playInfoList;
    @Schema(description = "赛事信息")
    private String eventInfo;
    @Schema(description = "赛事信息-数组展示")
    private List<String> eventInfoList;

    @Schema(description = "税费")
    private BigDecimal lightningAmount;

    @Schema(description = "投注本金")
    private BigDecimal totalAmount;

    /**
     * 转账ID
     */
    @Schema(description = "转账ID")
    private String transactionId;


}
