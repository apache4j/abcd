package com.cloud.baowang.report.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.vip.ReportVIPDataReq;
import com.cloud.baowang.report.api.vo.vip.ReportVIPDataVO;
import com.cloud.baowang.report.api.vo.vip.VIPDataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteVIPDataApi", value = ApiConstants.NAME)
@Tag(name = "RPC VIP数据报表相关")
public interface ReportVIPDataApi {

    String prefix = "/vipDataApi/api";

    @Operation(summary = "收集VIP数据报表相关数据")
    @PostMapping(value = prefix + "/collectVIPDataReport")
    void collectVIPDataReport(@RequestBody VIPDataVO vo);

    @Operation(summary = "查询VIP数据报表分页")
    @PostMapping(value = prefix + "/pageVIPData")
    ResponseVO<ReportVIPDataVO> pageVIPData(@RequestBody ReportVIPDataReq vo);

    @Operation(summary = "查询VIP数据报表总条数")
    @PostMapping(value = prefix + "/getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody ReportVIPDataReq req);
}
