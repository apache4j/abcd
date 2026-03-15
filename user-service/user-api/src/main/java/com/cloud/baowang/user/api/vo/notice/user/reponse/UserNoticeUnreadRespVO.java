package com.cloud.baowang.user.api.vo.notice.user.reponse;


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
public class UserNoticeUnreadRespVO implements Serializable {
    /**
     * 1=公告、2=活动、3=通知、4=系统消息，系统消息属于通知，故数据库存储是4，但是显示给前端是通知3
     */
    @Schema(title = "通知类型(1=公告、2=活动")
    private String noticeType;

    @Schema(title = "未读通知的数量")
    private Integer unreadCount;


}
