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
@TableName("transfer_record")
@Schema(title = "场馆转账记录")
public class TransferRecordPO extends BasePO {

    //场馆CODE
    private String venueCode;

    //订单ID
    private String orderId;

    //请求跟踪ID
    private String transId;

    //下注ID
    private String betId;

    //用户账号
    private String userAccount;

    //订单状态
    private Integer orderStatus;

    //转账金额
    private BigDecimal amount;

    //备注
    private String remark;

    //重结算次数
    private Integer settleCount;



}
