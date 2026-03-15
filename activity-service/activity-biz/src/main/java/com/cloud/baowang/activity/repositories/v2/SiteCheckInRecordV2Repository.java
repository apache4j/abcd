package com.cloud.baowang.activity.repositories.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteCheckInRecordPO;
import com.cloud.baowang.activity.po.v2.SiteCheckInRecordV2PO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author brence
 * @date 2025-10-17
 * @desc 会员签到记录数据访问层
 */
@Mapper
public interface SiteCheckInRecordV2Repository extends BaseMapper<SiteCheckInRecordV2PO> {
}
