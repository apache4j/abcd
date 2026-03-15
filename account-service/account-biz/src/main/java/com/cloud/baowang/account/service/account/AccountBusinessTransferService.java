package com.cloud.baowang.account.service.account;

import com.cloud.baowang.account.po.AccountBusinessTransferPO;

public interface AccountBusinessTransferService  {


    AccountBusinessTransferPO queryData(String businessType,String coinType);

}
