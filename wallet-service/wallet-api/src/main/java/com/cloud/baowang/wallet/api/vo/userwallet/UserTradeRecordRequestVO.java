package com.cloud.baowang.wallet.api.vo.userwallet;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "会员交易记录请求对象")
public class UserTradeRecordRequestVO extends PageVO {


    @Schema(description = "交易类型 全部类型传空 1存款 2取款 3平台币兑换")
    private String tradeType;

    @Schema(description = "交易状态 0处理中 1成功 2失败")
    private String tradeStatus;

    /*@Schema(description = "时间范围(-1:昨天,-7:近7天,-15:近15天 -30:近30天)")
    private Integer dateNum;*/

    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;

    private String userId;



}
