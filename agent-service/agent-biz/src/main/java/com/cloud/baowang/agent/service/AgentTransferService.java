package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentTransferDirection;
import com.cloud.baowang.agent.api.enums.AgentTransferEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferInfoVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferPageRecordVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordTotalVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.info.AgentPayPasswordParam;
import com.cloud.baowang.agent.api.vo.withdrawConfig.AgentWithdrawConfigVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentTransferRecordPO;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.agent.repositories.AgentTransferRecordRepository;
import com.cloud.baowang.agent.repositories.AgentWithdrawConfigRepository;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogPageVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogReqVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.TransferStatusEnum;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author : 小智
 * @Date : 19/10/23 7:59 PM
 * @Version : 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentTransferService extends ServiceImpl<AgentTransferRecordRepository, AgentTransferRecordPO> {

    private final AgentWithdrawConfigRepository agentWithdrawConfigRepository;
    private final AgentQuotaCoinService agentQuotaCoinService;
    private final AgentInfoRepository agentInfoRepository;
    private final AgentCommissionCoinService agentCommissionCoinService;
    private final AgentTransferRecordRepository agentTransferRecordRepository;
    private final AgentWithdrawConfigService agentWithdrawConfigService;

    private final AgentInfoService agentInfoService;
    private final AgentSocketService agentSocketService;

    private final AgentCommonCoinService agentCommonCoinService;


    public ResponseVO<AgentTransferVO> queryAgentTransfer(final AgentDetailParam param) {
        try {
            AgentTransferVO vo = new AgentTransferVO();
            AgentWithdrawConfigVO agentWithdrawConfigVO =  agentWithdrawConfigService
                    .getWithdrawConfigByAgentAccount(param.getAgentAccount());
            LambdaQueryWrapper<AgentInfoPO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AgentInfoPO::getAgentAccount, param.getAgentAccount());
            wrapper.eq(AgentInfoPO::getSiteCode,param.getSiteCode());
            AgentInfoPO agentInfoPO = agentInfoRepository.selectOne(wrapper);
            if(null != agentInfoPO){
                //支付密码检验
                vo.setIsPayPassword(ObjectUtil.isNotEmpty(agentInfoPO.getPayPassword())
                        ? BigDecimal.ZERO.intValue() : BigDecimal.ONE.intValue());
            }
            // 最小转账额度 同银行卡最小提款额度
            //vo.setMinTransAmount(agentWithdrawConfigVO.getBankCardWithdrawMinQuota()); //fixme
            // 最大转账额度 同银行卡最大提款额度
            //vo.setMaxTransAmount(agentWithdrawConfigVO.getBankCardWithdrawMaxQuota());
            vo.setQuotaCoinBalance(agentQuotaCoinService.getQuotaCoinBalanceSite(param.getAgentAccount(),agentInfoPO.getSiteCode()).getAvailableAmount());
            vo.setCommissionCoinBalance(agentCommissionCoinService.getCommissionCoinBalanceSite(
                    param.getAgentAccount(),agentInfoPO.getSiteCode()).getAvailableAmount());
            // 该代理已经转账的额度
            /*LambdaQueryWrapper<AgentTransferRecordPO> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(AgentTransferRecordPO::getAgentAccount, param.getAgentAccount());
            queryWrapper1.eq(AgentTransferRecordPO::getTransferStatus, TransferStatusEnum.SUCCESS.getCode());
            queryWrapper1.gt(AgentTransferRecordPO::getTransferTime, DateUtil.beginOfDay(new Date()).getTime());
            queryWrapper1.lt(AgentTransferRecordPO::getTransferTime, DateUtil.endOfDay(new Date()).getTime());
            List<AgentTransferRecordPO> list = agentTransferRecordRepository.selectList(queryWrapper1);
            BigDecimal successAmount = ObjectUtil.isEmpty(list) ? BigDecimal.ZERO : list.stream()
                    .map(AgentTransferRecordPO::getTransferAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            vo.setRemainingTodayAmount(agentWithdrawConfigVO.getDayWithdrawTotalAmount().subtract(successAmount)
                    .compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : agentWithdrawConfigVO
                    .getDayWithdrawTotalAmount().subtract(successAmount));*/
            return ResponseVO.success(vo);
        } catch (Exception e) {
            log.error("该代理:{} 代理转账钱包信息出现异常", param.getAgentAccount(), e);
            return ResponseVO.fail(ResultCode.QUERY_AGENT_TRANSFER_WALLET_ERROR);
        }
    }

    public ResponseVO<?> verifyPayPassword(final AgentPayPasswordParam param) {
        LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        AgentInfoPO po = agentInfoRepository.selectOne(queryWrapper.eq(AgentInfoPO::getAgentAccount,
                param.getAgentAccount()));
        String payPasswordVoEncrypt = AgentServerUtil.getEncryptPassword(param.getPayPassword(), po.getSalt());
        if(!payPasswordVoEncrypt.equals(po.getPayPassword())){
            return ResponseVO.fail(ResultCode.PAYPASSWORD_ERROR);
        }
        return ResponseVO.success();
    }

    public ResponseVO<?> saveAgentTransfer(final AgentTransferParam param) {
        try {
            BigDecimal transferAmount = param.getTransferAmount();
            if (transferAmount.compareTo(BigDecimal.ZERO) == 0) {
                return ResponseVO.fail(ResultCode.AMOUNT_CANNOT_BE_ZERO);
            }
            if (transferAmount.compareTo(BigDecimal.ZERO) < 0 || (transferAmount.scale() > 0 && transferAmount.stripTrailingZeros().scale() > 0)) {
                return ResponseVO.fail(ResultCode.AMOUNT_CAN_ONLY_BE_INTEGER);
            }
            AgentCoinRecordTypeEnum.AgentWalletTypeEnum walletTypeEnum=AgentCoinRecordTypeEnum.AgentWalletTypeEnum.nameOfCode(param.getWalletType().toString());
            if (walletTypeEnum == null) {
                log.info("传入代理钱包类型参数不正确:{}",param);
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }

            AgentTransferRecordPO po = new AgentTransferRecordPO();
            LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentInfoPO::getAgentAccount, param.getUnderAgentAccount());
            // 下级代理账号信息
            AgentInfoVO underAgentInfoVO = agentInfoService.getByAgentAccountAndSite(param.getUnderAgentAccount(),param.getSiteCode());
            if(null == underAgentInfoVO){
                return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXISTS);
            }
            // 当前代理账户信息
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentInfoPO::getAgentAccount, param.getAgentAccount());
            queryWrapper.eq(AgentInfoPO::getSiteCode, param.getSiteCode());
            AgentInfoPO currentAgentInfoPO = agentInfoRepository.selectOne(queryWrapper);
            AgentInfoVO currentAgentInfoVo = ConvertUtil.entityToModel(currentAgentInfoPO,AgentInfoVO.class);
            // 当前账号是否存在
            if(null == currentAgentInfoPO){
                return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXISTS);
            }
            if(currentAgentInfoPO.getIsAgentArrears() == 1 && (param.getIsCommission() == null || !param.getIsCommission())){
                return ResponseVO.fail(ResultCode.AGENT_ARREARS_NOT_TRANSFER);
            }
            // 充提限制 标识是3
            // 你的账号存在异常，请联系客服咨询
            if(Arrays.asList(currentAgentInfoPO.getStatus().split(",")).contains("3")){
                return ResponseVO.fail(ResultCode.AGENT_TRANSFER_ERROR);
            }

            AgentCoinBalanceVO agentCoinBalanceVO = null;
            //账户余额是否充足
            if(param.getWalletType().toString().equals(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode())){
                agentCoinBalanceVO =  agentCommissionCoinService.getCommissionCoinBalanceSite(param.getAgentAccount(),param.getSiteCode());
            }else{
                agentCoinBalanceVO = agentQuotaCoinService.getQuotaCoinBalanceSite(param.getAgentAccount(),param.getSiteCode());
            }
            if(agentCoinBalanceVO.getAvailableAmount().compareTo(transferAmount) < 0 ){
                return ResponseVO.fail(ResultCode.AGENT_TRANSFER_COIN_NOT_ENOUGH);
            }
            //校验支付密码
            String payPasswordVoEncrypt = AgentServerUtil.getEncryptPassword(param.getPayPassword(),
                    currentAgentInfoPO.getSalt());
            if(!payPasswordVoEncrypt.equals(currentAgentInfoPO.getPayPassword())){
                return ResponseVO.fail(ResultCode.PAYPASSWORD_ERROR);
            }
            // 输入账号不是您的直属下级
            if(!Objects.equals(underAgentInfoVO.getParentId(), currentAgentInfoPO.getAgentId())){
                return ResponseVO.fail(ResultCode.AGENT_PARENT_ERROR);
            }
            // 保存转账记录
            String orderNo = "AZ" + SnowFlakeUtils.getSnowId();
            po.setSiteCode(param.getSiteCode());
            po.setAgentId(param.getAgentId());
            po.setAgentAccount(param.getAgentAccount());
            po.setTransferType(String.valueOf(param.getWalletType()));
            po.setRemark(param.getRemark());
            po.setTransferAmount(transferAmount);
            po.setTransferTime(System.currentTimeMillis());
            po.setTransferAgentId(underAgentInfoVO.getAgentId());
            po.setTransferAccount(param.getUnderAgentAccount());
            po.setOrderNo(orderNo);
            po.setReportDay(param.getReportDay());

            // 账变记录，打码量
            if(addTransferCoin(param, orderNo,underAgentInfoVO,currentAgentInfoVo)){
                po.setTransferStatus(TransferStatusEnum.SUCCESS.getCode());
                log.info("AGENT_TRANSFERS SUCCESS : walletType : " + param.getWalletType() + " 当前代理钱包 : " + agentCoinBalanceVO +" 账号状态 : "+currentAgentInfoPO.getStatus() + " 下级代理id : " +underAgentInfoVO.getAgentId() + " 转账金额 : "+param.getTransferAmount());
            }else{
                po.setTransferStatus(TransferStatusEnum.FAIL.getCode());
                log.info("AGENT_TRANSFERS FAIL : walletType : " + param.getWalletType() + " 当前代理钱包 : " + agentCoinBalanceVO +" 账号状态 : "+currentAgentInfoPO.getStatus() + " 下级代理id : " +underAgentInfoVO.getAgentId() + " 转账金额 : "+param.getTransferAmount());
            }
            agentTransferRecordRepository.insert(po);
            // 转账成功通知
            agentSocketService.sendAgentTransferSocket(SystemMessageEnum.AGENT_TRANSFERS,param.getSiteCode(),param.getAgentId(), WSSubscribeEnum.AGENT_SYSTEM_NOTICE.getTopic(),param.getUnderAgentAccount());
            // 到账通知
            agentSocketService.sendAgentTransferSocket(SystemMessageEnum.AGENT_ARRIVAL,param.getSiteCode(),underAgentInfoVO.getAgentId(), WSSubscribeEnum.AGENT_SYSTEM_NOTICE.getTopic(),transferAmount,CurrReqUtils.getPlatCurrencyCode());
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("该代理:{} 给下级代理;{} 转账发生异常", param.getAgentAccount(),
                    param.getUnderAgentAccount(), e);
            return ResponseVO.fail(ResultCode.SAVE_AGENT_TRANSFER_ERROR);
        }
    }

    private boolean addTransferCoin(final AgentTransferParam param, final String orderNo,AgentInfoVO underAgentInfoVO,AgentInfoVO currentAgentInfoVo) {
        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentAccount(param.getAgentAccount());
        agentCoinAddVO.setOrderNo(orderNo);
        agentCoinAddVO.setCoinValue(param.getTransferAmount());
//        agentCoinAddVO.setRemark(param.getRemark());
        agentCoinAddVO.setRemark("转给"+param.getUnderAgentAccount());
        param.setRemark("上级转入");
        //agentCoinAddVO.setCurrency(CurrencyEnum.CNY.getCode());
        agentCoinAddVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        agentCoinAddVO.setBalanceType(AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());
        if(param.getWalletType().toString().equals(AgentCoinRecordTypeEnum
                .AgentWalletTypeEnum.COMMISSION_WALLET.getCode())){
            agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.
                    COMMISSION_WALLET.getCode());
            agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum
                    .AGENT_TRANSFER.getCode());
            agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum
                    .TRANSFER_SUBORDINATES.getCode());
            agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.
                    AGENT_TRANSFER.getCode());

            // 上级转下级生成账变成功
            agentCoinAddVO.setAgentInfo(currentAgentInfoVo);

            AgentCoinAddVO commissionAddVO = addTransferUnderIncomeCoin(param, orderNo,underAgentInfoVO);
            agentCommonCoinService.agentTransferCoin(agentCoinAddVO,commissionAddVO);
            /*if(agentCommissionCoinService.addCommissionCoin(agentCoinAddVO)){
                // 下级收到并同样生成一条下级收到上级转入账变记录
                return addTransferUnderIncomeCoin(param, orderNo,underAgentInfoVO);
            }*/
        }else{
            agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum
                    .QUOTA_WALLET.getCode());
            agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum
                    .AGENT_TRANSFER.getCode());
            agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum
                    .TRANSFER_SUBORDINATES.getCode());
            agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum
                    .AGENT_TRANSFER.getCode());
            // 上级转下级生成账变成功
            agentCoinAddVO.setAgentInfo(currentAgentInfoVo);

            AgentCoinAddVO quotaAddVO = addTransferUnderIncomeCoin(param, orderNo,underAgentInfoVO);
            agentCommonCoinService.agentTransferCoin(agentCoinAddVO,quotaAddVO);
            /*if(agentQuotaCoinService.addQuotaCoin(agentCoinAddVO)){
                // 下级收到并同样生成一条下级收到上级转入账变记录
                AgentCoinAddVO quotaAddVO = addTransferUnderIncomeCoin(param, orderNo,underAgentInfoVO);
            }*/
        }
        return false;
    }

    private AgentCoinAddVO addTransferUnderIncomeCoin(final AgentTransferParam param, final String orderNo, final AgentInfoVO agentInfoVO) {
        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentAccount(param.getUnderAgentAccount());
        agentCoinAddVO.setOrderNo(orderNo);
        agentCoinAddVO.setCoinValue(param.getTransferAmount());
        agentCoinAddVO.setRemark(param.getRemark());
        agentCoinAddVO.setCurrency(CurrencyEnum.CNY.getCode());
        agentCoinAddVO.setBalanceType(AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        agentCoinAddVO.setAgentInfo(agentInfoVO);
        if(param.getWalletType().toString().equals(AgentCoinRecordTypeEnum
                .AgentWalletTypeEnum.COMMISSION_WALLET.getCode())){
            agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.
                    COMMISSION_WALLET.getCode());
            agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum
                    .AGENT_TRANSFER.getCode());
            agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum
                    .SUPERIOR_TRANSFER.getCode());
            agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum
                    .AGENT_TRANSFER.getCode());

