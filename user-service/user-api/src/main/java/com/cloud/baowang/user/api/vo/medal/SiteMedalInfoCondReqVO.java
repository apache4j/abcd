package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章查询条件")
public class SiteMedalInfoCondReqVO {
    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

    /**
     * 勋章代码
     * {@link MedalCodeEnum}
     */
    @Schema(description = "勋章代码")
    private String medalCode;


}
