package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据上级代理ids 查询会员
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "根据上级代理ids 查询会员")
public class GetUserInfoByAgentIdsVO {

    @Schema(description = "上级代理id")
    private String superAgentId;
}
