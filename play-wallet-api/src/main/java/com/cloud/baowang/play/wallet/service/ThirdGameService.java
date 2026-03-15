package com.cloud.baowang.play.wallet.service;

import com.cloud.baowang.play.wallet.po.UserWalletGameRecordPO;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;

public interface ThirdGameService {

    UpdateBalanceStatusEnums updateBalance(UserWalletGameRecordPO userWalletGameRecordPO);
}
