package com.cloud.baowang.site.controller.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.utils.UserChecker;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.site.service.SiteAuthService;
import com.cloud.baowang.site.utils.auth.SiteAuthUtil;
import com.cloud.baowang.site.utils.auth.SiteSecurityUtils;
import com.cloud.baowang.system.api.api.site.SiteAdminApi;
import com.cloud.baowang.system.api.vo.adminLogin.AdminLogoutParamVO;
import com.cloud.baowang.system.api.vo.member.AccountSetParamVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.member.NameUniqueVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminAddVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminDetailVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminPageVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminQueryVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminResetPasswordVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminUpdateVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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

@Tag(name = "站点-系统-职员管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/site_admin/api")
public class SiteAdminController {

    private final SiteAdminApi siteAdminApi;

    private final SiteAuthService authService;


    @Operation(summary ="职员列表")
    @PostMapping("/listAdmin")
    public ResponseVO<Page<SiteAdminPageVO>> listAdmin(@RequestBody SiteAdminQueryVO siteAdminQueryVO) {

        siteAdminQueryVO.setIsSuperAdmin(SiteSecurityUtils.getSuperAdmin());
        siteAdminQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        siteAdminQueryVO.setAdminName(CurrReqUtils.getAccount());
        return ResponseVO.success(siteAdminApi.listAdmin(siteAdminQueryVO));
    }



