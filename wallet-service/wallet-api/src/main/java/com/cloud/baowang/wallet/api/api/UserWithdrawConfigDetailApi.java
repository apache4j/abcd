package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailAddOrUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailQueryVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author qiqi
 */


@FeignClient(contextId = "remoteUserWithdrawConfigDetailApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员提款配置详情 服务")
public interface UserWithdrawConfigDetailApi {


    String PREFIX = ApiConstants.PREFIX + "/userWithdrawConfigDetail/api/";

    @Operation(summary = "会员提款配置详细信息")
    @PostMapping("getUserWithdrawConfigDetail")
    UserWithdrawConfigDetailResponseVO getUserWithdrawConfigDetail(@RequestBody UserWithdrawConfigDetailQueryVO queryVO);

    @Operation(summary = "设置会员提款配置详细信息")
    @PostMapping("setUserWithdrawConfigDetail")
    Integer setUserWithdrawConfigDetail(@RequestBody UserWithdrawConfigDetailAddOrUpdateVO userWithdrawConfigAddVO);

    @Operation(summary = "恢复通用设置")
    @PostMapping("resetUserWithdrawConfigDetail")
    Integer resetUserWithdrawConfigDetail(@RequestParam("userId") String userId);

}
