package com.cloud.baowang.user.api.vo.notice.agent;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@Schema(description = "批量保存代理通知数据")
public class SaveAgentNoticeReq implements Serializable {

    /**
     *
     */
    @Schema(title = "批量插入参数")
    private List<AgentNoticeReq> agentNoticeReqList;


}
