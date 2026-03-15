package com.cloud.baowang.user.api.auth;

import com.cloud.baowang.user.api.api.auth.AliAuthApi;
import com.cloud.baowang.user.service.AliAuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AliAuthApiImpl implements AliAuthApi {
    private final AliAuthService aliAuthService;

    @Override
    public boolean bankVerification(String userName, String bankCard) {
        return aliAuthService.bankVerification(userName, bankCard);
    }

    @Override
    public boolean phoneVerify(String userName, String phone) {
        return aliAuthService.phoneVerify(userName, phone);
    }
}
