package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.SiteActivityLotteryRecordReqVO;
import com.cloud.baowang.activity.api.vo.SiteActivityLotteryRecordRespVO;
import com.cloud.baowang.activity.po.SiteActivityLotteryRecordPO;
import com.cloud.baowang.activity.po.SiteActivityRewardSpinWheelPO;
import com.cloud.baowang.activity.po.SiteTaskOrderRecordPO;
import com.cloud.baowang.activity.repositories.SiteActivityLotteryRecordRepository;
import com.cloud.baowang.activity.repositories.SiteActivityRewardSpinWheelRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @className: SiteActivityRewardVipGradeService
 * @author: wade
 * @description:
 * @date: 10/9/24 19:13
 */
@Service
@AllArgsConstructor
public class SiteActivityLotteryRecordService extends ServiceImpl<SiteActivityLotteryRecordRepository, SiteActivityLotteryRecordPO> {

    private final SiteActivityLotteryRecordRepository siteActivityLotteryRecordRepository;

    private final VipGradeApi vipGradeApi;

    private final VipRankApi vipRankApi;

    private final SystemParamApi systemParamApi;

    private LambdaQueryWrapper<SiteActivityLotteryRecordPO> buildQueryWrapper(SiteActivityLotteryRecordReqVO requestVO) {
        LambdaQueryWrapper<SiteActivityLotteryRecordPO> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ObjectUtil.isNotEmpty(requestVO.getSiteCode()), SiteActivityLotteryRecordPO::getSiteCode, requestVO.getSiteCode());
        queryWrapper.le(ObjectUtil.isNotEmpty(requestVO.getReceiveTimeEnd()), SiteActivityLotteryRecordPO::getCreatedTime, requestVO.getReceiveTimeEnd());
        queryWrapper.ge(ObjectUtil.isNotEmpty(requestVO.getReceiveTimeStart()), SiteActivityLotteryRecordPO::getCreatedTime, requestVO.getReceiveTimeStart());
        queryWrapper.like(ObjectUtil.isNotEmpty(requestVO.getUserAccount()), SiteActivityLotteryRecordPO::getUserAccount, requestVO.getUserAccount());
        queryWrapper.eq(ObjectUtil.isNotEmpty(requestVO.getPrizeSource()), SiteActivityLotteryRecordPO::getPrizeSource, requestVO.getPrizeSource());

        if (!StringUtils.isBlank(requestVO.getOrderField())) {
            if (StringUtils.equalsIgnoreCase("createTime", requestVO.getOrderField()) && StringUtils.equalsIgnoreCase("desc", requestVO.getOrderType())) {
                queryWrapper.orderByDesc(SiteActivityLotteryRecordPO::getCreatedTime);
            }
            if (StringUtils.equalsIgnoreCase("createTime", requestVO.getOrderField()) && StringUtils.equalsIgnoreCase("asc", requestVO.getOrderType())) {
                queryWrapper.orderByAsc(SiteActivityLotteryRecordPO::getCreatedTime);
            }
        } else {
            queryWrapper.orderByDesc(SiteActivityLotteryRecordPO::getCreatedTime); // 默认按创建时间倒序
        }

        return queryWrapper;
    }

    public Long getTotalCount(SiteActivityLotteryRecordReqVO requestVO) {

        LambdaQueryWrapper<SiteActivityLotteryRecordPO> queryWrapper = buildQueryWrapper(requestVO);
        return siteActivityLotteryRecordRepository.selectCount(queryWrapper);
    }

    public Page<SiteActivityLotteryRecordRespVO> activityPageList(SiteActivityLotteryRecordReqVO requestVO) {

        Page<SiteActivityLotteryRecordPO> respVOPage = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        LambdaQueryWrapper<SiteActivityLotteryRecordPO> queryWrapper = buildQueryWrapper(requestVO);

        Page<SiteActivityLotteryRecordPO> activityInfoPage = siteActivityLotteryRecordRepository.selectPage(respVOPage, queryWrapper);


        Page<SiteActivityLotteryRecordRespVO> resultPage = new Page<>();
        BeanUtils.copyProperties(activityInfoPage, resultPage);
        if (!CollectionUtils.isEmpty(activityInfoPage.getRecords())) {
            // 查询vip等级
            List<SiteVIPGradeVO> siteVIPGradeVOS = vipGradeApi.queryAllVIPGrade(requestVO.getSiteCode());
            Map<Integer, String> vipMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(siteVIPGradeVOS)) {
                vipMap = siteVIPGradeVOS.stream().collect(Collectors.toMap(vo -> vo.getVipGradeCode(), SiteVIPGradeVO::getVipGradeName, (k1, k2) -> k2));
            }
            // 查询vip Rank
            List<SiteVIPRankVO> siteVIPRankVOS = vipRankApi.getVipRankListBySiteCode(requestVO.getSiteCode()).getData();
            Map<Integer, String> vipRankMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(siteVIPRankVOS)) {
                vipRankMap = siteVIPRankVOS.stream().collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode, SiteVIPRankVO::getVipRankNameI18nCode, (k1, k2) -> k2));
            }
            // 查询 来源
            /*List<CodeValueVO> sources = systemParamApi.getSystemParamByType(CommonConstant.ACTIVITY_PRIZE_SOURCE).getData();
            Map<String, String> sourceMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(sources)){
                sourceMap = sources.stream().collect(Collectors.toMap(CodeValueVO::getCode,CodeValueVO::getValue,(k1, k2)->k2));
            }*/
            resultPage.setRecords(ConvertUtil.entityListToModelList(activityInfoPage.getRecords(), SiteActivityLotteryRecordRespVO.class));
            Map<Integer, String> finalVipMap = vipMap;
            Map<Integer, String> finalVipRankMap = vipRankMap;
            /*Map<String, String> finalSourceMap = sourceMap;*/
            resultPage.getRecords().forEach(e -> {
                if (ObjectUtils.isNotEmpty(e.getVipGradeCode())) {
                    e.setVipGradeCodeName(finalVipMap.get(e.getVipGradeCode()));
                }
                if (ObjectUtils.isNotEmpty(e.getVipRankCode())) {
                    e.setVipRankCodeName(finalVipRankMap.get(e.getVipRankCode()));
                }
                /*if(ObjectUtils.isNotEmpty(e.getPrizeSource())){
                    e.setPrizeSourceName(finalSourceMap.get(e.getPrizeSource()));
                }*/


            });
        }
        return resultPage;
    }


}
