package com.cloud.baowang.site.controller.member;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.utils.auth.SiteSecurityUtils;
import com.cloud.baowang.system.api.api.site.SiteMenuApi;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuQueryVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;



@Tag(name = "站点-系统-菜单管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/site_menu/api")
public class SiteMenuController {

    private final SiteMenuApi siteMenuApi;



   /* @Operation(summary = "菜单添加")
    @PostMapping("/addMenu")
    public ResponseVO<String> addMenu(@Valid @RequestBody SiteMenuAddVO siteMenuAddVO) {

        if (!siteMenuApi.checkMenuKeyUnique(siteMenuAddVO.getMenuKey())) {
            throw new BaowangDefaultException(ResultCode.MENU_KEY_IS_EXIST);
        }
        return ResponseVO.success(siteMenuApi.addMenu(siteMenuAddVO));
    }

    @Operation(summary ="菜单列表-树形结构")
    @PostMapping("/listTreeMenu")
    public ResponseVO<List<SiteMenuVO>> listTreeMenu() {
        SiteMenuQueryVO siteMenuQueryVO = new SiteMenuQueryVO();
        String adminId = CurrentRequestUtils.getCurrentOneId();
        List<SiteMenuVO> menuVOList = new ArrayList<>();
        if (SecurityUtils.getSuperAdmin()) {
            menuVOList = siteMenuApi.listTreeMenu(siteMenuQueryVO);
        } else {
            menuVOList = siteMenuApi.listTreeMenuByAdminId(adminId);
        }
        return ResponseVO.success(menuVOList);
    }*/
    @Operation(summary ="获取当前登录人员授权菜单列表-树形结构")
    @PostMapping("/listAdminTreeMenu")
    public ResponseVO<List<SiteMenuVO>> listAdminTreeMenu() {
        SiteMenuQueryVO siteMenuQueryVO = new SiteMenuQueryVO();
        String adminId = CurrReqUtils.getOneId();
        List<SiteMenuVO> menuVOList = new ArrayList<>();
        if (SiteSecurityUtils.getSuperAdmin()) {
            menuVOList = siteMenuApi.listTreeMenu(siteMenuQueryVO);
        } else {
            menuVOList = siteMenuApi.listTreeMenuByAdminId(adminId);
        }
        return ResponseVO.success(menuVOList);

    }

   /* @Operation(summary ="菜单修改")
    @PostMapping("/updateMenu")
    public ResponseVO<String> updateMenu(@Valid @RequestBody SiteMenuUpdateVO siteMenuUpdateVO) {

        return ResponseVO.success(siteMenuApi.updateMenu(siteMenuUpdateVO));
    }

    @Operation(summary ="菜单删除")
    @PostMapping("/deleteMenu")
    public ResponseVO<Integer> deleteMenu(@Valid @RequestBody IdVO idVO) {
        return ResponseVO.success(siteMenuApi.deleteMenu(idVO));
    }*/


}
