package com.cloud.baowang.play.api.vo.sba;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
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
public class SBCancelBetReq {

    @Schema(title = "CancelBet")
    private String action;

    @Schema(title = "交易纪录 id")
    private String operationId;

    @Schema(title = "用户 id")
    private String userId;

    @Schema(title = "更新时间")
    private String updateTime;

    @Schema(title = "投注失败原因的错误讯息。SBCancelBetErrorEnum.java")
    private String errorMessage;

    private List<TxnsReq> txns;

    /**
     * 主动发起拉单的参数进行转换
     */
    public static SBCancelBetReq transHistoryConversion(CheckTicketStatusVO checkTicketStatusVO, TransHistoryVO historyVO) {
        List<TxnsReq> txns = Lists.newArrayList();
        TxnsReq txnsReq = TxnsReq.builder().build();
        BeanUtils.copyProperties(historyVO, txnsReq);
        txnsReq.setRefId(checkTicketStatusVO.getRefId());
        txns.add(txnsReq);
        return SBCancelBetReq.builder()
                .action(historyVO.getAction())
                .userId(checkTicketStatusVO.getUserId())
                .txns(txns)
                .build();
    }

    public boolean validate() {

        if (!(ObjectUtil.isNotEmpty(userId) && CollectionUtil.isNotEmpty(txns))) {
            return false;
        }

        for (TxnsReq item : txns) {
            if (!(ObjectUtils.isNotEmpty(item.getRefId())
                    && ObjectUtils.isNotEmpty(item.getCreditAmount())
                    && ObjectUtils.isNotEmpty(item.getDebitAmount()))) {
                return false;
            }

            //加余额与减余额不能同时有值
            if (item.getCreditAmount().compareTo(BigDecimal.ZERO) > 0
                    && item.getDebitAmount().compareTo(BigDecimal.ZERO) > 0) {
                return false;
            }

        }


        return true;

    }

}

