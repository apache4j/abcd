package com.cloud.baowang.system.po.banner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("site_banner_config")
public class SiteBannerConfigPO extends BasePO {


    /**
     * 站点编码，标识所属站点
     */
    private String siteCode;

    /**
     * 展示位置，指明轮播图的展示位置
     */
    private String gameOneClassId;

    /**
     * 轮播图区域，标识轮播图所属区域
     * {@link com.cloud.baowang.system.api.enums.banner.BannerArea}
     */
    private Integer bannerArea;

    /**
     * 时效（使用 BannerDuration 枚举：0 - 限时, 1 - 长期）
     * {@link com.cloud.baowang.system.api.enums.banner.BannerDuration}
     */
    private Integer bannerDuration;

    /**
     * 展示开始时间（以时间戳形式表示）
     */
    private Long displayStartTime;

    /**
     * 展示结束时间（以时间戳形式表示）
     */
    private Long displayEndTime;

    /**
     * 是否跳转（0 - 否, 1 - 是）
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     */
    private Integer isRedirect;

    /**
     * 跳转目标（使用 BannerLinkTarget 枚举：0 - 内部链接, 1 - 游戏ID, 2 - 活动ID）
     * <p>
     * {@link com.cloud.baowang.system.api.enums.banner.BannerLinkTarget}
     */
    private Integer redirectTarget;

    /**
     * 跳转目标地址配置，具体的跳转目标信息
     */
    private String redirectTargetConfig;

    /**
     * 轮播图名称，展示的名称
     */
    private String bannerName;

    /**
     * 轮播图图片i18
     */
    private String bannerUrl;
    /**
     * h5轮播图片i18,皮肤2如果有
     */
    private String h5BannerName;

    /**
     * {@link com.cloud.baowang.common.core.enums.EnableStatusEnum}
     * 状态
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;


    /**
     * 黑底轮播图 皮肤4-pc
     */
    private String darkBannerUrl;

    /**
     * 黑底轮播图 皮肤4
     */
    private String darkH5BannerUrl;


}
