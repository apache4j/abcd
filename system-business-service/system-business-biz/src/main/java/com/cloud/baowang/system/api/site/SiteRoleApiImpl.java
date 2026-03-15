package com.cloud.baowang.system.api.site;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteRoleApi;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRoleAddVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRoleDetailVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRolePageVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRoleQueryVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteRoleUpdateVO;
import com.cloud.baowang.system.service.site.SiteRoleService;
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
public class SiteRoleApiImpl implements SiteRoleApi {

    private final SiteRoleService siteRoleService;


    @Override
    public Page<SiteRolePageVO> listRole(SiteRoleQueryVO siteRoleQueryVO){
        return siteRoleService.listRole(siteRoleQueryVO);
    }

    @Override
    public String addRole(SiteRoleAddVO siteRoleAddVO) {
        return siteRoleService.addRole(siteRoleAddVO);
    }

    @Override
    public ResponseVO<String> updateRole(SiteRoleUpdateVO siteRoleUpdateVO) {
        return siteRoleService.updateRole(siteRoleUpdateVO);
    }
    @Override
    public ResponseVO<Integer> deleteRole(IdVO idVO) {
        return ResponseVO.success(siteRoleService.deleteRole(idVO));
    }

    @Override
    public SiteRoleDetailVO getRoleById(IdVO idVO) {
        return siteRoleService.getRoleById(idVO);
    }

    @Override
    public boolean checkRoleNameUnique(String name) {
        return siteRoleService.checkRoleNameUnique(name);
    }

    @Override
    public List<SiteRoleDetailVO> listAllRole(SiteRoleQueryVO siteRoleQueryVO){
        return siteRoleService.listAllRole(siteRoleQueryVO);
    }

    @Override
    public Integer updateRoleStatus(ChangeStatusVO changeStatusVO) {

        return siteRoleService.updateAdminStatus(changeStatusVO);
    }
}
