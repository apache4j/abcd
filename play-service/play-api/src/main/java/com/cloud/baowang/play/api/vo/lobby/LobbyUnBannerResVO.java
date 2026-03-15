package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "未登陆轮播图对象")
public class LobbyUnBannerResVO {


    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String pcIcon;

    @Schema(title = "PC轮播图地址")
    private String pcIconFileUrl;

    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String h5Icon;

    @Schema(title = "H5轮播图地址")
    private String h5IconFileUrl;

}
