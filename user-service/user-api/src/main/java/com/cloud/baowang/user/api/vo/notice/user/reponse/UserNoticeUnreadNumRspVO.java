package com.cloud.baowang.user.api.vo.notice.user.reponse;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "未读消息数量VO")
public class UserNoticeUnreadNumRspVO implements Serializable {


    @Schema(title = "通知未读消息总数")
    private Integer unreadNoticeNum ;


    @Schema(title = "活动未读消息总数")
    private Integer unreadActivityNum ;


}
