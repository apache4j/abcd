package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.UserCoinPO;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author qiqi
 */
@Mapper
public interface UserCoinRepository extends BaseMapper<UserCoinPO> {

}
