package com.cloud.baowang.activity.api.vo.task;


import com.cloud.baowang.activity.api.vo.base.ActiveSortVO;
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
@Schema(title = "任务排序请求入参数")
public class SiteTasKConfigSortReqVO implements Serializable {
    @Schema(title = "站点siteCode",hidden = true)
    private String siteCode;

    @Schema(title = "operator",hidden = true)
    private String operator;

    @Schema(title = "排序标识")
    private List<ActiveSortVO> sortVOS;


}
