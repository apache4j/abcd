package com.cloud.baowang.play.api.vo.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "一级分类添加场馆请求对象")
public class AddGameOneClassVenueSortVO {

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "场馆")
    private String venueCode;

}
