package com.cloud.baowang.report.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "站点投注人数统计vo")
public class UserWinLoseBetUserVO {
    private String dateStr;
    private String siteCode;
    private String currencyCode;
    private Long betUserCount;
}

