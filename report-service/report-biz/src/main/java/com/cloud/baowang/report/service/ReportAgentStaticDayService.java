package com.cloud.baowang.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentDepositSubordinatesApi;
import com.cloud.baowang.agent.api.api.AgentDepositWithdrawApi;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.AgentLabelApi;
import com.cloud.baowang.agent.api.api.AgentManualUpDownApi;
import com.cloud.baowang.agent.api.enums.AgentLevelEnum;
import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositSubordinatesPageReqVo;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentInfoCondVo;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithDrawReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawRespVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualDownRequestVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpRecordResponseVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.user.api.vo.UserInfoResponseVO;
import com.cloud.baowang.wallet.api.enums.AgentManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.ManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.balanceChange.BalanceChangeStatusEnum;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.NumberUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.report.api.api.UserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsCondVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsPageVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsResponseVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentStaticsResult;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListPageCondVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListResponseVO;
import com.cloud.baowang.report.po.ReportAgentStaticBetPO;
import com.cloud.baowang.report.po.ReportAgentStaticDayPO;
import com.cloud.baowang.report.repositories.ReportAgentStaticDayRepository;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserInfoPageVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserDepositWithdrawApi;
import com.cloud.baowang.wallet.api.api.UserManualUpDownApi;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @Desciption: 代理报表service
 * @Author: Ford
 * @Date: 2024/11/5 11:23
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class ReportAgentStaticDayService extends ServiceImpl<ReportAgentStaticDayRepository, ReportAgentStaticDayPO> {

    private final AgentInfoApi agentInfoApi;

    private final UserWinLoseApi userWinLoseApi;

    private final UserInfoApi userInfoApi;

    //private final AgentCommissionReviewRecordApi agentCommissionReviewRecordApi;

    private final  ReportAgentStaticBetService reportAgentStaticBetService;

    private final SiteApi siteApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final RiskApi riskApi;

    private final AgentLabelApi agentLabelApi;

    private final UserDepositWithdrawApi userDepositWithdrawApi;

    private final AgentDepositWithdrawApi agentDepositWithdrawApi;

    private final UserManualUpDownApi userManualUpDownApi;

    private final AgentDepositSubordinatesApi agentDepositSubordinatesApi;

    private final AgentManualUpDownApi agentManualUpDownApi;


    /**
     * 按照站点、天 统计代理报表
     * @param reportAgentStaticsCondVO 统计条件
     * @return 统计成功 or 失败
     */
    public ResponseVO<Boolean> init(ReportAgentStaticsCondVO reportAgentStaticsCondVO) {
        String siteCode=reportAgentStaticsCondVO.getSiteCode();
        String reportType=reportAgentStaticsCondVO.getReportType();
        String timeZone=reportAgentStaticsCondVO.getTimeZone();
        if(!StringUtils.hasText(siteCode)){
            log.info("站点为空:{}",reportAgentStaticsCondVO);
            return ResponseVO.success(Boolean.TRUE);
        }
        if(!StringUtils.hasText(reportAgentStaticsCondVO.getTimeZone())){
            log.info("站点时区为空:{}",reportAgentStaticsCondVO);
            return ResponseVO.success(Boolean.TRUE);
        }

        UserWinLoseListPageCondVO vo =new UserWinLoseListPageCondVO();
        vo.setSiteCode(siteCode);
        vo.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        vo.setStartDayMillis(reportAgentStaticsCondVO.getStartDayMillis());
        vo.setEndDayMillis(reportAgentStaticsCondVO.getEndDayMillis());
        vo.setPageNumber(1);
        vo.setPageSize(500);
        ResponseVO<Page<UserWinLoseListResponseVO>> userWinLosePageResp=userWinLoseApi.listPage(vo);
        if(!userWinLosePageResp.isOk()){
            log.info("根据条件:{}没有查询到会员盈亏数据",vo);
            return ResponseVO.success(Boolean.TRUE);
        }

       // Map<String,BigDecimal> rateMap=siteCurrencyInfoApi.getAllFinalRate(siteCode);
        //删除当前站点 历史统计数据 重新计算
        LambdaQueryWrapper<ReportAgentStaticDayPO> reportAgentStaticDayPOLambdaQueryWrapper=new LambdaQueryWrapper<>();
        reportAgentStaticDayPOLambdaQueryWrapper.eq(ReportAgentStaticDayPO::getSiteCode,siteCode);
        reportAgentStaticDayPOLambdaQueryWrapper.eq(ReportAgentStaticDayPO::getReportType,reportAgentStaticsCondVO.getReportType());
        if(vo.getStartDayMillis()!=null){
            reportAgentStaticDayPOLambdaQueryWrapper.ge(ReportAgentStaticDayPO::getDayMillis,vo.getStartDayMillis());
        }
        if(vo.getEndDayMillis()!=null){
            reportAgentStaticDayPOLambdaQueryWrapper.le(ReportAgentStaticDayPO::getDayMillis,vo.getEndDayMillis());
        }
        this.baseMapper.delete(reportAgentStaticDayPOLambdaQueryWrapper);
        //删除当前站点 历史投注人数 重新计算
        reportAgentStaticBetService.delete(reportAgentStaticsCondVO);

        //统计注册人数 首存人数
         fillRegisterNum(reportAgentStaticsCondVO);

        //统计投注人数 key=agentId value是用户Id集合
        Map<String, Set<String>> betUserMap=new HashMap<String, Set<String>>();

        //统计代理所属团队 会员盈亏
        Page<UserWinLoseListResponseVO> userWinLoseResponseVOPage=userWinLosePageResp.getData();
        long totalPageNum=userWinLoseResponseVOPage.getPages();
        log.info("站点:{},总页数:{},参数:{}",siteCode,totalPageNum,vo);
        for(int pageNum=1;pageNum<=totalPageNum;pageNum++){
            vo.setPageNumber(pageNum);
            log.info("站点:{},开始处理第:{}页会员盈亏数据",siteCode,pageNum);
            ResponseVO<Page<UserWinLoseListResponseVO>> userWinLosePageRespForEach=userWinLoseApi.listPage(vo);
            //更新金额 新增or更新
            batchProcess(userWinLosePageRespForEach.getData().getRecords(),betUserMap,reportAgentStaticsCondVO.getReportType(),timeZone);
            log.info("站点:{},处理结束第:{}页会员盈亏数据",siteCode,pageNum);
        }
       // 存储代理关联的会员 用于统计投注人数
        for(String mapKey:betUserMap.keySet()){
            Set<String> userSet = betUserMap.get(mapKey);
            String[] mapKeyArray=mapKey.split("#");
            ReportAgentStaticBetPO reportAgentStaticBetPO=new ReportAgentStaticBetPO();
            reportAgentStaticBetPO.setReportType(reportType);
            reportAgentStaticBetPO.setSiteCode(mapKeyArray[0]);
            reportAgentStaticBetPO.setAgentId(mapKeyArray[1]);
            reportAgentStaticBetPO.setCurrencyCode(mapKeyArray[2]);
            reportAgentStaticBetPO.setDayMillis(Long.valueOf(mapKeyArray[3]));
            reportAgentStaticBetPO.setDayStr(mapKeyArray[4]);
            log.info("开始保存投注人数:{}",reportAgentStaticBetPO);
            reportAgentStaticBetService.saveData(reportAgentStaticBetPO,userSet);
            //修改投注人数
            updateAgentBetUserNum(reportAgentStaticBetPO,userSet);
        }

        Map<String,BigDecimal> rateMap=siteCurrencyInfoApi.getAllFinalRate(siteCode);

        //统计代理所属团队 充提手续费 user_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
        initUserDepositWithdrawFee(reportAgentStaticsCondVO);

        //统计会员人工加减额 user_manual_up_down_record 里 updated_time ;
        initUserManualUpDown(reportAgentStaticsCondVO);

        //统计代理代存 金额 agent_deposit_subordinates 里 deposit_time ; amount
        initAgentDepositSubordinate(reportAgentStaticsCondVO);

        //统计代理存款金额、取款金额、手续费 agent_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
        initAgentDepositWithdrawFee(reportAgentStaticsCondVO,rateMap);

        // 初始化代理 人工加减额
        initAgentManualUpDown(reportAgentStaticsCondVO);

        return ResponseVO.success(Boolean.TRUE);
    }

    /**
     * 代理人工加减额
     * 对于人工加减额来说   代理存款(后台)-额度钱包\代理提款(后台)-佣金钱包   才统计到存款总额、提款总额里
     * @param reportAgentStaticsCondVO
     */
    private void initAgentManualUpDown(ReportAgentStaticsCondVO reportAgentStaticsCondVO) {
        String siteCode=reportAgentStaticsCondVO.getSiteCode();
        String timeZoneId=reportAgentStaticsCondVO.getTimeZone();
        AgentManualDownRequestVO vo=new AgentManualDownRequestVO();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setCreatorStartTime(reportAgentStaticsCondVO.getStartDayMillis());
        vo.setCreatorEndTime(reportAgentStaticsCondVO.getEndDayMillis());
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
                    log.info("代理人工加减额,代理信息不存在:{}",agentManualUpRecordResponseVO.getAgentId());
                    continue;
                }
                AgentInfoVO currentAgentInfo=agentInfoVOOptional.get();
                String agentInfoPath=StringUtils.hasText(currentAgentInfo.getPath())?currentAgentInfo.getPath():currentAgentInfo.getAgentId();
                // 即计算某个代理所属团队的所有用户数据
                String[] allParentAgentIdArray=agentInfoPath.split(",");
                for(int i=0;i<=allParentAgentIdArray.length-1;i++){
                    String agentId=allParentAgentIdArray[i];
                    ReportAgentStaticDayPO reportAgentStaticDay=new ReportAgentStaticDayPO();
                    reportAgentStaticDay.setAgentId(agentId);
                    reportAgentStaticDay.setSiteCode(siteCode);
                    reportAgentStaticDay.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                    reportAgentStaticDay.setDayMillis(DateUtils.getStartDayMillis(agentManualUpRecordResponseVO.getUpdatedTime(),timeZoneId));
                    reportAgentStaticDay.setReportDate(DateUtils.formatDateByZoneId(reportAgentStaticDay.getDayMillis(),DateUtils.DATE_FORMAT_1,timeZoneId));
                    reportAgentStaticDay.setDepositTotalAmount(BigDecimal.ZERO);
                    reportAgentStaticDay.setWithdrawTotalAmount(BigDecimal.ZERO);
                    reportAgentStaticDay.setReportType(reportAgentStaticsCondVO.getReportType());
                    boolean dataExists=false;
                    if(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode().equals(agentManualUpRecordResponseVO.getAdjustWay())){
                        if(AgentManualAdjustTypeEnum.QUOTA_AGENT_DEPOSITION.getCode().equals(agentManualUpRecordResponseVO.getAdjustType().toString())){
                            reportAgentStaticDay.setDepositTotalAmount(agentManualUpRecordResponseVO.getAdjustAmount());
                            dataExists=true;
                        }
                    }
                    if(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode().equals(agentManualUpRecordResponseVO.getAdjustWay())){
                        if(AgentManualDownAdjustTypeEnum.COMMISSION_AGENT_WITHDRAWAL.getCode().equals(agentManualUpRecordResponseVO.getAdjustType().toString())){
                            reportAgentStaticDay.setWithdrawTotalAmount(agentManualUpRecordResponseVO.getAdjustAmount());
                            dataExists=true;
                        }
                    }
                    //更细至统计报表
                    if(dataExists){
                        log.info("代理人工加减额数据:{},来源订单号:{}",reportAgentStaticDay,agentManualUpRecordResponseVO.getOrderNo());
                        saveData(reportAgentStaticDay);
                    }
                }

            }
        }
    }



    //统计代理代存 金额 agent_deposit_subordinates 里 deposit_time ; amount
    private void initAgentDepositSubordinate(ReportAgentStaticsCondVO reportAgentStaticsCondVO) {
        String siteCode=reportAgentStaticsCondVO.getSiteCode();
        String reportType=reportAgentStaticsCondVO.getReportType();
        String timeZone=reportAgentStaticsCondVO.getTimeZone();

        AgentDepositSubordinatesPageReqVo vo=new AgentDepositSubordinatesPageReqVo();
        vo.setSiteCode(siteCode);
        vo.setStartTime(reportAgentStaticsCondVO.getStartDayMillis());
        vo.setEndTime(reportAgentStaticsCondVO.getEndDayMillis());
        vo.setAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        vo.setPageSize(500);
        vo.setPageNumber(1);
        Page<AgentDepositOfSubordinatesResVO> depositOfSubordinatesResVOPage=agentDepositSubordinatesApi.listPage(vo);
        if(!CollectionUtils.isEmpty(depositOfSubordinatesResVOPage.getRecords())){
            long totalPages=depositOfSubordinatesResVOPage.getPages();
            log.info("代理报表-代理代存 参数:{},总页数:{}",reportAgentStaticsCondVO,totalPages);

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
                        log.info("代理报表-代理代存 代理信息不存在:{}",agentDepositOfSubordinatesResVO.getAgentId());
                        continue;
                    }

                    AgentInfoVO currentAgentInfo=agentInfoVOOptional.get();
                    String agentInfoPath=StringUtils.hasText(currentAgentInfo.getPath())?currentAgentInfo.getPath():currentAgentInfo.getAgentId();
                    //计算当前用户所属代理、计算代理的所有上级代理
                    // 即计算某个代理所属团队的所有用户数据
                    String[] allParentAgentIdArray=agentInfoPath.split(",");
                    for(int i=0;i<=allParentAgentIdArray.length-1;i++){
                        String agentId=allParentAgentIdArray[i];
                        ReportAgentStaticDayPO reportAgentStaticDay=new ReportAgentStaticDayPO();
                        reportAgentStaticDay.setAgentId(agentId);
                        reportAgentStaticDay.setReportType(reportType);
                        String  reportDay=DateUtils.formatDateByZoneId(agentDepositOfSubordinatesResVO.getDepositTime(),DateUtils.DATE_FORMAT_1,timeZone);
                        String  reportMonth=DateUtils.formatDateByZoneId(agentDepositOfSubordinatesResVO.getDepositTime(),DateUtils.DATE_FORMAT_2,timeZone);
                        // 按天统计
                        if("0".equals(reportType)){
                            Long dayStartTime=DateUtils.getStartDayMillis(agentDepositOfSubordinatesResVO.getDepositTime(),timeZone);
                            reportAgentStaticDay.setDayMillis(dayStartTime);
                            reportAgentStaticDay.setReportDate(reportDay);
                        }else {
                            //按月统计
                            Long monthStartTime=DateUtils.getStartDayMonthTimestamp(agentDepositOfSubordinatesResVO.getDepositTime(),timeZone);
                            reportAgentStaticDay.setDayMillis(monthStartTime);
                            reportAgentStaticDay.setReportDate(reportMonth);
                        }

                        reportAgentStaticDay.setSiteCode(siteCode);
                        reportAgentStaticDay.setCurrencyCode(agentDepositOfSubordinatesResVO.getCurrencyCode());
                        //代理代存 会员存款增加
                        reportAgentStaticDay.setDepositTotalAmount(agentDepositOfSubordinatesResVO.getAmount());
                        //代理代存 代理提款同步增加
                        reportAgentStaticDay.setWithdrawTotalAmount(agentDepositOfSubordinatesResVO.getAmount());
                        //  reportAgentMerchantStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                        // BigDecimal convertRate=rateMap.get(agentDepositOfSubordinatesResVO.get());
                        //  BigDecimal depositAmountWtc=AmountUtils.divide(agentDepositOfSubordinatesResVO.getAmount(),convertRate);
                        // reportAgentMerchantStaticDayPO.setDepositAmount(agentDepositOfSubordinatesResVO.getPlatformAmount());
                        String uniKey=reportAgentStaticDay.getSiteCode().concat(reportAgentStaticDay.getReportDate()).concat(reportAgentStaticDay.getAgentId());
                        log.info("代理报表-代理代存,开始记录代理代存金额:{},{}",uniKey,reportAgentStaticDay);
                        saveData(reportAgentStaticDay);
                    }
                }
            }
        }


    }

    /**
     * 会员人工加减额
     * @param reportAgentStaticsCondVO 参数
     */
    private void initUserManualUpDown(ReportAgentStaticsCondVO reportAgentStaticsCondVO) {
        String siteCode=reportAgentStaticsCondVO.getSiteCode();
        String reportType=reportAgentStaticsCondVO.getReportType();
        String timeZone=reportAgentStaticsCondVO.getTimeZone();

        UserManualDownRecordRequestVO vo=new UserManualDownRecordRequestVO();
        vo.setSiteCode(siteCode);
        vo.setPageNumber(1);
        vo.setPageSize(1000);
        vo.setUpdateStartTime(reportAgentStaticsCondVO.getStartDayMillis());
        vo.setUpdateEndTime(reportAgentStaticsCondVO.getEndDayMillis());
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
                    log.info("代理报表 会员人工加减额 代理信息不存在:{}",userManualDownRecordVO.getAgentId());
                    continue;
                }
                AgentInfoVO currentAgentInfo=agentInfoVOOptional.get();
                String agentInfoPath=StringUtils.hasText(currentAgentInfo.getPath())?currentAgentInfo.getPath():currentAgentInfo.getAgentId();
                //计算当前用户所属代理、计算代理的所有上级代理
                // 即计算某个代理所属团队的所有用户数据
                String[] allParentAgentIdArray=agentInfoPath.split(",");
                for(int i=0;i<=allParentAgentIdArray.length-1;i++) {
                    String agentId = allParentAgentIdArray[i];

                    ReportAgentStaticDayPO reportAgentStaticDay=new ReportAgentStaticDayPO();
                    reportAgentStaticDay.setReportType(reportType);
                    String  reportDay=DateUtils.formatDateByZoneId(userManualDownRecordVO.getUpdatedTime(),DateUtils.DATE_FORMAT_1,timeZone);
                    String  reportMonth=DateUtils.formatDateByZoneId(userManualDownRecordVO.getUpdatedTime(),DateUtils.DATE_FORMAT_2,timeZone);
                    // 按天统计
                    if("0".equals(reportType)){
                        Long dayStartTime=DateUtils.getStartDayMillis(userManualDownRecordVO.getUpdatedTime(),timeZone);
                        reportAgentStaticDay.setDayMillis(dayStartTime);
                        reportAgentStaticDay.setReportDate(reportDay);
                    }else {
                        //按月统计
                        Long monthStartTime=DateUtils.getStartDayMonthTimestamp(userManualDownRecordVO.getUpdatedTime(),timeZone);
                        reportAgentStaticDay.setDayMillis(monthStartTime);
                        reportAgentStaticDay.setReportDate(reportMonth);
                    }
                    reportAgentStaticDay.setSiteCode(siteCode);
                    reportAgentStaticDay.setAgentId(agentId);
                    reportAgentStaticDay.setCurrencyCode(userManualDownRecordVO.getCurrencyCode());
                    boolean dataExists=false;
                    if(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode().equals(userManualDownRecordVO.getAdjustWay())){
                        if(Objects.equals(ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode(), userManualDownRecordVO.getAdjustType())){
                            // BigDecimal convertRate=rateMap.get(userManualDownRecordVO.getCurrencyCode());
                            //  BigDecimal depositAmountWtc=AmountUtils.divide(userManualDownRecordVO.getAdjustAmount(),convertRate);
                            reportAgentStaticDay.setDepositTotalAmount(userManualDownRecordVO.getAdjustAmount());
                            dataExists=true;
                        }
                    }
                    if(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode().equals(userManualDownRecordVO.getAdjustWay())){
                        if(Objects.equals(ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode(), userManualDownRecordVO.getAdjustType())){
                            // BigDecimal convertRate=rateMap.get(userManualDownRecordVO.getCurrencyCode());
                            //  BigDecimal withdrawAmountWtc=AmountUtils.divide(userManualDownRecordVO.getAdjustAmount(),convertRate);
                            reportAgentStaticDay.setWithdrawTotalAmount(userManualDownRecordVO.getAdjustAmount());
                            dataExists=true;
                        }
                    }
                    //更新至统计报表
                    if(dataExists){
                        String reportKey=reportAgentStaticDay.getReportDate().concat(reportAgentStaticDay.getAgentId());
                        log.info("代理报表,人工加减额日期-代理:{},开始记录会员人工加减金额:{}",reportKey,reportAgentStaticDay);
                        saveData(reportAgentStaticDay);
                    }
                }
            }
        }
    }

    /**
     * 修改 代理投注人数
     * @param reportAgentStaticBetPO
     * @param userSet
     */
    private void updateAgentBetUserNum(ReportAgentStaticBetPO reportAgentStaticBetPO,Set<String> userSet) {
        LambdaUpdateWrapper<ReportAgentStaticDayPO> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getBetUserNum,userSet.size());
        lambdaUpdateWrapper.eq(ReportAgentStaticDayPO::getSiteCode,reportAgentStaticBetPO.getSiteCode());
        lambdaUpdateWrapper.eq(ReportAgentStaticDayPO::getReportType,reportAgentStaticBetPO.getReportType());
        lambdaUpdateWrapper.eq(ReportAgentStaticDayPO::getReportDate,reportAgentStaticBetPO.getDayStr());
        lambdaUpdateWrapper.eq(ReportAgentStaticDayPO::getAgentId,reportAgentStaticBetPO.getAgentId());
        lambdaUpdateWrapper.eq(ReportAgentStaticDayPO::getCurrencyCode,reportAgentStaticBetPO.getCurrencyCode());
        this.update(lambdaUpdateWrapper);
    }

    //统计代理手续费 agent_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
    private void initAgentDepositWithdrawFee(ReportAgentStaticsCondVO reportAgentStaticsCondVO,Map<String,BigDecimal> rateMap) {
        String siteCode=reportAgentStaticsCondVO.getSiteCode();
        String reportType=reportAgentStaticsCondVO.getReportType();
        String timeZone=reportAgentStaticsCondVO.getTimeZone();
        AgentDepositWithDrawReqVO vo=new AgentDepositWithDrawReqVO();
        vo.setSiteCode(reportAgentStaticsCondVO.getSiteCode());
        vo.setStartTime(reportAgentStaticsCondVO.getStartDayMillis());
        vo.setEndTime(reportAgentStaticsCondVO.getEndDayMillis());
        vo.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        vo.setPageNumber(1);
        vo.setPageSize(500);
        Page<AgentDepositWithdrawRespVO>  agentDepositWithdrawRespVOPage=agentDepositWithdrawApi.listPage(vo);
        long totalPageNum = agentDepositWithdrawRespVOPage.getPages();
        log.info("代理充提手续费计算 参数:{},总页数:{}",reportAgentStaticsCondVO,totalPageNum);
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
                    log.info("代理充提手续费计算 代理信息不存在:{}",agentDepositWithdrawRespVO.getAgentId());
                    continue;
                }
                AgentInfoVO currentAgentInfo=agentInfoVOOptional.get();
                String agentInfoPath=StringUtils.hasText(currentAgentInfo.getPath())?currentAgentInfo.getPath():currentAgentInfo.getAgentId();
                //计算当前用户所属代理、计算代理的所有上级代理
                // 即计算某个代理所属团队的所有用户数据
                String[] allParentAgentIdArray=agentInfoPath.split(",");
                for(int i=0;i<=allParentAgentIdArray.length-1;i++){
                    String  reportDay=DateUtils.formatDateByZoneId(agentDepositWithdrawRespVO.getUpdatedTime(),DateUtils.DATE_FORMAT_1,timeZone);
                    String  reportMonth=DateUtils.formatDateByZoneId(agentDepositWithdrawRespVO.getUpdatedTime(),DateUtils.DATE_FORMAT_2,timeZone);
                    ReportAgentStaticDayPO reportAgentStaticDayPO=new ReportAgentStaticDayPO();
                    reportAgentStaticDayPO.setReportType(reportType);
                    // 按天统计
                    if("0".equals(reportType)){
                        Long dayStartTime=DateUtils.getStartDayMillis(agentDepositWithdrawRespVO.getUpdatedTime(),timeZone);
                        reportAgentStaticDayPO.setDayMillis(dayStartTime);
                        reportAgentStaticDayPO.setReportDate(reportDay);
                    }else {
                        //按月统计
                        Long monthStartTime=DateUtils.getStartDayMonthTimestamp(agentDepositWithdrawRespVO.getUpdatedTime(),timeZone);
                        reportAgentStaticDayPO.setDayMillis(monthStartTime);
                        reportAgentStaticDayPO.setReportDate(reportMonth);
                    }
                    String parentAgentId=allParentAgentIdArray[i];
                    reportAgentStaticDayPO.setSiteCode(siteCode);
                    reportAgentStaticDayPO.setAgentId(parentAgentId);
                    AgentInfoVO parentAgentAccount=agentInfoVOS.stream().filter(o->o.getAgentId().equals(agentDepositWithdrawRespVO.getAgentId())).findFirst().get();
                    reportAgentStaticDayPO.setAgentAccount(parentAgentAccount.getAgentAccount());
                    //WTC
                    reportAgentStaticDayPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                    //存款金额 WTC
                    if(Objects.equals(agentDepositWithdrawRespVO.getType(), DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())){
                        reportAgentStaticDayPO.setDepositTotalAmount(agentDepositWithdrawRespVO.getArriveAmount());
                    }else {
                        reportAgentStaticDayPO.setWithdrawTotalAmount(agentDepositWithdrawRespVO.getArriveAmount());
                    }
                    BigDecimal settlementFeeWtc=AmountUtils.divide(agentDepositWithdrawRespVO.getSettlementFeeAmount(),agentDepositWithdrawRespVO.getPlatformCurrencyExchangeRate());
                    //手续费转换为WTC
                    reportAgentStaticDayPO.setDepositWithdrawFeeAmount(settlementFeeWtc);
                    log.info("开始统计代理充提手续费:{}",reportAgentStaticDayPO);
                    saveData(reportAgentStaticDayPO);
                }
            }
            log.info("站点:{},处理结束第:{}页 代理充提手续费计算",siteCode,pageNum);
        }
    }

    //统计代理所属团队 充提手续费 user_deposit_withdrawal 里 updated_time ; status=101 的 settlement_fee_amount
    private void initUserDepositWithdrawFee(ReportAgentStaticsCondVO reportAgentStaticsCondVO) {
        //站点平台币兑换汇率
        Map<String,BigDecimal> rateMap=siteCurrencyInfoApi.getAllFinalRate(reportAgentStaticsCondVO.getSiteCode());

        String timeZone=reportAgentStaticsCondVO.getTimeZone();
        UserDepositWithdrawPageReqVO userDepositWithdrawPageReqVO=new UserDepositWithdrawPageReqVO();
        userDepositWithdrawPageReqVO.setSiteCode(reportAgentStaticsCondVO.getSiteCode());
        userDepositWithdrawPageReqVO.setStartTime(reportAgentStaticsCondVO.getStartDayMillis());
        userDepositWithdrawPageReqVO.setEndTime(reportAgentStaticsCondVO.getEndDayMillis());
       userDepositWithdrawPageReqVO.setUserAccountType(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode());
        userDepositWithdrawPageReqVO.setPageNumber(1);
        userDepositWithdrawPageReqVO.setPageSize(500);
        String siteCode=reportAgentStaticsCondVO.getSiteCode();
        String reportType=reportAgentStaticsCondVO.getReportType();
        Page<UserDepositWithdrawalResVO> userDepositWithdrawalResVOPage = userDepositWithdrawApi.findDepositWithdrawPage(userDepositWithdrawPageReqVO);
        long totalPageNum = userDepositWithdrawalResVOPage.getPages();
        log.info("充提手续费计算 参数:{},总页数:{}",reportAgentStaticsCondVO,totalPageNum);
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
                String agentInfoPath=StringUtils.hasText(currentAgentInfo.getPath())?currentAgentInfo.getPath():currentAgentInfo.getAgentId();
                //计算当前用户所属代理、计算代理的所有上级代理
                // 即计算某个代理所属团队的所有用户数据
                String[] allParentAgentIdArray=agentInfoPath.split(",");
                for(int i=0;i<=allParentAgentIdArray.length-1;i++){

                    String  reportDay=DateUtils.formatDateByZoneId(userDepositWithdrawalResVO.getUpdatedTime(),DateUtils.DATE_FORMAT_1,timeZone);
                    String  reportMonth=DateUtils.formatDateByZoneId(userDepositWithdrawalResVO.getUpdatedTime(),DateUtils.DATE_FORMAT_2,timeZone);
                    ReportAgentStaticDayPO reportAgentStaticDayPO=new ReportAgentStaticDayPO();
                    reportAgentStaticDayPO.setReportType(reportType);
                    // 按天统计
                    if("0".equals(reportType)){
                        Long dayStartTime=DateUtils.getStartDayMillis(userDepositWithdrawalResVO.getUpdatedTime(),timeZone);
                        reportAgentStaticDayPO.setDayMillis(dayStartTime);
                        reportAgentStaticDayPO.setReportDate(reportDay);
                    }else {
                        //按月统计
                        Long monthStartTime=DateUtils.getStartDayMonthTimestamp(userDepositWithdrawalResVO.getUpdatedTime(),timeZone);
                        reportAgentStaticDayPO.setDayMillis(monthStartTime);
                        reportAgentStaticDayPO.setReportDate(reportMonth);
                    }
                    String parentAgentId=allParentAgentIdArray[i];
                    reportAgentStaticDayPO.setSiteCode(siteCode);
                    reportAgentStaticDayPO.setAgentId(parentAgentId);
                    AgentInfoVO parentAgentAccount=agentInfoVOS.stream().filter(o->o.getAgentId().equals(userDepositWithdrawalResVO.getAgentId())).findFirst().get();
                    reportAgentStaticDayPO.setAgentAccount(parentAgentAccount.getAgentAccount());

                    reportAgentStaticDayPO.setCurrencyCode(userDepositWithdrawalResVO.getCurrencyCode());
                    BigDecimal transferRate=rateMap.get(userDepositWithdrawalResVO.getCurrencyCode());
                    //存款总额
                    if(Objects.equals(userDepositWithdrawalResVO.getType(), DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())){
                        reportAgentStaticDayPO.setDepositTotalAmount(userDepositWithdrawalResVO.getArriveAmount());
                    }else {
                        reportAgentStaticDayPO.setWithdrawTotalAmount(userDepositWithdrawalResVO.getApplyAmount());
                    }
                    reportAgentStaticDayPO.setDepositWithdrawFeeAmount(userDepositWithdrawalResVO.getSettlementFeeAmount());
                    log.info("开始统计用户充提手续费:{}",reportAgentStaticDayPO);
                    saveData(reportAgentStaticDayPO);
                }
            }
            log.info("站点:{},处理结束第:{}页 充提手续费计算",siteCode,pageNum);
        }



    }

    /**
     * 代理金额统计
     * @param userWinLoseResponseVOList 原始会员盈亏列表
     * @param betUserMap 会员投注列表
     * @param reportType 报表统计类型 0:日报 1:月报
     * @param timeZone  时区
     */
    private void batchProcess(List<UserWinLoseListResponseVO> userWinLoseResponseVOList,
                              Map<String, Set<String>> betUserMap,
                              String reportType,
                              String timeZone
                              ) {

        if(CollectionUtils.isEmpty(userWinLoseResponseVOList)){
            return;
        }
        Map<String,ReportAgentStaticDayPO> sumMap=new HashMap<String,ReportAgentStaticDayPO>();

        List<String> agentIds=userWinLoseResponseVOList.stream().filter(o->StringUtils.hasText(o.getAgentId())).map(UserWinLoseListResponseVO::getAgentId).toList();
        if(CollectionUtils.isEmpty(agentIds)){
            return;
        }
        List<AgentInfoVO> agentInfoVOS= agentInfoApi.getByAgentIds(agentIds);


       for(UserWinLoseListResponseVO userWinLoseResponseVO:userWinLoseResponseVOList ){
           ReportAgentStaticDayPO reportAgentStaticDayPO=new ReportAgentStaticDayPO();
           String siteCode=userWinLoseResponseVO.getSiteCode();
           String agentAccount=userWinLoseResponseVO.getSuperAgentAccount();
           if(!StringUtils.hasText(agentAccount)){
               log.debug("{},代理账号不存在,无须统计",userWinLoseResponseVO.getUserId());
               continue;
           }
           String currencyCode=userWinLoseResponseVO.getMainCurrency();
           Long dayMillis=userWinLoseResponseVO.getDayMillis();
           String  reportDay=DateUtils.formatDateByZoneId(dayMillis,DateUtils.DATE_FORMAT_1,timeZone);
           String  reportMonth=DateUtils.formatDateByZoneId(dayMillis,DateUtils.DATE_FORMAT_2,timeZone);
           String reportDate="";
           String dayStr="";
           Long monthStartTime=DateUtils.getYearMonthTime(reportMonth);
           reportAgentStaticDayPO.setAgentId(userWinLoseResponseVO.getAgentId());
           reportAgentStaticDayPO.setAgentAccount(agentAccount);
           reportAgentStaticDayPO.setSiteCode(userWinLoseResponseVO.getSiteCode());
           reportAgentStaticDayPO.setCurrencyCode(userWinLoseResponseVO.getMainCurrency());
           reportAgentStaticDayPO.setReportType(reportType);
            // 按天统计
           if("0".equals(reportType)){
               reportAgentStaticDayPO.setDayMillis(dayMillis);
               reportAgentStaticDayPO.setReportDate(reportDay);
               reportDate=reportDay;
               dayStr=dayMillis.toString();
           }else {
               //按月统计
               reportAgentStaticDayPO.setDayMillis(monthStartTime);
               reportAgentStaticDayPO.setReportDate(reportMonth);
               reportDate=reportMonth;
               dayMillis=monthStartTime;
               dayStr=monthStartTime.toString();
           }


           // 数据为空的不统计
           boolean hasData=false;
           if(userWinLoseResponseVO.getBetNum()>0){
               hasData=true;
           }
           if(userWinLoseResponseVO.getBetAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }
           if(userWinLoseResponseVO.getValidBetAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }
           if(userWinLoseResponseVO.getBetWinLose().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }
           if(userWinLoseResponseVO.getVipAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }
           if(userWinLoseResponseVO.getAlreadyUseAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }
           if(userWinLoseResponseVO.getActivityAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }
           if(userWinLoseResponseVO.getAdjustAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }

           if(userWinLoseResponseVO.getRebateAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }
           if(userWinLoseResponseVO.getTipsAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }

           if(userWinLoseResponseVO.getRiskAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }

           if(userWinLoseResponseVO.getPlatAdjustAmount().compareTo(BigDecimal.ZERO)!=0){
               hasData=true;
           }



           if(!hasData){
               log.info("代理报表,会员盈亏统计数据为空,不需要处理:{}",userWinLoseResponseVO);
               continue;
           }


           //投注人数 去除重复
           reportAgentStaticDayPO.setBetUserNum(0L);
           //投注金额
           reportAgentStaticDayPO.setBetAmount(userWinLoseResponseVO.getBetAmount());
           //有效投注
           reportAgentStaticDayPO.setValidAmount(userWinLoseResponseVO.getValidBetAmount());
           //增加返水汇总 mengfei
           reportAgentStaticDayPO.setRebateAmount(userWinLoseResponseVO.getRebateAmount());
           //会员输赢
           reportAgentStaticDayPO.setWinLossAmountUser(userWinLoseResponseVO.getBetWinLose());
           //平台总输赢 等于 -会员输赢
           reportAgentStaticDayPO.setWinLossAmountPlat(BigDecimal.ZERO.subtract(userWinLoseResponseVO.getBetWinLose()));
           //调整金额
           reportAgentStaticDayPO.setAdjustAmount(userWinLoseResponseVO.getAdjustAmount());
           log.debug("agentId:{},adjustAmount:{}",userWinLoseResponseVO.getAgentId(),userWinLoseResponseVO.getAdjustAmount());
           //盈亏比例 等于 平台总输赢 / 投注额
           reportAgentStaticDayPO.setWinLossRate(AmountUtils.divide(reportAgentStaticDayPO.getWinLossAmountPlat(),reportAgentStaticDayPO.getBetAmount(),4));
           reportAgentStaticDayPO.setActivityAmount(userWinLoseResponseVO.getActivityAmount());
           reportAgentStaticDayPO.setVipAmount(userWinLoseResponseVO.getVipAmount());
           reportAgentStaticDayPO.setAlreadyUseAmount(userWinLoseResponseVO.getAlreadyUseAmount());
           reportAgentStaticDayPO.setDepositWithdrawFeeAmount(BigDecimal.ZERO);
           reportAgentStaticDayPO.setDepositTotalAmount(BigDecimal.ZERO);
           reportAgentStaticDayPO.setWithdrawTotalAmount(BigDecimal.ZERO);

           reportAgentStaticDayPO.setTipsAmount(userWinLoseResponseVO.getTipsAmount());
           reportAgentStaticDayPO.setRiskAmount(userWinLoseResponseVO.getRiskAmount());
           reportAgentStaticDayPO.setPlatAdjustAmount(userWinLoseResponseVO.getPlatAdjustAmount());
         //  Long dayEndMillis=DateUtils.getEndDayByStartTime(dayMillis);
           //佣金金额
           //CommissionReviewCalculateReq commissionReviewCalculateReq=new CommissionReviewCalculateReq();
          // commissionReviewCalculateReq.setAgentAccount(reportAgentStaticDayPO.getAgentAccount());
          // commissionReviewCalculateReq.setSiteCode(reportAgentStaticDayPO.getSiteCode());
          // commissionReviewCalculateReq.setAuditStartTime(dayMillis);
          // commissionReviewCalculateReq.setAuditEndTime(dayEndMillis);
         //  ResponseVO<BigDecimal> bigDecimalResponseVO=agentCommissionReviewRecordApi.calculateAgentCommission(commissionReviewCalculateReq);
         //  reportAgentStaticDayPO.setCommissionAmount(bigDecimalResponseVO.getData());
           //平台收入 等于 平台总输赢(主货币) - 调整金额(主货币) - 已使用活动优惠(主货币) - 代理佣金(平台币)
          // BigDecimal transferRate=rateMap.get(reportAgentStaticDayPO.getCurrencyCode());
          // BigDecimal winLossAmountPlatWtc=AmountUtils.divide(reportAgentStaticDayPO.getWinLossAmountPlat(),transferRate);
          // BigDecimal adjustAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getAdjustAmount(),transferRate);
          // BigDecimal alreadyUseAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getAlreadyUseAmount(),transferRate);
          // BigDecimal platIncome=winLossAmountPlatWtc.subtract(adjustAmountWtc).subtract(alreadyUseAmountWtc).subtract(reportAgentStaticDayPO.getCommissionAmount());
          // reportAgentStaticDayPO.setPlatIncome(platIncome);
           AgentInfoVO currentAgentInfo=agentInfoVOS.stream().filter(o->o.getAgentId().equals(userWinLoseResponseVO.getAgentId())).findFirst().get();
           String agentInfoPath=StringUtils.hasText(currentAgentInfo.getPath())?currentAgentInfo.getPath():currentAgentInfo.getAgentId();
            //计算当前用户所属代理、计算代理的所有上级代理
           // 即计算某个代理所属团队的所有用户数据
           String[] allParentAgentIdArray=agentInfoPath.split(",");
           log.debug("currentAgentId:{},allParentAgentIdArray:{}",currentAgentInfo.getAgentId(),allParentAgentIdArray);
           for(int i=0;i<=allParentAgentIdArray.length-1;i++){
               String parentAgentId=allParentAgentIdArray[i];
               reportAgentStaticDayPO.setAgentId(parentAgentId);
               String mapKey=siteCode
                       .concat("#")
                       .concat(parentAgentId)
                       .concat("#")
                       .concat(currencyCode)
                       .concat("#")
                       .concat(dayStr)
                       .concat("#")
                       .concat(reportDate);
               Set<String> userSet=new HashSet<>();
               //计算投注人数
               if(userWinLoseResponseVO.getBetNum()>0){
                   if(betUserMap.containsKey(mapKey)){
                       userSet= betUserMap.get(mapKey);
                       userSet.add(userWinLoseResponseVO.getUserId());
                       betUserMap.put(mapKey,userSet);
                   }else {
                       userSet.add(userWinLoseResponseVO.getUserId());
                       betUserMap.put(mapKey,userSet);
                   }
               }

             //   log.info("{}投注人数:{},mapKey:{}",parentAgentId,userSet.size(),mapKey);
               if(sumMap.containsKey(mapKey)){
                   ReportAgentStaticDayPO reportAgentStaticDayPOMapMemory=sumMap.get(mapKey);
                   reportAgentStaticDayPOMapMemory.setBetAmount(reportAgentStaticDayPO.getBetAmount().add(reportAgentStaticDayPOMapMemory.getBetAmount()));
                   reportAgentStaticDayPOMapMemory.setValidAmount(reportAgentStaticDayPO.getValidAmount().add(reportAgentStaticDayPOMapMemory.getValidAmount()));

                   reportAgentStaticDayPOMapMemory.setRebateAmount(reportAgentStaticDayPO.getRebateAmount().add(reportAgentStaticDayPOMapMemory.getRebateAmount()));


                   reportAgentStaticDayPOMapMemory.setWinLossAmountUser(reportAgentStaticDayPO.getWinLossAmountUser().add(reportAgentStaticDayPOMapMemory.getWinLossAmountUser()));
                   reportAgentStaticDayPOMapMemory.setWinLossAmountPlat(reportAgentStaticDayPO.getWinLossAmountPlat().add(reportAgentStaticDayPOMapMemory.getWinLossAmountPlat()));
                   reportAgentStaticDayPOMapMemory.setAdjustAmount(reportAgentStaticDayPO.getAdjustAmount().add(reportAgentStaticDayPOMapMemory.getAdjustAmount()));
                   reportAgentStaticDayPOMapMemory.setActivityAmount(reportAgentStaticDayPO.getActivityAmount().add(reportAgentStaticDayPOMapMemory.getActivityAmount()));
                   reportAgentStaticDayPOMapMemory.setVipAmount(reportAgentStaticDayPO.getVipAmount().add(reportAgentStaticDayPOMapMemory.getVipAmount()));
                   reportAgentStaticDayPOMapMemory.setAlreadyUseAmount(reportAgentStaticDayPO.getAlreadyUseAmount().add(reportAgentStaticDayPOMapMemory.getAlreadyUseAmount()));
                   reportAgentStaticDayPOMapMemory.setTipsAmount(reportAgentStaticDayPO.getTipsAmount().add(reportAgentStaticDayPOMapMemory.getTipsAmount()));
                   reportAgentStaticDayPOMapMemory.setRiskAmount(reportAgentStaticDayPO.getRiskAmount().add(reportAgentStaticDayPOMapMemory.getRiskAmount()));
                   reportAgentStaticDayPOMapMemory.setPlatAdjustAmount(reportAgentStaticDayPO.getPlatAdjustAmount().add(reportAgentStaticDayPOMapMemory.getPlatAdjustAmount()));

                   BigDecimal betAmount=reportAgentStaticDayPOMapMemory.getBetAmount();
                   BigDecimal winLossAmountPlat=reportAgentStaticDayPOMapMemory.getWinLossAmountPlat();
                   //盈亏比例 等于 平台总输赢 / 投注额
                   BigDecimal winLossRate=AmountUtils.divide(winLossAmountPlat,betAmount,4);
                   reportAgentStaticDayPOMapMemory.setWinLossRate(winLossRate);

                   //投注人数
                  // reportAgentStaticDayPOMapMemory.setBetUserNum(Long.valueOf(userSet.size()));
                   log.debug("投注人数:{},key:{},数据2:{}",parentAgentId,mapKey,reportAgentStaticDayPOMapMemory);
                   sumMap.put(mapKey,reportAgentStaticDayPOMapMemory);
               }else {
                   ReportAgentStaticDayPO reportAgentStaticDayPOMap=new ReportAgentStaticDayPO();
                   BeanUtils.copyProperties(reportAgentStaticDayPO,reportAgentStaticDayPOMap);
                 //  reportAgentStaticDayPOMap.setBetUserNum(Long.valueOf(userSet.size()));
                   sumMap.put(mapKey,reportAgentStaticDayPOMap);
                   log.debug("投注人数:{},key:{},数据1:{}",parentAgentId,mapKey,reportAgentStaticDayPOMap);
               }
           }
       }
        //存储至DB
        for(ReportAgentStaticDayPO reportAgentStaticDay:sumMap.values()){
            //保存至数据库
            saveData(reportAgentStaticDay);
        }
    }


    /**
     * 填充统计人数
     * @param reportAgentStaticDay
     */
    private void fillAgentNum(ReportAgentStaticDayPO reportAgentStaticDay) {
        String agentId= reportAgentStaticDay.getAgentId();
        AgentInfoVO agentInfoVO=agentInfoApi.getByAgentId(agentId);
        //代理信息填充
        reportAgentStaticDay.setAgentAccount(agentInfoVO.getAgentAccount());
        reportAgentStaticDay.setPath(agentInfoVO.getPath());
        reportAgentStaticDay.setLevel(agentInfoVO.getLevel());
        reportAgentStaticDay.setAgentType(agentInfoVO.getAgentType());
        reportAgentStaticDay.setRegisterTime(agentInfoVO.getRegisterTime());
        reportAgentStaticDay.setAgentAttribution(agentInfoVO.getAgentAttribution());
        reportAgentStaticDay.setAgentLabelId(agentInfoVO.getAgentLabelId());
        reportAgentStaticDay.setRiskLevelId(agentInfoVO.getRiskLevelId());
        List<String> subAgentList=agentInfoApi.getSubAgentIdList(agentId);
        //所有下级代理人数 团队代理人数
        long teamNum=Long.valueOf(subAgentList.size());
        reportAgentStaticDay.setTeamAgentNum(teamNum);
        //直属下级人数
        List<String> subAgentDirectReportList=agentInfoApi.getSubAgentIdDirectReportList(agentId);
        reportAgentStaticDay.setDirectReportNum(Long.valueOf(subAgentDirectReportList.size()));
    }

    /**
     * 填充 注册人数 首存人数
     * @param reportAgentStaticsCondVO 代理信息统计
     * @return
     */
    private void  fillRegisterNum(ReportAgentStaticsCondVO reportAgentStaticsCondVO){
        String siteCode=reportAgentStaticsCondVO.getSiteCode();
        String reportType=reportAgentStaticsCondVO.getReportType();

        UserInfoPageVO vo=new UserInfoPageVO();
        vo.setSiteCode(siteCode);
        vo.setAccountType(List.of(UserAccountTypeEnum.FORMAL_ACCOUNT.getCode()));
        vo.setRegisterTimeStart(reportAgentStaticsCondVO.getStartDayMillis());
        vo.setRegisterTimeEnd(reportAgentStaticsCondVO.getEndDayMillis());
        vo.setAgentFlag(1);
        vo.setPageNumber(1);
        vo.setPageSize(500);
        Page<UserInfoResponseVO> userInfoResponseVOFirstPage= userInfoApi.listPage(vo);
        long totalPageNum=userInfoResponseVOFirstPage.getPages();
        Map<String,ReportAgentStaticDayPO> agentUserRegisterStaticMap=new HashMap<>();
        Map<String,Set<String>> userRegisterMap=new HashMap<>();
       Set<String> agentIds= Sets.newHashSet();
        for(int pageNum=1;pageNum<=totalPageNum;pageNum++){
            vo.setPageNumber(pageNum);
            log.info("注册人数查询:{}",vo);
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
                            .concat(DateUtils.getStartDayMillis(userInfoResponseVO.getRegisterTime(),reportAgentStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getRegisterTime(),DateUtils.DATE_FORMAT_1,reportAgentStaticsCondVO.getTimeZone()));
                        }else {
                            mapKey=mapKey.concat("#")
                            .concat(DateUtils.getStartDayMonthTimestamp(userInfoResponseVO.getRegisterTime(),reportAgentStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getRegisterTime(),DateUtils.DATE_FORMAT_2,reportAgentStaticsCondVO.getTimeZone()));
                        }

                ReportAgentStaticDayPO reportAgentStaticDayPO=new ReportAgentStaticDayPO();
                reportAgentStaticDayPO.setReportType(reportType);
                reportAgentStaticDayPO.setSiteCode(userInfoResponseVO.getSiteCode());
                reportAgentStaticDayPO.setAgentId(userInfoResponseVO.getSuperAgentId());
                reportAgentStaticDayPO.setCurrencyCode(userInfoResponseVO.getMainCurrency());
                long starDayMillis=Long.valueOf(mapKey.split("#")[3]);
                reportAgentStaticDayPO.setDayMillis(starDayMillis);
                reportAgentStaticDayPO.setReportDate(mapKey.split("#")[4]);
                if(userRegisterMap.containsKey(mapKey)){
                    Set<String> userAccountSet=userRegisterMap.get(mapKey);
                    userAccountSet.add(userInfoResponseVO.getUserId());
                    userRegisterMap.put(mapKey,userAccountSet) ;
                    reportAgentStaticDayPO.setRegisterUserNum(Long.valueOf(userAccountSet.size()));
                    log.info("原始注册人数:{},key:{},value:{},cond:{} register02",userInfoResponseVO.getSuperAgentId(),mapKey,reportAgentStaticDayPO,reportAgentStaticsCondVO);
                }else {
                    Set<String> userAccountSet=new HashSet<>();
                    userAccountSet.add(userInfoResponseVO.getUserId());
                    userRegisterMap.put(mapKey,userAccountSet) ;
                    reportAgentStaticDayPO.setRegisterUserNum(1L);
                    log.info("原始注册人数:{},key:{},value:{},cond:{} register01",userInfoResponseVO.getSuperAgentId(),mapKey,reportAgentStaticDayPO,reportAgentStaticsCondVO);
                }

                agentUserRegisterStaticMap.put(mapKey,reportAgentStaticDayPO);
                agentIds.add(userInfoResponseVO.getSuperAgentId());
            }
        }

        UserInfoPageVO depositVo=new UserInfoPageVO();
        depositVo.setSiteCode(siteCode);
        depositVo.setFirstDepositTimeStart(reportAgentStaticsCondVO.getStartDayMillis());
        depositVo.setFirstDepositTimeEnd(reportAgentStaticsCondVO.getEndDayMillis());
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
            for(UserInfoResponseVO userInfoResponseVO:userInfoDepositPage.getRecords()){
                if(!StringUtils.hasText(userInfoResponseVO.getSuperAgentId())){
                    log.debug("站点:{}首存人数 代理为空",siteCode);
                    continue;
                }
                if(userInfoResponseVO.getFirstDepositTime()==null){
                    continue;
                }
                String mapKey=userInfoResponseVO.getSiteCode()
                        .concat("#")
                        .concat(userInfoResponseVO.getSuperAgentId())
                        .concat("#")
                        .concat(userInfoResponseVO.getMainCurrency());
                if("0".equals(reportType)){
                    mapKey=mapKey.concat("#")
                            .concat(DateUtils.getStartDayMillis(userInfoResponseVO.getFirstDepositTime(),reportAgentStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getFirstDepositTime(),DateUtils.DATE_FORMAT_1,reportAgentStaticsCondVO.getTimeZone()));
                }else {
                    mapKey=mapKey.concat("#")
                            .concat(DateUtils.getStartDayMonthTimestamp(userInfoResponseVO.getFirstDepositTime(),reportAgentStaticsCondVO.getTimeZone()).toString())
                            .concat("#")
                            .concat(DateUtils.formatDateByZoneId(userInfoResponseVO.getFirstDepositTime(),DateUtils.DATE_FORMAT_2,reportAgentStaticsCondVO.getTimeZone()));
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
                    ReportAgentStaticDayPO registerStaticDay=agentUserRegisterStaticMap.get(mapKey);
                    registerStaticDay.setSiteCode(userInfoResponseVO.getSiteCode());
                    registerStaticDay.setFirstDepositNum(depositNum);
                    BigDecimal  convertRate= NumberUtil.divide(registerStaticDay.getFirstDepositNum(),registerStaticDay.getRegisterUserNum(),4);
                    registerStaticDay.setFirstDepositRate(convertRate);
                    agentUserRegisterStaticMap.put(mapKey,registerStaticDay);
                    log.info("充值人数:{},key:{},value:{} deposit01",registerStaticDay.getAgentId(),mapKey,registerStaticDay);
                }else {
                    ReportAgentStaticDayPO reportAgentStaticDayPO=new ReportAgentStaticDayPO();
                    reportAgentStaticDayPO.setSiteCode(userInfoResponseVO.getSiteCode());
                    reportAgentStaticDayPO.setReportType(reportType);
                    reportAgentStaticDayPO.setSiteCode(userInfoResponseVO.getSiteCode());
                    reportAgentStaticDayPO.setAgentId(userInfoResponseVO.getSuperAgentId());
                    reportAgentStaticDayPO.setCurrencyCode(userInfoResponseVO.getMainCurrency());
                    long starDayMillis=Long.valueOf(mapKey.split("#")[3]);
                    reportAgentStaticDayPO.setDayMillis(starDayMillis);
                    reportAgentStaticDayPO.setReportDate(mapKey.split("#")[4]);
                    reportAgentStaticDayPO.setRegisterUserNum(0L);
                    reportAgentStaticDayPO.setFirstDepositNum(depositNum);
                    reportAgentStaticDayPO.setFirstDepositRate(BigDecimal.ZERO);
                    agentUserRegisterStaticMap.put(mapKey,reportAgentStaticDayPO);
                    log.info("充值人数:{},key:{},value:{} deposit02",reportAgentStaticDayPO.getAgentId(),mapKey,reportAgentStaticDayPO);
                }
                agentIds.add(userInfoResponseVO.getSuperAgentId());

            }
        }

        if(CollectionUtils.isEmpty(agentUserRegisterStaticMap)){
            return ;
        }
        List<AgentInfoVO> agentInfoVOS= agentInfoApi.getByAgentIds(agentIds.stream().toList());
        Map<String,ReportAgentStaticDayPO> agentStaticParentMap=new HashMap<>();
        for(String mapKey:agentUserRegisterStaticMap.keySet()){
            String currentAgentId=mapKey.split("#")[1];
            ReportAgentStaticDayPO reportAgentStaticDayPO=agentUserRegisterStaticMap.get(mapKey);
            AgentInfoVO currentAgentInfoVo=agentInfoVOS.stream().filter(o->o.getAgentId().equals(currentAgentId)).findFirst().get();
            String agentPath=currentAgentInfoVo.getPath();
            if(StringUtils.hasText(agentPath)){
                for(String parentAgentId:agentPath.split(",")){
                    ReportAgentStaticDayPO reportAgentStaticDayPOParent=new ReportAgentStaticDayPO();
                    BeanUtils.copyProperties(reportAgentStaticDayPO,reportAgentStaticDayPOParent);
                    //上级同时累计
                    reportAgentStaticDayPOParent.setAgentId(parentAgentId);
                    String  actualMapKey=mapKey.replace(currentAgentId,parentAgentId);
                    if(agentStaticParentMap.containsKey(actualMapKey)){
                        ReportAgentStaticDayPO reportAgentStaticDayPOParentExist=agentStaticParentMap.get(actualMapKey);
                        //注册人数累计
                        reportAgentStaticDayPOParentExist.setRegisterUserNum(reportAgentStaticDayPOParent.getRegisterUserNum()+reportAgentStaticDayPOParentExist.getRegisterUserNum());
                        //首存人数累计
                        reportAgentStaticDayPOParentExist.setFirstDepositNum(reportAgentStaticDayPOParent.getFirstDepositNum()+reportAgentStaticDayPOParentExist.getFirstDepositNum());
                        BigDecimal  convertRate= NumberUtil.divide(reportAgentStaticDayPOParentExist.getFirstDepositNum(),reportAgentStaticDayPOParentExist.getRegisterUserNum(),4);
                        reportAgentStaticDayPOParentExist.setFirstDepositRate(convertRate);
                        agentStaticParentMap.put(actualMapKey,reportAgentStaticDayPOParentExist);
                        //保存至数据库
                        log.info("注册人数2:{},currentAgentId:{},key:{},value:{},mapKey:{}",parentAgentId,currentAgentId,actualMapKey,reportAgentStaticDayPOParentExist,mapKey);
                        saveData(reportAgentStaticDayPOParentExist);
                    }else {
                        agentStaticParentMap.put(actualMapKey,reportAgentStaticDayPOParent);
                        log.info("注册人数1:{},currentAgentId:{},key:{},value:{},mapKey:{}",parentAgentId,currentAgentId,actualMapKey,reportAgentStaticDayPOParent,mapKey);
                        //保存至数据库
                        saveData(reportAgentStaticDayPOParent);
                    }

                }
            }
        }
      //  return agentStaticParentMap;
    }


    /**
     * 保存至数据库
     * @param reportAgentStaticDay 原始数据
     */
    private void saveData(ReportAgentStaticDayPO reportAgentStaticDay) {
        LambdaQueryWrapper<ReportAgentStaticDayPO> reportAgentStaticDayPOLambdaQueryWrapper=new LambdaQueryWrapper<>();
        reportAgentStaticDayPOLambdaQueryWrapper.eq(ReportAgentStaticDayPO::getSiteCode,reportAgentStaticDay.getSiteCode());
        reportAgentStaticDayPOLambdaQueryWrapper.eq(ReportAgentStaticDayPO::getReportType,reportAgentStaticDay.getReportType());
        reportAgentStaticDayPOLambdaQueryWrapper.eq(ReportAgentStaticDayPO::getReportDate,reportAgentStaticDay.getReportDate());
        reportAgentStaticDayPOLambdaQueryWrapper.eq(ReportAgentStaticDayPO::getAgentId,reportAgentStaticDay.getAgentId());
        reportAgentStaticDayPOLambdaQueryWrapper.eq(ReportAgentStaticDayPO::getCurrencyCode,reportAgentStaticDay.getCurrencyCode());
        ReportAgentStaticDayPO reportAgentStaticDayPODb=this.baseMapper.selectOne(reportAgentStaticDayPOLambdaQueryWrapper);
        //已存在 则更新
        if(reportAgentStaticDayPODb!=null){
            LambdaUpdateWrapper<ReportAgentStaticDayPO> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();

            if(reportAgentStaticDay.getTeamAgentNum()!=null&&reportAgentStaticDay.getTeamAgentNum()>0){
                lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getTeamAgentNum,reportAgentStaticDay.getTeamAgentNum());
            }
            if(reportAgentStaticDay.getDirectReportNum()!=null&&reportAgentStaticDay.getDirectReportNum()>0){
                lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getDirectReportNum,reportAgentStaticDay.getDirectReportNum());
            }
            //注册人数
            if(reportAgentStaticDay.getRegisterUserNum()!=null&&reportAgentStaticDay.getRegisterUserNum()>0){
                lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getRegisterUserNum,reportAgentStaticDay.getRegisterUserNum());
            }
            //首存人数
            if(reportAgentStaticDay.getFirstDepositNum()!=null&&reportAgentStaticDay.getFirstDepositNum()>0){
                lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getFirstDepositNum,reportAgentStaticDay.getFirstDepositNum());
            }
            //首存转换率
            if(reportAgentStaticDay.getFirstDepositRate()!=null&&reportAgentStaticDay.getFirstDepositRate().compareTo(BigDecimal.ZERO)!=0){
                lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getFirstDepositRate,reportAgentStaticDay.getFirstDepositRate());
            }
            //投注人数
            if(reportAgentStaticDay.getBetUserNum()!=null&&reportAgentStaticDay.getBetUserNum()>0){
                //投注人数 去重
                lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getBetUserNum,reportAgentStaticDay.getBetUserNum());
            }

            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getBetAmount,reportAgentStaticDayPODb.getBetAmount().add(reportAgentStaticDay.getBetAmount()));
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getRebateAmount,reportAgentStaticDayPODb.getRebateAmount().add(reportAgentStaticDay.getRebateAmount()));
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getValidAmount,reportAgentStaticDayPODb.getValidAmount().add(reportAgentStaticDay.getValidAmount()));
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getWinLossAmountUser,reportAgentStaticDayPODb.getWinLossAmountUser().add(reportAgentStaticDay.getWinLossAmountUser()));
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getWinLossAmountPlat,reportAgentStaticDayPODb.getWinLossAmountPlat().add(reportAgentStaticDay.getWinLossAmountPlat()));
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getAdjustAmount,reportAgentStaticDayPODb.getAdjustAmount().add(reportAgentStaticDay.getAdjustAmount()));
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getVipAmount,reportAgentStaticDayPODb.getVipAmount().add(reportAgentStaticDay.getVipAmount()));
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getActivityAmount,reportAgentStaticDayPODb.getActivityAmount().add(reportAgentStaticDay.getActivityAmount()));
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getAlreadyUseAmount,reportAgentStaticDayPODb.getAlreadyUseAmount().add(reportAgentStaticDay.getAlreadyUseAmount()));
            BigDecimal tipsAmount=reportAgentStaticDay.getTipsAmount()==null?BigDecimal.ZERO:reportAgentStaticDay.getTipsAmount();
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getTipsAmount,reportAgentStaticDayPODb.getTipsAmount().add(tipsAmount));
            BigDecimal platAdjustAmount=reportAgentStaticDay.getPlatAdjustAmount()==null?BigDecimal.ZERO:reportAgentStaticDay.getPlatAdjustAmount();
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getPlatAdjustAmount,reportAgentStaticDayPODb.getPlatAdjustAmount().add(platAdjustAmount));
            BigDecimal riskAmount=reportAgentStaticDay.getRiskAmount()==null?BigDecimal.ZERO:reportAgentStaticDay.getRiskAmount();
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getRiskAmount,reportAgentStaticDayPODb.getRiskAmount().add(riskAmount));

            BigDecimal betAmount=reportAgentStaticDayPODb.getBetAmount().add(reportAgentStaticDay.getBetAmount());
            BigDecimal winLossAmountPlat=reportAgentStaticDayPODb.getWinLossAmountPlat().add(reportAgentStaticDay.getWinLossAmountPlat());
            //盈亏比例 等于 平台总输赢 / 投注额
            BigDecimal winLossRate=AmountUtils.divide(winLossAmountPlat,betAmount,4);
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getWinLossRate,winLossRate);

            //平台收入 等于 平台总输赢 - 调整金额 - 已使用活动优惠 - 代理佣金
            //lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getPlatIncome,reportAgentStaticDayPODb.getPlatIncome().add(reportAgentStaticDay.getPlatIncome()));

            //存提手续费
            if(reportAgentStaticDay.getDepositWithdrawFeeAmount()!=null){
                lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getDepositWithdrawFeeAmount,reportAgentStaticDayPODb.getDepositWithdrawFeeAmount().add(reportAgentStaticDay.getDepositWithdrawFeeAmount()));
            }
            if(reportAgentStaticDay.getDepositTotalAmount()!=null){
                lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getDepositTotalAmount,reportAgentStaticDayPODb.getDepositTotalAmount().add(reportAgentStaticDay.getDepositTotalAmount()));
            }
            if(reportAgentStaticDay.getWithdrawTotalAmount()!=null){
                lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getWithdrawTotalAmount,reportAgentStaticDayPODb.getWithdrawTotalAmount().add(reportAgentStaticDay.getWithdrawTotalAmount()));
            }
            lambdaUpdateWrapper.set(ReportAgentStaticDayPO::getUpdatedTime,System.currentTimeMillis());
            lambdaUpdateWrapper.eq(ReportAgentStaticDayPO::getId,reportAgentStaticDayPODb.getId());
            log.info("开始更新:{},统计数据:{}",reportAgentStaticDay.getAgentId(),reportAgentStaticDay);
            this.update(lambdaUpdateWrapper);
        }else {
            //填充代理 团队人数 直属下级人数
            fillAgentNum(reportAgentStaticDay);
            reportAgentStaticDay.setCreatedTime(System.currentTimeMillis());
            reportAgentStaticDay.setUpdatedTime(System.currentTimeMillis());
            reportAgentStaticDay.setId(null);
            log.info("开始记录:{},统计数据:{}",reportAgentStaticDay.getAgentId(),reportAgentStaticDay);
            if (Objects.isNull(reportAgentStaticDay.getReportType())){
                reportAgentStaticDay.setReportType("0");
            }
            this.baseMapper.insert(reportAgentStaticDay);
        }

    }

    /**
     * 分页查询
     * @param reportAgentStaticsPageVO
     * @return
     */
    public ResponseVO<ReportAgentStaticsResult> listPage(ReportAgentStaticsPageVO reportAgentStaticsPageVO) {
        //过滤代理信息
        List<AgentInfoVO> agentInfoVOS=Lists.newArrayList();
        if(
                reportAgentStaticsPageVO.getStartRegisterDay()!=null ||
                reportAgentStaticsPageVO.getEndRegisterDay()!=null ||
                StringUtils.hasText(reportAgentStaticsPageVO.getAgentAccount())||
                StringUtils.hasText(reportAgentStaticsPageVO.getSuperAgentAccount())||
                StringUtils.hasText(reportAgentStaticsPageVO.getAgentCategory())||
                StringUtils.hasText(reportAgentStaticsPageVO.getAgentType())||
                StringUtils.hasText(reportAgentStaticsPageVO.getInviteCode())
        ){
            AgentInfoCondVo agentInfoCondVo=new AgentInfoCondVo();
            BeanUtils.copyProperties(reportAgentStaticsPageVO,agentInfoCondVo);
            agentInfoVOS=agentInfoApi.getAgentListByCond(agentInfoCondVo);
            if(CollectionUtils.isEmpty(agentInfoVOS)){
                //按照查询条件没有查询出来代理信息 直接返回空
                return ResponseVO.success(new ReportAgentStaticsResult());
            }
        }
        String timeZone=siteApi.getSiteDetail(reportAgentStaticsPageVO.getSiteCode()).getTimezone();
        log.info("siteCode:{},timeZone:{}",reportAgentStaticsPageVO.getSiteCode(),timeZone);
        //站点平台币兑换汇率
        Map<String,BigDecimal> rateMap=siteCurrencyInfoApi.getAllFinalRate(reportAgentStaticsPageVO.getSiteCode());
        Page<ReportAgentStaticDayPO> page =new Page<ReportAgentStaticDayPO>(reportAgentStaticsPageVO.getPageNumber(),reportAgentStaticsPageVO.getPageSize());
        LambdaQueryWrapper<ReportAgentStaticDayPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportAgentStaticDayPO::getSiteCode,reportAgentStaticsPageVO.getSiteCode());
        //代理信息查询
        if(!CollectionUtils.isEmpty(agentInfoVOS)) {
            List<String> queryAgentIds = agentInfoVOS.stream().map(AgentInfoVO::getAgentId).toList();
            lambdaQueryWrapper.in(ReportAgentStaticDayPO::getAgentId, queryAgentIds);
        }

        //报表类型 字典类型:report_type  0:日报 1:月报
        lambdaQueryWrapper.eq(ReportAgentStaticDayPO::getReportType,reportAgentStaticsPageVO.getReportType());
        if(reportAgentStaticsPageVO.getStartStaticDay()!=null){
            lambdaQueryWrapper.ge(ReportAgentStaticDayPO::getDayMillis,reportAgentStaticsPageVO.getStartStaticDay());
        }

        if(reportAgentStaticsPageVO.getEndStaticDay()!=null){
            lambdaQueryWrapper.le(ReportAgentStaticDayPO::getDayMillis,reportAgentStaticsPageVO.getEndStaticDay());
        }


