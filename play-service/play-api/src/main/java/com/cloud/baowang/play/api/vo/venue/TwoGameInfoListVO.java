package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author qiqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏信息返回对象")
@I18nClass
public class TwoGameInfoListVO implements Serializable {

    @Schema(description = "所有的游戏")
    private List<TwoGameInfoVO> allGameList;

    @Schema(description = "已关联")
    private List<TwoGameInfoVO> inList;

    @Schema(description = "币种-所有的游戏")
    private List<TwoCurrencyGameInfoListVO> currencyAllGameList;


    @Schema(description = "币种-已关联")
    private List<TwoCurrencyGameInfoListVO> currencyInGameList;




}
