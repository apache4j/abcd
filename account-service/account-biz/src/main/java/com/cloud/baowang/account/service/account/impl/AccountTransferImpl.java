package com.cloud.baowang.account.service.account.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.account.api.enums.*;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinQueryVO;
import com.cloud.baowang.account.po.AccountBusinessTransferPO;
import com.cloud.baowang.account.po.AccountCoinPO;
import com.cloud.baowang.account.po.AccountCoinRecordPO;
import com.cloud.baowang.account.service.account.AccountBusinessTransferService;
import com.cloud.baowang.account.service.account.AccountCoinRecordService;
import com.cloud.baowang.account.service.account.AccountCoinService;
import com.cloud.baowang.account.service.account.AccountTransfer;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.kafka.vo.AccountRequestMqVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AccountTransferImpl implements AccountTransfer {

    private final AccountBusinessTransferService accountBusinessTransferService;
    private final AccountCoinService accountCoinService;
    private final AccountCoinRecordService accountCoinRecordService;

    @Override
    public void singleTransfer(AccountBusinessUserReqVO vo) {
        log.info("singleTransfer入参,{}",vo);
        //根据原有业务张遍类型转换成当前业务和帐变类型
        AccountTransferEnums accountTransferEnums=null;
        if (Objects.isNull(vo.getBussinessFlag())){
            accountTransferEnums=AccountTransferEnums.of(vo.getCode(),vo.getUserType());
        }else{
            accountTransferEnums=AccountTransferEnums.of(vo.getCode(),vo.getUserType(),vo.getBussinessFlag());
            if (Objects.isNull(accountTransferEnums)){
                accountTransferEnums=AccountTransferEnums.of(vo.getCode(),vo.getUserType());
            }
        }
        if (Objects.isNull(accountTransferEnums)){
            log.info("业务类型匹配错误入参,{}",vo);
            throw new BaowangDefaultException(ResultCode.ADJUST_TYPE_IS_ERROR);
        }
        //获取账目系统对应的帐变类型
        AccountCoinTypeEnums accountCoinTypeEnums=accountTransferEnums.getAccountCoinTypeEnums();
        //获取账目系统对应的业务类型
        AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums=accountCoinTypeEnums.getAccountBusinessCoinTypeEnums();
        //查询订单是否被处理过
        Long count=accountCoinRecordService.queryOrderByCoinTypeAndBussinessType(accountCoinTypeEnums,accountBusinessCoinTypeEnums,vo.getInnerOrderNo(),vo.getThirdOrderNo());
        if (count >0){
            throw new BaowangDefaultException("交易重复");
        }
        //根据账目系统的业务类型和帐变类型查询对应的业务扭转所需要的对应钱包
        AccountBusinessTransferPO accountBusinessTransferPO=accountBusinessTransferService.queryData(accountBusinessCoinTypeEnums.getCode(),accountCoinTypeEnums.getCode());
        AccountCoinQueryVO accountCoinQueryFrom= getAccountCoinQueryData(accountBusinessTransferPO.getSourceAccountTypeFrom(),accountBusinessTransferPO.getWalletTypeFrom(),vo);
        //查询FROM TO 两个账户
        AccountCoinPO accountCoinFrom =accountCoinService.selectOrderForUpdateLambda(accountCoinQueryFrom);
        if (Objects.isNull(accountCoinFrom)){
            accountCoinFrom= accountCoinService.addAccountCoin(accountCoinQueryFrom,vo);
        }
        AccountCoinPO accountCoinTo=null;
        if (Objects.nonNull(accountBusinessTransferPO.getSourceAccountTypeTo())&& Objects.nonNull(accountBusinessTransferPO.getWalletTypeTo())){
            AccountCoinQueryVO accountCoinQueryTo=  getAccountCoinQueryData(accountBusinessTransferPO.getSourceAccountTypeTo(),accountBusinessTransferPO.getWalletTypeTo(),vo);
             accountCoinTo =accountCoinService.selectOrderForUpdateLambda(accountCoinQueryTo);
            //账户是否存在 不存在则新增账户,
            if (Objects.isNull(accountCoinTo)){
                accountCoinTo=  accountCoinService.addAccountCoin(accountCoinQueryTo,vo);
            }
        }
        //更新2个账户
        accountCoinService.updateAccount(accountCoinFrom,accountCoinTo,vo);
        //生成帐变信息
        accountCoinRecordService.batchInsertAccountCoinRecord(accountCoinFrom,accountCoinTo,vo, accountCoinTypeEnums, accountBusinessCoinTypeEnums,null);
    }

    @Override
    public void gameTransfer(AccountBusinessUserReqVO vo) {
        log.info("gameTransfer,{}",vo);
        AccountCoinTypeEnums accountCoinTypeEnums= AccountCoinTypeEnums.of(vo.getCode());
        if (Objects.isNull(accountCoinTypeEnums)){
            log.info("业务类型匹配错误入参,{}",vo);
            throw new BaowangDefaultException(ResultCode.ADJUST_TYPE_IS_ERROR);
        }
        //查询订单是否被处理过
        Long count=accountCoinRecordService.queryOrderByCoinTypeAndBussinessType(accountCoinTypeEnums,accountCoinTypeEnums.getAccountBusinessCoinTypeEnums(),vo.getInnerOrderNo(),vo.getThirdOrderNo());
        if (count >0){
            throw new BaowangDefaultException("交易重复");
        }
        switch (accountCoinTypeEnums) {
            case GAME_TRANSFER_IN,GAME_BET_CONFIRM,GAME_BET_FREEZD,GAME_BET,GAME_CANCEL_BET,GAME_RETURN_BET,GAME_TIPS -> accountBusinessTransferData(vo,accountCoinTypeEnums);
            case GAME_TRANSFER_OUT,GAME_PAYOUT -> payOutTransferData(vo,accountCoinTypeEnums);
            case GAME_RECALCULATE_PAYOUT,GAME_CANCEL_PAYOUT -> recalculatePayoutTransferData(vo,accountCoinTypeEnums);
        }
    }

    private void accountBusinessTransferData(AccountBusinessUserReqVO vo,AccountCoinTypeEnums accountCoinTypeEnums){
        log.info("accountBusinessTransferData:{},accountCoinTypeEnums:{}",vo,accountCoinTypeEnums);
        if (Objects.equals(accountCoinTypeEnums,AccountCoinTypeEnums.GAME_BET_CONFIRM) ||Objects.equals(accountCoinTypeEnums,AccountCoinTypeEnums.GAME_BET_FREEZD)){
            vo.setBalanceType(BalanceTypeEnums.EXPENSES.getType());
        }
        if (Objects.equals(accountCoinTypeEnums,AccountCoinTypeEnums.GAME_BET)){
            vo.setThirdOrderNo(vo.getInnerOrderNo());
        }
        AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums=accountCoinTypeEnums.getAccountBusinessCoinTypeEnums();
        AccountBusinessTransferPO accountBusinessTransferPO=accountBusinessTransferService.queryData(accountBusinessCoinTypeEnums.getCode(),accountCoinTypeEnums.getCode());
        AccountCoinQueryVO accountCoinQueryFrom= getAccountCoinQueryData(accountBusinessTransferPO.getSourceAccountTypeFrom(),accountBusinessTransferPO.getWalletTypeFrom(),vo);
        //查询FROM TO 两个账户
        AccountCoinPO accountCoinFrom =accountCoinService.selectOrderForUpdateLambda(accountCoinQueryFrom);
        if (Objects.isNull(accountCoinFrom)){
            accountCoinFrom= accountCoinService.addAccountCoin(accountCoinQueryFrom,vo);
        }
        AccountCoinQueryVO accountCoinQueryTo=  getAccountCoinQueryData(accountBusinessTransferPO.getSourceAccountTypeTo(),accountBusinessTransferPO.getWalletTypeTo(),vo);
        AccountCoinPO accountCoinTo = accountCoinService.selectOrderForUpdateLambda(accountCoinQueryTo);
        //账户是否存在 不存在则新增账户,
        if (Objects.isNull(accountCoinTo)){
            accountCoinTo=  accountCoinService.addAccountCoin(accountCoinQueryTo,vo);
        }
        //更新2个账户
        accountCoinService.updateAccount(accountCoinFrom,accountCoinTo,vo);
        //只有冻结确认的时候才把类型类型转换成投注
        if (Objects.equals(accountCoinTypeEnums,AccountCoinTypeEnums.GAME_BET_CONFIRM)){
            accountCoinTypeEnums =AccountCoinTypeEnums.GAME_BET;
            accountBusinessCoinTypeEnums =accountCoinTypeEnums.getAccountBusinessCoinTypeEnums();
            vo.setThirdOrderNo(vo.getInnerOrderNo());
        }
        //生成帐变信息
        accountCoinRecordService.batchInsertAccountCoinRecord(accountCoinFrom,accountCoinTo,vo, accountCoinTypeEnums, accountBusinessCoinTypeEnums,vo.getToThridCode());
    }

    //派彩和转出功能用户都是收钱的
    private void payOutTransferData(AccountBusinessUserReqVO vo,AccountCoinTypeEnums accountCoinTypeEnums){
        log.info("payOutTransferData:{},accountCoinTypeEnums:{}",vo,accountCoinTypeEnums);
        List<AccountCoinRecordPO> pos=new ArrayList<>();
        List<AccountCoinPO> data=new ArrayList<>();
        //派彩金额大于0的时候才进行会员现金账户收入 三方盈亏账户支出功能
        AccountCoinQueryVO thirdWinLoss = getAccountCoinQueryData(SourceAccountTypeEnums.THIRDVENUE.getType(),AccountCategoryEnums.WINLOSS.getCode(),vo);
        AccountCoinPO thirdWinLossAccount =null;
        if(vo.getCoinValue().compareTo(BigDecimal.ZERO) >0){
            thirdWinLossAccount =accountCoinService.selectOrderForUpdateLambda(thirdWinLoss);
            if (Objects.isNull(thirdWinLossAccount)){
                thirdWinLossAccount= accountCoinService.addAccountCoin(thirdWinLoss,vo);
            }
            AccountCoinQueryVO accountCash= getAccountCoinQueryData(SourceAccountTypeEnums.MEMBER.getType(),AccountCategoryEnums.CASH.getCode(),vo);
            AccountCoinPO accountCashAccount =accountCoinService.selectOrderForUpdateLambda(accountCash);
            AccountBusinessCoinTypeEnums accountBusinessCoinTypeEnums= accountCoinTypeEnums.getAccountBusinessCoinTypeEnums();
            String balanceType=BalanceTypeEnums.EXPENSES.getType();
            if (BalanceTypeEnums.EXPENSES.getType().equals(vo.getBalanceType())){
                balanceType =BalanceTypeEnums.INCOME.getType();
            }
            pos.add(accountCoinRecordService.initAccountCoinRecordPO(thirdWinLossAccount,balanceType,vo.getInnerOrderNo(),vo.getThirdOrderNo(),vo.getCoinValue(),vo.getCoinTime(),accountCoinTypeEnums,accountBusinessCoinTypeEnums,vo.getToThridCode()));
            pos.add(accountCoinRecordService.initAccountCoinRecordPO(accountCashAccount,vo.getBalanceType(),vo.getInnerOrderNo(),vo.getThirdOrderNo(),vo.getCoinValue(),vo.getCoinTime(),accountCoinTypeEnums,accountBusinessCoinTypeEnums,vo.getToThridCode()));
            updateBlance(thirdWinLossAccount,balanceType,vo.getCoinValue());
            updateBlance(accountCashAccount,vo.getBalanceType(),vo.getCoinValue());
            data.add(accountCashAccount);
        }
        // 处理内部转账三方场馆账户支出 三方盈亏账户转入
        //查询订单是否被处理过
        Long count=accountCoinRecordService.queryOrderByCoinTypeAndBussinessType(AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER,AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER.getAccountBusinessCoinTypeEnums(),vo.getInnerOrderNo(),vo.getInnerOrderNo());
        if (count ==0){
            AccountCoinQueryVO thirdVenue= getAccountCoinQueryData(SourceAccountTypeEnums.THIRDVENUE.getType(),AccountCategoryEnums.VENUE.getCode(),vo);
            AccountCoinPO thirdVenueAccount =accountCoinService.selectOrderForUpdateLambda(thirdVenue);
            List<AccountCoinRecordPO> transferPo =  accountCoinRecordService.queryOrderByCoinTypeAndBussinessTypeList(AccountCoinTypeEnums.GAME_BET,AccountCoinTypeEnums.GAME_BET.getAccountBusinessCoinTypeEnums(),List.of(vo.getInnerOrderNo()),List.of(vo.getInnerOrderNo()),thirdVenueAccount.getAccountNo());
            if (CollectionUtil.isNotEmpty(transferPo)) {
                if (Objects.isNull(thirdWinLossAccount)){
                    thirdWinLossAccount =accountCoinService.selectOrderForUpdateLambda(thirdWinLoss);
                    if (Objects.isNull(thirdWinLossAccount)){
                        thirdWinLossAccount= accountCoinService.addAccountCoin(thirdWinLoss,vo);
                    }
                }
                BigDecimal coinValue =   transferPo.get(0).getCoinValue();
                String balanceType=BalanceTypeEnums.EXPENSES.getType();
                if (BalanceTypeEnums.EXPENSES.getType().equals(vo.getBalanceType())){
                    balanceType =BalanceTypeEnums.INCOME.getType();
                }
                pos.add(accountCoinRecordService.initAccountCoinRecordPO(thirdVenueAccount,balanceType,vo.getInnerOrderNo(),vo.getInnerOrderNo(),coinValue,vo.getCoinTime(),AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER,AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER.getAccountBusinessCoinTypeEnums(),vo.getToThridCode()));
                pos.add(accountCoinRecordService.initAccountCoinRecordPO(thirdWinLossAccount,vo.getBalanceType(),vo.getInnerOrderNo(),vo.getInnerOrderNo(),coinValue,vo.getCoinTime(),AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER,AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER.getAccountBusinessCoinTypeEnums(),vo.getToThridCode()));
                updateBlance(thirdVenueAccount,balanceType,coinValue);
                updateBlance(thirdWinLossAccount,vo.getBalanceType(),coinValue);
                data.add(thirdVenueAccount);
            }
        }
        if (Objects.nonNull(thirdWinLossAccount)){
            data.add(thirdWinLossAccount);
        }
        accountCoinService.batchUpdateAccountCoin(data);
        accountCoinRecordService.batchSaveAccountCoinRecord(pos);
    }

    private void updateBlance( AccountCoinPO accountCashAccount,String balanceType,BigDecimal coinValue){
        if (BalanceTypeEnums.EXPENSES.getType().equals(balanceType)){
            accountCashAccount.setBalanceAmount(accountCashAccount.getBalanceAmount().subtract(coinValue));
        }else{
            accountCashAccount.setBalanceAmount(accountCashAccount.getBalanceAmount().add(coinValue));
        }
    }

    private void recalculatePayoutTransferData(AccountBusinessUserReqVO vo,AccountCoinTypeEnums accountCoinTypeEnums){
        log.info("recalculatePayoutTransferData:{},accountCoinTypeEnums:{}",vo,accountCoinTypeEnums);
        List<AccountCoinRecordPO> pos=new ArrayList<>();
        List<AccountCoinPO> data=new ArrayList<>();
        AccountCoinQueryVO userCash = getAccountCoinQueryData(SourceAccountTypeEnums.MEMBER.getType(),AccountCategoryEnums.CASH.getCode(),vo);
        AccountCoinPO userCashAccount =accountCoinService.selectOrderForUpdateLambda(userCash);
        if (Objects.isNull(userCashAccount)){
            userCashAccount= accountCoinService.addAccountCoin(userCash,vo);
        }
        AccountCoinQueryVO gameWinLoss = getAccountCoinQueryData(SourceAccountTypeEnums.THIRDVENUE.getType(),AccountCategoryEnums.WINLOSS.getCode(),vo);
        AccountCoinPO gameWinLossAccount =accountCoinService.selectOrderForUpdateLambda(gameWinLoss);
        if (Objects.isNull(gameWinLossAccount)){
            gameWinLossAccount= accountCoinService.addAccountCoin(gameWinLoss,vo);
        }
        BigDecimal coinValue=vo.getCoinValue();
        String balanceType=BalanceTypeEnums.EXPENSES.getType();
        if (BalanceTypeEnums.EXPENSES.getType().equals(vo.getBalanceType())){
            balanceType=BalanceTypeEnums.INCOME.getType();
            //如果是支出判断支出金额是否大于当前用户现金
            //重结算的时候可能会导致会员现金账户不足的情况，-扣除用户现金，然后扣除用户透支账户
            if (vo.getCoinValue().compareTo(userCashAccount.getBalanceAmount())>0){
                AccountCoinQueryVO userCredit = getAccountCoinQueryData(SourceAccountTypeEnums.MEMBER.getType(),AccountCategoryEnums.CREDIT.getCode(),vo);
                AccountCoinPO userCreditAccount =accountCoinService.selectOrderForUpdateLambda(userCredit);
                if (Objects.isNull(userCreditAccount)){
                    userCreditAccount= accountCoinService.addAccountCoin(userCredit,vo);
                }
                BigDecimal userCreditAccountSubtract= vo.getCoinValue().subtract(userCashAccount.getBalanceAmount());
                pos.add(accountCoinRecordService.initAccountCoinRecordPO(userCreditAccount,vo.getBalanceType(),vo.getInnerOrderNo(),vo.getInnerOrderNo(),userCreditAccountSubtract,vo.getCoinTime(),accountCoinTypeEnums,accountCoinTypeEnums.getAccountBusinessCoinTypeEnums(),vo.getToThridCode()));
                updateBlance(userCreditAccount,vo.getBalanceType(),userCreditAccountSubtract);
                data.add(userCreditAccount);
                coinValue=userCashAccount.getBalanceAmount();
            }
        }
        pos.add(accountCoinRecordService.initAccountCoinRecordPO(userCashAccount,vo.getBalanceType(),vo.getInnerOrderNo(),vo.getInnerOrderNo(),coinValue,vo.getCoinTime(),accountCoinTypeEnums,accountCoinTypeEnums.getAccountBusinessCoinTypeEnums(),vo.getToThridCode()));
        updateBlance(userCashAccount,vo.getBalanceType(),coinValue);
        data.add(userCashAccount);
        pos.add(accountCoinRecordService.initAccountCoinRecordPO(gameWinLossAccount,balanceType,vo.getInnerOrderNo(),vo.getInnerOrderNo(),vo.getCoinValue(),vo.getCoinTime(),accountCoinTypeEnums,accountCoinTypeEnums.getAccountBusinessCoinTypeEnums(),vo.getToThridCode()));
        updateBlance(gameWinLossAccount,balanceType,vo.getCoinValue());
        data.add(gameWinLossAccount);
        accountCoinService.batchUpdateAccountCoin(data);
        accountCoinRecordService.batchSaveAccountCoinRecord(pos);
    }

    private AccountCoinQueryVO getAccountCoinQueryData(String accountTypeFrom, String walletType, AccountBusinessUserReqVO vo){
        AccountCoinQueryVO data=new AccountCoinQueryVO();
        data.setSourceAccountType(accountTypeFrom);
        data.setAccountCategory(walletType);
        SourceAccountTypeEnums enums= SourceAccountTypeEnums.of(accountTypeFrom);
        switch (enums) {
            case MEMBER, AGENT:
                data.setAccountName(vo.getAccountName());
                data.setSourceAccountNo(vo.getSourceAccountNo());
                data.setCurrencyCode(vo.getCurrencyCode());
                data.setSiteCode(vo.getSiteCode());
                break;
            case PLATFORM,THIRDPAY:
                data.setAccountName(vo.getToThridCode());
                data.setSourceAccountNo(vo.getToThridCode());
                data.setCurrencyCode(vo.getCurrencyCode());
                data.setSiteCode(vo.getSiteCode());
                break;
            case THIRDVENUE:
                data.setAccountName(vo.getAccountName());
                data.setSourceAccountNo(vo.getToThridCode());
                data.setCurrencyCode(vo.getCurrencyCode());
                data.setSiteCode(vo.getSiteCode());
                break;
        }
        return data;
    }


    @Transactional(rollbackFor = Exception.class)
    public void batchCleanAccountCoin(List<AccountRequestMqVO> data){
        log.error("批量清理用户场馆账户余额投注注单内容:{}",data);
        //根据场馆分租
        Map<String, List<AccountRequestMqVO>> flowMap = data.stream()
                .collect(Collectors.groupingBy(AccountRequestMqVO::getVenueCode));
        for (Map.Entry<String, List<AccountRequestMqVO>> map : flowMap.entrySet()) {
            List<AccountCoinRecordPO> pos=new ArrayList<>();
            List<AccountCoinPO> accountCoinPOS=new ArrayList<>();
            List<AccountRequestMqVO> accountVo=map.getValue();
            AccountRequestMqVO accountRequestMqVO= accountVo.get(0);
            AccountBusinessUserReqVO vo=new AccountBusinessUserReqVO();
            vo.setAccountName(accountRequestMqVO.getUserAccount());
            vo.setSourceAccountNo(accountRequestMqVO.getUserId());
            vo.setCurrencyCode(accountRequestMqVO.getCurrencyCode());
            vo.setSiteCode(accountRequestMqVO.getSiteCode());
            vo.setToThridCode(accountRequestMqVO.getVenueCode());
            vo.setCoinTime(System.currentTimeMillis());
            List<String> orderIds = accountVo.stream().distinct()
                    .map(AccountRequestMqVO::getOrderId)
                    .collect(Collectors.toList());

            AccountCoinQueryVO thirdVenue= getAccountCoinQueryData(SourceAccountTypeEnums.THIRDVENUE.getType(),AccountCategoryEnums.VENUE.getCode(),vo);
            AccountCoinPO thirdVenueAccount =accountCoinService.selectOrderForUpdateLambda(thirdVenue);
            //查询当前用户场馆账户投注列表
            List<AccountCoinRecordPO>  betAmount= accountCoinRecordService.queryOrderByCoinTypeAndBussinessTypeList(AccountCoinTypeEnums.GAME_BET,AccountCoinTypeEnums.GAME_BET.getAccountBusinessCoinTypeEnums(),orderIds,null,thirdVenueAccount.getAccountNo());
            Map<String, AccountCoinRecordPO> betAmountMap = betAmount.stream()
                    .collect(Collectors.toMap(
                            AccountCoinRecordPO::getInnerOrderNo,
                            record -> record
                    ));
            //查询当前用户场馆账户内部划转是否列表
            List<AccountCoinRecordPO>  cleanAccountCoinRecordPO = accountCoinRecordService.queryOrderByCoinTypeAndBussinessTypeList(AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER,AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER.getAccountBusinessCoinTypeEnums(),orderIds,new ArrayList<>(),thirdVenueAccount.getAccountNo());
            Map<String, AccountCoinRecordPO> cleanAccountMap = cleanAccountCoinRecordPO.stream()
                    .collect(Collectors.toMap(
                            AccountCoinRecordPO::getInnerOrderNo,
                            record -> record
                    ));
            AccountCoinQueryVO thirdWinLoss = getAccountCoinQueryData(SourceAccountTypeEnums.THIRDVENUE.getType(),AccountCategoryEnums.WINLOSS.getCode(),vo);
            AccountCoinPO thirdWinLossAccount =accountCoinService.selectOrderForUpdateLambda(thirdWinLoss);
            if (Objects.isNull(thirdWinLossAccount)){
                thirdWinLossAccount= accountCoinService.addAccountCoin(thirdWinLoss,vo);
            }
            AccountCoinPO finalThirdWinLossAccount = thirdWinLossAccount;
            accountVo.forEach(e->{
                //在查询的已清理的没有则说明未处理
                if (Objects.isNull(cleanAccountMap.get(e.getOrderId()))){
                    //查询注单中未找到此订单
                    AccountCoinRecordPO accountCoinRecordPO=  betAmountMap.get(e.getOrderId());
                    if (Objects.nonNull(accountCoinRecordPO)){
                        //清理场馆账户投注金额
                        pos.add(accountCoinRecordService.initAccountCoinRecordPO(thirdVenueAccount,BalanceTypeEnums.EXPENSES.getType(),e.getOrderId(),e.getOrderId(),accountCoinRecordPO.getCoinValue(),vo.getCoinTime(),AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER,AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER.getAccountBusinessCoinTypeEnums(),vo.getToThridCode()));
                        pos.add(accountCoinRecordService.initAccountCoinRecordPO(finalThirdWinLossAccount,BalanceTypeEnums.INCOME.getType(),e.getOrderId(),e.getOrderId(),accountCoinRecordPO.getCoinValue(),vo.getCoinTime(),AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER,AccountCoinTypeEnums.GAME_CLEAN_BET_AMOUNT_TRANSFER.getAccountBusinessCoinTypeEnums(),vo.getToThridCode()));
                        updateBlance(thirdVenueAccount,BalanceTypeEnums.EXPENSES.getType(),accountCoinRecordPO.getCoinValue());
                        updateBlance(finalThirdWinLossAccount,BalanceTypeEnums.INCOME.getType(),accountCoinRecordPO.getCoinValue());
                    }
                }
            });
            accountCoinPOS.add(thirdVenueAccount);
            accountCoinPOS.add(finalThirdWinLossAccount);
            accountCoinService.batchUpdateAccountCoin(accountCoinPOS);
            accountCoinRecordService.batchSaveAccountCoinRecord(pos);
        }
    }
}
