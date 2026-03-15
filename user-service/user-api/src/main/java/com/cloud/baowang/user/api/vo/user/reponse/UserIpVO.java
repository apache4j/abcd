package com.cloud.baowang.user.api.vo.user.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 6/30/24 10:58 PM
 * @Version : 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIpVO {

    @Schema(title = "国家code(台湾:TW, 中国:CN, 香港:HK, 澳门:MO)")
    private String countryCode;

    @Schema(title = "城市")
    private String city;

    @Schema(title = "地区")
    private String region;

    @Schema(title = "ip")
    private String ip;
}
