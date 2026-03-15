package com.cloud.baowang.system.api.vo.member;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * @author qiqi
 */
@Data
@Schema(description = "角色列表分页请求对象")
@I18nClass
public class BusinessRolePageVO implements Serializable {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "使用数量")
    private Integer useNums;

    @Schema(description = "创建时间")
    private Long createdTime;

    private String creator;

    @Schema(description = "创建人")
    private String creatorName;

    /**
     * 状态 0 正常 1禁用
     */
    @Schema(description = "状态 0禁用 1启用 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS )
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;

}
