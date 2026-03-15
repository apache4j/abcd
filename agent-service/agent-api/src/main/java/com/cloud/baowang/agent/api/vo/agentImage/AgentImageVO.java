package com.cloud.baowang.agent.api.vo.agentImage;

import com.cloud.baowang.agent.api.enums.AgentImageTypeEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
@Schema(description = "推广素材视图")
@I18nClass
public class AgentImageVO extends PageVO implements Serializable {
    @Schema(hidden = true)
    private String siteCode;
    @Schema(description = "Id")
    private String id;

    @Schema(description = "图片")
    @NotEmpty(message = ConstantsCode.MAX_LENGTH)
    @Length(max = 50, message = ConstantsCode.MISSING_PARAMETERS)
    private String imageName;

    @Schema(description = "图片尺寸")
    private String imageSize;

    @Schema(description = "图片地址")
    private String imageUrl;

    @Schema(description = "图片完整地址")
    private String imageUrlFull;
    /**
     * {@link AgentImageTypeEnum}
     */
    @Schema(description = "图片类型: 1=综合、2=体育、3=真人、4=电竞、5=彩票、6=棋牌、7=活动")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_IMAGE_TYPE)
    private Integer imageType;

    @Schema(description = "图片类型")
    private String imageTypeText;


    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图片状态: 0.停用、1.启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_IMAGE_TYPE)
    private Integer status;

    @Schema(description = "状态")
    private String statusText;

    @Schema(description = "合营代码")
    private String inviteCode;

    @Schema(description = "图片备注")
    private String remark;

    @Schema(hidden = true, description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(hidden = true, description = "修改人")
    private String updater;

    @Schema(description = "修改时间")
    private Long updatedTime;

    @Schema(description = "当前登陆代理-代理端使用", hidden = true)
    private String agentAccount;
    @Schema(description = "域名")
    private String domainName;

}
