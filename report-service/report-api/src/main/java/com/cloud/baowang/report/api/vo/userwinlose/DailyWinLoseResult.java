package com.cloud.baowang.report.api.vo.userwinlose;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 每日盈亏 结果
 */
@Data
@Schema(title = "每日盈亏 结果")
public class DailyWinLoseResult {

//   @Schema(title =   "本页合计")
//    private DailyWinLoseResponseVO currentPage;

   @Schema(title =   "全部合计")
    private DailyWinLoseResponseVO totalPage;

   @Schema(title =   "分页列表")
    private Page<DailyWinLoseResponseVO> pageList;
}
