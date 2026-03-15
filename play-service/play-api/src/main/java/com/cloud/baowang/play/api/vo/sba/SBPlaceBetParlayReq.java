package com.cloud.baowang.play.api.vo.sba;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBPlaceBetParlayReq {

    /**
     * 交易纪录 id
     */
    private String operationId;

    /**
     * 用户 id
     */
    private String userId;

    /**
     * 沙巴体育货币币别 例如：1, 2, 20
     * SBCurrencyEnum
     */
    private Integer currency;

    /**
     * 下注时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4
     */
    private String betTime;

    /**
     * 更新时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4
     */
    private String updateTime;

    /**
     * 总计注单金额 (for check balance)
     */
    private BigDecimal totalBetAmount;

    /**
     * 例如：61.221.35.49 (IPV4)
     */
    private String IP;

    /**
     * 用户登入会话 id，由商户提供
     */
    private String tsId;

    /**
     * 下注平台。请参阅 BetFrom Table(下注平台表)
     */
    private String betFrom;

    /**
     * 需增加在玩家的金额。
     */
    private BigDecimal creditAmount;

    /**
     * 需从玩家扣除的金额。
     */
    private BigDecimal debitAmount;

    /**
     * 只有透过数据源下注(direct API 和 odds feed API)才会回传此参数
     */
    private String vendorTransId;

    /**
     * 请参阅表 Combo Info
     */
    private List<SBComboInfo> txns;

    /**
     * 请参阅表 Ticket Detail
     */
    private List<SBTicketDetail> ticketDetail;


    public boolean validate() {

        if (CollectionUtil.isEmpty(txns) || ObjectUtil.isEmpty(userId) ||
                ObjectUtil.isEmpty(vendorTransId) || ObjectUtil.isEmpty(currency)) {
            return false;
        }


        for (SBComboInfo item : txns) {
            if (!(ObjectUtil.isNotEmpty(item.getRefId()) && ObjectUtil.isNotEmpty(item.getCreditAmount()) && ObjectUtil.isNotEmpty(item.getDebitAmount()))) {
                return false;
            }

            //下注接口,不可能给玩家加余额,所以这个字段必须为0
            if (item.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {
                log.info("增加余额的字段必须为0:{}", vendorTransId);
                return false;
            }

            //扣除余额的字段不能为0
            if (item.getDebitAmount().compareTo(BigDecimal.ZERO) <= 0) {
                log.info("扣除余额的字段不能为0:{}", vendorTransId);
                return false;
            }
        }
        return true;
    }

}
