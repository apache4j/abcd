package com.cloud.baowang.system.api.api;

import com.cloud.baowang.system.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "adminLogLockStatusApi", value = ApiConstants.NAME)
@Tag(name = "RPC 职员锁定状态查询/解锁api - adminLogLockStatusApi")
public interface AdminLogLockStatusApi {
    String PREFIX = ApiConstants.PREFIX + "/adminLogLockStatusApi/api";

    @Operation(summary = "查询某个职员有无被锁定")
    @GetMapping(PREFIX + "/checkAdminIsLock")
    Boolean checkAdminIsLock(@RequestParam("siteCode") String siteCode,
                             @RequestParam("userAccount") String userAccount);

    @Operation(summary = "解锁")
    @GetMapping(PREFIX + "/removeAdminLockStatus")
    void removeAdminLockStatus(@RequestParam("siteCode") String siteCode,
                               @RequestParam("userAccount") String userAccount);

    @Operation(summary = "添加一个职员的锁定状态")
    @GetMapping(PREFIX + "/addAdminLockStatus")
    void addAdminLockStatus(@RequestParam("siteCode") String siteCode,
                            @RequestParam("userAccount") String userAccount,
                            @RequestParam("timeStamp") Long timeStamp);

}
