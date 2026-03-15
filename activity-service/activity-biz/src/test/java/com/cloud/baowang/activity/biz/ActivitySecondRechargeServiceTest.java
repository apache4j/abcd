package com.cloud.baowang.activity.biz;

import com.cloud.baowang.activity.ActivityApplication;
import com.cloud.baowang.activity.service.consumer.ActivityUserRechargeListener;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/11/4 13:17
 * @Version: V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivityApplication.class)
public class ActivitySecondRechargeServiceTest {

   // private ActivitySecondRechargeService activitySecondRechargeService;

    @Resource
    private ActivityUserRechargeListener activityUserRechargeListener;

    @Test
    public void testSecondRecharge(){
        //RechargeTriggerVO(depositType=1, userId=62074731, userAccount=newuser1, orderNumber=null, rechargeTime=1730538684873, rechargeAmount=3000.00, currencyCode=USDT, platformCurrency=null, totalRecharge=null)
        RechargeTriggerVO triggerVO=new RechargeTriggerVO();
        triggerVO.setDepositType(1);
        triggerVO.setUserId("62074731");
        triggerVO.setUserAccount("newuser1");
        triggerVO.setRechargeTime(1730538684873L);
        triggerVO.setRechargeAmount(new BigDecimal("3000"));
        triggerVO.setCurrencyCode(CurrencyEnum.USDT.getCode());
        activityUserRechargeListener.memberRechargeMessage(triggerVO,null);
    }
}
