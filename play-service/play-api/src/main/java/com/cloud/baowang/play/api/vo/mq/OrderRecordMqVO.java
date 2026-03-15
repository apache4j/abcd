package com.cloud.baowang.play.api.vo.mq;

import com.cloud.baowang.common.core.annotations.KafkaPartitionClass;
import com.cloud.baowang.common.core.annotations.KafkaPartitionField;
import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "注单消息实体")
@KafkaPartitionClass
public class OrderRecordMqVO extends MessageBaseVO {

    @Schema(title = "id")
    private String id;
    @Schema(title = "上级代理id")
    private String agentId;
    @Schema(title = "上级代理账号")
    private String agentAcct;
    @Schema(title = "批次号")
    private String batchNo;
    @Schema(title = "会员账号")
    private String userAccount;
    @Schema(title = "会员姓名")
    private String userName;
    // @see com.maya.baowang.enums.user.UserTypeEnum
    @Schema(title = "账号类型 1测试 2正式")
    private Integer accountType;
    @Schema(title = "三方会员账号")
    private String casinoUserName;
    @Schema(title = "上级代理账号")
    private String superAgentName;
    @Schema(title = "VIP等级")
    private Integer vipRankCode;
    @Schema(title = "三方平台")
    private String venuePlatform;
    @Schema(title = "游戏平台名称")
    private String venueName;

    @Schema(title = "游戏平台CODE",requiredMode = Schema.RequiredMode.REQUIRED)
    private String venueCode;

    @Schema(title = "venueType")
    private String venueType;


    @Schema(title = "游戏大类")
    private String gameType;
    @Schema(title = "游戏id")
    private String gameId;
    @Schema(title = "三方游戏code")
    private String thirdGameCode;
    @Schema(title = "游戏名称")
    private String gameName;
    @Schema(title = "游戏Code")
    private String gameCode;
    @Schema(title = "玩法类型")
    private String playType;
    @Schema(title = "房间类型")
    private String roomType;
    @Schema(title = "房间类型名称")
    private String roomTypeName;
    @Schema(title = "投注时间")
    private Long betTime;
    @Schema(title = "结算时间")
    private Long settleTime;
    @Schema(title = "首次结算时间")
    private Long firstSettleTime;
    @Schema(title = "重结算时间")
    private Long reSettleTime;
    @Schema(title = "投注额")
    private BigDecimal betAmount;
    @Schema(title = "有效投注")
    private BigDecimal validAmount;
    @Schema(title = "派彩金额")
    private BigDecimal payoutAmount;
    @Schema(title = "输赢金额")
    private BigDecimal winLossAmount;
    @Schema(title = "注单ID")
    private String orderId;
    @Schema(title = "三方注单ID")
    @KafkaPartitionField
    private String thirdOrderId;
    @Schema(title = "注单状态(1:已结算,0:未结算)")
    private Integer orderStatus;
    @Schema(title = "注单归类")
    private Integer orderClassify;
    @Schema(title = "局号/期号")
    private String gameNo;
    @Schema(title = "桌号")
    private String deskNo;
    @Schema(title = "靴号")
    private String bootNo;
    @Schema(title = "结果牌 /结果")
    private String resultList;
    @Schema(title = "下注内容")
    private String betContent;
    @Schema(title = "返水比例")
    private BigDecimal rebateRate;
    @Schema(title = "返水金额")
    private BigDecimal rebateAmount;
    @Schema(title = "变更状态")
    private Integer changeStatus;
    @Schema(title = "变更时间")
    private Long changeTime;
    @Schema(title = "联赛名称")
    private String leagueName;
    @Schema(title = "客队名称")
    private String awayName;
    @Schema(title = "主队名称")
    private String homeName;
    @Schema(title = "赛事时间")
    private Long matchTime;
    @Schema(title = "赔率")
    private String odds;
    @Schema(title = "投注类型(1:滚球,2:单一,3:混合过关)")
    private Integer betOrderType;
    @Schema(title = "投注IP")
    private String betIp;
    @Schema(title = "币种")
    private String currency;
    @Schema(title = "设备类型")
    private Integer deviceType;
    @Schema(title = "投注详细信息")
    private String parlayInfo;
    @Schema(title = "备注")
    private String remark;
    @Schema(title = "创建时间")
    private Long createdTime;
    @Schema(title = "更新时间")
    private Long updatedTime;

    @Schema(title = "结果产生时间(重结算后结算时间不会变化的场景, 使用该字段来判断是否发生重结算)")
    private Long resultTime;

    @Schema(title = "新变更时间，有重结算和撤销等异常时变更，初始值为落库时间")
    private Long latestTime;

    @Schema(title = "注单详情")
    private String orderInfo;

    @Schema(title = "玩法")
    private String playInfo;

    @Schema(description = "赛事信息")
    private String eventInfo;

    @Schema(description = "是否是免费游戏注单")
    private boolean freeGame;
}
