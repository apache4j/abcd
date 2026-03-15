package com.cloud.baowang.user.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.activity.api.api.task.TaskOrderRecordApi;
import com.cloud.baowang.activity.api.enums.task.TaskEnum;
import com.cloud.baowang.activity.api.vo.task.TaskAppReqVO;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.user.api.enums.UserTypeEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.user.api.vo.UserSystemMessageConfigVO;
import com.cloud.baowang.user.api.vo.UserLanguageVO;
import com.cloud.baowang.common.core.vo.base.MessageNotifyVO;
import com.cloud.baowang.common.core.vo.base.MessageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.InsertUserRegistrationInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserCheckExistReqVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.RiskCtrlBlackApi;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.SiteRiskCtrlBlackApi;
import com.cloud.baowang.system.api.api.verify.SenderServiceApi;
import com.cloud.baowang.system.api.enums.RiskBlackTypeEnum;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.verify.VerifyCodeSendVO;
import com.cloud.baowang.user.api.api.SiteUserAvatarConfigApi;
import com.cloud.baowang.user.api.api.UserLoginApi;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.user.api.vo.UserGuideVO;
import com.cloud.baowang.user.api.vo.user.*;
import com.cloud.baowang.user.api.vo.user.request.UserQueryVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigRespVO;
import com.cloud.baowang.user.po.SiteUserInviteConfigPO;
import com.cloud.baowang.user.po.SiteUserInviteRecordPO;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.service.*;
import com.cloud.baowang.user.util.MessageSendUtil;
import com.cloud.baowang.user.util.NumberUtil;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.LOCKED_FOR_5_FAILED_ATTEMPTS;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserLoginApiImpl implements UserLoginApi {

    private final UserInfoService userInfoService;
    private final UserLoginLogService userLoginLogService;
    private final RiskApi riskApi;
    private final UserRegistrationInfoService userRegistrationInfoService;
    private final RiskCtrlBlackApi riskCtrlBlackApi;
    private final SiteRiskCtrlBlackApi siteRiskCtrlBlackApi;
    private final SiteApi siteApi;
    private final AgentInfoApi agentInfoApi;
    private final UserCommonService userCommonService;
    private final SiteUserAvatarConfigApi siteUserAvatarConfigApi;
    private final GenerateUserIdService generateUserIdService;
    private final SenderServiceApi senderServiceApi;
    private final SiteUserInviteRecordService siteUserInviteRecordService;
    private final SystemDictConfigApi systemDictConfigApi;
    private final SiteUserInviteConfigService siteUserInviteConfigService;

    private final TaskOrderRecordApi taskOrderRecordApi;

    private final AdsService adsService;


    @Override
    public ResponseVO sendMail(LoginGetMailCodeVO vo) {
        VerifyCodeSendVO verifyCodeSendVO = new VerifyCodeSendVO();
        verifyCodeSendVO.setSiteCode(vo.getSiteCode());
        verifyCodeSendVO.setAccount(vo.getEmail());
        verifyCodeSendVO.setUserAccount(vo.getUserAccount());
        ResponseVO responseVO = senderServiceApi.sendMail(verifyCodeSendVO);
        if (!responseVO.isOk()) {
            return responseVO;
        }

        RedisUtil.setValue(String.format(RedisConstants.USER_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getUserAccount()), 1, 5 * 60L);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO sendSms(LoginGetSmsCodeVO vo) {
        VerifyCodeSendVO verifyCodeSendVO = new VerifyCodeSendVO();
        verifyCodeSendVO.setSiteCode(vo.getSiteCode());
        verifyCodeSendVO.setAccount(vo.getPhone());
        verifyCodeSendVO.setAreaCode(vo.getAreaCode());
        verifyCodeSendVO.setUserAccount(vo.getUserAccount());
        ResponseVO responseVO = senderServiceApi.sendSms(verifyCodeSendVO);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        RedisUtil.setValue(String.format(RedisConstants.USER_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getUserAccount()), 1, 5 * 60L);

        return ResponseVO.success();
    }

    @Override
    public ResponseVO<?> userRegister(UserRegisterVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        SiteVO siteVO = siteApi.getSiteInfoByCode(siteCode);
        //校验账号是否已注册
        UserCheckExistReqVO reqVO = new UserCheckExistReqVO();
        reqVO.setUserAccount(vo.getUserAccount());
        reqVO.setSiteCode(siteCode);
        if (userInfoService.checkUserExist(reqVO)) {
            return ResponseVO.fail(ResultCode.ACCOUNT_IS_EXIST);
        }

        String userAccount = vo.getUserAccount();
        String nickName = NumberUtil.createCharacter(8);

        //随机获取用户头像
        String avatarId = null;
        String avatar = null;
        ResponseVO<List<SiteUserAvatarConfigRespVO>> avatarResp = siteUserAvatarConfigApi.getListBySiteCode(siteCode);
        if (avatarResp.isOk() && avatarResp.getData() != null && avatarResp.getData().size() > 0) {
            // 头像中的前5个随机
            int count = Math.min(avatarResp.getData().size(), 5);
            Random random = new Random();
            int index = random.nextInt(count);
            SiteUserAvatarConfigRespVO configRespVO = avatarResp.getData().get(index);
            avatarId = configRespVO.getAvatarId();
            avatar = configRespVO.getAvatarImageUrl();
        }


        UserInfoPO userInfoPO = new UserInfoPO();
        userInfoPO.setUserAccount(userAccount);
        userInfoPO.setSiteCode(siteCode);
        userInfoPO.setNickName(nickName);
        userInfoPO.setAvatarCode(avatarId);
        userInfoPO.setAvatar(avatar);
        userInfoPO.setMainCurrency(vo.getMainCurrency());
        userInfoPO.setRegisterIp(CurrReqUtils.getReqIp());
        if (StringUtils.isNotBlank(userInfoPO.getRegisterIp())) {
            IPRespVO ipResponse = IpAPICoUtils.getIp(userInfoPO.getRegisterIp());
            userInfoPO.setIpAddress(ipResponse.getAddress());
        }
        //NOTE 直接从请求参数获取 URL
        userInfoPO.setMemberDomain(StrUtil.subBefore(vo.getLoginAddress(), '#', false));

        // 生成15位加密盐
        String salt = MD5Util.randomGen();
        // 密码加密
        String encryptPassword = UserServerUtil.getEncryptPassword(vo.getPassword(), salt);

        userInfoPO.setPassword(encryptPassword);
        userInfoPO.setSalt(salt);
        userInfoPO.setAccountStatus(UserStatusEnum.NORMAL.getCode());
        userInfoPO.setAccountType(String.valueOf(UserTypeEnum.FORMAL.getCode()));
        userInfoPO.setOfflineDays(0);
        userInfoPO.setRegisterTime(System.currentTimeMillis());
        userInfoPO.setCreatedTime(System.currentTimeMillis());
        userInfoPO.setFriendInviteCode(userInfoService.generateInviteCode(siteVO.getSiteName()));

        //获取设备信息
        Integer deviceType = CurrReqUtils.getReqDeviceType();

        userInfoPO.setRegistry(deviceType);
        log.info("注册获取到的设备为{}", deviceType);

        userInfoPO.setVipRank(1);
        userInfoPO.setVipGradeCode(1);
        userInfoPO.setVipGradeUp(2);
        // 设备id
        userInfoPO.setDeviceNo(vo.getDeviceNo());
        // userInfoPO.setDeviceNo(CurrReqUtils.getReqDeviceId());

        // 注册黑名单校验 fixme 先临时注释
//        checkRegisterBlackAccount(userInfoPO);
        userInfoPO.setUserId(generateUserIdService.getAndRemoveRandomElement());

        if (ObjectUtil.isNotEmpty(vo.getInviteCode())) {
            //NOTE 增加 siteCode 过滤.
            UserInfoVO userInfoVO = userInfoService.getUserByInviteCode(vo.getInviteCode(), siteCode);
            if (userInfoVO != null) {
                userInfoPO.setInviterId(userInfoVO.getUserId());
                userInfoPO.setInviter(userInfoVO.getUserAccount());

                //先判断有没有邀请记录
                Long count = siteUserInviteRecordService.getCountByUserId(userInfoPO.getUserId());
                if (count == null || count == 0) {
                    SiteUserInviteConfigPO configPO = siteUserInviteConfigService.getInviteConfigBySiteCode(reqVO.getSiteCode());
                    SiteUserInviteRecordPO inviteRecordPO = new SiteUserInviteRecordPO();
                    inviteRecordPO.setInviteCode(vo.getInviteCode());
                    inviteRecordPO.setUserAccount(userInfoVO.getUserAccount());
                    inviteRecordPO.setUserId(userInfoVO.getUserId());
                    inviteRecordPO.setRegisterTime(userInfoPO.getRegisterTime());
                    inviteRecordPO.setSiteCode(siteCode);
                    inviteRecordPO.setCurrency(userInfoPO.getMainCurrency());
                    inviteRecordPO.setTargetUserId(userInfoPO.getUserId());
                    inviteRecordPO.setTargetAccount(userInfoPO.getUserAccount());
                    if(configPO != null) {
                       BigDecimal firstDepositAmount = configPO.getFirstDepositAmount();
                       BigDecimal depositAmountTotal = configPO.getDepositAmountTotal();
                       if(firstDepositAmount == null  || firstDepositAmount.compareTo(BigDecimal.ZERO) == 0){
                           inviteRecordPO.setValidFirstDeposit(1);
                       }
                       if (depositAmountTotal == null  || depositAmountTotal.compareTo(BigDecimal.ZERO) == 0){
                           inviteRecordPO.setValidTotalDeposit(1);
                       }
                   }
                    siteUserInviteRecordService.save(inviteRecordPO);

                    //查看邀请人是否有上级，有上级则也设置为被邀请人的上级
                    if (StringUtils.isNotBlank(userInfoVO.getSuperAgentId())) {
                        userInfoPO.setSuperAgentId(userInfoVO.getSuperAgentId());
                        userInfoPO.setSuperAgentAccount(userInfoVO.getSuperAgentAccount());
                    }
                }
            } else {
                //如果不是会员的邀请码，则需要判断是不是代理的邀请码
                AgentInfoVO agentInfoVO = agentInfoApi.getAgentByInviteCode(vo.getInviteCode(), siteCode);
                if (agentInfoVO != null) {
                    userInfoPO.setSuperAgentId(agentInfoVO.getAgentId());
                    userInfoPO.setSuperAgentAccount(agentInfoVO.getAgentAccount());
                    //fb
//                    if (StringUtils.isNotEmpty(vo.getEventId())){
//                        if (StringUtils.isNotEmpty(agentInfoVO.getFbPixId()) ) {
//                            adsService.onFbAdRegisterEventArrive(vo,agentInfoVO,deviceType);
//                        }
//                        if (StringUtils.isNotEmpty(agentInfoVO.getGooglePixId())) {
//                            adsService.onGoogleAdRegisterEventArrive(vo,agentInfoVO);
//                        }
//                    }
                }else {
                    // http://18.162.202.210/zentao/bug-view-772.html  如果没有绑定关系，不影响注册
                    //return ResponseVO.fail(ResultCode.REGISTER_INVITE_CODE_ERROR);
                }
            }
        }

        userInfoService.save(userInfoPO);

        saveRegisterInfo(userInfoPO);

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfoPO, userInfoVO);
        MessageSendUtil.kafkaSend(userInfoVO, TopicsConstants.TASK_NOVICE_ORDER_RECORD_TOPIC,
                List.of(TaskEnum.NOVICE_WELCOME.getSubTaskType(),
                        TaskEnum.NOVICE_CURRENCY.getSubTaskType()));

        // 好友邀请充值触发
        RechargeTriggerVO triggerVO = new RechargeTriggerVO();
        triggerVO.setUserId(userInfoPO.getUserId());
        triggerVO.setUserAccount(userInfoPO.getUserAccount());
        triggerVO.setSiteCode(userInfoPO.getSiteCode());
        triggerVO.setRechargeAmount(BigDecimal.ZERO);
        KafkaUtil.send(TopicsConstants.CALL_FRIEND_MEMBER_RECHARGE, triggerVO);
        RedisUtil.setValue("register::" + siteCode + "::" + vo.getUserAccount(), vo.getUserAccount(), 15L, TimeUnit.SECONDS);
        return ResponseVO.success(ResultCode.REG_SUCCESS.getMessageCode(), userInfoVO);
    }

    private void checkRegisterBlackAccount(UserInfoPO userInfoPO) {
        // 注册IP黑名单
        if (StringUtils.isNotEmpty(userInfoPO.getRegisterIp())) {
            RiskBlackAccountVO riskBlackAccountVO = new RiskBlackAccountVO();
            riskBlackAccountVO.setRiskControlTypeCode(RiskBlackTypeEnum.RISK_REG_IP.getCode());
            riskBlackAccountVO.setRiskControlAccount(userInfoPO.getRegisterIp());
            riskBlackAccountVO.setSiteCode(userInfoPO.getSiteCode());
            ResponseVO<Boolean> riskIpBlack = riskCtrlBlackApi.getRiskIpBlack(riskBlackAccountVO);
            //false
            if (!riskIpBlack.isOk() || riskIpBlack.getData()) {
                throw new BaowangDefaultException(ResultCode.REGISTER_IP_LIMIT);
            }
            ResponseVO<Boolean> siteRiskIpBlack = siteRiskCtrlBlackApi.getRiskIpBlack(riskBlackAccountVO);
            if (!siteRiskIpBlack.isOk() || siteRiskIpBlack.getData()) {
                throw new BaowangDefaultException(ResultCode.REGISTER_IP_LIMIT);
            }
        }
        // 注册设备黑名单
        if (ObjectUtil.isNotEmpty(userInfoPO.getDeviceNo())) {
            RiskBlackAccountVO riskBlackAccountVO = new RiskBlackAccountVO();
            riskBlackAccountVO.setRiskControlTypeCode(RiskBlackTypeEnum.RISK_REG_DEVICE.getCode());
            riskBlackAccountVO.setRiskControlAccount(userInfoPO.getDeviceNo());
            riskBlackAccountVO.setSiteCode(userInfoPO.getSiteCode());
            ResponseVO<List<RiskBlackAccountVO>> riskBlackResp = riskCtrlBlackApi.getRiskBlack(riskBlackAccountVO);
            if (!riskBlackResp.isOk() || CollectionUtil.isNotEmpty(riskBlackResp.getData())) {
                throw new BaowangDefaultException(ResultCode.REGISTER_DEVICE_LIMIT);
            }

            ResponseVO<List<RiskBlackAccountVO>> siteRiskBlackResp = siteRiskCtrlBlackApi.getRiskBlack(riskBlackAccountVO);
            if (!siteRiskBlackResp.isOk() || CollectionUtil.isNotEmpty(siteRiskBlackResp.getData())) {
                throw new BaowangDefaultException(ResultCode.REGISTER_DEVICE_LIMIT);
            }
        }
    }

    public void saveRegisterInfo(UserInfoPO userInfoPO) {
        // 生成注册信息
        InsertUserRegistrationInfoVO insertVo = new InsertUserRegistrationInfoVO();
        insertVo.setSiteCode(userInfoPO.getSiteCode());
        insertVo.setMemberId(userInfoPO.getUserId());
        insertVo.setMemberAccount(userInfoPO.getUserAccount());
        insertVo.setMemberName(userInfoPO.getUserName());
        insertVo.setMainCurrency(userInfoPO.getMainCurrency());
        insertVo.setMemberType(userInfoPO.getAccountType());
        insertVo.setSuperiorAgent(userInfoPO.getSuperAgentAccount());
        insertVo.setRegisterIp(userInfoPO.getRegisterIp());
        insertVo.setTerminalDeviceNumber(userInfoPO.getDeviceNo());
        //insertVo.setIpAttribution(IPInfoUtils.getIpInfo(userInfoPO.getRegisterIp()).getAddress());
        insertVo.setIpAttribution(userInfoPO.getIpAddress());
        //insertVo.setIpAttribution(ipResponse == null || ObjectUtil.isEmpty(ipResponse.getRegion()) ? "局域网" : ipResponse.getRegion());
        insertVo.setRegisterTerminal(String.valueOf(userInfoPO.getRegistry()));
        insertVo.setMemberDomain(userInfoPO.getMemberDomain());
        insertVo.setAgentId(userInfoPO.getSuperAgentId());
        insertVo.setPhone(userInfoPO.getAreaCode() == null ? "" : userInfoPO.getAreaCode() + CommonConstant.COMMA + userInfoPO.getPhone());
        userRegistrationInfoService.insertUserRegistrationInfo(insertVo);
    }


    public ResponseVO<UserInfoVO> userLogin(UserLoginVO vo) {
        String siteCode = vo.getSiteCode();

        log.info("{},登录参数:{},siteCode:{}", vo.getUserAccount(), vo, siteCode);

        UserInfoVO userInfoVO = userInfoService.getInfoByUserAccountAndSite(vo.getUserAccount(), siteCode);
        if (userInfoVO == null) {
            //登录日志
            loginLog(userInfoVO, CommonConstant.business_one, vo, ResultCode.USER_NOT_EXIST.getDesc());
            throw new BaowangDefaultException(ResultCode.ACCOUNT_ERROR);
        }
        // 判断是否是第一次登录
        userInfoVO.setFirstLogin(vo.getFirstLogin());

        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.LOGIN_LOCK.getCode())) {
            loginLog(userInfoVO, CommonConstant.business_one, vo, ResultCode.USER_LOGIN_LOCK.getDesc());
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }

        String salt = userInfoVO.getSalt();
        // 密码加密
        String encryptPassword = UserServerUtil.getEncryptPassword(vo.getPassword(), salt);

        String key = String.format(RedisConstants.USER_PASSWORD_CONTINUE_LOGIN_FAIL_COUNT, userInfoVO.getSiteCode(), userInfoVO.getUserAccount());
        if (!encryptPassword.equals(userInfoVO.getPassword())) {
            /*if (vo.getSubmitKey() != null && !vo.getSubmitKey()) {  //先每次都弹验证码
                String key = String.format(RedisConstants.USER_PASSWORD_LOGIN_FAIL_COUNT, userInfoVO.getSiteCode(), userInfoVO.getUserAccount());
                Integer count = RedisUtil.getValue(key);
                if (count == null) {
                    count = 1;
                } else {
                    count++;
                }
                RedisUtil.setValue(key, count, CommonConstant.ONE_HOUR_MILLISECONDS, TimeUnit.MILLISECONDS);
            }*/

            Integer count = RedisUtil.getValue(key);
            if (count == null) {
                count = 1;
            } else {
                count++;
            }

            RedisUtil.setValue(key, count, CommonConstant.ONE_DAY_SECONDS, TimeUnit.SECONDS);
            //用户锁定
            if (count >= 5) {
                String lockKey = String.format(RedisConstants.KEY_LOCKED_FOR_5_FAILED_ATTEMPTS, userInfoVO.getSiteCode(), userInfoVO.getUserAccount());
                if (!RedisUtil.isKeyExist(lockKey)) {
                    SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(LOCKED_FOR_5_FAILED_ATTEMPTS.getCode(), CurrReqUtils.getSiteCode()).getData();
                    if (Integer.parseInt(configValue.getConfigParam()) > 0) {
                        RedisUtil.setValue(lockKey, count, Long.parseLong(configValue.getConfigParam()), TimeUnit.MINUTES);
                    }
                }
            }


            //登录日志
            loginLog(userInfoVO, CommonConstant.business_one, vo, ResultCode.USER_LOGIN_ERROR.getDesc());
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_ERROR);

        }

        String ip = vo.getIp();

        // 登录黑名单检查
        checkLoginBlackAccount(ip, vo.getDeviceNo());

        if (userInfoVO.getLastLoginTime() == null) {
            vo.setIsRegister(true);
        }

        //IPResponse ipResponse = IpAddressUtils.queryIpRegion(ip);
        //String ipAddress = ipResponse == null || ObjectUtil.isEmpty(ipResponse.getRegion()) ? "局域网" : ipResponse.getRegion();
        Date date = new Date();
        UserInfoEditVO userInfoEditVO = new UserInfoEditVO();
        userInfoEditVO.setId(userInfoVO.getId());
        userInfoEditVO.setUserAccount(userInfoVO.getUserAccount());
        userInfoEditVO.setLastLoginTime(date.getTime());
        userInfoEditVO.setUpdatedTime(date.getTime());
        userInfoEditVO.setOfflineDays(0);
        userInfoEditVO.setLastLoginIp(ip);
        //userInfoEditVO.setIpAddress(ipAddress);
        userInfoEditVO.setLastDeviceNo(vo.getDeviceNo());
        userInfoService.updateUserInfoById(userInfoEditVO);
        // 判断是否是第一次登录
        //登录日志
        loginLog(userInfoVO, CommonConstant.business_zero, vo, null);

        userCommonService.sendLoginCount(userInfoVO.getUserId(), userInfoVO.getSiteCode(), userInfoVO.getUserAccount(), CurrReqUtils.getTimezone());
        if (vo.getIsRegister()) {
            sendWsMessage(userInfoVO, true);
        }

        //清空登录次数
        RedisUtil.deleteKey(key);
        // 完成的步骤
        userInfoVO.setStep(userLoginLogService.getNewUserGuide(userInfoVO.getUserId()));
        // 任务领取状态
        // 新人任务领取状态
        TaskAppReqVO taskAppReqVO = new TaskAppReqVO();
        taskAppReqVO.setUserId(userInfoVO.getUserId());
        taskAppReqVO.setSiteCode(siteCode);
        taskAppReqVO.setUserAccount(userInfoVO.getUserAccount());
        taskAppReqVO.setTaskType(TaskEnum.NOVICE_WELCOME.getTaskType());
        taskAppReqVO.setSubTaskType(TaskEnum.NOVICE_WELCOME.getSubTaskType());
        Integer noviceStatus = taskOrderRecordApi.noviceStatus(taskAppReqVO);
        userInfoVO.setReceiveStatus(noviceStatus);
        return ResponseVO.success(userInfoVO);
    }

    private void sendWsMessage(UserInfoVO userInfoVO, Boolean isRegister) {
        log.info("注册登录消息：{}", isRegister);
             try {
            //会员加入通知
            sendMessage(userInfoVO, SystemMessageEnum.MEMBER_WELCOME);
            //资金安全通知
            sendMessage(userInfoVO, SystemMessageEnum.MEMBER_FUNDS_SECURITY);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("{}注册消息发送失败", userInfoVO.getUserAccount());
        }
    }

    public void sendMessage(UserInfoVO userInfoVO, SystemMessageEnum messageType) {
        UserLanguageVO languageVO = new UserLanguageVO();
        languageVO.setUserType("0");
        languageVO.setMessageType(messageType);
        languageVO.setUserId(userInfoVO.getUserId());
        UserSystemMessageConfigVO messageConfigVO = userInfoService.getUserLanguage(languageVO);

        ResponseVO<SiteVO> responseVO = siteApi.getSiteInfo(userInfoVO.getSiteCode());
        SiteVO siteVO = responseVO.getData();
        MessageNotifyVO notifyVO = new MessageNotifyVO();

        String titleString = I18nMessageUtil.getI18NMessage(messageConfigVO.getTitleI18nCode());
        String messageString = I18nMessageUtil.getI18NMessage(messageConfigVO.getContentI18nCode());
        String title = null, message = null;
        String titleConvertValue = null;
        String contentConvertValue = null;
        if (messageType.getCode().equals(SystemMessageEnum.MEMBER_WELCOME.getCode())) {
            title = String.format(titleString, siteVO.getSiteName());
            message = String.format(messageString, userInfoVO.getUserAccount(), siteVO.getSiteName(), siteVO.getSiteName());
            titleConvertValue = siteVO.getSiteName();
            contentConvertValue = userInfoVO.getUserAccount() + "," + siteVO.getSiteName() + "," + siteVO.getSiteName();
        } else {
            title = titleString;
            message = String.format(messageConfigVO.getContent(), siteVO.getSiteName(), siteVO.getSiteName());
            contentConvertValue = siteVO.getSiteName() + "," + siteVO.getSiteName();
        }

        notifyVO.setUserIds(List.of(userInfoVO.getUserId()));
        notifyVO.setSiteCode(userInfoVO.getSiteCode());
        notifyVO.setMessageI18nCode(messageConfigVO.getContentI18nCode());
        notifyVO.setTitleI18nCode(messageConfigVO.getTitleI18nCode());
        notifyVO.setTitleConvertValue(titleConvertValue);
        notifyVO.setContentConvertValue(contentConvertValue);
        MessageVO messageVO = MessageVO.builder()
                .title(title)
                .message(message)
                .build();
        notifyVO.setMessageVO(messageVO);
        notifyVO.setMsgTopic(WSSubscribeEnum.REGISTER_SUCCESS.getTopic());
        notifyVO.setSystemMessageCode(messageType.getCode());

        log.info("注册登录消息发送：{}", JSONObject.toJSONString(notifyVO));
        userCommonService.sendSocketMessage(notifyVO);
    }

    private void checkLoginBlackAccount(String ip, String deviceNo) {
        // 登录IP黑名单
        if (StringUtils.isNotEmpty(ip)) {
            //校验总控设置的黑名单
            RiskBlackAccountVO riskBlackAccountVO = new RiskBlackAccountVO();
            riskBlackAccountVO.setRiskControlTypeCode(RiskBlackTypeEnum.RISK_LOGIN_IP.getCode());
            riskBlackAccountVO.setRiskControlAccount(ip);
            ResponseVO<Boolean> riskIpBlack = riskCtrlBlackApi.getRiskIpBlack(riskBlackAccountVO);
            if (!riskIpBlack.isOk() || riskIpBlack.getData()) {
                throw new BaowangDefaultException(ResultCode.LOGIN_IP_LIMIT);
            }

            //校验站点设置的黑名单那
            riskBlackAccountVO.setSiteCode(CurrReqUtils.getSiteCode());
            ResponseVO<Boolean> siteRiskIpBlack = siteRiskCtrlBlackApi.getRiskIpBlack(riskBlackAccountVO);
            if (!siteRiskIpBlack.isOk() || siteRiskIpBlack.getData()) {
                throw new BaowangDefaultException(ResultCode.LOGIN_IP_LIMIT);
            }
        }
        // 登录设备黑名单
        if (ObjectUtil.isNotEmpty(deviceNo)) {
            RiskBlackAccountVO riskBlackAccountVO = new RiskBlackAccountVO();
            riskBlackAccountVO.setRiskControlTypeCode(RiskBlackTypeEnum.RISK_LOGIN_DEVICE.getCode());
            riskBlackAccountVO.setRiskControlAccount(deviceNo);
            ResponseVO<List<RiskBlackAccountVO>> riskBlackResp = riskCtrlBlackApi.getRiskBlack(riskBlackAccountVO);
            if (!riskBlackResp.isOk() || CollectionUtil.isNotEmpty(riskBlackResp.getData())) {
                throw new BaowangDefaultException(ResultCode.LOGIN_DEVICE_LIMIT);
            }

            //校验站点设置的黑名单那
            riskBlackAccountVO.setSiteCode(CurrReqUtils.getSiteCode());
            ResponseVO<List<RiskBlackAccountVO>> siteBlackAccount = siteRiskCtrlBlackApi.getRiskBlack(riskBlackAccountVO);
            if (!siteBlackAccount.isOk() || CollectionUtil.isNotEmpty(siteBlackAccount.getData())) {
                throw new BaowangDefaultException(ResultCode.LOGIN_DEVICE_LIMIT);
            }
        }

    }

    public ResponseVO checkVerifyCode(VerifyCodeVO vo) {
        //校验验证码
        String code = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getUserAccount()));
        if (code == null || !code.equals(vo.getVerifyCode())) {
            RedisUtil.setValue(vo.getUserAccount() + vo.getAccount(), 0, 5L, TimeUnit.MINUTES);
            return ResponseVO.fail(ResultCode.CODE_ERROR);
        }

        RedisUtil.setValue(vo.getUserAccount() + vo.getAccount(), 1, 5L, TimeUnit.MINUTES);
        //验证码使用过后删除
        RedisUtil.deleteKey(String.format(RedisConstants.VERIFY_CODE_CACHE, vo.getSiteCode(), vo.getUserAccount()));
        return ResponseVO.success();
    }

    public ResponseVO resetPassword(ResetPasswordVO vo) {
        try {
            if (UserChecker.checkPassword(vo.getNewPassword())) {
                return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
            }

            if (!vo.getNewPassword().equals(vo.getConfirmPassword())) {
                return ResponseVO.fail(ResultCode.PASSWORD_CONFIRM_ERROR);
            }

            //再次校验验证码
            Integer result = RedisUtil.getValue(String.format(RedisConstants.USER_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getUserAccount()));
            if (result == null || result != 1) {
                return ResponseVO.fail(ResultCode.CODE_ERROR);
            }
            UserQueryVO queryVO = UserQueryVO.builder().userAccount(vo.getUserAccount())
                    .siteCode(vo.getSiteCode()).build();
            UserInfoVO userInfoVO = userInfoService.getUserInfoByQueryVO(queryVO);

            if (userInfoVO == null) {
                return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
            }
            String salt = userInfoVO.getSalt();
            // 密码加密
            String encryptPassword = UserServerUtil.getEncryptPassword(vo.getNewPassword(), salt);
            if (encryptPassword.equals(userInfoVO.getPassword())) {
                return ResponseVO.fail(ResultCode.NEW_OLD_PASSWORD_SAME);
            }
             userInfoService.resetPassword(vo);

            if (RedisUtil.isKeyExist(String.format(RedisConstants.USER_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getUserAccount()))) {
                RedisUtil.deleteKey(String.format(RedisConstants.USER_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getUserAccount()));
            }

            return ResponseVO.success(ResultCode.PASSWORD_SET_SUCCESS.getMessageCode(), null);
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.PASSWORD_SET_ERROR);
        }
    }

    @Override
    public ResponseVO<?> setNewUserGuide(UserGuideVO vo) {
        return userLoginLogService.setNewUserGuide(vo);
    }

    public void loginLog(UserInfoVO userInfoVO, Integer loginType, UserLoginVO userPassLoginVO, String remark) {
        if (userInfoVO == null) {
            userInfoVO = new UserInfoVO();
            userInfoVO.setAccountType("2");
            userInfoVO.setSiteCode(userPassLoginVO.getSiteCode());
            userInfoVO.setUserAccount(userPassLoginVO.getUserAccount());
        }

        HttpServletRequest request = ServletUtil.getRequest();
        //1、获取所有请求头名称
        Enumeration<String> headerNames = request.getHeaderNames();
        //2、遍历
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            //根据名称获取请求头
            String value = request.getHeader(name);
            log.info(userInfoVO.getUserAccount() + "登录 name:" + name + "-----" + value);
        }
        String ip = userPassLoginVO.getIp();
        log.info("{}登录获取到的ip为{}", userInfoVO.getUserAccount(), ip);

        //IPResponse ipResponse = IpAddressUtils.queryIpRegion(ip);
        IPRespVO ipResponse = userPassLoginVO.getIpApiVO();
        Integer deviceType = CurrReqUtils.getReqDeviceType();
        if (deviceType == null) deviceType = 1;
        UserLoginInfoVO vo = new UserLoginInfoVO();
        vo.setSiteCode(userPassLoginVO.getSiteCode());
        vo.setIpControl(userInfoVO.getRiskLevelId() == null ? null : userInfoVO.getRiskLevelId());
        vo.setLoginTerminal(String.valueOf(deviceType));
        vo.setAccountType(userInfoVO.getAccountType());
        vo.setIp(ip);
        vo.setLoginType(loginType);
        vo.setSuperAgentAccount(userInfoVO.getSuperAgentAccount());
        vo.setDeviceNo(userPassLoginVO.getDeviceNo());
        //非APP登录没有版本号，不需要返回改数据
        //vo.setVersion(userPassLoginVO.getVersion());
        vo.setIpAddress(ipResponse.getAddress());
        vo.setUserAccount(userInfoVO.getUserAccount());
        vo.setLoginTime(System.currentTimeMillis());
        String deviceVersion = StringUtil.getVersion(ServletUtil.getRequest().getHeader("userAgent"));
        if (deviceType.equals(3) || deviceType.equals(5)) {
            vo.setLoginAddress(null);
            String deviceTypeVersion = ServletUtil.getRequest().getHeader("deviceTypeVersion");
            vo.setDeviceVersion(ServletUtil.urlDecode(deviceTypeVersion));
            vo.setVersion(userPassLoginVO.getVersion());
        } else {
            vo.setLoginAddress(StrUtil.subBefore(userPassLoginVO.getLoginAddress(), '#', false));
            vo.setDeviceVersion(deviceVersion);
        }
        //vo.setLoginAddress(deviceType == 3 || deviceType == 5 ? null : CurrReqUtils.getReferer());
        vo.setRemark(remark);

        vo.setUserId(userInfoVO.getUserId());

        if (ObjectUtil.isNotEmpty(userPassLoginVO.getDeviceNo())) {
            RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
            riskAccountQueryVO.setRiskControlAccount(userPassLoginVO.getDeviceNo());
            riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
            RiskAccountVO riskAccountVO = riskApi.getRiskAccountByAccount(riskAccountQueryVO);
            if (riskAccountVO != null)
                vo.setDeviceControl(riskAccountVO.getRiskControlLevel());
        }

        userLoginLogService.insertUserLogin(vo);
    }
}
