package com.cloud.baowang.system.api.vo.site.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * @author qiqi
 */
@Data
@Schema(description = "角色添加请求对象")
public class SiteRoleAddVO {

    @Schema(description = "描述", required = true)
    private String remark;

    @Schema(description = "角色名称", required = true)
    @NotEmpty(message = "角色名称不能为空")
    @Length(max = 20, message = "角色名称不能超过20个字符!")
    private String name;

    @Schema(description = "菜单IDS")
    private String[] menuIds;


    private String creator;

    /**
     * 站点编号
     */
    private String siteCode;

}
