package com.cloud.baowang.user.api.vo.UserDetails;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "用户检查标签对象")
public class CheckUserReqVO {
    @Schema(title = "用户")
    private String users;

    @Schema(title = "站点code", hidden = true)
    private String siteCode;
}
