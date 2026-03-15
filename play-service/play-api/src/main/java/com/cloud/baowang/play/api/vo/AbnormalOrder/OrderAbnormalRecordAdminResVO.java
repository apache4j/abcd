package com.cloud.baowang.play.api.vo.AbnormalOrder;

import com.cloud.baowang.common.core.vo.base.PageVO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
@Schema(title = "异常注单列表查询-站点后台")
public class OrderAbnormalRecordAdminResVO extends PageVO {

    @Schema(title = "注单ID")
    @JsonDeserialize()
    private String orderId;
    @Schema(title = "三方注单ID")
    private String thirdOrderId;
    @Schema(description = "游戏类别")
    private List<Integer> venueTypeList;
    @Schema(title = "会员账号")
    private String userAccount;
    @Schema(title = "账号类型")
    private List<Integer> accountType;
    @Schema(title = "VIP段位")
    private List<Integer> vipRankList;
    @Schema(title = "VIP等级")
    private List<Integer> vipGradeList;
    @Schema(title = "币种")
    private String currency;
    @Schema(title = "游戏账号")
    private String casinoUserName;
    @Schema(title = "游戏场馆")
    private List<String> venueCode;
    @Schema(title = "投注IP")
    private String betIp;
    @Schema(title = "游戏名称")
    private String gameName;
    @Schema(title = "局号/赛事id")
    private String gameNo;
    @Schema(title = "上级代理账号")
    private String agentAcct;
    @Schema(title = "注单状态 多个")
    private List<Integer> orderStatusList;
    @Schema(title = "变更状态 ")
    private String changeStatusList;
    @Schema(title = "最小投注额")
    private BigDecimal betAmountMin;
    @Schema(title = "最大投注额")
    private BigDecimal betAmountMax;
    @Schema(title = "最小输赢金额")
    private BigDecimal winLossAmountMin;
    @Schema(title = "最大输赢金额")
    private BigDecimal winLossAmountMax;
    @Schema(title = "下注开始时间")
    private Long betBeginTime;
    @Schema(title = "下注结束时间")
    private Long betEndTime;
    @Schema(title = "结算开始时间")
    private Long settleBeginTime;
    @Schema(title = "结算结束时间")
    private Long settleEndTime;

    @Schema(description = "站点编码", hidden = true)
    private String siteCode;
}
