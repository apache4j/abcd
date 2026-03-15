package com.cloud.baowang.wallet.api.vo.platformCoinAdjust;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description ="会员平台币下分记录返回对象")
@I18nClass
public class UserPlatformCoinManualDownRecordResponseVO extends Page<UserPlatformCoinManualDownRecordVO> {

    /**
     * 小计
     */
    private UserPlatformCoinManualDownRecordVO currentPage;

    /**
     * 总计
     */
    private UserPlatformCoinManualDownRecordVO totalPage;
}
