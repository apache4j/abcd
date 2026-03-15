package com.cloud.baowang.agent.api.vo.agentImage;

import com.cloud.baowang.common.core.constants.ConstantsCode;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor

/**
 * 图片管理VO
 */
public class AgentImagePageQueryVO extends PageVO implements Serializable {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "图片")
    @NotEmpty(message = ConstantsCode.MAX_LENGTH)
    @Length(max = 50, message = ConstantsCode.MISSING_PARAMETERS)
    private String imageName;

    @Schema(description = "图片尺寸")
    private String imageSize;

    @Schema(description = "图片地址")
    private String imageUrl;
    /**
     * {@link com.cloud.baowang.agent.api.enums.AgentImageTypeEnum}
     */
    @Schema(description = "图片类型: 1=综合、2=体育、3=真人、4=电竞、5=彩票、6=棋牌、7=活动")
    private Integer imageType;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图片状态: 0.停用、1.启用")
    private Integer status;

    @Schema(description = "图片备注")
    private String remark;
    @Schema(hidden = true, description = "创建人")
    private String creator;
    @Schema(hidden = true, description = "修改人")
    private String updater;

}
