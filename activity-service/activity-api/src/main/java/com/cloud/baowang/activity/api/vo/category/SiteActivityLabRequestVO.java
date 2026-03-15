package com.cloud.baowang.activity.api.vo.category;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(title = "站点-活动页签新增/修改实体")
@I18nClass
public class SiteActivityLabRequestVO implements Serializable {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "活动页签i18code-修改用")
    private String labNameI18Code;
    /**
     * 分类名称
     */
    @Schema(description = "页签名称-多语言")
    @NotEmpty(message = "分类名称不能为空")
    private List<I18nMsgFrontVO> labNameI18List;
    /**
     * 所属站点
     */
    @Schema(description = "所属站点", hidden = true)
    private String siteCode;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    /**
     * 状态，0.禁用，1.启用
     */
    @Schema(description = "0.禁用，1.启用")
    private Integer status;

}
