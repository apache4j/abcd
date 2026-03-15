package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.UserBankCardManagePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员银行卡管理 Mapper 接口
 *
 */
@Mapper
public interface UserBankCardManageRepository extends BaseMapper<UserBankCardManagePO> {

}