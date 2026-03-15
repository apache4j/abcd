package com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sheldon
 * @Date: 3/29/24 6:14 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息多语言配置")
public class LanguageNoticeVO {

    /**
     * 多语言名称
     */
    private String id;

    /**
     * 多语言名称
     */
    @Schema(description = "名称", required = true)
    private String name;

    /**
     * 语言
     */
    @Schema(description = "语言-language", required = true)
    private String code;


}
