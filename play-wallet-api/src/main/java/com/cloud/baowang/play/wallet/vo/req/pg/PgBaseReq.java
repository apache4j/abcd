package com.cloud.baowang.play.wallet.vo.req.pg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PgBaseReq implements Serializable {

    private static final long serialVersionUID = -3603865953499373528L;

    @Schema(title = "请求的验证")
    private String trace_id;

    @Schema(title = "运营商独有的身份识别", required = true)
    protected String operator_token;

    @Schema(title = "PG SOFT 与运营商之间共享密码", required = true)
    protected String secret_key;


    @Schema(title = "游戏的独有代码")
    protected String game_id;

    @Schema(title = "游戏启动时运营商系统生成的玩家令牌")
    protected String operator_player_session;

    @Schema(title = "玩家账号", required = true)
    protected String player_name;

    @Schema(title = "玩家选择的币种", required = true)
    private String currency_code;

    @Schema(title = "玩家的输赢金额 玩家的输赢金额 该金额可以是正数或负数 负数：扣除余额 正数：增加余额")
    private BigDecimal transfer_amount;

    @Schema(title = "现实中的实际交易金额 负数：扣除余额 正数：增加余额")
    private BigDecimal real_transfer_amount;

    /**
     * 验证参数是否齐全
     *
     * @return true:验证通过 false:失败
     */
    public boolean validate() {
        return StringUtils.isNotBlank(operator_token)
            && StringUtils.isNotBlank(secret_key)
            && StringUtils.isNotBlank(player_name);
    }

}
