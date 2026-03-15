package com.cloud.baowang.user.api.vo.userlabel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 会员标签配置
 *
 * @author 阿虹
 * @since 2023-05-04 10:00:00
 */

@Data
@Accessors(chain = true)
@Schema(description = "会员标签配置")
public class GetUserLabelByIdsVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "labelId")
    private String labelId;

    @Schema(description = "标签名称")
    private String labelName;

    @Schema(description = "标签描述")
    private String labelDescribe;

    @Schema(description = "color")
    private String color;
    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     */
    @Schema(description = "是否是定制的标签0.否,1.是(系统默认自带,不可编辑修改)")
    private Integer customizeStatus;

}
