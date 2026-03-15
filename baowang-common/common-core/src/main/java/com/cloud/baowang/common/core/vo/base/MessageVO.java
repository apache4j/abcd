package com.cloud.baowang.common.core.vo.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/11/01 16:01
 * @description:
 */
@Schema(description = "通知消息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {
    @Schema(description = "标题")
    private String title;

    @Schema(description = "消息")
    private String message;
}
