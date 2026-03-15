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
@Schema(title ="站点配置短信通道授权对象")
public class SiteSmsVO {

    @Schema(description ="短信通道ID")
    private List<String> smsId;
}
