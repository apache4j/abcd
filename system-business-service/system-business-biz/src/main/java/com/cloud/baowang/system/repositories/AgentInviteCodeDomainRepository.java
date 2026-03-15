package com.cloud.baowang.system.repositories;

import com.cloud.baowang.system.api.vo.AgentInviteCodeDomainVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AgentInviteCodeDomainRepository {

    AgentInviteCodeDomainVO getDomainAndInCode(@Param("shortUrl") String shortUrl,
                                               @Param("domainType") Integer domainType,
                                               @Param("bind")Integer bind);
}
