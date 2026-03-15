package com.cloud.baowang.activity.api.vo.free;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @className: FreeGameRecordReqVO
 * @author: wade
 * @description: 免费旋转
 * @date: 7/6/25 10:36
 */
@Schema(description = "免费旋转查找消费记录，请求入参")
@Data
public class FreeGameConsumerReqVO extends PageVO implements Serializable {



    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "站点代码" ,hidden = true)
    private String siteCode;



    @Schema(description = "获取来源订单号 唯一值 做防重处理")
    private String orderNo;
    @Schema(description = "投注单号")
    private String betId;
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;

}
