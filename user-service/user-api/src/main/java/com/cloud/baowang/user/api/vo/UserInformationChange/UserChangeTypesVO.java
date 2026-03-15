package com.cloud.baowang.user.api.vo.UserInformationChange;

import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.user.api.vo.user.UserTypeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
@Data
@Schema(title = "变更类型下拉")
public class UserChangeTypesVO {
    @Schema(title = "变更类型")
    private List<CodeValueVO> changeType;

    @Schema(title = "账号类型")
    private List<UserTypeVO> accountType;

}
