package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityLotteryBalancePO;
import com.cloud.baowang.activity.po.SiteActivityLotteryRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SiteActivityLotteryBalanceRepository extends BaseMapper<SiteActivityLotteryBalancePO> {

}
