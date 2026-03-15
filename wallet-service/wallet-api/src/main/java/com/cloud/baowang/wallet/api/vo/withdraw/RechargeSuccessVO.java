package com.cloud.baowang.wallet.api.vo.withdraw;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeSuccessVO {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     *客户端状态 0 处理中 1成功 2失败
     */
    private String customerStatus;

    /**
     * 更新时间
     */
    private Long updatedTime;


}
