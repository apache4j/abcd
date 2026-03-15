package com.cloud.baowang.account.service.plat;


import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.account.po.UserPlatformCoinPO;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserPlatformCoinApi {

    /**
     * 平台币余额账变
     * @param accountUserPlatformCoinAddReqVO
     * @return
     */
    AccountCoinResultVO platformCoinAdd(AccountUserPlatformCoinAddReqVO accountUserPlatformCoinAddReqVO,UserPlatformCoinPO userPlatformCoinPO) ;


    /**
     * 获取会员平台币余额
     * @param userId
     * @return
     */
    UserPlatformCoinPO getPlatformCoinByUserId(String userId) ;



}
