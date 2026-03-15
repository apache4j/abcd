package com.cloud.baowang.activity.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceReqVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceRespVO;
import com.cloud.baowang.activity.service.v2.SiteActivityOrderRecordV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/26 10:37
 * @Version: V1.0
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityFinanceService {

    private final SiteActivityOrderRecordService siteActivityOrderRecordService;

    private final SiteActivityOrderRecordV2Service siteActivityOrderRecordV2Service;


    public Page<ActivityFinanceRespVO> financeListPage(ActivityFinanceReqVO activityFinanceReqVO) {

        Integer handicapMode = activityFinanceReqVO.getHandicapMode();

        List<String> activityTemplates= Lists.newArrayList();
        if (handicapMode==null || handicapMode==0){
            if(StringUtils.hasText(activityFinanceReqVO.getActivityRewardType())){
                for(ActivityTemplateEnum activityTemplateEnum:ActivityTemplateEnum.values()){
                    if(activityTemplateEnum.getTemplateRewardEnum().getType().equals(activityFinanceReqVO.getActivityRewardType())){
                        activityTemplates.add(activityTemplateEnum.getType());
                    }
                }
                activityFinanceReqVO.setActivityTemplates(activityTemplates);
            }
            return siteActivityOrderRecordService.financeListPage(activityFinanceReqVO);
        }else {
            if(StringUtils.hasText(activityFinanceReqVO.getActivityRewardType())){
                for(ActivityTemplateEnum activityTemplateEnum:ActivityTemplateEnum.values()){
                    if(activityTemplateEnum.getTemplateRewardEnum().getType().equals(activityFinanceReqVO.getActivityRewardType())){
                        activityTemplates.add(activityTemplateEnum.getType());
                    }
                }
                activityFinanceReqVO.setActivityTemplates(activityTemplates);
            }
            return siteActivityOrderRecordV2Service.financeListPage(activityFinanceReqVO);
        }

    }
}
