package com.cloud.baowang.wallet.repositories.bank;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.bank.BankCardManagerPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BankCardManagerRepository extends BaseMapper<BankCardManagerPO> {
    void updateChannelBankRelationStatus(@Param("currency") String currency);
}
