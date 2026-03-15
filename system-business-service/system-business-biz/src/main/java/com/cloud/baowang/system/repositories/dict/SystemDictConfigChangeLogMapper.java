package com.cloud.baowang.system.repositories.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.dict.SystemDictConfigChangeLogPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典配置变更记录 Mapper 接口
 */
@Mapper
public interface SystemDictConfigChangeLogMapper extends BaseMapper<SystemDictConfigChangeLogPO> {

}
