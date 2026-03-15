package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentVerifyCodeVO;
import com.cloud.baowang.agent.api.vo.security.*;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentSecurityPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.utils.UserChecker;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/06/19 9:35
 * @description: 代理安全中心
 */
@Service
@Slf4j
@AllArgsConstructor
public class AgentSafeCenterService {

    private AgentInfoService agentInfoService;
    private AgentSecurityService agentSecurityService;
    private AgentInfoRepository agentInfoRepository;

    public AgentSecuritySetVO column(String siteCode,String agentAccount) {
        AgentSecuritySetVO agentSecuritySetVO = new AgentSecuritySetVO();
        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(siteCode,agentAccount);
        if (ObjectUtil.isEmpty(agentInfoPO)){
            return agentSecuritySetVO;
        }
        agentSecuritySetVO.setAgentPasswordSet(StringUtils.isEmpty(agentInfoPO.getAgentPassword()) ? 0 : 1);
        agentSecuritySetVO.setPhoneSet(StringUtils.isEmpty(agentInfoPO.getPhone()) ? 0 : 1);
        agentSecuritySetVO.setEmailSet(StringUtils.isEmpty(agentInfoPO.getEmail()) ? 0 : 1);
        agentSecuritySetVO.setGoogleAuthKeySet(StringUtils.isEmpty(agentInfoPO.getGoogleAuthKey()) ? 0 : 1);
        agentSecuritySetVO.setPayPasswordSet(StringUtils.isEmpty(agentInfoPO.getPayPassword()) ? 0 : 1);

        List<AgentSecurityPO> agentSecurityPOS = agentSecurityService.list(Wrappers.<AgentSecurityPO>lambdaQuery().eq(AgentSecurityPO::getAgentAccount, agentAccount));
        if (CollectionUtil.isNotEmpty(agentSecurityPOS)){
            List<String> questions = agentSecurityPOS.stream().map(AgentSecurityPO::getSecurityQuestion).toList();
            agentSecuritySetVO.setSecurityQuestions(questions);
        }

        return agentSecuritySetVO;
    }

    public ResponseVO<Boolean> passwordEdit(AgentPasswordEditVO agentPasswordEditVO) {

        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(agentPasswordEditVO.getSiteCode(),agentPasswordEditVO.getAgentAccount());
        if (agentInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.USER_NOT_EXIST);
        }

        if (!agentPasswordEditVO.getNewPassword().equals(agentPasswordEditVO.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORD_CONFIRM_ERROR);
        }

        String salt = agentInfoPO.getSalt();

        // 旧密码校验
        String agentPassword = agentInfoPO.getAgentPassword();
        String passwordVoEncrypt = AgentServerUtil.getEncryptPassword(agentPasswordEditVO.getOldPassword(), salt);
        if (!agentPassword.equals(passwordVoEncrypt)){
            return ResponseVO.fail(ResultCode.USER_LOGIN_ERROR);
        }

        // 密码加密
        String encryptPassword = AgentServerUtil.getEncryptPassword(agentPasswordEditVO.getNewPassword(), salt);

        agentInfoService.updatePasswordByAgentAccount(agentInfoPO.getSiteCode(),agentInfoPO.getAgentAccount(), encryptPassword);

