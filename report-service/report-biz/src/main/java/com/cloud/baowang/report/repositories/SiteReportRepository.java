package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.report.api.vo.site.SiteReportStatisticsQueryPageQueryVO;
import com.cloud.baowang.report.api.vo.site.SiteStatisticsVO;
import com.cloud.baowang.report.po.SiteStatisticsPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteReportRepository extends BaseMapper<SiteStatisticsPO> {

    Page<SiteStatisticsVO> getSiteReportPage(Page<SiteStatisticsVO> page, @Param("vo") SiteReportStatisticsQueryPageQueryVO queryVO);

    List<SiteStatisticsVO> getSiteReportList(@Param("vo") SiteReportStatisticsQueryPageQueryVO queryVO);

    Long getTotal(@Param("vo") SiteReportStatisticsQueryPageQueryVO vo);
}
