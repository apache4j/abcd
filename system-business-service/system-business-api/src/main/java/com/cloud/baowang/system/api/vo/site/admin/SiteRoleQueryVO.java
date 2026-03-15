package com.cloud.baowang.system.api.vo.site.admin;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description = "角色列表参数对象")
public class SiteRoleQueryVO extends PageVO {


    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "状态 字典code: enable_disable_status")
    private Integer status;

    private String currentAdminId;

    private Boolean isSuperAdmin;

    private String siteCode;

}
