package com.cloud.baowang.user.api.vo.site;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户数据概览请求入参")
public class UserDataOverviewResVo implements Serializable {

    @Schema(title = "开始时间(时间戳)")
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    @Schema(title = "结束时间(时间戳)")
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "币种")
    private String currencyCode;

    private Boolean convertPlatCurrency;

    private String timeZone;


    @Schema(description = "开始时间(上期)", hidden = true)
    private Long startTimeLast;

    @Schema(description = "结束时间(上期)", hidden = true)
    private Long endTimeLast;



}
