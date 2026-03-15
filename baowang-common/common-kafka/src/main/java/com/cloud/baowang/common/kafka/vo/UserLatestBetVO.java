package com.cloud.baowang.common.kafka.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Schema(title = "用户最新投注信息-MQ参数")
public class UserLatestBetVO {

    @Schema(title = "用户账号")
    private String userId;
    @Schema(title ="投注时间")
    private Long betTime;


    /**
     * 账号类型 1测试 2正式
     */
    private Integer accountType;
}
