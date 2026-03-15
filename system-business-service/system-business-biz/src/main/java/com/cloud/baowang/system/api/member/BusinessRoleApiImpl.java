package com.cloud.baowang.system.api.member;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.member.BusinessRoleApi;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleDetailVO;
import com.cloud.baowang.system.api.vo.member.BusinessRolePageVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessRoleUpdateVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.service.member.BusinessRoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qiqi
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class BusinessRoleApiImpl implements BusinessRoleApi {

    private final BusinessRoleService businessRoleService;


    @Override
    public Page<BusinessRolePageVO> listRole(BusinessRoleQueryVO businessRoleQueryVO){
        return businessRoleService.listRole(businessRoleQueryVO);
    }

    @Override
    public String addRole(BusinessRoleAddVO businessRoleAddVO) {
        return businessRoleService.addRole(businessRoleAddVO);
    }

    @Override
    public ResponseVO<String> updateRole(BusinessRoleUpdateVO businessRoleUpdateVO) {
        return businessRoleService.updateRole(businessRoleUpdateVO);
    }
    @Override
    public ResponseVO<Integer> deleteRole(IdVO idVO) {
        return businessRoleService.deleteRole(idVO);
    }

    @Override
    public BusinessRoleDetailVO getRoleById(IdVO idVO) {
        return businessRoleService.getRoleById(idVO);
    }

    @Override
    public boolean checkRoleNameUnique(String name) {
        return businessRoleService.checkRoleNameUnique(name);
    }

    @Override
    public List<BusinessRoleDetailVO> listAllRole(BusinessRoleQueryVO businessRoleQueryVO){
        return businessRoleService.listAllRole(businessRoleQueryVO);
    }

    @Override
    public Integer updateRoleStatus(ChangeStatusVO changeStatusVO) {

        return businessRoleService.updateAdminStatus(changeStatusVO);
    }
}
