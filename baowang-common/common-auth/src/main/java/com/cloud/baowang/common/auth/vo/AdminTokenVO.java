package com.cloud.baowang.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/08/11 22:36
 * @description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "总控token解析返回对象")
public class AdminTokenVO {
    @Schema(title = "管理员ID")
    private String adminId;
    @Schema(title = "管理员名称")
    private String userName;
    @Schema(title = "管理员角色ID集合")
    private List<String> userRoleIds;
    @Schema(title = "数据脱敏 true 需要脱敏 false 不需要脱敏")
    private Boolean dataDesensitization;
}
