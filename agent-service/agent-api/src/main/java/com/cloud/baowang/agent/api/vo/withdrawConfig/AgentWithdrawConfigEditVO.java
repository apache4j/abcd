package com.cloud.baowang.agent.api.vo.withdrawConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "代理提款配置 编辑入参")
public class AgentWithdrawConfigEditVO {

    @Schema(title = "id")
    @NotNull(message = "id不能为空")
    private String id;

    @Schema(title = "状态 1开启 0关闭")
    @NotNull(message = "开关状态不能为空")
    private Integer status;


    @Schema(title = "配置详情", description = "配置详情 币种为key-提款方式 -> 1对多")
    private List<AgentWithdrawDetailRspVO> detailTotalList;
}
