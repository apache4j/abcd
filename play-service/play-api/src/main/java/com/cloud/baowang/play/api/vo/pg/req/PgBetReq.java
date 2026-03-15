package com.cloud.baowang.play.api.vo.pg.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class PgBetReq extends PgBaseReq {

    private static final long serialVersionUID = 4146835454954836027L;
    @Schema(title = "母注单的唯一标识符", required = true)
    private String parent_bet_id;

    @Schema(title = "子投注的唯一标识符", required = true)
    private String bet_id;


    @Schema(title = "投注金额", required = true)
    private BigDecimal bet_amount;



    @Schema(title = "赢得金额", required = true)
    private BigDecimal win_amount;

    @Schema(title = "交易的唯一标识符" +
        "106：投付\n" +
        "400：红利转现金\n" +
        "403：免费游戏转现金", required = true)
    private String transaction_id;

    @Schema(title = "交易中使用的钱包类型" +
        "C：现金\n" +
        "B：红利\n" +
        "G：免费游戏")
    private String wallet_type;

    @Schema(title = "投注记录的投注类型 1：真实游戏", required = true)
    private Integer bet_type;


    @Schema(title = "玩家平台")
    private String platform;


    @Schema(title = "投注的最近更新时间（时间戳)", required = true)
    private Long create_time;

    @Schema(title = "updated_time 不等于投注的结束时间", required = true)
    private Long updated_time;

    @Schema(title = "表示该请求是否是为进行验证而重新\n" +
        "发送的交易 True: 重新发送的交易,False: 正常交易", required = true)
    private Boolean is_validate_bet;

    @Schema(title = "表示该请求是否是待处理投注的调整\n" +
        "或正常交易,True: 调整,False: 正常交易", required = true)
    private Boolean is_adjustment;


    /**
     * 验证参数是否齐全
     *
     * @return true表示成功 false表示失败
     */
    @Override
    public boolean validate() {
        return StringUtils.isNotBlank(parent_bet_id)
            && StringUtils.isNotBlank(bet_id)
            && StringUtils.isNotBlank(operator_token)
            && StringUtils.isNotBlank(secret_key)
            && StringUtils.isNotBlank(player_name)
            && StringUtils.isNotBlank(game_id)
            && StringUtils.isNotBlank(super.getCurrency_code())
            && bet_amount.compareTo(BigDecimal.ZERO) >= 0
            && null != getTransfer_amount()
            && null != win_amount
            && StringUtils.isNotBlank(transaction_id)
            && null != bet_type
            && null != create_time
            && null != updated_time;
    }


}
