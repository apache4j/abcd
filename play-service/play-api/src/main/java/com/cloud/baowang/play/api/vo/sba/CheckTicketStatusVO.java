package com.cloud.baowang.play.api.vo.sba;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckTicketStatusVO {
    /**
     * 状态代码 (附录: 状态代码)
     */
    private int errorCode;

    /**
     * 若状态为失败，则回传失败描述
     */
    private String message;

    /**
     * 下注类型 (请参阅附件: BetType Table)
     */
    private String betType;

    /**
     * 用户 id
     */
    private String userId;

    /**
     * 商户系统交易 id
     */
    private String licenseeTxId;

    /**
     * 决胜时间(仅显示日期) (yyyy-MM-dd 00:00:00.000) GMT-4
     */
    private String winlostDate;

    /**
     * 沙巴体育系统交易 id
     */
    private String txId;

    /**
     * 唯一 id
     */
    private String refId;

    /**
     * 注单金额
     */
    private BigDecimal stake;

    /**
     * 赔率，例如：0.53
     */
    private BigDecimal odds;


    private List<TransHistoryVO> transHistory;
}
