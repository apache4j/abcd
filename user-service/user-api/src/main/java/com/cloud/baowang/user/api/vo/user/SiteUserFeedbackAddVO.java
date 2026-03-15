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
public class SiteUserFeedbackAddVO implements Serializable {

    @Schema(description = "反馈顶层ID")
    private String feedTopId;

    @Schema(description = "问题类型")
    private Integer type;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "订单id")
    private String orderId;

    @Schema(description = "图片: 格式: 'picUrl1,picUrl2,picUrl3'")
    private String picUrls;
}
