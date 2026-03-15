package com.cloud.baowang.wallet.consumer;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AddressBalanceMessageVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.ChainTradeMessageVO;
import com.cloud.baowang.wallet.po.HotWalletAddressTradeRecordPO;
import com.cloud.baowang.wallet.service.HotWalletAddressService;
import com.cloud.baowang.wallet.service.HotWalletAddressTradeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class HotWalletAddressConsumer {

    private final HotWalletAddressService hotWalletAddressService;

    private final HotWalletAddressTradeRecordService hotWalletAddressTradeRecordService;


    @KafkaListener(topics = TopicsConstants.ADDRESS_BALANCE_NOTIFY_TOPIC, groupId = GroupConstants.ADDRESS_BALANCE_NOTIFY_GROUP)
    public void addressBalance(String topicText, Acknowledgment ackItem) {
        AddressBalanceMessageVO addressBalanceMessageVO=JSONObject.parseObject(topicText,AddressBalanceMessageVO.class);
        if (null == addressBalanceMessageVO) {
            log.error("地址余额 消息-MQ队列-参数不能为空");
            return;
        }
        log.info("地址余额 消息,the msg: {} by kafka,MQ消息id:{}", JSONObject.toJSONString(addressBalanceMessageVO), addressBalanceMessageVO.getMsgId());
        long start = System.currentTimeMillis();
        try {
            String addressNo = addressBalanceMessageVO.getAddressNo();
            hotWalletAddressService.updateAddressBalance(addressBalanceMessageVO);

        } catch (Exception e) {
            log.info("地址余额 消息-MQ队列执行报错，报错信息{}", e.getMessage());
        } finally {
            log.info("地址余额 消息,MQ队列-消息id:{},整体耗时:{}毫秒", addressBalanceMessageVO.getMsgId(), System.currentTimeMillis() - start);
            ackItem.acknowledge();
        }
    }



    @KafkaListener(topics = TopicsConstants.CHAIN_TRADE_NOTIFY_TOPIC, groupId = GroupConstants.CHAIN_TRADE_NOTIFY_GROUP)
    public void chainTradeNotify(String topicText, Acknowledgment ackItem) {
        ChainTradeMessageVO chainTradeMessageVO=JSONObject.parseObject(topicText,ChainTradeMessageVO.class);
        if (null == chainTradeMessageVO) {
            log.error("充值、归集 交易消息-MQ队列-参数不能为空");
            return;
        }
        String jsonStr = JSONObject.toJSONString(chainTradeMessageVO);
        log.info("充值、归集 交易消息,the msg: {} by kafka,MQ消息id:{}",jsonStr, chainTradeMessageVO.getMsgId());
        long start = System.currentTimeMillis();
        try {
            HotWalletAddressTradeRecordPO po = new HotWalletAddressTradeRecordPO();
            po.setAddress(chainTradeMessageVO.getUserAddress());
            po.setTradeHash(chainTradeMessageVO.getTradeHash());
            po.setJsonStr(jsonStr);
            hotWalletAddressTradeRecordService.save(po);

        } catch (Exception e) {
            log.info("充值、归集 交易消息-MQ队列执行报错，报错信息{}", e.getMessage());
        } finally {
            log.info("充值、归集 交易消息,MQ队列-消息id:{},整体耗时:{}毫秒", chainTradeMessageVO.getMsgId(), System.currentTimeMillis() - start);
            ackItem.acknowledge();
        }
    }
}
