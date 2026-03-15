package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteUserManualUpDownApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员人工加减额 服务")
public interface UserManualUpDownApi {

    @Operation(summary = "会员人工加减额 统计数据查询")
    @PostMapping(value = "/user-manual-up-down/api/listStaticData")
    Map<String, UserManualDownRecordVO> listStaticData(@RequestBody List<String> userIds);



    @Operation(summary = "会员人工加减额 统计数据查询")
    @PostMapping(value = "/user-manual-up-down/api/listPage")
    Page<UserManualDownRecordVO> listPage(@RequestBody UserManualDownRecordRequestVO userManualDownRecordRequestVO);

    @Operation(summary = "获取人工加减额活动金额")
    @PostMapping(value = "/user-manual-up-down/api/getActivityAmountByUserId")
    BigDecimal getActivityAmountByUserId(@RequestParam("userId")String userId);

}
