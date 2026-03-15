package com.cloud.baowang.play.wallet.vo.req.pg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PgAdjustmentReq extends PgBaseReq {

    @Schema(title = "调整的参考 ID")
    private String adjustment_id;
    @Schema(title = "交易的唯一标识符 注：最多200字符运营商应使用此参数来检查请求是否重复并实现幂等操作")
    private String adjustment_transaction_id;
    @Schema(title = "调整时间 Unix时间戳以毫秒为单位")
    private Long adjustment_time;
    @Schema(title = "调整来源：115-优惠；900-外部调整；901-锦标赛调整")
    private String transaction_type;
    @Schema(title = "游戏启动模式")
    private Integer bet_type;

    @Schema(title = "优惠的参考 ID 非必须字段")
    private Integer promotion_id;
    @Schema(title = "优惠类型：1-幸运红包雨、2-幸运红包雨每日奖金、3-天天大满罐")
    private Integer promotion_type;
    @Schema(title = "余额的备注")
    private String remark;
}
