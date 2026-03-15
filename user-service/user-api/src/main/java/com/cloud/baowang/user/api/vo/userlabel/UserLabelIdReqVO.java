package com.cloud.baowang.user.api.vo.userlabel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/11/1 18:30
 * @Version: V1.0
 **/
@Data
@Schema(description = "标签查询条件")
public class UserLabelIdReqVO {
    @Schema(description = "标签ID")
    private String labelIds;
    @Schema(description = "站点编码")
    private String siteCode;
}
