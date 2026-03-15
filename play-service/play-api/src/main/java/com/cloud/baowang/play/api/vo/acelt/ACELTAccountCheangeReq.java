package com.cloud.baowang.play.api.vo.acelt;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ACELTAccountCheangeReq extends ACELTBaseReq {

    /**
     * 用户名
     */
    private String operatorAccount;

    /**
     * 交易凭证
     */
    private String transferReference;

    /**
     * 交易类型
     */
    private Integer transferType;

    private Integer inOut;            // 收入或支出（总）

    private String currencyType;      // 币种

    /**
     * 金额
     */
    private BigDecimal totalAmount;

    private List<ACELTDetailReq> detail;

    public Boolean valid() {
        boolean result = ObjectUtil.isAllNotEmpty(this.getOperatorAccount(), this.getOperatorId(), this.getSign(), this.getTimestamp()
                , transferReference, transferType, inOut, currencyType, totalAmount) && totalAmount.compareTo(BigDecimal.ZERO) > 0
                && CollectionUtil.isNotEmpty(detail);
        if (!result) {
            return false;
        }
        for (ACELTDetailReq item : detail) {
            if (ObjectUtil.isAllNotEmpty(item.getBetNum(), item.getTradeAmount()) && item.getTradeAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
        }
        return true;
    }

}
