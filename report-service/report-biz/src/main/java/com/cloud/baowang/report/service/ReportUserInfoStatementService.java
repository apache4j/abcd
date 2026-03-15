package com.cloud.baowang.report.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.UserTransferAgentApi;
import com.cloud.baowang.agent.api.enums.AgentAttributionEnum;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoPartVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.member.ReportUserTransferReqVO;
import com.cloud.baowang.agent.api.vo.member.ReportUserTransferRespVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.report.StatementVO;
import com.cloud.baowang.report.api.vo.*;
import com.cloud.baowang.report.api.vo.userwinlose.GetUserLabelByIdsResVO;
import com.cloud.baowang.report.po.ReportUserInfoStatementPO;
import com.cloud.baowang.report.repositories.ReportUserInfoStatementRepository;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.site.SiteRequestVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.report.DepositWithdrawalVO;
import com.cloud.baowang.wallet.api.vo.report.user.UserInfoStatementVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ReportUserInfoStatementService extends ServiceImpl<ReportUserInfoStatementRepository, ReportUserInfoStatementPO> {

    private final AgentInfoApi agentInfoApi;


    private final RiskApi riskApi;


    private final SiteUserLabelConfigApi siteUserLabelConfigApi;

    private final VipGradeApi vipGradeApi;

    private final VipRankApi vipRankApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final ReportUserWinLoseService reportUserWinLoseService;

    private final SiteApi siteApi;

    private final ReportUserRechargeService reportUserRechargeService;


    private final UserTransferAgentApi userTransferAgentApi;


    /**
     * 会员报表列表
     *
     * @param vo 入参
     * @return long 返回数量
     */
    public ResponseVO<Long> reportUserCount(ReportUserInfoStatementPageVO vo) {
        try {
            if (ObjectUtil.isNotEmpty(vo.getStatisticalDateStart()) && ObjectUtil.isNotEmpty(vo.getStatisticalDateEnd())) {

                int between = TimeZoneUtils.getDaysBetweenInclusive(vo.getStatisticalDateStart(), vo.getStatisticalDateEnd(), vo.getTimeZone());
                if (between > 31) {
                    return ResponseVO.fail(ResultCode.DATE_MAX_SPAN_92);
                }
            }
            if (ObjectUtil.isNotEmpty(vo.getRegisterTimeStart()) && ObjectUtil.isNotEmpty(vo.getRegisterTimeEnd())) {
                int between = TimeZoneUtils.getDaysBetweenInclusive(vo.getRegisterTimeStart(), vo.getRegisterTimeEnd(), vo.getTimeZone());
                if (between > 31) {
                    return ResponseVO.fail(ResultCode.DATE_MAX_SPAN_92);
                }
            }
            Long totalCount = baseMapper.findTotalCount(vo);
            return ResponseVO.success(totalCount);
        } catch (Exception e) {
            log.error("会员报表展示错误：", e);
            throw new BaowangDefaultException(ResultCode.SERVER_INTERNAL_ERROR);
        }
    }


    /**
     * 会员报表列表
     *
     * @param vo 入参
     * @return 返回结果
     */
    public ResponseVO<UserInfoStatementResponseVO> pageList(ReportUserInfoStatementPageVO vo) {
        // 构建查询条件
        try {
            String siteCode = vo.getSiteCode();
            if (ObjectUtil.isNotEmpty(vo.getStatisticalDateStart()) && ObjectUtil.isNotEmpty(vo.getStatisticalDateEnd())) {
                int between = TimeZoneUtils.getDaysBetweenInclusive(vo.getStatisticalDateStart(), vo.getStatisticalDateEnd(), vo.getTimeZone());
                if (between > 31) {
                    return ResponseVO.fail(ResultCode.DATE_MAX_SPAN_31);
                }
            }
            if (ObjectUtil.isNotEmpty(vo.getRegisterTimeStart()) && ObjectUtil.isNotEmpty(vo.getRegisterTimeEnd())) {
                int between = TimeZoneUtils.getDaysBetweenInclusive(vo.getRegisterTimeStart(), vo.getRegisterTimeEnd(), vo.getTimeZone());
                if (between > 31) {
                    return ResponseVO.fail(ResultCode.DATE_MAX_SPAN_31);
                }
            }
            Page<ReportUserInfoStatementPO> reportUserInfoStatementPOPage = new Page<>(vo.getPageNumber(), vo.getPageSize());
            UserInfoStatementResponseVO statementVO = new UserInfoStatementResponseVO();
            Page<ReportUserInfoStatementVO> pageList = baseMapper.findPageList(reportUserInfoStatementPOPage, vo);


            List<ReportUserInfoStatementVO> arrayList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(pageList.getRecords())) {
                //
                Map<String, Map<String, BigDecimal>> allFinalRate = new HashMap<>();
                if (StringUtils.isBlank(vo.getSiteCode())) {
                    allFinalRate = siteCurrencyInfoApi.getAllPlateFinalRate(); // 获取所有币种汇率
                } else {
                    Map<String, BigDecimal> allFinalRate1 = siteCurrencyInfoApi.getAllFinalRate(siteCode);
                    allFinalRate.put(siteCode, allFinalRate1);
                }
                // 查询siteName
                Map<String, String> siteCodeMap = new HashMap<>();
                // 需要查询所有的站点或者指定的站点
                SiteRequestVO siteRequestVO = new SiteRequestVO();
                siteRequestVO.setPageSize(10000);
                ResponseVO<Page<SiteVO>> responseVO = siteApi.querySiteInfo(siteRequestVO);
                List<SiteVO> siteVOS = responseVO.getData().getRecords();
                if (CollUtil.isNotEmpty(siteVOS)) {
                    siteCodeMap = siteVOS.stream().filter(siteVO -> StringUtils.isNotBlank(siteVO.getSiteCode())).collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO::getSiteName));
                }


                ReportUserTransferReqVO reportUserTransferReqVO = ReportUserTransferReqVO.builder().startTime(vo.getStatisticalDateStart()).endTime(vo.getStatisticalDateEnd()).siteCode(siteCode).build();
                List<ReportUserTransferRespVO> respVOS = userTransferAgentApi.queryUserTransferCount(reportUserTransferReqVO);
                // 转代次数 Map<siteCode, Map<userId, Map<agentId, userTransferCount>>>
                Map<String, Map<String, Map<String, Integer>>> transferMap = Optional.ofNullable(respVOS)
                        .filter(CollectionUtil::isNotEmpty) // 过滤空集合
                        .orElse(Collections.emptyList()) // 如果为空，则返回空集合
                        .stream()
                        .collect(Collectors.groupingBy(
                                ReportUserTransferRespVO::getSiteCode, // 按照 siteCode 分组
                                Collectors.groupingBy(
                                        ReportUserTransferRespVO::getUserId, // 按照 userId 分组
                                        Collectors.toMap(
                                                ReportUserTransferRespVO::getAgentId, // 以 agentId 为 key
                                                ReportUserTransferRespVO::getUserTransferCount, // 以 userTransferCount 为 value
                                                (existing, replacement) -> existing // 如果有重复的 key（即 agentId 相同），保留原值
                                        )
                                )
                        ));


                // 查询vip等级
                Map<String, Map<Integer, String>> vipGradeMap = new HashMap<>();
                if (StringUtils.isBlank(vo.getSiteCode())) {

                    Map<String, List<SiteVIPGradeVO>> allSiteVipGrade = vipGradeApi.getAllSiteVipGrade();
                    if (CollectionUtil.isNotEmpty(allSiteVipGrade)) {
                        for (Map.Entry<String, List<SiteVIPGradeVO>> entry : allSiteVipGrade.entrySet()) {
                            String key = entry.getKey();
                            List<SiteVIPGradeVO> value = entry.getValue();
                            if (CollectionUtil.isNotEmpty(value)) {
                                Map<Integer, String> vipGradeMapSiteT = value.stream().filter(vip -> vip.getVipGradeCode() != null && StringUtils.isNotBlank(vip.getVipGradeName())).collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipGradeName, (k1, k2) -> k2));
                                vipGradeMap.put(key, vipGradeMapSiteT);
                            }
                        }
                    }

                } else {
                    List<SiteVIPGradeVO> siteVIPGradeVOS = vipGradeApi.queryAllVIPGrade(siteCode);

                    Map<Integer, String> vipGradeMapSite;
                    if (CollectionUtil.isNotEmpty(siteVIPGradeVOS)) {
                        vipGradeMapSite = siteVIPGradeVOS.stream().filter(vip -> vip.getVipGradeCode() != null && StringUtils.isNotBlank(vip.getVipGradeName())).collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipGradeName, (k1, k2) -> k2));
                        vipGradeMap.put(siteCode, vipGradeMapSite);
                    }

                }
                // 查询vip Rank
                Map<String, Map<Integer, String>> vipRankMap = new HashMap<>();
                if (StringUtils.isBlank(vo.getSiteCode())) {
                    Map<String, List<SiteVIPRankVO>> allSiteVipRank = vipRankApi.getAllSiteVipRank();
                    for (Map.Entry<String, List<SiteVIPRankVO>> entry : allSiteVipRank.entrySet()) {
                        String key = entry.getKey();
                        List<SiteVIPRankVO> value = entry.getValue();
                        if (CollectionUtil.isNotEmpty(value)) {
                            Map<Integer, String> vipRankMapS = value.stream().filter(vip -> vip.getVipRankCode() != null && StringUtils.isNotBlank(vip.getVipRankNameI18nCode()) && StringUtils.isNotBlank(vip.getVipGradeCodes())) // Corrected: ensure VipRankCode is not null (since it's an Integer)
                                    .collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode, SiteVIPRankVO::getVipRankNameI18nCode, (k1, k2) -> k2));
                            vipRankMap.put(key, vipRankMapS);
                        }
                    }

                } else {
                    ResponseVO<List<SiteVIPRankVO>> siteVIPRankVOSResponse = vipRankApi.getVipRankListBySiteCode(siteCode);
                    if (siteVIPRankVOSResponse.isOk() && CollectionUtil.isNotEmpty(siteVIPRankVOSResponse.getData())) {
                        Map<Integer, String> vipRankMapS = siteVIPRankVOSResponse.getData().stream().filter(vip -> vip.getVipRankCode() != null && StringUtils.isNotBlank(vip.getVipRankNameI18nCode()) && StringUtils.isNotBlank(vip.getVipGradeCodes())) // Corrected: ensure VipRankCode is not null (since it's an Integer)
                                .collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode, SiteVIPRankVO::getVipRankNameI18nCode, (k1, k2) -> k2));
                        vipRankMap.put(vo.getSiteCode(), vipRankMapS);

                    }
                }
                RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
                riskLevelDownReqVO.setRiskControlType(RiskTypeEnum.RISK_MEMBER.getCode());
                riskLevelDownReqVO.setSiteCode(vo.getSiteCode());
                // 风险会员风控层级
                // 风控层级
                Set<String> riskIds = pageList.getRecords().stream().map(ReportUserInfoStatementVO::getRiskLevelId).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
                Map<String, RiskLevelDetailsVO> riskLevelDetailsVOMap = riskApi.getByIds(new ArrayList<>(riskIds));

                // 会员标签
                Set<String> labelIds = pageList.getRecords().stream().filter(record -> StrUtil.isNotEmpty(record.getUserLabelId())).flatMap(record -> Arrays.stream(record.getUserLabelId().split(CommonConstant.COMMA))).collect(Collectors.toSet());

                List<GetUserLabelByIdsVO> userLabels = siteUserLabelConfigApi.getUserLabelByIds(new ArrayList<>(labelIds));
                Map<String, GetUserLabelByIdsResVO> userLabelMap = new HashMap<>();
                if (CollectionUtil.isNotEmpty(userLabels)) {
                    userLabelMap = userLabels.stream()
                            .collect(Collectors.toMap(GetUserLabelByIdsVO::getId, tempVo -> ConvertUtil.entityToModel(tempVo, GetUserLabelByIdsResVO.class), (K1, K2) -> K2));
                }
                Boolean convertPlat = vo.getConvertPlatCurrency(); // 是否转换为平台币
                for (ReportUserInfoStatementVO record : pageList.getRecords()) {
                    ReportUserInfoStatementVO reportUserInfoStatementVO = new ReportUserInfoStatementVO();
                    // 取反 以公司视角展示
                    //record.setBettingProfitLoss(Objects.nonNull(record.getBettingProfitLoss()) ? record.getBettingProfitLoss().negate() : BigDecimal.ZERO);
                    BeanUtils.copyProperties(record, reportUserInfoStatementVO);
                    reportUserInfoStatementVO.setTransAgentTime(null);
                    // 会员标签
                    if (ObjectUtil.isNotEmpty(record.getUserLabelId())) {
                        // 直接通过流进行 split 和非空检查
                        List<GetUserLabelByIdsResVO> userLabelList = Arrays.stream(record.getUserLabelId().split(CommonConstant.COMMA))
                                .filter(StrUtil::isNotEmpty)// 过滤掉空的标签ID
                                .map(userLabelMap::get)// 映射到标签名称
                                .filter(Objects::nonNull)// 过滤掉映射为空的情况
                                .map(label -> {
                                    GetUserLabelByIdsResVO temp = new GetUserLabelByIdsResVO();
                                    temp.setLabelName("#" + label.getLabelName()); // 修改标签名称，添加 #
                                    temp.setColor(label.getColor());
                                    return temp;
                                })
                                .toList(); // 收集结果到列表中
                        if (CollUtil.isNotEmpty(userLabelList)) {
                            reportUserInfoStatementVO.setUserLabelIds(userLabelList);
                            String str = userLabelList.stream()
                                    .map(GetUserLabelByIdsResVO::getLabelName)
                                    .collect(Collectors.joining(","));
                            reportUserInfoStatementVO.setUserLabelName(str);


                        }

                    }

                    // 会员风控层级
                    if (ObjectUtil.isNotEmpty(record.getRiskLevelId())) {
                        RiskLevelDetailsVO riskLevelDetailsVO = riskLevelDetailsVOMap.get(record.getRiskLevelId());
                        reportUserInfoStatementVO.setRiskLevelName(riskLevelDetailsVO == null ? "" : riskLevelDetailsVO.getRiskControlLevel());
                    }

                    // 代理归属
                    if (null != record.getAgentAttribution()) {
                        AgentAttributionEnum agentAttributionEnum = AgentAttributionEnum.nameOfCode(record.getAgentAttribution());
                        reportUserInfoStatementVO.setAgentAttributionName(null == agentAttributionEnum ? null : agentAttributionEnum.getName());
                    }
                    if (ObjectUtils.isNotEmpty(reportUserInfoStatementVO.getVipGradeCode())) {
                        Map<Integer, String> integerStringMap = vipGradeMap.get(record.getSiteCode());
                        if (CollectionUtil.isNotEmpty(integerStringMap)) {
                            reportUserInfoStatementVO.setVipGradeCodeName(integerStringMap.get(reportUserInfoStatementVO.getVipGradeCode()));
                        }

                    }
                    if (ObjectUtils.isNotEmpty(reportUserInfoStatementVO.getVipRankCode())) {
                        Map<Integer, String> integerStringMap = vipRankMap.get(record.getSiteCode());
                        if (CollectionUtil.isNotEmpty(integerStringMap)) {
                            reportUserInfoStatementVO.setVipRankCodeName(integerStringMap.get(reportUserInfoStatementVO.getVipRankCode()));
                        }

                    }
                    // 站点名称
                    reportUserInfoStatementVO.setSiteCodeName(siteCodeMap.get(record.getSiteCode()));
                    // 设置转代次数
                    if (reportUserInfoStatementVO.getSuperAgentId() != null && CollectionUtil.isNotEmpty(transferMap.get(reportUserInfoStatementVO.getSiteCode()))
                            && CollectionUtil.isNotEmpty(transferMap.get(reportUserInfoStatementVO.getSiteCode()).get(reportUserInfoStatementVO.getUserAccount()))) {
                        Integer transferTime = transferMap.get(reportUserInfoStatementVO.getSiteCode()).get(reportUserInfoStatementVO.getUserAccount()).get(reportUserInfoStatementVO.getSuperAgentId());
                        if (transferTime != null) {
                            reportUserInfoStatementVO.setTransAgentTime(transferTime);
                        } else {
                            reportUserInfoStatementVO.setTransAgentTime(0);
                        }
                    } else {
                        reportUserInfoStatementVO.setTransAgentTime(0);
                    }

                    //
                    BigDecimal rate = getRateByRecord(convertPlat, allFinalRate, record);
                    if (vo.getConvertPlatCurrency()) {
                        // 首存金额
                        reportUserInfoStatementVO.setFirstDepositAmount(record.getFirstDepositAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 总存款
                        reportUserInfoStatementVO.setTotalDeposit(record.getTotalDeposit().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 上级转入
                        reportUserInfoStatementVO.setAdvancedTransfer(record.getAdvancedTransfer().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 总取款
                        reportUserInfoStatementVO.setTotalWithdrawal(record.getTotalWithdrawal().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 存取差
                        reportUserInfoStatementVO.setPoorAccess(record.getPoorAccess().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // VIP福利 活动优惠
                        reportUserInfoStatementVO.setVipAmount(record.getVipAmount().setScale(4, RoundingMode.DOWN));
                        reportUserInfoStatementVO.setActivityAmount(record.getActivityAmount().setScale(4, RoundingMode.DOWN));
                        // 返水金额 是主货币
                        reportUserInfoStatementVO.setRebateAmount(record.getRebateAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 已使用优惠
                        reportUserInfoStatementVO.setAlreadyUseAmount(record.getAlreadyUseAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 其他调整
                        reportUserInfoStatementVO.setOtherAdjustments(record.getOtherAdjustments().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 投注金额
                        reportUserInfoStatementVO.setBetAmount(record.getBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 有效投注
                        reportUserInfoStatementVO.setActiveBet(record.getActiveBet().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 会员输赢
                        reportUserInfoStatementVO.setBettingProfitLoss(record.getBettingProfitLoss().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        // 会员净盈利
                        reportUserInfoStatementVO.setTotalPreference(record.getTotalPreference().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        reportUserInfoStatementVO.setAmountLargeDeposits(record.getAmountLargeDeposits().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        reportUserInfoStatementVO.setAmountLargeWithdrawal(record.getAmountLargeWithdrawal().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        //reportUserInfoStatementVO.setMainCurrency(vo.getPlatCurrencyCode());
                        // 会员主货币转换
                        //log.info("转换前： "+record.getTotalPlatAmount());
                        reportUserInfoStatementVO.setCenterAmount(record.getCenterAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        //log.info("转换后： "+reportUserInfoStatementVO.getTotalPlatAmount());
                        //
                        reportUserInfoStatementVO.setPlatAdjustAmount(record.getPlatAdjustAmount().setScale(4, RoundingMode.DOWN));
                        reportUserInfoStatementVO.setTipsAmount(record.getTipsAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                        reportUserInfoStatementVO.setRiskAmount(record.getRiskAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));


                    }


                    reportUserInfoStatementVO.setPlatCurrencyCode(vo.getPlatCurrencyCode());
                    reportUserInfoStatementVO.setConvertPlatCurrency(vo.getConvertPlatCurrency());

                    arrayList.add(reportUserInfoStatementVO);
                }
                if (!vo.getExportFlag()) {
                    //获取总计
                    List<ReportUserInfoStatementVO> reportUserInfoStatementList = baseMapper.getReportUserInfoStatementList(vo);
                    // 取反 以公司视角展示
                    //reportUserInfoStatementList.forEach(s -> s.setBettingProfitLoss(Objects.nonNull(s.getBettingProfitLoss()) ? s.getBettingProfitLoss().negate() : BigDecimal.ZERO));
                    // 总计
                    total(statementVO, reportUserInfoStatementList, vo, allFinalRate);
                    //小计
                    subtotal(statementVO, pageList, vo, allFinalRate);
                }
            }
            Page<ReportUserInfoStatementVO> page = new Page<>();
            page.setRecords(arrayList);
            page.setCurrent(pageList.getCurrent());
            page.setPages(pageList.getPages());
            page.setSize(pageList.getSize());
            page.setTotal(pageList.getTotal());

            statementVO.setReportUserInfoStatementVOList(page);
            return ResponseVO.success(statementVO);
        } catch (Exception e) {
            log.error("会员报表展示错误：", e);
            throw new BaowangDefaultException("会员报表展示错误");
        }

    }


    /**
     * 总计统计
     *
     * @param statementVO  入参
     * @param pageList     详细每页数据
     * @param allFinalRate 汇率
     */
    private void subtotal(UserInfoStatementResponseVO statementVO, Page<ReportUserInfoStatementVO> pageList, ReportUserInfoStatementPageVO vo, Map<String, Map<String, BigDecimal>> allFinalRate) {
        if (ObjectUtil.isNotEmpty(pageList.getRecords())) {

            ReportUserInfoStatementVO userInfoStatementVO = new ReportUserInfoStatementVO();

            AtomicReference<BigDecimal> firstDepositAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> totalDeposit = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberDeposit = new AtomicReference<>(CommonConstant.business_zero);
            AtomicReference<BigDecimal> advancedTransfer = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberTransfer = new AtomicReference<>(CommonConstant.business_zero);
            // 大额存款次数 与 大额存款总额
            AtomicReference<BigDecimal> amountLargeDeposits = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberLargeDeposits = new AtomicReference<>(CommonConstant.business_zero);

            AtomicReference<BigDecimal> totalWithdrawal = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberWithdrawal = new AtomicReference<>(CommonConstant.business_zero);
            //大额取款次数 与 大额取款总额
            AtomicReference<BigDecimal> amountLargeWithdrawal = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberLargeWithdrawal = new AtomicReference<>(CommonConstant.business_zero);

            AtomicReference<BigDecimal> poorAccess = new AtomicReference<>(BigDecimal.ZERO);

            AtomicReference<BigDecimal> memberLabourAmount = new AtomicReference<>(BigDecimal.ZERO);
            // 返水
            AtomicReference<BigDecimal> rebateAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> otherAdjustments = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> placeOrderQuantity = new AtomicReference<>(CommonConstant.business_zero);
            AtomicReference<BigDecimal> betAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> activeBet = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> bettingProfitLoss = new AtomicReference<>(BigDecimal.ZERO);

            AtomicReference<BigDecimal> activityAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> vipAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> alreadyUseAmount = new AtomicReference<>(BigDecimal.ZERO);
            // 主货币
            AtomicReference<BigDecimal> centerAmount = new AtomicReference<>(BigDecimal.ZERO);
            // 平台币
            AtomicReference<BigDecimal> totalPlatAmount = new AtomicReference<>(BigDecimal.ZERO);

            // 调整金额(其他调整)-平台币
            AtomicReference<BigDecimal> platAdjustAmount = new AtomicReference<>(BigDecimal.ZERO);
            // 打赏金额
            AtomicReference<BigDecimal> tipsAmount = new AtomicReference<>(BigDecimal.ZERO);
            // 风控金额
            AtomicReference<BigDecimal> riskAmount = new AtomicReference<>(BigDecimal.ZERO);


            for (ReportUserInfoStatementVO record : pageList.getRecords()) {

                BigDecimal rate = getRateByRecord(vo.getConvertPlatCurrency(), allFinalRate, record);
                firstDepositAmount.updateAndGet(v -> v.add(record.getFirstDepositAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                totalDeposit.updateAndGet(v -> v.add(record.getTotalDeposit().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                numberDeposit.updateAndGet(v -> v + record.getNumberDeposit());
                advancedTransfer.updateAndGet(v -> v.add(record.getAdvancedTransfer().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                numberTransfer.updateAndGet(v -> v + record.getNumberTransfer());

                // 大额存款总额
                amountLargeDeposits.updateAndGet(v -> v.add(record.getAmountLargeDeposits().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                // 大额存款次数
                numberLargeDeposits.updateAndGet(v -> v + record.getNumberLargeDeposits());


                totalWithdrawal.updateAndGet(v -> v.add(record.getTotalWithdrawal().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                numberWithdrawal.updateAndGet(v -> v + record.getNumberWithdrawal());

                // 大额取款总额
                amountLargeWithdrawal.updateAndGet(v -> v.add(record.getAmountLargeWithdrawal().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                // 大额取款次数
                numberLargeWithdrawal.updateAndGet(v -> v + record.getNumberLargeWithdrawal());

                poorAccess.updateAndGet(v -> v.add(record.getPoorAccess().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                memberLabourAmount.updateAndGet(v -> v.add(record.getTotalPreference().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                //返水
                rebateAmount.updateAndGet(v -> v.add(record.getRebateAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                otherAdjustments.updateAndGet(v -> v.add(record.getOtherAdjustments().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                // 注单量
                placeOrderQuantity.updateAndGet(v -> v + record.getPlaceOrderQuantity());

                betAmount.updateAndGet(v -> v.add(record.getBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                activeBet.updateAndGet(v -> v.add(record.getActiveBet().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                bettingProfitLoss.updateAndGet(v -> v.add(record.getBettingProfitLoss().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                // 活动福利
                activityAmount.updateAndGet(v -> v.add(record.getActivityAmount()));
                // vip
                vipAmount.updateAndGet(v -> v.add(record.getVipAmount()));

                alreadyUseAmount.updateAndGet(v -> v.add(record.getAlreadyUseAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));


                centerAmount.updateAndGet(v -> v.add(record.getCenterAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                totalPlatAmount.updateAndGet(v -> v.add(record.getTotalPlatAmount()));


                platAdjustAmount.updateAndGet(v -> v.add(record.getPlatAdjustAmount()));
                tipsAmount.updateAndGet(v -> v.add(record.getTipsAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                riskAmount.updateAndGet(v -> v.add(record.getRiskAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

            }


            userInfoStatementVO.setFirstDepositAmount(firstDepositAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setTotalDeposit(totalDeposit.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setNumberDeposit(numberDeposit.get());
            userInfoStatementVO.setAdvancedTransfer(advancedTransfer.get().setScale(4, RoundingMode.DOWN));
            //大额存款次数
            userInfoStatementVO.setNumberLargeDeposits(numberLargeDeposits.get());
            userInfoStatementVO.setAmountLargeDeposits(amountLargeDeposits.get().setScale(4, RoundingMode.DOWN));

            userInfoStatementVO.setTotalWithdrawal(totalWithdrawal.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setNumberWithdrawal(numberWithdrawal.get());

            // 大额取款总额
            userInfoStatementVO.setNumberLargeWithdrawal(numberLargeWithdrawal.get());
            userInfoStatementVO.setAmountLargeWithdrawal(amountLargeWithdrawal.get().setScale(4, RoundingMode.DOWN));

            userInfoStatementVO.setPoorAccess(poorAccess.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setTotalPreference(memberLabourAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setRebateAmount(rebateAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setOtherAdjustments(otherAdjustments.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setPlaceOrderQuantity(placeOrderQuantity.get());
            userInfoStatementVO.setBetAmount(betAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setActiveBet(activeBet.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setBettingProfitLoss(bettingProfitLoss.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setActivityAmount(activityAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setVipAmount(vipAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setAlreadyUseAmount(alreadyUseAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setNumberTransfer(numberTransfer.get());
            userInfoStatementVO.setCenterAmount(centerAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setTotalPlatAmount(totalPlatAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setPlatAdjustAmount(platAdjustAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setTipsAmount(tipsAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setRiskAmount(riskAmount.get().setScale(4, RoundingMode.DOWN));
            statementVO.setCurrentPage(userInfoStatementVO);
        }
    }

    private BigDecimal getRateByRecord(Boolean convertPlatCurrency, Map<String, Map<String, BigDecimal>> allFinalRate, ReportUserInfoStatementVO record) {
        if (convertPlatCurrency) {
            if (null == allFinalRate.get(record.getSiteCode()) || null == allFinalRate.get(record.getSiteCode()).get(record.getMainCurrency())) {
                throw new BaowangDefaultException("汇率未配置，货币是:" + record.getMainCurrency() + ",用户是：" + record.getUserId());
            }
            return allFinalRate.get(record.getSiteCode()).get(record.getMainCurrency());
        } else {
            return BigDecimal.ONE;
        }

    }

    /**
     * 总计
     */
    private void total(UserInfoStatementResponseVO statementVO, List<ReportUserInfoStatementVO> listed, ReportUserInfoStatementPageVO vo, Map<String, Map<String, BigDecimal>> allFinalRate) {
        if (ObjectUtil.isNotEmpty(listed)) {

            ReportUserInfoStatementVO userInfoStatementVO = new ReportUserInfoStatementVO();

            AtomicReference<BigDecimal> firstDepositAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> totalDeposit = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberDeposit = new AtomicReference<>(CommonConstant.business_zero);
            AtomicReference<BigDecimal> advancedTransfer = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberTransfer = new AtomicReference<>(CommonConstant.business_zero);
            // 大额存款次数 与 大额存款总额
            AtomicReference<BigDecimal> amountLargeDeposits = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberLargeDeposits = new AtomicReference<>(CommonConstant.business_zero);

            AtomicReference<BigDecimal> totalWithdrawal = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberWithdrawal = new AtomicReference<>(CommonConstant.business_zero);
            //大额取款次数 与 大额取款总额
            AtomicReference<BigDecimal> amountLargeWithdrawal = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> numberLargeWithdrawal = new AtomicReference<>(CommonConstant.business_zero);

            AtomicReference<BigDecimal> poorAccess = new AtomicReference<>(BigDecimal.ZERO);

            AtomicReference<BigDecimal> memberLabourAmount = new AtomicReference<>(BigDecimal.ZERO);

            AtomicReference<BigDecimal> rebateAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> otherAdjustments = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<Integer> placeOrderQuantity = new AtomicReference<>(CommonConstant.business_zero);
            AtomicReference<BigDecimal> betAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> activeBet = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> bettingProfitLoss = new AtomicReference<>(BigDecimal.ZERO);

            AtomicReference<BigDecimal> activityAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> vipAmount = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> alreadyUseAmount = new AtomicReference<>(BigDecimal.ZERO);

            // 主货币
            AtomicReference<BigDecimal> centerAmount = new AtomicReference<>(BigDecimal.ZERO);
            // 平台币
            AtomicReference<BigDecimal> totalPlatAmount = new AtomicReference<>(BigDecimal.ZERO);

            // 调整金额(其他调整)-平台币
            AtomicReference<BigDecimal> platAdjustAmount = new AtomicReference<>(BigDecimal.ZERO);
            // 打赏金额
            AtomicReference<BigDecimal> tipsAmount = new AtomicReference<>(BigDecimal.ZERO);
            // 风控金额
            AtomicReference<BigDecimal> riskAmount = new AtomicReference<>(BigDecimal.ZERO);


            for (ReportUserInfoStatementVO record : listed) {
                BigDecimal rate = getRateByRecord(vo.getConvertPlatCurrency(), allFinalRate, record);
                firstDepositAmount.updateAndGet(v -> v.add(record.getFirstDepositAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                totalDeposit.updateAndGet(v -> v.add(record.getTotalDeposit().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                numberDeposit.updateAndGet(v -> v + record.getNumberDeposit());
                advancedTransfer.updateAndGet(v -> v.add(record.getAdvancedTransfer().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                numberTransfer.updateAndGet(v -> v + record.getNumberTransfer());
                // 大额存款总额
                amountLargeDeposits.updateAndGet(v -> v.add(record.getAmountLargeDeposits().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                // 大额存款次数
                numberLargeDeposits.updateAndGet(v -> v + record.getNumberLargeDeposits());
                // 大额取款总额
                amountLargeWithdrawal.updateAndGet(v -> v.add(record.getAmountLargeWithdrawal().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                // 大额取款次数
                numberLargeWithdrawal.updateAndGet(v -> v + record.getNumberLargeWithdrawal());

                totalWithdrawal.updateAndGet(v -> v.add(record.getTotalWithdrawal().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                numberWithdrawal.updateAndGet(v -> v + record.getNumberWithdrawal());
                poorAccess.updateAndGet(v -> v.add(record.getPoorAccess().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                memberLabourAmount.updateAndGet(v -> v.add(record.getTotalPreference().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                // 返水
                rebateAmount.updateAndGet(v -> v.add(record.getRebateAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                otherAdjustments.updateAndGet(v -> v.add(record.getOtherAdjustments().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                placeOrderQuantity.updateAndGet(v -> v + record.getPlaceOrderQuantity());

                betAmount.updateAndGet(v -> v.add(record.getBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                activeBet.updateAndGet(v -> v.add(record.getActiveBet().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                bettingProfitLoss.updateAndGet(v -> v.add(record.getBettingProfitLoss().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                activityAmount.updateAndGet(v -> v.add(record.getActivityAmount()));

                vipAmount.updateAndGet(v -> v.add(record.getVipAmount()));

                alreadyUseAmount.updateAndGet(v -> v.add(record.getAlreadyUseAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                centerAmount.updateAndGet(v -> v.add(record.getCenterAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                totalPlatAmount.updateAndGet(v -> v.add(record.getTotalPlatAmount()));

                platAdjustAmount.updateAndGet(v -> v.add(record.getPlatAdjustAmount()));
                tipsAmount.updateAndGet(v -> v.add(record.getTipsAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                riskAmount.updateAndGet(v -> v.add(record.getRiskAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

            }
            userInfoStatementVO.setFirstDepositAmount(firstDepositAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setTotalDeposit(totalDeposit.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setNumberDeposit(numberDeposit.get());
            userInfoStatementVO.setAdvancedTransfer(advancedTransfer.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setNumberTransfer(numberTransfer.get());
            //大额存款次数
            userInfoStatementVO.setNumberLargeDeposits(numberLargeDeposits.get());
            userInfoStatementVO.setAmountLargeDeposits(amountLargeDeposits.get().setScale(4, RoundingMode.DOWN));

            userInfoStatementVO.setTotalWithdrawal(totalWithdrawal.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setNumberWithdrawal(numberWithdrawal.get());
            // 大额取款总额
            userInfoStatementVO.setNumberLargeWithdrawal(numberLargeWithdrawal.get());
            userInfoStatementVO.setAmountLargeWithdrawal(amountLargeWithdrawal.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setPoorAccess(poorAccess.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setTotalPreference(memberLabourAmount.get());
            userInfoStatementVO.setRebateAmount(rebateAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setOtherAdjustments(otherAdjustments.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setPlaceOrderQuantity(placeOrderQuantity.get());
            userInfoStatementVO.setBetAmount(betAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setActiveBet(activeBet.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setBettingProfitLoss(bettingProfitLoss.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setActivityAmount(activityAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setVipAmount(vipAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setAlreadyUseAmount(alreadyUseAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setCenterAmount(centerAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setTotalPlatAmount(totalPlatAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setPlatAdjustAmount(platAdjustAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setTipsAmount(tipsAmount.get().setScale(4, RoundingMode.DOWN));
            userInfoStatementVO.setRiskAmount(riskAmount.get().setScale(4, RoundingMode.DOWN));

            statementVO.setTotalPage(userInfoStatementVO);
        }
    }
   /* @Scheduled(cron = "0 40 18 * * ?")
    public void test() {
        ReportUserInfoStatementSyncVO param = new ReportUserInfoStatementSyncVO();
        param.setStartTime(1733155200000L);
        param.setEndTime(1733371199000L);
        param.setSiteCode("Vd438R");
        saveReportUserInfoStatement(param);
    }*/


    /**
     * 定时任务生成会员报表入口，按照小时
     *
     * @param requestParam c
     */
    public void saveReportUserInfoStatement(ReportUserInfoStatementSyncVO requestParam) {

        log.info("定时任务生成会员报表:report服务参数:{}", JSONObject.toJSONString(requestParam));
        List<Long> needRunTimes = new ArrayList<>();
        //  不传时间
        if (requestParam.getStartTime() == null || requestParam.getEndTime() == null) {
            // 是否第一次
            ReportUserInfoStatementPO isNoRecord = this.baseMapper.selectOne(new LambdaQueryWrapper<ReportUserInfoStatementPO>().last(" limit 1 "));
            if (isNoRecord == null) {
                // 第一次进行定时任务，根据任务获取时间，来计算上个小时的
                long dataHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(System.currentTimeMillis());
                needRunTimes.add(dataHourTime);
            } else {
                jobTime(needRunTimes, System.currentTimeMillis());
            }

        } else {
            // 计算需要跑的整点时间
            computerTime(requestParam.getStartTime(), requestParam.getEndTime(), needRunTimes);
        }
        // 进行定时任务逻辑
        if (CollectionUtil.isNotEmpty(needRunTimes)) {
            ResponseVO<List<SiteVO>> listResponseVO = siteApi.siteInfoAllstauts();
            if (!listResponseVO.isOk()) {
                log.error("任务报表查询站点出错了:{}", JSONObject.toJSONString(listResponseVO));
                return;
            }
            List<SiteVO> siteVOs = listResponseVO.getData();
            for (Long dataHourTime : needRunTimes) {
                if (StringUtils.isNotBlank(requestParam.getSiteCode())) {
                    handleGenerateReportData(siteVOs, dataHourTime, requestParam.getSiteCode());
                } else {
                    for (SiteVO siteVO : siteVOs) {
                        handleGenerateReportData(siteVOs, dataHourTime, siteVO.getSiteCode());
                    }
                }


            }

        }

    }

    /**
     * 只多算前面3次
     *
     * @param needRunTimes 需要跑的时间lsit
     * @param currTime     当前时间
     */

    private void jobTime(List<Long> needRunTimes, long currTime) {
        final int MAX_RUN_TIMES = 3;
        long dataHourTime;

        while (true) {
            dataHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(currTime);
            // 查询数据是否存在
            ReportUserInfoStatementPO reportUserInfoStatementPO = this.baseMapper.selectOne(new LambdaQueryWrapper<ReportUserInfoStatementPO>().eq(ReportUserInfoStatementPO::getDayHourMillis, dataHourTime).last(" limit 1 "));
            if (reportUserInfoStatementPO == null) {
                needRunTimes.add(dataHourTime);
                // 达到最大次数，添加上一个整点并退出
                if (needRunTimes.size() >= MAX_RUN_TIMES) {
                    long previousHour = TimeZoneUtils.convertToPreviousUtcStartOfHour(System.currentTimeMillis());
                    if (!needRunTimes.contains(previousHour)) {
                        needRunTimes.add(previousHour);
                    }
                    return; // 退出
                }
                currTime = dataHourTime; // 更新 currTime 为当前循环的整点时间
            } else {
                // 找到数据，退出
                return;
            }
        }
    }

    private void computerTime(Long startTime, Long endTime, List<Long> needRunTimes) {
        long startHourTime = TimeZoneUtils.convertToUtcStartOfHour(startTime);
        long endHourTime = TimeZoneUtils.convertToUtcStartOfHour(endTime);
        if (endHourTime >= TimeZoneUtils.convertToUtcStartOfHour(System.currentTimeMillis())) {
            // 如果结束时间是大于当前时间，则截止到统计到上个小时
            endHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(System.currentTimeMillis());
        }
        while (startHourTime <= endHourTime) {
            needRunTimes.add(endHourTime);
            endHourTime = TimeZoneUtils.convertToPreviousUtcStartOfHour(endHourTime);
        }
    }


    /***
     * 添加会员报表，按照小时生成,
     * @param siteCode 需要删除，重新跑数据
     */
    public void handleGenerateReportData(List<SiteVO> siteVOs, Long dataHourTime, String siteCode) {
        try {

            String timeZone = null;
            // 会员盈亏报表统计，投注，vip，ac
            for (SiteVO siteVO : siteVOs) {
                timeZone = siteVO.getTimezone();
                break;
            }
            if (StringUtils.isBlank(timeZone)) {
                log.error("定时任务生成会员报表:report服务参数获取时区错误:{}", dataHourTime);
                return;
            }
            //
            //获取昨天的开始时间
            log.info("定时任务生成会员报表:report服务参数dataHourTime:{}", dataHourTime);
            long startTime = TimeZoneUtils.convertToUtcStartOfHour(dataHourTime);
            long endTime = TimeZoneUtils.convertToUtcEndOfHour(dataHourTime);
            // 删除指定时间
            LambdaUpdateWrapper<ReportUserInfoStatementPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.ge(ReportUserInfoStatementPO::getDayHourMillis, startTime).le(ReportUserInfoStatementPO::getDayHourMillis, endTime).eq(ReportUserInfoStatementPO::getSiteCode, siteCode);
            this.baseMapper.delete(updateWrapper);
            long dayMillis = TimeZoneUtils.getStartOfDayInTimeZone(dataHourTime, timeZone);
            log.info("定时任务生成会员报表,startTime:{},endTime:{}", startTime, endTime);
            // 变动补偿
            historicalChangeCompensation(startTime, endTime, siteCode);
            log.info("补偿完成，定时任务生成会员报表,startTime:{},endTime:{}", startTime, endTime);
            // 查询数据
            // 计算本次变动用户所有用户id
            Set<String> userChangeAgentSet = new HashSet<>();
            //获取用户投注金额，有效投注，投注盈亏，总返水
            AgentUserWinLossDetailsPageVO winLossDetailsPageVO = new AgentUserWinLossDetailsPageVO();
            winLossDetailsPageVO.setStatTime(startTime);
            winLossDetailsPageVO.setEndTime(endTime);
            winLossDetailsPageVO.setSiteCode(siteCode);
            // 来源1： 获取日期范围内所有会员详细盈亏明细
            ResponseVO<List<MemberWinLossDetailVO>> responseVO = reportUserWinLoseService.allMemWinLostDetail(winLossDetailsPageVO);

            List<MemberWinLossDetailVO> memberWinLossDetailVOList = responseVO.getData();
            List<StatementVO> allList = BeanUtil.copyToList(memberWinLossDetailVOList, StatementVO.class);
            // Map<userId, Map<agentId, List<StatementVO>>>
            Map<String, Map<String, List<StatementVO>>> betMap = userAgentGroup(allList, userChangeAgentSet);
            // 会员存取款数据汇总
            // 条件查询会员存款数据      * 订单类型 1 存款 2 取款
            //     * {@link com.cloud.baowang.common.core.enums.usercoin.DepositWithdrawalOrderTypeEnum}
            UserInfoStatementVO userInfoStatementVO = UserInfoStatementVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).build();
            // 存取款报表，每个小时进行的统计。
            List<DepositWithdrawalVO> depositWithdrawalVOS = reportUserRechargeService.getUserDepositWithdrawalPOList(userInfoStatementVO);


            Map<String, Map<String, List<DepositWithdrawalVO>>> depositWithdrawalMap = userAccessAgentGroup(depositWithdrawalVOS, userChangeAgentSet);
            // 查询站点所有代理信息
            List<AgentInfoPartVO> agentInfoVOList = agentInfoApi.getAllPartAgentInfoBySiteCode(siteCode);
            Map<String, AgentInfoPartVO> agentInfoPartVOMap = agentInfoVOList.stream().collect(Collectors.toMap(AgentInfoPartVO::getAgentId, Function.identity()));

            // 转代次数
            /*ReportUserTransferReqVO reportUserTransferReqVO = ReportUserTransferReqVO.builder()
                    .startTime(startTime)
                    .endTime(endTime)
                    .siteCode(siteCode).build();*/
            // 来源3 计算转代
            /*List<ReportUserTransferRespVO> respVOS = userTransferAgentApi.queryUserTransferCount(reportUserTransferReqVO);
            Map<String, Integer> userTransferMap = respVOS.stream()
                    .filter(vo -> vo.getUserAccount() != null && vo.getUserTransferCount() != null) // 过滤掉 null 值
                    .collect(Collectors.toMap(ReportUserTransferRespVO::getUserAccount, ReportUserTransferRespVO::getUserTransferCount));*/

            if (CollectionUtil.isEmpty(depositWithdrawalMap) && CollectionUtil.isEmpty(betMap)) {
                log.info("本次同步用户无变动用户,size:");
                return;
            }
            log.info("定时任务生成会员报表:本次同步用户报表数据,userAccountSet:{}", userChangeAgentSet);
            List<ReportUserInfoStatementPO> poList = new ArrayList<>();
            // 处理 会员盈亏

            // 处理 会员存取款

            // 处理 转代


            // 首先根据userId 进行遍历
            for (String userId : userChangeAgentSet) {
                // 会员盈亏报表
                Map<String, List<StatementVO>> betAgentMap = Optional.ofNullable(betMap.get(userId)).orElse(new HashMap<>());

                Set<String> userAgentIdSet = new HashSet<>(betAgentMap.keySet());
                // 存款
                Map<String, List<DepositWithdrawalVO>> depositWithDrawlAgentMap = Optional.ofNullable(depositWithdrawalMap.get(userId)).orElse(new HashMap<>());
                userAgentIdSet.addAll(depositWithDrawlAgentMap.keySet());
                // 在根据代理进行分组
                for (String agentId : userAgentIdSet) {

                    boolean change = false;// 会员变动标志 默认无变动
                    ReportUserInfoStatementPO po = new ReportUserInfoStatementPO();
                    Integer betNumber = 0;//注单量
                    BigDecimal winLossAmount = BigDecimal.ZERO;//输赢金额
                    BigDecimal validAmount = BigDecimal.ZERO;//有效投注
                    BigDecimal betAmount = BigDecimal.ZERO;//投注金额
                    BigDecimal bettingProfitLoss = BigDecimal.ZERO;//投注盈亏
                    BigDecimal profitAndLoss = BigDecimal.ZERO;//净盈亏
                    BigDecimal activityAmount = BigDecimal.ZERO;//活动加减额  ==> 总优惠
                    BigDecimal vipAmount = BigDecimal.ZERO;//vip
                    BigDecimal alreadyUseAmount = BigDecimal.ZERO;//已使用
                    BigDecimal rebateAmount = BigDecimal.ZERO;//返水
                    BigDecimal otherAdjustments = BigDecimal.ZERO; //其他调整
                    BigDecimal platAdjustAmount = BigDecimal.ZERO; //平台调整
                    BigDecimal tipsAmount = BigDecimal.ZERO; // 打赏
                    BigDecimal riskAmount = BigDecimal.ZERO; // 封控金额-主货币

                    String userAccount = null;
                    String agentAccount = null;
                    String currency = null;
                    boolean isUserAccountAssigned = false; // 标志位，表示 userAccount 是否已经赋值
                    boolean isAgentAccountAssigned = false; // 标志位，表示 agentAccount 是否已经赋值
                    boolean isCurrencyAssigned = false; // 标志位，表示 currency 是否已经赋值

                    //
                    List<StatementVO> betAgentList = Optional.ofNullable(betAgentMap.get(agentId)).orElse(Lists.newArrayList());
                    if (CollectionUtil.isNotEmpty(betAgentList)) {
                        for (StatementVO statementVO : betAgentList) {
                            winLossAmount = winLossAmount.add(Optional.ofNullable(statementVO.getWinLossAmount()).orElse(BigDecimal.ZERO));
                            validAmount = validAmount.add(Optional.ofNullable(statementVO.getValidAmount()).orElse(BigDecimal.ZERO));
                            betAmount = betAmount.add(Optional.ofNullable(statementVO.getBetAmount()).orElse(BigDecimal.ZERO));
                            betNumber += Optional.ofNullable(statementVO.getBetNumber()).orElse(0);
                            activityAmount = activityAmount.add(Optional.ofNullable(statementVO.getActivityAmount()).orElse(BigDecimal.ZERO));
                            vipAmount = vipAmount.add(Optional.ofNullable(statementVO.getVipAmount()).orElse(BigDecimal.ZERO));
                            rebateAmount = rebateAmount.add(Optional.ofNullable(statementVO.getRebateAmount()).orElse(BigDecimal.ZERO));
                            alreadyUseAmount = alreadyUseAmount.add(Optional.ofNullable(statementVO.getAlreadyUseAmount()).orElse(BigDecimal.ZERO));
                            otherAdjustments = otherAdjustments.add(Optional.ofNullable(statementVO.getAdjustAmount()).orElse(BigDecimal.ZERO));
                            bettingProfitLoss = bettingProfitLoss.add(Optional.ofNullable(statementVO.getWinLossAmount()).orElse(BigDecimal.ZERO));
                            profitAndLoss = profitAndLoss.add(Optional.ofNullable(statementVO.getProfitAndLoss()).orElse(BigDecimal.ZERO));
                            platAdjustAmount = platAdjustAmount.add(Optional.ofNullable(statementVO.getPlatAdjustAmount()).orElse(BigDecimal.ZERO));
                            tipsAmount = tipsAmount.add(Optional.ofNullable(statementVO.getTipsAmount()).orElse(BigDecimal.ZERO));
                            riskAmount = riskAmount.add(Optional.ofNullable(statementVO.getRiskAmount()).orElse(BigDecimal.ZERO));
                            // 根据查询 userId 查询
                            // 根据agentId查询
                            // 赋值 userAccount 和 agentAccount，只在它们未被赋值的情况下
                            if (!isUserAccountAssigned && statementVO.getUserAccount() != null) {
                                userAccount = statementVO.getUserAccount();
                                isUserAccountAssigned = true;
                            }
                            if (!isAgentAccountAssigned && statementVO.getAgentAccount() != null) {
                                agentAccount = statementVO.getAgentAccount();
                                isAgentAccountAssigned = true;
                            }
                            if (!isCurrencyAssigned && statementVO.getCurrency() != null) {
                                currency = statementVO.getCurrency();
                                isCurrencyAssigned = true;
                            }
                        }
                    }

                    // 存取款 agentId 只会对应一个
                    Map<String, List<DepositWithdrawalVO>> agentUserDeposit = Optional.ofNullable(depositWithdrawalMap.get(userId)).orElse(Maps.newHashMap());
                    List<DepositWithdrawalVO> userDepositList = agentUserDeposit.get(agentId);
                    Integer numberDeposit = 0;//存款次数
                    BigDecimal totalDeposit = BigDecimal.ZERO;//存取款金额
                    Integer numberLargeDeposits = 0;//大额存取款次数
                    BigDecimal amountLargeDeposits = BigDecimal.ZERO;//大额存款金额
                    Integer depositSubordinatesNums = 0;//代理代存次数
                    BigDecimal depositSubordinatesAmount = BigDecimal.ZERO;//代理代存金额

                    Integer numberWithdrawal = 0;//取款次数
                    BigDecimal totalWithdrawal = BigDecimal.ZERO;//总取款
                    Integer numberLargeWithdrawal = 0;//大额取款次数
                    BigDecimal amountLargeWithdrawal = BigDecimal.ZERO;//大额款金额
                    if (CollectionUtil.isNotEmpty(userDepositList)) {
                        for (DepositWithdrawalVO withdrawalVO : userDepositList) {
                            if (String.valueOf(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode()).equals(withdrawalVO.getType())) {
                                numberDeposit += Optional.ofNullable(withdrawalVO.getNums()).orElse(0);
                                totalDeposit = totalDeposit.add(Optional.ofNullable(withdrawalVO.getAmount()).orElse(BigDecimal.ZERO));
                                numberLargeDeposits += Optional.ofNullable(withdrawalVO.getLargeNums()).orElse(0);
                                amountLargeDeposits = amountLargeDeposits.add(Optional.ofNullable(withdrawalVO.getLargeAmount()).orElse(BigDecimal.ZERO));
                                depositSubordinatesNums += Optional.ofNullable(withdrawalVO.getDepositSubordinatesNums()).orElse(0);
                                depositSubordinatesAmount = depositSubordinatesAmount.add(Optional.ofNullable(withdrawalVO.getDepositSubordinatesAmount()).orElse(BigDecimal.ZERO));
                            }
                            if (String.valueOf(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode()).equals(withdrawalVO.getType())) {
                                numberWithdrawal += Optional.ofNullable(withdrawalVO.getNums()).orElse(0);
                                totalWithdrawal = totalWithdrawal.add(Optional.ofNullable(withdrawalVO.getAmount()).orElse(BigDecimal.ZERO));
                                numberLargeWithdrawal += Optional.ofNullable(withdrawalVO.getLargeNums()).orElse(0);
                                amountLargeWithdrawal = amountLargeWithdrawal.add(Optional.ofNullable(withdrawalVO.getLargeAmount()).orElse(BigDecimal.ZERO));

                            }
                            // 仅当之前未赋值时，尝试赋值 userAccount 和 agentAccount
                            if (!isUserAccountAssigned && withdrawalVO.getUserAccount() != null) {
                                userAccount = withdrawalVO.getUserAccount();
                                isUserAccountAssigned = true;
                            }
                            if (!isAgentAccountAssigned && withdrawalVO.getAgentAccount() != null) {
                                agentAccount = withdrawalVO.getAgentAccount();
                                isAgentAccountAssigned = true;
                            }
                            if (!isCurrencyAssigned && withdrawalVO.getCurrency() != null) {
                                currency = withdrawalVO.getCurrency();
                                isCurrencyAssigned = true;
                            }
                        }
                    }

                    po.setUserAccount(userAccount);
                    po.setUserId(userId);
                    po.setSuperAgentId(StringUtils.isBlank(agentId) ? null : agentId);
                    po.setSuperAgentAccount(StringUtils.isBlank(agentAccount) ? null : agentAccount);
                    po.setTotalDeposit(totalDeposit);//总存
                    if (StringUtils.isNotBlank(agentId)) {
                        //AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(agentId);
                        AgentInfoPartVO agentInfoVO = agentInfoPartVOMap.get(agentId);
                        if (agentInfoVO != null) {
                            po.setAgentAttribution(null == agentInfoVO ? null : agentInfoVO.getAgentAttribution()); // 代理归属 1推广 2招商 3官资
                        }

                    }
                    // 确认代理模式
                    po.setTotalDeposit(totalDeposit);
                    po.setNumberDeposit(numberDeposit);//存款次数
                    po.setAdvancedTransfer(depositSubordinatesAmount);//上级转入
                    po.setNumberTransfer(depositSubordinatesNums);//转入次数
                    po.setNumberLargeDeposits(numberLargeDeposits);//大额存款次数
                    po.setAmountLargeDeposits(amountLargeDeposits);//大额存款金额
                    // 取款
                    po.setTotalWithdrawal(totalWithdrawal);//总取
                    po.setNumberWithdrawal(numberWithdrawal);//取款次数
                    po.setNumberLargeWithdrawal(numberLargeWithdrawal);//大额取款次数
                    po.setAmountLargeWithdrawal(amountLargeWithdrawal);//大额款金额
                    po.setPoorAccess(totalDeposit.subtract(totalWithdrawal));//存取差
                    // 投注方面
                    //po.setGrossRecoil(zongFan);//总返水


                    po.setPlaceOrderQuantity(betNumber);//注单量
                    po.setBetAmount(betAmount);//投注金额
                    po.setBettingProfitLoss(winLossAmount);//投注盈亏
                    po.setActiveBet(validAmount);//有效投注
                    po.setProfitAndLoss(profitAndLoss);//净盈亏
                    po.setActivityAmount(activityAmount);//活动优惠
                    po.setVipAmount(vipAmount);//vip
                    po.setAlreadyUseAmount(alreadyUseAmount);//已使用
                    po.setOtherAdjustments(otherAdjustments);//其他调整
                    po.setRebateAmount(rebateAmount);//返水

                    po.setCreatedTime(System.currentTimeMillis());//时间
                    po.setDayHourMillis(dataHourTime);
                    po.setDayMillis(dayMillis);
                    po.setSiteCode(siteCode);
                    po.setMainCurrency(currency);
                    po.setPlatAdjustAmount(platAdjustAmount);//平台调整
                    po.setTipsAmount(tipsAmount);// 打赏
                    po.setRiskAmount(riskAmount);// 封控金额-主货币
                    //po.setTransAgentTime(userTransferMap.getOrDefault(userAccount, 0));
                    if (StringUtils.isBlank(currency)) {
                        log.error("定时任务生成会员报表,会员数据获取会员主货币出错:{}", JSONObject.toJSONString(po));
                    }
                    poList.add(po);
                    log.info("定时任务生成会员报表,会员数据:{}", JSONObject.toJSONString(po));
                }
            }


            this.saveBatch(poList);


            log.info("定时任务生成会员报表同步用户报表完成");
        } catch (Exception e) {
            log.error("定时任务生成会员报表会员报表添加失败：", e);
            throw new BaowangDefaultException("会员报表添加失败");
        }
    }

    /**
     * 变动补偿
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param siteCode  需要补充的站点,如果为空，则表明全部都要变动补偿
     */
    private void historicalChangeCompensation(long startTime, long endTime, String siteCode) {
        AgentUserWinLossDetailsPageVO vo = new AgentUserWinLossDetailsPageVO();
        vo.setStatTime(startTime);
        vo.setEndTime(endTime);
        vo.setSiteCode(siteCode);
        //  获取变更日期范围内所有会员详细盈亏明细，更新时间在上个小时，但是统计时间在上个小时之前
        ResponseVO<List<MemberWinLossDetailVO>> rv = reportUserWinLoseService.allChangeMemWinLostDetail(vo);
        if (ResultCode.SUCCESS.getCode() != rv.getCode()) {
            log.error("获取变更日期范围内所有会员详细盈亏明细失败，开始：{}，结束：{},响应：{}", startTime, endTime, rv);
            throw new BaowangDefaultException(ResultCode.SERVER_INTERNAL_ERROR);
        }

        List<MemberWinLossDetailVO> changeData = rv.getData();
        if (CollectionUtil.isEmpty(changeData)) {
            log.info("本次会员报表补偿无变动用户");
            return;
        }
        for (MemberWinLossDetailVO changeDatum : changeData) {
            LambdaQueryWrapper<ReportUserInfoStatementPO> wrapper = Wrappers.<ReportUserInfoStatementPO>lambdaQuery()
                    .eq(ReportUserInfoStatementPO::getDayHourMillis, changeDatum.getBetTime())
                    .eq(ReportUserInfoStatementPO::getUserId, changeDatum.getUserId());
            if (StringUtils.isNotEmpty(changeDatum.getAgentId())) {
                wrapper.eq(ReportUserInfoStatementPO::getSuperAgentId, changeDatum.getAgentId());
            } else {
                wrapper.isNull(ReportUserInfoStatementPO::getSuperAgentId);
            }
            //
            ReportUserInfoStatementPO po = baseMapper.selectOne(wrapper);
            // 如果没有查询到，则生成一条数据
            if (ObjectUtil.isEmpty(po)) {
                initReportInfoStatementPO(changeDatum);
            } else {
                // 需要更新的，会员盈亏报表也是一个小时统计，所以可以替换
                po.setBetAmount(changeDatum.getBetAmount());
                po.setActiveBet(changeDatum.getValidAmount());
                po.setBettingProfitLoss(changeDatum.getWinLossAmount());
                po.setOtherAdjustments(changeDatum.getAdjustAmount());
                po.setRebateAmount(changeDatum.getRebateAmount());
                po.setPlaceOrderQuantity(changeDatum.getBetNumber());
                //  活动，vip福利，已使用优惠
                po.setActivityAmount(changeDatum.getActivityAmount());
                po.setVipAmount(changeDatum.getVipAmount());
                po.setAlreadyUseAmount(changeDatum.getAlreadyUseAmount());
                //
                po.setPlatAdjustAmount(changeDatum.getPlatAdjustAmount());
                po.setTipsAmount(changeDatum.getTipsAmount());
                po.setRiskAmount(changeDatum.getRiskAmount());

                baseMapper.updateById(po);
            }

        }

    }

    private void initReportInfoStatementPO(MemberWinLossDetailVO changeDatum) {
        ReportUserInfoStatementPO po = new ReportUserInfoStatementPO();

        // --
        String agentId = changeDatum.getAgentId();
        String agentAccount = changeDatum.getAgentAccount();
        po.setUserAccount(changeDatum.getUserAccount());
        po.setUserId(changeDatum.getUserId());
        po.setSuperAgentId(StringUtils.isBlank(agentId) ? null : agentId);
        po.setSuperAgentAccount(StringUtils.isBlank(agentAccount) ? null : agentAccount);

        if (StringUtils.isNotBlank(agentId)) {
            AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(agentId);
            po.setAgentAttribution(null == agentInfoVO ? null : agentInfoVO.getAgentAttribution()); // 代理归属 1推广 2招商 3官资

        }
        // 确认代理模式
        po.setTotalDeposit(BigDecimal.ZERO);//总存
        po.setNumberDeposit(0);//存款次数
        po.setAdvancedTransfer(BigDecimal.ZERO);//上级转入
        po.setNumberTransfer(0);//转入次数
        po.setNumberLargeDeposits(0);//大额存款次数
        po.setAmountLargeDeposits(BigDecimal.ZERO);//大额存款金额
        // 取款
        po.setTotalWithdrawal(BigDecimal.ZERO);//总取
        po.setNumberWithdrawal(0);//取款次数
        po.setNumberLargeWithdrawal(0);//大额取款次数
        po.setAmountLargeWithdrawal(BigDecimal.ZERO);//大额款金额
        po.setPoorAccess(BigDecimal.ZERO);//存取差
        // 投注方面
        //po.setGrossRecoil(zongFan);//总返水


        po.setBetAmount(changeDatum.getBetAmount());
        po.setActiveBet(changeDatum.getValidAmount());
        po.setBettingProfitLoss(changeDatum.getWinLossAmount());
        po.setOtherAdjustments(changeDatum.getAdjustAmount());
        po.setRebateAmount(changeDatum.getRebateAmount());
        po.setPlaceOrderQuantity(changeDatum.getBetNumber());
        //  活动，vip福利，已使用优惠
        po.setActivityAmount(changeDatum.getActivityAmount());
        po.setVipAmount(changeDatum.getVipAmount());
        po.setAlreadyUseAmount(changeDatum.getAlreadyUseAmount());


        po.setCreatedTime(System.currentTimeMillis());//时间
        po.setDayHourMillis(changeDatum.getBetTime());
        po.setDayMillis(po.getDayHourMillis());
        po.setSiteCode(changeDatum.getSiteCode());
        po.setMainCurrency(changeDatum.getMainCurrency());

        po.setPlatAdjustAmount(changeDatum.getPlatAdjustAmount());
        po.setTipsAmount(changeDatum.getTipsAmount());
        po.setRiskAmount(changeDatum.getRiskAmount());

        this.baseMapper.insert(po);

    }


    /**
     * 按用户账号和代理账号对 DepositWithdrawalVO 对象列表进行分组。
     *
     * @param list 要分组的 DepositWithdrawalVO 对象列表。
     * @return 一个嵌套的 Map，其中外层的 key 是用户账号，value 是一个 Map；
     * 内层 Map 的 key 是代理账号（如果没有代理则为一个空字符串），
     * value 是对应每个用户-代理账号组合的 DepositWithdrawalVO 对象列表。
     */
    private Map<String, Map<String, List<DepositWithdrawalVO>>> userAccessAgentGroup(List<DepositWithdrawalVO> list, Set<String> userChangeAgentSet) {
        Map<String, Map<String, List<DepositWithdrawalVO>>> recordMap = new HashMap<>();
        // 会员的代理分组
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, List<DepositWithdrawalVO>> userAccountGroup = list.stream().collect(Collectors.groupingBy(DepositWithdrawalVO::getUserId));

            userAccountGroup.forEach((userId, recordList) -> {
                userChangeAgentSet.add(userId);
                Map<String, List<DepositWithdrawalVO>> agentMap = new HashMap<>();

                recordList.forEach(recordVO -> {
                    // 检查代理账号是否非空。
                    if (StringUtils.isNotBlank(recordVO.getAgentAccount())) {
                        // 获取当前代理账号对应的列表。
                        List<DepositWithdrawalVO> agentList = agentMap.get(recordVO.getAgentId());
                        if (CollectionUtil.isNotEmpty(agentList)) {
                            agentList.add(recordVO);
                        } else {
                            agentList = new ArrayList<>();
                            agentList.add(recordVO);
                            agentMap.put(recordVO.getAgentId(), agentList);
                        }
                    } else {
                        // 如果代理账号为空字符串，使用空字符串作为 key。
                        List<DepositWithdrawalVO> manualDownRecordVOS = agentMap.get(Strings.EMPTY);
                        if (CollectionUtil.isNotEmpty(manualDownRecordVOS)) {
                            manualDownRecordVOS.add(recordVO);
                        } else {
                            manualDownRecordVOS = new ArrayList<>();
                            manualDownRecordVOS.add(recordVO);
                            agentMap.put(Strings.EMPTY, manualDownRecordVOS);
                        }
                    }
                });
                recordMap.put(userId, agentMap);
            });
        }
        return recordMap;
    }

    /**
     * 按用户账号和代理账号对 StatementVO 对象列表进行分组。
     * Map<userId, Map<agentId, List<StatementVO>>>
     *
     * @param allList 要分组的 StatementVO 对象列表。
     * @return 一个嵌套的 Map，其中外层的 key 是用户账号userId，value 是一个 Map；
     * 内层 Map 的 key 是代理账号（可能为空字符串表示没有代理），
     * value 是对应每个用户-代理账号组合的 StatementVO 对象列表。
     */
    private Map<String, Map<String, List<StatementVO>>> userAgentGroup(List<StatementVO> allList, Set<String> userChangeAgentSet) {
        Map<String, Map<String, List<StatementVO>>> recordMap = new HashMap<>();
        // 会员的代理分组
        if (CollectionUtil.isNotEmpty(allList)) {
            Map<String, List<StatementVO>> userAccountGroup = allList.stream().collect(Collectors.groupingBy(StatementVO::getUserId));
            userAccountGroup.forEach((userId, list) -> {
                Map<String, List<StatementVO>> agentMap = new HashMap<>();
                userChangeAgentSet.add(userId);
                list.forEach(recordVO -> {
                    // 有代理
                    if (StringUtils.isNotBlank(recordVO.getAgentId())) {
                        List<StatementVO> agentList = agentMap.get(recordVO.getAgentId());
                        if (CollectionUtil.isNotEmpty(agentList)) {
                            agentList.add(recordVO);
                        } else {
                            agentList = new ArrayList<>();
                            agentList.add(recordVO);
                            agentMap.put(recordVO.getAgentId(), agentList);
                        }
                    } else {
                        // 如果代理账号为空字符串，使用空字符串作为 key
                        List<StatementVO> manualDownRecordVOS = agentMap.get(Strings.EMPTY);
                        if (CollectionUtil.isNotEmpty(manualDownRecordVOS)) {
                            // 如果空字符串的列表存在，将记录添加到列表
                            manualDownRecordVOS.add(recordVO);
                        } else {
                            // 如果空字符串的列表不存在，创建新的列表并添加记录，然后放入 Map。
                            manualDownRecordVOS = new ArrayList<>();
                            manualDownRecordVOS.add(recordVO);
                            agentMap.put(Strings.EMPTY, manualDownRecordVOS);
                        }
                    }
                });
                recordMap.put(userId, agentMap);
            });
        }
        return recordMap;
    }


    public ResponseVO<Page<ReportUserInfoStatementResponseVO>> pageListUserAccount(ReportUserInfoStatementPageVO vo) {
        Page<ReportUserInfoStatementResponseVO> voPage = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<ReportUserInfoStatementPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<ReportUserInfoStatementPO> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(vo.getUserAccount())) {
            queryWrapper.eq(ReportUserInfoStatementPO::getUserAccount, vo.getUserAccount());
            queryWrapper.eq(ReportUserInfoStatementPO::getSiteCode, vo.getSiteCode());
            if (ObjectUtil.isNotEmpty(vo.getStatisticalDateStart())) {
                queryWrapper.ge(ReportUserInfoStatementPO::getCreatedTime, vo.getStatisticalDateStart());
            }
            if (ObjectUtil.isNotEmpty(vo.getStatisticalDateEnd())) {
                queryWrapper.le(ReportUserInfoStatementPO::getCreatedTime, vo.getStatisticalDateEnd());
            }
            queryWrapper.orderByDesc(ReportUserInfoStatementPO::getCreatedTime);
        }

        Page<ReportUserInfoStatementResponseVO> pageList = baseMapper.pageListUserAccount(voPage, vo);
        List<ReportUserInfoStatementResponseVO> list = new ArrayList<>();
        for (ReportUserInfoStatementResponseVO record : pageList.getRecords()) {
            record.setTotalPreference(record.getMemberLabourAmount().add(record.getMemberVipLabourAmount()));
            list.add(record);
        }
        voPage.setRecords(list);
        voPage.setCurrent(pageList.getCurrent());
        voPage.setPages(pageList.getPages());
        voPage.setTotal(pageList.getTotal());
        voPage.setSize(page.getSize());
        return ResponseVO.success(voPage);
    }


}
