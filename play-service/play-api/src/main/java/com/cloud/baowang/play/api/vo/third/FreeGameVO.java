package com.cloud.baowang.play.api.vo.third;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "免费游戏对象")
public class FreeGameVO implements Serializable {
    @Schema(title = "用户账号")
    private List<String> userAccounts;

    /**
     * {@link com.cloud.baowang.play.api.enums.venue.VenueEnum}
     */
    @Schema(title = "场馆code")
    private String venueCode;

    @Schema(title = "站点编码")
    private String siteCode;

    @Schema(title = "赠送次数")
    private Integer count;

    @Schema(title = "赠送备注")
    private String description;

}
