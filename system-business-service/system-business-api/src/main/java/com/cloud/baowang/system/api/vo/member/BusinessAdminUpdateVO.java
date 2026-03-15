package com.cloud.baowang.system.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * @author qiqi
 */
@Data
@Schema(description = "职员修改请求对象")
public class BusinessAdminUpdateVO {

    @Schema(description = "职员ID", required = true)
    @NotNull(message = "职员ID不能为空")
    private String id;

    /*@Schema(description = "用户名", required = true)
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6, max = 12, message = "用户名介于6-12个字符!")
    private String userName;*/

    @Schema(description = "姓名", required = true)
    @NotEmpty(message = "姓名不能为空")
    @Length(min = 2, max = 10, message = "姓名介于2-10个字符!")
    private String nickName;


    @Schema(description = "谷歌验证秘钥", required = true)
    private String googleAuthKey;

    @Schema(description = "角色IDS")
    @NotNull(message = "角色IDS不能为空")
    @Size(min = 1, message = "至少选择一个角色")
    private String[] roleIds;

    private String updater;


}
