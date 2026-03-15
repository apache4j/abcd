package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@Data
@Schema(description = "站点提现方式批量保存")
public class SiteWithdrawChannelBatchRequestVO {

    @Schema(description = "操作人 ")
    private String operatorUserNo;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "提现方式列表")
    private List<SiteWithdrawChannelSingleNewRequestVO> siteWithdrawChannelSingleNewRequestVOS;

}
