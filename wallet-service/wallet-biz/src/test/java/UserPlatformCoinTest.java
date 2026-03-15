import com.cloud.baowang.account.api.enums.AccountBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountPlatformCoinBalanceTypeEnum;
import com.cloud.baowang.account.api.enums.AccountPlatformWalletEnum;
import com.cloud.baowang.account.api.enums.AccountUserCoinEnum;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.WalletApplication;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformWalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
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
public class UserPlatformCoinTest {


    @Resource
    private WalletUserCommonPlatformCoinService walletUserCommonPlatformCoinService;


    /**
     * 平台币-会员VIP优惠发放
     */
    @Test
    public void platformCoinVIPff(){
        UserPlatformCoinAddVO reqVO = new UserPlatformCoinAddVO();
        WalletUserInfoVO userInfoVO = new WalletUserInfoVO();
        userInfoVO.setSiteCode("10002");
        userInfoVO.setUserId("75765545");
        userInfoVO.setUserAccount("qiqi005");
        userInfoVO.setSuperAgentAccount(null);
        userInfoVO.setMainCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userInfoVO.setUserName("PAZq4mEY");
        userInfoVO.setAccountStatus("1");
        userInfoVO.setAccountType("2");
        userInfoVO.setVipRank(1);
        userInfoVO.setVipGradeCode(2);
        reqVO.setOrderNo("pmvipff000000005");
        reqVO.setCoinTime(System.currentTimeMillis());
        reqVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
        reqVO.setCoinValue(new BigDecimal("150"));
        reqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.INCOME.getCode());
        reqVO.setUserInfoVO(userInfoVO);
        CoinRecordResultVO coinResultVO = walletUserCommonPlatformCoinService.userCommonPlatformCoin(reqVO);
    }

    @Test
    public void wtcToMainCurrency(){
        UserPlatformCoinAddVO platformReqVO = new UserPlatformCoinAddVO();
        WalletUserInfoVO userInfoVO = new WalletUserInfoVO();
        userInfoVO.setSiteCode("10002");
        userInfoVO.setUserId("75765545");
        userInfoVO.setUserAccount("qiqi005");
        userInfoVO.setSuperAgentAccount(null);
        userInfoVO.setMainCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userInfoVO.setUserName("PAZq4mEY");
        userInfoVO.setAccountStatus("1");
        userInfoVO.setAccountType("2");
        userInfoVO.setVipRank(1);
        userInfoVO.setVipGradeCode(2);
        platformReqVO.setOrderNo("pmTo000000006");
        platformReqVO.setCoinTime(System.currentTimeMillis());
        platformReqVO.setBusinessCoinType(AccountPlatformWalletEnum.BusinessCoinTypeEnum.PLATFORM_CONVERSION.getCode());
        platformReqVO.setCoinType(AccountPlatformWalletEnum.CoinTypeEnum.PLATFORM_CONVERSION.getCode());
        platformReqVO.setCoinValue(new BigDecimal("30"));
        platformReqVO.setBalanceType(AccountPlatformCoinBalanceTypeEnum.EXPENSES.getCode());
        platformReqVO.setUserInfoVO(userInfoVO);
        UserCoinAddVO userReqVO = new UserCoinAddVO();
        WalletUserInfoVO userInfoVO1 = new WalletUserInfoVO();
        userInfoVO1.setSiteCode("10002");
        userInfoVO1.setUserId("75765545");
        userInfoVO1.setUserAccount("qiqi005");
        userInfoVO1.setSuperAgentAccount(null);
        userInfoVO1.setMainCurrency("USD");
        userInfoVO1.setUserName("PAZq4mEY");
        userInfoVO1.setAccountStatus("1");
        userInfoVO1.setAccountType("2");
        userInfoVO1.setVipRank(1);
        userInfoVO1.setVipGradeCode(2);
        userReqVO.setOrderNo("pmTo000000011");
        userReqVO.setCoinTime(System.currentTimeMillis());
        userReqVO.setBusinessCoinType(AccountUserCoinEnum.BusinessCoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userReqVO.setCoinType(AccountUserCoinEnum.CoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userReqVO.setCustomerCoinType(AccountUserCoinEnum.CustomerCoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userReqVO.setCoinValue(new BigDecimal("60"));
        userReqVO.setUserInfoVO(userInfoVO1);
        userReqVO.setBalanceType(AccountBalanceTypeEnum.INCOME.getCode());
        CoinRecordResultVO result = walletUserCommonPlatformCoinService.wtcToMainCurrency(platformReqVO,userReqVO);


    }

}
