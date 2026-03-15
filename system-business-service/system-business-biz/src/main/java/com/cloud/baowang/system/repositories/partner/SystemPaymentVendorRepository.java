package com.cloud.baowang.system.repositories.partner;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.partner.SystemPaymentVendorPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统支付商数据访问层
 */
@Mapper
public interface SystemPaymentVendorRepository extends BaseMapper<SystemPaymentVendorPO> {
}
