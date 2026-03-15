package com.cloud.baowang.system.repositories.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.member.BusinessAdminPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 职员信息 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface BusinessAdminRepository extends BaseMapper<BusinessAdminPO> {

    List<String> selectUserBySiteCodeAndApiUrl(@Param("siteCode") String siteCode,@Param("menuKey") List<String> menuKey);
}
