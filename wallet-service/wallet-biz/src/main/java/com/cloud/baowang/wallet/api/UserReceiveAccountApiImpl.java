package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.wallet.api.api.UserReceiveAccountApi;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserReceiveAccountUnBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountQueryVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountUnBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserAccountBindBaseInfoVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO;
import com.cloud.baowang.wallet.service.UserReceiveAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserReceiveAccountApiImpl implements UserReceiveAccountApi {

    private final UserReceiveAccountService userReceiveAccountService;

    @Override
    public ResponseVO<List<UserReceiveAccountResponseVO>> userReceiveAccount(WalletUserBasicRequestVO requestVO) {
        return ResponseVO.success(userReceiveAccountService.userReceiveAccount(requestVO));
    }

    @Override
    public ResponseVO<List<UserReceiveAccountResponseVO>> clientUserReceiveAccount(WalletUserBasicRequestVO requestVO){
        return ResponseVO.success(userReceiveAccountService.clientUserReceiveAccount(requestVO));
    }

    @Override
    public ResponseVO<Boolean> userReceiveAccountUnBind(UserReceiveAccountUnBindVO vo) {
        return userReceiveAccountService.userReceiveAccountUnBind(vo);
    }

    @Override
    public ResponseVO<Boolean> userReceiveAccountBind(UserReceiveAccountBindVO vo) {
        return userReceiveAccountService.userReceiveAccountBind(vo);
    }

    @Override
    public ResponseVO<Boolean> clientUserReceiveAccountUnBind(ClientUserReceiveAccountUnBindVO vo) {
        return userReceiveAccountService.clientUserReceiveAccountUnBind(vo);
    }

    @Override
    public ResponseVO<Boolean> siteUerReceiveAccountUnBind(IdVO vo){
        return userReceiveAccountService.siteUerReceiveAccountUnBind(vo);
    }
    @Override
    public ResponseVO<UserReceiveAccountVO> getUserReceiveAccountByAddressNo(String receiveAccount) {
        return userReceiveAccountService.getUserReceiveAccountByAddressNo(receiveAccount);
    }

    @Override
    public ResponseVO<List<WithdrawCollectInfoVO>> getCollectInfo(UserReceiveAccountQueryVO vo) {
        return userReceiveAccountService.getCollectInfo(vo);
    }

    @Override
    public ResponseVO<UserAccountBindBaseInfoVO> getBindBaseInfo() {

        return userReceiveAccountService.getBindBaseInfo();
    }

    @Override
    public ResponseVO<Boolean> userReceiveAccountDefault(IdVO vo) {
        return userReceiveAccountService.userReceiveAccountDefault(vo);
    }

}
