package com.cloud.baowang.wallet.api.vo.platformCoinAdjust;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: qiqi
 */
@Data
@Schema(title = "会员平台币上分记录 返回")
@I18nClass
public class UserPlatformCoinManualUpRecordResult {

    @Schema(title = "小计")
    private UserPlatformCoinManualUpRecordResponseVO currentPage;

    @Schema(title = "总计")
    private UserPlatformCoinManualUpRecordResponseVO totalPage;

    @Schema(title = "分页列表")
    private Page<UserPlatformCoinManualUpRecordResponseVO> pageList;
}
