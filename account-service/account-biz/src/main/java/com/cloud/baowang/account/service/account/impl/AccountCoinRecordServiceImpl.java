package com.cloud.baowang.account.service.account.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.account.api.enums.AccountBusinessCoinTypeEnums;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.account.api.enums.BalanceTypeEnums;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.po.AccountCoinPO;
import com.cloud.baowang.account.po.AccountCoinRecordPO;
import com.cloud.baowang.account.repositories.AccountCoinRecordRepository;
import com.cloud.baowang.account.service.account.AccountCoinRecordService;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class AccountCoinRecordServiceImpl extends ServiceImpl<AccountCoinRecordRepository, AccountCoinRecordPO> implements AccountCoinRecordService {

    @Override
    public void batchInsertAccountCoinRecord(AccountCoinPO accountCoinFrom, AccountCoinPO accountCoinTo, AccountBusinessUserReqVO vo, AccountCoinTypeEnums accountCoinTypeEnums, AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums,String venueCode) {
        String fromBalanceType=null;
        String toBalanceType=null;
        if (BalanceTypeEnums.EXPENSES.getType().equals(vo.getBalanceType())){
            fromBalanceType=BalanceTypeEnums.EXPENSES.getType();
            toBalanceType=BalanceTypeEnums.INCOME.getType();
        }else{
            fromBalanceType=BalanceTypeEnums.INCOME.getType();
            toBalanceType=BalanceTypeEnums.EXPENSES.getType();
        }
        List<AccountCoinRecordPO> pos=new ArrayList<>();
        pos.add(initAccountCoinRecordPO(accountCoinFrom,fromBalanceType,vo.getInnerOrderNo(),vo.getThirdOrderNo(),vo.getCoinValue(),vo.getCoinTime(),accountCoinTypeEnums,accountBusinessCoinTypeEnums,venueCode));
        if ( accountCoinTo !=null){
            pos.add(initAccountCoinRecordPO(accountCoinTo,toBalanceType,vo.getInnerOrderNo(),vo.getThirdOrderNo(),vo.getCoinValue(),vo.getCoinTime(),accountCoinTypeEnums,accountBusinessCoinTypeEnums,venueCode));
        }
        this.saveBatch(pos);
    }



    @Override
    public void batchSaveAccountCoinRecord( List<AccountCoinRecordPO> pos) {
        this.saveBatch(pos);
    }


    @Override
    public Long queryOrderByCoinTypeAndBussinessType(AccountCoinTypeEnums accountCoinTypeEnums, AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums,String innerOrderId,String thirdOrderNo) {
        LambdaQueryWrapper<AccountCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
        ucrlqw.eq(AccountCoinRecordPO::getBusinessCoinType, accountBusinessCoinTypeEnums.getCode());
        ucrlqw.eq(AccountCoinRecordPO::getCoinType, accountCoinTypeEnums.getCode());
        ucrlqw.eq(AccountCoinRecordPO::getInnerOrderNo, innerOrderId);
        ucrlqw.eq(Objects.nonNull(thirdOrderNo),AccountCoinRecordPO::getThirdOrderNo,thirdOrderNo);
        return this.getBaseMapper().selectCount(ucrlqw);
    }

    @Override
    public List<AccountCoinRecordPO> queryOrderByCoinTypeAndBussinessTypeList(AccountCoinTypeEnums accountCoinTypeEnums, AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums, List<String> innerOrderId, List<String> thirdOrderNo, String accountNo) {
        LambdaQueryWrapper<AccountCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
        ucrlqw.eq(AccountCoinRecordPO::getBusinessCoinType, accountBusinessCoinTypeEnums.getCode());
        ucrlqw.eq(AccountCoinRecordPO::getCoinType, accountCoinTypeEnums.getCode());
        ucrlqw.in(Objects.nonNull(innerOrderId), AccountCoinRecordPO::getInnerOrderNo,innerOrderId);
        ucrlqw.in(CollectionUtil.isNotEmpty(thirdOrderNo),AccountCoinRecordPO::getThirdOrderNo,thirdOrderNo);
        ucrlqw.eq(AccountCoinRecordPO::getAccountNo, accountNo);
        return  this.getBaseMapper().selectList(ucrlqw);
    }

    @Override
    public AccountCoinRecordPO initAccountCoinRecordPO(AccountCoinPO accountCoin, String balanceType, String innerOrderNo, String thirdOrderNo, BigDecimal coinValue,Long coinTime, AccountCoinTypeEnums accountCoinTypeEnums, AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums, String venueCode){
        AccountCoinRecordPO po=new AccountCoinRecordPO();
        po.setAccountNo(accountCoin.getAccountNo());
        po.setOrderNo(SnowFlakeUtils.getSnowIdBySelfCenterId(accountCoin.getSiteCode()));
        po.setInnerOrderNo(innerOrderNo);
        po.setThirdOrderNo(thirdOrderNo);
        po.setCoinValue(coinValue);
        po.setCoinFrom(accountCoin.getBalanceAmount());
        if (BalanceTypeEnums.EXPENSES.getType().equals(balanceType)){
            po.setCoinTo(accountCoin.getBalanceAmount().subtract(coinValue));
        }else{
            po.setCoinTo(accountCoin.getBalanceAmount().add(coinValue));
        }
        po.setCurrencyCode(accountCoin.getCurrencyCode());
        po.setBalanceType(balanceType);
        po.setBusinessCoinType(accountBusinessCoinTypeEnums.getCode());
        po.setCreatedTime(coinTime);
        po.setUpdatedTime(coinTime);
        po.setCoinType(accountCoinTypeEnums.getCode());
        po.setAccountName(accountCoin.getAccountName());
        po.setSourceAccountType(accountCoin.getSourceAccountType());
        po.setSourceAccountNo(accountCoin.getSourceAccountNo());
        po.setAccountCategory(accountCoin.getAccountCategory());
        po.setSiteCode(accountCoin.getSiteCode());
        po.setVenueCode(venueCode);
       return po;
    }
}
