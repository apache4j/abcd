package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 14:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "币种排序保存")
public class SortNewReqVO {
    @Schema(description = "主键ID")
    private String id;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

}
