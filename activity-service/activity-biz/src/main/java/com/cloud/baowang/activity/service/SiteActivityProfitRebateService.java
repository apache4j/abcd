package com.cloud.baowang.activity.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.vo.ActivityLossInSportsRespVO;
import com.cloud.baowang.activity.api.vo.ActivityLossInSportsVO;
import com.cloud.baowang.activity.api.vo.RechargePercentageVO;
import com.cloud.baowang.activity.api.vo.SiteActivityProfitRebateDetail;
import com.cloud.baowang.activity.po.SiteActivityProfitRebatePO;
import com.cloud.baowang.activity.repositories.SiteActivityProfitRebateRepository;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
@Slf4j
@Service
public class SiteActivityProfitRebateService extends
        ServiceImpl<SiteActivityProfitRebateRepository, SiteActivityProfitRebatePO> {


    public ActivityLossInSportsRespVO getActivityByActivityId(String activityId) {
        SiteActivityProfitRebatePO activityProfitRebatePO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityProfitRebatePO.class)
                .eq(SiteActivityProfitRebatePO::getActivityId, activityId));
        ActivityLossInSportsRespVO vo = ActivityLossInSportsRespVO.builder().build();
        BeanUtils.copyProperties(activityProfitRebatePO, vo);


        String activityDetail = activityProfitRebatePO.getActivityDetail();
        String[] detailArray = activityDetail.split(",");
        List<String> venueCodeList = Arrays.asList(activityProfitRebatePO.getVenueCode().split(","));
        vo.setVenueCodeList(venueCodeList);
        if (ObjectUtil.isNotEmpty(activityDetail)) {
            List<SiteActivityProfitRebateDetail> detailList = Lists.newArrayList();

            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(activityProfitRebatePO.getActivityDiscountType())) {
                String[] rebateDetail = activityDetail.split("-");
                RechargePercentageVO percentageVO = new RechargePercentageVO();
                BigDecimal minDeposit = new BigDecimal(rebateDetail[0]);
                BigDecimal discountPct = new BigDecimal(rebateDetail[1]);
                BigDecimal maxDailyBonus = new BigDecimal(rebateDetail[2]);
                percentageVO.setMinDeposit(minDeposit);
                percentageVO.setDiscountPct(discountPct);
                percentageVO.setMaxDailyBonus(maxDailyBonus);
                vo.setPercentageVO(percentageVO);
            }

            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(activityProfitRebatePO.getActivityDiscountType())) {
                for (String detail : detailArray) {
                    String[] rebateDetail = detail.split("-");
                    detailList.add(SiteActivityProfitRebateDetail.builder()
                            .startAmount(new BigDecimal(rebateDetail[0]))
                            .endAmount(new BigDecimal(rebateDetail[1]))
                            .rebateAmount(new BigDecimal(rebateDetail[2]))
                            .build());
                }
                vo.setActivityDetail(detailList);
            }

        }
        return vo;
    }

    public boolean save(ActivityLossInSportsVO activity, String baseId) {
        SiteActivityProfitRebatePO po = new SiteActivityProfitRebatePO();
        BeanUtils.copyProperties(activity, po);
        StringBuilder stringBuilder = new StringBuilder();

        po.setVenueCode(String.join(",", activity.getVenueCodeList()));
        if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(activity.getActivityDiscountType())) {
            RechargePercentageVO percentageVO = activity.getPercentageVO();
            BigDecimal minDeposit = percentageVO.getMinDeposit();
            BigDecimal discountPct = percentageVO.getDiscountPct();
            BigDecimal maxDailyBonus = percentageVO.getMaxDailyBonus();
            stringBuilder.append(minDeposit).append("-").append(discountPct).append("-").append(maxDailyBonus);
        }

        if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(activity.getActivityDiscountType())) {
            List<SiteActivityProfitRebateDetail> activityDetail = activity.getActivityDetail();
            for (int i = 0; i < activityDetail.size(); i++) {
                SiteActivityProfitRebateDetail detail = activityDetail.get(i);
                BigDecimal startAmount = detail.getStartAmount();
                BigDecimal endAmount = detail.getEndAmount();
                BigDecimal rebateAmount = detail.getRebateAmount();
                stringBuilder.append(startAmount).append("-").append(endAmount).append("-").append(rebateAmount);
                if (i < activityDetail.size() - 1) {
                    stringBuilder.append(",");
                }
            }
        }

        po.setActivityId(baseId);
        po.setActivityDetail(stringBuilder.toString());
        po.setSiteCode(CurrReqUtils.getSiteCode());

        return baseMapper.insert(po) > 0;
    }


}
