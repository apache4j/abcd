package com.cloud.baowang.system.api.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteAdminApi;
import com.cloud.baowang.system.api.vo.adminLogin.AdminUpdateVO;
import com.cloud.baowang.system.api.vo.adminLogin.GoogleKeyVO;
import com.cloud.baowang.system.api.vo.adminLogin.PasswordEditVO;
import com.cloud.baowang.system.api.vo.member.AccountSetParamVO;
import com.cloud.baowang.system.api.vo.member.AdminPasswordEditVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.member.NameUniqueVO;
import com.cloud.baowang.system.api.vo.site.admin.*;
import com.cloud.baowang.system.service.site.SiteAdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteAdminApiImpl implements SiteAdminApi {

    private final SiteAdminService siteAdminService;


    @Override
    public Page<SiteAdminPageVO> listAdmin(SiteAdminQueryVO siteAdminQueryVO) {
        return siteAdminService.listAdmin(siteAdminQueryVO);
    }

    @Override
    public String addAdmin(SiteAdminAddVO siteAdminAddVO) {
        return siteAdminService.addAdmin(siteAdminAddVO);
    }

    @Override
    public String updateAdmin(SiteAdminUpdateVO siteAdminUpdateVO) {
        return siteAdminService.updateAdmin(siteAdminUpdateVO);
    }

    @Override
    public Integer deleteAdmin(IdVO idVO) {
        return siteAdminService.deleteAdmin(idVO);
    }

    @Override
    public SiteAdminDetailVO getAdminById(IdVO idVO) {
        return siteAdminService.getAdminById(idVO);
    }

    @Override
    public Boolean checkAdminNameUnique(NameUniqueVO nameUniqueVO) {
        return siteAdminService.checkAdminNameUnique(nameUniqueVO);
    }
    @Override
    public Integer updateAdminStatus(ChangeStatusVO changeStatusVO) {
        return siteAdminService.updateAdminStatus(changeStatusVO);
    }
    @Override
    public Integer  adminUnLock(IdVO idVO) {
        return siteAdminService.adminUnLock(idVO);
    }
    @Override
    public Integer  resetPassword(SiteAdminResetPasswordVO siteAdminResetPasswordVO) {
        return siteAdminService.resetPassword(siteAdminResetPasswordVO);
    }

    @Override
    public SiteAdminVO getAdminByUserName(String userName) {
        return siteAdminService.getAdminByUserName(userName, null);
    }

    @Override
    public SiteAdminVO getAdminByUserNameAndSite( String userName, String siteCode) {
        return siteAdminService.getAdminByUserName(userName, siteCode);
    }
    @Override
    public Integer  lockAdmin(String userName, String siteCode) {
        return siteAdminService.lockAdmin(userName, siteCode);
    }
    @Override
    public List<String> getUserIdsByUseName(String userName) {
        return siteAdminService.getUserIdsByUseName(userName);
    }
    @Override
    public String getUserNameById(String id) {
        return siteAdminService.getUserNameById(id);
    }
    @Override
    public List<SiteAdminVO> getUserByIds(List<String> ids) {
        return siteAdminService.getUserByIds(ids);
    }
    @Override
    public List<String> getRoleIdsByUseName(String adminId) {
        return siteAdminService.getRoleIdsByUseName(adminId);
    }
    @Override
    public Integer accountSet(AccountSetParamVO accountSetParamVO) {
        return siteAdminService.accountSet(accountSetParamVO);
    }

    @Override
    public SiteAdminVO getSiteAdminById(String id){
        return siteAdminService.getSiteAdminById(id);
    }

    @Override
    public ResponseVO<Boolean> updatePassword(PasswordEditVO passwordEditVO) {
        AdminUpdateVO adminUpdateVO = new AdminUpdateVO();
        adminUpdateVO.setPassword(passwordEditVO.getPassword());
        adminUpdateVO.setUserName(passwordEditVO.getUserName());
        adminUpdateVO.setSiteCode(passwordEditVO.getSiteCode());
        return ResponseVO.success(siteAdminService.update(adminUpdateVO));
    }

    @Override
    public ResponseVO<Boolean> updateGoogleKey(GoogleKeyVO googleKeyVO) {
        AdminUpdateVO adminUpdateVO = new AdminUpdateVO();
        adminUpdateVO.setGoogleAuthKey(googleKeyVO.getGoogleAuthKey());
        adminUpdateVO.setUserName(googleKeyVO.getUserName());
        adminUpdateVO.setSiteCode(googleKeyVO.getSiteCode());
        return ResponseVO.success(siteAdminService.update(adminUpdateVO));
    }

    @Override
    public ResponseVO<Boolean> update(AdminUpdateVO adminUpdateVO) {
        return ResponseVO.success(siteAdminService.update(adminUpdateVO));
    }

    @Override
    public Integer resetGoogleAuthKey(IdVO idVO) {
        return siteAdminService.resetGoogleAuthKey(idVO);
    }

    @Override
    public Integer editPassword(AdminPasswordEditVO editVO) {
        return siteAdminService.editPassword(editVO);
    }

}
