package com.cloud.baowang.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.*;
import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositSubordinatesPageReqVo;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoPageResultVo;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithDrawReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawRespVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResponseVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberOverflowReviewPageResVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.member.MemberTransferReviewPageResVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.UserInfoResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.user.api.vo.user.request.UserRegistrationInfoReqVO;
import com.cloud.baowang.report.api.api.UserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsResponseVO;
import com.cloud.baowang.report.api.vo.agent.ReportTopAgentStaticsResult;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListPageCondVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListResponseVO;
import com.cloud.baowang.report.po.ReportTopAgentStaticDayPO;
import com.cloud.baowang.report.repositories.ReportTopAgentStaticDayRepository;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.UserRegistrationInfoApi;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.user.api.vo.user.UserInfoPageVO;
import com.cloud.baowang.user.api.vo.user.UserRegistrationInfoResVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserDepositWithdrawApi;
import com.cloud.baowang.wallet.api.api.UserManualUpDownApi;
import com.cloud.baowang.wallet.api.enums.AgentManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.ManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserDepositWithdrawPageReqVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Desciption: 商务代理报表service
 * @Author: Ford
 * @Date: 2024/11/5 11:23
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class ReportTopAgentStaticDayService extends ServiceImpl<ReportTopAgentStaticDayRepository, ReportTopAgentStaticDayPO> {

    private final AgentInfoApi agentInfoApi;

    private final AgentListApi agentListApi;

    private final UserWinLoseApi userWinLoseApi;

    private final UserInfoApi userInfoApi;

    private final UserRegistrationInfoApi userRegistrationInfoApi;

    private final ReportAgentStaticBetService reportAgentStaticBetService;

    private final SiteApi siteApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final RiskApi riskApi;

    private final AgentLabelApi agentLabelApi;

    private final UserDepositWithdrawApi userDepositWithdrawApi;

    private final AgentDepositWithdrawApi agentDepositWithdrawApi;


    private final AgentDepositSubordinatesApi agentDepositSubordinatesApi;

    private final UserManualUpDownApi userManualUpDownApi;

    private final AgentManualUpDownApi agentManualUpDownApi;

    //会员溢出
    private final AgentUserOverflowApi agentUserOverflowApi;
    //会员转代
    private final UserTransferAgentApi userTransferAgentApi;


    /**
     * 按照站点、天 统计商务总代代报表
     *
     * @param reportTopAgentStaticsCondVO 统计条件
     * @return 统计成功 or 失败
     */
    public ResponseVO<Boolean> init(ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO) {
        String siteCode = reportTopAgentStaticsCondVO.getSiteCode();
        String reportType = reportTopAgentStaticsCondVO.getReportType();
        String timeZone = reportTopAgentStaticsCondVO.getTimeZone();
        if (!StringUtils.hasText(siteCode)) {
            log.info("站点为空:{}", reportTopAgentStaticsCondVO);
            return ResponseVO.success(Boolean.TRUE);
        }
        if (!StringUtils.hasText(reportTopAgentStaticsCondVO.getTimeZone())) {
            log.info("站点时区为空:{}", reportTopAgentStaticsCondVO);
            return ResponseVO.success(Boolean.TRUE);
        }

        UserWinLoseListPageCondVO vo = new UserWinLoseListPageCondVO();
        vo.setSiteCode(siteCode);
        vo.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        vo.setStartDayMillis(reportTopAgentStaticsCondVO.getStartDayMillis());
        vo.setEndDayMillis(reportTopAgentStaticsCondVO.getEndDayMillis());
        vo.setPageNumber(1);
        vo.setPageSize(500);
        ResponseVO<Page<UserWinLoseListResponseVO>> userWinLosePageResp = userWinLoseApi.listPage(vo);
        if (!userWinLosePageResp.isOk()) {
            log.info("商务总代报表根据条件:{}没有查询到会员盈亏数据", vo);
            return ResponseVO.success(Boolean.TRUE);
        }

        Map<String, BigDecimal> rateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        //删除当前站点 历史统计数据 重新计算
        LambdaQueryWrapper<ReportTopAgentStaticDayPO> reportTopAgentStaticDayPOLambdaQueryWrapper = new LambdaQueryWrapper<ReportTopAgentStaticDayPO>();
        reportTopAgentStaticDayPOLambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getSiteCode, siteCode);
        reportTopAgentStaticDayPOLambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getReportType, reportType);
        if (vo.getStartDayMillis() != null) {
            reportTopAgentStaticDayPOLambdaQueryWrapper.ge(ReportTopAgentStaticDayPO::getDayMillis, vo.getStartDayMillis());
        }
        if (vo.getEndDayMillis() != null) {
            reportTopAgentStaticDayPOLambdaQueryWrapper.le(ReportTopAgentStaticDayPO::getDayMillis, vo.getEndDayMillis());
        }
        this.baseMapper.delete(reportTopAgentStaticDayPOLambdaQueryWrapper);

        //统计代理所属团队 会员盈亏
        Page<UserWinLoseListResponseVO> userWinLoseResponseVOPage = userWinLosePageResp.getData();
        long totalPageNum = userWinLoseResponseVOPage.getPages();
        log.info("商务总代报表站点:{},总页数:{},参数:{}", siteCode, totalPageNum, vo);
        for (int pageNum = 1; pageNum <= totalPageNum; pageNum++) {
            vo.setPageNumber(pageNum);
            log.info("商务总代报表站点:{},开始处理第:{}页会员盈亏数据", siteCode, pageNum);
            ResponseVO<Page<UserWinLoseListResponseVO>> userWinLosePageRespForEach = userWinLoseApi.listPage(vo);
            //更新金额 新增or更新
            batchProcess(userWinLosePageRespForEach.getData().getRecords(), reportTopAgentStaticsCondVO.getReportType(), timeZone, rateMap);
            log.info("商务总代报表站点:{},处理结束第:{}页会员盈亏数据", siteCode, pageNum);
        }
        //统计代理所属团队 存款金额、提款金额、存提手续费 user_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
        initUserDepositWithdraw(reportTopAgentStaticsCondVO, rateMap);

        //统计代理代存 金额 agent_deposit_subordinates 里 deposit_time ; amount
        initAgentDepositSubordinate(reportTopAgentStaticsCondVO, rateMap);

        //统计会员人工加减额 user_manual_up_down_record
        initUserManualUpDown(reportTopAgentStaticsCondVO, rateMap);

        //统计代理手续费 存款金额、提款金额、存提手续费 agent_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
        initAgentDepositWithdraw(reportTopAgentStaticsCondVO, rateMap);

        // 初始化代理 人工加减额 agent_manual_up_down_record
        initAgentManualUpDown(reportTopAgentStaticsCondVO);


        //统计 注册人数 首存人数
        fillRegisterNum(reportTopAgentStaticsCondVO);
        // 统计 团队代理人数  团队会员人数
        fillTeamNum(reportTopAgentStaticsCondVO);


        return ResponseVO.success(Boolean.TRUE);
    }


    //统计代理手续费 agent_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
    private void initAgentDepositWithdraw(ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO, Map<String, BigDecimal> rateMap) {
        String siteCode = reportTopAgentStaticsCondVO.getSiteCode();
        String reportType = reportTopAgentStaticsCondVO.getReportType();
        String timeZone = reportTopAgentStaticsCondVO.getTimeZone();
        AgentDepositWithDrawReqVO vo = new AgentDepositWithDrawReqVO();
        vo.setSiteCode(reportTopAgentStaticsCondVO.getSiteCode());
        vo.setStartTime(reportTopAgentStaticsCondVO.getStartDayMillis());
        vo.setEndTime(reportTopAgentStaticsCondVO.getEndDayMillis());
        vo.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        vo.setPageNumber(1);
        vo.setPageSize(500);
        Page<AgentDepositWithdrawRespVO> agentDepositWithdrawRespVOPage = agentDepositWithdrawApi.listPage(vo);
        long totalPageNum = agentDepositWithdrawRespVOPage.getPages();
        log.info("商务总代报表代理充提手续费计算 参数:{},总页数:{}", reportTopAgentStaticsCondVO, totalPageNum);
        for (int pageNum = 1; pageNum <= totalPageNum; pageNum++) {
            vo.setPageNumber(pageNum);
            log.info("商务总代报表站点:{},开始处理第:{}页 代理充提手续费计算", siteCode, pageNum);
            Page<AgentDepositWithdrawRespVO> agentDepositWithdrawRespVOPageForEach = agentDepositWithdrawApi.listPage(vo);

            List<String> agentIds = agentDepositWithdrawRespVOPageForEach.getRecords().stream().filter(o -> StringUtils.hasText(o.getAgentId())).map(AgentDepositWithdrawRespVO::getAgentId).toList();
            if (CollectionUtils.isEmpty(agentIds)) {
                continue;
            }
            List<AgentInfoVO> agentInfoVOS = agentInfoApi.getByAgentIds(agentIds);
            for (AgentDepositWithdrawRespVO agentDepositWithdrawRespVO : agentDepositWithdrawRespVOPageForEach.getRecords()) {
                Optional<AgentInfoVO> agentInfoVOOptional = agentInfoVOS.stream().filter(o -> o.getAgentId().equals(agentDepositWithdrawRespVO.getAgentId())).findFirst();
                if (agentInfoVOOptional.isEmpty()) {
                    log.info("商务总代报表代理充提手续费计算 代理信息不存在:{}", agentDepositWithdrawRespVO.getAgentId());
                    continue;
                }
                AgentInfoVO currentAgentInfo = agentInfoVOOptional.get();
                String agentInfoPath = StringUtils.hasText(currentAgentInfo.getPath()) ? currentAgentInfo.getPath() : currentAgentInfo.getAgentId();
                //计算当前用户所属代理、计算代理的所有上级代理
                // 即计算某个代理所属团队的所有用户数据
                String[] allParentAgentIdArray = agentInfoPath.split(",");
                for (int i = 0; i <= allParentAgentIdArray.length - 1; i++) {
                    String reportDay = DateUtils.formatDateByZoneId(agentDepositWithdrawRespVO.getUpdatedTime(), DateUtils.DATE_FORMAT_1, timeZone);
                    String reportMonth = DateUtils.formatDateByZoneId(agentDepositWithdrawRespVO.getUpdatedTime(), DateUtils.DATE_FORMAT_2, timeZone);
                    ReportTopAgentStaticDayPO reportTopAgentStaticDayPO = new ReportTopAgentStaticDayPO();
                    reportTopAgentStaticDayPO.setReportType(reportType);
                    // 按天统计
                    if ("0".equals(reportType)) {
                        Long dayStartTime = DateUtils.getStartDayMillis(agentDepositWithdrawRespVO.getUpdatedTime(), timeZone);
                        reportTopAgentStaticDayPO.setDayMillis(dayStartTime);
                        reportTopAgentStaticDayPO.setReportDate(reportDay);
                    } else {
                        //按月统计
                        Long monthStartTime = DateUtils.getStartDayMonthTimestamp(agentDepositWithdrawRespVO.getUpdatedTime(), timeZone);
                        reportTopAgentStaticDayPO.setDayMillis(monthStartTime);
                        reportTopAgentStaticDayPO.setReportDate(reportMonth);
                    }
                    String parentAgentId = allParentAgentIdArray[i];
                    reportTopAgentStaticDayPO.setSiteCode(siteCode);
                    reportTopAgentStaticDayPO.setAgentId(parentAgentId);
                    BigDecimal convertRate = rateMap.get(agentDepositWithdrawRespVO.getCurrencyCode());
                    reportTopAgentStaticDayPO.setDepositAmount(BigDecimal.ZERO);
                    reportTopAgentStaticDayPO.setWithdrawAmount(BigDecimal.ZERO);
                    BigDecimal settlementFeeWtc = AmountUtils.divide(agentDepositWithdrawRespVO.getSettlementFeeAmount(), agentDepositWithdrawRespVO.getPlatformCurrencyExchangeRate());
                    reportTopAgentStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                    if (Objects.equals(agentDepositWithdrawRespVO.getType(), DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())) {
                        // reportTopAgentStaticDayPO.setCurrencyCode(agentDepositWithdrawRespVO.getCurrencyCode());
                        //BigDecimal depositAmountWtc=AmountUtils.divide(agentDepositWithdrawRespVO.getArriveAmount(),convertRate);
                        reportTopAgentStaticDayPO.setDepositAmount(agentDepositWithdrawRespVO.getArriveAmount());
                        reportTopAgentStaticDayPO.setDepositWithdrawFeeAmount(settlementFeeWtc);
                    } else {
                        // BigDecimal withdrawAmountWtc=AmountUtils.divide(agentDepositWithdrawRespVO.getArriveAmount(),convertRate);
                        reportTopAgentStaticDayPO.setWithdrawAmount(agentDepositWithdrawRespVO.getArriveAmount());
                        reportTopAgentStaticDayPO.setDepositWithdrawFeeAmount(settlementFeeWtc);
                    }
                    //  BigDecimal settlementFeeWtc=AmountUtils.divide(agentDepositWithdrawRespVO.getSettlementFeeAmount(),convertRate);
                    // reportTopAgentStaticDayPO.setDepositWithdrawFeeAmount(agentDepositWithdrawRespVO.getSettlementFeeAmount());
                    String reportKey = reportTopAgentStaticDayPO.getReportDate().concat(reportTopAgentStaticDayPO.getAgentId());
                    log.info("商务总代报表开始统计代理充提手续费:{},{}", reportKey, reportTopAgentStaticDayPO);
                    saveData(reportTopAgentStaticDayPO);
                }
            }
            log.info("商务总代报表站点:{},处理结束第:{}页 代理充提手续费计算", siteCode, pageNum);
        }
    }

    //统计代理所属团队 充提手续费 user_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
    private void initUserDepositWithdraw(ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO, Map<String, BigDecimal> rateMap) {
        String timeZone = reportTopAgentStaticsCondVO.getTimeZone();
        UserDepositWithdrawPageReqVO userDepositWithdrawPageReqVO = new UserDepositWithdrawPageReqVO();
        userDepositWithdrawPageReqVO.setSiteCode(reportTopAgentStaticsCondVO.getSiteCode());
        userDepositWithdrawPageReqVO.setStartTime(reportTopAgentStaticsCondVO.getStartDayMillis());
        userDepositWithdrawPageReqVO.setEndTime(reportTopAgentStaticsCondVO.getEndDayMillis());
        userDepositWithdrawPageReqVO.setUserAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        userDepositWithdrawPageReqVO.setPageNumber(1);
        userDepositWithdrawPageReqVO.setPageSize(500);
        String siteCode = reportTopAgentStaticsCondVO.getSiteCode();
        String reportType = reportTopAgentStaticsCondVO.getReportType();
        Page<UserDepositWithdrawalResVO> userDepositWithdrawalResVOPage = userDepositWithdrawApi.findDepositWithdrawPage(userDepositWithdrawPageReqVO);
        long totalPageNum = userDepositWithdrawalResVOPage.getPages();
        log.info("商务总代报表充提手续费计算 参数:{},总页数:{}", reportTopAgentStaticsCondVO, totalPageNum);
        for (int pageNum = 1; pageNum <= totalPageNum; pageNum++) {
            userDepositWithdrawPageReqVO.setPageNumber(pageNum);
            log.info("商务总代报表站点:{},开始处理第:{}页 充提手续费计算", siteCode, pageNum);
            Page<UserDepositWithdrawalResVO> userDepositWithdrawalPageForEach = userDepositWithdrawApi.findDepositWithdrawPage(userDepositWithdrawPageReqVO);

            List<String> agentIds = userDepositWithdrawalPageForEach.getRecords().stream().filter(o -> StringUtils.hasText(o.getAgentId())).map(UserDepositWithdrawalResVO::getAgentId).toList();
            if (CollectionUtils.isEmpty(agentIds)) {
                continue;
            }
            List<AgentInfoVO> agentInfoVOS = agentInfoApi.getByAgentIds(agentIds);
            for (UserDepositWithdrawalResVO userDepositWithdrawalResVO : userDepositWithdrawalPageForEach.getRecords()) {
                Optional<AgentInfoVO> agentInfoVOOptional = agentInfoVOS.stream().filter(o -> o.getAgentId().equals(userDepositWithdrawalResVO.getAgentId())).findFirst();
                if (agentInfoVOOptional.isEmpty()) {
                    log.info("商务总代报表充提手续费计算 代理信息不存在:{}", userDepositWithdrawalResVO.getAgentId());
                    continue;
                }
                AgentInfoVO currentAgentInfo = agentInfoVOOptional.get();
                String agentInfoPath = StringUtils.hasText(currentAgentInfo.getPath()) ? currentAgentInfo.getPath() : currentAgentInfo.getAgentId();
                //计算当前用户所属代理、计算代理的所有上级代理
                // 即计算某个代理所属团队的所有用户数据
                String[] allParentAgentIdArray = agentInfoPath.split(",");
                for (int i = 0; i <= allParentAgentIdArray.length - 1; i++) {

                    String reportDay = DateUtils.formatDateByZoneId(userDepositWithdrawalResVO.getUpdatedTime(), DateUtils.DATE_FORMAT_1, timeZone);
                    String reportMonth = DateUtils.formatDateByZoneId(userDepositWithdrawalResVO.getUpdatedTime(), DateUtils.DATE_FORMAT_2, timeZone);
                    ReportTopAgentStaticDayPO reportTopAgentStaticDayPO = new ReportTopAgentStaticDayPO();
                    reportTopAgentStaticDayPO.setReportType(reportType);
                    // 按天统计
                    if ("0".equals(reportType)) {
                        Long dayStartTime = DateUtils.getStartDayMillis(userDepositWithdrawalResVO.getUpdatedTime(), timeZone);
                        reportTopAgentStaticDayPO.setDayMillis(dayStartTime);
                        reportTopAgentStaticDayPO.setReportDate(reportDay);
                    } else {
                        //按月统计
                        Long monthStartTime = DateUtils.getStartDayMonthTimestamp(userDepositWithdrawalResVO.getUpdatedTime(), timeZone);
                        reportTopAgentStaticDayPO.setDayMillis(monthStartTime);
                        reportTopAgentStaticDayPO.setReportDate(reportMonth);
                    }
                    String parentAgentId = allParentAgentIdArray[i];
                    reportTopAgentStaticDayPO.setSiteCode(siteCode);
                    reportTopAgentStaticDayPO.setAgentId(parentAgentId);
                    reportTopAgentStaticDayPO.setDepositAmount(BigDecimal.ZERO);
                    reportTopAgentStaticDayPO.setWithdrawAmount(BigDecimal.ZERO);
                    reportTopAgentStaticDayPO.setCurrencyCode(userDepositWithdrawalResVO.getCurrencyCode());
                    //  reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                    BigDecimal convertRate = rateMap.get(userDepositWithdrawalResVO.getCurrencyCode());
                    if (Objects.equals(userDepositWithdrawalResVO.getType(), DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())) {
                        //  BigDecimal depositAmountWtc=AmountUtils.divide(userDepositWithdrawalResVO.getArriveAmount(),convertRate);
                        reportTopAgentStaticDayPO.setDepositAmount(userDepositWithdrawalResVO.getArriveAmount());
                    } else {
                        // BigDecimal withdrawAmountWtc=AmountUtils.divide(userDepositWithdrawalResVO.getArriveAmount(),convertRate);
                        reportTopAgentStaticDayPO.setWithdrawAmount(userDepositWithdrawalResVO.getApplyAmount());
                    }
                    // BigDecimal settlementFeeWtc=AmountUtils.divide(userDepositWithdrawalResVO.getSettlementFeeAmount(),convertRate);
                    reportTopAgentStaticDayPO.setDepositWithdrawFeeAmount(userDepositWithdrawalResVO.getSettlementFeeAmount());
                    String reportKey = reportTopAgentStaticDayPO.getReportDate().concat(reportTopAgentStaticDayPO.getAgentId());
                    log.info("总代报表-开始统计用户充提手续费:{},{}", reportKey, reportTopAgentStaticDayPO);
                    saveData(reportTopAgentStaticDayPO);
                }
            }
            log.info("总代报表站点:{},处理结束第:{}页 充提手续费计算", siteCode, pageNum);
        }
    }


    //统计代理代存 金额 agent_deposit_subordinates 里 deposit_time ; amount
    private void initAgentDepositSubordinate(ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO, Map<String, BigDecimal> rateMap) {
        String siteCode = reportTopAgentStaticsCondVO.getSiteCode();
        String reportType = reportTopAgentStaticsCondVO.getReportType();
        String timeZone = reportTopAgentStaticsCondVO.getTimeZone();

        AgentDepositSubordinatesPageReqVo vo = new AgentDepositSubordinatesPageReqVo();
        vo.setSiteCode(siteCode);
        vo.setStartTime(reportTopAgentStaticsCondVO.getStartDayMillis());
        vo.setEndTime(reportTopAgentStaticsCondVO.getEndDayMillis());
        vo.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        vo.setPageSize(500);
        vo.setPageNumber(1);
        Page<AgentDepositOfSubordinatesResVO> depositOfSubordinatesResVOPage = agentDepositSubordinatesApi.listPage(vo);
        if (!CollectionUtils.isEmpty(depositOfSubordinatesResVOPage.getRecords())) {
            long totalPages = depositOfSubordinatesResVOPage.getPages();
            log.info("总代报表-代理代存 参数:{},总页数:{}", reportTopAgentStaticsCondVO, totalPages);

            for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
                vo.setPageNumber(pageNum);
                Page<AgentDepositOfSubordinatesResVO> resultPage = agentDepositSubordinatesApi.listPage(vo);


                List<String> agentIds = resultPage.getRecords().stream().filter(o -> StringUtils.hasText(o.getAgentId())).map(AgentDepositOfSubordinatesResVO::getAgentId).toList();
                if (CollectionUtils.isEmpty(agentIds)) {
                    continue;
                }
                List<AgentInfoVO> agentInfoVOS = agentInfoApi.getByAgentIds(agentIds);

                for (AgentDepositOfSubordinatesResVO agentDepositOfSubordinatesResVO : resultPage.getRecords()) {
                    Optional<AgentInfoVO> agentInfoVOOptional = agentInfoVOS.stream().filter(o -> o.getAgentId().equals(agentDepositOfSubordinatesResVO.getAgentId())).findFirst();
                    if (agentInfoVOOptional.isEmpty()) {
                        log.info("总代报表-代理代存 代理信息不存在:{}", agentDepositOfSubordinatesResVO.getAgentId());
                        continue;
                    }

                    AgentInfoVO currentAgentInfo = agentInfoVOOptional.get();
                    String agentInfoPath = StringUtils.hasText(currentAgentInfo.getPath()) ? currentAgentInfo.getPath() : currentAgentInfo.getAgentId();
                    //计算当前用户所属代理、计算代理的所有上级代理
                    // 即计算某个代理所属团队的所有用户数据
                    String[] allParentAgentIdArray = agentInfoPath.split(",");
                    for (int i = 0; i <= allParentAgentIdArray.length - 1; i++) {
                        ReportTopAgentStaticDayPO reportTopAgentStaticDayPO = new ReportTopAgentStaticDayPO();
                        reportTopAgentStaticDayPO.setReportType(reportType);
                        String reportDay = DateUtils.formatDateByZoneId(agentDepositOfSubordinatesResVO.getDepositTime(), DateUtils.DATE_FORMAT_1, timeZone);
                        String reportMonth = DateUtils.formatDateByZoneId(agentDepositOfSubordinatesResVO.getDepositTime(), DateUtils.DATE_FORMAT_2, timeZone);
                        // 按天统计
                        if ("0".equals(reportType)) {
                            Long dayStartTime = DateUtils.getStartDayMillis(agentDepositOfSubordinatesResVO.getDepositTime(), timeZone);
                            reportTopAgentStaticDayPO.setDayMillis(dayStartTime);
                            reportTopAgentStaticDayPO.setReportDate(reportDay);
                        } else {
                            //按月统计
                            Long monthStartTime = DateUtils.getStartDayMonthTimestamp(agentDepositOfSubordinatesResVO.getDepositTime(), timeZone);
                            reportTopAgentStaticDayPO.setDayMillis(monthStartTime);
                            reportTopAgentStaticDayPO.setReportDate(reportMonth);
                        }
                        reportTopAgentStaticDayPO.setSiteCode(siteCode);
                        String parentAgentId = allParentAgentIdArray[i];
                        reportTopAgentStaticDayPO.setMerchantAccount(currentAgentInfo.getMerchantAccount());
                        reportTopAgentStaticDayPO.setMerchantName(currentAgentInfo.getMerchantName());
                        reportTopAgentStaticDayPO.setAgentId(parentAgentId);
                        reportTopAgentStaticDayPO.setCurrencyCode(agentDepositOfSubordinatesResVO.getCurrencyCode());
                        //代理代存 同时操作  会员存款增加  代理提款增加
                        reportTopAgentStaticDayPO.setDepositAmount(agentDepositOfSubordinatesResVO.getAmount());
                        reportTopAgentStaticDayPO.setWithdrawAmount(agentDepositOfSubordinatesResVO.getAmount());
                        //  reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                        // BigDecimal convertRate=rateMap.get(agentDepositOfSubordinatesResVO.get());
                        //  BigDecimal depositAmountWtc=AmountUtils.divide(agentDepositOfSubordinatesResVO.getAmount(),convertRate);
                        // reportAgentMerchantStaticDayPO.setDepositAmount(agentDepositOfSubordinatesResVO.getPlatformAmount());
                        String reportKey = reportTopAgentStaticDayPO.getReportDate().concat(reportTopAgentStaticDayPO.getAgentId());
                        log.info("总代报表-代理代存,开始记录代理代存金额:{},{}", reportKey, reportTopAgentStaticDayPO);
                        saveData(reportTopAgentStaticDayPO);
                    }
                }
            }
        }
    }


    /**
     * 会员人工加减额
     *
     * @param reportTopAgentStaticsCondVO 参数
     * @param rateMap                     汇率
     */
    private void initUserManualUpDown(ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO, Map<String, BigDecimal> rateMap) {
        String siteCode = reportTopAgentStaticsCondVO.getSiteCode();
        String reportType = reportTopAgentStaticsCondVO.getReportType();
        String timeZone = reportTopAgentStaticsCondVO.getTimeZone();

        UserManualDownRecordRequestVO vo = new UserManualDownRecordRequestVO();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setUpdateStartTime(reportTopAgentStaticsCondVO.getStartDayMillis());
        vo.setUpdateEndTime(reportTopAgentStaticsCondVO.getEndDayMillis());
        vo.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
        Page<UserManualDownRecordVO> userManualDownRecordVOPage = userManualUpDownApi.listPage(vo);
        long totalPages = userManualDownRecordVOPage.getPages();
        for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
            vo.setPageNumber(pageNum);
            userManualDownRecordVOPage = userManualUpDownApi.listPage(vo);
            List<String> agentIds = userManualDownRecordVOPage.getRecords().stream().filter(o -> StringUtils.hasText(o.getAgentId())).map(UserManualDownRecordVO::getAgentId).toList();
            if (CollectionUtils.isEmpty(agentIds)) {
                continue;
            }
            List<AgentInfoVO> agentInfoVOS = agentInfoApi.getByAgentIds(agentIds);
            List<UserManualDownRecordVO> userManualDownRecordVOS = userManualDownRecordVOPage.getRecords();
            for (UserManualDownRecordVO userManualDownRecordVO : userManualDownRecordVOS) {

                Optional<AgentInfoVO> agentInfoVOOptional = agentInfoVOS.stream().filter(o -> o.getAgentId().equals(userManualDownRecordVO.getAgentId())).findFirst();
                if (agentInfoVOOptional.isEmpty()) {
                    log.info("总代报表-会员人工加减额 代理信息不存在:{}", userManualDownRecordVO.getAgentId());
                    continue;
                }

                AgentInfoVO currentAgentInfo = agentInfoVOOptional.get();
                String agentInfoPath = StringUtils.hasText(currentAgentInfo.getPath()) ? currentAgentInfo.getPath() : currentAgentInfo.getAgentId();
                //计算当前用户所属代理、计算代理的所有上级代理
                // 即计算某个代理所属团队的所有用户数据
                String[] allParentAgentIdArray = agentInfoPath.split(",");
                for (int i = 0; i <= allParentAgentIdArray.length - 1; i++) {
                    ReportTopAgentStaticDayPO reportTopAgentStaticDayPO = new ReportTopAgentStaticDayPO();
                    reportTopAgentStaticDayPO.setReportType(reportType);
                    String reportDay = DateUtils.formatDateByZoneId(userManualDownRecordVO.getUpdatedTime(), DateUtils.DATE_FORMAT_1, timeZone);
                    String reportMonth = DateUtils.formatDateByZoneId(userManualDownRecordVO.getUpdatedTime(), DateUtils.DATE_FORMAT_2, timeZone);
                    // 按天统计
                    if ("0".equals(reportType)) {
                        Long dayStartTime = DateUtils.getStartDayMillis(userManualDownRecordVO.getUpdatedTime(), timeZone);
                        reportTopAgentStaticDayPO.setDayMillis(dayStartTime);
                        reportTopAgentStaticDayPO.setReportDate(reportDay);
                    } else {
                        //按月统计
                        Long monthStartTime = DateUtils.getStartDayMonthTimestamp(userManualDownRecordVO.getUpdatedTime(), timeZone);
                        reportTopAgentStaticDayPO.setDayMillis(monthStartTime);
                        reportTopAgentStaticDayPO.setReportDate(reportMonth);
                    }
                    String parentAgentId = allParentAgentIdArray[i];
                    reportTopAgentStaticDayPO.setSiteCode(siteCode);
                    reportTopAgentStaticDayPO.setMerchantAccount(currentAgentInfo.getMerchantAccount());
                    reportTopAgentStaticDayPO.setMerchantName(currentAgentInfo.getMerchantName());
                    reportTopAgentStaticDayPO.setAgentId(parentAgentId);
                    // reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                    reportTopAgentStaticDayPO.setCurrencyCode(userManualDownRecordVO.getCurrencyCode());
                    boolean dataExists = false;
                    if (ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode().equals(userManualDownRecordVO.getAdjustWay())) {
                        if (Objects.equals(ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode(), userManualDownRecordVO.getAdjustType())) {
                            // BigDecimal convertRate=rateMap.get(userManualDownRecordVO.getCurrencyCode());
                            //  BigDecimal depositAmountWtc=AmountUtils.divide(userManualDownRecordVO.getAdjustAmount(),convertRate);
                            reportTopAgentStaticDayPO.setDepositAmount(userManualDownRecordVO.getAdjustAmount());
                            dataExists = true;
                        }
                    }
                    if (ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode().equals(userManualDownRecordVO.getAdjustWay())) {
                        if (Objects.equals(ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode(), userManualDownRecordVO.getAdjustType())) {
                            // BigDecimal convertRate=rateMap.get(userManualDownRecordVO.getCurrencyCode());
                            //  BigDecimal withdrawAmountWtc=AmountUtils.divide(userManualDownRecordVO.getAdjustAmount(),convertRate);
                            reportTopAgentStaticDayPO.setWithdrawAmount(userManualDownRecordVO.getAdjustAmount());
                            dataExists = true;
                        }
                    }
                    //更新至统计报表
                    if (dataExists) {
                        String reportKey = reportTopAgentStaticDayPO.getReportDate().concat(reportTopAgentStaticDayPO.getAgentId());
                        log.info("总代报表-会员人工加减金额开始记录:{},{}", reportKey, reportTopAgentStaticDayPO);
                        saveData(reportTopAgentStaticDayPO);
                    }
                }

            }
        }
    }

    /**
     * 代理人工加减额
     * 对于人工加减额来说   代理存款(后台)-额度钱包\代理提款(后台)-佣金钱包   才统计到存款总额、提款总额里
     *
     * @param reportTopAgentStaticsCondVO 代理人工加减额
     */
    private void initAgentManualUpDown(ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO) {
        String siteCode = reportTopAgentStaticsCondVO.getSiteCode();
        String timeZone = reportTopAgentStaticsCondVO.getTimeZone();
        String reportType = reportTopAgentStaticsCondVO.getReportType();
        AgentManualDownRequestVO vo = new AgentManualDownRequestVO();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setCreatorStartTime(reportTopAgentStaticsCondVO.getStartDayMillis());
        vo.setCreatorEndTime(reportTopAgentStaticsCondVO.getEndDayMillis());
        vo.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
        Page<AgentManualUpRecordResponseVO> agentManualUpRecordResponseVOPage = agentManualUpDownApi.listPage(vo);
        for (int pageNum = 1; pageNum <= agentManualUpRecordResponseVOPage.getPages(); pageNum++) {
            vo.setPageNumber(pageNum);
            agentManualUpRecordResponseVOPage = agentManualUpDownApi.listPage(vo);
            List<AgentManualUpRecordResponseVO> agentManualUpRecordResponseVOS = agentManualUpRecordResponseVOPage.getRecords();

            List<String> agentIds = agentManualUpRecordResponseVOS.stream().filter(o -> StringUtils.hasText(o.getAgentId())).map(AgentManualUpRecordResponseVO::getAgentId).toList();
            if (CollectionUtils.isEmpty(agentIds)) {
                continue;
            }
            List<AgentInfoVO> agentInfoVOS = agentInfoApi.getByAgentIds(agentIds);

            for (AgentManualUpRecordResponseVO agentManualUpRecordResponseVO : agentManualUpRecordResponseVOS) {
                Optional<AgentInfoVO> agentInfoVOOptional = agentInfoVOS.stream().filter(o -> o.getAgentId().equals(agentManualUpRecordResponseVO.getAgentId())).findFirst();
                if (agentInfoVOOptional.isEmpty()) {
                    log.info("商务报表代理人工加减额 代理信息不存在:{}", agentManualUpRecordResponseVO.getAgentId());
                    continue;
                }
                AgentInfoVO currentAgentInfo = agentInfoVOOptional.get();
                String agentInfoPath = StringUtils.hasText(currentAgentInfo.getPath()) ? currentAgentInfo.getPath() : currentAgentInfo.getAgentId();
                //计算当前用户所属代理、计算代理的所有上级代理
                // 即计算某个代理所属团队的所有用户数据
                String[] allParentAgentIdArray = agentInfoPath.split(",");
                for (int i = 0; i <= allParentAgentIdArray.length - 1; i++) {
                    ReportTopAgentStaticDayPO reportTopAgentStaticDayPO = new ReportTopAgentStaticDayPO();
                    reportTopAgentStaticDayPO.setReportType(reportType);
                    reportTopAgentStaticDayPO.setSiteCode(siteCode);
                    String parentAgentId = allParentAgentIdArray[i];
                    reportTopAgentStaticDayPO.setMerchantAccount(currentAgentInfo.getMerchantAccount());
                    reportTopAgentStaticDayPO.setMerchantName(currentAgentInfo.getMerchantName());
                    reportTopAgentStaticDayPO.setAgentId(parentAgentId);
                    reportTopAgentStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                    String reportDay = DateUtils.formatDateByZoneId(agentManualUpRecordResponseVO.getUpdatedTime(), DateUtils.DATE_FORMAT_1, timeZone);
                    String reportMonth = DateUtils.formatDateByZoneId(agentManualUpRecordResponseVO.getUpdatedTime(), DateUtils.DATE_FORMAT_2, timeZone);
                    // 按天统计
                    if ("0".equals(reportType)) {
                        Long dayStartTime = DateUtils.getStartDayMillis(agentManualUpRecordResponseVO.getUpdatedTime(), timeZone);
                        reportTopAgentStaticDayPO.setDayMillis(dayStartTime);
                        reportTopAgentStaticDayPO.setReportDate(reportDay);
                    } else {
                        //按月统计
                        Long monthStartTime = DateUtils.getStartDayMonthTimestamp(agentManualUpRecordResponseVO.getUpdatedTime(), timeZone);
                        reportTopAgentStaticDayPO.setDayMillis(monthStartTime);
                        reportTopAgentStaticDayPO.setReportDate(reportMonth);
                    }
                    reportTopAgentStaticDayPO.setDepositAmount(BigDecimal.ZERO);
                    reportTopAgentStaticDayPO.setWithdrawAmount(BigDecimal.ZERO);
                    boolean dataExists = false;
                    if (ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode().equals(agentManualUpRecordResponseVO.getAdjustWay())) {
                        if (AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode().equals(agentManualUpRecordResponseVO.getAdjustType().toString())) {
                            reportTopAgentStaticDayPO.setDepositAmount(agentManualUpRecordResponseVO.getAdjustAmount());
                            dataExists = true;
                        }
                    }
                    if (ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode().equals(agentManualUpRecordResponseVO.getAdjustWay())) {
                        if (AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getCode().equals(agentManualUpRecordResponseVO.getAdjustType().toString())) {
                            reportTopAgentStaticDayPO.setWithdrawAmount(agentManualUpRecordResponseVO.getAdjustAmount());
                            dataExists = true;
                        }
                    }
                    //更细至统计报表
                    if (dataExists) {
                        String reportKey = reportTopAgentStaticDayPO.getReportDate().concat(reportTopAgentStaticDayPO.getAgentId());
                        log.info("总代报表-代理人工加减额开始记录:{},{}", reportKey, reportTopAgentStaticDayPO);
                        saveData(reportTopAgentStaticDayPO);
                    }
                }

            }
        }
    }

    /**
     * 代理金额统计
     *
     * @param userWinLoseResponseVOList 原始会员盈亏列表
     * @param reportType                报表统计类型 0:日报 1:月报
     * @param timeZone                  时区
     * @param rateMap                   汇率
     */
    private void batchProcess(List<UserWinLoseListResponseVO> userWinLoseResponseVOList,
                              String reportType,
                              String timeZone,
                              Map<String, BigDecimal> rateMap
    ) {

        if (CollectionUtils.isEmpty(userWinLoseResponseVOList)) {
            return;
        }
        Map<String, ReportTopAgentStaticDayPO> sumMap = new HashMap<String, ReportTopAgentStaticDayPO>();

        List<String> agentIds = userWinLoseResponseVOList.stream().filter(o -> StringUtils.hasText(o.getAgentId())).map(UserWinLoseListResponseVO::getAgentId).toList();
        if (CollectionUtils.isEmpty(agentIds)) {
            return;
        }
        //代理信息
        List<AgentInfoVO> agentInfoVOS = agentInfoApi.getByAgentIds(agentIds);
        for (UserWinLoseListResponseVO userWinLoseResponseVO : userWinLoseResponseVOList) {

            // 数据为空的不统计
            boolean hasData = false;
            if (userWinLoseResponseVO.getBetNum() > 0) {
                hasData = true;
            }
            if (userWinLoseResponseVO.getBetAmount().compareTo(BigDecimal.ZERO) != 0) {
                hasData = true;
            }
            if (userWinLoseResponseVO.getValidBetAmount().compareTo(BigDecimal.ZERO) != 0) {
                hasData = true;
            }
            if (userWinLoseResponseVO.getBetWinLose().compareTo(BigDecimal.ZERO) != 0) {
                hasData = true;
            }
            if (userWinLoseResponseVO.getVipAmount().compareTo(BigDecimal.ZERO) != 0) {
                hasData = true;
            }
            if (userWinLoseResponseVO.getAlreadyUseAmount().compareTo(BigDecimal.ZERO) != 0) {
                hasData = true;
            }
            if (userWinLoseResponseVO.getActivityAmount().compareTo(BigDecimal.ZERO) != 0) {
                hasData = true;
            }
            if (userWinLoseResponseVO.getRebateAmount().compareTo(BigDecimal.ZERO) != 0) {
                hasData = true;
            }

            if (!hasData) {
                log.info("统计数据为空,不需要处理:{}", userWinLoseResponseVO);
                continue;
            }


            ReportTopAgentStaticDayPO reportTopAgentStaticDayPO = new ReportTopAgentStaticDayPO();
            String siteCode = userWinLoseResponseVO.getSiteCode();
            String agentAccount = userWinLoseResponseVO.getSuperAgentAccount();
            if (!StringUtils.hasText(agentAccount)) {
                log.debug("{},代理账号不存在,无须统计", userWinLoseResponseVO.getUserId());
                continue;
            }
            String currencyCode = userWinLoseResponseVO.getMainCurrency();
            Long dayMillis = userWinLoseResponseVO.getDayMillis();
            String reportDay = DateUtils.formatDateByZoneId(dayMillis, DateUtils.DATE_FORMAT_1, timeZone);
            String reportMonth = DateUtils.formatDateByZoneId(dayMillis, DateUtils.DATE_FORMAT_2, timeZone);
            String reportDate = "";
            String dayStr = "";
            Long monthStartTime = DateUtils.getYearMonthTime(reportMonth);
            reportTopAgentStaticDayPO.setAgentId(userWinLoseResponseVO.getAgentId());
            reportTopAgentStaticDayPO.setAgentAccount(agentAccount);
            reportTopAgentStaticDayPO.setSiteCode(userWinLoseResponseVO.getSiteCode());
            reportTopAgentStaticDayPO.setReportType(reportType);
            // 按天统计
            if ("0".equals(reportType)) {
                reportTopAgentStaticDayPO.setDayMillis(dayMillis);
                reportTopAgentStaticDayPO.setReportDate(reportDay);
                reportDate = reportDay;
                dayStr = dayMillis.toString();
            } else {
                //按月统计
                reportTopAgentStaticDayPO.setDayMillis(monthStartTime);
                reportTopAgentStaticDayPO.setReportDate(reportMonth);
                reportDate = reportMonth;
                dayMillis = monthStartTime;
                dayStr = monthStartTime.toString();
            }
            reportTopAgentStaticDayPO.setCurrencyCode(userWinLoseResponseVO.getMainCurrency());


            //注单量
            reportTopAgentStaticDayPO.setBetUserCount(Long.valueOf(userWinLoseResponseVO.getBetNum()));
            //投注金额
            // BigDecimal convertRate=rateMap.get(userWinLoseResponseVO.getMainCurrency());
            // BigDecimal betAmountWtc=AmountUtils.divide(userWinLoseResponseVO.getBetAmount(),convertRate);
            reportTopAgentStaticDayPO.setBetAmount(userWinLoseResponseVO.getBetAmount());
            //有效投注
            //  BigDecimal validBetAmountWtc=AmountUtils.divide(userWinLoseResponseVO.getValidBetAmount(),convertRate);
            reportTopAgentStaticDayPO.setValidAmount(userWinLoseResponseVO.getValidBetAmount());
            //返水金额
            reportTopAgentStaticDayPO.setRebateAmount(userWinLoseResponseVO.getRebateAmount());
            //会员输赢
            //  BigDecimal betWinLoseWtc=AmountUtils.divide(userWinLoseResponseVO.getBetWinLose(),convertRate);
            reportTopAgentStaticDayPO.setWinLossAmountUser(userWinLoseResponseVO.getBetWinLose());
            //打赏金额
            reportTopAgentStaticDayPO.setTipsAmount(userWinLoseResponseVO.getTipsAmount());
            //总输赢 = -（用户投注输赢 - 打赏金额）
            reportTopAgentStaticDayPO.setWinLossAmountPlat(BigDecimal.ZERO.subtract(reportTopAgentStaticDayPO.getWinLossAmountUser().subtract(reportTopAgentStaticDayPO.getTipsAmount())));
            //已使用金额
            //BigDecimal alreadyUseAmountWtc=AmountUtils.divide(userWinLoseResponseVO.getAlreadyUseAmount(),convertRate);
            reportTopAgentStaticDayPO.setAlreadyUseAmount(userWinLoseResponseVO.getAlreadyUseAmount());

            reportTopAgentStaticDayPO.setVipAmount(userWinLoseResponseVO.getVipAmount());
            reportTopAgentStaticDayPO.setActivityAmount(userWinLoseResponseVO.getActivityAmount());
            reportTopAgentStaticDayPO.setDepositWithdrawFeeAmount(BigDecimal.ZERO);
            reportTopAgentStaticDayPO.setDepositAmount(BigDecimal.ZERO);
            reportTopAgentStaticDayPO.setWithdrawAmount(BigDecimal.ZERO);

            AgentInfoVO currentAgentInfo = agentInfoVOS.stream().filter(o -> o.getAgentId().equals(userWinLoseResponseVO.getAgentId())).findFirst().get();
            String agentInfoPath = StringUtils.hasText(currentAgentInfo.getPath()) ? currentAgentInfo.getPath() : currentAgentInfo.getAgentId();
            //计算当前用户所属代理、计算代理的所有上级代理
            // 即计算某个代理所属团队的所有用户数据
            String[] allParentAgentIdArray = agentInfoPath.split(",");
            log.debug("currentAgentId:{},allParentAgentIdArray:{}", currentAgentInfo.getAgentId(), allParentAgentIdArray);
            for (int i = 0; i <= allParentAgentIdArray.length - 1; i++) {
                String parentAgentId = allParentAgentIdArray[i];
                reportTopAgentStaticDayPO.setAgentId(parentAgentId);
                String mapKey = siteCode
                        .concat("#")
                        .concat(parentAgentId)
                        .concat("#")
                        .concat(currencyCode)
                        .concat("#")
                        .concat(dayStr)
                        .concat("#")
                        .concat(reportDate);

                //   log.info("{}投注人数:{},mapKey:{}",parentAgentId,userSet.size(),mapKey);
                if (sumMap.containsKey(mapKey)) {
                    ReportTopAgentStaticDayPO reportTopAgentStaticDayPOMapMemory = sumMap.get(mapKey);
                    reportTopAgentStaticDayPOMapMemory.setBetAmount(reportTopAgentStaticDayPO.getBetAmount().add(reportTopAgentStaticDayPOMapMemory.getBetAmount()));
                    reportTopAgentStaticDayPOMapMemory.setValidAmount(reportTopAgentStaticDayPO.getValidAmount().add(reportTopAgentStaticDayPOMapMemory.getValidAmount()));
                    //返水金额
                    reportTopAgentStaticDayPOMapMemory.setRebateAmount(reportTopAgentStaticDayPO.getRebateAmount().add(reportTopAgentStaticDayPOMapMemory.getRebateAmount()));

                    reportTopAgentStaticDayPOMapMemory.setWinLossAmountUser(reportTopAgentStaticDayPO.getWinLossAmountUser().add(reportTopAgentStaticDayPOMapMemory.getWinLossAmountUser()));
                    reportTopAgentStaticDayPOMapMemory.setTipsAmount(reportTopAgentStaticDayPO.getTipsAmount().add(reportTopAgentStaticDayPOMapMemory.getTipsAmount()));
                    reportTopAgentStaticDayPOMapMemory.setWinLossAmountPlat(reportTopAgentStaticDayPO.getWinLossAmountPlat().add(reportTopAgentStaticDayPOMapMemory.getWinLossAmountPlat()));
                    reportTopAgentStaticDayPOMapMemory.setAdjustAmount(reportTopAgentStaticDayPO.getAdjustAmount().add(reportTopAgentStaticDayPOMapMemory.getAdjustAmount()));
                    reportTopAgentStaticDayPOMapMemory.setActivityAmount(reportTopAgentStaticDayPO.getActivityAmount().add(reportTopAgentStaticDayPOMapMemory.getActivityAmount()));
                    reportTopAgentStaticDayPOMapMemory.setVipAmount(reportTopAgentStaticDayPO.getVipAmount().add(reportTopAgentStaticDayPOMapMemory.getVipAmount()));
                    reportTopAgentStaticDayPOMapMemory.setAlreadyUseAmount(reportTopAgentStaticDayPO.getAlreadyUseAmount().add(reportTopAgentStaticDayPOMapMemory.getAlreadyUseAmount()));

                    BigDecimal betAmount = reportTopAgentStaticDayPOMapMemory.getBetAmount();
                    BigDecimal winLossAmountPlat = reportTopAgentStaticDayPOMapMemory.getWinLossAmountPlat();
                    //盈亏比例 等于 平台总输赢 / 投注额
                    BigDecimal winLossRate = AmountUtils.divide(winLossAmountPlat, betAmount, 4);
                    reportTopAgentStaticDayPOMapMemory.setWinLossRate(winLossRate);

                    //投注人次
                    reportTopAgentStaticDayPOMapMemory.setBetUserCount(reportTopAgentStaticDayPO.getBetUserCount() + reportTopAgentStaticDayPOMapMemory.getBetUserCount());
                    log.debug("总代报表-投注人数:{},key:{},数据2:{}", parentAgentId, mapKey, reportTopAgentStaticDayPOMapMemory);
                    sumMap.put(mapKey, reportTopAgentStaticDayPOMapMemory);
                } else {
                    ReportTopAgentStaticDayPO reportTopAgentStaticDayPOMap = new ReportTopAgentStaticDayPO();
                    BeanUtils.copyProperties(reportTopAgentStaticDayPO, reportTopAgentStaticDayPOMap);
                    sumMap.put(mapKey, reportTopAgentStaticDayPOMap);
                    log.debug("总代报表-投注人数:{},key:{},数据1:{}", parentAgentId, mapKey, reportTopAgentStaticDayPOMap);
                }
            }
        }
        //存储至DB
        for (ReportTopAgentStaticDayPO reportAgentStaticDay : sumMap.values()) {
            //保存至数据库
            saveData(reportAgentStaticDay);
        }
    }


    /**
     * 填充统计人数
     *
     * @param reportTopAgentStaticDay
     */
    private void fillAgentNum(ReportTopAgentStaticDayPO reportTopAgentStaticDay) {
        String agentId = reportTopAgentStaticDay.getAgentId();
        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(agentId);
        //代理信息填充
        reportTopAgentStaticDay.setParentAccount(agentInfoVO.getParentAccount());
        reportTopAgentStaticDay.setParentId(agentInfoVO.getParentId());
        reportTopAgentStaticDay.setAgentAccount(agentInfoVO.getAgentAccount());
        reportTopAgentStaticDay.setPath(agentInfoVO.getPath());
        reportTopAgentStaticDay.setLevel(agentInfoVO.getLevel());
        reportTopAgentStaticDay.setAgentType(agentInfoVO.getAgentType());
        reportTopAgentStaticDay.setRegisterTime(agentInfoVO.getRegisterTime());
        reportTopAgentStaticDay.setAgentAttribution(agentInfoVO.getAgentAttribution());
        reportTopAgentStaticDay.setAgentLabelId(agentInfoVO.getAgentLabelId());
        reportTopAgentStaticDay.setRiskLevelId(agentInfoVO.getRiskLevelId());
        reportTopAgentStaticDay.setMerchantAccount(agentInfoVO.getMerchantAccount());
        reportTopAgentStaticDay.setMerchantName(agentInfoVO.getMerchantName());
        //List<String> subAgentList=agentInfoApi.getSubAgentIdList(agentId);
        //所有下级代理人数 团队代理人数
        // long teamNum=Long.valueOf(subAgentList.size());
        // reportTopAgentStaticDay.setTeamAgentNum(teamNum);
        //直属下级人数
        // List<String> subAgentDirectReportList=agentInfoApi.getSubAgentIdDirectReportList(agentId);
        // reportTopAgentStaticDay.setDirectReportNum(Long.valueOf(subAgentDirectReportList.size()));
    }

    /**
     * 统计 团队代理人数  团队会员人数
     *
     * @param reportTopAgentStaticsCondVO 统计条件
     */
    private void fillTeamNum(ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO) {
        String siteCode = reportTopAgentStaticsCondVO.getSiteCode();
        String reportType = reportTopAgentStaticsCondVO.getReportType();
        //代理注册日期
        AgentInfoPageVO vo = new AgentInfoPageVO();
        vo.setSiteCode(reportTopAgentStaticsCondVO.getSiteCode());
        vo.setRegisterTimeStart(reportTopAgentStaticsCondVO.getStartDayMillis());
        vo.setRegisterTimeEnd(reportTopAgentStaticsCondVO.getEndDayMillis());
        vo.setPageNumber(1);
        vo.setPageSize(500);
        Page<AgentInfoPageResultVo> agentInfoPage = agentListApi.listPage(vo);
        long totalPageNum = agentInfoPage.getPages();
        for (int pageNum = 1; pageNum <= totalPageNum; pageNum++) {
            vo.setPageNumber(pageNum);
            log.info("商务端-总代报表代理团队人数查询:{}", vo);
            agentInfoPage = agentListApi.listPage(vo);
            for (AgentInfoPageResultVo agentInfoResponseVO : agentInfoPage.getRecords()) {
                String agentPah = agentInfoResponseVO.getPath();
                long dayMillis = DateUtils.getStartDayMillis(agentInfoResponseVO.getRegisterTime(), reportTopAgentStaticsCondVO.getTimeZone());
                if (StringUtils.hasText(agentPah)) {
                    String[] agentIdArray = agentPah.split(",");
                    for (int i = 0; i <= agentIdArray.length - 1; i++) {
                        ReportTopAgentStaticDayPO agentStaticDayPO = new ReportTopAgentStaticDayPO();
                        agentStaticDayPO.setDayMillis(dayMillis);
                        agentStaticDayPO.setSiteCode(siteCode);
                        agentStaticDayPO.setReportType(reportType);
                        String staticDay = DateUtils.formatDateByZoneId(dayMillis, DateUtils.DATE_FORMAT_1, reportTopAgentStaticsCondVO.getTimeZone());
                        agentStaticDayPO.setReportDate(staticDay);
                        agentStaticDayPO.setTeamAgentNum(1L);
                        agentStaticDayPO.setAgentId(agentIdArray[i]);
                        agentStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                        String uniKey = agentStaticDayPO.getAgentId()
                                .concat("-")
                                .concat(staticDay);
                        log.info("商务端-总代报表,代理团队人数累计:{},{}", uniKey, agentStaticDayPO);
                        saveData(agentStaticDayPO);
                        // saveTeamNum(agentStaticDayPO);
                    }
                }
            }
        }
        //会员注册时间、会员溢出时间、会员转代时间 如果是同一天 按照会员转代时间、会员溢出时间顺序归属到某个代理上
        UserRegistrationInfoReqVO userRegistrationInfoReqVO = new UserRegistrationInfoReqVO();
        userRegistrationInfoReqVO.setSiteCode(siteCode);
        userRegistrationInfoReqVO.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        userRegistrationInfoReqVO.setStartRegistrationTime(reportTopAgentStaticsCondVO.getStartDayMillis());
        userRegistrationInfoReqVO.setEndRegistrationTime(reportTopAgentStaticsCondVO.getEndDayMillis());
        userRegistrationInfoReqVO.setOwnerAgent(1);
        userRegistrationInfoReqVO.setPageNumber(1);
        userRegistrationInfoReqVO.setPageSize(100);
        Page<UserRegistrationInfoResVO> userRegistrationInfoResVOPage = userRegistrationInfoApi.listPage(userRegistrationInfoReqVO);
        long userRegisterTotalPageNum = userRegistrationInfoResVOPage.getPages();
        for (int pageNum = 1; pageNum <= userRegisterTotalPageNum; pageNum++) {
            userRegistrationInfoReqVO.setPageNumber(pageNum);
            log.info("商务端-总代报表,总代,注册会员人数统计:{},当前页数:{},总页数:{}", userRegistrationInfoReqVO, pageNum, userRegisterTotalPageNum);
            userRegistrationInfoResVOPage = userRegistrationInfoApi.listPage(userRegistrationInfoReqVO);
            for (UserRegistrationInfoResVO userRegistrationInfoResVO : userRegistrationInfoResVOPage.getRecords()) {
                if (!StringUtils.hasText(userRegistrationInfoResVO.getAgentId())) {
                    continue;
                }
                Map<String, String> incUserMap = new HashMap<>();//key=站点+会员账号+时间 值=代理id 统计会员人数增量
                long dayMillis = DateUtils.getStartDayMillis(userRegistrationInfoResVO.getRegistrationTime(), reportTopAgentStaticsCondVO.getTimeZone());
                String uniKey = userRegistrationInfoResVO.getSiteCode().concat("-").concat(userRegistrationInfoResVO.getMemberAccount()).concat("-").concat(dayMillis + "");
                incUserMap.put(uniKey, userRegistrationInfoResVO.getAgentId());
                log.info("商务端-总代报表:{},注册会员人数统计::{}", userRegistrationInfoResVO.getAgentId(), uniKey);
                //存储增量数据
                saveUserNum(incUserMap, 1L, reportTopAgentStaticsCondVO, "注册会员");
            }

        }
        //会员存在溢出 则归属到溢出代理上去
        MemberOverflowReviewPageReqVO memberOverflowReviewPageReqVO = new MemberOverflowReviewPageReqVO();
        memberOverflowReviewPageReqVO.setSiteCode(siteCode);
        memberOverflowReviewPageReqVO.setAuditTimeStart(reportTopAgentStaticsCondVO.getStartDayMillis());
        memberOverflowReviewPageReqVO.setAuditTimeEnd(reportTopAgentStaticsCondVO.getEndDayMillis());
        memberOverflowReviewPageReqVO.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        memberOverflowReviewPageReqVO.setPageNumber(1);
        memberOverflowReviewPageReqVO.setPageSize(500);
        Page<MemberOverflowReviewPageResVO> userOverflowResultPage = agentUserOverflowApi.listByAuditTime(memberOverflowReviewPageReqVO);
        if (!CollectionUtils.isEmpty(userOverflowResultPage.getRecords())) {
            long totalUserOverFlowPageNum = userOverflowResultPage.getPages();
            for (int pageNum = 1; pageNum <= totalUserOverFlowPageNum; pageNum++) {
                memberOverflowReviewPageReqVO.setPageNumber(pageNum);
                log.info("商务端-总代报表,溢出会员人数统计::{},当前页数:{},总页数:{}", memberOverflowReviewPageReqVO, pageNum, totalUserOverFlowPageNum);
                userOverflowResultPage = agentUserOverflowApi.listByAuditTime(memberOverflowReviewPageReqVO);
                for (MemberOverflowReviewPageResVO memberOverflowReviewPageResVO : userOverflowResultPage.getRecords()) {
                    Map<String, String> incUserMap = new HashMap<>();//key=站点+会员账号+时间 值=代理id 统计会员人数增量
                    long dayMillis = DateUtils.getStartDayMillis(memberOverflowReviewPageResVO.getAuditDatetime(), reportTopAgentStaticsCondVO.getTimeZone());
                    String uniKey = memberOverflowReviewPageResVO.getSiteCode().concat("-").concat(memberOverflowReviewPageResVO.getMemberName()).concat("-").concat(dayMillis + "");
                    incUserMap.put(uniKey, memberOverflowReviewPageResVO.getTransferAgentId());
                    log.info("商务端-总代报表:{},溢出会员人数统计::{}", memberOverflowReviewPageResVO.getTransferAgentId(), uniKey);
                    //存储增量数据
                    saveUserNum(incUserMap, 1L, reportTopAgentStaticsCondVO, "溢出会员");
                }
            }
        }

        MemberTransferReviewPageReqVO transferVo = new MemberTransferReviewPageReqVO();
        transferVo.setSiteCode(siteCode);
        transferVo.setAuditTimeStart(reportTopAgentStaticsCondVO.getStartDayMillis());
        transferVo.setAuditTimeEnd(reportTopAgentStaticsCondVO.getEndDayMillis());
        transferVo.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        transferVo.setPageNumber(1);
        transferVo.setPageSize(500);
        Page<MemberTransferReviewPageResVO> userTransferPage = userTransferAgentApi.listByAuditTime(transferVo);
        if (!CollectionUtils.isEmpty(userTransferPage.getRecords())) {
            long totalTransferPageNum = userTransferPage.getPages();
            for (int pageNum = 1; pageNum <= totalTransferPageNum; pageNum++) {
                transferVo.setPageNumber(pageNum);
                log.info("商务端-总代报表,会员转代人数统计::{},当前页数:{},总页数:{}", transferVo, pageNum, totalTransferPageNum);
                userTransferPage = userTransferAgentApi.listByAuditTime(transferVo);
                //同一天 会员存在转移 则归属到转移代理上去
                for (MemberTransferReviewPageResVO memberTransferReviewPageResVO : userTransferPage.getRecords()) {
                    Map<String, String> incUserMap = new HashMap<>();//key=站点+会员账号+时间 值=代理id 统计会员人数增量
                    Map<String, String> reduceUserMap = new HashMap<>();//key=站点+会员账号+时间 值=代理id 统计会员人数减量
                    long dayMillis = DateUtils.getStartDayMillis(memberTransferReviewPageResVO.getAuditDatetime(), reportTopAgentStaticsCondVO.getTimeZone());
                    String uniKey = memberTransferReviewPageResVO.getSiteCode().concat("-").concat(memberTransferReviewPageResVO.getUserAccount()).concat("-").concat(dayMillis + "");
                    incUserMap.put(uniKey, memberTransferReviewPageResVO.getTransferAgentId());
                    reduceUserMap.put(uniKey, memberTransferReviewPageResVO.getCurrentAgentId());
                    log.info("商务端-总代报表:{},会员转入统计:{}", memberTransferReviewPageResVO.getTransferAgentId(), uniKey);
                    //存储减量数据
                    saveUserNum(incUserMap, 1L, reportTopAgentStaticsCondVO, "会员转入");
                    log.info("商务端-总代报表:{},会员转出统计:{}", memberTransferReviewPageResVO.getCurrentAgentId(), uniKey);
                    //存储减量数据
                    saveUserNum(reduceUserMap, -1L, reportTopAgentStaticsCondVO, "会员转出");
                }
            }
        }
    }

    /**
     * 保存代理下会员数量
     *
     * @param userMap
     * @param incNum
     * @param reportTopAgentStaticsCondVO
     */
    private void saveUserNum(Map<String, String> userMap, Long incNum, ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO, String type) {
        String siteCode = reportTopAgentStaticsCondVO.getSiteCode();
        String reportType = reportTopAgentStaticsCondVO.getReportType();
        String timeZone = reportTopAgentStaticsCondVO.getTimeZone();
        if (!CollectionUtils.isEmpty(userMap)) {
            List<AgentInfoVO> agentInfoVOS = agentInfoApi.getByAgentIds(userMap.values().stream().toList());
            List<String> userAccounts = userMap.keySet().stream().map(o -> o.split("-")[1]).toList();
            List<UserInfoVO> userInfoVOS = userInfoApi.getByUserAccounts(userAccounts, siteCode);

            for (String userKey : userMap.keySet()) {
                // String siteCode=userKey.split("-")[0];
                String userAccount = userKey.split("-")[1];
                String dayMillis = userKey.split("-")[2];
                String agentId = userMap.get(userKey);
                Optional<UserInfoVO> usreInfoVOOptional = userInfoVOS.stream().filter(o -> o.getUserAccount().equals(userAccount)).findFirst();
                if (usreInfoVOOptional.isEmpty()) {
                    log.error("注册会员:{}在会员信息里找不到数据", userAccount);
                    return;
                }
                UserInfoVO currentUserInfo = usreInfoVOOptional.get();
                AgentInfoVO currentAgentInfo = agentInfoVOS.stream().filter(o -> o.getAgentId().equals(agentId)).findFirst().get();
                String agentPah = currentAgentInfo.getPath();
                log.info("商务端-总代报表,子代,会员人数累计:{}", userKey);
                if (StringUtils.hasText(agentPah)) {
                    String[] agentIdArray = agentPah.split(",");
                    for (int i = 0; i <= agentIdArray.length - 1; i++) {
                        String currentAgentId = agentIdArray[i];
                        ReportTopAgentStaticDayPO agentStaticDayPO = new ReportTopAgentStaticDayPO();
                        agentStaticDayPO.setDayMillis(Long.valueOf(dayMillis));
                        agentStaticDayPO.setSiteCode(siteCode);
                        agentStaticDayPO.setReportType(reportType);
                        String staticDay = DateUtils.formatDateByZoneId(Long.valueOf(dayMillis), DateUtils.DATE_FORMAT_1, timeZone);
                        agentStaticDayPO.setReportDate(staticDay);
                        agentStaticDayPO.setCurrencyCode(currentUserInfo.getMainCurrency());
                        agentStaticDayPO.setUserNum(incNum);
                        agentStaticDayPO.setAgentId(currentAgentId);
                        String uniKey = agentStaticDayPO.getAgentId()
                                .concat("-")
                                .concat(incNum + "")
                                .concat("-")
                                .concat(agentStaticDayPO.getCurrencyCode())
                                .concat("-")
                                .concat(staticDay);
                        log.info("商务端-总代报表,{},会员人数累计:{},{}", type, uniKey, agentStaticDayPO);
                        saveData(agentStaticDayPO);
                    }
                }
            }
        }
    }

    /**
     * 填充 注册人数 首存人数
     *
     * @param reportTopAgentStaticsCondVO 代理信息统计
     * @return
     */
    private void fillRegisterNum(ReportTopAgentStaticsCondVO reportTopAgentStaticsCondVO) {
        String siteCode = reportTopAgentStaticsCondVO.getSiteCode();
        String reportType = reportTopAgentStaticsCondVO.getReportType();
        //Map<String,ReportTopAgentStaticDayPO> agentUserRegisterStaticMap=new HashMap<>();
        Set<String> agentIds = Sets.newHashSet();
       /* UserInfoPageVO vo=new UserInfoPageVO();
        vo.setSiteCode(siteCode);
        vo.setAccountType(List.of(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode()));
        vo.setRegisterTimeStart(reportTopAgentStaticsCondVO.getStartDayMillis());
        vo.setRegisterTimeEnd(reportTopAgentStaticsCondVO.getEndDayMillis());
        vo.setPageNumber(1);
        vo.setPageSize(500);
        Page<UserInfoResponseVO> userInfoResponseVOFirstPage= userInfoApi.listPage(vo);
        long totalPageNum=userInfoResponseVOFirstPage.getPages();
        Map<String,Set<String>> userRegisterMap=new HashMap<>();
        for(int pageNum=1;pageNum<=totalPageNum;pageNum++){
            vo.setPageNumber(pageNum);
            log.info("商务总代报表注册人数查询:{}",vo);
            Page<UserInfoResponseVO> userInfoResponseVOPage= userInfoApi.listPage(vo);

            for(UserInfoResponseVO userInfoResponseVO:userInfoResponseVOPage.getRecords()){
                if(!StringUtils.hasText(userInfoResponseVO.getSuperAgentId())){
                    continue;
                }
                String mapKey=userInfoResponseVO.getSiteCode()
                        .concat("#")
                        .concat(userInfoResponseVO.getSuperAgentId())
                        .concat("#")
                        .concat(userInfoResponseVO.getMainCurrency());
                        if("0".equals(reportType)){
                            mapKey=mapKey.concat("#")
                            .concat(DateUtils.getStartDayMillis(userInfoResponseVO.getRegisterTime(),reportTopAgentStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getRegisterTime(),DateUtils.DATE_FORMAT_1,reportTopAgentStaticsCondVO.getTimeZone()));
                        }else {
                            mapKey=mapKey.concat("#")
                            .concat(DateUtils.getStartDayMonthTimestamp(userInfoResponseVO.getRegisterTime(),reportTopAgentStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getRegisterTime(),DateUtils.DATE_FORMAT_2,reportTopAgentStaticsCondVO.getTimeZone()));
                        }

                ReportTopAgentStaticDayPO reportTopAgentStaticDayPO=new ReportTopAgentStaticDayPO();
                reportTopAgentStaticDayPO.setReportType(reportType);
                reportTopAgentStaticDayPO.setSiteCode(userInfoResponseVO.getSiteCode());
                reportTopAgentStaticDayPO.setAgentId(userInfoResponseVO.getSuperAgentId());
                reportTopAgentStaticDayPO.setCurrencyCode(userInfoResponseVO.getMainCurrency());
                long starDayMillis=Long.valueOf(mapKey.split("#")[3]);
                reportTopAgentStaticDayPO.setDayMillis(starDayMillis);
                reportTopAgentStaticDayPO.setReportDate(mapKey.split("#")[4]);
                if(userRegisterMap.containsKey(mapKey)){
                    Set<String> userAccountSet=userRegisterMap.get(mapKey);
                    userAccountSet.add(userInfoResponseVO.getUserId());
                    userRegisterMap.put(mapKey,userAccountSet) ;
                    reportTopAgentStaticDayPO.setRegisterUserNum(Long.valueOf(userAccountSet.size()));
                    log.info("商务总代报表原始注册人数:{},key:{},value:{},cond:{} register02",userInfoResponseVO.getSuperAgentId(),mapKey,reportTopAgentStaticDayPO,reportTopAgentStaticsCondVO);
                }else {
                    Set<String> userAccountSet=new HashSet<>();
                    userAccountSet.add(userInfoResponseVO.getUserId());
                    userRegisterMap.put(mapKey,userAccountSet) ;
                    reportTopAgentStaticDayPO.setRegisterUserNum(1L);
                    log.info("商务总代报表原始注册人数:{},key:{},value:{},cond:{} register01",userInfoResponseVO.getSuperAgentId(),mapKey,reportTopAgentStaticDayPO,reportTopAgentStaticsCondVO);
                }

                agentUserRegisterStaticMap.put(mapKey,reportTopAgentStaticDayPO);
                agentIds.add(userInfoResponseVO.getSuperAgentId());
            }
        }*/

        Map<String, ReportTopAgentStaticDayPO> agentUserDepositStaticMap = new HashMap<>();

        UserInfoPageVO depositVo = new UserInfoPageVO();
        depositVo.setSiteCode(siteCode);
        depositVo.setFirstDepositTimeStart(reportTopAgentStaticsCondVO.getStartDayMillis());
        depositVo.setFirstDepositTimeEnd(reportTopAgentStaticsCondVO.getEndDayMillis());
        depositVo.setAccountType(List.of(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode()));
        depositVo.setAgentFlag(1);
        depositVo.setPageNumber(1);
        depositVo.setPageSize(500);
        Page<UserInfoResponseVO> userInfoDepositFirstPage = userInfoApi.listPage(depositVo);
        long totalDepositPageNum = userInfoDepositFirstPage.getPages();
        //Map<String,Set<String>> userDepositMap=new HashMap<>();
        for (int pageNum = 1; pageNum <= totalDepositPageNum; pageNum++) {
            depositVo.setPageNumber(pageNum);
            log.info("商务总代报表首存人数查询:{}", depositVo);
            Page<UserInfoResponseVO> userInfoDepositPage = userInfoApi.listPage(depositVo);
            for (UserInfoResponseVO userInfoResponseVO : userInfoDepositPage.getRecords()) {
                // log.debug("商务总代报表站点:{}首存人数:{},上级代理:{}",siteCode,userInfoResponseVO.getUserId(),userInfoResponseVO.getSuperAgentId());
                if (!StringUtils.hasText(userInfoResponseVO.getSuperAgentId())) {
                    log.debug("商务总代报表站点:{}首存人数:{},代理为空", siteCode, userInfoResponseVO.getUserId());
                    continue;
                }
                if (userInfoResponseVO.getFirstDepositTime() == null) {
                    log.debug("商务总代报表站点:{}首存人数:{},首存时间为空", siteCode, userInfoResponseVO.getUserId());
                    continue;
                }
                String mapKey = userInfoResponseVO.getSiteCode()
                        .concat("#")
                        .concat(userInfoResponseVO.getSuperAgentId())
                        .concat("#")
                        .concat(userInfoResponseVO.getUserId())
                        .concat("#")
                        .concat(userInfoResponseVO.getMainCurrency());
                if ("0".equals(reportType)) {
                    mapKey = mapKey.concat("#")
                            .concat(DateUtils.getStartDayMillis(userInfoResponseVO.getFirstDepositTime(), reportTopAgentStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getFirstDepositTime(), DateUtils.DATE_FORMAT_1, reportTopAgentStaticsCondVO.getTimeZone()));
                } else {
                    mapKey = mapKey.concat("#")
                            .concat(DateUtils.getStartDayMonthTimestamp(userInfoResponseVO.getFirstDepositTime(), reportTopAgentStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getFirstDepositTime(), DateUtils.DATE_FORMAT_2, reportTopAgentStaticsCondVO.getTimeZone()));
                }
                ReportTopAgentStaticDayPO reportTopAgentStaticDayPO = new ReportTopAgentStaticDayPO();
                reportTopAgentStaticDayPO.setReportType(reportType);
                reportTopAgentStaticDayPO.setSiteCode(userInfoResponseVO.getSiteCode());
                reportTopAgentStaticDayPO.setAgentId(userInfoResponseVO.getSuperAgentId());
                reportTopAgentStaticDayPO.setCurrencyCode(userInfoResponseVO.getMainCurrency());
                long starDayMillis = Long.valueOf(mapKey.split("#")[4]);
                reportTopAgentStaticDayPO.setDayMillis(starDayMillis);
                reportTopAgentStaticDayPO.setReportDate(mapKey.split("#")[5]);
                reportTopAgentStaticDayPO.setRegisterUserNum(0L);
                reportTopAgentStaticDayPO.setFirstDepositNum(1L);
                reportTopAgentStaticDayPO.setFirstDepositRate(BigDecimal.ZERO);
                agentUserDepositStaticMap.put(mapKey, reportTopAgentStaticDayPO);

                agentIds.add(userInfoResponseVO.getSuperAgentId());
            }
        }

        List<AgentInfoVO> agentInfoVOS = agentInfoApi.getByAgentIds(agentIds.stream().toList());
        Map<String, ReportTopAgentStaticDayPO> agentStaticParentMap = new HashMap<>();
        for (String mapKey : agentUserDepositStaticMap.keySet()) {
            String currentAgentId = mapKey.split("#")[1];
            String userId = mapKey.split("#")[2];
            ReportTopAgentStaticDayPO reportTopAgentStaticDayPO = agentUserDepositStaticMap.get(mapKey);
            AgentInfoVO currentAgentInfoVo = agentInfoVOS.stream().filter(o -> o.getAgentId().equals(currentAgentId)).findFirst().get();
            String agentPath = currentAgentInfoVo.getPath();
            if (StringUtils.hasText(agentPath)) {
                for (String parentAgentId : agentPath.split(",")) {
                    ReportTopAgentStaticDayPO reportTopAgentStaticDayPOParent = new ReportTopAgentStaticDayPO();
                    BeanUtils.copyProperties(reportTopAgentStaticDayPO, reportTopAgentStaticDayPOParent);
                    //上级同时累计
                    reportTopAgentStaticDayPOParent.setAgentId(parentAgentId);
                    String actualMapKey = mapKey.replace(currentAgentId, parentAgentId);
                    agentStaticParentMap.put(actualMapKey, reportTopAgentStaticDayPOParent);
                    String uniKey = parentAgentId.concat(reportTopAgentStaticDayPOParent.getCurrencyCode()).concat(userId);
                    log.info("商务总代报表首存人数:{},value:{}", uniKey, reportTopAgentStaticDayPOParent);
                    //保存至数据库
                    saveData(reportTopAgentStaticDayPOParent);
                }
            }
        }
    }

    private void saveTeamNum(ReportTopAgentStaticDayPO reportAgentStaticDay) {
       /* LambdaQueryWrapper<ReportTopAgentStaticDayPO> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getSiteCode,reportAgentStaticDay.getSiteCode());
        lambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getReportType,reportAgentStaticDay.getReportType());
        lambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getReportDate,reportAgentStaticDay.getReportDate());
        lambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getAgentId,reportAgentStaticDay.getAgentId());
        Long countNum=this.baseMapper.selectCount(lambdaQueryWrapper);
        if(countNum>0){*/
        LambdaUpdateWrapper<ReportTopAgentStaticDayPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.setSql("team_agent_num=team_agent_num+1");
        lambdaUpdateWrapper.eq(ReportTopAgentStaticDayPO::getSiteCode, reportAgentStaticDay.getSiteCode());
        lambdaUpdateWrapper.eq(ReportTopAgentStaticDayPO::getReportType, reportAgentStaticDay.getReportType());
        lambdaUpdateWrapper.eq(ReportTopAgentStaticDayPO::getReportDate, reportAgentStaticDay.getReportDate());
        lambdaUpdateWrapper.eq(ReportTopAgentStaticDayPO::getAgentId, reportAgentStaticDay.getAgentId());
        this.update(lambdaUpdateWrapper);
        String uniKey = reportAgentStaticDay.getAgentId()
                .concat("-")
                .concat(reportAgentStaticDay.getReportDate());
        log.info("商务端-总代报表,代理团队人数修改:{},{}", uniKey, reportAgentStaticDay);
        /*}else {
            //填充代理信息
            fillAgentNum(reportAgentStaticDay);
            String uniKey = reportAgentStaticDay.getAgentId()
                    .concat("-")
                    .concat(reportAgentStaticDay.getReportDate());
            log.info("商务端-总代报表,代理团队人数新增:{},{}", uniKey,reportAgentStaticDay);
            this.baseMapper.insert(reportAgentStaticDay);
        }*/
    }


    /**
     * 保存至数据库
     *
     * @param reportAgentStaticDay 原始数据
     */
    private void saveData(ReportTopAgentStaticDayPO reportAgentStaticDay) {
        LambdaQueryWrapper<ReportTopAgentStaticDayPO> reportTopAgentStaticDayPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportTopAgentStaticDayPOLambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getSiteCode, reportAgentStaticDay.getSiteCode());
        reportTopAgentStaticDayPOLambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getReportType, reportAgentStaticDay.getReportType());
        reportTopAgentStaticDayPOLambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getReportDate, reportAgentStaticDay.getReportDate());
        reportTopAgentStaticDayPOLambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getAgentId, reportAgentStaticDay.getAgentId());
        reportTopAgentStaticDayPOLambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getCurrencyCode, reportAgentStaticDay.getCurrencyCode());
        ReportTopAgentStaticDayPO reportTopAgentStaticDayPODb = this.baseMapper.selectOne(reportTopAgentStaticDayPOLambdaQueryWrapper);
        //已存在 则更新
        if (reportTopAgentStaticDayPODb != null) {
            LambdaUpdateWrapper<ReportTopAgentStaticDayPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            //团队代理人数
            if (reportAgentStaticDay.getTeamAgentNum() != null && reportAgentStaticDay.getTeamAgentNum() > 0) {
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getTeamAgentNum, reportTopAgentStaticDayPODb.getTeamAgentNum() + reportAgentStaticDay.getTeamAgentNum());
            }
            //团队会员人数
            if (reportAgentStaticDay.getUserNum() != null) {
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getUserNum, reportTopAgentStaticDayPODb.getUserNum() + reportAgentStaticDay.getUserNum());
            }
            //直属下级人数
            /*if(reportAgentStaticDay.getDirectReportNum()!=null&&reportAgentStaticDay.getDirectReportNum()>0){
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getDirectReportNum,reportAgentStaticDay.getDirectReportNum());
            }*/
            //注册人数
            if (reportAgentStaticDay.getRegisterUserNum() != null && reportAgentStaticDay.getRegisterUserNum() > 0) {
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getRegisterUserNum, reportTopAgentStaticDayPODb.getRegisterUserNum() + reportAgentStaticDay.getRegisterUserNum());
            }
            //首存人数
            if (reportAgentStaticDay.getFirstDepositNum() != null && reportAgentStaticDay.getFirstDepositNum() > 0) {
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getFirstDepositNum, reportTopAgentStaticDayPODb.getFirstDepositNum() + reportAgentStaticDay.getFirstDepositNum());
            }
            //首存转换率 无须展示
            /*if(reportAgentStaticDay.getFirstDepositRate()!=null&&reportAgentStaticDay.getFirstDepositRate().compareTo(BigDecimal.ZERO)!=0){
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getFirstDepositRate,reportAgentStaticDay.getFirstDepositRate());
            }*/
            //注单量
            if (reportAgentStaticDay.getBetUserCount() != null) {
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getBetUserCount, reportTopAgentStaticDayPODb.getBetUserCount() + reportAgentStaticDay.getBetUserCount());
            }

            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getBetAmount, reportTopAgentStaticDayPODb.getBetAmount().add(reportAgentStaticDay.getBetAmount()));
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getValidAmount, reportTopAgentStaticDayPODb.getValidAmount().add(reportAgentStaticDay.getValidAmount()));
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getWinLossAmountUser, reportTopAgentStaticDayPODb.getWinLossAmountUser().add(reportAgentStaticDay.getWinLossAmountUser()));
            BigDecimal tipsAmount = reportAgentStaticDay.getTipsAmount() == null ? BigDecimal.ZERO : reportAgentStaticDay.getTipsAmount();
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getTipsAmount, reportTopAgentStaticDayPODb.getTipsAmount().add(tipsAmount));
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getWinLossAmountPlat, reportTopAgentStaticDayPODb.getWinLossAmountPlat().add(reportAgentStaticDay.getWinLossAmountPlat()));
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getAdjustAmount, reportTopAgentStaticDayPODb.getAdjustAmount().add(reportAgentStaticDay.getAdjustAmount()));
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getVipAmount, reportTopAgentStaticDayPODb.getVipAmount().add(reportAgentStaticDay.getVipAmount()));
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getActivityAmount, reportTopAgentStaticDayPODb.getActivityAmount().add(reportAgentStaticDay.getActivityAmount()));
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getAlreadyUseAmount, reportTopAgentStaticDayPODb.getAlreadyUseAmount().add(reportAgentStaticDay.getAlreadyUseAmount()));
            lambdaUpdateWrapper.set(reportAgentStaticDay.getRebateAmount() != null, ReportTopAgentStaticDayPO::getRebateAmount, reportTopAgentStaticDayPODb.getRebateAmount().add(reportAgentStaticDay.getRebateAmount()));

            BigDecimal betAmount = reportTopAgentStaticDayPODb.getBetAmount().add(reportAgentStaticDay.getBetAmount());
            BigDecimal winLossAmountPlat = reportTopAgentStaticDayPODb.getWinLossAmountPlat().add(reportAgentStaticDay.getWinLossAmountPlat());
            //盈亏比例 等于 平台总输赢 / 投注额
            BigDecimal winLossRate = AmountUtils.divide(winLossAmountPlat, betAmount, 4);
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getWinLossRate, winLossRate);

            //平台收入 等于 平台总输赢 - 调整金额 - 已使用活动优惠 - 代理佣金
            //lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getPlatIncome,reportTopAgentStaticDayPODb.getPlatIncome().add(reportAgentStaticDay.getPlatIncome()));

            //存款
            if (reportAgentStaticDay.getDepositAmount() != null) {
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getDepositAmount, reportTopAgentStaticDayPODb.getDepositAmount().add(reportAgentStaticDay.getDepositAmount()));
            }
            //提款
            if (reportAgentStaticDay.getWithdrawAmount() != null) {
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getWithdrawAmount, reportTopAgentStaticDayPODb.getWithdrawAmount().add(reportAgentStaticDay.getWithdrawAmount()));
            }
            //存提手续费
            if (reportAgentStaticDay.getDepositWithdrawFeeAmount() != null) {
                lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getDepositWithdrawFeeAmount, reportTopAgentStaticDayPODb.getDepositWithdrawFeeAmount().add(reportAgentStaticDay.getDepositWithdrawFeeAmount()));
            }
            lambdaUpdateWrapper.set(ReportTopAgentStaticDayPO::getUpdatedTime, System.currentTimeMillis());
            lambdaUpdateWrapper.eq(ReportTopAgentStaticDayPO::getId, reportTopAgentStaticDayPODb.getId());
            String reportKey = reportAgentStaticDay.getReportDate().concat(reportAgentStaticDay.getAgentId());
            log.info("商务总代报表开始更新:{},统计数据:{}", reportKey, reportAgentStaticDay);
            this.update(lambdaUpdateWrapper);
        } else {
            //填充代理信息
            fillAgentNum(reportAgentStaticDay);
            reportAgentStaticDay.setCreatedTime(System.currentTimeMillis());
            reportAgentStaticDay.setUpdatedTime(System.currentTimeMillis());
            reportAgentStaticDay.setId(null);
            String reportKey = reportAgentStaticDay.getReportDate().concat(reportAgentStaticDay.getAgentId());
            log.info("商务总代报表开始记录:{},统计数据:{}", reportKey, reportAgentStaticDay);
            this.baseMapper.insert(reportAgentStaticDay);
        }

    }

    /**
     * 分页查询
     *
     * @param reportTopAgentStaticsPageVO
     * @return
     */
    public ResponseVO<ReportTopAgentStaticsResult> listPage(ReportTopAgentStaticsPageVO reportTopAgentStaticsPageVO) {
        SiteVO siteDetail = siteApi.getSiteDetail(reportTopAgentStaticsPageVO.getSiteCode());
        String timeZone = siteDetail.getTimezone();
        Integer commissionPlan = siteDetail.getCommissionPlan();
        log.info("siteCode:{},timeZone:{}", reportTopAgentStaticsPageVO.getSiteCode(), timeZone);
        //站点平台币兑换汇率
        Map<String, BigDecimal> rateMap = siteCurrencyInfoApi.getAllFinalRate(reportTopAgentStaticsPageVO.getSiteCode());
        Page<ReportTopAgentStaticDayPO> page = new Page<ReportTopAgentStaticDayPO>(reportTopAgentStaticsPageVO.getPageNumber(), reportTopAgentStaticsPageVO.getPageSize());
        LambdaQueryWrapper<ReportTopAgentStaticDayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getSiteCode, reportTopAgentStaticsPageVO.getSiteCode());
        lambdaQueryWrapper.isNull(ReportTopAgentStaticDayPO::getParentAccount);

        //报表类型 字典类型:report_type  0:日报 1:月报
        lambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getReportType, reportTopAgentStaticsPageVO.getReportType());
        if (reportTopAgentStaticsPageVO.getStartStaticDay() != null) {
            lambdaQueryWrapper.ge(ReportTopAgentStaticDayPO::getDayMillis, reportTopAgentStaticsPageVO.getStartStaticDay());
        }

        if (reportTopAgentStaticsPageVO.getEndStaticDay() != null) {
            lambdaQueryWrapper.le(ReportTopAgentStaticDayPO::getDayMillis, reportTopAgentStaticsPageVO.getEndStaticDay());
        }
        if (StringUtils.hasText(reportTopAgentStaticsPageVO.getAgentAccount())) {
            lambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getAgentAccount, reportTopAgentStaticsPageVO.getAgentAccount());
        }

        if (StringUtils.hasText(reportTopAgentStaticsPageVO.getCurrencyCode())) {
            lambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getCurrencyCode, reportTopAgentStaticsPageVO.getCurrencyCode());
        }

        if (reportTopAgentStaticsPageVO.getMerchantAccount() != null) {
            lambdaQueryWrapper.eq(ReportTopAgentStaticDayPO::getMerchantAccount, reportTopAgentStaticsPageVO.getMerchantAccount());
        }

        if (StringUtils.hasText(reportTopAgentStaticsPageVO.getOrderField()) && StringUtils.hasText(reportTopAgentStaticsPageVO.getOrderType())) {

            if (reportTopAgentStaticsPageVO.getOrderField().equals("directReportNum") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getDirectReportNum);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("directReportNum") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getDirectReportNum);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("firstDepositRate") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getFirstDepositRate);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("firstDepositRate") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getFirstDepositRate);
            }

            if (reportTopAgentStaticsPageVO.getOrderField().equals("winLossRate") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getWinLossRate);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("winLossRate") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getWinLossRate);
            }

            if (reportTopAgentStaticsPageVO.getOrderField().equals("betAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getBetAmount);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("betAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getBetAmount);
            }

            if (reportTopAgentStaticsPageVO.getOrderField().equals("validAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getValidAmount);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("validAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getValidAmount);
            }

            if (reportTopAgentStaticsPageVO.getOrderField().equals("winLossAmountUser") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getWinLossAmountUser);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("winLossAmountUser") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getWinLossAmountUser);
            }

            if (reportTopAgentStaticsPageVO.getOrderField().equals("winLossAmountPlat") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getWinLossAmountPlat);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("winLossAmountPlat") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getWinLossAmountPlat);
            }


            if (reportTopAgentStaticsPageVO.getOrderField().equals("adjustAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getAdjustAmount);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("adjustAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getAdjustAmount);
            }

            if (reportTopAgentStaticsPageVO.getOrderField().equals("activityAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getActivityAmount);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("activityAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getActivityAmount);
            }

            if (reportTopAgentStaticsPageVO.getOrderField().equals("vipAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getVipAmount);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("vipAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getVipAmount);
            }


            if (reportTopAgentStaticsPageVO.getOrderField().equals("alreadyUseAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getAlreadyUseAmount);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("alreadyUseAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getAlreadyUseAmount);
            }

           /* if (reportTopAgentStaticsPageVO.getOrderField().equals("commissionAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getCommissionAmount);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("commissionAmount") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getCommissionAmount);
            }

            if (reportTopAgentStaticsPageVO.getOrderField().equals("platIncome") && reportTopAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportTopAgentStaticDayPO::getPlatIncome);
            }
            if (reportTopAgentStaticsPageVO.getOrderField().equals("platIncome") && reportTopAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getPlatIncome);
            }*/
        } else {
            lambdaQueryWrapper.orderByDesc(ReportTopAgentStaticDayPO::getRegisterTime);
        }
        //团队总计查询条件 begin
        LambdaQueryWrapper<ReportTopAgentStaticDayPO> lambdaTeamQueryWrapper = new LambdaQueryWrapper<>();
        lambdaTeamQueryWrapper.eq(ReportTopAgentStaticDayPO::getSiteCode, reportTopAgentStaticsPageVO.getSiteCode());
        lambdaTeamQueryWrapper.isNull(ReportTopAgentStaticDayPO::getParentAccount);
        //报表类型 字典类型:report_type  0:日报 1:月报
        lambdaTeamQueryWrapper.eq(ReportTopAgentStaticDayPO::getReportType, reportTopAgentStaticsPageVO.getReportType());
        if (reportTopAgentStaticsPageVO.getStartStaticDay() != null) {
            lambdaTeamQueryWrapper.ge(ReportTopAgentStaticDayPO::getDayMillis, reportTopAgentStaticsPageVO.getStartStaticDay());
        }
        if (reportTopAgentStaticsPageVO.getEndStaticDay() != null) {
            lambdaTeamQueryWrapper.le(ReportTopAgentStaticDayPO::getDayMillis, reportTopAgentStaticsPageVO.getEndStaticDay());
        }
        if (StringUtils.hasText(reportTopAgentStaticsPageVO.getAgentAccount())) {
            lambdaTeamQueryWrapper.eq(ReportTopAgentStaticDayPO::getAgentAccount, reportTopAgentStaticsPageVO.getAgentAccount());
        }

        if (reportTopAgentStaticsPageVO.getMerchantAccount() != null) {
            lambdaTeamQueryWrapper.eq(ReportTopAgentStaticDayPO::getMerchantAccount, reportTopAgentStaticsPageVO.getMerchantAccount());
        }

        // lambdaTeamQueryWrapper.eq(ReportTopAgentStaticDayPO::getCurrencyCode,CommonConstant.PLAT_CURRENCY_CODE);
        if (StringUtils.hasText(reportTopAgentStaticsPageVO.getCurrencyCode())) {
            lambdaTeamQueryWrapper.eq(ReportTopAgentStaticDayPO::getCurrencyCode, reportTopAgentStaticsPageVO.getCurrencyCode());
        }


        //团队总计查询条件 end

        //Page<ReportTopAgentStaticDayPO> reportTopAgentStaticDayPOPage=this.baseMapper.selectPage(page,lambdaQueryWrapper);
        Page<ReportTopAgentStaticDayPO> reportTopAgentStaticDayPOPage = this.baseMapper.selectGroupPage(page, lambdaQueryWrapper);
        List<ReportTopAgentStaticsResponseVO> reportTopAgentStaticsResponseVOS = Lists.newArrayList();
        Page<ReportTopAgentStaticsResponseVO> reportTopAgentStaticsResponseVOPage = new Page<ReportTopAgentStaticsResponseVO>();
        BeanUtils.copyProperties(reportTopAgentStaticDayPOPage, reportTopAgentStaticsResponseVOPage);
        ReportTopAgentStaticsResponseVO currentPage = new ReportTopAgentStaticsResponseVO();
        List<String> allAgentIds = Lists.newArrayList();
        List<ReportTopAgentStaticDayPO> reportTopAgentStaticDayPOS = reportTopAgentStaticDayPOPage.getRecords();
        if (!CollectionUtils.isEmpty(reportTopAgentStaticDayPOS)) {

            List<String> agentIds = reportTopAgentStaticDayPOS.stream().filter(o -> StringUtils.hasText(o.getAgentId())).map(ReportTopAgentStaticDayPO::getAgentId).toList();

            List<AgentInfoVO> agentInfoVOList = agentInfoApi.getByAgentIds(agentIds);

            List<String> riskLevelIds = agentInfoVOList.stream().filter(o -> StringUtils.hasText(o.getRiskLevelId())).map(o -> o.getRiskLevelId()).toList();
            Map<String, RiskLevelDetailsVO> riskMap = riskApi.getByIds(riskLevelIds);
            List<String> agentLabelIds = agentInfoVOList.stream().filter(o -> StringUtils.hasText(o.getAgentLabelId())).map(o -> o.getAgentLabelId()).toList();
            List<AgentLabelVO> agentLabelVOS = agentLabelApi.getAgentLabelByAgentLabelIds(agentLabelIds);

            //查询代理团队人数
            // Page<ReportTopAgentStaticDayPO> teamNumGroupPage=this.baseMapper.selectTeamNumGroupPage(page,lambdaTeamQueryWrapper);
            int num = 0;
            for (ReportTopAgentStaticDayPO reportTopAgentStaticDayPO : reportTopAgentStaticDayPOS) {
                //  log.debug("第{}次数据:{}",++num,reportTopAgentStaticDayPO);

                ReportTopAgentStaticsResponseVO reportTopAgentStaticsResponseVO = new ReportTopAgentStaticsResponseVO();
                BeanUtils.copyProperties(reportTopAgentStaticDayPO, reportTopAgentStaticsResponseVO);
                //代理信息填充
                fillAgentInfo(reportTopAgentStaticsResponseVO, agentInfoVOList, riskMap, agentLabelVOS);

                allAgentIds.add(reportTopAgentStaticDayPO.getAgentId());

                reportTopAgentStaticsResponseVO.setRegisterTimeStr(TimeZoneUtils.formatTimestampToTimeZone(reportTopAgentStaticsResponseVO.getRegisterTime(), timeZone));

             /*   BigDecimal winLossRate=reportTopAgentStaticDayPO.getWinLossRate();
                reportTopAgentStaticsResponseVO.setWinLossRateBigDecimal(winLossRate);
                reportTopAgentStaticsResponseVO.setWinLossRate(AmountUtils.formatPercent(winLossRate));

                reportTopAgentStaticsResponseVO.setFirstDepositRate(AmountUtils.formatPercent(reportTopAgentStaticDayPO.getFirstDepositRate()));
                reportTopAgentStaticsResponseVO.setFirstDepositRateBigDecimal(reportTopAgentStaticDayPO.getFirstDepositRate());*/

                reportTopAgentStaticsResponseVO.setPlatCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                String currencyCode = reportTopAgentStaticDayPO.getCurrencyCode();

                //是否转换成平台币
                if (reportTopAgentStaticsPageVO.isTransferPlatformFlag()) {
                    //
                    BigDecimal transferRate = rateMap.get(currencyCode);
                    //存款金额
                    BigDecimal depositAmountWtc1 = AmountUtils.divide(reportTopAgentStaticDayPO.getDepositAmount(), transferRate);
                    reportTopAgentStaticsResponseVO.setDepositAmount(depositAmountWtc1);
                    //提款金额
                    BigDecimal withDrawAmountWtc1 = AmountUtils.divide(reportTopAgentStaticDayPO.getWithdrawAmount(), transferRate);
                    reportTopAgentStaticsResponseVO.setWithdrawAmount(withDrawAmountWtc1);
                    //存提手续费
                    BigDecimal depositWithDrawFeeWtc1 = AmountUtils.divide(reportTopAgentStaticDayPO.getDepositWithdrawFeeAmount(), transferRate);
                    reportTopAgentStaticsResponseVO.setDepositWithdrawFeeAmount(depositWithDrawFeeWtc1);

                    //投注金额
                    BigDecimal betAmountWtc1 = AmountUtils.divide(reportTopAgentStaticDayPO.getBetAmount(), transferRate);
                    reportTopAgentStaticsResponseVO.setBetAmount(betAmountWtc1);
                    //有效投注金额
                    BigDecimal validAmountWtc1 = AmountUtils.divide(reportTopAgentStaticDayPO.getValidAmount(), transferRate);
                    reportTopAgentStaticsResponseVO.setValidAmount(validAmountWtc1);

                    //返水金额
//                    BigDecimal rebateAmountWtc1=AmountUtils.divide(reportTopAgentStaticDayPO.getRebateAmount(),transferRate);
//                    reportTopAgentStaticsResponseVO.setRebateAmount(rebateAmountWtc1);

                    //会员输赢
                    BigDecimal winLossAmountUserWtc1 = AmountUtils.divide(reportTopAgentStaticDayPO.getWinLossAmountUser(), transferRate);
                    reportTopAgentStaticsResponseVO.setWinLossAmountUser(winLossAmountUserWtc1);
                    //平台总输赢
                    BigDecimal winLossAmountPlatWtc1 = AmountUtils.divide(reportTopAgentStaticDayPO.getWinLossAmountPlat(), transferRate);
                    reportTopAgentStaticsResponseVO.setWinLossAmountPlat(winLossAmountPlatWtc1);

                    //VIP福利 无须转换
                    //  reportTopAgentStaticsResponseVO.setVipAmount(reportTopAgentStaticDayPO.getVipAmount());
                    //  totalPage.setVipAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //已使用优惠
                    BigDecimal alreadyUseAmountWtc = AmountUtils.divide(reportTopAgentStaticDayPO.getAlreadyUseAmount(), transferRate);
                    reportTopAgentStaticsResponseVO.setAlreadyUseAmount(alreadyUseAmountWtc);

                    //活动优惠 无须转换
                    // reportTopAgentStaticsResponseVO.setActivityAmount(reportTopAgentStaticDayPO.getActivityAmount());
                    reportTopAgentStaticsResponseVO.setCommissionPlan(commissionPlan);

                }
                //log.debug("第{}次数据Add:{}",num,reportTopAgentStaticsResponseVO);
                reportTopAgentStaticsResponseVOS.add(reportTopAgentStaticsResponseVO);
            }
        }


        reportTopAgentStaticsResponseVOPage.setRecords(reportTopAgentStaticsResponseVOS);

        ReportTopAgentStaticsResult reportTopAgentStaticsResult = new ReportTopAgentStaticsResult();
        reportTopAgentStaticsResult.setPageList(reportTopAgentStaticsResponseVOPage);
        reportTopAgentStaticsResult.setCurrentPage(currentPage);
        // 汇总
        ReportTopAgentStaticsResponseVO totalPage = new ReportTopAgentStaticsResponseVO();
        List<ReportTopAgentStaticDayPO> reportTopAgentStaticDayPOList = this.baseMapper.selectTotal(lambdaQueryWrapper);

        if (!CollectionUtils.isEmpty(reportTopAgentStaticDayPOList)) {
            for (ReportTopAgentStaticDayPO reportTopAgentStaticDayPO : reportTopAgentStaticDayPOList) {
                //团队代理人数
                // totalPage.addTeamAgentNum(reportTopAgentStaticDayPO.getTeamAgentNum());
                totalPage.addUserNum(reportTopAgentStaticDayPO.getUserNum());
                //直属下级人数
                //  totalPage.addDirectReportNum(reportTopAgentStaticDayPO.getDirectReportNum());
                //注册人数
                // totalPage.addRegisterUserNum(reportTopAgentStaticDayPO.getRegisterUserNum());
                //首存人数
                totalPage.addFirstDepositNum(reportTopAgentStaticDayPO.getFirstDepositNum());
                //首存转换率=首存人数 / 注册人数
                // BigDecimal  convertRate= NumberUtil.divide(totalPage.getFirstDepositNum(),totalPage.getRegisterUserNum(),4);
                // totalPage.setFirstDepositRateBigDecimal(convertRate);
                // totalPage.setFirstDepositRate(AmountUtils.formatPercent(convertRate));
                //投注人次
                totalPage.addBetUserCount(reportTopAgentStaticDayPO.getBetUserCount());
                //返水金额
                totalPage.addRebateAmount(reportTopAgentStaticDayPO.getRebateAmount());

                //是否转换成平台币
                if (!reportTopAgentStaticsPageVO.isTransferPlatformFlag()) {
                    //投注金额
                    totalPage.addBetAmount(reportTopAgentStaticDayPO.getBetAmount());
                    //有效投注金额
                    totalPage.addValidAmount(reportTopAgentStaticDayPO.getValidAmount());

                    //会员输赢
                    totalPage.addWinLossAmountUser(reportTopAgentStaticDayPO.getWinLossAmountUser());
                    //平台总输赢
                    totalPage.addWinLossAmountPlat(reportTopAgentStaticDayPO.getWinLossAmountPlat());
                    //调整金额
                    totalPage.addAdjustAmount(reportTopAgentStaticDayPO.getAdjustAmount());
                    //盈亏比例 等于 平台总输赢 / 投注额
                    BigDecimal winLossRate = AmountUtils.divide(totalPage.getWinLossAmountPlat(), totalPage.getBetAmount(), 4);
                    totalPage.setWinLossRateBigDecimal(winLossRate);
                    totalPage.setWinLossRate(AmountUtils.formatPercent(winLossRate));
                    //活动优惠
                    totalPage.addActivityAmount(reportTopAgentStaticDayPO.getActivityAmount());
                    //VIP福利
                    totalPage.addVipAmount(reportTopAgentStaticDayPO.getVipAmount());
                    //已使用优惠
                    totalPage.addAlreadyUseAmount(reportTopAgentStaticDayPO.getAlreadyUseAmount());

                    //存款金额
                    totalPage.addDepositAmount(reportTopAgentStaticDayPO.getDepositAmount());

                    //存取手续费
                    totalPage.addWithdrawAmount(reportTopAgentStaticDayPO.getWithdrawAmount());

                    //存取手续费
                    totalPage.addDepositWithdrawFeeAmount(reportTopAgentStaticDayPO.getDepositWithdrawFeeAmount());
                } else {
                    String currencyCode = reportTopAgentStaticDayPO.getCurrencyCode();
                    BigDecimal transferRate = rateMap.get(currencyCode);
                    //存款金额
                    BigDecimal depositAmountWtc = AmountUtils.divide(reportTopAgentStaticDayPO.getDepositAmount(), transferRate);
                    totalPage.addDepositAmount(depositAmountWtc);
                    //提款金额
                    BigDecimal withDrawAmountWtc = AmountUtils.divide(reportTopAgentStaticDayPO.getWithdrawAmount(), transferRate);
                    totalPage.addWithdrawAmount(withDrawAmountWtc);
                    //存提手续费
                    BigDecimal depositWithDrawFeeWtc = AmountUtils.divide(reportTopAgentStaticDayPO.getDepositWithdrawFeeAmount(), transferRate);
                    totalPage.addDepositWithdrawFeeAmount(depositWithDrawFeeWtc);
                    //投注金额
                    BigDecimal betAmountWtc = AmountUtils.divide(reportTopAgentStaticDayPO.getBetAmount(), transferRate);
                    totalPage.addBetAmount(betAmountWtc);
                    //有效投注金额
                    BigDecimal validAmountWtc = AmountUtils.divide(reportTopAgentStaticDayPO.getValidAmount(), transferRate);
                    totalPage.addValidAmount(validAmountWtc);

                    //返水金额
                    //BigDecimal rebateAmountWtc=AmountUtils.divide(reportTopAgentStaticDayPO.getRebateAmount(),transferRate);
                    //totalPage.addRebateAmount(rebateAmountWtc);

                    //会员输赢
                    BigDecimal winLossAmountUserWtc = AmountUtils.divide(reportTopAgentStaticDayPO.getWinLossAmountUser(), transferRate);
                    totalPage.addWinLossAmountUser(winLossAmountUserWtc);
                    //平台总输赢
                    BigDecimal winLossAmountPlatWtc = AmountUtils.divide(reportTopAgentStaticDayPO.getWinLossAmountPlat(), transferRate);
                    totalPage.addWinLossAmountPlat(winLossAmountPlatWtc);
                    //活动优惠
                    totalPage.addActivityAmount(reportTopAgentStaticDayPO.getActivityAmount());
                    //VIP福利
                    totalPage.addVipAmount(reportTopAgentStaticDayPO.getVipAmount());
                    //  totalPage.setVipAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //已使用优惠
                    BigDecimal alreadyUseAmountWtc = AmountUtils.divide(reportTopAgentStaticDayPO.getAlreadyUseAmount(), transferRate);
                    totalPage.addAlreadyUseAmount(alreadyUseAmountWtc);

                }
            }

            //团队代理人数统计 不需要过滤币种
            ReportTopAgentStaticDayPO teamNumTotalPO = this.baseMapper.selectTeamNumTotal(lambdaTeamQueryWrapper);
            if (teamNumTotalPO != null) {
                totalPage.setTeamAgentNum(teamNumTotalPO.getTeamAgentNum());

            }
        }
        reportTopAgentStaticsResult.setTotalPage(totalPage);
        return ResponseVO.success(reportTopAgentStaticsResult);
    }


    /**
     * 代理信息填充
     *
     * @param reportTopAgentStaticsResponseVO 返回信息
     * @param agentInfoVOList                 代理信息
     */
    private void fillAgentInfo(ReportTopAgentStaticsResponseVO reportTopAgentStaticsResponseVO, List<AgentInfoVO> agentInfoVOList, Map<String, RiskLevelDetailsVO> riskMap,
                               List<AgentLabelVO> agentLabelVOS) {
        Optional<AgentInfoVO> agentInfoVOOptional = agentInfoVOList.stream().filter(o -> o.getAgentId().equals(reportTopAgentStaticsResponseVO.getAgentId())).findFirst();
        if (agentInfoVOOptional.isEmpty()) {
            return;
        }
        AgentInfoVO agentInfoVO = agentInfoVOOptional.get();
        //上级代理
        reportTopAgentStaticsResponseVO.setParentId(agentInfoVO.getParentId());
        reportTopAgentStaticsResponseVO.setParentAccount(agentInfoVO.getParentAccount());
        //代理信息填充
        /*reportTopAgentStaticsResponseVO.setPath(agentInfoVO.getPath());
        reportTopAgentStaticsResponseVO.setLevel(agentInfoVO.getLevel());
        // 层级名称
        if (null != agentInfoVO.getLevel()) {
            AgentLevelEnum agentLevelEnum = AgentLevelEnum.nameOfCode(agentInfoVO.getLevel());
            if (null != agentLevelEnum) {
                reportTopAgentStaticsResponseVO.setLevelName(agentLevelEnum.getName());
            }
        }*/
        reportTopAgentStaticsResponseVO.setAgentType(agentInfoVO.getAgentType());
        reportTopAgentStaticsResponseVO.setAgentCategory(agentInfoVO.getAgentCategory());
        // reportTopAgentStaticsResponseVO.setRegisterTime(agentInfoVO.getRegisterTime());
        reportTopAgentStaticsResponseVO.setAgentAttribution(agentInfoVO.getAgentAttribution());
        reportTopAgentStaticsResponseVO.setAgentLabelId(agentInfoVO.getAgentLabelId());
        reportTopAgentStaticsResponseVO.setRiskLevelId(agentInfoVO.getRiskLevelId());

        // 风控层级
        if (null != agentInfoVO.getRiskLevelId()) {
            RiskLevelDetailsVO riskLevelDetailsVO = riskMap.get(agentInfoVO.getRiskLevelId());
            reportTopAgentStaticsResponseVO.setRiskLevel(null == riskLevelDetailsVO ? null : riskLevelDetailsVO.getRiskControlLevel());
        }

        // 代理标签
        if (null != agentInfoVO.getAgentLabelId()) {
            List<AgentLabelVO> agentLabelPOList = agentLabelVOS.stream().filter(o -> agentInfoVO.getAgentLabelId().contains(o.getId())).toList();
            if (!CollectionUtils.isEmpty(agentLabelPOList)) {
                List<String> agentLabelNames = agentLabelPOList.stream().map(o -> o.getName()).toList();
                String labelNames = String.join(CommonConstant.COMMA, agentLabelNames);
                reportTopAgentStaticsResponseVO.setAgentLabel(labelNames);
            }
        }
        //团队人数
       /* if(!CollectionUtils.isEmpty(teamNumList)){
            Optional<ReportTopAgentStaticDayPO> agentStaticDayPOOptional= teamNumList.stream().filter(o->o.getAgentId().equals(agentInfoVO.getAgentId())).findFirst();
            agentStaticDayPOOptional.ifPresent(reportTopAgentStaticDayPO -> reportTopAgentStaticsResponseVO.setTeamAgentNum(reportTopAgentStaticDayPO.getTeamAgentNum()));

        }*/

    }
}
