package com.cloud.baowang.user.api.vo.notice.user.request;


import com.cloud.baowang.common.core.vo.base.PageVO;
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
@Schema(description = "一键已读通知配置入参")
public class UserNoticeReqVO extends PageVO implements Serializable {

    /**
     * 读取消息，系统消息4都是通知，且对于客户端进行查询
     */
    @Schema(title ="通知类型(1=公告/通知、2=活动)")
    @NotNull(message = "通知类型不能为空")
    private Integer noticeType;

    @Schema(title = "用户账号", hidden = true)
    private String userAccount;

    @Schema(title = "用户id", hidden = true)
    private String userId;

    @Schema(title = "终端设备")
    private String deviceTerminal;

   /* @Schema(title =value = "会员消息默认是1,不传默认配置1")
    private Integer platform = CommonConstant.business_one;*/
   @Schema(title = "当前时间戳",hidden = true)
   private Long currentTime;

    @Schema(title = "是否删除 0-删除 1-已读")
    private int isDelete;



}
