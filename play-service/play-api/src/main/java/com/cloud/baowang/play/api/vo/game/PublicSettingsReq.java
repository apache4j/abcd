package com.cloud.baowang.play.api.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicSettingsReq {


    @Schema(title = "标记类型:sport_odds = 体育赔率设置")
    private String type;
    
    @Schema(title = "值,配置中涉及到的 boolean统一使用 0:1 存储 ;0=false 1=ture")
    private String value;


}
