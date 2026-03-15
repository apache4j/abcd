import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountPlatformCoinBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountPlatformWalletEnum;
import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.wallet.WalletApplication;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.wallet.service.WalletUserCommonCoinService;
import com.cloud.baowang.wallet.service.WalletUserCommonPlatformCoinService;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = WalletApplication.class)
@AutoConfigureMockMvc
public class UserCoinTest {


    @Resource
    private WalletUserCommonCoinService userCommonCoinService;


    /**
     * 会员存款（后台）
     */
    @Test
    public void userManualDepositTest(){

        UserCoinAddVO reqVO = new UserCoinAddVO();
        reqVO.setUserId("75765545");
        WalletUserInfoVO userInfoVO = new WalletUserInfoVO();
        userInfoVO.setSiteCode("10002");
        userInfoVO.setUserId("75765545");
        userInfoVO.setUserAccount("qiqi005");
        userInfoVO.setSuperAgentAccount(null);
        userInfoVO.setMainCurrency(CurrencyEnum.USD.getCode());
        userInfoVO.setUserName("PAZq4mEY");
        userInfoVO.setAccountStatus("1");
        userInfoVO.setAccountType("2");
        userInfoVO.setVipRank(1);
        userInfoVO.setVipGradeCode(2);
        reqVO.setOrderNo("user0000011");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode());
        reqVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_DEPOSIT_ADMIN.getCode());
        reqVO.setCoinValue(new BigDecimal("55"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        reqVO.setUserInfoVO(userInfoVO);
        CoinRecordResultVO coinResultVO = userCommonCoinService.userCommonCoinAdd(reqVO);
    }



}
