package com.cloud.baowang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 27/5/23 5:07 PM
 * @Version : 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "游戏补单请求对象")
public class CompensationOrderReqVO implements Serializable {

    @Schema(title = "场馆")
    private String venueCode;

    @Schema(title = "开始时间(时间戳)")
    @NotNull(message = "开始时间不能为空")
    private String startTime;

    @Schema(title = "结束时间(时间戳)")
    @NotNull(message = "结束时间不能为空")
    private String endTime;

}
