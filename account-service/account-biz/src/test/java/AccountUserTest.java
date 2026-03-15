import com.cloud.baowang.account.AccountApplication;
import com.cloud.baowang.account.api.AccountUserApiImpl;
import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountFreezeFlagEnum;
import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.service.account.AccountCoinService;
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
public class AccountUserTest {


    @Resource
    private AccountUserApiImpl accountUserApi;

    @Resource
    private AccountCoinService accountCoinService;

    /**
     * 会员充值
     */
    @Test
    public void userDepositTest(){
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
        reqVO.setInnerOrderNo("MFTest000000001");
        reqVO.setThirdOrderNo("thirdCkMFTest0000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.MEMBER_DEPOSIT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.DEPOSIT.getCode());
        reqVO.setCoinValue(new BigDecimal("10"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        AccountCoinResultVO coinResultVO = accountUserApi.userBalanceCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 会员提现申请，冻结金额
     */
    @Test
    public void userWithdrawApply(){
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
        reqVO.setInnerOrderNo("mufanPayout000000001");
        reqVO.setThirdOrderNo("mufanPayout000000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        reqVO.setCoinValue(new BigDecimal("100"));
        reqVO.setBalanceType(AccountBalanceTypeEnum.FREEZE.getCode());
        reqVO.setToThirdCode("10002");
        AccountCoinResultVO coinResultVO = accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员提现 审核拒绝/出款失败
     */
    @Test
    public void userWithdrawFail(){
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
        reqVO.setInnerOrderNo("TK000000002");
        reqVO.setThirdOrderNo("");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.MEMBER_WITHDRAWAL_FAIL.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        reqVO.setCoinValue(new BigDecimal("88"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.UN_FREEZE.getCode());
        reqVO.setFreezeFlag(AccountFreezeFlagEnum.UNFREEZE.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员提现 出款成功
     */
    @Test
    public void userWithdrawSuccess(){
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
        reqVO.setInnerOrderNo("TK000000004");
        reqVO.setThirdOrderNo("thirdTk0000004");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        reqVO.setCoinValue(new BigDecimal("99"));
        reqVO.setToThirdCode("10002");
        reqVO.setFreezeFlag(AccountFreezeFlagEnum.UNFREEZE.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员人工加额 -存款后台
     */
    @Test
    public void userManualDepositTest(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("10002");
        reqVO.setUserId("75765545");
        reqVO.setUserAccount("qiqi005");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.USD.getCode());
        reqVO.setUserName("PAZq4mEY");
        reqVO.setAccountStatus("");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("RGA000000002");
        reqVO.setThirdOrderNo(reqVO.getSiteCode());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.MEMBER_DEPOSIT_ADMIN.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.DEPOSIT.getCode());
        reqVO.setCoinValue(new BigDecimal("45"));
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        reqVO.setToThirdCode("10002");
        AccountCoinResultVO coinResultVO = accountUserApi.userBalanceCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }


    /**
     * 会员人工减额-后台提款
     */
    @Test
    public void userManualWithdrawTest(){
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
        reqVO.setInnerOrderNo("RGS000000002");
        reqVO.setThirdOrderNo(reqVO.getSiteCode());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.MEMBER_WITHDRAWAL_ADMIN.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        reqVO.setCoinValue(new BigDecimal("33"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        AccountCoinResultVO coinResultVO = accountUserApi.userBalanceCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 会员VIP福利发放
     */
    @Test
    public void uservipff(){
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
        reqVO.setCoinValue(new BigDecimal("50"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员人工调整-VIP福利增加
     */
    @Test
    public void uservipz(){
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
        reqVO.setInnerOrderNo("uservipz000000001");
        reqVO.setThirdOrderNo("uservipz000000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS_ADD.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("60"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }
    /**
     * 会员人工调整-VIP福利扣除
     */
    @Test
    public void uservipj(){
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
        reqVO.setInnerOrderNo("uservipj000000001");
        reqVO.setThirdOrderNo("uservipj000000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS_SUBTRACT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("50"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
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
        reqVO.setCoinValue(new BigDecimal("210"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员人工调整-活动优惠增加
     */
    @Test
    public void userhdyhz(){
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
        reqVO.setInnerOrderNo("userhdyhz000000001");
        reqVO.setThirdOrderNo("userhdyhz000000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.PROMOTIONS_ADD.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("123"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员人工调整-活动优惠扣除
     */
    @Test
    public void userhdyhj(){
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
        reqVO.setInnerOrderNo("userhdyhj000000001");
        reqVO.setThirdOrderNo("userhdyhj000000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.PROMOTIONS_SUBTRACT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("50"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员人工调整-其他增加
     */
    @Test
    public void userqtz(){
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
        reqVO.setInnerOrderNo("userqtz000000001");
        reqVO.setThirdOrderNo("userqtz000000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.OTHER_ADD.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("66"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员人工调整-其他扣除
     */
    @Test
    public void userqtj(){
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
        reqVO.setInnerOrderNo("userqtj000000001");
        reqVO.setThirdOrderNo("userqtj000000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.OTHER_SUBTRACT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("45"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员返水
     */
    @Test
    public void userfsff(){
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
        reqVO.setInnerOrderNo("userfs000000001");
        reqVO.setThirdOrderNo("thirdfs0000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.REBATE.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.REBATE.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.OFFLINE_BONUS.getCode());
        reqVO.setCoinValue(new BigDecimal("77"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }
    /**
     * 会员人工调整-返水增加
     */
    @Test
    public void userfsz(){
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
        reqVO.setInnerOrderNo("userfsz000000001");
        reqVO.setThirdOrderNo("userfsz000000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.REBATE.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.REBATE_ADD.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("50"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }
    /**
     * 会员人工调整-返水扣除
     */
    @Test
    public void userfsj(){
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
        reqVO.setInnerOrderNo("userfs000000001");
        reqVO.setThirdOrderNo("thirdfs0000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.REBATE.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.REBATE_SUBTRACT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("89"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 会员人工调整-风控调整增加
     */
    @Test
    public void userfktzz(){
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
        reqVO.setInnerOrderNo("userfktzz000000001");
        reqVO.setThirdOrderNo("userfktzz000000001");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.RISK_CONTROL_ADJUSTMENT_ADD.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.ADD_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("55"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }
    /**
     * 会员人工调整-风控扣除
     */
    @Test
    public void userfktzj(){
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
        reqVO.setInnerOrderNo("userfktzj000000002");
        reqVO.setThirdOrderNo("userfktzj000000002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.RISK_CONTROL_ADJUSTMENT_SUBTRACT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.SUBTRACT_COIN.getCode());
        reqVO.setCoinValue(new BigDecimal("28"));
        reqVO.setToThirdCode("10002");
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        accountUserApi.userFreezeBalanceCoin(reqVO);
    }

    /**
     * 清除打码量获取用户当前余额
     */
    @Test
    public void getUserAmount(){
        BigDecimal amount = accountCoinService.selectAccountFlowAmount("wtfAIo","mufan001");
    }

}
