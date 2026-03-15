package com.cloud.baowang.activity.api.v2;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.v2.ActivityFinanceV2Api;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceReqVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceRespVO;
import com.cloud.baowang.activity.service.v2.ActivityFinanceV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityOrderRecordV2Service;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/26 10:35
 * @Version: V1.0
 **/
@Slf4j
@RestController
@AllArgsConstructor
public class ActivityFinanceV2ApiImpl implements ActivityFinanceV2Api {

    private ActivityFinanceV2Service activityFinanceService;

    private SiteActivityOrderRecordV2Service siteActivityOrderRecordService;

    @Override
    public ResponseVO<Page<ActivityFinanceRespVO>> financeListPage(ActivityFinanceReqVO activityFinanceReqVO) {
        return ResponseVO.success(activityFinanceService.financeListPage(activityFinanceReqVO));
    }

    @Override
    public ResponseVO<Void> bachInvalidData() {
        return siteActivityOrderRecordService.bachInvalidData();
    }


}
