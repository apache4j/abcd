package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/4 13:29
 * @Version: V1.0
 **/
@Data
@Schema(description = "提现方式收集字段")
public class SystemWithDrawWayCollectFieldVO {

    @Schema(description = "字段代码")
    private String filedCode;
    @Schema(description = "是否启用")
    private boolean checkFlag;




}
