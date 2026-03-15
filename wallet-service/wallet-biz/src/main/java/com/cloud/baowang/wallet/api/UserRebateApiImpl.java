package com.cloud.baowang.wallet.api;

import com.cloud.baowang.wallet.api.vo.rebate.OrderRebateRequestVO;
import com.cloud.baowang.wallet.api.api.UserRebateApi;
import com.cloud.baowang.wallet.service.VIPAwardRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


import java.util.Date;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 12/6/24 7:18 PM
 * @Version : 1.0
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserRebateApiImpl implements UserRebateApi {

    private final VIPAwardRecordService vipAwardRecordService;

    @Override
    public void recordUserRebate(List<OrderRebateRequestVO> rebateRequestVOList) {
        vipAwardRecordService.recordUserRebate(rebateRequestVOList);
    }

    @Override
    public void vipUpgradeAward(Date upgradeStart, Date upgradeEnd) {
        vipAwardRecordService.vipUpgradeAward(upgradeStart, upgradeEnd);
    }

}
