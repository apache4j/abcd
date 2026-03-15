package com.cloud.baowang.system.api.vo.area;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@I18nClass
@Schema(description = "手机区号管理返回对象")
public class AreaAdminManageVO extends BaseVO {
    @Schema(description = "区号ID")
    private String areaId;
    @Schema(description = "区号")
    private String areaCode;
    @Schema(description = "国家名称")
    private String countryName;
    @Schema(description = "国家简写")
    private String countryCode;
    @Schema(title = "最大长度")
    private Integer maxLength;
    @Schema(title = "最小长度")
    private Integer minLength;
    @Schema(description = "状态  0 禁用  1 启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    @Schema(description = "状态名称")
    private String statusText;
    @Schema(description = "图标地址")
    private String icon;
    @Schema(description = "图标地址完整路径-展示用")
    private String iconImage;

    @Schema(description = "国家名称列表")
    private List<AreaNameVO> nameList;
}
