package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.po.OrderPullParamsPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderPullParamsRepository extends BaseMapper<OrderPullParamsPO> {
}
