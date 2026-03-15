package com.cloud.baowang.account.service.account;

import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinQueryVO;
import com.cloud.baowang.account.po.AccountCoinPO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author mufan
 * @Date 2025-10-11
 * 账户相关
 */
public interface AccountCoinService {
    AccountCoinPO selectOrderForUpdateLambda(AccountCoinQueryVO accountCoinQueryVO);

    AccountCoinPO addAccountCoin(AccountCoinQueryVO accountCoinQuery, AccountBusinessUserReqVO vo);

    void updateAccount( AccountCoinPO accountCoinFrom, AccountCoinPO accountCoinTo,AccountBusinessUserReqVO vo);

    void batchUpdateAccountCoin(List<AccountCoinPO> data);

    BigDecimal selectAccountFlowAmount(String siteCode, String userAccount);
}
