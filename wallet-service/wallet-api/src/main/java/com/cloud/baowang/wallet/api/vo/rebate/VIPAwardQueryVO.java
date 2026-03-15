package com.cloud.baowang.wallet.api.vo.rebate;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 26/6/23 1:56 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title ="VIP奖励发放记录请求对象")
@Slf4j
public class VIPAwardQueryVO extends PageVO implements Serializable {

    @Schema(title ="订单号")
    private String orderId;

    @Schema(title ="会员账号")
    private String userAccount;

    @Schema(title ="奖励类型")
    private Integer awardType;

    @Schema(title ="领取状态")
    private Integer receiveStatus;

    @Schema(title ="订单生成开始时间")
    private Long createStartTime;

    @Schema(title ="订单生成结束时间")
    private Long createEndTime;

    @Schema(title ="领取开始时间")
    private Long receiveStartTime;

    @Schema(title ="领取结束时间")
    private Long receiveEndTime;
}
