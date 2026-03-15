package com.cloud.baowang.user.api.vo.notice.user.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "通知阅读")
public class UserNoticeSetReadStateReqVO implements Serializable {


  /*  @Schema(title = "通知Id")
    @NotNull(message = "通知Id不能为空")
    private String noticeId;*/

    @Schema(title = "会员账号", hidden = true)
    private String userAccount;
    @Schema(title = "用户id", hidden = true)
    private String userId;

    @Schema(title = "消息id")
    private String targetId;

    @Schema(title = "终端设备")
    private String deviceTerminal;

    @Schema(title = "1-已读,2-删除")
    private int status;

}
