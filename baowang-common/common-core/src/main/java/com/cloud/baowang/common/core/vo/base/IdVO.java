package com.cloud.baowang.common.core.vo.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author kimi
 */
@Builder
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台-id请求对象")
public class IdVO {

    @Schema(description = "id")
    @NotEmpty(message = "id不能为空")
    private String id;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization = false;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    public IdVO(String id) {
        this.id = id;
    }
}
