package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserReceiveAccountUnBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountQueryVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountUnBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserAccountBindBaseInfoVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteUserReceiveAccountApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - userReceiveAccount")
public interface UserReceiveAccountApi {

    String PREFIX = ApiConstants.PREFIX + "/userReceiveAccount/api/";

    @Operation(summary = "大陆盘-会员收款信息 银行卡，电子钱包，加密货币列表")
    @PostMapping(value = PREFIX + "userReceiveAccount")
    ResponseVO<List<UserReceiveAccountResponseVO>> userReceiveAccount(@RequestBody WalletUserBasicRequestVO requestVO);


    @Operation(summary = "大陆盘-客户端会员收款信息 银行卡，电子钱包，加密货币列表")
    @PostMapping(value = PREFIX + "clientUserReceiveAccount")
    ResponseVO<List<UserReceiveAccountResponseVO>> clientUserReceiveAccount(@RequestBody WalletUserBasicRequestVO requestVO);

    @Operation(summary = "大陆盘-会员详情-收款信息 解绑申请")
    @PostMapping(value = PREFIX + "userReceiveAccountUnBind")
    ResponseVO<Boolean> userReceiveAccountUnBind(@RequestBody UserReceiveAccountUnBindVO vo);

    @Operation(summary = "大陆盘-会员详情-收款信息 绑定")
    @PostMapping(value = PREFIX + "userReceiveAccountBind")
    ResponseVO<Boolean> userReceiveAccountBind (@RequestBody UserReceiveAccountBindVO vo);

    @Operation(summary = "大陆盘-客户端-收款信息 解除绑定")
    @PostMapping(value = PREFIX + "clientUserReceiveAccountUnBind")
    ResponseVO<Boolean> clientUserReceiveAccountUnBind(@RequestBody ClientUserReceiveAccountUnBindVO vo);

    @Operation(summary = "根据加密货币地址查询绑定账号信息")
    @PostMapping(value = PREFIX + "getUserReceiveAccountByAddressNo")
    ResponseVO<UserReceiveAccountVO> getUserReceiveAccountByAddressNo(@RequestParam("receiveAccount") String receiveAccount);

    @Operation(summary = "获取会员收款信息绑定收集信息")
    @PostMapping(value = PREFIX + "getCollectInfo")
    ResponseVO<List<WithdrawCollectInfoVO>> getCollectInfo(@RequestBody UserReceiveAccountQueryVO vo);

    @Operation(summary = "获取绑定基础信息，银行卡，加密货币协议，是否需要身份认证")
    @PostMapping(value = PREFIX + "getBindBaseInfo")
    ResponseVO<UserAccountBindBaseInfoVO> getBindBaseInfo();

    @Operation(summary = "客户端会员收款信息设置默认")
    @PostMapping(value = PREFIX + "userReceiveAccountDefault")
    ResponseVO<Boolean> userReceiveAccountDefault(@RequestBody IdVO vo);

    @Operation(summary = "站点解绑收款信息")
    @PostMapping(value = PREFIX + "siteUerReceiveAccountUnBind")
    ResponseVO<Boolean> siteUerReceiveAccountUnBind(@RequestBody IdVO vo);

}
