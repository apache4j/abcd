package com.cloud.baowang.user.api.vo.userlabel;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author kimi
 */
@Data
@I18nClass
@Schema(description = "标签对应的会员 分页查询")
public class GetUserPageByLabelIdVO {

    @Schema(description = "会员账号")
    private String userAccount;

    @I18nField
    @Schema(description = "账号类型")
    private String accountType;

}
