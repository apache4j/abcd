package com.cloud.baowang.account.service.account;


import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.common.kafka.vo.AccountRequestMqVO;

import java.util.List;

public interface AccountTransfer {
    /**
     * 用户主货币，代理，以及平台币相关业务合并
     */
    void singleTransfer(AccountBusinessUserReqVO vo);

    /**
     * 游戏相关入口
     */
    void gameTransfer(AccountBusinessUserReqVO vo);

    /**
     * kafka消息推送清理场馆金额
     * @param data
     */
    void batchCleanAccountCoin(List<AccountRequestMqVO> data);
}
