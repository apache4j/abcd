package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "体育推荐-查询参数")
public class SportRecommendRequestVO extends PageVO {

    /**
     * 1: 足球。2: 篮球。3: 美式足球。4: 冰上曲棍球。9: 羽毛球。24: 手球。26: 橄榄球。43: 电子竞技
     */
    @Schema(description = "球类,字典CODE: sport_recommend_type")
    private Integer sportType;

    /**
     * 1: 足球。2: 篮球。3: 美式足球。4: 冰上曲棍球。9: 羽毛球。24: 手球。26: 橄榄球。43: 电子竞技
     */
    @Schema(description = "球类,字典CODE: sport_recommend_type")
    private List<Integer> sportTypeList;

    @Schema(description = "联赛名称")
    private String leagueName;

    @Schema(description = "球队名称")
    private String teamName;

    @Schema(description = "赛事ID")
    private String eventsId;

    @Schema(description = "状态,字典CODE: sport_recommend_status")
    private Integer status;


    @Schema(description = "当前系统时间",hidden = true)
    private Long endTime;


}