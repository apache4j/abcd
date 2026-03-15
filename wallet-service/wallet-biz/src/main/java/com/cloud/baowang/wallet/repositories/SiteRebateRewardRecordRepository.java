package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.SiteCurrencyInfoPO;
import com.cloud.baowang.wallet.po.SiteRebateRewardRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Desciption: 站点币种
 * @Author: mufan
 * @Date: 2025/4/21
 * @Version: V1.0
 **/
@Mapper
public interface SiteRebateRewardRecordRepository extends BaseMapper<SiteRebateRewardRecordPO> {
}
