package com.cloud.baowang.account.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.account.api.enums.AccountCategoryEnums;
import com.cloud.baowang.account.api.enums.AccountTypeEnums;
import com.cloud.baowang.account.api.enums.BalanceTypeEnums;
import com.cloud.baowang.account.api.enums.SourceAccountTypeEnums;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinQueryVO;
import com.cloud.baowang.account.po.AccountCoinPO;
import com.cloud.baowang.account.repositories.AccountCoinRepository;
import com.cloud.baowang.account.service.account.AccountCoinService;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AccountCoinServiceImpl extends ServiceImpl<AccountCoinRepository, AccountCoinPO>  implements AccountCoinService {
    private final static int MaxLenght= 10;
    /**
     * Lambda 方式的 for update
     */
    public AccountCoinPO selectOrderForUpdateLambda(AccountCoinQueryVO accountCoinQueryVO) {
        return this.lambdaQuery()
                .eq(AccountCoinPO::getAccountName, accountCoinQueryVO.getAccountName())
                .eq(AccountCoinPO::getSourceAccountType,accountCoinQueryVO.getSourceAccountType())
                .eq(AccountCoinPO::getSourceAccountNo,accountCoinQueryVO.getSourceAccountNo())
                .eq(AccountCoinPO::getAccountCategory,accountCoinQueryVO.getAccountCategory())
                .eq(AccountCoinPO::getCurrencyCode,accountCoinQueryVO.getCurrencyCode())
                .eq(AccountCoinPO::getSiteCode,accountCoinQueryVO.getSiteCode())
                .last("FOR UPDATE")
                .one();
    }

    public BigDecimal selectAccountFlowAmount(String siteCode ,String userAccount) {
        QueryWrapper<AccountCoinPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("SUM(balance_amount) as balanceAmount");
        queryWrapper.and(wrapper -> wrapper
                .eq("site_code", siteCode)
                .eq("account_name", userAccount)
                .eq("account_category", AccountCategoryEnums.VENUE.getCode())
                .eq("source_account_type", SourceAccountTypeEnums.THIRDVENUE.getType())
                .or(orWrapper -> orWrapper
                        .eq("site_code", siteCode)
                        .eq("account_name", userAccount)
                        .eq("account_category", AccountCategoryEnums.CASH.getCode())
                        .eq("source_account_type", SourceAccountTypeEnums.MEMBER.getType())
                )
        );

        List<Map<String, Object>> result = getBaseMapper().selectMaps(queryWrapper);
        BigDecimal total = result.stream()
                .findFirst()
                .map(map -> map.get("balanceAmount") != null ?
                        new BigDecimal(map.get("balanceAmount").toString()) : BigDecimal.ZERO)
                .orElse(BigDecimal.ZERO);
        return total;
    }


    @Override
    public AccountCoinPO addAccountCoin(AccountCoinQueryVO accountCoinQuery, AccountBusinessUserReqVO vo) {
        Long createtime=System.currentTimeMillis();
        AccountCoinPO po =new AccountCoinPO();
        po.setAccountNo(SnowFlakeUtils.getCommonRandomIdByLenght(MaxLenght));
        po.setAccountName(accountCoinQuery.getAccountName());
        po.setSourceAccountType(accountCoinQuery.getSourceAccountType());
        po.setSourceAccountNo(accountCoinQuery.getSourceAccountNo());
        po.setAccountCategory(accountCoinQuery.getAccountCategory());
        po.setCurrencyCode(accountCoinQuery.getCurrencyCode());
        SourceAccountTypeEnums enums=SourceAccountTypeEnums.of(accountCoinQuery.getSourceAccountType());
        AccountCategoryEnums accountCategoryEnums=AccountCategoryEnums.of(accountCoinQuery.getAccountCategory());
        if (accountCategoryEnums !=null){
            po.setAccountType(accountCategoryEnums.getAccountTypeEnums().getType());
        }
        switch (enums) {
            case MEMBER, AGENT:
                po.setBalanceAmount(vo.getWalletAmount());
                break;
            case PLATFORM:
                po.setBalanceAmount(BigDecimal.ZERO);
                po.setAccountType(AccountTypeEnums.CREDIT.getType());
                break;
            case THIRDPAY:
                po.setBalanceAmount(BigDecimal.ZERO);
                po.setAccountType(AccountTypeEnums.CREDIT.getType());
                break;
            case THIRDVENUE:
                po.setSourceAccountNo(vo.getToThridCode());
                po.setBalanceAmount(BigDecimal.ZERO);
                break;
        }

        if (SourceAccountTypeEnums.MEMBER.equals(enums) &&( AccountCategoryEnums.CREDIT.equals(accountCategoryEnums)
                || AccountCategoryEnums.FREEZE.equals(accountCategoryEnums))){
            po.setBalanceAmount(BigDecimal.ZERO);
        }
        po.setSiteCode(accountCoinQuery.getSiteCode());
        po.setAccountStatus(vo.getAccountStatus());
        po.setCreatedTime(createtime);
        po.setUpdatedTime(createtime);
        this.save(po);
        return po;
    }
    @Override
    public void updateAccount( AccountCoinPO accountCoinFrom, AccountCoinPO accountCoinTo,AccountBusinessUserReqVO vo){
         Long time= System.currentTimeMillis();
        LambdaUpdateWrapper<AccountCoinPO> updateFrom=new LambdaUpdateWrapper<>();
        BigDecimal amountFrom=null;
        if (BalanceTypeEnums.EXPENSES.getType().equals(vo.getBalanceType())){
            amountFrom = accountCoinFrom.getBalanceAmount().subtract(vo.getCoinValue());
        }else{
            amountFrom = accountCoinFrom.getBalanceAmount().add(vo.getCoinValue());
        }
        updateFrom.set(AccountCoinPO::getBalanceAmount,amountFrom);
        updateFrom.set(AccountCoinPO::getUpdatedTime,time);
        updateFrom.eq(AccountCoinPO::getAccountNo, accountCoinFrom.getAccountNo());


        AccountTypeEnums accountTypeEnumsFrom= AccountTypeEnums.of(accountCoinFrom.getAccountType());

        if (AccountTypeEnums.DEBIT.getType().equals(accountTypeEnumsFrom.getType()) && isLessThanZero(amountFrom)){
            throw new BaowangDefaultException("交易金额不能大于用户余额");
        }
        this.baseMapper.update(null,updateFrom);

        if (accountCoinTo!=null){
            AccountTypeEnums accountTypeEnumsTo= AccountTypeEnums.of(accountCoinTo.getAccountType());
            LambdaUpdateWrapper<AccountCoinPO> updateTo=new LambdaUpdateWrapper<>();
            BigDecimal amountTO=null;
            if (BalanceTypeEnums.EXPENSES.getType().equals(vo.getBalanceType())){
                amountTO=accountCoinTo.getBalanceAmount().add(vo.getCoinValue());
            }else{
                amountTO=accountCoinTo.getBalanceAmount().subtract(vo.getCoinValue());
            }
            updateTo.set(AccountCoinPO::getBalanceAmount,amountTO );
            updateTo.set(AccountCoinPO::getUpdatedTime,time);
            updateTo.eq(AccountCoinPO::getAccountNo, accountCoinTo.getAccountNo());
            if (AccountTypeEnums.DEBIT.getType().equals(accountTypeEnumsTo.getType()) && isLessThanZero(amountTO) ){
                throw new BaowangDefaultException("交易金额不能大于用户余额");
            }
            this.baseMapper.update(null,updateTo);
        }
    }

    @Override
    public void batchUpdateAccountCoin(List<AccountCoinPO> data) {
          this.saveOrUpdateBatch(data);
    }


    /**
     * 判断是否小于0
     */
    public static boolean isLessThanZero(BigDecimal value) {
        if (value == null) {
            return false; // 或者根据业务需求抛出异常
        }
        return value.compareTo(BigDecimal.ZERO) < 0;
    }
}
