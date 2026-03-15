package com.cloud.baowang.play.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("user_wallet_game_record")
public class UserWalletGameRecordPO {

    /**
     * 交易Id
     */
    private String transactionId;

    /**
     * 注单号
     */
    private String betNum;

    /**
     * 场馆CODE
     */
    private String venueCode;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 类型 1:投注 2:派彩 3:撤单 4:活动
     */
    private Integer eventType;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 更新前余额
     */
    private BigDecimal beforeBalance;

    /**
     * 更新后余额
     */
    private BigDecimal afterBalance;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 年月,取值为当月1日
     */
    private Date monthYear;

    /**
     * 更新版本，默认0,每次更新会加
     */
    private Integer version;

}
