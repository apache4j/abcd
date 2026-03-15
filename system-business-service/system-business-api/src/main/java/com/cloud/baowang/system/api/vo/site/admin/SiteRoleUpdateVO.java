package com.cloud.baowang.system.api.vo.site.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(description = "角色修改请求对象")
public class SiteRoleUpdateVO {

    @Schema(description = "角色ID", required = true)
    @NotNull(message = "角色ID不能为空")
    private String id;


    @Schema(description = "菜单IDS")
    private String[] menuIds;

    private String updater;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "备注")
    private String remark;

}
