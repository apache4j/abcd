package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayBatchRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayStatusRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSiteWithdrawWayApi", value = ApiConstants.NAME)
@Tag(name = "RPC服务-站点提现方式api")
public interface SiteWithdrawWayApi {

    String PREFIX = ApiConstants.PREFIX + "/site/withdrawWay/";

    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "提现方式批量新增")
    ResponseVO<Void> batchSave(@RequestBody SiteWithdrawWayBatchRequestVO siteWithdrawWayBatchRequestVO);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "提现方式启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SiteWithdrawWayStatusRequestVO siteWithdrawWayStatusRequestVO);

    @PostMapping(value = PREFIX + "queryBySite")
    @Operation(summary = "查询站点下提款方式")
    ResponseVO<List<SiteWithdrawWayResVO>> queryBySite();

    @PostMapping(value = PREFIX + "queryBySiteAndTypeCode")
    @Operation(summary = "查询站点下某个类型的提款方式")
    ResponseVO<List<SiteWithdrawWayResVO>> queryBySiteAndTypeCode(@RequestParam("siteCode") String siteCode,
                                                                  @RequestParam("typeCode") String typeCode);

    @GetMapping(value = PREFIX + "queryWithdrawListBySite")
    @Operation(summary = "查询站点下某个类型的提款方式")
    ResponseVO<List<SiteWithdrawWayResVO>> queryWithdrawListBySite(@RequestParam("siteCode") String siteCode);

    @GetMapping(value = PREFIX + "queryWithdrawWay")
    @Operation(summary = "查询站点下某个类型的提款方式")
    SiteWithdrawWayVO queryWithdrawWay(@RequestParam("siteCode") String siteCode, @RequestParam("withdrawWayId") String withdrawWayId);


    @GetMapping(value = PREFIX + "withdrawBoxByCurrency")
    @Operation(summary = "根据币种获取提款方式下拉框")
    ResponseVO<List<CodeValueVO>> queryListBySiteAndCurrencyCode(@RequestParam("siteCode") String siteCode,
                                                                 @RequestParam("currencyCode") String currencyCode);

    @PostMapping(value = PREFIX + "selectWithdrawPage")
    @Operation(summary = "站点下提款方式分页查询")
    ResponseVO<Page<SiteWithdrawWayResponseVO>> selectWithdrawPage(@RequestBody SiteWithdrawWayRequestVO siteWithdrawWayRequestVO);

    @PostMapping(value = PREFIX + "selectBySort")
    @Operation(summary = "站点下提款方式排序查询")
    ResponseVO<List<SiteWithdrawWayResponseVO>> selectBySort(@RequestBody SiteWithdrawWayRequestVO siteWithdrawWayRequestVO);

    @PostMapping(value = PREFIX + "batchSaveSort")
    @Operation(summary = "批量保存提款方式排序")
    ResponseVO<Boolean> batchSaveSort(@RequestParam("userAccount") String userAccount, @RequestBody List<SortNewReqVO> sortNewReqVOS);
}
