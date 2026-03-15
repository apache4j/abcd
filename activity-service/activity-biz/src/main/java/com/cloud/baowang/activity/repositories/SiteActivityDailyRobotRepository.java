package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityDailyRobotPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

@Mapper
public interface SiteActivityDailyRobotRepository extends BaseMapper<SiteActivityDailyRobotPO> {


    /**
     * 初始化 每日竞赛机器人
     */
    int updateInitDailyRobot(@Param("id") String id);
}
