package com.cloud.baowang.system.api.vo.version;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "变更记录分页查询VO")
@I18nClass
public class SystemVersionChangeRecordPageQueryVO extends PageVO {

    @Schema(description = "站点编码，标识所属站点")
    private String siteCode;

    @Schema(description = "平台类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VERSION_MOBILE_PLATFORM)
    private Integer deviceTerminal;

    @Schema(description = "最后操作人")
    private String updater;

}
