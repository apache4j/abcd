package com.cloud.baowang.play.api.vo.venue;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/29 11:57
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点场馆授权查询参数")
@I18nClass
public class SiteVenueResponseVO {

    @Schema(description ="选中场馆手续费集合")
    private List<SiteVenueAuthorizeQueryVO> siteVenue;

    @Schema(description ="全部场馆手续费集合")
    private List<FeeVO> allVenueFee;

    @Schema(description ="站点场馆授权分页")
    private Page<SiteVenueResponsePageVO> pageVO;
}
