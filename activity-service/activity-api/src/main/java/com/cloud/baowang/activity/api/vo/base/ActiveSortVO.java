package com.cloud.baowang.activity.api.vo.base;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "活动页签下拉框返回对象")
public class ActiveSortVO {
    @Schema(title = "活动id")
    private String id;

    @Schema(title = "排序标识")
    private Integer sort;
}
