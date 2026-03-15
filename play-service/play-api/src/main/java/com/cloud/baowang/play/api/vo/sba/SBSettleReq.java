package com.cloud.baowang.play.api.vo.sba;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBSettleReq {

    @Schema(title = "CancelBet")
    private String action;

    @Schema(title = "交易纪录 id")
    private String operationId;


    private List<SBSettleDetailReq> txns;

    /**
     * 主动发起拉单的参数进行转换
     */
    public static SBSettleReq transHistoryConversion(CheckTicketStatusVO checkTicketStatusVO, TransHistoryVO historyVO) {
        List<SBSettleDetailReq> txns = Lists.newArrayList();
        SBSettleDetailReq settleDetailReq = SBSettleDetailReq.builder().build();
        BeanUtils.copyProperties(historyVO, settleDetailReq);
        settleDetailReq.setRefId(checkTicketStatusVO.getRefId());
        settleDetailReq.setUserId(checkTicketStatusVO.getUserId());
        txns.add(settleDetailReq);
        return SBSettleReq.builder().txns(txns).action(historyVO.getAction()).operationId(historyVO.getOperationId()).build();
    }


    public boolean validate() {

        if (CollectionUtil.isEmpty(txns)) {
            return false;
        }
        for (SBSettleDetailReq item : txns) {
            if (!(ObjectUtil.isNotEmpty(item.getUserId()) && ObjectUtil.isNotEmpty(item.getRefId()))) {
                return false;
            }

            //加余额与减余额不能同时有值
            if (item.getCreditAmount().compareTo(BigDecimal.ZERO) > 0 && item.getDebitAmount().compareTo(BigDecimal.ZERO) > 0) {
                return false;
            }
        }
        return true;
    }


}

