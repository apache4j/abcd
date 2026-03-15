package com.cloud.baowang.wallet.api.vo.userCoinManualDown;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description ="会员人工减额记录返回对象")
@I18nClass
public class UserManualDownRecordResponseVO extends Page<UserManualDownRecordVO> {

    /**
     * 小计
     */
    private UserManualDownRecordVO currentPage;

    /**
     * 总计
     */
    private UserManualDownRecordVO totalPage;
}
