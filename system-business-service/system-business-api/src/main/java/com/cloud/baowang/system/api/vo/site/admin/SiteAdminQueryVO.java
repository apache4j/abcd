package com.cloud.baowang.system.api.vo.site.admin;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description = "职员列表参数对象")
public class SiteAdminQueryVO extends PageVO {


    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "状态")
    private Integer status;


    @Schema(description = "IP白名单")
    private String allowIps;


    private Boolean isSuperAdmin;

    private String siteCode;

    private String adminName;

}
