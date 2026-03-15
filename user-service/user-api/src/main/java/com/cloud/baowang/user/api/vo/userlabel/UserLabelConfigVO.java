package com.cloud.baowang.user.api.vo.userlabel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wade
 */
@Data
@Schema(description = "返回会员的标签userd,labelId  Request")
public class UserLabelConfigVO {

    @Schema(description = "标签ID")
    private String userLabelId;

    @Schema(description = "会员id")
    private String userAccount;
    @Schema(description = "账号类型 1-测试 2-正式")
    private String accountType;
}
