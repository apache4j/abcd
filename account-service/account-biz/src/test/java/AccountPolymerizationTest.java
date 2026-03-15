import com.cloud.baowang.account.AccountApplication;
import com.cloud.baowang.account.api.AccountPolymerizationApiImpl;
import com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum;
import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountPlatformCoinBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountPlatformWalletEnum;
import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountAgentCommissionDepositSubordinatesVO;
import com.cloud.baowang.account.api.vo.AccountAgentQuotaDepositSubordinatesVO;
import com.cloud.baowang.account.api.vo.AccountAgentQuotaTransferVO;
import com.cloud.baowang.account.api.vo.AccountAgentTransferToCommissionVO;
import com.cloud.baowang.account.api.vo.AccountAgentTransferToQuotaVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserWtcToMainCurrencyVO;
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
public class AccountPolymerizationTest {

    @Resource
    private AccountPolymerizationApiImpl accountUserPlatformAgentPolymerizationApi;

    @Test
    public void wtcToMainCurrency(){
        AccountUserPlatformCoinAddReqVO platformReqVO = new AccountUserPlatformCoinAddReqVO();
        platformReqVO.setSiteCode("10002");
        platformReqVO.setUserId("75765545");
        platformReqVO.setUserAccount("qiqi005");
        platformReqVO.setAgentAccount(null);
        platformReqVO.setUserName("PAZq4mEY");
        platformReqVO.setAccountStatus("1");
        platformReqVO.setAccountType("2");
        platformReqVO.setVipRank(1);
        platformReqVO.setVipGradeCode(2);
        platformReqVO.setInnerOrderNo("pmTo000000001");
        platformReqVO.setThirdOrderNo("thirdpmto0000001");
        platformReqVO.setToThirdCode("10002");
        platformReqVO.setCoinTime(System.currentTimeMillis());
        platformReqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.PLATFORM_CONVERSION.getCode());
        platformReqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.PLATFORM_CONVERSION.getCode());
        platformReqVO.setCoinValue(new BigDecimal("30"));
        platformReqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.EXPENSES.getCode());
        platformReqVO.setFinalRate(new BigDecimal("2"));

        AccountUserCoinAddReqVO userReqVO = new AccountUserCoinAddReqVO();
        userReqVO.setSiteCode("10002");
        userReqVO.setUserId("75765545");
        userReqVO.setUserAccount("qiqi005");
        userReqVO.setAgentAccount(null);
        userReqVO.setCurrencyCode(CurrencyEnum.USD.getCode());
        userReqVO.setUserName("PAZq4mEY");
        userReqVO.setAccountStatus("1");
        userReqVO.setAccountType("2");
        userReqVO.setVipRank(1);
        userReqVO.setVipGradeCode(2);
        userReqVO.setInnerOrderNo("pmTo000000001");
        userReqVO.setThirdOrderNo("thirdpmTo0000001");
        userReqVO.setCoinTime(System.currentTimeMillis());
        userReqVO.setToThirdCode("10002");
        userReqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userReqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userReqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userReqVO.setCoinValue(new BigDecimal("60"));
        userReqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        userReqVO.setFinalRate(new BigDecimal("2"));
        AccountUserWtcToMainCurrencyVO accountUserWtcToMainCurrencyVO = new AccountUserWtcToMainCurrencyVO();
        accountUserWtcToMainCurrencyVO.setUserPlatformCoinAddReqVO(platformReqVO);
        accountUserWtcToMainCurrencyVO.setUserCoinAddReqVO(userReqVO);
        AccountCoinResultVO result = accountUserPlatformAgentPolymerizationApi.WtcToMainCurrency(accountUserWtcToMainCurrencyVO);
    }

    @Test
    public void agentQuotaTransfer(){

        AccountAgentCoinAddReqVO commissionReqVO = new AccountAgentCoinAddReqVO();
        commissionReqVO.setSiteCode("10002");
        commissionReqVO.setAgentId("6121231");
        commissionReqVO.setAgentAccount("qiqi001");
        commissionReqVO.setAgentName("");
        commissionReqVO.setParentId(null);
        commissionReqVO.setPath("6121231");
        commissionReqVO.setLevel(1);
        commissionReqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        commissionReqVO.setAgentName(null);
        commissionReqVO.setStatus("1");
        commissionReqVO.setAgentLabelId("1");
        commissionReqVO.setRiskLevelId(null);
        commissionReqVO.setInnerOrderNo("agent00000001");
        commissionReqVO.setThirdOrderNo("");
        commissionReqVO.setCoinTime(System.currentTimeMillis());
        commissionReqVO.setToThirdCode("10002");
        commissionReqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        commissionReqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_QUOTA_TRANSFER.getCode());
        commissionReqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.TO_QUOTA_TRANSFER.getCode());
        commissionReqVO.setCoinValue(new BigDecimal("200"));
        commissionReqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());


        AccountAgentCoinAddReqVO reqVO = new AccountAgentCoinAddReqVO();
        reqVO.setSiteCode("10002");
        reqVO.setAgentId("6121231");
        reqVO.setAgentAccount("qiqi001");
        reqVO.setAgentName("");
        reqVO.setParentId(null);
        reqVO.setPath("6121231");
        reqVO.setLevel(1);
        reqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO.setAgentName(null);
        reqVO.setStatus("1");
        reqVO.setAgentLabelId("1");
        reqVO.setRiskLevelId(null);
        reqVO.setInnerOrderNo("agent00000001");
        reqVO.setThirdOrderNo("");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_QUOTA_TRANSFER.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.QUOTA_TRANSFER.getCode());
        reqVO.setCoinValue(new BigDecimal("200"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        AccountAgentQuotaTransferVO agentQuotaTransferVO =  new AccountAgentQuotaTransferVO();
        agentQuotaTransferVO.setAgentQuotaCoinReqVO(reqVO);
        agentQuotaTransferVO.setAgentCommissionCoinReqVO(commissionReqVO);

        Boolean result= accountUserPlatformAgentPolymerizationApi.agentQuotaTransfer(agentQuotaTransferVO);
    }

    @Test
    public void agentTransferToQuota(){

        AccountAgentCoinAddReqVO reqVO1 = new AccountAgentCoinAddReqVO();
        reqVO1.setSiteCode("10002");
        reqVO1.setAgentId("6121231");
        reqVO1.setAgentAccount("qiqi001");
        reqVO1.setAgentName("");
        reqVO1.setParentId(null);
        reqVO1.setPath("6121231");
        reqVO1.setLevel(1);
        reqVO1.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO1.setAgentName(null);
        reqVO1.setStatus("1");
        reqVO1.setAgentLabelId("1");
        reqVO1.setRiskLevelId(null);
        reqVO1.setInnerOrderNo("agent00000002");
        reqVO1.setThirdOrderNo("");
        reqVO1.setCoinTime(System.currentTimeMillis());
        reqVO1.setToThirdCode("10002");
        reqVO1.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO1.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_TRANSFER.getCode());
        reqVO1.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.TRANSFER_SUBORDINATES.getCode());
        reqVO1.setCoinValue(new BigDecimal("155"));
        reqVO1.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());


        AccountAgentCoinAddReqVO reqVO = new AccountAgentCoinAddReqVO();
        reqVO.setSiteCode("10002");
        reqVO.setAgentId("8066747");
        reqVO.setAgentAccount("suqin01");
        reqVO.setAgentName("");
        reqVO.setParentId(null);
        reqVO.setPath("8066747");
        reqVO.setLevel(1);
        reqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO.setAgentName(null);
        reqVO.setStatus("1");
        reqVO.setAgentLabelId("1");
        reqVO.setRiskLevelId(null);
        reqVO.setInnerOrderNo("agent00000002");
        reqVO.setThirdOrderNo("");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_TRANSFER.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.SUPERIOR_TRANSFER.getCode());
        reqVO.setCoinValue(new BigDecimal("155"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());

        AccountAgentTransferToQuotaVO agentTransferToQuotaVO = new AccountAgentTransferToQuotaVO();
        agentTransferToQuotaVO.setAgentQuotaCoinReqVO(reqVO1);
        agentTransferToQuotaVO.setAgentSubordinatesQuotaCoinReqVO(reqVO);
        Boolean result= accountUserPlatformAgentPolymerizationApi.agentTransferToQuota(agentTransferToQuotaVO);
    }

    @Test
    public void agentTransferToCommission(){

        AccountAgentCoinAddReqVO reqVO1 = new AccountAgentCoinAddReqVO();
        reqVO1.setSiteCode("10002");
        reqVO1.setAgentId("6121231");
        reqVO1.setAgentAccount("qiqi001");
        reqVO1.setAgentName("");
        reqVO1.setParentId(null);
        reqVO1.setPath("6121231");
        reqVO1.setLevel(1);
        reqVO1.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO1.setAgentName(null);
        reqVO1.setStatus("1");
        reqVO1.setAgentLabelId("1");
        reqVO1.setRiskLevelId(null);
        reqVO1.setInnerOrderNo("agent00000003");
        reqVO1.setThirdOrderNo("agent00000003");
        reqVO1.setCoinTime(System.currentTimeMillis());
        reqVO1.setToThirdCode("10002");
        reqVO1.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO1.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_TRANSFER.getCode());
        reqVO1.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.TRANSFER_SUBORDINATES.getCode());
        reqVO1.setCoinValue(new BigDecimal("333"));
        reqVO1.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());


        AccountAgentCoinAddReqVO reqVO = new AccountAgentCoinAddReqVO();
        reqVO.setSiteCode("10002");
        reqVO.setAgentId("8066747");
        reqVO.setAgentAccount("suqin01");
        reqVO.setAgentName("");
        reqVO.setParentId(null);
        reqVO.setPath("8066747");
        reqVO.setLevel(1);
        reqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO.setAgentName(null);
        reqVO.setStatus("1");
        reqVO.setAgentLabelId("1");
        reqVO.setRiskLevelId(null);
        reqVO.setInnerOrderNo("agent00000003");
        reqVO.setThirdOrderNo("agent00000003");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_TRANSFER.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.SUPERIOR_TRANSFER.getCode());
        reqVO.setCoinValue(new BigDecimal("333"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        AccountAgentTransferToCommissionVO agentTransferToCommissionVO = new AccountAgentTransferToCommissionVO();
        agentTransferToCommissionVO.setAgentCommissionCoinReqVO(reqVO1);
        agentTransferToCommissionVO.setAgentSubordinatesCommissionCoinReqVO(reqVO);
        Boolean result= accountUserPlatformAgentPolymerizationApi.agentTransferToCommission(agentTransferToCommissionVO);
    }

    @Test
    public void agentQuotaDepositSubordinates(){

        AccountAgentCoinAddReqVO reqVO1 = new AccountAgentCoinAddReqVO();
        reqVO1.setSiteCode("10002");
        reqVO1.setAgentId("6121231");
        reqVO1.setAgentAccount("qiqi001");
        reqVO1.setAgentName("");
        reqVO1.setParentId(null);
        reqVO1.setPath("6121231");
        reqVO1.setLevel(1);
        reqVO1.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO1.setAgentName(null);
        reqVO1.setStatus("1");
        reqVO1.setAgentLabelId("1");
        reqVO1.setRiskLevelId(null);
        reqVO1.setInnerOrderNo("agent00000006");
        reqVO1.setThirdOrderNo("agent00000006");
        reqVO1.setCoinTime(System.currentTimeMillis());
        reqVO1.setToThirdCode("10002");
        reqVO1.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO1.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.DEPOSIT_OF_SUBORDINATES.getCode());
        reqVO1.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.TRANSFER_SUBORDINATES_MEMBER.getCode());
        reqVO1.setCoinValue(new BigDecimal("1"));
        reqVO1.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());


        AccountUserCoinAddReqVO userReqVO = new AccountUserCoinAddReqVO();
        userReqVO.setSiteCode("10002");
        userReqVO.setUserId("75765545");
        userReqVO.setUserAccount("qiqi005");
        userReqVO.setAgentAccount(null);
        userReqVO.setCurrencyCode(CurrencyEnum.USD.getCode());
        userReqVO.setUserName("PAZq4mEY");
        userReqVO.setAccountStatus("1");
        userReqVO.setAccountType("2");
        userReqVO.setVipRank(1);
        userReqVO.setVipGradeCode(2);
        userReqVO.setInnerOrderNo("agent00000006");
        userReqVO.setThirdOrderNo("agent00000006");
        userReqVO.setCoinTime(System.currentTimeMillis());
        userReqVO.setToThirdCode("10002");
        userReqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode());
        userReqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.TRANSFER_FROM_SUPERIOR.getCode());
        userReqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.DEPOSIT.getCode());
        userReqVO.setCoinValue(new BigDecimal("2"));
        userReqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());

        AccountAgentQuotaDepositSubordinatesVO agentQuotaDepositSubordinatesVO = new AccountAgentQuotaDepositSubordinatesVO();
        agentQuotaDepositSubordinatesVO.setAgentQuotaCoinReqVO(reqVO1);
        agentQuotaDepositSubordinatesVO.setUserCoinAddReqVO(userReqVO);
        boolean result = accountUserPlatformAgentPolymerizationApi.agentQuotaDepositSubordinates(agentQuotaDepositSubordinatesVO);

    }


    @Test
    public void agentCommissionDepositSubordinates(){

        AccountAgentCoinAddReqVO reqVO1 = new AccountAgentCoinAddReqVO();
        reqVO1.setSiteCode("10002");
        reqVO1.setAgentId("6121231");
        reqVO1.setAgentAccount("qiqi001");
        reqVO1.setAgentName("");
        reqVO1.setParentId(null);
        reqVO1.setPath("6121231");
        reqVO1.setLevel(1);
        reqVO1.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        reqVO1.setAgentName(null);
        reqVO1.setStatus("1");
        reqVO1.setAgentLabelId("1");
        reqVO1.setRiskLevelId(null);
        reqVO1.setInnerOrderNo("agent00000005");
        reqVO1.setThirdOrderNo("agent00000005");
        reqVO1.setCoinTime(System.currentTimeMillis());
        reqVO1.setToThirdCode("10002");
        reqVO1.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO1.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.DEPOSIT_OF_SUBORDINATES.getCode());
        reqVO1.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.TRANSFER_SUBORDINATES_MEMBER.getCode());
        reqVO1.setCoinValue(new BigDecimal("9"));
        reqVO1.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());


        AccountUserCoinAddReqVO userReqVO = new AccountUserCoinAddReqVO();
        userReqVO.setSiteCode("10002");
        userReqVO.setUserId("75765545");
        userReqVO.setUserAccount("qiqi005");
        userReqVO.setAgentAccount(null);
        userReqVO.setCurrencyCode(CurrencyEnum.USD.getCode());
        userReqVO.setUserName("PAZq4mEY");
        userReqVO.setAccountStatus("1");
        userReqVO.setAccountType("2");
        userReqVO.setVipRank(1);
        userReqVO.setVipGradeCode(2);
        userReqVO.setInnerOrderNo("agent00000005");
        userReqVO.setThirdOrderNo("agent00000005");
        userReqVO.setCoinTime(System.currentTimeMillis());
        userReqVO.setToThirdCode("10002");
        userReqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode());
        userReqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.TRANSFER_FROM_SUPERIOR.getCode());
        userReqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.DEPOSIT.getCode());
        userReqVO.setCoinValue(new BigDecimal("18"));
        userReqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());

        AccountAgentCommissionDepositSubordinatesVO agentCommissionDepositSubordinatesVO = new AccountAgentCommissionDepositSubordinatesVO();
        agentCommissionDepositSubordinatesVO.setAgentCommissionCoinReqVO(reqVO1);
        agentCommissionDepositSubordinatesVO.setUserCoinAddReqVO(userReqVO);
        boolean result = accountUserPlatformAgentPolymerizationApi.agentCommissionDepositSubordinates(agentCommissionDepositSubordinatesVO);

    }

}
