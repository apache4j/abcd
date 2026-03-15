package com.cloud.baowang.system.api.vo.member;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description = "角色详情返回对象")
public class BusinessRoleDetailVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "使用数量")
    private Integer useNums;

    @Schema(description = "菜单IDS")
    private String[] menuIds;


}
