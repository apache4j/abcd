package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineResVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.risk.RiskWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserDepositWithdrawVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordPagesVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteUserWithdrawRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员提款记录 服务")
public interface UserWithdrawRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userWithdrawRecord/api/";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "withdrawalRecordPageList")
    UserWithdrawRecordPagesVO withdrawalRecordPageList(@RequestBody UserWithdrawalRecordRequestVO vo);

    @Operation(summary = "记录数")
    @PostMapping(value = PREFIX + "withdrawalRecordPageCount")
    Long withdrawalRecordPageCount(@RequestBody UserWithdrawalRecordRequestVO vo);


    @Operation(summary = "会员存取款统计")
    @PostMapping(value = PREFIX + "getUserDepositWithdraw")
    WalletUserDepositWithdrawVO getUserDepositWithdraw(@RequestParam("userId") String userId);

    @Operation(summary = "查询代理下会员存/取款总计")
    @PostMapping(value = PREFIX + "getUserFundsListByAgent")
    List<WalletAgentSubLineResVO> getUserFundsListByAgent(@RequestBody WalletAgentSubLineReqVO reqVO);

    @Operation(summary = "根据时间和类型分组计算代理下会员人工加减金额 总计")
    @PostMapping(PREFIX + "getManualAmountGroupAgent")
    List<WalletAgentSubLineResVO> getManualAmountGroupAgent(@RequestBody WalletAgentSubLineReqVO reqVO);

    @Operation(summary = "根据订单id查询记录")
    @PostMapping(PREFIX + "getRecordByOrderId")
    UserDepositWithdrawalResVO getRecordByOrderId(@RequestParam("orderId") String orderId);


    @Operation(summary = "会员提款记录总数")
    @PostMapping(PREFIX + "getWithDrawalRecord")
    UserDepositRecordRespVO getWithDrawalRecord(@RequestBody UserWithdrawalRecordRequestVO vo);

    @Operation(summary = "会员提款重复项查询")
    @PostMapping(PREFIX + "getWithdrawalRecordDuplicateList")
    Page<RiskWithdrawRecordVO> getWithdrawalRecordDuplicateList(@RequestBody UserWithdrawalRecordRequestVO vo);

    @Operation(summary = "会员提款重复项总条数查询")
    @PostMapping(PREFIX + "getWithdrawalRecordDuplicateListCount")
    long getWithdrawalRecordDuplicateListCount(@RequestBody UserWithdrawalRecordRequestVO vo);

    @Operation(summary = "会员第一次提现成功")
    @PostMapping(PREFIX + "getUserFirstSuccessWithdrawal")
    UserDepositWithdrawalResVO getUserFirstSuccessWithdrawal(@RequestBody UserWithdrawalRecordRequestVO vo);
}
