package com.cloud.baowang.wallet.api.vo.userwallet;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/16 10:01
 * @Version: V1.0
 **/
@Data
@Schema(title = "平台币兑换查询参数")
public class UserPlatformTransferCondReqVO extends SitePageVO {
    @Schema(description = "开始时间")
    private Long beginTime;
    @Schema(description = "结束时间")
    private Long endTime;
    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "会员账号")
    private String userAccount;
    @Schema(description = "转换币种")
    private String targetCurrencyCode;

}
