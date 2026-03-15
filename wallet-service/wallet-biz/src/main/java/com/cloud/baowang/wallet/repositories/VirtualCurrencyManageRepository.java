package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.wallet.po.VirtualCurrencyManagePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员虚拟币账号管理 Mapper 接口
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Mapper
public interface VirtualCurrencyManageRepository extends BaseMapper<VirtualCurrencyManagePO> {

}