package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.ReportUserWinLossReqVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.report.po.ReportUserFixedVenueWinlosePO;
import com.cloud.baowang.report.po.ReportUserVenueWinLosePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportUserFixedVenueWinloseRepository extends BaseMapper<ReportUserFixedVenueWinlosePO> {
    List<ReportUserVenueBetsTopVO> queryUserWinLossInfo(@Param("vo") ReportUserWinLossReqVO userWinLossReqVO);

    /**
     * 查询指定天数条件内,有打码的币种
     */
    List<String> queryVenueDayCurrency(@Param("vo") ReportUserTopReqVO vo);

    /**
     * 查询指定天数条件内,每天都有打码的用户 并且超过指定条件的用
     */
    Page<ReportUserVenueBetsTopVO> queryUserIdsByVenueDayAmount(Page<ReportUserVenueWinLosePO> page, @Param("vo") ReportUserTopReqVO vo);

    /**
     * 查询出每个币种的前100条数据
     */
    List<ReportUserVenueBetsTopVO> queryAllCurrencyTop(@Param("vo") ReportUserTopReqVO ReportUserTopReqVO);

    /**
     * 汇总指定用户的打码
     */
    List<ReportUserVenueBetsTopVO> queryUserBetsPlatBetAmountTotal(@Param("vo") ReportUserTopReqVO ReportUserTopReqVO);

    /**
     * 汇总指定场馆的打码
     */
    List<ReportUserVenueBetsTopVO> queryVenueBetsPlatBetAmountTotal(@Param("vo") ReportUserTopReqVO ReportUserTopReqVO);


    Page<ReportUserVenueBetsTopVO> queryUserBetsTop(Page<ReportUserVenueWinLosePO> page, @Param("vo") ReportUserTopReqVO ReportUserTopReqVO);

}
