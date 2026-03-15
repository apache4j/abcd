package com.cloud.baowang.system.api.vo.banner;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@I18nClass
@Schema(description = "站点banner新增VO")
public class SiteBannerConfigPageQueryReqVO extends PageVO {


    @Schema(description = "站点编码，标识所属站点", hidden = true)
    private String siteCode;

    @Schema(description = "展示位置(一级分类id,首页顶部为0)")
    private String gameOneClassId;

    @Schema(description = "轮播图名称")
    private String bannerName;

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

    @Schema(description = "启用状态")
    private Integer status;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "操作人")
    private String updater;
}
