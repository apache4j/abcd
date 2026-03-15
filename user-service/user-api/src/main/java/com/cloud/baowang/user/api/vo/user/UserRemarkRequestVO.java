package com.cloud.baowang.user.api.vo.user;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 11/5/23 7:45 PM
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户被注意修改入参数")
public class UserRemarkRequestVO implements Serializable {


    @Schema(description = "站点编号", hidden = true)
    private String siteCode;


    @Schema(description = "id")
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    private String id;


    @Schema(description = "备注")
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    @NotEmpty(message = ConstantsCode.MAX_LENGTH)
    @Length(min = 1, max = 50, message = ConstantsCode.MAX_LENGTH)
    private String remark;

    @Schema(description = "修改人", hidden = true)
    private String operator;


}
