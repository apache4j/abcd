package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.UserActivityTypingAmountPO;
import com.cloud.baowang.wallet.po.UserActivityTypingAmountRecordPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserActivityTypingAmountRepository extends BaseMapper<UserActivityTypingAmountPO> {

}
