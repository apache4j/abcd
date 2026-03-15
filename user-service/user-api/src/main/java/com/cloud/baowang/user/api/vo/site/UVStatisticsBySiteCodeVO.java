package com.cloud.baowang.user.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "流量UV 按天统计 VO")
public class UVStatisticsBySiteCodeVO {

    @Schema(description = "日期")
    private String siteCode;

    @Schema(description = "ip")
    private String ip ;

    @Schema(description = "userId")
    private String userId ;
}
