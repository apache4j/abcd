package com.cloud.baowang.system.api.api;

import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.StatisticsPendingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "statisticsPendingReviewRecordsApi", value = ApiConstants.NAME)
@Tag(name = "RPC 站点首页统计待审核api")
public interface StatisticsPendingReviewRecordsApi {
    String PREFIX = ApiConstants.PREFIX + "/statistics/api/";

    @GetMapping(PREFIX + "getRecordsBySiteCode")
    @Operation(summary = "统计当前站点下所有待审核的提款/加额记录")
    List<StatisticsPendingVO> getRecordsBySiteCode(@RequestParam("siteCode") String siteCode);

    @GetMapping(PREFIX + "getAgentInfoReviewRecord")
    @Operation(summary = "统计当前站点下所有代理信息变更待审核记录数")
    StatisticsPendingVO getAgentInfoReviewRecord(@RequestParam("siteCode") String siteCode);
}
