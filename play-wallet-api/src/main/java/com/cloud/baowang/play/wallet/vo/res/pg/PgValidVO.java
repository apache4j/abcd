package com.cloud.baowang.play.wallet.vo.res.pg;

import com.cloud.baowang.play.wallet.enums.PGErrorEnums;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import lombok.Data;

@Data
public class PgValidVO {

    private PGErrorEnums pgErrorEnums;

    private UserInfoVO  userInfoVO;

    private UserCoinWalletVO userCenterCoin;
}
