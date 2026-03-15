package com.cloud.baowang.play.game.db.acelt.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AceltOrderVO {
    private Long orderId;           // 注单id
    private Long memberId;          // 玩家id
    private String memberAccount;   // 玩家帐号
    private Integer seriesId;       // 彩系id
    private String seriesName;      // 彩系名称
    private Integer ticketId;       // 彩种id
    private String ticketName;      // 彩种名称
    private String ticketPlanNo;    // 奖期号
    private String ticketResult;    // 开奖结果
    private Integer groupMode;      // 盘面 - 1：标准盘；2：双面盘； 3：特色盘。
    private Long playLevelId;       // 玩法群id
    private String playLevel;       // 玩法群名称
    private Long playId;            // 玩法id
    private String playName;        // 玩法名称
    private Long playItemId;        // 投注项id
    private String betNum;          // 投注内容编码
    private String betContent;      // 投注内容翻译
    private BigDecimal betMoney;    // 投注金额
    private BigDecimal winAmount;   // 中奖金额
    private BigDecimal profitAmount;// 盈利金额
    private Integer currencyType;   // 玩家结算币种类型
    private Integer betNums;        // 投注注数
    private Integer winNums;        // 中奖注数
    private Integer betStatus;      // 投注状态     - 1：待开奖；2：未中奖；3：已中奖；4：挂起；5：已结算。
    private String betTime;  // 投注时间
    private Integer betMultiple;    // 投注倍数
    private BigDecimal betModel;    // 投注模式
    private BigDecimal betRebate;   // 投注返点
    private String betPrize;        // 奖金 (JSON 字符串)
    private String odd;             // 赔率 (JSON 字符串)
    private Integer riskStatus;     // 风控状态     - 1：待风控；2：风控通过；3：风控锁定；4：风控解锁。
    private String riskUnlockBy;    // 风控解锁人
    private String riskUnlockAt; // 风控解锁时间
    private Boolean chaseOrder;     // 是否为追号单 - true：追号单；false：普通注单。
    private Long chaseId;           // 追号单id
    private Long chasePlanId;       // 追号排期id
    private Boolean cancelStatus;   // 是否已撤单    -true：已撤单；false：未撤单。
    private Integer cancelType;     // 撤单类型      - 1：个人撤单；2：系统撤单；3：中奖停追撤单；4：不中停追撤单
    private String cancelBy;        // 注单撤销人
    private String cancelAt; // 注单撤销时间
    private String cancelDesc;      // 注单撤销说明
    private String updateAt; // 注单最后更新时间
    private Long merchantId;        // 商户id
    private String merchantAccount; // 商户名称
    private Boolean tester;         // 玩家帐号是否为测试帐号
    private Boolean directlyMode;   // 是否为直客结算模式
    private String baseRate;        // 标准盘返奖率
    private String bonusReduceRate; // 返奖率下调幅度
    private BigDecimal memberRebate;// 玩家返点值
    private Boolean singleGame;     // 是否为单式玩法      - true：单式玩法；false：非单式玩法。
    private Boolean solo;           // 是否为单挑        - true：单挑；false：非单挑。
    private Integer device;         // 投注终端         - 1：web；2：IOS；3：Android；4：H5。
    private Boolean seriesType;     // 彩系类型         - true：特色彩；false：传统彩。
    private Integer orderSource;    // 注单来源
    private String orderIp;         // 投注IP
    private String timezone;        // 时区
    private Integer hide;           // 是否藏单
}
