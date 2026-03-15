package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.report.po.ReportUserRechargeWithdrawMqMessagePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportUserRechargeMqMessageRepository extends BaseMapper<ReportUserRechargeWithdrawMqMessagePO> {
}
