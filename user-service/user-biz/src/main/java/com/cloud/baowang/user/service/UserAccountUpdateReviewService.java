package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.vo.order.OrderInfoVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipMedalRewardReceiveApi;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.user.api.enums.UserChangeTypeEnum;
import com.cloud.baowang.user.api.enums.UserConorRouterConstants;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.user.api.vo.ReviewVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewDetailsResVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewReqVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.user.request.UserQueryVO;
import com.cloud.baowang.user.api.vo.userlabel.GetAllUserLabelVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordRequestVO;
import com.cloud.baowang.user.po.*;
import com.cloud.baowang.user.repositories.*;
import com.cloud.baowang.user.util.TokenUtil;
import com.cloud.baowang.wallet.api.api.UserReceiveAccountApi;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserDepositWithdrawVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserAccountUpdateReviewService extends ServiceImpl<UserAccountUpdateReviewRepository, UserAccountUpdateReviewPO> {


    private final UserAccountUpdateReviewRepository userAccountUpdateReviewRepository;
    private final UserDetailsHistoryRemarkRepository historyRemarkRepository;

    private final SystemParamApi systemParamApi;

    private final OrderRecordApi orderRecordApi;

    private final SiteUserLabelConfigService userLabelConfigService;

    private final UserInfoRepository userInfoRepository;

    private final UserInformationChangeRepository userInformationChangeRepository;

    private final SiteUserLabelRecordRepository siteUserLabelRecordRepository;

    private final RiskApi riskApi;

    private final VipGradeApi vipGradeApi;

    private final UserVIPFlowRecordRepository userVIPFlowRecordRepository;
    private final SiteVipChangeRecordService changeRecordService;

    private final UserWithdrawRecordApi userWithdrawRecordApi;

    private final SiteVIPRankRepository siteVIPRankRepository;
    private final SiteVIPGradeRepository siteVIPGradeRepository;
    private final UserTypingAmountApi userTypingAmountApi;

    private final VipMedalRewardReceiveApi vipMedalRewardReceiveApi;

    private final SiteMedalInfoService siteMedalInfoService;

    private final VipMedalRewardReceiveService rewardReceiveService;

    private final UserInfoService userInfoService;

    private final UserVipFlowRecordCnService userVipFlowRecordCnService;

    private final UserReceiveAccountApi userReceiveAccountApi;

    public Page<UserAccountUpdateReviewResVO> getUserAccountUpdateReview(UserAccountUpdateReviewReqVO vo) {
        try {
            String siteCode = vo.getSiteCode();
            Page<UserAccountUpdateReviewResVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
            String adminName = vo.getAdminName();
            Page<UserAccountUpdateReviewResVO> pageResult = userAccountUpdateReviewRepository.getReviewPage(page, vo, adminName);
            List<CodeValueVO> reviewApplicationType = systemParamApi.getSystemParamByType(CommonConstant.USER_CHANGE_TYPE).getData();
            //账号状态
            List<CodeValueVO> accountStatus = systemParamApi.getSystemParamByType(CommonConstant.USER_ACCOUNT_STATUS).getData();
            RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
            riskLevelDownReqVO.setRiskControlType("1");
            ResponseVO<List<RiskLevelResVO>> riskLevelList = riskApi.getRiskLevelList(riskLevelDownReqVO);
            List<GetAllUserLabelVO> labelVOS = userLabelConfigService.getAllEnableUserLabelBySiteCode(vo.getSiteCode());

            List<CodeValueVO> reviewOperations = systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_OPERATION).getData();
            List<CodeValueVO> accountType = systemParamApi.getSystemParamByType(CommonConstant.USER_ACCOUNT_TYPE).getData();
            List<CodeValueVO> reviewStatus = systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_REVIEW_STATUS).getData();
            String sexManName = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.USER_GENDER, "1");
            String sexFemaleName = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.USER_GENDER, "2");
            for (UserAccountUpdateReviewResVO record : pageResult.getRecords()) {
                if (ObjectUtil.isNotEmpty(record.getApplicant())) {
                    if (record.getApplicant().equals(vo.getAdminName())) {
                        record.setIsApplicant("1");
                    }
                }
                if (StringUtils.isNotBlank(record.getReviewApplicationType())) {
                    reviewApplicationType.forEach(systemParamVO -> {
                        if (systemParamVO.getCode().equals(record.getReviewApplicationType())) {
                            record.setReviewApplicationTypeName(systemParamVO.getValue());
                        }
                    });
                    record.setApplicationInfo(record.getApplicationInfo());
                    StringBuilder beforeName = new StringBuilder();
                    StringBuilder afterName = new StringBuilder();
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.ZHANG_HAO_STATUS.getCode()))) {
                        if (ObjectUtil.isNotEmpty(record.getBeforeFixing())) {
                            String[] before = record.getBeforeFixing().split(",");
                            for (String be : before) {
                                for (CodeValueVO res : accountStatus) {
                                    if (be.equals(res.getCode())) {
                                        beforeName.append(I18nMessageUtil.getI18NMessage(res.getValue())).append(",");
                                    }
                                }
                            }
                            record.setBeforeFixing(beforeName.substring(0, beforeName.length() - 1));
                        }
                        if (ObjectUtil.isNotEmpty(record.getAfterModification())) {
                            String[] after = record.getAfterModification().split(",");
                            for (String be : after) {
                                for (CodeValueVO res : accountStatus) {
                                    if (be.equals(res.getCode())) {
                                        afterName.append(I18nMessageUtil.getI18NMessage(res.getValue())).append(",");
                                    }
                                }
                            }
                            if (StringUtils.isNotBlank(afterName)) {
                                String afterModification = afterName.substring(0, afterName.length() - 1);
                                record.setAfterModification(afterModification);
                            } else {
                                // 处理空字符串的情况，例如设置默认值或记录日志
                                record.setAfterModification("");  // 或其他适当的默认值
                            }
                        }
                    }
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.FENG_KONG_STATUS.getCode()))) {
                        riskLevelList.getData().forEach(datum -> {
                            if (ObjectUtil.isNotEmpty(record.getBeforeFixing())) {
                                if (record.getBeforeFixing().equals(String.valueOf(datum.getId()))) {
                                    record.setBeforeFixing(datum.getRiskControlLevel());
                                }
                            }
                            if (ObjectUtil.isNotEmpty(record.getAfterModification())) {
                                if (record.getAfterModification().equals(String.valueOf(datum.getId()))) {
                                    record.setAfterModification(datum.getRiskControlLevel());
                                }
                            }
                        });
                    }
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.ADD_TYPING.getCode()))) {
                        BigDecimal before = BigDecimal.ZERO;
                        String beforeFixing = record.getBeforeFixing();
                        String afterModification = record.getAfterModification();

                        if (StringUtils.isNotBlank(beforeFixing)) {
                            before = new BigDecimal(beforeFixing);
                        }
                        if (StringUtils.isNotBlank(afterModification)) {
                            BigDecimal after = new BigDecimal(afterModification);
                            after = after.add(before);
                            record.setAfterModification(String.valueOf(after));
                        }
                    }
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.HUI_YUAN_STATUS.getCode()))) {
                        if (StringUtils.isNotBlank(record.getBeforeFixing())) {
                            String[] accountLabelList = record.getBeforeFixing().split(",");
                            StringJoiner before = new StringJoiner(",");
                            for (String status : accountLabelList) {
                                for (GetAllUserLabelVO labelVO : labelVOS) {
                                    if (status.equals(labelVO.getId())) {
                                        before.add(labelVO.getLabelName());
                                    }
                                }
                            }
                            record.setBeforeFixing(before.toString());//修改前
                        }
                        StringJoiner after = new StringJoiner(",");
                        String[] strings = record.getAfterModification().split(",");
                        for (String str : strings) {
                            for (GetAllUserLabelVO labelVO : labelVOS) {
                                if (str.equals(labelVO.getId())) {
                                    after.add(labelVO.getLabelName());
                                }
                            }

                        }
                        record.setAfterModification(after.toString());//修改前
                    }
                    //
                    if (String.valueOf(UserChangeTypeEnum.VIP_RANK_STATUS.getCode()).equals(record.getReviewApplicationType())) {
                        //会员等级变更的，code转一下name
                        String beforeFixing = record.getBeforeFixing();
                        String afterModification = record.getAfterModification();
                        List<String> gradeCodeArr = new ArrayList<>();
                        gradeCodeArr.add(beforeFixing);
                        gradeCodeArr.add(afterModification);
                        LambdaQueryWrapper<SiteVIPGradePO> beAfQuery = Wrappers.lambdaQuery();
                        beAfQuery.eq(SiteVIPGradePO::getSiteCode, vo.getSiteCode()).in(SiteVIPGradePO::getVipGradeCode, gradeCodeArr);
                        List<SiteVIPGradePO> siteVIPGradePOS = siteVIPGradeRepository.selectList(beAfQuery);
                        // 转换 List<SiteVIPGradePO> 为 Map<String, String>
                        Map<Integer, String> codeNameMap = siteVIPGradePOS.stream()
                                .collect(Collectors.toMap(SiteVIPGradePO::getVipGradeCode, SiteVIPGradePO::getVipGradeName));
                        record.setBeforeFixing(codeNameMap.get(Integer.parseInt(record.getBeforeFixing())));//修改前
                        record.setAfterModification(codeNameMap.get(Integer.parseInt(record.getAfterModification())));//修改后


                    }
                    if (String.valueOf(UserChangeTypeEnum.SEX_STATUS.getCode()).equals(record.getReviewApplicationType())) {
                        // 男女，code转一下name
                        String beforeFixing = record.getBeforeFixing();
                        String afterModification = record.getAfterModification();
                        // 男女进行翻译
                        record.setBeforeFixing(getSexName(beforeFixing, sexManName, sexFemaleName));//修改前
                        record.setAfterModification(getSexName(afterModification, sexManName, sexFemaleName));//修改后

                    }

                }
                // todo
                if (vo.getDataDesensitization()) {
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.PHONE_STATUS.getCode()))) {
                        record.setBeforeFixing(SymbolUtil.showPhone(record.getBeforeFixing()));
                        record.setAfterModification(SymbolUtil.showPhone(record.getAfterModification()));
                    }
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.EMAIL_STATUS.getCode()))) {
                        record.setBeforeFixing(SymbolUtil.showEmail(record.getBeforeFixing()));
                        record.setAfterModification(SymbolUtil.showEmail(record.getAfterModification()));
                    }
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.NAME_STATUS.getCode()))) {
                        record.setBeforeFixing(SymbolUtil.showUserName(record.getBeforeFixing()));
                        record.setAfterModification(SymbolUtil.showUserName(record.getAfterModification()));
                    }
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.BANK_CARD_UN_BIND.getCode()))) {
                        String[] beforeChangeArr = record.getBeforeFixing().split("\\|");
                        String backCard =  SymbolUtil.showBankOrVirtualNo(beforeChangeArr[1]);
                        beforeChangeArr[1] = backCard;
                        record.setBeforeFixing(String.join("|",beforeChangeArr));
                    }
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.ELECTRONIC_WALLET_UN_BIND.getCode()))) {
                        String[] beforeChangeArr = record.getBeforeFixing().split("\\|");
                        String userName = "";
                        String addressNo = "";
                        String electronicAccount = "";
                        if(beforeChangeArr.length == CommonConstant.business_three){
                            userName = SymbolUtil.showUserName(beforeChangeArr[0]);
                            beforeChangeArr[0] = userName;
                            electronicAccount = SymbolUtil.showWalletNo(beforeChangeArr[1]);
                            beforeChangeArr[1] = electronicAccount;
                            addressNo = SymbolUtil.showWalletNo(beforeChangeArr[2]);
                            beforeChangeArr[2] = addressNo;

                        }else{
                            userName = SymbolUtil.showUserName(beforeChangeArr[1]);
                            beforeChangeArr[1] = userName;
                            electronicAccount = SymbolUtil.showWalletNo(beforeChangeArr[2]);
                            beforeChangeArr[2] = electronicAccount;
                            addressNo = SymbolUtil.showWalletNo(beforeChangeArr[3]);
                            beforeChangeArr[3] = addressNo;
                        }
                        record.setBeforeFixing(String.join("|",beforeChangeArr));
                    }
                    if (record.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.CRYPTO_CURRENCY_UN_BIND.getCode()))) {
                        String[] beforeChangeArr = record.getBeforeFixing().split("\\|");
                        String addressNo =  SymbolUtil.showBankOrVirtualNo(beforeChangeArr[1]);
                        beforeChangeArr[1] = addressNo;
                        record.setBeforeFixing(String.join("|",beforeChangeArr));
                    }
                }

                //手机号码变更的，列表展示的时候，移除掉拼接的逗号
                if (String.valueOf(UserChangeTypeEnum.PHONE_STATUS.getCode()).equals(record.getReviewApplicationType())) {
                    String beforeFixing = record.getBeforeFixing();
                    String afterModification = record.getAfterModification();
                    if (StringUtils.isNotBlank(beforeFixing)) {
                        record.setBeforeFixing(beforeFixing.replaceAll(CommonConstant.COMMA, ""));
                    }
                    if (StringUtils.isNotBlank(afterModification)) {
                        record.setAfterModification(afterModification.replaceAll(CommonConstant.COMMA, ""));
                    }
                }
                // 审核操作 1一审审核 2结单查看
                if (null != record.getReviewOperation()) {
                    reviewOperations.forEach(reviewOperation -> {
                        if (reviewOperation.getCode().equals(record.getReviewOperation())) {
                            record.setReviewOperationName(reviewOperation.getValue());
                        }
                    });
                }

                // 审核状态 1待处理 2处理中 3审核通过 4一审拒绝
                if (null != record.getReviewStatus()) {
                    String reviewOperationName = reviewStatus.stream().filter(item ->
                            item.getCode().equals(record.getReviewStatus())
                    ).toList().get(0).getValue();
                    record.setReviewStatusName(reviewOperationName);
                }
                // 锁单人是否当前登录人 0否 1是
                // 前端先判断locker，再判断isLocker
                if (StrUtil.isNotEmpty(record.getLocker())) {
                    if (record.getLocker().equals(vo.getAdminName())) {
                        record.setIsLocker(CommonConstant.business_one);
                    } else {
                        record.setIsLocker(CommonConstant.business_zero);
                    }
                }
            }
            return pageResult;
        } catch (Exception e) {
            log.error("会员账户修改审核列表异常", e);
            throw new BaowangDefaultException("会员账户修改审核列表异常");
        }
    }

    public String getSexName(String sex, String sexManName, String sexFemaleName) {
        if (StringUtils.isBlank(sex)) {
            return "";
        }
        return switch (sex) {
            case "1" -> sexManName;
            case "2" -> sexFemaleName;
            default -> "";
        };
    }

    public ResponseVO getLock(IdVO vo, String adminId, String adminName) {
        // 获取参数
        String id = vo.getId();
        //UserAccountUpdateReviewPO userReview = this.getById(id);
        UserAccountUpdateReviewPO userReview = this.getOne(new LambdaQueryWrapper<UserAccountUpdateReviewPO>()
                .eq(UserAccountUpdateReviewPO::getId, id)
                .eq(UserAccountUpdateReviewPO::getSiteCode, vo.getSiteCode()));
        if (null == userReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        if (userReview.getApplicant().equals(adminName)) {
            return ResponseVO.fail(ResultCode.WRONG_OPERATION);
        }
        if (userReview.getReviewStatus().equals(String.valueOf(CommonConstant.business_four)) ||
                userReview.getReviewStatus().equals(String.valueOf(CommonConstant.business_three))) {
            return ResponseVO.fail(ResultCode.AUDITED);
        }
        if (userReview.getReviewStatus().equals(String.valueOf(CommonConstant.business_two))
                && userReview.getLockStatus().equals(String.valueOf(CommonConstant.business_one))
                && !userReview.getUpdater().equals(adminId)) {
            return ResponseVO.fail(ResultCode.LOCKED);
        }
        boolean res = false;
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.USER_REVIEW_ORDER_NO + userReview.getReviewOrderNumber());
        try {
            res = fairLock.tryLock(0, 50, TimeUnit.SECONDS);
            if (res) {
                // 业务操作
                lockOperate(id, userReview, adminId, adminName);
                fairLock.unlock();
                return ResponseVO.success();
            } else {
                return ResponseVO.fail(ResultCode.USER_REVIEW_ALREADY_LOCK_ERROR);
            }
        } catch (Exception e) {
            log.error("新增会员审核-锁单/解锁 error,审核单号:{},操作人:{}", e);
            if (res) {
                fairLock.unlock();
            }
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private void lockOperate(String id, UserAccountUpdateReviewPO userReview, String adminId, String adminName) {
        Integer myLockStatus;
        Integer myReviewStatus;
        // 锁单人
        String locker;
        if (CommonConstant.business_zero.toString().equals(userReview.getLockStatus())) {
            // 锁单
            myLockStatus = CommonConstant.business_one;
            myReviewStatus = CommonConstant.business_two;
            locker = adminName;
        } else {
            // 解锁
            myLockStatus = CommonConstant.business_zero;
            myReviewStatus = CommonConstant.business_one;
            locker = null;
        }

        LambdaUpdateWrapper<UserAccountUpdateReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserAccountUpdateReviewPO::getId, id)
                .eq(UserAccountUpdateReviewPO::getSiteCode, userReview.getSiteCode())
                .set(UserAccountUpdateReviewPO::getLockStatus, myLockStatus)
                .set(UserAccountUpdateReviewPO::getLocker, locker)
                .set(UserAccountUpdateReviewPO::getReviewStatus, myReviewStatus)
                .set(UserAccountUpdateReviewPO::getUpdater, adminId)
                .set(UserAccountUpdateReviewPO::getUpdatedTime, System.currentTimeMillis());

        this.update(null, lambdaUpdate);
    }

    public ResponseVO firstReviewFail(ReviewVO vo, String adminId, String adminName) {
        // 获取参数
        String id = vo.getId();
        String reviewRemark = vo.getReviewRemark();

        //UserAccountUpdateReviewPO userReview = this.getById(id);
        UserAccountUpdateReviewPO userReview = this.getOne(new LambdaQueryWrapper<UserAccountUpdateReviewPO>()
                .eq(UserAccountUpdateReviewPO::getId, id)
                .eq(UserAccountUpdateReviewPO::getSiteCode, vo.getSiteCode()));
        if (null == userReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        if (userReview.getApplicant().equals(adminName)) {
            return ResponseVO.fail(ResultCode.WRONG_OPERATION);
        }
        LambdaUpdateWrapper<UserAccountUpdateReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserAccountUpdateReviewPO::getId, id)
                .eq(UserAccountUpdateReviewPO::getSiteCode, vo.getSiteCode())
                .set(UserAccountUpdateReviewPO::getFirstReviewTime, System.currentTimeMillis())
                .set(UserAccountUpdateReviewPO::getFirstInstance, adminName)
                .set(UserAccountUpdateReviewPO::getReviewRemark, reviewRemark)
                .set(UserAccountUpdateReviewPO::getReviewOperation, CommonConstant.business_two)
                .set(UserAccountUpdateReviewPO::getReviewStatus, CommonConstant.business_four)
                .set(UserAccountUpdateReviewPO::getLockStatus, CommonConstant.business_zero)
                .set(UserAccountUpdateReviewPO::getLocker, null)

                .set(UserAccountUpdateReviewPO::getUpdater, adminId)
                .set(UserAccountUpdateReviewPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);

        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO fristReviewSuccess(ReviewVO vo, String registerIp, String adminId, String adminName) {
        //UserAccountUpdateReviewPO userReview = this.getById(vo.getId());
        UserAccountUpdateReviewPO userReview = this.getOne(new LambdaQueryWrapper<UserAccountUpdateReviewPO>()
                .eq(UserAccountUpdateReviewPO::getId, vo.getId())
                .eq(UserAccountUpdateReviewPO::getSiteCode, vo.getSiteCode()));
        String siteCode = vo.getSiteCode();
        if (null == userReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        if (ReviewOperationEnum.CHECK.getCode().equals(Integer.parseInt(userReview.getReviewOperation()))) {
            throw new BaowangDefaultException(ResultCode.AUDITED);
        }
        if (userReview.getApplicant().equals(adminName)) {
            return ResponseVO.fail(ResultCode.WRONG_OPERATION);
        }
        LambdaQueryWrapper<UserInfoPO> userQuery = Wrappers.lambdaQuery();
        userQuery.eq(UserInfoPO::getSiteCode, userReview.getSiteCode()).eq(UserInfoPO::getUserAccount, userReview.getMemberAccount());
        UserInfoPO userInfoPO = userInfoRepository.selectOne(userQuery);
        if (userInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        LambdaUpdateWrapper<UserAccountUpdateReviewPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(UserAccountUpdateReviewPO::getId, vo.getId())
                .eq(UserAccountUpdateReviewPO::getSiteCode, vo.getSiteCode())
                .set(UserAccountUpdateReviewPO::getFirstReviewTime, System.currentTimeMillis())
                .set(UserAccountUpdateReviewPO::getFirstInstance, adminName)
                .set(UserAccountUpdateReviewPO::getReviewRemark, vo.getReviewRemark())
                .set(UserAccountUpdateReviewPO::getReviewOperation, ReviewOperationEnum.CHECK.getCode())
                .set(UserAccountUpdateReviewPO::getReviewStatus, ReviewStatusEnum.REVIEW_PASS.getCode())
                .set(UserAccountUpdateReviewPO::getLockStatus, LockStatusEnum.UNLOCK.getCode())
                .set(UserAccountUpdateReviewPO::getLocker, null)
                .set(UserAccountUpdateReviewPO::getUpdater, adminId)
                .set(UserAccountUpdateReviewPO::getUpdatedTime, System.currentTimeMillis());

            /*//增加流水
            if (String.valueOf(UserChangeTypeEnum.ADD_TYPING.getCode()).equals(userReview.getReviewApplicationType())) {
                WithdrawRunningWaterAddVO withdrawRunningWaterAddVO = new WithdrawRunningWaterAddVO();
                withdrawRunningWaterAddVO.setUserAccount(userReview.getMemberAccount());
                String afterModification = userReview.getAfterModification();
                if (StringUtils.isNotBlank(afterModification)) {
                    withdrawRunningWaterAddVO.setAddTypingAmount(new BigDecimal(afterModification));
                }
                withdrawRunningWaterAddVO.setSiteCode(userReview.getSiteCode());
                withdrawRunningWaterAddVO.setRemark(userReview.getReviewRemark());
                withdrawRunningWaterAddVO.setUserId(userInfoPO.getUserId());
                userTypingAmountApi.addWithdrawRunningWater(withdrawRunningWaterAddVO);
            }*/

        //会员变更-风控信息变更
        if (String.valueOf((UserChangeTypeEnum.FENG_KONG_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
            RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
            riskLevelDownReqVO.setSiteCode(vo.getSiteCode());
            riskLevelDownReqVO.setRiskControlType(RiskTypeEnum.RISK_MEMBER.getCode());
            //获取风控层级
            ResponseVO<List<RiskLevelResVO>> riskLevelList = riskApi.getRiskLevelList(riskLevelDownReqVO);
            RiskAccountQueryVO riskAccountQueryVO = new RiskAccountQueryVO();
            riskAccountQueryVO.setRiskControlAccount(userReview.getMemberAccount());
            riskAccountQueryVO.setRiskControlTypeCode(RiskTypeEnum.RISK_MEMBER.getCode());
            riskAccountQueryVO.setSiteCode(vo.getSiteCode());
            //查询当前会员是否存在风控会员账号记录
            RiskAccountVO riskAccountByAccount = riskApi.getRiskAccountByAccount(riskAccountQueryVO);

            if (ObjectUtil.isNotEmpty(riskAccountByAccount) && StringUtils.isNotBlank(riskAccountByAccount.getId())) {
                RiskAccountVO riskAccountVO = new RiskAccountVO();
                riskAccountVO.setId(riskAccountByAccount.getId());
                for (RiskLevelResVO datum : riskLevelList.getData()) {
                    if (ObjectUtil.isNotEmpty(userReview.getAfterModification()) &&
                            datum.getRiskControlLevel().equals(userReview.getAfterModification())) {
                        riskAccountVO.setRiskControlLevel(datum.getRiskControlLevel());
                    }
                }
                riskAccountVO.setRiskDesc(userReview.getApplicationInformation());
                riskAccountVO.setUpdater(adminId);
                riskAccountVO.setUpdatedTime(System.currentTimeMillis());
                riskApi.updateRiskListAccount(riskAccountVO);
            } else {
                RiskAccountVO riskAccountVO = new RiskAccountVO();
                riskAccountVO.setRiskControlAccount(userReview.getMemberAccount());
                riskAccountVO.setRiskControlType(RiskTypeEnum.RISK_MEMBER.getCode());
                riskAccountVO.setRiskControlTypeCode(RiskTypeEnum.RISK_MEMBER.getCode());
                riskAccountVO.setRiskControlLevelId(userReview.getAfterModification());
                for (RiskLevelResVO datum : riskLevelList.getData()) {
                    if (ObjectUtil.isNotEmpty(userReview.getAfterModification()) &&
                            datum.getId().equals(userReview.getAfterModification())) {
                        riskAccountVO.setRiskControlLevel(datum.getRiskControlLevel());
                    }
                }
                riskAccountVO.setRiskDesc(userReview.getApplicationInformation());
                riskAccountVO.setCreator(adminId);
                riskAccountVO.setCreatedTime(System.currentTimeMillis());
                riskAccountVO.setSiteCode(vo.getSiteCode());
                riskApi.saveRiskListAccount(riskAccountVO);
            }
        }

        LambdaUpdateWrapper<UserInfoPO> userInfoPOLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userInfoPOLambdaUpdateWrapper.eq(UserInfoPO::getUserAccount, userReview.getMemberAccount());
        userInfoPOLambdaUpdateWrapper.eq(UserInfoPO::getSiteCode, userReview.getSiteCode());
        if (String.valueOf((UserChangeTypeEnum.ZHANG_HAO_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
            userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getAccountStatus, userReview.getAfterModification());

        }

        //账号备注
        if (String.valueOf(UserChangeTypeEnum.ACCOUNT_NUMBER_STATUS.getCode()).equals(userReview.getReviewApplicationType())) {

            userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getAcountRemark, userReview.getAfterModification());

            UserChangeTypeHistoryRecordPO historyPO = new UserChangeTypeHistoryRecordPO();
            //会员账号
            historyPO.setMemberAccount(userInfoPO.getUserAccount());
            //审核备注
            historyPO.setRemark(vo.getReviewRemark());
            historyPO.setCreatedTime(System.currentTimeMillis());
            historyPO.setUpdatedTime(System.currentTimeMillis());
            historyPO.setCreator(adminId);
            historyPO.setUpdater(adminId);
            //站点
            historyPO.setSiteCode(siteCode);
            historyRemarkRepository.insert(historyPO);
        }
        // 风控层级
        if (String.valueOf((UserChangeTypeEnum.FENG_KONG_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
            RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
            riskLevelDownReqVO.setRiskControlType("1");
            ResponseVO<List<RiskLevelResVO>> riskLevelList = riskApi.getRiskLevelList(riskLevelDownReqVO);
            for (RiskLevelResVO datum : riskLevelList.getData()) {
                if (datum.getId().equals(userReview.getAfterModification())) {
                    userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getRiskLevelId, datum.getId());
                }
            }

        }
        //会员标签
        if (String.valueOf((UserChangeTypeEnum.HUI_YUAN_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
            // 所有标签
            List<GetAllUserLabelVO> list = userLabelConfigService.getAllEnableUserLabelBySiteCodeForHis(userReview.getSiteCode());
            List<CodeValueVO> userLabel = list.stream().map(item ->
                            CodeValueVO.builder()
                                    .code(item.getId())
                                    .value(item.getLabelName())
                                    .build())
                    .toList();
            userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getUserLabelId, userReview.getAfterModification());
            //新增标签变化记录
            SiteUserLabelRecordsPO siteUserLabelRecordsPO = new SiteUserLabelRecordsPO();
            if (ObjectUtil.isNotEmpty(userReview.getBeforeFixing())) {
                siteUserLabelRecordsPO.setBeforeChange(userReview.getBeforeFixing());
            }
            if (ObjectUtil.isNotEmpty(userReview.getAfterModification())) {
                siteUserLabelRecordsPO.setAfterChange(userReview.getAfterModification());
            }
            siteUserLabelRecordsPO.setMemberAccount(userReview.getMemberAccount());
            siteUserLabelRecordsPO.setAccountType(userReview.getAccountType());
            siteUserLabelRecordsPO.setUpdater(String.valueOf(adminId));
            siteUserLabelRecordsPO.setOperator(adminName);
            siteUserLabelRecordsPO.setUpdatedTime(System.currentTimeMillis());
            siteUserLabelRecordsPO.setSiteCode(userReview.getSiteCode());
            siteUserLabelRecordsPO.setAccountStatus(userInfoPO.getAccountStatus());
            siteUserLabelRecordsPO.setRiskControlLevel(userInfoPO.getRiskLevelId());
            siteUserLabelRecordRepository.insert(siteUserLabelRecordsPO);

        }
        boolean isVipGradeChange = true;
        // VIP等级
        if (String.valueOf(UserChangeTypeEnum.VIP_RANK_STATUS.getCode()).equals(userReview.getReviewApplicationType())) {
            if (SiteHandicapModeEnum.Internacional.getCode().equals(vo.getHandicapMode())) {
                // 会员等级
                SiteVipChangeRecordRequestVO requestVo = new SiteVipChangeRecordRequestVO();
                requestVo.setSiteCode(siteCode);
                requestVo.setUserId(userInfoPO.getUserId());
                requestVo.setUserAccount(userReview.getMemberAccount());
                requestVo.setBeforeChange(String.valueOf(userInfoPO.getVipGradeCode()));
                requestVo.setAfterChange(userReview.getAfterModification());
                requestVo.setChangeTime(System.currentTimeMillis());
                requestVo.setOperator(adminName);
                changeRecordService.insertChangeInfo(requestVo);

                // 当前VIP等级
                int nowVipGradeCode = Integer.parseInt(userReview.getAfterModification());
                // 查询当前等级对应的段位
                SiteVIPGradeVO siteVipGradeByCodeAndSiteCode = vipGradeApi.getSiteVipGradeByCodeAndSiteCode(userReview.getSiteCode(), nowVipGradeCode);
                userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getVipGradeCode, nowVipGradeCode);
                userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getVipRank, siteVipGradeByCodeAndSiteCode.getVipRankCode());
                // 升级后VIP等级
                SiteVIPGradeVO lastSiteVipGrade = vipGradeApi.getLastSiteVipGrade(siteCode);
                int nextVipGradeCode = lastSiteVipGrade.getVipGradeCode() == nowVipGradeCode
                        ? nowVipGradeCode : nowVipGradeCode + 1;
                userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getVipGradeUp, nextVipGradeCode);
//            SiteVIPGradeVO siteVIPGradeVO = vipGradeApi.queryVIPGradeByGrade(String.valueOf(nowVipGradeCode));
                UserVIPFlowRecordPO lastUserVIPFlowRecordPO = userVIPFlowRecordRepository.selectLastOne(userInfoPO.getUserId(), nowVipGradeCode);
                UserVIPFlowRecordPO po = new UserVIPFlowRecordPO();
                po.setUserAccount(userReview.getMemberAccount());
                po.setSiteCode(userReview.getSiteCode());
                po.setUserId(userInfoPO.getUserId());
                po.setVipGradeCode(nowVipGradeCode);
                String lastVipTime = TimeZoneUtils.formatTimestampToTimeZoneYyyyMMdd(System.currentTimeMillis(), vo.getTimeZone());
                po.setLastVipTime(lastVipTime);
                po.setValidExe(BigDecimal.ZERO);
                po.setCreatedTime(System.currentTimeMillis());
                po.setUpdatedTime(System.currentTimeMillis());
                if (null == lastUserVIPFlowRecordPO) {
                    po.setValidSumExe(BigDecimal.ZERO);
                } else {
                    po.setValidSumExe(lastUserVIPFlowRecordPO.getValidSumExe());
                }
                userVIPFlowRecordRepository.insert(po);
                UserInfoVO userInfoVO = new UserInfoVO();
                userInfoVO.setUserAccount(userReview.getMemberAccount());
                userInfoVO.setVipGradeCode(nowVipGradeCode);
            } else {
                //
                userVipFlowRecordCnService.updateCnVipGrade(userInfoPO.getUserId(), Integer.valueOf(userReview.getAfterModification()));
                isVipGradeChange = false;
            }

        }
        // 出生年月
        if (String.valueOf((UserChangeTypeEnum.CHU_SHENG_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
            userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getBirthday, userReview.getAfterModification());
        }
        if (String.valueOf((UserChangeTypeEnum.PHONE_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
            //申请手机号变更参数，逗号分隔
            String afterModification = userReview.getAfterModification();
            if (StringUtils.isNotBlank(afterModification)) {
                List<String> list = Arrays.asList(afterModification.split(CommonConstant.COMMA));
                if (list.size() == 2) {
                    userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getPhone, list.get(1));
                    userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getAreaCode, list.get(0));
                }
            }

        }
        // 姓名
        if (String.valueOf((UserChangeTypeEnum.NAME_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
            userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getUserName, userReview.getAfterModification());
        }

        List<CodeValueVO> genderType = systemParamApi.getSystemParamByType(CommonConstant.USER_GENDER).getData();
        // 性别
        if (String.valueOf((UserChangeTypeEnum.SEX_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
            genderType.forEach(sex -> {
                if (userReview.getAfterModification().equals(sex.getValue())) {
                    userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getGender, sex.getCode());
                }
            });
        }
        if (String.valueOf((UserChangeTypeEnum.EMAIL_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
            userInfoPOLambdaUpdateWrapper.set(UserInfoPO::getEmail, userReview.getAfterModification());
        }
        //不是增加流水的流程,才去修改会员基础信息表
        if (!String.valueOf(UserChangeTypeEnum.ADD_TYPING.getCode()).equals(userReview.getReviewApplicationType())) {
            // 如果是解绑银行卡，虚拟货币，电子钱包 三种情况，需要调钱包接口
            if (String.valueOf((UserChangeTypeEnum.BANK_CARD_UN_BIND.getCode())).equals(userReview.getReviewApplicationType()) ||
                    String.valueOf((UserChangeTypeEnum.ELECTRONIC_WALLET_UN_BIND.getCode())).equals(userReview.getReviewApplicationType()) ||
                    String.valueOf((UserChangeTypeEnum.CRYPTO_CURRENCY_UN_BIND.getCode())).equals(userReview.getReviewApplicationType())) {
                // 调钱包接口   解绑银行卡，虚拟货币，电子钱包 三种情况，需要调钱包接口
                // walletApi.unbindWallet(userReview.getMemberAccount(), userReview.getSiteCode());
                IdVO idVO = new IdVO();
                idVO.setId(userReview.getExtParam());
                userReceiveAccountApi.siteUerReceiveAccountUnBind(idVO);


            } else {
                // vip 国内盘通过指定接口更新
                if (isVipGradeChange) {

                    userInfoRepository.update(null, userInfoPOLambdaUpdateWrapper);
                }

            }

            if (String.valueOf((UserChangeTypeEnum.ZHANG_HAO_STATUS.getCode())).equals(userReview.getReviewApplicationType())) {
                //如果是登录锁定，强制退出会员
                if (UserStatusEnum.LOGIN_LOCK.getCode().equals(userReview.getAfterModification())) {
                    UserQueryVO userQueryVO = new UserQueryVO();
                    userQueryVO.setUserAccount(userReview.getMemberAccount());
                    userQueryVO.setSiteCode(userReview.getSiteCode());
                    UserInfoVO userInfoVO = userInfoService.getUserInfoByQueryVO(userQueryVO);
                    log.info("账号被锁定,登录:{}强制下线", userInfoVO.getUserId());
                    TokenUtil.delLoginUser(userReview.getSiteCode(), userInfoVO.getUserId());
                }
            }
        } else {
            //增加流水,走mq
            UserTypingAmountRequestVO userTypingAmountRequestVO = new UserTypingAmountRequestVO();
            userTypingAmountRequestVO.setSiteCode(userReview.getSiteCode());
            userTypingAmountRequestVO.setUserId(userInfoPO.getUserId());
            userTypingAmountRequestVO.setUserAccount(userInfoPO.getUserAccount());
            String orderNo = "R" + SnowFlakeUtils.getSnowId();
            userTypingAmountRequestVO.setOrderNo(orderNo);
            userTypingAmountRequestVO.setTypingAmount(new BigDecimal(userReview.getAfterModification()));
            userTypingAmountRequestVO.setCurrencyCode(userInfoPO.getMainCurrency());
            userTypingAmountRequestVO.setType(TypingAmountEnum.ADD.getCode());
            userTypingAmountRequestVO.setAdjustType(TypingAmountAdjustTypeEnum.MANUAL.getCode());
            List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(userTypingAmountRequestVO);
            // 发送 发送消息，添加打码量
            UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();
            //增加流水,发mq消息
            KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
        }

        //插入会员信息变更记录表
        UserInformationChangePO infoChangePO = new UserInformationChangePO();
        infoChangePO.setOperatingTime(System.currentTimeMillis());
        infoChangePO.setMemberAccount(userReview.getMemberAccount());
        infoChangePO.setSiteCode(userReview.getSiteCode());
        infoChangePO.setAccountType(userReview.getAccountType());
        infoChangePO.setChangeType(userReview.getReviewApplicationType());
        infoChangePO.setInformationBeforeChange(userReview.getBeforeFixing());
        infoChangePO.setInformationAfterChange(userReview.getAfterModification());
        infoChangePO.setSubmitInformation(vo.getReviewRemark());
        infoChangePO.setOperator(adminName);// 操作人
        userInformationChangeRepository.insert(infoChangePO);
        this.update(null, lambdaUpdate);

        // 发送勋章奖励
        if (String.valueOf(UserChangeTypeEnum.VIP_RANK_STATUS.getCode()).equals(userReview.getReviewApplicationType())) {
            // 当前VIP等级
            int nowVipGradeCode = Integer.parseInt(userReview.getAfterModification());
            SiteMedalInfoCondReqVO condReqVO = new SiteMedalInfoCondReqVO();
            condReqVO.setSiteCode(userReview.getSiteCode());
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

            if (nowVipGradeCode >= condValue) {
                UserInfoVO userInfoVO = new UserInfoVO();
                userInfoVO.setUserAccount(userReview.getMemberAccount());
                userInfoVO.setVipGradeCode(nowVipGradeCode);
                userInfoVO.setUserId(userInfoPO.getUserId());
                rewardReceiveService.receiveMedal(List.of(userInfoVO), siteCode);
            }
        }
        return ResponseVO.success();
    }

    public ResponseVO<UserAccountUpdateReviewDetailsResVO> getUpdateReviewDetails(IdVO vo) {
        //UserAccountUpdateReviewPO userReview = this.getById(vo.getId());
        UserAccountUpdateReviewPO userReview = this.getOne(new LambdaQueryWrapper<UserAccountUpdateReviewPO>()
                .eq(UserAccountUpdateReviewPO::getId, vo.getId())
                .eq(UserAccountUpdateReviewPO::getSiteCode, vo.getSiteCode()));
        if (null == userReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        UserAccountUpdateReviewResVO result = ConvertUtil.entityToModel(userReview, UserAccountUpdateReviewResVO.class);

        UserAccountUpdateReviewDetailsResVO res = new UserAccountUpdateReviewDetailsResVO();
        if (!Objects.isNull(result)) {
            // 根据会员账号来查询会员列表
            LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfoPO::getUserAccount, result.getMemberAccount());
            if (StringUtils.isNotBlank(vo.getSiteCode())) {
                queryWrapper.eq(UserInfoPO::getSiteCode, vo.getSiteCode());
            }
            UserInfoPO userInfoPO = userInfoRepository.selectOne(queryWrapper);
            List<CodeValueVO> reviewType = systemParamApi.getSystemParamByType(CommonConstant.USER_CHANGE_TYPE).getData();
            List<CodeValueVO> accountStatus = systemParamApi.getSystemParamByType(CommonConstant.USER_ACCOUNT_STATUS).getData();

            ResponseVO<OrderInfoVO> lastOrderRecord = orderRecordApi.getLastOrderRecord(userInfoPO.getUserId());
            // 风控层级
            RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
            riskLevelDownReqVO.setRiskControlType("1");
            riskLevelDownReqVO.setSiteCode(vo.getSiteCode());

            ResponseVO<List<RiskLevelResVO>> riskLevelList = riskApi.getRiskLevelList(riskLevelDownReqVO);
            // 用户标签-所有
            List<GetAllUserLabelVO> labelVOS = userLabelConfigService.getAllEnableUserLabelBySiteCodeForHis(userReview.getSiteCode());
            res.setRegistrationTime(userInfoPO.getRegisterTime());//注册时间
            res.setLastLoginTime(userInfoPO.getLastLoginTime());//上次登录时间
            if (!Objects.isNull(lastOrderRecord.getData())) {
                res.setLastBetTime(lastOrderRecord.getData().getBetTime());
            }
            res.setRegisterTerminal(userInfoPO.getRegisterIp());//注册终端
            res.setSuperiorAgent(userInfoPO.getSuperAgentAccount());// 上级代理

            res.setChangeType(userReview.getReviewApplicationType());

            res.setAccountType(userInfoPO.getAccountType());
            res.setAccount(userInfoPO.getUserAccount());//账号
            if (StrUtil.isNotEmpty(userInfoPO.getAccountStatus())) {
                String[] accountStatusList = userInfoPO.getAccountStatus().split(",");
                List<CodeValueVO> accountStatusName = Lists.newArrayList();
                for (String status : accountStatusList) {
                    for (CodeValueVO systemParamVO : accountStatus) {
                        if (status.equals(systemParamVO.getCode())) {
                            CodeValueVO codeValueVO = new CodeValueVO();
                            codeValueVO.setCode(systemParamVO.getCode());
                            codeValueVO.setValue(systemParamVO.getValue());
                            accountStatusName.add(codeValueVO);
                        }
                    }
                }
                res.setAccountStatusName(accountStatusName);//账号状态
            }
            Integer vipRank = userInfoPO.getVipRank();
            String siteCode = userInfoPO.getSiteCode();
            LambdaQueryWrapper<SiteVIPRankPO> vipRankQuery = Wrappers.lambdaQuery();
            vipRankQuery.eq(SiteVIPRankPO::getSiteCode, siteCode).eq(SiteVIPRankPO::getVipRankCode, vipRank);
            SiteVIPRankPO siteVIPRankPO = siteVIPRankRepository.selectOne(vipRankQuery);
            if (siteVIPRankPO != null) {
                res.setVipRankNameI18nCode(siteVIPRankPO.getVipRankNameI18nCode());
            }

            res.setId(vo.getId());
            LambdaQueryWrapper<SiteVIPGradePO> gradeQuery = Wrappers.lambdaQuery();
            gradeQuery.eq(SiteVIPGradePO::getSiteCode, siteCode).eq(SiteVIPGradePO::getVipGradeCode, userInfoPO.getVipGradeCode());
            SiteVIPGradePO siteVIPGradePO = siteVIPGradeRepository.selectOne(gradeQuery);
            if (siteVIPGradePO != null) {
                res.setVipLevel(siteVIPGradePO.getVipGradeName());//VIP 等级
            }

            for (RiskLevelResVO datum : riskLevelList.getData()) {
                if (ObjectUtil.isNotEmpty(userInfoPO.getRiskLevelId()) && userInfoPO.getRiskLevelId().equals(datum.getId())) {
                    res.setRiskLevel(datum.getRiskControlLevel());//风控层级
                }
            }
            if (ObjectUtil.isNotEmpty(userInfoPO.getUserLabelId())) {
                Map<String, String> labelMap = new HashMap<>();
                // 获取sitecode+总站
                List<GetAllUserLabelVO> labelByIds = userLabelConfigService.getAllEnableUserLabelBySiteCodeForHis(vo.getSiteCode());
                if (CollectionUtil.isNotEmpty(labelByIds)) {
                    labelMap = labelByIds.stream().collect(Collectors.toMap(GetAllUserLabelVO::getId, GetAllUserLabelVO::getLabelName, (k1, k2) -> k2));
                }
                String[] split = userInfoPO.getUserLabelId().split(",");
                StringJoiner before = new StringJoiner(",");
                for (String id : split) {
                    before.add(labelMap.get(id));
                }
                res.setMemberLabel(before.toString());//会员标签
            }
            res.setApplicant(result.getApplicant());//申请人
            res.setApplicationTime(result.getApplicationTime());//申请时间

            if (null != result.getReviewApplicationType()) {
                for (CodeValueVO systemParamVO : reviewType) {
                    if (result.getReviewApplicationType().equals(systemParamVO.getCode())) {
                        res.setReviewApplicationType(systemParamVO.getValue());//审核申请类型
                    }
                }
            }
            String reviewApplicationType = result.getReviewApplicationType();

            res.setBeforeFixing(result.getBeforeFixing());//修改前
            res.setAfterModification(result.getAfterModification());//修改后
            if (String.valueOf(UserChangeTypeEnum.VIP_RANK_STATUS.getCode()).equals(reviewApplicationType)) {
                //会员等级变更的，code转一下name
                String beforeFixing = result.getBeforeFixing();
                String afterModification = result.getAfterModification();
                List<String> gradeCodeArr = new ArrayList<>();
                gradeCodeArr.add(beforeFixing);
                gradeCodeArr.add(afterModification);
                LambdaQueryWrapper<SiteVIPGradePO> beAfQuery = Wrappers.lambdaQuery();
                beAfQuery.eq(SiteVIPGradePO::getSiteCode, siteCode).in(SiteVIPGradePO::getVipGradeCode, gradeCodeArr);
                List<SiteVIPGradePO> siteVIPGradePOS = siteVIPGradeRepository.selectList(beAfQuery);
                // 转换 List<SiteVIPGradePO> 为 Map<String, String>
                Map<Integer, String> codeNameMap = siteVIPGradePOS.stream()
                        .collect(Collectors.toMap(SiteVIPGradePO::getVipGradeCode, SiteVIPGradePO::getVipGradeName));
                res.setBeforeFixing(codeNameMap.get(Integer.parseInt(result.getBeforeFixing())));//修改前
                res.setAfterModification(codeNameMap.get(Integer.parseInt(result.getAfterModification())));//修改后
            }
            if (result.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.ADD_TYPING.getCode()))) {
                BigDecimal before = BigDecimal.ZERO;
                String beforeFixing = result.getBeforeFixing();
                String afterModification = result.getAfterModification();

                if (StringUtils.isNotBlank(beforeFixing)) {
                    before = new BigDecimal(beforeFixing);
                }
                if (StringUtils.isNotBlank(afterModification)) {
                    BigDecimal after = new BigDecimal(afterModification);
                    after = after.add(before);
                    res.setAfterModification(String.valueOf(after));
                }
            }
            // 脱敏
            handleSensitiveInfo(res, result, vo);
            if (String.valueOf(UserChangeTypeEnum.PHONE_STATUS.getCode()).equals(reviewApplicationType)) {
                String beforeFixing = res.getBeforeFixing();
                String afterModification = res.getAfterModification();
                if (StringUtils.isNotBlank(beforeFixing)) {
                    res.setBeforeFixing(beforeFixing.replaceAll(CommonConstant.COMMA, ""));
                }
                if (StringUtils.isNotBlank(afterModification)) {
                    res.setAfterModification(afterModification.replaceAll(CommonConstant.COMMA, ""));
                }
            }
            //账号状态变更前后
            if (userReview.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.ZHANG_HAO_STATUS.getCode()))) {
                String beforeFixing = res.getBeforeFixing();
                String afterModification = res.getAfterModification();
                if (StringUtils.isNotBlank(beforeFixing)) {
                    res.setBeforeFixing(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.USER_ACCOUNT_STATUS, beforeFixing));
                }
                if (StringUtils.isNotBlank(afterModification)) {
                    res.setAfterModification(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.USER_ACCOUNT_STATUS, afterModification));
                }

            }
            if (userReview.getReviewApplicationType().equals("2")) {
                for (RiskLevelResVO datum : riskLevelList.getData()) {
                    if (ObjectUtil.isNotEmpty(userReview.getBeforeFixing()) && userReview.getBeforeFixing().equals(String.valueOf(datum.getId()))) {
                        res.setBeforeFixing(datum.getRiskControlLevel());
                    }
                    if (ObjectUtil.isNotEmpty(userReview.getAfterModification()) && userReview.getAfterModification().equals(String.valueOf(datum.getId()))) {
                        res.setAfterModification(datum.getRiskControlLevel());
                    }
                }
            }

            if (userReview.getReviewApplicationType().equals(String.valueOf(UserChangeTypeEnum.HUI_YUAN_STATUS.getCode()))) {
                if (StringUtils.isNotBlank(userReview.getBeforeFixing())) {
                    String[] accountLabelList = userReview.getBeforeFixing().split(",");
                    StringJoiner before = new StringJoiner(",");
                    for (String status : accountLabelList) {
                        for (GetAllUserLabelVO labelVO : labelVOS) {
                            if (status.equals(labelVO.getId())) {
                                before.add(labelVO.getLabelName());
                            }
                        }
                    }
                    res.setBeforeFixing(before.toString());//修改前
                }
                StringJoiner after = new StringJoiner(",");
                String[] strings = userReview.getAfterModification().split(",");
                for (String str : strings) {
                    for (GetAllUserLabelVO labelVO : labelVOS) {
                        if (str.equals(labelVO.getId())) {
                            after.add(labelVO.getLabelName());
                        }
                    }

                }
                res.setAfterModification(after.toString());//修改前
            }
            if (userReview.getReviewApplicationType().equals("4")) {
                if (ObjectUtil.isNotEmpty(userReview.getBeforeFixing())) {
//                    long time = DateUtil.parse(userReview.getBeforeFixing(), DatePattern.NORM_DATE_PATTERN).getTime();
                    res.setBeforeFixing(userReview.getBeforeFixing());
                }
//                long time = DateUtil.parse(userReview.getAfterModification(), DatePattern.NORM_DATE_PATTERN).getTime();
                res.setAfterModification(userReview.getAfterModification());//修改后
            }

            res.setReasonApplication(userReview.getApplicationInformation());//申请原因

//                res.setBeforeFixing(result.getBeforeFixing());//修改前
//                res.setAfterModification(result.getAfterModification());//修改后
            res.setFirstInstance(result.getFirstInstance());//一审人
            List<CodeValueVO> userLabel = labelVOS.stream().map(item ->
                            CodeValueVO.builder()
                                    .code(item.getId())
                                    .value(item.getLabelName())
                                    .build())
                    .toList();
            if (ObjectUtil.isNotEmpty(userInfoPO.getUserLabelId())) {
                for (CodeValueVO systemParamVO : userLabel) {
                    if (systemParamVO.getCode().equals(String.valueOf(userInfoPO.getUserLabelId())))
                        res.setMemberLabel(systemParamVO.getValue());
                }
            }
            if (ObjectUtil.isNotEmpty(userInfoPO.getRiskLevelId())) {
                for (RiskLevelResVO datum : riskLevelList.getData()) {
                    if (datum.getId().equals(userInfoPO.getRiskLevelId())) {
                        res.setRiskLevel(datum.getRiskControlLevel());
                    }
                }
            }
            res.setFirstTrialTime(result.getFirstReviewTime());//一审时间
            res.setFirstRemark(userReview.getReviewRemark());//一审备注
            res.setRemark(userInfoPO.getAcountRemark());
            WalletUserDepositWithdrawVO userDepositWithdraw = userWithdrawRecordApi.getUserDepositWithdraw(userInfoPO.getUserId());
            res.setDepositNumber(Optional.ofNullable(userDepositWithdraw.getDepositTotalNum()).orElse(0).toString());//存款次数
            res.setWithdrawNumber(Optional.ofNullable(userDepositWithdraw.getWithdrawTotalNum()).orElse(0).toString());//提款次数
            res.setMainCurrency(userInfoPO.getMainCurrency());
            res.setRegistry(DeviceType.nameOfCode(userInfoPO.getRegistry()).getName());
        }
        return ResponseVO.success(res);
    }

    private void handleSensitiveInfo(UserAccountUpdateReviewDetailsResVO res, UserAccountUpdateReviewResVO result, IdVO vo) {
        String before = result.getBeforeFixing();
        String after = result.getAfterModification();
        if (vo.getDataDesensitization()) {
            if (ObjectUtil.equals(String.valueOf(UserChangeTypeEnum.PHONE_STATUS.getCode()), result.getReviewApplicationType())) {
                // 处理 PHONE_STATUS 且 vo.getDataDesensitization() 为 true 的情况
                res.setBeforeFixing(SymbolUtil.showPhone(before));//修改前
                res.setAfterModification(SymbolUtil.showPhone(after));//修改后
            } else if (ObjectUtil.equals(String.valueOf(UserChangeTypeEnum.EMAIL_STATUS.getCode()), result.getReviewApplicationType())) {
                // 处理 EMAIL_STATUS 且 vo.getDataDesensitization() 为 true 的情况
                res.setBeforeFixing(SymbolUtil.showEmail(before));//修改前
                res.setAfterModification(SymbolUtil.showEmail(after));//修改后
            } else if (ObjectUtil.equals(String.valueOf(UserChangeTypeEnum.NAME_STATUS.getCode()), result.getReviewApplicationType())) {
                res.setBeforeFixing(SymbolUtil.showUserName(before));
                res.setAfterModification(SymbolUtil.showUserName(after));
            }else if (ObjectUtil.equals(String.valueOf(UserChangeTypeEnum.BANK_CARD_UN_BIND.getCode()), result.getReviewApplicationType())) {
                String[] beforeChangeArr = res.getBeforeFixing().split("\\|");
                String backCard =  SymbolUtil.showBankOrVirtualNo(beforeChangeArr[1]);
                beforeChangeArr[1] = backCard;
                res.setBeforeFixing(String.join("|",beforeChangeArr));
            }else if (ObjectUtil.equals(String.valueOf(UserChangeTypeEnum.ELECTRONIC_WALLET_UN_BIND.getCode()), result.getReviewApplicationType())) {
                String[] beforeChangeArr = res.getBeforeFixing().split("\\|");
                String userName = "";
                String addressNo = "";
                String electronicAccount = "";
                if(beforeChangeArr.length == CommonConstant.business_three){
                    userName = SymbolUtil.showUserName(beforeChangeArr[0]);
                    beforeChangeArr[0] = userName;
                    electronicAccount = SymbolUtil.showWalletNo(beforeChangeArr[1]);
                    beforeChangeArr[1] = electronicAccount;
                    addressNo = SymbolUtil.showWalletNo(beforeChangeArr[2]);
                    beforeChangeArr[2] = addressNo;

                }else{
                    userName = SymbolUtil.showUserName(beforeChangeArr[1]);
                    beforeChangeArr[1] = userName;
                    electronicAccount = SymbolUtil.showWalletNo(beforeChangeArr[2]);
                    beforeChangeArr[2] = electronicAccount;
                    addressNo = SymbolUtil.showWalletNo(beforeChangeArr[3]);
                    beforeChangeArr[3] = addressNo;
                }
                res.setBeforeFixing(String.join("|",beforeChangeArr));
            } else if (ObjectUtil.equals(String.valueOf(UserChangeTypeEnum.CRYPTO_CURRENCY_UN_BIND.getCode()), result.getReviewApplicationType())) {
                String[] beforeChangeArr = res.getBeforeFixing().split("\\|");
                String addressNo =  SymbolUtil.showBankOrVirtualNo(beforeChangeArr[1]);
                beforeChangeArr[1] = addressNo;
                res.setBeforeFixing(String.join("|",beforeChangeArr));
            }
        }
    }

    /***
     * 查询会员审核页面待审核和处理中的条数
     * @return
     */
    public UserAccountUpdateVO getNumber(String siteCode) {
        Long number = userAccountUpdateReviewRepository.selectCount(Wrappers.<UserAccountUpdateReviewPO>lambdaQuery()
                .eq(UserAccountUpdateReviewPO::getSiteCode, siteCode)
                .in(UserAccountUpdateReviewPO::getReviewStatus, Lists.newArrayList(CommonConstant.business_one, CommonConstant.business_two)));
        UserAccountUpdateVO vo = new UserAccountUpdateVO();
        vo.setNum(number.intValue());
        vo.setRouter(UserConorRouterConstants.UPDATE_MEMBER_REVIEW);
        return vo;
    }
}
