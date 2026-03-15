package com.cloud.baowang.system.po.site;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 站点后台-快捷入口(全部功能)
 * </p>
 *
 * @author ford
 * @since 2024-10-31
 */
@Data
@TableName("site_home_all_button_entrance")
@Schema(description = "代理客户端-快捷入口(全部功能)")
public class SiteHomeAllButtonEntrancePO extends SiteBasePO {

    @Schema(description ="快捷菜单code")
    private Integer code;

    @Schema(description ="快捷菜单name")
    private String name;

    @Schema(description ="1:PC端 2:H5端")
    private Integer pcOrH5;
}
