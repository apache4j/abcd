package com.cloud.baowang.system.api.vo.site.agreement;


import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "商务排序对象")
public class SortVO {

    private List<String> ids;

    @Schema(hidden = true)
    private String siteCode;

    @Schema(hidden = true)
    private Long updatedTime;
    @Schema(hidden = true)
    private String updater;

}
