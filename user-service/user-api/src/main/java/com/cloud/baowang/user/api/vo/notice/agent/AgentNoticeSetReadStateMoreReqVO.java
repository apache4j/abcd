package com.cloud.baowang.user.api.vo.notice.agent;


import com.cloud.baowang.common.core.constants.CommonConstant;
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
@Schema(description = "代理通知批量操作VO(阅读与删除),只需要noticeType和operatorStatus")
public class AgentNoticeSetReadStateMoreReqVO implements Serializable {


    @Schema(title = "通知Id列表")
    private List<AgentNoticeDTO> noticeList;

    @Schema(title = "消息类型(1=公告、3=消息和通知)")
    private Integer noticeType;

    @Schema(title = "操作状态: 1=已读取、2=删除")
    private Integer operatorStatus = CommonConstant.business_one;


    @Schema(title = "登录账号", hidden = true)
    private String agentAccount;

    @Schema(title = "登录账号", hidden = true)
    private String agentId;

    @Schema(title = "是否全部一键读取/删除 0=否 1=是")
    private Integer isAllOperator = CommonConstant.business_zero;


    private Long currentTime;
}
