package com.cloud.baowang.play.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理客户端 首页游戏输赢 查询最新的5条注单 Param")
public class GetNewest5OrderRecordParam {

    @Schema(description = "1按照输赢金额排序 2按照时间排序 (如果没有排序则不需要传递)")
    private Integer orderField;

    @Schema(description = "1升序 2降序 (如果没有排序则不需要传递)")
    private Integer orderType;
    @Schema(description = "当前代理账号",hidden = true)
    private String agentAccount;
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

    @Schema(description = "币种")
    private String currencyCode;
}
