package com.cloud.baowang.system.api.vo.business;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @className: BusinessContectVO
 * @author: wade
 * @description: 商务信息返回
 * @date: 2024/1/19 10:34
 */
@Data
@Schema( description = "商务信息返回")
public class BusinessConfigVO {


    @Schema(description ="地址类型(8:skype,9:telegram,10:join_us)")
    private String addType;

    @Schema(description ="地址类型type:name(8:skype,9:telegram,10:join_us)")
    private String addTypeName;

    @Schema(description ="地址")
    private String address;

    @Schema(description ="备注")
    private String remark;
}
