package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 会员-标签管理
 *
 * @since 2023-07-26 10:00:00
 */

@Data
@Accessors(chain = true)
@TableName("site_user_label_config")
@Schema(description = "会员-标签管理")
public class SiteUserLabelConfigPO extends SiteBasePO {

    @Schema(description = "标签ID")
    private String labelId;

    @Schema(description = "标签名称")
    private String labelName;

    @Schema(description = "标签描述")
    private String labelDescribe;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "标签状态 0:停用; 1:启用;")
    private Integer status;

    @Schema(description = "删除状态 1: 未删除; 0:已删除")
    private Integer deleted;

    @Schema(description = "最近操作人")
    private String lastOperator;

    @Schema(description = "创建人")
    private String createName;

    @Schema(description = "标签状态 0:非定制，1定制")
    private Integer customizeStatus;

}
