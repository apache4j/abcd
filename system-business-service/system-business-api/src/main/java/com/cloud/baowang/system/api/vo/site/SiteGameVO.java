package com.cloud.baowang.system.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 2024/7/27 15:02
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点配置游戏授权对象")
public class SiteGameVO {

    @Schema(title ="站点授权游戏ID")
    private String gameId;

    private String updater;

    private String siteCode;

    private Integer label;

    private Integer cornerLabels;


}
