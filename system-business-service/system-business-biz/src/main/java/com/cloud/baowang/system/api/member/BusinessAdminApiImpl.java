package com.cloud.baowang.system.api.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.business.BusinessStorageMenuRespVO;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.vo.adminLogin.AdminUpdateVO;
import com.cloud.baowang.system.api.vo.adminLogin.GoogleBindVO;
import com.cloud.baowang.system.api.vo.member.*;
import com.cloud.baowang.system.api.vo.param.SystemSiteSelectQuickEntryParam;
import com.cloud.baowang.system.service.member.BusinessAdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class BusinessAdminApiImpl implements BusinessAdminApi {

    private final BusinessAdminService businessAdminService;


    @Override
    public Page<BusinessAdminPageVO> listAdmin(BusinessAdminQueryVO businessAdminQueryVO) {
        return businessAdminService.listAdmin(businessAdminQueryVO);
    }

    @Override
    public String addAdmin(BusinessAdminAddVO businessAdminAddVO) {
        return businessAdminService.addAdmin(businessAdminAddVO);
    }

    @Override
    public String updateAdmin(BusinessAdminUpdateVO businessAdminUpdateVO) {
        return businessAdminService.updateAdmin(businessAdminUpdateVO);
    }

    @Override
    public Integer deleteAdmin(IdVO idVO) {
        return businessAdminService.deleteAdmin(idVO);
    }

    @Override
    public BusinessAdminDetailVO getAdminById(IdVO idVO) {
        return businessAdminService.getAdminById(idVO);
    }

    @Override
    public Boolean checkAdminNameUnique(NameUniqueVO nameUniqueVO) {
        return businessAdminService.checkAdminNameUnique(nameUniqueVO);
    }
    @Override
    public Integer updateAdminStatus(ChangeStatusVO changeStatusVO) {
        return businessAdminService.updateAdminStatus(changeStatusVO);
    }
    @Override
    public Integer  adminUnLock(IdVO idVO) {
        return businessAdminService.adminUnLock(idVO);
    }
    @Override
    public Integer  resetPassword(BusinessAdminResetPasswordVO businessAdminResetPasswordVO) {
        return businessAdminService.resetPassword(businessAdminResetPasswordVO);
    }
    @Override
    public BusinessAdminVO getAdminByUserName( String userName) {
        return businessAdminService.getAdminByUserName(userName);
    }
    @Override
    public Integer  lockAdmin(String userName) {
        return businessAdminService.lockAdmin(userName);
    }
    @Override
    public List<String> getUserIdsByUseName(String userName) {
        return businessAdminService.getUserIdsByUseName(userName);
    }
    @Override
    public String getUserNameById(String id) {
        return businessAdminService.getUserNameById(id);
    }
    @Override
    public List<BusinessAdminVO> getUserByIds(List<String> ids) {
        return businessAdminService.getUserByIds(ids);
    }
    @Override
    public List<String> getRoleIdsByUseName(String adminId) {
        return businessAdminService.getRoleIdsByUseName(adminId);
    }
    @Override
    public Integer accountSet(AccountSetParamVO accountSetParamVO) {
        return businessAdminService.accountSet(accountSetParamVO);
    }

    @Override
    public BusinessAdminVO getBusinessAdminById(String id){
        return businessAdminService.getBusinessAdminById(id);
    }

    @Override
    public Integer editPassword(AdminPasswordEditVO editVO) {
        return businessAdminService.editPassword(editVO);
    }

    @Override
    public ResponseVO<Boolean> updateGoogleKey(GoogleBindVO googleBindVO) {
        AdminUpdateVO adminUpdateVO = new AdminUpdateVO();
        adminUpdateVO.setGoogleAuthKey(googleBindVO.getGoogleAuthKey());
        adminUpdateVO.setId(googleBindVO.getId());
        adminUpdateVO.setIsSetGoogle(CommonConstant.business_zero);
        return ResponseVO.success(businessAdminService.update(adminUpdateVO));
    }

    @Override
    public Integer resetGoogleAuthKey(IdVO idVO) {

        return businessAdminService.resetGoogleAuthKey(idVO);
    }

    @Override
    public Boolean update(AdminUpdateVO adminUpdateVO) {
        return businessAdminService.update(adminUpdateVO);
    }

    @Override
    public Boolean updateQuickButton(String adminId, List<BusinessStorageMenuRespVO> quickEntry) {
        return businessAdminService.updateQuickButton(adminId,quickEntry);
    }

    @Override
    public List<BusinessStorageMenuRespVO> getQuickButton(SystemSiteSelectQuickEntryParam vo) {
        return businessAdminService.getQuickButton(vo);
    }

    @Override
    public List<String> selectUserBySiteCodeAndApiUrl(String siteCode, List<String> menuKey) {
        return businessAdminService.selectUserBySiteCodeAndApiUrl(siteCode,menuKey);
    }

    @Override
    public List<String> getUserIdsBySiteCode(String siteCode) {
        return businessAdminService.getUserIdsBySiteCode(siteCode);
    }
}
