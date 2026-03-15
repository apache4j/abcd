package com.cloud.baowang.system.repositories.operations;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.operations.SiteCustomerPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SiteCustomerRepository extends BaseMapper<SiteCustomerPO> {
    @Select("SELECT COUNT(c.id) AS channelCount\n" +
            "FROM customer_channel c\n" +
            "INNER JOIN site_customer s ON c.channel_code = s.channel_code\n" +
            "WHERE s.site_code = #{siteCode} \n" +
            "  AND c.status = 1;\n")
    int getCountByStatus(@Param("siteCode") String siteCode);
}
