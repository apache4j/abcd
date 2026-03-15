package com.cloud.baowang.activity.api.vo.category;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 站点-活动分类视图
 *
 * @author aomiao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点-活动页签基础配置")
@I18nClass
public class SiteActivityLabsVO implements Serializable {
    @Schema(description = "id")
    private String id;

    @Schema(description = "活动页签名称-分页用")
    @I18nField
    private String labNameI18Code;

    @Schema(description = "页签名称-详情用")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String labName;

    @Schema(description = "页签名称-多语言集合")
    private List<I18nMsgFrontVO> labNameList;
    /**
     * 备注
     */
    /*@Schema(description = "备注")
    private String remark;*/
    /**
     * 状态，0.禁用，1.启用
     * {@link com.cloud.baowang.common.core.enums.EnableStatusEnum}
     */
    @Schema(description = "0.禁用，1.启用", hidden = true)
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    @Schema(description = "状态名称")
    private String statusText;
    @Schema(description = "排序")
    private Integer sort;
}
