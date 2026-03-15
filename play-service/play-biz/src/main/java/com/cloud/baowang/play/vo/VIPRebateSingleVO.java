package com.cloud.baowang.play.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author 小智
 * @Date 9/5/23 4:47 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "VIP返水比例返回单个对象")
public class VIPRebateSingleVO implements Serializable {

    @Schema(title = "场馆id")
    private String venueCode;

    @Schema(title = "场馆名称")
    private String venueName;

    @Schema(title = "返水开关")
    private Integer rebateSwitch;

    @Schema(title = "VIP0返水比例")
    private BigDecimal rebateVip0;

    @Schema(title = "VIP1返水比例")
    private BigDecimal rebateVip1;

    @Schema(title = "VIP2返水比例")
    private BigDecimal rebateVip2;

    @Schema(title = "VIP3返水比例")
    private BigDecimal rebateVip3;

    @Schema(title = "VIP4返水比例")
    private BigDecimal rebateVip4;

    @Schema(title = "VIP5返水比例")
    private BigDecimal rebateVip5;

    @Schema(title = "VIP6返水比例")
    private BigDecimal rebateVip6;

    @Schema(title = "VIP7返水比例")
    private BigDecimal rebateVip7;

    @Schema(title = "VIP8返水比例")
    private BigDecimal rebateVip8;

    @Schema(title = "VIP9返水比例")
    private BigDecimal rebateVip9;

    @Schema(title = "VIP10返水比例")
    private BigDecimal rebateVip10;


}
