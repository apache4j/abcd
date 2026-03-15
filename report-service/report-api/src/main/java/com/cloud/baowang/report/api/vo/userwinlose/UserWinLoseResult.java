package com.cloud.baowang.report.api.vo.userwinlose;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员盈亏 返回")
@I18nClass
public class UserWinLoseResult {

    @Schema(title = "本页合计")
    private UserWinLoseResponseVO currentPage;

    @Schema(title = "全部合计")
    private UserWinLoseResponseVO totalPage;

    @Schema(title = "分页列表")
    private Page<UserWinLoseResponseVO> pageList;
}
