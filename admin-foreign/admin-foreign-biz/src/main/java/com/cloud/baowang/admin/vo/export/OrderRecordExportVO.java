package com.cloud.baowang.admin.vo.export;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@I18nClass
@ExcelIgnoreUnannotated
@Schema(title = "注单详情导出VO")
public class OrderRecordExportVO {
    @Schema(title = "父单")
    @ExcelProperty("父单")
    @ColumnWidth(20)
    private String transactionId;

    @Schema(title = "注单ID")
    @ExcelProperty("注单号")
    @ColumnWidth(20)
    private String orderId;
    @Schema(title = "三方订单号")
    @ExcelProperty("三方订单号")
    @ColumnWidth(20)
    private String thirdOrderId;
    @Schema(title = "三方平台code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(title = "三方平台名字")
    @ExcelProperty("游戏场馆" + "\n" + "游戏类别")
    @ColumnWidth(20)
    private String venueCodeText;
    @Schema(title = "游戏code")
    private String gameCode;
    @Schema(title = "游戏类别")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;
    @Schema(title = "游戏类别名称")
    private String venueTypeText;
    @Schema(title = "局号/期号")
    @ExcelProperty("赛事id/局号")
    @ColumnWidth(10)
    private String gameNo;
    @Schema(title = "站点编号")
    @ExcelProperty("站点编号")
    @ColumnWidth(30)
    private String siteCode;
    @Schema(title = "站点名称")
    @ExcelProperty("站点名称")
    @ColumnWidth(30)
    private String siteName;
    @Schema(title = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(10)
    private String userAccount;
    @Schema(title = "三方会员账号/游戏账号")
    @ExcelProperty("游戏账号")
    @ColumnWidth(10)
    private String casinoUserName;
    @ExcelProperty("上级代理账号")
    @Schema(title = "上级代理账号")
    @ColumnWidth(10)
    private String agentAcct;
    @Schema(title = "上级代理ID")
    @ExcelProperty("上级代理ID")
    @ColumnWidth(15)
    private String agentId;
    @Schema(title = "账号类型 1测试 2正式 3商务 4置换")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;
    @ExcelProperty("账号类型")
    @ColumnWidth(10)
    @Schema(title = "账号类型名称")
    private String accountTypeText;
    @Schema(title = "VIP段位")
    private Integer vipRank;
    @ExcelProperty("VIP段位")
    @I18nField
    @Schema(title = "VIP段位-文本")
    private String vipRankText;
    @Schema(title = "VIP等级")
    private Integer vipGradeCode;
    @ExcelProperty("VIP等级")
    @Schema(title = "VIP等级-文本")
    private String vipGradeText;
    @Schema(title = "游戏名称")
    @ExcelProperty("游戏名称")
    @ColumnWidth(10)
    private String gameName;
    @Schema(title = "玩法(导出)")
    private String playInfo;
    @Schema(title = "注单详情(导出)")
    @ExcelProperty("注单详情")
    @ColumnWidth(60)
    private String orderInfo;
    @Schema(title = "币种")
    @ExcelProperty("币种")
    @ColumnWidth(10)
    private String currency;
    @Schema(title = "注单状态(1:已结算,2:未结算)")
    private Integer orderStatus;
    @Schema(title = "投注额")
    @ExcelProperty("投注金额")
    @ColumnWidth(10)
    private BigDecimal betAmount;
    @Schema(title = "输赢金额")
    @ExcelProperty("会员输赢")
    @ColumnWidth(10)
    private BigDecimal winLossAmount;
    @Schema(title = "有效投注")
    @ExcelProperty("有效投注")
    @ColumnWidth(10)
    private BigDecimal validAmount;
    @Schema(title = "注单归类")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(title = "注单状态名称")
    @ExcelProperty("注单状态")
    @ColumnWidth(10)
    private String orderClassifyText;
    @Schema(title = "变更状态")
    private Integer changeStatus;
    @Schema(title = "投注时间")
    private Long betTime;
    @Schema(title = "投注时间(导出需要)")
    @ExcelProperty("投注时间")
    @ColumnWidth(15)
    private String betTimeStr;
    @Schema(title = "首次结算时间")
    private Long firstSettleTime;
    @Schema(title = "首次结算时间(导出需要)")
    //@ExcelProperty("结算时间")
    //@ColumnWidth(15)
    private String firstSettleTimeStr;
    @Schema(title = "结算时间")
    private Long settleTime;
    @Schema(title = "结算时间(导出需要)")
    @ExcelProperty("结算时间")
    @ColumnWidth(15)
    private String settleTimeStr;
    @Schema(title = "玩法项")
    private String playType;
    @Schema(title = "投注内容")
    private String betContent;
    @Schema(title = "开奖内容")
    private String resultList;
    @Schema(title = "同步时间")
    private Long updatedTime;
    @Schema(title = "同步时间")
    @ExcelProperty("同步时间")
    @ColumnWidth(15)
    private String updatedTimeStr;
    @Schema(title = "创建时间")
    private Long createdTime;
    @Schema(title = "创建时间")
    private String createdTimeStr;
    @Schema(title = "投注IP")
    @ExcelProperty("投注IP")
    @ColumnWidth(10)
    private String betIp;
    @Schema(title = "投注终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TERMINAL)
    private Integer deviceType;
    @Schema(title = "投注终端名称")
    @ExcelProperty("投注终端")
    @ColumnWidth(10)
    private String deviceTypeText;
    @Schema(title = "桌台号")
    private String deskNo;
    @Schema(title = "玩法赔率")
    private String odds;
    @Schema(title = "房间类型")
    private String roomType;
    @Schema(title = "房间类型名称")
    private String roomTypeName;

    public String getBetTimeStr() {
        return null == betTime ?
                null : StrUtil.isNotBlank(CurrReqUtils.getTimezone()) ?
                TimeZoneUtils.formatTimestampToTimeZone(betTime, CurrReqUtils.getTimezone()) : DateUtils.formatUTC5Date(betTime, DatePattern.NORM_DATETIME_PATTERN);
    }

    public String getSettleTimeStr() {
        return null == settleTime ?
                null : StrUtil.isNotBlank(CurrReqUtils.getTimezone()) ?
                TimeZoneUtils.formatTimestampToTimeZone(settleTime, CurrReqUtils.getTimezone()) : DateUtils.formatUTC5Date(settleTime, DatePattern.NORM_DATETIME_PATTERN);
    }

    public String getFirstSettleTimeStr() {
        return null == firstSettleTime ?
                null : StrUtil.isNotBlank(CurrReqUtils.getTimezone()) ?
                TimeZoneUtils.formatTimestampToTimeZone(firstSettleTime, CurrReqUtils.getTimezone()) : DateUtils.formatUTC5Date(firstSettleTime, DatePattern.NORM_DATETIME_PATTERN);
    }

    public String getCreatedTimeStr() {
        return null == createdTime ?
                null : StrUtil.isNotBlank(CurrReqUtils.getTimezone()) ?
                TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone()) : DateUtils.formatUTC5Date(createdTime, DatePattern.NORM_DATETIME_PATTERN);
    }


    public String getUpdatedTimeStr() {
        return null == updatedTime ?
                null : StrUtil.isNotBlank(CurrReqUtils.getTimezone()) ?
                TimeZoneUtils.formatTimestampToTimeZone(updatedTime, CurrReqUtils.getTimezone()) : DateUtils.formatUTC5Date(updatedTime, DatePattern.NORM_DATETIME_PATTERN);
    }


    public String getVenueCodeText() {
        return venueCodeText + "\n" + venueTypeText;
    }
}
