package com.cloud.baowang.system.api.vo.site.tutorial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="排序通用对象")
public class SortVo {
    @NotNull(message = "站点id不能为空")
    private String siteCode;
    @NotNull(message = "分类名称集合不能为空")
    private List<String> sourceList;
}
