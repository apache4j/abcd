package com.cloud.baowang.wallet.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.enums.usercoin.UserWithdrawReviewNumberEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.wallet.api.enums.UserAuditSystemParamEnum;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewRecordVO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalAuditPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class UserWithdrawReviewRecordService {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

    private final UserDepositWithdrawalAuditService userDepositWithdrawalAuditService;

    /**
     * 提款审核记录分页列表
     *
     * @param vo
     * @return
     */
    public Page<UserWithdrawReviewRecordVO> withdrawalReviewRecordPageList(UserWithdrawReviewRecordPageReqVO vo) {
        Page<UserWithdrawReviewRecordVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserWithdrawReviewRecordVO> pageResult = userDepositWithdrawalRepository.withdrawalReviewRecordPageList(page, vo);
        if (CollUtil.isEmpty(pageResult.getRecords())) {
            return new Page<>();
        }
        List<String> orderNoList = pageResult.getRecords().stream().map(UserWithdrawReviewRecordVO::getOrderNo).toList();
        Map<String, List<UserDepositWithdrawalAuditPO>> auditInfoMap = userDepositWithdrawalAuditService.getAuditInfoMap(orderNoList);
        for (UserWithdrawReviewRecordVO record : pageResult.getRecords()) {
            StringBuilder auditUserStr = new StringBuilder();
            StringBuilder auditTimeStr = new StringBuilder();
            StringBuilder auditRemarkStr = new StringBuilder();
            StringBuilder auditUseTimeStr = new StringBuilder();

            List<UserDepositWithdrawalAuditPO> auditPOList = auditInfoMap.get(record.getOrderNo());
            if (CollectionUtil.isNotEmpty(auditPOList)) {
                for (UserDepositWithdrawalAuditPO audit : auditPOList) {
                    String auditUser = audit.getAuditUser();
                    String auditTime = TimeZoneUtils.formatTimestampToTimeZone(audit.getAuditTime(), CurrReqUtils.getTimezone());
                    String remark = audit.getAuditInfo();
                    String useTime = DateUtils.formatTime(audit.getAuditTimeConsuming());

                    if (UserWithdrawReviewNumberEnum.WAIT_ONE_REVIEW.getCode().equals(audit.getNum())) {
                        String firstAuditI18value = I18nMessageUtil.getI18NMessageInAdvice(UserAuditSystemParamEnum.FIRST_AUDIT.getSystemParamValue());
                        auditUserStr.append(firstAuditI18value).append(":")
                                .append(StringUtils.isBlank(auditUser) ? "" :
                                        auditUser).append("$$");

                        auditTimeStr.append(firstAuditI18value).append(":")
                                .append(auditTime).append("$$");

                        auditRemarkStr.append(firstAuditI18value).append(":")
                                .append(StringUtils.isBlank(remark) ? "" : remark).append("$$");
                        auditUseTimeStr.append(firstAuditI18value).append(":")
                                .append(StringUtils.isBlank(useTime) ? "" : useTime).append("$$");

                    } else if (UserWithdrawReviewNumberEnum.WAIT_ORDER_REVIEW.getCode().equals(audit.getNum())) {
                        //挂单审核明细（如果有）
                        String pendOrderI18value = I18nMessageUtil.getI18NMessageInAdvice(UserAuditSystemParamEnum.PENDING_ORDER_AUDIT.getSystemParamValue());
                        auditUserStr.append(pendOrderI18value).append(":")
                                .append(StringUtils.isBlank(auditUser) ? "" :
                                        auditUser).append("$$");

                        auditTimeStr.append(pendOrderI18value).append(":")
                                .append(auditTime).append("$$");

                        auditRemarkStr.append(pendOrderI18value).append(":")
                                .append(StringUtils.isBlank(remark) ? "" : remark).append("$$");
                        auditUseTimeStr.append(pendOrderI18value).append(":")
                                .append(StringUtils.isBlank(useTime) ? "" : useTime).append("$$");

                    } else if (UserWithdrawReviewNumberEnum.WAIT_PAY_REVIEW.getCode().equals(audit.getNum())) {
                        //待出款审核
                        String withDrawReview = I18nMessageUtil.getI18NMessageInAdvice(UserAuditSystemParamEnum.WITHDRAWAL_REVIEW.getSystemParamValue());
                        auditUserStr.append(withDrawReview).append(":")
                                .append(StringUtils.isBlank(auditUser) ? "" :
                                        auditUser);

                        auditTimeStr.append(withDrawReview).append(":")
                                .append(auditTime);

                        auditRemarkStr.append(withDrawReview).append(":")
                                .append(StringUtils.isBlank(remark) ? "" : remark);
                        auditUseTimeStr.append(withDrawReview).append(":")
                                .append(StringUtils.isBlank(useTime) ? "" : useTime);
                    }
                }
            }
            record.setAuditUserInfo(auditUserStr.toString());
            record.setAuditRemarkInfo(auditRemarkStr.toString());
            record.setAuditTimeInfo(auditTimeStr.toString());
            record.setAuditUseTimeInfo(auditUseTimeStr.toString());
        }
        return pageResult;
    }

    public Long withdrawalReviewRecordPageListCount(UserWithdrawReviewRecordPageReqVO vo) {

        return userDepositWithdrawalRepository.withdrawalReviewRecordPageCount(vo);
    }
}
