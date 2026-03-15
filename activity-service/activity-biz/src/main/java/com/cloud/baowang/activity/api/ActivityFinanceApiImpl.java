package com.cloud.baowang.activity.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityFinanceApi;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceReqVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceRespVO;
import com.cloud.baowang.activity.service.ActivityFinanceService;
import com.cloud.baowang.activity.service.SiteActivityOrderRecordService;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
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
public class ActivityFinanceApiImpl implements ActivityFinanceApi {

    private ActivityFinanceService activityFinanceService;

    private SiteActivityOrderRecordService siteActivityOrderRecordService;
    @Override
    public ResponseVO<Page<ActivityFinanceRespVO>> financeListPage(ActivityFinanceReqVO activityFinanceReqVO) {
        activityFinanceReqVO.setHandicapMode(CurrReqUtils.getHandicapMode());
        return ResponseVO.success(activityFinanceService.financeListPage(activityFinanceReqVO));
    }

    @Override
    public ResponseVO<Void> bachInvalidData() {
        return siteActivityOrderRecordService.bachInvalidData();
    }


}
