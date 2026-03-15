package com.cloud.baowang.agent.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackWithdrawParamVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualDetailReqVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualPageReqVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualPayReqVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualRecordPageResVO;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualRecordlDetailVO;
import com.cloud.baowang.agent.po.AgentWithdrawalManualRecordPO;
import com.cloud.baowang.agent.repositories.AgentWithdrawalManualRecordRepository;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@AllArgsConstructor
public class AgentWithdrawManualRecordService extends ServiceImpl<AgentWithdrawalManualRecordRepository, AgentWithdrawalManualRecordPO> {

    private final AgentWithdrawalManualRecordRepository agentWithdrawalManualRecordRepository;

    private final AgentDepositWithdrawCallbackService agentDepositWithdrawCallbackService;
    public Page<AgentWithdrawManualRecordPageResVO> withdrawManualPage(AgentWithdrawManualPageReqVO vo) {

        Page<AgentWithdrawalManualRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<AgentWithdrawalManualRecordPO> lqw = buildLqw(vo);

        Page<AgentWithdrawalManualRecordPO> agentWithdrawReviewPageResVOPage = agentWithdrawalManualRecordRepository.selectPage(page, lqw);

        Page<AgentWithdrawManualRecordPageResVO> agentWithdrawManualRecordPageResVOPage=new Page<AgentWithdrawManualRecordPageResVO>(vo.getPageNumber(), vo.getPageSize());
        BeanUtils.copyProperties(agentWithdrawReviewPageResVOPage, agentWithdrawManualRecordPageResVOPage);
        agentWithdrawManualRecordPageResVOPage.setTotal(agentWithdrawReviewPageResVOPage.getTotal());
        agentWithdrawManualRecordPageResVOPage.setPages(agentWithdrawReviewPageResVOPage.getPages());
        return agentWithdrawManualRecordPageResVOPage;
    }

    public AgentWithdrawManualRecordlDetailVO withdrawManualDetail(AgentWithdrawManualDetailReqVO vo) {
        AgentWithdrawalManualRecordPO agentWithdrawalManualRecordPO = this.getById(vo.getId());
        if (null == agentWithdrawalManualRecordPO) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        AgentWithdrawManualRecordlDetailVO result = ConvertUtil.entityToModel(agentWithdrawalManualRecordPO, AgentWithdrawManualRecordlDetailVO.class);

        return result;
    }

    @DistributedLock(name = RedisConstants.USER_WITHDRAW_MANUAL_ORDER_ID, unique = "#vo.id", waitTime = 60, leaseTime = 180)
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> withdrawManualPay(AgentWithdrawManualPayReqVO vo) {
        AgentWithdrawalManualRecordPO agentWithdrawalManualRecordPO = this.getById(vo.getId());
        if(!AgentDepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode().equals(agentWithdrawalManualRecordPO.getCustomerStatus())){
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }
        agentWithdrawalManualRecordPO.setCustomerStatus(vo.getCustomerStatus());
        agentWithdrawalManualRecordPO.setFileKey(vo.getFileKey());
        agentWithdrawalManualRecordPO.setUpdatedTime(System.currentTimeMillis());
        this.updateById(agentWithdrawalManualRecordPO);
        //回调出款
        AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
        paramVO.setAmount(agentWithdrawalManualRecordPO.getApplyAmount());
        paramVO.setOrderNo(agentWithdrawalManualRecordPO.getOrderNo());
        paramVO.setPayId("");
        paramVO.setRemark("人工提款");
        paramVO.setStatus(Integer.parseInt(vo.getCustomerStatus()));
        boolean result = agentDepositWithdrawCallbackService.agentWithdrawCallback(paramVO);
        if(!result){
            throw new BaowangDefaultException(ResultCode.WITHDRAW_FAIL);
        }
        return ResponseVO.success(true);
    }
    public LambdaQueryWrapper<AgentWithdrawalManualRecordPO> buildLqw(AgentWithdrawManualPageReqVO vo) {
        LambdaQueryWrapper<AgentWithdrawalManualRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.in(AgentWithdrawalManualRecordPO::getCustomerStatus, vo.getCustomerStatusList());
        lqw.eq(AgentWithdrawalManualRecordPO::getSiteCode,vo.getSiteCode());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()),AgentWithdrawalManualRecordPO::getOrderNo,vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentAccount()),AgentWithdrawalManualRecordPO::getAgentAccount,vo.getAgentAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getCurrencyCode()),AgentWithdrawalManualRecordPO::getCurrencyCode,vo.getCurrencyCode());
        lqw.eq(StringUtils.isNotBlank(vo.getWithdrawWayId()),AgentWithdrawalManualRecordPO::getDepositWithdrawWayId,vo.getWithdrawWayId());
        lqw.ge(null != vo.getStartTime(),AgentWithdrawalManualRecordPO::getUpdatedTime,vo.getStartTime());
        lqw.le(null != vo.getEndTime(),AgentWithdrawalManualRecordPO::getUpdatedTime,vo.getEndTime());
        lqw.orderByDesc(AgentWithdrawalManualRecordPO::getCreatedTime);
        return lqw;
    }

    public Long withdrawalManualRecordPageCount(AgentWithdrawManualPageReqVO vo) {
        LambdaQueryWrapper<AgentWithdrawalManualRecordPO> lqw = buildLqw(vo);
        return agentWithdrawalManualRecordRepository.selectCount(lqw);
    }
}
