package com.cloud.baowang.wallet.api;


import com.cloud.baowang.agent.api.api.AgentDepositWithdrawApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawRespVO;
import com.cloud.baowang.wallet.api.enums.wallet.OwnerUserTypeEnum;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.wallet.api.api.DepositWithdrawOrderQueryApi;
import com.cloud.baowang.wallet.api.vo.DepositWithdrawOrderQueryResponseVO;
import com.cloud.baowang.wallet.service.UserDepositWithdrawService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class DepositWithdrawOrderQueryApiImpl implements DepositWithdrawOrderQueryApi {

    private final UserDepositWithdrawService userDepositWithdrawService;

    private final AgentDepositWithdrawApi agentDepositWithdrawApi;


    @Override
    public DepositWithdrawOrderQueryResponseVO queryOrderByOrderNo(String orderNo) {
        log.info("订单查询参数orderNo{}",orderNo);
        DepositWithdrawOrderQueryResponseVO depositWithdrawOrderQueryResponseVO = new DepositWithdrawOrderQueryResponseVO();
        UserDepositWithdrawalResVO userDepositWithdrawalResVO = userDepositWithdrawService.getRecordByOrderId(orderNo);
        if(null != userDepositWithdrawalResVO){
            depositWithdrawOrderQueryResponseVO.setOwnerUserType(OwnerUserTypeEnum.USER.getCode());
            depositWithdrawOrderQueryResponseVO.setOrderType(userDepositWithdrawalResVO.getType());
        }else{
            AgentDepositWithdrawRespVO agentDepositWithdrawRespVO =  agentDepositWithdrawApi.getDepositWithdrawOrderByOrderNo(orderNo);
            if(null != agentDepositWithdrawRespVO){
                depositWithdrawOrderQueryResponseVO.setOwnerUserType(OwnerUserTypeEnum.AGENT.getCode());
                depositWithdrawOrderQueryResponseVO.setOrderType(agentDepositWithdrawRespVO.getType());
            }
        }
        return depositWithdrawOrderQueryResponseVO;
    }
}
