package com.cloud.baowang.common.core.vo.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/01 15:54
 * @description: 系统通知消息
 */
@Schema(description = "系统通知消息参数")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageNotifyVO {
    @Schema(title = "siteCode")
    private String siteCode;

    @Schema(title = "会员id集合")
    private List<String> userIds;

    @Schema(title = "主题")
    private String msgTopic;

    @Schema(title = "消息")
    private MessageVO messageVO;


    @Schema(description = "标题code")
    private String titleI18nCode;

    @Schema(description = "消息code")
    private String messageI18nCode;

    private String titleConvertValue;

    private String contentConvertValue;

    private String systemMessageCode;
}
