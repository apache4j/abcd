package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.user.api.vo.GetUserLabelByIdsResponseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 小智
 * @Date 15/5/23 10:34 AM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员详情用户登录日志信息-总登录次数")
@I18nClass
public class UserLoginInfoCountVO implements Serializable {


    @Schema(description = "登录次数")
    private Long loginNum;
    @Schema(description = "总登录次数")
    private Long logAll;
}
