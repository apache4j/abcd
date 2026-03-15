package com.cloud.baowang.play.wallet.vo.res.pg;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Tag(name = "令牌校验响应值")
public class VerifySessionRes {
	@Schema(title = "玩家帐号")
	private String player_name;

	@Schema(title = "玩家昵称")
	private String nickName;

	@Schema(title = "玩家选择的币种")
	private String currency;

}
