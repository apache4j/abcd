package com.cloud.baowang.user.api.vo.notice.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "未读通知配置的响应对象")
public class AgentNoticeUnreadRespVO implements Serializable {

    @Schema(title ="通知类型(1=公告、2=消息)")
    private Integer noticeType;

    @Schema(title ="未读通知的数量")
    private Integer unreadCount;


}
