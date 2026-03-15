package com.cloud.baowang.play.vo;

import com.cloud.baowang.common.core.enums.LanguageEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(title = "游戏名称VO")
public class GameNameVO {

    @Schema(title = "游戏名称")
    private String gameName;

    /**
     * @link{com.cloud.baowang.common.core.enums.LanguageEnum}
     */
    @Schema(title = "语言")
    private String lang;


}
