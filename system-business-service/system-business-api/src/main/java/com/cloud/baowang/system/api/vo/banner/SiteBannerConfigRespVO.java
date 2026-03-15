package com.cloud.baowang.system.api.vo.banner;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@I18nClass
@Schema(description = "站点banner配置vo")
public class SiteBannerConfigRespVO {

    @Schema(description = "主键，唯一标识每条记录")
    private String id;

    @Schema(description = "站点编码，标识所属站点")
    private String siteCode;
    @Schema(description = "所属分类id")
    private String gameOneClassId;

    @Schema(description = "展示位置，指明轮播图的展示位置")
    private Integer displayPosition;

    @Schema(description = "轮播图区域，标识轮播图所属区域")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BANNER_AREA)
    private Integer bannerArea;

    @Schema(description = "轮播图区域，标识轮播图所属区域")
    private String bannerAreaText;

    @Schema(description = "时效（使用 BannerDuration 枚举：0 - 限时, 1 - 长期）")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BANNER_DURATION)
    private Integer bannerDuration;

    @Schema(description = "时效（使用 BannerDuration 枚举：0 - 限时, 1 - 长期）")
    private String bannerDurationText;


    @Schema(description = "展示开始时间（以时间戳形式表示）")
    private Long displayStartTime;

    @Schema(description = "展示结束时间（以时间戳形式表示）")
    private Long displayEndTime;

    @Schema(description = "是否跳转（0 - 否, 1 - 是）")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private Integer isRedirect;

    @Schema(description = "是否跳转（0 - 否, 1 - 是）")
    private String isRedirectText;

    @Schema(description = "跳转目标（使用 BannerLinkTarget 枚举：0 - 内部链接, 1 - 游戏ID, 2 - 活动ID）")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BANNER_LINK_TARGET)
    private Integer redirectTarget;

    @Schema(description = "跳转目标（使用 BannerLinkTarget 枚举：0 - 内部链接, 1 - 游戏ID, 2 - 活动ID）")
    private String redirectTargetText;

    @Schema(description = "跳转目标地址配置，具体的跳转目标信息")
    private String redirectTargetConfig;

    @Schema(description = "轮播图名称，展示的名称")
    private String bannerName;

    @Schema(description = "轮播图i18")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String bannerUrl;

    @Schema(description = "轮播图")
    private List<I18nMsgFrontVO> bannerUrlList;

    @Schema(description = "皮肤2h5轮播图i18")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String h5BannerName;

    @Schema(description = "皮肤2h5轮播图")
    private List<I18nMsgFrontVO> h5BannerNameList;

    @Schema(description = "启用状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    @Schema(description = "启用状态")
    private String statusText;


    @Schema(description = "黑底轮播图i18-皮肤4")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String darkBannerUrl;


    @Schema(description = "轮播图多语言数组-黑,皮肤4-pc")
    private List<I18nMsgFrontVO> darkBannerUrlList;


    @Schema(description = "黑底h5轮播图i18-h5")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String darkH5BannerUrl;

    @Schema(description = "h5-黑底轮播图多语言数组-皮肤4-h5")
    private List<I18nMsgFrontVO> darkH5BannerUrlList;

}
