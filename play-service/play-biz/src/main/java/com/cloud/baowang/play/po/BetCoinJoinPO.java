package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@FieldNameConstants
@TableName("bet_coin_join")
@Schema(title = "扣费_下注关联关系表")
public class BetCoinJoinPO extends BasePO {

    //场馆CODE
    private String venueCode;

    //订单ID 三方-订单号扣款的唯一ID
    private String orderId;

    //我方扣费 请求跟踪ID
    private String transId;

    //下注ID
    private String betId;

    //用户账号
    private String userAccount;

    //金额
    private BigDecimal amount;



}
