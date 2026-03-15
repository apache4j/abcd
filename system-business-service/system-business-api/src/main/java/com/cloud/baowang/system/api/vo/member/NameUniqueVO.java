package com.cloud.baowang.system.api.vo.member;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(description = "账户设置请求对象")
public class NameUniqueVO {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "Id")
    private String id;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
