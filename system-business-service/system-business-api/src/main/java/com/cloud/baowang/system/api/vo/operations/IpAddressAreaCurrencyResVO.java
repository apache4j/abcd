package com.cloud.baowang.system.api.vo.operations;

import com.alibaba.fastjson.JSONArray;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpAddressAreaCurrencyResVO implements Serializable {

    /**
     *  主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     *  分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "分类ID")
    private String categoryId;

    /**
     *  包含国家
     */
    @Schema(description = "包含国家")
    private String areaCode;

    /**
     *  包含国家
     */
    @Schema(description = "包含国家")
    private List<AreaVO> areaNameList;

    @Schema(description = "包含国家名称")
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

    /**
     *  创建时间
     */
    @Schema(description = "创建时间")
    private Long createdTime;

    /**
     *  更新时间
     */
    @Schema(description = "更新时间")
    private Long updatedTime;

    /**
     *
     */
    @Schema(description = " ")
    private String creator;

    /**
     *
     */
    @Schema(description = " ")
    private String updater;
}
