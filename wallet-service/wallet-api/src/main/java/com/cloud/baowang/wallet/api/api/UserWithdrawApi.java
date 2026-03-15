package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.withdraw.CheckRemainingFlowVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithDrawApplyVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserWithdrawApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员提款 服务")
public interface UserWithdrawApi {

    String PREFIX = ApiConstants.PREFIX + "/remoteUserWithdrawApi/api/";

    @Operation(summary = "会员提现申请")
    @PostMapping(value = PREFIX + "userWithdrawApply")
    ResponseVO<Integer> userWithdrawApply(@RequestBody UserWithDrawApplyVO vo);

    @Operation(summary = "会员提款配置")
    @PostMapping(value = PREFIX + "getWithdrawConfig")
    WithdrawConfigVO getWithdrawConfig(@RequestBody WithdrawConfigRequestVO withdrawConfigRequestVO);

    @Operation(summary = "校验剩余流水")
    @PostMapping(value = PREFIX + "checkRemainingFlow")
    CheckRemainingFlowVO checkRemainingFlow(@RequestParam("userId") String userId);
}
