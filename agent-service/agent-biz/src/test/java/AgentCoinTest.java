import com.cloud.baowang.account.api.enums.AccountAgentCoinRecordTypeEnum;
import com.cloud.baowang.account.api.enums.AccountPlatformCoinBalanceTypeEnum;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.agent.AgentApplication;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.service.AgentCommonCoinService;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AgentApplication.class)
@AutoConfigureMockMvc
public class AgentCoinTest {


    @Resource
    private AgentCommonCoinService agentCommonCoinService;


    /**
     * 代理存款（后台）
     */
    @Test
    public void userManualDepositTest(){

        AgentCoinAddVO reqVO = new AgentCoinAddVO();
        reqVO.setAgentId("75765545");
        AgentInfoVO agentInfoVO = new AgentInfoVO();
        agentInfoVO.setAgentId("6121231");
        agentInfoVO.setSiteCode("10002");
        agentInfoVO.setAgentAccount("qiqi001");
        agentInfoVO.setName("");
        agentInfoVO.setParentId(null);
        agentInfoVO.setPath("6121231");
        agentInfoVO.setLevel(1);
        agentInfoVO.setStatus("1");
        agentInfoVO.setAgentLabelId("1");
        agentInfoVO.setRiskLevelId(null);
        reqVO.setOrderNo("user0000017");
        reqVO.setThirdOrderNo("third0000017");
        reqVO.setToThridCode("00002");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode());
        reqVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_DEPOSIT_ADMIN.getCode());
        reqVO.setCoinValue(new BigDecimal("22"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        reqVO.setAgentInfo(agentInfoVO);
        agentCommonCoinService.agentCommonQuotaCoinAdd(reqVO);
    }




}
