package com.cloud.baowang.user.api.vo.notice.user.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "批量删除VO")
public class NoticeUpdateVO  implements Serializable {

    /**
     * 读取消息，系统消息4都是通知，且对于客户端进行查询
     */
    @Schema(title ="通知类型(1=公告/通知、2=活动)")
    @NotNull(message = "通知类型不能为空")
    private Integer noticeType;

    @Schema(title ="targetId数组")
    private List<String> ids;

    @Schema(title = "用户id", hidden = true)
    private String userId;


}
