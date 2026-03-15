package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("site_venue")
public class SiteVenuePO extends BasePO implements Serializable {


    /**
     * 游戏平台CODE
     */
    private String venueCode;

    /**
     * 场馆ID
     */
    private String venueId;

    /**
     * 负盈利手续费
     */
    private BigDecimal handlingFee;

    /**
     * 场馆有效流水费率
     */
    private BigDecimal validProportion;



    /**
     * 站点CODE
     */
    private String siteCode;


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

    private Integer siteLabelChangeType;

}
