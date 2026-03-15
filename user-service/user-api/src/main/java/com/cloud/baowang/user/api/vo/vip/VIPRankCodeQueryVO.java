package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 4/8/23 1:52 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP等级传入参数")
public class VIPRankCodeQueryVO implements Serializable {

    @Schema(title = "VIP等级code")
    @NotEmpty(message = "VIPCode不能为空")
    private String vipGradeCode;

    private String siteCode;
}
