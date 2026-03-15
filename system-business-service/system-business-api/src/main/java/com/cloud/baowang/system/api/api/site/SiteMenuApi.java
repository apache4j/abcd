package com.cloud.baowang.system.api.api.site;


import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuAddVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuQueryVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuUpdateVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author qiqi
 */
@FeignClient(contextId = "remoteSiteMenuApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - siteMenu")
public interface SiteMenuApi {

    String PREFIX = ApiConstants.PREFIX + "/siteMenu/api/";

    @PostMapping(PREFIX + "addMenu")
    @Schema(description = "菜单添加")
    String addMenu(@RequestBody SiteMenuAddVO siteMenuAddVO);

    @PostMapping(PREFIX + "updateMenu")
    @Schema(description = "菜单修改")
    String updateMenu(@RequestBody SiteMenuUpdateVO siteMenuUpdateVO);


    @PostMapping(PREFIX + "deleteMenu")
    @Schema(description = "菜单删除")
    Integer deleteMenu(@RequestBody IdVO idVO);

    @PostMapping(PREFIX + "listTreeMenu")
    @Schema(description = "菜单列表")
    List<SiteMenuVO> listTreeMenu(@RequestBody SiteMenuQueryVO siteMenuQueryVO);

    @PostMapping(PREFIX + "listTreeMenuByAdminId")
    @Schema(description = "菜单列表-当前人员")
    List<SiteMenuVO> listTreeMenuByAdminId(@RequestParam("adminId") String adminId);

    @PostMapping(PREFIX + "checkMenuKeyUnique")
    @Schema(description = "校验menuKey")
    boolean checkMenuKeyUnique(@RequestParam("menuKey") String menuKey);
}
