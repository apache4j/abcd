package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.SiteUserLabelConfigApiImpl;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.userlabel.userLabelRecord.UserLabelRecordsReqVO;
import com.cloud.baowang.user.api.vo.userlabel.userLabelRecord.UserLabelRecordsResVO;
import com.cloud.baowang.user.po.SiteUserLabelRecordsPO;
import com.cloud.baowang.user.repositories.SiteUserLabelRecordRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.prometheus.client.SampleNameFilter.stringToList;

@Slf4j
@Service
@AllArgsConstructor
public class SiteUserLabelRecordService extends ServiceImpl<SiteUserLabelRecordRepository, SiteUserLabelRecordsPO> {
    private final SiteUserLabelRecordRepository siteUserLabelRecordRepository;
    private final SiteUserLabelConfigApiImpl userLabelConfigService;
    private final BusinessAdminApi businessAdminApi;
    private final RiskApi riskApi;
    private final SystemParamApi systemParamApi;

    
    public ResponseVO<Page<UserLabelRecordsResVO>> getUserLabelRecords(UserLabelRecordsReqVO reqVO) {
        try {
            String siteCode = CurrReqUtils.getSiteCode();
            // 分页查询
            Page<SiteUserLabelRecordsPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
            LambdaQueryWrapper<SiteUserLabelRecordsPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SiteUserLabelRecordsPO::getSiteCode, siteCode);
            if (ObjectUtil.isNotEmpty(reqVO.getStartUpdatedTime()) && ObjectUtil.isNotEmpty(reqVO.getEndUpdatedTime())) {
                queryWrapper.ge(SiteUserLabelRecordsPO::getUpdatedTime, reqVO.getStartUpdatedTime());
                queryWrapper.le(SiteUserLabelRecordsPO::getUpdatedTime, reqVO.getEndUpdatedTime());
            }

            if (StringUtils.isNotBlank(reqVO.getMemberAccount())) {
                queryWrapper.eq(SiteUserLabelRecordsPO::getMemberAccount, reqVO.getMemberAccount());
            }
            if (StringUtils.isNotBlank(reqVO.getUpdater())) {
                queryWrapper.eq(SiteUserLabelRecordsPO::getOperator, reqVO.getUpdater());

            }
            if (ObjectUtil.isNotEmpty(reqVO.getOrderType())) {
                //升序
                if (reqVO.getOrderType().equals(CommonConstant.ORDER_BY_ASC)) {
                    queryWrapper.orderByAsc(SiteUserLabelRecordsPO::getUpdatedTime);
                } else {
                    //降序
                    queryWrapper.orderByDesc(SiteUserLabelRecordsPO::getUpdatedTime);
                }
            } else {
                queryWrapper.orderByDesc(SiteUserLabelRecordsPO::getUpdatedTime);
            }
            Page<UserLabelRecordsResVO> pageResult = new Page<>();
            Page<SiteUserLabelRecordsPO> result = siteUserLabelRecordRepository.selectPage(page, queryWrapper);
            if (CollectionUtil.isEmpty(result.getRecords())) {
                return ResponseVO.success(pageResult);
            }

            List<UserLabelRecordsResVO> list = result.getRecords().stream().map(po -> {
                UserLabelRecordsResVO vo = new UserLabelRecordsResVO();
                BeanUtils.copyProperties(po, vo);
                return vo;
            }).toList();

            BeanUtils.copyProperties(result, pageResult);

            pageResult.setRecords(list);

            Set<String> riskIds = Sets.newHashSet();
            Set<String> labelIds = Sets.newHashSet();

            for (UserLabelRecordsResVO record : pageResult.getRecords()) {

                String riskId = record.getRiskControlLevel();
                if (StringUtils.isNotEmpty(riskId)){
                    riskIds.add(riskId);
                }

                String beforeChange = record.getBeforeChange();
                if (StringUtils.isNotEmpty(beforeChange)){
                    String[] split = beforeChange.split(CommonConstant.COMMA);
                    labelIds.addAll(List.of(split));
                }

                String afterChange = record.getAfterChange();
                if (StringUtils.isNotEmpty(afterChange)){
                    String[] split = afterChange.split(CommonConstant.COMMA);
                    labelIds.addAll(List.of(split));
                }
            }

            // label
            List<GetUserLabelByIdsVO> labelByIds = userLabelConfigService.getUserLabelByIds(labelIds.stream().toList());
            Map<String, String> labelMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(labelByIds)) {
                labelMap = labelByIds.stream().collect(Collectors.toMap(GetUserLabelByIdsVO::getId, GetUserLabelByIdsVO::getLabelName, (k1, k2) -> k2));
            }

