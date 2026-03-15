package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "站点-日期-币种-人数对应查询vo")
@Data
public class SiteUserDateQueryVO {
    @Schema(description = "utc-5的日期字符串")
    private String dateStr;
    @Schema(description = "站点")
    private String siteCode;
    @Schema(description = "币种")
    private String mainCurrency;
    @Schema(description = "注册会员人数")
    private Long userCount;
}
