package com.cloud.baowang.report.api.vo.userwinlose;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 点击会员账号 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "点击会员账号 Request")
public class ClickUserAccountPageVO extends PageVO {

    @Schema(title = "开始日期")
    @NotNull(message = "日期不能为空")
    private Long startDay;

    @Schema(title = "结束日期")
    @NotNull(message = "日期不能为空")
    private Long endDay;

    @Schema(title = "点击的会员账号")
    @NotEmpty(message = "会员账号不能为空")
    private String userAccount;

    @Schema(title = "点击的会员账号是否有上级")
    private String superAgentAccount;
    @Schema(title = "查询使用", hidden = true)
    private String utcStr;

    @Schema(description = "是否转换为平台币")
    private Boolean convertPlatCurrency = Boolean.FALSE;

    @Schema(title = "时区", hidden = true)
    private String timeZone;

    @Schema(title = "站点", hidden = true)
    private String siteCode;

    @Schema(title = "平台币code", hidden = true)
    private String platCurrencyCode;
}
