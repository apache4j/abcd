package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserWithdrawRunningWaterVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.GetUserTypingAmountVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingAmountVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.WithdrawRunningWaterAddVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteUserTypingAmountApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员打码量 服务")
public interface UserTypingAmountApi {

    String PREFIX = ApiConstants.PREFIX + "/userTypingAmount/api";


    @Operation(summary = "清除会员流水")
    @PostMapping(value = PREFIX+"cleanWithdrawRunningWater")
    ResponseVO<Object> cleanWithdrawRunningWater(@RequestParam("siteCode") String siteCode,@RequestParam("userAccount") String userAccount);

    @Operation(summary = "添加会员流水")
    @PostMapping(value = "/addWithdrawRunningWater")
    ResponseVO<Object> addWithdrawRunningWater(@RequestBody WithdrawRunningWaterAddVO requestVO);


    @Operation(summary = "获取提现流水信息")
    @PostMapping(value = "/getWithdrawRunningWater")
    UserWithdrawRunningWaterVO getWithdrawRunningWater(@RequestBody WalletUserBasicRequestVO requestVO);

    @Operation(summary = "获取会员打码量" )
    @PostMapping(value = PREFIX+ "/getUserTypingAmountByAccount")
    UserTypingAmountVO getUserTypingAmountByAccount(@RequestParam("siteCode") String siteCode,@RequestParam("userAccount") String userAccount);

    @Operation(summary = "批量获取会员打码量" )
    @PostMapping(value = PREFIX+ "/getUserTypingAmountListByAccounts")
    List<UserTypingAmountVO> getUserTypingAmountListByAccounts(@RequestBody GetUserTypingAmountVO vo);


    @Operation(summary = "获取会员打码量" )
    @PostMapping(value = PREFIX+ "/getUserTypingAmount")
    UserTypingAmountVO getUserTypingAmount(@RequestParam("userAccount") String userAccount,@RequestParam("siteCode") String siteCode);

    /**
     * 获取会员打码量记录
     * @param vo
     * @return
     */
    @Operation(summary = "获取会员打码量记录" )
    @PostMapping(value = PREFIX+ "/listUserTypingRecordPage")
    ResponseVO<Page<UserTypingRecordVO>> listUserTypingRecordPage(@RequestBody UserTypingRecordRequestVO vo);


    @Operation(summary = "获取会员打码量记录计数" )
    @PostMapping(value = PREFIX+ "/userTypingRecordPageCount")
    ResponseVO<Long> userTypingRecordPageCount(@RequestBody UserTypingRecordRequestVO vo);

    @Operation(summary = "会员打码量清零" )
    @PostMapping(value = PREFIX+ "/userTypingAmountCleanZero")
    void userTypingAmountCleanZero();

    @Operation(summary = "清除活动流水")
    @PostMapping(value = PREFIX+"cleanActivityRunningWater")
    ResponseVO<Object>  cleanActivityRunningWater(@RequestParam("siteCode") String siteCode,@RequestParam("userAccount") String userAccount);


    @Operation(summary = "根据会员id 校验，清零打码量" )
    @PostMapping(value = PREFIX+ "/userTypingAmountCleanZeroByUserId")
    void userTypingAmountCleanZeroByUserId(@RequestParam("userId") String userId);
}
