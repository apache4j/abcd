package com.cloud.baowang.agent.api.vo.domian;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "域名访问量统计")
public class AgentDomainVisitVO implements Serializable {

    @Schema(description="域名地址")
    @NotBlank(message = "域名地址不能为空")
    private String domainUrl;

    @Schema(description="域名类型: 1=PC、2=H5、3=APP、4=专属")
    @NotNull
    private Integer domainType;

    @Schema(description = "合营代码")
    @NotBlank(message = "域名地址不能为空")
    private String inviteCode;
}
