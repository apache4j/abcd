package com.cloud.baowang.system.api.vo.banner;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
@I18nClass
@Schema(description = "站点banner新增VO")
public class SiteBannerConfigReqVO {
    @Schema(description = "operator", hidden = true)
    private String operator;

    @Schema(description = "站点编码，标识所属站点", hidden = true)
    private String siteCode;

    @Schema(description = "主键，唯一标识每条记录")
    private String id;

    @Schema(description = "一级分类id,展示位置(大于0=一级分类ID,0:首页,-1:皮肤四,我的,-2:皮肤四,优惠活动)")
    private String gameOneClassId;

    @Schema(description = "轮播图区域，标识轮播图所属区域")
    private Integer bannerArea;

    @Schema(description = "时效（使用 BannerDuration 枚举：0 - 限时, 1 - 长期）")
    private Integer bannerDuration;

    @Schema(description = "展示开始时间（以时间戳形式表示）")
    private Long displayStartTime;

    @Schema(description = "展示结束时间（以时间戳形式表示）")
    private Long displayEndTime;

    @Schema(description = "是否跳转（0 - 否, 1 - 是）")
    private Integer isRedirect;

    @Schema(description = "跳转目标（使用 BannerLinkTarget 枚举：0 - 内部链接, 1 - 游戏ID, 2 - 活动ID）")
    private Integer redirectTarget;

    @Schema(description = "跳转目标地址配置，具体的跳转目标信息")
    private String redirectTargetConfig;

    @Schema(description = "轮播图名称，展示的名称")
    private String bannerName;

    @Schema(description = "轮播图i18", hidden = true)
    private String bannerUrl;

    @Schema(description = "status")
    @Max(value = 1, message = ConstantsCode.PARAM_ERROR)
    @Min(value = 0, message = ConstantsCode.PARAM_ERROR)
    private Integer status;

    @Schema(description = "轮播图多语言数组")
    private List<I18nMsgFrontVO> bannerUrlList;

    @Schema(description = "h5轮播图i18", hidden = true)
    private String h5BannerName;

    @Schema(description = "皮肤2h5轮播图")
    private List<I18nMsgFrontVO> h5BannerNameList;

    @Schema(description = "轮播图多语言数组-黑,皮肤4-pc")
    private List<I18nMsgFrontVO> darkBannerUrlList;

    @Schema(description = "黑底轮播图i18-皮肤4", hidden = true)
    private String darkBannerUrl;




    @Schema(description = "黑底h5轮播图i18", hidden = true)
    private String darkH5BannerUrl;

    @Schema(description = "h5-黑底轮播图多语言数组-皮肤4-h5")
    private List<I18nMsgFrontVO> darkH5BannerUrlList;






}
