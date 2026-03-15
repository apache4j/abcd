package com.cloud.baowang.account.service.account;

import com.cloud.baowang.account.api.enums.AccountBusinessCoinTypeEnums;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.po.AccountCoinPO;
import com.cloud.baowang.account.po.AccountCoinRecordPO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author mufan
 * @Desc 转账相关
 */
public interface AccountCoinRecordService {

    void batchInsertAccountCoinRecord(AccountCoinPO accountCoinFrom, AccountCoinPO accountCoinTo, AccountBusinessUserReqVO vo, AccountCoinTypeEnums accountCoinTypeEnums, AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums,String venueCode);
    Long queryOrderByCoinTypeAndBussinessType(AccountCoinTypeEnums accountCoinTypeEnums, AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums,String innerOrderId,String thirdOrderNo);
    List<AccountCoinRecordPO> queryOrderByCoinTypeAndBussinessTypeList(AccountCoinTypeEnums accountCoinTypeEnums,AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums,List<String> innerOrderId,List<String> thirdOrderNo,String accountNo);
    AccountCoinRecordPO initAccountCoinRecordPO(AccountCoinPO accountCoin, String balanceType, String innerOrderNo, String thirdOrderNo, BigDecimal coinValue, Long coinTime, AccountCoinTypeEnums accountCoinTypeEnums, AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums, String venueCode);

    void batchSaveAccountCoinRecord( List<AccountCoinRecordPO> pos);
}
