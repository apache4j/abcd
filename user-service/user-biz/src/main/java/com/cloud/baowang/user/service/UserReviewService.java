package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.task.TaskEnum;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.BigDecimalConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.enums.vip.VipChangeTypeEnum;
import com.cloud.baowang.user.api.vo.*;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.user.request.InsertUserRegistrationInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserCheckExistReqVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigRespVO;
import com.cloud.baowang.user.api.vo.userreview.UserAddVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordRequestVO;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.po.UserRegistrationInfoPO;
import com.cloud.baowang.user.po.UserReviewPO;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import com.cloud.baowang.user.repositories.UserRegistrationInfoRepository;
import com.cloud.baowang.user.repositories.UserReviewRepository;
import com.cloud.baowang.user.util.MessageSendUtil;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class UserReviewService extends ServiceImpl<UserReviewRepository, UserReviewPO> {

    private final UserReviewRepository userReviewRepository;
    private final UserInfoRepository userInfoRepository;
    private final SystemParamApi systemParamApi;
    private final UserRegistrationInfoRepository userRegistrationInfoRepository;
    private final UserRegistrationInfoService userRegistrationInfoService;
    private final AgentInfoApi agentInfoApi;
    private final SiteVIPGradeService vipGradeService;
    private final GenerateUserIdService generateUserIdService;

    private final GenerateInviteService generateInviteService;

    private final AreaSiteManageApi areaSiteManageApi;

    private final VipGradeApi vipGradeApi;

    private final SiteVIPRankService vipRankService;


    private final SiteMedalInfoService siteMedalInfoService;

    private final VipMedalRewardReceiveService rewardReceiveService;

    private final SiteUserAvatarConfigService avatarConfigService;

    private final SiteVipChangeRecordService siteVipChangeRecordService;

    private final SiteApi siteApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SiteVipChangeRecordCnService siteVipChangeRecordCnService;


    public ResponseVO<Boolean> lock(StatusVO vo, String adminId, String adminName) {
        // 获取参数
        String id = vo.getId();
        //UserReviewPO userReview = this.getById(id);
        UserReviewPO userReview = this.getOne(new LambdaQueryWrapper<UserReviewPO>().eq(UserReviewPO::getId, id).eq(UserReviewPO::getSiteCode, vo.getSiteCode()));
        if (null == userReview) {
            return ResponseVO.fail(ResultCode.ORDER_NOT_EXIST);
        }
        try {
            // 业务操作
            return lockOperate(vo, userReview, adminId, adminName);
        } catch (Exception e) {
            log.error("新增会员审核-锁单/解锁error,审核单号:{},操作人:{}", userReview.getReviewOrderNo(), adminName, e);
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private ResponseVO<Boolean> lockOperate(StatusVO vo, UserReviewPO userReview, String adminId, String adminName) {
        RLock lock = RedisUtil.getLock(String.format(RedisConstants.ADD_MEMBER_REVIEW_ORDER, vo.getId()));
        try {
            boolean lockFlag = lock.tryLock();
            if (!lockFlag) {
                return ResponseVO.fail(ResultCode.OPERATING_BY_OTHER);
            }

            //
            Integer myLockStatus;
            Integer myReviewStatus;
            String locker;
            // 锁单状态 0未锁 1已锁
            if (CommonConstant.business_one.equals(vo.getStatus())) {
                // 开始锁单
                if (CommonConstant.business_one.equals(userReview.getLockStatus())) {
                    return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
                }
                // 审核操作 1一审审核 2结单查看
                if (CommonConstant.business_two.equals(userReview.getReviewOperation())) {
                    return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
                }
                myLockStatus = CommonConstant.business_one;
                myReviewStatus = CommonConstant.business_two;
                locker = adminName;
            } else {
                // 开始解锁
                if (userReview.getLocker() != null && !userReview.getLocker().equals(adminName)) {
                    return ResponseVO.fail(ResultCode.CURRENT_USER_CANT_UNLOCK);
                }
                myLockStatus = CommonConstant.business_zero;
                myReviewStatus = CommonConstant.business_one;
                locker = null;
            }

            LambdaUpdateWrapper<UserReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
            lambdaUpdate.eq(UserReviewPO::getId, vo.getId())
                    .eq(StringUtils.isNotBlank(vo.getSiteCode()), UserReviewPO::getSiteCode, vo.getSiteCode())
                    .set(UserReviewPO::getLockStatus, myLockStatus)
                    .set(UserReviewPO::getLocker, locker)
                    .set(UserReviewPO::getReviewStatus, myReviewStatus)
                    .set(UserReviewPO::getUpdater, adminId)
                    .set(UserReviewPO::getUpdatedTime, System.currentTimeMillis());
            this.update(null, lambdaUpdate);
            return ResponseVO.success(true);

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }


    }

    public Long getTotalCount(UserReviewPageVO vo, String adminName) {

        return userReviewRepository.getTotalCount(vo, adminName);
    }

    public ResponseVO<Page<UserReviewResponseVO>> getReviewPage(UserReviewPageVO vo, String adminName) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        Page<UserReviewResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        Page<UserReviewResponseVO> pageResult = userReviewRepository.getReviewPage(page, vo, adminName);

        Map<String, String> reviewOperationMap = systemParamApi.getSystemParamMap(CommonConstant.USER_REVIEW_REVIEW_OPERATION).getData();
        Map<String, String> reviewStatusMap = systemParamApi.getSystemParamMap(CommonConstant.USER_REVIEW_REVIEW_STATUS).getData();

        for (UserReviewResponseVO record : pageResult.getRecords()) {
            // 审核操作 1一审审核 2结单查看
            if (null != record.getReviewOperation()) {
                record.setReviewOperationName(reviewOperationMap.get(record.getReviewOperation().toString()));
            }

            // 审核状态 1待处理 2处理中 3审核通过 4一审拒绝
            if (null != record.getReviewStatus()) {
                record.setReviewStatusName(reviewStatusMap.get(record.getReviewStatus().toString()));
            }

            // 锁单人是否当前登录人 0否 1是
            // 前端先判断locker，再判断isLocker
            if (StrUtil.isNotEmpty(record.getLocker())) {
                if (record.getLocker().equals(adminName)) {
                    record.setIsLocker(CommonConstant.business_one);
                } else {
                    record.setIsLocker(CommonConstant.business_zero);
                }
            }

            // 申请人是否当前登录人 0否 1是
            if (StrUtil.isNotEmpty(record.getApplicant())) {
                if (record.getApplicant().equals(adminName)) {
                    record.setIsApplicant(CommonConstant.business_one);
                } else {
                    record.setIsApplicant(CommonConstant.business_zero);
                }
            }
            // 对出生
        }
        return ResponseVO.success(pageResult);
    }

    public ResponseVO<UserReviewDetailsVO> getReviewDetails(IdVO vo, Boolean dataDesensitization) {
        UserReviewDetailsVO userReviewDetailsVO = new UserReviewDetailsVO();
        //UserReviewPO userReviewPO = userReviewRepository.selectById(vo.getId());
        UserReviewPO userReviewPO = this.getOne(new LambdaQueryWrapper<UserReviewPO>().eq(UserReviewPO::getId, vo.getId()).eq(UserReviewPO::getSiteCode, vo.getSiteCode()));
        if (ObjectUtil.isEmpty(userReviewPO)) {
            return ResponseVO.fail(ResultCode.ORDER_NOT_EXIST);
        }

        BeanUtil.copyProperties(userReviewPO, userReviewDetailsVO);
        userReviewDetailsVO.setRemark(userReviewPO.getApplyInfo());
        userReviewDetailsVO.setVipGradeCode(userReviewPO.getVipGrade());
        // 获取vipGreadeName
        if (ObjectUtil.isNotEmpty(userReviewPO.getVipGrade())) {
            SiteVIPGradeVO siteVIPGradeVO = vipGradeService.queryVIPGradeByGrade(String.valueOf(userReviewPO.getVipGrade()), vo.getSiteCode());
            if (siteVIPGradeVO != null) {
                userReviewDetailsVO.setVipGradeCodeName(siteVIPGradeVO.getVipGradeName());
                userReviewDetailsVO.setVipRankCodeName(siteVIPGradeVO.getVipRankName());
            }
        }
        userReviewDetailsVO.setVipRankCode(userReviewPO.getVipRankCode());
        userReviewDetailsVO.setSuperAgentId(userReviewPO.getSuperAgentId());
        userReviewDetailsVO.setSuperAgentAccount(userReviewPO.getSuperAgentAccount());
        userReviewDetailsVO.setRegisterTerminalName(DeviceType.Home.getName());
        userReviewDetailsVO.setRegisterTerminal(DeviceType.Home.getCode().toString());

        //如果userId存在， 把用户详情显示，从userInfo
        String userId = userReviewPO.getUserId();
        if (userId != null) {
            UserRegistrationInfoPO userRegistrationInfoPO = userRegistrationInfoRepository.selectOne(Wrappers.<UserRegistrationInfoPO>lambdaQuery()
                    .eq(UserRegistrationInfoPO::getMemberId, userId));
            if (ObjectUtil.isNotEmpty(userRegistrationInfoPO)) {
                BeanUtil.copyProperties(userRegistrationInfoPO, userReviewDetailsVO, "id", "phone");
                String name = DeviceType.nameByCode(Integer.parseInt(userRegistrationInfoPO.getRegisterTerminal()));
                userReviewDetailsVO.setRegisterTerminalName(name);
            }
        }

        UserInfoPO userInfoPO = userInfoRepository.selectOne(Wrappers.<UserInfoPO>lambdaQuery()
                .eq(UserInfoPO::getUserAccount, userReviewPO.getUserAccount())
                .eq(UserInfoPO::getSiteCode, userReviewPO.getSiteCode()));
        if (userInfoPO != null) {
            String accountStatus = userInfoPO.getAccountStatus();
            UserStatusEnum userStatusEnum = UserStatusEnum.nameOfCode(accountStatus);
            if (userStatusEnum != null) {
                userReviewDetailsVO.setAccountStatusName(userStatusEnum.getName());
            }
            //userReviewDetailsVO.setRegisterInfo(userInfoPO.getUserRegister());
            userReviewDetailsVO.setVipGradeCode(userInfoPO.getVipGradeCode());
            userReviewDetailsVO.setRegisterTime(userInfoPO.getRegisterTime());
            userReviewDetailsVO.setUserId(userInfoPO.getUserAccount());
        }

        if (dataDesensitization) {
            // 注册-脱敏
            if (StrUtil.isNotEmpty(userReviewPO.getPhone())) {
                userReviewDetailsVO.setPhone(SymbolUtil.showPhone(userReviewDetailsVO.getPhone()));
            }
            if (StrUtil.isNotEmpty(userReviewPO.getEmail())) {
                userReviewDetailsVO.setEmail(SymbolUtil.showEmail(userReviewDetailsVO.getEmail()));
            }
        } else {
            userReviewDetailsVO.setPhone(userReviewDetailsVO.getPhone());
            userReviewDetailsVO.setEmail(userReviewDetailsVO.getEmail());
        }
        //userReviewDetailsVO.setPhone(userReviewPO.getAreaCode() == null ? "" : userReviewPO.getAreaCode() + userReviewDetailsVO.getPhone());

        return ResponseVO.success(userReviewDetailsVO);
    }

    public ResponseVO<?> addUser(UserAddVO vo, String adminId, String adminName) {
        // 注册不区分大小写(保存用户原始输入的)，登录区分
        // 获取参数
        Integer accountType = vo.getAccountType();
        String userAccount = vo.getUserAccount();
        String password = vo.getPassword();
        //String confirmPassword = vo.getConfirmPassword();
        String mainCurrency = vo.getMainCurrency();
        String superAgentAccount = vo.getSuperAgentAccount();
        Integer vipGrade = vo.getVipGrade();
        String applyInfo = vo.getApplyInfo();
        String phone = vo.getPhone();
        String email = vo.getEmail();
        String siteCode = vo.getSiteCode();
        //如果有手机号码，区号一定要有，如果填写了区号，手机号码也一定要有
        if (StringUtils.isNotBlank(phone)) {
            if (StringUtils.isBlank(vo.getAreaCode())) {
                return ResponseVO.fail(ResultCode.AREA_CODE_NOT_NULL);
            }

        }
        if (StringUtils.isNotBlank(vo.getAreaCode())) {
            if (StringUtils.isBlank(phone)) {
                return ResponseVO.fail(ResultCode.PHONE_NOT_NULL);
            }
            // 校验手机长度
            AreaSiteLangVO areaSiteLangVO = areaSiteManageApi.getAreaInfo(vo.getAreaCode(), vo.getSiteCode());
            if (areaSiteLangVO != null) {
                if (StringUtils.equals(vo.getAreaCode(), areaSiteLangVO.getAreaCode())) {
                    // 获取最大和最小长度
                    Integer max = areaSiteLangVO.getMaxLength();
                    Integer min = areaSiteLangVO.getMinLength();
                    // 校验 max 和 min 是否为空
                    if (max != null && min != null) {
                        // 校验手机号码长度
                        if (phone.length() > max || phone.length() < min) {
                            return ResponseVO.fail(ResultCode.PHONE_ERROR);
                        }
                    }

                }
            }

        }
        // 会员账号校验
        // 注册信息 1手机号码 2电子邮箱
        if (StringUtils.isNotBlank(phone)) {
            // 手机号校验
            if (UserChecker.checkPhone(phone)) {
                return ResponseVO.fail(ResultCode.PHONE_ERROR);
            }
            if (checkPhoneExist(vo.getAreaCode(), vo.getPhone(), vo.getSiteCode())) {
                return ResponseVO.fail(ResultCode.PHONE_BOUND);
            }
        }
        if (StringUtils.isNotBlank(email)) {
            // 邮箱校验
            if (UserChecker.checkEmail(email)) {
                return ResponseVO.fail(ResultCode.EMAIL_ERROR);
            }
            if (checkEmailExist(vo.getEmail(), vo.getSiteCode())) {
                return ResponseVO.fail(ResultCode.MAIL_BOUND);
            }
        }
        // 会员账号校验
        if (UserChecker.checkUserAccount(userAccount)) {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_ERROR);
        }

        // 会员账号信息 是否已经存在
        if (null != this.getByUserAccount(userAccount, siteCode) || null != this.getUserInfoByUserAccount(userAccount, siteCode)) {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_REPEAT_ERROR);
        }

        // 密码校验
        if (UserChecker.checkPassword(password)) {
            return ResponseVO.fail(ResultCode.USER_PASSWORD_ERROR);
        }
        /*if (!StringUtils.equals(password, confirmPassword)) {
            return ResponseVO.fail(ResultCode.PASSWORD_CONFIRM_ERROR);
        }*/


        // 上级代理校验 + 账号类型校验
        AgentInfoVO agentInfoVO = null;
        if (StrUtil.isNotEmpty(superAgentAccount)) {
            agentInfoVO = agentInfoApi.getByAgentAccountSite(vo.getSiteCode(), superAgentAccount);
            if (null == agentInfoVO) {
                return ResponseVO.fail(ResultCode.SUPER_AGENT_ACCOUNT_NOT_EXIST);
            }
        }
        if (CommonConstant.business_one.equals(vo.getAccountType())) {
            // 测试
            if (StrUtil.isNotEmpty(superAgentAccount)) {
                assert agentInfoVO != null;
                if (!AgentTypeEnum.TEST.getCode().equals(agentInfoVO.getAgentType().toString())) {
                    return ResponseVO.fail(ResultCode.TYPE_NOT_SAME);
                }
            }
        } else {
            // 正式
            if (StrUtil.isNotEmpty(superAgentAccount)) {
                assert agentInfoVO != null;
                if (AgentTypeEnum.TEST.getCode().equals(agentInfoVO.getAgentType().toString())) {
                    return ResponseVO.fail(ResultCode.TYPE_NOT_SAME);
                }
            }
        }

        // 币种校验
        boolean checkValidCurrency = checkValidCurrency(mainCurrency, siteCode);
        if (!checkValidCurrency) {
            return ResponseVO.fail(ResultCode.CURRENCY_FORBID);
        }
        // 保存到会员审核表
        UserReviewPO userReviewPO = new UserReviewPO();
        userReviewPO.setUserAccount(userAccount);
        // 生成15位加密盐
        String salt = MD5Util.randomGen();
        // 密码加密 . 生成密码是在review 生成
        String encryptPassword = UserServerUtil.getEncryptPassword(password, salt);
        userReviewPO.setPassword(encryptPassword);
        userReviewPO.setSalt(salt);
        userReviewPO.setAccountType(accountType);
        userReviewPO.setPhone(phone);
        userReviewPO.setEmail(email);
        userReviewPO.setMainCurrency(mainCurrency);
        userReviewPO.setSuperAgentId(StrUtil.isNotEmpty(superAgentAccount) ? agentInfoVO.getAgentId() : null);
        userReviewPO.setSuperAgentAccount(StrUtil.isNotEmpty(superAgentAccount) ? agentInfoVO.getAgentAccount() : null);
        // 如果vip等级与code没有配置，默认都是0
        if (vipGrade == null) {
            SiteVIPGradeVO firstSiteVipGrade = vipGradeApi.getFirstSiteVipGrade(siteCode);
            vipGrade = firstSiteVipGrade.getVipGradeCode();
            userReviewPO.setVipGrade(vipGrade);
            SiteVIPRankVO firstVipRankBySiteCode = vipRankService.getFirstVipRankBySiteCode(siteCode);

            userReviewPO.setVipRankCode(firstVipRankBySiteCode.getVipRankCode());
        } else {
            SiteVIPGradeVO siteVIPGradeVO = vipGradeApi.getSiteVipGradeByCodeAndSiteCode(siteCode, vipGrade);
            userReviewPO.setVipRankCode(siteVIPGradeVO.getVipRankCode());
        }
        userReviewPO.setVipGrade(vipGrade);
        userReviewPO.setReviewOrderNo(UserServerUtil.getUserReviewOrderNo());
        userReviewPO.setApplyInfo(applyInfo);
        userReviewPO.setApplyTime(System.currentTimeMillis());
        userReviewPO.setApplicant(adminName);
        userReviewPO.setReviewOperation(Integer.valueOf(
                systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_OPERATION).getData().get(0).getCode()));
        userReviewPO.setReviewStatus(Integer.valueOf(
                systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_STATUS).getData().get(0).getCode()));
        userReviewPO.setLockStatus(Integer.valueOf(
                systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_LOCK_STATUS).getData().get(0).getCode()));

        userReviewPO.setCreator(adminId);
        userReviewPO.setUpdater(adminId);
        userReviewPO.setCreatedTime(System.currentTimeMillis());
        userReviewPO.setUpdatedTime(System.currentTimeMillis());
        userReviewPO.setSiteCode(vo.getSiteCode());
        userReviewPO.setAreaCode(vo.getAreaCode());

        this.save(userReviewPO);

        return ResponseVO.success();
    }

    private boolean checkValidCurrency(String mainCurrency, String siteCode) {

        if (StringUtils.isBlank(mainCurrency)) {
            return false;
        }
        List<SiteCurrencyInfoRespVO> currencyList = siteCurrencyInfoApi.getValidBySiteCode(siteCode);
        return currencyList.stream()
                .anyMatch(siteCurrencyInfoRespVO -> StringUtils.equals(mainCurrency, siteCurrencyInfoRespVO.getCurrencyCode()));

    }


    /**
     * 根据 会员注册信息 查询，排除一审拒绝
     *
     * @param userAccount 会员注册信息
     * @return 返回结果
     */
    public UserReviewPO getByUserAccount(String userAccount, String siteCode) {
        return userReviewRepository.getByUserAccount(userAccount, siteCode);
    }

    /**
     * 根据会员注册信息 查询会员
     *
     * @param userAccount 会员注册信息
     * @return 返回注册信息
     */
    public UserInfoVO getUserInfoByUserAccount(String userAccount, String siteCode) {
        return userInfoRepository.findUserInfoNotCase(userAccount, siteCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> reviewSuccess(ReviewVO vo, String registerIp, String registerHost, String adminId, String adminName) {
        String id = vo.getId();
        UserReviewPO userReviewPO = this.getOne(new LambdaQueryWrapper<UserReviewPO>().eq(UserReviewPO::getId, id).eq(UserReviewPO::getSiteCode, vo.getSiteCode()));
        if (userReviewPO == null) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        if (userReviewPO.getLocker() != null && !userReviewPO.getLocker().equals(adminName)) {
            return ResponseVO.fail(ResultCode.CURRENT_USER_CANT_OPERATION);
        }
        // 审核状态 1待处理 2处理中 3审核通过 4一审拒绝
        if (!CommonConstant.business_two.equals(userReviewPO.getReviewStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }
        String mainCurrency = userReviewPO.getMainCurrency();
        String siteCode = userReviewPO.getSiteCode();
        ;
        // 币种校验
        boolean checkValidCurrency = checkValidCurrency(mainCurrency, siteCode);
        if (!checkValidCurrency) {
            return ResponseVO.fail(ResultCode.CURRENCY_FORBID);
        }

        long currentTimeMillis = System.currentTimeMillis();
        // 生成账户信息
        UserInfoPO userInfoPO = new UserInfoPO();
        BeanUtil.copyProperties(vo, userInfoPO, "id");
        //userInfoPO.setUserName(generateUserName());
        // 生成15位加密盐
        //String salt = MD5Util.randomGen();
        // 密码加密 . 生成密码是在review 生成
        //String encryptPassword = UserServerUtil.getEncryptPassword(userReviewPO.getPassword(), salt);
        userInfoPO.setPassword(userReviewPO.getPassword());
        userInfoPO.setSalt(userReviewPO.getSalt());
        userInfoPO.setAccountType(userReviewPO.getAccountType().toString());
        userInfoPO.setBindingAgentTime(StrUtil.isEmpty(userReviewPO.getSuperAgentAccount()) ? null : currentTimeMillis);
        //String userAccount = generateAccount();
        String userAccount = userReviewPO.getUserAccount();
        // nickname 是
        //String nickName = com.cloud.baowang.user.util.NumberUtil.createCharacter(8);
        userInfoPO.setUserAccount(userAccount);
        //userInfoPO.setNickName(nickName);
        userInfoPO.setAccountStatus(CommonConstant.business_one.toString());
        userInfoPO.setFirstDepositAmount(BigDecimalConstants.ZERO);
        userInfoPO.setRegisterTime(currentTimeMillis);
        userInfoPO.setRegisterIp(registerIp);
        userInfoPO.setRegistry(DeviceType.Home.getCode());
        if (StringUtils.isNotBlank(userReviewPO.getPhone())) {
            if (checkPhoneExist(userReviewPO.getAreaCode(), userReviewPO.getPhone(), userReviewPO.getSiteCode())) {
                return ResponseVO.fail(ResultCode.PHONE_BOUND);
            }
        } else {
            userReviewPO.setPhone(null);
        }
        if (StringUtils.isNotBlank(userReviewPO.getEmail())) {
            if (checkEmailExist(userReviewPO.getEmail(), userReviewPO.getSiteCode())) {
                return ResponseVO.fail(ResultCode.MAIL_BOUND);
            }
        } else {
            userReviewPO.setEmail(null);
        }
        userInfoPO.setEmail(userReviewPO.getEmail() == null ? null : userReviewPO.getEmail());
        userInfoPO.setPhone(userReviewPO.getPhone() == null ? null : userReviewPO.getPhone());
        userInfoPO.setMainCurrency(userReviewPO.getMainCurrency());
        userInfoPO.setSuperAgentId(userReviewPO.getSuperAgentId());
        userInfoPO.setSuperAgentAccount(userReviewPO.getSuperAgentAccount());
        userInfoPO.setBindingAgentTime(StrUtil.isEmpty(userReviewPO.getSuperAgentAccount()) ? null : currentTimeMillis);
        int vipRankCode = null == userReviewPO.getVipGrade() ? 0 : userReviewPO.getVipGrade();
        userInfoPO.setVipGradeCode(vipRankCode);
        // 各个站点最高等级

        Integer maxGrade = getMaxVipGrade(vo.getSiteCode());
        userInfoPO.setVipRank(userReviewPO.getVipRankCode());
        // 找到vip最大

        userInfoPO.setVipGradeUp(maxGrade == vipRankCode ? vipRankCode : vipRankCode + 1);
        // todo wade  根据vip等级查询段位
        //userInfoPO.setVipRank();
        userInfoPO.setCreator(adminId);
        userInfoPO.setUpdater(adminId);
        userInfoPO.setCreatedTime(currentTimeMillis);
        userInfoPO.setUpdatedTime(currentTimeMillis);
        String userId = generateUserIdService.getAndRemoveRandomElement();
        userInfoPO.setUserId(userId);
        //userInfoPO.setUserRegister(userReviewPO.getPhone() == null ? userReviewPO.getEmail():userReviewPO.getPhone());
        //生成邀请码
        SiteVO siteVO = siteApi.getSiteInfoByCode(userReviewPO.getSiteCode());
        String friendCode = generateInviteService.generateInviteCode(siteVO.getSiteName());
        userInfoPO.setFriendInviteCode(friendCode);
        userInfoPO.setAreaCode(userReviewPO.getAreaCode());
        //随机获取用户头像
        String avatarId = null;
        String avatar = null;
        SiteUserAvatarConfigRespVO avatarVO = avatarConfigService.getRandomUserAvatar(siteVO.getSiteCode());
        if (avatarVO != null) {
            avatarId = avatarVO.getAvatarId();
            avatar = avatarVO.getAvatarImageUrl();
            userInfoPO.setAvatarCode(avatarId);
            userInfoPO.setAvatar(avatar);
        }
        if (StringUtils.isNotBlank(userInfoPO.getRegisterIp())) {
//            IPResponse ipResponse = IpAddressUtils.queryIpRegion(userInfoPO.getRegisterIp());
            IPRespVO ipResponse = IpAPICoUtils.getIp(userInfoPO.getRegisterIp());
            userInfoPO.setIpAddress(ipResponse.getAddress());
        }
        //获取邀请人
        /*UserInfoPO friendPo =  userInfoRepository.selectOne(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getSiteCode,userReviewPO.getSiteCode())
                .eq(UserInfoPO::getFriendInviteCode,friendCode));
        userInfoPO.setInviter(friendPo.getUserAccount());*/
        userInfoRepository.insert(userInfoPO);
        // 勋章
        //vipMedalRewardReceiveApi.receiveMedal(Arrays.asList(BeanUtil.copyProperties(userInfoPO, UserInfoVO.class)), userInfoPO.getSiteCode());
        // 生成注册信息
        InsertUserRegistrationInfoVO insertVo = new InsertUserRegistrationInfoVO();
        insertVo.setMemberId(userInfoPO.getUserId());
        insertVo.setMemberAccount(userReviewPO.getUserAccount());
        insertVo.setMemberName(userInfoPO.getUserName());
        insertVo.setMainCurrency(userReviewPO.getMainCurrency());
        insertVo.setMemberType(userReviewPO.getAccountType().toString());
        insertVo.setSuperiorAgent(userReviewPO.getSuperAgentAccount());
        insertVo.setAgentId(userReviewPO.getSuperAgentId());
        insertVo.setRegisterIp(registerIp);

        insertVo.setIpAttribution(userInfoPO.getIpAddress());
        //insertVo.setIpAttribution(IPInfoUtils.getIpInfo(registerIp).getAddress());
        insertVo.setRegisterTerminal(String.valueOf(DeviceType.Home.getCode()));
        insertVo.setMemberDomain(registerHost);
        insertVo.setCreator(adminId);
        insertVo.setUpdater(adminId);
        insertVo.setSiteCode(userInfoPO.getSiteCode());
        insertVo.setEmail(userInfoPO.getEmail());
        if (StringUtils.isNotBlank(userInfoPO.getAreaCode()) && StringUtils.isNotBlank(userInfoPO.getPhone())) {
            insertVo.setPhone(userInfoPO.getAreaCode() + CommonConstant.COMMA + userInfoPO.getPhone());
        }
        userRegistrationInfoService.insertUserRegistrationInfo(insertVo);
        // 更新审核状态
        LambdaUpdateWrapper<UserReviewPO> UpdateWrapperUserReview = new LambdaUpdateWrapper<>();
        UpdateWrapperUserReview.set(UserReviewPO::getUserId, userInfoPO.getUserId());
        UpdateWrapperUserReview.set(UserReviewPO::getOneReviewFinishTime, currentTimeMillis);
        UpdateWrapperUserReview.set(UserReviewPO::getReviewer, adminName);
        UpdateWrapperUserReview.set(UserReviewPO::getReviewRemark, vo.getReviewRemark());
        UpdateWrapperUserReview.set(UserReviewPO::getReviewOperation, CommonConstant.business_two);
        UpdateWrapperUserReview.set(UserReviewPO::getReviewStatus, CommonConstant.business_three);
        UpdateWrapperUserReview.set(UserReviewPO::getLockStatus, CommonConstant.business_zero);
        UpdateWrapperUserReview.set(UserReviewPO::getLocker, null);
        UpdateWrapperUserReview.set(UserReviewPO::getUpdater, adminId);
        UpdateWrapperUserReview.set(UserReviewPO::getUpdatedTime, currentTimeMillis);
        //
        if (StringUtils.isNotBlank(userInfoPO.getRegisterIp())) {
//            IPResponse ipResponse = IpAddressUtils.queryIpRegion(userInfoPO.getRegisterIp());
            IPRespVO ipResponse = IpAPICoUtils.getIp(userInfoPO.getRegisterIp());
            userInfoPO.setIpAddress(ipResponse.getAddress());
        }
        UpdateWrapperUserReview.eq(UserReviewPO::getId, userReviewPO.getId());
        this.update(null, UpdateWrapperUserReview);
        /*updateUserReview.setId(userReviewPO.getId());
        updateUserReview.setUserId(userInfoPO.getUserId());
        updateUserReview.setOneReviewFinishTime(currentTimeMillis);
        updateUserReview.setReviewer(adminName);
        updateUserReview.setReviewRemark(vo.getReviewRemark());
        updateUserReview.setReviewOperation(CommonConstant.business_two);
        updateUserReview.setReviewStatus(CommonConstant.business_three);
        updateUserReview.setLockStatus(CommonConstant.business_zero);
        updateUserReview.setLocker(null);
        updateUserReview.setUpdater(adminId);
        updateUserReview.setUpdatedTime(currentTimeMillis);
        userReviewRepository.updateById(updateUserReview);*/
        // 生成一个
        receiveMedal(userInfoPO.getSiteCode(), userInfoPO.getVipGradeCode(), userAccount, userId);
        // 区分是否是 handicap 模式
        if (ObjectUtil.equals(SiteHandicapModeEnum.Internacional.getCode(), vo.getHandicapMode())) {
            // 更新vip等级与段位
            SiteVipChangeRecordRequestVO siteVipChangeRecordRequestVO = new SiteVipChangeRecordRequestVO();
            siteVipChangeRecordRequestVO.setUserId(userInfoPO.getUserId());
            siteVipChangeRecordRequestVO.setUserAccount(userInfoPO.getUserAccount());
            SiteVIPGradeVO firstSiteVipGrade = vipGradeApi.getFirstSiteVipGrade(userInfoPO.getSiteCode());
            siteVipChangeRecordRequestVO.setBeforeChange(String.valueOf(firstSiteVipGrade.getVipGradeCode()));
            siteVipChangeRecordRequestVO.setAfterChange(String.valueOf(userInfoPO.getVipGradeCode()));
            siteVipChangeRecordRequestVO.setOperator(adminName);
            siteVipChangeRecordRequestVO.setChangeTime(System.currentTimeMillis());
            siteVipChangeRecordRequestVO.setSiteCode(userInfoPO.getSiteCode());
            siteVipChangeRecordService.insertChangeInfo(siteVipChangeRecordRequestVO);
        }else{
            LocalDateTime localDateTime=TimeZoneUtils.timeByTimeZone(currentTimeMillis,siteVO.getTimezone());
            String localDateTimeStr=TimeZoneUtils.formatLocalDateTime(localDateTime,TimeZoneUtils.patten_yyyyMMdd);
            siteVipChangeRecordCnService.isertSystemSiteVipChangeRecordCn(
                    userInfoPO.getUserId(),userInfoPO.getUserAccount(), userInfoPO.getAccountType(),userInfoPO.getAccountStatus(),
                      siteVO.getSiteCode(),0, userInfoPO.getVipGradeCode(), VipChangeTypeEnum.up.getCode(), localDateTimeStr
            );
        }
        // 新人任务
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfoPO, userInfoVO);
        List<String> subTaskTypes = new ArrayList<>();
        subTaskTypes.add(TaskEnum.NOVICE_WELCOME.getSubTaskType());
        subTaskTypes.add(TaskEnum.NOVICE_CURRENCY.getSubTaskType());
        if (StringUtils.isNotBlank(userInfoPO.getPhone())) {
            subTaskTypes.add(TaskEnum.NOVICE_PHONE.getSubTaskType());
        }
        if (StringUtils.isNotBlank(userInfoPO.getEmail())) {
            subTaskTypes.add(TaskEnum.NOVICE_EMAIL.getSubTaskType());
        }
        MessageSendUtil.kafkaSend(userInfoVO, TopicsConstants.TASK_NOVICE_ORDER_RECORD_TOPIC, subTaskTypes);
        log.info("后台注册用户，发送新人任务:{}", JSON.toJSONString(userInfoVO));
        return ResponseVO.success(true);
    }

    public void receiveMedal(String siteCode, Integer nowVipGradeCode, String account, String userId) {
        SiteMedalInfoCondReqVO condReqVO = new SiteMedalInfoCondReqVO();
        condReqVO.setSiteCode(siteCode);
        condReqVO.setMedalCode(MedalCodeEnum.MEDAL_1004.getCode());
        ResponseVO<SiteMedalInfoRespVO> resp = siteMedalInfoService.selectByCond(condReqVO);
        if (!resp.isOk() || resp.getData() == null) {
            log.error("vip升级领取勋章,获取勋章配置失败,原因:{}", resp.getMessage());
        }
        //判断哪一些会员满足派发条件的
        SiteMedalInfoRespVO medalInfoRespVO = resp.getData();
        //只要当前会员vip等级大于等级配置的等级就可以
        String condNum1 = medalInfoRespVO.getCondNum1();
        int condValue = Integer.parseInt(condNum1);
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserAccount(account);
        userInfoVO.setVipGradeCode(nowVipGradeCode);
        userInfoVO.setUserId(userId);
        if (nowVipGradeCode >= condValue) {
            rewardReceiveService.receiveMedal(List.of(userInfoVO), siteCode);
        }
    }

    private Integer getMaxVipGrade(String siteCode) {
        // todo vip等级，读取redis字段 默认VIP级别 130
        List<SiteVIPGradeVO> siteVIPGradeVOS = vipGradeService.queryAllVIPGrade(siteCode);
        return siteVIPGradeVOS.stream().max(Comparator.comparing(SiteVIPGradeVO::getVipGradeCode))
                .map(SiteVIPGradeVO::getVipGradeCode)
                .get();
    }


    /**
     * 检查指定的邮箱和站点代码的用户是否存在于存储库中。
     * <p>
     * 该方法使用 MyBatis-Plus 的 {@link Wrappers#lambdaQuery()} 构建查询条件，
     * 在 {@code userInfoRepository} 中统计符合给定邮箱和站点代码的用户数量。
     *
     * @param email    要检查的邮箱地址
     * @param siteCode 与用户关联的站点代码
     * @return 如果存在具有指定邮箱和站点代码的用户，则返回 {@code true}；否则返回 {@code false}
     */
    private boolean checkEmailExist(String email, String siteCode) {
        Long count = userInfoRepository.selectCount(Wrappers.<UserInfoPO>lambdaQuery().eq(UserInfoPO::getEmail, email)
                .eq(UserInfoPO::getSiteCode, siteCode));
        return count > 0;
    }


    /**
     * 检查指定的区号、手机号和站点代码的用户是否存在于存储库中。
     * <p>
     * 在 {@code userInfoRepository} 中统计符合给定区号、手机号和站点代码的用户数量。
     *
     * @param areaCode 要检查的电话号码区号
     * @param phone    要检查的手机号
     * @param siteCode 与用户关联的站点代码
     * @return 如果存在具有指定区号、手机号和站点代码的用户，则返回 {@code true}；否则返回 {@code false}
     */
    private boolean checkPhoneExist(String areaCode, String phone, String siteCode) {
        Long count = userInfoRepository.selectCount(Wrappers.<UserInfoPO>lambdaQuery().eq(UserInfoPO::getPhone, phone)
                .eq(UserInfoPO::getSiteCode, siteCode)
                .eq(UserInfoPO::getAreaCode, areaCode));
        return count > 0;
    }


    public ResponseVO<Boolean> reviewFail(ReviewVO vo, String adminId, String adminName) {
        UserReviewPO userReviewPO = this.getOne(new LambdaQueryWrapper<UserReviewPO>().eq(UserReviewPO::getId, vo.getId()).eq(UserReviewPO::getSiteCode, vo.getSiteCode()));

        if (userReviewPO == null) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        if (userReviewPO.getLocker() != null && !userReviewPO.getLocker().equals(adminName)) {
            return ResponseVO.fail(ResultCode.CURRENT_USER_CANT_OPERATION);
        }
        if (!CommonConstant.business_two.equals(userReviewPO.getReviewStatus())) {
            return ResponseVO.fail(ResultCode.REVIEW_STATUS_ERROR);
        }
        // 更新审核状态
        LambdaUpdateWrapper<UserReviewPO> UpdateWrapperUserReview = new LambdaUpdateWrapper<>();
        long currentTimeMillis = System.currentTimeMillis();
        UpdateWrapperUserReview.set(UserReviewPO::getOneReviewFinishTime, currentTimeMillis)
                .set(UserReviewPO::getReviewer, adminName)
                .set(UserReviewPO::getReviewRemark, vo.getReviewRemark())
                // 审核操作 1一审审核 2结单查看
                .set(UserReviewPO::getReviewOperation, CommonConstant.business_two)
                // 审核状态 1待处理 2处理中 3审核通过 4一审拒绝
                .set(UserReviewPO::getReviewStatus, CommonConstant.business_four)
                // 锁单状态 0未锁 1已锁
                .set(UserReviewPO::getLockStatus, CommonConstant.business_zero)
                .set(UserReviewPO::getLocker, null)  // Setting locker to null
                .set(UserReviewPO::getUpdater, adminId)
                .set(UserReviewPO::getUpdatedTime, currentTimeMillis)
                .eq(UserReviewPO::getId, userReviewPO.getId());
        this.update(null, UpdateWrapperUserReview);

        return ResponseVO.success(true);
    }

    public UserReviewPO findUserReviewExist(UserCheckExistReqVO reqVO) {
        return userReviewRepository.findUserReviewExist(reqVO);
    }

    /**
     * 获取指定站点的审核计数。
     *
     * @param siteCode 站点代码，用于标识特定站点。
     * @return 审核计数，确保返回的计数在 int 类型的范围内。
     * 如果计数超过 Integer.MAX_VALUE，则返回 Integer.MAX_VALUE；
     * 如果计数为负数，则返回 0。
     */
    public Integer getReviewCount(String siteCode) {
        long count = this.count(Wrappers.<UserReviewPO>lambdaQuery().eq(UserReviewPO::getReviewOperation, CommonConstant.business_one)
                .eq(UserReviewPO::getSiteCode, siteCode));
        try {
            return Math.toIntExact(count);
        } catch (ArithmeticException e) {
            log.error("计数值超出 int 类型的范围: {}", count);
            return count < 0 ? 0 : Integer.MAX_VALUE;
        }
    }
}
