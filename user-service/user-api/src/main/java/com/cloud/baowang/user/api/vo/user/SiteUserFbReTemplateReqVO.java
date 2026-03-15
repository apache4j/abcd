package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "意见反馈回复模板提交")
public class SiteUserFbReTemplateReqVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "内容")
    private String content;


}
