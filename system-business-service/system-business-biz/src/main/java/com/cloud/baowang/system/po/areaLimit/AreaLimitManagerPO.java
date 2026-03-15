package com.cloud.baowang.system.po.areaLimit;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("area_limit_manager")
public class AreaLimitManagerPO extends BasePO {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "类型 ip 或 国家")
    private Integer type;
    @Schema(description = "国家code")
    private String areaCode;
    @Schema(description = "生效状态")
    private Integer status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "操作人")
    private String operator;
    @Schema(description = "操作时间")
    private Long operatorTime;
}
