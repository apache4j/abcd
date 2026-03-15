package com.cloud.baowang.user.api;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.util.StringUtils;
import com.cloud.baowang.activity.api.enums.task.TaskEnum;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.verify.SenderServiceApi;
import com.cloud.baowang.system.api.vo.verify.VerifyCodeSendVO;
import com.cloud.baowang.user.api.api.UserGlobalSetApi;
import com.cloud.baowang.user.api.vo.user.*;
import com.cloud.baowang.user.api.vo.user.reponse.UserGlobalSetResVO;
import com.cloud.baowang.user.api.vo.user.request.*;
import com.cloud.baowang.user.service.UserInfoService;
import com.cloud.baowang.user.util.MessageSendUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserGlobalSetApiImpl implements UserGlobalSetApi {

    private final UserInfoService userInfoService;
    private final SenderServiceApi senderServiceApi;


    @Override
    public ResponseVO<?> bindAccount(BindAccountVO vo) {
        boolean isBind = false;

        UserEditVO editVO = new UserEditVO();
        editVO.setSiteCode(vo.getSiteCode());
        //检查验证码是否已经校验过
        String code = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getUserAccount()));
        if (code == null) {
            //再次校验验证码
            Integer result = RedisUtil.getValue(String.format(RedisConstants.USER_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getUserAccount()));
            if (result == null || result != 1) {
                return ResponseVO.fail(ResultCode.CODE_ERROR);
            }
        } else {
            if (!code.equals(vo.getVerifyCode())) {
                return ResponseVO.fail(ResultCode.CODE_ERROR);
            }
        }

        if (RedisUtil.isKeyExist(String.format(RedisConstants.USER_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getUserAccount()))) {
            RedisUtil.deleteKey(String.format(RedisConstants.USER_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getUserAccount()));
        }

        if(RedisUtil.isKeyExist(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getUserAccount()))) {
            RedisUtil.deleteKey(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getUserAccount()));
        }

        UserInfoVO userInfoVO = null;
        UserQueryVO userQueryVO = new UserQueryVO();
        userQueryVO.setSiteCode(vo.getSiteCode());

        if (vo.getType() == 1) {
            userQueryVO.setEmail(vo.getAccount());
            userInfoVO = userInfoService.getUserInfoByQueryVO(userQueryVO);
            editVO.setEmail(vo.getAccount());
        } else {
            editVO.setAreaCode(vo.getAreaCode());
            userQueryVO.setPhone(vo.getAccount());
            userInfoVO = userInfoService.getUserInfoByQueryVO(userQueryVO);
            editVO.setPhone(vo.getAccount());
        }

        if(userInfoVO != null && !userInfoVO.getUserAccount().equals(vo.getUserAccount())) {
            if (vo.getType() == 1) {
                return ResponseVO.fail(ResultCode.MAIL_HAS_BINGED);
            } else {
                return ResponseVO.fail(ResultCode.PHONE_HAS_BINGED);
            }
        }

        UserInfoVO infoVO = userInfoService.getInfoByUserAccountAndSite(vo.getUserAccount(), vo.getSiteCode());
        if (vo.getType() == 1) {
            if (StringUtils.isEmpty(infoVO.getEmail())) {
                //说明是绑定
                isBind = true;
            } else {
                //修改
                if (vo.getAccount().equals(infoVO.getEmail())) {
                    return ResponseVO.fail(ResultCode.NEW_EMAIL_SAME);
                }
            }
        } else {
            if (StringUtils.isEmpty(infoVO.getPhone())) {
                //说明是绑定
                isBind = true;
            } else {
                //修改
                if (vo.getAccount().equals(infoVO.getPhone())) {
                    return ResponseVO.fail(ResultCode.NEW_PHONE_SAME);
                }
            }
        }

        editVO.setUserAccount(vo.getUserAccount());
        userInfoService.updateInfo(editVO);

        if (isBind) {
            if (vo.getType() == 1) {
                MessageSendUtil.kafkaSend(infoVO, TopicsConstants.TASK_NOVICE_ORDER_RECORD_TOPIC,
                        List.of(TaskEnum.NOVICE_EMAIL.getSubTaskType()));
            } else {
                MessageSendUtil.kafkaSend(infoVO, TopicsConstants.TASK_NOVICE_ORDER_RECORD_TOPIC,
                        List.of(TaskEnum.NOVICE_PHONE.getSubTaskType()));
            }
        }

        if (isBind) {
            return ResponseVO.success(ResultCode.BIND_SUCCESS.getMessageCode(), userInfoService.getInfoByUserAccountAndSite(vo.getUserAccount(), vo.getSiteCode()));
        } else {
            return ResponseVO.success(ResultCode.CHANGE_SUCCESS.getMessageCode(), userInfoService.getInfoByUserAccountAndSite(vo.getUserAccount(), vo.getSiteCode()));
        }
    }

    @Override
    public ResponseVO changePassword(ChangePasswordReqVO vo) {
        if (UserChecker.checkPassword(vo.getNewPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }

        if (!vo.getNewPassword().equals(vo.getConfirmPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_CONFIRM_ERROR);
        }
        
        UserInfoVO userInfoVO = userInfoService.getInfoByUserAccountAndSite(vo.getUserAccount(), vo.getSiteCode());

        String salt = userInfoVO.getSalt();
        // 密码加密
        String encryptPassword = UserServerUtil.getEncryptPassword(vo.getOldPassword(), salt);

        if (!encryptPassword.equals(userInfoVO.getPassword())) {
            return ResponseVO.fail(ResultCode.OLD_PASSWORD_ERROR);
        }

        if (vo.getNewPassword().equals(vo.getOldPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_SAME);
        }

        String newPassword = UserServerUtil.getEncryptPassword(vo.getNewPassword(), salt);

        UserEditVO editVO = new UserEditVO();
        editVO.setUserAccount(vo.getUserAccount());
        editVO.setPassword(newPassword);
        editVO.setSiteCode(vo.getSiteCode());

        if (userInfoService.updateInfo(editVO)) {

            return ResponseVO.success(ResultCode.CHANGE_SUCCESS.getMessageCode(), null);
        }

        return ResponseVO.fail(ResultCode.PASSWORD_SET_ERROR);
    }

    @Override
    public UserGlobalSetResVO getUserGlobalSetInfo(UserQueryVO queryVO) {
        UserInfoVO userInfoVO =
                userInfoService.getInfoByUserAccountAndSite(queryVO.getUserAccount(), queryVO.getSiteCode());
        UserGlobalSetResVO resVO = new UserGlobalSetResVO();
        BeanUtils.copyProperties(userInfoVO, resVO);
        resVO.setIsSetPwd(!ObjectUtil.isEmpty(userInfoVO.getWithdrawPwd()));

        return resVO;
    }

    @Override
    public ResponseVO<Boolean> setUserInfo(UserEditVO vo) {
        if (ObjectUtil.isEmpty(vo) || StringUtils.isBlank(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if(!StringUtils.isBlank(vo.getNickName()) && userInfoService.getUserInfoByNickNameExist(vo.getNickName())){
            throw new BaowangDefaultException(ResultCode.USER_REPEAT_NICK_ERROR);
        }

        return ResponseVO.success(userInfoService.updateInfo(vo));
    }

    @Override
    public ResponseVO sendMail(UserGetMailCodeVO vo) {
        VerifyCodeSendVO verifyCodeSendVO = new VerifyCodeSendVO();
        verifyCodeSendVO.setSiteCode(vo.getSiteCode());
        verifyCodeSendVO.setAccount(vo.getEmail());
        verifyCodeSendVO.setUserAccount(vo.getUserAccount());
        ResponseVO responseVO = senderServiceApi.sendMail(verifyCodeSendVO);
        if (!responseVO.isOk()) {
            return responseVO;
        }

        return ResponseVO.success();
    }

    @Override
    public ResponseVO sendSms(UserGetSmsCodeVO vo) {
        VerifyCodeSendVO verifyCodeSendVO = new VerifyCodeSendVO();
        verifyCodeSendVO.setSiteCode(vo.getSiteCode());
        verifyCodeSendVO.setAccount(vo.getPhone());
        verifyCodeSendVO.setAreaCode(vo.getAreaCode());
        verifyCodeSendVO.setUserAccount(vo.getUserAccount());
        ResponseVO responseVO = senderServiceApi.sendSms(verifyCodeSendVO);
        if (!responseVO.isOk()) {
            return responseVO;
        }

        return ResponseVO.success();
    }

    @Override
    public ResponseVO<ResultCode> checkVerifyCode(SafeVerifyCodeVO vo) {
        //校验验证码
        String code = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getUserAccount()));
        if (code == null || !code.equals(vo.getVerifyCode())) {
            return ResponseVO.fail(ResultCode.CODE_ERROR);
        }

        //设置标志位
        RedisUtil.setValue(String.format(RedisConstants.USER_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getUserAccount()), 1, 5 * 60L);
        //校验通过后删除原来的验证码
        RedisUtil.deleteKey(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getUserAccount()));

        return ResponseVO.success(ResultCode.SUCCESS);
    }

    @Override
    public ResponseVO setWithdrawPwd(SetWithdrawalPasswordReqVO vo) {
        if (!UserChecker.checkWithdrawPwd(vo.getPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }

        if (!vo.getPassword().equals(vo.getConfirmPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_CONFIRM_ERROR);
        }

        UserInfoVO userInfoVO = userInfoService.getInfoByUserAccountAndSite(vo.getUserAccount(), vo.getSiteCode());

        String salt = userInfoVO.getSalt();
        // 密码加密
        String encryptPassword = UserServerUtil.getEncryptPassword(vo.getPassword(), salt);

        UserEditVO editVO = new UserEditVO();
        editVO.setUserAccount(vo.getUserAccount());
        editVO.setWithdrawPwd(encryptPassword);
        editVO.setSiteCode(vo.getSiteCode());

        if (userInfoService.updateInfo(editVO)) {

            return ResponseVO.success(ResultCode.SET_SUCCESS.getMessageCode(), null);
        }

        return ResponseVO.fail(ResultCode.PASSWORD_SET_ERROR);
    }

    @Override
    public ResponseVO changeWithdrawPwd(ChangePasswordReqVO vo) {
        if (!UserChecker.checkWithdrawPwd(vo.getNewPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }


        UserInfoVO userInfoVO = userInfoService.getInfoByUserAccountAndSite(vo.getUserAccount(), vo.getSiteCode());

        String salt = userInfoVO.getSalt();
        // 密码加密
        String encryptPassword = UserServerUtil.getEncryptPassword(vo.getOldPassword(), salt);

        if (!encryptPassword.equals(userInfoVO.getWithdrawPwd())) {
            return ResponseVO.fail(ResultCode.WITHDRAW_PASSWORD_ERROR);
        }

        if (!vo.getNewPassword().equals(vo.getConfirmPassword())) {
            return ResponseVO.fail(ResultCode.TWICE_PASSWORD_NOT_SAME);
        }

        if (vo.getNewPassword().equals(vo.getOldPassword())) {
            return ResponseVO.fail(ResultCode.TRADE_PASSWORD_SAME);
        }

        String newPassword = UserServerUtil.getEncryptPassword(vo.getNewPassword(), salt);

        UserEditVO editVO = new UserEditVO();
        editVO.setUserAccount(vo.getUserAccount());
        editVO.setWithdrawPwd(newPassword);
        editVO.setSiteCode(vo.getSiteCode());

        if (userInfoService.updateInfo(editVO)) {

            return ResponseVO.success(ResultCode.CHANGE_SUCCESS.getMessageCode(), null);
        }

        return ResponseVO.fail(ResultCode.PASSWORD_SET_ERROR);
    }

    @Override
    public ResponseVO reFindWithdrawPwd(ReFindWPwdVO vo) {
        UserInfoVO userInfoVO = userInfoService.getInfoByUserAccountAndSite(vo.getUserAccount(), vo.getSiteCode());
        if (vo.getType() == 1 && StringUtils.isEmpty(userInfoVO.getEmail())) {
            throw new BaowangDefaultException(ResultCode.EMAIL_NOT_BIND);
        } else if (vo.getType() == 2 && StringUtils.isEmpty(userInfoVO.getPhone())) {
            throw new BaowangDefaultException(ResultCode.PHONE_NOT_BIND);
        }

        String salt = userInfoVO.getSalt();
        // 密码加密
        String encryptPassword = UserServerUtil.getEncryptPassword(vo.getPassword(), salt);

        if (!encryptPassword.equals(userInfoVO.getPassword())) {
            return ResponseVO.fail(ResultCode.AGENT_LOGIN_PASSWROD_ERROR);
        }

        return ResponseVO.success();
    }

    @Override
    public ResponseVO reSetWithdrawPwd(ChangeWithPasswordReqVO vo) {
        //校验验证码
        String code = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getUserAccount()));
        if (code == null || !code.equals(vo.getVerifyCode())) {
            return ResponseVO.fail(ResultCode.CODE_ERROR);
        }

        if (!UserChecker.checkWithdrawPwd(vo.getNewPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }

        if (!vo.getNewPassword().equals(vo.getConfirmPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_CONFIRM_ERROR);
        }

        UserInfoVO userInfoVO = userInfoService.getInfoByUserAccountAndSite(vo.getUserAccount(), vo.getSiteCode());

        String salt = userInfoVO.getSalt();

        String newPassword = UserServerUtil.getEncryptPassword(vo.getNewPassword(), salt);

        UserEditVO editVO = new UserEditVO();
        editVO.setUserAccount(vo.getUserAccount());
        editVO.setWithdrawPwd(newPassword);
        editVO.setSiteCode(vo.getSiteCode());

        if (userInfoService.updateInfo(editVO)) {

            return ResponseVO.success(ResultCode.CHANGE_SUCCESS.getMessageCode(), null);
        }

        return ResponseVO.fail(ResultCode.PASSWORD_SET_ERROR);
    }
}
