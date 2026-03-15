package com.cloud.baowang.play.api.vo.sba;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class SBConfirmBetParlayReq {

    /**
     * 交易纪录 id
     */
    private String operationId;

    /**
     * 用户 id
     */
    private String userId;


    /**
     * 更新时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4
     */
    private String updateTime;


    /**
     * 需增加在玩家的金额。
     */
    private BigDecimal creditAmount;

    /**
     * 需从玩家扣除的金额。
     */
    private BigDecimal debitAmount;

    /**
     * 请参阅表 Combo Info
     */
    private List<SBTicketInfoReq> txns;

    /**
     * 请参阅表 Ticket Detail
     */
    private List<SBTicketDetail> ticketDetail;


    public boolean validate() {

        if (CollectionUtil.isEmpty(txns) || ObjectUtil.isEmpty(userId)) {
            return false;
        }


        // 使用流操作
        Set<String> uniqueProperties = new HashSet<>();
        Set<String> duplicateProperties = txns.stream()
                .map(SBTicketInfoReq::getRefId)
                .filter(property -> !uniqueProperties.add(property)) // 如果不能添加到集合中，说明重复
                .collect(Collectors.toSet());

        if (!duplicateProperties.isEmpty()) {
            log.info("{},串关下注确认-接口异常,出现重复的ID.:{}", VenuePlatformConstants.SBA, txns);
            return false;
        }


        for (SBTicketInfoReq item : txns) {
            if (!(ObjectUtil.isNotEmpty(item.getRefId())
                    && ObjectUtil.isNotEmpty(item.getTxId())
                    && ObjectUtil.isNotEmpty(item.getCreditAmount()))) {
                return false;
            }

            //此处已跟沙巴确认,在确认下注确认接口中,不会出现需要二次扣款的情况,如果出现则拒绝
            if (item.getDebitAmount().compareTo(BigDecimal.ZERO) > 0) {
                log.info("{},串关下注确认-接口异常,下注确认接口不允许出现二次扣款.:{}", VenuePlatformConstants.SBA, item);
                return false;
            }
        }
        return true;
    }

}