        return ResponseVO.success();
    }


    public ResponseVO<Boolean> payPasswordEdit(AgentPayPasswordEditVO vo) {
        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(vo.getSiteCode(),vo.getAgentAccount());
        if(ObjectUtil.isEmpty(agentInfoPO)){
            return ResponseVO.fail(ResultCode.AGENT_NOT_EXISTS);
        }

        if (!vo.getNewPassword().equals(vo.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORD_CONFIRM_ERROR);
        }

        if (!UserChecker.checkPayPassword(vo.getNewPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }
        // 编辑校验
        if(StringUtils.isNotEmpty(agentInfoPO.getPayPassword())){
            // 非首次设置支付密码 不允许修改
            return ResponseVO.fail(ResultCode.AGENT_PAYPASSWORD_EDIT_ERROR);
        }

        String encryptPayPassword = AgentServerUtil.getEncryptPassword(vo.getNewPassword(), agentInfoPO.getSalt());
        agentInfoPO.setPayPassword(encryptPayPassword);
        agentInfoRepository.update(null,Wrappers.<AgentInfoPO>lambdaUpdate()
                .set(AgentInfoPO::getPayPassword,encryptPayPassword)
                .eq(AgentInfoPO::getId,agentInfoPO.getId())
        );
        return ResponseVO.success();
    }


    public ResponseVO<Boolean> bindAuthenticator(AgentBindAuthenticatorVO vo) {
        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(vo.getSiteCode(),vo.getAgentAccount());
        if (StringUtils.isNotEmpty(agentInfoPO.getGoogleAuthKey()) && !vo.getRebind()){
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_KEY_EXSIT);
        }

        if (!verifyCode(vo.getVerifyCode(), vo.getGoogleAuthKey())) {
            throw new BaowangDefaultException(ResultCode.DYNAMIC_VERIFICATION_CODE_ERR);
        }
        // 登录密码校验
        String agentPassword = agentInfoPO.getAgentPassword();
        String passwordVoEncrypt = AgentServerUtil.getEncryptPassword(vo.getPassword(), agentInfoPO.getSalt());
        if (!agentPassword.equals(passwordVoEncrypt)){
            return ResponseVO.fail(ResultCode.USER_LOGIN_ERROR);
        }

        agentInfoPO.setGoogleAuthKey(vo.getGoogleAuthKey());
        agentInfoRepository.update(null,Wrappers.<AgentInfoPO>lambdaUpdate()
                .set(AgentInfoPO::getGoogleAuthKey,vo.getGoogleAuthKey())
                .eq(AgentInfoPO::getId,agentInfoPO.getId()));
        return ResponseVO.success(true);
    }

    private boolean verifyCode(String verifyCode, String googleAuthKey) {
        if (!NumberUtil.isNumber(verifyCode)) {
            return false;
        }
        return GoogleAuthUtil.checkCode(googleAuthKey, Integer.parseInt(verifyCode));
    }

    public ResponseVO<Boolean> bindEmail(AgentBindEmailVO vo) {

        if (ObjectUtil.isEmpty(vo.getVerifyCode())) {
            return ResponseVO.fail(ResultCode.CODE_IS_EMPTY);
        }

        if (UserChecker.checkEmail(vo.getEmail()) || UserChecker.checkCharator32(vo.getEmail())) {
            return ResponseVO.fail(ResultCode.EMAIL_TYPE_ERROR);
        }
        Long count = agentInfoRepository.selectCount(Wrappers.<AgentInfoPO>lambdaQuery().eq(AgentInfoPO::getEmail, vo.getEmail()));
        if (count > 0){
            return ResponseVO.fail(ResultCode.EMAIL_ALREADY_USED);
        }

        //校验验证码
        String code = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getAgentAccount()));
        if (code == null || !code.equals(vo.getVerifyCode())) {
            return ResponseVO.fail(ResultCode.CODE_ERROR);
        }

        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(vo.getSiteCode(),vo.getAgentAccount());
        if (agentInfoPO != null && agentInfoPO.getEmail() != null){
            return ResponseVO.fail(ResultCode.AGENT_CURRENT_ALREADY_BIND_EMAIL);
        }

        agentInfoPO.setEmail(vo.getEmail());
        agentInfoRepository.update(null,Wrappers.<AgentInfoPO>lambdaUpdate()
                .set(AgentInfoPO::getEmail,vo.getEmail())
                .eq(AgentInfoPO::getId,agentInfoPO.getId())
        );

        RedisUtil.deleteKey(vo.getEmail());
        return ResponseVO.success(true);
    }
}
