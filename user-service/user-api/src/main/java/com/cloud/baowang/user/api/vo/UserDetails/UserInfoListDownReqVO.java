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
@Schema(title = "会员列表编辑信息下拉框")
public class UserInfoListDownReqVO  {

    @Schema(title = "站点code")
    private String siteCode;
}
