package com.cloud.baowang.system.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/27 15:24
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点配置邮箱通道授权对象")
public class SiteEmailVO {

    @Schema(description ="邮箱通道ID")
    private List<String> emailId;
}
