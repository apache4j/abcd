package com.cloud.baowang.play.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalRecordVO;
import com.cloud.baowang.play.po.OrderAbnormalRecordPO;
import com.cloud.baowang.play.repositories.OrderAbnormalRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class OrderAbnormalRecordService extends ServiceImpl<OrderAbnormalRecordRepository, OrderAbnormalRecordPO> {
    private final OrderAbnormalRecordRepository orderAbnormalRecordRepository;


    public OrderAbnormalRecordVO findByOrderId(String orderId) {
        OrderAbnormalRecordPO orderAbnormalRecordPO = orderAbnormalRecordRepository.selectOne(Wrappers.<OrderAbnormalRecordPO>lambdaQuery()
                .eq(OrderAbnormalRecordPO::getOrderId, orderId)
                .orderByDesc(OrderAbnormalRecordPO::getChangeCount)
                .last("limit 1"));
        if (orderAbnormalRecordPO == null){
            return null;
        }
        OrderAbnormalRecordVO orderRecordVO = new OrderAbnormalRecordVO();
        BeanUtils.copyProperties(orderAbnormalRecordPO,orderRecordVO);
        return orderRecordVO;
    }

    public OrderAbnormalRecordVO saveAbnormalOrder(OrderAbnormalRecordVO orderVO) {
        OrderAbnormalRecordPO orderRecordPO = new OrderAbnormalRecordPO();
        BeanUtils.copyProperties(orderVO, orderRecordPO);
        orderRecordPO.setCreatedTime(System.currentTimeMillis());
        orderAbnormalRecordRepository.insert(orderRecordPO);
        OrderAbnormalRecordVO orderRecordVO = new OrderAbnormalRecordVO();
        BeanUtils.copyProperties(orderRecordPO, orderRecordVO);
        return orderRecordVO;
    }
}
