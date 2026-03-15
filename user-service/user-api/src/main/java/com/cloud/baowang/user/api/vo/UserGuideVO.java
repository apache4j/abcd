package com.cloud.baowang.user.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "新手指引步骤入参数")
public class UserGuideVO implements Serializable {
    @Schema(description = "账号", hidden = true)
    private String userAccount;

    @Schema(description = "userID", hidden = true)
    private String userId;

    @Schema(description = "新手指引步骤")
    private Integer step;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;


}
