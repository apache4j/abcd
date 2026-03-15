package com.cloud.baowang.agent.api.vo.agent.clienthome;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(description = "数据对比 曲线图 Param")
public class DataCompareGraphParam {
    @Schema(description = "币种")
    @NotNull(message = "币种不能为空")
    private String currencyCode;
    @Schema(description = "所选月份的结束时间戳，如果为空，后端会单独设置")
    private Long time;
    @Schema(description = "数据类型 1存款金额 2取款金额 3总输赢 4新注册人数 5首存人数")
    @NotNull(message = "数据类型不能为空")
    private Integer type;
    @Schema(description = "当前代理id",hidden = true)
    private String currentId;
    @Schema(description = "当前代理账号",hidden = true)
    private String currentAgent;
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;
    @Schema(description = "时区编号",hidden = true)
    private String timeZoneId;
}
