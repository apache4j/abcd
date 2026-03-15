import com.cloud.baowang.account.AccountApplication;
import com.cloud.baowang.account.api.AccountPlayApiImpl;
import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.account.api.enums.AccountFreezeFlagEnum;
import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
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
public class AccountPlayTest {


    @Resource
    private AccountPlayApiImpl accountPlayApi;


    /**
     * 会员投注
     */
    @Test
    public void userBetCoin(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playNextSpinTestClean0003");
        reqVO.setThirdOrderNo("playNextSpinTestClean0003");
        reqVO.setToThirdCode("NextSpin");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.GAME_BET.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        reqVO.setCoinValue(new BigDecimal("15"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userBetCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 会员转入
     */
    @Test
    public void userBetCoinTRANSFER_IN(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playDBBYZR0001");
        reqVO.setThirdOrderNo("playDBBYZR0001");
        reqVO.setToThirdCode("DBBY");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.GAME_BET.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_TRANSFER_IN.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        reqVO.setCoinValue(new BigDecimal("1"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userBetCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 会员打赏
     */
    @Test
    public void userBetCoinTips(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playDBZRTIPS0001");
        reqVO.setThirdOrderNo("playDBZRTIPS0001");
        reqVO.setToThirdCode("DBZR");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.GAME_BET.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_TIPS.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        reqVO.setCoinValue(new BigDecimal("1"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userBetCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 会员投注取消
     */
    @Test
    public void userBetCoinGameCancelBet(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playGameCancelBetNextSpin0001");
        reqVO.setThirdOrderNo("playGameCancelBetNextSpin0001");
        reqVO.setToThirdCode("NextSpin");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.CANCEL_BET.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_BET.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        reqVO.setCoinValue(new BigDecimal("1"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userBetCancelCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }

    /**
     * 会员投注返还
     */
    @Test
    public void userBetCoinGameReturnBet(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playGameReturnBetNextSpin0001");
        reqVO.setThirdOrderNo("playGameReturnBetNextSpin0001");
        reqVO.setToThirdCode("NextSpin");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.RETURN_BET.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RETURN_BET.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        reqVO.setCoinValue(new BigDecimal("1"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userBetCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }


    /**
     * 会员投注冻结
     */
    @Test
    public void userBetCoinFrezzd(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playSBDBFreedPAYOUT0001");
        reqVO.setThirdOrderNo("playSBDBFreedPAYOUT0001");
        reqVO.setToThirdCode("SBA");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.GAME_BET.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET_FREEZD.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.FREEZE.getCode());
        reqVO.setCoinValue(new BigDecimal("10"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userBetCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }


    /**
     * 会员投注冻结确认
     */
    @Test
    public void userBetCoinFrezPay(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playSBDBFreedPAYOUT0001");
        reqVO.setThirdOrderNo("playSBDBFreedPAYOUT0001001");
        reqVO.setToThirdCode("SBA");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.GAME_BET.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET_CONFIRM.getCode());
        reqVO.setFreezeFlag(AccountFreezeFlagEnum.UNFREEZE.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        reqVO.setCoinValue(new BigDecimal("10"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userBetCoin(reqVO);
        assertTrue(coinResultVO.getResult());
    }


    /**
     * 会员投注派彩
     */
    @Test
    public void userBetCoinPayOut(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playSBDBFreedPAYOUT0001");
        reqVO.setThirdOrderNo("playSBDBFreedPAYOUT0001ThirdOrder0001");
        reqVO.setToThirdCode("SBA");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_PAYOUT.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        reqVO.setCoinValue(new BigDecimal("20"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userGamePayout(reqVO);
        assertTrue(coinResultVO.getResult());
    }


    /**
     * 会员投注转出
     */
    @Test
    public void userBetGameTransferOut(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playNextSpinTestPayZero0002");
        reqVO.setThirdOrderNo("playNextSpinTestPayZero0002001");
        reqVO.setToThirdCode("NextSpin");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_TRANSFER_OUT.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        reqVO.setCoinValue(new BigDecimal("0"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userGamePayout(reqVO);
        assertTrue(coinResultVO.getResult());
    }


    /**
     * 会员派彩取消
     */
    @Test
    public void userBetGamePayOutRepayment(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playuserBetGamePayOutRepayment0001");
        reqVO.setThirdOrderNo("playNuserBetGamePayOutRepayment0001");
        reqVO.setToThirdCode("NextSpin");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_RECALCULATE_PAYOUT.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        reqVO.setCoinValue(new BigDecimal("100"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userGamePayout(reqVO);
        assertTrue(coinResultVO.getResult());
    }


    /**
     * 会员重派彩
     */
    @Test
    public void userBetGamePayOutCennel(){
        AccountUserCoinAddReqVO reqVO = new AccountUserCoinAddReqVO();
        reqVO.setSiteCode("wtfAIo");
        reqVO.setUserId("80932768");
        reqVO.setUserAccount("mufan001");
        reqVO.setAgentAccount(null);
        reqVO.setCurrencyCode(CurrencyEnum.CNY.getCode());
        reqVO.setUserName("mufan001");
        reqVO.setAccountStatus("1");
        reqVO.setAccountType("2");
        reqVO.setVipRank(1);
        reqVO.setVipGradeCode(2);
        reqVO.setInnerOrderNo("playNextSpinTestPayZero0002");
        reqVO.setThirdOrderNo("playNextSpinTestPayZero0002002");
        reqVO.setToThirdCode("NextSpin");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        reqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        reqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        reqVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
        reqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
        reqVO.setCoinValue(new BigDecimal("208"));
        reqVO.setFinalRate(new BigDecimal("2"));
        AccountCoinResultVO coinResultVO = accountPlayApi.userGamePayout(reqVO);
        assertTrue(coinResultVO.getResult());
    }

}
