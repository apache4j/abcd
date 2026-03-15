package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.UserChecker;
import com.cloud.baowang.common.core.utils.UserServerUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.RiskCtrlEditApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.risk.RiskEditReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.enums.UserChangeTypeEnum;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import com.cloud.baowang.user.api.vo.UserDetails.*;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserInformationDownVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserLabelVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserListInformationDownVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserRiskVO;
import com.cloud.baowang.user.api.vo.userlabel.GetAllUserLabelVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.po.*;
import com.cloud.baowang.user.repositories.*;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingAmountVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class UserDetailsService {
    private final UserTypingAmountApi userTypingAmountApi;
    private final UserInformationChangeRepository userInformationChangeRepository;
    private final SystemParamApi systemParamApi;
    private final UserReviewRepository userReviewRepository;
    private final UserAccountUpdateReviewRepository userAccountUpdateReviewRepository;
    private final UserDetailsHistoryRemarkRepository userDetailsHistoryRemarkRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserInfoService userInfoService;
    private final RiskApi riskApi;
    private final RiskCtrlEditApi riskCtrlEditApi;

    private final SiteUserLabelRecordRepository siteUserLabelRecordRepository;
    private final SiteUserLabelConfigService userLabelConfigService;

    private final VipGradeApi vipGradeApi;

    private final SiteVIPGradeService siteVIPGradeService;

    private final SiteVIPRankService siteVIPRankService;

    private final SiteApi siteApi;

    private final AreaSiteManageApi areaSiteManageApi;

    private final UserInformationChangePOService userInformationChangePOService;
    private final SiteUserLabelRecordService siteUserLabelRecordService;

    private final UserDetailsHistoryRemarkService userDetailsHistoryRemarkService;


    /***
     * 会员详情编辑会员
     * @param userDetailsReqVO 会员详情接参对象
     * @param adminId  后台用户id
     * @param adminName 后台用户name
     * @return 返回结果
     */
    public ResponseVO<Void> updateInformation(UserDetailsReqVO userDetailsReqVO, String adminId, String adminName) {
        try {
            if (ObjectUtil.isEmpty(userDetailsReqVO.getUserAccount())) {
                return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
            }
            UserInfoPO userInfoPO = userInfoService.getUserInfoPOByAccountOrRegister(userDetailsReqVO.getUserAccount(), userDetailsReqVO.getSiteCode());
            if (userInfoPO == null) {
                return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
            }
            if (StringUtils.isNotBlank(userDetailsReqVO.getUserAccount())) {
                /*UserInfoPO userInfoPO = userInfoService.getUserInfoPOByAccountOrRegister(userDetailsReqVO.getUserAccount(), userDetailsReqVO.getSiteCode());
                if(userInfoPO == null){
                    return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
                }*/
                userDetailsReqVO.setUserAccount(userInfoPO.getUserAccount());
                GetByUserAccountVO exitUserAccount = ConvertUtil.entityToModel(userInfoPO, GetByUserAccountVO.class);
                if (exitUserAccount == null) {
                    return ResponseVO.fail(ResultCode.USER_INFO_ADD_ERROR);
                }
                userDetailsReqVO.setId(Long.parseLong(userInfoPO.getId()));
            } else {
                return ResponseVO.fail(ResultCode.USER_INFO_ADD_ERROR);
            }

            List<CodeValueVO> changeType = systemParamApi.getSystemParamByType(CommonConstant.USER_CHANGE_TYPE).getData();
            String changeTypeCode = changeType.stream().filter(item -> item.getCode().equals(userDetailsReqVO.getChangeType())).toList().get(0).getCode();
            // 记录备注历史表
            UserInfoPO result = userInfoRepository.selectById(userDetailsReqVO.getId());

            LambdaUpdateWrapper<UserChangeTypeHistoryRecordPO> interWrapper = new LambdaUpdateWrapper<>();
            //  通过会员账号查历史记录表
            interWrapper.eq(UserChangeTypeHistoryRecordPO::getMemberAccount, result.getUserAccount());
            interWrapper.eq(UserChangeTypeHistoryRecordPO::getCode, changeTypeCode);
            interWrapper.eq(UserChangeTypeHistoryRecordPO::getSiteCode, result.getSiteCode());
            interWrapper.orderByDesc(UserChangeTypeHistoryRecordPO::getCreatedTime);

//        List<UserChangeTypeHistoryRecordPO> historyList = userDetailsHistoryRemarkRepository.selectList(interWrapper);
            //风控层级，会员标签，会员备注 直接修改会员表
            LambdaUpdateWrapper<UserInfoPO> userInfoPOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userInfoPOLambdaUpdateWrapper.eq(UserInfoPO::getUserAccount, result.getUserAccount());
            userInfoPOLambdaUpdateWrapper.eq(UserInfoPO::getSiteCode, result.getSiteCode());
            SiteUserLabelRecordsPO labelPO = new SiteUserLabelRecordsPO();
            UserInformationChangePO userInformationChangePO = new UserInformationChangePO();
            // 备注信息直接修改成功
            if (StringUtils.isNotBlank(changeTypeCode)) {
                if (String.valueOf((UserChangeTypeEnum.ACCOUNT_NUMBER_STATUS.getCode())).equals(changeTypeCode)) {
                    //修改会员账号备注信息
                    //LambdaUpdateWrapper<UserInfoPO> userInfoPOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    userInfoPOLambdaUpdateWrapper.eq(UserInfoPO::getId, userDetailsReqVO.getId());
                    // 限制只能修改自己的站点会员
                    userInfoPOLambdaUpdateWrapper.eq(UserInfoPO::getSiteCode, userDetailsReqVO.getSiteCode());
                    userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getAcountRemark, userDetailsReqVO.getAccountRemark());
                    userInfoService.update(userInfoPOLambdaUpdateWrapper);
                    //新增账号备注记录表
                    UserChangeTypeHistoryRecordPO historyPO = new UserChangeTypeHistoryRecordPO();
                    historyPO.setMemberAccount(userDetailsReqVO.getUserAccount());
                    historyPO.setRemark(userDetailsReqVO.getAccountRemark());
                    historyPO.setCreatedTime(System.currentTimeMillis());
                    historyPO.setUpdatedTime(System.currentTimeMillis());
                    historyPO.setCreator(adminName);
                    historyPO.setUpdater(adminName);
                    historyPO.setSiteCode(userDetailsReqVO.getSiteCode());
                    userDetailsHistoryRemarkRepository.insert(historyPO);
                    //        插入会员信息变更记录表
                    UserInformationChangePO infoChangePO = new UserInformationChangePO();
                    infoChangePO.setOperatingTime(System.currentTimeMillis());
                    infoChangePO.setMemberAccount(result.getUserAccount());
                    infoChangePO.setSiteCode(result.getSiteCode());
                    infoChangePO.setAccountType(result.getAccountType());
                    infoChangePO.setChangeType(userDetailsReqVO.getChangeType());
                    infoChangePO.setInformationBeforeChange(result.getAcountRemark());
                    infoChangePO.setInformationAfterChange(userDetailsReqVO.getAccountRemark());
                    infoChangePO.setSubmitInformation(userDetailsReqVO.getRemark());
                    infoChangePO.setOperator(adminName);// 操作人
                    userInformationChangeRepository.insert(infoChangePO);

                } else if (String.valueOf((UserChangeTypeEnum.FENG_KONG_STATUS.getCode())).equals(changeTypeCode)) {
                    //无需审核,直接添加至风控账号记录中
                    RiskEditReqVO riskEditReqVO = new RiskEditReqVO();
                    riskEditReqVO.setRiskDesc(userDetailsReqVO.getRemark());
                    riskEditReqVO.setCreator(adminName);
                    riskEditReqVO.setCreatorName(adminName);
                    riskEditReqVO.setSiteCode(result.getSiteCode());
                    riskEditReqVO.setRiskControlAccount(userDetailsReqVO.getUserAccount());
                    riskEditReqVO.setRiskLevelId(Long.parseLong(userDetailsReqVO.getRiskLevel()));
                    //创建风控信息时,会去更新user_info数据,所以这里不需要手动upd会员数据了
                    riskCtrlEditApi.submitRiskRecord(riskEditReqVO);

                    //插入会员信息变更记录表
                    UserInformationChangePO infoChangePO = new UserInformationChangePO();
                    infoChangePO.setOperatingTime(System.currentTimeMillis());
                    infoChangePO.setMemberAccount(result.getUserAccount());
                    infoChangePO.setSiteCode(result.getSiteCode());
                    infoChangePO.setAccountType(result.getAccountType());
                    infoChangePO.setChangeType(userDetailsReqVO.getChangeType());
                    infoChangePO.setInformationBeforeChange(result.getRiskLevelId());
                    infoChangePO.setInformationAfterChange(userDetailsReqVO.getRiskLevel());
                    infoChangePO.setSubmitInformation(userDetailsReqVO.getRemark());
                    infoChangePO.setOperator(adminName);// 操作人
                    userInformationChangeRepository.insert(infoChangePO);

                } else if (String.valueOf((UserChangeTypeEnum.HUI_YUAN_STATUS.getCode())).equals(changeTypeCode)) {
                    // 获取所有
                    String memberLabels = ListToString(userDetailsReqVO.getMemberLabel());
                    userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getUserLabelId, memberLabels);
                    //新增标签变化记录
                    SiteUserLabelRecordsPO siteUserLabelRecordsPO = new SiteUserLabelRecordsPO();
                    if (ObjectUtil.isNotEmpty(result.getUserLabelId())) {
                        siteUserLabelRecordsPO.setBeforeChange(result.getUserLabelId());
                    }

                    if (ObjectUtil.isNotEmpty(memberLabels)) {
                        siteUserLabelRecordsPO.setAfterChange(memberLabels);
                    }
                    siteUserLabelRecordsPO.setMemberAccount(result.getUserAccount());
                    siteUserLabelRecordsPO.setAccountType(result.getAccountType());
                    siteUserLabelRecordsPO.setUpdater(String.valueOf(adminId));
                    siteUserLabelRecordsPO.setOperator(adminName);
                    siteUserLabelRecordsPO.setUpdatedTime(System.currentTimeMillis());
                    siteUserLabelRecordsPO.setSiteCode(userDetailsReqVO.getSiteCode());
                    siteUserLabelRecordsPO.setAccountStatus(result.getAccountStatus());
                    siteUserLabelRecordsPO.setRiskControlLevel(result.getRiskLevelId());
                    siteUserLabelRecordRepository.insert(siteUserLabelRecordsPO);
                    // 修改会员表
                    userInfoService.update(userInfoPOLambdaUpdateWrapper);
                    //        插入会员信息变更记录表
                    UserInformationChangePO infoChangePO = new UserInformationChangePO();
                    infoChangePO.setOperatingTime(System.currentTimeMillis());
                    infoChangePO.setMemberAccount(result.getUserAccount());
                    infoChangePO.setSiteCode(result.getSiteCode());
                    infoChangePO.setAccountType(result.getAccountType());
                    infoChangePO.setChangeType(userDetailsReqVO.getChangeType());
                    infoChangePO.setInformationBeforeChange(result.getUserLabelId());
                    infoChangePO.setInformationAfterChange(memberLabels);
                    infoChangePO.setSubmitInformation(userDetailsReqVO.getRemark());
                    infoChangePO.setOperator(adminName);// 操作人
                    userInformationChangeRepository.insert(infoChangePO);
                } else if (String.valueOf((UserChangeTypeEnum.SEX_STATUS.getCode())).equals(changeTypeCode)) {
                    // 性别直接修改成功
                    //修改会员账号备注信息
                    userInfoPOLambdaUpdateWrapper.eq(UserInfoPO::getId, userDetailsReqVO.getId());
                    // 限制只能修改自己的站点会员
                    userInfoPOLambdaUpdateWrapper.eq(UserInfoPO::getSiteCode, userDetailsReqVO.getSiteCode());
                    userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getGender, userDetailsReqVO.getGender());
                    userInfoService.update(userInfoPOLambdaUpdateWrapper);
                    //        插入会员信息变更记录表
                    UserInformationChangePO infoChangePO = new UserInformationChangePO();
                    infoChangePO.setOperatingTime(System.currentTimeMillis());
                    infoChangePO.setMemberAccount(result.getUserAccount());
                    infoChangePO.setSiteCode(result.getSiteCode());
                    infoChangePO.setAccountType(result.getAccountType());
                    infoChangePO.setChangeType(userDetailsReqVO.getChangeType());
                    infoChangePO.setInformationBeforeChange(ObjectUtil.isNotEmpty(result.getGender()) ? String.valueOf(result.getGender()) : "");
                    infoChangePO.setInformationAfterChange(userDetailsReqVO.getGender());
                    infoChangePO.setSubmitInformation(userDetailsReqVO.getRemark());
                    infoChangePO.setOperator(adminName);// 操作人
                    userInformationChangeRepository.insert(infoChangePO);

                } else {
                    // 存在审核中记录
                    List<UserAccountUpdateReviewPO> userAccountUpdateReviewPOS = new LambdaQueryChainWrapper<>(userAccountUpdateReviewRepository).eq(UserAccountUpdateReviewPO::getMemberAccount, userDetailsReqVO.getUserAccount()).eq(UserAccountUpdateReviewPO::getSiteCode, userDetailsReqVO.getSiteCode()).in(UserAccountUpdateReviewPO::getReviewStatus, Lists.newArrayList(CommonConstant.business_one, CommonConstant.business_two)).list();
                    if (CollectionUtil.isNotEmpty(userAccountUpdateReviewPOS)) {
                        return ResponseVO.fail(ResultCode.USER_EXISTS_TO_REVIEWED);
                    }
                    /**
                     *    需要审核的变更类型,同步更新数据到，会员账号修改审核表 ACCOUNT_NUMBER_STATUS
                     */
                    if (StringUtils.isNotBlank(userDetailsReqVO.getAccountStatus()) // 账户状态
                            || StringUtils.isNotBlank(userDetailsReqVO.getDateOfBirth()) // 出生年月
                            || StringUtils.isNotBlank(userDetailsReqVO.getPhoneNumber()) // 手机号码
                            || StringUtils.isNotBlank(userDetailsReqVO.getName()) // 姓名
                            || StringUtils.isNotBlank(userDetailsReqVO.getGender()) // 性别
                            || StringUtils.isNotBlank(userDetailsReqVO.getMail()) // 邮箱
                            || StringUtils.isNotBlank(userDetailsReqVO.getVipRank())  // VIP等级
                            || userDetailsReqVO.getTypingAmount() != null) {
                        // 手机号码是否未空，为空则判断是否有存在同样的手机号码
                        if (StringUtils.isNotBlank(userDetailsReqVO.getPhoneNumber())) {
                            String phoneNumber = userDetailsReqVO.getPhoneNumber();
                            String areaCode = userDetailsReqVO.getAreaCode();
                            if (StringUtils.isNotBlank(phoneNumber)) {
                                if (StringUtils.isBlank(areaCode)) {
                                    return ResponseVO.fail(ResultCode.AREA_CODE_NOT_NULL);
                                }
                            }
                            if (StringUtils.isNotBlank(areaCode)) {
                                if (StringUtils.isBlank(phoneNumber)) {
                                    return ResponseVO.fail(ResultCode.PHONE_NOT_NULL);
                                }
                                // 校验手机长度
                                AreaSiteLangVO areaSiteLangVO = areaSiteManageApi.getAreaInfo(areaCode, userDetailsReqVO.getSiteCode());
                                // 校验手机好吗是否
                                if (UserChecker.checkPhone(phoneNumber)) {
                                    return ResponseVO.fail(ResultCode.PHONE_ERROR);
                                }
                                if (areaSiteLangVO != null) {
                                    if (StringUtils.equals(areaCode, areaSiteLangVO.getAreaCode())) {
                                        // 获取最大和最小长度
                                        Integer max = areaSiteLangVO.getMaxLength();
                                        Integer min = areaSiteLangVO.getMinLength();
                                        // 校验 max 和 min 是否为空
                                        if (max != null && min != null) {
                                            // 校验手机号码长度
                                            if (phoneNumber.length() > max || phoneNumber.length() < min) {
                                                return ResponseVO.fail(ResultCode.PHONE_ERROR);
                                            }
                                        }
                                    }
                                }

                            }
                            LambdaQueryWrapper<UserInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                            lambdaQueryWrapper.eq(UserInfoPO::getPhone, userDetailsReqVO.getPhoneNumber());
                            lambdaQueryWrapper.eq(UserInfoPO::getSiteCode, userDetailsReqVO.getSiteCode());
                            List<UserInfoPO> userInfoPOList = userInfoService.list(lambdaQueryWrapper);
                            if (ObjectUtil.isNotEmpty(userInfoPOList)) {
                                return ResponseVO.fail(ResultCode.PHONE_BOUND);
                            }


                        }
                        // 邮箱是否未空，为空则判断是否有存在同样的邮箱, 校验不区分站点，
                        if (StringUtils.isNotBlank(userDetailsReqVO.getMail())) {
                            LambdaQueryWrapper<UserInfoPO> queryWrapperEmail = new LambdaQueryWrapper<>();
                            queryWrapperEmail.eq(UserInfoPO::getEmail, userDetailsReqVO.getMail());
                            queryWrapperEmail.eq(UserInfoPO::getSiteCode, userDetailsReqVO.getSiteCode());
                            List<UserInfoPO> userEmail = userInfoService.list(queryWrapperEmail);
                            if (ObjectUtil.isNotEmpty(userEmail)) {
                                return ResponseVO.fail(ResultCode.MAIL_BOUND);
                            }
                        }
                        UserAccountUpdateReviewPO po = new UserAccountUpdateReviewPO();
                        // 锁单
                        po.setLockStatus(String.valueOf(LockStatusEnum.UNLOCK.getCode()));
                        // 审核单号  通过会员账号取查询会员审核表
                        LambdaQueryWrapper<UserReviewPO> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(UserReviewPO::getUserAccount, result.getUserAccount());
                        queryWrapper.eq(UserReviewPO::getSiteCode, CurrReqUtils.getSiteCode());
                        List<UserReviewPO> obj = userReviewRepository.selectList(queryWrapper);
                        if (CollectionUtil.isNotEmpty(obj)) {
                            String reviewNo = obj.stream().filter(itm -> itm.getUserAccount().equals(result.getUserAccount())).toList().get(0).getReviewOrderNo();
                            if (StringUtils.isNotBlank(reviewNo)) {
                                po.setReviewOrderNumber(reviewNo);
                            }
                        } else {
                            po.setReviewOrderNumber(UserServerUtil.getUserReviewOrderNo());
                        }
                        //修改前 对应的审核类型的值  userDetailsReqVO
                        if (StringUtils.isNotBlank(userDetailsReqVO.getAccountStatus())) {
                            // 通过会员标签ID获取会员标签name
                            if (ObjectUtil.isNotEmpty(result.getAccountStatus())) {
                                po.setBeforeFixing(result.getAccountStatus());
                            }
                            //修改后 对应的审核类型的值
                            po.setAfterModification(userDetailsReqVO.getAccountStatus());

                        }
                        //修改前 对应的审核类型的值  userDetailsReqVO
                       /* if (StringUtils.isNotBlank(userDetailsReqVO.getAccountRemark())) {
                            // 账号备注
                            if (ObjectUtil.isNotEmpty(result.getAcountRemark())) {
                                po.setBeforeFixing(result.getAcountRemark());
                            }
                            //修改后 申请的备注
                            po.setAfterModification(userDetailsReqVO.getAccountRemark());
                        }*/
                        //增加流水
                        if (userDetailsReqVO.getTypingAmount() != null) {
                            UserTypingAmountVO userTypingAmount = userTypingAmountApi.getUserTypingAmount(result.getUserAccount(), result.getSiteCode());
                            if (userTypingAmount == null) {
                                po.setBeforeFixing("");
                            } else {
                                po.setBeforeFixing(String.valueOf(userTypingAmount.getTypingAmount()));
                            }
                            BigDecimal typingAmount = userDetailsReqVO.getTypingAmount();
                            po.setAfterModification(String.valueOf(typingAmount));
                        }
                        if (StringUtils.isNotBlank(userDetailsReqVO.getVipRank())) {
                            //获取会员的当前vip等级
                            LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
                            query.eq(UserInfoPO::getSiteCode, userDetailsReqVO.getSiteCode()).eq(UserInfoPO::getUserAccount, userDetailsReqVO.getUserAccount());
                            UserInfoPO one = userInfoService.getOne(query);
                            if (one == null) {
                                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                            }
                            // vip等级修改，只能升级，不能降级
                            Integer vipUserRecord = one.getVipGradeCode();
                            Integer vipConfig = Integer.valueOf(userDetailsReqVO.getVipRank());
                            // vip等级修改要大于会员当前等级
                            if (!SiteHandicapModeEnum.China.getCode().equals(userDetailsReqVO.getHandicapMode())) {
                                if (vipConfig <= vipUserRecord) {
                                    return ResponseVO.fail(ResultCode.VIP_GARDE_WRONG);
                                }
                            }

                            // vip等级
                            if (ObjectUtil.isNotEmpty(one.getVipGradeCode())) {
                                po.setBeforeFixing(String.valueOf(one.getVipGradeCode()));
                            }
                            po.setAfterModification(userDetailsReqVO.getVipRank());
                        }
                        //审核申请类型
                        po.setReviewApplicationType(changeTypeCode);
                        if (CollUtil.isNotEmpty(userDetailsReqVO.getMemberLabel())) {
                            // 会员标签
                           /* if (ObjectUtil.isNotEmpty(result.getUserLabelId())) {
                                po.setBeforeFixing(String.valueOf(result.getUserLabelId()));
                            }
                            String memberLabels = ListToString(userDetailsReqVO.getMemberLabel());
                            po.setAfterModification(memberLabels);
                            // 如果修改了会员标签，则需要更改会员标签记录
                            //会员标签  同步更新到会员标签记录表  UserLabelRecordsResVO
                            labelPO.setUpdatedTime(System.currentTimeMillis());//变更时间
                            labelPO.setBeforeChange(String.valueOf(result.getUserLabelId()));//变更前 会员标签名

                            labelPO.setMemberAccount(userDetailsReqVO.getUserAccount());//会员账号
                            labelPO.setAccountType(String.valueOf(result.getAccountType()));//账号类型
                            labelPO.setCreator(adminId);//操作人
                            labelPO.setUpdater(adminId);
                            labelPO.setSiteCode(userDetailsReqVO.getSiteCode());
                            labelPO.setAfterChange(memberLabels);
                            siteUserLabelRecordRepository.insert(labelPO);*/

                        }
                        // 风控层级无需修改
                        if (StringUtils.isNotBlank(userDetailsReqVO.getRiskLevel())) {
                            // 风控层级
                            /*if (ObjectUtil.isNotEmpty(result.getRiskLevelId())) {
                                po.setBeforeFixing(String.valueOf(result.getRiskLevelId()));
                            }
                            po.setAfterModification(userDetailsReqVO.getRiskLevel());*/
                        }
                        // 出生年月
                        if (StringUtils.isNotBlank(userDetailsReqVO.getDateOfBirth())) {
                            po.setBeforeFixing(result.getBirthday());//生日
                            po.setAfterModification(userDetailsReqVO.getDateOfBirth());
                        }
                        if (StringUtils.isNotBlank(userDetailsReqVO.getPhoneNumber())) {
                            if (StringUtils.isNotBlank(result.getPhone())) {
                                po.setBeforeFixing(result.getAreaCode() == null ? "" : result.getAreaCode() + CommonConstant.COMMA + result.getPhone());//手机
                            } else {
                                po.setBeforeFixing("");
                            }
                            po.setAfterModification(userDetailsReqVO.getAreaCode() + CommonConstant.COMMA + userDetailsReqVO.getPhoneNumber());
                        }
                        // 姓名
                        if (StringUtils.isNotBlank(userDetailsReqVO.getName())) {
                            po.setBeforeFixing(result.getUserName());//姓名
                            po.setAfterModification(userDetailsReqVO.getName());
                        }
                        /*if (StringUtils.isNotBlank(userDetailsReqVO.getAccountRemark())) {
                            po.setBeforeFixing(result.getAcountRemark());//备注信息
                            po.setAfterModification(userDetailsReqVO.getAccountRemark());
                        }*/
                        if (StringUtils.isNotBlank(userDetailsReqVO.getGender())) {
                            //性别
                            if (null != result.getGender()) {
                                List<CodeValueVO> genderType = systemParamApi.getSystemParamByType(CommonConstant.USER_GENDER).getData();
                                String genderName = genderType.stream().filter(item -> item.getCode().equals(result.getGender().toString())).toList().get(0).getValue();
                                po.setBeforeFixing(genderName);

                            }
                            List<CodeValueVO> genderType = systemParamApi.getSystemParamByType(CommonConstant.USER_GENDER).getData();
                            String genderNameAfter = genderType.stream().filter(item -> item.getCode().equals(userDetailsReqVO.getGender())).toList().get(0).getValue();
                            po.setAfterModification(genderNameAfter);

                        }
                        if (StringUtils.isNotBlank(userDetailsReqVO.getMail())) {
                            po.setBeforeFixing(result.getEmail());//邮箱
                            po.setAfterModification(userDetailsReqVO.getMail());
                        }
                        // 会员账号
                        po.setMemberAccount(result.getUserAccount());
                        // 账号类型
                        po.setAccountType(String.valueOf(result.getAccountType()));
                        // 申请人
                        po.setApplicant(adminName);
                        //申请时间
                        po.setApplicationTime(System.currentTimeMillis());
                        // 申请信息  存入提交备注
                        po.setApplicationInformation(userDetailsReqVO.getRemark());
                        // 审核状态 处理中
                        po.setReviewStatus(systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_STATUS).getData().get(0).getCode());
                        // 审核操作 一审审核
                        po.setReviewOperation(systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_OPERATION).getData().get(0).getCode());
                        po.setSiteCode(userDetailsReqVO.getSiteCode());
                        // 同步更新到会员账号修改审核表
                        userAccountUpdateReviewRepository.insert(po);
                    }
                    // 同步更新到会员信息变更记录表
                    //userInformationChangeRepository.insert(resInfo);
                }


            }
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("编辑会员信息异常：", e);
            throw new BaowangDefaultException("编辑会员信息异常");
        }
    }

    /***
     * 变更后
     * @param userDetailsReqVO 变更后记录
     * @param labelPO  标签
     * @param resInfo 信息
     * @param resultAfter 返回修改结果
     */
    private void afterTheChange(UserDetailsReqVO userDetailsReqVO, SiteUserLabelRecordsPO labelPO, UserInformationChangePO resInfo, UserInfoPO resultAfter) {
        //变更后信息
        if (StringUtils.isNotBlank(userDetailsReqVO.getAccountStatus())) {
            resInfo.setInformationAfterChange(userDetailsReqVO.getAccountStatus());
        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getRiskLevel())) {
            // 风控层级
            resInfo.setInformationAfterChange(userDetailsReqVO.getRiskLevel());
        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getAccountRemark())) {
            resInfo.setInformationAfterChange(userDetailsReqVO.getAccountRemark());
        }
        if (CollUtil.isNotEmpty(userDetailsReqVO.getMemberLabel())) {
            // 会员标签
            resInfo.setInformationAfterChange(ListToString(userDetailsReqVO.getMemberLabel()));
            labelPO.setAfterChange(String.valueOf(resultAfter.getUserLabelId()));//变更后  会员标签名
            LambdaUpdateWrapper<SiteUserLabelRecordsPO> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(SiteUserLabelRecordsPO::getMemberAccount, userDetailsReqVO.getUserAccount());
            wrapper.set(SiteUserLabelRecordsPO::getAfterChange, ListToString(userDetailsReqVO.getMemberLabel()));
            siteUserLabelRecordRepository.update(null, wrapper);

        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getDateOfBirth())) {
            //将时间戳转换为yyyy-MM-dd时间格式
//            String formattedDateTime =TimeFormat(Long.valueOf(resultAfter.getBirthday()));
            resInfo.setInformationAfterChange(userDetailsReqVO.getDateOfBirth());

        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getPhoneNumber())) {
            resInfo.setInformationAfterChange(userDetailsReqVO.getPhoneNumber());

        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getName())) {
            resInfo.setInformationAfterChange(userDetailsReqVO.getName());

        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getGender())) {
            resInfo.setInformationAfterChange(String.valueOf(userDetailsReqVO.getGender()));

        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getMail())) {
            resInfo.setInformationAfterChange(userDetailsReqVO.getMail());

        }
    }

    private String ListToString(List<String> memberLabelList) {
        return memberLabelList.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public ResponseVO<Boolean> addUserReceiveAccountReview(UserDetailsReqVO vo) {
        UserInfoPO result = userInfoService.getUserInfoPOByAccountOrRegister(vo.getUserAccount(), vo.getSiteCode());
        UserAccountUpdateReviewPO po = new UserAccountUpdateReviewPO();
        po.setReviewOrderNumber(UserServerUtil.getUserReviewOrderNo());
        // 锁单
        po.setLockStatus(String.valueOf(LockStatusEnum.UNLOCK.getCode()));
        po.setReviewApplicationType(vo.getChangeType());
        po.setBeforeFixing(vo.getUserReceiveAccount());
        po.setAfterModification(CommonConstant.CENTER_LINE);
        po.setExtParam(vo.getExtParam());
        // 会员账号
        po.setMemberAccount(result.getUserAccount());
        // 账号类型
        po.setAccountType(String.valueOf(result.getAccountType()));
        // 申请人
        po.setApplicant(CurrReqUtils.getAccount());
        //申请时间
        po.setApplicationTime(System.currentTimeMillis());
        // 申请信息  存入提交备注
//        po.setApplicationInformation(userDetailsReqVO.getRemark());
        // 审核状态 处理中
        po.setReviewStatus(systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_STATUS).getData().get(0).getCode());
        // 审核操作 一审审核
        po.setReviewOperation(systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_OPERATION).getData().get(0).getCode());
        po.setSiteCode(result.getSiteCode());
        // 同步更新到会员账号修改审核表
        userAccountUpdateReviewRepository.insert(po);


        return ResponseVO.success();
    }

    /**
     * 变更前信息
     */
    private void beforeChange(UserDetailsReqVO userDetailsReqVO, UserInfoPO result, UserInformationChangePO resInfo) {
        if (StringUtils.isNotBlank(userDetailsReqVO.getAccountStatus())) {
            resInfo.setInformationBeforeChange(result.getAccountStatus());
        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getRiskLevel())) {
            // 风控层级
            if (null != result.getRiskLevelId()) {
                //  未实现，记得实现
                RiskLevelDetailsVO riskLevelDetailsVO = riskApi.getById(IdVO.builder().id(result.getRiskLevelId()).build());
                if (null != riskLevelDetailsVO) {
                    resInfo.setInformationBeforeChange(riskLevelDetailsVO.getRiskControlLevel());
                }
            }
        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getAccountRemark())) {
            resInfo.setInformationBeforeChange(result.getAcountRemark());
        }
        if (CollUtil.isNotEmpty(userDetailsReqVO.getMemberLabel())) {
            // 会员标签
            if (null != result.getUserLabelId()) {
                List<String> ids = stringToList(result.getUserLabelId());
                List<GetUserLabelByIdsVO> userLabelConfigPOs = userLabelConfigService.getUserLabelByIds(ids);
                StringBuilder userLabel = new StringBuilder();
                if (CollUtil.isNotEmpty(userLabelConfigPOs)) {
                    for (GetUserLabelByIdsVO userLabelConfigPO : userLabelConfigPOs) {
                        userLabel.append(userLabelConfigPO.getLabelName()).append(CommonConstant.COMMA);
                    }
                    userLabel.setLength(userLabel.length() - 1);
                }

                resInfo.setInformationBeforeChange(userLabel.toString());
            }
        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getDateOfBirth())) {
            //将时间戳转换为yyyy-MM-dd时间格式
//            String formattedDateTime =TimeFormat(Long.valueOf(result.getBirthday()));
            resInfo.setInformationBeforeChange(result.getBirthday());
        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getPhoneNumber())) {
            resInfo.setInformationBeforeChange((result.getPhone()));
        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getName())) {
            resInfo.setInformationBeforeChange(result.getUserName());
        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getGender())) {
            resInfo.setInformationBeforeChange(String.valueOf(result.getGender()));
        }
        if (StringUtils.isNotBlank(userDetailsReqVO.getMail())) {
            resInfo.setInformationBeforeChange(result.getEmail());
        }
    }

    private List<String> stringToList(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        // 使用 split 方法将字符串按逗号分隔
        String[] items = str.split(",");

        // 将数组转换为列表
        return Arrays.asList(items);
    }

    public ResponseVO<UserInformationDownVO> getInformationDown(UserInfoDwonReqVO userInfoDwonReqVO) {
        // 会员标签   会员标签-下拉框
        List<GetAllUserLabelVO> list = userLabelConfigService.getAllEnableUserLabelBySiteCode(userInfoDwonReqVO.getSiteCode());
        List<UserLabelVO> userLabel = list.stream().map(item -> UserLabelVO.builder().code(item.getId()).value(item.getLabelName()).customizeStatus(item.getCustomizeStatus()).build()).toList();

        // 会员风险层级下拉框 通过会员账号查询会员列表  userInfoService

        RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
        riskLevelDownReqVO.setSiteCode(userInfoDwonReqVO.getSiteCode());
        riskLevelDownReqVO.setRiskControlType(RiskTypeEnum.RISK_MEMBER.getCode());
        //  封控等级下拉框
        ResponseVO<List<RiskLevelResVO>> riskLevelList = riskApi.getRiskLevelList(riskLevelDownReqVO);
        List<UserRiskVO> riskList = new ArrayList<>();

        for (RiskLevelResVO datum : riskLevelList.getData()) {
            UserRiskVO riskVO = new UserRiskVO();
            riskVO.setRiskLevelId(datum.getId());
            riskVO.setRiskLevel(datum.getRiskControlLevel());
            riskList.add(riskVO);
        }
        // 账号状态-下拉框
        List<CodeValueVO> accountState = systemParamApi.getSystemParamByType(CommonConstant.USER_ACCOUNT_STATUS).getData();

        //vip等级-使用站点的配置code
        List<SiteVIPGradeVO> gradeVOS = vipGradeApi.queryAllVIPGrade(userInfoDwonReqVO.getSiteCode());
        List<CodeValueNoI18VO> vipRank = gradeVOS.stream().map(item -> new CodeValueNoI18VO(String.valueOf(item.getVipGradeCode()), item.getVipGradeName())).collect(Collectors.toList());

        // 男女-下拉框
        List<CodeValueVO> gender = systemParamApi.getSystemParamByType(CommonConstant.USER_GENDER).getData();


        UserInformationDownVO result = new UserInformationDownVO();

        result.setUserLabel(userLabel);
        result.setRiskControlLevel(riskList);
        result.setAccountState(accountState);

        result.setVipRank(vipRank);

        result.setGender(gender);
        return ResponseVO.success(result);
    }

    public ResponseVO<UserListInformationDownVO> getUserListInformationDown(UserInfoListDownReqVO userInfoListDownReqVO) {
        // 会员标签   会员标签-下拉框
        //List<GetAllUserLabelVO> list = userLabelConfigService.getAllUserLabelBySiteCode(userInfoListDownReqVO.getSiteCode());
        List<GetAllUserLabelVO> list = userLabelConfigService.getAllEnableUserLabelBySiteCode(userInfoListDownReqVO.getSiteCode());
        List<UserLabelVO> userLabel = list.stream().map(item ->
                UserLabelVO.builder().code(item.getId().toString()).value(item.getLabelName()).customizeStatus(item.getCustomizeStatus()).build()).toList();
        // 会员风险层级下拉框 通过会员账号查询会员列表  userInfoService
        RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
        riskLevelDownReqVO.setSiteCode(userInfoListDownReqVO.getSiteCode());
        //  封控等级下拉框
        var riskLevelList = riskApi.getRiskLevelList(riskLevelDownReqVO);
        //ResponseVO<List<RiskLevelResVO>> riskLevelList = new ResponseVO<>();
        List<UserRiskVO> riskList = new ArrayList<>();

        for (RiskLevelResVO datum : riskLevelList.getData()) {
            UserRiskVO riskVO = new UserRiskVO();
            riskVO.setRiskLevelId(datum.getId());
            riskVO.setRiskLevel(datum.getRiskControlLevel());
            riskList.add(riskVO);
        }

        // 账号状态-下拉框
        List<CodeValueVO> accountState = systemParamApi.getSystemParamByType(CommonConstant.USER_ACCOUNT_STATUS).getData();
        // 注册终端-下拉框
        List<CodeValueVO> registry = systemParamApi.getSystemParamByType(CommonConstant.USER_REGISTRY).getData();
        // 玩家账号类型-下拉框

        List<CodeValueVO> accountType = systemParamApi.getSystemParamByType(CommonConstant.USER_ACCOUNT_TYPE).getData();

        // 玩家账号类型-下拉框

        List<CodeValueVO> onlineStatus = systemParamApi.getSystemParamByType(CommonConstant.ONLINE_STATUS).getData();


        //VIP段位
        //使用数据库中的会员段位
        List<SiteVIPRankVO> vipRankListBySiteCode = siteVIPRankService.getVipRankListBySiteCode(userInfoListDownReqVO.getSiteCode());

        List<CodeValueVO> vipRankNameEnums = Lists.newArrayList();

        for (SiteVIPRankVO respVo : vipRankListBySiteCode) {
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setType(respVo.getVipRankCode().toString());
            codeValueVO.setCode(respVo.getVipRankCode().toString());
            codeValueVO.setValue(respVo.getVipRankNameI18nCode());
            vipRankNameEnums.add(codeValueVO);
        }
        //会员等级
        List<SiteVIPGradeVO> siteVIPGradeVOS = siteVIPGradeService.queryAllVIPGrade(userInfoListDownReqVO.getSiteCode());
        List<CodeValueNoI18VO> vipGrades = new ArrayList<>();
        for (SiteVIPGradeVO siteVIPGradeVO : siteVIPGradeVOS) {
            CodeValueNoI18VO codeValueNoI18VO = new CodeValueNoI18VO(String.valueOf(siteVIPGradeVO.getVipGradeCode()), siteVIPGradeVO.getVipGradeName());
            vipGrades.add(codeValueNoI18VO);
        }
        vipGrades.sort(Comparator.comparing(v -> {
                    try {
                        return Integer.parseInt(v.getCode());
                    } catch (NumberFormatException e) {
                        return 0; // 对于无法解析为整数的代码，赋予默认值 0
                    }
                }
        ));
        // 自定义排序

        // 男女-下拉框
        UserListInformationDownVO result = new UserListInformationDownVO();
        ResponseVO<List<CodeValueVO>> curryResponseVO = siteApi.chooseCurrency(userInfoListDownReqVO.getSiteCode());
        if (curryResponseVO.isOk()) {
            result.setCurrency(curryResponseVO.getData());
        }
        result.setUserLabel(userLabel);
        result.setRiskControlLevel(riskList);
        result.setAccountState(ConvertUtil.entityListToModelList(accountState, CodeValueVO.class));
        result.setRegistry(registry);
        result.setAccountType(accountType);
        result.setOnlineStatus(onlineStatus);
        result.setVipRank(vipRankNameEnums);
        result.setVipGrade(vipGrades);
        return ResponseVO.success(result);
    }

    public void bachUpdateUserLable(BathUserReqVO vo, String adminId, String adminName) {
        // 获取所有
        String memberLabels = ListToString(vo.getMemberLabel());
        if (CollectionUtils.isEmpty(vo.getUserAccounts())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        List<UserInfoPO> result = userInfoRepository.selectList(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getSiteCode, vo.getSiteCode()).in(UserInfoPO::getUserAccount, vo.getUserAccounts()));
        List<SiteUserLabelRecordsPO> siteUserLabelRecordsPOs = new ArrayList<>();
        List<UserInfoPO> userInfoPOs = new ArrayList<>();
        List<UserInformationChangePO> changePOs = new ArrayList<>();
        result.forEach(e -> {
            String userLabels = memberLabels;
            //新增标签变化记录
            SiteUserLabelRecordsPO siteUserLabelRecordsPO = new SiteUserLabelRecordsPO();
            if (ObjectUtil.isNotEmpty(e.getUserLabelId())) {
                siteUserLabelRecordsPO.setBeforeChange(e.getUserLabelId());
                Set<String> set = new HashSet<>(Arrays.asList(e.getUserLabelId().split(",")));
                set.addAll(vo.getMemberLabel());
                userLabels = set.stream().map(String::valueOf).collect(Collectors.joining(","));
            }
            if (ObjectUtil.isNotEmpty(userLabels)) {
                siteUserLabelRecordsPO.setAfterChange(userLabels);
            }
            siteUserLabelRecordsPO.setMemberAccount(e.getUserAccount());
            siteUserLabelRecordsPO.setAccountType(e.getAccountType());
            siteUserLabelRecordsPO.setUpdater(String.valueOf(adminId));
            siteUserLabelRecordsPO.setOperator(adminName);
            siteUserLabelRecordsPO.setUpdatedTime(System.currentTimeMillis());
            siteUserLabelRecordsPO.setSiteCode(e.getSiteCode());
            siteUserLabelRecordsPO.setAccountStatus(e.getAccountStatus());
            siteUserLabelRecordsPO.setRiskControlLevel(e.getRiskLevelId());
            // 插入会员信息变更记录表
            UserInformationChangePO infoChangePO = new UserInformationChangePO();
            infoChangePO.setOperatingTime(System.currentTimeMillis());
            infoChangePO.setMemberAccount(e.getUserAccount());
            infoChangePO.setSiteCode(e.getSiteCode());
            infoChangePO.setAccountType(e.getAccountType());
            infoChangePO.setChangeType(String.valueOf((UserChangeTypeEnum.HUI_YUAN_STATUS.getCode())));
            infoChangePO.setInformationBeforeChange(e.getUserLabelId());
            infoChangePO.setInformationAfterChange(userLabels);
            infoChangePO.setOperator(adminName);// 操作人
            // 修改会员表
            e.setUserLabelId(userLabels);
            siteUserLabelRecordsPOs.add(siteUserLabelRecordsPO);
            userInfoPOs.add(e);
            changePOs.add(infoChangePO);
        });
        if (CollectionUtils.isNotEmpty(changePOs) && CollectionUtils.isNotEmpty(siteUserLabelRecordsPOs) && CollectionUtils.isNotEmpty(userInfoPOs)) {
            userInfoService.saveOrUpdateBatch(userInfoPOs);
            userInformationChangePOService.saveBatch(changePOs);
            siteUserLabelRecordService.saveBatch(siteUserLabelRecordsPOs);
        }
    }

    public ResponseVO<String> checkUsers(CheckUserReqVO vo) {
        List<String> list = Arrays.asList(vo.getUsers().split(","));
        if (StringUtils.isBlank(vo.getUsers())) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        // 获取所有用户
        List<UserInfoPO> result = userInfoRepository.selectList(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getSiteCode, vo.getSiteCode()).in(UserInfoPO::getUserAccount, list));
        if (CollectionUtil.isEmpty(result)) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        } else {
            // 使用Stream提取ID并拼接成字符串
            return ResponseVO.success(result.stream()
                    .map(UserInfoPO::getUserAccount)
                    .map(Object::toString)
                    .collect(Collectors.joining(",")));
        }
    }


    public ResponseVO<List<UserLabelVO>> getBathUsersLabel(CheckUserReqVO vo) {
        List<String> userlist = Arrays.asList(vo.getUsers().split(","));
        if (StringUtils.isBlank(vo.getUsers())) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        List<UserInfoPO> users = userInfoRepository.selectList(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getSiteCode, vo.getSiteCode()).in(UserInfoPO::getUserAccount, userlist));
        if (CollectionUtil.isEmpty(users)) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        // 提取所有 tags，按逗号分割，去重，并收集成 List
        List<String> userLabelIds = users.stream()
                .map(UserInfoPO::getUserLabelId)          // 提取 tags 字符串
                .flatMap(tags -> Arrays.stream(tags.split(",")))  // 按逗号分割并扁平化
                .map(String::trim)             // 去除前后空格（可选）
                .filter(tag -> !tag.isEmpty()) // 过滤空字符串（可选）
                .distinct()                    // 去重
                .collect(Collectors.toList()); // 收集成 List
        //获取站点会员标签列表
        List<GetUserLabelByIdsVO> list = userLabelConfigService.getUserLabelByIds(userLabelIds);
        List<UserLabelVO> userLabel = list.stream().map(item ->
                UserLabelVO.builder().code(item.getId().toString()).value(item.getLabelName()).customizeStatus(item.getCustomizeStatus()).build()).toList();
        return ResponseVO.success(userLabel);
    }

    public void bathDeleteLabel(BathUserReqVO vo, String adminId, String adminName) {
        // 获取所有
        String memberLabels = ListToString(vo.getMemberLabel());
        if (CollectionUtils.isEmpty(vo.getUserAccounts())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        List<UserInfoPO> result = userInfoRepository.selectList(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getSiteCode, vo.getSiteCode()).in(UserInfoPO::getUserAccount, vo.getUserAccounts()));
        List<SiteUserLabelRecordsPO> siteUserLabelRecordsPOs = new ArrayList<>();
        List<UserInfoPO> userInfoPOs = new ArrayList<>();
        List<UserInformationChangePO> changePOs = new ArrayList<>();
        for (UserInfoPO e : result) {
            String userLabels = memberLabels;
            //新增标签变化记录
            SiteUserLabelRecordsPO siteUserLabelRecordsPO = new SiteUserLabelRecordsPO();
            if (ObjectUtil.isEmpty(e.getUserLabelId()) || StringUtils.isEmpty(memberLabels)) {
                continue;
            }
            List<String> beforedata = Arrays.asList(e.getUserLabelId().split(","));
            List<String> memberLabel = vo.getMemberLabel();
            List<String> data = beforedata.stream()
                    .filter(element -> !memberLabel.contains(element))
                    .collect(Collectors.toList());

            if (data.size() == beforedata.size()) {
                log.info("用户{},删除前标签，删除后标签相同", e.getUserAccount());
                continue;
            }
            siteUserLabelRecordsPO.setBeforeChange(e.getUserLabelId());
            userLabels = data.stream().map(String::valueOf).collect(Collectors.joining(","));
            siteUserLabelRecordsPO.setAfterChange(userLabels);
            siteUserLabelRecordsPO.setMemberAccount(e.getUserAccount());
            siteUserLabelRecordsPO.setAccountType(e.getAccountType());
            siteUserLabelRecordsPO.setUpdater(String.valueOf(adminId));
            siteUserLabelRecordsPO.setOperator(adminName);
            siteUserLabelRecordsPO.setUpdatedTime(System.currentTimeMillis());
            siteUserLabelRecordsPO.setSiteCode(e.getSiteCode());
            siteUserLabelRecordsPO.setAccountStatus(e.getAccountStatus());
            siteUserLabelRecordsPO.setRiskControlLevel(e.getRiskLevelId());
            // 插入会员信息变更记录表
            UserInformationChangePO infoChangePO = new UserInformationChangePO();
            infoChangePO.setOperatingTime(System.currentTimeMillis());
            infoChangePO.setMemberAccount(e.getUserAccount());
            infoChangePO.setSiteCode(e.getSiteCode());
            infoChangePO.setAccountType(e.getAccountType());
            infoChangePO.setChangeType(String.valueOf((UserChangeTypeEnum.HUI_YUAN_STATUS.getCode())));
            infoChangePO.setInformationBeforeChange(e.getUserLabelId());
            infoChangePO.setInformationAfterChange(userLabels);
            infoChangePO.setOperator(adminName);// 操作人
            // 修改会员表
            e.setUserLabelId(userLabels);
            siteUserLabelRecordsPOs.add(siteUserLabelRecordsPO);
            userInfoPOs.add(e);
            changePOs.add(infoChangePO);
        }
        if (CollectionUtils.isNotEmpty(changePOs) && CollectionUtils.isNotEmpty(siteUserLabelRecordsPOs) && CollectionUtils.isNotEmpty(userInfoPOs)) {
            userInfoService.saveOrUpdateBatch(userInfoPOs);
            userInformationChangePOService.saveBatch(changePOs);
            siteUserLabelRecordService.saveBatch(siteUserLabelRecordsPOs);
        }
    }

    public void bathUpdateRemark(BathUserRemarkReqVO requestVO) {
        if (CollectionUtils.isEmpty(requestVO.getUserAccounts())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserInfoPO::getUserAccount, requestVO.getUserAccounts());
        queryWrapper.eq(UserInfoPO::getSiteCode, CurrReqUtils.getSiteCode());
        //新增账号备注记录表
        List<UserInfoPO> userInfoPOS = userInfoRepository.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(userInfoPOS)) {
            return;
        }
        List<String> ids = userInfoPOS.stream().map(UserInfoPO::getId).toList();
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        LambdaUpdateWrapper<UserInfoPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserInfoPO::getAcountRemark, requestVO.getRemark());
        updateWrapper.set(UserInfoPO::getUpdater, requestVO.getOperator());
        updateWrapper.set(UserInfoPO::getUpdatedTime, System.currentTimeMillis());
        updateWrapper.in(UserInfoPO::getId, ids);
        updateWrapper.eq(UserInfoPO::getSiteCode, requestVO.getSiteCode());
        userInfoRepository.update(null, updateWrapper);

        List<UserChangeTypeHistoryRecordPO> historyRecordPOS = new ArrayList<>();
        List<UserInformationChangePO> infoChangePOS = new ArrayList<>();
        for (UserInfoPO userInfoPO : userInfoPOS) {
            UserChangeTypeHistoryRecordPO historyPO = new UserChangeTypeHistoryRecordPO();
            historyPO.setMemberAccount(userInfoPO.getUserAccount());
            historyPO.setRemark(requestVO.getRemark());
            historyPO.setSiteCode(requestVO.getSiteCode());
            historyPO.setCreatedTime(System.currentTimeMillis());
            historyPO.setUpdatedTime(System.currentTimeMillis());
            historyPO.setCreator(requestVO.getOperator());
            historyPO.setUpdater(requestVO.getOperator());
            historyRecordPOS.add(historyPO);
            //
            UserInformationChangePO infoChangePO = new UserInformationChangePO();
            infoChangePO.setOperatingTime(System.currentTimeMillis());
            infoChangePO.setMemberAccount(userInfoPO.getUserAccount());
            infoChangePO.setSiteCode(userInfoPO.getSiteCode());
            infoChangePO.setAccountType(userInfoPO.getAccountType());
            infoChangePO.setChangeType(String.valueOf(UserChangeTypeEnum.ACCOUNT_NUMBER_STATUS.getCode()));
            infoChangePO.setInformationBeforeChange(userInfoPO.getAcountRemark());
            infoChangePO.setInformationAfterChange(requestVO.getRemark());
            infoChangePO.setSubmitInformation(null);
            infoChangePO.setOperator(requestVO.getOperator());
            infoChangePOS.add(infoChangePO);// 操作人
        }
        if (CollectionUtil.isNotEmpty(historyRecordPOS)) {
            userDetailsHistoryRemarkService.saveBatch(historyRecordPOS);
        }
        if (CollectionUtil.isNotEmpty(infoChangePOS)) {
            userInformationChangePOService.saveBatch(infoChangePOS);

        }


    }

    public List<UserAccountUpdateReviewResVO> getReviewingList(List<String> ids) {
        LambdaQueryWrapper<UserAccountUpdateReviewPO> lqw = new LambdaQueryWrapper<>();
        lqw.in(UserAccountUpdateReviewPO::getExtParam, ids);
        lqw.in(UserAccountUpdateReviewPO::getReviewStatus, Lists.newArrayList(CommonConstant.business_one, CommonConstant.business_two));
        List<UserAccountUpdateReviewPO> userAccountUpdateReviewPOS = userAccountUpdateReviewRepository.selectList(lqw);
        return ConvertUtil.entityListToModelList(userAccountUpdateReviewPOS, UserAccountUpdateReviewResVO.class);
    }
}

