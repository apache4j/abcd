package com.cloud.baowang.user.api.vo.medal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章信息")
public class SiteMedalInfoStatusReqVO {
    @Schema(description = "Id")
    @NotNull(message = "主键ID不能为空")
    private String id;

    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

}
