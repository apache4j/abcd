package com.cloud.baowang.play.service;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.play.po.OrderPullParamsPO;
import com.cloud.baowang.play.repositories.OrderPullParamsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注单拉取参数服务实现类
 *
 */
@Log4j2
@Service
@AllArgsConstructor
public class OrderPullParamsService  extends ServiceImpl<OrderPullParamsRepository,OrderPullParamsPO> {

    private final OrderPullParamsRepository orderPullParamsRepository;

    public OrderPullParamsPO findByVenueCode(String venueCode) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(OrderPullParamsPO::getVenueCode, venueCode)
                .one();
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrderPullParams(OrderPullParamsPO pullParamsPO) {
        OrderPullParamsPO oldPullParam = findByVenueCode(pullParamsPO.getVenueCode());
        if (oldPullParam != null) {
            pullParamsPO.setId(oldPullParam.getId());
            pullParamsPO.setUpdatedTime(System.currentTimeMillis());
            orderPullParamsRepository.updateById(pullParamsPO);
        } else {
            pullParamsPO.setCreatedTime(System.currentTimeMillis());
            orderPullParamsRepository.insert(pullParamsPO);
        }
        return true;
    }
}
