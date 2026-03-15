package com.cloud.baowang.user.api.vo.user.reponse;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Schema(description = "全局设置返回对象")
@I18nClass
public class UserGlobalSetResVO implements Serializable {

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "区号")
    private String areaCode;

    @Schema(description = "手机号码, 空表示为绑定")
    private String phone;

    @Schema(description = "邮箱， 空表示为绑定")
    private String email;

    @Schema(description = "是否已交易密码 false 未设置 true 已设置")
    private Boolean isSetPwd;
}
