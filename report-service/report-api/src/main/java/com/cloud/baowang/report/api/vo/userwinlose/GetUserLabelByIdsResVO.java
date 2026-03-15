package com.cloud.baowang.report.api.vo.userwinlose;

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
//@Accessors(chain = true)
@Schema(description = "会员标签配置返回-update")
public class GetUserLabelByIdsResVO {

    @Schema(description = "标签名称")
    private String labelName;


    @Schema(description = "color")
    private String color;


}
