package com.cloud.baowang.report.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 2024/11/7 18:28
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "VIP已达成人数")
public class VIPAchieveVO implements Serializable {

    @Schema(description = "vip等级code")
    private Integer vipGradeCode;

    @Schema(description = "该等级已达成人数")
    private Integer achieveNum;

    @Schema(title = "站点编码")
    private String siteCode;
}
