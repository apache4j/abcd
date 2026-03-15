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
@Schema(description = "站点充值通道批量保存 SiteRechargeChannelBatchReqVO")
public class SiteRechargeChannelBatchReqVO {

    @Schema(description = "操作人 ")
    private String operatorUserNo;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "充值通道列表")
    private List<SiteRechargeChannelSingleNewReqVO> siteRechargeChannelSingleNewReqVOList;

}
