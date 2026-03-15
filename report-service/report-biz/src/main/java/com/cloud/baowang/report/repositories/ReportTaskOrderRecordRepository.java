package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.report.po.ReportTaskOrderRecordPO;
import com.cloud.baowang.report.po.ReportUserWinLoseMessagePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务领取记录报表 Mapper 接口
 *
 * @author wade
 * @since 2023-05-02 10:00:00
 */
@Mapper
public interface ReportTaskOrderRecordRepository extends BaseMapper<ReportTaskOrderRecordPO> {

}
