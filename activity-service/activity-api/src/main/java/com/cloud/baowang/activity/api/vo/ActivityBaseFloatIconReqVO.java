package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2VO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 活动基础信息的所有字段属性
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "查询活动浮标入参")
public class ActivityBaseFloatIconReqVO {

    @Schema(title = "活动列表")
    private List<ActivityBaseV2VO> requestVOList;

    @Schema(title = "浮标展示数量")
    private Integer floatIconShowNumber = 0;


}