            // risk
            Map<String, RiskLevelDetailsVO> riskMap = riskApi.getByIds(riskIds.stream().toList());

            // accountStatus
            List<CodeValueVO> accountStatus = systemParamApi.getSystemParamByType(CommonConstant.USER_ACCOUNT_STATUS).getData();
            Map<String, String> statusCode2Val = accountStatus.stream().collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue));

            for (UserLabelRecordsResVO record : pageResult.getRecords()) {

                // risk
                if (StringUtils.isNotBlank(record.getRiskControlLevel())) {
                    RiskLevelDetailsVO riskLevelDetailsVO = riskMap.get(record.getRiskControlLevel());
                    if (ObjectUtil.isNotEmpty(riskLevelDetailsVO)) {
                        record.setRiskControlLevel(riskLevelDetailsVO.getRiskControlLevel());
                    }
                }

                // beforeChange
                if (StringUtils.isNotBlank(record.getBeforeChange()) && !record.getBeforeChange().equals("null")) {
                    StringBuilder userLabel = new StringBuilder();
                    List<String> ids = stringToList(record.getBeforeChange());
                    for (String id : ids) {
                        userLabel.append("#").append(labelMap.get(id)).append(CommonConstant.COMMA);
                    }
                    if (userLabel.toString().endsWith(CommonConstant.COMMA)) {
                        userLabel = new StringBuilder(userLabel.substring(0, userLabel.length() - 1));
                    }
                    record.setBeforeChange(userLabel.toString());
                }
                // afterChange
                if (StringUtils.isNotBlank(record.getAfterChange()) && !record.getAfterChange().equals("null")) {
                    StringBuilder userLabel = new StringBuilder();
                    List<String> ids = stringToList(record.getAfterChange());
                    for (String id : ids) {
                        userLabel.append("#").append(labelMap.get(id)).append(CommonConstant.COMMA);
                    }
                    if (userLabel.toString().endsWith(CommonConstant.COMMA)) {
                        userLabel = new StringBuilder(userLabel.substring(0, userLabel.length() - 1));
                    }
                    record.setAfterChange(userLabel.toString());
                }
                // account Status
                List<CodeValueVO> accountStatusName = Lists.newArrayList();
                String[] accountStatusList = record.getAccountStatus().split(CommonConstant.COMMA);

                for (String status : accountStatusList) {
                    String value = statusCode2Val.get(status);
                    CodeValueVO codeValueVO = new CodeValueVO();
                    codeValueVO.setCode(status);
                    codeValueVO.setValue(value);
                    accountStatusName.add(codeValueVO);
                }
                record.setAccountStatusName$Arr(accountStatusName);
            }
            return ResponseVO.success(pageResult);
        } catch (Exception e) {
            log.error("会员标签记录查询异常：", e);
            throw new BaowangDefaultException("会员标签记录查询异常");
        }
    }

    public ResponseVO<Long> getTotalCount(UserLabelRecordsReqVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        LambdaQueryWrapper<SiteUserLabelRecordsPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteUserLabelRecordsPO::getSiteCode, siteCode);
        if (ObjectUtil.isNotEmpty(reqVO.getStartUpdatedTime()) && ObjectUtil.isNotEmpty(reqVO.getEndUpdatedTime())) {
            queryWrapper.ge(SiteUserLabelRecordsPO::getUpdatedTime, reqVO.getStartUpdatedTime());
            queryWrapper.le(SiteUserLabelRecordsPO::getUpdatedTime, reqVO.getEndUpdatedTime());
        }
        if (StringUtils.isNotBlank(reqVO.getMemberAccount())) {
            queryWrapper.eq(SiteUserLabelRecordsPO::getMemberAccount, reqVO.getMemberAccount());
        }
        if (StringUtils.isNotBlank(reqVO.getUpdater())) {
            queryWrapper.eq(SiteUserLabelRecordsPO::getOperator, reqVO.getUpdater());
        }
        Long count = siteUserLabelRecordRepository.selectCount(queryWrapper);
        return ResponseVO.success(count);
    }
}

