package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: sheldon
 * @Date: 3/30/24 9:23 上午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "游戏大厅品牌视图")
public class LobbyPaymentVendorVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "名称")
    private String name;


    @Schema(description = "图片")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(description = "图片")
    private String iconFileUrl;


}
