package com.cloud.baowang.play.api.vo.pp.req;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "币种限制-三方配置")
public class PPGameLimitReqVO implements Serializable {

    //奖励码
    @Schema(description = "游戏id, 如果是多个,号添加")
    String gameIds;
    //场馆编码
    @Schema(description = "场馆code")
    String venueCode;
    //站点编码
    @Schema(description = "站点编码",hidden = true)
    String siteCode;
    @Schema(description = "币种，如果是多个,号添加")
    String currencies;

    public boolean isValid() {
        return

                StrUtil.isNotEmpty(this.getSiteCode()) &&
                        StrUtil.isNotEmpty(this.getVenueCode())
                ;
    }

}
