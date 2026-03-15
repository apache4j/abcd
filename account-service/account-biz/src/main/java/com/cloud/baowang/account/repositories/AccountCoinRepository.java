package com.cloud.baowang.account.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.account.po.AccountCoinPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountCoinRepository extends BaseMapper<AccountCoinPO> {
}
