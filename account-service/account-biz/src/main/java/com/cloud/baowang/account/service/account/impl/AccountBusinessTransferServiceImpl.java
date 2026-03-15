package com.cloud.baowang.account.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.account.po.AccountBusinessTransferPO;
import com.cloud.baowang.account.repositories.AccountBusinessTransferRepository;
import com.cloud.baowang.account.service.account.AccountBusinessTransferService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AccountBusinessTransferServiceImpl  extends ServiceImpl<AccountBusinessTransferRepository, AccountBusinessTransferPO> implements AccountBusinessTransferService {

    @Override
    public AccountBusinessTransferPO queryData(String businessType, String coinType) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<AccountBusinessTransferPO>()
                .eq(AccountBusinessTransferPO::getBusinessCoinType,businessType)
                .eq(AccountBusinessTransferPO::getCoinType,coinType));
    }
}
