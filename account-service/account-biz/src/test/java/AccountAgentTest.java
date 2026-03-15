import com.cloud.baowang.account.AccountApplication;
import com.cloud.baowang.account.api.AccountAgentApiImpl;
import com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum;
import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountFreezeFlagEnum;
import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
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
public class AccountAgentTest {


    @Resource
    private AccountAgentApiImpl accountAgentApi;

    /**
     * 代理充值
     */
    @Test
    public void agentDepositTest(){
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
        reqVO.setInnerOrderNo("ACk000000006");
        reqVO.setThirdOrderNo("thirdACk0000006");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("ACK0001");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_DEPOSIT.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_DEPOSIT.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.DEPOSIT.getCode());
        reqVO.setCoinValue(new BigDecimal("999"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        Boolean result= accountAgentApi.agentQuotaCoin(reqVO);
    }

    /**
     * 代理提款申请
     */
    @Test
    public void agentWithdrawAllyTest(){
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
        reqVO.setInnerOrderNo("ATK000000013");
        reqVO.setThirdOrderNo("ATK000000013");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.WITHDRAWAL.getCode());
        reqVO.setCoinValue(new BigDecimal("66"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.FREEZE.getCode());
        Boolean result= accountAgentApi.agentCommissionCoin(reqVO);
        assertTrue(result);
    }

    /**
     * 代理提款失败/审核失败
     */
    @Test
    public void agentWithdrawFailTest(){
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
        reqVO.setInnerOrderNo("ATK000000010");
        reqVO.setThirdOrderNo("ATK000000010");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL_FAIL.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.WITHDRAWAL.getCode());
        reqVO.setCoinValue(new BigDecimal("10"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.UN_FREEZE.getCode());
        reqVO.setFreezeFlag(AccountFreezeFlagEnum.UNFREEZE.getCode());
        Boolean result= accountAgentApi.agentCommissionCoin(reqVO);
        assertTrue(result);
    }

    /**
     * 代理提款成功
     */
    @Test
    public void agentWithdrawSuccessTest(){
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
        reqVO.setInnerOrderNo("ATK000000013");
        reqVO.setThirdOrderNo("ATK00000001312");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("ACK0001");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.WITHDRAWAL.getCode());
        reqVO.setCoinValue(new BigDecimal("66"));
        reqVO.setFreezeFlag(AccountFreezeFlagEnum.UNFREEZE.getCode());
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());
        Boolean result= accountAgentApi.agentCommissionCoin(reqVO);
        assertTrue(result);
    }


    /**
     * 代理资金调整 -额度调整减少
     */
    @Test
    public void agentQuotajTest(){
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
        reqVO.setInnerOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setThirdOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_QUOTA.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.QUOTA_SUBTRACT.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.AGENT_QUOTA.getCode());
        reqVO.setCoinValue(new BigDecimal("88"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());
        Boolean result= accountAgentApi.agentQuotaCoin(reqVO);
        assertTrue(result);
    }
    /**
     * 代理资金调整 -代理活动增加
     */
    @Test
    public void agentQuotahdzTest(){
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
        reqVO.setInnerOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setThirdOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_PROMOTIONS.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.PROMOTIONS_ADD.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.PROMOTIONS.getCode());
        reqVO.setCoinValue(new BigDecimal("222"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        Boolean result= accountAgentApi.agentQuotaCoin(reqVO);
        assertTrue(result);
    }

    /**
     * 代理资金调整 -代理活动减少
     */
    @Test
    public void agentQuotahdjTest(){
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
        reqVO.setInnerOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setThirdOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_PROMOTIONS.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.PROMOTIONS_SUBTRACT.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.PROMOTIONS.getCode());
        reqVO.setCoinValue(new BigDecimal("88"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());
        Boolean result= accountAgentApi.agentQuotaCoin(reqVO);
        assertTrue(result);
    }
    /**
     * 代理资金调整 -额度其他调整增加
     */
    @Test
    public void agentQuotaOtherzTest(){
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
        reqVO.setInnerOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setThirdOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.OTHERS_ADD_ADJUSTMENTS.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinValue(new BigDecimal("33"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        Boolean result= accountAgentApi.agentQuotaCoin(reqVO);
        assertTrue(result);
    }

    /**
     * 代理资金调整 -额度其他调整减少
     */
    @Test
    public void agentQuotaOtherjTest(){
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
        reqVO.setInnerOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setThirdOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.OTHERS_SUBTRACT_ADJUSTMENTS.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinValue(new BigDecimal("55"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());
        Boolean result= accountAgentApi.agentQuotaCoin(reqVO);
        assertTrue(result);
    }

    /**
     * 代理资金调整 -佣金其他调整增加
     */
    @Test
    public void agentCommissionOtherzTest(){
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
        reqVO.setInnerOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setThirdOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.OTHERS_ADD_ADJUSTMENTS.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinValue(new BigDecimal("33"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        Boolean result= accountAgentApi.agentCommissionCoin(reqVO);
        assertTrue(result);
    }

    /**
     * 代理资金调整 -佣金其他调整减少
     */
    @Test
    public void agentCommissionOtherjTest(){
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
        reqVO.setInnerOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setThirdOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.OTHERS_SUBTRACT_ADJUSTMENTS.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.OTHER_ADJUSTMENTS.getCode());
        reqVO.setCoinValue(new BigDecimal("55"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());
        Boolean result= accountAgentApi.agentCommissionCoin(reqVO);
        assertTrue(result);
    }

    /**
     * 代理资金调整 -佣金调整增加
     */
    @Test
    public void agentCommissionzTest(){
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
        reqVO.setInnerOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setThirdOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_COMMISSION.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.COMMISSION_ADD.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.COMMISSION.getCode());
        reqVO.setCoinValue(new BigDecimal("47"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.INCOME.getCode());
        Boolean result= accountAgentApi.agentCommissionCoin(reqVO);
        assertTrue(result);
    }

    /**
     * 代理资金调整 -佣金调整减少
     */
    @Test
    public void agentCommissionjTest(){
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
        reqVO.setInnerOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setThirdOrderNo(SnowFlakeUtils.getSnowId());
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setToThirdCode("10002");
        reqVO.setAgentWalletType(AccountAgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        reqVO.setBusinessCoinType(AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_COMMISSION.getCode());
        reqVO.setCoinType(AccountAgentCoinRecordTypeEnum.AgentCoinTypeEnum.COMMISSION_SUBTRACT.getCode());
        reqVO.setCustomerCoinType(AccountAgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.COMMISSION.getCode());
        reqVO.setCoinValue(new BigDecimal("82"));
        reqVO.setBalanceType(AccountAgentCoinRecordTypeEnum.AgentBalanceTypeEnum.EXPENSES.getCode());
        Boolean result= accountAgentApi.agentCommissionCoin(reqVO);
        assertTrue(result);
    }
}
