package com.cloud.baowang.system.api.site.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;

import com.cloud.baowang.system.api.api.site.rebate.ReportUserRebateRecordApi;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateInfoVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateRspVO;
import com.cloud.baowang.system.service.site.rebate.UserRebateVenueRecordService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
public class ReportUserRebateRecordApiImpl implements ReportUserRebateRecordApi {
    private final UserRebateVenueRecordService recordService;
    @Override
    public ResponseVO<ReportUserRebateRspVO> listPage(ReportUserRebateQueryVO reqVO) {
        return recordService.siteRebateInfoPage(reqVO);
    }

    @Override
    public ResponseVO<Page<ReportUserRebateInfoVO>> venueRebateDetails(ReportUserRebateQueryVO reqVo) {
        return recordService.venueRebateDetails(reqVo);
    }


}
