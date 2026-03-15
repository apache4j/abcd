package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("site_game")
public class SiteGamePO extends BasePO implements Serializable {


    /**
     * 游戏ID
     */
    private String gameInfoId;

    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 场馆CODE
     */
    private String venueCode;

    /**
     * 标签
     */
    private Integer label;

    /**
     * 角标
     */
    private Integer cornerLabels;

    /**
     * 首页 - 热门排序
     */
    private Integer homeHotSort;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 总控:最后一次状态
     */
    private Integer lastStatus;

    /**
     * 站点:最后一次状态
     */
    private Integer siteLastStatus;


    /**
     * 备注
     */
    private String remark;

    /**
     * 维护开始时间
     */
    private Long maintenanceStartTime;

    /**
     * 维护结束时间
     */
    private Long maintenanceEndTime;

    /**
     * 支持的币种
     */
    private String currencyCode;






}
