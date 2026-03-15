package com.cloud.baowang.wallet.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawReviewPageResVO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalAuditPO;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalAuditRepository;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserDepositWithdrawalAuditService  {

    private final UserDepositWithdrawalAuditRepository userDepositWithdrawalAuditRepository;


    /**
     * 获取订单审核信息
     * @param orderNoList
     * @return
     */
    public Map<String, List<UserDepositWithdrawalAuditPO>> getAuditInfoMap(List<String> orderNoList){
        Map<String, List<UserDepositWithdrawalAuditPO>> auditInfoMap = new HashMap<>();
        if(!orderNoList.isEmpty()){
            LambdaQueryWrapper<UserDepositWithdrawalAuditPO > lqw = new LambdaQueryWrapper<>();
            lqw.in(UserDepositWithdrawalAuditPO::getOrderNo,orderNoList);
            List<UserDepositWithdrawalAuditPO> list = userDepositWithdrawalAuditRepository.selectList(lqw);
            auditInfoMap = list.stream()
                    .collect(Collectors.groupingBy(UserDepositWithdrawalAuditPO::getOrderNo));
        }
        return auditInfoMap;
    }
}
