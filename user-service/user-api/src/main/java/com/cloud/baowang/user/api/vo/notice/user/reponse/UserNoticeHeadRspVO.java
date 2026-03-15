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
@Schema(description = "首页公告响应对象")
public class UserNoticeHeadRspVO implements Serializable {

    @Schema(title = "通知列表数据")
    private List<UserNoticeRespVO> userNoticeList;

//    @Schema(title = "未读消息总数")
//    private Integer unreadTotal;

}
