package com.cloud.baowang.user.api.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author : 小智
 * @Date : 2025/3/21 14:55
 * @Version : 1.0
 */
@Data
@Schema(description = "手动归集传入参数")
public class ManuBlockVO {

    @Schema(description = "链类型 (TRON,ETH)")
    private String chainType;

    @Schema(description = "区块高度")
    private String block;
}
