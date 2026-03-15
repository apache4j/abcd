package com.cloud.baowang.agent.api.vo.withdraw;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="代理提款信息搜集VO")
public class AgentWithdrawCollectInfoVO {

    @Schema(description = "code字段")
    private String filedCode;

    @Schema(description = "校验标志")
    private Boolean checkFlag;


}
