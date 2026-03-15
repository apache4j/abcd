package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author dami
 */
@Data
@Schema(title ="邀请好友充值数量请求对象")
public class UserCoinRecordCallFriendsRequestVO {

    // userIds
    private List<String> inviteeUserIds;
    // 账变类型
    private List<String> coinTypes;
}
