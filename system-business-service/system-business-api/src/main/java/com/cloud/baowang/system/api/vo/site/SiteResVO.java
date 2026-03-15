package com.cloud.baowang.system.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 2024/7/31 13:47
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="新增站点完成返回对象")
public class SiteResVO {

    @Schema(description ="站点名称")
    private String siteName;

    @Schema(description ="后台管理员账号")
    private String siteAdminAccount;

    @Schema(description ="管理员对应密码")
    private String pwd;

    @Schema(description = "白名单ip")
    private String allowIps;
}
