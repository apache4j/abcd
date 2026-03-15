package com.cloud.baowang.user.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员标签配置
 *
 * @author wade
 * @since 2024-12-19
 */
@Data

@Schema(description = "会员标签配置返回")
public class GetUserLabelByIdsResponseVO {
    @Schema(description = "标签名称")
    private String labelName;
    @Schema(description = "color")
    private String color;
}