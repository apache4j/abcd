package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityLotteryRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SiteActivityLotteryRecordRepository extends BaseMapper<SiteActivityLotteryRecordPO> {
    @Select("SELECT SUM(reward_count) FROM site_activity_lottery_record WHERE user_id = #{userId}")
    Integer getTotalRewardCountByUserId(@Param("userId") String userId);
}
