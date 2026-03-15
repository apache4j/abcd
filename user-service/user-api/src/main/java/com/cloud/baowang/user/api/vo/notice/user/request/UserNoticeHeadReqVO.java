package com.cloud.baowang.user.api.vo.notice.user.request;


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
@Schema(description = "首页获取公告入参")
public class UserNoticeHeadReqVO implements Serializable {

    @Schema(title = "通知类型(1=公告、3=通知+系统消息)", hidden = true)
    private Integer noticeType;

    @Schema(title = "用户账号", hidden = true)
    private String userAccount;
    @Schema(title = "用户id", hidden = true)
    private String userId;

    @Schema(title = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(title = "终端设备")
    private String deviceTerminal;

    @Schema(title = "站点编号")
    private String siteCode;

    @Schema(title = "当前时间戳",hidden = true)
    private Long currentTime;

}
