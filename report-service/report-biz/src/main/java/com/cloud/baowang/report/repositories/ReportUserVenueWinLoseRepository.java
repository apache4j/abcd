package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.report.api.vo.ReportUserAgentVenueStaticsVO;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.ReportUserVenueStaticsVO;
import com.cloud.baowang.user.api.vo.user.request.UserTopReqVO;
import com.cloud.baowang.report.api.vo.PlatformVenueRequestVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentUserVenueLisParam;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueStaticsVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueWinLossVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.game.*;
import com.cloud.baowang.report.api.vo.site.SiteDataUserWinLossResVo;
import com.cloud.baowang.report.api.vo.site.SiteReportUserVenueStaticsVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueTopVO;
import com.cloud.baowang.report.api.vo.venuewinlose.*;
import com.cloud.baowang.report.po.ReportUserVenueWinLosePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员场馆每日盈亏 Mapper 接口
 */
@Mapper
public interface ReportUserVenueWinLoseRepository extends BaseMapper<ReportUserVenueWinLosePO> {

    List<ReportUserVenueTopVO> userVenueBets(@Param("userAccount") String userAccount,
                                       @Param("siteCode") String siteCode,
                                       @Param("startTime") Long startTime,@Param("endTime")Long endTime);

    Page<ReportUserVenueTopVO> topPlatformVenue(@Param("page") Page<ReportUserVenueTopVO> page,
                                       @Param("vo") PlatformVenueRequestVO vo);

    Page<ReportUserVenueBetsTopVO> queryUserBetsTop(Page<ReportUserVenueWinLosePO> page, @Param("vo") ReportUserTopReqVO userTopReqVO);

    Page<String> pageListByVenueCode(Page<ReportUserVenueWinLosePO> page, @Param("vo") ReportVenueWinLossPageReqVO vo);

    Page<VenueWinLossDetailResVO> detailList(Page<VenueWinLossDetailResVO> pageResult, @Param("vo") ReportVenueWinLossPageReqVO vo);

    List<VenueWinLossDetailResVO> totalByParam(@Param("vo") ReportVenueWinLossPageReqVO vo);


    /**
     * 查询指定天数条件内,有打码的币种
     */
    List<String> queryVenueDayCurrency(@Param("vo") UserTopReqVO vo);

    List<ReportAgentVenueWinLossVO> queryAgentVenueWinLoss(@Param("vo") ReportAgentWinLossParamVO vo);

    /**
     * 查询代理下各场馆总计
     * @param vo
     * @return
     */
    List<ReportVenueWinLossAgentVO> queryByTimeAndAgent(@Param("vo") ReportVenueWinLossAgentReqVO vo);

    /**
     * 查询代理下各场馆总计
     * @param vo
     * @return
     */
    Integer queryByTimeAndSiteCode(@Param("vo") SiteDataUserWinLossResVo vo);


    Page<VenueWinLossInfoResVO> infoList(Page<ReportUserVenueWinLosePO> tPage, @Param("vo") ReportVenueWinLossPageReqVO vo);

    Page<ReportGameQueryCenterVO> reportGameCenterPageList(Page<ReportUserVenueWinLosePO> objectPage, @Param("vo") ReportGameQueryCenterReqVO vo);

    List<ReportGameQueryCenterVO> reportGameCenterPageListSum(Page<ReportUserVenueWinLosePO> objectPage, @Param("vo") ReportGameQueryCenterReqVO vo);

    List<ReportGameQueryCenterVO> reportGameCenterPageListTotalSum(@Param("vo") ReportGameQueryCenterReqVO vo);

    Page<ReportGameQuerySiteVO> reportGameSitePageList(Page<ReportUserVenueWinLosePO> objectPage, @Param("vo") ReportGameQuerySiteReqVO vo);

    List<ReportGameQuerySiteVO> reportGameSitePageListSum(Page<ReportUserVenueWinLosePO> objectPage, @Param("vo") ReportGameQuerySiteReqVO vo);

    /**
     * reportGameSitePageList 分页汇总合计
     */
    List<ReportGameQuerySiteVO> reportGameSitePageListALL(@Param("vo") ReportGameQuerySiteReqVO vo);

    Page<ReportGameQueryVenueTypeVO> reportGameVenueTypePageList(Page<ReportUserVenueWinLosePO> objectPage, @Param("vo") ReportGameQueryVenueTypeReqVO vo);

    List<ReportGameQueryVenueTypeVO> reportGameVenueTypePageListAll( @Param("vo") ReportGameQueryVenueTypeReqVO vo);

    List<ReportGameQueryVenueTypeVO> reportGameVenueTypePageListSum( @Param("vo") ReportGameQueryVenueTypeReqVO vo);


    Page<ReportGameQueryVenueVO> reportGameVenuePageList(Page<ReportUserVenueWinLosePO> objectPage, @Param("vo") ReportGameQueryVenueReqVO vo);


    List<ReportGameQueryVenueVO> reportGameVenuePageListAll(Page<ReportUserVenueWinLosePO> objectPage, @Param("vo") ReportGameQueryVenueReqVO vo);

    List<ReportGameQueryVenueVO> reportGameVenuePageListPageSum(Page<ReportUserVenueWinLosePO> objectPage, @Param("vo") ReportGameQueryVenueReqVO vo);


    Long reportGameCenterCount(@Param("vo") ReportGameQueryCenterReqVO vo);

    Long sitePageListCount(@Param("vo") ReportGameQuerySiteReqVO vo);

    Long venueTypePageListCount(@Param("vo") ReportGameQueryVenueTypeReqVO vo);

    Long venuePageListCount(@Param("vo") ReportGameQueryVenueReqVO vo);

    List<ReportAgentVenueStaticsVO> getUserVenueAmountByAgentIds(@Param("vo") ReportAgentWinLossParamVO vo);

    List<ReportAgentVenueStaticsVO> getUserVenueAmountGroupByAgent(@Param("vo") ReportAgentWinLossParamVO vo);

    List<ReportAgentVenueStaticsVO> queryVenueAmountByDay(@Param("vo")  ReportAgentWinLossParamVO vo);

    List<ReportUserVenueTopVO> getAgentUserVenueList(@Param("vo")  ReportAgentUserVenueLisParam vo);

    Long getTotalCount(@Param("vo") ReportVenueWinLossPageReqVO vo);


    List<SiteReportUserVenueStaticsVO> getDailyDistinctUserCount(@Param("siteCode") String siteCode,
                                                                 @Param("startMillis") Long startMillis,
                                                                 @Param("endMillis") Long endMillis,
                                                                 @Param("timezone") String timezone
    );

    List<SiteReportUserVenueStaticsVO> getDailyCurrencyAmount(@Param("siteCode") String siteCode,
                                                                 @Param("startMillis") Long startMillis,
                                                                 @Param("endMillis") Long endMillis,
                                                                 @Param("currencyCode") String currencyCode,
                                                                 @Param("timezone") String timezone
    );

    List<ReportUserAgentVenueStaticsVO> statAgentValidBetAmount(
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("venueType") Integer venueType,
            @Param("siteCode") String siteCode,
            @Param("currency") String currency
    );
}
