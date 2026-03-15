package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.SiteCurrencyInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Desciption: 站点币种
 * @Author: Ford
 * @Date: 2024/9/3 15:21
 * @Version: V1.0
 **/
@Mapper
public interface SiteCurrencyInfoRepository extends BaseMapper<SiteCurrencyInfoPO> {
    //批量修改各个站点汇率
    void updateBatchList(@Param("voList") List<SiteCurrencyInfoPO> voList);
}
