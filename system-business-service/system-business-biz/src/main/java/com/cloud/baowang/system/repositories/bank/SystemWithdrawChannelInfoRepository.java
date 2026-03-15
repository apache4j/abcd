package com.cloud.baowang.system.repositories.bank;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.bank.SystemChannelBankRelationBasePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemWithdrawChannelInfoRepository extends BaseMapper<SystemChannelBankRelationBasePO> {
}
