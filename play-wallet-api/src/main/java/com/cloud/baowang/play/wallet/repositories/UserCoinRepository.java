package com.cloud.baowang.play.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.wallet.po.UserCoinPO;
import com.cloud.baowang.play.wallet.po.UserWalletGameRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;


@Mapper
public interface UserCoinRepository extends BaseMapper<UserCoinPO> {

    int updateBalance(@Param("userAccount") String userAccount, @Param("amount") BigDecimal amount);
}
