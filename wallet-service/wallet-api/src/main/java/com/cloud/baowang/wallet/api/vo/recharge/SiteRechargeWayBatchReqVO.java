package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:16
 * @Version: V1.0
 **/
@Data
@Schema(description = "站点充值方式批量保存")
public class SiteRechargeWayBatchReqVO {

    @Schema(description = "操作人 ")
    private String operatorUserNo;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "充值方式列表")
    private List<SiteRechargeWaySingleNewReqVO> siteRechargeWaySingleNewReqVOList;

    @Schema(description = "站点模式")
    private Integer handicapMode;

}
