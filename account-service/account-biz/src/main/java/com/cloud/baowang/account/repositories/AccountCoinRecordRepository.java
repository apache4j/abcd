package com.cloud.baowang.account.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.account.po.AccountCoinRecordPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountCoinRecordRepository extends BaseMapper<AccountCoinRecordPO> {
}
