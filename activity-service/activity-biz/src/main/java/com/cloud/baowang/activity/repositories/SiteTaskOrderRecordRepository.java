package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.vo.report.ReportTaskReportPageCopyVO;
import com.cloud.baowang.activity.api.vo.task.ReportTaskOrderRecordResVO;
import com.cloud.baowang.activity.po.SiteTaskOrderRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface SiteTaskOrderRecordRepository extends BaseMapper<SiteTaskOrderRecordPO> {
    @Select("SELECT SUM(task_amount) FROM site_task_order_record where user_id = #{userId} " +
            " and  receive_status = #{receiveStatus}")
    BigDecimal sumTaskAmountByUserId(@Param("userId") String userId, @Param("receiveStatus") Integer receiveStatus);


    @Select("SELECT distinct( user_id) FROM site_task_order_record where site_code = #{siteCode} " +
            " and  created_time >= #{timeStart} and created_time <= #{timeEnd} and task_type= #{taskType} ")
    List<String> getUserIdsOfSiteTaskOrderRecords(@Param("taskType") String taskType, @Param("siteCode") String siteCode,
                                                  @Param("timeStart") Long timeStart, @Param("timeEnd") Long timeEnd
    );


    List<ReportTaskOrderRecordResVO> reportList(@Param("startTime") Long startTime,
                                                @Param("endTime") Long endTime,
                                                @Param("siteCode") String siteCode,
                                                @Param("dbZon") String dbZon);

    List<ReportTaskOrderRecordResVO> reportReceivedList(@Param("startTime") Long startTime,
                                                        @Param("endTime") Long endTime,
                                                        @Param("siteCode") String siteCode,
                                                        @Param("dbZon") String dbZon);


    List<String> noReceivedList(@Param("siteCode") String siteCode);

    Page<ReportTaskOrderRecordResVO> reportListPage(@Param("page") Page<ReportTaskOrderRecordResVO> page, @Param("vo") ReportTaskReportPageCopyVO reportPageVO);

    ReportTaskOrderRecordResVO reportListPageTotal(@Param("vo") ReportTaskReportPageCopyVO reportPageVO);

    long getTotalCountReport(@Param("vo") ReportTaskReportPageCopyVO reportPageVO);

    ReportTaskOrderRecordResVO reportListAll(@Param("vo") ReportTaskReportPageCopyVO reportPageVO);


}
