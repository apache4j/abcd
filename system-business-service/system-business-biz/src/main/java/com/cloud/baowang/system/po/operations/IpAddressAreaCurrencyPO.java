package com.cloud.baowang.system.po.operations;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IP归属地方案表
 *
 * @author system
 * @since 2025-06-03 10:22:26
 */
@Data
@TableName("ip_address_area_currency")
@Schema(description = "IP归属地方案表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpAddressAreaCurrencyPO extends BasePO {

    /**
     *  分类名称
     */
    @Schema(description = "分类ID")
    private String categoryId;

    /**
     *  分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     *  包含国家
     */
    @Schema(description = "包含国家")
    private String areaCode;

    /**
     *  包含国家
     */
    @Schema(description = "包含国家")
    private String areaName;


    /**
     *  映射币种
     */
    @Schema(description = "映射币种")
    private String currencyCode;

    /**
     *  映射币种
     */
    @Schema(description = "映射币种")
    private String currencyName;


    /**
     *  优先级
     */
    @Schema(description = "优先级")
    private Integer orderSort;

    /**
     *  状态: (1 开启中 2 维护中 3 已禁用)
     */
    @Schema(description = "状态: (1 开启中 2 维护中 3 已禁用)")
    private Integer status;

    /**
     *  状态: (1 开启中 2 维护中 3 已禁用)
     */
    @Schema(description = "状态: (1 默认 0 非默认)")
    private Integer defaultType;

    /**
     *  备注
     */
    @Schema(description = "备注")
    private String remark;

}