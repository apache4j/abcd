package com.cloud.baowang.user.api.vo.notice.agent;


import com.cloud.baowang.common.core.constants.CommonConstant;
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
@Schema(description = "代理通知阅读VO(包括删除)")
public class AgentNoticeSetReadStateReqVO implements Serializable {


//
//    @Schema(title ="通知noticeId")
//    private String noticeId;
    @Schema(title ="通知targetId")
    private String targetId;
    @Schema(title ="操作状态: 1=已读取、2=删除")
    private Integer operatorStatus = CommonConstant.business_one;


    @Schema(title = "登录账号", hidden = true)
    private String agentAccount;

    @Schema(title = "登录账号", hidden = true)
    private String agentId;


}
