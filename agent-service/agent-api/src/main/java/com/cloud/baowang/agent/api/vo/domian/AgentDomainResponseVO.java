package com.cloud.baowang.agent.api.vo.domian;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Schema(title = "域名管理")
@I18nClass
public class AgentDomainResponseVO extends BaseVO implements Serializable {

    /**
     * 域名
     */
    @Schema(description = "域名")
    private String domainName;

    /**
     * 域名描述
     */
    @Schema(description = "域名描述")
    private String domainDescription;

    /**
     * 防微信短连接
     */
    @Schema(description = "防微信短连接")
    private String wechatAddress;

    /**
     * 防QQ短连接
     */
    @Schema(description = "防QQ短连接")
    private String qqAddress;

    /**
     * 域名类型: 1=PC、2=H5、3=APP、4=专属 system_param site_domain_type code
     */
    @Schema(description = "域名类型: 1=代理后台、2=网页端 system_param site_domain_type code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_DOMAIN_TYPE)
    private Integer domainType;

    @Schema(description = "类型名称")
    private String domainTypeText;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 域名备注
     */
    @Schema(description = "域名备注")
    private String remark;

    @Schema(description = "合营代码")
    private String inviteCode;

    @Schema(description = "短码链接")
    private String shortUrl;

    @Schema(description = "短链接访问量")
    private Long shortUrlVisitCount;

    @Schema(description = "长链接访问量")
    private Long longUrlVisitCount;


}
