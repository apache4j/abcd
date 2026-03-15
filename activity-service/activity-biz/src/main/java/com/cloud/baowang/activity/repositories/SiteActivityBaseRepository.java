package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.vo.report.ActivityDataReportRespVO;
import com.cloud.baowang.activity.api.vo.report.DataReportReqVO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SiteActivityBaseRepository extends BaseMapper<SiteActivityBasePO> {
    /**
     * 活动数据报表查询
     * @param dataReportReqVO 请求参数
     * @return
     */
    Page<ActivityDataReportRespVO> selectDataReportPage(@Param("page") Page<ActivityDataReportRespVO> page,@Param("dataReportReqVO") DataReportReqVO dataReportReqVO);

    /**
     * 活动数据报表汇总
     * @param dataReportReqVO 请求参数
     * @return
     */
    ActivityDataReportRespVO sumAllOrderDataReport(@Param("dataReportReqVO") DataReportReqVO dataReportReqVO);

    /**
     * 旋转次数汇总
     * @param dataReportReqVO
     * @return
     */
    ActivityDataReportRespVO sumAllWheelNumDataReport(@Param("dataReportReqVO") DataReportReqVO dataReportReqVO);

    /**
     * 参与人数
     * @param dataReportReqVO
     * @return
     */
    ActivityDataReportRespVO sumAllEventDataReport(@Param("dataReportReqVO") DataReportReqVO dataReportReqVO);
    /**
     * 已领取人数
     * @param dataReportReqVO
     * @return
     */
    ActivityDataReportRespVO sumAllRecvDataReport(@Param("dataReportReqVO") DataReportReqVO dataReportReqVO);
}
