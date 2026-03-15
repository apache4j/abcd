package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/29/24 5:29 下午
 */

@Data
@Builder
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "轮播图对象")
public class LobbyBannerResVO {

    @Schema(title = "id")
    private String id;

    @Schema(description = "是否跳转（0 - 否, 1 - 是）")
    private Integer isRedirect;

    @Schema(title = "轮播图地址")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(title = "轮播图地址")
    private String iconFileUrl;

    @Schema(title = "H5-轮播图地址")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String h5Icon;

    @Schema(title = "H5-轮播图地址")
    private String h5IconFileUrl;

    @Schema(title = "游戏名称")
    @I18nField
    private String gameName;

    @Schema(title = "游戏id")
    private String gameId;

    @Schema(title = "游戏CODE")
    private String gameCode;

    @Schema(description = "游戏类型CODE")
    private String gameCategoryCode;

    @Schema(title = "场馆CODE,SBA:沙巴体育,王牌彩票:ACELT")
    private String venueCode;

    @Schema(title = "活动模板")
    private String activityTemplate;

    @Schema(title = "模板:0:内部链接,1:游戏ID,2:活动ID")
    private Integer template;

    @Schema(title = "内部链接")
    @I18nField
    private String url;

    @Schema(title = "活动ID")
    private String activityId;

    @Schema(title = "活动名称")
    @I18nField
    private String activityNameI18nCode;

    @Schema(description = "自动切换时间,单位秒")
    private Long switchTime;


    @Schema(title = "黑底轮播图地址-皮肤4-pc")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String darkIcon;

    @Schema(title = "黑底轮播图地址-黑底-pc")
    private String darkIconFileUrl;


    @Schema(title = "H5-轮播图地址-黑底-h5")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String darkH5Icon;

    @Schema(title = "H5-轮播图地址-黑底-h5")
    private String darkH5IconFileUrl;

    @Schema(description = "h5活动跳转URl")
    private String h5ActivityUrl;
}
