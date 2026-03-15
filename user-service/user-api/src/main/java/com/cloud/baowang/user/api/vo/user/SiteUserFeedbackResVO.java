package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "意见反馈提交")
public class SiteUserFeedbackResVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "会员层级Id，多条根据','拼接")
    private String userLabel;

    private String userLabelText;

    @Schema(description = "会员vip")
    private String vipLevel;

    @Schema(description = "意见内容")
    private String content;

    @Schema(description = "回复内容")
    private String backContent;

    @Schema(description = "回复人")
    private String backAccount;

    @Schema(description = "回复时间")
    private Long backTime;

    @Schema(description = "截图")
    private String picUrls;
}
