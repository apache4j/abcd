package com.cloud.baowang.play.api.vo.betCoinJoin;

import com.baomidou.mybatisplus.annotation.TableName;
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
public class BetCoinJoinVO {

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

}
