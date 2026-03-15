package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.po.BetCoinJoinPO;
import com.cloud.baowang.play.po.TransferRecordPO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface BetCoinJoinRepository extends BaseMapper<BetCoinJoinPO> {

}