    @Operation(summary ="职员添加")
    @PostMapping("/addAdmin")
    public ResponseVO<String> addAdmin( @RequestBody SiteAdminAddVO siteAdminAddVO) {
        siteAdminAddVO.setCreator(CurrReqUtils.getAccount());
        NameUniqueVO nameUniqueVO = new NameUniqueVO();
        nameUniqueVO.setUserName(siteAdminAddVO.getUserName());
        nameUniqueVO.setSiteCode(CurrReqUtils.getSiteCode());
        siteAdminAddVO.setSiteCode(CurrReqUtils.getSiteCode());
        if (!siteAdminApi.checkAdminNameUnique(nameUniqueVO)) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_IS_EXIST);
        }
        if (!siteAdminAddVO.getPassword().equals(siteAdminAddVO.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORDS_ENTERED_TWICE_ARE_INCONSISTENT);
        }
        if (ObjectUtils.isNotEmpty(siteAdminAddVO.getAllowIps())) {
            String[] ips = siteAdminAddVO.getAllowIps().split(",");
            for (String ip : ips) {
                if (UserChecker.checkIp(ip)) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }
        }
        siteAdminAddVO.setPassword(SiteSecurityUtils.encryptPassword(siteAdminAddVO.getPassword()));
        return ResponseVO.success(siteAdminApi.addAdmin(siteAdminAddVO));
    }

    @Operation(summary ="职员详情")
    @PostMapping("/detailAdmin")
    public ResponseVO<SiteAdminDetailVO> detailAdmin(@Valid @RequestBody IdVO idVO) {

        return ResponseVO.success(siteAdminApi.getAdminById(idVO));
    }

    @Operation(summary ="职员修改")
    @PostMapping("/updateAdmin")
    public ResponseVO<String> updateAdmin(@Valid @RequestBody SiteAdminUpdateVO siteAdminUpdateVO) {
        siteAdminUpdateVO.setUpdater(CurrReqUtils.getAccount());
        SiteAdminVO siteAdminVO = siteAdminApi.getSiteAdminById(siteAdminUpdateVO.getId());
        if(!CommonConstant.business_zero.equals(siteAdminVO.getStatus())){
            throw new BaowangDefaultException(ResultCode.BUSINESS_ADMIN_EDIT_ERROR);
        }
        /*NameUniqueVO nameUniqueVO = new NameUniqueVO();
        nameUniqueVO.setUserName(siteAdminUpdateVO.getUserName());
        nameUniqueVO.setId(siteAdminUpdateVO.getId());
        nameUniqueVO.setSiteCode(siteAdminVO.getSiteCode());
        if (!siteAdminApi.checkAdminNameUnique(nameUniqueVO)) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_IS_EXIST);
        }*/
        if (ObjectUtils.isNotEmpty(siteAdminUpdateVO.getAllowIps())){
            String[] ips = siteAdminUpdateVO.getAllowIps().split(",");
            for (String ip: ips) {
                if(UserChecker.checkIp(ip)){
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }
        }
        List<String> oldRoleIds = siteAdminApi.getRoleIdsByUseName(siteAdminUpdateVO.getId());
        String num = siteAdminApi.updateAdmin(siteAdminUpdateVO);
        String[] roleIds = siteAdminUpdateVO.getRoleIds();
        List<String> list1 = new ArrayList<>(Arrays.asList(roleIds));
        if(oldRoleIds.size() != roleIds.length || checkEquals(list1,oldRoleIds) ){
            AdminLogoutParamVO adminLogoutParamVO = new AdminLogoutParamVO();
            adminLogoutParamVO.setUserName(siteAdminVO.getUserName());
            adminLogoutParamVO.setUserId(siteAdminVO.getUserId());
            adminLogoutParamVO.setSiteCode(siteAdminVO.getSiteCode());
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
        SiteAdminVO siteAdminVO = siteAdminApi.getSiteAdminById(idVO.getId());
        if(!CommonConstant.business_zero.equals(siteAdminVO.getStatus())){
            throw new BaowangDefaultException(ResultCode.BUSINESS_ADMIN_DELETE_ERROR);
        }
        Integer num = siteAdminApi.deleteAdmin(idVO);
        adminLogoutParamVO.setUserName(siteAdminVO.getUserName());
        adminLogoutParamVO.setUserId(siteAdminVO.getUserId());
        authService.adminUserLogout(adminLogoutParamVO);
        return ResponseVO.success(num);
    }

    @Operation(summary ="修改状态")
    @PostMapping("/changeStatus")
    public ResponseVO<Integer> changeStatus(@Valid @RequestBody ChangeStatusVO changeStatusVO) {
        changeStatusVO.setUpdater(CurrReqUtils.getAccount());
        if(CommonConstant.business_zero.toString().equals(changeStatusVO.getAbleStatus())){
            if(changeStatusVO.getId().equals(CurrReqUtils.getOneId())){
                throw new BaowangDefaultException(ResultCode.CANNOT_DISABLE_YOURSELF);
            }
            AdminLogoutParamVO adminLogoutParamVO = new AdminLogoutParamVO();
            IdVO idVO = new IdVO();
            idVO.setId(changeStatusVO.getId());
            SiteAdminDetailVO siteAdminDetailVO = siteAdminApi.getAdminById(idVO);
            adminLogoutParamVO.setUserName(siteAdminDetailVO.getUserName());
            adminLogoutParamVO.setUserId(siteAdminDetailVO.getUserId());
            authService.adminUserLogout(adminLogoutParamVO);
        }
        return ResponseVO.success(siteAdminApi.updateAdminStatus(changeStatusVO));
    }
    @Operation(summary ="职员解锁")
    @PostMapping("/unLock")
    public ResponseVO<Integer> unLock(@Valid @RequestBody IdVO idVO) {
        return ResponseVO.success(siteAdminApi.adminUnLock(idVO));
    }
    @Operation(summary ="重置密码")
    @PostMapping("/resetPassword")
    public ResponseVO<Integer> resetPassword(@Valid @RequestBody SiteAdminResetPasswordVO siteAdminResetPasswordVO) {
        IdVO idVO = new IdVO();
        idVO.setId(siteAdminResetPasswordVO.getId());
        String siteCode=CurrReqUtils.getSiteCode();
        SiteAdminVO siteAdminVO = siteAdminApi.getSiteAdminById(siteAdminResetPasswordVO.getId());
        if(YesOrNoEnum.YES.getCode().equals(siteAdminVO.getIsSuperAdmin())){
            throw new BaowangDefaultException(ResultCode.SUPER_ADMIN_PASSWORD_NOT_RESET);
        }
        if (!siteAdminResetPasswordVO.getPassword().equals(siteAdminResetPasswordVO.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORDS_ENTERED_TWICE_ARE_INCONSISTENT);
        }
        siteAdminResetPasswordVO.setPassword(SiteSecurityUtils.encryptPassword(siteAdminResetPasswordVO.getPassword()));
        Integer num =siteAdminApi.resetPassword(siteAdminResetPasswordVO);
        if(num > 0){
            String oldToken = RedisUtil.getValue(com.cloud.baowang.common.auth.util.SiteAuthUtil.getJwtKey(siteCode,siteAdminVO.getUserId()));
            if (StringUtils.isNotBlank(oldToken)) {
                SiteAuthUtil.logoutByToken(siteCode,oldToken);
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
        SiteAdminVO siteAdminVOResponseVO = siteAdminApi.getAdminByUserName(accountSetParamVO.getUserName());
        boolean result = SiteSecurityUtils.matchesPassword(accountSetParamVO.getOldPassword(), siteAdminVOResponseVO.getPassword());
        if (!result) {
            throw new BaowangDefaultException(ResultCode.OLD_PASSWORD_NOT_MATCH);
        }

        accountSetParamVO.setNewPassword(SiteSecurityUtils.encryptPassword(accountSetParamVO.getNewPassword()));
        return ResponseVO.success(siteAdminApi.accountSet(accountSetParamVO));
    }

    @PostMapping("genGoogleAuthKey")
    @Operation(summary ="生成google key")
    public ResponseVO<String> genGoogleAuthKey() {
        return ResponseVO.success(GoogleAuthUtil.generateSecretKey());
    }


    @Operation(summary ="重置谷歌秘钥")
    @PostMapping("/resetGoogleAuthKey")
    public ResponseVO<Integer> resetGoogleAuthKey(@Valid @RequestBody IdVO idVO) {
        Integer num =siteAdminApi.resetGoogleAuthKey(idVO);
        return ResponseVO.success(num);

    }


}


