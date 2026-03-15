package com.cloud.baowang.agent.api.vo.domian;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
@Schema(title = "代理推广链接视图")
public class AgentDomainVO implements Serializable {

    @Schema(description = "Id")
    private String id;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "域名")
    private String domainName;
    @Schema(description = "域名类型")
    private Integer domainType;

    /**
     * 域名描述
     */
    @Schema(description = "域名描述")
    @NotEmpty(message = "域名描述不能为空")
    @Size(max = 50, message = "域名描述最大范围50个字符")
    private String domainDescription;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 域名备注
     */
    @Schema(description = "域名备注")
    private String remark;

    @Schema(hidden = true)
    private String creator;

    @Schema(hidden = true)
    private String updater;

}