//            return agentCommissionCoinService.addCommissionCoin(agentCoinAddVO);
        }else{
            agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum
                    .QUOTA_WALLET.getCode());
            agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum
                    .AGENT_TRANSFER.getCode());
            agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum
                    .SUPERIOR_TRANSFER.getCode());
            agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum
                    .AGENT_TRANSFER.getCode());
//            return agentQuotaCoinService.addQuotaCoin(agentCoinAddVO);
        }
        return agentCoinAddVO;
    }

    public ResponseVO<Page<AgentTransferPageRecordVO>> queryAgentTransferRecord(final AgentTransferRecordParam param) {
        try {
            if(param.getDateNum() != 9999){
                long startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(),
                        param.getDateNum())).getTime();
                if(param.getDateNum() != -1 && param.getDateNum() != 0){
                    startTime  = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(),
                            param.getDateNum() + 1)).getTime();
                }
                long endTime = DateUtil.endOfDay(new Date()).getTime();
                if(param.getDateNum() == -1){
                    endTime = DateUtil.endOfDay(DateUtil.offsetDay(new Date(),
                            param.getDateNum())).getTime();
                }
                param.setStartTime(startTime);
                param.setEndTime(endTime);
            }
            LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentInfoPO::getAgentAccount, param.getAgentAccount());
            if(null == agentInfoRepository.selectOne(queryWrapper)){
                return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXISTS);
            }
            if(!param.getIsMe()){
                queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AgentInfoPO::getAgentAccount, param.getAgentAccount());
                queryWrapper.eq(AgentInfoPO::getParentId, param.getAgentId());
                if(null == agentInfoRepository.selectOne(queryWrapper)){
                    return ResponseVO.fail(ResultCode.AGENT_PARENT_ERROR);
                }
            }
            Page<AgentTransferRecordPO> page = new Page<>(param.getPageNumber(), param.getPageSize());
            Page<AgentTransferPageRecordVO> result = agentTransferRecordRepository.queryAgentTransferRecord(page,
                    param);
            result.getRecords().forEach(obj->{
                obj.setTransferTypeName(AgentTransferEnum.nameOfCode(obj.getTransferType()).getName());
                obj.setTransferStatusName(TransferStatusEnum.nameOfCode(obj.getTransferStatus()).getName());
                // 收支角度为上级转入 显示上级账号，角度为转给下级显示下级账号
                obj.setTransferAccount(param.getDirection().equals(AgentTransferDirection
                        .SUPER_AGENT.getCode()) ? obj.getAgentAccount() : obj.getTransferAccount());
                String date = DateUtil.format(new Date(obj.getTransferTime()), DatePattern.NORM_DATE_PATTERN);
                obj.setTransferGroupTime(DateUtil.parse(date, DatePattern.NORM_DATE_PATTERN).getTime());
                obj.setDirection(param.getDirection());
                obj.setDirectionName(AgentTransferDirection.nameOfCode(param.getDirection()).getName());
            });
