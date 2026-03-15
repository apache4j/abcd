package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogRespVO;
import com.cloud.baowang.wallet.po.SiteSecurityBalancePO;
import com.cloud.baowang.wallet.po.SiteSecurityChangeLogPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface SiteSecurityChangeLogRepository extends BaseMapper<SiteSecurityChangeLogPO> {


    SiteSecurityChangeLogRespVO selectTotal(@Param(Constants.WRAPPER)LambdaQueryWrapper<SiteSecurityChangeLogPO> lqw);
}
