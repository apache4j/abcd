package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.po.AgentMerchantLoginInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AgentMerchantLoginInfoRepository extends BaseMapper<AgentMerchantLoginInfoPO> {
    Long getCountByLoginType(@Param("siteCode") String siteCode, @Param("loginType") Integer loginType);
}