//            List<AgentTransferPageRecordVO> list = result.getRecords();
//            Map<Long, List<AgentTransferPageRecordVO>> map = list.stream().collect(Collectors
//                    .groupingBy(AgentTransferPageRecordVO::getTransferGroupTime));
//            Map<Long, List<AgentTransferPageRecordVO>> resultMap = Maps.newHashMap();
//            map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(obj->
//                    resultMap.put(obj.getKey(), obj.getValue()));
//            for(Map.Entry<Long, List<AgentTransferPageRecordVO>> keyMap : resultMap.entrySet()){
//                AgentTransferRecordVO vo = new AgentTransferRecordVO();
//                vo.setGroupTime(keyMap.getKey());
//                vo.setRecordVO(keyMap.getValue().stream().sorted(Comparator
//                        .comparing(AgentTransferPageRecordVO::getTransferTime).reversed()).toList());
//                voList.add(vo);
//            }
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询代理转账记录发生异常", e);
            return ResponseVO.fail(ResultCode.AGENT_TRANSFER_RECORD_ERROR);
        }
    }

    public ResponseVO<AgentTransferRecordTotalVO> queryAgentTransferRecordPage(
            final AgentTransferRecordPageParam param) {
        try {
            AgentTransferRecordTotalVO vo = new AgentTransferRecordTotalVO();
            Page<AgentTransferRecordPO> page = new Page<>(param.getPageNumber(), param.getPageSize());
            Page<AgentTransferRecordPageVO> result = agentTransferRecordRepository.queryAgentTransferRecordPage(page, param);
            result.getRecords().forEach(obj->{
                obj.setAgentWalletTypeName(AgentCoinRecordTypeEnum.AgentWalletTypeEnum
                        .nameOfCode(obj.getAgentWalletType()).getName());
                obj.setOrderStatusName(TransferStatusEnum.nameOfCode(obj.getOrderStatus()).getName());
            });
            vo.setPageList(result);
            // 本页合计
            AgentTransferRecordPageVO currentVO = new AgentTransferRecordPageVO();
            currentVO.setOrderNo("小计");
            currentVO.setTransferAmount(result.getRecords().stream().map(AgentTransferRecordPageVO::getTransferAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            vo.setCurrentPage(currentVO);
            // 全部合计
            AgentTransferRecordPageVO totalVO = new AgentTransferRecordPageVO();
            totalVO.setOrderNo("总计");
            totalVO.setTransferAmount(agentTransferRecordRepository.queryTotalTransferRecord(param));
            vo.setTotalPage(totalVO);
            return ResponseVO.success(vo);
        } catch (Exception e) {
            log.error("代理转账记录查询发生异常", e);
            return ResponseVO.fail(ResultCode.AGENT_TRANSFER_RECORD_ERROR);
        }
    }

    public AgentTransferInfoVO getAgentTransferInfo(String agentAccount) {
        LambdaQueryWrapper<AgentTransferRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentTransferRecordPO::getAgentAccount,agentAccount);
        lqw.eq(AgentTransferRecordPO::getTransferStatus, CommonConstant.business_zero);

        List<AgentTransferRecordPO> list = this.baseMapper.selectList(lqw);
        AgentTransferInfoVO agentTransferInfoVO = new AgentTransferInfoVO();
        agentTransferInfoVO.setTransferAmount(BigDecimal.ZERO);
        agentTransferInfoVO.setTransferNum(CommonConstant.business_zero);
        agentTransferInfoVO.setCommissionCoinTransferNum(CommonConstant.business_zero);
        agentTransferInfoVO.setCommissionCoinTransferAmount(BigDecimal.ZERO);
        agentTransferInfoVO.setQuotaCoinTransferNum(CommonConstant.business_zero);
        agentTransferInfoVO.setQuotaCoinTransferAmount(BigDecimal.ZERO);

        if(null != list && !list.isEmpty()) {
            BigDecimal totalAmount = list.stream().map(AgentTransferRecordPO::getTransferAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            agentTransferInfoVO.setTransferAmount(totalAmount);
            agentTransferInfoVO.setTransferNum(list.size());
            Map<String, List<AgentTransferRecordPO>> group = list.stream()
                    .collect(Collectors.groupingBy(AgentTransferRecordPO::getTransferType));
            //佣金钱包转账
            List<AgentTransferRecordPO> commissionTransferRecordList = group.get(AgentTransferEnum.COMMISSION.getCode());
            if (null != commissionTransferRecordList && !commissionTransferRecordList.isEmpty()) {
                BigDecimal amount = commissionTransferRecordList.stream().map(AgentTransferRecordPO::getTransferAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                agentTransferInfoVO.setCommissionCoinTransferAmount(amount);
                agentTransferInfoVO.setCommissionCoinTransferNum(commissionTransferRecordList.size());
            }
            //额度钱包转账
            List<AgentTransferRecordPO> quotaTransferRecordList = group.get(AgentTransferEnum.QUOTA.getCode());
            if (null != quotaTransferRecordList && !quotaTransferRecordList.isEmpty()) {
                BigDecimal amount = quotaTransferRecordList.stream().map(AgentTransferRecordPO::getTransferAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                agentTransferInfoVO.setQuotaCoinTransferAmount(amount);
                agentTransferInfoVO.setQuotaCoinTransferNum(quotaTransferRecordList.size());
            }
        }
        return agentTransferInfoVO;
    }

    /**
     * 平台转总代
     * @param param
     * @return
     */
    public ResponseVO<?> savePlayerToCommissionTransfer(final AgentTransferParam param) {
        try {
            AgentTransferRecordPO po = new AgentTransferRecordPO();
            // 总代账户信息
            LambdaQueryWrapper<AgentInfoPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentInfoPO::getAgentAccount, param.getAgentAccount());
            AgentInfoPO currentAgentInfoPO = agentInfoRepository.selectOne(queryWrapper);
            // 当前账号是否存在
            if(null == currentAgentInfoPO){
                return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXISTS);
            }
            // 你的账号存在异常，请联系客服咨询
            if(currentAgentInfoPO.getRemoveRechargeLimit() == BigDecimal.ONE.intValue()){
                return ResponseVO.fail(ResultCode.AGENT_TRANSFER_ERROR);
            }
            // 保存转账记录
            String orderNo = "AZ" + SnowFlakeUtils.getSnowId();
            po.setAgentId(param.getAgentId());
            po.setAgentAccount("playes");
            po.setTransferAmount(param.getTransferAmount());
            po.setTransferType(String.valueOf(param.getWalletType()));
            po.setRemark(param.getRemark());
            po.setTransferTime(System.currentTimeMillis());
            po.setTransferAccount(param.getAgentAccount());
            po.setOrderNo(orderNo);
            po.setReportDay(param.getReportDay());
            // 账变记录
            if(addPlayToCommissionTransferCoin(param, orderNo)){
                po.setTransferStatus(TransferStatusEnum.SUCCESS.getCode());
            }else{
                po.setTransferStatus(TransferStatusEnum.FAIL.getCode());
            }
            agentTransferRecordRepository.insert(po);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("平台转总代: {} 转账发生异常", param.getAgentAccount(), e);
            return ResponseVO.fail(ResultCode.SAVE_AGENT_TRANSFER_ERROR);
        }
    }

    private boolean addPlayToCommissionTransferCoin(AgentTransferParam param, String orderNo) {
        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentAccount(param.getAgentAccount());
        agentCoinAddVO.setCoinValue(param.getTransferAmount());
        agentCoinAddVO.setOrderNo(orderNo);
        agentCoinAddVO.setRemark(param.getRemark());
        agentCoinAddVO.setCurrency(CurrencyEnum.CNY.getCode());
        agentCoinAddVO.setBalanceType(AgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        if(param.getWalletType().toString().equals(AgentCoinRecordTypeEnum
                .AgentWalletTypeEnum.COMMISSION_WALLET.getCode())){
            agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.
                    COMMISSION_WALLET.getCode());
            agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum
                    .AGENT_COMMISSION.getCode());
            agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum
                    .PROMOTIONS_ADD.getCode());
            agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum
                    .COMMISSION.getCode());
            return agentCommissionCoinService.addCommissionCoin(agentCoinAddVO);
        }else{
            return false;
        }
    }

    public Page<AgentDistributeLogPageVO> distributeLog(AgentDistributeLogReqVO vo) {
        return agentTransferRecordRepository.distributeLog(new Page<>(vo.getPageNumber(),vo.getPageSize()), vo);
    }

    public Long siteQueryAgentTransferRecordCount(AgentTransferRecordPageParam vo) {
        return agentTransferRecordRepository.siteQueryAgentTransferRecordCount( vo);
    }


    /**
     * 分页查询
     * @param vo
     * @return
     */
    public Page<AgentTransferPageRecordVO> listPage(AgentTransferRecordPageReqVO vo) {
        Page<AgentTransferRecordPO> page=new Page<AgentTransferRecordPO>(vo.getPageNumber(),vo.getPageSize());
        LambdaQueryWrapper<AgentTransferRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentTransferRecordPO::getSiteCode, vo.getSiteCode());
        if (vo.getStartTransferTime() != null) {
            queryWrapper.ge(AgentTransferRecordPO::getTransferTime, vo.getStartTransferTime());
        }
        if (vo.getEndTransferTime() != null) {
            queryWrapper.le(AgentTransferRecordPO::getTransferTime, vo.getEndTransferTime());
        }
        if (vo.getTransferStatus() != null) {
            queryWrapper.eq(AgentTransferRecordPO::getTransferStatus, vo.getTransferStatus());
        }
        Page<AgentTransferRecordPO> agentTransferRecordPOPage = agentTransferRecordRepository.selectPage(page, queryWrapper);
        IPage<AgentTransferPageRecordVO> result = agentTransferRecordPOPage.convert(item -> {
            AgentTransferPageRecordVO resp = BeanUtil.copyProperties(item, AgentTransferPageRecordVO.class);
            return resp;
        });
        return ConvertUtil.toConverPage(result);

    }
}
