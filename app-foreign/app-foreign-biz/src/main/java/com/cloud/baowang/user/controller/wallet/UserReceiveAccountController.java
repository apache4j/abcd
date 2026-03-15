package com.cloud.baowang.user.controller.wallet;


import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.UserReceiveAccountApi;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserReceiveAccountUnBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountQueryVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserAccountBindBaseInfoVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "客户端-个人中心-收款账户")
@RestController
@AllArgsConstructor
@RequestMapping("/userReceiveAccount/api")
public class UserReceiveAccountController {



    private final SystemParamApi systemParamApi;

    private final UserReceiveAccountApi userReceiveAccountApi;


    @Operation(summary = "下拉框 提款类型 withdraw_type ")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {

        List<String> list = new ArrayList<>();
        list.add(CommonConstant.WITHDRAW_TYPE);
        list.add(CommonConstant.YES_NO);
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(list).getData();
        List<CodeValueVO> typeList = map.get(CommonConstant.WITHDRAW_TYPE);
        typeList = typeList.stream()
                .filter(obj -> !obj.getCode().equals(WithdrawTypeEnum.MANUAL_WITHDRAW.getCode()))
                .collect(Collectors.toList());
        map.put(CommonConstant.WITHDRAW_TYPE,typeList);
        return ResponseVO.success(map);
    }



    @Operation(summary = "会员收款信息绑定 银行卡，电子钱包，加密货币 ")
    @PostMapping(value = "/userReceiveAccountBind")
    public ResponseVO<Boolean> userReceiveAccountBind(@Validated @RequestBody UserReceiveAccountBindVO vo) {

        return userReceiveAccountApi.userReceiveAccountBind(vo);
    }

    @Operation(summary = "获取会员收款信息列表")
    @PostMapping(value = "/getUserReceiveAccounts")
    public ResponseVO<List<UserReceiveAccountResponseVO>> getUserReceiveAccounts(@RequestBody UserReceiveAccountQueryVO vo) {
        WalletUserBasicRequestVO userBasicRequestVO = new WalletUserBasicRequestVO();
        userBasicRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        userBasicRequestVO.setUserAccount(CurrReqUtils.getAccount());
        return userReceiveAccountApi.clientUserReceiveAccount(userBasicRequestVO);
    }

    @Operation(summary = "客户端会员解除绑定")
    @PostMapping(value = "/clientUserReceiveAccountUnBind")
    public ResponseVO<Boolean> clientUserReceiveAccountUnBind(@RequestBody ClientUserReceiveAccountUnBindVO vo) {
        return userReceiveAccountApi.clientUserReceiveAccountUnBind(vo);
    }

    @Operation(summary = "获取会员收款信息绑定收集信息 ")
    @PostMapping(value = "/getCollectInfo")
    public ResponseVO<List<WithdrawCollectInfoVO>> getCollectInfo(@RequestBody UserReceiveAccountQueryVO vo) {

        return userReceiveAccountApi.getCollectInfo(vo);
    }

    @Operation(summary = "获取绑定基础信息，银行卡，加密货币协议，是否需要身份认证 ")
    @PostMapping(value = "/getBindBaseInfo")
    public ResponseVO<UserAccountBindBaseInfoVO> getBindBaseInfo() {

        return userReceiveAccountApi.getBindBaseInfo();
    }

    @Operation(summary = "客户端会员收款信息设置默认")
    @PostMapping(value = "/userReceiveAccountDefault")
    public ResponseVO<Boolean> userReceiveAccountDefault(@RequestBody IdVO vo) {
        return userReceiveAccountApi.userReceiveAccountDefault(vo);
    }

}
