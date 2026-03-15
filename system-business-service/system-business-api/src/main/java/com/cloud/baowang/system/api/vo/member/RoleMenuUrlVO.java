package com.cloud.baowang.system.api.vo.member;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "角色菜单URL")
public class RoleMenuUrlVO {

    private String roleId;

    private String menuUrl;
}
