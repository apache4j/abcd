package com.cloud.baowang.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * @author: kimi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Schema(title = "银行卡管理/虚拟币账号管理-解绑 Request")
public class UnbindVO {

    @Schema(description = "id")
    @NotNull(message = "id不能为空")
    private String id;

    @Schema(description = "备注信息")
    @NotEmpty(message = "备注信息不能为空")
    @Size(min = 2, max = 50, message = "备注信息在2-50个字符之间")
    private String remark;

    @Schema(description = "将该银行卡变更为黑名单禁用状态 0未选中 1选中")
    @NotNull(message = "缺少参数isChosen")
    private Integer isChosen;
}
