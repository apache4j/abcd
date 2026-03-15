package com.cloud.baowang.report.api.vo.userwinlose;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 会员盈亏 Request
 *
 * @author ford
 * @since 2024-11-02
 */
@Data
@Schema( title = "会员盈亏 Request")
public class UserWinLoseListPageCondVO extends PageVO {

    @Schema(title =   "统计日期 dayMillis")
    private Long startDayMillis;

    @Schema(title =   "统计日期 dayMillis")
    private Long endDayMillis;

    @Schema(title  = "站点Code")
    private String siteCode;

    @Schema(title = "账号类型")
    private Integer accountType;

}
