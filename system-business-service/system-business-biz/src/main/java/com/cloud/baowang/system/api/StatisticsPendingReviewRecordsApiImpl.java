package com.cloud.baowang.system.api;

import com.cloud.baowang.system.api.api.StatisticsPendingReviewRecordsApi;
import com.cloud.baowang.system.api.vo.StatisticsPendingVO;
import com.cloud.baowang.system.service.StatisticsPendingReviewRecordsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class StatisticsPendingReviewRecordsApiImpl implements StatisticsPendingReviewRecordsApi {
    private final StatisticsPendingReviewRecordsService recordsService;

    @Override
    public List<StatisticsPendingVO> getRecordsBySiteCode(String siteCode) {
        return recordsService.getRecordsBySiteCode(siteCode);
    }

    @Override
    public StatisticsPendingVO getAgentInfoReviewRecord(String siteCode) {
        return recordsService.getAgentInfoReviewRecord(siteCode);
    }
}
