package com.cloud.baowang.user.api.vo.user.request;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "更新昵称")
public class UserEditNameVO implements Serializable {

    @Schema(description = "昵称")
    @Length(min = 2, max = 16, message = ConstantsCode.USER_NOTICE_OF_TARGET_TYPE_IS_ERROR)
    private String nickName;

    @Schema(description = "用户头像code-从/common/getAvaList中获取到的列表的avatarId")
    private String avatarCode;

    @Schema(description = "用户头像相对路径")
    private String avatar;

}
