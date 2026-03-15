package com.cloud.baowang.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.*;
import com.cloud.baowang.agent.api.enums.AgentLevelEnum;
import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositSubordinatesPageReqVo;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferPageRecordVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageReqVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoCondVo;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithDrawReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawRespVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResponseVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.report.ReportAgentDepositWithdrawStaticType;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawResponseVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentDepositWithdrawResult;
import com.cloud.baowang.report.po.ReportAgentDepositWithdrawPO;
import com.cloud.baowang.report.po.ReportAgentDepositWithdrawUserPO;
import com.cloud.baowang.report.repositories.ReportAgentDepositWithdrawRepository;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.enums.TransferStatusEnum;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.wallet.api.enums.AgentManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
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
 * @Desciption: 代理充提报表service
 * @Author: Ford
 * @Date: 2024/11/5 11:23
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class ReportAgentDepositWithdrawService extends ServiceImpl<ReportAgentDepositWithdrawRepository, ReportAgentDepositWithdrawPO> {

    private ReportAgentDepositWithdrawRepository reportAgentDepositWithdrawRepository;

    private final AgentInfoApi agentInfoApi;

    private final AgentDepositWithdrawApi agentDepositWithdrawApi;

    private final AgentManualUpDownApi agentManualUpDownApi;

    private final AgentTransferApi agentTransferApi;

    private final AgentDepositSubordinatesApi agentDepositSubordinatesApi;

    private static final Integer PAGE_SIZE=1000;

    private final ReportAgentDepositWithdrawUserService reportAgentDepositWithdrawUserService;

    private final RiskApi riskApi;

    private final AgentLabelApi agentLabelApi;



    /**
     * 按照站点、天 统计代理报表
     * agent_deposit_withdrawal  代理存取
     * agent_manual_up_down_record 代理人工加减额 adjust_type 调整类型
     * agent_transfer_record 转账记录
     * agent_deposit_subordinates 代存
     *
     * @param reportAgentDepositWithdrawCondVO 统计条件
     * @return 统计成功 or 失败
     */
    public ResponseVO<Boolean> init(ReportAgentDepositWithdrawCondVO reportAgentDepositWithdrawCondVO) {
        if(!StringUtils.hasText(reportAgentDepositWithdrawCondVO.getSiteCode())|| !StringUtils.hasText(reportAgentDepositWithdrawCondVO.getTimeZoneId())){
            log.info("站点信息不存在:{}",reportAgentDepositWithdrawCondVO.getSiteCode());
            return ResponseVO.success(Boolean.FALSE);
        }
        LambdaQueryWrapper<ReportAgentDepositWithdrawPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        if(reportAgentDepositWithdrawCondVO.getStartDayMillis()!=null){
            lambdaQueryWrapper.ge(ReportAgentDepositWithdrawPO::getDayMillis,reportAgentDepositWithdrawCondVO.getStartDayMillis());
        }
        if(reportAgentDepositWithdrawCondVO.getEndDayMillis()!=null){
            lambdaQueryWrapper.le(ReportAgentDepositWithdrawPO::getDayMillis,reportAgentDepositWithdrawCondVO.getEndDayMillis());
        }
        if(StringUtils.hasText(reportAgentDepositWithdrawCondVO.getSiteCode())){
            lambdaQueryWrapper.eq(ReportAgentDepositWithdrawPO::getSiteCode,reportAgentDepositWithdrawCondVO.getSiteCode());
        }
        reportAgentDepositWithdrawRepository.delete(lambdaQueryWrapper);

        reportAgentDepositWithdrawUserService.delete(reportAgentDepositWithdrawCondVO);


        // 初始化代理自己存提数据
        initAgentSelf(reportAgentDepositWithdrawCondVO);

        // 初始化代理 人工加减额
        initAgentManualUpDown(reportAgentDepositWithdrawCondVO);

        // 初始化代理 转账记录
        initAgentTransfer(reportAgentDepositWithdrawCondVO);

        // 初始化代理 代存记录
        initAgentSubordinate(reportAgentDepositWithdrawCondVO);

        return ResponseVO.success(Boolean.TRUE);
    }

    /**
    *代理存取
     */
    private void initAgentSelf(ReportAgentDepositWithdrawCondVO reportAgentDepositWithdrawCondVO) {
        String siteCode=reportAgentDepositWithdrawCondVO.getSiteCode();
        String timeZoneId=reportAgentDepositWithdrawCondVO.getTimeZoneId();
        AgentDepositWithDrawReqVO vo=new AgentDepositWithDrawReqVO();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(PAGE_SIZE);
        vo.setStartTime(reportAgentDepositWithdrawCondVO.getStartDayMillis());
        vo.setEndTime(reportAgentDepositWithdrawCondVO.getEndDayMillis());
        vo.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        Page<AgentDepositWithdrawRespVO>  agentDepositWithdrawRespVOPage = agentDepositWithdrawApi.listPage(vo);
        for(int pageNum=1;pageNum<=agentDepositWithdrawRespVOPage.getPages();pageNum++){
            vo.setPageNumber(pageNum);
            agentDepositWithdrawRespVOPage = agentDepositWithdrawApi.listPage(vo);
            List<AgentDepositWithdrawRespVO>  agentDepositWithdrawRespVOS = agentDepositWithdrawRespVOPage.getRecords();
            for(AgentDepositWithdrawRespVO agentDepositWithdrawRespVO:agentDepositWithdrawRespVOS){
                ReportAgentDepositWithdrawPO reportAgentDepositWithdrawPO=new ReportAgentDepositWithdrawPO();
                reportAgentDepositWithdrawPO.setAgentId(agentDepositWithdrawRespVO.getAgentId());
                reportAgentDepositWithdrawPO.setAgentAccount(agentDepositWithdrawRespVO.getAgentAccount());
                reportAgentDepositWithdrawPO.setSiteCode(agentDepositWithdrawRespVO.getSiteCode());
                reportAgentDepositWithdrawPO.setTimeZoneId(timeZoneId);
                reportAgentDepositWithdrawPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                reportAgentDepositWithdrawPO.setDayMillis(DateUtils.getStartDayMillis(agentDepositWithdrawRespVO.getUpdatedTime(),timeZoneId));
                reportAgentDepositWithdrawPO.setReportDate(DateUtils.formatDateByZoneId(reportAgentDepositWithdrawPO.getDayMillis(),DateUtils.DATE_FORMAT_1,timeZoneId));
                reportAgentDepositWithdrawPO.setTotalDepositAmount(BigDecimal.ZERO);
                reportAgentDepositWithdrawPO.setTotalDepositNum(0L);
                reportAgentDepositWithdrawPO.setTotalWithdrawAmount(BigDecimal.ZERO);
                reportAgentDepositWithdrawPO.setTotalWithdrawNum(0L);
                boolean dataExists=false;
                if(AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode().equals(agentDepositWithdrawRespVO.getType())){
                    reportAgentDepositWithdrawPO.setTotalDepositNum(1L);
                    reportAgentDepositWithdrawPO.setTotalDepositAmount(agentDepositWithdrawRespVO.getArriveAmount());
                    dataExists=true;
                }
                if(AgentDepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode().equals(agentDepositWithdrawRespVO.getType())){
                    reportAgentDepositWithdrawPO.setTotalWithdrawNum(1L);
                    reportAgentDepositWithdrawPO.setTotalWithdrawAmount(agentDepositWithdrawRespVO.getArriveAmount());
                    dataExists=true;
                }
                reportAgentDepositWithdrawPO.setDiffDepositWithdraw(reportAgentDepositWithdrawPO.getTotalDepositAmount().subtract(reportAgentDepositWithdrawPO.getTotalWithdrawAmount()));
                //更细至统计报表
                if(dataExists){
                    log.info("代理自己存提数据:{}",reportAgentDepositWithdrawPO);
                    saveData(reportAgentDepositWithdrawPO);
                }

            }
        }
    }

    /**
     * 代理人工加减额
     * 对于人工加减额来说   代理存款(后台)-额度钱包\代理提款(后台)-佣金钱包   才统计到存款总额、提款总额里
     * @param reportAgentDepositWithdrawCondVO
     */
    private void initAgentManualUpDown(ReportAgentDepositWithdrawCondVO reportAgentDepositWithdrawCondVO) {
        String siteCode=reportAgentDepositWithdrawCondVO.getSiteCode();
        String timeZoneId=reportAgentDepositWithdrawCondVO.getTimeZoneId();
        AgentManualDownRequestVO vo=new AgentManualDownRequestVO();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setCreatorStartTime(reportAgentDepositWithdrawCondVO.getStartDayMillis());
        vo.setCreatorEndTime(reportAgentDepositWithdrawCondVO.getEndDayMillis());
        vo.setBalanceChangeStatus(BalanceChangeStatusEnum.SUCCESS.getStatus());
        Page<AgentManualUpRecordResponseVO>  agentManualUpRecordResponseVOPage = agentManualUpDownApi.listPage(vo);
        for(int pageNum=1;pageNum<=agentManualUpRecordResponseVOPage.getPages();pageNum++){
            vo.setPageNumber(pageNum);
            agentManualUpRecordResponseVOPage = agentManualUpDownApi.listPage(vo);
            List<AgentManualUpRecordResponseVO>  agentManualUpRecordResponseVOS = agentManualUpRecordResponseVOPage.getRecords();
            for(AgentManualUpRecordResponseVO agentManualUpRecordResponseVO:agentManualUpRecordResponseVOS){
                ReportAgentDepositWithdrawPO reportAgentDepositWithdrawPO=new ReportAgentDepositWithdrawPO();
                reportAgentDepositWithdrawPO.setAgentId(agentManualUpRecordResponseVO.getAgentId());
                reportAgentDepositWithdrawPO.setAgentAccount(agentManualUpRecordResponseVO.getAgentAccount());
                reportAgentDepositWithdrawPO.setSiteCode(siteCode);
                reportAgentDepositWithdrawPO.setTimeZoneId(timeZoneId);
                reportAgentDepositWithdrawPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                reportAgentDepositWithdrawPO.setDayMillis(DateUtils.getStartDayMillis(agentManualUpRecordResponseVO.getUpdatedTime(),timeZoneId));
                reportAgentDepositWithdrawPO.setReportDate(DateUtils.formatDateByZoneId(reportAgentDepositWithdrawPO.getDayMillis(),DateUtils.DATE_FORMAT_1,timeZoneId));
                reportAgentDepositWithdrawPO.setTotalDepositAmount(BigDecimal.ZERO);
                reportAgentDepositWithdrawPO.setTotalDepositNum(0L);
                reportAgentDepositWithdrawPO.setTotalWithdrawAmount(BigDecimal.ZERO);
                reportAgentDepositWithdrawPO.setTotalWithdrawNum(0L);
                reportAgentDepositWithdrawPO.setDiffDepositWithdraw(BigDecimal.ZERO);
                boolean dataExists=false;
                if(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode().equals(agentManualUpRecordResponseVO.getAdjustWay())){
                    if(AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode().equals(agentManualUpRecordResponseVO.getAdjustType().toString())){
                        reportAgentDepositWithdrawPO.setTotalDepositNum(1L);
                        reportAgentDepositWithdrawPO.setTotalDepositAmount(agentManualUpRecordResponseVO.getAdjustAmount());
                        dataExists=true;
                    }
                }
                if(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode().equals(agentManualUpRecordResponseVO.getAdjustWay())){
                    if(AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getCode().equals(agentManualUpRecordResponseVO.getAdjustType().toString())){
                        reportAgentDepositWithdrawPO.setTotalWithdrawNum(1L);
                        reportAgentDepositWithdrawPO.setTotalWithdrawAmount(agentManualUpRecordResponseVO.getAdjustAmount());
                        dataExists=true;
                    }
                }
                reportAgentDepositWithdrawPO.setDiffDepositWithdraw(reportAgentDepositWithdrawPO.getTotalDepositAmount().subtract(reportAgentDepositWithdrawPO.getTotalWithdrawAmount()));
                //更细至统计报表
                if(dataExists){
                    log.info("代理人工加减额数据:{},来源订单号:{}",reportAgentDepositWithdrawPO,agentManualUpRecordResponseVO.getOrderNo());
                    saveData(reportAgentDepositWithdrawPO);
                }
            }
        }
    }

    /**
     * 转账记录
     * @param reportAgentDepositWithdrawCondVO 参数
     */
    private void initAgentTransfer(ReportAgentDepositWithdrawCondVO reportAgentDepositWithdrawCondVO) {
        String siteCode=reportAgentDepositWithdrawCondVO.getSiteCode();
        String timeZoneId=reportAgentDepositWithdrawCondVO.getTimeZoneId();
        AgentTransferRecordPageReqVO vo=new AgentTransferRecordPageReqVO();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setStartTransferTime(reportAgentDepositWithdrawCondVO.getStartDayMillis());
        vo.setEndTransferTime(reportAgentDepositWithdrawCondVO.getEndDayMillis());
        vo.setTransferStatus(TransferStatusEnum.SUCCESS.getCode());
        Page<AgentTransferPageRecordVO>  agentTransferPageRecordVOPage = agentTransferApi.listPage(vo);
        for(int pageNum=1;pageNum<=agentTransferPageRecordVOPage.getPages();pageNum++){
            vo.setPageNumber(pageNum);
            agentTransferPageRecordVOPage = agentTransferApi.listPage(vo);
            List<AgentTransferPageRecordVO>  agentTransferPageRecordVOS = agentTransferPageRecordVOPage.getRecords();
            Map<String,Set<String>> userMap=new HashMap<>();
            for(AgentTransferPageRecordVO agentTransferPageRecordVO:agentTransferPageRecordVOS){
                ReportAgentDepositWithdrawPO reportAgentDepositWithdrawPO=new ReportAgentDepositWithdrawPO();
                reportAgentDepositWithdrawPO.setAgentId(agentTransferPageRecordVO.getAgentId());
                reportAgentDepositWithdrawPO.setAgentAccount(agentTransferPageRecordVO.getAgentAccount());
                reportAgentDepositWithdrawPO.setSiteCode(siteCode);
                reportAgentDepositWithdrawPO.setTimeZoneId(timeZoneId);
                reportAgentDepositWithdrawPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                reportAgentDepositWithdrawPO.setDayMillis(DateUtils.getStartDayMillis(agentTransferPageRecordVO.getTransferTime(),timeZoneId));
                reportAgentDepositWithdrawPO.setReportDate(DateUtils.formatDateByZoneId(reportAgentDepositWithdrawPO.getDayMillis(),DateUtils.DATE_FORMAT_1,timeZoneId));
                reportAgentDepositWithdrawPO.setTotalDepositAmount(BigDecimal.ZERO);
                reportAgentDepositWithdrawPO.setTotalDepositNum(0L);
                reportAgentDepositWithdrawPO.setTotalWithdrawAmount(BigDecimal.ZERO);
                reportAgentDepositWithdrawPO.setTotalWithdrawNum(0L);

                String  reportDate=reportAgentDepositWithdrawPO.getReportDate();
                Set<String> userSet=new HashSet<String>();
                String mapKey=reportDate.concat(reportAgentDepositWithdrawPO.getAgentId());
                if(userMap.containsKey(mapKey)){
                    userSet=userMap.get(mapKey);
                    userSet.add(agentTransferPageRecordVO.getTransferAgentId());
                }else {
                    userSet.add(agentTransferPageRecordVO.getTransferAgentId());
                }
                userMap.put(mapKey,userSet);

                reportAgentDepositWithdrawPO.setAgentTransferCount(1L);
                reportAgentDepositWithdrawPO.setAgentTransferAmount(agentTransferPageRecordVO.getTransferAmount());
                // 代理转账人数  需要去除重复处理
                reportAgentDepositWithdrawPO.setAgentTransferUser(Long.valueOf(userSet.size()));
                //更细至统计报表
                log.info("转账记录:{}",reportAgentDepositWithdrawPO);
                saveData(reportAgentDepositWithdrawPO);

                ReportAgentDepositWithdrawUserPO reportAgentDepositWithdrawUserPO=new ReportAgentDepositWithdrawUserPO();
                reportAgentDepositWithdrawUserPO.setAgentId(reportAgentDepositWithdrawPO.getAgentId());
                reportAgentDepositWithdrawUserPO.setSiteCode(siteCode);
                reportAgentDepositWithdrawUserPO.setDayMillis(reportAgentDepositWithdrawPO.getDayMillis());
                reportAgentDepositWithdrawUserPO.setReportDate(reportAgentDepositWithdrawPO.getReportDate());
                reportAgentDepositWithdrawUserPO.setAgentAccount(reportAgentDepositWithdrawPO.getAgentAccount());
                reportAgentDepositWithdrawUserPO.setStaticType(ReportAgentDepositWithdrawStaticType.TRANSFER.getType());
                reportAgentDepositWithdrawUserPO.setStaticUserId(agentTransferPageRecordVO.getTransferAgentId());
                saveStaticUser(reportAgentDepositWithdrawUserPO);
            }
        }
    }




    /**
     * 代理代存记录
     * @param reportAgentDepositWithdrawCondVO 参数
     */
    private void initAgentSubordinate(ReportAgentDepositWithdrawCondVO reportAgentDepositWithdrawCondVO) {
        String siteCode=reportAgentDepositWithdrawCondVO.getSiteCode();
        String timeZoneId=reportAgentDepositWithdrawCondVO.getTimeZoneId();
        AgentDepositSubordinatesPageReqVo vo=new AgentDepositSubordinatesPageReqVo();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setStartTime(reportAgentDepositWithdrawCondVO.getStartDayMillis());
        vo.setEndTime(reportAgentDepositWithdrawCondVO.getEndDayMillis());
        vo.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        Page<AgentDepositOfSubordinatesResVO>  agentDepositOfSubordinatesResVOPage = agentDepositSubordinatesApi.listPage(vo);
        Map<String,Set<String>> userMap=new HashMap<>();
        for(int pageNum=1;pageNum<=agentDepositOfSubordinatesResVOPage.getPages();pageNum++){
            vo.setPageNumber(pageNum);
            agentDepositOfSubordinatesResVOPage = agentDepositSubordinatesApi.listPage(vo);
            List<AgentDepositOfSubordinatesResVO>  agentTransferPageRecordVOS = agentDepositOfSubordinatesResVOPage.getRecords();
            for(AgentDepositOfSubordinatesResVO agentDepositOfSubordinatesResVO:agentTransferPageRecordVOS){
                ReportAgentDepositWithdrawPO reportAgentDepositWithdrawPO=new ReportAgentDepositWithdrawPO();
                reportAgentDepositWithdrawPO.setAgentId(agentDepositOfSubordinatesResVO.getAgentId());
                reportAgentDepositWithdrawPO.setAgentAccount(agentDepositOfSubordinatesResVO.getAgentAccount());
                reportAgentDepositWithdrawPO.setSiteCode(siteCode);
                reportAgentDepositWithdrawPO.setTimeZoneId(timeZoneId);
                reportAgentDepositWithdrawPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                reportAgentDepositWithdrawPO.setDayMillis(DateUtils.getStartDayMillis(agentDepositOfSubordinatesResVO.getDepositTime(),timeZoneId));
                reportAgentDepositWithdrawPO.setReportDate(DateUtils.formatDateByZoneId(reportAgentDepositWithdrawPO.getDayMillis(),DateUtils.DATE_FORMAT_1,timeZoneId));
                reportAgentDepositWithdrawPO.setTotalDepositAmount(BigDecimal.ZERO);
                reportAgentDepositWithdrawPO.setTotalDepositNum(0L);
                reportAgentDepositWithdrawPO.setTotalWithdrawAmount(agentDepositOfSubordinatesResVO.getPlatformAmount());
                reportAgentDepositWithdrawPO.setTotalWithdrawNum(1L);
                String reportDate=reportAgentDepositWithdrawPO.getReportDate();
                Set<String> userSet=new HashSet<String>();
                String mapKey=reportDate.concat(reportAgentDepositWithdrawPO.getAgentId());
                if(userMap.containsKey(mapKey)){
                    userSet=userMap.get(mapKey);
                    userSet.add(agentDepositOfSubordinatesResVO.getUserId());
                }else {
                    userSet.add(agentDepositOfSubordinatesResVO.getUserId());
                }
                userMap.put(mapKey,userSet);

                reportAgentDepositWithdrawPO.setAgentSubordinatesCount(1L);
                reportAgentDepositWithdrawPO.setAgentSubordinatesAmount(agentDepositOfSubordinatesResVO.getPlatformAmount());
                // 代理代存人数  需要去除重复处理
                reportAgentDepositWithdrawPO.setAgentSubordinatesUser(Long.valueOf(userSet.size()));
                //更细至统计报表
                log.info("代理代存记录:{}",reportAgentDepositWithdrawPO);
                saveData(reportAgentDepositWithdrawPO);

                ReportAgentDepositWithdrawUserPO reportAgentDepositWithdrawUserPO=new ReportAgentDepositWithdrawUserPO();
                reportAgentDepositWithdrawUserPO.setAgentId(reportAgentDepositWithdrawPO.getAgentId());
                reportAgentDepositWithdrawUserPO.setAgentAccount(reportAgentDepositWithdrawPO.getAgentAccount());
                reportAgentDepositWithdrawUserPO.setSiteCode(siteCode);
                reportAgentDepositWithdrawUserPO.setDayMillis(reportAgentDepositWithdrawPO.getDayMillis());
                reportAgentDepositWithdrawUserPO.setReportDate(reportAgentDepositWithdrawPO.getReportDate());
                reportAgentDepositWithdrawUserPO.setAgentAccount(reportAgentDepositWithdrawPO.getAgentAccount());
                reportAgentDepositWithdrawUserPO.setStaticType(ReportAgentDepositWithdrawStaticType.SUBORD.getType());
                reportAgentDepositWithdrawUserPO.setStaticUserId(agentDepositOfSubordinatesResVO.getUserId());
                saveStaticUser(reportAgentDepositWithdrawUserPO);
            }
        }
    }



    /**
     * 保存至数据库
     * @param reportAgentDepositWithdrawPO 原始数据
     */
    private void saveData(ReportAgentDepositWithdrawPO reportAgentDepositWithdrawPO) {
        //开始更新统计信息
        LambdaQueryWrapper<ReportAgentDepositWithdrawPO> lambdaQueryWrapper=new LambdaQueryWrapper<ReportAgentDepositWithdrawPO>();
        lambdaQueryWrapper.eq(ReportAgentDepositWithdrawPO::getAgentId,reportAgentDepositWithdrawPO.getAgentId());
        lambdaQueryWrapper.eq(ReportAgentDepositWithdrawPO::getDayMillis,reportAgentDepositWithdrawPO.getDayMillis());
        ReportAgentDepositWithdrawPO reportAgentDepositWithdrawPODb=reportAgentDepositWithdrawRepository.selectOne(lambdaQueryWrapper);
        if(reportAgentDepositWithdrawPODb!=null){
            //更新或累加数据
            reportAgentDepositWithdrawPO.setId(reportAgentDepositWithdrawPODb.getId());
            reportAgentDepositWithdrawRepository.addSumData(reportAgentDepositWithdrawPO);
        }else {
            // 初始化数据
            reportAgentDepositWithdrawPO.setCreatedTime(System.currentTimeMillis());
            reportAgentDepositWithdrawPO.setUpdatedTime(System.currentTimeMillis());
            reportAgentDepositWithdrawRepository.insert(reportAgentDepositWithdrawPO);
        }
    }

    /**
     * 保存代理 存取 用户相关
     * @param reportAgentDepositWithdrawUserPO 参数
     */
    private void saveStaticUser(ReportAgentDepositWithdrawUserPO reportAgentDepositWithdrawUserPO) {
        //开始更新统计信息
        // 初始化数据
        reportAgentDepositWithdrawUserPO.setCreatedTime(System.currentTimeMillis());
        reportAgentDepositWithdrawUserPO.setUpdatedTime(System.currentTimeMillis());
        reportAgentDepositWithdrawUserService.saveData(reportAgentDepositWithdrawUserPO);
    }

    /**
     * 分页查询
     * @param reportAgentDepositWithdrawPageVO 查询条件
     * @return
     */
    public ResponseVO<ReportAgentDepositWithdrawResult> listPage(ReportAgentDepositWithdrawPageVO reportAgentDepositWithdrawPageVO) {
        //过滤代理信息
        AgentInfoCondVo agentInfoCondVo=new AgentInfoCondVo();
        BeanUtils.copyProperties(reportAgentDepositWithdrawPageVO,agentInfoCondVo);
        List<AgentInfoVO> agentInfoVOS=agentInfoApi.getAgentListByCond(agentInfoCondVo);
        ReportAgentDepositWithdrawResult reportAgentDepositWithdrawResult=new ReportAgentDepositWithdrawResult();
        if(CollectionUtils.isEmpty(agentInfoVOS)){
            log.info("按照注册日期查询不到代理:{}",reportAgentDepositWithdrawPageVO);
            reportAgentDepositWithdrawResult.setPageList(new Page<ReportAgentDepositWithdrawResponseVO>(reportAgentDepositWithdrawPageVO.getPageNumber(),reportAgentDepositWithdrawPageVO.getPageSize()));
            reportAgentDepositWithdrawResult.setCurrentPage(new ReportAgentDepositWithdrawResponseVO());
            reportAgentDepositWithdrawResult.setTotalPage(new ReportAgentDepositWithdrawResponseVO());
            return ResponseVO.success(reportAgentDepositWithdrawResult);
        }


        Page<ReportAgentDepositWithdrawPO> page =new Page<ReportAgentDepositWithdrawPO>(reportAgentDepositWithdrawPageVO.getPageNumber(),reportAgentDepositWithdrawPageVO.getPageSize());
        LambdaQueryWrapper<ReportAgentDepositWithdrawPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportAgentDepositWithdrawPO::getSiteCode,reportAgentDepositWithdrawPageVO.getSiteCode());
        //代理信息查询
        if(!CollectionUtils.isEmpty(agentInfoVOS)){
            List<String> agentIds=agentInfoVOS.stream().map(AgentInfoVO::getAgentId).toList();
            lambdaQueryWrapper.in(ReportAgentDepositWithdrawPO::getAgentId,agentIds);
        }

        if(reportAgentDepositWithdrawPageVO.getStartStaticDay()!=null){
            lambdaQueryWrapper.ge(ReportAgentDepositWithdrawPO::getDayMillis,reportAgentDepositWithdrawPageVO.getStartStaticDay());
        }
        if(reportAgentDepositWithdrawPageVO.getEndStaticDay()!=null){
            lambdaQueryWrapper.le(ReportAgentDepositWithdrawPO::getDayMillis,reportAgentDepositWithdrawPageVO.getEndStaticDay());
        }

        if(reportAgentDepositWithdrawPageVO.getMinDepositAmount()!=null){
            lambdaQueryWrapper.ge(ReportAgentDepositWithdrawPO::getTotalDepositAmount,reportAgentDepositWithdrawPageVO.getMinDepositAmount());
        }
        if(reportAgentDepositWithdrawPageVO.getMaxDepositAmount()!=null){
            lambdaQueryWrapper.le(ReportAgentDepositWithdrawPO::getTotalDepositAmount,reportAgentDepositWithdrawPageVO.getMaxDepositAmount());
        }

        if(reportAgentDepositWithdrawPageVO.getMinWithdrawAmount()!=null){
            lambdaQueryWrapper.ge(ReportAgentDepositWithdrawPO::getTotalWithdrawAmount,reportAgentDepositWithdrawPageVO.getMinWithdrawAmount());
        }

        if(reportAgentDepositWithdrawPageVO.getMaxWithdrawAmount()!=null){
            lambdaQueryWrapper.le(ReportAgentDepositWithdrawPO::getTotalWithdrawAmount,reportAgentDepositWithdrawPageVO.getMaxWithdrawAmount());
        }

        if (StringUtils.hasText(reportAgentDepositWithdrawPageVO.getOrderField()) && StringUtils.hasText(reportAgentDepositWithdrawPageVO.getOrderType())) {
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("totalDepositAmount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getTotalDepositAmount);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("totalDepositAmount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getTotalDepositAmount);
            }


            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("totalDepositNum") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getTotalDepositNum);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("totalDepositNum") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getTotalDepositNum);
            }

            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("totalWithdrawAmount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getTotalWithdrawAmount);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("totalWithdrawAmount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getTotalWithdrawAmount);
            }

            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("totalWithdrawNum") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getTotalWithdrawNum);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("totalWithdrawNum") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getTotalWithdrawNum);
            }

            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("diffDepositWithdraw") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getDiffDepositWithdraw);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("diffDepositWithdraw") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getDiffDepositWithdraw);
            }

            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentSubordinatesAmount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getAgentSubordinatesAmount);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentSubordinatesAmount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getAgentSubordinatesAmount);
            }

            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentSubordinatesCount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getAgentSubordinatesCount);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentSubordinatesCount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getAgentSubordinatesCount);
            }

            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentSubordinatesUser") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getAgentSubordinatesUser);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentSubordinatesUser") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getAgentSubordinatesUser);
            }

            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentTransferAmount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getAgentTransferAmount);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentTransferAmount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getAgentTransferAmount);
            }

            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentTransferCount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getAgentTransferCount);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentTransferCount") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getAgentTransferCount);
            }

            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentTransferUser") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentDepositWithdrawPO::getAgentTransferUser);
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("agentTransferUser") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getAgentTransferUser);
            }


        }else {
            lambdaQueryWrapper.orderByDesc(ReportAgentDepositWithdrawPO::getDayMillis);
        }


        Page<ReportAgentDepositWithdrawPO> reportAgentDepositWithdrawPOPage=this.baseMapper.selectPage(page,lambdaQueryWrapper);
        List<ReportAgentDepositWithdrawResponseVO> reportAgentDepositWithdrawResponseVOS=Lists.newArrayList();
        Page<ReportAgentDepositWithdrawResponseVO> reportAgentDepositWithdrawResponseVOPage=new Page<ReportAgentDepositWithdrawResponseVO>();
        BeanUtils.copyProperties(reportAgentDepositWithdrawPOPage,reportAgentDepositWithdrawResponseVOPage);
        ReportAgentDepositWithdrawResponseVO currentPage=new ReportAgentDepositWithdrawResponseVO();
        if(!CollectionUtils.isEmpty(reportAgentDepositWithdrawPOPage.getRecords())){
            List<String> agentIds=reportAgentDepositWithdrawPOPage.getRecords().stream().map(ReportAgentDepositWithdrawPO::getAgentId).toList();
            List<AgentInfoVO>  agentInfoVOList=agentInfoApi.getByAgentIds(agentIds);
            List<String> parentAgentIds=agentInfoVOList.stream().map(AgentInfoVO::getParentId).toList();
            List<AgentInfoVO>  parentAgentInfoVos=agentInfoApi.getByAgentIds(parentAgentIds);

            List<String> riskLevelIds = agentInfoVOList.stream().map(o -> o.getRiskLevelId()).toList();
            Map<String, RiskLevelDetailsVO> riskMap = riskApi.getByIds(riskLevelIds);
            List<String> agentLabelIds = Lists.newArrayList();
            agentInfoVOList.forEach(o -> {
                if(StringUtils.hasText(o.getAgentLabelId())){
                    agentLabelIds.addAll(Arrays.stream(o.getAgentLabelId().split(CommonConstant.COMMA)).toList());
                }
            });
            List<AgentLabelVO> agentLabelVOS=agentLabelApi.getAgentLabelByAgentLabelIds(agentLabelIds);
            for(ReportAgentDepositWithdrawPO reportAgentDepositWithdrawPO:reportAgentDepositWithdrawPOPage.getRecords()){
                ReportAgentDepositWithdrawResponseVO reportAgentDepositWithdrawResponseVO=new ReportAgentDepositWithdrawResponseVO();
                BeanUtils.copyProperties(reportAgentDepositWithdrawPO,reportAgentDepositWithdrawResponseVO);
                //代理信息填充
                fillAgentInfo(reportAgentDepositWithdrawResponseVO,agentInfoVOList,parentAgentInfoVos,riskMap,agentLabelVOS);

                currentPage.addTotalDepositAmount(reportAgentDepositWithdrawPO.getTotalDepositAmount());
                currentPage.addTotalDepositNum(reportAgentDepositWithdrawPO.getTotalDepositNum());
                currentPage.addTotalWithdrawAmount(reportAgentDepositWithdrawPO.getTotalWithdrawAmount());
                currentPage.addTotalWithdrawNum(reportAgentDepositWithdrawPO.getTotalWithdrawNum());
                currentPage.addDiffDepositWithdraw(reportAgentDepositWithdrawPO.getDiffDepositWithdraw());

                currentPage.addAgentTransferAmount(reportAgentDepositWithdrawPO.getAgentTransferAmount());
                //代存会员人数
                currentPage.setAgentTransferUser(null);
                currentPage.addAgentTransferCount(reportAgentDepositWithdrawPO.getAgentTransferCount());

                currentPage.addAgentSubordinatesAmount(reportAgentDepositWithdrawPO.getAgentSubordinatesAmount());
                currentPage.addAgentSubordinatesCount(reportAgentDepositWithdrawPO.getAgentSubordinatesCount());
                //代理转账人数
                currentPage.setAgentSubordinatesUser(null);

                reportAgentDepositWithdrawResponseVOS.add(reportAgentDepositWithdrawResponseVO);
            }
        }

        if (StringUtils.hasText(reportAgentDepositWithdrawPageVO.getOrderField()) && StringUtils.hasText(reportAgentDepositWithdrawPageVO.getOrderType())) {
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("registerTime") && reportAgentDepositWithdrawPageVO.getOrderType().equals("asc")) {
                reportAgentDepositWithdrawResponseVOS=reportAgentDepositWithdrawResponseVOS.stream().sorted(Comparator.comparingLong(ReportAgentDepositWithdrawResponseVO::getRegisterTime)).toList();
            }
            if (reportAgentDepositWithdrawPageVO.getOrderField().equals("registerTime") && reportAgentDepositWithdrawPageVO.getOrderType().equals("desc")) {
                reportAgentDepositWithdrawResponseVOS=reportAgentDepositWithdrawResponseVOS
                        .stream()
                        .sorted(Comparator.comparingLong(ReportAgentDepositWithdrawResponseVO::getRegisterTime).reversed())
                        .toList();
            }
        }

        reportAgentDepositWithdrawResponseVOPage.setRecords(reportAgentDepositWithdrawResponseVOS);

        reportAgentDepositWithdrawResult.setPageList(reportAgentDepositWithdrawResponseVOPage);
        reportAgentDepositWithdrawResult.setCurrentPage(currentPage);
        // 汇总
        ReportAgentDepositWithdrawResponseVO totalPage=new ReportAgentDepositWithdrawResponseVO();
        ReportAgentDepositWithdrawPO reportAgentDepositWithdrawPOTotal=this.baseMapper.selectTotal(lambdaQueryWrapper);
        if(reportAgentDepositWithdrawPOTotal!=null){
            BeanUtils.copyProperties(reportAgentDepositWithdrawPOTotal,totalPage);
        }
        //人数去重复
        LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO> lambdaQueryUserWrapper=new LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO>();
        LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO> lambdaQuerySubUserWrapper=new LambdaQueryWrapper<ReportAgentDepositWithdrawUserPO>();
        lambdaQueryUserWrapper.eq(ReportAgentDepositWithdrawUserPO::getSiteCode,reportAgentDepositWithdrawPageVO.getSiteCode());
        //代理信息查询
        if(!CollectionUtils.isEmpty(reportAgentDepositWithdrawResponseVOS)){
            //列表不为空,查询代存,转账信息
            List<String> agentIds=reportAgentDepositWithdrawResponseVOS.stream().map(ReportAgentDepositWithdrawResponseVO::getAgentId).toList();
            List<Long> dayMills=reportAgentDepositWithdrawResponseVOS.stream().map(ReportAgentDepositWithdrawResponseVO::getDayMillis).toList();
            //代存人数统计
            lambdaQueryUserWrapper.in(ReportAgentDepositWithdrawUserPO::getAgentId,agentIds);
            lambdaQueryUserWrapper.in(ReportAgentDepositWithdrawUserPO::getDayMillis,dayMills);
            lambdaQueryUserWrapper.eq(ReportAgentDepositWithdrawUserPO::getStaticType,ReportAgentDepositWithdrawStaticType.SUBORD.getType());
            Long subordUserCount=reportAgentDepositWithdrawUserService.selectUserCount(lambdaQueryUserWrapper);
            totalPage.setAgentSubordinatesUser(subordUserCount);
            //代理转账人数统计
            lambdaQuerySubUserWrapper.in(ReportAgentDepositWithdrawUserPO::getAgentId,agentIds);
            lambdaQuerySubUserWrapper.in(ReportAgentDepositWithdrawUserPO::getDayMillis,dayMills);
            lambdaQuerySubUserWrapper.eq(ReportAgentDepositWithdrawUserPO::getStaticType,ReportAgentDepositWithdrawStaticType.TRANSFER.getType());
            Long agentTransferUserCount=reportAgentDepositWithdrawUserService.selectUserCount(lambdaQuerySubUserWrapper);
            totalPage.setAgentTransferUser(agentTransferUserCount);
        }
        reportAgentDepositWithdrawResult.setTotalPage(totalPage);
        return ResponseVO.success(reportAgentDepositWithdrawResult);
    }


    /**
     * 代理信息填充
     * @param reportAgentDepositWithdrawResponseVO 返回信息
     * @param agentInfoVOList 代理信息
     */
    private void fillAgentInfo(ReportAgentDepositWithdrawResponseVO reportAgentDepositWithdrawResponseVO, List<AgentInfoVO> agentInfoVOList, List<AgentInfoVO>  parentAgentInfoVos, Map<String, RiskLevelDetailsVO> riskMap, List<AgentLabelVO> agentLabelVOS) {
        AgentInfoVO agentInfoVO=agentInfoVOList.stream().filter(o->o.getAgentId().equals(reportAgentDepositWithdrawResponseVO.getAgentId())).findFirst().get();
        reportAgentDepositWithdrawResponseVO.setAgentAccount(agentInfoVO.getAgentAccount());
        //上级代理
        if(StringUtils.hasText(agentInfoVO.getParentId())){
            AgentInfoVO parentAgentInfoVO=parentAgentInfoVos.stream().filter(o->o.getAgentId().equals(agentInfoVO.getParentId())).findFirst().get();
            reportAgentDepositWithdrawResponseVO.setParentId(parentAgentInfoVO.getParentId());
            reportAgentDepositWithdrawResponseVO.setParentAccount(parentAgentInfoVO.getAgentAccount());
        }
        //代理信息填充
        reportAgentDepositWithdrawResponseVO.setPath(agentInfoVO.getPath());
        reportAgentDepositWithdrawResponseVO.setLevel(agentInfoVO.getLevel());
        // 层级名称
        if (null != agentInfoVO.getLevel()) {
            AgentLevelEnum agentLevelEnum = AgentLevelEnum.nameOfCode(agentInfoVO.getLevel());
            if (null != agentLevelEnum) {
                reportAgentDepositWithdrawResponseVO.setLevelName(agentLevelEnum.getName());
            }
        }
        reportAgentDepositWithdrawResponseVO.setAgentType(agentInfoVO.getAgentType());
        reportAgentDepositWithdrawResponseVO.setAgentCategory(agentInfoVO.getAgentCategory());
        reportAgentDepositWithdrawResponseVO.setRegisterTime(agentInfoVO.getRegisterTime());
        reportAgentDepositWithdrawResponseVO.setAgentLabelId(agentInfoVO.getAgentLabelId());
        reportAgentDepositWithdrawResponseVO.setRiskLevelId(agentInfoVO.getRiskLevelId());

        // 风控层级
        if (null != agentInfoVO.getRiskLevelId()) {
            RiskLevelDetailsVO riskLevelDetailsVO = riskMap.get(agentInfoVO.getRiskLevelId());
            reportAgentDepositWithdrawResponseVO.setRiskLevel(null == riskLevelDetailsVO ? null : riskLevelDetailsVO.getRiskControlLevel());
        }

        // 代理标签
        if (null != agentInfoVO.getAgentLabelId()) {
            List<AgentLabelVO> agentLabelPOList = agentLabelVOS.stream().filter(o -> agentInfoVO.getAgentLabelId().contains(o.getId())).toList();
            if (!CollectionUtils.isEmpty(agentLabelPOList)) {
                List<String> agentLabelNames = agentLabelPOList.stream().map(o -> o.getName()).toList();
                String labelNames = String.join(CommonConstant.COMMA, agentLabelNames);
                reportAgentDepositWithdrawResponseVO.setAgentLabel(labelNames);
            }
        }
    }
}
