package com.cloud.baowang.report.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 2024/11/6 11:57
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "VIP数据报表参数")
public class VIPDataVO implements Serializable {

    private String siteCode;

    private Long beginDate;

    private Long endDate;

    private String timeZone;
}
