package com.cloud.baowang.system.po.lang;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("language_manager")
public class LanguageManagerPO extends BasePO {
    @Schema(description = "语言名称")
    private String name;
    @Schema(description = "站点编码")
    private String siteCode;
    @Schema(description = "展示code")
    private String showCode;
    @Schema(description = "语言code")
    private String code;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
    @Schema(description = "操作时间")
    private Long operateTime;
    @Schema(description = "操作人")
    private String operator;
}
