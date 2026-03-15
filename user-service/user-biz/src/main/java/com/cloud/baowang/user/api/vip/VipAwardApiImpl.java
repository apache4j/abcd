package com.cloud.baowang.user.api.vip;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.vip.VipAwardApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.*;
import com.cloud.baowang.user.po.SiteVIPGradePO;
import com.cloud.baowang.user.po.SiteVipAwardRecordPO;
import com.cloud.baowang.user.po.SiteVipAwardRecordV2PO;
import com.cloud.baowang.user.repositories.SiteVIPGradeRepository;
import com.cloud.baowang.user.repositories.SiteVipAwardRecordRepository;
import com.cloud.baowang.user.repositories.SiteVipAwardRecordV2Repository;
import com.cloud.baowang.user.service.SiteVipOptionService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class VipAwardApiImpl implements VipAwardApi {

    private SiteVipAwardRecordRepository recordRepository;
    private final VipRankApi vipRankApi;
    private final SiteVIPGradeRepository vipGradeRepository;
    private final SiteApi siteApi;
    private SiteVipAwardRecordV2Repository siteVipAwardRecordV2Repository;
    private SiteVipOptionService siteVipOptionService;


    @Override
    public ResponseVO<Page<SiteVipAwardRecordVo>> queryVIPAwardList(SiteVipAwardRecordReqVo vo) {
        SiteVO siteInfo = siteApi.getSiteInfo(vo.getSiteCode()).getData();
        if (SiteHandicapModeEnum.Internacional.getCode()==siteInfo.getHandicapMode()){
             return getIntegernacionalData (vo);
        }else{
             return getCnData(vo);
        }
    }

    private  ResponseVO<Page<SiteVipAwardRecordVo>> getIntegernacionalData(SiteVipAwardRecordReqVo vo){
        try {
            Page<SiteVipAwardRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
            LambdaQueryWrapper<SiteVipAwardRecordPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SiteVipAwardRecordPO::getSiteCode,vo.getSiteCode());
            if (ObjectUtil.isNotEmpty(vo.getReceiveTimeStart())) {
                queryWrapper.ge(SiteVipAwardRecordPO::getReceiveTime, vo.getReceiveTimeStart());
            }
            if (ObjectUtil.isNotEmpty(vo.getReceiveTimeEnd())) {
                queryWrapper.le(SiteVipAwardRecordPO::getReceiveTime, vo.getReceiveTimeEnd());
            }
            if (ObjectUtil.isNotEmpty(vo.getCreatedTimeStart())) {
                queryWrapper.ge(SiteVipAwardRecordPO::getCreatedTime, vo.getCreatedTimeStart());
            }
            if (ObjectUtil.isNotEmpty(vo.getCreatedTimeEnd())) {
                queryWrapper.le(SiteVipAwardRecordPO::getCreatedTime, vo.getCreatedTimeEnd());
            }
            if (ObjectUtil.isNotEmpty(vo.getExpiredTimeStart())) {
                queryWrapper.ge(SiteVipAwardRecordPO::getExpiredTime, vo.getExpiredTimeStart());
            }
            if (ObjectUtil.isNotEmpty(vo.getExpiredTimeEnd())) {
                queryWrapper.le(SiteVipAwardRecordPO::getExpiredTime, vo.getExpiredTimeEnd());
            }
            if (ObjectUtil.isNotEmpty(vo.getReceiveType())) {
                queryWrapper.eq(SiteVipAwardRecordPO::getReceiveType, vo.getReceiveType());
            }
            if (ObjectUtil.isNotEmpty(vo.getReceiveStatus())) {
                queryWrapper.eq(SiteVipAwardRecordPO::getReceiveStatus, vo.getReceiveStatus());
            }
            if (ObjectUtil.isNotEmpty(vo.getOrderId())) {
                queryWrapper.eq(SiteVipAwardRecordPO::getOrderId, vo.getOrderId());
            }
            if (ObjectUtil.isNotEmpty(vo.getUserAccount())) {
                queryWrapper.eq(SiteVipAwardRecordPO::getUserAccount, vo.getUserAccount());
            }
            if (ObjectUtil.isNotEmpty(vo.getAccountType())) {
                queryWrapper.eq(SiteVipAwardRecordPO::getAccountType, vo.getAccountType());
            }
            if (ObjectUtil.isNotEmpty(vo.getAwardType())) {
                queryWrapper.eq(SiteVipAwardRecordPO::getAwardType, vo.getAwardType());
            }
            queryWrapper.orderByDesc(SiteVipAwardRecordPO::getCreatedTime);

            Page<SiteVipAwardRecordPO> resultPage = recordRepository.selectPage(page, queryWrapper);

            LambdaQueryWrapper<SiteVIPGradePO> vipQuery = Wrappers.lambdaQuery();
            vipQuery.eq(SiteVIPGradePO::getSiteCode,vo.getSiteCode());
            List<SiteVIPGradePO> siteVIPGradePOS = vipGradeRepository.selectList(vipQuery);

            Map<Integer,String> gradeMap = siteVIPGradePOS.stream().collect(Collectors.toMap(SiteVIPGradePO::getVipGradeCode,SiteVIPGradePO::getVipGradeName));

            Map<Integer, String> vipRankMap = adaptiveVIPRank(vo.getSiteCode());

            Page<SiteVipAwardRecordVo> result = new Page<>();
            List<SiteVipAwardRecordVo> voList = Lists.newArrayList();
            if (ObjectUtils.isNotEmpty(resultPage.getRecords())) {
                resultPage.getRecords().forEach(obj -> {
                    SiteVipAwardRecordVo temp = new SiteVipAwardRecordVo();
                    BeanUtils.copyProperties(obj, temp);
                    temp.setVipRankCodeText(vipRankMap.get(obj.getVipRankCode()));
                    temp.setVipGrade(gradeMap.get(obj.getVipGradeCode()));
                    voList.add(temp);
                });
                BeanUtils.copyProperties(resultPage, result);
            }
            result.setRecords(voList);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询VIP奖励异常", e);
            return ResponseVO.fail(ResultCode.VIP_AWARD_QUERY_ERROR);
        }
    }
    private Map<Integer, String> adaptiveVIPRank(String siteCode) {
        Map<Integer, String> vipRankMap;
        if (Objects.equals(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE)) {
            vipRankMap = Optional.ofNullable(vipRankApi.getVipRankList())
                    .map(list -> list.stream()
                            .filter(v -> v.getVipRankCode() != null && v.getVipRankNameI18nCode() != null)
                            .collect(Collectors.toMap(
                                    VIPRankVO::getVipRankCode,
                                    VIPRankVO::getVipRankNameI18nCode,
                                    (oldVal, newVal) -> oldVal // 如果 key 冲突，保留旧值
                            )))
                    .orElseGet(HashMap::new);
        } else {
            ResponseVO<List<SiteVIPRankVO>> listResponseVO = vipRankApi.getVipRankListBySiteCode(siteCode);
            vipRankMap = Optional.ofNullable(listResponseVO)
                    .map(ResponseVO::getData)
                    .map(list -> list.stream()
                            .filter(v -> v.getVipRankCode() != null && v.getVipRankNameI18nCode() != null)
                            .collect(Collectors.toMap(
                                    SiteVIPRankVO::getVipRankCode,
                                    SiteVIPRankVO::getVipRankNameI18nCode,
                                    (oldVal, newVal) -> oldVal
                            )))
                    .orElseGet(HashMap::new);
        }
        return vipRankMap;
    }


    private  ResponseVO<Page<SiteVipAwardRecordVo>> getCnData(SiteVipAwardRecordReqVo vo){
        try {
            Page<SiteVipAwardRecordV2PO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
            LambdaQueryWrapper<SiteVipAwardRecordV2PO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SiteVipAwardRecordV2PO::getSiteCode,vo.getSiteCode());
            if (ObjectUtil.isNotEmpty(vo.getReceiveTimeStart())) {
                queryWrapper.ge(SiteVipAwardRecordV2PO::getReceiveTime, vo.getReceiveTimeStart());
            }
            if (ObjectUtil.isNotEmpty(vo.getReceiveTimeEnd())) {
                queryWrapper.le(SiteVipAwardRecordV2PO::getReceiveTime, vo.getReceiveTimeEnd());
            }
            if (ObjectUtil.isNotEmpty(vo.getCreatedTimeStart())) {
                queryWrapper.ge(SiteVipAwardRecordV2PO::getCreatedTime, vo.getCreatedTimeStart());
            }
            if (ObjectUtil.isNotEmpty(vo.getCreatedTimeEnd())) {
                queryWrapper.le(SiteVipAwardRecordV2PO::getCreatedTime, vo.getCreatedTimeEnd());
            }
            if (ObjectUtil.isNotEmpty(vo.getExpiredTimeStart())) {
                queryWrapper.ge(SiteVipAwardRecordV2PO::getExpiredTime, vo.getExpiredTimeStart());
            }
            if (ObjectUtil.isNotEmpty(vo.getExpiredTimeEnd())) {
                queryWrapper.le(SiteVipAwardRecordV2PO::getExpiredTime, vo.getExpiredTimeEnd());
            }
            if (ObjectUtil.isNotEmpty(vo.getReceiveType())) {
                queryWrapper.eq(SiteVipAwardRecordV2PO::getReceiveType, vo.getReceiveType());
            }
            if (ObjectUtil.isNotEmpty(vo.getReceiveStatus())) {
                queryWrapper.eq(SiteVipAwardRecordV2PO::getReceiveStatus, vo.getReceiveStatus());
            }
            if (ObjectUtil.isNotEmpty(vo.getOrderId())) {
                queryWrapper.eq(SiteVipAwardRecordV2PO::getOrderId, vo.getOrderId());
            }
            if (ObjectUtil.isNotEmpty(vo.getUserAccount())) {
                queryWrapper.eq(SiteVipAwardRecordV2PO::getUserAccount, vo.getUserAccount());
            }
            if (ObjectUtil.isNotEmpty(vo.getAccountType())) {
                queryWrapper.eq(SiteVipAwardRecordV2PO::getAccountType, vo.getAccountType());
            }
            if (ObjectUtil.isNotEmpty(vo.getAwardType())) {
                queryWrapper.eq(SiteVipAwardRecordV2PO::getAwardType, vo.getAwardType());
            }
            queryWrapper.orderByDesc(SiteVipAwardRecordV2PO::getCreatedTime);

            Page<SiteVipAwardRecordV2PO> resultPage = siteVipAwardRecordV2Repository.selectPage(page, queryWrapper);

            LambdaQueryWrapper<SiteVIPGradePO> vipQuery = Wrappers.lambdaQuery();
            vipQuery.eq(SiteVIPGradePO::getSiteCode,vo.getSiteCode());
            List<VIPGradeVO> siteVIPGradePOS = siteVipOptionService.getInitVIPGrade();

            Map<Integer,String> gradeMap = siteVIPGradePOS.stream().collect(Collectors.toMap(VIPGradeVO::getVipGradeCode,VIPGradeVO::getVipGradeName));
            Page<SiteVipAwardRecordVo> result = new Page<>();
            List<SiteVipAwardRecordVo> voList = Lists.newArrayList();
            if (ObjectUtils.isNotEmpty(resultPage.getRecords())) {
                resultPage.getRecords().forEach(obj -> {
                    SiteVipAwardRecordVo temp = new SiteVipAwardRecordVo();
                    BeanUtils.copyProperties(obj, temp);
                    temp.setVipGrade(gradeMap.get(obj.getVipGradeCode()));
                    voList.add(temp);
                });
                BeanUtils.copyProperties(resultPage, result);
            }
            result.setRecords(voList);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询VIP奖励异常", e);
            return ResponseVO.fail(ResultCode.VIP_AWARD_QUERY_ERROR);
        }
    }
}
