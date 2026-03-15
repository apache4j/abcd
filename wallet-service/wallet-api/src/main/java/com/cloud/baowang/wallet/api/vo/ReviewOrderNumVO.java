package com.cloud.baowang.wallet.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "审核订单数量返回对象")
public class ReviewOrderNumVO {

    private Integer num;

    private String router;
}
