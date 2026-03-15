package com.cloud.baowang.report.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 2024/11/6 18:52
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "用户VIP数据返回信息")
public class UserVIPVO implements Serializable {

    @Schema(title = "日期显示 维度 天")
    private Long dateShow;

    @Schema(title = "站点编码")
    private String siteCode;

    @Schema(title = "段位code")
    private Integer vipRankCode;

    @Schema(title = "等级code")
    private Integer vipGradeCode;

    @Schema(title = "现有等级人数")
    private Integer currentGradeNum;

}
