package com.cloud.baowang.admin.controller.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.service.AuthService;
import com.cloud.baowang.common.auth.util.AdminAuthUtil;
import com.cloud.baowang.common.auth.util.BusinessAuthUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.admin.utils.auth.AuthUtil;
import com.cloud.baowang.admin.utils.auth.SecurityUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.vo.adminLogin.AdminLogoutParamVO;
import com.cloud.baowang.system.api.vo.member.AccountSetParamVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminDetailVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminPageVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminResetPasswordVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminUpdateVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.member.NameUniqueVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author qiqi
 */

@Tag(name = "系统-职员管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/business_admin/api")
public class BusinessAdminController {


    private final BusinessAdminApi businessAdminApi;



    private final AuthService authService;




    @Operation(summary ="职员列表")
    @PostMapping("/listAdmin")
    public ResponseVO<Page<BusinessAdminPageVO>> listAdmin(@RequestBody BusinessAdminQueryVO businessAdminQueryVO) {

        businessAdminQueryVO.setIsSuperAdmin(SecurityUtils.getSuperAdmin());
        businessAdminQueryVO.setAdminUserName(CurrReqUtils.getAccount());
        return ResponseVO.success(businessAdminApi.listAdmin(businessAdminQueryVO));
    }



    @Operation(summary ="职员添加")
    @PostMapping("/addAdmin")
    public ResponseVO<String> addAdmin(@Valid @RequestBody BusinessAdminAddVO businessAdminAddVO) {
        businessAdminAddVO.setCreator(CurrReqUtils.getAccount());
        NameUniqueVO nameUniqueVO = new NameUniqueVO();
        nameUniqueVO.setUserName(businessAdminAddVO.getUserName());
        if (!businessAdminApi.checkAdminNameUnique(nameUniqueVO)) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_IS_EXIST);
        }
        if (!businessAdminAddVO.getPassword().equals(businessAdminAddVO.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORDS_ENTERED_TWICE_ARE_INCONSISTENT);
        }
        businessAdminAddVO.setPassword(SecurityUtils.encryptPassword(businessAdminAddVO.getPassword()));
        return ResponseVO.success(businessAdminApi.addAdmin(businessAdminAddVO));
    }

    @Operation(summary ="职员详情")
    @PostMapping("/detailAdmin")
    public ResponseVO<BusinessAdminDetailVO> detailAdmin(@Valid @RequestBody IdVO idVO) {

        return ResponseVO.success(businessAdminApi.getAdminById(idVO));
    }

    @Operation(summary ="职员修改")
    @PostMapping("/updateAdmin")
    public ResponseVO<String> updateAdmin(@Valid @RequestBody BusinessAdminUpdateVO businessAdminUpdateVO) {
        businessAdminUpdateVO.setUpdater(CurrReqUtils.getAccount());
        BusinessAdminVO businessAdminVO = businessAdminApi.getBusinessAdminById(businessAdminUpdateVO.getId());
        if(!CommonConstant.business_zero.equals(businessAdminVO.getStatus())){
            throw new BaowangDefaultException(ResultCode.BUSINESS_ADMIN_EDIT_ERROR);
        }
        /*NameUniqueVO nameUniqueVO = new NameUniqueVO();
        nameUniqueVO.setUserName(businessAdminUpdateVO.getUserName());
        nameUniqueVO.setId(businessAdminUpdateVO.getId());
        if (!businessAdminApi.checkAdminNameUnique(nameUniqueVO)) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_IS_EXIST);
        }*/
        List<String> oldRoleIds = businessAdminApi.getRoleIdsByUseName(businessAdminUpdateVO.getId());
        String num = businessAdminApi.updateAdmin(businessAdminUpdateVO);
        String[] roleIds = businessAdminUpdateVO.getRoleIds();
        List<String> list1 = new ArrayList<>(Arrays.asList(roleIds));
        if(oldRoleIds.size() != roleIds.length || checkEquals(list1,oldRoleIds) ){
            AdminLogoutParamVO adminLogoutParamVO = new AdminLogoutParamVO();
            adminLogoutParamVO.setUserName(businessAdminVO.getUserName());
            adminLogoutParamVO.setUserId(businessAdminVO.getUserId());
            authService.adminUserLogout(adminLogoutParamVO);
        }

        return ResponseVO.success(num);
    }

    private boolean checkEquals(List<String> list1 ,List<String> list2){
        list1.removeAll(list2);
        return !list1.isEmpty();
    }

    @Operation(summary ="职员删除")
    @PostMapping("/deleteAdmin")
    public ResponseVO<Integer> deleteAdmin(@Valid @RequestBody IdVO idVO) {
        AdminLogoutParamVO adminLogoutParamVO = new AdminLogoutParamVO();
        BusinessAdminVO businessAdminVO = businessAdminApi.getBusinessAdminById(idVO.getId());
        if(!CommonConstant.business_zero.equals(businessAdminVO.getStatus())){
            throw new BaowangDefaultException(ResultCode.BUSINESS_ADMIN_DELETE_ERROR);
        }
        Integer num = businessAdminApi.deleteAdmin(idVO);
        adminLogoutParamVO.setUserName(businessAdminVO.getUserName());
        adminLogoutParamVO.setUserId(businessAdminVO.getUserId());
        authService.adminUserLogout(adminLogoutParamVO);
        return ResponseVO.success(num);
    }

    @Operation(summary ="修改状态")
    @PostMapping("/changeStatus")
    public ResponseVO<Integer> changeStatus(@Valid @RequestBody ChangeStatusVO changeStatusVO) {

        if(CommonConstant.business_zero.toString().equals(changeStatusVO.getAbleStatus())){
            if(changeStatusVO.getId().equals(CurrReqUtils.getOneId())){
                throw new BaowangDefaultException(ResultCode.CANNOT_DISABLE_YOURSELF);
            }
            AdminLogoutParamVO adminLogoutParamVO = new AdminLogoutParamVO();
            IdVO idVO = new IdVO();
            idVO.setId(changeStatusVO.getId());
            BusinessAdminDetailVO businessAdminDetailVO = businessAdminApi.getAdminById(idVO);
            adminLogoutParamVO.setUserName(businessAdminDetailVO.getUserName());
            adminLogoutParamVO.setUserId(businessAdminDetailVO.getUserId());
            authService.adminUserLogout(adminLogoutParamVO);
        }
        changeStatusVO.setUpdater(CurrReqUtils.getAccount());
        return ResponseVO.success(businessAdminApi.updateAdminStatus(changeStatusVO));
    }
    @Operation(summary ="职员解锁")
    @PostMapping("/unLock")
    public ResponseVO<Integer> unLock(@Valid @RequestBody IdVO idVO) {
        return ResponseVO.success(businessAdminApi.adminUnLock(idVO));
    }
    @Operation(summary ="重置密码")
    @PostMapping("/resetPassword")
    public ResponseVO<Integer> resetPassword(@Valid @RequestBody BusinessAdminResetPasswordVO businessAdminResetPasswordVO) {
        businessAdminResetPasswordVO.setSiteCode(CurrReqUtils.getSiteCode());
        IdVO idVO = new IdVO();
        idVO.setId(businessAdminResetPasswordVO.getId());
        BusinessAdminVO businessAdminVO = businessAdminApi.getBusinessAdminById(businessAdminResetPasswordVO.getId());
        if(YesOrNoEnum.YES.getCode().equals(businessAdminVO.getIsSuperAdmin())){
            throw new BaowangDefaultException(ResultCode.SUPER_ADMIN_PASSWORD_NOT_RESET);
        }
        if (!businessAdminResetPasswordVO.getPassword().equals(businessAdminResetPasswordVO.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORDS_ENTERED_TWICE_ARE_INCONSISTENT);
        }
        businessAdminResetPasswordVO.setPassword(SecurityUtils.encryptPassword(businessAdminResetPasswordVO.getPassword()));
        Integer num =businessAdminApi.resetPassword(businessAdminResetPasswordVO);
        if(num > 0){
            String oldToken = RedisUtil.getValue(AdminAuthUtil.getJwtKey(businessAdminVO.getUserId()));
            if (StringUtils.isNotBlank(oldToken)) {
                AuthUtil.logoutByToken(oldToken);
            }
        }
        return ResponseVO.success(num);

    }


    @PostMapping("accountSet")
    @Operation(summary ="账户设置")
    public ResponseVO<Integer> accountSet(AccountSetParamVO accountSetParamVO) {
        if (!accountSetParamVO.getNewPassword().equals(accountSetParamVO.getConfirmNewPassword())) {
            throw new BaowangDefaultException(ResultCode.TWO_PASSWORDS_ENTERED_NOT_MATCH);
        }
        BusinessAdminVO businessAdminVOResponseVO = businessAdminApi.getAdminByUserName(accountSetParamVO.getUserName());
        boolean result = SecurityUtils.matchesPassword(accountSetParamVO.getOldPassword(), businessAdminVOResponseVO.getPassword());
        if (!result) {
            throw new BaowangDefaultException(ResultCode.OLD_PASSWORD_NOT_MATCH);
        }

        accountSetParamVO.setNewPassword(SecurityUtils.encryptPassword(accountSetParamVO.getNewPassword()));
        return ResponseVO.success(businessAdminApi.accountSet(accountSetParamVO));
    }

    @PostMapping("genGoogleAuthKey")
    @Operation(summary ="生成google key")
    public ResponseVO<String> genGoogleAuthKey() {
        return ResponseVO.success(GoogleAuthUtil.generateSecretKey());
    }

    @Operation(summary ="重置谷歌秘钥")
    @PostMapping("/resetGoogleAuthKey")
    public ResponseVO<Integer> resetGoogleAuthKey(@Valid @RequestBody IdVO idVO) {
        Integer num =businessAdminApi.resetGoogleAuthKey(idVO);
        return ResponseVO.success(num);

    }

}


