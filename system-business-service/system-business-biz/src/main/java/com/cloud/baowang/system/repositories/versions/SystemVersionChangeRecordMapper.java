package com.cloud.baowang.system.repositories.versions;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.versions.SystemVersionChangeRecordPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemVersionChangeRecordMapper extends BaseMapper<SystemVersionChangeRecordPO> {
}
