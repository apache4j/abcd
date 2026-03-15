package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 2024/7/29 11:47
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点场馆授权查询参数")
public class SiteVenueRequestVO extends PageVO {

    @Schema(description ="场馆名称")
    private String venueName;

    @Schema(description ="场馆代码")
    private String venueCode;

    @Schema(description ="场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞")
    private Integer venueType;

    @Schema(description ="场馆状态 取type = platform_class_status_type")
    private String status;

    @Schema(description ="场馆code")
    //@NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String siteCode;
}
