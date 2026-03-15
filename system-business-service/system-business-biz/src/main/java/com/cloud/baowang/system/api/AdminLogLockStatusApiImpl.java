package com.cloud.baowang.system.api;


import com.cloud.baowang.system.api.api.AdminLogLockStatusApi;
import com.cloud.baowang.system.service.member.AdminLogLockStatusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AdminLogLockStatusApiImpl implements AdminLogLockStatusApi {
    private final AdminLogLockStatusService logLockStatusService;


    @Override
    public Boolean checkAdminIsLock(String siteCode, String userAccount) {
        return logLockStatusService.checkAdminIsLock(siteCode, userAccount);
    }

    @Override
    public void removeAdminLockStatus(String siteCode, String userAccount) {
        logLockStatusService.removeAdminLockStatus(siteCode, userAccount);
    }

    @Override
    public void addAdminLockStatus(String siteCode, String userAccount,Long timeStamp) {
        logLockStatusService.addAdminLockStatus(siteCode, userAccount,timeStamp);
    }
}
