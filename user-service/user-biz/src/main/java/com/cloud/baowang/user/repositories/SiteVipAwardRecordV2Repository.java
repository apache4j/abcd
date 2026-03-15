package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.SiteVipAwardRecordPO;
import com.cloud.baowang.user.po.SiteVipAwardRecordV2PO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * VIP奖励发放记录 Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2024-10-28
 */
@Mapper
public interface SiteVipAwardRecordV2Repository extends BaseMapper<SiteVipAwardRecordV2PO> {

}
