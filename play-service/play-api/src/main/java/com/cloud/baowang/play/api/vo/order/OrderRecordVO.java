package com.cloud.baowang.play.api.vo.order;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "注单返回对象明细")
public class OrderRecordVO implements Serializable {

    private String siteCode;

    private String siteName;

    private String userId;

    @Schema(description = "id")
    private String id;
    @Schema(description = "上级代理id")
    private String agentId;
    @Schema(description = "上级代理账号")
    private String agentAcct;
    @Schema(description = "批次号")
    private String batchNo;
    @Schema(description = "会员账号")
    private String userAccount;
    @Schema(description = "会员姓名")
    private String userName;
    // @see com.maya.baowang.enums.user.UserTypeEnum
    @Schema(description = "账号类型 1测试 2正式 3商务 4置换")
    private Integer accountType;
    @Schema(description = "三方会员账号")
    private String casinoUserName;
    @Schema(description = "上级代理账号")
    private String superAgentName;
    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;
    @Schema(description = "vip段位")
    private Integer vipRank;
    @Schema(description = "三方平台")
    private String venuePlatform;
    @Schema(description = "三方场馆code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description = "三方场馆-文本")
    private String venueCodeText;
    @Schema(description = "游戏大类")
    private Integer venueType;
    @Schema(description = "游戏id")
    private String gameId;
    @Schema(description = "三方游戏code")
    private String thirdGameCode;
    @Schema(description = "游戏名称")
    private String gameName;
    @Schema(description = "游戏Code")
    private String gameCode;
    @Schema(description = "玩法类型")
    private String playType;
    @Schema(description = "房间类型")
    private String roomType;
    @Schema(description = "房间类型名称")
    private String roomTypeName;
    @Schema(description = "投注时间")
    private Long betTime;
    @Schema(description = "结算时间")
    private Long settleTime;
    @Schema(description = "首次结算时间")
    private Long firstSettleTime;
    @Schema(description = "重结算时间")
    private Long reSettleTime;
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
    @Schema(description = "注单状态(1:已结算,0:未结算)")
    private Integer orderStatus;
    @Schema(description = "注单归类")
    private Integer orderClassify;
    @Schema(description = "局号/期号")
    private String gameNo;
    @Schema(description = "桌号")
    private String deskNo;
    @Schema(description = "靴号")
    private String bootNo;
    @Schema(description = "结果牌 /结果")
    private String resultList;
    @Schema(description = "下注内容")
    private String betContent;
    @Schema(description = "返水比例")
    private BigDecimal rebateRate;
    @Schema(description = "返水金额")
    private BigDecimal rebateAmount;
    @Schema(description = "变更状态")
    private Integer changeStatus;
    @Schema(description = "变更时间")
    private Long changeTime;
    @Schema(description = "联赛名称")
    private String leagueName;
    @Schema(description = "客队名称")
    private String awayName;
    @Schema(description = "主队名称")
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
    private Integer deviceType;
    @Schema(description = "串关信息")
    @JsonIgnore
    private String parlayInfo;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "结果产生时间(重结算后结算时间不会变化的场景, 使用该字段来判断是否发生重结算)")
    private Long resultTime;

    @Schema(description = "新变更时间，有重结算和撤销等异常时变更，初始值为落库时间")
    private Long latestTime;

    @Schema(description = "注单详情")
    private String orderInfo;

    @Schema(description = "玩法")
    private String playInfo;

    @Schema(description = "赛事信息")
    private String eventInfo;

    @Schema(description = "是否是免费游戏注单")
    private boolean freeGame;

    /**
     * 备用字段 BetGameTypeEnum.java
     */
    @Schema(description = "备用字段,用作投注类型")
    private String exId1;

    /**
     * 备用字段
     */
    @Schema(description = "备用字段")
    private String exId2;


    @Schema(description = "交易编号")
    private String transactionId;

    public BigDecimal getBetAmount() {
        if (ObjectUtil.isEmpty(betAmount)) {
            betAmount = BigDecimal.valueOf(0);
        }
        return betAmount.setScale(4, RoundingMode.DOWN);
    }

    public BigDecimal getValidAmount() {
        if (ObjectUtil.isEmpty(validAmount)) {
            validAmount = BigDecimal.valueOf(0);
        }
        return validAmount.setScale(4, RoundingMode.DOWN);
    }

    public BigDecimal getWinLossAmount() {
        if (ObjectUtil.isEmpty(winLossAmount)) {
            winLossAmount = BigDecimal.valueOf(0);
        }
        return winLossAmount.setScale(4, RoundingMode.DOWN);
    }

    public BigDecimal getRebateAmount() {
        if (ObjectUtil.isEmpty(rebateAmount)) {
            rebateAmount  = BigDecimal.valueOf(0);
        }
        return new BigDecimal(rebateAmount.toPlainString()).setScale(6, RoundingMode.DOWN);
    }
    
    public BigDecimal getPayoutAmount() {
        return ObjectUtil.isEmpty(this.payoutAmount) ? BigDecimal.ZERO : this.payoutAmount.setScale(4, RoundingMode.DOWN);
    }
}
