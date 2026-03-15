package com.cloud.baowang.user.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理客户端-新注册人数 按天统计 VO")
public class GetRegisterStatisticsByAgentIdVO {

    @Schema(description = "日期")
    private String myDay;

    @Schema(description = "新注册人数总和")
    private BigDecimal registerNumber;
}
