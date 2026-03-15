package com.cloud.baowang.activity.api;

import com.cloud.baowang.activity.api.api.ActivityReportApi;
import com.cloud.baowang.activity.api.vo.report.DataReportReqVO;
import com.cloud.baowang.activity.api.vo.report.DataReportRespVO;
import com.cloud.baowang.activity.service.ActivityReportService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption: 活动报表相关
 * @Author: Ford
 * @Date: 2024/10/3 11:54
 * @Version: V1.0
 **/
@Slf4j
@RestController
@AllArgsConstructor
public class ActivityReportApiImpl implements ActivityReportApi {

    private final ActivityReportService activityReportService;

    /**
     * 站点活动数据报表
     * @param dataReportReqVO 请求参数
     * @return
     */
    @Override
    public ResponseVO<DataReportRespVO> getDataReportPage(DataReportReqVO dataReportReqVO) {
        return ResponseVO.success(activityReportService.getDataReportPage(dataReportReqVO));
    }
}
