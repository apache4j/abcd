import com.cloud.baowang.account.AccountApplication;
import com.cloud.baowang.account.api.AccountAgentApiImpl;
import com.cloud.baowang.account.api.AccountPlatformCoinApiImpl;
import com.cloud.baowang.account.api.AccountUserApiImpl;
import com.cloud.baowang.account.api.enums.*;
import com.cloud.baowang.account.api.enums.activity.AccountActivityTemplateEnum;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.wildfly.common.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@AutoConfigureMockMvc
public class AccountActivityTest {

    @Resource
    private AccountUserApiImpl accountUserApiImpl;

    @Resource
    private AccountPlatformCoinApiImpl accountPlatformCoinApi;
    /**
     * 会员VIP福利发放  -晋级奖励-主货币
     */
    @Test
    public void uservipJJJL(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("10002");
        reqVO.setUserId("75765545");
        reqVO.setUserAccount("qiqi005");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.USD.getCode());
        reqVO.setUserName("PAZq4mEY");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("uservip000000001");
        reqVO.setThirdOrderNo("thirduservip0000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.OFFLINE_BONUS.getCode());
        reqVO.setActivityFlag(AccountActivityTemplateEnum.PROMOTION_BONUS.getCode());
        reqVO.setCoinValue(new BigDecimal("50"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApiImpl.userBalanceCoin(reqVO);
    }


    /**
     * 会员活动优惠发放
     */
    @Test
    public void userhdyhff(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("10002");
        reqVO.setUserId("75765545");
        reqVO.setUserAccount("qiqi005");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.USD.getCode());
        reqVO.setUserName("PAZq4mEY");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("userhdyh000000001");
        reqVO.setThirdOrderNo("thirdhdyh0000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.PROMOTIONS.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.OFFLINE_BONUS.getCode());
        reqVO.setActivityFlag(AccountActivityTemplateEnum.FIRST_DEPOSIT.getCode());
        reqVO.setCoinValue(new BigDecimal("210"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApiImpl.userBalanceCoin(reqVO);
    }



    /**
     * 会员VIP福利发放 -晋级奖励-平台币
     */
    @Test
    public void uservipJJJLWTC(){
        AccountUserPlatformCoinAddReqVO reqVO = new AccountUserPlatformCoinAddReqVO();
        reqVO.setSiteCode("10002");
        reqVO.setUserId("75765545");
        reqVO.setUserAccount("qiqi005");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO.setUserName("PAZq4mEY");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("pmvipff000000001");
        reqVO.setThirdOrderNo("thirdpmvipff0000001");
        reqVO.setToThirdCode("10002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setActivityFlag(AccountActivityTemplateEnum.PROMOTION_BONUS.getCode());
        reqVO.setCoinValue(new BigDecimal("222"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 平台币-会员活动发放-首存
     */
    @Test
    public void platformCoinHdPff(){
        AccountUserPlatformCoinAddReqVO reqVO = new AccountUserPlatformCoinAddReqVO();
        reqVO.setSiteCode("10002");
        reqVO.setUserId("75765545");
        reqVO.setUserAccount("qiqi005");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO.setUserName("PAZq4mEY");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("pmhdff000000001");
        reqVO.setThirdOrderNo("thirdpmhdff0000001");
        reqVO.setToThirdCode("10002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
        reqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.PROMOTIONS.getCode());
        reqVO.setActivityFlag(AccountActivityTemplateEnum.FIRST_DEPOSIT.getCode());
        reqVO.setCoinValue(new BigDecimal("222"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }

}
