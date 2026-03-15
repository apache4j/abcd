package com.cloud.baowang.play.api.vo.lobby;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "注单记录")
public class LobbyOrderRecordVO {

    @Schema(title = "游戏名称")
    private String gameName;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "倍数")
    private String odds;

    @Schema(title = "盈利")
    private BigDecimal winLossAmount;

    @Schema(description = "图标")
    private String pcIcon;

    @Schema(title = "注单ID")
    private String orderId;

    @Schema(title = "投注时间")
    private Long betTime;

    @Schema(title = "收藏:true=收藏,false=未收藏")
    private Boolean collection;

    @Schema(title = "游戏ID")
    private String gameId;



}
