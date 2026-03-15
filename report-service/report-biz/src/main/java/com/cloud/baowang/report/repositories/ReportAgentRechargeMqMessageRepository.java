package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.report.po.ReportAgentRechargeWithdrawMqMessagePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportAgentRechargeMqMessageRepository extends BaseMapper<ReportAgentRechargeWithdrawMqMessagePO> {
}
