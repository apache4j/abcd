package com.cloud.baowang.user.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 12/5/23 6:22 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员备注信息")
public class UserRemarkVO implements Serializable {

    @Schema(description ="主键id")
    private String id;

    @Schema(description ="备注账号")
    private String memberAccount;

    @Schema(description ="备注信息")
    private String remark;

    @Schema(description ="更新时间")
    private Long updateTime;
    @Schema(description ="操作人")
    private String operator;
}
