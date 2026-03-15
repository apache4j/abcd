package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceQueryVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResultVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownSubmitVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpSubmitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(contextId = "remoteUserPlatformCoinManualUpDownApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员平台币调整 服务")
public interface UserPlatformCoinManualUpDownApi {

    String PREFIX = ApiConstants.PREFIX + "/userPlatformManualUp/api/";


    @Operation(summary = "平台币上分提交")
    @PostMapping(value = PREFIX + "savePlatformCoinManualUp")
    ResponseVO<Boolean> savePlatformCoinManualUp(@RequestBody UserPlatformCoinManualUpSubmitVO vo);

    @PostMapping(value = PREFIX + "savePlatformCoinManualDown")
    @Operation(summary = "平台币下分提交")
    ResponseVO<Boolean> savePlatformCoinManualDown(@RequestBody UserPlatformCoinManualDownSubmitVO vo) ;

    @Operation(summary = "会员账号查询-输入框查询")
    @PostMapping(value = PREFIX + "getUserBalance")
    ResponseVO<GetUserBalanceVO> getUserBalance(@RequestBody GetUserBalanceQueryVO vo);

    @Operation(summary = "会员平台币上分账号信息导入校验")
    @PostMapping(value = PREFIX + "checkUpUserAccountInfo")
    ResponseVO<UserManualAccountResponseVO> checkUpUserAccountInfo(@RequestBody List<UserManualAccountResultVO> list);

    @Operation(summary = "会员平台币下分信息导入校验")
    @PostMapping(value = PREFIX + "checkDownUserAccountInfo")
    ResponseVO<UserManualDownAccountResponseVO> checkDownUserAccountInfo(@RequestBody List<UserManualDownAccountResultVO> list);

    /**
     * 获取会员平台币调整总计金额
     * @param userId
     * @return
     */
    @Operation(summary = "获取会员平台币调整总计金额")
    @GetMapping(value = PREFIX + "getPlatManualUpDownAmount")
    BigDecimal getPlatManualUpDownAmount(@RequestParam("userId") String userId);
}
