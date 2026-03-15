package com.cloud.baowang.system.repositories.site.rebate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateInfoVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateQueryVO;
import com.cloud.baowang.system.po.site.rebate.UserRebateVenueRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface UserRebateVenueRecordRepository extends BaseMapper<UserRebateVenueRecordPO> {

    Page<ReportUserRebateInfoVO>  siteRebateListPage(@Param("page") Page<ReportUserRebateInfoVO> page,@Param("vo") ReportUserRebateQueryVO reqVO);

   // Page<ReportUserRebateInfoVO> siteRebateInfoPage(@Param("page") Page<ReportUserRebateInfoVO> page,@Param("vo") ReportUserRebateQueryVO reqVO);

    ReportUserRebateInfoVO siteTotalRebateInfo(@Param("vo") ReportUserRebateQueryVO reqVO);

  //  Page<ReportUserRebateInfoVO> manualRebateInfo(@Param("page") Page<ReportUserRebateInfoVO> page,@Param("vo") ReportUserRebateQueryVO reqVO);

    Page<ReportUserRebateInfoVO> venueRebatePage(@Param("page") Page<ReportUserRebateInfoVO> page,@Param("vo") ReportUserRebateQueryVO reqVO);

    Page<ReportUserRebateInfoVO> siteBackAdjustRebatePage(@Param("page") Page<ReportUserRebateInfoVO> page,@Param("vo") ReportUserRebateQueryVO reqVO);

    //统计领取返水
    Page<ReportUserRebateInfoVO> siteReceiveRebatePage(@Param("page") Page<ReportUserRebateInfoVO> page, @Param("vo") ReportUserRebateQueryVO reqVO);
}
