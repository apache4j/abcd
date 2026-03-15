package com.cloud.baowang.play.api.vo.pg.rsp;

import com.cloud.baowang.common.core.serializer.BigDecimalTwoDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PgAmountBetRes {
	@Schema(title = "用户币种")
	private String currency_code;
	@JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
	@Schema(title = "玩家现金余额 只支持最多 2 个小数位")
	private BigDecimal balance_amount;
	@JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
	@Schema(title = "实际交易金额")
	private BigDecimal real_transfer_amount;
	@Schema(title = "交易的更新时间(Unix 时间戳，以毫秒为单位)")
	private Long   updated_time;
}
