package com.cloud.baowang.agent.api.vo.domian;

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
@Schema(name = "域名管理BO")
@I18nClass
public class AgentDomainBO implements Serializable {

    private String id;



    /**
     * 域名
     */
    @Schema(description="域名")
    private String domainName;

    /**
     * 域名描述
     */
    @Schema(description="域名描述")
    private String domainDescription;

    /**
     * 防微信短连接
     */
    @Schema(description="防微信短连接")
    private String wechatAddress;

    /**
     * 防QQ短连接
     */
    @Schema(description="防QQ短连接")
    private String qqAddress;

    /**
     * 域名类型: 1=PC、2=H5、3=APP、4=专属
     */
    @Schema(description="域名类型: 1=PC、2=H5、3=APP、4=专属")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_DOMAIN_TYPE)
    private Integer domainType;

    @Schema(description="域名类型: 1=PC、2=H5、3=APP、4=专属")
    private String domainTypeText;

    /**
     * 排序
     */
    @Schema(description="排序")
    private Integer orderNumber;

    /**
     * 域名状态: 1=停用、2=启用
     */
    @Schema(description="域名状态: 1=停用、2=启用")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer domainState;
    @Schema(description="域名状态: 1=停用、2=启用")
    private String domainStateText;

    /**
     * 删除状态: 1=删除、2=正常
     */
    @Schema(description="删除状态: 1=删除、2=正常")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer deleteState;
    @Schema(description="删除状态: 1=删除、2=正常")
    private String deleteStateText;

    /**
     * 域名备注
     */
    @Schema(description="域名备注")
    private String remark;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "修改人")
    private String updater;
    @Schema(description = "修改时间")
    private Long updatedTime;


    @Schema(description = "合营代码")
    private String inviteCode;

    @Schema(description = "短码链接")
    private String shortUrl;

    @Schema(description = "短链接访问量")
    private Long shortUrlVisitCount;

    @Schema(description = "长链接访问量")
    private Long longUrlVisitCount;


}