/*
        if(reportAgentStaticsPageVO.getMinCommissionAmount()!=null){
            lambdaQueryWrapper.ge(ReportAgentStaticDayPO::getCommissionAmount,reportAgentStaticsPageVO.getMinCommissionAmount());
        }

        if(reportAgentStaticsPageVO.getMaxCommissionAmount()!=null){
            lambdaQueryWrapper.le(ReportAgentStaticDayPO::getCommissionAmount,reportAgentStaticsPageVO.getMaxCommissionAmount());
        }*/

        if(StringUtils.hasText(reportAgentStaticsPageVO.getCurrencyCode())){
            lambdaQueryWrapper.eq(ReportAgentStaticDayPO::getCurrencyCode,reportAgentStaticsPageVO.getCurrencyCode());
        }

        //平台收入类型 0:正收入 1:负收入
        /*if(reportAgentStaticsPageVO.getPlatWinLossType()!=null && reportAgentStaticsPageVO.getPlatWinLossType()==0){
            lambdaQueryWrapper.gt(ReportAgentStaticDayPO::getPlatIncome,0);
        }

        if(reportAgentStaticsPageVO.getPlatWinLossType()!=null && reportAgentStaticsPageVO.getPlatWinLossType()==1){
            lambdaQueryWrapper.lt(ReportAgentStaticDayPO::getPlatIncome,0);
        }*/

        if (StringUtils.hasText(reportAgentStaticsPageVO.getOrderField()) && StringUtils.hasText(reportAgentStaticsPageVO.getOrderType())) {
           /* if (reportAgentStaticsPageVO.getOrderField().equals("registerTime") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getRegisterTime);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("registerTime") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getRegisterTime);
            }*/
            if (reportAgentStaticsPageVO.getOrderField().equals("directReportNum") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getDirectReportNum);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("directReportNum") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getDirectReportNum);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("firstDepositRate") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getFirstDepositRate);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("firstDepositRate") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getFirstDepositRate);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("winLossRate") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getWinLossRate);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("winLossRate") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getWinLossRate);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("betAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getBetAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("betAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getBetAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("validAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getValidAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("validAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getValidAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("winLossAmountUser") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getWinLossAmountUser);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("winLossAmountUser") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getWinLossAmountUser);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("winLossAmountPlat") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getWinLossAmountPlat);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("winLossAmountPlat") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getWinLossAmountPlat);
            }


            if (reportAgentStaticsPageVO.getOrderField().equals("adjustAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getAdjustAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("adjustAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getAdjustAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("activityAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getActivityAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("activityAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getActivityAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("vipAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getVipAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("vipAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getVipAmount);
            }


            if (reportAgentStaticsPageVO.getOrderField().equals("alreadyUseAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getAlreadyUseAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("alreadyUseAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getAlreadyUseAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("rebateAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getRebateAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("rebateAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getRebateAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("tipsAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getTipsAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("tipsAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getTipsAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("riskAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getRiskAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("riskAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getRiskAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("platAdjustAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getPlatAdjustAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("platAdjustAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getPlatAdjustAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("withdrawTotalAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getWithdrawTotalAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("withdrawTotalAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getWithdrawTotalAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("depositTotalAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getDepositTotalAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("depositTotalAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getDepositTotalAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("depositWithdrawFeeAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getDepositWithdrawFeeAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("depositWithdrawFeeAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getDepositWithdrawFeeAmount);
            }


           /* if (reportAgentStaticsPageVO.getOrderField().equals("commissionAmount") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getCommissionAmount);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("commissionAmount") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getCommissionAmount);
            }

            if (reportAgentStaticsPageVO.getOrderField().equals("platIncome") && reportAgentStaticsPageVO.getOrderType().equals("asc")) {
                lambdaQueryWrapper.orderByAsc(ReportAgentStaticDayPO::getPlatIncome);
            }
            if (reportAgentStaticsPageVO.getOrderField().equals("platIncome") && reportAgentStaticsPageVO.getOrderType().equals("desc")) {
                lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getPlatIncome);
            }*/
        }else{
            lambdaQueryWrapper.orderByDesc(ReportAgentStaticDayPO::getDayMillis);
        }

        Page<ReportAgentStaticDayPO> reportAgentStaticDayPOPage=this.baseMapper.selectPage(page,lambdaQueryWrapper);
        List<ReportAgentStaticsResponseVO> reportAgentStaticsResponseVOS=Lists.newArrayList();
        Page<ReportAgentStaticsResponseVO> reportAgentStaticsResponseVOPage=new Page<ReportAgentStaticsResponseVO>();
        BeanUtils.copyProperties(reportAgentStaticDayPOPage,reportAgentStaticsResponseVOPage);
        ReportAgentStaticsResponseVO currentPage=new ReportAgentStaticsResponseVO();
        List<String> allAgentIds=Lists.newArrayList();
        if(!CollectionUtils.isEmpty(reportAgentStaticDayPOPage.getRecords())){
            List<String> agentIds=reportAgentStaticDayPOPage.getRecords().stream().map(ReportAgentStaticDayPO::getAgentId).toList();
            List<AgentInfoVO>  agentInfoVOList=agentInfoApi.getByAgentIds(agentIds);

            List<String> riskLevelIds = agentInfoVOList.stream().map(o -> o.getRiskLevelId()).toList();
            Map<String, RiskLevelDetailsVO> riskMap = riskApi.getByIds(riskLevelIds);
            List<String> agentLabelIds = agentInfoVOList.stream().map(o -> o.getAgentLabelId()).toList();
            List<AgentLabelVO> agentLabelVOS=agentLabelApi.getAgentLabelByAgentLabelIds(agentLabelIds);
            for(ReportAgentStaticDayPO reportAgentStaticDayPO:reportAgentStaticDayPOPage.getRecords()){
                ReportAgentStaticsResponseVO reportAgentStaticsResponseVO=new ReportAgentStaticsResponseVO();
                BeanUtils.copyProperties(reportAgentStaticDayPO,reportAgentStaticsResponseVO);
                //代理信息填充
                fillAgentInfo(reportAgentStaticsResponseVO,agentInfoVOList,riskMap,agentLabelVOS);

                allAgentIds.add(reportAgentStaticDayPO.getAgentId());

                reportAgentStaticsResponseVO.setRegisterTimeStr(TimeZoneUtils.formatTimestampToTimeZone(reportAgentStaticsResponseVO.getRegisterTime(), timeZone));

                reportAgentStaticsResponseVO.setActivityAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                reportAgentStaticsResponseVO.setValidAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                reportAgentStaticsResponseVO.setVipAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
               // reportAgentStaticsResponseVO.setCommissionAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                //reportAgentStaticsResponseVO.setPlatIncomeCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                BigDecimal winLossRate=reportAgentStaticDayPO.getWinLossRate();
                reportAgentStaticsResponseVO.setWinLossRateBigDecimal(winLossRate);
                reportAgentStaticsResponseVO.setWinLossRate(AmountUtils.formatPercent(winLossRate));

                reportAgentStaticsResponseVO.setFirstDepositRate(AmountUtils.formatPercent(reportAgentStaticDayPO.getFirstDepositRate()));
                reportAgentStaticsResponseVO.setFirstDepositRateBigDecimal(reportAgentStaticDayPO.getFirstDepositRate());

                 // 人数需要去重 当页不统计
                //团队代理人数
                //currentPage.addTeamAgentNum(reportAgentStaticDayPO.getTeamAgentNum());
                currentPage.setTeamAgentNum(null);
                //直属下级人数
               // currentPage.addDirectReportNum(reportAgentStaticDayPO.getDirectReportNum());
                currentPage.setDirectReportNum(null);
                //注册人数
                //currentPage.addRegisterUserNum(reportAgentStaticDayPO.getRegisterUserNum());
                currentPage.setRegisterUserNum(null);
                //首存人数
                //currentPage.addFirstDepositNum(reportAgentStaticDayPO.getFirstDepositNum());
                currentPage.setFirstDepositNum(null);
                //首存转换率=首存人数 / 注册人数
               // BigDecimal  convertRate= NumberUtil.divide(currentPage.getFirstDepositNum(),currentPage.getRegisterUserNum());
                //currentPage.setFirstDepositRate(AmountUtils.formatPercent(reportAgentStaticDayPO.getFirstDepositRate()));
                currentPage.setFirstDepositRate(null);
                //投注人数
                currentPage.setBetUserNum(null);

                //当前页 汇总全是平台币
                BigDecimal transferRate=rateMap.get(reportAgentStaticDayPO.getCurrencyCode());
                //投注金额 汇总不需要转换为平台币 直接累加
               // BigDecimal betAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getBetAmount(),transferRate);
               // currentPage.addBetAmount(betAmountWtc);
                currentPage.addBetAmount(reportAgentStaticDayPO.getBetAmount());
               // currentPage.setBetAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                //有效投注金额
               // BigDecimal validAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getValidAmount(),transferRate);
               // currentPage.addValidAmount(validAmountWtc);
                currentPage.addValidAmount(reportAgentStaticDayPO.getValidAmount());
                //currentPage.setValidAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                //会员输赢
               // BigDecimal winLossAmountUserWtc=AmountUtils.divide(reportAgentStaticDayPO.getWinLossAmountUser(),transferRate);
              //  currentPage.addWinLossAmountUser(winLossAmountUserWtc);
               // currentPage.setWinLossAmountUserCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                currentPage.addWinLossAmountUser(reportAgentStaticDayPO.getWinLossAmountUser());
                //返水金额
                currentPage.addRebateAmount(reportAgentStaticDayPO.getRebateAmount());
                //平台总输赢
               // BigDecimal winLossAmountPlatWtc=AmountUtils.divide(reportAgentStaticDayPO.getWinLossAmountPlat(),transferRate);
               // currentPage.addWinLossAmountPlat(winLossAmountPlatWtc);
                //currentPage.setWinLossAmountPlatCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                currentPage.addWinLossAmountPlat(reportAgentStaticDayPO.getWinLossAmountPlat());
                //调整金额
                //BigDecimal adjustAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getAdjustAmount(),transferRate);
                //currentPage.addAdjustAmount(adjustAmountWtc);
               // currentPage.setAdjustAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                currentPage.addAdjustAmount(reportAgentStaticDayPO.getAdjustAmount());

                //盈亏比例 等于 平台总输赢 / 投注额
                BigDecimal winLossRateCurrent=reportAgentStaticDayPO.getWinLossRate();
                currentPage.setWinLossRateBigDecimal(winLossRateCurrent);
                currentPage.setWinLossRate(AmountUtils.formatPercent(winLossRateCurrent));

                //活动优惠
                currentPage.addActivityAmount(reportAgentStaticDayPO.getActivityAmount());
              //  currentPage.setActivityAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                //VIP福利
                currentPage.addVipAmount(reportAgentStaticDayPO.getVipAmount());
              //  currentPage.setVipAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                //已使用优惠
                currentPage.addAlreadyUseAmount(reportAgentStaticDayPO.getAlreadyUseAmount());
               // currentPage.setAlreadyUseAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                //存提手续费
                currentPage.addDepositWithdrawFeeAmount(reportAgentStaticDayPO.getDepositWithdrawFeeAmount());

                //代理佣金
               // currentPage.addCommissionAmount(reportAgentStaticDayPO.getCommissionAmount());
               // currentPage.setCommissionAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                //平台收入
                //currentPage.addPlatIncome(reportAgentStaticDayPO.getPlatIncome());
               // currentPage.setPlatIncomeCurrency(CommonConstant.PLAT_CURRENCY_CODE);

               // currentPage.setPlatCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);

                //投注金额
                reportAgentStaticsResponseVO.setBetAmountCurrency(reportAgentStaticDayPO.getCurrencyCode());
                //有效投注金额
                reportAgentStaticsResponseVO.setValidAmountCurrency(reportAgentStaticDayPO.getCurrencyCode());

                reportAgentStaticsResponseVO.setRebateAmountCurrency(reportAgentStaticDayPO.getCurrencyCode());
                //会员输赢
                reportAgentStaticsResponseVO.setWinLossAmountUserCurrency(reportAgentStaticDayPO.getCurrencyCode());
                //平台总输赢
                reportAgentStaticsResponseVO.setWinLossAmountPlatCurrency(reportAgentStaticDayPO.getCurrencyCode());
                //调整金额
                reportAgentStaticsResponseVO.setAdjustAmountCurrency(reportAgentStaticDayPO.getCurrencyCode());
                //已使用优惠
                reportAgentStaticsResponseVO.setAlreadyUseAmountCurrency(reportAgentStaticsResponseVO.getCurrencyCode());

                //打赏金额币种
                reportAgentStaticsResponseVO.setTipsAmountCurrency(reportAgentStaticsResponseVO.getCurrencyCode());
                //风控金额币种
                reportAgentStaticsResponseVO.setRiskAmountCurrency(reportAgentStaticsResponseVO.getCurrencyCode());
                //调整金额(其他调整)-平台币
                reportAgentStaticsResponseVO.setPlatAdjustAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                //存提手续费
                reportAgentStaticsResponseVO.setDepositWithdrawFeeAmountCurrency(reportAgentStaticsResponseVO.getCurrencyCode());
                //存款总额
                reportAgentStaticsResponseVO.setDepositTotalAmount(reportAgentStaticsResponseVO.getDepositTotalAmount());
                reportAgentStaticsResponseVO.setDepositTotalAmountCurrency(reportAgentStaticsResponseVO.getCurrencyCode());
                //提款总额
                reportAgentStaticsResponseVO.setWithdrawTotalAmount(reportAgentStaticsResponseVO.getWithdrawTotalAmount());
                reportAgentStaticsResponseVO.setWithdrawTotalAmountCurrency(reportAgentStaticsResponseVO.getCurrencyCode());

                //平台币调整金额
                currentPage.addPlatAdjustAmount(reportAgentStaticDayPO.getPlatAdjustAmount());

                //打赏金额
                currentPage.addTipsAmount(reportAgentStaticDayPO.getTipsAmount());
                //风控金额
                currentPage.addRiskAmount(reportAgentStaticDayPO.getRiskAmount());


                //是否转换成平台币
                if(reportAgentStaticsPageVO.isTransferPlatformFlag()){
                    //投注金额
                    BigDecimal betAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getBetAmount(),transferRate);
                    reportAgentStaticsResponseVO.setBetAmount(betAmountWtc);
                    reportAgentStaticsResponseVO.setBetAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //有效投注金额
                    BigDecimal validAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getValidAmount(),transferRate);
                    reportAgentStaticsResponseVO.setValidAmount(validAmountWtc);
                    reportAgentStaticsResponseVO.setValidAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                    //返水金额
                    BigDecimal rebateAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getRebateAmount(),transferRate);
                    reportAgentStaticsResponseVO.setRebateAmount(rebateAmountWtc);
                    reportAgentStaticsResponseVO.setRebateAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                    //会员输赢
                    BigDecimal winLossAmountUserWtc=AmountUtils.divide(reportAgentStaticDayPO.getWinLossAmountUser(),transferRate);
                    reportAgentStaticsResponseVO.setWinLossAmountUser(winLossAmountUserWtc);
                    reportAgentStaticsResponseVO.setWinLossAmountUserCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //平台总输赢
                    BigDecimal winLossAmountPlatWtc=AmountUtils.divide(reportAgentStaticDayPO.getWinLossAmountPlat(),transferRate);
                    reportAgentStaticsResponseVO.setWinLossAmountPlat(winLossAmountPlatWtc);
                    reportAgentStaticsResponseVO.setWinLossAmountPlatCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //调整金额
                    BigDecimal adjustAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getAdjustAmount(),transferRate);
                    reportAgentStaticsResponseVO.setAdjustAmount(adjustAmountWtc);
                    reportAgentStaticsResponseVO.setAdjustAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                    //已使用优惠
                    BigDecimal getAlreadyUseAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getAlreadyUseAmount(),transferRate);
                    reportAgentStaticsResponseVO.setAlreadyUseAmount(getAlreadyUseAmountWtc);
                    reportAgentStaticsResponseVO.setAlreadyUseAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                    //存提手续费
                    BigDecimal depositWithDrawFeeWtc=AmountUtils.divide(reportAgentStaticDayPO.getDepositWithdrawFeeAmount(),transferRate);
                    reportAgentStaticsResponseVO.setDepositWithdrawFeeAmount(depositWithDrawFeeWtc);
                    reportAgentStaticsResponseVO.setDepositWithdrawFeeAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //存款总额
                    BigDecimal depositTotalAmount=AmountUtils.divide(reportAgentStaticsResponseVO.getDepositTotalAmount(),transferRate,4);
                    reportAgentStaticsResponseVO.setDepositTotalAmount(depositTotalAmount);
                    reportAgentStaticsResponseVO.setDepositTotalAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //提款总额
                    BigDecimal withdrawTotalAmount=AmountUtils.divide(reportAgentStaticsResponseVO.getWithdrawTotalAmount(),transferRate,4);
                    reportAgentStaticsResponseVO.setWithdrawTotalAmount(withdrawTotalAmount);
                    reportAgentStaticsResponseVO.setWithdrawTotalAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                    //打赏金额
                    BigDecimal tipsAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getTipsAmount(),transferRate);
                    reportAgentStaticsResponseVO.setTipsAmount(tipsAmountWtc);
                    reportAgentStaticsResponseVO.setTipsAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //风控金额
                    BigDecimal riskAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getRiskAmount(),transferRate);
                    reportAgentStaticsResponseVO.setRiskAmount(riskAmountWtc);
                    reportAgentStaticsResponseVO.setRiskAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                }
               // reportAgentStaticsResponseVO.setPlatCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                reportAgentStaticsResponseVOS.add(reportAgentStaticsResponseVO);
            }
        }


        reportAgentStaticsResponseVOPage.setRecords(reportAgentStaticsResponseVOS);

        ReportAgentStaticsResult reportAgentStaticsResult=new ReportAgentStaticsResult();
        reportAgentStaticsResult.setPageList(reportAgentStaticsResponseVOPage);
        reportAgentStaticsResult.setCurrentPage(currentPage);
        // 汇总
        ReportAgentStaticsResponseVO totalPage=new ReportAgentStaticsResponseVO();
        List<ReportAgentStaticDayPO> reportAgentStaticDayPOList=this.baseMapper.selectTotal(lambdaQueryWrapper);

        if(!CollectionUtils.isEmpty(reportAgentStaticDayPOList)){
            for(ReportAgentStaticDayPO reportAgentStaticDayPO:reportAgentStaticDayPOList){
                //汇总页需要全部转换为平台币
                BigDecimal transferRate=rateMap.get(reportAgentStaticDayPO.getCurrencyCode());
                //团队代理人数
             //   totalPage.addTeamAgentNum(reportAgentStaticDayPO.getTeamAgentNum());
                //直属下级人数
              //  totalPage.addDirectReportNum(reportAgentStaticDayPO.getDirectReportNum());
                //注册人数
                totalPage.addRegisterUserNum(reportAgentStaticDayPO.getRegisterUserNum());
                //首存人数
                totalPage.addFirstDepositNum(reportAgentStaticDayPO.getFirstDepositNum());
                //首存转换率=首存人数 / 注册人数
                BigDecimal  convertRate= NumberUtil.divide(totalPage.getFirstDepositNum(),totalPage.getRegisterUserNum(),4);
                totalPage.setFirstDepositRateBigDecimal(convertRate);
                totalPage.setFirstDepositRate(AmountUtils.formatPercent(convertRate));

                //是否转换成平台币
                if(reportAgentStaticsPageVO.isTransferPlatformFlag()){
                    //投注金额
                      BigDecimal betAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getBetAmount(),transferRate);
                      totalPage.addBetAmount(betAmountWtc);
                    //有效投注金额
                     BigDecimal validAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getValidAmount(),transferRate);
                     totalPage.addValidAmount(validAmountWtc);

                     //返水金额
                    BigDecimal rebateAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getRebateAmount(),transferRate);
                    totalPage.addRebateAmount(rebateAmountWtc);

                    //会员输赢
                      BigDecimal winLossAmountUserWtc=AmountUtils.divide(reportAgentStaticDayPO.getWinLossAmountUser(),transferRate);
                     totalPage.addWinLossAmountUser(winLossAmountUserWtc);
                    //平台总输赢
                    BigDecimal winLossAmountPlatWtc=AmountUtils.divide(reportAgentStaticDayPO.getWinLossAmountPlat(),transferRate);
                    totalPage.addWinLossAmountPlat(winLossAmountPlatWtc);
                    //  totalPage.setWinLossAmountPlatCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //调整金额
                      BigDecimal adjustAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getAdjustAmount(),transferRate);
                      totalPage.addAdjustAmount(adjustAmountWtc);
                    //      totalPage.setAdjustAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //盈亏比例 等于 平台总输赢 / 投注额
                    BigDecimal winLossRate=AmountUtils.divide(totalPage.getWinLossAmountPlat(),totalPage.getBetAmount(),4);
                    totalPage.setWinLossRateBigDecimal(winLossRate);
                    totalPage.setWinLossRate(AmountUtils.formatPercent(winLossRate));
                    //活动优惠
                    totalPage.addActivityAmount(reportAgentStaticDayPO.getActivityAmount());
                    //   totalPage.setActivityAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //VIP福利
                    totalPage.addVipAmount(reportAgentStaticDayPO.getVipAmount());
                    //  totalPage.setVipAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //已使用优惠
                    BigDecimal alreadyUseAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getAlreadyUseAmount(),transferRate);
                    totalPage.addAlreadyUseAmount(alreadyUseAmountWtc);
                    //  totalPage.setAlreadyUseAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                    //存取手续费
                    BigDecimal depositWithdrawFeeAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getDepositWithdrawFeeAmount(),transferRate);
                    totalPage.addDepositWithdrawFeeAmount(depositWithdrawFeeAmountWtc);
                    //存款总额
                    BigDecimal depositTotalAmount=AmountUtils.divide(reportAgentStaticDayPO.getDepositTotalAmount(),transferRate);
                    totalPage.addDepositTotalAmount(depositTotalAmount);
                    //提款总额
                    BigDecimal withdrawTotalAmount=AmountUtils.divide(reportAgentStaticDayPO.getWithdrawTotalAmount(),transferRate);
                    totalPage.addWithdrawTotalAmount(withdrawTotalAmount);
                    //代理佣金
                  //  totalPage.addCommissionAmount(reportAgentStaticDayPO.getCommissionAmount());
                    //  totalPage.setCommissionAmountCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    //平台收入
                   // totalPage.addPlatIncome(reportAgentStaticDayPO.getPlatIncome());
                    //  totalPage.setPlatIncomeCurrency(CommonConstant.PLAT_CURRENCY_CODE);

                    //打赏金额
                    BigDecimal tipsAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getTipsAmount(),transferRate);
                    totalPage.addTipsAmount(tipsAmountWtc);
                    //风控金额
                    BigDecimal riskAmountWtc=AmountUtils.divide(reportAgentStaticDayPO.getRiskAmount(),transferRate);
                    totalPage.addRiskAmount(riskAmountWtc);

                    totalPage.addPlatAdjustAmount(reportAgentStaticDayPO.getPlatAdjustAmount());
                }else {
                    //投注金额
                    totalPage.addBetAmount(reportAgentStaticDayPO.getBetAmount());
                    //有效投注金额
                    totalPage.addValidAmount(reportAgentStaticDayPO.getValidAmount());

                    //返水金额
                    totalPage.addRebateAmount(reportAgentStaticDayPO.getRebateAmount());

                    //会员输赢
                    totalPage.addWinLossAmountUser(reportAgentStaticDayPO.getWinLossAmountUser());
                    //平台总输赢
                    totalPage.addWinLossAmountPlat(reportAgentStaticDayPO.getWinLossAmountPlat());
                    //调整金额
                    totalPage.addAdjustAmount(reportAgentStaticDayPO.getAdjustAmount());
                    //盈亏比例 等于 平台总输赢 / 投注额
                    BigDecimal winLossRate=AmountUtils.divide(totalPage.getWinLossAmountPlat(),totalPage.getBetAmount(),4);
                    totalPage.setWinLossRateBigDecimal(winLossRate);
                    totalPage.setWinLossRate(AmountUtils.formatPercent(winLossRate));
                    //活动优惠
                    totalPage.addActivityAmount(reportAgentStaticDayPO.getActivityAmount());
                    //VIP福利
                    totalPage.addVipAmount(reportAgentStaticDayPO.getVipAmount());
                    //已使用优惠
                    totalPage.addAlreadyUseAmount(reportAgentStaticDayPO.getAlreadyUseAmount());
                    //存取手续费
                    totalPage.addDepositWithdrawFeeAmount(reportAgentStaticDayPO.getDepositWithdrawFeeAmount());
                    //存款总额

                    totalPage.addDepositTotalAmount(reportAgentStaticDayPO.getDepositTotalAmount());
                    //提款总额

                    totalPage.addWithdrawTotalAmount(reportAgentStaticDayPO.getWithdrawTotalAmount());
                    //代理佣金
                  //  totalPage.addCommissionAmount(reportAgentStaticDayPO.getCommissionAmount());
                    //平台收入
                   // totalPage.addPlatIncome(reportAgentStaticDayPO.getPlatIncome());

                    totalPage.addPlatAdjustAmount(reportAgentStaticDayPO.getPlatAdjustAmount());
                    totalPage.addTipsAmount(reportAgentStaticDayPO.getTipsAmount());
                    totalPage.addRiskAmount(reportAgentStaticDayPO.getRiskAmount());
                }

            }
            //投注人数 去除重复
           // reportAgentStaticsPageVO.setAgentIds(allAgentIds);
            Long betUserNum=reportAgentStaticBetService.staticBetUserNum(reportAgentStaticsPageVO,agentInfoVOS);
            totalPage.setBetUserNum(betUserNum);
        }
        //团队代理人数
        totalPage.setTeamAgentNum(null);
        //直属人数
        totalPage.setDirectReportNum(null);
        reportAgentStaticsResult.setTotalPage(totalPage);
        return ResponseVO.success(reportAgentStaticsResult);
    }


    /**
     * 代理信息填充
     * @param reportAgentStaticsResponseVO 返回信息
     * @param agentInfoVOList 代理信息
     */
    private void fillAgentInfo(ReportAgentStaticsResponseVO reportAgentStaticsResponseVO, List<AgentInfoVO> agentInfoVOList, Map<String, RiskLevelDetailsVO> riskMap, List<AgentLabelVO> agentLabelVOS) {
        AgentInfoVO agentInfoVO=agentInfoVOList.stream().filter(o->o.getAgentId().equals(reportAgentStaticsResponseVO.getAgentId())).findFirst().get();
        //上级代理
        reportAgentStaticsResponseVO.setParentId(agentInfoVO.getParentId());
        reportAgentStaticsResponseVO.setParentAccount(agentInfoVO.getParentAccount());
        //代理信息填充
        reportAgentStaticsResponseVO.setPath(agentInfoVO.getPath());
        reportAgentStaticsResponseVO.setLevel(agentInfoVO.getLevel());
        // 层级名称
        if (null != agentInfoVO.getLevel()) {
            AgentLevelEnum agentLevelEnum = AgentLevelEnum.nameOfCode(agentInfoVO.getLevel());
            if (null != agentLevelEnum) {
                reportAgentStaticsResponseVO.setLevelName(agentLevelEnum.getName());
            }
        }
        reportAgentStaticsResponseVO.setAgentType(agentInfoVO.getAgentType());
        reportAgentStaticsResponseVO.setAgentCategory(agentInfoVO.getAgentCategory());
        reportAgentStaticsResponseVO.setRegisterTime(agentInfoVO.getRegisterTime());
        reportAgentStaticsResponseVO.setAgentAttribution(agentInfoVO.getAgentAttribution());
        reportAgentStaticsResponseVO.setAgentLabelId(agentInfoVO.getAgentLabelId());
        reportAgentStaticsResponseVO.setRiskLevelId(agentInfoVO.getRiskLevelId());
        reportAgentStaticsResponseVO.setInviteCode(agentInfoVO.getInviteCode());

        // 风控层级
        if (null != agentInfoVO.getRiskLevelId()) {
            RiskLevelDetailsVO riskLevelDetailsVO = riskMap.get(agentInfoVO.getRiskLevelId());
            reportAgentStaticsResponseVO.setRiskLevel(null == riskLevelDetailsVO ? null : riskLevelDetailsVO.getRiskControlLevel());
        }

        // 代理标签
        if (null != agentInfoVO.getAgentLabelId()) {
            List<AgentLabelVO> agentLabelPOList = agentLabelVOS.stream().filter(o -> agentInfoVO.getAgentLabelId().contains(o.getId())).toList();
            if (!CollectionUtils.isEmpty(agentLabelPOList)) {
                List<String> agentLabelNames = agentLabelPOList.stream().map(o -> o.getName()).toList();
                String labelNames = String.join(CommonConstant.COMMA, agentLabelNames);
                reportAgentStaticsResponseVO.setAgentLabel(labelNames);
            }
        }

    }
}
