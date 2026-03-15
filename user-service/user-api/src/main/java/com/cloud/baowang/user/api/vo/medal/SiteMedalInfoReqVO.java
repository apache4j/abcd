package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章新增")
public class SiteMedalInfoReqVO extends PageVO {
    /**
     * 站点代码
     */
    @Schema(description = "站点代码",hidden = true)
    private String siteCode;

    /**
     * 勋章代码
     */
    @Schema(description = "勋章代码")
    private String medalCode;

    /**
     * 勋章名称
     */
    @Schema(description = "勋章名称")
    private String medalName;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

}
