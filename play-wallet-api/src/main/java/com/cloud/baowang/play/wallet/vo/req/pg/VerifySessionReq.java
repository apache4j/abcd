package com.cloud.baowang.play.wallet.vo.req.pg;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Tag(name = "令牌校验")
@ToString(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VerifySessionReq {
	@Schema(title = "请求的验证")
	private String trace_id;

	@Schema(title = "运营商独有的身份识别", required = true)
	private String operator_token;

	@Schema(title = "PGSoft 与运营商之间共享密码", required = true)
	private String secret_key;

	@Schema(title = "运营商系统生成的令牌", required = true)
	private String operator_player_session; //  注解: 上限 200 字符  请使用 UrlDecode 解码参数值，以避免发生未知错误。

	@Schema(title = "玩家 IP 地址")
	private String ip;

	@Schema(title = "URL schema16中的operator_param 值")
	private String custom_parameter;

	@Schema(title = "游戏的独有代码")
	private String game_id; // 游戏的独有代码

	@Schema(title = "游戏启动模式")
	private String bet_type; // 游戏的独有代码
}
