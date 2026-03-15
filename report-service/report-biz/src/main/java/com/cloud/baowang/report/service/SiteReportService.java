package com.cloud.baowang.report.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.report.api.vo.SiteReportSyncDataVO;
import com.cloud.baowang.report.api.vo.SiteStatisticsRecordVO;
import com.cloud.baowang.report.api.vo.UserWinLoseBetUserVO;
import com.cloud.baowang.report.api.vo.site.SiteReportStatisticsQueryPageQueryVO;
import com.cloud.baowang.report.api.vo.site.SiteStatisticsVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseResponseVO;
import com.cloud.baowang.report.po.ReportUserRechargeWithdrawPO;
import com.cloud.baowang.report.po.SiteStatisticsPO;
import com.cloud.baowang.report.repositories.ReportUserRechargeWithdrawRepository;
import com.cloud.baowang.report.repositories.SiteReportRepository;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.SiteUserDateQueryVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SiteReportService extends ServiceImpl<SiteReportRepository, SiteStatisticsPO> {
    private final SiteReportRepository reportRepository;
    private final SiteApi siteApi;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final UserInfoApi userInfoApi;
    private final ReportUserWinLoseService winLoseService;
    private final ReportUserRechargeWithdrawRepository userRechargeWithdrawRepository;

    public ResponseVO<SiteStatisticsRecordVO> getSiteReport(SiteReportStatisticsQueryPageQueryVO queryVO) {
        SiteStatisticsRecordVO result = new SiteStatisticsRecordVO();
        Page<SiteStatisticsVO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());

        page = reportRepository.getSiteReportPage(page, queryVO);
        List<SiteStatisticsVO> records = page.getRecords();
        List<SiteStatisticsVO> siteReportList = reportRepository.getSiteReportList(queryVO);
        //变更了现有会员人数的统计方式
        processTotalMember(records, queryVO.getCurrencyCode());
        //总计数据也一起变更一下
        processTotalMember(siteReportList, queryVO.getCurrencyCode());
        //获取投注人数-小计/总计
        Long smallBetUserCount = processBetUserCount(records, queryVO.getStartTime(), queryVO.getEndTime());
        Long allBetUserCount = processBetUserCount(siteReportList, queryVO.getStartTime(), queryVO.getEndTime());

        //转化为平台币查看
        if (queryVO.getIsPlantShow()) {
            processPlantShow(records);
            processPlantShow(siteReportList);
        }

        //生成小计/总计数据
        SiteStatisticsVO small = accumulateStatistics(records, "小计");
        SiteStatisticsVO total = accumulateStatistics(siteReportList, "总计");

        small.setBetUserCount(smallBetUserCount);
        total.setBetUserCount(allBetUserCount);

        Page<SiteStatisticsVO> pages = new Page<>();
        pages.setSize(page.getSize());
        pages.setCurrent(page.getCurrent());
        pages.setCountId(page.countId());
        pages.setTotal(page.getTotal());
        pages.setPages(page.getPages());


        pages.setRecords(records);
        result.setSmallRecord(small);
        result.setTotalRecord(total);
        result.setPages(pages);
        return ResponseVO.success(result);
    }


    private Long processBetUserCount(List<SiteStatisticsVO> records, Long startTime, Long endTime) {
        Long smallBetUserCount = 0L;

        //组装一下下注人数
        if (CollectionUtil.isNotEmpty(records)) {
            String platCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;

            List<String> siteCodeList = records.stream()
                    .map(SiteStatisticsVO::getSiteCode)
                    .distinct()
                    .toList();
            Map<String, Map<String, List<UserWinLoseBetUserVO>>> betUserVOS = winLoseService.getSiteBetUserList(startTime, endTime, siteCodeList);
            for (SiteStatisticsVO record : records) {
                Long betUserCount = 0L;
                String dateStr = record.getDateStr();
                if (betUserVOS.containsKey(record.getSiteCode())) {
                    Map<String, List<UserWinLoseBetUserVO>> dateBetVOMap = betUserVOS.get(record.getSiteCode());
                    if (dateBetVOMap.containsKey(dateStr)) {
                        List<UserWinLoseBetUserVO> currencyBetList = dateBetVOMap.get(dateStr);
                        if (CollectionUtil.isNotEmpty(currencyBetList)) {
                            for (UserWinLoseBetUserVO userWinLoseBetUserVO : currencyBetList) {
                                if (record.getCurrencyCode().equals(userWinLoseBetUserVO.getCurrencyCode())) {
                                    betUserCount = userWinLoseBetUserVO.getBetUserCount();
                                    break;
                                }
                            }
                        }
                        smallBetUserCount += betUserCount;
                    }
                }
                record.setBetUserCount(betUserCount);
                record.setPlatCurrencyCode(platCurrencyCode);
            }
        }
        return smallBetUserCount;
    }


    private void processTotalMember(List<SiteStatisticsVO> records, String currencyCode) {
        if (CollectionUtil.isNotEmpty(records)) {
            Map<String, List<String>> dateSiteStrMap = records.stream()
                    .collect(Collectors.groupingBy(
                            SiteStatisticsVO::getDateStr,
                            Collectors.mapping(SiteStatisticsVO::getSiteCode,
                                    Collectors.toList()))
                    );

            List<SiteUserDateQueryVO> userDateQueryVOS = userInfoApi.getSiteCurrencyUserList(dateSiteStrMap, currencyCode);
            if (CollectionUtil.isNotEmpty(userDateQueryVOS)) {
                Map<String, Map<String, Map<String, Long>>> userCount = userDateQueryVOS.stream()
                        .collect(Collectors.groupingBy(SiteUserDateQueryVO::getSiteCode,
                                Collectors.groupingBy(SiteUserDateQueryVO::getDateStr,
                                        Collectors.toMap(SiteUserDateQueryVO::getMainCurrency, SiteUserDateQueryVO::getUserCount))));
                for (SiteStatisticsVO record : records) {
                    Long totalMember = 0L;
                    if (userCount.containsKey(record.getSiteCode()) &&
                            userCount.get(record.getSiteCode()).containsKey(record.getDateStr())) {
                        Map<String, Long> currencyUserCountMap = userCount.get(record.getSiteCode()).get(record.getDateStr());
                        if (currencyUserCountMap.containsKey(record.getCurrencyCode())) {
                            totalMember = currencyUserCountMap.get(record.getCurrencyCode());
                        }
                    }
                    record.setTotalMembers(totalMember);
                }
            }else {
                records.forEach(item->item.setTotalMembers(0L));
            }
        }
    }

    /**
     * 转为平台币查看
     *
     * @param records 列表数据
     */
    private void processPlantShow(List<SiteStatisticsVO> records) {
        if (CollectionUtil.isNotEmpty(records)) {
            Long time=System.currentTimeMillis();
            //转换为平台币查看,这里把返回给前端展示用的币种换成平台币符号,修改数据
            List<String> siteCodes = records.stream().map(SiteStatisticsVO::getSiteCode).distinct().toList();
            Map<String, Map<String, BigDecimal>> allFinalRate = currencyInfoApi.getAllSiteFinalRate(siteCodes);
            for (SiteStatisticsVO record : records) {
                if (allFinalRate.containsKey(record.getSiteCode())) {
                    Map<String, BigDecimal> currencyMap = allFinalRate.get(record.getSiteCode());
                    BigDecimal rate = currencyMap.get(record.getCurrencyCode());
                    if (rate != null) {
                        record.setFirstDepositAmount(AmountUtils.divide(record.getFirstDepositAmount(), rate));
                        // 处理存款金额
                        record.setDepositAmount(AmountUtils.divide(record.getDepositAmount(), rate));
                        // 处理取款金额
                        record.setWithdrawalAmount(AmountUtils.divide(record.getWithdrawalAmount(), rate));
                        // 处理存取差
                        record.setDepositWithdrawalDifference(AmountUtils.divide(record.getDepositWithdrawalDifference(), rate));
                        // 处理已使用优惠
                        record.setSiteUsedOffers(AmountUtils.divide(record.getSiteUsedOffers(), rate));
                        // 处理其他调整
                        record.setSiteOtherAdjustments(AmountUtils.divide(record.getSiteOtherAdjustments(), rate));
                        // 处理投注金额
                        record.setBettingAmount(AmountUtils.divide(record.getBettingAmount(), rate));
                        // 处理有效投注
                        record.setValidBetting(AmountUtils.divide(record.getValidBetting(), rate));
                        // 处理会员输赢
                        record.setMemberProfitLoss(AmountUtils.divide(record.getMemberProfitLoss(), rate));
                        // 处理净盈利
                        record.setNetProfit(AmountUtils.divide(record.getNetProfit(), rate));
                        // 打赏金额
                        record.setTipsAmount(AmountUtils.divide(record.getTipsAmount(), rate));
//                        已经是平台币了不用换算
//                        // 调整金额(其他调整)-平台币
//                        record.setPlatAdjustAmount(AmountUtils.divide(record.getPlatAdjustAmount(), rate));
                        // 封控金额-主货币
                        record.setRiskAmount(AmountUtils.divide(record.getRiskAmount(), rate));
                    }
                }
            }
        }

    }

    /**
     * 创建小计/总计
     *
     * @param list     数组
     * @param siteName 表头名
     * @return vo
     */
    private SiteStatisticsVO accumulateStatistics(List<SiteStatisticsVO> list,
                                                  String siteName) {
        SiteStatisticsVO result = new SiteStatisticsVO();
        result.setSiteName(null);

        long totalMembers = 0L;
        int totalNewMembers = 0;
        int totalFirstDepositCount = 0;
        BigDecimal totalFirstDepositAmount = BigDecimal.ZERO;
        BigDecimal totalDepositAmount = BigDecimal.ZERO;
        int totalDepositCount = 0;
        BigDecimal totalWithdrawalAmount = BigDecimal.ZERO;
        int totalWithdrawalCount = 0;
        int totalLargeWithdrawalCount = 0;
        BigDecimal totalDepositWithdrawalDifference = BigDecimal.ZERO;
        int totalBetCount = 0;
        BigDecimal totalBettingAmount = BigDecimal.ZERO;
        BigDecimal totalValidBetting = BigDecimal.ZERO;
        BigDecimal totalMemberProfitLoss = BigDecimal.ZERO;
        BigDecimal totalNetProfit = BigDecimal.ZERO;
        BigDecimal totalSiteVipBenefits = BigDecimal.ZERO;
        BigDecimal totalSitePromotionalOffers = BigDecimal.ZERO;
        BigDecimal totalSiteUsedOffers = BigDecimal.ZERO;
        BigDecimal totalSiteOtherAdjustments = BigDecimal.ZERO;

        BigDecimal platAdjustAmount= BigDecimal.ZERO;
        BigDecimal tipsAmount= BigDecimal.ZERO;
        BigDecimal riskAmount= BigDecimal.ZERO;

        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, Map<String, Long>> siteMemberCountMap = new ConcurrentHashMap<>();
            for (SiteStatisticsVO vo : list) {
                if (!siteMemberCountMap.containsKey(vo.getSiteCode())) {
                    //当前某个站点对应某个币种的会员人数
                    HashMap<String, Long> currencyMember = new HashMap<>();
                    currencyMember.put(vo.getCurrencyCode(), vo.getTotalMembers());
                    siteMemberCountMap.put(vo.getSiteCode(), currencyMember);
                } else {
                    Map<String, Long> currencyMember = siteMemberCountMap.get(vo.getSiteCode());
                    if (!currencyMember.containsKey(vo.getCurrencyCode())) {
                        currencyMember.put(vo.getCurrencyCode(), vo.getTotalMembers());
                        siteMemberCountMap.put(vo.getSiteCode(), currencyMember);
                    }
                }
                // 累加新增会员人数
                totalNewMembers += (vo.getNewMembers() != null ? vo.getNewMembers() : 0);

                // 累加首存人数
                totalFirstDepositCount += (vo.getFirstDepositCount() != null ? vo.getFirstDepositCount() : 0);

                // 累加首存金额
                totalFirstDepositAmount = totalFirstDepositAmount.add(vo.getFirstDepositAmount() != null ? vo.getFirstDepositAmount() : BigDecimal.ZERO);

                // 累加存款金额
                totalDepositAmount = totalDepositAmount.add(vo.getDepositAmount() != null ? vo.getDepositAmount() : BigDecimal.ZERO);

                // 累加存款次数
                totalDepositCount += (vo.getDepositCount() != null ? vo.getDepositCount() : 0);

                // 累加取款金额
                totalWithdrawalAmount = totalWithdrawalAmount.add(vo.getWithdrawalAmount() != null ? vo.getWithdrawalAmount() : BigDecimal.ZERO);

                // 累加取款次数
                totalWithdrawalCount += (vo.getWithdrawalCount() != null ? vo.getWithdrawalCount() : 0);

                // 累加大额取款次数
                totalLargeWithdrawalCount += (vo.getLargeWithdrawalCount() != null ? vo.getLargeWithdrawalCount() : 0);

                // 累加存取差
                totalDepositWithdrawalDifference = totalDepositWithdrawalDifference.add(vo.getDepositWithdrawalDifference() != null ? vo.getDepositWithdrawalDifference() : BigDecimal.ZERO);

                // 累加注单量
                totalBetCount += (vo.getBetCount() != null ? vo.getBetCount() : 0);

                // 累加投注金额
                totalBettingAmount = totalBettingAmount.add(vo.getBettingAmount() != null ? vo.getBettingAmount() : BigDecimal.ZERO);

                // 累加有效投注
                totalValidBetting = totalValidBetting.add(vo.getValidBetting() != null ? vo.getValidBetting() : BigDecimal.ZERO);

                // 累加会员输赢
                totalMemberProfitLoss = totalMemberProfitLoss.add(vo.getMemberProfitLoss() != null ? vo.getMemberProfitLoss() : BigDecimal.ZERO);
                //vip福利
                totalSiteVipBenefits = totalSiteVipBenefits.add(vo.getSiteVipBenefits() != null ? vo.getSiteVipBenefits() : BigDecimal.ZERO);
                //活动优惠
                totalSitePromotionalOffers = totalSitePromotionalOffers.add(vo.getSitePromotionalOffers() != null ? vo.getSitePromotionalOffers() : BigDecimal.ZERO);
                //已使用优惠
                totalSiteUsedOffers = totalSiteUsedOffers.add(vo.getSiteUsedOffers() != null ? vo.getSiteUsedOffers() : BigDecimal.ZERO);
                //其他调整
                totalSiteOtherAdjustments = totalSiteOtherAdjustments.add(vo.getSiteOtherAdjustments() != null ? vo.getSiteOtherAdjustments() : BigDecimal.ZERO);
                // 累加净盈利
                totalNetProfit = totalNetProfit.add(vo.getNetProfit() != null ? vo.getNetProfit() : BigDecimal.ZERO);
                // 累加调整金额(其他调整)-平台币
                platAdjustAmount = platAdjustAmount.add(vo.getPlatAdjustAmount() != null ? vo.getPlatAdjustAmount() : BigDecimal.ZERO);
                // 累加打赏金额
                tipsAmount = tipsAmount.add(vo.getTipsAmount() != null ? vo.getTipsAmount() : BigDecimal.ZERO);
                // 累加封控金额-主货币
                riskAmount = riskAmount.add(vo.getRiskAmount() != null ? vo.getRiskAmount() : BigDecimal.ZERO);
            }
            totalMembers = siteMemberCountMap.values().stream()
                    .flatMap(innerMap -> innerMap.values().stream())
                    .mapToLong(Long::longValue)
                    .sum();
        }
        result.setTotalMembers(totalMembers);
        result.setNewMembers(totalNewMembers);
        result.setFirstDepositCount(totalFirstDepositCount);
        result.setFirstDepositAmount(totalFirstDepositAmount);
        result.setDepositAmount(totalDepositAmount);
        result.setDepositCount(totalDepositCount);
        result.setWithdrawalAmount(totalWithdrawalAmount);
        result.setWithdrawalCount(totalWithdrawalCount);
        result.setLargeWithdrawalCount(totalLargeWithdrawalCount);
        result.setDepositWithdrawalDifference(totalDepositWithdrawalDifference);
        result.setBetCount(totalBetCount);
        result.setBettingAmount(totalBettingAmount);
        result.setValidBetting(totalValidBetting);
        result.setMemberProfitLoss(totalMemberProfitLoss);
        result.setNetProfit(totalNetProfit);
        result.setSiteVipBenefits(totalSiteVipBenefits);
        result.setSiteUsedOffers(totalSiteUsedOffers);
        result.setSitePromotionalOffers(totalSitePromotionalOffers);
        result.setSiteOtherAdjustments(totalSiteOtherAdjustments);
        // 打赏金额
        result.setTipsAmount(tipsAmount);
        // 调整金额(其他调整)-平台币
        result.setPlatAdjustAmount(platAdjustAmount);
        // 封控金额-主货币
        result.setRiskAmount(riskAmount);

        return result;
    }

    private void clearOldData(Long startTime, Long endTime, String siteCode) {
        LambdaQueryWrapper<SiteStatisticsPO> del = Wrappers.lambdaQuery();
        del.ge(SiteStatisticsPO::getCreatedTime, startTime)
                .le(SiteStatisticsPO::getCreatedTime, endTime);
        if (StringUtils.isNotBlank(siteCode)) {
            del.eq(SiteStatisticsPO::getSiteCode, siteCode);
        }
        this.remove(del);
        log.info("重跑数据");
    }

    /**
     * 同步数据
     *
     * @param dataVO 请求参数
     * @return true
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> syncData(SiteReportSyncDataVO dataVO) {
        log.info("开始执行同步站点报表数据====>当前参数:{}", JSON.toJSONString(dataVO));
        List<SiteStatisticsPO> saveList = new ArrayList<>();
        Long startTime = dataVO.getStartTime();
        Long endTime = dataVO.getEndTime();
        String paramSiteCode = dataVO.getSiteCode();
        List<SiteVO> data = siteApi.siteInfoAllstauts().getData();
        //每次执行,按照当前时间,先删除数据后,再添加进来
        clearOldData(startTime, endTime, paramSiteCode);

        Map<String, SiteVO> siteMap = data.stream()
                .collect(Collectors.toMap(SiteVO::getSiteCode, site -> site));
        List<String> siteCodes = data.stream().map(SiteVO::getSiteCode).toList();

        //如果传入siteCode,则只需要同步某个站点的数据
        if (StringUtils.isNotBlank(paramSiteCode)) {
            siteCodes = new ArrayList<>();
            siteCodes.add(paramSiteCode);
        }

        if (CollectionUtil.isNotEmpty(siteCodes)) {
            log.info("开始同步站点数据,当前需要同步数据的站点:{}", JSON.toJSONString(siteCodes));
            Map<String, List<SiteCurrencyInfoRespVO>> currencyMap = currencyInfoApi.getCurrencyBySiteCodes(siteCodes);
            //站点,币种,会员分组
            Map<String, Map<String, List<UserInfoVO>>> userGroupMap = userInfoApi.getUserListGroupSiteCode(startTime,endTime, paramSiteCode);
            //会员盈亏信息
            Map<String, Map<String, List<UserWinLoseResponseVO>>> userWLMap = winLoseService.selectGroup(startTime, endTime, paramSiteCode);
            //会员存取信息
            LambdaQueryWrapper<ReportUserRechargeWithdrawPO> query = Wrappers.lambdaQuery();
            query.ge(ReportUserRechargeWithdrawPO::getDayHourMillis, startTime).lt(ReportUserRechargeWithdrawPO::getDayHourMillis, endTime);
            query.eq(ReportUserRechargeWithdrawPO::getAccountType, UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
            if (StringUtils.isNotBlank(paramSiteCode)) {
                query.eq(ReportUserRechargeWithdrawPO::getSiteCode, paramSiteCode);
            }
            List<ReportUserRechargeWithdrawPO> reportUserRechargeWithdrawPOS = userRechargeWithdrawRepository.selectList(query);

            long daysDifference = (endTime - startTime) / (1000 * 60 * 60);
            for (long i = 0; i <= daysDifference; i++) {
                long hourStart = startTime + (i * 3600 * 1000);
                long hourEnd = hourStart + (3600 * 1000) - 1000;
                //组装数据
                for (String siteCode : siteCodes) {
                    SiteVO siteVO = siteMap.get(siteCode);
                    String company = siteVO.getCompany();
                    String siteName = siteVO.getSiteName();
                    Integer siteType = siteVO.getSiteType();
                    Map<String, Map<String, List<ReportUserRechargeWithdrawPO>>> userDwMap = new HashMap<>();
                    if (CollectionUtil.isNotEmpty(reportUserRechargeWithdrawPOS)) {
                        userDwMap = reportUserRechargeWithdrawPOS.stream()
                                .collect(Collectors.groupingBy(
                                        ReportUserRechargeWithdrawPO::getSiteCode,
                                        Collectors.groupingBy(ReportUserRechargeWithdrawPO::getCurrency)
                                ));
                    }
                    //组装会员信息
                    Map<String, List<UserInfoVO>> currencyUserListMap = new HashMap<>();
                    if (currencyMap != null && currencyMap.containsKey(siteCode)) {
                        if (CollectionUtil.isNotEmpty(userGroupMap)) {
                            currencyUserListMap = userGroupMap.get(siteCode);
                        }
                        //站点必须要有币种才能统计
                        List<SiteCurrencyInfoRespVO> siteCurrencyList = currencyMap.get(siteCode);
                        for (SiteCurrencyInfoRespVO currency : siteCurrencyList) {
                            String currencyCode = currency.getCurrencyCode();
                                SiteStatisticsPO po = new SiteStatisticsPO();
                                po.setCreatedTime(hourStart);
                                po.setUpdatedTime(hourEnd);
                                po.setCreator("System");
                                po.setUpdater("System");
                                po.setSiteCode(siteCode);
                                po.setSiteName(siteName);
                                po.setCompanyName(company);
                                po.setSiteType(siteType);
                                po.setCurrencyCode(currencyCode);
                                po.setCurrentFinalRate(currency.getFinalRate());
                                //统计站点 对应 币种 对应会员
                                boolean isUpdUser = setUserData(currencyUserListMap, currencyCode, po, hourStart, hourEnd);
                                //组装存取款信息
                                Map<String, List<ReportUserRechargeWithdrawPO>> currencyDeWiMap = userDwMap.get(siteCode);
                                //计算存取款数据
                                boolean isUpdDeWi = setDeWiData(currencyDeWiMap, currencyCode, po, hourStart, hourEnd);
                                //会员盈亏数据
                                Map<String, List<UserWinLoseResponseVO>> currencyWinLoseMap = userWLMap.get(siteCode);
                                boolean isUpdWL = setWinLoseData(currencyWinLoseMap, currencyCode, po, hourStart, hourEnd);
                                //至少有一种数据有值,才去入库
                                saveList.add(po);
                        }
                    }
                }

            }

        }

        if (CollectionUtil.isNotEmpty(saveList)) {
            log.info("同步平台报表完成,开始时间===>:{},结束时间===>:{},存在需要入库的记录:{}条", startTime, endTime, saveList.size());
            this.saveBatch(saveList);
        } else {
            log.info("同步平台报表完成,开始时间===>:{},结束时间===>:{},没有入库的记录", startTime, endTime);
        }

        return ResponseVO.success();
    }


    /**
     * 计算会员盈亏
     *
     * @param currencyWinLoseMap 币种盈亏map
     * @param currencyCode       币种
     * @param po                 po
     */
    private boolean setWinLoseData(Map<String, List<UserWinLoseResponseVO>> currencyWinLoseMap,
                                   String currencyCode,
                                   SiteStatisticsPO po,
                                   Long startTime,
                                   Long endTime) {
        boolean result = false;
        //VIP福利
        BigDecimal siteVipBenefits = BigDecimal.ZERO;
        //活动优惠
        BigDecimal sitePromotionalOffers = BigDecimal.ZERO;
        //已使用优惠
        BigDecimal siteUsedOffers = BigDecimal.ZERO;
        //其他调整
        BigDecimal siteOtherAdjustments = BigDecimal.ZERO;
        //注单量
        int betCount = 0;
        //投注金额
        BigDecimal bettingAmount = BigDecimal.ZERO;
        //有效投注
        BigDecimal validBetting = BigDecimal.ZERO;
        //平台输赢
        BigDecimal memberProfitLoss = BigDecimal.ZERO;
        //净盈利
        BigDecimal netProfit = BigDecimal.ZERO;
        //调整金额(其他调整)-平台币
        BigDecimal platAdjustAmount = BigDecimal.ZERO;
        //打赏金额
        BigDecimal tipsAmount = BigDecimal.ZERO;
        //封控金额-主货币
        BigDecimal riskAmount = BigDecimal.ZERO;


        if (CollectionUtil.isNotEmpty(currencyWinLoseMap)) {
            List<UserWinLoseResponseVO> winLoseList = currencyWinLoseMap.get(currencyCode);
            if (CollectionUtil.isNotEmpty(winLoseList)) {
                //根据时间再分一下
                winLoseList = winLoseList.stream().filter(item -> item.getDayHourMillis() >= startTime && item.getDayHourMillis() <= endTime).toList();
            }

            if (CollectionUtil.isNotEmpty(winLoseList)) {
                for (UserWinLoseResponseVO winLoseVo : winLoseList) {
                    //会员福利
                    siteVipBenefits = siteVipBenefits.add(winLoseVo.getVipAmount());
                    //活动优惠
                    sitePromotionalOffers = sitePromotionalOffers.add(winLoseVo.getActivityAmount());
                    siteUsedOffers = siteUsedOffers.add(winLoseVo.getAlreadyUseAmount());
                    //人工加减额调整金额
                    siteOtherAdjustments = siteOtherAdjustments.add(winLoseVo.getAdjustAmount());
                    betCount += winLoseVo.getBetNum();
                    bettingAmount = bettingAmount.add(winLoseVo.getBetAmount());
                    validBetting = validBetting.add(winLoseVo.getValidBetAmount());

                    //memberProfitLoss = memberProfitLoss.add(winLoseVo.getBetWinLose());
                    BigDecimal betWinLose = winLoseVo.getBetWinLose();
                    log.info("当前会员盈亏:{}", betWinLose);
                    BigDecimal alreadyUseAmount = winLoseVo.getAlreadyUseAmount();
                    log.info("已使用优惠:{}", alreadyUseAmount);
                    //平台输赢就是会员盈利取反
                    memberProfitLoss = memberProfitLoss.add(betWinLose.negate());
                    //调整金额(其他调整)-平台币
                     platAdjustAmount = platAdjustAmount.add(winLoseVo.getPlatAdjustAmount());
                    //打赏金额
                     tipsAmount = tipsAmount.add(winLoseVo.getTipsAmount());
                    //封控金额-主货币
                     riskAmount = riskAmount.add(winLoseVo.getRiskAmount());
                }
//                // 计算净盈利=平台输赢-已使用优惠
//                netProfit = memberProfitLoss.subtract(siteUsedOffers);
//                更改为 平台净盈利 = 用户投注输赢+打赏金额-已使用优惠-人工加减额的其他调整）
                  netProfit = memberProfitLoss.add(tipsAmount).subtract(siteUsedOffers).subtract(siteOtherAdjustments);
            }
            if (!siteVipBenefits.equals(BigDecimal.ZERO)
                    || !sitePromotionalOffers.equals(BigDecimal.ZERO)
                    || !siteUsedOffers.equals(BigDecimal.ZERO)
                    || !siteOtherAdjustments.equals(BigDecimal.ZERO)
                    || !bettingAmount.equals(BigDecimal.ZERO)
                    || !validBetting.equals(BigDecimal.ZERO)
                    || !memberProfitLoss.equals(BigDecimal.ZERO)
                    || !netProfit.equals(BigDecimal.ZERO)
                    || !platAdjustAmount.equals(BigDecimal.ZERO)
                    || !tipsAmount.equals(BigDecimal.ZERO)
                    || !riskAmount.equals(BigDecimal.ZERO)) {
                //只要有任何一种盈亏数据发生了变化,才去入库
                result = true;
            }
        }
        po.setSiteVipBenefits(siteVipBenefits);
        po.setSitePromotionalOffers(sitePromotionalOffers);
        po.setSiteUsedOffers(siteUsedOffers);
        po.setSiteOtherAdjustments(siteOtherAdjustments);
        po.setBetCount(betCount);
        po.setBettingAmount(bettingAmount);
        po.setValidBetting(validBetting);
        po.setMemberProfitLoss(memberProfitLoss);
        po.setNetProfit(netProfit);
        po.setPlatAdjustAmount(platAdjustAmount);
        po.setTipsAmount(tipsAmount);
        po.setRiskAmount(riskAmount);
        return result;
    }

    /**
     * 统计会员相关数据
     *
     * @param currencyUserListMap 当前站点对应所有币种所有会员数据
     * @param currencyCode        币种
     * @param po                  po
     * @param startTime           时间范围,用来筛选某个时间段内的会员
     * @param endTime             时间范围,用来筛选某个时间段内的会员
     */
    private boolean setUserData(Map<String, List<UserInfoVO>> currencyUserListMap,
                                String currencyCode,
                                SiteStatisticsPO po,
                                Long startTime,
                                Long endTime) {
        boolean result = false;
        if (CollectionUtil.isNotEmpty(currencyUserListMap) && currencyUserListMap.containsKey(currencyCode)) {
            List<UserInfoVO> userInfoVOS = currencyUserListMap.get(currencyCode);
            int newMembers = 0;
            int firstDepositCount = 0;
            BigDecimal firstDepositAmount = BigDecimal.ZERO;
            for (UserInfoVO item : userInfoVOS) {
                //新增会员人数-过滤注册时间之后的数据
                if (item.getRegisterTime() != null
                        && item.getRegisterTime() >= startTime
                        && item.getRegisterTime() < endTime) {
                    newMembers++;
                }
                //首存人数-根据首存时间过滤
                //首存金额-满足时间后获取
                if (item.getFirstDepositTime() != null
                        && item.getFirstDepositTime() >= startTime
                        && item.getFirstDepositTime() <= endTime) {
                    log.info("当前会员:{},首存时间:{}", item.getUserAccount(), item.getFirstDepositTime());
                    firstDepositCount++; // 累加首存人数
                    firstDepositAmount = firstDepositAmount.add(item.getFirstDepositAmount() != null ? item.getFirstDepositAmount() : BigDecimal.ZERO);
                }
            }
            if (newMembers != 0) {
                result = true;
            }
            //新增会员数
            po.setNewMembers(newMembers);
            //首存人
            po.setFirstDepositCount(firstDepositCount);
            //首存金额
            po.setFirstDepositAmount(firstDepositAmount);

        }
        return result;
    }

    /**
     * 计算存取款信息
     *
     * @param currencyDeWiMap 当前站点对应所有币种对应存取款信息
     * @param currencyCode    当前币种
     * @param po              po
     */
    private boolean setDeWiData(Map<String, List<ReportUserRechargeWithdrawPO>> currencyDeWiMap,
                                String currencyCode,
                                SiteStatisticsPO po,
                                Long startTime,
                                Long endTime) {
        boolean result = false;
        if (CollectionUtil.isNotEmpty(currencyDeWiMap)) {
            List<ReportUserRechargeWithdrawPO> dewiList = currencyDeWiMap.get(currencyCode);
            if (CollectionUtil.isNotEmpty(dewiList)) {
                //存款金额
                BigDecimal depositAmount = BigDecimal.ZERO;
                //存款次数
                int depositCount = 0;
                //取款金额
                BigDecimal withdrawalAmount = BigDecimal.ZERO;
                //取款次数
                int withdrawalCount = 0;
                //大额取款次数
                int largeWithdrawalCount = 0;
                //存取差
                BigDecimal depositWithdrawalDifference;

                //根据存取款类型分组
                Map<String, List<ReportUserRechargeWithdrawPO>> dewiTypeMap = dewiList.stream()
                        .collect(Collectors.groupingBy(ReportUserRechargeWithdrawPO::getType));
                //存款类型数据
                List<ReportUserRechargeWithdrawPO> deList = dewiTypeMap.get(String.valueOf(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode()));
                if (CollectionUtil.isNotEmpty(deList)) {
                    //按照具体小时数再筛一份
                    deList = deList.stream().filter(item -> item.getDayHourMillis() >= startTime && item.getDayHourMillis() < endTime).toList();
                    if (CollectionUtil.isNotEmpty(deList)) {
                        for (ReportUserRechargeWithdrawPO withdrawPO : deList) {
                            depositCount += withdrawPO.getNums();
                            //累加金额
                            depositAmount = depositAmount.add(withdrawPO.getAmount());
                        }
                    }
                }

                //取款类型数据
                List<ReportUserRechargeWithdrawPO> wiList = dewiTypeMap.get(String.valueOf(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode()));
                if (CollectionUtil.isNotEmpty(wiList)) {
                    //按照具体小时数再筛一份
                    wiList = wiList.stream().filter(item -> item.getDayHourMillis() >= startTime && item.getDayHourMillis() <= endTime).toList();
                    if (CollectionUtil.isNotEmpty(wiList)) {
                        for (ReportUserRechargeWithdrawPO withdrawPO : wiList) {
                            withdrawalCount += withdrawPO.getNums();
                            largeWithdrawalCount += withdrawPO.getLargeNums();
                            withdrawalAmount = withdrawalAmount.add(withdrawPO.getAmount());
                        }
                    }
                }

                //要有存取款操作数据变化了,再去入库
                if (!depositAmount.equals(BigDecimal.ZERO) ||
                        !withdrawalAmount.equals(BigDecimal.ZERO)) {
                    result = true;
                }
                //计算存取差
                depositWithdrawalDifference = depositAmount.subtract(withdrawalAmount);

                po.setDepositAmount(depositAmount);
                po.setDepositCount(depositCount);
                po.setWithdrawalAmount(withdrawalAmount);
                po.setWithdrawalCount(withdrawalCount);
                po.setLargeWithdrawalCount(largeWithdrawalCount);
                po.setDepositWithdrawalDifference(depositWithdrawalDifference);
            }
        }
        return result;
    }

    public Long getTotal(SiteReportStatisticsQueryPageQueryVO vo) {
        return reportRepository.getTotal(vo);
    }

    public ResponseVO<Page<SiteStatisticsVO>> getPage(SiteReportStatisticsQueryPageQueryVO queryVO) {
        Page<SiteStatisticsVO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        page = reportRepository.getSiteReportPage(page, queryVO);
        List<SiteStatisticsVO> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            if (queryVO.getIsPlantShow()) {
                processPlantShow(records);
            }
            processTotalMember(records, queryVO.getCurrencyCode());
            processBetUserCount(records, queryVO.getStartTime(), queryVO.getEndTime());
        }

        return ResponseVO.success(page);
    }
}
