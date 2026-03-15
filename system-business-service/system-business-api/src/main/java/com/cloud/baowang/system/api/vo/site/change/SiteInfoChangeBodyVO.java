package com.cloud.baowang.system.api.vo.site.change;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
/**
 * @Author : mufan
 * @Date : 2025/4/5 11:57
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteInfoChangeBodyVO {
    @Schema(title = "修改前数据")
    private Object changeBeforeObj;
    @Schema(title = "所属修改后数据")
    private Object changeAfterObj;
    @Schema(title = "字段名和中文名内容")
    private Map<String,String> columnNameMap;
    @Schema(title = "分类  SiteChangeTypeEnum")
    private String changeType;
}
