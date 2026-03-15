package com.cloud.baowang.user.api.vo.medal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章信息")
public class MedalInfoStatusReqVO {
    @Schema(description = "Id")
    private String id;

    @Schema(description = "操作人 ")
    private String operatorUserNo;

}
