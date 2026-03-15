package com.cloud.baowang.agent.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**

 *
 * @author 作者
 * @since 2024-10-31
 */
@Schema(description = "消息配置表")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
public class AgentSystemMessageConfigVO implements Serializable {

    @Schema(description = "消息类型")
    private String messageType;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "标题code")
    private String titleI18nCode;

    @Schema(description = "内容code")
    private String contentI18nCode;

    @Schema(description = "语言")
    private String language;




}
