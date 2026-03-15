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
@Schema(title = "域名列表返回对象")
@I18nClass
public class DomainVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "域名地址")
    private String domainAddr;

    @Schema(description = "使用站点")
    private String siteCode;

    @Schema(description = "使用站点的名称（如果有）")
    private String siteName;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_DOMAIN_TYPE)
    @Schema(description = "域名类型;1-代理端,2-H5端,3-app端,4-后端")
    private Integer domainType;

    @Schema(description = "域名类型名称")
    private String domainTypeText;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "更新人")
    private String updater;

    @Schema(description = "当前域名是否已经绑定过站点0.否,1.是")
    private Integer isSiteUsed;
}
