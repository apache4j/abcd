package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 2024/7/26 15:51
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点列表查询参数")
public class SiteRequestVO extends PageVO {

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "所属公司")
    private String company;

    @Schema(description = "站点模式")
    private String siteModel;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;

    @Schema(description = "站点类型")
    private Integer siteType;

    @Schema(description = "保证金开关 0-禁用 1-启用")
    private Integer guaranTeeFlag;
}
