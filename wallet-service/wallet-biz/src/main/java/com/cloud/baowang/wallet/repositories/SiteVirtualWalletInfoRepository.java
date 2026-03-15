package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.SiteVirtualWalletSinglePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteVirtualWalletInfoRepository extends BaseMapper<SiteVirtualWalletSinglePO> {
}
