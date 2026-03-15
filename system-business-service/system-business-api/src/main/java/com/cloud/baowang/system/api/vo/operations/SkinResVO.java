package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="查询皮肤列表返回对象")
@I18nClass
public class SkinResVO {

    @Schema(description ="ID")
    private String id;

    @Schema(description ="皮肤编码")
    private String skinCode;

    @Schema(description ="皮肤名称")
    private String skinName;

    @Schema(description ="PC皮肤地址")
    private String pcAddr;

    @Schema(description ="H5皮肤地址")
    private String h5Addr;

    @Schema(description ="备注")
    private String remark;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    @Schema(description = "状态;1-启用,0-禁用")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;

    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "更新人")
    private String updaterName;

}
