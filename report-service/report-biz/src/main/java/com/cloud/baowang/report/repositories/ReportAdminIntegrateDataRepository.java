package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataReportReqVO;
import com.cloud.baowang.report.api.vo.user.complex.AdminIntegrateDataTempRspVO;
import com.cloud.baowang.report.po.ReportMembershipStatsPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportAdminIntegrateDataRepository extends BaseMapper<ReportMembershipStatsPO> {
    Page<AdminIntegrateDataTempRspVO> getIntegrateTempData(@Param("page") Page<AdminIntegrateDataTempRspVO> page, @Param("vo") AdminIntegrateDataReportReqVO vo);

    List<AdminIntegrateDataTempRspVO> statisticIntegrateAllTempData(@Param("vo") AdminIntegrateDataReportReqVO vo);

    Page<AdminIntegrateDataTempRspVO> getIntegrateTempAdminData(@Param("page") Page<AdminIntegrateDataTempRspVO> page, @Param("vo") AdminIntegrateDataReportReqVO vo);

    List<AdminIntegrateDataTempRspVO> getBettorNum(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("currencyList") List<String> currencyList, @Param("siteCodeList") List<String> siteCodeList,@Param("timeZoneDb") String timeZoneDb);

    List<AdminIntegrateDataTempRspVO> getTotalBettorNum(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("currencyList") List<String> currencyList, @Param("siteCodeList") List<String> siteCodeList);
}

