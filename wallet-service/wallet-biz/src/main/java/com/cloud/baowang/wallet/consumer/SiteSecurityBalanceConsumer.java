package com.cloud.baowang.wallet.consumer;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.SitSecurityBalanceMqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceChangeRecordReqVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AddressBalanceMessageVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.ChainTradeMessageVO;
import com.cloud.baowang.wallet.po.HotWalletAddressTradeRecordPO;
import com.cloud.baowang.wallet.service.HotWalletAddressService;
import com.cloud.baowang.wallet.service.HotWalletAddressTradeRecordService;
import com.cloud.baowang.wallet.service.SiteSecurityBalanceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SiteSecurityBalanceConsumer {

    private final SiteSecurityBalanceService siteSecurityBalanceService;


    @KafkaListener(topics = TopicsConstants.SITE_SECURITY_BALANCE, groupId = GroupConstants.SITE_SECURITY_BALANCE_GROUP)
    public void addressBalance(SitSecurityBalanceMqVO sitSecurityBalanceMqVO, Acknowledgment ackItem) {
        if (null == sitSecurityBalanceMqVO) {
            log.error("保证金增减 消息-MQ队列-参数不能为空");
            return;
        }
        log.info("保证金增减 消息,the msg: {} by kafka,MQ消息id:{}", JSONObject.toJSONString(sitSecurityBalanceMqVO), sitSecurityBalanceMqVO.getMsgId());
        long start = System.currentTimeMillis();
        try {
            SiteSecurityBalanceChangeRecordReqVO siteSecurityBalanceChangeRecordReqVO = ConvertUtil.entityToModel(sitSecurityBalanceMqVO,SiteSecurityBalanceChangeRecordReqVO.class);
            siteSecurityBalanceService.recordBalanceChangeLog(siteSecurityBalanceChangeRecordReqVO);
        } catch (Exception e) {
            log.error("保证金增减 消息-MQ队列执行报错，报错信息{}", e.getMessage());
        } finally {
            log.info("保证金增减 消息,MQ队列-消息id:{},整体耗时:{}毫秒", sitSecurityBalanceMqVO.getMsgId(), System.currentTimeMillis() - start);
            ackItem.acknowledge();
        }
    }


}
