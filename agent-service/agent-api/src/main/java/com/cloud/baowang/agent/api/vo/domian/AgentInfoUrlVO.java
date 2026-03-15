package com.cloud.baowang.agent.api.vo.domian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : leiding
 * @Date : 11/12/24 16:27 PM
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "短网址返回VO")
public class AgentInfoUrlVO implements Serializable {
    @Schema(description="短网址")
    private String shortUrl;
    @Schema(description="合营代码")
    private String inviteCode;
}
