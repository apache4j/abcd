package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.wallet.api.vo.rebate.OrderRebateRequestVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@FeignClient(contextId = "userRebateApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员返水 服务")
public interface UserRebateApi {

    String PREFIX = ApiConstants.PREFIX + "/userRebate/api/";

    @Operation(summary = "记录用户返水")
    @PostMapping(value = PREFIX + "recordUserRebate")
    void recordUserRebate(@RequestBody List<OrderRebateRequestVO> rebateRequestVOList);

    @Operation(summary = "VIP升级奖励")
    @GetMapping(PREFIX + "vipUpgradeAward")
    void vipUpgradeAward(@RequestParam("upgradeStart") Date upgradeStart, @RequestParam("upgradeEnd") Date upgradeEnd);
}
