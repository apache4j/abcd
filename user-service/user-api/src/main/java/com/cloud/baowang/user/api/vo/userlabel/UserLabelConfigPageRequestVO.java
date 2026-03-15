package com.cloud.baowang.user.api.vo.userlabel;


import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 标签管理 分页查询VO
 */
@Data
@Schema(description = "标签配置分页 Request")
public class UserLabelConfigPageRequestVO extends SitePageVO {
    @Schema(description = "标签ID")
    private String labelId;

    @Schema(description = "标签名称")
    private String labelName;

    @Schema(description = "状态")
    private String status;
}
