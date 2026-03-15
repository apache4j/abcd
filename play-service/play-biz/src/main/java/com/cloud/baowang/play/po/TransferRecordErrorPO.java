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
@TableName("transfer_record_error")
@Schema(title = "异常场馆转账记录")
public class TransferRecordErrorPO extends BasePO {

    //场馆CODE
    private String venueCode;

    //查询开始时间
    private String startTime;

    //请求跟踪ID
    private String transId;

    //订单ID
    private String orderId;

    private String operationId;

    //下注ID
    private String betId;

    //用户账号
    private String userAccount;

    private String detail;

    private String api;



}
