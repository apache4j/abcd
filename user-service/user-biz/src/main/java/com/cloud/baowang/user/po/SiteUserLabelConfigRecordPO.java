package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("site_user_label_config_record")
@Schema(description = "会员标签配置记录")
public class SiteUserLabelConfigRecordPO extends SiteBasePO {

    @Schema(description =  "标签名称")
    private String labelName;

    @Schema(description =  "变更类型")
    private String changeType;

    @Schema(description =  "变更前")
    private String beforeChange;

    @Schema(description =  "变更后")
    private String afterChange;

    @Schema(description =  "会员标签ID")
    private String siteUserLabelConfigId;

    @Schema(description =  "最近操作人")
    private String lastOperator;
}
