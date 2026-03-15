package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sheldon
 * @Date: 3/30/24 9:23 上午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "供应商返回对象")
public class LobbyVenueVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "名称")
    @I18nField
    private String name;

    @Schema(title = "图片")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(title = "游戏数量")
    private Integer gameSize;

}
