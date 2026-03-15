package com.cloud.baowang.user.api.vo.userlabel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询所有会员标签
 *
 * @author kimi
 * @since 2023-05-04 10:00:00
 */
@Data
@Schema(description = "查询所有会员标签")
public class GetAllUserLabelVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "标签名称")
    private String labelName;

    @Schema(description = "标签状态 0:非定制，1定制")
    private Integer customizeStatus;

    @Schema(description = "颜色")
    private String color;
}
