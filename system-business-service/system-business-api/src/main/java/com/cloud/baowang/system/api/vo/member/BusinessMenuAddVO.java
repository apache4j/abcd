package com.cloud.baowang.system.api.vo.member;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(description = "菜单添加请求对象")
public class BusinessMenuAddVO {

    @Schema(description = "前端地址", required = true)
    @NotEmpty(message = "前端地址不能为空")
    private String url;

    @Schema(description = "菜单KEY唯一标识", required = true)
    @NotEmpty(message = "菜单KEY唯一标识不能为空")
    private String menuKey;

    @Schema(description = "菜单或功能名称", required = true)
    @NotEmpty(message = "菜单或功能名称不能为空")
    private String name;

    @Schema(description = "上层节点ID,无上级传入0", required = true)
    @NotNull(message = "上级节点不能为空")
    private String parentId;

    @Schema(description = "类型 1 目录 2 菜单 3 tab页权限 9 按钮")
    private Integer type;

    @Schema(description = "功能对应的API_URL")
    private String apiUrl;

    @Schema(description = "状态 0正常 1 禁用", required = true)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序")
    private Integer orderNum;
}
