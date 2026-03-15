package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceQueryVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpSubmitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteUserManualUpApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员人工添加额度 服务")
public interface UserManualUpApi {

    String PREFIX = ApiConstants.PREFIX + "/userManualUp/api/";

    @Operation(summary = "提交")
    @PostMapping(value = PREFIX + "submit")
    ResponseVO<Boolean> submit(@RequestBody UserManualUpSubmitVO vo, @RequestParam("operator") String operator);

    @Operation(summary = "会员账号查询-输入框查询")
    @PostMapping(value = PREFIX + "getUserBalance")
    ResponseVO<GetUserBalanceVO> getUserBalance(@RequestBody GetUserBalanceQueryVO vo);


    @Operation(summary = "会员加额账号信息导入校验")
    @PostMapping(value = PREFIX + "checkUpUserAccountInfo")
    ResponseVO<UserManualAccountResponseVO> checkUpUserAccountInfo(@RequestBody List<UserManualAccountResultVO> list);

    @Operation(summary = "会员减额账号信息导入校验")
    @PostMapping(value = PREFIX + "checkDownUserAccountInfo")
    ResponseVO<UserManualDownAccountResponseVO> checkDownUserAccountInfo(@RequestBody List<UserManualDownAccountResultVO> list);
}
