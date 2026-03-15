package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author: kimi
 */
@Data
@Schema(title = "下级会员列表 Param")
public class SubordinateUserListParam extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "timeZone", hidden = true)
    private String timeZone;

    @Schema(description = "agentAccount")
    private String agentAccount;
    @Schema(description = "agentId")
    private String agentId;

    @Schema(title = "会员账号")
    private String userAccount;


    @Schema(title = "货币币种")
    private String currencyCode;


    @Schema(title = "统计开始时间")
    private Long registerStartTime;

    @Schema(title = "统计结束时间")
    private Long registerEndTime;
}
