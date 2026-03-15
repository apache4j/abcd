package com.cloud.baowang.activity.repositories.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityMakeupCountRecordPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author brence
 * @date 2025-10-17
 * @desc 可签到余额--可以补签的次数
 */
@Mapper
public interface SiteActivityMakeupCountRecordV2Repository extends BaseMapper<SiteActivityMakeupCountRecordPO> {
}
