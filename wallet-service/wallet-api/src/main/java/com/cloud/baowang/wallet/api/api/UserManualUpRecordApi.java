package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentActiveVO;
import com.cloud.baowang.wallet.api.vo.agent.AgentUserTeamParam;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordResult;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.GetDepositWithdrawManualRecordListResponse;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.GetDepositWithdrawManualRecordListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserManualUpRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员人工加额记录 服务")
public interface UserManualUpRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userManualUpRecord/api/";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "getUpRecordPage")
    UserManualUpRecordResult getUpRecordPage(@RequestBody UserManualUpRecordPageVO vo);

    @Operation(summary = "总记录数")
    @PostMapping(value = PREFIX + "getUpRecordPageCount")
    ResponseVO<Long> getUpRecordPageCount(@RequestBody UserManualUpRecordPageVO vo);

    @Operation(summary = "查询满足条件的存款会员")
    @PostMapping(value = PREFIX + "getDepositActiveInfo")
    WalletAgentActiveVO getDepositActiveInfo(@RequestBody AgentUserTeamParam vo);

    @Operation(summary = "查询加额记录(会员存款(后台)) 和 减额记录(会员提款(后台))")
    @PostMapping(value = PREFIX +"getDepositWithdrawManualRecordList")
    GetDepositWithdrawManualRecordListResponse getDepositWithdrawManualRecordList(@RequestBody GetDepositWithdrawManualRecordListVO vo);
}