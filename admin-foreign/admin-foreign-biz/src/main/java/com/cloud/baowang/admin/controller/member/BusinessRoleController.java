package com.cloud.baowang.admin.controller.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.utils.CommonAdminUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.member.BusinessRoleApi;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import com.cloud.baowang.system.api.vo.member.BusinessRoleAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleDetailVO;
import com.cloud.baowang.system.api.vo.member.BusinessRolePageVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleUpdateVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@Tag(name ="系统-角色管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/business_role/api")
public class BusinessRoleController {


    private final BusinessRoleApi businessRoleApi;





    @Operation(summary ="角色列表-分页")
    @PostMapping("/listRole")
    public ResponseVO<Page<BusinessRolePageVO>> listRole(@RequestBody BusinessRoleQueryVO businessRoleQueryVO) {
        LoginAdmin loginAdmin = CommonAdminUtils.getLoginAdmin();
        businessRoleQueryVO.setCurrentAdminId(CurrReqUtils.getAccount());
        businessRoleQueryVO.setIsSuperAdmin(loginAdmin.getIsSuperAdmin());
        return ResponseVO.success(businessRoleApi.listRole(businessRoleQueryVO));
    }

    @Operation(summary = "角色列表-所有")
    @PostMapping("/listAllRole")
    public ResponseVO<List<BusinessRoleDetailVO>> listAllRole() {
        BusinessRoleQueryVO businessRoleQueryVO  = new BusinessRoleQueryVO();
        LoginAdmin loginAdmin = CommonAdminUtils.getLoginAdmin();
        businessRoleQueryVO.setCurrentAdminId(CurrReqUtils.getAccount());
        businessRoleQueryVO.setIsSuperAdmin(loginAdmin.getIsSuperAdmin());
        return ResponseVO.success(businessRoleApi.listAllRole(businessRoleQueryVO));
    }


    @Operation(summary ="角色添加")
    @PostMapping("/addRole")
    public ResponseVO<String> addRole(@Valid @RequestBody BusinessRoleAddVO businessRoleAddVO) {
        businessRoleAddVO.setCreator(CurrReqUtils.getAccount());
        if (!businessRoleApi.checkRoleNameUnique(businessRoleAddVO.getName())) {
            throw new BaowangDefaultException(ResultCode.ROLE_NAME_IS_EXIST);
        }
        return ResponseVO.success(businessRoleApi.addRole(businessRoleAddVO));
    }

    @Operation(summary ="角色详情")
    @PostMapping("/detailRole")
    public ResponseVO<BusinessRoleDetailVO> detailRole(@Valid @RequestBody IdVO idVO) {
        return ResponseVO.success(businessRoleApi.getRoleById(idVO));
    }

    @Operation(summary ="角色修改授权菜单")
    @PostMapping("/updateRoleMenu")
    public ResponseVO<String> updateRole(@Valid @RequestBody BusinessRoleUpdateVO businessRoleUpdateVO) {

        businessRoleUpdateVO.setUpdater(CurrReqUtils.getAccount());
        return businessRoleApi.updateRole(businessRoleUpdateVO);
    }

    @Operation(summary ="角色删除")
    @PostMapping("/deleteRole")
    public ResponseVO<Integer> deleteRole(@Valid @RequestBody IdVO idVO) {
        return businessRoleApi.deleteRole(idVO);
    }


    @Operation(summary ="修改状态")
    @PostMapping("/changeStatus")
    public ResponseVO<Integer> changeStatus(@Valid @RequestBody ChangeStatusVO changeStatusVO) {
        changeStatusVO.setUpdater(CurrReqUtils.getAccount());
        return ResponseVO.success(businessRoleApi.updateRoleStatus(changeStatusVO));
    }
}
