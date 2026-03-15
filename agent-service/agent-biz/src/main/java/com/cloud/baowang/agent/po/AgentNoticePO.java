package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("agent_notice")
@Schema(description = "代理通知表")
public class AgentNoticePO extends BasePO implements Serializable {

    /**
     * 通知Id
     */
    /*private Long noticeId;*/
    @Schema(description = "通知类型(1=公告、2=活动、3=通知)")
    private Integer noticeType;

    @Schema(description = "通知标题")
    private String noticeTitle;

    @Schema(description = "通知消息内容")
    private String messageContent;

    @Schema(description = "发送对象(1=全部会员、2=特定会员、3=终端)")
    private Integer targetType;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "阅读状态: 0=未读、1=已读")
    private Integer readState;

    @Schema(description = "删除状态: 1=删除、2=正常")
    private Integer deleteState;



    @Schema(description = "创建者的账号")
    private String createName;

    @Schema(description = "修改者的账号")
    private String updateName;



}
