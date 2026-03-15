package com.cloud.baowang.system.api.vo.exchange;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 13:55
 * @Version: V1.0
 **/
@Data
@Schema(description = "币种信息")
public class SystemCurrencyInfoUpdateReqVO extends SystemCurrencyInfoNewReqVO{

    @Schema(description = "主键ID")
    private String id;

}
