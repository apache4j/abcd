package com.cloud.baowang.common.core.vo.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
/**
 * @author jianguo
 */
@Builder
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台-ids请求对象")
public class IdSVO {
    @Schema(description = "ids")
    @NotNull(message = "ids不能为null")
    @NotEmpty(message = "ids不能为空数组")
    private String[] ids;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization = false;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    public IdSVO(String[] ids) {
        this.ids = ids;
    }
}

