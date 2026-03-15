package com.cloud.baowang.play.api.vo.sba;


import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class SBConfirmBetReq {

    @Schema(title = "ConfirmBet")
    private String action;

    @Schema(title = "交易纪录 id")
    private String operationId;

    @Schema(title = "用户 id")
    private String userId;

    @Schema(title = "更新时间")
    private String updateTime;

    @Schema(title = "沙巴系統投注交易时间")
    private String transactionTime;

    private List<SBTicketInfoReq> txns;

    /**
     * 主动发起拉单的参数进行转换
     */
    public static SBConfirmBetReq transHistoryConversion(CheckTicketStatusVO checkTicketStatusVO, TransHistoryVO historyVO) {
        List<SBTicketInfoReq> txns = Lists.newArrayList();
        SBTicketInfoReq sbTicketInfoReq = SBTicketInfoReq.builder().build();
        BeanUtils.copyProperties(historyVO, sbTicketInfoReq);
        sbTicketInfoReq.setLicenseeTxId(checkTicketStatusVO.getLicenseeTxId());
        sbTicketInfoReq.setRefId(checkTicketStatusVO.getRefId());
        sbTicketInfoReq.setTxId(checkTicketStatusVO.getTxId());
        txns.add(sbTicketInfoReq);
        return SBConfirmBetReq.builder()
                .userId(checkTicketStatusVO.getUserId())
                .operationId(historyVO.getOperationId())
                .action(historyVO.getAction())
                .txns(txns)
                .build();
    }


    public boolean validate() {

        if (!(ObjectUtil.isNotEmpty(userId) &&
                ObjectUtil.isNotEmpty(txns))) {
            return false;
        }

        for (SBTicketInfoReq item : txns) {

            if (!(ObjectUtil.isNotEmpty(item.getLicenseeTxId()) &&
                    ObjectUtil.isNotEmpty(item.getTxId()) &&
                    ObjectUtil.isNotEmpty(item.getCreditAmount()) &&
                    ObjectUtil.isNotEmpty(item.getDebitAmount()))) {
                return false;
            }

            //此处已跟沙巴确认,在确认下注确认接口中,不会出现需要二次扣款的情况,如果出现则拒绝
            if (item.getDebitAmount().compareTo(BigDecimal.ZERO) > 0) {
                log.info("{},下注确认接口异常,下注确认接口不允许出现二次扣款.:{}", VenuePlatformConstants.SBA, item);
                return false;
            }
        }

        return true;
    }


}

