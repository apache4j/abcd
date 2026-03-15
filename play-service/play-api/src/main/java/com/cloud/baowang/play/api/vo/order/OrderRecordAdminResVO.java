package com.cloud.baowang.play.api.vo.order;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@Schema(description = "注单列表查询-中控后台")
public class OrderRecordAdminResVO extends PageVO {

    @Schema(description = "注单ID")
    private String orderId;
    @Schema(description = "站点CODE")
    private String siteCode;
    @Schema(description = "站点名称")
    private String siteName;
    @Schema(description = "游戏类别")
    private List<Integer> venueTypeList;
    @Schema(description = "游戏ID")
    private String gameId;
    @Schema(description = "三方注单ID")
    private String thirdOrderId;
    @Schema(description = "会员账号")
    private String userAccount;
    @Schema(description = "账号类型 1测试 2正式")
    private List<Integer> accountType;
    @Schema(description = "三方会员账号/游戏账号")
    private String casinoUserName;
    @Schema(description = "三方平台游戏平台")
    private List<String> venueCode;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "投注IP")
    private String betIp;
    @Schema(description = "游戏名称")
    private String gameName;
    @Schema(description = "局号/期号")
    private String gameNo;
    @Schema(description = "上级代理id")
    private String agentId;
    @Schema(description = "上级代理账号")
    private String agentAcct;
    @Schema(description = "注单状态多个")
    private List<Integer> orderStatusList;
    @Schema(description = "变更状态")
    private Integer changeStatus;
    @Schema(description = "设备类型")
    private List<Integer> deviceType;
    @Schema(description = "最小投注额")
    private BigDecimal betAmountMin;
    @Schema(description = "最大投注额")
    private BigDecimal betAmountMax;
    @Schema(description = "最小有效投注额")
    private BigDecimal validAmountMin;
    @Schema(description = "最大有效投注额")
    private BigDecimal validAmountMax;
    @Schema(description = "最小输赢金额")
    private BigDecimal winLossAmountMin;
    @Schema(description = "最大输赢金额")
    private BigDecimal winLossAmountMax;
    @Schema(description = "VIP等级")
    private List<Integer> vipGradeList;
    @Schema(description = "VIP段位")
    private List<Integer> vipRankList;
    @Schema(description = "下注开始时间")
    private Long betBeginTime;
    @Schema(description = "下注结束时间")
    private Long betEndTime;
    @Schema(description = "结算开始时间")
    private Long settleBeginTime;
    @Schema(description = "结算结束时间")
    private Long settleEndTime;
    @Schema(description = "首次结算开始时间")
    private Long firstSettleBeginTime;
    @Schema(description = "首次结算结束时间")
    private Long firstSettleEndTime;
    @Schema(description = "是否导出 true 是 false 否")
    private Boolean exportFlag = false;
    @Schema(description = "当前请求站点code")
    private String curSiteCode;
    @Schema(description = "游戏名称总计赛事信息", hidden = true)
    private String eventInfo;
    @Schema(description = "游戏名称总计搜索sql参数", hidden = true)
    private List<String> gameNameList;
    @Schema(description = "游戏名称总计 真人游戏名称 大类搜索sql参数", hidden = true)
    private List<String> roomTypeList;
    @Schema(description = "游戏名称总计 真人游戏名称 三方游戏编号搜索sql参数", hidden = true)
    private List<String> thirdGameCodeList;

    @Schema(description = "父ID")
    private String transactionId;

    /**
     * 串关信息
     */
    @Schema(description = "免费旋转注单参数", hidden = true)
    private String freeRoundGameOrderNo;

    public static boolean checkTime(final Long startTime, final Long endTime) {
        if (ObjectUtil.isEmpty(startTime) || ObjectUtil.isEmpty(endTime)) {
            return true;
        }
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        if (startDate.after(endDate)) {
            return true;
        }
        int maxDays = 90;
        DateTime dateTime = DateUtil.offsetDay(startDate, maxDays);
        int num = DateUtil.compare(endDate, dateTime);
        return num > 0;
    }

    public static void main(String[] args) {
        checkTime(1730390400000L,1734278399999L);
    }
}
