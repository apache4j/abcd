package com.cloud.baowang.user.api.vo.site;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(description = "站点首页to10 IP返回结果")
public class VisitFromResVO implements Serializable {


    /**
     * ip归宿
     */
    @Schema(title = "登录终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TERMINAL)
    private String loginTerminal;

    /**
     * ip归宿
     */
    @Schema(title = "登录终端")
    private String loginTerminalText;

    @Schema(title = "访问数量")
    private Long visitCount;

}
