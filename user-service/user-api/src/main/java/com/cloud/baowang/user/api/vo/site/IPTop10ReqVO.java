package com.cloud.baowang.user.api.vo.site;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "站点首页to10 IP请求入参")
public class IPTop10ReqVO extends PageVO implements Serializable {

    @Schema(title = "站点", hidden = true)
    private String siteCode;

    /**
     * ip归宿
     */
    @Schema(title = "查询开始")
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    @Schema(title = "查询结束")
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

}
