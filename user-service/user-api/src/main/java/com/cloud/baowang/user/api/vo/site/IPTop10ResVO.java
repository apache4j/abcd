package com.cloud.baowang.user.api.vo.site;


import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(description = "站点首页IP或者域名返回结果")
public class IPTop10ResVO  implements Serializable {


    /**
     * ip归宿
     */
    @Schema(title = "来路")
    @ExcelProperty("来路")
    private String ipAddress;

    @Schema(title = "访问数量")
    @ExcelProperty("访问数量")
    private Long visitCount;

}
