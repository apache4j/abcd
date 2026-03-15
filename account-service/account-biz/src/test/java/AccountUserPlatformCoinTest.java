import com.cloud.baowang.account.AccountApplication;
import com.cloud.baowang.account.api.AccountAgentApiImpl;
import com.cloud.baowang.account.api.AccountPlatformCoinApiImpl;
import com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum;
import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountPlatformCoinBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountPlatformWalletEnum;
import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
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
public class AccountUserPlatformCoinTest {


    @Resource
    private AccountPlatformCoinApiImpl accountPlatformCoinApi;


    /**
     * 平台币-会员VIP优惠发放
     */
    @Test
    public void platformCoinVIPff(){
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
        reqVO.setCoinValue(new BigDecimal("222"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 平台币-会员活动发放
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
        reqVO.setCoinValue(new BigDecimal("222"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 平台币-勋章奖励
     */
    @Test
    public void platformCoinxzjl(){
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
        reqVO.setInnerOrderNo("pmxzjl000000001");
        reqVO.setThirdOrderNo("pmxzjl000000001");
        reqVO.setToThirdCode("10002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEDAL_REWARD.getCode());
        reqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.MEDAL_REWARD.getCode());
        reqVO.setCoinValue(new BigDecimal("222"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 平台币人工上下分-会员VIP优惠增加
     */
    @Test
    public void platformCoinManualUpVip(){
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
        reqVO.setInnerOrderNo("pmvip000000005");
        reqVO.setThirdOrderNo("pmvip000000005");
        reqVO.setToThirdCode("10002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS_ADD.getCode());
        reqVO.setCoinValue(new BigDecimal("500"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
    }
    /**
     * 平台币人工上下分-会员VIP优惠扣除
     */
    @Test
    public void platformCoinManualDownVip(){
        AccountUserPlatformCoinAddReqVO reqVO = new AccountUserPlatformCoinAddReqVO();
        reqVO.setSiteCode("10002");
        reqVO.setUserId("75765545");
        reqVO.setUserAccount("qiqi005");
        reqVO.setAgentAccount(null);
        reqVO.setUserName("PAZq4mEY");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("pmvip000000003");
        reqVO.setThirdOrderNo("pmvip000000003");
        reqVO.setToThirdCode("10002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS_SUBTRACT.getCode());
        reqVO.setCoinValue(new BigDecimal("42"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.EXPENSES.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }
    /**
     * 平台币人工上下分-活动优惠增加
     */
    @Test
    public void platformCoinManualUphdz(){
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
        reqVO.setInnerOrderNo("pmhdz000000001");
        reqVO.setThirdOrderNo("pmhdz000000001");
        reqVO.setToThirdCode("10002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
        reqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.MEMBER_ACTIVITIES_ADD.getCode());
        reqVO.setCoinValue(new BigDecimal("300"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }
    /**
     * 平台币人工上下分-活动优惠扣除
     */
    @Test
    public void platformCoinManualUphdj(){
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
        reqVO.setInnerOrderNo("pmhdj000000001");
        reqVO.setThirdOrderNo("pmhdj000000001");
        reqVO.setToThirdCode("10002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
        reqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.MEMBER_ACTIVITIES_SUBTRACT.getCode());
        reqVO.setCoinValue(new BigDecimal("55"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.EXPENSES.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }
    /**
     * 平台币人工上分-其他调整增加
     */
    @Test
    public void platformCoinManualUpqtz(){
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
        reqVO.setInnerOrderNo("pmqtz000000001");
        reqVO.setThirdOrderNo("pmqtz000000001");
        reqVO.setToThirdCode("10002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.OTHER_ADJUSTMENTS_ADD.getCode());
        reqVO.setCoinValue(new BigDecimal("212"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }
    /**
     * 平台币人工上分-其他调整扣除
     */
    @Test
    public void platformCoinManualUpqtj(){
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
        reqVO.setInnerOrderNo("pmqtj000000001");
        reqVO.setThirdOrderNo("pmqtj000000001");
        reqVO.setToThirdCode("10002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.OTHER_ADJUSTMENTS_SUBTRACT.getCode());
        reqVO.setCoinValue(new BigDecimal("55"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.EXPENSES.getCode());
        AccountCoinResultVO coinResultVO = accountPlatformCoinApi.platformCoinAdd(reqVO);
        assertTrue(coinResultVO.getResult());
    }


}
