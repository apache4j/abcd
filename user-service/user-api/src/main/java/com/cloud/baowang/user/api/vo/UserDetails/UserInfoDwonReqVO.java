package com.cloud.baowang.user.api.vo.UserDetails;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "会员编辑信息下拉框")
public class UserInfoDwonReqVO extends PageVO {
    @Schema(title = "会员账号")
    private String userAccount;
    @Schema(title = "站点code",hidden = true)
    private String siteCode;
}
