package com.cloud.baowang.agent.api.vo.manualup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title ="查询 Request")
public class GetAgentBalanceQueryVO {

    @Schema(title = "代理账号,多个用逗号,或者分号隔开")
    @NotEmpty(message = "代理账号不能为空")
    private List<String> agentAccountList;

    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "钱包类型1.佣金钱包，2.额度钱包-第二步使用")
    private int walletTypeCode;

    /*@Schema(title = "调整金额-第二步使用")
    @Size(max = 11, message = "调整金额最大11位")
    private String adjustAmount;*/

}
