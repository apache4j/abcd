package com.cloud.baowang.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.system.po.risk.RiskAccountPO;
import com.cloud.baowang.system.repositories.RiskAccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



/**
 * @author rudger
 * @Date 2023.05.03
 */
@Slf4j
@Service
@AllArgsConstructor
public class RiskAccountService extends ServiceImpl<RiskAccountRepository, RiskAccountPO> {

    private final RiskAccountRepository riskAccountRepository;

}
