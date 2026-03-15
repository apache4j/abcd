package com.cloud.baowang.account.service.plat;

import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.po.UserCoinPO;
import com.cloud.baowang.account.po.UserCoinRecordPO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员帐变相关
 * @Author ford
 * @Date 2025-10-14
 */
public interface UserCoinApi {


    /**
     * 会员账变
     * @param accountUserCoinAddReqVO
     * @return
     */
    AccountCoinResultVO userCoinAdd(AccountUserCoinAddReqVO accountUserCoinAddReqVO,UserCoinPO userCoinPO );

    /**
     * 会员钱包信息
     * @param userId
     * @return
     */
    UserCoinPO getUserCoin(String userId);


    List<UserCoinRecordPO> getCoinRecordList(String innerOrderNo, String balanceType, String coinType);
}
