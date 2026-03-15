package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.VIPGradePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author 小智
 * @Date 2/5/23 6:42 PM
 * @Version 1.0
 */
@Mapper
public interface VIPGradeRepository extends BaseMapper<VIPGradePO> {

    @Select("SELECT vip_grade_name from site_vip_grade svg where site_code  = #{siteCode} and vip_grade_code = #{code}")
    String getVipGradeNameBySiteCode(@Param("siteCode") String siteCode, @Param("code") Integer code);
}
