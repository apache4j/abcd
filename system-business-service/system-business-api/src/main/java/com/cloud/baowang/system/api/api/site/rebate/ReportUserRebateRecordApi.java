package com.cloud.baowang.system.api.api.site.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;

import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateInfoVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateRspVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "reportUserRebateRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 返水报表")
public interface ReportUserRebateRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/reportUserRebateRecord/api";

    @Operation(summary = "返水报表列表")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<ReportUserRebateRspVO> listPage(@RequestBody ReportUserRebateQueryVO reqVO);

    @Operation(summary = "场馆 返水 明细")
    @PostMapping(value = PREFIX + "/venueRebateDetails")
    ResponseVO<Page<ReportUserRebateInfoVO>> venueRebateDetails(@Valid @RequestBody ReportUserRebateQueryVO reqVo);


    /**
     *
     * @return 成功失败
     */
//    @Operation(summary = "代理报表数据初始化")
//    @PostMapping(value = PREFIX + "/init")
//    ResponseVO<Boolean> init(@RequestBody ReportAgentStaticsCondVO reportAgentStaticsCondVO);



}
