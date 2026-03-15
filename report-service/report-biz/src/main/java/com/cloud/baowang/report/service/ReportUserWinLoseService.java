package com.cloud.baowang.report.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentCommissionPlanApi;
import com.cloud.baowang.agent.api.enums.AgentAttributionEnum;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanVO;
import com.cloud.baowang.agent.api.vo.winloss.AgentUserWinLossParam;
import com.cloud.baowang.agent.api.vo.winloss.AgentWInLossInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.vo.order.report.WinLoseRecalculateFeelSpinVO;
import com.cloud.baowang.play.api.vo.order.report.WinLoseRecalculateReqVO;
import com.cloud.baowang.play.api.vo.order.report.WinLoseRecalculateVO;
import com.cloud.baowang.report.api.vo.*;
import com.cloud.baowang.report.api.vo.agent.*;
import com.cloud.baowang.report.api.vo.user.ReportUserBetsVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.report.api.vo.userwinlose.*;
import com.cloud.baowang.report.po.ReportUserWinLosePO;
import com.cloud.baowang.report.repositories.ReportUserWinLoseRepository;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateReqWalletVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateWalletVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.intersection;

/**
 * 会员每日盈亏 服务类
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Slf4j
@Service
@AllArgsConstructor
@EnableScheduling
public class ReportUserWinLoseService extends ServiceImpl<ReportUserWinLoseRepository, ReportUserWinLosePO> {

    private final ReportUserWinLoseRepository reportUserWinLoseRepository;


    private final SiteUserLabelConfigApi siteUserLabelConfigApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final RiskApi riskApi;
    private final AgentCommissionPlanApi agentCommissionPlanApi;

    private final VipRankApi vipRankApi;

    private final VipGradeApi vipGradeApi;
    private final SiteApi siteApi;

    private final OrderRecordApi orderRecordApi;

    private final UserCoinRecordApi userCoinRecordApi;

    private final UserPlatformCoinRecordApi userPlatformCoinRecordApi;

    public ResponseVO<Long> getUserWinLosePageCount(UserWinLosePageVO vo) {
        try {
            ResponseVO<UserWinLoseResult> checkParamResult = checkParam(vo);
            if (checkParamResult != null) {
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }

        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return ResponseVO.success(reportUserWinLoseRepository.getPageCount(vo));
    }

    public Long getTotalCount(UserWinLosePageVO vo) {
        return reportUserWinLoseRepository.getTotalCount(vo);
    }

    public ResponseVO<UserWinLoseResult> getUserWinLosePage(UserWinLosePageVO vo) {
        UserWinLoseResult result = new UserWinLoseResult();
        Page<UserWinLoseResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        ResponseVO<UserWinLoseResult> checkParamResult = checkParam(vo);
        if (checkParamResult != null) return checkParamResult;

        Page<UserWinLoseResponseVO> pageResult = reportUserWinLoseRepository.getPage(page, vo);

        // 集中获取配置参数
       /* List<String> types = Lists.newArrayList();
        types.add(CommonConstant.USER_ACCOUNT_TYPE);
        types.add(CommonConstant.USER_ACCOUNT_STATUS);
        Map<String, List<CodeValueVO>> systemParamsMap = systemParamApi.getSystemParamsByList(types).getData();*/

        String platCurrencycode = CommonConstant.PLAT_CURRENCY_CODE; // 获取平台币种名称
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(vo.getSiteCode()); // 获取所有币种汇率
        Boolean convertPlat = vo.getConvertPlatCurrency(); // 是否转换为平台币
        // 注单量
        AtomicReference<Integer> betNumAll = new AtomicReference<>(CommonConstant.business_zero);
        // 投注金额
        AtomicReference<BigDecimal> betAmountAll = new AtomicReference<>(BigDecimal.ZERO);
        // 有效投注
        AtomicReference<BigDecimal> validBetAmountAll = new AtomicReference<>(BigDecimal.ZERO);
        // 流水纠正
        AtomicReference<BigDecimal> runWaterCorrectAll = new AtomicReference<>(BigDecimal.ZERO);
        // 投注盈亏
        AtomicReference<BigDecimal> betWinLoseAll = new AtomicReference<>(BigDecimal.ZERO);
        // 优惠金额
        AtomicReference<BigDecimal> activityAmountAll = new AtomicReference<>(BigDecimal.ZERO);
        // 已使用金额
        AtomicReference<BigDecimal> alreadyUseAmountAll = new AtomicReference<>(BigDecimal.ZERO);
        // 返水
        AtomicReference<BigDecimal> rebateAmountAll = new AtomicReference<>(BigDecimal.ZERO);

        // vip 福利
        AtomicReference<BigDecimal> vipAmountAll = new AtomicReference<>(BigDecimal.ZERO);
        // 调整金额(其他调整)
        AtomicReference<BigDecimal> adjustAmountAll = new AtomicReference<>(BigDecimal.ZERO);
        // 补单其他调整
        AtomicReference<BigDecimal> repairOrderOtherAdjustAll = new AtomicReference<>(BigDecimal.ZERO);
        // 净输赢(主货币)
        AtomicReference<BigDecimal> profitAndLossAll = new AtomicReference<>(BigDecimal.ZERO);
        // 调整金额(其他调整)-平台币
        AtomicReference<BigDecimal> platAdjustAmount = new AtomicReference<>(BigDecimal.ZERO);
        // 打赏金额
        AtomicReference<BigDecimal> tipsAmount = new AtomicReference<>(BigDecimal.ZERO);
        // 封控金额-主货币
        AtomicReference<BigDecimal> riskAmount = new AtomicReference<>(BigDecimal.ZERO);
        // 会员标签
        Set<String> labelIds = pageResult.getRecords().stream().filter(record -> StrUtil.isNotEmpty(record.getUserLabelId())).flatMap(record -> Arrays.stream(record.getUserLabelId().split(CommonConstant.COMMA))).collect(Collectors.toSet());

        List<GetUserLabelByIdsVO> userLabels = siteUserLabelConfigApi.getUserLabelByIds(new ArrayList<>(labelIds));
        Map<String, GetUserLabelByIdsResVO> userLabelMap = new HashMap<>();
        //Map<String, GetUserLabelByIdsResVO> userLabelColorMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(userLabels)) {

            userLabelMap = userLabels.stream()
                    .collect(Collectors.toMap(GetUserLabelByIdsVO::getId, tempVo -> ConvertUtil.entityToModel(tempVo, GetUserLabelByIdsResVO.class), (K1, K2) -> K2));
        }
        // 风控层级
        Set<String> riskIds = pageResult.getRecords().stream().map(UserWinLoseResponseVO::getRiskLevelId).filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
        Map<String, RiskLevelDetailsVO> riskLevelDetailsVOMap = riskApi.getByIds(new ArrayList<>(riskIds));
        // 查询vip Rank
        ResponseVO<List<SiteVIPRankVO>> siteVIPRankVOSResponse = vipRankApi.getVipRankListBySiteCode(vo.getSiteCode());
        Map<Integer, String> vipRankMap = new HashMap<>();
        if (siteVIPRankVOSResponse.isOk() && CollectionUtil.isNotEmpty(siteVIPRankVOSResponse.getData())) {
            vipRankMap = siteVIPRankVOSResponse.getData().stream().filter(vip -> vip.getVipRankCode() != null && StringUtils.isNotBlank(vip.getVipRankNameI18nCode()) && StringUtils.isNotBlank(vip.getVipGradeCodes())) // Corrected: ensure VipRankCode is not null (since it's an Integer)
                    .collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode, SiteVIPRankVO::getVipRankNameI18nCode, (k1, k2) -> k2));
        }
        // 查询vip等级
        List<SiteVIPGradeVO> siteVIPGradeVOS = vipGradeApi.queryAllVIPGrade(vo.getSiteCode());
        Map<Integer, String> vipGradeMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(siteVIPGradeVOS)) {
            vipGradeMap = siteVIPGradeVOS.stream().filter(vip -> vip.getVipGradeCode() != null && StringUtils.isNotBlank(vip.getVipGradeName())).collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipGradeName, (k1, k2) -> k2));
        }


        // 计算本页
        for (UserWinLoseResponseVO record : pageResult.getRecords()) {
            BigDecimal rate;
            if (convertPlat) {
                rate = allFinalRate.get(record.getMainCurrency());
                if (rate == null) {
                    throw new BaowangDefaultException("汇率未配置，货币是:" + record.getMainCurrency() + ",用户是：" + record.getUserId());
                }
            } else {
                rate = BigDecimal.ONE;
            }

            betNumAll.updateAndGet(v -> v + record.getBetNum());
            betAmountAll.updateAndGet(v -> v.add(record.getBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            validBetAmountAll.updateAndGet(v -> v.add(record.getValidBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            //runWaterCorrectAll.updateAndGet(v -> v.add(record.getRunWaterCorrect().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            betWinLoseAll.updateAndGet(v -> v.add(record.getBetWinLose().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            vipAmountAll.updateAndGet(v -> v.add(record.getVipAmount()));
            activityAmountAll.updateAndGet(v -> v.add(record.getActivityAmount()));
            adjustAmountAll.updateAndGet(v -> v.add(record.getAdjustAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            alreadyUseAmountAll.updateAndGet(v -> v.add(record.getAlreadyUseAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            // 返水金额 是主货币
            rebateAmountAll.updateAndGet(v -> v.add(record.getRebateAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

            repairOrderOtherAdjustAll.updateAndGet(v -> v.add(record.getRepairOrderOtherAdjust().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            profitAndLossAll.updateAndGet(v -> v.add(record.getProfitAndLoss().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            platAdjustAmount.updateAndGet(v -> v.add(record.getPlatAdjustAmount()));
            tipsAmount.updateAndGet(v -> v.add(record.getTipsAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            riskAmount.updateAndGet(v -> v.add(record.getRiskAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));


            if (vo.getConvertPlatCurrency()) {
                record.setBetAmount(record.getBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                record.setValidBetAmount(record.getValidBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                //record.setRunWaterCorrect(record.getRunWaterCorrect().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN));
                record.setBetWinLose(record.getBetWinLose().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                record.setVipAmount(record.getVipAmount().setScale(4, RoundingMode.DOWN));
                record.setActivityAmount(record.getActivityAmount().setScale(4, RoundingMode.DOWN));
                record.setAdjustAmount(record.getAdjustAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                record.setAlreadyUseAmount(record.getAlreadyUseAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                // 返水
                record.setRebateAmount(record.getRebateAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));

                record.setRepairOrderOtherAdjust(record.getRepairOrderOtherAdjust().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                record.setProfitAndLoss(record.getProfitAndLoss().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                record.setPlatAdjustAmount(record.getPlatAdjustAmount().setScale(4, RoundingMode.DOWN));
                record.setTipsAmount(record.getTipsAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                record.setRiskAmount(record.getRiskAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                //record.setMainCurrency(platCurrencycode);
            }
            record.setPlatCurrencyCode(platCurrencycode);

            // 代理归属
            if (null != record.getAgentAttribution()) {
                AgentAttributionEnum agentAttributionEnum = AgentAttributionEnum.nameOfCode(record.getAgentAttribution());
                record.setAgentAttributionName(null == agentAttributionEnum ? null : agentAttributionEnum.getName());
            }

            // 会员标签
            if (StrUtil.isNotEmpty(record.getUserLabelId())) {
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
                        }).toList(); // 收集结果到列表中
                if (CollUtil.isNotEmpty(userLabelList)) {
                    String str = userLabelList.stream()
                            .map(GetUserLabelByIdsResVO::getLabelName)
                            .collect(Collectors.joining(","));
                    record.setUserLabelIdName(str);
                    record.setUserLabelIdNames(userLabelList);
                }
            }

            // 风控层级
            if (null != record.getRiskLevelId()) {
                RiskLevelDetailsVO riskLevelDetailsVO = riskLevelDetailsVOMap.get(record.getRiskLevelId());
                record.setRiskLevelIdName(riskLevelDetailsVO == null ? "" : riskLevelDetailsVO.getRiskControlLevel());
            }
            if (ObjectUtils.isNotEmpty(record.getVipRankCode())) {
                record.setVipRankCodeName(vipRankMap.get(record.getVipRankCode()));
            }
            if (ObjectUtils.isNotEmpty(record.getVipGradeCode())) {
                record.setVipGradeCodeName(vipGradeMap.get(record.getVipGradeCode()));
            }
            record.setConvertPlatCurrency(vo.getConvertPlatCurrency());

        }
        // 本页合计
        UserWinLoseResponseVO currentPage = new UserWinLoseResponseVO();
        currentPage.setUserAccount("本页合计");
        currentPage.setBetNum(betNumAll.get());
        currentPage.setBetAmount(betAmountAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setValidBetAmount(validBetAmountAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setRunWaterCorrect(runWaterCorrectAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setBetWinLose(betWinLoseAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setActivityAmount(activityAmountAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setAlreadyUseAmount(alreadyUseAmountAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setRebateAmount(rebateAmountAll.get().setScale(4, RoundingMode.DOWN));

        currentPage.setVipAmount(vipAmountAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setAdjustAmount(adjustAmountAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setRepairOrderOtherAdjust(repairOrderOtherAdjustAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setProfitAndLoss(profitAndLossAll.get().setScale(4, RoundingMode.DOWN));
        currentPage.setPlatAdjustAmount(platAdjustAmount.get().setScale(4, RoundingMode.DOWN));
        currentPage.setTipsAmount(tipsAmount.get().setScale(4, RoundingMode.DOWN));
        currentPage.setRiskAmount(riskAmount.get().setScale(4, RoundingMode.DOWN));
        if (convertPlat) {
            currentPage.setMainCurrency(platCurrencycode);
        }
        result.setCurrentPage(currentPage);

        // 全部合计
        List<UserWinLoseResponseVO> totalPages = reportUserWinLoseRepository.getTotalPage(vo);
        UserWinLoseResponseVO totalPage = new UserWinLoseResponseVO();
        if (CollectionUtil.isNotEmpty(totalPages)) {
            // 注单量
            AtomicReference<Integer> betNumTotal = new AtomicReference<>(CommonConstant.business_zero);
            // 投注金额
            AtomicReference<BigDecimal> betAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 有效投注
            AtomicReference<BigDecimal> validBetAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 流水纠正
            AtomicReference<BigDecimal> runWaterCorrectTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 投注盈亏
            AtomicReference<BigDecimal> betWinLoseTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 返水金额
            AtomicReference<BigDecimal> rebateAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 优惠金额
            AtomicReference<BigDecimal> activityAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 已使用金额
            AtomicReference<BigDecimal> alreadyUseAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            // vip 福利
            AtomicReference<BigDecimal> vipAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 调整金额(其他调整)
            AtomicReference<BigDecimal> adjustAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 补单其他调整
            AtomicReference<BigDecimal> repairOrderOtherAdjustTotal = new AtomicReference<>(BigDecimal.ZERO);

            AtomicReference<BigDecimal> profitAndLossTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 调整金额(其他调整)-平台币
            AtomicReference<BigDecimal> platAdjustAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 打赏金额
            AtomicReference<BigDecimal> tipsAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            // 风控金额
            AtomicReference<BigDecimal> riskAmountTotal = new AtomicReference<>(BigDecimal.ZERO);
            for (UserWinLoseResponseVO record : totalPages) {
                BigDecimal rate;
                if (convertPlat) {
                    rate = allFinalRate.get(record.getMainCurrency());
                    if (rate == null) {
                        throw new BaowangDefaultException("汇率未配置，货币是:" + record.getMainCurrency() + ",用户是：" + record.getUserId());
                    }
                } else {
                    rate = BigDecimal.ONE;
                }
                betNumTotal.updateAndGet(v -> v + record.getBetNum());
                betAmountTotal.updateAndGet(v -> v.add(record.getBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                validBetAmountTotal.updateAndGet(v -> v.add(record.getValidBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                runWaterCorrectTotal.updateAndGet(v -> v.add(record.getRunWaterCorrect().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                betWinLoseTotal.updateAndGet(v -> v.add(record.getBetWinLose().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                vipAmountTotal.updateAndGet(v -> v.add(record.getVipAmount()));
                activityAmountTotal.updateAndGet(v -> v.add(record.getActivityAmount()));
                adjustAmountTotal.updateAndGet(v -> v.add(record.getAdjustAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                alreadyUseAmountTotal.updateAndGet(v -> v.add(record.getAlreadyUseAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                // 返水是主货币
                rebateAmountTotal.updateAndGet(v -> v.add(record.getRebateAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                repairOrderOtherAdjustTotal.updateAndGet(v -> v.add(record.getRepairOrderOtherAdjust().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                profitAndLossTotal.updateAndGet(v -> v.add(record.getProfitAndLoss().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                platAdjustAmountTotal.updateAndGet(v -> v.add(record.getPlatAdjustAmount()));
                tipsAmountTotal.updateAndGet(v -> v.add(record.getTipsAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                riskAmountTotal.updateAndGet(v -> v.add(record.getRiskAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

            }

            totalPage.setBetNum(betNumTotal.get());
            totalPage.setBetAmount(betAmountTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setValidBetAmount(validBetAmountTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setRunWaterCorrect(runWaterCorrectTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setBetWinLose(betWinLoseTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setActivityAmount(activityAmountTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setAlreadyUseAmount(alreadyUseAmountTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setRebateAmount(rebateAmountTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setVipAmount(vipAmountTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setAdjustAmount(adjustAmountTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setPlatAdjustAmount(platAdjustAmountTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setTipsAmount(tipsAmountTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setRiskAmount(riskAmountTotal.get().setScale(4, RoundingMode.DOWN));


            totalPage.setRepairOrderOtherAdjust(repairOrderOtherAdjustTotal.get().setScale(4, RoundingMode.DOWN));
            totalPage.setProfitAndLoss(profitAndLossTotal.get().setScale(4, RoundingMode.DOWN));
            if (convertPlat) {
                totalPage.setMainCurrency(platCurrencycode);
            }
        }
        result.setTotalPage(totalPage);
        result.setPageList(pageResult);

        return ResponseVO.success(result);
    }

    private ResponseVO<UserWinLoseResult> checkParam(UserWinLosePageVO vo) {

        List<String> days = TimeZoneUtils.getBetweenDates(vo.getStartDay(), vo.getEndDay(), vo.getTimeZone());
        int between = 0;
        if (CollectionUtil.isNotEmpty(days)) {
            between = days.size();
        }
        //long between = DateUtil.between(startDayDate, endDayDate, DateUnit.DAY);
        if (between > 31) {
            // todo wade
            return ResponseVO.fail(ResultCode.DATE_MAX_SPAN_31);
        }
        return null;
    }

    public ReportAgentTeamVO getNewTeamOrderInfo(ReportAgentUserWinLossVO vo) {
        String siteCode = vo.getSiteCode();
        ReportAgentTeamVO result = new ReportAgentTeamVO();
        result.setSiteCode(siteCode);
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(siteCode); // 获取所有币种汇率

        AgentUserWinLossParam param = new AgentUserWinLossParam();
        param.setUnderAgentAccount(vo.getUnderAgentAccount());
        param.setSiteCode(siteCode);
        Long todayStartTime = DateUtils.getTodayStartTime(vo.getTimeZone());
        Long todayEndTime = DateUtils.getTodayEndTime(vo.getTimeZone());
        param.setStartTime(todayStartTime);
        param.setEndTime(todayEndTime);
        log.debug("查询当日投注信息:{}", param);
        List<AgentWInLossInfoVO> todayVOs = reportUserWinLoseRepository.queryAgentWinLossInfo(param);
        if (!CollectionUtils.isEmpty(todayVOs)) {
            for (AgentWInLossInfoVO todayVO : todayVOs) {
                BigDecimal currencyRate = allFinalRate.get(todayVO.getMainCurrency());
                //平台币
                result.addTodayDiscount(todayVO.getTotalDiscount());
                result.addTodayRebate(AmountUtils.divide(todayVO.getTotalRebate(), currencyRate));
                result.addTodayWinLoss(AmountUtils.divide(todayVO.getTotalWinLoss(), currencyRate));
                result.addTodayTotalBetAmount(AmountUtils.divide(todayVO.getTotalBetAmount(), currencyRate));
                result.addTodayTotalValidBetAmount(AmountUtils.divide(todayVO.getValidAmount(), currencyRate));
                result.addTodayTotalWinLoss(AmountUtils.divide(todayVO.getBetWinLoss(), currencyRate));
            }
        }
        param = new AgentUserWinLossParam();
        param.setUnderAgentAccount(vo.getUnderAgentAccount());
        param.setSiteCode(siteCode);
        param.setStartTime(vo.getStartTime());
        param.setEndTime(vo.getEndTime());
        log.debug("查询本期投注信息:{}", param);
        List<AgentWInLossInfoVO> monthVOs = reportUserWinLoseRepository.queryAgentWinLossInfo(param);
        if (!CollectionUtils.isEmpty(monthVOs)) {
            for (AgentWInLossInfoVO monthVO : monthVOs) {
                BigDecimal currencyRate = allFinalRate.get(monthVO.getMainCurrency());
                result.addMonthDiscount(monthVO.getTotalDiscount());
                result.addMonthRebate(AmountUtils.divide(monthVO.getTotalRebate(), currencyRate));
                result.addMonthWinLoss(AmountUtils.divide(monthVO.getTotalWinLoss(), currencyRate));
                result.addMonthTotalBetAmount(AmountUtils.divide(monthVO.getTotalBetAmount(), currencyRate));
                result.addMonthTotalValidBetAmount(AmountUtils.divide(monthVO.getValidAmount(), currencyRate));
                result.addMonthTotalWinLoss(AmountUtils.divide(monthVO.getBetWinLoss(), currencyRate));
            }
        }
        return result;
    }

    public List<HashMap> getAgentUserRecordByUserWinLoss(final ReportAgentUserTeamParam param) {
        var result = reportUserWinLoseRepository.getAgentUserRecordByUserWinLoss(param);
        if (CollUtil.isEmpty(result)) {
            return Collections.emptyList();
        }
        return result;

    }

    public ReportUserBetsVO getUserBetsInfo(String userAccount, String siteCode) {
        return reportUserWinLoseRepository.getUserBetsInfo(userAccount, siteCode);
    }


    public List<ReportAgentSubLineResVO> getUserWinLoseByAgent(ReportAgentSubLineReqVO reqVO) {
        return reportUserWinLoseRepository.getUserWinLoseByAgent(reqVO);
    }

    public ActiveByAgentVO getActiveInfoByAgent(ReportAgentUserTeamParam param) {
        ActiveByAgentVO agentVO = new ActiveByAgentVO();
        List<AgentCommissionPlanVO> planCodes = agentCommissionPlanApi.getPlanBySiteAndCodes(param.getSiteCode(), Lists.newArrayList(param.getPlanCode()));
        if (CollectionUtil.isEmpty(planCodes)) {
            log.info("佣金方案不存在");
            return agentVO;
        }
        AgentCommissionPlanVO cPlan = planCodes.get(0);
        // 有效活跃
        BigDecimal validActiveBetAmount = cPlan.getActiveBet();
        BigDecimal validActiveDepositAmount = cPlan.getActiveDeposit();
        // 有效活跃
        List<Map<String, String>> validBetActive = reportUserWinLoseRepository.queryActiveDirectBetList(param.getStartTime(), param.getEndTime(), validActiveBetAmount, param.getAllDownAgentUser());
        List<Map<String, String>> validDepositActive = reportUserWinLoseRepository.queryActiveDirectDepositList(param.getStartTime(), param.getEndTime(), validActiveDepositAmount, param.getAllDownAgentUser());

        Map<String, List<Map<String, String>>> betMap = Optional.of(validBetActive.stream().collect(Collectors.groupingBy((Map<String, String> m) -> m.get("superAgentAccount")))).orElse(new HashMap<>());

        Map<String, List<Map<String, String>>> depositMap = Optional.of(validDepositActive.stream().collect(Collectors.groupingBy((Map<String, String> m) -> m.get("superAgentAccount")))).orElse(new HashMap<>());
        Map<String, Set<String>> validActive = com.google.common.collect.Maps.newHashMap();
        Set<String> activeSet = Sets.newHashSet();
        activeSet.addAll(betMap.keySet());
        activeSet.addAll(depositMap.keySet());
        for (String account : activeSet) {
            Set<String> activeAccount = Sets.newHashSet();
            Set<String> depositAccount = Sets.newHashSet();
            if (betMap.containsKey(account)) {
                activeAccount = betMap.get(account).stream().map(obj -> obj.get("userAccount")).collect(Collectors.toSet());
            }
            if (depositMap.containsKey(account)) {
                depositAccount = depositMap.get(account).stream().map(obj -> obj.get("userAccount")).collect(Collectors.toSet());
            }
            activeAccount.addAll(depositAccount);
            validActive.put(account, activeAccount);
        }
        // 有效新增
        BigDecimal validAddBetAmount = cPlan.getValidBet();
        BigDecimal validAddDepositAmount = cPlan.getValidDeposit();
        // 有效新增
        List<Map<String, String>> betActive = reportUserWinLoseRepository.queryActiveDirectBetList(param.getStartTime(), param.getEndTime(), validAddBetAmount, param.getAllDownAgentUser());
        List<Map<String, String>> depositActive = reportUserWinLoseRepository.queryActiveDirectDepositList(param.getStartTime(), param.getEndTime(), validAddDepositAmount, param.getAllDownAgentUser());


        Map<String, List<Map<String, String>>> validBetMap = Optional.ofNullable(betActive.stream().collect(Collectors.groupingBy((Map<String, String> m) -> m.get("superAgentAccount")))).orElse(new HashMap<>());

        Map<String, List<Map<String, String>>> validDepositMap = Optional.ofNullable(depositActive.stream().collect(Collectors.groupingBy((Map<String, String> m) -> m.get("superAgentAccount")))).orElse(new HashMap<>());
        Map<String, Set<String>> validAdd = com.google.common.collect.Maps.newHashMap();
        Set<String> validSet = Sets.newHashSet();
        validSet.addAll(validBetMap.keySet());
        validSet.addAll(validDepositMap.keySet());
        for (String account : validSet) {
            Set<String> validActiveAccount = Sets.newHashSet();
            Set<String> validDepositAccount = Sets.newHashSet();
            if (validBetMap.containsKey(account)) {
                validActiveAccount = validBetMap.get(account).stream().map(obj -> obj.get("userAccount")).collect(Collectors.toSet());
            }
            if (validDepositMap.containsKey(account)) {
                validDepositAccount = validDepositMap.get(account).stream().map(obj -> obj.get("userAccount")).collect(Collectors.toSet());
            }
            validActiveAccount.addAll(validDepositAccount);
            validAdd.put(account, validActiveAccount);
        }
        agentVO.setValidAdd(validAdd);
        agentVO.setValidActive(validActive);
        return agentVO;
    }

    public List<GetBetNumberByAgentIdVO> getBetNumberByAgentId(String siteCode, Long start, Long end, String agentId, String userId) {
        return reportUserWinLoseRepository.getBetNumberByAgentId(siteCode, start, end, agentId, userId);
    }

    public ReportAgentActiveVO getAgentActiveInfo(ReportAgentUserTeamParam param) {
        ReportAgentActiveVO result = new ReportAgentActiveVO();
        // 今天，本月活跃
        ReportAgentActiveVO nowAgent = getAgentActiveInfo(param);
        param.setStartTime(DateUtil.beginOfDay(DateUtil.yesterday()).getTime());
        param.setEndTime(DateUtil.endOfDay(DateUtil.yesterday()).getTime());
        // 昨天
        ReportAgentActiveVO beforeAgent = getAgentActiveInfo(param);
        // 上个月
        param.setStartTime(DateUtil.beginOfMonth(DateUtil.lastMonth()).getTime());
        param.setEndTime(DateUtil.endOfMonth(DateUtil.lastMonth()).getTime());
        ReportAgentActiveVO lastMonthAgent = getAgentActiveInfo(param);
        result.setTodayActive(intersection(nowAgent.getTodayActive(), beforeAgent.getTodayActive()));
        result.setTodayValidActive(intersection(nowAgent.getTodayValidActive(), beforeAgent.getTodayValidActive()));
        result.setMonthActive(intersection(nowAgent.getMonthActive(), lastMonthAgent.getMonthActive()));
        result.setMonthValidActive(intersection(nowAgent.getMonthValidActive(), lastMonthAgent.getMonthValidActive()));
        return result;

    }

    public DailyWinLoseResult dailyWinLosePage(DailyWinLosePageVO vo) {
        DailyWinLoseResult winLoseResult = new DailyWinLoseResult();
        String siteCode = vo.getSiteCode();
        Page<DailyWinLoseResponseVO> page = new Page(vo.getPageNumber(), vo.getPageSize());
        Page<DailyWinLoseResponseVO> pageResult = reportUserWinLoseRepository.dailyWinLosePage(page, vo, siteCode);

        Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        // 当前页总数
        List<DailyWinLoseResponseVO> records = pageResult.getRecords();

//        DailyWinLoseResponseVO curResult = new DailyWinLoseResponseVO();
        if (CollectionUtil.isNotEmpty(records)) {
            for (DailyWinLoseResponseVO record : records) {
                // 盈利取反
//                平台净盈利 = -（用户投注输赢-打赏金额+已使用优惠+人工加减额的其他调整）+返水金额
                BigDecimal profit = record.getBetWinLose().subtract(record.getTipsAmount()).add(record.getAlreadyUseAmount()).add(record.getAdjustAmount());
                record.setProfitAndLoss(profit.negate());
                record.setBetWinLose(record.getBetWinLose().negate());
                String mainCurrency = record.getMainCurrency();
                // 当前页总数
                BigDecimal rate = currency2Rate.get(mainCurrency);

                // 分页内容转为平台币
                if (vo.isToPlatCurr()) {
                    record.setAdjustAmount(AmountUtils.divide(record.getAdjustAmount(), rate, 4));
                    record.setBetWinLose(AmountUtils.divide(record.getBetWinLose(), rate, 4));
                    record.setBetAmount(AmountUtils.divide(record.getBetAmount(), rate, 4));
                    record.setProfitAndLoss(AmountUtils.divide(record.getProfitAndLoss(), rate, 4));
                    record.setValidBetAmount(AmountUtils.divide(record.getValidBetAmount(), rate, 4));
                    record.setAlreadyUseAmount(AmountUtils.divide(record.getAlreadyUseAmount(), rate, 4));
                    record.setTipsAmount(AmountUtils.divide(record.getTipsAmount(), rate, 4));
                    record.setRebateAmount(AmountUtils.divide(record.getRebateAmount(), rate, 4));
                }
                record.setPlatformCurrency(CommonConstant.PLAT_CURRENCY_CODE);

//                curResult.setBetNum(curResult.getBetNum() + record.getBetNum());
//                curResult.setActivityAmount(curResult.getActivityAmount().add(record.getActivityAmount()));
//                curResult.setAdjustAmount(curResult.getAdjustAmount().add(record.getAdjustAmount()));
//                curResult.setBetWinLose(curResult.getBetWinLose().add(record.getBetWinLose()));
//                curResult.setBetAmount(curResult.getBetAmount().add(record.getBetAmount()));
//                curResult.setProfitAndLoss(curResult.getProfitAndLoss().add(record.getProfitAndLoss()));
//                curResult.setValidBetAmount(curResult.getValidBetAmount().add(record.getValidBetAmount()));
//                curResult.setVipAmount(curResult.getVipAmount().add(record.getVipAmount()));
//                curResult.setBetMemNum(curResult.getBetMemNum() + record.getBetMemNum());
//                curResult.setAlreadyUseAmount(curResult.getAlreadyUseAmount().add(record.getAlreadyUseAmount()));
//                curResult.setRebateAmount(curResult.getRebateAmount().add(record.getRebateAmount()));
//                curResult.setPlatAdjustAmount(curResult.getPlatAdjustAmount().add(record.getPlatAdjustAmount()));
//                curResult.setTipsAmount(curResult.getTipsAmount().add(record.getTipsAmount()));
//                curResult.setDayStr("本页合计");

            }
            if ("profitAndLoss".equals(vo.getOrderField())) {
                if (vo.getOrderType().equals("asc")) {
                    records.sort(Comparator.comparing(DailyWinLoseResponseVO::getProfitAndLoss).reversed());
                } else if (vo.getOrderType().equals("desc")) {
                    records.sort(Comparator.comparing(DailyWinLoseResponseVO::getProfitAndLoss));
                }
            }
        }

        // 总数
        DailyWinLoseResponseVO totalResult = new DailyWinLoseResponseVO();
        List<DailyWinLoseResponseVO> totalList = reportUserWinLoseRepository.dailyWinLoseTotal(vo, siteCode);
        if (CollectionUtil.isNotEmpty(totalList)) {
            for (DailyWinLoseResponseVO record : totalList) {
                // 内容转为平台币
                BigDecimal profit = record.getBetWinLose().subtract(record.getTipsAmount()).add(record.getAlreadyUseAmount()).add(record.getAdjustAmount());
                record.setProfitAndLoss(profit.negate());
                record.setBetWinLose(record.getBetWinLose().negate());
                String mainCurrency = record.getMainCurrency();
                // 当前页总数
                BigDecimal rate = currency2Rate.get(mainCurrency);

                // 分页内容转为平台币
                if (vo.isToPlatCurr()) {
                    record.setAdjustAmount(AmountUtils.divide(record.getAdjustAmount(), rate, 4));
                    record.setBetWinLose(AmountUtils.divide(record.getBetWinLose(), rate, 4));
                    record.setBetAmount(AmountUtils.divide(record.getBetAmount(), rate, 4));
                    record.setProfitAndLoss(AmountUtils.divide(record.getProfitAndLoss(), rate, 4));
                    record.setValidBetAmount(AmountUtils.divide(record.getValidBetAmount(), rate, 4));
                    record.setAlreadyUseAmount(AmountUtils.divide(record.getAlreadyUseAmount(), rate, 4));
                    record.setTipsAmount(AmountUtils.divide(record.getTipsAmount(), rate, 4));
                    record.setRebateAmount(AmountUtils.divide(record.getRebateAmount(), rate, 4));
                }
                record.setPlatformCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                totalResult.setBetNum(totalResult.getBetNum() + record.getBetNum());
                totalResult.setActivityAmount(totalResult.getActivityAmount().add(record.getActivityAmount()));
                totalResult.setAdjustAmount(totalResult.getAdjustAmount().add(record.getAdjustAmount()));
                totalResult.setBetWinLose(totalResult.getBetWinLose().add(record.getBetWinLose()));
                totalResult.setBetAmount(totalResult.getBetAmount().add(record.getBetAmount()));
                totalResult.setProfitAndLoss(totalResult.getProfitAndLoss().add(record.getProfitAndLoss()));
                totalResult.setValidBetAmount(totalResult.getValidBetAmount().add(record.getValidBetAmount()));
                totalResult.setVipAmount(totalResult.getVipAmount().add(record.getVipAmount()));
                totalResult.setBetMemNum(totalResult.getBetMemNum() + record.getBetMemNum());
                totalResult.setAlreadyUseAmount(totalResult.getAlreadyUseAmount().add(record.getAlreadyUseAmount()));
                totalResult.setRebateAmount(totalResult.getRebateAmount().add(record.getRebateAmount()));
                totalResult.setPlatAdjustAmount(totalResult.getPlatAdjustAmount().add(record.getPlatAdjustAmount()));
                totalResult.setTipsAmount(totalResult.getTipsAmount().add(record.getTipsAmount()));
                totalResult.setDayStr("全部合计");
            }
        }
        winLoseResult.setPageList(pageResult);
//        winLoseResult.setCurrentPage(curResult);
        winLoseResult.setTotalPage(totalResult);
        return winLoseResult;
    }

    public Long dailyWinLosePageCount(DailyWinLosePageVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        return reportUserWinLoseRepository.dailyWinLosePageCount(vo, siteCode);
    }

    public ResponseVO<Page<ClickUserAccountResponseVO>> clickUserAccount(ClickUserAccountPageVO vo) {
        checkParamDetail(vo);
        boolean convertPlat = vo.getConvertPlatCurrency();
        Page<ClickUserAccountVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        vo.setUtcStr(TimeZoneUtils.getTimeZoneUTC(vo.getTimeZone()));
        Page<ClickUserAccountVO> pageResult = reportUserWinLoseRepository.clickUserAccount(page, vo);
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(vo.getSiteCode()); // 获取所有币种汇率
        if (CollectionUtil.isNotEmpty(pageResult.getRecords())) {
            for (ClickUserAccountVO record : pageResult.getRecords()) {

                if (vo.getConvertPlatCurrency()) {
                    BigDecimal rate = allFinalRate.get(record.getMainCurrency());
                    if (rate == null) {
                        throw new BaowangDefaultException("汇率未配置，货币是:" + record.getMainCurrency() + ",用户是：" + vo.getUserAccount());
                    }
                    record.setBetAmount(record.getBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                    record.setValidBetAmount(record.getValidBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                    record.setRunWaterCorrect(record.getRunWaterCorrect().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                    record.setBetWinLose(record.getBetWinLose().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                    record.setVipAmount(record.getVipAmount().setScale(4, RoundingMode.DOWN));
                    record.setActivityAmount(record.getActivityAmount().setScale(4, RoundingMode.DOWN));
                    record.setRebateAmount(record.getRebateAmount().setScale(4, RoundingMode.DOWN));
                    record.setAdjustAmount(record.getAdjustAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                    record.setAlreadyUseAmount(record.getAlreadyUseAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                    record.setProfitAndLoss(record.getProfitAndLoss().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                    record.setTipsAmount(record.getTipsAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                    record.setRiskAmount(record.getRiskAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(4, RoundingMode.DOWN));
                    record.setPlatAdjustAmount(record.getPlatAdjustAmount().setScale(4, RoundingMode.DOWN));
                    //record.setMainCurrency(vo.getPlatCurrencyCode());
                }
            }
        }

        Map<String, List<ClickUserAccountVO>> map = pageResult.getRecords().stream().collect(Collectors.groupingBy(ClickUserAccountVO::getDayMillis));
        // 对Map的key降序排序
        TreeMap<String, List<ClickUserAccountVO>> treeMap = new TreeMap<>(Collections.reverseOrder());
        treeMap.putAll(map);

        List<ClickUserAccountResponseVO> resultList = Lists.newArrayList();
        treeMap.forEach((key, value) -> {
            ClickUserAccountResponseVO responseVO = new ClickUserAccountResponseVO();
            responseVO.setDay(key);
            responseVO.setDayAmount(value);
            resultList.add(responseVO);
        });
        Page<ClickUserAccountResponseVO> result = new Page<>(vo.getPageNumber(), vo.getPageSize(), pageResult.getTotal());
        result.setRecords(resultList);
        return ResponseVO.success(result);
    }

    private void checkParamDetail(ClickUserAccountPageVO vo) {
        Date startDayDate = new Date(vo.getStartDay());
        Date endDayDate = new Date(vo.getEndDay());

        long between = DateUtil.between(startDayDate, endDayDate, DateUnit.DAY);
        if (between > 31) {
            // todo wade 添加时间限制
            //throw new BaowangDefaultException(ResultCode.DATE_MAX_SPAN_31);
        }
    }

    /**
     * 根据时间范围查询 有效投注 最大值
     *
     * @param firstDayMilli 开始日期
     * @param lastDayMilli  结束日期
     * @param siteCode      站点
     * @return 返回结果
     */
    public List<ReportUserWinLosePO> queryValidBetAmountMaxByTime(Long firstDayMilli, Long lastDayMilli, String siteCode) {
        return reportUserWinLoseRepository.queryValidBetAmountMaxByTime(firstDayMilli, lastDayMilli, siteCode);
    }

    /**
     * 根据时间范围查询 输赢 最大值
     *
     * @param firstDayMilli 开始时间
     * @param lastDayMilli  结束时间
     * @param siteCode      站点
     * @return 返回
     */
    public List<ReportUserWinLosePO> queryWinLoseAmountMaxByTime(Long firstDayMilli, Long lastDayMilli, String siteCode) {
        return reportUserWinLoseRepository.queryWinLoseAmountMaxByTime(firstDayMilli, lastDayMilli, siteCode);
    }

    public List<ReportAgentWinLoseVO> getUserWinLossByAgentIds(ReportAgentWinLossParamVO paramVO) {
        if (CollectionUtils.isEmpty(paramVO.getAgentIds())) {
            return Lists.newArrayList();
        }
        return reportUserWinLoseRepository.getUserWinLossByAgentIds(paramVO);
    }

    /**
     * 获取变更日期范围内所有会员详细盈亏明细
     * 统计时间在之前，更新时间在后面
     *
     * @param vo 入参
     */
    ResponseVO<List<MemberWinLossDetailVO>> allChangeMemWinLostDetail(AgentUserWinLossDetailsPageVO vo) {
        return ResponseVO.success(reportUserWinLoseRepository.allChangeMemWinLostDetail(vo));

    }

    public List<UserWinLoseAgentVO> queryByTimeAndAgent(UserWinLoseAgentReqVO vo) {
        return reportUserWinLoseRepository.queryByTimeAndAgent(vo);
    }

    public Page<UserWinLoseListResponseVO> listPage(UserWinLoseListPageCondVO vo) {
        Page<ReportUserWinLosePO> page = new Page<ReportUserWinLosePO>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<ReportUserWinLosePO> lambdaQueryWrapper = new LambdaQueryWrapper<ReportUserWinLosePO>();
        lambdaQueryWrapper.eq(ReportUserWinLosePO::getSiteCode, vo.getSiteCode());
        if (vo.getAccountType() != null) {
            lambdaQueryWrapper.ge(ReportUserWinLosePO::getAccountType, vo.getAccountType());
        }
        if (vo.getStartDayMillis() != null) {
            lambdaQueryWrapper.ge(ReportUserWinLosePO::getDayMillis, vo.getStartDayMillis());
        }
        if (vo.getEndDayMillis() != null) {
            lambdaQueryWrapper.le(ReportUserWinLosePO::getDayMillis, vo.getEndDayMillis());
        }
        Page<ReportUserWinLosePO> reportUserWinLosePOPage = this.baseMapper.selectPage(page, lambdaQueryWrapper);
        Page<UserWinLoseListResponseVO> resultPage = new Page<UserWinLoseListResponseVO>();
        BeanUtils.copyProperties(reportUserWinLosePOPage, resultPage);
        List<UserWinLoseListResponseVO> userWinLoseResponseVOS = Lists.newArrayList();
        for (ReportUserWinLosePO reportUserWinLosePO : reportUserWinLosePOPage.getRecords()) {
            UserWinLoseListResponseVO userWinLoseResponseVO = new UserWinLoseListResponseVO();
            BeanUtils.copyProperties(reportUserWinLosePO, userWinLoseResponseVO);
            userWinLoseResponseVOS.add(userWinLoseResponseVO);
        }
        resultPage.setRecords(userWinLoseResponseVOS);
        return resultPage;
    }

    public Map<String, Map<String, List<UserWinLoseResponseVO>>> selectGroup(Long startTime, Long endTime, String siteCode) {
        LambdaQueryWrapper<ReportUserWinLosePO> query = Wrappers.lambdaQuery();
        query.ge(ReportUserWinLosePO::getDayHourMillis, startTime).lt(ReportUserWinLosePO::getDayHourMillis, endTime);
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(ReportUserWinLosePO::getSiteCode, siteCode);
        }
        query.eq(ReportUserWinLosePO::getAccountType, UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        List<ReportUserWinLosePO> list = this.list(query);
        if (CollectionUtil.isNotEmpty(list)) {
            List<UserWinLoseResponseVO> vos = BeanUtil.copyToList(list, UserWinLoseResponseVO.class);
            return vos.stream().collect(Collectors.groupingBy(UserWinLoseResponseVO::getSiteCode, Collectors.groupingBy(UserWinLoseResponseVO::getMainCurrency)));
        }
        return new HashMap<>();
    }

    public ResponseVO<List<MemberWinLossDetailVO>> allMemWinLostDetail(AgentUserWinLossDetailsPageVO vo) {
        return ResponseVO.success(reportUserWinLoseRepository.allMemWinLostDetail(vo));
    }


    /**
     * 获取代理 当期投注人数
     *
     * @param userWinLoseAgentReqVO 参数
     * @return
     */
    public Long getBetUserNum(UserWinLoseAgentReqVO userWinLoseAgentReqVO) {
        return reportUserWinLoseRepository.getBetUserNum(userWinLoseAgentReqVO);
    }

    /*@Scheduled(cron = "0 3 10 * * ?")
    public void test() {
        ReportRecalculateVO param = new ReportRecalculateVO();
        param.setStartTime(1733068799000L);
        param.setEndTime(1735574399000L);
        param.setSiteCode("Vd438R");
        addReportWinLoseRecord(param);
    }*/


    /**
     * 定时任务，每个小时统计一条数据¬
     *
     * @param requestParam 入参
     */
    @Transactional(rollbackFor = Exception.class)
    public void addReportWinLoseRecord(ReportRecalculateVO requestParam) {
        log.info("会员盈亏报表:report服务参数:{}", JSONObject.toJSONString(requestParam));
        // needRunTimes 是每个小时UTC开始时间
        List<Long> needRunTimes = new ArrayList<>();
        // 重新跑 ，计算起止时间
        // 插入前进行删除
        // 计算需要跑的整点时间
        computerTime(requestParam.getStartTime(), requestParam.getEndTime(), needRunTimes);
        log.info("时间：{}", JSONObject.toJSONString(needRunTimes));
        // 进行定时任务逻辑
        if (CollectionUtil.isNotEmpty(needRunTimes)) {
            // 是否指定了siteCode
            ResponseVO<List<SiteVO>> listResponseVO = siteApi.siteInfoAllstauts();
            if (!listResponseVO.isOk()) {
                log.error("任务报表查询站点出错了:{}", JSONObject.toJSONString(listResponseVO));
                return;
            }
            List<SiteVO> siteVOs = listResponseVO.getData();
            if (StringUtils.isNotBlank(requestParam.getSiteCode())) {
                for (SiteVO siteVO : siteVOs) {
                    if (StringUtils.equals(siteVO.getSiteCode(), requestParam.getSiteCode())) {
                        for (Long dataHourTime : needRunTimes) {
                            handleGenerateReportData(siteVO, dataHourTime, requestParam);
                        }
                        break;
                    }
                }

            } else {
                for (Long dataHourTime : needRunTimes) {
                    for (SiteVO siteVO : siteVOs) {
                        handleGenerateReportData(siteVO, dataHourTime, requestParam);
                    }
                }
            }
        }
    }

    private void handleGenerateReportData(SiteVO siteVO, Long dataHourTime, ReportRecalculateVO vo) {
        // 统计 投注信息，先查投注信息，有数据，拿出100条，然后插入数据。一条一条插入
        log.info("任务领取记录报表:report服务参数dataHourTime:{}", dataHourTime);
        // 整点开始时间
        long startTime = dataHourTime;
        long endTime = TimeZoneUtils.convertToUtcEndOfHour(dataHourTime);
        String siteCode = siteVO.getSiteCode();
        String timeZone = siteVO.getTimezone();
        // 删除这个时间间隔的数据
        // 删除存量数据
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(StrUtil.isNotBlank(siteCode), ReportUserWinLosePO::getSiteCode, siteCode)
                .eq(ReportUserWinLosePO::getDayHourMillis, startTime)
                .remove();
        log.info("会员盈亏重算删除存在的数据: siteCode: {}, dayHourMillis: {}", siteCode, startTime);

        // 查询数据，更新 投注单量，投注金额，投注盈亏
        int pages = 1;
        int size = 500;
        WinLoseRecalculateReqVO reqVO = new WinLoseRecalculateReqVO().setSiteCode(siteCode).setStartTime(startTime).setEndTime(endTime);
        reqVO.setPageSize(size);
        List<WinLoseRecalculateVO> records;
        // 新加入的记录，每次都清空
        List<ReportUserWinLosePO> poList = Lists.newArrayList();
        do {
            reqVO.setPageNumber(pages);
            Page<WinLoseRecalculateVO> recalculateVOPage = orderRecordApi.winLoseRecalculatePage(reqVO);
            records = recalculateVOPage.getRecords();
            log.debug("会员盈亏重算 获取的记录 {}, records size: {}", pages, records.size());
            log.info("会员盈亏重算 获取的记录  records size: {}", records.size());
            if (CollUtil.isNotEmpty(records)) {
                records.forEach(record -> {
                    record.setDayHourMillis(startTime);
                    record.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(startTime, timeZone));
                    ReportUserWinLosePO po = new ReportUserWinLosePO();
                    po.setBetNum(0);
                    po.setBetAmount(BigDecimal.ZERO);
                    po.setValidBetAmount(BigDecimal.ZERO);
                    po.setRunWaterCorrect(BigDecimal.ZERO);
                    po.setBetWinLose(BigDecimal.ZERO);
                    po.setRebateAmount(BigDecimal.ZERO);
                    po.setActivityAmount(BigDecimal.ZERO);
                    po.setAdjustAmount(BigDecimal.ZERO);
                    po.setRepairOrderOtherAdjust(BigDecimal.ZERO);
                    po.setProfitAndLoss(BigDecimal.ZERO);
                    // 已经使用优惠，根据当时汇率转换为主货币
                    po.setAlreadyUseAmount(BigDecimal.ZERO);
                    // vip福利，根据当时汇率转换为主货币
                    po.setVipAmount(BigDecimal.ZERO);
                    po.setPlatAdjustAmount(BigDecimal.ZERO);
                    po.setTipsAmount(BigDecimal.ZERO);
                    po.setRiskAmount(BigDecimal.ZERO);
                    BeanUtil.copyProperties(record, po);
                    po.setUpdatedTime(System.currentTimeMillis());
                    po.setAccountType(record.getAccountType());
                    poList.add(po);
                });
                // 新增
                saveBatch(poList);
                poList.clear();
            }
            pages++;

        } while (CollUtil.isNotEmpty(records));
        log.info("会员盈亏重算 投注信息 for siteCode: {}, dayHourMillis: {}", siteCode, startTime);
        //
        WinLoseRecalculateReqVO reqFreeSpinVO = new WinLoseRecalculateReqVO().setSiteCode(siteCode).setStartTime(startTime).setEndTime(endTime);

        List<WinLoseRecalculateFeelSpinVO> winLoseRecalculateFeelSpinVOS = orderRecordApi.winLoseRecalculateFreeSpinPage(reqFreeSpinVO);
        log.info("会员盈亏重算 免费 spins for siteCode: {}, dayHourMillis: {}", siteCode, startTime);
        // pp免费旋转 -> start

        //log.debug("会员盈亏重算 获取的记录 {}, records size: {}", pages, records.size());
        log.info("会员盈亏重算 pp免费旋转   records size: {}", records.size());
        if (CollUtil.isNotEmpty(winLoseRecalculateFeelSpinVOS)) {
            //
            List<String> userIds = winLoseRecalculateFeelSpinVOS.stream().map(WinLoseRecalculateFeelSpinVO::getUserId).collect(Collectors.toList());
            // 查询这个小时内所有数据 需要更新的
            List<ReportUserWinLosePO> recordExists = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(userIds)) {
                recordExists = getUserWinLoseByUserId(userIds, startTime);
            }
            List<ReportUserWinLosePO> recordPPMains = new ArrayList<>();
            List<ReportUserWinLosePO> finalRecordPPExists = recordExists;
            winLoseRecalculateFeelSpinVOS.forEach(record -> {
                // 判断统计的数据是否未空，防止数据是0的数据，导致数据错误 统计 已使用优惠
                if (record.getFreeSpinAmount() != null && record.getFreeSpinAmount().compareTo(BigDecimal.ZERO) != 0) {
                    record.setDayHourMillis(startTime);
                    record.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(startTime, timeZone));
                    // 批量判断，且插入，且更新
                    // 一条一条的记录，查询是否有，如果有，则更新，如果没有，则插入
                    //ReportUserWinLosePO initPO = selectUserWinLose(startTime, record.getUserId(), record.getUserAccount(), record.getAgentId(), record.getSuperAgentAccount(), siteVO, record.getMainCurrency(), record.getAccountType());
                    // 判断这条record数据是否包括。 只更新 已使用优惠，
                    ReportUserWinLosePO initPO = checkAndUpdateUserWinLosePP(record, finalRecordPPExists);
                    if (initPO == null) {
                        initPO = initializeUserWinLose(startTime, record.getUserId(), record.getUserAccount(), record.getAgentId(), record.getSuperAgentAccount(), siteVO, record.getMainCurrency(), record.getAccountType());
                        initPO.setAlreadyUseAmount(record.getFreeSpinAmount());
                        initPO.setUpdatedTime(System.currentTimeMillis());
                        poList.add(initPO);

                    } else {
                        initPO.setAlreadyUseAmount(initPO.getAlreadyUseAmount().add(record.getFreeSpinAmount()));
                        initPO.setUpdatedTime(System.currentTimeMillis());
                        recordPPMains.add(initPO);
                    }
                }
            });
            // 插入 新增
            if (CollectionUtil.isNotEmpty(poList)) {
                saveBatch(poList);
                poList.clear();
            }
            // 更新
            if (CollectionUtil.isNotEmpty(recordPPMains)) {
                this.updateBatchById(recordPPMains);
                recordPPMains.clear();
            }

        }
        // pp免费旋转 -> end

        // 帐变表，主货币帐变表，平台币帐变表
        //主货币帐变表 已经使用优惠，其他调整 ，统计类型 3 VIP福利 4 活动优惠 8平台币兑换 9 其他调整 10. 返水
        pages = 1;
        size = 500;
        WinLoseRecalculateReqWalletVO reqWalletVO = new WinLoseRecalculateReqWalletVO().setSiteCode(siteCode).setStartTime(startTime).setEndTime(endTime);
        reqWalletVO.setPageSize(size);
        List<WinLoseRecalculateWalletVO> recordMainsWalletRecords;
        //List<ReportUserWinLosePO> recordMains = Lists.newArrayList();
        do {
            reqWalletVO.setPageNumber(pages);
            Page<WinLoseRecalculateWalletVO> recalculateVOPage = userCoinRecordApi.winLoseRecalculateMainPage(reqWalletVO);
            recordMainsWalletRecords = recalculateVOPage.getRecords();
            //log.debug("会员盈亏重算 获取的记录 {}, records size: {}", pages, records.size());
            log.info("会员盈亏重算 获取的账变记录  records size: {}", records.size());
            if (CollUtil.isNotEmpty(recordMainsWalletRecords)) {
                //
                List<String> userIds = recordMainsWalletRecords.stream().map(WinLoseRecalculateWalletVO::getUserId).collect(Collectors.toList());
                // 查询这个小时内所有数据 需要更新的
                List<ReportUserWinLosePO> recordExists = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(userIds)) {
                    recordExists = getUserWinLoseByUserId(userIds, startTime);
                }
                List<ReportUserWinLosePO> recordMains = new ArrayList<>();
                List<ReportUserWinLosePO> finalRecordExists = recordExists;
                recordMainsWalletRecords.forEach(record -> {
                    // 判断统计的数据是否未空，防止数据是0的数据，导致数据错误 统计 已使用优惠，其他调整
                    if ((record.getAlreadyUseAmount() != null && record.getAlreadyUseAmount().compareTo(BigDecimal.ZERO) != 0)
                            || (record.getAdjustAmount() != null && record.getAdjustAmount().compareTo(BigDecimal.ZERO) != 0)
                            || (record.getRiskAmount() != null && record.getRiskAmount().compareTo(BigDecimal.ZERO) != 0)
                            || (record.getRebateAmount() != null && record.getRebateAmount().compareTo(BigDecimal.ZERO) != 0)) {
                        record.setDayHourMillis(startTime);
                        record.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(startTime, timeZone));
                        // 批量判断，且插入，且更新
                        // 一条一条的记录，查询是否有，如果有，则更新，如果没有，则插入
                        //ReportUserWinLosePO initPO = selectUserWinLose(startTime, record.getUserId(), record.getUserAccount(), record.getAgentId(), record.getSuperAgentAccount(), siteVO, record.getMainCurrency(), record.getAccountType());
                        // 判断这条record数据是否包括。
                        ReportUserWinLosePO initPO = checkAndUpdateUserWinLose(record, finalRecordExists);
                        if (initPO == null) {
                            initPO = initializeUserWinLose(startTime, record.getUserId(), record.getUserAccount(), record.getAgentId(), record.getSuperAgentAccount(), siteVO, record.getMainCurrency(), record.getAccountType());
                            initPO.setAlreadyUseAmount(record.getAlreadyUseAmount());
                            initPO.setAdjustAmount(record.getAdjustAmount());
                            initPO.setRiskAmount(record.getRiskAmount());
                            initPO.setRebateAmount(record.getRebateAmount());
                            initPO.setUpdatedTime(System.currentTimeMillis());
                            poList.add(initPO);

                        } else {
                            initPO.setAlreadyUseAmount(initPO.getAlreadyUseAmount().add(record.getAlreadyUseAmount()));
                            initPO.setAdjustAmount(initPO.getAdjustAmount().add(record.getAdjustAmount()));
                            initPO.setRiskAmount(initPO.getRiskAmount().add(record.getRiskAmount()));
                            initPO.setRebateAmount(initPO.getRebateAmount().add(record.getRebateAmount()));
                            initPO.setUpdatedTime(System.currentTimeMillis());
                            recordMains.add(initPO);
                        }
                        // 主货币
                        //reportUserWinLoseRepository.updateMainAmount(initPO.getId(), record.getAlreadyUseAmount(), record.getAdjustAmount(), System.currentTimeMillis());
                    }
                });
                // 插入 新增
                if (CollectionUtil.isNotEmpty(poList)) {
                    saveBatch(poList);
                    poList.clear();
                }
                // 更新
                if (CollectionUtil.isNotEmpty(recordMains)) {
                    this.updateBatchById(recordMains);
                    recordMains.clear();
                }

            }
            pages++;

        } while (CollUtil.isNotEmpty(recordMainsWalletRecords));
        log.info("会员盈亏重算 主货币信息 for siteCode: {}, dayHourMillis: {}", siteCode, startTime);


        // 平台币帐变 VIP福利， 活动优惠 是统计类型：1: VIP福利,2:活动优惠,3:勋章奖励,4.返水
        pages = 1;
        size = 500;
        WinLoseRecalculateReqWalletVO reqPlatWalletVO = new WinLoseRecalculateReqWalletVO().setSiteCode(siteCode).setStartTime(startTime).setEndTime(endTime);
        reqPlatWalletVO.setPageSize(size);
        List<WinLoseRecalculateWalletVO> recordPlatWalletRecords;

        do {
            reqWalletVO.setPageNumber(pages);
            Page<WinLoseRecalculateWalletVO> recalculateVOPage = userPlatformCoinRecordApi.winLoseRecalculateMainPage(reqWalletVO);

            recordPlatWalletRecords = recalculateVOPage.getRecords();
            log.debug("会员盈亏重算 获取平台币账变的记录:{}, records size: {}", pages, records.size());
            if (CollUtil.isNotEmpty(recordPlatWalletRecords)) {
                List<String> userIds = recordPlatWalletRecords.stream().map(WinLoseRecalculateWalletVO::getUserId).collect(Collectors.toList());
                List<ReportUserWinLosePO> recordPlatExists = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(userIds)) {
                    recordPlatExists = getUserWinLoseByUserId(userIds, startTime);
                }
                // 需要更新的
                List<ReportUserWinLosePO> recordPlatMains = Lists.newArrayList();
                List<ReportUserWinLosePO> finalRecordPlatExists = recordPlatExists;
                recordPlatWalletRecords.forEach(record -> {
                    if ((record.getVipAmount() != null && record.getVipAmount().compareTo(BigDecimal.ZERO) != 0)
                            || (record.getActivityAmount() != null && record.getActivityAmount().compareTo(BigDecimal.ZERO) != 0)
                            /*|| (record.getRebateAmount() != null && record.getRebateAmount().compareTo(BigDecimal.ZERO) != 0)*/
                            || (record.getPlatAdjustAmount() != null && record.getPlatAdjustAmount().compareTo(BigDecimal.ZERO) != 0)) {
                        record.setDayHourMillis(startTime);
                        record.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(startTime, timeZone));
                        // 一条一条的记录，查询是否有，如果有，则更新，如果没有，则插入
                        // ReportUserWinLosePO initPO = selectUserWinLose(startTime, record.getUserId(), record.getUserAccount(), record.getAgentId(), record.getSuperAgentAccount(), siteVO, record.getMainCurrency(), record.getAccountType());
                        // 平台币，
                        ReportUserWinLosePO initPO = checkAndUpdateUserWinLose(record, finalRecordPlatExists);
                        if (initPO == null) {
                            initPO = initializeUserWinLose(startTime, record.getUserId(), record.getUserAccount(), record.getAgentId(), record.getSuperAgentAccount(), siteVO, record.getMainCurrency(), record.getAccountType());
                            initPO.setAlreadyUseAmount(record.getAlreadyUseAmount());
                            //initPO.setAdjustAmount(record.getAdjustAmount());
                            initPO.setActivityAmount(record.getActivityAmount());
                            //initPO.setRebateAmount(record.getRebateAmount());
                            initPO.setVipAmount(record.getVipAmount());
                            initPO.setPlatAdjustAmount(record.getPlatAdjustAmount());
                            initPO.setUpdatedTime(System.currentTimeMillis());
                            poList.add(initPO);

                        } else {
                            initPO.setAlreadyUseAmount(initPO.getAlreadyUseAmount().add(record.getAlreadyUseAmount()));
                            //initPO.setAdjustAmount(initPO.getAdjustAmount().add(record.getAdjustAmount()));
                            initPO.setUpdatedTime(System.currentTimeMillis());
                            // 活动优惠
                            initPO.setActivityAmount(initPO.getActivityAmount().add(record.getActivityAmount()));
                            //返水
                            //initPO.setRebateAmount(initPO.getRebateAmount().add(record.getRebateAmount()));
                            // vip
                            initPO.setVipAmount(initPO.getVipAmount().add(record.getVipAmount()));
                            // 平台币调整
                            initPO.setPlatAdjustAmount(initPO.getPlatAdjustAmount().add(record.getPlatAdjustAmount()));
                            recordPlatMains.add(initPO);
                        }
                        //reportUserWinLoseRepository.updatePlatAmount(initPO.getId(), record.getVipAmount(), record.getActivityAmount(), System.currentTimeMillis());

                    }
                });
                // 插入 新增
                if (CollectionUtil.isNotEmpty(poList)) {
                    saveBatch(poList);
                    poList.clear();
                }
                // 更新
                if (CollectionUtil.isNotEmpty(recordPlatMains)) {
                    this.updateBatchById(recordPlatMains);
                    recordPlatMains.clear();
                }
            }
            pages++;

        } while (CollUtil.isNotEmpty(recordPlatWalletRecords));
        log.info("会员盈亏重算 平台币信息 for siteCode: {}, dayHourMillis: {}", siteCode, startTime);

    }

    /**
     * 校验这条记录是否存在，如果不存在，则需要插入，如果存在，则也返回，在基础上更新
     *
     * @param record       当前需要校验的记录
     * @param recordExists 已存在的记录列表
     * @return 如果存在则返回该记录，否则返回 null
     */
    private ReportUserWinLosePO checkAndUpdateUserWinLose(WinLoseRecalculateWalletVO record, List<ReportUserWinLosePO> recordExists) {
        return recordExists.stream()
                .filter(e -> e.getUserId().equals(record.getUserId())
                        && ObjectUtil.equals(e.getAgentId(), record.getAgentId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 校验这条记录是否存在，如果不存在，则需要插入，如果存在，则也返回，在基础上更新
     *
     * @param record       当前需要校验的记录
     * @param recordExists 已存在的记录列表
     * @return 如果存在则返回该记录，否则返回 null
     */
    private ReportUserWinLosePO checkAndUpdateUserWinLosePP(WinLoseRecalculateFeelSpinVO record, List<ReportUserWinLosePO> recordExists) {
        return recordExists.stream()
                .filter(e -> e.getUserId().equals(record.getUserId())
                        && ObjectUtil.equals(e.getAgentId(), record.getAgentId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * @param userIds         用户ids
     * @param dateUTCHourTime 每个小时的记录
     */
    private List<ReportUserWinLosePO> getUserWinLoseByUserId(List<String> userIds, long dateUTCHourTime) {
        LambdaQueryWrapper<ReportUserWinLosePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportUserWinLosePO::getDayHourMillis, dateUTCHourTime)
                .in(ReportUserWinLosePO::getUserId, userIds);
        //.eq(StrUtil.isNotEmpty(agentId), ReportUserWinLosePO::getAgentId, agentId)
        // 如果代理ID不为空，则添加代理ID的查询条件,如果没有代理，则查询agentId为空的记录
        //.isNull(StrUtil.isEmpty(agentId), ReportUserWinLosePO::getAgentId);
        return reportUserWinLoseRepository.selectList(queryWrapper);

    }

    /**
     * 查询在指定时间节点是否存在记录，如果不存在，则初始化一个新记录并返回；如果存在，则直接返回该记录。
     *
     * @param dateUTCHourTime 时间戳（毫秒），表示查询的日期。
     * @return ReportUserWinLosePO 用户输赢记录的实体对象。
     */
    private ReportUserWinLosePO selectUserWinLose(Long dateUTCHourTime, String userId, String userAccount, String agentId, String agentAccount,
                                                  SiteVO siteInfo, String mainCurrency, String accountType) {
        LambdaQueryWrapper<ReportUserWinLosePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportUserWinLosePO::getDayHourMillis, dateUTCHourTime)
                .eq(ReportUserWinLosePO::getUserId, userId)
                .eq(StrUtil.isNotEmpty(agentId), ReportUserWinLosePO::getAgentId, agentId)
                // 如果代理ID不为空，则添加代理ID的查询条件,如果没有代理，则查询agentId为空的记录
                .isNull(StrUtil.isEmpty(agentId), ReportUserWinLosePO::getAgentId);
        ReportUserWinLosePO reportUserWinLosePO = reportUserWinLoseRepository.selectOne(queryWrapper);
        if (reportUserWinLosePO == null) {
            reportUserWinLosePO = initializeUserWinLose(dateUTCHourTime, userId, userAccount, agentId, agentAccount, siteInfo, mainCurrency, accountType);
            //reportUserWinLoseRepository.saveData(userWinLose);
            reportUserWinLoseRepository.insert(reportUserWinLosePO);
        }
        return reportUserWinLosePO;
    }

    /**
     * 初始化一个新的用户输赢记录对象。
     *
     * @param day    时间戳（毫秒），表示查询的日期。
     * @param userId 包含用户信息的对象，包含用户ID、账号、代理信息等。
     * @return ReportUserWinLosePO 初始化后的用户输赢记录对象。
     */
    private ReportUserWinLosePO initializeUserWinLose(Long day, String userId, String userAccount,
                                                      String agentId, String agentAccount, SiteVO siteInfo, String mainCurrency, String accountType) {
        ReportUserWinLosePO userWinLose = new ReportUserWinLosePO();
        userWinLose.setDayHourMillis(day);


        //userWinLose.setDayStr(DateUtil.format(new Date(day), DatePattern.NORM_DATETIME_PATTERN));
        String timeZone = siteInfo.getTimezone();
        userWinLose.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(day, timeZone));
        userWinLose.setUserAccount(userAccount);
        userWinLose.setUserId(userId);
        if (StrUtil.isNotEmpty(agentId)) {
            // 如果需要进一步设置代理属性，可以在此处调用外部API获取代理信息
            // 查询
            // AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(agentId);
            userWinLose.setSuperAgentAccount(agentAccount);
            userWinLose.setAgentId(agentId);
           /* if (agentInfoVO != null) {
                // 设置代理层级信息
                //userWinLose.setAgentAttribution(agentInfoVO.getAgentAttribution());
            }*/
        }

        // 初始化各项数值
        userWinLose.setBetNum(0);
        userWinLose.setBetAmount(BigDecimal.ZERO);
        userWinLose.setValidBetAmount(BigDecimal.ZERO);
        userWinLose.setRunWaterCorrect(BigDecimal.ZERO);
        userWinLose.setBetWinLose(BigDecimal.ZERO);
        userWinLose.setRebateAmount(BigDecimal.ZERO);
        userWinLose.setActivityAmount(BigDecimal.ZERO);
        userWinLose.setAdjustAmount(BigDecimal.ZERO);
        userWinLose.setRepairOrderOtherAdjust(BigDecimal.ZERO);
        userWinLose.setProfitAndLoss(BigDecimal.ZERO);
        // 已经使用优惠，根据当时汇率转换为主货币
        userWinLose.setAlreadyUseAmount(BigDecimal.ZERO);
        // vip福利，根据当时汇率转换为主货币
        userWinLose.setVipAmount(BigDecimal.ZERO);
        // 设置创建和更新时间为当前时间
        long currentTimeMillis = System.currentTimeMillis();
        userWinLose.setCreatedTime(currentTimeMillis);
        userWinLose.setUpdatedTime(currentTimeMillis);
        userWinLose.setSiteCode(siteInfo.getSiteCode());
        // 主货币
        userWinLose.setMainCurrency(mainCurrency);
        userWinLose.setAccountType(accountType);
        userWinLose.setProfitAndLoss(BigDecimal.ZERO);
        userWinLose.setTipsAmount(BigDecimal.ZERO);
        userWinLose.setRiskAmount(BigDecimal.ZERO);
        userWinLose.setPlatAdjustAmount(BigDecimal.ZERO);
        return userWinLose;
    }


    /**
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param needRunTimes 返回需要跑的时间的整点开始时间
     */
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


    public Map<String, Map<String, List<UserWinLoseBetUserVO>>> getSiteBetUserList(Long startTime, Long endTime, List<String> siteCodes) {
        Map<String, Map<String, List<UserWinLoseBetUserVO>>> result = new HashMap<>();
        List<UserWinLoseBetUserVO> userWinLoseBetUserVOS = reportUserWinLoseRepository.getSiteBetUserList(startTime, endTime, siteCodes);
        if (CollectionUtil.isNotEmpty(userWinLoseBetUserVOS)) {
            result = userWinLoseBetUserVOS.stream()
                    .collect(Collectors.groupingBy(UserWinLoseBetUserVO::getSiteCode, Collectors.groupingBy(UserWinLoseBetUserVO::getDateStr)
                    ));
        }
        return result;
    }

    public List<UserWinLoseResponseVO> queryListByParam(UserWinLoseAgentReqVO vo) {
        return reportUserWinLoseRepository.queryListByParam(vo);
    }

    public List<UserWinLossAmountReportVO> queryUserOrderAmountByAgent(UserWinLossAmountParamVO vo) {
        return reportUserWinLoseRepository.queryUserOrderAmountByAgent(vo);
    }

    public List<DailyWinLoseResponseVO> dailyWinLoseCurrency(DailyWinLoseVO vo) {
        return reportUserWinLoseRepository.dailyWinLoseCurrency(vo);
    }
}
