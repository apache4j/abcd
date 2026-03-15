package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 会员充值提现统计报表
 */
@FeignClient(contextId = "remoteUserDepositWithDrawStaticReportApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员充值提现统计报表 服务")
public interface UserDepositWithDrawStaticReportApi {

    String PREFIX = ApiConstants.PREFIX + "/userDepositWithdrawStaticReport/api/";
    /**
     *  元宇宙大富翁 任意单个自然月 虚拟币 充值排行名次 n
     * @return
     */
    @Operation(summary = "元宇宙大富翁 勋章派发")
    @PostMapping(value = PREFIX + "staticVirtualDepositMedal1016ByMonth")
    ResponseVO<Boolean> staticVirtualDepositMedal1016ByMonth(@RequestParam("siteCode") String siteCode,@RequestParam("timeZone") String timeZone);


    /**
     * 富甲天下
     * @return
     */
    @Operation(summary = "富甲天下 勋章派发")
    @PostMapping(value = PREFIX + "staticVirtualDepositMedal1018ByMonth")
    ResponseVO<Boolean> staticVirtualDepositMedal1018ByMonth(@RequestParam("siteCode") String siteCode,@RequestParam("timeZone") String timeZone);



    /**
     * 大老板
     * 任意单个自然月内总提款金额名次 n
     * @return
     */
    @Operation(summary = "大老板 勋章派发")
    @PostMapping(value = PREFIX + "staticVirtualWithdrawMedal1019ByMonth")
    ResponseVO<Boolean> staticVirtualWithdrawMedal1019ByMonth(@RequestParam("siteCode") String siteCode,@RequestParam("timeZone") String timeZone);

}
