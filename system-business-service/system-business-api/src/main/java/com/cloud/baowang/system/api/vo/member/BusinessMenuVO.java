package com.cloud.baowang.system.api.vo.member;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "菜单实体VO")
public class BusinessMenuVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "前端地址")
    private String url;

    @Schema(description = "菜单KEY唯一标识")
    private String menuKey;

    @Schema(description = "菜单或功能名称")
    private String name;

    @Schema(description = "上层节点ID,无上级传入0")
    private String parentId;

    @Schema(description = "类型（1-目录 2-菜单  3-tab页  9-按钮）")
    private Integer type;

    @Schema(description = "功能对应的API_URL")
    private String apiUrl;

    @Schema(description = "状态 0 正常 1 禁用")
    private Integer status;

    @Schema(description = "级别")
    private Integer level;

    @Schema(description = "排序")
    private Integer orderNum;

    @Schema(description = "子菜单列表")
    private List<BusinessMenuVO> childMenuList;


}
