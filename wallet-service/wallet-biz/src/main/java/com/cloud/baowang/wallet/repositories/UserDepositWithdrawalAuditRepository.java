package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalAuditPO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qiqi
 */
@Mapper
public interface UserDepositWithdrawalAuditRepository extends BaseMapper<UserDepositWithdrawalAuditPO> {

}
