package com.cloud.baowang.play.api.vo.evo;

import com.cloud.baowang.play.api.enums.evo.EvoGameErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BalanceResponse implements Serializable {
    /**
     * Describes status of request.
     * One of the "status" enumerated values. See Appendix "Status Types and Error Messages".
     * - If response header is not HTTP 200, it is mapped to TEMPORARY_ERROR;
     * - If response cannot be parsed, it is mapped to TEMPORARY_ERROR;
     * - Any values that are not in the list are mapped to UNKNOWN_ERROR.
     * <p>
     * 请求状态描述。
     * 取值为“状态”枚举值之一。详见附录“状态类型和错误信息”。
     * - 若响应头非HTTP 200，映射为TEMPORARY_ERROR；
     * - 若响应无法解析，映射为TEMPORARY_ERROR；
     * - 其它不在列表中的值映射为UNKNOWN_ERROR。
     */
    private String status;

    /**
     * Available balance of a player (without reserved for any session).
     * Balance returned should have a precision of 2 decimal digits.
     * Evolution does not round returned value, only uses 2 decimal values.
     * <p>
     * 玩家可用余额（不包含为任何会话预留的部分）。
     * 返回余额精度为小数点后2位，Evolution不做四舍五入，仅保留2位小数。
     */
    private BigDecimal balance;

    /**
     * Player's bonus balance to be added to real balance in "balance" property,
     * and used as total allowed bonus for user.
     * Optional.
     * <p>
     * 玩家奖金余额，将与“balance”属性中的真实余额相加，
     * 用作用户允许使用的总奖金余额。
     * 可选字段。
     */
    private BigDecimal bonus;

    /**
     * Unique response ID, that identifies AAMSBalanceResponse.
     * <p>
     * 唯一响应ID，用于标识该AAMSBalanceResponse。
     */
    private String uuid;

    /**
     * Retransmission flag, optional.
     * <p>
     * 是否为重传标志，可选字段。
     */
    private Boolean retransmission;

    /**
     * 构建成功响应
     */
    public static BalanceResponse success(String uuid, BigDecimal balance) {
        BalanceResponse resp = new BalanceResponse();
        resp.setStatus("OK");  // 通常状态用大写 OK
        resp.setUuid(uuid);
        resp.setBalance(balance);
        resp.setBonus(BigDecimal.ZERO);
        return resp;
    }

    /**
     * 构建失败响应
     */
    public static BalanceResponse fail(String uuid, EvoGameErrorCode evoGameErrorCode) {
        BalanceResponse resp = new BalanceResponse();
        resp.setStatus(evoGameErrorCode.getMessage());  // 通常状态用大写 FAIL
        resp.setUuid(uuid);
        resp.setBalance(null);
        resp.setBonus(null);
        return resp;
    }
}
