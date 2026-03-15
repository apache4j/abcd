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
@Schema(title = "会员详情编辑信息下拉框返回对象")
public class UserInfoDwonResVO {
    @Schema(title = "会员标签id")
    private Long memberLabelId;
    @Schema(title = "会员标签name")
    private String memberLabelName;
    @Schema(title = "风控层级id")
    private Long riskLevelId;
    @Schema(title = "风控层级name")
    private String riskLevelName;
}
