package com.cloud.baowang.user.api.vo.medal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/6 11:23
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "勋章解锁")
public class MedalAcquireReqVO {
    /**
     * 站点代码
     */
    @Schema(description = "站点代码",hidden = true)
    private String siteCode;

    /**
     * 勋章代码
     */
    @Schema(description = "勋章代码")
    @NotNull(message = "勋章代码不能为空!")
    private String medalCode;

    /**
     * 用户编号
     */
    @Schema(description = "用户账号",hidden = true)
    private String userAccount;


    @Schema(description = "用户Id",hidden = true)
    private String userId;



}
