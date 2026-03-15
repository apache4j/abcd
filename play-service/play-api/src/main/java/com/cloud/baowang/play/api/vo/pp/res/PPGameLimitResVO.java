package com.cloud.baowang.play.api.vo.pp.res;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "币种限制-三方配置")
public class PPGameLimitResVO implements Serializable {

    //游戏编码
    @Schema(description = "游戏编码")
    String gameId;

    //游戏限红, 币种
    @Schema(description = "游戏限红, 币种")
    List<PPGameLimitCurrencyResVO> currencyGameLimits;

    public boolean isValid() {
        return

                StrUtil.isNotEmpty(this.getGameId()) &&
                        CollUtil.isNotEmpty(this.getCurrencyGameLimits())
                ;
    }

}
