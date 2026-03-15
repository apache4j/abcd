package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemActivityTemplateReqVO  extends PageVO implements Serializable {

    @Schema(title = "活动模版编号")
    private String activityTemplate;

//    @Schema(title = "盘口模式:0:国际盘 1:大陆盘")
//    private Integer handicapMode=0;

}
