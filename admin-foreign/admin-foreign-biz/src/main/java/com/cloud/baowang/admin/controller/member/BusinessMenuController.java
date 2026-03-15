package com.cloud.baowang.admin.controller.member;

import com.cloud.baowang.admin.utils.auth.SecurityUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.member.BusinessMenuApi;
import com.cloud.baowang.system.api.vo.member.BusinessMenuAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuUpdateVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;



@Tag(name = "系统-菜单管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/business_menu/api")
public class BusinessMenuController {

    private final BusinessMenuApi businessMenuApi;



    @Operation(summary = "菜单添加")
    @PostMapping("/addMenu")
    public ResponseVO<String> addMenu(@Valid @RequestBody BusinessMenuAddVO businessMenuAddVO) {

        if (!businessMenuApi.checkMenuKeyUnique(businessMenuAddVO.getMenuKey())) {
            throw new BaowangDefaultException(ResultCode.MENU_KEY_IS_EXIST);
        }
        return ResponseVO.success(businessMenuApi.addMenu(businessMenuAddVO));
    }

    @Operation(summary ="菜单列表-树形结构")
    @PostMapping("/listTreeMenu")
    public ResponseVO<List<BusinessMenuVO>> listTreeMenu() {
        BusinessMenuQueryVO businessMenuQueryVO = new BusinessMenuQueryVO();
        String adminId = CurrReqUtils.getOneId();
        List<BusinessMenuVO> menuVOList = new ArrayList<>();
        if (SecurityUtils.getSuperAdmin()) {
            menuVOList = businessMenuApi.listTreeMenu(businessMenuQueryVO);
        } else {
            menuVOList = businessMenuApi.listTreeMenuByAdminId(adminId);
        }
        return ResponseVO.success(menuVOList);
    }
    @Operation(summary ="获取当前登录人员授权菜单列表-树形结构")
    @PostMapping("/listAdminTreeMenu")
    public ResponseVO<List<BusinessMenuVO>> listAdminTreeMenu() {
        BusinessMenuQueryVO businessMenuQueryVO = new BusinessMenuQueryVO();
        String adminId = CurrReqUtils.getOneId();
        List<BusinessMenuVO> menuVOList = new ArrayList<>();
        if (SecurityUtils.getSuperAdmin()) {
            menuVOList = businessMenuApi.listTreeMenu(businessMenuQueryVO);
        } else {
            menuVOList = businessMenuApi.listTreeMenuByAdminId(adminId);
        }
        return ResponseVO.success(menuVOList);

    }

    @Operation(summary ="菜单修改")
    @PostMapping("/updateMenu")
    public ResponseVO<String> updateMenu(@Valid @RequestBody BusinessMenuUpdateVO businessMenuUpdateVO) {

        return ResponseVO.success(businessMenuApi.updateMenu(businessMenuUpdateVO));
    }

    @Operation(summary ="菜单删除")
    @PostMapping("/deleteMenu")
    public ResponseVO<Integer> deleteMenu(@Valid @RequestBody IdVO idVO) {
        return ResponseVO.success(businessMenuApi.deleteMenu(idVO));
    }


}
