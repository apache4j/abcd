package com.cloud.baowang.user.api.vo.medal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/31 15:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章获取记录查询条件")
public class MedalAcquireRecordNewReqVO {

    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

    /**
     * 会员ID
     */
    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "勋章代码")
    private String medalCode;


}
