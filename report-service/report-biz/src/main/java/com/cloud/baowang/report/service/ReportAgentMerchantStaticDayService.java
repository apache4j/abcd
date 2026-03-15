package com.cloud.baowang.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.*;
import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositSubordinatesPageReqVo;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.MerchantAgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithDrawReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawRespVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResponseVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.NumberUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.report.api.api.UserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsResponseVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentMerchantStaticsResult;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListPageCondVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListResponseVO;
import com.cloud.baowang.report.po.ReportAgentMerchantStaticDayPO;
import com.cloud.baowang.report.repositories.ReportAgentMerchantStaticDayRepository;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.user.api.vo.user.UserInfoPageVO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Desciption: 商务报表service
 * @Author: Ford
 * @Date: 2024/11/5 11:23
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class ReportAgentMerchantStaticDayService extends ServiceImpl<ReportAgentMerchantStaticDayRepository, ReportAgentMerchantStaticDayPO> {

    private final AgentInfoApi agentInfoApi;

    private final AgentMerchantApi agentMerchantApi;

    private final UserWinLoseApi userWinLoseApi;

    private final UserInfoApi userInfoApi;

    private final  ReportAgentStaticBetService reportAgentStaticBetService;

    private final SiteApi siteApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final RiskApi riskApi;

    private final AgentLabelApi agentLabelApi;

    private final UserDepositWithdrawApi userDepositWithdrawApi;

    private final AgentDepositWithdrawApi agentDepositWithdrawApi;

    private final AgentDepositSubordinatesApi agentDepositSubordinatesApi;

    private final UserManualUpDownApi userManualUpDownApi;

    private final AgentManualUpDownApi agentManualUpDownApi;



    /**
     * 按照站点、天 统计商务报表
     * @param reportAgentMerchantStaticsCondVO 统计条件
     * @return 统计成功 or 失败
     */
    public ResponseVO<Boolean> init(ReportAgentMerchantStaticsCondVO reportAgentMerchantStaticsCondVO) {
        String siteCode=reportAgentMerchantStaticsCondVO.getSiteCode();
        String reportType=reportAgentMerchantStaticsCondVO.getReportType();
        String timeZone=reportAgentMerchantStaticsCondVO.getTimeZone();
        if(!StringUtils.hasText(siteCode)){
            log.info("站点为空:{}",reportAgentMerchantStaticsCondVO);
            return ResponseVO.success(Boolean.TRUE);
        }
        if(!StringUtils.hasText(reportAgentMerchantStaticsCondVO.getTimeZone())){
            log.info("站点时区为空:{}",reportAgentMerchantStaticsCondVO);
            return ResponseVO.success(Boolean.TRUE);
        }

        UserWinLoseListPageCondVO vo =new UserWinLoseListPageCondVO();
        vo.setSiteCode(siteCode);
        vo.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        vo.setStartDayMillis(reportAgentMerchantStaticsCondVO.getStartDayMillis());
        vo.setEndDayMillis(reportAgentMerchantStaticsCondVO.getEndDayMillis());
        vo.setPageNumber(1);
        vo.setPageSize(500);
        ResponseVO<Page<UserWinLoseListResponseVO>> userWinLosePageResp=userWinLoseApi.listPage(vo);
        if(!userWinLosePageResp.isOk()){
            log.info("根据条件:{}没有查询到会员盈亏数据",vo);
            return ResponseVO.success(Boolean.TRUE);
        }

        Map<String,BigDecimal> rateMap=siteCurrencyInfoApi.getAllFinalRate(siteCode);
        //删除当前站点 历史统计数据 重新计算
        LambdaQueryWrapper<ReportAgentMerchantStaticDayPO> reportAgentMerchantStaticDayPOLambdaQueryWrapper=new LambdaQueryWrapper<>();
        reportAgentMerchantStaticDayPOLambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getSiteCode,siteCode);
        reportAgentMerchantStaticDayPOLambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getReportType,reportAgentMerchantStaticsCondVO.getReportType());
        if(vo.getStartDayMillis()!=null){
            reportAgentMerchantStaticDayPOLambdaQueryWrapper.ge(ReportAgentMerchantStaticDayPO::getDayMillis,vo.getStartDayMillis());
        }
        if(vo.getEndDayMillis()!=null){
            reportAgentMerchantStaticDayPOLambdaQueryWrapper.le(ReportAgentMerchantStaticDayPO::getDayMillis,vo.getEndDayMillis());
        }
        this.baseMapper.delete(reportAgentMerchantStaticDayPOLambdaQueryWrapper);

        //统计注册人数 首存人数
         fillRegisterNum(reportAgentMerchantStaticsCondVO);

        //统计代理所属团队 会员盈亏
        Page<UserWinLoseListResponseVO> userWinLoseResponseVOPage=userWinLosePageResp.getData();
        long totalPageNum=userWinLoseResponseVOPage.getPages();
        log.info("站点:{},总页数:{},参数:{}",siteCode,totalPageNum,vo);
        for(int pageNum=1;pageNum<=totalPageNum;pageNum++){
            vo.setPageNumber(pageNum);
            log.info("站点:{},开始处理第:{}页会员盈亏数据",siteCode,pageNum);
            ResponseVO<Page<UserWinLoseListResponseVO>> userWinLosePageRespForEach=userWinLoseApi.listPage(vo);
            //更新金额 新增or更新
            batchProcess(userWinLosePageRespForEach.getData().getRecords(),reportAgentMerchantStaticsCondVO.getReportType(),timeZone,rateMap);
            log.info("站点:{},处理结束第:{}页会员盈亏数据",siteCode,pageNum);
        }
        //统计商务 充值提现金额、充提手续费 user_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
        initUserDepositWithdrawFee(reportAgentMerchantStaticsCondVO,rateMap);

        //统计会员人工加减额 user_manual_up_down_record
        initUserManualUpDown(reportAgentMerchantStaticsCondVO,rateMap);

        //统计代理代存 金额 agent_deposit_subordinates 里 deposit_time ; amount
        initAgentDepositSubordinate(reportAgentMerchantStaticsCondVO,rateMap);

        //统计商务 充值提现金额、代理手续费 agent_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
        initAgentDepositWithdraw(reportAgentMerchantStaticsCondVO,rateMap);

        // 初始化代理 人工加减额 agent_manual_up_down_record
        initAgentManualUpDown(reportAgentMerchantStaticsCondVO);

        return ResponseVO.success(Boolean.TRUE);
    }

    /**
     * 代理人工加减额
     * 对于人工加减额来说   代理存款(后台)-额度钱包\代理提款(后台)-佣金钱包   才统计到存款总额、提款总额里
     * @param reportAgentMerchantStaticsCondVO 代理人工加减额
     */
    private void initAgentManualUpDown(ReportAgentMerchantStaticsCondVO reportAgentMerchantStaticsCondVO) {
        String siteCode=reportAgentMerchantStaticsCondVO.getSiteCode();
        String timeZone=reportAgentMerchantStaticsCondVO.getTimeZone();
        String reportType=reportAgentMerchantStaticsCondVO.getReportType();
        AgentManualDownRequestVO vo=new AgentManualDownRequestVO();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setCreatorStartTime(reportAgentMerchantStaticsCondVO.getStartDayMillis());
        vo.setCreatorEndTime(reportAgentMerchantStaticsCondVO.getEndDayMillis());
        vo.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
        Page<AgentManualUpRecordResponseVO>  agentManualUpRecordResponseVOPage = agentManualUpDownApi.listPage(vo);
        for(int pageNum=1;pageNum<=agentManualUpRecordResponseVOPage.getPages();pageNum++){
            vo.setPageNumber(pageNum);
            agentManualUpRecordResponseVOPage = agentManualUpDownApi.listPage(vo);
            List<AgentManualUpRecordResponseVO>  agentManualUpRecordResponseVOS = agentManualUpRecordResponseVOPage.getRecords();

            List<String> agentIds=agentManualUpRecordResponseVOS.stream().filter(o->StringUtils.hasText(o.getAgentId())).map(AgentManualUpRecordResponseVO::getAgentId).toList();
            if(CollectionUtils.isEmpty(agentIds)){
                continue;
            }
            List<AgentInfoVO> agentInfoVOS= agentInfoApi.getByAgentIds(agentIds);

            for(AgentManualUpRecordResponseVO agentManualUpRecordResponseVO:agentManualUpRecordResponseVOS){
                Optional<AgentInfoVO> agentInfoVOOptional=agentInfoVOS.stream().filter(o->o.getAgentId().equals(agentManualUpRecordResponseVO.getAgentId())).findFirst();
                if(agentInfoVOOptional.isEmpty()){
                    log.info("商务报表代理人工加减额 代理信息不存在:{}",agentManualUpRecordResponseVO.getAgentId());
                    continue;
                }
                AgentInfoVO currentAgentInfo=agentInfoVOOptional.get();
                if(!StringUtils.hasText(currentAgentInfo.getMerchantAccount())){
                    log.info("商务报表代理人工加减额 商务信息不存在:{}",agentManualUpRecordResponseVO.getAgentId());
                    continue;
                }
                ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO=new ReportAgentMerchantStaticDayPO();
                reportAgentMerchantStaticDayPO.setReportType(reportType);
                reportAgentMerchantStaticDayPO.setSiteCode(siteCode);
                reportAgentMerchantStaticDayPO.setMerchantAccount(currentAgentInfo.getMerchantAccount());
                reportAgentMerchantStaticDayPO.setMerchantName(currentAgentInfo.getMerchantName());
                reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                String  reportDay=DateUtils.formatDateByZoneId(agentManualUpRecordResponseVO.getUpdatedTime(),DateUtils.DATE_FORMAT_1,timeZone);
                String  reportMonth=DateUtils.formatDateByZoneId(agentManualUpRecordResponseVO.getUpdatedTime(),DateUtils.DATE_FORMAT_2,timeZone);
                // 按天统计
                if("0".equals(reportType)){
                    Long dayStartTime=DateUtils.getStartDayMillis(agentManualUpRecordResponseVO.getUpdatedTime(),timeZone);
                    reportAgentMerchantStaticDayPO.setDayMillis(dayStartTime);
                    reportAgentMerchantStaticDayPO.setReportDate(reportDay);
                }else {
                    //按月统计
                    Long monthStartTime=DateUtils.getStartDayMonthTimestamp(agentManualUpRecordResponseVO.getUpdatedTime(),timeZone);
                    reportAgentMerchantStaticDayPO.setDayMillis(monthStartTime);
                    reportAgentMerchantStaticDayPO.setReportDate(reportMonth);
                }
                reportAgentMerchantStaticDayPO.setDepositAmount(BigDecimal.ZERO);
                reportAgentMerchantStaticDayPO.setWithdrawAmount(BigDecimal.ZERO);
                boolean dataExists=false;
                if(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode().equals(agentManualUpRecordResponseVO.getAdjustWay())){
                    if(AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode().equals(agentManualUpRecordResponseVO.getAdjustType().toString())){
                        reportAgentMerchantStaticDayPO.setDepositAmount(agentManualUpRecordResponseVO.getAdjustAmount());
                        dataExists=true;
                    }
                }
                if(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode().equals(agentManualUpRecordResponseVO.getAdjustWay())){
                    if(AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getCode().equals(agentManualUpRecordResponseVO.getAdjustType().toString())){
                        reportAgentMerchantStaticDayPO.setWithdrawAmount(agentManualUpRecordResponseVO.getAdjustAmount());
                        dataExists=true;
                    }
                }
                //更细至统计报表
                if(dataExists){
                    String reportKey=reportAgentMerchantStaticDayPO.getReportDate().concat(reportAgentMerchantStaticDayPO.getMerchantAccount());
                    log.info("商务报表代理人工加减额数据:{},开始记录会员人工加减金额:{}",reportKey,reportAgentMerchantStaticDayPO);
                    saveData(reportAgentMerchantStaticDayPO);
                }
            }
        }
    }

    /**
     * 会员人工加减额
     * @param reportAgentMerchantStaticsCondVO 参数
     * @param rateMap 汇率
     */
    private void initUserManualUpDown(ReportAgentMerchantStaticsCondVO reportAgentMerchantStaticsCondVO,Map<String,BigDecimal> rateMap) {
        String siteCode=reportAgentMerchantStaticsCondVO.getSiteCode();
        String reportType=reportAgentMerchantStaticsCondVO.getReportType();
        String timeZone=reportAgentMerchantStaticsCondVO.getTimeZone();

        UserManualDownRecordRequestVO vo=new UserManualDownRecordRequestVO();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setUpdateStartTime(reportAgentMerchantStaticsCondVO.getStartDayMillis());
        vo.setUpdateEndTime(reportAgentMerchantStaticsCondVO.getEndDayMillis());
        vo.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
        Page<UserManualDownRecordVO>  userManualDownRecordVOPage = userManualUpDownApi.listPage(vo);
        long totalPages=userManualDownRecordVOPage.getPages();
        for(int pageNum=1;pageNum<=totalPages;pageNum++){
            vo.setPageNumber(pageNum);
            userManualDownRecordVOPage = userManualUpDownApi.listPage(vo);
            List<String> agentIds=userManualDownRecordVOPage.getRecords().stream().filter(o->StringUtils.hasText(o.getAgentId())).map(UserManualDownRecordVO::getAgentId).toList();
            if(CollectionUtils.isEmpty(agentIds)){
                continue;
            }
            List<AgentInfoVO> agentInfoVOS= agentInfoApi.getByAgentIds(agentIds);
            List<UserManualDownRecordVO>  userManualDownRecordVOS = userManualDownRecordVOPage.getRecords();
            for(UserManualDownRecordVO userManualDownRecordVO:userManualDownRecordVOS){

                Optional<AgentInfoVO> agentInfoVOOptional=agentInfoVOS.stream().filter(o->o.getAgentId().equals(userManualDownRecordVO.getAgentId())).findFirst();
                if(agentInfoVOOptional.isEmpty()){
                    log.info("商务报表 会员人工加减额 代理信息不存在:{}",userManualDownRecordVO.getAgentId());
                    continue;
                }

                ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO=new ReportAgentMerchantStaticDayPO();
                reportAgentMerchantStaticDayPO.setReportType(reportType);
                String  reportDay=DateUtils.formatDateByZoneId(userManualDownRecordVO.getUpdatedTime(),DateUtils.DATE_FORMAT_1,timeZone);
                String  reportMonth=DateUtils.formatDateByZoneId(userManualDownRecordVO.getUpdatedTime(),DateUtils.DATE_FORMAT_2,timeZone);
                // 按天统计
                if("0".equals(reportType)){
                    Long dayStartTime=DateUtils.getStartDayMillis(userManualDownRecordVO.getUpdatedTime(),timeZone);
                    reportAgentMerchantStaticDayPO.setDayMillis(dayStartTime);
                    reportAgentMerchantStaticDayPO.setReportDate(reportDay);
                }else {
                    //按月统计
                    Long monthStartTime=DateUtils.getStartDayMonthTimestamp(userManualDownRecordVO.getUpdatedTime(),timeZone);
                    reportAgentMerchantStaticDayPO.setDayMillis(monthStartTime);
                    reportAgentMerchantStaticDayPO.setReportDate(reportMonth);
                }
                AgentInfoVO currentAgentInfo=agentInfoVOOptional.get();
                reportAgentMerchantStaticDayPO.setSiteCode(siteCode);
                reportAgentMerchantStaticDayPO.setMerchantAccount(currentAgentInfo.getMerchantAccount());
                reportAgentMerchantStaticDayPO.setMerchantName(currentAgentInfo.getMerchantName());
               // reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                reportAgentMerchantStaticDayPO.setCurrencyCode(userManualDownRecordVO.getCurrencyCode());
                boolean dataExists=false;
                if(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode().equals(userManualDownRecordVO.getAdjustWay())){
                    if(Objects.equals(ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode(), userManualDownRecordVO.getAdjustType())){
                        // BigDecimal convertRate=rateMap.get(userManualDownRecordVO.getCurrencyCode());
                       //  BigDecimal depositAmountWtc=AmountUtils.divide(userManualDownRecordVO.getAdjustAmount(),convertRate);
                         reportAgentMerchantStaticDayPO.setDepositAmount(userManualDownRecordVO.getAdjustAmount());
                         dataExists=true;
                    }
                }
                if(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode().equals(userManualDownRecordVO.getAdjustWay())){
                    if(Objects.equals(ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode(), userManualDownRecordVO.getAdjustType())){
                       // BigDecimal convertRate=rateMap.get(userManualDownRecordVO.getCurrencyCode());
                      //  BigDecimal withdrawAmountWtc=AmountUtils.divide(userManualDownRecordVO.getAdjustAmount(),convertRate);
                        reportAgentMerchantStaticDayPO.setWithdrawAmount(userManualDownRecordVO.getAdjustAmount());
                        dataExists=true;
                    }
                }
                //更新至统计报表
                if(dataExists){
                    String reportKey=reportAgentMerchantStaticDayPO.getReportDate().concat(reportAgentMerchantStaticDayPO.getMerchantAccount());
                    log.info("人工加减额日期-商务:{},开始记录会员人工加减金额:{}",reportKey,reportAgentMerchantStaticDayPO);
                    saveData(reportAgentMerchantStaticDayPO);
                }
            }
        }
    }


    //统计代理代存 金额 agent_deposit_subordinates 里 deposit_time ; amount
    private void initAgentDepositSubordinate(ReportAgentMerchantStaticsCondVO reportAgentMerchantStaticsCondVO,Map<String,BigDecimal> rateMap) {
        String siteCode=reportAgentMerchantStaticsCondVO.getSiteCode();
        String reportType=reportAgentMerchantStaticsCondVO.getReportType();
        String timeZone=reportAgentMerchantStaticsCondVO.getTimeZone();

        AgentDepositSubordinatesPageReqVo vo=new AgentDepositSubordinatesPageReqVo();
        vo.setSiteCode(siteCode);
        vo.setStartTime(reportAgentMerchantStaticsCondVO.getStartDayMillis());
        vo.setEndTime(reportAgentMerchantStaticsCondVO.getEndDayMillis());
        vo.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        vo.setPageSize(500);
        vo.setPageNumber(1);
        Page<AgentDepositOfSubordinatesResVO> depositOfSubordinatesResVOPage=agentDepositSubordinatesApi.listPage(vo);
        if(!CollectionUtils.isEmpty(depositOfSubordinatesResVOPage.getRecords())){
            long totalPages=depositOfSubordinatesResVOPage.getPages();
            log.info("代理代存 参数:{},总页数:{}",reportAgentMerchantStaticsCondVO,totalPages);

            for(int pageNum=1;pageNum<=totalPages;pageNum++){
                vo.setPageNumber(pageNum);
                Page<AgentDepositOfSubordinatesResVO> resultPage=agentDepositSubordinatesApi.listPage(vo);


                List<String> agentIds=resultPage.getRecords().stream().filter(o->StringUtils.hasText(o.getAgentId())).map(AgentDepositOfSubordinatesResVO::getAgentId).toList();
                if(CollectionUtils.isEmpty(agentIds)){
                    continue;
                }
                List<AgentInfoVO> agentInfoVOS= agentInfoApi.getByAgentIds(agentIds);

                for(AgentDepositOfSubordinatesResVO agentDepositOfSubordinatesResVO:resultPage.getRecords()){
                    Optional<AgentInfoVO> agentInfoVOOptional=agentInfoVOS.stream().filter(o->o.getAgentId().equals(agentDepositOfSubordinatesResVO.getAgentId())).findFirst();
                    if(agentInfoVOOptional.isEmpty()){
                        log.info("商务报表 代理代存 代理信息不存在:{}",agentDepositOfSubordinatesResVO.getAgentId());
                        continue;
                    }

                    ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO=new ReportAgentMerchantStaticDayPO();
                    reportAgentMerchantStaticDayPO.setReportType(reportType);
                    String  reportDay=DateUtils.formatDateByZoneId(agentDepositOfSubordinatesResVO.getDepositTime(),DateUtils.DATE_FORMAT_1,timeZone);
                    String  reportMonth=DateUtils.formatDateByZoneId(agentDepositOfSubordinatesResVO.getDepositTime(),DateUtils.DATE_FORMAT_2,timeZone);
                    // 按天统计
                    if("0".equals(reportType)){
                        Long dayStartTime=DateUtils.getStartDayMillis(agentDepositOfSubordinatesResVO.getDepositTime(),timeZone);
                        reportAgentMerchantStaticDayPO.setDayMillis(dayStartTime);
                        reportAgentMerchantStaticDayPO.setReportDate(reportDay);
                    }else {
                        //按月统计
                        Long monthStartTime=DateUtils.getStartDayMonthTimestamp(agentDepositOfSubordinatesResVO.getDepositTime(),timeZone);
                        reportAgentMerchantStaticDayPO.setDayMillis(monthStartTime);
                        reportAgentMerchantStaticDayPO.setReportDate(reportMonth);
                    }
                    AgentInfoVO currentAgentInfo=agentInfoVOOptional.get();
                    reportAgentMerchantStaticDayPO.setSiteCode(siteCode);
                    reportAgentMerchantStaticDayPO.setMerchantAccount(currentAgentInfo.getMerchantAccount());
                    reportAgentMerchantStaticDayPO.setMerchantName(currentAgentInfo.getMerchantName());
                    reportAgentMerchantStaticDayPO.setCurrencyCode(agentDepositOfSubordinatesResVO.getCurrencyCode());
                    //代理代存 同时操作  会员存款增加  代理提款增加
                    reportAgentMerchantStaticDayPO.setDepositAmount(agentDepositOfSubordinatesResVO.getAmount());
                    reportAgentMerchantStaticDayPO.setWithdrawAmount(agentDepositOfSubordinatesResVO.getAmount());
                  //  reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                   // BigDecimal convertRate=rateMap.get(agentDepositOfSubordinatesResVO.get());
                  //  BigDecimal depositAmountWtc=AmountUtils.divide(agentDepositOfSubordinatesResVO.getAmount(),convertRate);
                   // reportAgentMerchantStaticDayPO.setDepositAmount(agentDepositOfSubordinatesResVO.getPlatformAmount());
                    String uniKey=reportAgentMerchantStaticDayPO.getSiteCode().concat(reportAgentMerchantStaticDayPO.getReportDate()).concat(reportAgentMerchantStaticDayPO.getMerchantAccount());
                    log.info("开始记录代理代存金额:{},{}",uniKey,reportAgentMerchantStaticDayPO);
                    saveData(reportAgentMerchantStaticDayPO);
                }
            }
        }
    }


    //统计代理手续费 agent_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
    private void initAgentDepositWithdraw(ReportAgentMerchantStaticsCondVO reportAgentMerchantStaticsCondVO,Map<String,BigDecimal> rateMap) {
        String siteCode=reportAgentMerchantStaticsCondVO.getSiteCode();
        String reportType=reportAgentMerchantStaticsCondVO.getReportType();
        String timeZone=reportAgentMerchantStaticsCondVO.getTimeZone();
        AgentDepositWithDrawReqVO vo=new AgentDepositWithDrawReqVO();
        vo.setSiteCode(reportAgentMerchantStaticsCondVO.getSiteCode());
        vo.setStartTime(reportAgentMerchantStaticsCondVO.getStartDayMillis());
        vo.setEndTime(reportAgentMerchantStaticsCondVO.getEndDayMillis());
        vo.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        vo.setPageNumber(1);
        vo.setPageSize(500);
        Page<AgentDepositWithdrawRespVO>  agentDepositWithdrawRespVOPage=agentDepositWithdrawApi.listPage(vo);
        long totalPageNum = agentDepositWithdrawRespVOPage.getPages();
        log.info("代理充提手续费计算 参数:{},总页数:{}",reportAgentMerchantStaticsCondVO,totalPageNum);
        for(int pageNum=1;pageNum<=totalPageNum;pageNum++) {
            vo.setPageNumber(pageNum);
            log.info("站点:{},开始处理第:{}页 代理充提手续费计算",siteCode,pageNum);
            Page<AgentDepositWithdrawRespVO> agentDepositWithdrawRespVOPageForEach=agentDepositWithdrawApi.listPage(vo);

            List<String> agentIds=agentDepositWithdrawRespVOPageForEach.getRecords().stream().filter(o->StringUtils.hasText(o.getAgentId())).map(AgentDepositWithdrawRespVO::getAgentId).toList();
            if(CollectionUtils.isEmpty(agentIds)){
                continue;
            }
            List<AgentInfoVO> agentInfoVOS= agentInfoApi.getByAgentIds(agentIds);
            for(AgentDepositWithdrawRespVO agentDepositWithdrawRespVO:agentDepositWithdrawRespVOPageForEach.getRecords()){
                Optional<AgentInfoVO> agentInfoVOOptional=agentInfoVOS.stream().filter(o->o.getAgentId().equals(agentDepositWithdrawRespVO.getAgentId())).findFirst();
                if(agentInfoVOOptional.isEmpty()){
                    log.info("商务报表 代理充提手续费计算 代理信息不存在:{}",agentDepositWithdrawRespVO.getAgentId());
                    continue;
                }
                AgentInfoVO currentAgentInfo=agentInfoVOOptional.get();
                // 即计算某个代理所属团队的所有用户数据
                String  reportDay=DateUtils.formatDateByZoneId(agentDepositWithdrawRespVO.getUpdatedTime(),DateUtils.DATE_FORMAT_1,timeZone);
                String  reportMonth=DateUtils.formatDateByZoneId(agentDepositWithdrawRespVO.getUpdatedTime(),DateUtils.DATE_FORMAT_2,timeZone);
                ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO=new ReportAgentMerchantStaticDayPO();
                reportAgentMerchantStaticDayPO.setReportType(reportType);
                // 按天统计
                if("0".equals(reportType)){
                    Long dayStartTime=DateUtils.getStartDayMillis(agentDepositWithdrawRespVO.getUpdatedTime(),timeZone);
                    reportAgentMerchantStaticDayPO.setDayMillis(dayStartTime);
                    reportAgentMerchantStaticDayPO.setReportDate(reportDay);
                }else {
                    //按月统计
                    Long monthStartTime=DateUtils.getStartDayMonthTimestamp(agentDepositWithdrawRespVO.getUpdatedTime(),timeZone);
                    reportAgentMerchantStaticDayPO.setDayMillis(monthStartTime);
                    reportAgentMerchantStaticDayPO.setReportDate(reportMonth);
                }
                reportAgentMerchantStaticDayPO.setSiteCode(siteCode);
                reportAgentMerchantStaticDayPO.setMerchantAccount(currentAgentInfo.getMerchantAccount());
                reportAgentMerchantStaticDayPO.setMerchantName(currentAgentInfo.getMerchantName());
                BigDecimal convertRate=rateMap.get(agentDepositWithdrawRespVO.getCurrencyCode());
                reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                BigDecimal settlementFeeWtc=AmountUtils.divide(agentDepositWithdrawRespVO.getSettlementFeeAmount(),agentDepositWithdrawRespVO.getPlatformCurrencyExchangeRate());
                if(Objects.equals(agentDepositWithdrawRespVO.getType(), DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())){
                 //   reportAgentMerchantStaticDayPO.setCurrencyCode(agentDepositWithdrawRespVO.getCurrencyCode());
                    //BigDecimal depositAmountWtc=AmountUtils.divide(agentDepositWithdrawRespVO.√(),convertRate);
                    reportAgentMerchantStaticDayPO.setDepositAmount(agentDepositWithdrawRespVO.getArriveAmount());
                    reportAgentMerchantStaticDayPO.setDepositWithdrawFee(settlementFeeWtc);
                }else {

                   // BigDecimal withdrawAmountWtc=AmountUtils.divide(agentDepositWithdrawRespVO.getArriveAmount(),convertRate);
                    reportAgentMerchantStaticDayPO.setWithdrawAmount(agentDepositWithdrawRespVO.getArriveAmount());
                    reportAgentMerchantStaticDayPO.setDepositWithdrawFee(settlementFeeWtc);
                }
              //  BigDecimal settlementFeeWtc=AmountUtils.divide(agentDepositWithdrawRespVO.getSettlementFeeAmount(),convertRate);
             //   reportAgentMerchantStaticDayPO.setDepositWithdrawFee(agentDepositWithdrawRespVO.getSettlementFeeAmount());//主货币
                log.info("商务报表 开始统计代理充提手续费:{}",reportAgentMerchantStaticDayPO);
                saveData(reportAgentMerchantStaticDayPO);
            }
            log.info("商务报表 站点:{},处理结束第:{}页 代理充提手续费计算",siteCode,pageNum);
        }
    }

    //统计代理所属团队 充提手续费 user_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
    private void initUserDepositWithdrawFee(ReportAgentMerchantStaticsCondVO reportAgentMerchantStaticsCondVO,Map<String,BigDecimal> rateMap) {
        String timeZone=reportAgentMerchantStaticsCondVO.getTimeZone();
        UserDepositWithdrawPageReqVO userDepositWithdrawPageReqVO=new UserDepositWithdrawPageReqVO();
        userDepositWithdrawPageReqVO.setSiteCode(reportAgentMerchantStaticsCondVO.getSiteCode());
        userDepositWithdrawPageReqVO.setStartTime(reportAgentMerchantStaticsCondVO.getStartDayMillis());
        userDepositWithdrawPageReqVO.setEndTime(reportAgentMerchantStaticsCondVO.getEndDayMillis());
        userDepositWithdrawPageReqVO.setUserAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        userDepositWithdrawPageReqVO.setPageNumber(1);
        userDepositWithdrawPageReqVO.setPageSize(500);
        String siteCode=reportAgentMerchantStaticsCondVO.getSiteCode();
        String reportType=reportAgentMerchantStaticsCondVO.getReportType();
        Page<UserDepositWithdrawalResVO> userDepositWithdrawalResVOPage = userDepositWithdrawApi.findDepositWithdrawPage(userDepositWithdrawPageReqVO);
        long totalPageNum = userDepositWithdrawalResVOPage.getPages();
        log.info("充提手续费计算 参数:{},总页数:{}",reportAgentMerchantStaticsCondVO,totalPageNum);
        for(int pageNum=1;pageNum<=totalPageNum;pageNum++) {
            userDepositWithdrawPageReqVO.setPageNumber(pageNum);
            log.info("站点:{},开始处理第:{}页 充提手续费计算",siteCode,pageNum);
            Page<UserDepositWithdrawalResVO> userDepositWithdrawalPageForEach=userDepositWithdrawApi.findDepositWithdrawPage(userDepositWithdrawPageReqVO);

            List<String> agentIds=userDepositWithdrawalPageForEach.getRecords().stream().filter(o->StringUtils.hasText(o.getAgentId())).map(UserDepositWithdrawalResVO::getAgentId).toList();
            if(CollectionUtils.isEmpty(agentIds)){
                continue;
            }
            List<AgentInfoVO> agentInfoVOS= agentInfoApi.getByAgentIds(agentIds);
            for(UserDepositWithdrawalResVO userDepositWithdrawalResVO:userDepositWithdrawalPageForEach.getRecords()){
                Optional<AgentInfoVO> agentInfoVOOptional=agentInfoVOS.stream().filter(o->o.getAgentId().equals(userDepositWithdrawalResVO.getAgentId())).findFirst();
                if(agentInfoVOOptional.isEmpty()){
                    log.info("充提手续费计算 代理信息不存在:{}",userDepositWithdrawalResVO.getAgentId());
                    continue;
                }
                AgentInfoVO currentAgentInfo=agentInfoVOOptional.get();
                String  reportDay=DateUtils.formatDateByZoneId(userDepositWithdrawalResVO.getUpdatedTime(),DateUtils.DATE_FORMAT_1,timeZone);
                String  reportMonth=DateUtils.formatDateByZoneId(userDepositWithdrawalResVO.getUpdatedTime(),DateUtils.DATE_FORMAT_2,timeZone);
                ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO=new ReportAgentMerchantStaticDayPO();
                reportAgentMerchantStaticDayPO.setReportType(reportType);
                // 按天统计
                if("0".equals(reportType)){
                    Long dayStartTime=DateUtils.getStartDayMillis(userDepositWithdrawalResVO.getUpdatedTime(),timeZone);
                    reportAgentMerchantStaticDayPO.setDayMillis(dayStartTime);
                    reportAgentMerchantStaticDayPO.setReportDate(reportDay);
                }else {
                    //按月统计
                    Long monthStartTime=DateUtils.getStartDayMonthTimestamp(userDepositWithdrawalResVO.getUpdatedTime(),timeZone);
                    reportAgentMerchantStaticDayPO.setDayMillis(monthStartTime);
                    reportAgentMerchantStaticDayPO.setReportDate(reportMonth);
                }
                reportAgentMerchantStaticDayPO.setSiteCode(siteCode);
                reportAgentMerchantStaticDayPO.setMerchantAccount(currentAgentInfo.getMerchantAccount());
                reportAgentMerchantStaticDayPO.setMerchantName(currentAgentInfo.getMerchantName());
                reportAgentMerchantStaticDayPO.setCurrencyCode(userDepositWithdrawalResVO.getCurrencyCode());
              //  reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                BigDecimal convertRate=rateMap.get(userDepositWithdrawalResVO.getCurrencyCode());
                if(Objects.equals(userDepositWithdrawalResVO.getType(), DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())){
                  //  BigDecimal depositAmountWtc=AmountUtils.divide(userDepositWithdrawalResVO.getArriveAmount(),convertRate);
                    reportAgentMerchantStaticDayPO.setDepositAmount(userDepositWithdrawalResVO.getArriveAmount());
                }else {
                   // BigDecimal withdrawAmountWtc=AmountUtils.divide(userDepositWithdrawalResVO.getArriveAmount(),convertRate);
                    reportAgentMerchantStaticDayPO.setWithdrawAmount(userDepositWithdrawalResVO.getApplyAmount());
                }
             //   BigDecimal settlementFeeWtc=AmountUtils.divide(userDepositWithdrawalResVO.getSettlementFeeAmount(),convertRate);
                reportAgentMerchantStaticDayPO.setDepositWithdrawFee(userDepositWithdrawalResVO.getSettlementFeeAmount());
                log.info("商务报表 开始统计用户充提手续费:{}",reportAgentMerchantStaticDayPO);
                saveData(reportAgentMerchantStaticDayPO);
            }
            log.info("商务报表 站点:{},处理结束第:{}页 充提手续费计算",siteCode,pageNum);
        }



    }

    /**
     * 代理金额统计
     * @param userWinLoseResponseVOList 原始会员盈亏列表
     * @param reportType 报表统计类型 0:日报 1:月报
     * @param timeZone  时区
     */
    private void batchProcess(List<UserWinLoseListResponseVO> userWinLoseResponseVOList,
                              String reportType,
                              String timeZone,
                              Map<String,BigDecimal> rateMap
                              ) {

        if(CollectionUtils.isEmpty(userWinLoseResponseVOList)){
            return;
        }
        List<String> agentIds=userWinLoseResponseVOList.stream().filter(o->StringUtils.hasText(o.getAgentId())).map(UserWinLoseListResponseVO::getAgentId).toList();
        if(CollectionUtils.isEmpty(agentIds)){
            return;
        }
        List<AgentInfoVO> agentInfoVOS= agentInfoApi.getByAgentIds(agentIds);
        userWinLoseResponseVOList= userWinLoseResponseVOList.stream().filter(o->StringUtils.hasText(o.getAgentId())).toList();

       for(UserWinLoseListResponseVO userWinLoseResponseVO:userWinLoseResponseVOList ) {
           //过滤出来当期代理详细信息
           AgentInfoVO agentInfoVO = agentInfoVOS.stream().filter(o -> o.getAgentId().equals(userWinLoseResponseVO.getAgentId())).findFirst().get();

           ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO = new ReportAgentMerchantStaticDayPO();
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
           reportAgentMerchantStaticDayPO.setMerchantAccount(agentInfoVO.getMerchantAccount());
           reportAgentMerchantStaticDayPO.setMerchantName(agentInfoVO.getMerchantName());
           reportAgentMerchantStaticDayPO.setSiteCode(siteCode);
           // reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
           reportAgentMerchantStaticDayPO.setCurrencyCode(currencyCode);
           reportAgentMerchantStaticDayPO.setReportType(reportType);
           // 按天统计
           if ("0".equals(reportType)) {
               reportAgentMerchantStaticDayPO.setDayMillis(dayMillis);
               reportAgentMerchantStaticDayPO.setReportDate(reportDay);
               reportDate = reportDay;
               dayStr = dayMillis.toString();
           } else {
               //按月统计
               reportAgentMerchantStaticDayPO.setDayMillis(monthStartTime);
               reportAgentMerchantStaticDayPO.setReportDate(reportMonth);
               reportDate = reportMonth;
               dayMillis = monthStartTime;
               dayStr = monthStartTime.toString();
           }
           boolean dataExists = false;

           //投注单数
           reportAgentMerchantStaticDayPO.setBetUserNum(Long.valueOf(userWinLoseResponseVO.getBetNum()));

           //投注金额
           //  BigDecimal convertRate=rateMap.get(currencyCode);
           //  BigDecimal betAmountWtc=AmountUtils.divide(userWinLoseResponseVO.getBetAmount(),convertRate);
           reportAgentMerchantStaticDayPO.setBetAmount(userWinLoseResponseVO.getBetAmount());
           //有效投注
           //  BigDecimal validBetAmountWtc=AmountUtils.divide(userWinLoseResponseVO.getValidBetAmount(),convertRate);
           reportAgentMerchantStaticDayPO.setValidAmount(userWinLoseResponseVO.getValidBetAmount());
           //会员输赢
           // BigDecimal betWinLoseWtc=AmountUtils.divide(userWinLoseResponseVO.getBetWinLose(),convertRate);
           reportAgentMerchantStaticDayPO.setWinLossAmountUser(userWinLoseResponseVO.getBetWinLose());
           //平台总输赢 等于 -会员输赢
           reportAgentMerchantStaticDayPO.setWinLossAmountPlat(BigDecimal.ZERO.subtract(userWinLoseResponseVO.getBetWinLose()));
           //已使用优惠
           //  BigDecimal alreadyUseAmountWtc=AmountUtils.divide(userWinLoseResponseVO.getAlreadyUseAmount(),convertRate);
           reportAgentMerchantStaticDayPO.setAlreadyUseAmount(userWinLoseResponseVO.getAlreadyUseAmount());
           reportAgentMerchantStaticDayPO.setTipsAmount(userWinLoseResponseVO.getTipsAmount());
           reportAgentMerchantStaticDayPO.setAdjustAmount(userWinLoseResponseVO.getAdjustAmount());
           reportAgentMerchantStaticDayPO.setDepositWithdrawFee(BigDecimal.ZERO);
           reportAgentMerchantStaticDayPO.setDepositAmount(BigDecimal.ZERO);
           reportAgentMerchantStaticDayPO.setWithdrawAmount(BigDecimal.ZERO);

           if (reportAgentMerchantStaticDayPO.getBetUserNum() !=0) {
               dataExists = true;
           }
           if (reportAgentMerchantStaticDayPO.getBetAmount().compareTo(BigDecimal.ZERO) != 0) {
               dataExists = true;
           }
           if (reportAgentMerchantStaticDayPO.getValidAmount().compareTo(BigDecimal.ZERO) != 0) {
               dataExists = true;
           }
           if (reportAgentMerchantStaticDayPO.getWinLossAmountUser().compareTo(BigDecimal.ZERO) != 0) {
               dataExists = true;
           }
           if (reportAgentMerchantStaticDayPO.getWinLossAmountPlat().compareTo(BigDecimal.ZERO) != 0) {
               dataExists = true;
           }
           if (reportAgentMerchantStaticDayPO.getAlreadyUseAmount().compareTo(BigDecimal.ZERO) != 0) {
               dataExists = true;
           }
           if (reportAgentMerchantStaticDayPO.getTipsAmount().compareTo(BigDecimal.ZERO) != 0) {
               dataExists = true;
           }
           if (reportAgentMerchantStaticDayPO.getAdjustAmount().compareTo(BigDecimal.ZERO) != 0) {
               dataExists = true;
           }
           if (dataExists) {
               //保存至数据库
               String uniKey=reportAgentMerchantStaticDayPO.getSiteCode().concat(reportAgentMerchantStaticDayPO.getReportDate()).concat(reportAgentMerchantStaticDayPO.getMerchantAccount());
               log.info("开始记录投注:{},{}",uniKey,reportAgentMerchantStaticDayPO);
               saveData(reportAgentMerchantStaticDayPO);
           }
       }
    }


    /**
     * 填充统计人数
     * @param reportAgentStaticDay
     */
    private void fillAgentNum(ReportAgentMerchantStaticDayPO reportAgentStaticDay) {
        String siteCode= reportAgentStaticDay.getSiteCode();
        String merchantAccount= reportAgentStaticDay.getMerchantAccount();
        MerchantAgentInfoVO merchantAgentInfo=agentMerchantApi.getMerchantAgentInfo(siteCode,merchantAccount);
        if(merchantAgentInfo!=null){
            //代理信息填充
            reportAgentStaticDay.setRegisterTime(merchantAgentInfo.getRegisterTime());
            reportAgentStaticDay.setRiskLevelId(merchantAgentInfo.getRiskId());
            //直属总代人数
            Long directAgentNum=merchantAgentInfo.getAgentCount()==null?0:merchantAgentInfo.getAgentCount();
            reportAgentStaticDay.setDirectAgentNum(directAgentNum);
        }
        Long teamAgentNum=agentMerchantApi.getTeamNum(siteCode,merchantAccount);
        teamAgentNum=teamAgentNum==null?0:teamAgentNum;
        //团队代理人数
        reportAgentStaticDay.setTeamAgentNum(teamAgentNum);
    }

    /**
     * 填充 注册人数 首存人数
     * @param reportAgentMerchantStaticsCondVO 代理信息统计
     * @return
     */
    private void  fillRegisterNum(ReportAgentMerchantStaticsCondVO reportAgentMerchantStaticsCondVO){
        String siteCode=reportAgentMerchantStaticsCondVO.getSiteCode();
        String reportType=reportAgentMerchantStaticsCondVO.getReportType();

        UserInfoPageVO vo=new UserInfoPageVO();
        vo.setSiteCode(siteCode);
        vo.setAccountType(List.of(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode()));
        vo.setRegisterTimeStart(reportAgentMerchantStaticsCondVO.getStartDayMillis());
        vo.setRegisterTimeEnd(reportAgentMerchantStaticsCondVO.getEndDayMillis());
        vo.setAgentFlag(1);
        vo.setPageNumber(1);
        vo.setPageSize(500);
        Page<UserInfoResponseVO> userInfoResponseVOFirstPage= userInfoApi.listPage(vo);
        long totalPageNum=userInfoResponseVOFirstPage.getPages();
        Map<String,ReportAgentMerchantStaticDayPO> agentUserRegisterStaticMap=new HashMap<>();
        Map<String,Set<String>> userRegisterMap=new HashMap<>();
        for(int pageNum=1;pageNum<=totalPageNum;pageNum++){
            vo.setPageNumber(pageNum);
            log.info("注册人数查询:{}",vo);
            Page<UserInfoResponseVO> userInfoResponseVOPage= userInfoApi.listPage(vo);

            List<String> agentIds=userInfoResponseVOPage.getRecords().stream().filter(o-> StringUtils.hasText(o.getSuperAgentId())).map(UserInfoResponseVO::getSuperAgentId).toList();
            List<AgentInfoVO> agentInfoVOS =Lists.newArrayList();
            if(!CollectionUtils.isEmpty(agentIds)){
                agentInfoVOS = agentInfoApi.getByAgentIds(agentIds);
            }
            if(CollectionUtils.isEmpty(agentInfoVOS)){
                continue;
            }

            for(UserInfoResponseVO userInfoResponseVO:userInfoResponseVOPage.getRecords()){
                if(!StringUtils.hasText(userInfoResponseVO.getSuperAgentId())){
                    continue;
                }
                AgentInfoVO agentInfoVO=agentInfoVOS.stream().filter(o->o.getAgentId().equals(userInfoResponseVO.getSuperAgentId())).findFirst().get();

                String mapKey=userInfoResponseVO.getSiteCode()
                        .concat("#")
                        .concat(agentInfoVO.getMerchantAccount())
                        .concat("#")
                        .concat(agentInfoVO.getMerchantName())
                        .concat("#")
                        .concat(userInfoResponseVO.getMainCurrency());
                        if("0".equals(reportType)){
                            mapKey=mapKey.concat("#")
                            .concat(DateUtils.getStartDayMillis(userInfoResponseVO.getRegisterTime(),reportAgentMerchantStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getRegisterTime(),DateUtils.DATE_FORMAT_1,reportAgentMerchantStaticsCondVO.getTimeZone()));
                        }else {
                            mapKey=mapKey.concat("#")
                            .concat(DateUtils.getStartDayMonthTimestamp(userInfoResponseVO.getRegisterTime(),reportAgentMerchantStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getRegisterTime(),DateUtils.DATE_FORMAT_2,reportAgentMerchantStaticsCondVO.getTimeZone()));
                        }

                ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO=new ReportAgentMerchantStaticDayPO();
                reportAgentMerchantStaticDayPO.setReportType(reportType);
                reportAgentMerchantStaticDayPO.setSiteCode(userInfoResponseVO.getSiteCode());
                reportAgentMerchantStaticDayPO.setMerchantAccount(agentInfoVO.getMerchantAccount());
                reportAgentMerchantStaticDayPO.setMerchantName(agentInfoVO.getMerchantName());
                reportAgentMerchantStaticDayPO.setCurrencyCode(userInfoResponseVO.getMainCurrency());
                // reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                long starDayMillis=Long.valueOf(mapKey.split("#")[4]);
                reportAgentMerchantStaticDayPO.setDayMillis(starDayMillis);
                reportAgentMerchantStaticDayPO.setReportDate(mapKey.split("#")[5]);
                String uniKey=reportAgentMerchantStaticDayPO.getSiteCode().concat(reportAgentMerchantStaticDayPO.getReportDate()).concat(reportAgentMerchantStaticDayPO.getMerchantAccount());
                if(userRegisterMap.containsKey(mapKey)){
                    Set<String> userAccountSet=userRegisterMap.get(mapKey);
                    userAccountSet.add(userInfoResponseVO.getUserId());
                    userRegisterMap.put(mapKey,userAccountSet) ;
                    reportAgentMerchantStaticDayPO.setRegisterUserNum(Long.valueOf(userAccountSet.size()));
                    log.info("原始注册人数:{},value:{},cond:{} register02",uniKey,reportAgentMerchantStaticDayPO,reportAgentMerchantStaticsCondVO);
                }else {
                    Set<String> userAccountSet=new HashSet<>();
                    userAccountSet.add(userInfoResponseVO.getUserId());
                    userRegisterMap.put(mapKey,userAccountSet) ;
                    reportAgentMerchantStaticDayPO.setRegisterUserNum(1L);
                    log.info("原始注册人数:{},value:{},cond:{} register01",uniKey,reportAgentMerchantStaticDayPO,reportAgentMerchantStaticsCondVO);
                }
                agentUserRegisterStaticMap.put(mapKey,reportAgentMerchantStaticDayPO);
            }
        }

        UserInfoPageVO depositVo=new UserInfoPageVO();
        depositVo.setSiteCode(siteCode);
        depositVo.setFirstDepositTimeStart(reportAgentMerchantStaticsCondVO.getStartDayMillis());
        depositVo.setFirstDepositTimeEnd(reportAgentMerchantStaticsCondVO.getEndDayMillis());
        depositVo.setAccountType(List.of(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode()));
        depositVo.setAgentFlag(1);
        depositVo.setPageNumber(1);
        depositVo.setPageSize(500);
        Page<UserInfoResponseVO> userInfoDepositFirstPage= userInfoApi.listPage(depositVo);
        long totalDepositPageNum=userInfoDepositFirstPage.getPages();
        Map<String,Set<String>> userDepositMap=new HashMap<>();
        for(int pageNum=1;pageNum<=totalDepositPageNum;pageNum++){
            depositVo.setPageNumber(pageNum);
            log.info("首存人数查询:{}",depositVo);
            Page<UserInfoResponseVO> userInfoDepositPage= userInfoApi.listPage(depositVo);

            List<String> agentIds=userInfoDepositPage.getRecords().stream().filter(o-> StringUtils.hasText(o.getSuperAgentId())).map(UserInfoResponseVO::getSuperAgentId).toList();
            List<AgentInfoVO> agentInfoVOS =Lists.newArrayList();
            if(!CollectionUtils.isEmpty(agentIds)){
                agentInfoVOS = agentInfoApi.getByAgentIds(agentIds);
            }
            if(CollectionUtils.isEmpty(agentInfoVOS)){
                continue;
            }

            for(UserInfoResponseVO userInfoResponseVO:userInfoDepositPage.getRecords()){
                if(!StringUtils.hasText(userInfoResponseVO.getSuperAgentId())){
                    log.debug("站点:{}首存人数 代理为空",siteCode);
                    continue;
                }
                if(userInfoResponseVO.getFirstDepositTime()==null){
                    continue;
                }
                AgentInfoVO agentInfoVO=agentInfoVOS.stream().filter(o->o.getAgentId().equals(userInfoResponseVO.getSuperAgentId())).findFirst().get();


                String mapKey=userInfoResponseVO.getSiteCode()
                        .concat("#")
                        .concat(agentInfoVO.getMerchantAccount())
                        .concat("#")
                        .concat(agentInfoVO.getMerchantName())
                        .concat("#")
                        .concat(userInfoResponseVO.getMainCurrency());
                if("0".equals(reportType)){
                    mapKey=mapKey.concat("#")
                            .concat(DateUtils.getStartDayMillis(userInfoResponseVO.getFirstDepositTime(),reportAgentMerchantStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getFirstDepositTime(),DateUtils.DATE_FORMAT_1,reportAgentMerchantStaticsCondVO.getTimeZone()));
                }else {
                    mapKey=mapKey.concat("#")
                            .concat(DateUtils.getStartDayMonthTimestamp(userInfoResponseVO.getFirstDepositTime(),reportAgentMerchantStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getFirstDepositTime(),DateUtils.DATE_FORMAT_2,reportAgentMerchantStaticsCondVO.getTimeZone()));
                }

                Long depositNum=0L;
                if(userDepositMap.containsKey(mapKey)){
                    Set<String> userAccountSet=userDepositMap.get(mapKey);
                    userAccountSet.add(userInfoResponseVO.getUserId());
                    userDepositMap.put(mapKey,userAccountSet) ;
                    depositNum=Long.valueOf(userAccountSet.size());
                }else {
                    Set<String> userAccountSet=new HashSet<>();
                    userAccountSet.add(userInfoResponseVO.getUserId());
                    userDepositMap.put(mapKey,userAccountSet) ;
                    depositNum=1L;
                }

                if(agentUserRegisterStaticMap.containsKey(mapKey)){
                    ReportAgentMerchantStaticDayPO registerStaticDay=agentUserRegisterStaticMap.get(mapKey);
                    registerStaticDay.setSiteCode(userInfoResponseVO.getSiteCode());
                    registerStaticDay.setFirstDepositNum(depositNum);
                    BigDecimal  convertRate= NumberUtil.divide(registerStaticDay.getFirstDepositNum(),registerStaticDay.getRegisterUserNum(),4);
                    registerStaticDay.setFirstDepositRate(convertRate);
                    agentUserRegisterStaticMap.put(mapKey,registerStaticDay);
                    String uniKey=registerStaticDay.getSiteCode().concat(registerStaticDay.getReportDate()).concat(registerStaticDay.getMerchantAccount());
                    log.info("商务报表充值人数:{},key:{},value:{} deposit01",uniKey,mapKey,registerStaticDay);
                }else {
                    ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO=new ReportAgentMerchantStaticDayPO();
                    reportAgentMerchantStaticDayPO.setSiteCode(userInfoResponseVO.getSiteCode());
                    reportAgentMerchantStaticDayPO.setReportType(reportType);
                    reportAgentMerchantStaticDayPO.setSiteCode(userInfoResponseVO.getSiteCode());
                    reportAgentMerchantStaticDayPO.setMerchantAccount(agentInfoVO.getMerchantAccount());
                    reportAgentMerchantStaticDayPO.setMerchantName(agentInfoVO.getMerchantName());
                    reportAgentMerchantStaticDayPO.setCurrencyCode(userInfoResponseVO.getMainCurrency());
                    //reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                    long starDayMillis=Long.valueOf(mapKey.split("#")[4]);
                    reportAgentMerchantStaticDayPO.setDayMillis(starDayMillis);
                    reportAgentMerchantStaticDayPO.setReportDate(mapKey.split("#")[5]);
                    reportAgentMerchantStaticDayPO.setRegisterUserNum(0L);
                    reportAgentMerchantStaticDayPO.setFirstDepositNum(depositNum);
                    reportAgentMerchantStaticDayPO.setFirstDepositRate(BigDecimal.ZERO);
                    agentUserRegisterStaticMap.put(mapKey,reportAgentMerchantStaticDayPO);
                    String uniKey=reportAgentMerchantStaticDayPO.getSiteCode().concat(reportAgentMerchantStaticDayPO.getReportDate()).concat(reportAgentMerchantStaticDayPO.getMerchantAccount());
                    log.info("商务报表充值人数:{},key:{},value:{} deposit02",uniKey,mapKey,reportAgentMerchantStaticDayPO);
                }
            }
        }
        if(CollectionUtils.isEmpty(agentUserRegisterStaticMap)){
            return ;
        }
        for(String mapKey:agentUserRegisterStaticMap.keySet()) {
            ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO = agentUserRegisterStaticMap.get(mapKey);
            String uniKey=reportAgentMerchantStaticDayPO.getSiteCode().concat(reportAgentMerchantStaticDayPO.getReportDate()).concat(reportAgentMerchantStaticDayPO.getMerchantAccount());
            //保存 注册人数 首存人数
            log.info("商务报表:{},开始保存注册人数、首存人数:{}",uniKey,reportAgentMerchantStaticDayPO);
            saveData(reportAgentMerchantStaticDayPO);
        }
    }


    /**
     * 保存至数据库
     * @param reportAgentStaticDay 原始数据
     */
    private void saveData(ReportAgentMerchantStaticDayPO reportAgentStaticDay) {
        LambdaQueryWrapper<ReportAgentMerchantStaticDayPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getSiteCode,reportAgentStaticDay.getSiteCode());
        lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getReportType,reportAgentStaticDay.getReportType());
        lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getReportDate,reportAgentStaticDay.getReportDate());
        lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getCurrencyCode,reportAgentStaticDay.getCurrencyCode());
        lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getMerchantAccount,reportAgentStaticDay.getMerchantAccount());
        ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPODb=this.baseMapper.selectOne(lambdaQueryWrapper);
        //已存在 则更新
        if(reportAgentMerchantStaticDayPODb!=null){
            LambdaUpdateWrapper<ReportAgentMerchantStaticDayPO> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();

            if(reportAgentStaticDay.getTeamAgentNum()!=null&&reportAgentStaticDay.getTeamAgentNum()>0){
                lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getTeamAgentNum,reportAgentStaticDay.getTeamAgentNum());
            }
            if(reportAgentStaticDay.getDirectAgentNum()!=null&&reportAgentStaticDay.getDirectAgentNum()>0){
                lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getDirectAgentNum,reportAgentStaticDay.getDirectAgentNum());
            }
            //注册人数
            if(reportAgentStaticDay.getRegisterUserNum()!=null&&reportAgentStaticDay.getRegisterUserNum()>0){
                lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getRegisterUserNum,reportAgentStaticDay.getRegisterUserNum());
            }
            //首存人数
            if(reportAgentStaticDay.getFirstDepositNum()!=null&&reportAgentStaticDay.getFirstDepositNum()>0){
                lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getFirstDepositNum,reportAgentStaticDay.getFirstDepositNum());
            }
            //首存转换率
            if(reportAgentStaticDay.getFirstDepositRate()!=null&&reportAgentStaticDay.getFirstDepositRate().compareTo(BigDecimal.ZERO)!=0){
                lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getFirstDepositRate,reportAgentStaticDay.getFirstDepositRate());
            }
            //投注人次
            if(reportAgentStaticDay.getBetUserNum()!=null&&reportAgentStaticDay.getBetUserNum()>0){
                //投注人次
                lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getBetUserNum,reportAgentMerchantStaticDayPODb.getBetUserNum()+reportAgentStaticDay.getBetUserNum());
            }
            lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getBetAmount,reportAgentMerchantStaticDayPODb.getBetAmount().add(reportAgentStaticDay.getBetAmount()));
            lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getValidAmount,reportAgentMerchantStaticDayPODb.getValidAmount().add(reportAgentStaticDay.getValidAmount()));
            lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getWinLossAmountUser,reportAgentMerchantStaticDayPODb.getWinLossAmountUser().add(reportAgentStaticDay.getWinLossAmountUser()));
            lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getWinLossAmountPlat,reportAgentMerchantStaticDayPODb.getWinLossAmountPlat().add(reportAgentStaticDay.getWinLossAmountPlat()));
            lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getAlreadyUseAmount,reportAgentMerchantStaticDayPODb.getAlreadyUseAmount().add(reportAgentStaticDay.getAlreadyUseAmount()));
            BigDecimal tipsAmount=reportAgentStaticDay.getTipsAmount()==null?BigDecimal.ZERO:reportAgentStaticDay.getTipsAmount();
            lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getTipsAmount,reportAgentMerchantStaticDayPODb.getTipsAmount().add(tipsAmount));
            BigDecimal adjustAmount=reportAgentStaticDay.getAdjustAmount()==null?BigDecimal.ZERO:reportAgentStaticDay.getAdjustAmount();
            lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getAdjustAmount,reportAgentMerchantStaticDayPODb.getAdjustAmount().add(adjustAmount));

            //存款金额
            if(reportAgentStaticDay.getDepositAmount()!=null){
                lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getDepositAmount,reportAgentMerchantStaticDayPODb.getDepositAmount().add(reportAgentStaticDay.getDepositAmount()));
            }
            //提款金额
            if(reportAgentStaticDay.getWithdrawAmount()!=null){
                lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getWithdrawAmount,reportAgentMerchantStaticDayPODb.getWithdrawAmount().add(reportAgentStaticDay.getWithdrawAmount()));
            }
            //存提手续费
            if(reportAgentStaticDay.getDepositWithdrawFee()!=null){
                lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getDepositWithdrawFee,reportAgentMerchantStaticDayPODb.getDepositWithdrawFee().add(reportAgentStaticDay.getDepositWithdrawFee()));
            }
            lambdaUpdateWrapper.set(ReportAgentMerchantStaticDayPO::getUpdatedTime,System.currentTimeMillis());
            lambdaUpdateWrapper.eq(ReportAgentMerchantStaticDayPO::getId,reportAgentMerchantStaticDayPODb.getId());
            String uniKey=reportAgentStaticDay.getSiteCode().concat(reportAgentStaticDay.getReportDate()).concat(reportAgentStaticDay.getMerchantAccount());
            log.info("商务报表开始更新:{},统计数据:{}",uniKey,reportAgentStaticDay);
            this.update(lambdaUpdateWrapper);
        }else {
            //填充代理 团队人数 直属下级人数
            fillAgentNum(reportAgentStaticDay);
            reportAgentStaticDay.setCreatedTime(System.currentTimeMillis());
            reportAgentStaticDay.setUpdatedTime(System.currentTimeMillis());
            reportAgentStaticDay.setId(null);
            String uniKey=reportAgentStaticDay.getSiteCode().concat(reportAgentStaticDay.getReportDate()).concat(reportAgentStaticDay.getMerchantAccount());
            log.info("商务报表开始记录:{},统计数据:{}",uniKey,reportAgentStaticDay);
            this.baseMapper.insert(reportAgentStaticDay);
        }
    }

    /**
     * 分页查询
     * @param reportAgentMerchantStaticsPageVO
     * @return
     */
    public ResponseVO<ReportAgentMerchantStaticsResult> listPage(ReportAgentMerchantStaticsPageVO reportAgentMerchantStaticsPageVO) {
        //过滤代理信息
        /*List<AgentInfoVO> agentInfoVOS=Lists.newArrayList();
        if(
                reportAgentMerchantStaticsPageVO.getStartRegisterDay()!=null ||
                reportAgentMerchantStaticsPageVO.getEndRegisterDay()!=null
        ){
            AgentInfoCondVo agentInfoCondVo=new AgentInfoCondVo();
            BeanUtils.copyProperties(reportAgentMerchantStaticsPageVO,agentInfoCondVo);
            agentInfoVOS=agentInfoApi.getAgentListByCond(agentInfoCondVo);
            if(CollectionUtils.isEmpty(agentInfoVOS)){
                //按照查询条件没有查询出来代理信息 直接返回空
                return ResponseVO.success(new ReportAgentMerchantStaticsResult());
            }
        }*/
        String siteCode=reportAgentMerchantStaticsPageVO.getSiteCode();
        String timeZone=siteApi.getSiteDetail(siteCode).getTimezone();
        log.info("siteCode:{},timeZone:{}",siteCode,timeZone);
        Page<ReportAgentMerchantStaticDayPO> page =new Page<ReportAgentMerchantStaticDayPO>(reportAgentMerchantStaticsPageVO.getPageNumber(),reportAgentMerchantStaticsPageVO.getPageSize());
        LambdaQueryWrapper<ReportAgentMerchantStaticDayPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getSiteCode,siteCode);
        //代理信息查询
        /*if(!CollectionUtils.isEmpty(agentInfoVOS)) {
            List<String> queryAgentIds = agentInfoVOS.stream().map(AgentInfoVO::getAgentId).toList();
            lambdaQueryWrapper.in(ReportAgentMerchantStaticDayPO::getAgentId, queryAgentIds);
        }*/

        //报表类型 字典类型:report_type  0:日报 1:月报
        lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getReportType,reportAgentMerchantStaticsPageVO.getReportType());
        if(reportAgentMerchantStaticsPageVO.getStartStaticDay()!=null){
            lambdaQueryWrapper.ge(ReportAgentMerchantStaticDayPO::getDayMillis,reportAgentMerchantStaticsPageVO.getStartStaticDay());
        }

        if(reportAgentMerchantStaticsPageVO.getEndStaticDay()!=null){
            lambdaQueryWrapper.le(ReportAgentMerchantStaticDayPO::getDayMillis,reportAgentMerchantStaticsPageVO.getEndStaticDay());
        }

        if(reportAgentMerchantStaticsPageVO.getStartRegisterDay()!=null){
            lambdaQueryWrapper.ge(ReportAgentMerchantStaticDayPO::getRegisterTime,reportAgentMerchantStaticsPageVO.getStartRegisterDay());
        }

        if(reportAgentMerchantStaticsPageVO.getEndRegisterDay()!=null){
            lambdaQueryWrapper.le(ReportAgentMerchantStaticDayPO::getRegisterTime,reportAgentMerchantStaticsPageVO.getEndRegisterDay());
        }


        if(StringUtils.hasText(reportAgentMerchantStaticsPageVO.getMerchantAccount())){
            lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getMerchantAccount,reportAgentMerchantStaticsPageVO.getMerchantAccount());
        }

        if(StringUtils.hasText(reportAgentMerchantStaticsPageVO.getMerchantName())){
            lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getMerchantName,reportAgentMerchantStaticsPageVO.getMerchantName());
        }

        if(StringUtils.hasText(reportAgentMerchantStaticsPageVO.getCurrencyCode())){
            lambdaQueryWrapper.eq(ReportAgentMerchantStaticDayPO::getCurrencyCode,reportAgentMerchantStaticsPageVO.getCurrencyCode());
        }

        if (StringUtils.hasText(reportAgentMerchantStaticsPageVO.getOrderField()) && StringUtils.hasText(reportAgentMerchantStaticsPageVO.getOrderType())) {

            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("directAgentNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getDirectAgentNum);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("directAgentNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getDirectAgentNum);
            }

            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("depositAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getDepositAmount);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("depositAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getDepositAmount);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("withdrawAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getWithdrawAmount);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("withdrawAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getWithdrawAmount);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("depositWithdrawFee") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getDepositWithdrawFee);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("depositWithdrawFee") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getDepositWithdrawFee);
            }


            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("betAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getBetAmount);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("betAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getBetAmount);
            }

            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("validAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getValidAmount);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("validAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getValidAmount);
            }

            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("winLossAmountUser") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getWinLossAmountUser);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("winLossAmountUser") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getWinLossAmountUser);
            }

            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("winLossAmountPlat") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getWinLossAmountPlat);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("winLossAmountPlat") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getWinLossAmountPlat);
            }


            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("alreadyUseAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getAlreadyUseAmount);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("alreadyUseAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getAlreadyUseAmount);
            }

            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("tipsAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getTipsAmount);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("tipsAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getTipsAmount);
            }

            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("adjustAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentMerchantStaticDayPO::getAdjustAmount);
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("adjustAmount") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getAdjustAmount);
            }


        }else{
            lambdaQueryWrapper.orderByDesc(ReportAgentMerchantStaticDayPO::getDayMillis);
        }
        //站点平台币兑换汇率
        Map<String,BigDecimal> rateMap=siteCurrencyInfoApi.getAllFinalRate(siteCode);
        rateMap.put(CommonConstant.PLAT_CURRENCY_CODE,BigDecimal.ONE);

        Page<ReportAgentMerchantStaticDayPO> reportAgentMerchantStaticDayPOPage=this.baseMapper.selectPage(page,lambdaQueryWrapper);
        List<ReportAgentMerchantStaticsResponseVO> reportAgentMerchantStaticsResponseVOS=Lists.newArrayList();
        Page<ReportAgentMerchantStaticsResponseVO> reportAgentMerchantStaticsResponseVOPage=new Page<ReportAgentMerchantStaticsResponseVO>();
        BeanUtils.copyProperties(reportAgentMerchantStaticDayPOPage,reportAgentMerchantStaticsResponseVOPage);
        ReportAgentMerchantStaticsResponseVO currentPage=new ReportAgentMerchantStaticsResponseVO();
        if(!CollectionUtils.isEmpty(reportAgentMerchantStaticDayPOPage.getRecords())){
            List<String> merchantAccounts=reportAgentMerchantStaticDayPOPage.getRecords().stream().map(ReportAgentMerchantStaticDayPO::getMerchantAccount).toList();
            List<AgentMerchantVO>  agentMerchantVOS=agentMerchantApi.getListByAccounts(siteCode,merchantAccounts);

            List<String> riskLevelIds = agentMerchantVOS.stream().map(o -> o.getRiskId()).toList();
            Map<String, RiskLevelDetailsVO> riskMap = riskApi.getByIds(riskLevelIds);
            for(ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO:reportAgentMerchantStaticDayPOPage.getRecords()){
                ReportAgentMerchantStaticsResponseVO reportAgentStaticsResponseVO=new ReportAgentMerchantStaticsResponseVO();
                BeanUtils.copyProperties(reportAgentMerchantStaticDayPO,reportAgentStaticsResponseVO);
                //商务信息填充
               fillAgentMerchantInfo(reportAgentStaticsResponseVO,agentMerchantVOS,riskMap);


                reportAgentStaticsResponseVO.setRegisterTimeStr(TimeZoneUtils.formatTimestampToTimeZone(reportAgentStaticsResponseVO.getRegisterTime(), timeZone));
                String currencyCode=reportAgentMerchantStaticDayPO.getCurrencyCode();
                //汇总页需要全部转换为平台币
                BigDecimal transferRate=rateMap.get(currencyCode);

                 /*// 人数需要去重 当页不统计
                //团队代理人数
                //currentPage.addTeamAgentNum(ReportAgentMerchantStaticDayPO.getTeamAgentNum());
                currentPage.setTeamAgentNum(null);
                //直属下级人数
               // currentPage.addDirectReportNum(ReportAgentMerchantStaticDayPO.getDirectReportNum());
                currentPage.setDirectAgentNum(null);
                //注册人数
                //currentPage.addRegisterUserNum(ReportAgentMerchantStaticDayPO.getRegisterUserNum());
                currentPage.setRegisterUserNum(null);
                //首存人数
                //currentPage.addFirstDepositNum(ReportAgentMerchantStaticDayPO.getFirstDepositNum());
                //注单量
                currentPage.setBetUserNum(null);



                //存款金额
                BigDecimal depositAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getDepositAmount(),transferRate);
                currentPage.addDepositAmount(depositAmountWtc);
                //提款金额
                BigDecimal withdrawAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getWithdrawAmount(),transferRate);
                currentPage.addWithdrawAmount(withdrawAmountWtc);
                //存提手续费
                BigDecimal depositWithdrawFeeWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getDepositWithdrawFee(),transferRate);
                currentPage.addDepositWithdrawFee(depositWithdrawFeeWtc);

                //当前页 汇总全是平台币
                //投注金额 汇总不需要转换为平台币 直接累加
                BigDecimal betAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getBetAmount(),transferRate);
                currentPage.addBetAmount(betAmountWtc);
              //  currentPage.addBetAmount(reportAgentMerchantStaticDayPO.getBetAmount());
               // currentPage.setBetAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                //有效投注金额
                BigDecimal validAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getValidAmount(),transferRate);
                currentPage.addValidAmount(validAmountWtc);
                //currentPage.addValidAmount(reportAgentMerchantStaticDayPO.getValidAmount());
                //currentPage.setValidAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                //会员输赢
                BigDecimal winLossAmountUserWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getWinLossAmountUser(),transferRate);
                currentPage.addWinLossAmountUser(winLossAmountUserWtc);
               // currentPage.setWinLossAmountUserCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                //currentPage.addWinLossAmountUser(reportAgentMerchantStaticDayPO.getWinLossAmountUser());
                //平台总输赢
                BigDecimal winLossAmountPlatWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getWinLossAmountPlat(),transferRate);
                currentPage.addWinLossAmountPlat(winLossAmountPlatWtc);
                //currentPage.setWinLossAmountPlatCurrency(CommonConstant.PLAT_CURRENCY_CODE);
               // currentPage.addWinLossAmountPlat(reportAgentMerchantStaticDayPO.getWinLossAmountPlat());

                //已使用优惠
                BigDecimal alreadyUseAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getAlreadyUseAmount(),transferRate);
                currentPage.addAlreadyUseAmount(alreadyUseAmountWtc);
               // currentPage.setAlreadyUseAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);*/

                reportAgentStaticsResponseVO.setPlatCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);

                //是否转换成平台币
                if(reportAgentMerchantStaticsPageVO.isTransferPlatformFlag()){
                    //
                    //BigDecimal transferRate1=rateMap.get(currencyCode);
                    //存款金额
                    BigDecimal depositAmountWtc1=AmountUtils.divide(reportAgentMerchantStaticDayPO.getDepositAmount(),transferRate);
                    reportAgentStaticsResponseVO.setDepositAmount(depositAmountWtc1);
                    //提款金额
                    BigDecimal withDrawAmountWtc1=AmountUtils.divide(reportAgentMerchantStaticDayPO.getWithdrawAmount(),transferRate);
                    reportAgentStaticsResponseVO.setWithdrawAmount(withDrawAmountWtc1);
                    //存提手续费
                    BigDecimal depositWithDrawFeeWtc1=AmountUtils.divide(reportAgentMerchantStaticDayPO.getDepositWithdrawFee(),transferRate);
                    reportAgentStaticsResponseVO.setDepositWithdrawFee(depositWithDrawFeeWtc1);

                    //投注金额
                    BigDecimal betAmountWtc1=AmountUtils.divide(reportAgentMerchantStaticDayPO.getBetAmount(),transferRate);
                    reportAgentStaticsResponseVO.setBetAmount(betAmountWtc1);
                    //有效投注金额
                    BigDecimal validAmountWtc1=AmountUtils.divide(reportAgentMerchantStaticDayPO.getValidAmount(),transferRate);
                    reportAgentStaticsResponseVO.setValidAmount(validAmountWtc1);
                    //会员输赢
                    BigDecimal winLossAmountUserWtc1=AmountUtils.divide(reportAgentMerchantStaticDayPO.getWinLossAmountUser(),transferRate);
                    reportAgentStaticsResponseVO.setWinLossAmountUser(winLossAmountUserWtc1);
                    //平台总输赢
                    BigDecimal winLossAmountPlatWtc1=AmountUtils.divide(reportAgentMerchantStaticDayPO.getWinLossAmountPlat(),transferRate);
                    reportAgentStaticsResponseVO.setWinLossAmountPlat(winLossAmountPlatWtc1);
                    //已使用优惠金额
                    BigDecimal alreadyUseAmountWtc1=AmountUtils.divide(reportAgentMerchantStaticDayPO.getAlreadyUseAmount(),transferRate);
                    reportAgentStaticsResponseVO.setAlreadyUseAmount(alreadyUseAmountWtc1);
                    //已使用优惠金额
                    BigDecimal tipsAmountWtc1=AmountUtils.divide(reportAgentMerchantStaticDayPO.getTipsAmount(),transferRate);
                    reportAgentStaticsResponseVO.setTipsAmount(tipsAmountWtc1);
                    //调整金额
                    BigDecimal adjustAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getAdjustAmount(),transferRate);
                    reportAgentStaticsResponseVO.setAdjustAmount(adjustAmountWtc);
                }
                reportAgentMerchantStaticsResponseVOS.add(reportAgentStaticsResponseVO);
            }
        }

        if (StringUtils.hasText(reportAgentMerchantStaticsPageVO.getOrderField()) && StringUtils.hasText(reportAgentMerchantStaticsPageVO.getOrderType())) {
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("registerTime") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS.stream().sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getRegisterTime)).toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("registerTime") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS
                        .stream()
                        .sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getRegisterTime).reversed())
                        .toList();
            }

            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("teamAgentNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS.stream().sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getTeamAgentNum)).toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("teamAgentNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS
                        .stream()
                        .sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getTeamAgentNum).reversed())
                        .toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("directReportNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS.stream().sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getDirectAgentNum)).toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("directReportNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS
                        .stream()
                        .sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getDirectAgentNum).reversed())
                        .toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("registerUserNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS.stream().sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getRegisterUserNum)).toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("registerUserNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS
                        .stream()
                        .sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getRegisterUserNum).reversed())
                        .toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("firstDepositNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS.stream().sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getFirstDepositNum)).toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("firstDepositNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS
                        .stream()
                        .sorted(Comparator.comparingLong(ReportAgentMerchantStaticsResponseVO::getFirstDepositNum).reversed())
                        .toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("betUserNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS.stream().sorted(Comparator.comparing(ReportAgentMerchantStaticsResponseVO::getBetUserNum)).toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("betUserNum") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS
                        .stream()
                        .sorted(Comparator.comparing(ReportAgentMerchantStaticsResponseVO::getBetUserNum).reversed())
                        .toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("depositWithdrawFee") && reportAgentMerchantStaticsPageVO.getOrderType().equals("asc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS.stream().sorted(Comparator.comparing(ReportAgentMerchantStaticsResponseVO::getDepositWithdrawFee)).toList();
            }
            if (reportAgentMerchantStaticsPageVO.getOrderField().equals("depositWithdrawFee") && reportAgentMerchantStaticsPageVO.getOrderType().equals("desc")) {
                reportAgentMerchantStaticsResponseVOS=reportAgentMerchantStaticsResponseVOS
                        .stream()
                        .sorted(Comparator.comparing(ReportAgentMerchantStaticsResponseVO::getDepositWithdrawFee).reversed())
                        .toList();
            }
        }

        reportAgentMerchantStaticsResponseVOPage.setRecords(reportAgentMerchantStaticsResponseVOS);
        ReportAgentMerchantStaticsResult reportAgentMerchantStaticsResult=new ReportAgentMerchantStaticsResult();
        reportAgentMerchantStaticsResult.setPageList(reportAgentMerchantStaticsResponseVOPage);
        reportAgentMerchantStaticsResult.setCurrentPage(currentPage);
        // 汇总
        ReportAgentMerchantStaticsResponseVO totalPage=new ReportAgentMerchantStaticsResponseVO();
        List<ReportAgentMerchantStaticDayPO> reportAgentMerchantStaticDayPOList=this.baseMapper.selectTotal(lambdaQueryWrapper);
        if(!CollectionUtils.isEmpty(reportAgentMerchantStaticDayPOList)){
            for(ReportAgentMerchantStaticDayPO reportAgentMerchantStaticDayPO:reportAgentMerchantStaticDayPOList){
                //团队代理人数
                totalPage.addTeamAgentNum(reportAgentMerchantStaticDayPO.getTeamAgentNum());
                //直属人数
                totalPage.addDirectAgentNum(reportAgentMerchantStaticDayPO.getDirectAgentNum());
                //注册人数
                totalPage.addRegisterUserNum(reportAgentMerchantStaticDayPO.getRegisterUserNum());
                //首存人数
                totalPage.addFirstDepositNum(reportAgentMerchantStaticDayPO.getFirstDepositNum());
                //注单量
                totalPage.addBetUserNum(reportAgentMerchantStaticDayPO.getBetUserNum());
                //是否转换成平台币
                if(!reportAgentMerchantStaticsPageVO.isTransferPlatformFlag()) {
                    //存款金额
                    totalPage.addDepositAmount(reportAgentMerchantStaticDayPO.getDepositAmount());
                    //提款金额
                    totalPage.addWithdrawAmount(reportAgentMerchantStaticDayPO.getWithdrawAmount());
                    //存取手续费
                    totalPage.addDepositWithdrawFee(reportAgentMerchantStaticDayPO.getDepositWithdrawFee());
                    //投注金额
                    totalPage.addBetAmount(reportAgentMerchantStaticDayPO.getBetAmount());
                    //有效投注金额
                    totalPage.addValidAmount(reportAgentMerchantStaticDayPO.getValidAmount());
                    //会员输赢
                    totalPage.addWinLossAmountUser(reportAgentMerchantStaticDayPO.getWinLossAmountUser());
                    //平台总输赢
                    totalPage.addWinLossAmountPlat(reportAgentMerchantStaticDayPO.getWinLossAmountPlat());
                    //已使用优惠
                    totalPage.addAlreadyUseAmount(reportAgentMerchantStaticDayPO.getAlreadyUseAmount());
                    //打赏金额
                    totalPage.addTipsAmount(reportAgentMerchantStaticDayPO.getTipsAmount());
                    //调整金额
                    totalPage.addAdjustAmount(reportAgentMerchantStaticDayPO.getAdjustAmount());
                }else {
                    String currencyCode=reportAgentMerchantStaticDayPO.getCurrencyCode();
                    BigDecimal transferRate=rateMap.get(currencyCode);
                    //存款金额
                    BigDecimal depositAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getDepositAmount(),transferRate);
                    totalPage.addDepositAmount(depositAmountWtc);
                    //提款金额
                    BigDecimal withDrawAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getWithdrawAmount(),transferRate);
                    totalPage.addWithdrawAmount(withDrawAmountWtc);
                    //存提手续费
                    BigDecimal depositWithDrawFeeWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getDepositWithdrawFee(),transferRate);
                    totalPage.addDepositWithdrawFee(depositWithDrawFeeWtc);
                    //投注金额
                    BigDecimal betAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getBetAmount(),transferRate);
                    totalPage.addBetAmount(betAmountWtc);
                    //有效投注金额
                    BigDecimal validAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getValidAmount(),transferRate);
                    totalPage.addValidAmount(validAmountWtc);
                    //会员输赢
                    BigDecimal winLossAmountUserWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getWinLossAmountUser(),transferRate);
                    totalPage.addWinLossAmountUser(winLossAmountUserWtc);
                    //平台总输赢
                    BigDecimal winLossAmountPlatWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getWinLossAmountPlat(),transferRate);
                    totalPage.addWinLossAmountPlat(winLossAmountPlatWtc);
                    //已使用优惠金额
                    BigDecimal alreadyUseAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getAlreadyUseAmount(),transferRate);
                    totalPage.addAlreadyUseAmount(alreadyUseAmountWtc);
                    //打赏金额
                    BigDecimal tipsAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getTipsAmount(),transferRate);
                    totalPage.addTipsAmount(tipsAmountWtc);
                    //调整金额
                    BigDecimal adjustAmountWtc=AmountUtils.divide(reportAgentMerchantStaticDayPO.getAdjustAmount(),transferRate);
                    totalPage.addAdjustAmount(adjustAmountWtc);
                }
            }
        }
        reportAgentMerchantStaticsResult.setTotalPage(totalPage);
        return ResponseVO.success(reportAgentMerchantStaticsResult);
    }


    /**
     * 商务信息填充
     * @param reportAgentStaticsResponseVO 返回信息
     * @param agentMerchantVOS 商务信息
     */
    private void fillAgentMerchantInfo(ReportAgentMerchantStaticsResponseVO reportAgentStaticsResponseVO, List<AgentMerchantVO>  agentMerchantVOS, Map<String, RiskLevelDetailsVO> riskMap) {
        AgentMerchantVO agentMerchantVO=agentMerchantVOS.stream().filter(o->o.getMerchantAccount().equals(reportAgentStaticsResponseVO.getMerchantAccount())).findFirst().get();
        reportAgentStaticsResponseVO.setRegisterTime(agentMerchantVO.getRegisterTime());
        reportAgentStaticsResponseVO.setRiskLevelId(agentMerchantVO.getRiskId());
        // 风控层级
        if (null != agentMerchantVO.getRiskId()) {
            RiskLevelDetailsVO riskLevelDetailsVO = riskMap.get(agentMerchantVO.getRiskId());
            reportAgentStaticsResponseVO.setRiskLevel(null == riskLevelDetailsVO ? null : riskLevelDetailsVO.getRiskControlLevel());
        }


    }
}
