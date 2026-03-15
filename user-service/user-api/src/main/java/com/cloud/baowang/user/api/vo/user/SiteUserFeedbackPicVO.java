package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "意见反馈提交图片")
public class SiteUserFeedbackPicVO implements Serializable {

    @Schema(description = "图片url")
    private String picUrl;

}
