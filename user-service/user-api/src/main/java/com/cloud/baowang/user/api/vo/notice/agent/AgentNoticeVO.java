package com.cloud.baowang.user.api.vo.notice.agent;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理通知VO")
public class AgentNoticeVO extends PageVO implements Serializable {


    @Schema(title ="通知类型 赋值: 1(公告) 3=通知+系统消息")
    private Integer noticeType;


    /*@Schema(title =value = "终端设备", hidden = true)
    private String deviceTerminal;*/

    @Schema(title ="登陆者", hidden = true)
    private String userAccount;

    @Schema(title ="登陆者", hidden = true)
    private String userId;

    @Schema(title = "终端设备")
    private String deviceTerminal;

    @Schema(title = "当前时间戳",hidden = true)
    private Long currentTime;

    @Schema(title ="站点", hidden = true)
    private String siteCode;

    private Integer platform;







}
