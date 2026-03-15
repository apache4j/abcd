package com.cloud.baowang.wallet.api.vo.fundadjust;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员人工加额记录 返回")
@I18nClass
public class UserManualUpRecordResult {

    @Schema(title = "小计")
    private UserManualUpRecordResponseVO currentPage;

    @Schema(title = "总计")
    private UserManualUpRecordResponseVO totalPage;

    @Schema(title = "分页列表")
    private Page<UserManualUpRecordResponseVO> pageList;
}
