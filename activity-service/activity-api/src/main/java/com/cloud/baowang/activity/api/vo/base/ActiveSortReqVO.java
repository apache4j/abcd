package com.cloud.baowang.activity.api.vo.base;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "活动排序请求入参数")
public class ActiveSortReqVO implements Serializable {
    @Schema(title = "站点siteCode",hidden = true)
    private String siteCode;

    @Schema(title = "adminName",hidden = true)
    private String adminName;

    @Schema(title = "排序标识")
    private List<ActiveSortVO> sortVOS;


}
