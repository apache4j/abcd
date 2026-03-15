package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.report.api.vo.vip.ReportVIPDataReq;
import com.cloud.baowang.report.api.vo.vip.UserVIPVO;
import com.cloud.baowang.report.api.vo.vip.VIPAchieveVO;
import com.cloud.baowang.report.api.vo.vip.VIPAwardVO;
import com.cloud.baowang.report.po.ReportVIPInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ReportVIPInfoRepository extends BaseMapper<ReportVIPInfoPO> {

    List<UserVIPVO> getUserVIPData(@Param("startTime") long startTime, @Param("endTime")long endTime,
                                   @Param("siteCode") String siteCode);

    List<VIPAwardVO> getUserBonus(@Param("startTime") long startTime, @Param("endTime") long endTime,
                                  @Param("siteCode") String siteCode);

    List<VIPAchieveVO> getVIPAchieve(@Param("startTime") long startTime, @Param("endTime")long endTime,
                                     @Param("siteCode") String siteCode);

    BigDecimal selectBonusTotal(@Param("vo") ReportVIPDataReq req);


    List<VIPAwardVO> getCnUserBonus(@Param("startTime") long startTime, @Param("endTime") long endTime,
                                  @Param("siteCode") String siteCode);

    List<VIPAchieveVO> getCnVIPAchieve(@Param("startTime") long startTime, @Param("endTime")long endTime,
                                     @Param("siteCode") String siteCode);
}
