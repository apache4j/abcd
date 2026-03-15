package com.cloud.baowang.admin.controller.member;

import com.cloud.baowang.admin.service.PasswordService;
import com.cloud.baowang.admin.utils.auth.SecurityUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.vo.member.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通用接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/business_admin_common/api")
public class AdminCommonController {

    private final BusinessAdminApi businessAdminApi;

    private final PasswordService passwordService;

    @PostMapping("editPassword")
    @Operation(summary = "中控后台修改密码")
    public ResponseVO editPassword(@Valid @RequestBody AdminPasswordEditVO editVO) {
        String userName = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        if (userName == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        editVO.setUserName(userName);

        BusinessAdminVO adminVO = businessAdminApi.getAdminByUserName(userName);
        if (null == adminVO || null == adminVO.getId()) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
        }
        if (!editVO.getNewPassword().equals(editVO.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.TWO_PASSWORDS_ENTERED_NOT_MATCH);
        }
        if (editVO.getOldPassword().equals(editVO.getNewPassword())) {
            throw new BaowangDefaultException(ResultCode.NEW_PASSWORD_MATCH_OLD);
        }

        if (!passwordService.matches(adminVO, editVO.getOldPassword())) {
            throw new BaowangDefaultException(ResultCode.OLD_PASSWORD_ERROR);
        }
        editVO.setNewPassword(SecurityUtils.encryptPassword(editVO.getNewPassword()));
        editVO.setId(adminVO.getId());

        boolean googleAuth = checkGoogleCode(adminVO.getGoogleAuthKey(), editVO.getVerifyCode());
        if (!googleAuth) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }

        return ResponseVO.success(businessAdminApi.editPassword(editVO));
    }

    public Boolean checkGoogleCode(String authKey, String verifyCode) {
        Integer googleAuthCode = Integer.parseInt(verifyCode);
        return GoogleAuthUtil.checkCode(authKey, googleAuthCode);
    }

}


