package com.cloud.baowang.user.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户类型")
@Builder
public class UserTypeVO {
    @Schema(description = "编号")
    private Integer code;
    @Schema(description = "名称")
    private String value;

}
