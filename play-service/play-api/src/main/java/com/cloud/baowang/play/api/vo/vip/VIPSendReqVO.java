package com.cloud.baowang.play.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 2024/10/26 15:41
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "VIP福利发放对象")
public class VIPSendReqVO implements Serializable {

    private String timeZone;

    private String siteCode;
}
