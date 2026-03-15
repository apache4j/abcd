package com.cloud.baowang.activity.api.vo.category;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 站点-活动分类视图
 *
 * @author aomiao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点-活动页签分页列表查询对象")
@I18nClass
public class SiteActivityLabPageQueryVO extends PageVO implements Serializable {
    @Schema(description = "站点code",hidden = true)
    private String siteCode;
    @Schema(description = "页签名称")
    private String labNameI18Code;
    /**
     * 状态，0.禁用，1.启用
     */
    @Schema(description = "0.禁用，1.启用,同system_param enable_disable_status code值")
    private Integer status;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "最近操作人")
    private String recentOperator;


}
