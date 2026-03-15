package com.cloud.baowang.report.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.SexyGameTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.enums.dg2.DG2GameTypeEnum;
import com.cloud.baowang.play.api.vo.venue.GameInfoRequestVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
import com.cloud.baowang.report.api.vo.game.*;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReportGameService {
    private final ReportUserVenueWinLoseService reportUserVenueWinLoseService;
    private final SiteApi siteApi;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final I18nApi i18nApi;
    private final GameInfoApi gameInfoApi;

    private final PlayVenueInfoApi sitePlayVenueInfoApi;


    public void checkTime(long startTime, long endTime) {
        boolean b = DateUtils.checkTime(startTime, endTime, 31);
        if (b) {
            throw new BaowangDefaultException(ResultCode.OUT_TIME_RANGE);
        }
    }

    public Page<ReportGameQueryCenterVO> centerPageList(ReportGameQueryCenterReqVO vo) {
        checkTime(vo.getStartTime(), vo.getEndTime());
        String siteName = vo.getSiteName();
        if (StrUtil.isNotBlank(siteName)) {
            ResponseVO<List<SiteVO>> siteInfoResVO = siteApi.getSiteInfoByName(siteName);
            if (siteInfoResVO.isOk()) {
                List<SiteVO> data = siteInfoResVO.getData();
                if (CollUtil.isEmpty(data)) {
                    return new Page<>(vo.getPageNumber(), vo.getPageSize());
                } else {
                    vo.setSiteCodeList(data.stream().map(SiteVO::getSiteCode).toList());
                }
            }
        }
        Page<ReportGameQueryCenterVO> page = reportUserVenueWinLoseService.reportGameCenterPageList(vo);
        List<ReportGameQueryCenterVO> records = page.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            ResponseVO<List<SiteVO>> listResponseVO = siteApi.siteInfoAllstauts();
            Map<String, String> map = listResponseVO.getData().stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO::getSiteName, (k1, k2) -> k2));
            records.stream().forEach(x->{
                x.setSiteName(map.get(x.getSiteCode()));
            });
            Boolean convertPlatCurrency = vo.getConvertPlatCurrency();
            Map<String, Map<String, BigDecimal>> finalRateBySiteList;
            if (convertPlatCurrency) {
                List<String> siteCodeList = records.stream().map(ReportGameQueryCenterVO::getSiteCode).distinct().toList();
                finalRateBySiteList = currencyInfoApi.getAllFinalRateBySiteList(siteCodeList);
            } else {
                finalRateBySiteList = Maps.newHashMap();
            }


            //当页汇总
            List<ReportGameQueryCenterVO> pageSum = reportUserVenueWinLoseService.reportGameCenterPageListSum(vo);
            if(CollectionUtil.isNotEmpty(pageSum)){
                pageSum.forEach(x->{
                    x.setPageSumType(true);
                });
                if (convertPlatCurrency) {
                    pageSum.forEach(x-> {
                        Map<String, BigDecimal> coinRateMap = finalRateBySiteList.get(x.getSiteCode());
                        toSetReportGameConversionToPlatAmount(x, coinRateMap);
                    });
                }
            }


            //全部汇总
            List<ReportGameQueryCenterVO> totalSum = reportUserVenueWinLoseService.reportGameCenterPageListTotalSum(vo);
            if(CollectionUtil.isNotEmpty(totalSum)){
                totalSum.forEach(x->{
                    x.setAllSumType(true);
                });
                if (convertPlatCurrency) {
                    totalSum.forEach(x-> {
                        Map<String, BigDecimal> coinRateMap = finalRateBySiteList.get(x.getSiteCode());
                        toSetReportGameConversionToPlatAmount(x, coinRateMap);
                    });
                }
            }


            records.forEach(record -> {
                if (convertPlatCurrency) {
                    Map<String, BigDecimal> coinRateMap = finalRateBySiteList.get(record.getSiteCode());
                    toSetReportGameConversionToPlatAmount(record, coinRateMap);
                }
            });

            //本页统计
            if (CollectionUtil.isNotEmpty(records) && CollectionUtil.isNotEmpty(pageSum)) {
                records.add(setReportGameQueryVenueTypeVOSum(pageSum, ReportGameQueryCenterVO.class));
            }

            //全部统计
            if (CollectionUtil.isNotEmpty(records) && CollectionUtil.isNotEmpty(totalSum)) {
                records.add(setReportGameQueryVenueTypeVOSum(totalSum, ReportGameQueryCenterVO.class));
            }

        }
        return page;
    }


    public Page<ReportGameQuerySiteVO> sitePageList(ReportGameQuerySiteReqVO vo) {
        checkTime(vo.getStartTime(), vo.getEndTime());
        Page<ReportGameQuerySiteVO> reportGameQuerySiteVOPage = reportUserVenueWinLoseService.reportGameSitePageList(vo);
        List<ReportGameQuerySiteVO> records = reportGameQuerySiteVOPage.getRecords();
        Boolean convertPlatCurrency = vo.getConvertPlatCurrency();

        Map<String, BigDecimal> allFinalRate = null;
        if (convertPlatCurrency) {
            // 汇率
            allFinalRate = currencyInfoApi.getAllFinalRate(vo.getSiteCode());
        } else {
            allFinalRate = Map.of();
        }

        Map<String, BigDecimal> finalAllFinalRate = allFinalRate;

        if (convertPlatCurrency) {
            records.forEach(x->{
                toSetReportGameConversionToPlatAmount(x, finalAllFinalRate);
            });
        }

        //全部汇总
        List<ReportGameQuerySiteVO> reportGameQuerySiteVOAll = reportUserVenueWinLoseService.reportGameSitePageListALL(vo);
        if(CollectionUtil.isNotEmpty(reportGameQuerySiteVOAll)){
            reportGameQuerySiteVOAll.forEach(x->{
                x.setAllSumType(true);
                if (convertPlatCurrency) {
                    toSetReportGameConversionToPlatAmount(x, finalAllFinalRate);
                }
            });
        }

        //全部汇总
        if (CollectionUtil.isNotEmpty(reportGameQuerySiteVOAll) && CollectionUtil.isNotEmpty(records)) {
            records.add(setReportGameQueryVenueTypeVOSum(reportGameQuerySiteVOAll, ReportGameQuerySiteVO.class));
        }


        //分页汇总
        List<ReportGameQuerySiteVO> reportGameQuerySiteVOPageSum = reportUserVenueWinLoseService.reportGameSitePageListSum(vo);
        if(CollectionUtil.isNotEmpty(reportGameQuerySiteVOPageSum)){
            reportGameQuerySiteVOPageSum.forEach(x->{
                x.setPageSumType(true);
                if (convertPlatCurrency) {
                    toSetReportGameConversionToPlatAmount(x, finalAllFinalRate);
                }
            });
        }
        //分页汇总
        if (CollectionUtil.isNotEmpty(reportGameQuerySiteVOPageSum) && CollectionUtil.isNotEmpty(records)) {
            records.add(setReportGameQueryVenueTypeVOSum(reportGameQuerySiteVOPageSum, ReportGameQuerySiteVO.class));
        }
        return reportGameQuerySiteVOPage;
    }

    /**
     * 游戏报表转平台币
     */
    private <T extends ReportGameAmountBase> void toSetReportGameConversionToPlatAmount(T record, Map<String, BigDecimal> allFinalRate) {

        BigDecimal rate = allFinalRate.get(record.getCurrency());
        if (rate == null || BigDecimal.ZERO.compareTo(rate) == 0) {
            return;
        }

        record.setBetAmount(AmountUtils.divide(record.getBetAmount(), rate));
        record.setValidBetAmount(AmountUtils.divide(record.getValidBetAmount(), rate));
        record.setTipsAmount(AmountUtils.divide(record.getTipsAmount(), rate));
        record.setWinLoseAmount(AmountUtils.divide(record.getWinLoseAmount(), rate));
        record.setBetWinLose(AmountUtils.divide(record.getBetWinLose(), rate));
    }



    public Page<ReportGameQueryVenueTypeVO> venueTypePageList(ReportGameQueryVenueTypeReqVO vo) {
        checkTime(vo.getStartTime(), vo.getEndTime());
        String siteCode = CurrReqUtils.getSiteCode();
        Map<String,String> venueMap = Maps.newHashMap();
        //总控
        if(CommonConstant.ADMIN_CENTER_SITE_CODE.equals(siteCode)){
            venueMap = sitePlayVenueInfoApi.getAdminVenueNameMap().getData();
        }else{
            venueMap = sitePlayVenueInfoApi.getSiteVenueNameMap().getData();
        }

        Page<ReportGameQueryVenueTypeVO> venueTypeVOPage = reportUserVenueWinLoseService.reportGameVenueTypePageList(vo);
        List<ReportGameQueryVenueTypeVO> records = venueTypeVOPage.getRecords();
        Boolean convertPlatCurrency = vo.getConvertPlatCurrency();
        Map<String, BigDecimal> allFinalRate;
        if (convertPlatCurrency) {
            // 汇率9
            allFinalRate = currencyInfoApi.getAllFinalRate(vo.getSiteCode());
        } else {
            allFinalRate = Map.of();
        }
        Map<String, String> finalVenueMap = venueMap;
        records.forEach(record -> {
            if(finalVenueMap != null && ObjectUtil.isNotEmpty(record.getVenueCode())){
                String venueName = finalVenueMap.get(record.getVenueCode());
                record.setVenueCodeText(venueName);
            }
        });

        if (CollUtil.isNotEmpty(records)) {
            if (convertPlatCurrency) {
                records.forEach(record -> {
                    toSetReportGameConversionToPlatAmount(record, allFinalRate);
                });
            }
        }

        //全部汇总
        List<ReportGameQueryVenueTypeVO> venueTypeVOAll = reportUserVenueWinLoseService.reportGameVenueTypePageListAll(vo);
        if(CollectionUtil.isNotEmpty(venueTypeVOAll)){
            venueTypeVOAll.forEach(x -> {
                x.setAllSumType(true);
                if (convertPlatCurrency) {
                    toSetReportGameConversionToPlatAmount(x, allFinalRate);
                }
            });
        }

        if (CollectionUtil.isNotEmpty(venueTypeVOAll) && CollectionUtil.isNotEmpty(records)) {
            records.add(setReportGameQueryVenueTypeVOSum(venueTypeVOAll,ReportGameQueryVenueTypeVO.class));
        }


        //分页汇总
        List<ReportGameQueryVenueTypeVO> venueTypeVOPageSum = reportUserVenueWinLoseService.reportGameVenueTypePageListSum(vo);
        if(CollectionUtil.isNotEmpty(venueTypeVOPageSum)){
            venueTypeVOPageSum.forEach(x -> {
                x.setPageSumType(true);
                if (convertPlatCurrency) {
                    toSetReportGameConversionToPlatAmount(x, allFinalRate);
                }
            });
        }

        //分页汇总
        if (CollectionUtil.isNotEmpty(venueTypeVOPageSum) && CollectionUtil.isNotEmpty(venueTypeVOPageSum)) {
            records.add(setReportGameQueryVenueTypeVOSum(venueTypeVOPageSum,ReportGameQueryVenueTypeVO.class));
        }

        return venueTypeVOPage;
    }


    /**
     * 游戏报表汇总
     */
    private <T extends ReportGameAmountBase> T setReportGameQueryVenueTypeVOSum(List<T> venueTypeVOAll, Class<T> clazz) {

        try {
            // 创建子类实例
            T result = clazz.getDeclaredConstructor().newInstance();

            // 注单量
            Long betNum = venueTypeVOAll.stream()
                    .mapToLong(r -> r.getBetNum() == null ? 0 : r.getBetNum())
                    .sum();
            result.setBetNum(betNum);

            // 投注人数
            Long bettorNum = venueTypeVOAll.stream()
                    .mapToLong(r -> r.getBettorNum() == null ? 0 : r.getBettorNum())
                    .sum();
            result.setBettorNum(bettorNum);

            // 投注金额
            BigDecimal betAmount = venueTypeVOAll.stream()
                    .map(r -> r.getBetAmount() == null ? BigDecimal.ZERO : r.getBetAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setBetAmount(betAmount);

            // 有效投注金额
            BigDecimal totalValidBetAmount = venueTypeVOAll.stream()
                    .map(r -> r.getValidBetAmount() == null ? BigDecimal.ZERO : r.getValidBetAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setValidBetAmount(totalValidBetAmount);

            // 输赢金额
            BigDecimal totalWinLoseAmount = venueTypeVOAll.stream()
                    .map(r -> r.getWinLoseAmount() == null ? BigDecimal.ZERO : r.getWinLoseAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setWinLoseAmount(totalWinLoseAmount);

            // 投注金额
            BigDecimal totalBetWinLose = venueTypeVOAll.stream()
                    .map(r -> r.getBetWinLose() == null ? BigDecimal.ZERO : r.getBetWinLose())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setBetWinLose(totalBetWinLose);

            // 打赏金额
            BigDecimal tipsAmount = venueTypeVOAll.stream()
                    .map(r -> r.getTipsAmount() == null ? BigDecimal.ZERO : r.getTipsAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setTipsAmount(tipsAmount);

            if(CollectionUtil.isNotEmpty(venueTypeVOAll)){
                result.setAllSumType(venueTypeVOAll.get(0).getAllSumType());
                result.setPageSumType(venueTypeVOAll.get(0).getPageSumType());
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("创建子类实例失败", e);
        }
    }




    public Page<ReportGameQueryVenueVO> venuePageList(ReportGameQueryVenueReqVO vo) {
        checkTime(vo.getStartTime(), vo.getEndTime());
        // 游戏名称搜索
        Integer venueType = vo.getVenueType();
        String venueCode = vo.getVenueCode();
        String gameName = vo.getGameName();
        VenueTypeEnum typeEnum = VenueTypeEnum.of(venueType);
        if (StrUtil.isNotBlank(gameName)) {
            List<String> codeList = null;
            codeList = searchByName(typeEnum, gameName, codeList, venueCode);
            if (CollUtil.isEmpty(codeList)) {
                return new Page<>(vo.getPageNumber(), vo.getPageSize());
            } else {
                if (!typeEnum.equals(VenueTypeEnum.COCKFIGHTING)) {
                    vo.setThirdGameCodes(codeList);
                }
            }
        }

        Page<ReportGameQueryVenueVO> venueVOPage = reportUserVenueWinLoseService.reportGameVenuePageList(vo);
        List<ReportGameQueryVenueVO> records = venueVOPage.getRecords();
        Boolean convertPlatCurrency = vo.getConvertPlatCurrency();
        if (CollUtil.isNotEmpty(records)) {
            List<String> list = records.stream().map(ReportGameQueryVenueVO::getThirdGameType).filter(ObjUtil::isNotNull).distinct().toList();
            Map<String, String> gameNameMap = null;
            switch (typeEnum) {
                case CHESS, ELECTRONICS,FISHING,MARBLES,SH -> {
                    GameInfoRequestVO build = GameInfoRequestVO.builder().venueCode(venueCode).gameCodeIds(list).build();
                    build.setPageSize(-1);
                    ResponseVO<Page<GameInfoVO>> pageResponseVO = gameInfoApi.adminGameInfoPage(build);

                    if (pageResponseVO.isOk()) {
                        List<GameInfoVO> gameInfoVOS = pageResponseVO.getData().getRecords();
                        gameNameMap = Optional.ofNullable(gameInfoVOS)
                                .map(s -> s.stream().collect(
                                        Collectors.toMap(GameInfoVO::getAccessParameters, o -> StrUtil.isBlank(I18nMessageUtil.getI18NMessageInAdvice(o.getGameI18nCode())) ? Strings.EMPTY : I18nMessageUtil.getI18NMessageInAdvice(o.getGameI18nCode()), (k1, k2) -> k2)))
                                .orElse(Maps.newHashMap());
                    }
                }
            }

            Map<String, BigDecimal> allFinalRate;
            if (convertPlatCurrency) {
                // 汇率
                allFinalRate = currencyInfoApi.getAllFinalRate(vo.getSiteCode());
            } else {
                allFinalRate = Map.of();
            }
            Map<String, String> finalGameNameMap = gameNameMap;
            records.forEach(record -> {
                String name = null;
                switch (typeEnum) {
                    case SH: {
                        VenueEnum vEnum = VenueEnum.nameOfCode(record.getVenueCode());
                        if (vEnum != null) {
                            switch (vEnum) {
                                case SH ->
                                        name = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_GAME_TYPE, record.getThirdGameType());
                                case SA ->
                                        name = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SA_GAME_TYPE, record.getThirdGameType());
                                case EVO ->{
                                    if(finalGameNameMap != null){
                                        name =finalGameNameMap.get(record.getThirdGameType());
                                    }
                                }

                                case DG2 ->
                                        name = DG2GameTypeEnum.fromGameType(record.getThirdGameType())==null?"":DG2GameTypeEnum.fromGameType(record.getThirdGameType()).getDescription();
                                case SEXY ->
                                        name = SexyGameTypeEnum.getEnumByGameType(record.getThirdGameType())==null?"":SexyGameTypeEnum.getEnumByGameType(record.getThirdGameType()).getName();


                            }
                        }
                        record.setGameName(name);
                        break;
                    }
                    case SPORTS: {
                        record.setGameName(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SBA_SPORT_TYPE, record.getThirdGameType()));
                        break;
                    }
                    case ELECTRONIC_SPORTS: {
                        record.setGameName(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.ES_GAME_TYPE, record.getThirdGameType()));
                        break;
                    }
                    case ELECTRONICS, CHESS, FISHING, MARBLES: {
                        if(finalGameNameMap != null){
                            record.setGameName(finalGameNameMap.get(record.getThirdGameType()));
                        }
                        break;
                    }
                    case ACELT: {
                        record.setGameName(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.LT_GAME_TYPE, record.getThirdGameType()));
                        break;
                    }
                    case COCKFIGHTING: {
                        record.setGameName(I18nMessageUtil.getI18NMessage(CommonConstant.CF_GAME_TYPE));
                        break;
                    }
                }
                if (convertPlatCurrency) {
                    toSetReportGameConversionToPlatAmount(record, allFinalRate);
                }
            });


            //全部汇总
            List<ReportGameQueryVenueVO> venueVOListAll = reportUserVenueWinLoseService.reportGameVenuePageListAll(vo);
            if(CollectionUtil.isNotEmpty(venueVOListAll)){
                venueVOListAll.forEach(x -> {
                    x.setAllSumType(true);
                    if (convertPlatCurrency) {
                        toSetReportGameConversionToPlatAmount(x, allFinalRate);
                    }
                });
            }
            //全部汇总
            if (CollectionUtil.isNotEmpty(venueVOListAll) && CollectionUtil.isNotEmpty(records)) {
                records.add(setReportGameQueryVenueTypeVOSum(venueVOListAll,ReportGameQueryVenueVO.class));
            }


            //分页汇总
            List<ReportGameQueryVenueVO> venueVOListPageSumAll = reportUserVenueWinLoseService.reportGameVenuePageListPageSum(vo);
            if(CollectionUtil.isNotEmpty(venueVOListPageSumAll)){
                venueVOListPageSumAll.forEach(x -> {
                    x.setPageSumType(true);
                    if (convertPlatCurrency) {
                        toSetReportGameConversionToPlatAmount(x, allFinalRate);
                    }
                });
            }
            //分页汇总
            if (CollectionUtil.isNotEmpty(venueVOListAll) && CollectionUtil.isNotEmpty(records)) {
                records.add(setReportGameQueryVenueTypeVOSum(venueVOListPageSumAll,ReportGameQueryVenueVO.class));
            }


        }
        return venueVOPage;
    }

    private List<String> searchByName(VenueTypeEnum typeEnum, String gameName, List<String> codeList, String venueCode) {
        switch (typeEnum) {
            case SPORTS -> {
                String msgKey = I18MsgKeyEnum.SBA_SPORT.getCode();
                ResponseVO<List<CodeValueVO>> listResponseVO = i18nApi.searchGetLookup(I18nSearchVO.builder().bizKeyPrefix(msgKey).lang(CurrReqUtils.getLanguage()).searchContent(gameName).build());
                if (CollUtil.isNotEmpty(listResponseVO.getData())) {
                    codeList = listResponseVO.getData().stream().map(CodeValueVO::getCode).toList();
                }
            }
            case ELECTRONIC_SPORTS -> {
                String msgKey = I18MsgKeyEnum.ES_GAME_TYPE.getCode();
                ResponseVO<List<CodeValueVO>> listResponseVO = i18nApi.searchGetLookup(I18nSearchVO.builder().bizKeyPrefix(msgKey).lang(CurrReqUtils.getLanguage()).searchContent(gameName).build());
                if (CollUtil.isNotEmpty(listResponseVO.getData())) {
                    codeList = listResponseVO.getData().stream().map(CodeValueVO::getCode).toList();
                }
            }
            case SH -> {
                String msgKey = I18MsgKeyEnum.SH_GAME_TYPE.getCode();
                ResponseVO<List<CodeValueVO>> listResponseVO = i18nApi.searchGetLookup(I18nSearchVO.builder().bizKeyPrefix(msgKey).lang(CurrReqUtils.getLanguage()).searchContent(gameName).build());
                if (CollUtil.isNotEmpty(listResponseVO.getData())) {
                    codeList = listResponseVO.getData().stream().map(CodeValueVO::getCode).toList();
                }
            }
            case ACELT -> {
                String msgKey = I18MsgKeyEnum.ACELT_GAME.getCode();
                ResponseVO<List<CodeValueVO>> listResponseVO = i18nApi.searchGetLookup(I18nSearchVO.builder().bizKeyPrefix(msgKey).lang(CurrReqUtils.getLanguage()).searchContent(gameName).build());
                if (CollUtil.isNotEmpty(listResponseVO.getData())) {
                    codeList = listResponseVO.getData().stream().map(CodeValueVO::getCode).toList();
                }
            }
            case CHESS, ELECTRONICS,MARBLES -> {
                GameInfoRequestVO build = GameInfoRequestVO.builder().venueCode(venueCode).exactSearchContent(gameName).build();
                build.setPageSize(-1);
                ResponseVO<Page<GameInfoVO>> pageResponseVO = gameInfoApi.adminGameInfoPage(build);
                Page<GameInfoVO> data = pageResponseVO.getData();
                List<GameInfoVO> records = data.getRecords();
                codeList = records.stream().map(GameInfoVO::getAccessParameters).toList();
            }
            case COCKFIGHTING -> {
                String msgKey = I18MsgKeyEnum.COCKFIGHTING_GAME_TYPE.getCode();
                ResponseVO<List<CodeValueVO>> listResponseVO = i18nApi.searchGetLookup(I18nSearchVO.builder().bizKeyPrefix(msgKey).lang(CurrReqUtils.getLanguage()).searchContent(gameName).build());
                if (CollUtil.isNotEmpty(listResponseVO.getData())) {
                    codeList = listResponseVO.getData().stream().map(CodeValueVO::getCode).toList();
                }
            }
        }
        return codeList;
    }

    public Long centerPageListCount(ReportGameQueryCenterReqVO vo) {
        checkTime(vo.getStartTime(), vo.getEndTime());
        String siteName = vo.getSiteName();
        if (StrUtil.isNotBlank(siteName)) {
            ResponseVO<List<SiteVO>> siteInfoResVO = siteApi.getSiteInfoByName(siteName);
            if (siteInfoResVO.isOk()) {
                List<SiteVO> data = siteInfoResVO.getData();
                if (CollUtil.isEmpty(data)) {
                    return 0L;
                } else {
                    vo.setSiteCodeList(data.stream().map(SiteVO::getSiteCode).toList());
                }
            }
        }
        return reportUserVenueWinLoseService.reportGameCenterCount(vo);
    }

    public Long sitePageListCount(ReportGameQuerySiteReqVO vo) {
        checkTime(vo.getStartTime(), vo.getEndTime());
        return reportUserVenueWinLoseService.sitePageListCount(vo);
    }

    public Long venueTypePageListCount(ReportGameQueryVenueTypeReqVO vo) {
        checkTime(vo.getStartTime(), vo.getEndTime());
        return reportUserVenueWinLoseService.venueTypePageListCount(vo);
    }

    public Long venuePageListCount(ReportGameQueryVenueReqVO vo) {
        checkTime(vo.getStartTime(), vo.getEndTime());
        checkTime(vo.getStartTime(), vo.getEndTime());
        // 游戏名称搜索
        Integer venueType = vo.getVenueType();
        String venueCode = vo.getVenueCode();
        String gameName = vo.getGameName();
        VenueTypeEnum typeEnum = VenueTypeEnum.of(venueType);
        if (StrUtil.isNotBlank(gameName)) {
            List<String> codeList = null;
            codeList = searchByName(typeEnum, gameName, codeList, venueCode);
            if (CollUtil.isEmpty(codeList)) {
                return 0L;
            } else {
                if (!typeEnum.equals(VenueTypeEnum.COCKFIGHTING)) {
                    vo.setThirdGameCodes(codeList);
                }
            }
        }
        return reportUserVenueWinLoseService.venuePageListCount(vo);
    }
}
