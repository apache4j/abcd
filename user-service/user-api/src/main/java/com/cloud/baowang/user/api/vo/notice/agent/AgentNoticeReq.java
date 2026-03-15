package com.cloud.baowang.user.api.vo.notice.agent;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@Schema(description = "代理通知")
public class AgentNoticeReq implements Serializable {
    /**
     * 通知Id
     */
    private String noticeId;
    @Schema(title = "通知类型(1=公告、2=活动、3=通知)")
    private Integer noticeType;

    @Schema(title = "通知标题")
    private String noticeTitle;

    @Schema(title = "通知消息内容")
    private String messageContent;

    @Schema(title = "发送对象(1=全部会员、2=特定会员、3=终端)")
    private Integer targetType;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "代理账号Id")
    private String agentAccountId;

    @Schema(title = "阅读状态: 0=未读、1=已读")
    private Integer readState;

    @Schema(title = "删除状态: 1=删除、2=正常")
    private Integer deleteState;


    @Schema(title = "创建者的账号")
    private String createName;

    @Schema(title = "修改者的账号")
    private String updateName;

    /**
     * 撤销状态，1=正常、2=撤销
     */
    private Integer revokeState;


    private String creator;


    private Long createdTime;

    private String updater;

    private Long updatedTime;


}
