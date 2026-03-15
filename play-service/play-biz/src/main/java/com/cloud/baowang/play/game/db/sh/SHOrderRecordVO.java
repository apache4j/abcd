package com.cloud.baowang.play.game.db.sh;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SHOrderRecordVO {

    private Long id;
    private Long playerId;
    private String playerName;
    private Long agentId;
    private BigDecimal betAmount;
    private BigDecimal validBetAmount;
    private BigDecimal netAmount;
    private BigDecimal beforeAmount;

    private Long createdAt;
    private Long netAt;
    private Long recalcuAt;
    private Long updatedAt;

    private Integer gameTypeId;
    private String gameTypeName;
    private Integer platformId;
    private String platformName;

    private Integer betStatus;
    private Integer betFlag;
    private Integer betPointId;

    private String odds;
    private String judgeResult;
    private String currency;
    private String tableCode;
    private String tableName;
    private String roundNo;
    private String bootNo;

    private String loginIp;
    private Integer deviceType;
    private String deviceId;
    private Integer recordType;
    private Integer gameMode;

    private String signature;
    private BigDecimal payAmount;
    private Long startid;

    private BigDecimal handingFee;
    //实际产生闪电投注
    private BigDecimal actualHandingFee;
    private BigDecimal realDeductAmount;

    private Integer bettingRecordType;
    private Integer roundCount;

    private String addstr1;
    private String addstr2;

    private BigDecimal preDebitAmount;//牛牛-预扣金额


}
