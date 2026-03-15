package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员提款记录返回对象")
public class UserWithdrawRecordResponseVO {

    /**
     * 小计
     */
    private UserWithdrawRecordVO currentPage;

    /**
     * 总计
     */
    private UserWithdrawRecordVO totalPage;

}
